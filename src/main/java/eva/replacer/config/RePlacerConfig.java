package eva.replacer.config;

import eva.replacer.RePlacerClient;
import eva.replacer.util.BuildHolder;
import eva.replacer.util.RelPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static eva.replacer.config.JsonConfigHelper.*;

public class RePlacerConfig {
    static int ver = 0;
    private int v = ver;
    private boolean rotateFace = true;
    private boolean rotatePlace = true;
    private List<String> names =  new ArrayList<>();

    public static int selection = 0;
    public static boolean reCording = false;
    static String buildName;
    private static List<RelPos> tempBuild;
    private static RePlacerConfig INSTANCE;
    private static Direction placeDir;
    private static Direction faceDir;

    public static RePlacerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RePlacerConfig();
        }
        return INSTANCE;
    }

    void updateConfigs(RePlacerConfig config) {
        this.rotateFace = config.rotateFace;
        this.rotatePlace = config.rotatePlace;
        this.names = config.names;
    }

    public static boolean isRotateFace() {
        return getInstance().rotateFace;
    }

    static void setRotateFace(boolean rotate) {
        getInstance().rotateFace = rotate;
    }

    public static boolean isRotatePlace() {
        return getInstance().rotatePlace;
    }

    static void setRotatePlace(boolean rotate) {
        getInstance().rotatePlace = rotate;
    }

    public static List<String> getNames() {return getInstance().names;}

    static void setNames(List<String> names) {getInstance().names = names;}

    public static BuildHolder getBuild() {
        try {
            return readBuild(getInstance().names.get(selection));
        } catch (Exception e) {
            writeSquare();
            return null;
        }
    }

    public static void saveBuild(boolean deny) {
        try {
            if (!deny) {
                writeBuild(buildName, new BuildHolder(placeDir, faceDir, tempBuild.toArray(new RelPos[0])));
                RePlacerClient.LOGGER.info("Saved {}!", buildName);
                getInstance().names.add(buildName);
            }
        }catch (NullPointerException e) {
            RePlacerClient.LOGGER.warn("Could not save build! Build was likely empty!");
        }
        buildName = null;
        tempBuild = null;
        reCording = false;
        placeDir = null;
        faceDir = null;
        RePlacerClient.LOGGER.info("Purged temp vars");
    }

    static void buildDeleter(List<String> list) {
        list.forEach(name -> {
             if (!getInstance().names.contains(name)) {
                 list.remove(name);
             }
        });
        getInstance().names.forEach(name -> {
            if (!list.contains(name)) {
                deleteBuild(name);
            }
        });
        getInstance().names = list;
        selection = 0;
    }

    public static boolean buildSaver(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        boolean disp = false;
        if (tempBuild == null) {
            placeDir = context.getClickedFace();
            if (placeDir.getAxis() == Direction.Axis.Y) {
                assert context.getPlayer() != null;
                faceDir = context.getPlayer().getDirection();
            }
            tempBuild = new ArrayList<>();
            RelPos.setBase(pos);
            disp = true;
        }
        RelPos rel = new RelPos(pos);
        final boolean[] containerCheck = {true};
        tempBuild.forEach(r -> {
            if (r.equals(rel)) containerCheck[0] = false;
        });
        if (containerCheck[0]) tempBuild.add(rel);
        return disp;
    }

    public static String buildDefault() {
        return "{\n" +
                "  \"firstDir\": \"UP\",\n" +
                "  \"faceDir\": \"null\",\n" +
                "  \"posList\": [\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        0,\n" +
                "        0,\n" +
                "        0\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        1,\n" +
                "        0,\n" +
                "        0\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        -1,\n" +
                "        0,\n" +
                "        0\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        0,\n" +
                "        0,\n" +
                "        1\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        -1,\n" +
                "        0,\n" +
                "        1\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        1,\n" +
                "        0,\n" +
                "        1\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        1,\n" +
                "        0,\n" +
                "        -1\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        0,\n" +
                "        0,\n" +
                "        -1\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"pos\": [\n" +
                "        -1,\n" +
                "        0,\n" +
                "        -1\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
