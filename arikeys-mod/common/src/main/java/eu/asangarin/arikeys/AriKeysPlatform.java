package eu.asangarin.arikeys;

import dev.architectury.injectables.annotations.ExpectPlatform;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.Collection;

public class AriKeysPlatform {
	@ExpectPlatform
	public static void sendHandshake() {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static void sendKey(KeyPressData data) {
		throw new AssertionError();
	}

	@ExpectPlatform
	public static Collection<KeyBinding> getKeyBinding(InputUtil.Key code) {
		throw new AssertionError();
	}
}
