package eu.asangarin.arikeys.neoforge.payloads;

import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.network.KeyAddData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AriKeysAddKeyPayload(KeyAddData keyAddData) implements CustomPayload {
	public AriKeysAddKeyPayload(KeyAddData data, boolean test) {
		this(data);
		System.out.println("Testing stuff...");
	}

	@Override
	public void write(PacketByteBuf buf) {
	}

	@Override
	public Identifier id() {
		return AriKeysChannels.ADD_KEY_CHANNEL;
	}
}
