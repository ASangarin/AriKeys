package eu.asangarin.arikeys.compat.mythic;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.compat.MythicMobsCompat;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.conditions.ISkillMetaCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(name = "arikeys:keyid", aliases = {"keyid", "id", "arikeys:id"})
public final class AriKeysKeyIdCondition implements ISkillMetaCondition {

    @MythicField(name = "mythickey", aliases = {"key","keyid", "id"}, description = "The MythicKey id to check for", defValue = "minecraft:attack")
    private final PlaceholderString keyId;

    public AriKeysKeyIdCondition(AriKeysPlugin plugin, MythicConditionLoadEvent event) {
        this.keyId = event.getConfig().getPlaceholderString(new String[]{"key","keyid", "id"},null);
    }

    @Override
    public boolean check(SkillMetadata data) {
        if (this.keyId == null) return false;
        return data.getVariables().has(MythicMobsCompat.KEY_ID)
                       && data.getVariables().getString(MythicMobsCompat.KEY_ID).equalsIgnoreCase(this.keyId.get(data));
    }

}
