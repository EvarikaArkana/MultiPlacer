package eva.replacer;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static eva.replacer.config.RePlacerConfig.*;

public interface ItemStackAccess {
    Direction[] firstDir = new Direction[]{null};
    Player[] player =  new Player[]{null};
    static void findFirst() {
        try {
            List<RelPos> poss = getBuilds().get(names.get(selection));
            Direction[] dir = new Direction[]{Direction.DOWN};
            poss.forEach(pos -> {
               if (pos.pos().getX() == 0 && pos.pos().getY() == 0 && pos.pos().getZ() == 0) {
                   dir[0] = pos.dir();
               }
            });
            firstDir[0] = dir[0];
        } catch (NullPointerException ignored) {
            player[0].displayClientMessage(Component.literal("Failed to get build!"), true);
        }
    }

    static void passPlayer(Player player0) {
        player[0] = player0;
    }
}
