package eu.asangarin.arikeys.fabric.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(KeyBinding.class)
public interface AKKeyboardFabricMixin {
	@Accessor("KEY_TO_BINDINGS")
	static Map<InputUtil.Key, KeyBinding> getKeyBindings() {
		throw new AssertionError();
	}
}
