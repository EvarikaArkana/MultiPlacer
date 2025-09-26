package eva.replacer.config;

import eva.replacer.RePlacerClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RePlacerConfig {

    private boolean rotate = true;
    private Hashtable<String, List<RelPos>> builds = new Hashtable<>();

    public static List<String> names =  new ArrayList<>();
    public static int selection = 0;
    public static boolean reCording = false;
    static String buildName;
    private static Set<RelPos> build;
    private static RePlacerConfig INSTANCE;

    public static RePlacerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RePlacerConfig();
        }
        return INSTANCE;
    }

    public void updateConfigs(RePlacerConfig config) {
        this.rotate = config.rotate;
        this.builds = new Hashtable<>();
        this.builds.putAll(config.builds);
        names = buildNames();
    }

    public static boolean isRotate() {
        return getInstance().rotate;
    }

    static void setRotate(boolean rotate) {
        getInstance().rotate = rotate;
    }

    @NotNull
    public static List<RelPos> getBuild() {
        return getInstance().builds.get(names.get(selection));
    }

    public static Hashtable<String, List<RelPos>> getBuilds() {return getInstance().builds;}

    public static void saveBuild(boolean confirm) {
        if (confirm) {
            getInstance().builds.put(buildName, new ArrayList<>(build));
            RePlacerClient.LOGGER.info("Saved {}!", buildName);
            names.add(buildName);
        }
        buildName = null;
        build = null;
        reCording = false;
        RePlacerClient.LOGGER.info("Purged temp vars");
    }

    static void saveBuilds(List<String> list) {
        final List<String> names2 = list;
        Hashtable<String, List<RelPos>> buildict = new Hashtable<>();
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
        private final Direction dir;
        private final int[] pos = new int[3];
        private static int[] basePos;
        private static Direction[] shiftDir;
        private static Hashtable<Direction, Direction> shiftImpl;

        public static void setBase(BlockPos p) {
            basePos = new int[]{p.getX(), p.getY(), p.getZ()};
        }
        public static void setDirShift(Direction baseDir, Direction firstDir) {
            shiftDir = new Direction[]{baseDir, firstDir};
            shiftImpl = new Hashtable<>();
            shiftImpl.put(baseDir, firstDir);
            if (baseDir == firstDir) return;
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
            if (!isRotate()) return this.dir;
            if (shiftDir[0] == shiftDir[1]) return this.dir;
            if (!(this.dir.getAxis() == shiftDir[0].getAxis() || this.dir.getAxis() == shiftDir[1].getAxis())) return this.dir;
            return shiftImpl.get(this.dir);
        }
        public BlockPos pos() {
            int[] temp = new int[3];
            if (isRotate()) {
                for (int i = 0; i < 3; i++) {
                    if (!(this.dir.getAxis() == shiftDir[0].getAxis() || this.dir.getAxis() == shiftDir[1].getAxis())) temp[i] = this.pos[i];
                    if (shiftDir[0].getAxis() == shiftDir[1].getAxis())
                        temp[i] = this.pos[0] * (shiftDir[0].getAxisDirection() == shiftDir[1].getAxisDirection() ? 1 : -1);
                    else temp[i] = this.pos[shiftImpl.get(this.dir).getAxis().ordinal()] *  (shiftImpl.get(this.dir).getAxisDirection() == shiftDir[1].getAxisDirection() ? 1 : -1);
                }
            } else {temp = this.pos.clone();}
            return new BlockPos(temp[0] + basePos[0], temp[1] + basePos[1], temp[2] + basePos[2]);
        }
    }

    public static void buildSaver(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Direction dir = context.getClickedFace();
        if (build == null) {
            build = ConcurrentHashMap.newKeySet();
            RelPos.setBase(pos);
        }
        build.add(new RelPos(dir, pos));
    }
}
