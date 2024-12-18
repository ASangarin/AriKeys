package eu.asangarin.arikeys.fabric;

import eu.asangarin.arikeys.fabric.mixin.AKKeyboardFabricMixin;
import eu.asangarin.arikeys.util.AriKeysPayloads;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.Collection;
import java.util.Collections;

public class AriKeysPlatformImpl {
	public static void sendHandshake() {
		ClientPlayNetworking.send(new AriKeysPayloads.Handshake());
	}

	public static Collection<KeyBinding> getKeyBinding(InputUtil.Key code) {
		return Collections.singleton(AKKeyboardFabricMixin.getKeyBindings().get(code));
	}

	public static void sendKey(KeyPressData data) {
		ClientPlayNetworking.send(new AriKeysPayloads.Key(data));
	}
}
