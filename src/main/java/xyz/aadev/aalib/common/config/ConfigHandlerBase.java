package xyz.aadev.aalib.common.config;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import scala.tools.nsc.Global;
import xyz.aadev.aalib.api.common.config.IConfigListener;
import xyz.aadev.aalib.api.common.util.IModInitializationHandler;
import xyz.aadev.aalib.common.util.ModContainerHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ConfigHandlerBase implements IModInitializationHandler, IConfigListener {

    public ConfigHandlerBase(final String fileName) {
        this(fileName, null, null);
    }

    public ConfigHandlerBase(final String fileName, final String directoryName) {
        this(fileName, directoryName, null);
    }

    public ConfigHandlerBase(final String fileName, final String directoryName, final String languageKeyPrefix) {

        this.configFileName = fileName;
        this.configDirectoryName = directoryName;
        this.languageKeyPrefix = languageKeyPrefix;
        this.categories = new ArrayList<>();
        this.listeners = new ArrayList<>();
        this.addListener(this);
    }

    public void addListener(IConfigListener listener) {
        if(!listeners.contains(listener))
            this.listeners.add(listener);
    }

    public List<IConfigElement> getConfigElements() {
        final List<IConfigElement> elements = new ArrayList<>(this.categories.size());

        elements.addAll(this.categories.stream().map(ConfigElement::new).collect(Collectors.toList()));

        return elements;
    }

    @Override
    public void onPreInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        this.modId = ModContainerHelper.getModIdFromActiveContainer();

        if(this.languageKeyPrefix == null) {
            this.languageKeyPrefix = String.format("config.%s.", this.modId);
        } else if(!this.languageKeyPrefix.endsWith(".")) {
            this.languageKeyPrefix += ".";
        }

        final File directory = this.configDirectoryName != null ? new File(event.getModConfigurationDirectory(), this.configDirectoryName)
                : event.getModConfigurationDirectory();

        if(!directory.exists() && !directory.mkdir()) {
            throw new RuntimeException(String.format("Unable to create configuration directory: %s", directory.getName()));
        }

        this.configuration = new Configuration(new File(directory, this.configFileName));
        this.loadConfigurationCategories();
        this.loadConfigurationValues();

        if(this.configuration.hasChanged()) {
            this.configuration.save();
        }

        this.notifyListeners();
    }

    @Override
    public void onInit(FMLInitializationEvent event) {

    }

    @Override
    public void onPostInit(FMLPostInitializationEvent event) {

    }

    @SubscribeEvent
    public void onConfigChangedFromGUI(ConfigChangedEvent.OnConfigChangedEvent event) {

        if (this.modId.equalsIgnoreCase(event.getModID())) {

            this.loadConfigurationValues();

            if (this.configuration.hasChanged())
                this.configuration.save();

            this.notifyListeners();
        }
    }

    @Override
    public String toString() {
        return this.configuration != null ? this.configuration.toString() : "configuration";
    }

    protected abstract void loadConfigurationCategories();

    protected abstract void loadConfigurationValues();

    protected ConfigCategory getCategory(final String name, final String comment) {
        return getCategory(name, comment, true, false);
    }

    protected ConfigCategory getCategory(final String name, final String comment, final boolean requireWorldRestart) {
        return getCategory(name, comment, requireWorldRestart, false);
    }

    protected ConfigCategory getCategory(final String name, final String comment, final boolean requireWorldRestart, final boolean requireMcRestart) {

        final ConfigCategory category = this.configuration.getCategory(name);

        if (comment != null)
            category.setComment(comment);

        category.setRequiresMcRestart(requireMcRestart);
        category.setRequiresWorldRestart(requireWorldRestart);

        this.config(category);
        this.categories.add(category);
        return category;
    }

    // Boolean properties and values

    protected Property getBooleanProperty(String propertyName, ConfigCategory category, boolean defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected Property getBooleanListProperty(String propertyName, ConfigCategory category, boolean[] defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected boolean getBoolean(String propertyName, ConfigCategory category, boolean defaultValue, String comment) {
        return this.getBooleanProperty(propertyName, category, defaultValue, comment).getBoolean();
    }

    protected boolean[] getBooleanList(String propertyName, ConfigCategory category, boolean[] defaultValue, String comment) {
        return this.getBooleanListProperty(propertyName, category, defaultValue, comment).getBooleanList();
    }

    // Integer properties and values

    protected Property getIntProperty(String propertyName, ConfigCategory category, int defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected Property getIntListProperty(String propertyName, ConfigCategory category, int[] defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected int getInt(String propertyName, ConfigCategory category, int defaultValue, String comment) {
        return this.getIntProperty(propertyName, category, defaultValue, comment).getInt();
    }

    protected int[] getIntList(String propertyName, ConfigCategory category, int[] defaultValue, String comment) {
        return this.getIntListProperty(propertyName, category, defaultValue, comment).getIntList();
    }

    // Double properties and values

    protected Property getDoubleProperty(String propertyName, ConfigCategory category, double defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected Property getDoubleListProperty(String propertyName, ConfigCategory category, double[] defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected double getDouble(String propertyName, ConfigCategory category, double defaultValue, String comment) {
        return this.getDoubleProperty(propertyName, category, defaultValue, comment).getDouble();
    }

    protected double[] getDoubleList(String propertyName, ConfigCategory category, double[] defaultValue, String comment) {
        return this.getDoubleListProperty(propertyName, category, defaultValue, comment).getDoubleList();
    }

    protected float getFloat(String propertyName, ConfigCategory category, float defaultValue, String comment) {
        return (float)this.getDouble(propertyName, category, (double)defaultValue, comment);
    }

    protected float[] getFloatList(String propertyName, ConfigCategory category, float[] defaultValue, String comment) {

        double[] doubles;
        float[] floats;
        int length, idx;

        length = defaultValue.length;
        doubles = new double[length];

        for (idx = 0; idx < length; ++idx)
            doubles[idx] = defaultValue[idx];

        doubles = this.getDoubleList(propertyName, category, doubles, comment);

        length = doubles.length;
        floats = new float[length];

        for (idx = 0; idx < length; ++idx)
            floats[idx] = (float)doubles[idx];

        return floats;
    }

    // String properties and values

    protected Property getStringProperty(String propertyName, ConfigCategory category, String defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected Property getStringListProperty(String propertyName, ConfigCategory category, String[] defaultValue, String comment) {
        return this.config(this.configuration.get(category.getName(), propertyName, defaultValue, comment), category);
    }

    protected String getString(String propertyName, ConfigCategory category, String defaultValue, String comment) {

        String value = this.getStringProperty(propertyName, category, defaultValue, comment).getString();

        return null != value ? value : defaultValue;
    }

    protected String[] getStringList(String propertyName, ConfigCategory category, String[] defaultValue, String comment) {

        String[] value = this.getStringListProperty(propertyName, category, defaultValue, comment).getStringList();

        return null != value ? value : defaultValue;
    }

    protected void notifyListeners() {
        this.listeners.forEach(IConfigListener::onConfigChanged);
    }

    private ConfigCategory config(ConfigCategory category) {
        return category.setLanguageKey(this.languageKeyPrefix + category.getName());
    }

    private Property config(Property property, ConfigCategory category) {
        return property.setLanguageKey(this.languageKeyPrefix + category.getName() + "." + property.getName());
    }

    private String modId;
    private String languageKeyPrefix;
    private final String configFileName;
    private final String configDirectoryName;
    private final List<ConfigCategory> categories;
    private final List<IConfigListener> listeners;
    private Configuration configuration;
}
