package eu.asangarin.arikeys.compat.mythic;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.api.AriKeyReleaseEvent;
import eu.asangarin.arikeys.compat.MythicMobsCompat;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicField;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.util.Optional;

@MythicMechanic(name = "onKeyrelease", aliases = {"keyrelease", "kr", "arikeys:onkeyrelease", "arikeys:keyrelease"})
public class AriKeysOnReleaseMechanic extends Aura implements ITargetedEntitySkill, INoTargetSkill {

    @MythicField(name = "onrelease", aliases = {"or"}, description = "The name of the skill to trigger when the key is releaseed.")
    private String onReleaseSkillName;

    @MythicField(name = "key", aliases = {"k", "id"}, description = "The key ID that triggers the skill.")
    private PlaceholderString keyId;

    private Optional<Skill> onReleaseSkill = Optional.empty();

    public AriKeysOnReleaseMechanic(AriKeysPlugin plugin, MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile(), event.getContainer().getConfigLine(), event.getConfig());
        var mlc = event.getConfig();
        this.onReleaseSkillName = mlc.getString(new String[] {"onrelease", "or"});
        this.keyId = mlc.getPlaceholderString(new String[] {"key", "k", "id"}, null);

        getManager().queueSecondPass(() -> {
            if(this.onReleaseSkillName != null) this.onReleaseSkill = getManager().getSkill(file, this, this.onReleaseSkillName);
        });
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()){
            return SkillResult.INVALID_TARGET;
        }
        new Tracker(target, data);
        return SkillResult.SUCCESS;
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        if (!data.getCaster().getEntity().isPlayer()){
            return SkillResult.INVALID_TARGET;
        }
        new Tracker(data.getCaster().getEntity(), data);
        return SkillResult.SUCCESS;
    }

    private class Tracker extends AuraTracker implements IParentSkill, Runnable {

        public Tracker(AbstractEntity entity, SkillMetadata data) {
            super(entity, data);
            start();
        }

        @Override
        public void auraStart(){
            var id = keyId.get(skillMetadata);
            this.registerAuraComponent(
                    Events.subscribe(AriKeyReleaseEvent.class)
                            .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
                            .filter(event -> event.getId().toString().equalsIgnoreCase(id))
                            .handler(event -> {

                                final SkillMetadata data = skillMetadata.deepClone();
                                data.getVariables().putString(MythicMobsCompat.KEY_ID, id);
                                data.setTrigger(BukkitAdapter.adapt(event.getPlayer()));

                                if (executeAuraSkill(onReleaseSkill, data)){
                                    consumeCharge();
                                }
                            }));

            executeAuraSkill(onStartSkill, skillMetadata);
        }
    }
}