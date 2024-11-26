package eu.asangarin.arikeys.compat.mythic;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.api.AriKeyPressEvent;
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

@MythicMechanic(name = "onKeyPress", aliases = {"keypress", "kp", "arikeys:press", "arikeys:onkeypress", "arikeys:keypress"})
public class AriKeyPressMechanic extends Aura implements ITargetedEntitySkill, INoTargetSkill {

    private String onPressSkillName;

    private PlaceholderString keyId;

    private Optional<Skill> onPressSkill = Optional.empty();

    public AriKeyPressMechanic(AriKeysPlugin plugin, MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile(), event.getContainer().getConfigLine(), event.getConfig());
        var mlc = event.getConfig();
        this.onPressSkillName = mlc.getString(new String[]{"onPress", "op"});
        this.keyId = mlc.getPlaceholderString(new String[]{"key", "k", "keyid", "id"}, null);

        getManager().queueSecondPass(() -> {
            if (this.onPressSkillName != null)
                this.onPressSkill = getManager().getSkill(file, this, this.onPressSkillName);
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
                    Events.subscribe(AriKeyPressEvent.class)
                            .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
                            .filter(event -> event.getId().toString().equalsIgnoreCase(keyId.get(this.skillMetadata, this.entity.get())))
                            .handler(event -> {
                                final SkillMetadata data = skillMetadata.deepClone();
                                data.setTrigger(this.entity.get());

                                if (executeAuraSkill(onPressSkill, data)) {
                                    consumeCharge();
                                }
                            }));

            executeAuraSkill(onStartSkill, skillMetadata);
        }
    }
}
