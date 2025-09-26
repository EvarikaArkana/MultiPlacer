package eva.replacer.config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;

public class RelPos {
    private final Direction dir;
    private final int[] pos = new int[3];
    private static int[] basePos;
    private static Direction[] shiftDir;
    private static Hashtable<Direction, Direction> shiftImpl;

    public static void setBase(BlockPos p) {
        basePos = new int[]{p.getX(), p.getY(), p.getZ()};
    }

    public static void setDirShift(@NotNull Direction baseDir, @NotNull Direction firstDir) {
        shiftDir = new Direction[]{baseDir, firstDir};
        if (baseDir == firstDir) return;
        shiftImpl = new Hashtable<>();
        shiftImpl.put(baseDir, firstDir);
        if (baseDir == firstDir.getOpposite()) {
            shiftImpl.put(firstDir, baseDir);
            return;
        }
        shiftImpl.put(firstDir, baseDir.getOpposite());
        shiftImpl.put(baseDir.getOpposite(), firstDir.getOpposite());
        shiftImpl.put(firstDir.getOpposite(), baseDir);
    }

    public RelPos(Direction dir, BlockPos pos) {
        this.dir = dir;
        this.pos[0] = pos.getX() - basePos[0];
        this.pos[1] = pos.getY() - basePos[1];
        this.pos[2] = pos.getZ() - basePos[2];
    }

    public Vec3 vec() {
        return pos().getCenter();
    }

    public Direction dir() {
        if (!RePlacerConfig.isRotate() || shiftDir == null) return this.dir;
        if (shiftDir[0] == shiftDir[1]) return this.dir;
        if (!(this.dir.getAxis() == shiftDir[0].getAxis() || this.dir.getAxis() == shiftDir[1].getAxis()))
            return this.dir;
        return shiftImpl.get(this.dir);
    }

    public BlockPos pos() {
        int[] temp = new int[3];
        if (RePlacerConfig.isRotate()) {
            for (int i = 0; i < 3; i++) {
                if (!(this.dir.getAxis() == shiftDir[0].getAxis() || this.dir.getAxis() == shiftDir[1].getAxis()))
                    temp[i] = this.pos[i];
                if (shiftDir[0].getAxis() == shiftDir[1].getAxis())
                    temp[i] = this.pos[0] * (shiftDir[0].getAxisDirection() == shiftDir[1].getAxisDirection() ? 1 : -1);
                else
                    temp[i] = this.pos[shiftImpl.get(this.dir).getAxis().ordinal()] * (shiftImpl.get(this.dir).getAxisDirection() == shiftDir[1].getAxisDirection() ? 1 : -1);
            }
        } else {
            temp = this.pos.clone();
        }
        return new BlockPos(temp[0] + basePos[0], temp[1] + basePos[1], temp[2] + basePos[2]);
    }

    public boolean equals(RelPos tester) {
        return this.pos[0] == tester.pos[0] && this.pos[1] == tester.pos[1] && this.pos[2] == tester.pos[2] && this.dir == tester.dir;
    }
}
