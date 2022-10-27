package eu.asangarin.arikeys.config;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.compat.MythicMobsCompat;
import lombok.Getter;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

@Getter
public class AriKeyInfo {
	private AriKeyInfo(NamespacedKey id, String name, String category, int def, String command, String mythicPress, String mythicRelease) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.def = def;
		this.command = command;
		this.mythicPress = mythicPress;
		this.mythicRelease = mythicRelease;
	}

	private final NamespacedKey id;
	private final String name, category, command, mythicPress, mythicRelease;
	private final int def;

	// Using a static method to insert KeyInfo verification code.
	public static @Nullable AriKeyInfo from(ConfigurationSection config) {
		if (config.contains("Id") && config.contains("Name") && config.contains("DefaultKey") && config.contains("Category")) {
			NamespacedKey key = NamespacedKey.fromString(config.getString("Id", ""), AriKeysPlugin.get());
			if (key == null) return null;

			return new AriKeyInfo(key, config.getString("Name"), config.getString("Category"), config.getInt("DefaultKey"),
					config.getString("RunCommand", ""), config.getString("SkillPress", ""), config.getString("SkillRelease", ""));
		}
		return null;
	}

	public boolean runCommand(Player player) {
		if (command == null || command.isEmpty()) return false;

		final boolean isAdmin = command.startsWith("!");
		String cmd = (isAdmin ? command.substring(1) : command).replace("%player%", player.getName());
		if (AriKeysPlugin.get().papi) cmd = PlaceholderAPI.setPlaceholders(player, cmd);

		if (isAdmin) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		else Bukkit.dispatchCommand(player, cmd);
		return true;
	}

	public boolean hasCommand() {
		return command != null && !command.isEmpty();
	}

	public void mmSkill(Player player, boolean press) {
		if (!AriKeysPlugin.get().mm) return;
		MythicMobsCompat.runSkill(press ? mythicPress : mythicRelease, player);
	}
}
