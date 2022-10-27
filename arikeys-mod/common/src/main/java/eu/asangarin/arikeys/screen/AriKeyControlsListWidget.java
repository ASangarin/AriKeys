package eu.asangarin.arikeys.screen;

import com.google.common.collect.ImmutableList;
import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import eu.asangarin.arikeys.util.AriKeysIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


public class AriKeyControlsListWidget extends ElementListWidget<AriKeyControlsListWidget.Entry> {
	final AriKeysOptions parent;
	int maxKeyNameLength;

	public AriKeyControlsListWidget(AriKeysOptions parent, MinecraftClient client) {
		super(client, parent.width + 45, parent.height, 43, parent.height - 32, 20);
		this.parent = parent;
		String category = null;

		for (AriKey ariKey : AriKeys.getSortedKeybinds()) {
			String keyCat = ariKey.getCategory();
			if (!keyCat.equals(category)) {
				category = keyCat;
				this.addEntry(new CategoryEntry(Text.literal(keyCat)));
			}

			Text text = Text.literal(ariKey.getName());
			int i = client.textRenderer.getWidth(text);
			if (i > this.maxKeyNameLength) {
				this.maxKeyNameLength = i;
			}

			this.addEntry(new KeyBindingEntry(ariKey, text));
		}

	}

	protected int getScrollbarPositionX() {
		return super.getScrollbarPositionX() + 15;
	}

	public int getRowWidth() {
		return super.getRowWidth() + 32;
	}

	public class CategoryEntry extends AriKeyControlsListWidget.Entry {
		final Text text;
		private final int textWidth;

		public CategoryEntry(Text text) {
			this.text = text;
			this.textWidth = AriKeyControlsListWidget.this.client.textRenderer.getWidth(this.text);
		}

		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			TextRenderer var10000 = AriKeyControlsListWidget.this.client.textRenderer;
			assert AriKeyControlsListWidget.this.client.currentScreen != null;
			float var10003 = (float) (AriKeyControlsListWidget.this.client.currentScreen.width / 2 - this.textWidth / 2);
			int var10004 = y + entryHeight;
			Objects.requireNonNull(AriKeyControlsListWidget.this.client.textRenderer);
			var10000.draw(matrices, this.text, var10003, (float) (var10004 - 9 - 1), 16777215);
		}

		public boolean changeFocus(boolean lookForwards) {
			return false;
		}

		public List<? extends Element> children() {
			return Collections.emptyList();
		}

		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(new Selectable() {
				public Selectable.SelectionType getType() {
					return Selectable.SelectionType.HOVERED;
				}

				public void appendNarrations(NarrationMessageBuilder builder) {
					builder.put(NarrationPart.TITLE, CategoryEntry.this.text);
				}
			});
		}
	}

	public class KeyBindingEntry extends AriKeyControlsListWidget.Entry {
		private final AriKey ariKey;
		private final Text bindingName;
		private final ButtonWidget editButton;
		private final ButtonWidget resetButton;

		KeyBindingEntry(AriKey ariKey, Text bindingName) {
			this.ariKey = ariKey;
			this.bindingName = bindingName;
			this.editButton = new ButtonWidget(0, 0, 75, 20, bindingName,
					(button) -> AriKeyControlsListWidget.this.parent.focusedMKey = ariKey) {
				protected MutableText getNarrationMessage() {
					return ariKey.isUnbound() ? Text.translatable("narrator.controls.unbound", bindingName) : Text.translatable(
							"narrator.controls.bound", bindingName, super.getNarrationMessage());
				}
			};
			this.resetButton = new ButtonWidget(0, 0, 50, 20, Text.translatable("controls.reset"), (button) -> {
				ariKey.setBoundKey(ariKey.getKeyCode());
				AriKeysIO.save();
				KeyBinding.updateKeysByCode();
			}) {
				protected MutableText getNarrationMessage() {
					return Text.translatable("narrator.controls.reset", bindingName);
				}
			};
		}

		public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
			boolean bl = AriKeyControlsListWidget.this.parent.focusedMKey == this.ariKey;
			TextRenderer var10000 = AriKeyControlsListWidget.this.client.textRenderer;
			float var10003 = (float) (x + 90 - AriKeyControlsListWidget.this.maxKeyNameLength);
			int var10004 = y + entryHeight / 2;
			Objects.requireNonNull(AriKeyControlsListWidget.this.client.textRenderer);
			var10000.draw(matrices, this.bindingName, var10003, (float) (var10004 - 9 / 2), 16777215);
			this.resetButton.x = x + 190;
			this.resetButton.y = y;
			this.resetButton.active = this.ariKey.hasChanged();
			this.resetButton.render(matrices, mouseX, mouseY, tickDelta);
			this.editButton.x = x + 105;
			this.editButton.y = y;
			this.editButton.setMessage(this.ariKey.getBoundKeyCode().getLocalizedText());
			boolean bl2 = false;
			if (!this.ariKey.isUnbound()) {
				final List<KeyBinding> bindings = new ArrayList<>(List.of(client.options.allKeys));
				for (KeyBinding keyBinding : bindings) {
					if (keyBinding.getBoundKeyTranslationKey().equals(ariKey.getBoundKeyCode().getTranslationKey())) {
						bl2 = true;
						break;
					}
				}
				for (AriKey key : AriKeys.getKeybinds()) {
					if (!key.equals(ariKey) && key.getBoundKeyCode().equals(ariKey.getBoundKeyCode())) {
						bl2 = true;
						break;
					}
				}
			}

			if (bl) {
				this.editButton.setMessage(
						(Text.literal("> ")).append(this.editButton.getMessage().copyContentOnly().formatted(Formatting.YELLOW)).append(" <")
								.formatted(Formatting.YELLOW));
			} else if (bl2) {
				this.editButton.setMessage(this.editButton.getMessage().copyContentOnly().formatted(Formatting.RED));
			}

			this.editButton.render(matrices, mouseX, mouseY, tickDelta);
		}

		public List<? extends Element> children() {
			return ImmutableList.of(this.editButton, this.resetButton);
		}

		public List<? extends Selectable> selectableChildren() {
			return ImmutableList.of(this.editButton, this.resetButton);
		}

		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (this.editButton.mouseClicked(mouseX, mouseY, button)) {
				return true;
			} else {
				return this.resetButton.mouseClicked(mouseX, mouseY, button);
			}
		}

		public boolean mouseReleased(double mouseX, double mouseY, int button) {
			return this.editButton.mouseReleased(mouseX, mouseY, button) || this.resetButton.mouseReleased(mouseX, mouseY, button);
		}
	}

	public abstract static class Entry extends ElementListWidget.Entry<AriKeyControlsListWidget.Entry> {
	}
}
