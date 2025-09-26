package eva.replacer.config;

import eva.replacer.RePlacerClient;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.UseOnContext;
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
    public static RelPos[] getBuild() {
        return readBuild(getInstance().names.get(selection));
    }

    public static void saveBuild(boolean confirm) {
        if (confirm) {
            writeBuild(buildName, tempBuild.toArray(new RelPos[0]));
            RePlacerClient.LOGGER.info("Saved {}!", buildName);
            getInstance().names.add(buildName);
        }
        buildName = null;
        tempBuild = null;
        reCording = false;
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

    public static void buildSaver(UseOnContext context) {
        BlockPos pos = context.getClickedPos();
        Direction dir = context.getClickedFace();
        pos = pos.relative(dir.getAxis(), switch (dir.getAxisDirection()) {
            case POSITIVE -> 1;
            case NEGATIVE -> -1;
        });
        if (tempBuild == null) {
            tempBuild = new ArrayList<>();
            RelPos.setBase(pos);
        }
        RelPos rel = new RelPos(dir, pos);
        final boolean[] containerCheck = {true};
        tempBuild.forEach(r -> {
            if (r.equals(rel)) containerCheck[0] = false;
        });
        if (containerCheck[0]) tempBuild.add(rel);
    }
}
