package eu.asangarin.arikeys.forge.network;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.Identifier;

@Getter
@RequiredArgsConstructor
public class KeyAddData {
	private final Identifier id;
	private final String name, category;
	private final int defKey;
}
