package eva.replacer.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eva.replacer.RePlacerClient.modifierBind;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique ItemStack itstck = (ItemStack) (Object) this;
    @Unique boolean rePlacing = false;

    @Unique int[][] poslist = {{1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}};

    @Inject(
            method = "useOn",
            at = @At(
                    value = "RETURN",
                    ordinal = 1
            )
    )
    private void placeAgain(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local InteractionResult result) {
        if (!modifierBind.isDown()) return;
        if (!(result instanceof InteractionResult.Success) || rePlacing || !(itstck.getItem() instanceof BlockItem)) return;

        Player player = context.getPlayer();
        assert player != null;
        int c = itstck.getCount();
        if (player.hasInfiniteMaterials()) c = 8;
        BlockPos pos = context.getClickedPos();
        Direction direction = context.getClickedFace();

        pos = pos.relative(direction.getAxis(), switch (direction.getAxisDirection()) {
            case POSITIVE -> 1;
            case NEGATIVE -> -1;
        });

        Direction.Axis[] axes = new Direction.Axis[2];
        int i = 0;
        for (Direction.Axis axis : Direction.Axis.values()) {
            if (axis != direction.getAxis()) {
                axes[i++] = axis;
            }
        }

        Direction[] dirs = new Direction[4];
        i = 0;
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() != direction.getAxis())
                dirs[i++] = dir;
        }


        rePlacing = true;

        Vec3 ref = pos.getCenter();

        for (i = 0; i < poslist.length; i++) {
            BlockPos rePos = pos.relative(axes[0], poslist[i][0]).relative(axes[1], poslist[i][1]);
            if (!player.level().getBlockState(rePos).canBeReplaced()) continue;
            BlockHitResult hit = new BlockHitResult(
                    ref,
                    dirs[i % 4],
                    rePos,
                    false
            );
            UseOnContext reContext = new UseOnContext(player, context.getHand(), hit);
            itstck.useOn(reContext);
        }

        rePlacing = false;
    }

}