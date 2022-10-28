package eu.asangarin.arikeys;

import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class AriKeysListener implements PluginMessageListener {
	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] message) {
		// When receiving a handshake or key press, forward to their respective methods.
		if (channel.equalsIgnoreCase(AriKeysChannels.HANDSHAKE))
			AriKeysNetwork.receiveGreeting(player);
		else if (channel.equalsIgnoreCase(AriKeysChannels.KEY_PRESS))
			AriKeysNetwork.receiveKeyPress(player, Unpooled.wrappedBuffer(message));
	}
}
