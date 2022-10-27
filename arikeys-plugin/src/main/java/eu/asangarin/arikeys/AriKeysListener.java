package eu.asangarin.arikeys;

import eu.asangarin.arikeys.api.AriKeyPressEvent;
import eu.asangarin.arikeys.api.AriKeyReleaseEvent;
import eu.asangarin.arikeys.config.AriKeyInfo;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class AriKeysListener implements PluginMessageListener {
	@Override
	public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] message) {
		System.out.println(channel);
		// When receiving a handshake or key press, forward to their respective methods.
		if (channel.equalsIgnoreCase(AriKeysChannels.HANDSHAKE)) receiveGreeting(player);
		else if (channel.equalsIgnoreCase(AriKeysChannels.KEY_PRESS))
			receiveKeyPress(player, new DataInputStream(new ByteArrayInputStream(message)));
	}

	public void receiveKeyPress(Player player, DataInputStream buf) {
		try {
			// Read the key press ID then call the AriKeyPress event.
			buf.readByte();
			String namespace = buf.readUTF();
			String key = buf.readUTF();
			boolean firstPress = !buf.readBoolean();
			NamespacedKey id = NamespacedKey.fromString(namespace + ":" + key);

			if (AriKeysPlugin.get().getConf().getKeyInfoList().containsKey(id)) {
				AriKeyInfo info = AriKeysPlugin.get().getConf().getKeyInfoList().get(id);
				boolean eventCmd = AriKeysPlugin.get().getConf().isEventOnCommand();

				if (firstPress) {
					if (!info.runCommand(player) || eventCmd) Bukkit.getPluginManager().callEvent(new AriKeyPressEvent(player, id, true));
					info.mmSkill(player, true);
					return;
				}

				if (!info.hasCommand() || eventCmd) Bukkit.getPluginManager().callEvent(new AriKeyReleaseEvent(player, id, true));
				info.mmSkill(player, false);
			} else {
				Bukkit.getPluginManager()
						.callEvent(firstPress ? new AriKeyPressEvent(player, id, false) : new AriKeyReleaseEvent(player, id, false));
			}
		} catch (IOException ignored) {
			ignored.printStackTrace();
		}
	}

	public void receiveGreeting(Player player) {
		/* Send this server's specified keybindings to the
		 client. This is delayed to make sure the client is properly
		 connected before attempting to send any data over. */
		Bukkit.getScheduler().runTaskLater(AriKeysPlugin.get(), () -> {
			for (AriKeyInfo info : AriKeysPlugin.get().getConf().getKeyInfoList().values())
				sendKeyInformation(player, info.getId(), info.getDef(), info.getName(), info.getCategory());

			/* Send the "load" packet after sending every keybinding packet, to tell
			 the client to load all the user-specific keybinds saved on their machine. */
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			DataOutputStream out = new DataOutputStream(b);
			try {
				out.writeByte(0);
			} catch (IOException ignored) {
			}

			player.sendPluginMessage(AriKeysPlugin.get(), AriKeysChannels.LOAD_KEYS, b.toByteArray());
		}, 20);
	}

	// Simply send over the information in an add key packet.
	public void sendKeyInformation(Player player, NamespacedKey id, int def, String name, String category) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeByte(0);
			out.writeUTF(id.getNamespace());
			out.writeUTF(id.getKey());
			out.writeInt(def);
			out.writeUTF(name);
			out.writeUTF(category);
		} catch (IOException ignored) {
		}

		player.sendPluginMessage(AriKeysPlugin.get(), AriKeysChannels.ADD_KEY, b.toByteArray());
	}
}
