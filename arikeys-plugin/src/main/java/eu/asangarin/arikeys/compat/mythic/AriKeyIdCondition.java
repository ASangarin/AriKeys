package eu.asangarin.arikeys.compat.mythic;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.compat.MythicMobsCompat;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicCondition;

@MythicCondition(name = "arikeyid", aliases = {"keyid", "arikeys:keyid", "arikeys:id"})
public class AriKeyIdCondition implements ISkillMetaCondition {

    private final PlaceholderString id;

    public AriKeyIdCondition(AriKeysPlugin plugin, MythicConditionLoadEvent event){
        this.id = event.getConfig().getPlaceholderString(new String[]{"id", "keyid"}, null);
    }

    @Override
    public boolean check(SkillMetadata data) {
        if (id == null) return false;
        return data.getVariables().has(MythicMobsCompat.KEY_ID) &&
                       data.getVariables().getString(MythicMobsCompat.KEY_ID).equalsIgnoreCase(this.id.get(data));
    }
}
