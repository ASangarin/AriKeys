package eu.asangarin.arikeys.forge.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HandshakeHandler {
	public static void encode(HandshakeHandler handler, PacketByteBuf buf) {
	}

	@SuppressWarnings("InstantiationOfUtilityClass")
	public static HandshakeHandler decode(PacketByteBuf buf) {
		return new HandshakeHandler();
	}

	public static void consume(HandshakeHandler handler, Supplier<NetworkEvent.Context> context) {
	}
}
