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

package pl.asie.foamfix.bugfixmod.mod;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraftforge.event.world.BlockEvent;
import pl.asie.foamfix.bugfixmod.coremod.BugfixModClassTransformer;

import java.lang.reflect.Method;

public class ToolDesyncFixEventHandler {
    private final Method syncItemMth;

    public ToolDesyncFixEventHandler() {
        syncItemMth = ReflectionHelper.findMethod(PlayerControllerMP.class, null, new String[] {"syncCurrentPlayItem", "func_78750_j"});
        if (syncItemMth != null) {
            syncItemMth.setAccessible(true);
        }
    }

    public boolean isValid() {
        return syncItemMth != null;
    }

    @SubscribeEvent
    public void blockBreak(BlockEvent.BreakEvent evt) {
        try {
            syncItemMth.invoke(Minecraft.getMinecraft().playerController);
        } catch (Exception ex) {
            BugfixModClassTransformer.instance.logger.warn("ToolDesyncFix failed to resync");
        }
    }
}
