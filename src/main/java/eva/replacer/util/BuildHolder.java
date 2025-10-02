package eva.replacer.util;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static eva.replacer.config.RePlacerConfig.isRotateFace;
import static eva.replacer.config.RePlacerConfig.isRotatePlace;

public record BuildHolder(Direction firstDir, @Nullable Direction faceDir, RelPos[] posList) {
    private static Direction baseDir;
    private static Direction facing;

    public BuildHolder(Direction firstDir, RelPos[] posList) {
        this(firstDir, null, posList);
    }

    public static void setBaseDir(Direction baseDir) {
        BuildHolder.baseDir = baseDir;
    }
    public static void setFacing(Direction facing) {BuildHolder.facing = facing;}

    private RelPos rotatePlace(RelPos pos) {
        if (!isRotatePlace() || this.firstDir == baseDir)
            return pos;
        if (this.firstDir().getAxis() == baseDir.getAxis()) {
            int ind = this.firstDir().getAxis().ordinal();
            int[] vals = new int[3];
            vals[0] = pos.vals()[0];
            vals[1] = pos.vals()[1];
            vals[2] = pos.vals()[2];
            vals[ind] *= -1;
            return new RelPos(vals);
        }
        int[] ind = {this.firstDir().getAxis().ordinal(), baseDir.getAxis().ordinal()};
        int[] neg;
        if (this.firstDir().getAxisDirection() == baseDir.getAxisDirection())
            neg = new int[]{-1, 1};
        else
            neg = new int[]{1, -1};
        int[] vals = new int[3];
        vals[ind[0]] = pos.vals()[ind[1]] * neg[1];
        vals[ind[1]] = pos.vals()[ind[0]] * neg[0];
        vals[3 - ind[0] - ind[1]] = pos.vals()[3 - ind[0] - ind[1]];
        return new RelPos(vals);
    }

    private RelPos rotateFace(RelPos pos) {
        if (!isRotateFace() || baseDir.getAxis() != Direction.Axis.Y || facing == this.faceDir || this.faceDir == null)
            return pos;
        if (facing.getAxis() == this.faceDir.getAxis()) {
            return new RelPos(-pos.vals()[0], pos.vals()[1], -pos.vals()[2]);
        }
        if (this.faceDir.getClockWise() == facing) {
            return new RelPos(-pos.vals()[2], pos.vals()[1], pos.vals()[0]);
        } else
            return new RelPos(pos.vals()[2], pos.vals()[1], -pos.vals()[0]);
    }

    public void rotateEach(Consumer<RelPos> action) {
        for (RelPos pos : this.posList) {
            if (!(isRotatePlace() || isRotateFace()))
                action.accept(pos);
            else if (baseDir.getAxis() == Direction.Axis.Y)
                action.accept(rotateFace(rotatePlace(pos)));
            else {
                if (isRotateFace() && this.firstDir.getAxis() == Direction.Axis.Y) {
                    Direction temp = baseDir;
                    baseDir = firstDir();
                    pos = rotateFace(pos);
                    baseDir = temp;
                    if (firstDir.getAxis() == Direction.Axis.Y)
                        pos = new RelPos(-pos.vals()[0], pos.vals()[1], -pos.vals()[2]);
                }
                action.accept(rotatePlace(pos));
            }
        }
    }
}
