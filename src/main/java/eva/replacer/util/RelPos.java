package eva.replacer.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class RelPos {
    private final int[] pos = new int[3];
    private static BlockPos basePos;
    private static Vec3 baseVec;

    public static void setBase(BlockPos p) {
        basePos = p;
    }

    public static void setBase(Vec3 v, BlockPos p) {
        basePos = p;
        baseVec = new Vec3(
                v.x() - p.getX(),
                v.y() - p.getY(),
                v.z() - p.getZ()
        );
    }

    public RelPos(BlockPos pos) {
        this.pos[0] = pos.getX() - basePos.getX();
        this.pos[1] = pos.getY() - basePos.getY();
        this.pos[2] = pos.getZ() - basePos.getZ();
    }

    public RelPos(int... pos) {
        try {
            this.pos[0] = pos[0];
            this.pos[1] = pos[1];
            this.pos[2] = pos[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new RuntimeException(e);
        }
    }

    public Vec3 vec() {
        Vec3 v = pos().getCenter();
        return new Vec3(
                v.x() + baseVec.x(),
                v.y() + baseVec.y(),
                v.z() + baseVec.z()
        );
    }

    public BlockPos pos() {
        return basePos
                .relative(Direction.Axis.X, this.pos[0])
                .relative(Direction.Axis.Y, this.pos[1])
                .relative(Direction.Axis.Z, this.pos[2]);
    }

    public int[] vals() {
        return pos;
    }
    public boolean equals(RelPos tester) {
        return this.pos[0] == tester.pos[0] && this.pos[1] == tester.pos[1] && this.pos[2] == tester.pos[2];
    }
}
