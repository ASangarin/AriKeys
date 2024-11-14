package eu.asangarin.arikeys.forge;

import eu.asangarin.arikeys.forge.mixin.AKKeyboardForgeMixin;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraftforge.network.PacketDistributor;

import java.util.Collection;

public class AriKeysPlatformImpl {
	public static void sendHandshake() {
		AriKeysForge.HANDSHAKE.send(new Object(), PacketDistributor.SERVER.noArg());
	}

	public static Collection<KeyBinding> getKeyBinding(InputUtil.Key code) {
		return AKKeyboardForgeMixin.getKeyBindings().getAll(code);
	}

	public static void sendKey(KeyPressData data) {
		AriKeysForge.KEY.send(data, PacketDistributor.SERVER.noArg());
	}
}
