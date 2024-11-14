package eu.asangarin.arikeys.cmd;

import eu.asangarin.arikeys.AriKeysPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AriKeysCommand implements CommandExecutor, TabCompleter {
	private static final List<String> COMMANDS = Arrays.asList("reload", "info");

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		if (args.length < 1) return false;

		String cmd = args[0];
		if (cmd.equals("reload")) {
			AriKeysPlugin.get().reload();
			sender.sendMessage(ChatColor.AQUA + "[AriKeys] " + ChatColor.GREEN + "Plugin was successfully reloaded.");
			return true;
		}
		if (cmd.equals("info")) {
			sender.sendMessage(ChatColor.AQUA + "[AriKeys] " + ChatColor.GREEN + "Version 2.3.1");
			return true;
		}

		return false;
	}

	@Nullable
	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
		final List<String> completions = new ArrayList<>();
		StringUtil.copyPartialMatches(args[0], COMMANDS, completions);
		Collections.sort(completions);
		return completions;
	}
}