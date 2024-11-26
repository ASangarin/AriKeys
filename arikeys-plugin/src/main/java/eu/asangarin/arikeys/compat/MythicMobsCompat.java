package eu.asangarin.arikeys.compat;

import eu.asangarin.arikeys.AriKeysPlugin;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.CustomComponentRegistry;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import io.lumine.mythic.core.skills.triggers.SkillTriggerMetadata;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class MythicMobsCompat {

	public static final String KEY_ID = "arikeys-key-id";

	public static void registerComponents(){
		new CustomComponentRegistry(AriKeysPlugin.get(), "eu.asangarin.arikeys.compat.mythic");
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

	public static void runSkills(boolean press, NamespacedKey key, Player player){
		var playerData = MythicBukkit.inst().getPlayerManager().getProfile(player);
		if (playerData == null) return;

		var eventBus = MythicBukkit.inst().getSkillManager().getEventBus();
		var data = eventBus.buildSkillMetadata(
				(press) ? SkillTriggers.PRESS : SkillTriggers.RELEASE,
				SkillTriggerMetadata.simple(metaData -> metaData.getVariables().putString(KEY_ID, key.toString())),
				playerData,
				playerData.getEntity(),
				playerData.getLocation(),
				false);

		eventBus.processTriggerMechanics(data);
	}
}
