package eu.asangarin.arikeys.mixin;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.screen.AriKeysButton;
import eu.asangarin.arikeys.screen.AriKeysOptions;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public class AKOptionsScreen extends Screen {
	@Shadow
	@Final
	private GameOptions settings;

	protected AKOptionsScreen(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	protected void initAriKeysButton(CallbackInfo ci) {
		if (client == null || client.isInSingleplayer() || AriKeys.getKeybinds().isEmpty()) return;
		// Add the arikeys button widget
		this.addDrawableChild(new AriKeysButton(this.width / 2 + 158, this.height / 6 + 72 - 6,
				(button) -> this.client.setScreen(new AriKeysOptions(this, this.settings))));
	}

}
