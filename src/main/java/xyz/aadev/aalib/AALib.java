package xyz.aadev.aalib;

import com.google.common.base.Stopwatch;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import xyz.aadev.aalib.api.common.exceptions.OutdatedJavaException;
import xyz.aadev.aalib.common.logging.Logger;

import java.util.concurrent.TimeUnit;

@Mod(modid = Reference.MOD_ID, version = Reference.VERSION_BUILD, name = Reference.MOD_NAME, certificateFingerprint = Reference.FINGERPRINT, dependencies = Reference.DEPENDENCIES/*, guiFactory = Reference.GUI_FACTORY*/)
public class AALib {


    @Mod.Instance(Reference.MOD_ID)
    public static AALib instance;

    //@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
    //public static IProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        final Stopwatch watch = Stopwatch.createStarted();
        Logger.info("Pre Initialization ( started )");

        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            throw new OutdatedJavaException(String.format("%s requires Java 8 or newer, Please update your java", Reference.MOD_NAME));
        }

        Logger.info("Pre Initialization ( ended after " + watch.elapsed(TimeUnit.MILLISECONDS) + "ms )");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        final Stopwatch watch = Stopwatch.createStarted();
        Logger.info("Initialization ( started )");

        Logger.info("Initialization ( ended after " + watch.elapsed(TimeUnit.MILLISECONDS) + "ms )");
    }


    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        final Stopwatch watch = Stopwatch.createStarted();
        Logger.info("Post Initialization ( started )");

        Logger.info("Post Initialization ( ended after " + watch.elapsed(TimeUnit.MILLISECONDS) + "ms )");
    }
}

