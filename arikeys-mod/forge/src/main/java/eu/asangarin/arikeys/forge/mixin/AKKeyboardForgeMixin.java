package eu.asangarin.arikeys.forge.mixin;

import net.minecraft.client.option.KeyBinding;
import net.minecraftforge.client.settings.KeyMappingLookup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(KeyBinding.class)
public interface AKKeyboardForgeMixin {
	@Accessor("f_90810_")
	static KeyMappingLookup getKeyBindings() {
		throw new AssertionError();
	}
}
