package net.knsh.forgeeventapiport.mixin.client;

import net.knsh.forgeeventapiport.accessors.ForgeEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(
            method = "addEntity",
            at = @At("TAIL")
    )
    private void neoforged$onAddEntity(int entityId, Entity entityToSpawn, CallbackInfo ci) {
        ((ForgeEntity) entityToSpawn).onAddedToWorld();
    }
}
