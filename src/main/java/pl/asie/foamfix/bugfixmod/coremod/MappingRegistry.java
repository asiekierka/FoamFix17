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

import com.google.common.collect.Maps;
import pl.asie.foamfix.bugfixmod.BugfixModSettings;

import java.util.Map;

/**
 * Created by Vincent on 6/6/2014.
 */
public class MappingRegistry {
    private static Map<String, String> fieldMap = Maps.newHashMap();
    private static Map<String, String> methodMap = Maps.newHashMap();

    private static boolean hasInit = false;
    private static boolean isObf;

    public static void init(boolean isObf) {
        if (!hasInit) {
            MappingRegistry.isObf = isObf;
            if (MappingRegistry.isObf) {
                BugfixModSettings settings = BugfixModClassTransformer.instance.settings;

                if (settings.BoatDesyncFixEnabled) {
                    methodMap.put("EntityBoat.setPositionAndRotation2", "func_70056_a");
                    methodMap.put("EntityBoat.setIsBoatEmpty", "func_70270_d")
;
                }

                if (settings.ChickenLureTweakEnabled) {
                    fieldMap.put("EntityChicken.tasks", "field_70714_bg");
                    methodMap.put("EntityAITasks.addTask", "func_75776_a");
                    fieldMap.put("Items.pumpkin_seeds", "field_151080_bb");
                    fieldMap.put("Items.melon_seeds", "field_151081_bc");
                    fieldMap.put("Items.nether_wart", "field_151075_bm");
                }

                if (settings.HeartBlinkFixEnabled) {
                    methodMap.put("EntityPlayerSP.setPlayerSPHealth", "func_71150_b");
                }

                if (settings.HeartFlashFixEnabled) {
                    fieldMap.put("EntityClientPlayerMP.prevHealth", "field_70735_aL");
                    methodMap.put("EntityClientPlayerMP.getHealth", "func_110143_aJ");
                    methodMap.put("EntityClientPlayerMP.attackEntityFrom", "func_70097_a");
                    methodMap.put("EntityClientPlayerMP.setPlayerSPHealth", "func_71150_b");
                }

                if (settings.ItemHopperBounceFixEnabled) {
                    methodMap.put("BlockHopper.addCollisionBoxesToList", "func_149743_a");
                    methodMap.put("BlockHopper.setBlockBounds", "func_149676_a");
                }

                if (settings.ItemStairBounceFixEnabled) {
                    methodMap.put("BlockStairs.addCollisionBoxesToList", "func_149743_a");
                    methodMap.put("BlockStairs.setBlockBounds", "func_149676_a");
                }

                if (settings.SnowballFixEnabled) {
                    methodMap.put("EntityPlayer.attackEntityFrom", "func_70097_a");
                }

                if (settings.VillageAnvilTweakEnabled) {
                    methodMap.put("StructureVillagePieces$House2.addComponentParts", "func_74875_a");
                    fieldMap.put("Blocks.anvil", "field_150467_bQ");
                    fieldMap.put("Blocks.double_stone_slab", "field_150334_T");
                }

                if (settings.gbEnableDebugger) {
                    methodMap.put("ChunkProviderServer.provideChunk", "func_73154_d");
                }

                if (settings.gbFixFluidsVanilla) {
                    methodMap.put("BlockStaticLiquid.isFlammable", "func_149817_o");
                }

                // ghost buster - general
                methodMap.put("Block.updateTick", "func_149674_a");
                methodMap.put("IBlockAccess.getBlock", "func_147439_a");
                methodMap.put("IBlockAccess.isAirBlock", "func_147437_c");
            }
            hasInit = true;
        }
    }

    public static String getFieldNameFor(String request) {
        // par1 will be in the format className.fieldName
        if (!isObf) {
            return request.substring(request.lastIndexOf(".") + 1); // return second half, the fieldname
        } else {
            String get = fieldMap.get(request);
            if (get == null) {
                BugfixModClassTransformer.instance.logger.warn("MappingRegistry just returned null for field lookup: " + request);
            }
            return get;
        }
    }

    public static String getMethodNameFor(String request) {
        // par1 will be in the format className.methodName
        if (!isObf) {
            return request.substring(request.lastIndexOf(".") + 1); // return second half, the methodname
        } else {
            String get = methodMap.get(request);
            if (get == null) {
                BugfixModClassTransformer.instance.logger.warn("MappingRegistry just returned null for method lookup: " + request);
            }
            return get;
        }
    }
}
