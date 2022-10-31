package eu.asangarin.arikeys.forge;

import eu.asangarin.arikeys.forge.mixin.AKKeyboardForgeMixin;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class AriKeysPlatformImpl {
	public static void sendHandshake() {
		AriKeysForge.HANDSHAKE.sendToServer(new Object());
	}

	public static KeyBinding getKeyBinding(InputUtil.Key code) {
		return AKKeyboardForgeMixin.getKeyBindings().get(code);
	}

	public static void sendKey(KeyPressData data) {
		AriKeysForge.KEY.sendToServer(data);
	}
}
