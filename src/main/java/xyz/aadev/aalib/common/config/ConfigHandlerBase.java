package xyz.aadev.aalib.common.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import xyz.aadev.aalib.common.logging.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public abstract class ConfigHandlerBase {
    protected List<ConfigFileBase> configFileList = new ArrayList<>();
    private List<Function<?, ?>> configFiles = new ArrayList<>();

    public static boolean syncConfig(ConfigHandlerBase config, List<ConfigFileBase> files) {
        boolean changed = false;

        if (config.configFileList.size() != files.size())
            return false;

        Iterator<ConfigFileBase> iteratorLocal = config.configFileList.iterator();
        Iterator<ConfigFileBase> iteratorRemote = files.iterator();

        while (iteratorLocal.hasNext() && iteratorRemote.hasNext())
            changed |= iteratorLocal.next().sync(iteratorRemote.next());

        if (changed)
            config.save();

        return changed;
    }

    public void save() {
        configFiles.forEach(function -> function.apply(null));
    }

    public List<ConfigFileBase> getConfigFileList() {
        return configFileList;
    }

    public <T extends ConfigFileBase> void save(T configFile, Class<T> clazz) {
        if (configFile.needsSave()) {
            try {
                CommentedConfigurationNode commentedConfigurationNode = configFile.load();
                commentedConfigurationNode.setValue(TypeToken.of(clazz), configFile);
                configFile.save(commentedConfigurationNode);
            } catch (ObjectMappingException | IOException ex) {
                Logger.fatal(ex.getMessage());
                ex.printStackTrace();
            }
        }
        configFile.clearNeedsSave();
    }

    public <T extends ConfigFileBase> T load(T configFile, Class<T> clazz) {
        try {
            CommentedConfigurationNode commentedConfigurationNode = configFile.load();

            T val = commentedConfigurationNode.getValue(TypeToken.of(clazz), configFile);
            val.loader = configFile.loader;
            val.file = configFile.file;
            val.insertDefaults();
            val.setConfigVersion();

            configFileList.add(val);
            configFiles.add(configFile1 -> {
                this.save(val, clazz);
                return true;
            });

            return val;
        } catch (ObjectMappingException | IOException ex) {
            Logger.fatal(ex.getMessage());
            ex.printStackTrace();
        }
        return configFile;
    }
}
