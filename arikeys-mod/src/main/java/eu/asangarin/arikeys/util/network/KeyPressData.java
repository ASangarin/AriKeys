package eu.asangarin.arikeys.util.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

@RequiredArgsConstructor
public class KeyPressData {
	private final Identifier id;
	private final boolean release;

	public void write(PacketByteBuf buf) {
		buf.writeString(id.getNamespace());
		buf.writeString(id.getPath());
		buf.writeBoolean(release);
	}
}
