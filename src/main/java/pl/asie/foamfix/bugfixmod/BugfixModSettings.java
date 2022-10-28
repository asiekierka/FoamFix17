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

package pl.asie.foamfix.bugfixmod;

import java.io.File;

public class BugfixModSettings {
    // Coremod
    public boolean SnowballFixEnabled;
    public boolean ChickenLureTweakEnabled;
    public boolean VillageAnvilTweakEnabled;
    public boolean HeartFlashFixEnabled;
    public boolean ItemHopperBounceFixEnabled;
    public boolean ItemStairBounceFixEnabled;
    public boolean HeartBlinkFixEnabled;
    public boolean BoatDesyncFixEnabled;

    public boolean bfJarDiscovererMemoryLeakFixEnabled;
    public boolean bfSoundSystemUnpauseFixEnabled;
    public boolean bfEntityHeldItemNBTRenderFixEnabled;
    public boolean bfAlphaPassTessellatorCrashFixEnabled;
    public boolean bfLog4JExploitFixEnabled;
    public boolean bfBiblioRCEFixEnabled;

    public boolean clOpenUrlLinux;

    public boolean mc18SkinSupport;

    public boolean lwWeakenResourceCache;
    public boolean lwRemovePackageManifestMap;

    public boolean helloMmcg;

    // Mod
    public boolean ArrowDingTweakEnabled;
    public boolean ToolDesyncFixEnabled;

    // Ghostbuster
    public String gbDebuggerLogFilePath;
    public File gbDebuggerLogFile;
    public boolean gbEnableDebugger;
    public boolean gbEnableFixes;
    public boolean gbFixGrassVanilla;
    public boolean gbFixGrassBOP;
    public boolean gbFixFluidsVanilla;
    public boolean gbFixFluidsModded;
    public boolean gbFixVinesVanilla;
}
