package eva.replacer.config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Hashtable;

public class RelPos {
    private final Direction dir;
    private final int[] pos = new int[3];
    private static BlockPos basePos;
    private static Direction[] shiftDir;
    private static Hashtable<Direction, Direction> shiftImpl;

    public static void setBase(BlockPos p) {basePos = p;}

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
        this.pos[0] = pos.getX() - basePos.getX();
        this.pos[1] = pos.getY() - basePos.getY();
        this.pos[2] = pos.getZ() - basePos.getZ();
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
//        if ((RePlacerConfig.isRotate() || shiftDir[0] == shiftDir[1]) && shiftDir != null) {
        if (false) {
            int[] temp = new int[3];
            for (int i = 0; i < 3; i++) {
                if (!(this.dir.getAxis() == shiftDir[0].getAxis() || this.dir.getAxis() == shiftDir[1].getAxis()))
                    temp[i] = this.pos[i];
                if (shiftDir[0].getAxis() == shiftDir[1].getAxis())
                    temp[i] = this.pos[0] * (shiftDir[0].getAxisDirection() == shiftDir[1].getAxisDirection() ? 1 : -1);
                else
                    temp[i] = this.pos[shiftImpl.get(this.dir).getAxis().ordinal()] * (shiftImpl.get(this.dir).getAxisDirection() == shiftDir[1].getAxisDirection() ? 1 : -1);
            }
            return basePos
                    .relative(Direction.Axis.X, temp[0])
                    .relative(Direction.Axis.Y, temp[1])
                    .relative(Direction.Axis.Z, temp[2]);
        } else {
            return basePos
                    .relative(Direction.Axis.X, this.pos[0])
                    .relative(Direction.Axis.Y, this.pos[1])
                    .relative(Direction.Axis.Z, this.pos[2]);
        }
    }

    public boolean equals(RelPos tester) {
        return this.pos[0] == tester.pos[0] && this.pos[1] == tester.pos[1] && this.pos[2] == tester.pos[2] && this.dir == tester.dir;
    }
}
