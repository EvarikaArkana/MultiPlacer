package eva.replacer.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import eva.replacer.rendering.BlockHighlightRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject(
            method = "renderBlockOutline",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/state/BlockOutlineRenderState;highContrast()Z"
            ),
            cancellable = true
    )
    private void renderBoxes(MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean bl, LevelRenderState levelRenderState, CallbackInfo ci) {
        if (BlockHighlightRenderer.renderRePlacerBox(poseStack)) ci.cancel();
    }
}
