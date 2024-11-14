package eu.asangarin.arikeys.neoforge;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysAddKeyPayload;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysHandshakePayload;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysKeyPressPayload;
import eu.asangarin.arikeys.neoforge.payloads.AriKeysLoadPayload;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.network.KeyAddData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import net.neoforged.neoforgespi.Environment;

@Mod(AriKeys.MOD_ID)
public class AriKeysNeoForge {
	public AriKeysNeoForge(IEventBus modBus) {
		if (Environment.get().getDist().isClient()) {
			modBus.addListener(this::handlePacketRegistration);
			NeoForge.EVENT_BUS.addListener(this::handlePlayerLogin);
			NeoForge.EVENT_BUS.addListener(this::handlePlayerDisconnect);
		}
	}

	private void handlePacketRegistration(RegisterPayloadHandlerEvent event) {
		System.out.println("HIFINDMEINLOG! - Registration running!");
		final IPayloadRegistrar registrar = event.registrar(AriKeys.MOD_ID);
		registrar.optional().play(AriKeysChannels.HANDSHAKE_CHANNEL, AriKeysHandshakePayload::new, handler -> {});
		registrar.optional().play(AriKeysChannels.KEY_CHANNEL, AriKeysKeyPressPayload::new, handler -> {});
		registrar.optional().play(AriKeysChannels.ADD_KEY_CHANNEL, buf -> {
			buf.readByte();
			KeyAddData keyData = KeyAddData.fromBuffer(buf);
			readFully(buf);
			return new AriKeysAddKeyPayload(keyData);
		}, handler -> handler.client(
				AKClientHandler::handleKeyData));
		registrar.optional().play(AriKeysChannels.LOAD_CHANNEL, AriKeysLoadPayload::new, handler -> handler.client(AKClientHandler::handleLoad));
	}

	public static void readFully(PacketByteBuf buf) {
		while(buf.readableBytes() != 0)
			buf.readByte();
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