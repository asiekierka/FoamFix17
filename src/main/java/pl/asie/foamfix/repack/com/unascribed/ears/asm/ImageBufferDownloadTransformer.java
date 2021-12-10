package pl.asie.foamfix.repack.com.unascribed.ears.asm;

import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.annotation.Patch;
import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.MiniTransformer;
import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.PatchContext;

@Patch.Class("net.minecraft.client.renderer.ImageBufferDownload")
public class ImageBufferDownloadTransformer extends MiniTransformer {
	
	@Patch.Method("func_78432_a(Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;")
	@Patch.Method.AffectsControlFlow
	public void patchParseUserSkin(PatchContext ctx) {
		ctx.jumpToStart();
		// return EarsMod.interceptParseUserSkin(this, ...);
		ctx.add(
			ALOAD(0),
			ALOAD(1),
			INVOKESTATIC("pl/asie/foamfix/repack/com/unascribed/ears/Ears", "interceptParseUserSkin", "(Lnet/minecraft/client/renderer/ImageBufferDownload;Ljava/awt/image/BufferedImage;)Ljava/awt/image/BufferedImage;"),
			ARETURN()
		);
	}

}
