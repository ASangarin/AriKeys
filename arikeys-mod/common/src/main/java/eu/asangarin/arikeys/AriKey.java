package eu.asangarin.arikeys;

import eu.asangarin.arikeys.util.network.KeyAddData;
import lombok.Getter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

@Getter
public class AriKey {
	private final Identifier id;
	private final String name, category;
	private final InputUtil.Key keyCode;
	private InputUtil.Key boundKeyCode;

	public AriKey(KeyAddData data) {
		this(data.getId(), data.getName(), data.getCategory(), InputUtil.Type.KEYSYM.createFromCode(data.getDefKey()));
	}

	public AriKey(Identifier id, String name, String category, InputUtil.Key keyCode) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.keyCode = keyCode;
		this.boundKeyCode = keyCode;
	}

	public void setBoundKey(InputUtil.Key key) {
		this.boundKeyCode = key;
	}

	public boolean hasChanged() {
		return !keyCode.equals(boundKeyCode);
	}

	public boolean isUnbound() {
		return boundKeyCode.equals(InputUtil.UNKNOWN_KEY);
	}
}
