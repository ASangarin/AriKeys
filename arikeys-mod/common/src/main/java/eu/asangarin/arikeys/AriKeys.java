package eu.asangarin.arikeys;

import eu.asangarin.arikeys.util.AriKeysIO;
import eu.asangarin.arikeys.util.KeyCategoryComparator;
import eu.asangarin.arikeys.util.network.KeyAddData;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AriKeys {
	public static final String MOD_ID = "arikeys";
	private static final Map<Identifier, AriKey> CURRENT_KEYS = new HashMap<>();

	private static final KeyCategoryComparator CATEGORY_COMPARATOR = new KeyCategoryComparator();

	public static Collection<AriKey> getKeybinds() {
		return CURRENT_KEYS.values();
	}

	public static void handleConnect() {
		// Clean up, then perform handshake protocol
		AriKeys.clear();
		AriKeysPlatform.sendHandshake();
	}

	public static void handleDisconnect() {
		// Clean up after disconnection
		AriKeysIO.save();
		AriKeys.clear();
	}

	/* Custom sorting rules as running Collections.sort()
	 will cause a crash, since these keybinds aren't
	 registered the usual way. */
	public static List<AriKey> getSortedKeybinds() {
		List<AriKey> set = new ArrayList<>(CURRENT_KEYS.values());
		set.sort(CATEGORY_COMPARATOR);
		return set;
	}

	public static Identifier cleanIdentifier(String key) {
		return new Identifier(Identifier.DEFAULT_NAMESPACE, key.replace("key.", "").replace(".", "").toLowerCase());
	}

	public static void clear() {
		CURRENT_KEYS.clear();
	}

	public static void add(KeyAddData key) {
		Identifier id = key.getId();
		CURRENT_KEYS.put(id, new AriKey(key));
	}
}
