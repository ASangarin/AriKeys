package eu.asangarin.arikeys;

import eu.asangarin.arikeys.api.AriKeyPressEvent;
import eu.asangarin.arikeys.api.AriKeyReleaseEvent;
import eu.asangarin.arikeys.compat.MythicMobsCompat;
import eu.asangarin.arikeys.config.AriKeyInfo;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.ModifierKey;
import eu.asangarin.arikeys.util.PressType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class AriKeysNetwork {
	public static final short DEFAULT_MAX_STRING_LENGTH = Short.MAX_VALUE;

	public static void receiveKeyPress(Player player, ByteBuf buf) {
		try {
			// Read the key press ID then call the AriKeyPress event.
			buf.readByte(); // Forge compatability
			String namespace = readString(buf);
			String key = readString(buf);
			boolean firstPress = !buf.readBoolean();
			NamespacedKey id = NamespacedKey.fromString(namespace + ":" + key);

			if (AriKeysPlugin.get().getConf().getKeyInfoList().containsKey(id)) {
				AriKeyInfo info = AriKeysPlugin.get().getConf().getKeyInfoList().get(id);
				boolean eventCmd = AriKeysPlugin.get().getConf().isEventOnCommand();

				if (firstPress) {
					info.getExecutor().execute(PressType.PRESS);
					if (!info.runCommand(player) || eventCmd) Bukkit.getPluginManager().callEvent(new AriKeyPressEvent(player, id, true));
					if (info.hasMM(true)) info.mmSkill(player, true);
					if (AriKeysPlugin.get().mm){
						MythicMobsCompat.runSkills(true, id, player);
					}
					return;
				}

				info.getExecutor().execute(PressType.RELEASE);
				if (!info.hasCommand() || eventCmd) Bukkit.getPluginManager().callEvent(new AriKeyReleaseEvent(player, id, true));
				if (info.hasMM(false)) info.mmSkill(player, false);
				MythicMobsCompat.runSkills(false, id, player);

			} else {
				Bukkit.getPluginManager()
						.callEvent(firstPress ? new AriKeyPressEvent(player, id, false) : new AriKeyReleaseEvent(player, id, false));
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	public static void receiveGreeting(Player player) {
		/* Send this server's specified keybindings to the
		 client. This is delayed to make sure the client is properly
		 connected before attempting to send any data over. */
		Bukkit.getScheduler().runTaskLater(AriKeysPlugin.get(), () -> {
			for (AriKeyInfo info : AriKeysPlugin.get().getConf().getKeyInfoList().values())
				sendKeyInformation(player, info.getId(), info.getDef(), info.getName(), info.getCategory(), info.getModifiers());

			/* Send the "load" packet after sending every keybinding packet, to tell
			 the client to load all the user-specific keybinds saved on their machine.
			 Have to write a single byte here for Forge compatability, as the Forge
			 networking implementation requires an identification byte from each packet. */
			ByteBuf buf = Unpooled.buffer();
			buf.writeByte(0);
			player.sendPluginMessage(AriKeysPlugin.get(), AriKeysChannels.LOAD_KEYS, buf.array());
		}, 20);
	}

	// Simply send over the information in an add key packet.
	public static void sendKeyInformation(Player player, NamespacedKey id, int def, String name, String category, Set<ModifierKey> modifiers) {
		ByteBuf buf = Unpooled.buffer();
		buf.writeByte(0);
		writeString(buf, id.getNamespace());
		writeString(buf, id.getKey());
		buf.writeInt(def);
		writeString(buf, name);
		writeString(buf, category);
		int[] modArray = modifiers.stream().mapToInt(ModifierKey::getId).toArray();
		writeIntArray(buf, modArray);
		player.sendPluginMessage(AriKeysPlugin.get(), AriKeysChannels.ADD_KEY, buf.array());
	}

	/* <!!! Important Note !!!>
	 The next following methods are rewritten from Minecrafts "PacketByteBuf" class.
	 These are neccessary for reading the String data that's being sent from the client.
	 version 2.1: New methods added for reading int arrays */

	private static String readString(ByteBuf buf) throws IOException {
		int i = DEFAULT_MAX_STRING_LENGTH * 3;
		int j = readVarInt(buf);
		if (j > i) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i + ")");
		}
		if (j < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		}
		String s = buf.toString(buf.readerIndex(), j, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + j);
		if (s.length() > DEFAULT_MAX_STRING_LENGTH) {
			throw new DecoderException(
					"The received string length is longer than maximum allowed (" + s.length() + " > " + DEFAULT_MAX_STRING_LENGTH + ")");
		}
		return s;
	}

	private static int readVarInt(ByteBuf buf) {
		byte b0;
		int i = 0;
		int j = 0;
		//noinspection InfiniteLoopStatement
		do {
			b0 = buf.readByte();
			i |= (b0 & 0x7F) << j++ * 7;
			if (j <= 5) continue;
			throw new RuntimeException("VarInt too big");
		} while ((b0 & 0x80) == 128);
		return i;
	}

	private static void writeString(ByteBuf buf, String string) {
		if (string.length() > DEFAULT_MAX_STRING_LENGTH) {
			int var10002 = string.length();
			throw new EncoderException("String too big (was " + var10002 + " characters, max " + DEFAULT_MAX_STRING_LENGTH + ")");
		} else {
			byte[] bs = string.getBytes(StandardCharsets.UTF_8);
			int i = DEFAULT_MAX_STRING_LENGTH * 3;
			if (bs.length > i) {
				throw new EncoderException("String too big (was " + bs.length + " bytes encoded, max " + i + ")");
			} else {
				writeVarInt(buf, bs.length);
				buf.writeBytes(bs);
			}
		}
	}

	private static void writeVarInt(ByteBuf buf, int value) {
		while ((value & -128) != 0) {
			buf.writeByte(value & 127 | 128);
			value >>>= 7;
		}

		buf.writeByte(value);
	}

	/* I may never need to read an int array on this side, but I'm keeping it just in case...

	public static int[] readIntArray(ByteBuf buf) {
		return readIntArray(buf, buf.readableBytes());
	}

	public static int[] readIntArray(ByteBuf buf, int maxSize) {
		int i = readVarInt(buf);
		if (i > maxSize) {
			throw new DecoderException("VarIntArray with size " + i + " is bigger than allowed " + maxSize);
		} else {
			int[] is = new int[i];

			for (int j = 0; j < is.length; ++j)
				is[j] = readVarInt(buf);

			return is;
		}
	}*/

	public static void writeIntArray(ByteBuf buf, int[] array) {
		writeVarInt(buf, array.length);

		for (int i : array)
			writeVarInt(buf, i);
	}
}
