package eva.replacer.mixin.client;

import eva.replacer.RePlacerClient;
import eva.replacer.util.RelPos;
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
import static eva.replacer.config.JsonConfigHelper.writeSquare;
import static eva.replacer.config.RePlacerConfig.*;
import static eva.replacer.util.BuildHolder.setBaseDir;
import static eva.replacer.util.BuildHolder.setFacing;

@Mixin(BlockItem.class)
public class BlockItemMixin {

    @Unique private final BlockItem blit = (BlockItem) (Object) this;
    @Unique private boolean rePlacing = false;
    @Unique private boolean no = false;
    @Inject(
            method = "place",
            at = @At("TAIL")
    )
    private void placeAgain(BlockPlaceContext context, CallbackInfoReturnable<InteractionResult> cir) {
        if (no) {
            no = false;
            return;
        }
        if (rePlacing)
            return;
        if (reCording) {
            if (buildSaver(context)) {
                assert context.getPlayer() != null;
                context.getPlayer().displayClientMessage(Component.literal("Central block saved!"), true);
            }
            return;
        }
        if (!isHeldOrToggled()) return;
        Player player = context.getPlayer();
        assert player != null;
        if (getBuild() == null) {
            RePlacerClient.LOGGER.info("Failed to get build!");
            player.displayClientMessage(Component.literal("Failed to get build!"), true);
            player.displayClientMessage(Component.literal("Writing default build."), false);
            writeSquare();
            no = true;
            return;
        }
        rePlacing = true;
        RelPos.setBase(context.getClickLocation(), context.getClickedPos());
        try {
            if (isRotatePlace())
                setBaseDir(context.getClickedFace());
            if (isRotateFace())
                setFacing(player.getDirection());
            getBuild().rotateEach(pos -> {
                if (!context.getItemInHand().isEmpty())
                    if (player.canInteractWithBlock(pos.pos(), player.blockInteractionRange() * 1.2))
                        if (!pos.equals(new RelPos(0, 0, 0)))
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
            });
        } catch (Exception ignored) {}
        setBaseDir(null);
        rePlacing = false;
    }
}
