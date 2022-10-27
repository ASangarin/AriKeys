package eu.asangarin.arikeys.forge.network;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public class AddKeyHandler {
	private final Identifier id;
	private final String name, category;
	private final int defKey;

	public static void encode(AddKeyHandler handler, PacketByteBuf buf) {
	}

	public static AddKeyHandler decode(PacketByteBuf buf) {
		buf.readString();
		String path = buf.readString();
		buf.readString();
		String key = buf.readString();
		int defKey = buf.readInt();
		buf.readString();
		String name = buf.readString();
		buf.readString();
		String category = buf.readString();

		Identifier id = new Identifier(path, key);
		return new AddKeyHandler(id, name, category, defKey);
	}

	public static void consume(AddKeyHandler handler, Supplier<NetworkEvent.Context> context) {
		System.out.println("Added Key Handler: " + handler.id + " | " + handler.name + ", " + handler.category + " | " + handler.defKey);
		context.get().enqueueWork(() -> AriKeys.add(handler.id,
				new AriKey(handler.id, handler.name, handler.category, InputUtil.Type.KEYSYM.createFromCode(handler.defKey))));
	}
}
