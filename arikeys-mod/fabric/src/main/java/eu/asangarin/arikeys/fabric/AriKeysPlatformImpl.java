package eu.asangarin.arikeys.fabric;

import eu.asangarin.arikeys.fabric.mixin.AKKeyboardFabricMixin;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;
import java.util.Collections;

public class AriKeysPlatformImpl {
	public static void sendHandshake() {
		ClientPlayNetworking.send(AriKeysChannels.HANDSHAKE_CHANNEL, PacketByteBufs.empty());
	}

	public static Collection<KeyBinding> getKeyBinding(InputUtil.Key code) {
		return Collections.singleton(AKKeyboardFabricMixin.getKeyBindings().get(code));
	}

	public static void sendKey(KeyPressData data) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeByte(0);
		data.write(buf);
		ClientPlayNetworking.send(AriKeysChannels.KEY_CHANNEL, buf);
	}
}
