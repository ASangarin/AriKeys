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
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forgespi.Environment;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

@Mod(AriKeys.MOD_ID)
public class AriKeysForge {
	private static final Object DEF_OBJECT = new Object();

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

			HANDSHAKE.registerMessage(0, Object.class, (v, buf) -> {}, (buf) -> DEF_OBJECT, (v, context) -> {});
			KEY.registerMessage(0, KeyPressData.class, KeyPressData::write, (buf) -> null, (key, context) -> {});
			ADD_KEY.registerMessage(0, KeyAddData.class, (key, buf) -> {}, KeyAddData::fromBuffer,
					(key, ctx) -> ctx.get().enqueueWork(() -> AriKeys.add(key)));
			LOAD.registerMessage(0, Object.class, (v, buf) -> {
			}, (buf) -> DEF_OBJECT, (v, ctx) -> ctx.get().enqueueWork(AriKeysIO::load));
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