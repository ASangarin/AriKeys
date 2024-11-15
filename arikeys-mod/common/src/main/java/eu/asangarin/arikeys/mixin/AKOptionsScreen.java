package eu.asangarin.arikeys.mixin;

import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.screen.AriKeysButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ControlsOptionsScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ControlsOptionsScreen.class)
public class AKOptionsScreen extends Screen {
	@Unique
	private final AriKeysButton arikeys$ak_button = new AriKeysButton(this);

	protected AKOptionsScreen(Text title) {
		super(title);
	}

	@Inject(method = "init", at = @At("TAIL"))
	protected void initAriKeysButton(CallbackInfo ci) {
		if (client == null || client.isInSingleplayer()) return;
		addDrawableChild(arikeys$ak_button);
		arikeys$refresh();
		System.out.println("Yay!");
		if(AriKeys.getKeybinds().isEmpty()) {
			System.out.println("Oh... Nevermind...");
			arikeys$ak_button.setActive(false);
			arikeys$ak_button.setTooltip(Tooltip.of(Text.translatable("arikeys.disabled_message")));
		}
	}

	@Inject(method = "initTabNavigation", at = @At("TAIL"))
	protected void refreshAriKeysButton(CallbackInfo ci) {
		if (client == null || client.isInSingleplayer()) return;
		arikeys$refresh();
	}

	@Unique
	private void arikeys$refresh() {
		arikeys$ak_button.setPosition(this.width / 2 + 158, 37);
	}
}
