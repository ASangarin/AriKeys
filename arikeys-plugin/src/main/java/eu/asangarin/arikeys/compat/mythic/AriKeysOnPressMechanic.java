package eu.asangarin.arikeys.compat.mythic;

import eu.asangarin.arikeys.AriKeysPlugin;
import eu.asangarin.arikeys.api.AriKeyPressEvent;
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

@MythicMechanic(name = "onKeyPress", aliases = {"keyPress", "kp", "arikeys:onkeypress", "arikeys:keypress"})
public class AriKeysOnPressMechanic extends Aura implements ITargetedEntitySkill, INoTargetSkill {

    @MythicField(name = "onPress", aliases = {"op"}, description = "The name of the skill to trigger when the key is pressed.")
    private String onPressSkillName;

    @MythicField(name = "key", aliases = {"k"}, description = "The key ID that triggers the skill.")
    private PlaceholderString keyId;

    private Optional<Skill> onPressSkill = Optional.empty();

    public AriKeysOnPressMechanic(AriKeysPlugin plugin, MythicMechanicLoadEvent event) {
        super(event.getContainer().getManager(), event.getContainer().getFile(), event.getContainer().getConfigLine(), event.getConfig());
        var mlc = event.getConfig();

        this.onPressSkillName = mlc.getString(new String[] {"onpress", "op"});
        this.keyId = mlc.getPlaceholderString(new String[] {"key", "k", "id"}, null);

        getManager().queueSecondPass(() -> {
            if(this.onPressSkillName != null) this.onPressSkill = getManager().getSkill(file, this, this.onPressSkillName);
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
                    Events.subscribe(AriKeyPressEvent.class)
                            .filter(event -> event.getPlayer().getUniqueId().equals(this.entity.get().getUniqueId()))
                            .filter(event -> event.getId().toString().equalsIgnoreCase(id))
                            .handler(event -> {

                                final SkillMetadata data = skillMetadata.deepClone();
                                data.getVariables().putString(MythicMobsCompat.KEY_ID, id);
                                data.setTrigger(BukkitAdapter.adapt(event.getPlayer()));

                                if (executeAuraSkill(onPressSkill, data)){
                                    consumeCharge();
                                }
                            }));

            executeAuraSkill(onStartSkill, skillMetadata);
        }
    }
}
