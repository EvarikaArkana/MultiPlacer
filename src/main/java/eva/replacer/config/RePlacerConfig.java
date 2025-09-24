package eva.replacer.config;

import net.minecraft.core.Direction;

import java.util.*;

public class RePlacerConfig {

    private boolean rotate = true;

    private Hashtable<String, RelPos[]> builds = new Hashtable<>();

    private static List<String> names =  new ArrayList<>();

    public static boolean reCording = false;
    public static boolean reFirst = true;
    public static String buildName = "";
    public static RelPos[] build;

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
    }

    public static boolean isRotate() {
        return getInstance().rotate;
    }

    static void setRotate(boolean rotate) {
        getInstance().rotate = rotate;
    }

    public static void saveBuild(boolean confirm) {
        getInstance().builds.put(buildName, build);
        buildName = null;
        build = null;
    }

    static void saveBuilds(List<String> list) {
        final List<String> names2 = list;
        Hashtable<String, RelPos[]> buildict = new Hashtable<>();
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

    static List<String> buildNames() {
        List<String> buildNames = new ArrayList<>();
        Enumeration<String> it = getInstance().builds.keys();
        while (it.hasMoreElements())
            buildNames.add(it.nextElement());
        return buildNames;
    }

    public class RelPos {
        private final Direction dir;
        private final int[] pos;
        public RelPos(Direction dir, int x, int y, int z) {
            this.dir = dir;
            this.pos = new int[]{x, y, z};
        }
        public RelPos(Direction dir, int x, int y) {
            this.dir = dir;
            this.pos = new int[]{x, y};
        }
        public Direction dir() {return dir;}
        public int[] pos() {return pos;}
    }

}
