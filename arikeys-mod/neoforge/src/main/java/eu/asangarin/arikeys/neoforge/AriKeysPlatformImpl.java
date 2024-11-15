package eu.asangarin.arikeys.neoforge;

import eu.asangarin.arikeys.neoforge.mixin.AKKeyboardNeoForgeMixin;
import eu.asangarin.arikeys.util.AriKeysPayloads;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

public class AriKeysPlatformImpl {
	public static void sendHandshake() {
		PacketDistributor.sendToServer(new AriKeysPayloads.Handshake());
	}

	public static Collection<KeyBinding> getKeyBinding(InputUtil.Key code) {
		return AKKeyboardNeoForgeMixin.getKeyBindings().getAll(code);
	}

	public static void sendKey(KeyPressData data) {
		PacketDistributor.sendToServer(new AriKeysPayloads.Key(data));
	}
}
