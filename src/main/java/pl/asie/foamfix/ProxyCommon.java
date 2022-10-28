package pl.asie.foamfix;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class ProxyCommon {
    public void openUrlLinux(URI url) {

    }

    public static File createFileSafe(String parent, String filename) {
        Path parentPath = new File(parent).toPath().normalize().toAbsolutePath();
        File childFile = new File(parent, filename);
        if (!childFile.toPath().toAbsolutePath().normalize().startsWith(parentPath)) {
            FoamFixMod.logger.error("Caught potential RCE attempt: tried to create File at " + parent + " with suffix " + filename, new Throwable());
            File safeChildFile = new File(parent, filename.replaceAll("[\\\\/]", "_"));
            if (!safeChildFile.toPath().toAbsolutePath().normalize().startsWith(parentPath)) {
                throw new RuntimeException("Could not sanitize filename: " + parent + " + " + filename);
            }
            return safeChildFile;
        } else {
            return childFile;
        }
    }

    public static File createFileSafe(File parent, String filename) {
        Path parentPath = parent.toPath().toAbsolutePath().normalize();
        File childFile = new File(parent, filename);
        if (!childFile.toPath().toAbsolutePath().normalize().startsWith(parentPath)) {
            FoamFixMod.logger.error("Caught potential RCE attempt: tried to create File at " + parent + " with suffix " + filename, new Throwable());
            File safeChildFile = new File(parent, filename.replaceAll("[\\\\/]", "_"));
            if (!safeChildFile.toPath().toAbsolutePath().normalize().startsWith(parentPath)) {
                throw new RuntimeException("Could not sanitize filename: " + parent + " + " + filename);
            }
            return safeChildFile;
        } else {
            return childFile;
        }
    }
}
