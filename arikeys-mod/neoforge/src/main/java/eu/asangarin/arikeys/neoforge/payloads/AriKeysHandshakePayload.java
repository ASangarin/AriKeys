package eu.asangarin.arikeys.neoforge.payloads;

import eu.asangarin.arikeys.neoforge.AriKeysNeoForge;
import eu.asangarin.arikeys.util.AriKeysChannels;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AriKeysHandshakePayload() implements CustomPayload {
	public AriKeysHandshakePayload(PacketByteBuf buf) {
		this();
		AriKeysNeoForge.readFully(buf);
	}

	@Override
	public void write(final PacketByteBuf buf) {}

	@Override
	public Identifier id() {
		return AriKeysChannels.HANDSHAKE_CHANNEL;
	}
}
