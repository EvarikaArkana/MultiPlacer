package eva.replacer.mixin.client;

import eva.replacer.RePlacerClient;
import eva.replacer.util.BuildHolder;
import eva.replacer.util.RelPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static eva.replacer.RePlacerClient.isHeldOrToggled;
import static eva.replacer.config.RePlacerConfig.*;
import static eva.replacer.util.BuildHolder.rotate;
import static eva.replacer.util.BuildHolder.setBaseDir;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Unique private final BlockItem blit = (BlockItem) (Object) this;
    @Unique private boolean rePlacing = false;
    @Inject(
            method = "place",
            at = @At("TAIL")
    )
    private void placeAgain(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (rePlacing)
            return;
        if (reCording) {
            buildSaver(context);
            return;
        }
        if (!isHeldOrToggled()) return;
        BlockPos pos0 = context.getClickedPos();
        Player player = context.getPlayer();
        assert player != null;
        rePlacing = true;
        RelPos.setBase(pos0);
        try {
            setBaseDir(context.getClickedFace());
            BuildHolder build = rotate(getBuild());
            for (RelPos pos : build.posList()) {
                if (pos.equals(new RelPos(new int[]{0,0,0}))) {
                    continue;
                }
                if (player.level().getBlockState(pos.pos()).canBeReplaced()) {
                    blit.place(new BlockPlaceContext(
                                    player,
                                    context.getHand(),
                                    player.getItemInHand(context.getHand()),
                                    new BlockHitResult(
                                            pos.vec(),
                                            context.getClickedFace(),
                                            pos.pos(),
                                            context.isInside()
                                    )
                            )
                    );
                    player.awardStat(Stats.ITEM_USED.get(blit));
                }
            }
        } catch (Exception ignored) {
            RePlacerClient.LOGGER.info("Failed to get build!");
            player.displayClientMessage(Component.literal("Failed to get build!"), false);
        }
        setBaseDir(null);
        rePlacing = false;
    }
}
