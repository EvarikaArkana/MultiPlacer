package eva.replacer.mixin.client;


import com.llamalad7.mixinextras.sugar.Local;
import eva.replacer.config.RePlacerConfig;
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

import java.util.Set;

import static eva.replacer.RePlacerClient.modifierBind;
import static eva.replacer.config.RePlacerConfig.*;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique ItemStack itstck = (ItemStack) (Object) this;
    @Unique boolean rePlacing = false;

    @Inject(
            method = "useOn",
            at = @At(
                    value = "RETURN",
                    ordinal = 1
            )
    )
    private void placeAgain(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir, @Local InteractionResult result) {
        try {
            if (!(result instanceof InteractionResult.Success) || rePlacing || !(itstck.getItem() instanceof BlockItem))
                return;

            if (reCording) {
                buildSaver(context);
                return;
            }
            if (!modifierBind.isDown()) return;
            BlockPos pos0 = context.getClickedPos();
            Direction dir = context.getClickedFace();
            Vec3 vec = context.getClickLocation();
            pos0 = pos0.relative(dir.getAxis(), switch (dir.getAxisDirection()) {
                case POSITIVE -> 1;
                case NEGATIVE -> -1;
            });
            vec = vec.relative(dir, switch (dir.getAxisDirection()) {
                case POSITIVE -> 1;
                case NEGATIVE -> -1;
            });
            Player player = context.getPlayer();
            assert player != null;
            rePlacing = true;
            RelPos.setBase(vec, pos0);
            Set<RelPos> poss = getBuilds().get(names.get(selection));
            poss.forEach(pos -> {
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
            });


            rePlacing = false;
        } catch (Exception ignored) {

        }
    }

}