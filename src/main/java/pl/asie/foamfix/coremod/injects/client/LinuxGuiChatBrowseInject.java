package pl.asie.foamfix.coremod.injects.client;

import net.minecraft.util.Util;
import pl.asie.foamfix.FoamFixMod;

import java.net.URI;

public class LinuxGuiChatBrowseInject {
    private void func_146407_a(URI url) {
        Util.EnumOS osType = Util.getOSType();
        if (osType == Util.EnumOS.LINUX || osType == Util.EnumOS.UNKNOWN) {
            FoamFixMod.proxy.openUrlLinux(url);
        } else {
            func_146407_a_foamfix_old(url);
        }
    }

    private void func_146407_a_foamfix_old(URI url) {

    }
}