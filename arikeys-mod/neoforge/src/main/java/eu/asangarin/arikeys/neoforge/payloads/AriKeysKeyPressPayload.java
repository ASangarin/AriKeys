package eu.asangarin.arikeys.neoforge.payloads;

import eu.asangarin.arikeys.neoforge.AriKeysNeoForge;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AriKeysKeyPressPayload(KeyPressData data) implements CustomPayload {
	public AriKeysKeyPressPayload(PacketByteBuf buf) {
		this((KeyPressData) null);
		AriKeysNeoForge.readFully(buf);
	}

	@Override
	public void write(PacketByteBuf buf) {
		buf.writeByte(0);
		data.write(buf);
	}

	@Override
	public Identifier id() {
		return AriKeysChannels.KEY_CHANNEL;
	}
}
