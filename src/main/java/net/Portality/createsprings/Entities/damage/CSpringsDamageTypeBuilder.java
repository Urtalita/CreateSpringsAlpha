package net.Portality.createsprings.Entities.damage;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageScaling;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DeathMessageType;

public class CSpringsDamageTypeBuilder {
    protected final ResourceKey<DamageType> key;

    protected String msgId;
    protected DamageScaling scaling;
    protected float exhaustion = 0.0f;
    protected DamageEffects effects;
    protected DeathMessageType deathMessageType;

    public CSpringsDamageTypeBuilder(ResourceKey<DamageType> key) {
        this.key = key;

        this.scaling = DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER;
        this.effects = DamageEffects.HURT;
        this.deathMessageType = DeathMessageType.DEFAULT;
        simpleMsgId();
    }

    /**
     * Set the message ID. this is used for death message lang keys.
     *
     * @see #deathMessageType(DeathMessageType)
     */
    public CSpringsDamageTypeBuilder msgId(String msgId) {
        this.msgId = msgId;
        return this;
    }

    public CSpringsDamageTypeBuilder simpleMsgId() {
        return msgId(key.location().getNamespace() + "." + key.location().getPath());
    }

    /**
     * Set the scaling of this type. This determines whether damage is increased based on difficulty or not.
     */
    public CSpringsDamageTypeBuilder scaling(DamageScaling scaling) {
        this.scaling = scaling;
        return this;
    }

    /**
     * Set the exhaustion of this type. This is the amount of hunger that will be consumed when an entity is damaged.
     */
    public CSpringsDamageTypeBuilder exhaustion(float exhaustion) {
        this.exhaustion = exhaustion;
        return this;
    }

    /**
     * Set the effects of this type. This determines the sound that plays when damaged.
     */
    public CSpringsDamageTypeBuilder effects(DamageEffects effects) {
        this.effects = effects;
        return this;
    }

    public CSpringsDamageTypeBuilder deathMessageType(DeathMessageType deathMessageType) {
        this.deathMessageType = deathMessageType;
        return this;
    }

    public DamageType build() {
        if (msgId == null) {
            simpleMsgId();
        }
        if (scaling == null) {
            scaling(DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER);
        }
        if (effects == null) {
            effects(DamageEffects.HURT);
        }
        if (deathMessageType == null) {
            deathMessageType(DeathMessageType.DEFAULT);
        }
        return new DamageType(msgId, scaling, exhaustion, effects, deathMessageType);
    }

    public DamageType register(BootstapContext<DamageType> ctx) {
        DamageType type = build();
        ctx.register(key, type);
        return type;
    }
}
