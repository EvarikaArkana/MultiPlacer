package eva.replacer.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eva.replacer.RePlacerClient;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class JsonConfigHelper {
    private static final File folder = new File("config");
    private static final File buildFolder = new File(folder,"RePlacer Builds");
    private static File rePlacerConfig;
    private static final Hashtable<String, File> builds =  new Hashtable<>();
    public static Gson configGson = new GsonBuilder().setPrettyPrinting().create();

    public static void init() {
        createConfig();
        readFromConfig();
        writeToConfig();
        createBuilds();
    }

    private static void createConfig() {
        if (!folder.exists()) folder.mkdir();

        if (folder.isDirectory()) {
            rePlacerConfig = new File(folder, "replacer.json");
            boolean seemsValid;
            if (rePlacerConfig.exists()) {
                try {
                    String templateConfigJson = Files.readString(Path.of(rePlacerConfig.getPath()));
                    seemsValid = templateConfigJson.trim().startsWith("{\n  \"v\": " + RePlacerConfig.ver + ",");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                seemsValid = true;
            }
            if (!rePlacerConfig.exists() || !seemsValid) {
                if (!seemsValid) {
                    RePlacerClient.LOGGER.info("Found invalid config file, creating new config file at './config/replacer.json'.");
                }
                try {
                    rePlacerConfig.createNewFile();
                    String json = configGson.toJson(RePlacerConfig.getInstance());
                    FileWriter writer = new FileWriter(rePlacerConfig);
                    writer.write(json);
                    writer.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
    }

    private static void readFromConfig() {
        try {
            RePlacerConfig config = configGson.fromJson(new FileReader(rePlacerConfig), RePlacerConfig.class);
            RePlacerConfig.getInstance().updateConfigs(config);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    static void writeToConfig() {
        try {
            String json = configGson.toJson(RePlacerConfig.getInstance());
            FileWriter writer = new FileWriter(rePlacerConfig, false);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void createBuilds() {
        if (!buildFolder.exists()) buildFolder.mkdir();

        if (buildFolder.isDirectory()) {
            final Hashtable<String, File> tempBuilds =  new Hashtable<>();
            List<String> names =  new ArrayList<>();
            if (RePlacerConfig.getNames() == null || RePlacerConfig.getNames().isEmpty()) return;
            for (String name : RePlacerConfig.getNames()) {
                File file = new  File(buildFolder, name + ".json");
                boolean seemsValid;
                if (file.exists()) {
                    try {
                        String buildJson = Files.readString(Path.of(file.getPath()));
                        seemsValid = buildJson.trim().startsWith("{");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    seemsValid = true;
                }
                if (!seemsValid || !file.exists()) {
                    if (!seemsValid) RePlacerClient.LOGGER.info("Found invalid build file, purging.");
                    else RePlacerClient.LOGGER.info("Found invalid build name, purging.");
                    file.delete();
                }
                else {
                    tempBuilds.put(name, file);
                    names.add(name);
                }
            }
            builds.putAll(tempBuilds);
            RePlacerConfig.setNames(new ArrayList<>(builds.keySet()));
        }
    }

    static boolean checkBuild(String name) {
        return builds.containsKey(name);
    }

    static void deleteBuild(String name) {
        builds.get(name).delete();
    }

    static void writeBuild(String name, RelPos[] build) {
        try {
            builds.put(name, new File(buildFolder, name + ".json"));
            String json = configGson.toJson(new BuildHolder(build));
            FileWriter writer = new FileWriter(builds.get(name), false);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    static RelPos[] readBuild(String name) {
        try {
            BuildHolder holder = configGson.fromJson(new FileReader(builds.get(name)), BuildHolder.class);
            return holder.posList();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}