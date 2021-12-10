package pl.asie.foamfix.repack.com.unascribed.ears.asm;

import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.annotation.Patch;
import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.MiniTransformer;
import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.PatchContext;
import pl.asie.foamfix.repack.com.unascribed.ears.common.debug.EarsLog;

@Patch.Class("net.minecraft.client.renderer.ThreadDownloadImageData")
public class ThreadDownloadImageDataTransformer extends MiniTransformer {

	@Patch.Method("func_147641_a(Ljava/awt/image/BufferedImage;)V")
	public void patchSetBufferedImage(PatchContext ctx) {
		EarsLog.debug("Platform:Inject", "Patching setBufferedImage");
		ctx.jumpToStart();
		// EarsMod.checkSkin(this, ...);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("pl/asie/foamfix/repack/com/unascribed/ears/Ears", "checkSkin", "(Lnet/minecraft/client/renderer/ThreadDownloadImageData;Ljava/awt/image/BufferedImage;)V")
		);
	}
	
}
