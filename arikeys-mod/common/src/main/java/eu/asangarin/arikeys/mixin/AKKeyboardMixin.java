package eu.asangarin.arikeys.mixin;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.AriKeysPlatform;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(KeyBinding.class)
public class AKKeyboardMixin {
	private static final List<InputUtil.Key> pressedKeys = new ArrayList<>();

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void input(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		// Only check for keybinds while outside a GUI
		if (MinecraftClient.getInstance().currentScreen != null) return;

		KeyBinding keyBinding = AriKeysPlatform.getKeyBinding(key);
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
		// Call the platform specific packet sending code
		AriKeysPlatform.sendKey(new KeyPressData(id, release));

		/* Send the packet that a key was pressed
		alongside the ID of the binding in question
		and whether it was released or not */
		/*
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeByte(0);
		buf.writeString(id.getNamespace());
		buf.writeString(id.getPath());
		buf.writeBoolean(release);
		ClientPlayNetworking.send(AriKeysChannels.KEY_CHANNEL, buf);
		 */
	}
}
