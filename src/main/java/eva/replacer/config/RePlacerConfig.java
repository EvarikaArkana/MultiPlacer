package eva.replacer.config;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class RePlacerConfig {

    private boolean rotate = true;

    private Hashtable<String, Set<RelPos>> builds = new Hashtable<>();

    public static List<String> names =  new ArrayList<>();
    public static int selection = 0;
    public static boolean reCording = false;
    public static boolean reFirst = true;
    public static String buildName;
    public static List<RelPos> build;
    private static RePlacerConfig INSTANCE;

    public static RePlacerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RePlacerConfig();
        }
        return INSTANCE;
    }

    public void updateConfigs(RePlacerConfig config) {
        this.rotate = config.rotate;
        this.builds = config.builds;
        names = buildNames();
    }

    public static boolean isRotate() {
        return getInstance().rotate;
    }

    static void setRotate(boolean rotate) {
        getInstance().rotate = rotate;
    }

    public static Hashtable<String, Set<RelPos>> getBuilds() {return getInstance().builds;}

    public static void saveBuild(boolean confirm) {
        getInstance().builds.put(buildName, new HashSet<>(build));
        buildName = null;
        build = null;
        reFirst = true;
        reCording = false;
    }

    static void saveBuilds(List<String> list) {
        final List<String> names2 = list;
        Hashtable<String, Set<RelPos>> buildict = new Hashtable<>();
        list.forEach(name -> {
             try {
                 buildict.put(name, getInstance().builds.get(name));
             } catch (NullPointerException ignored) {
                 names2.remove(name);
             }
        });
        RePlacerConfig.names = names2;
        getInstance().builds = buildict;
    }

    public static List<String> buildNames() {
        List<String> buildNames = new ArrayList<>();
        Enumeration<String> it = getInstance().builds.keys();
        while (it.hasMoreElements())
            buildNames.add(it.nextElement());
        names = buildNames;
        return names;
    }

    public static class RelPos {
        private final double[] vec = new double[3];
        private final Direction dir;
        private final int[] pos = new int[3];
        private static double[] baseVec;
        private static int[] basePos;

        public static void setBase(Vec3 v, BlockPos p) {
            baseVec = new double[]{v.x, v.y, v.z};
            basePos = new int[]{p.getX(), p.getY(), p.getZ()};
        }
        public RelPos(Vec3 vec, Direction dir, BlockPos pos) {
            this.vec[0] = vec.x - baseVec[0];
            this.vec[1] = vec.y - baseVec[1];
            this.vec[2] = vec.z - baseVec[2];
            this.dir = dir;
            this.pos[0] = pos.getX() - basePos[0];
            this.pos[1] = pos.getY() - basePos[1];
            this.pos[2] = pos.getZ() - basePos[2];
        }

        public Vec3 vec() {
            return new Vec3(this.vec[0] + baseVec[0], this.vec[1] + baseVec[1], this.vec[2] + baseVec[2]);
        }
        public Direction dir() {return dir;}
        public BlockPos pos() {
            return new BlockPos(this.pos[0] + basePos[0], this.pos[1] + basePos[1], this.pos[2] + basePos[2]);
        }
    }

    public static void buildSaver(UseOnContext context) {
        if (reFirst) {
            build = new ArrayList<>();
        }
        BlockPos pos = context.getClickedPos();
        Direction dir = context.getClickedFace();
        Vec3 vec = context.getClickLocation();
        if (reFirst) {
            RelPos.setBase(vec, pos);
            reFirst = false;
        }
        build.add(new RelPos(vec, dir, pos));
    }
}
