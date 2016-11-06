package xyz.aadev.aalib.common.config;

import com.google.common.reflect.TypeToken;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.AtomicFiles;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import scala.actors.threadpool.Arrays;
import xyz.aadev.aalib.common.logging.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public abstract class ConfigFileBase implements Serializable {
    private static boolean initialized = false;

    File file;
    ConfigurationLoader<CommentedConfigurationNode> loader;
    private boolean needsSave = true;

    @Setting("_VERSION")
    private int configVersion;

    public ConfigFileBase() {
        file = null;
        loader = null;
    }

    public ConfigFileBase(File configFolder, String configName) {
        this(new File(configFolder, configName + ".cfg"));
    }

    public ConfigFileBase(File configFile) {
        configFile.getParentFile().mkdirs();
        file = configFile;
        loader = HoconConfigurationLoader.builder().setFile(file).build();
    }

    public static void init() {
        if (!initialized)
            return;

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Block.class), new RegistrySerializer<Block>() {
            @Override
            IForgeRegistry<Block> getRegistry() {
                return GameData.getBlockRegistry();
            }
        });

        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Item.class), new RegistrySerializer<Item>() {
            @Override
            IForgeRegistry<Item> getRegistry() {
                return GameData.getItemRegistry();
            }
        });


        initialized = true;
    }

    public static List<Field> getAllFields(List<Field> fields, Class<?> type) {
        if (type != ConfigFileBase.class) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
            if (type.getSuperclass() != null && ConfigFileBase.class.isAssignableFrom(type.getSuperclass()))
                fields = getAllFields(fields, type.getSuperclass());
        }

        return fields;
    }

    public CommentedConfigurationNode load() throws IOException {
        return loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
    }

    public void save(ConfigurationNode configurationNode) throws IOException {
        loader.save(configurationNode);
    }

    public String getName() {
        return file.getName();
    }

    public abstract void insertDefaults();

    protected abstract int getConfigVersion();

    void setConfigVersion() {
        if (configVersion != getConfigVersion()) {
            configVersion = getConfigVersion();
            setNeedsSave();
        }
    }

    public void setNeedsSave() {
        needsSave = true;
    }

    public boolean needsSave() {
        return needsSave;
    }

    public void clearNeedsSave() {
        needsSave = false;
    }

    public void syncField(Object other, Object that, Field field) {
        try {
            if (Modifier.isTransient(field.getModifiers()))
                return;

            if (Modifier.isStatic(field.getModifiers()))
                return;

            if (!field.isAccessible())
                field.setAccessible(true);

            Object orig = field.get(that);
            Object remote = field.get(other);

            if (field.getType().isAnnotationPresent(ConfigSerializable.class)) {
                sync(remote, orig);
            } else {
                if (!orig.equals(remote)) {
                    field.set(that, remote);
                    setNeedsSave();
                }
            }
        } catch (IllegalAccessException ex) {
            Logger.fatal(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public boolean sync(ConfigFileBase other) {
        return sync(other, this);
    }

    public boolean sync(Object other, Object that) {
        if (other.getClass() != that.getClass())
            return false;

        List<Field> fields = new ArrayList<>();
        getAllFields(fields, that.getClass());

        for (Field field : fields)
            syncField(other, that, field);

        return needsSave;
    }

    public ConfigFileBase loadFromPacket(byte[] packetPayload) {
        ConfigurationLoader<CommentedConfigurationNode> packetPayloadLoader =
                HoconConfigurationLoader
                        .builder()
                        .setSource(() -> new BufferedReader(new InputStreamReader(new ByteArrayInputStream(packetPayload))))
                        .setSink(AtomicFiles.createAtomicWriterFactory(file.toPath(), StandardCharsets.UTF_8))
                        .build();

        try {
            CommentedConfigurationNode commentedConfigurationNode = packetPayloadLoader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));

            try {
                return commentedConfigurationNode.getValue(TypeToken.of(this.getClass()));
            } catch (ObjectMappingException ex) {
                Logger.fatal(ex.getMessage());
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            Logger.fatal(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    public byte[] getPacketPayload() {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException ex) {
            Logger.fatal(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }

    private static abstract class RegistrySerializer<T extends IForgeRegistryEntry<T>> implements TypeSerializer<T> {
        abstract IForgeRegistry<T> getRegistry();

        @Override
        public T deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
            return getRegistry().getValue(new ResourceLocation(value.getString()));
        }

        @Override
        public void serialize(TypeToken<?> type, T obj, ConfigurationNode value) throws ObjectMappingException {
            value.setValue(obj.getRegistryName());
        }
    }
}
