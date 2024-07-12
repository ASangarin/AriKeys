package eu.asangarin.arikeys.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class AriKeysButton implements Drawable, Element, Selectable {
	public static final Identifier WIDGETS_TEXTURE = new Identifier("arikeys", "textures/gui/arikeys_button.png");
	private final Text message = Text.translatable("arikeys.aributton");
	public int x;
	public int y;
	protected boolean hovered;
	public boolean active = true;
	public boolean visible = true;
	private boolean focused;

	protected final AriKeysButton.PressAction onPress;

	public AriKeysButton(int x, int y, AriKeysButton.PressAction onPress) {
		this.x = x;
		this.y = y;
		this.onPress = onPress;
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		if (this.visible) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + 20 && mouseY < this.y + 20;
			this.renderButton(context);
		}
	}

	protected MutableText getNarrationMessage() {
		return getNarrationMessage(this.getMessage());
	}

	public static MutableText getNarrationMessage(Text message) {
		return Text.translatable("gui.narrate.button", message);
	}

	public void renderButton(DrawContext context) {
		RenderSystem.setShader(GameRenderer::getPositionTexProgram);
		RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.enableDepthTest();
		context.drawTexture(WIDGETS_TEXTURE, this.x, this.y, 0, this.isHovered() ? 20 : 0, 20, 20);
	}

	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean click = this.clicked(mouseX, mouseY);
				if (click) {
					this.playDownSound(MinecraftClient.getInstance().getSoundManager());
					this.onPress.onPress(this);
					return true;
				}
			}

			return false;
		} else {
			return false;
		}
	}

	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return this.isValidClickButton(button);
	}

	protected boolean isValidClickButton(int button) {
		return button == 0;
	}

	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return this.isValidClickButton(button);
	}

	protected boolean clicked(double mouseX, double mouseY) {
		return this.active && this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + 20) && mouseY < (double) (this.y + 20);
	}

	public boolean isHovered() {
		return this.hovered || this.focused;
	}

	public boolean isMouseOver(double mouseX, double mouseY) {
		return this.active && this.visible && mouseX >= (double) this.x && mouseY >= (double) this.y && mouseX < (double) (this.x + 20) && mouseY < (double) (this.y + 20);
	}

	@Override
	public void setFocused(boolean focused) {
		this.focused = focused;
	}

	public void playDownSound(SoundManager soundManager) {
		soundManager.play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0F));
	}

	public Text getMessage() {
		return this.message;
	}

	public boolean isFocused() {
		return this.focused;
	}

	public boolean isNarratable() {
		return this.visible && this.active;
	}

	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (this.active && this.visible) {
			if (keyCode != GLFW.GLFW_KEY_ENTER && keyCode != GLFW.GLFW_KEY_SPACE && keyCode != GLFW.GLFW_KEY_KP_ENTER) {
				return false;
			} else {
				this.playDownSound(MinecraftClient.getInstance().getSoundManager());
				this.onPress.onPress(this);
				return true;
			}
		} else {
			return false;
		}
	}

	public Selectable.SelectionType getType() {
		if (this.focused) {
			return Selectable.SelectionType.FOCUSED;
		} else {
			return this.hovered ? Selectable.SelectionType.HOVERED : Selectable.SelectionType.NONE;
		}
	}

	public void appendNarrations(NarrationMessageBuilder builder) {
		builder.put(NarrationPart.TITLE, this.getNarrationMessage());
		if (this.active) {
			if (this.isFocused()) {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.focused"));
			} else {
				builder.put(NarrationPart.USAGE, Text.translatable("narration.button.usage.hovered"));
			}
		}
	}

	public interface PressAction {
		void onPress(AriKeysButton button);
	}
}
