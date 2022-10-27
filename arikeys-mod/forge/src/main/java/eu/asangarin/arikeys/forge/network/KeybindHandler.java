package eu.asangarin.arikeys.forge.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public class KeybindHandler {
	private final Identifier id;
	private final boolean release;

	public static void encode(KeybindHandler handler, PacketByteBuf buf) {
		buf.writeString(handler.id.getNamespace());
		buf.writeString(handler.id.getPath());
		buf.writeBoolean(handler.release);
		System.out.println("Encoded Keybind");
	}

	public static KeybindHandler decode(PacketByteBuf buf) {
		System.out.println("Decoded Keybind");
		return new KeybindHandler(null, false);
	}

	public static void consume(KeybindHandler handler, Supplier<NetworkEvent.Context> context) {
		System.out.println("Consumed Keybind");
	}
}
