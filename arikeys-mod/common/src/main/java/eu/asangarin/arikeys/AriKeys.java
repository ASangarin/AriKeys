package eu.asangarin.arikeys;

import eu.asangarin.arikeys.util.KeyCategoryComparator;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AriKeys {
	public static final String MOD_ID = "arikeys";
	private static final Map<Identifier, AriKey> CURRENT_KEYS = new HashMap<>();

	private static final KeyCategoryComparator CATEGORY_COMPARATOR = new KeyCategoryComparator();

	public static void init() {
		/*ClientPlayerEvent.CLIENT_PLAYER_QUIT.register((player) -> {
			// Clean up after disconnecting...
			AriKeysIO.save();
			CURRENT_KEYS.clear();
		});
		ClientPlayerEvent.CLIENT_PLAYER_JOIN.register((player) -> {
			CURRENT_KEYS.clear();
			// Send a packet informing the server that a client with the mod has joined
			NetworkManager.sendToServer(HANDSHAKE_CHANNEL, PacketByteBufs.create());
		});

		// Receive keybindings data from the server
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, ADD_KEY_CHANNEL, (buf, context) -> {
			buf.readString(); // Man, I love working with bytes...
			String path = buf.readString();
			buf.readString(); // Don't you love it too?
			String key = buf.readString();
			int defKey = buf.readInt();
			buf.readString(); // Wow, it got even better!
			String name = buf.readString();
			buf.readString(); // This is just incredible.
			String category = buf.readString();

			// Always read data async and then use client.execute() after for thread safety.
			Identifier id = new Identifier(path, key);
			context.queue(() -> CURRENT_KEYS.put(id, new AriKey(id, name, category, InputUtil.Type.KEYSYM.createFromCode(defKey))));
		});

		/* When the server finish sending keybinds it will send the load packet and
		 the client will load the keybinding data from the AriKeysIO */
		//NetworkManager.registerReceiver(NetworkManager.Side.C2S, LOAD_CHANNEL, (buf, context) -> context.queue(AriKeysIO::load));
	}

	public static Collection<AriKey> getKeybinds() {
		return CURRENT_KEYS.values();
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

	public static void add(Identifier id, AriKey ariKey) {
		CURRENT_KEYS.put(id, ariKey);
	}
}
