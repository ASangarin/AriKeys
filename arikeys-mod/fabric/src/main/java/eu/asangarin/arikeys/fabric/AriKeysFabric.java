package eu.asangarin.arikeys.fabric;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.AriKeysPayloads;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class AriKeysFabric implements ClientModInitializer {
	@SuppressWarnings("resource")
	@Override
	public void onInitializeClient() {
		PayloadTypeRegistry.playC2S().register(AriKeysPayloads.Handshake.ID, AriKeysPayloads.Handshake.CODEC);
		PayloadTypeRegistry.playC2S().register(AriKeysPayloads.Key.ID, AriKeysPayloads.Key.CODEC);
		PayloadTypeRegistry.playS2C().register(AriKeysPayloads.AddKey.ID, AriKeysPayloads.AddKey.CODEC);
		PayloadTypeRegistry.playS2C().register(AriKeysPayloads.Load.ID, AriKeysPayloads.Load.CODEC);

		// Configure AriKeys on join and clean up on disconnect
		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> AriKeys.handleDisconnect()));
		ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> AriKeys.handleConnect());

		// Receive keybindings data from the server
		ClientPlayNetworking.registerGlobalReceiver(AriKeysPayloads.AddKey.ID,
				(payload, ctx) -> ctx.client().execute(() -> AriKeys.add(payload.data())));

		/* When the server finish sending keybinds it will send the load packet and
		 the client will load the keybinding data from the AriKeysIO */
		ClientPlayNetworking.registerGlobalReceiver(AriKeysPayloads.Load.ID,
				(payload, ctx) -> ctx.client().execute(AriKeysIO::load));
	}
}