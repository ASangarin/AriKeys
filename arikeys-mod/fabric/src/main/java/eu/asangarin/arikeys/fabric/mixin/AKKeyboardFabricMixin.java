package eu.asangarin.arikeys.fabric.mixin;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysChannels;
import eu.asangarin.arikeys.util.PacketByteBufs;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Mixin(KeyBinding.class)
public class AKKeyboardFabricMixin {
	@Shadow
	@Final
	private static Map<InputUtil.Key, KeyBinding> KEY_TO_BINDINGS;

	private static final List<InputUtil.Key> pressedKeys = new ArrayList<>();

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void input(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		// Only check for keybinds while outside a GUI
		if (MinecraftClient.getInstance().currentScreen != null) return;

		KeyBinding keyBinding = KEY_TO_BINDINGS.get(key);
		if (keyBinding != null) registerPress(AriKeys.cleanIdentifier(keyBinding.getTranslationKey()), key, pressed);

		for (AriKey ariKey : AriKeys.getKeybinds())
			if (key.equals(ariKey.getBoundKeyCode())) registerPress(ariKey.getId(), key, pressed);
	}

	private static void registerPress(Identifier id, InputUtil.Key key, boolean pressed) {
		// Check if the button was pressed or released
		if (pressed) {
			boolean held = pressedKeys.contains(key);
			// Check if it is already being pressed
			if (!held) {
				// Add it to the list of currently pressed keys
				pressedKeys.add(key);
				sendPacket(id, false);
			}
		} else {
			// Remove it from the list of currently pressed keys
			pressedKeys.remove(key);
			sendPacket(id, true);
		}
	}

	private static void sendPacket(Identifier id, boolean release) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString("");
		buf.writeString(id.getNamespace());
		buf.writeString("");
		buf.writeString(id.getPath());
		buf.writeBoolean(release);
		/* Send the packet that a key was pressed
		alongside the ID of the binding in question
		and whether it was released or not */
		ClientPlayNetworking.send(AriKeysChannels.KEY_CHANNEL, buf);
	}
}
