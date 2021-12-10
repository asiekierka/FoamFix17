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

package pl.asie.foamfix.bugfixmod.coremod;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.asie.foamfix.bugfixmod.BugfixModSettings;
import pl.asie.foamfix.bugfixmod.coremod.patchers.BoatDesyncFixPatcher_Extra;
import pl.asie.foamfix.bugfixmod.coremod.patchers.ChickenLureTweakPatcher;
import pl.asie.foamfix.coremod.patchers.*;
import pl.asie.foamfix.bugfixmod.coremod.patchers.HeartBlinkFixPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.SnowballFixPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.AbstractPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.BoatDesyncFixPatcher_Main;
import pl.asie.foamfix.bugfixmod.coremod.patchers.HeartFlashFixPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.ItemHopperBounceFixPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.ItemStairBounceFixPatcher;
import pl.asie.foamfix.bugfixmod.coremod.patchers.VillageAnvilTweakPatcher;
import pl.asie.foamfix.forkage.coremod.patchers.*;
import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.EarsAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Vincent on 3/10/14.
 */
public class BugfixModClassTransformer implements IClassTransformer {

    public static BugfixModClassTransformer instance;
    public File settingsFile;
    private boolean hasInit = false;
    public BugfixModSettings settings;
    private ArrayList<AbstractPatcher> globalPatchers = new ArrayList<>();
    private Map<String, ArrayList<AbstractPatcher>> patchers;
    public Logger logger = LogManager.getLogger("foamfix");

    public BugfixModClassTransformer() {
        if (instance != null) {
            throw new RuntimeException("Only one transformer may exist!");
        } else {
            instance = this;
        }
    }

    public void initialize(Boolean isObf) {
        if (!hasInit) {
            Configuration config = new Configuration(settingsFile);
            config.load();
            settings = new BugfixModSettings();

            settings.gbEnableDebugger = config.get("ghostbuster", "enableDebugger", false,
                    "Enable the /ghostbuster command used for logging ghost chunkloading events.").getBoolean(false);
            settings.gbDebuggerLogFilePath = config.get("ghostbuster", "debuggerLogFile", "",
                    "Path of the log file for /ghostbuster logging; if empty, outputs to Minecraft log file.").getString().trim();
            settings.gbDebuggerLogFile = settings.gbDebuggerLogFilePath.isEmpty() ? null : new File(settings.gbDebuggerLogFilePath);
            settings.gbEnableFixes = config.get("ghostbuster", "enableFixes", true,
                    "Main toggle. If disabled, none of the ghost chunkloading fixes are applied.").getBoolean(true);
            settings.gbFixGrassVanilla = config.get("ghostbuster", "fixGrassVanilla", true,
                    "Fix ghost chunkloading caused by vanilla grass blocks.").getBoolean(true);
            settings.gbFixGrassBOP = config.get("ghostbuster", "fixGrassBop", true,
                    "Fix ghost chunkloading caused by Biomes O' Plenty grass blocks.").getBoolean(true);
            settings.gbFixFluidsVanilla = config.get("ghostbuster", "fixFluidsVanilla", true,
                    "Partially fix ghost chunkloading caused by vanilla fluid flow.").getBoolean(true);
            settings.gbFixFluidsModded = config.get("ghostbuster", "fixFluidsModded", true,
                    "Fix ghost chunkloading caused by modded fluid blocks.").getBoolean(true);
            settings.gbFixVinesVanilla = config.get("ghostbuster", "fixVinesVanilla", true,
                    "Fix ghost chunkloading caused by vanilla vine blocks.").getBoolean(true);

            settings.bfJarDiscovererMemoryLeakFixEnabled = config.get("bugfixes", "jarDiscovererMemoryLeakFix", true,
                    "Fix native memory leak in JarDiscoverer (from Forkage by immibis)").getBoolean(true);
            settings.bfSoundSystemUnpauseFixEnabled = config.get("bugfixes", "soundSystemUnpauseFix", true,
                    "Fix sounds playing extra times when you close a GUI or unpause the game (from Forkage by immibis)").getBoolean(true);
            settings.bfEntityHeldItemNBTRenderFixEnabled = config.get("bugfixes", "entityHeldItemNBTRenderFix", true,
                    "Fix items held by mobs not reflecting their NBT status (from Forkage by immibis)").getBoolean(true);
            settings.bfAlphaPassTessellatorCrashFixEnabled = config.get("bugfixes", "tessellatorAlphaPassCrashFix", true,
                    "Fix PriorityQueue tessellator crash on empty alpha pass.").getBoolean(true);

            settings.lwWeakenResourceCache = config.get("launchwrapper", "weakenResourceCache", true,
                    "Weaken LaunchWrapper's byte[] resource cache to make it cleanuppable by the GC. Safe.").getBoolean(true);
            settings.lwRemovePackageManifestMap = config.get("launchwrapper", "removePackageManifestMap", true,
                    "Remove LaunchWrapper package manifest map (which is not used anyway).").getBoolean(true);

            settings.ItemHopperBounceFixEnabled = config.get("bugfixes", "itemBounceHopperFix", false,
                    "Fix items bouncing around on locked hoppers. (from BugfixMod by williewillus)"
                    ).getBoolean(false);
            settings.ItemStairBounceFixEnabled = config.get("bugfixes", "itemBounceStairFix", false,
                    "Fix items bouncing around on stairs. (from BugfixMod by williewillus)"
                    ).getBoolean(false);
            settings.SnowballFixEnabled = config.get("bugfixes", "snowballFix", true,
                    "Fix projectiles that deal 0 damage not knocking back players. (from BugfixMod by williewillus)"
                    ).getBoolean(true);

            settings.ArrowDingTweakEnabled = config.get("tweaks", "arrowDing", false,
                    "Emits a 'ding' sound when mobs are hit by an arrow, not just players. (from BugfixMod by williewillus)"
                    ).getBoolean(false);
            settings.ChickenLureTweakEnabled = config.get("tweaks", "chickenLureFix", false,
                    "Adds AI tasks that make chickens attracted to all items they can breed with, not just seeds. (from BugfixMod by williewillus)"
                    ).getBoolean(false);
            settings.VillageAnvilTweakEnabled = config.get("tweaks", "villageAnvils", false,
                    "Blacksmith houses generate with an anvil where there was a double stone slab (which was supposed to be an \"anvil\" anyway). (from BugfixMod by williewillus)"
                    ).getBoolean(false);

            settings.BoatDesyncFixEnabled = config.get("bugfixes", "clientBoatDesyncFix", true,
                    "Reduce the amount of boat desynchronization between client and server. (from BugfixMod by williewillus)"
                    ).getBoolean(true);
            settings.HeartBlinkFixEnabled = config.get("bugfixes", "clientHeartBlinkFix", true,
                    "Restore rendering the client-side heart blink. (from BugfixMod by williewillus)"
                    ).getBoolean(true);
            settings.HeartFlashFixEnabled = config.get("bugfixes", "clientHeartFlashFix", true,
                    "Restore lost hearts flashing to indicate loss. (from BugfixMod by williewillus)"
                    ).getBoolean(true);
            settings.ToolDesyncFixEnabled = config.get("bugfixes", "clientToolDesyncFix", true,
                    "Fix client-side desynchronization of damaged tools related to Unbreaking enchantments. (from BugfixMod by williewillus)"
                    ).getBoolean(true);

            settings.bfLog4JExploitFixEnabled = config.get("bugfixes", "log4jExploitFix", true,
                    "Fix Log4j formatting exploit."
                    ).getBoolean(true);

            settings.mc18SkinSupport = config.get("tweaks", "mc18SkinSupport", true,
                    "Add support for Minecraft 1.8+ skins."
                    ).getBoolean(true);

            if (!Arrays.asList(new File(new File(settingsFile.getParent()).getParent()).list()).contains("saves")) {
                logger.info("You probably are on a dedicated server. Disabling client fixes");
                settings.BoatDesyncFixEnabled = false;
                settings.HeartBlinkFixEnabled = false;
                settings.HeartFlashFixEnabled = false;
            }

            config.save();
            MappingRegistry.init(isObf);
            setupPatchers();
            hasInit = true;

            if (settings.mc18SkinSupport) {
                try {
                    Class c = Class.forName("com.unascribed.ears.Ears");
                    settings.helloMmcg = false;
                } catch (Throwable t) {
                    settings.helloMmcg = true;
                }
            } else {
                settings.helloMmcg = false;
            }

            if (settings.helloMmcg) {
                try {
                    Class c = Class.forName("api.player.render.RenderPlayerAPI");
                    settings.helloMmcg = false;
                } catch (Throwable t) {
                    // pass
                }
            }
        }
    }


    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (hasInit) {
            List<AbstractPatcher> gpl = globalPatchers;
            for (AbstractPatcher p : gpl) {
                bytes = p.patch(transformedName, bytes);
            }
            List<AbstractPatcher> pl = patchers.get(transformedName);
            if (pl != null) {
                for (AbstractPatcher p : pl) {
                    bytes = p.patch(transformedName, bytes);
                }
            }

            if (settings.helloMmcg) {
                bytes = EarsAgent.transform(transformedName, bytes);
            }
        }
        return bytes;
    }


    private void setupPatchers() {
        if (patchers != null) {
            logger.warn("Patcher already initialized!!");
        } else {
            patchers = new HashMap<>();

            if (settings.BoatDesyncFixEnabled) {
                addPatcher(new BoatDesyncFixPatcher_Main(
                    "BoatDesyncFix",
                    "net/minecraft/entity/item/EntityBoat",
                    MappingRegistry.getMethodNameFor("EntityBoat.setIsBoatEmpty"),
                    "(Z)V"
                ));
                addPatcher(new BoatDesyncFixPatcher_Extra(
                    "BoatDesyncFix|Extra",
                    "net/minecraft/entity/item/EntityBoat",
                    MappingRegistry.getMethodNameFor("EntityBoat.setPositionAndRotation2"),
                    "(DDDFFI)V"
                ));
            }

            if (settings.ChickenLureTweakEnabled) {
                addPatcher(new ChickenLureTweakPatcher(
                        "ChickenLureTweak",
                        "net/minecraft/entity/passive/EntityChicken",
                        "<init>",
                        "(Lnet/minecraft/world/World;)V"
                ));
            }

            if (settings.HeartBlinkFixEnabled) {
                addPatcher(new HeartBlinkFixPatcher(
                    "HeartBlinkFix",
                    "net/minecraft/client/entity/EntityPlayerSP",
                    MappingRegistry.getMethodNameFor("EntityPlayerSP.setPlayerSPHealth"),
                    "(F)V"
                ));
            }

            if (settings.HeartFlashFixEnabled) {
                addPatcher(new HeartFlashFixPatcher(
                        "HeartFlashFix",
                        "net/minecraft/client/entity/EntityClientPlayerMP",
                        MappingRegistry.getMethodNameFor("EntityClientPlayerMP.attackEntityFrom"),
                        "(Lnet/minecraft/util/DamageSource;F)Z"
                ));

            }

            if (settings.HeartBlinkFixEnabled && settings.HeartFlashFixEnabled) {
//                addPatcher(new HeartFlashFixCompatPatcher(
//                        "HeartFlashFix|Compat",
//                        MappingRegistry.getClassNameFor("net/minecraft/client/entity/EntityClientPlayerMP"),
//                        MappingRegistry.getMethodNameFor("EntityClientPlayerMP.setPlayerSPHealth"),
//                        "(F)V"
//                ));
            }

            if (settings.ItemHopperBounceFixEnabled) {
                addPatcher(new ItemHopperBounceFixPatcher(
                        "ItemHopperBounceFix",
                        "net/minecraft/block/BlockHopper",
                        MappingRegistry.getMethodNameFor("BlockHopper.addCollisionBoxesToList"),
                        "(Lnet/minecraft/world/World;IIIL" +
                                "net/minecraft/util/AxisAlignedBB;Ljava/util/List;L" +
                                "net/minecraft/entity/Entity;)V"
                ));
            }

            if (settings.ItemStairBounceFixEnabled) {
                addPatcher(new ItemStairBounceFixPatcher(
                        "ItemStairBounceFix",
                        "net/minecraft/block/BlockStairs",
                        MappingRegistry.getMethodNameFor("BlockStairs.addCollisionBoxesToList"),
                        "(Lnet/minecraft/world/World;IIIL" +
                                "net/minecraft/util/AxisAlignedBB;Ljava/util/List;L" +
                                "net/minecraft/entity/Entity;)V"
                ));
            }

            if (settings.SnowballFixEnabled) {
                addPatcher(new SnowballFixPatcher(
                        "SnowballFix",
                        "net/minecraft/entity/player/EntityPlayer",
                        MappingRegistry.getMethodNameFor("EntityPlayer.attackEntityFrom"),
                        "(Lnet/minecraft/util/DamageSource;F)Z"
                ));
            }

            String sig2 = "(Lnet/minecraft/world/World;"
                    + "Ljava/util/Random;"
                    + "Lnet/minecraft/world/gen/structure/StructureBoundingBox;)Z";

            if (settings.VillageAnvilTweakEnabled) {
                addPatcher(new VillageAnvilTweakPatcher(
                        "VillageAnvilTweak",
                        "net/minecraft/world/gen/structure/StructureVillagePieces$House2",
                        MappingRegistry.getMethodNameFor("StructureVillagePieces$House2.addComponentParts"),
                        sig2 // break out into separate block above for readability
                ));
            }

            if (settings.gbEnableDebugger) {
                addPatcher(new GhostBusterHookPatcher(
                        "GhostBusterHook",
                        "net/minecraft/world/gen/ChunkProviderServer",
                        MappingRegistry.getMethodNameFor("ChunkProviderServer.provideChunk"),
                        null
                ));
            }

            if (settings.gbEnableFixes) {
                if (settings.gbFixGrassVanilla) {
                    addPatcher(GhostBusterEarlyReturnPatcher.updateTick(
                            "net/minecraft/block/BlockGrass", 3
                    ));
                }

                if (settings.gbFixGrassBOP) {
                    addPatcher(GhostBusterEarlyReturnPatcher.updateTick(
                            "biomesoplenty/common/blocks/BlockBOPGrass", 3
                    ));
                }

                if (settings.gbFixFluidsVanilla) {
                    addPatcher(new GhostBusterWrapperPatcher(
                            "GhostBusterWrapStaticLiquid",
                            "net/minecraft/block/BlockStaticLiquid",
                            MappingRegistry.getMethodNameFor("Block.updateTick"),
                            null
                    ));
                    addPatcher(new GhostBusterWrapperPatcher(
                            "GhostBusterWrapStaticLiquid",
                            "net/minecraft/block/BlockStaticLiquid",
                            MappingRegistry.getMethodNameFor("BlockStaticLiquid.isFlammable"),
                            null
                    ));

                    addPatcher(GhostBusterEarlyReturnPatcher.updateTick(
                            "net/minecraft/block/BlockDynamicLiquid", 4 /* 1.7.10 max slope distance */
                    ));
                }

                if (settings.gbFixFluidsModded) {
                    addPatcher(GhostBusterEarlyReturnPatcher.updateTick(
                            "net/minecraftforge/fluids/BlockFluidClassic", 4 /* recursionDepth limit */
                    ));
                    addPatcher(GhostBusterEarlyReturnPatcher.updateTick(
                            "net/minecraftforge/fluids/BlockFluidFinite", 1
                    ));
                }

                if (settings.gbFixVinesVanilla) {
                    addPatcher(GhostBusterEarlyReturnPatcher.updateTick(
                            "net/minecraft/block/BlockVine", 4
                    ));
                }
            }

            if (settings.bfJarDiscovererMemoryLeakFixEnabled) {
                addPatcher(new JarDiscovererMemoryLeakFixPatcher(
                        "JarDiscovererMemoryLeakFix",
                        "cpw/mods/fml/common/discovery/JarDiscoverer",
                        "discover",
                        "(Lcpw/mods/fml/common/discovery/ModCandidate;Lcpw/mods/fml/common/discovery/ASMDataTable;)Ljava/util/List;"
                ));
            }

            if (settings.bfSoundSystemUnpauseFixEnabled) {
                addPatcher(new SoundSystemUnpauseFixPatcher(
                        "SoundSystemUnpauseFix",
                        "net/minecraft/client/audio/SoundManager"
                ));
            }

            if (settings.bfEntityHeldItemNBTRenderFixEnabled) {
                addPatcher(new EntityLivingBaseItemNBTRenderFixPatcher(
                        "EntityHeldItemNBTRenderFix",
                        "net/minecraft/entity/EntityLivingBase"
                ));
            }

            if (settings.bfAlphaPassTessellatorCrashFixEnabled) {
                addPatcher(new TessellatorAlphaPassWrapFixPatcher(
                        "AlphaPassTessellatorCrashFix",
                        "net/minecraft/client/renderer/Tessellator"
                ));
            }

            if (settings.bfLog4JExploitFixEnabled) {
                addPatcher(new Log4JLoggerWrapperPatcher("Log4JExploitFix"));
            }
        }
    }

    private void addPatcher(AbstractPatcher patcher) {
        if (patcher.getTargetClassName() == null || patcher.getTargetClassName().isEmpty()) {
            globalPatchers.add(patcher);
            globalPatchers.trimToSize();
        } else {
            ArrayList<AbstractPatcher> list = patchers.computeIfAbsent(patcher.getTargetClassName(), k -> new ArrayList<>());
            list.add(patcher);
            list.trimToSize();
        }
    }
}
