package eu.asangarin.arikeys.config;

import eu.asangarin.arikeys.AriKeysPlugin;
import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

@Getter
public class AriKeysConfig {
	private final Map<NamespacedKey, AriKeyInfo> keyInfoList = new HashMap<>();

	private boolean eventOnCommand;

	// Loads all the keys specified in config.yml and puts them in keyInfoList.
	public void reload(FileConfiguration config) {
		eventOnCommand = config.getBoolean("run_event_on_command");
		keyInfoList.clear();
		ConfigurationSection keys = config.getConfigurationSection("Keys");
		if (keys == null) return;
		for (String key : keys.getKeys(false)) {
			if (keys.contains(key, true)) {
				ConfigurationSection section = keys.getConfigurationSection(key);
				if (section == null) {
					error(key);
					return;
				}

				AriKeyInfo info = AriKeyInfo.from(section);
				if (info == null) {
					error(key);
					return;
				}

				keyInfoList.put(info.getId(), info);
			}
		}
	}

	private void error(String name) {
		AriKeysPlugin.get().getLogger().severe("Unable to add AriKey: '" + name + "' - Check your syntax!");
	}

	public boolean addCustom(AriKeyInfo keyInfo) {
		NamespacedKey key = keyInfo.getId();
		if(keyInfoList.containsKey(key))
			return false;
		keyInfoList.put(key, keyInfo);
		return true;
	}
}
