package pl.asie.foamfix.repack.com.unascribed.ears;

import java.util.UUID;

import pl.asie.foamfix.repack.com.unascribed.ears.api.features.EarsFeatures;

public interface EarsFeaturesLookup {
	EarsFeatures getById(UUID id);
	EarsFeatures getByUsername(String username);
}