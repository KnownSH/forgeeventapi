package net.knsh.forgeeventapiport.mixin.world.entity.player;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.knsh.forgeeventapiport.accessors.ForgePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.CommonHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.EventHooks;
import net.neoforged.neoforge.event.entity.player.CriticalHitEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin implements ForgePlayer {
    @Shadow @Final private Inventory inventory;

    @Inject(
            method = "die",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$onPlayerDeathEvent(DamageSource damageSource, CallbackInfo ci) {
        if (CommonHooks.onLivingDeath((Player) (Object) this, damageSource)) {
            ci.cancel();
        }
    }
    
    @Inject(
            method = "attack",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z", ordinal = 1)
    )
    private void neoforged$criticalHitEvent(Entity target, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef flag2, @Share("critEvent") LocalRef<CriticalHitEvent> critEvent) {
        CriticalHitEvent hitResult = CommonHooks.getCriticalHit((Player) (Object) this, target, flag2.get(), flag2.get() ? 1.5F : 1.0F);
        flag2.set(hitResult != null);
        critEvent.set(hitResult);
    }
    
    @ModifyConstant(
            method = "attack",
            constant = @Constant(floatValue = 1.5F)
    )
    private float neoforged$modifyCriticalHitDamage(float constant, @Share("critEvent") LocalRef<CriticalHitEvent> critEvent) {
        return critEvent.get().getDamageModifier();
    }

    @Inject(
            method = "getDestroySpeed",
            at = @At("HEAD"),
            cancellable = true
    )
    private void neoforged$overrideDestroySpeed(BlockState state, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(this.getDigSpeed(state, null));
    }

    @Override
    public float getDigSpeed(BlockState state, @Nullable BlockPos pos) {
        Player player = (Player) (Object) this;
        float f = this.inventory.getDestroySpeed(state);
        if (f > 1.0F) {
            int i = EnchantmentHelper.getBlockEfficiency(player);
            ItemStack itemStack = player.getMainHandItem();
            if (i > 0 && !itemStack.isEmpty()) {
                f += (float)(i * i + 1);
            }
        }

        if (MobEffectUtil.hasDigSpeed(player)) {
            f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(player) + 1) * 0.2F;
        }

        if (player.hasEffect(MobEffects.DIG_SLOWDOWN)) {
            f *= switch(player.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                case 0 -> 0.3F;
                case 1 -> 0.09F;
                case 2 -> 0.0027F;
                default -> 8.1E-4F;
            };
        }

        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            f /= 5.0F;
        }

        if (!player.onGround()) {
            f /= 5.0F;
        }

        f = EventHooks.getBreakSpeed((Player) (Object) this, state, f, pos);
        return f;
    }
}
