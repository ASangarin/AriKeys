package eu.asangarin.arikeys.neoforge;

import eu.asangarin.arikeys.neoforge.mixin.AKKeyboardNeoForgeMixin;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysHandshakePayload;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysKeyPressPayload;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;

public class AriKeysPlatformImpl {
	public static void sendHandshake() {
		PacketDistributor.SERVER.noArg().send(new AriKeysHandshakePayload());
	}

	public static Collection<KeyBinding> getKeyBinding(InputUtil.Key code) {
		return AKKeyboardNeoForgeMixin.getKeyBindings().getAll(code);
	}

	public static void sendKey(KeyPressData data) {
		System.out.println("Sending key press?");
		PacketDistributor.SERVER.noArg().send(new AriKeysKeyPressPayload(data));
	}
}
