package eva.replacer.util;

import net.minecraft.core.Direction;

import static eva.replacer.config.RePlacerConfig.isRotate;

public record BuildHolder(Direction firstDir, RelPos[] posList) {
    private static Direction baseDir;
    public static void setBaseDir(Direction baseDir) {
        BuildHolder.baseDir = baseDir;
    }
    public static BuildHolder rotate(BuildHolder holder) {
        RelPos[] posList = holder.posList();
        if (!isRotate() || holder.firstDir() == baseDir)
            return holder;
        if (holder.firstDir().getAxis() == baseDir.getAxis()) {
            for (int i = 0; i < posList.length; ++i) {
                int[] vals = posList[i].vals();
                vals[holder.firstDir().getAxis().ordinal()] *= -1;
                posList[i] = new RelPos(vals);
            }
            return new BuildHolder(holder.firstDir(), posList);
        }
        int[] ind = {holder.firstDir().getAxis().ordinal(), baseDir.getAxis().ordinal()};
        int[] neg;
        if (holder.firstDir().getAxisDirection() == baseDir.getAxisDirection())
            neg = new int[]{-1, 1};
        else
            neg = new int[]{1, -1};
        for (int i = 0; i < posList.length; ++i) {
            int[] vals = new int[3];
            vals[ind[0]] = posList[i].vals()[ind[1]] * neg[1];
            vals[ind[1]] = posList[i].vals()[ind[0]] * neg[0];
            vals[3 - ind[0] - ind[1]] = posList[i].vals()[3 - ind[0] - ind[1]];
            posList[i] = new RelPos(vals);
        }
        return new BuildHolder(baseDir, posList);
    }

    public static String buildDefault() {
        return "{\n" +
                "  \"firstDir\": \"UP\",\n" +
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
