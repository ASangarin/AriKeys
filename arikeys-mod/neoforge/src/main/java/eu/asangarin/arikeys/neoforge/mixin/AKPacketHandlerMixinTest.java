package eu.asangarin.arikeys.neoforge.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class AKPacketHandlerMixinTest {
	@Inject(method = "handleCustomPayload", at = @At("HEAD"))
	private void arikeys$handlePayload(CustomPayloadS2CPacket arg, CustomPayload arg2, CallbackInfo ci) {
		System.out.println("Handling: " + arg2.id());
	}
}
