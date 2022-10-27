package eu.asangarin.arikeys.fabric;

import eu.asangarin.arikeys.AriKeys;
import net.fabricmc.api.ClientModInitializer;

public class AriKeysFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AriKeys.init();
	}
}