package pl.asie.foamfix.repack.com.unascribed.ears.api.iface;

import pl.asie.foamfix.repack.com.unascribed.ears.api.EarsFeatureType;
import pl.asie.foamfix.repack.com.unascribed.ears.api.registry.EarsInhibitorRegistry;

public interface EarsInhibitor {

	/**
	 * @see EarsInhibitorRegistry#register(String, EarsInhibitor)
	 */
	boolean shouldInhibit(EarsFeatureType type, Object peer);
	
}
