package eu.asangarin.arikeys.util;

import eu.asangarin.arikeys.util.network.KeyAddData;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class AriKeysPayloads {
	public record Handshake() implements CustomPayload {
		public static final Id<Handshake> ID = new Id<>(AriKeysChannels.HANDSHAKE_CHANNEL);
		public static final PacketCodec<RegistryByteBuf, Handshake> CODEC = PacketCodec.of(Handshake::write, Handshake::read);

		private void write(RegistryByteBuf buf) {}

		private static Handshake read(RegistryByteBuf buf) {
			readFully(buf);
			return new Handshake();
		}

		@Override
		public Id<Handshake> getId() {
			return ID;
		}
	}

	public record AddKey(KeyAddData data) implements CustomPayload {
		public static final Id<AddKey> ID = new Id<>(AriKeysChannels.ADD_KEY_CHANNEL);
		public static final PacketCodec<RegistryByteBuf, AddKey> CODEC = PacketCodec.of(AddKey::write, AddKey::read);

		private void write(RegistryByteBuf buf) {}

		private static AddKey read(RegistryByteBuf buf) {
			KeyAddData keyData = KeyAddData.fromBuffer(buf);
			readFully(buf);
			return new AddKey(keyData);
		}

		@Override
		public Id<AddKey> getId() {
			return ID;
		}
	}

	public record Load() implements CustomPayload {
		public static final Id<Load> ID = new Id<>(AriKeysChannels.LOAD_CHANNEL);
		public static final PacketCodec<RegistryByteBuf, Load> CODEC = PacketCodec.of(Load::write, Load::read);

		private void write(RegistryByteBuf buf) {}

		private static Load read(RegistryByteBuf buf) {
			readFully(buf);
			return new Load();
		}

		@Override
		public Id<Load> getId() {
			return ID;
		}
	}

	public record Key(KeyPressData data) implements CustomPayload {
		public static final Id<Key> ID = new Id<>(AriKeysChannels.KEY_CHANNEL);
		public static final PacketCodec<RegistryByteBuf, Key> CODEC = PacketCodec.of(Key::write, Key::read);

		private void write(RegistryByteBuf buf) {
			buf.writeByte(0);
			data.write(buf);
		}

		private static Key read(RegistryByteBuf buf) {
			readFully(buf);
			return new Key(null);
		}

		@Override
		public Id<Key> getId() {
			return ID;
		}
	}

	protected static void readFully(PacketByteBuf buf) {
		while(buf.readableBytes() != 0)
			buf.readByte();
	}
}
