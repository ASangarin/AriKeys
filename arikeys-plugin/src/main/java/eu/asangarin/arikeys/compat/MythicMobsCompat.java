package eu.asangarin.arikeys.compat;

import eu.asangarin.arikeys.AriKeysPlugin;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillTrigger;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.players.PlayerData;
import io.lumine.mythic.core.skills.CustomComponentRegistry;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class MythicMobsCompat {

	public static final String KEY_ID = "key-id";

	public static final SkillTrigger PRESS = SkillTrigger.create("PRESS");
	public static final SkillTrigger RELEASE = SkillTrigger.create("RELEASE");

	public static void registerComponents(){
		PRESS.register();
		RELEASE.register();

		var components = new CustomComponentRegistry(AriKeysPlugin.get(), "eu.asangarin.arikeys.compat.mythic");

	}

	public static void runSkill(String id, Player player) {
		if (id == null || id.isEmpty()) return;

		MythicBukkit.inst().getSkillManager().getSkill(id).ifPresent(skill -> {
			AbstractPlayer trigger = BukkitAdapter.adapt(player);
			GenericCaster genericCaster = new GenericCaster(trigger);
			SkillMetadata skillMeta = new SkillMetadataImpl(SkillTriggers.API, genericCaster, trigger, BukkitAdapter.adapt(player.getLocation()),
					new HashSet<>(), null, 1.0F);
			if (skill.isUsable(skillMeta, SkillTriggers.API)) skill.execute(skillMeta);
		});
	}

	public static void runSkills(boolean press, NamespacedKey key, Player player) {
		PlayerData playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
		if (playerData == null) return;
		var eventBus = MythicBukkit.inst().getSkillManager().getEventBus();
		var data = eventBus
						   .buildSkillMetadata(
								   (press) ? PRESS : RELEASE,
								   SkillTriggerMetadata.simple(skill -> skill.getVariables().putString(KEY_ID, key.toString())),
								   playerData,
								   playerData.getEntity(),
								   null,
								   false
						   );

		eventBus.processTriggerMechanics(data);
	}
}
