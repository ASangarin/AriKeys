package eu.asangarin.arikeys;

import eu.asangarin.arikeys.cmd.AriKeysCommand;
import eu.asangarin.arikeys.compat.MythicMobsCompat;
import eu.asangarin.arikeys.config.AriKeyInfo;
import eu.asangarin.arikeys.config.AriKeysConfig;
import eu.asangarin.arikeys.util.AriKeysChannels;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AriKeysPlugin extends JavaPlugin {
	private static AriKeysPlugin plugin;

	@Getter
	private final AriKeysConfig conf = new AriKeysConfig();
	private final AriKeysListener mkl = new AriKeysListener();

	public boolean papi, mm;

	@Override
	public void onEnable() {
		plugin = this;
		// Register all the channels needed for incoming/outgoing packets.
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, AriKeysChannels.HANDSHAKE, mkl);
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, AriKeysChannels.KEY_PRESS, mkl);
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, AriKeysChannels.ADD_KEY);
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, AriKeysChannels.LOAD_KEYS);

		// Set up command
		AriKeysCommand cmd = new AriKeysCommand();
		PluginCommand command = getCommand("arikeys");
		if (command != null) {
			command.setExecutor(cmd);
			command.setTabCompleter(cmd);
		}

		papi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
		mm = Bukkit.getPluginManager().getPlugin("MythicMobs") != null;
		if (mm){
			MythicMobsCompat.registerComponents();
		}
		saveDefaultConfig();
		reload();
	}

	@Override
	public void onDisable() {
		// Unregister the channels
		Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
		Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
		plugin = null;
	}

	// arikeys reload
	public void reload() {
		reloadConfig();
		conf.reload(getConfig());
	}

	public static boolean addCustomKeyInfo(AriKeyInfo keyInfo) {
		if(plugin != null)
			return plugin.conf.addCustom(keyInfo);
		return false;
	}

	public static AriKeysPlugin get() {
		return plugin;
	}
}
