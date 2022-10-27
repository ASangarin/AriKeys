package eu.asangarin.arikeys.compat;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.mobs.GenericCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.SkillMetadataImpl;
import io.lumine.mythic.core.skills.SkillTriggers;
import org.bukkit.entity.Player;

import java.util.HashSet;

public class MythicMobsCompat {
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
}
