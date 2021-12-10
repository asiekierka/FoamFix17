package pl.asie.foamfix.repack.com.unascribed.ears.asm;

import java.util.List;

import pl.asie.foamfix.repack.com.unascribed.ears.common.agent.mini.MiniTransformer;

public class Patches {

	public static void addTransformers(List<MiniTransformer> out) {
		out.add(new ImageBufferDownloadTransformer());
		out.add(new RenderPlayerTransformer());
		out.add(new ThreadDownloadImageDataTransformer());
		out.add(new GalacticraftRenderPlayerTransformer());
	}
	
}
