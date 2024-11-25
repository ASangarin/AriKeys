package eu.asangarin.arikeys.mixin;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.AriKeysPlatform;
import eu.asangarin.arikeys.util.network.KeyPressData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(KeyBinding.class)
public class AKKeyboardMixin {
	@Unique
	private static final List<InputUtil.Key> arikeys$pressedKeys = new ArrayList<>();

	@Inject(method = "setKeyPressed", at = @At("HEAD"))
	private static void input(InputUtil.Key key, boolean pressed, CallbackInfo ci) {
		// Only check for keybinds while outside a GUI
		if (MinecraftClient.getInstance().currentScreen != null) return;

		Collection<KeyBinding> keyBindings = AriKeysPlatform.getKeyBinding(key);
		for(KeyBinding binding : keyBindings) {
			if (binding != null) {
				String path = arikeys$cleanTranslationKey(binding.getTranslationKey());
				try {
					Identifier id = Identifier.of(Identifier.DEFAULT_NAMESPACE, path);
					if (AriKeys.getVanillaKeys().contains(id))
						arikeys$registerPress(id, key, pressed);
				} catch (InvalidIdentifierException id) {
					//noinspection CallToPrintStackTrace
					id.printStackTrace();
				}
			}
		}

		for (AriKey ariKey : AriKeys.getModifierSortedKeybinds())
			if (key.equals(ariKey.getBoundKeyCode()) && ariKey.testModifiers())
				arikeys$registerPress(ariKey.getId(), key, pressed);
	}

	@Unique
	private static void arikeys$registerPress(Identifier id, InputUtil.Key key, boolean pressed) {
		// Check if the button was pressed or released
		if (pressed) {
			boolean held = arikeys$pressedKeys.contains(key);
			// Check if it is already being pressed
			if (!held) {
				// Add it to the list of currently pressed keys
				arikeys$pressedKeys.add(key);
				arikeys$sendPacket(id, false);
			}
		} else {
			// Remove it from the list of currently pressed keys
			arikeys$pressedKeys.remove(key);
			arikeys$sendPacket(id, true);
		}
	}

	@Unique
	private static void arikeys$sendPacket(Identifier id, boolean release) {
		// Call the platform specific packet sending code
		AriKeysPlatform.sendKey(new KeyPressData(id, release));
	}

	@Unique
	private static String arikeys$cleanTranslationKey(String key) {
		return key.replace("key.", "").replace(".", "")
			.replace(" ", "_").toLowerCase();
	}
}
