package eva.replacer;

import eva.replacer.config.RelPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import static eva.replacer.config.RePlacerConfig.*;

public interface ItemStackAccess {
    Direction[] firstDir = new Direction[]{Direction.DOWN};
    Player[] player =  new Player[]{null};
    static void findFirst() {
        try {
            RelPos[] poss = getBuild();
            Direction[] dir = new Direction[]{Direction.DOWN};
            for (RelPos pos : poss) {
               if (pos.pos().getX() == 0 && pos.pos().getY() == 0 && pos.pos().getZ() == 0) {
                   dir[0] = pos.dir();
               }
            }
            firstDir[0] = dir[0];
        } catch (Exception ignored) {
            player[0].displayClientMessage(Component.literal("Failed to get build! 0"), true);
        }
    }

    static void passPlayer(Player player0) {
        player[0] = player0;
    }
}
