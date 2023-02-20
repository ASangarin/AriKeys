package eu.asangarin.arikeys.fabric;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.network.KeyAddData;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class AriKeysFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// Test
		// Configure AriKeys on join and clean up on disconnect
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> AriKeys.handleDisconnect()));
		ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> AriKeys.handleConnect());

		// Receive keybindings data from the server
		ClientPlayNetworking.registerGlobalReceiver(AriKeysChannels.ADD_KEY_CHANNEL, (client, handler, buf, responseSender) -> {
			buf.readByte();
			KeyAddData data = KeyAddData.fromBuffer(buf);

			/* Always read data async and then use client.execute() after for thread safety. */
			client.execute(() -> AriKeys.add(data));
		});

		/* When the server finish sending keybinds it will send the load packet and
		 the client will load the keybinding data from the AriKeysIO */
		ClientPlayNetworking.registerGlobalReceiver(AriKeysChannels.LOAD_CHANNEL,
				(client, handler, buf, responseSender) -> client.execute(AriKeysIO::load));
	}
}