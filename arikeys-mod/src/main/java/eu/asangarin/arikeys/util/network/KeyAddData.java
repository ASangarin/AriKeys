package eu.asangarin.arikeys.util.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@Getter
@RequiredArgsConstructor
public class KeyAddData {
	private final Identifier id;
	private final String name, category;
	private final int defKey;
	private final int[] modifiers;

	public static KeyAddData fromBuffer(PacketByteBuf buf) {
		buf.readByte();
		String path = buf.readString();
		String key = buf.readString();
		int defKey = buf.readInt();
		String name = buf.readString();
		String category = buf.readString();
		int[] modifiers = buf.readIntArray();

		Identifier id = Identifier.of(path, key);
		return new KeyAddData(id, name, category, defKey, modifiers);
	}
}
