package eu.asangarin.arikeys.util;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;

public final class PacketByteBufs {
	public static PacketByteBuf create() {
		return new PacketByteBuf(Unpooled.buffer());
	}
}
