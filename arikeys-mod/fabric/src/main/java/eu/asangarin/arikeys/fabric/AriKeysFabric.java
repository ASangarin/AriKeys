package eu.asangarin.arikeys.fabric;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.PacketByteBufs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

public class AriKeysFabric implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AriKeys.init();

		ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
			// Clean up after disconnection
			AriKeysIO.save();
			AriKeys.clear();
		}));
		ClientPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			AriKeys.clear();
			// Send a packet informing the server that a client with the mod has joined
			ClientPlayNetworking.send(AriKeysChannels.HANDSHAKE_CHANNEL, PacketByteBufs.create());
		});

		// Receive keybindings data from the server
		ClientPlayNetworking.registerGlobalReceiver(AriKeysChannels.ADD_KEY_CHANNEL, (client, handler, buf, responseSender) -> {
			buf.readByte();
			buf.readString(); // Man, I love working with bytes...
			String path = buf.readString();
			buf.readString(); // Don't you love it too?
			String key = buf.readString();
			int defKey = buf.readInt();
			buf.readString(); // Wow, it got even better!
			String name = buf.readString();
			buf.readString(); // This is just incredible.
			String category = buf.readString();

			/* Always read data async and then use client.execute() after for thread safety. */
			Identifier id = new Identifier(path, key);
			client.execute(() -> AriKeys.add(id, new AriKey(id, name, category, InputUtil.Type.KEYSYM.createFromCode(defKey))));
		});

		/* When the server finish sending keybinds it will send the load packet and
		 the client will load the keybinding data from the AriKeysIO */
		ClientPlayNetworking.registerGlobalReceiver(AriKeysChannels.LOAD_CHANNEL,
				(client, handler, buf, responseSender) -> client.execute(AriKeysIO::load));
	}
}