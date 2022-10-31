package eu.asangarin.arikeys;

import eu.asangarin.arikeys.util.ModifierKey;
import eu.asangarin.arikeys.util.network.KeyAddData;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import java.util.HashSet;
import java.util.Set;

@Getter
public class AriKey {
	private final Identifier id;
	private final String name, category;
	private final InputUtil.Key keyCode;
	private InputUtil.Key boundKeyCode;
	private final Set<ModifierKey> modifiers;
	private Set<ModifierKey> boundModifiers;

	public AriKey(KeyAddData data) {
		this(data.getId(), data.getName(), data.getCategory(), InputUtil.Type.KEYSYM.createFromCode(data.getDefKey()), data.getModifiers());
	}

	public AriKey(Identifier id, String name, String category, InputUtil.Key keyCode, int[] modifiers) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.keyCode = keyCode;
		this.boundKeyCode = keyCode;
		this.modifiers = ModifierKey.getFromArray(modifiers);
		this.boundModifiers = new HashSet<>(this.modifiers);
	}

	public void setBoundKey(InputUtil.Key key, boolean handleModifiers) {
		if (handleModifiers) {
			Set<ModifierKey> mods = new HashSet<>();
			for (ModifierKey modifier : ModifierKey.ALL)
				if (InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), modifier.getCode())) mods.add(modifier);
			setBoundModifiers(mods);
		}
		this.boundKeyCode = key;
	}

	public void setBoundModifiers(Set<ModifierKey> modifiers) {
		this.boundModifiers = modifiers;
	}

	public void resetBoundModifiers() {
		setBoundModifiers(new HashSet<>(this.modifiers));
	}

	public boolean hasChanged() {
		return !keyCode.equals(boundKeyCode) || !testModifiers(this.modifiers);
	}

	public boolean isUnbound() {
		return boundKeyCode.equals(InputUtil.UNKNOWN_KEY);
	}

	public boolean testModifiers() {
		for (ModifierKey key : boundModifiers)
			if (!InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), key.getCode())) return false;
		return true;
	}

	public boolean testModifiers(Set<ModifierKey> otherKeys) {
		return boundModifiers.containsAll(otherKeys) && otherKeys.containsAll(boundModifiers);
	}
}
