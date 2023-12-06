package net.knsh.neoforged.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.knsh.neoforged.bus.api.ForgeEvent;
import net.knsh.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Spider.class)
public class SpiderMixin extends Monster {
    protected SpiderMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(
            method = "canBeAffected",
            at = @At("RETURN")
    )
    private boolean forgeevents$canBeAffected$eventCall(boolean returnValue, @Local MobEffectInstance effectInstance) {
        if (effectInstance.getEffect() == MobEffects.POISON) {
            MobEffectEvent.Applicable event = new MobEffectEvent.Applicable((Spider) (Object) this, effectInstance);
            MobEffectEvent.Applicable.EVENT.invoker().onApplicableEvent(event);
            return event.getResult() == ForgeEvent.Result.ALLOW;
        }
        return super.canBeAffected(effectInstance);
    }
}
