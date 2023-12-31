package net.knsh.forgeeventapiport.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.knsh.forgeeventapiport.accessors.ForgeLevelRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Inject(
            method = "renderLevel",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 1)
    )
    private void neoforged$onRenderLevelEvent(
            float partialTicks, long finishTimeNano, PoseStack poseStack, CallbackInfo ci,
            @Local LocalRef<Matrix4f> matrix4f, @Local LocalRef<Camera> camera)
    {
        ClientHooks.dispatchRenderStage(
                RenderLevelStageEvent.Stage.AFTER_LEVEL,
                this.minecraft.levelRenderer,
                poseStack,
                matrix4f.get(),
                ((ForgeLevelRenderer) this.minecraft.levelRenderer).getTicks(),
                camera.get(),
                ((ForgeLevelRenderer) this.minecraft.levelRenderer).getFrustum()
        );
    }
}
