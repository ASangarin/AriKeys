package eu.asangarin.arikeys.util;

import com.google.common.base.Charsets;
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.io.Files;
import eu.asangarin.arikeys.AriKey;
import eu.asangarin.arikeys.AriKeys;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.nbt.NbtCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/* Minecrafts way of storing keybinds. Not the cleanest, but it works. */
@SuppressWarnings("UnstableApiUsage")
public class AriKeysIO {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Splitter COLON_SPLITTER = Splitter.on(':').limit(2);
	private static final File KEYBIND_FILE = new File(MinecraftClient.getInstance().runDirectory, "arikeys.txt");

	public static void load() {
		try {
			if (!KEYBIND_FILE.exists()) return;

			NbtCompound nbtCompound = new NbtCompound();
			BufferedReader bufferedReader = Files.newReader(KEYBIND_FILE, Charsets.UTF_8);

			try {
				bufferedReader.lines().forEach((line) -> {
					try {
						Iterator<String> iterator = COLON_SPLITTER.split(line).iterator();
						nbtCompound.putString(iterator.next(), iterator.next());
					} catch (Exception exception) {
						LOGGER.warn("Skipping bad option: {}", line);
					}

				});
			} catch (Throwable throwable) {
				try {
					bufferedReader.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}
			bufferedReader.close();

			for (AriKey ariKey : AriKeys.getKeybinds()) {
				String key = "arikey_" + ariKey.getId().toString().replace(":", "+");

				String defKey = ariKey.getBoundKeyCode().getTranslationKey();
				String keybind = MoreObjects.firstNonNull(nbtCompound.contains(key) ? nbtCompound.getString(key) : null, defKey);

				Set<ModifierKey> modifiers = new HashSet<>(ariKey.getModifiers());
				for (ModifierKey modifier : ModifierKey.ALL) {
					String modKey = key + "_" + modifier.getId();
					if (nbtCompound.contains(modKey)) modifiers.add(modifier);
					else modifiers.remove(modifier);
				}

				if (!defKey.equals(keybind) || !modifiers.containsAll(ariKey.getModifiers())) {
					ariKey.setBoundKey(InputUtil.fromTranslationKey(keybind), false);
					ariKey.setBoundModifiers(modifiers);
				}
			}

			KeyBinding.updateKeysByCode();
		} catch (Exception exception) {
			LOGGER.error("Failed to load arikeys bindings", exception);
		}
	}

	public static void save() {
		try {
			final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(KEYBIND_FILE), StandardCharsets.UTF_8));

			try {
				for (AriKey ariKey : AriKeys.getKeybinds()) {
					printWriter.print("arikey_" + ariKey.getId().toString().replace(":", "+"));
					printWriter.print(':');
					printWriter.println(ariKey.getBoundKeyCode().getTranslationKey());

					for (ModifierKey modifier : ariKey.getBoundModifiers()) {
						printWriter.print("arikey_" + ariKey.getId().toString().replace(":", "+") + "_" + modifier.getId());
						printWriter.print(':');
						printWriter.println("true");
					}
				}
			} catch (Throwable throwable) {
				try {
					printWriter.close();
				} catch (Throwable throwable2) {
					throwable.addSuppressed(throwable2);
				}

				throw throwable;
			}

			printWriter.close();
		} catch (Exception exception) {
			LOGGER.error("Failed to save arikeys bindings", exception);
		}
	}
}
