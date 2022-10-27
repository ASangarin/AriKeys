package eu.asangarin.arikeys.forge.network;

import eu.asangarin.arikeys.util.AriKeysIO;
import net.minecraft.network.PacketByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LoadHandler {
	public static void encode(LoadHandler handler, PacketByteBuf buf) {
	}

	public static LoadHandler decode(PacketByteBuf buf) {
		return new LoadHandler();
	}

	public static void consume(LoadHandler handler, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(AriKeysIO::load);
	}
}
