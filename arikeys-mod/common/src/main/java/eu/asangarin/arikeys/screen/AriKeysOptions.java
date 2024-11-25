package eu.asangarin.arikeys.screen;

import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.ModifierKey;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;

public class AriKeysOptions extends GameOptionsScreen {
	public AriKey focusedMKey;
	private AriKeyControlsListWidget keyBindingListWidget;
	private ButtonWidget resetButton;

	public AriKeysOptions(Screen parent) {
		super(parent, MinecraftClient.getInstance().options, Text.translatable("arikeys.controls.title"));
	}

	@Override
	protected void initBody() {
		if (client != null)
			this.keyBindingListWidget = this.layout.addBody(new AriKeyControlsListWidget(this, this.client));
	}

	@Override
	protected void addOptions() {}

	@Override
	protected void initFooter() {
		this.resetButton = ButtonWidget.builder(Text.translatable("controls.resetAll"), (button) -> {
			for (AriKey keyBinding : AriKeys.getKeybinds()) {
				keyBinding.setBoundKey(keyBinding.getKeyCode(), false);
				keyBinding.resetBoundModifiers();
			}
			KeyBinding.updateKeysByCode();
		}).build();
		DirectionalLayoutWidget linearlayout = this.layout.addFooter(DirectionalLayoutWidget.horizontal().spacing(8));
		linearlayout.add(this.resetButton);
		linearlayout.add(ButtonWidget.builder(ScreenTexts.DONE, (button) -> this.close()).build());
	}

	@Override
	protected void initTabNavigation() {
		this.layout.refreshPositions();
		this.keyBindingListWidget.position(this.width, this.layout);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.focusedMKey != null) {
			if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
				focusedMKey.setBoundKey(InputUtil.UNKNOWN_KEY, false);
				focusedMKey.setBoundModifiers(new HashSet<>());
			} else if (isModifier(keyCode)) return super.keyPressed(keyCode, scanCode, modifiers);
			else focusedMKey.setBoundKey(InputUtil.fromKeyCode(keyCode, scanCode), true);
			AriKeysIO.save();

			this.focusedMKey = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.keyPressed(keyCode, scanCode, modifiers);
		}
	}

	private boolean isModifier(int code) {
		for (ModifierKey modifier : ModifierKey.ALL)
			if (modifier.getCode() == code) return true;
		return false;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.focusedMKey != null) {
			focusedMKey.setBoundKey(InputUtil.Type.MOUSE.createFromCode(button), true);
			AriKeysIO.save();

			this.focusedMKey = null;
			KeyBinding.updateKeysByCode();
			return true;
		} else {
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		super.render(context, mouseX, mouseY, delta);

		/*this.keyBindingListWidget.render(context, mouseX, mouseY, delta);
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);*/

		boolean canReset = false;

		for (AriKey ariKey : AriKeys.getKeybinds()) {
			if (ariKey.hasChanged()) {
				canReset = true;
				break;
			}
		}

		this.resetButton.active = canReset;
	}
}
