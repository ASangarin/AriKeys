package eu.asangarin.arikeys.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ModifierKey {
	NONE(-1), LEFT_SHIFT(0), RIGHT_SHIFT(1), LEFT_ALT(2), RIGHT_ALT(3), LEFT_CTRL(4), RIGHT_CTRL(5);

	public final static ModifierKey[] ALL = new ModifierKey[]{LEFT_SHIFT, RIGHT_SHIFT, LEFT_ALT, RIGHT_ALT, LEFT_CTRL, RIGHT_CTRL};

	private final int id;

	public static ModifierKey fromString(String modifier) {
		return switch (modifier.toUpperCase()) {
			case "LEFT_SHIFT", "LS", "L_SHIFT" -> ModifierKey.LEFT_SHIFT;
			case "RIGHT_SHIFT", "RS", "R_SHIFT" -> ModifierKey.RIGHT_SHIFT;
			case "LEFT_ALT", "LA", "L_ALT" -> ModifierKey.LEFT_ALT;
			case "RIGHT_ALT", "RA", "R_ALT" -> ModifierKey.RIGHT_ALT;
			case "LEFT_CTRL", "LEFT_CONTROL", "LC", "L_CTRL", "L_CONTROL" -> ModifierKey.LEFT_CTRL;
			case "RIGHT_CTRL", "RIGHT_CONTROL", "RC", "R_CTRL", "R_CONTROL" -> ModifierKey.RIGHT_CTRL;
			default -> ModifierKey.NONE;
		};
	}
}
