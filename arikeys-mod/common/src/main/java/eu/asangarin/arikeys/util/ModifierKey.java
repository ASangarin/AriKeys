package eu.asangarin.arikeys.util;

import lombok.Getter;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

@Getter
public enum ModifierKey {
	NONE(-1, -1),
	LEFT_CTRL(4, GLFW.GLFW_KEY_LEFT_CONTROL),
	LEFT_SHIFT(0, GLFW.GLFW_KEY_LEFT_SHIFT),
	LEFT_ALT(2, GLFW.GLFW_KEY_LEFT_ALT),
	RIGHT_CTRL(5, GLFW.GLFW_KEY_RIGHT_CONTROL),
	RIGHT_SHIFT(1, GLFW.GLFW_KEY_RIGHT_SHIFT),
	RIGHT_ALT(3, GLFW.GLFW_KEY_RIGHT_ALT);

	public final static ModifierKey[] ALL = new ModifierKey[] {
			LEFT_CTRL, LEFT_SHIFT, LEFT_ALT, RIGHT_CTRL, RIGHT_SHIFT, RIGHT_ALT
	};

	private final int id, code;
	private final InputUtil.Key key;
	private final String translationKey;

	ModifierKey(int id, int code) {
		this.id = id;
		this.code = code;
		this.key = InputUtil.Type.KEYSYM.createFromCode(code);
		this.translationKey = "arikeys.modifier." + name().toLowerCase();
	}

	public static Set<ModifierKey> getFromArray(int[] modifiers) {
		Set<ModifierKey> keys = new HashSet<>();
		for (int i : modifiers) {
			ModifierKey key = fromCode(i);
			if (key == ModifierKey.NONE) continue;
			keys.add(key);
		}
		return keys;
	}

	private static ModifierKey fromCode(int i) {
		for (ModifierKey key : ALL)
			if (key.id == i) return key;
		return ModifierKey.NONE;
	}
}
