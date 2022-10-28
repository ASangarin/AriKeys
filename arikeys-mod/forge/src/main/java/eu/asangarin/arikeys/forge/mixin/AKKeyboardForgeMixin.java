package eu.asangarin.arikeys.forge.mixin;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.forge.AriKeysForge;
import eu.asangarin.arikeys.forge.network.KeyPressData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(KeyBinding.class)
public class AKKeyboardForgeMixin {
	@Shadow
	@Final
	private static KeyMappingLookup f_90810_;

	private static final List<InputUtil.Key> pressedKeys = new ArrayList<>();

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void input(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		// Only check for keybinds while outside a GUI
		if (MinecraftClient.getInstance().currentScreen != null) return;

		KeyBinding keyBinding = f_90810_.get(key);
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
		/* Send the packet that a key was pressed
		alongside the ID of the binding in question
		and whether it was released or not */
		AriKeysForge.KEY.sendToServer(new KeyPressData(id, release));
	}
}
