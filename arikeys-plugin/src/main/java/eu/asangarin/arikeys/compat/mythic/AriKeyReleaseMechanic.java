package eu.asangarin.arikeys.compat.mythic;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.api.AriKeyReleaseEvent;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.IParentSkill;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.core.skills.auras.Aura;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

import java.util.Optional;

@MythicMechanic(name = "onKeyRelease", aliases = {"keyRelease", "kr", "arikeys:release", "arikeys:onkeyrelease", "arikeys:keyrelease"})
public class AriKeyReleaseMechanic extends Aura implements ITargetedEntitySkill, INoTargetSkill {

    private String onReleaseSkillName;

    private PlaceholderString keyId;

    private Optional<Skill> onReleaseSkill = Optional.empty();

    public AriKeyReleaseMechanic(AriKeysPlugin plugin, MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile(), event.getContainer().getConfigLine(), event.getConfig());
        var mlc = event.getConfig();
        this.onReleaseSkillName = mlc.getString(new String[]{"onRelease", "or"});
        this.keyId = mlc.getPlaceholderString(new String[]{"key", "k", "keyid", "id"}, null);

        getManager().queueSecondPass(() -> {
            if (this.onReleaseSkillName != null)
                this.onReleaseSkill = getManager().getSkill(file, this, this.onReleaseSkillName);
        });
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata data, AbstractEntity target) {
        if (!target.isPlayer()) {
            return SkillResult.INVALID_TARGET;
        }
        new Tracker(target, data);
        return SkillResult.SUCCESS;
    }

    @Override
    public SkillResult cast(SkillMetadata data) {
        if (!data.getCaster().getEntity().isPlayer()) {
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
        public void auraStart() {
            this.registerAuraComponent(
                    Events.subscribe(AriKeyReleaseEvent.class)
                            .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
                            .filter(event -> event.getId().toString().equalsIgnoreCase(keyId.get(this.skillMetadata, this.entity.get())))
                            .handler(event -> {
                                final SkillMetadata data = skillMetadata.deepClone();
                                data.setTrigger(this.entity.get());

                                if (executeAuraSkill(onReleaseSkill, data)) {
                                    consumeCharge();
                                }
                            }));

            executeAuraSkill(onStartSkill, skillMetadata);
        }
    }
}
