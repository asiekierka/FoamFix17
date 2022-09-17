package pl.asie.foamfix;

import java.net.URI;

public class ProxyClient extends ProxyCommon {
    @Override
    public void openUrlLinux(URI url) {
        try {
            new ProcessBuilder().command("xdg-open", url.toString()).start();
        } catch (Throwable t) {
            FoamFixMod.logger.error("Couldn't open link!", t);
        }
    }
}
