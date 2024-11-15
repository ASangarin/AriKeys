package eu.asangarin.arikeys.neoforge;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.AriKeysPayloads;
import eu.asangarin.arikeys.util.network.KeyAddData;
import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
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

	private void handlePacketRegistration(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(AriKeys.MOD_ID);
		registrar.optional().playToServer(AriKeysPayloads.Handshake.ID, AriKeysPayloads.Handshake.CODEC,
			(payload, ctx) -> {});
		registrar.optional().playToServer(AriKeysPayloads.Key.ID, AriKeysPayloads.Key.CODEC,
			(payload, ctx) -> {});

		registrar.optional().playToClient(AriKeysPayloads.AddKey.ID, AriKeysPayloads.AddKey.CODEC,
			(payload, ctx) -> {
				KeyAddData key = payload.data();
				ctx.enqueueWork(() -> AriKeys.add(key));
			}
		);
		registrar.optional().playToClient(AriKeysPayloads.Load.ID, AriKeysPayloads.Load.CODEC,
			(payload, ctx) -> ctx.enqueueWork(AriKeysIO::load));
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