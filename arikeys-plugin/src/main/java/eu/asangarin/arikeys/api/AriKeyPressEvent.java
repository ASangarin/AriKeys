package eu.asangarin.arikeys.api;

import lombok.Getter;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class AriKeyPressEvent extends AriKeyEvent {
	@Getter
	private static final HandlerList handlerList = new HandlerList();

	public AriKeyPressEvent(Player player, NamespacedKey id, boolean registered) {
		super(player, id, registered);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return handlerList;
	}
}