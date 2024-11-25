package eu.asangarin.arikeys.screen;

import eu.asangarin.arikeys.AriKeys;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.util.Identifier;

@Getter
@Setter
public class AriKeysButton extends TexturedButtonWidget {
	private boolean active = true;

	private static final ButtonTextures BUTTON_TEXTURES = new ButtonTextures(
		Identifier.of(AriKeys.MOD_ID, "arikeys/ak_button_enabled"),
		Identifier.of(AriKeys.MOD_ID, "arikeys/ak_button_disabled"),
		Identifier.of(AriKeys.MOD_ID, "arikeys/ak_button_focused")
	);

	public AriKeysButton(Screen parent) {
		super(20, 20, BUTTON_TEXTURES, action -> MinecraftClient.getInstance().setScreen(new AriKeysOptions(parent)), ScreenTexts.EMPTY);
	}

	@Override
	public void onPress() {
		if(this.isActive())
			onPress.onPress(this);
	}

	@Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
		Identifier identifier = this.textures.get(this.isActive(), this.isSelected());
		context.drawGuiTexture(identifier, this.getX(), this.getY(), this.width, this.height);
	}
}
