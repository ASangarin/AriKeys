package eu.asangarin.arikeys.forge;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.network.KeyAddData;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.MinecraftClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

@Mod(AriKeys.MOD_ID)
public class AriKeysForge {
	private static final Object DEF_OBJECT = new Object();

	private static final int PROTOCOL_VERSION = 1;

	public static final SimpleChannel HANDSHAKE = ChannelBuilder.named(AriKeysChannels.HANDSHAKE_CHANNEL)
			.acceptedVersions((status, version) -> PROTOCOL_VERSION == version)
			.networkProtocolVersion(PROTOCOL_VERSION)
			.optionalServer()
			.simpleChannel();

	public static final SimpleChannel KEY = ChannelBuilder.named(AriKeysChannels.KEY_CHANNEL)
			.acceptedVersions((status, version) -> PROTOCOL_VERSION == version)
			.networkProtocolVersion(PROTOCOL_VERSION)
			.optionalServer()
			.simpleChannel();

	public static final SimpleChannel ADD_KEY = ChannelBuilder.named(AriKeysChannels.ADD_KEY_CHANNEL)
			.acceptedVersions((status, version) -> PROTOCOL_VERSION == version)
			.networkProtocolVersion(PROTOCOL_VERSION)
			.optionalServer()
			.simpleChannel();

	public static final SimpleChannel LOAD = ChannelBuilder.named(AriKeysChannels.LOAD_CHANNEL)
			.acceptedVersions((status, version) -> PROTOCOL_VERSION == version)
			.networkProtocolVersion(PROTOCOL_VERSION)
			.optionalServer()
			.simpleChannel();

	public AriKeysForge() {
		/* This code might be outdated?
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class,
				() -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));*/

		if (Environment.get().getDist().isClient()) {
			MinecraftForge.EVENT_BUS.addListener(this::handlePlayerLogin);
			MinecraftForge.EVENT_BUS.addListener(this::handlePlayerDisconnect);

			HANDSHAKE.messageBuilder(Object.class, NetworkDirection.PLAY_TO_SERVER)
					.encoder((obj, buf) -> {})
					.decoder(buf -> DEF_OBJECT)
					.consumerMainThread((obj, ctx) -> {})
					.add();


			KEY.messageBuilder(KeyPressData.class, NetworkDirection.PLAY_TO_SERVER)
					.encoder(KeyPressData::write)
					.decoder(buf -> null)
					.consumerMainThread((obj, ctx) -> {})
					.add();

			ADD_KEY.messageBuilder(KeyAddData.class, NetworkDirection.PLAY_TO_CLIENT)
					.encoder((obj, buf) -> {})
					.decoder(KeyAddData::fromBuffer)
					.consumerMainThread((key, ctx) -> ctx.enqueueWork(() -> AriKeys.add(key)))
					.add();

			LOAD.messageBuilder(Object.class, NetworkDirection.PLAY_TO_CLIENT)
					.encoder((obj, buf) -> {})
					.decoder(buf -> DEF_OBJECT)
					.consumerMainThread((obj, ctx) -> ctx.enqueueWork(AriKeysIO::load))
					.add();
		}
	}

	private void handlePlayerLogin(EntityJoinLevelEvent event) {
		if (MinecraftClient.getInstance().player == null || (event.getEntity().getUuid() != MinecraftClient.getInstance().player.getUuid())) return;
		AriKeys.handleConnect();
	}

	private void handlePlayerDisconnect(EntityLeaveLevelEvent event) {
		if (MinecraftClient.getInstance().player == null || (event.getEntity().getUuid() != MinecraftClient.getInstance().player.getUuid())) return;
		AriKeys.handleDisconnect();
	}
}