/*
 * Copyright (c) 2015 Vincent Lee
 * Copyright (c) 2020, 2021 Adrian "asie" Siekierka
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package pl.asie.foamfix;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import pl.asie.foamfix.bugfixmod.coremod.BugfixModClassTransformer;
import pl.asie.foamfix.bugfixmod.mod.ToolDesyncFixEventHandler;
import pl.asie.foamfix.bugfixmod.mod.ArrowDingTweakEventHandler;
import pl.asie.foamfix.ghostbuster.CommandGhostBuster;
import pl.asie.foamfix.ghostbuster.GhostBusterLogger;
import pl.asie.foamfix.repack.com.unascribed.ears.Ears;

import java.net.URLClassLoader;
import java.util.Arrays;

@Mod(name = "FoamFix", modid = "foamfix", version = "@VERSION@", acceptableRemoteVersions="*")
public class FoamFixMod {
    @Mod.Instance
    public static FoamFixMod instance;

    public static Logger logger;

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt) {
        logger = evt.getModLog();

        System.out.println( System.getProperty("sun.boot.class.path"));
        System.out.println(Arrays.toString(((URLClassLoader) ClassLoader.getSystemClassLoader()).getURLs()));

        if (BugfixModClassTransformer.instance.settings.ArrowDingTweakEnabled) {
            ArrowDingTweakEventHandler handler = new ArrowDingTweakEventHandler();
            FMLCommonHandler.instance().bus().register(handler);
            MinecraftForge.EVENT_BUS.register(handler);
        }

        if (evt.getSide() == Side.CLIENT) {
            if (BugfixModClassTransformer.instance.settings.ToolDesyncFixEnabled) {
                ToolDesyncFixEventHandler handler = new ToolDesyncFixEventHandler();
                if (handler.isValid()) {
                    FMLCommonHandler.instance().bus().register(handler);
                    MinecraftForge.EVENT_BUS.register(handler);
                } else {
                    logger.error("Could not initialize ToolDesyncFixEventHandler!");
                }
            }

            if (BugfixModClassTransformer.instance.settings.helloMmcg) {
                try {
                    Class c = Class.forName("pl.asie.foamfix.repack.com.unascribed.ears.Ears");
                    Object o = c.newInstance();
                    c.getMethod("onPreInit", FMLPreInitializationEvent.class)
                            .invoke(o, evt);
                } catch (Throwable t) {
                    t.printStackTrace();
                    // pass
                }
            }
        }

        if (BugfixModClassTransformer.instance.settings.lwRemovePackageManifestMap) {
            logger.info("Removing LaunchWrapper package manifest map...");
            LaunchWrapperRuntimeFix.removePackageManifestMap();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent evt) {
        if (BugfixModClassTransformer.instance.settings.lwWeakenResourceCache) {
            logger.info("Weakening LaunchWrapper resource cache...");
            LaunchWrapperRuntimeFix.weakenResourceCache();
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        if (BugfixModClassTransformer.instance.settings.gbEnableDebugger) {
            event.registerServerCommand(new CommandGhostBuster());
        }
    }

    @Mod.EventHandler
    public void serverStopped(FMLServerStoppedEvent event) {
        GhostBusterLogger.saveLogFile();
    }
}
