package xyz.aadev.aalib;

public class Reference {
    public static final String PACKAGE_NAME = "xyz.aadev.";
    public static final String MOD_ID = "aalib";
    public static final String MOD_NAME = "AALib";
    public static final String VERSION_BUILD = "@VERSION@";
    public static final String MINECRAFT_VERSION = "@MCVERSION@";
    public static final String DEPENDENCIES = "";
    public static final String CLIENT_PROXY_CLASS = PACKAGE_NAME + MOD_ID + ".proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = PACKAGE_NAME + MOD_ID + ".proxy.ServerProxy";
    public static final String FINGERPRINT = "@FINGERPRINT@";
    public static final String GUI_FACTORY = PACKAGE_NAME + MOD_ID + ".client.gui.config.ConfigGuiFactory";
    public static final String PATH_INTEGRATIONS = PACKAGE_NAME + MOD_ID + ".common.integrations.";
}
