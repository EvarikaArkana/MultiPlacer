package eva.replacer.mixin.client;


import eva.replacer.ItemStackAccess;
import eva.replacer.RePlacerClient;
import eva.replacer.config.RelPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eva.replacer.RePlacerClient.modifierBind;
import static eva.replacer.config.RePlacerConfig.*;

@Debug(export = true)
@Mixin(ItemStack.class)
public class ItemStackMixin implements ItemStackAccess {

    @Unique ItemStack itstck = (ItemStack) (Object) this;
    @Unique boolean rePlacing = false;

    @Inject(
            method = "useOn",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;awardStat(Lnet/minecraft/stats/Stat;)V"
            )
    )
    private void placeAgain(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (player[0] == null) ItemStackAccess.passPlayer(context.getPlayer());
        if (rePlacing || !(itstck.getItem() instanceof BlockItem))
            return;
        if (reCording) {
            buildSaver(context);
            return;
        }
        if (!modifierBind.isDown()) return;
        BlockPos pos0 = context.getClickedPos();
        Direction dir = context.getClickedFace();
        pos0 = pos0.relative(dir.getAxis(), switch (dir.getAxisDirection()) {
            case POSITIVE -> 1;
            case NEGATIVE -> -1;
        });
        Player player = context.getPlayer();
        assert player != null;
        rePlacing = true;
        RelPos.setBase(pos0);
        if (isRotate()) RelPos.setDirShift(dir, getBuild()[0].dir());
        try {
            for (RelPos pos : getBuild()) {
                if (player.level().getBlockState(pos.pos()).canBeReplaced()) {
                    BlockHitResult check = new BlockHitResult(
                            pos.vec(),
                            pos.dir(),
                            pos.pos(),
                            false
                    );
                    UseOnContext reContext = new UseOnContext(player, context.getHand(), check);
                    itstck.useOn(reContext);
                }
            }
        } catch (Exception ignored) {
            RePlacerClient.LOGGER.info("Failed to get build!");
            player.displayClientMessage(Component.literal("Failed to get build! 1"), false);
        }
        rePlacing = false;
    }
}