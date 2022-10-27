package eu.asangarin.arikeys.forge;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.forge.network.AddKeyHandler;
import eu.asangarin.arikeys.forge.network.HandshakeHandler;
import eu.asangarin.arikeys.forge.network.KeybindHandler;
import eu.asangarin.arikeys.forge.network.LoadHandler;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.AriKeysIO;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(AriKeys.MOD_ID)
public class AriKeysForge {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel HANDSHAKE = NetworkRegistry.newSimpleChannel(AriKeysChannels.HANDSHAKE_CHANNEL, () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static final SimpleChannel KEY = NetworkRegistry.newSimpleChannel(AriKeysChannels.KEY_CHANNEL, () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static final SimpleChannel ADD_KEY = NetworkRegistry.newSimpleChannel(AriKeysChannels.ADD_KEY_CHANNEL, () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	public static final SimpleChannel LOAD = NetworkRegistry.newSimpleChannel(AriKeysChannels.LOAD_CHANNEL, () -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public AriKeysForge() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
				() -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));

		if (Environment.get().getDist().isClient()) {
			MinecraftForge.EVENT_BUS.addListener(this::handlePlayerLogin);
			MinecraftForge.EVENT_BUS.addListener(this::handlePlayerDisconnect);

			HANDSHAKE.registerMessage(0, HandshakeHandler.class, HandshakeHandler::encode, HandshakeHandler::decode, HandshakeHandler::consume);
			KEY.registerMessage(0, KeybindHandler.class, KeybindHandler::encode, KeybindHandler::decode, KeybindHandler::consume);
			ADD_KEY.registerMessage(0, AddKeyHandler.class, AddKeyHandler::encode, AddKeyHandler::decode, AddKeyHandler::consume);
			LOAD.registerMessage(0, LoadHandler.class, LoadHandler::encode, LoadHandler::decode, LoadHandler::consume);


			String protocol = "1";
			SimpleChannel test = NetworkRegistry.newSimpleChannel(AriKeysChannels.LOAD_CHANNEL, () -> protocol,
					protocol::equals, protocol::equals);
			test.registerMessage(0, Object.class, (obj, buf) -> {}, (buf) -> new Object(),
					(obj, ctx) -> ctx.get().enqueueWork(AriKeysIO::load));

			AriKeys.init();
		}
	}

	private void handlePlayerLogin(EntityJoinLevelEvent event) {
		if (MinecraftClient.getInstance().player == null || (event.getEntity().getUuid() != MinecraftClient.getInstance().player.getUuid())) return;
		System.out.println("Wooo, forge!");
		AriKeys.clear();
		// Send a packet informing the server that a client with the mod has joined
		//noinspection InstantiationOfUtilityClass
		HANDSHAKE.sendToServer(new HandshakeHandler());
	}

	private void handlePlayerDisconnect(EntityLeaveLevelEvent event) {
		if (MinecraftClient.getInstance().player == null || (event.getEntity().getUuid() != MinecraftClient.getInstance().player.getUuid())) return;
		// Clean up after disconnection
		AriKeysIO.save();
		AriKeys.clear();
	}
}