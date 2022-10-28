package eu.asangarin.arikeys.forge.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;

@Getter
@RequiredArgsConstructor
public class KeyPressData {
	private final Identifier id;
	private final boolean release;
}
