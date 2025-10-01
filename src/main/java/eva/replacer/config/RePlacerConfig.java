package eva.replacer.config;

import eva.replacer.RePlacerClient;
import eva.replacer.util.BuildHolder;
import eva.replacer.util.RelPos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static eva.replacer.config.JsonConfigHelper.*;

public class RePlacerConfig {
    static int ver = 0;
    private int v = ver;
    private boolean rotate = true;
    private List<String> names =  new ArrayList<>();

    public static int selection = 0;
    public static boolean reCording = false;
    static String buildName;
    private static List<RelPos> tempBuild;
    private static RePlacerConfig INSTANCE;
    private static Direction dir;

    public static RePlacerConfig getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RePlacerConfig();
        }
        return INSTANCE;
    }

    void updateConfigs(RePlacerConfig config) {
        this.v = config.v;
        this.rotate = config.rotate;
        this.names = config.names;
    }

    public static boolean isRotate() {
        return getInstance().rotate;
    }

    static void setRotate(boolean rotate) {
        getInstance().rotate = rotate;
    }

    public static List<String> getNames() {return getInstance().names;}

    static void setNames(List<String> names) {getInstance().names = names;}

    @NotNull
    public static BuildHolder getBuild() {
        return readBuild(getInstance().names.get(selection));
    }

    public static void saveBuild(boolean confirm) {
        try {
            if (confirm) {
                writeBuild(buildName, dir, tempBuild.toArray(new RelPos[0]));
                RePlacerClient.LOGGER.info("Saved {}!", buildName);
                getInstance().names.add(buildName);
            }
        }catch (NullPointerException e) {
            RePlacerClient.LOGGER.warn("Could not save build! Build was likely empty!");
        }
        buildName = null;
        tempBuild = null;
        reCording = false;
        dir = null;
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
            dir = context.getClickedFace();
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
}
