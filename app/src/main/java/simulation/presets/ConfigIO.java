package simulation.presets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import simulation.SimulationPreset;
import simulation.configs.SimulationConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class PresetLoader {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static List<SimulationPreset> loadPresets(InputStream is) {
        return List.of(gson.fromJson(new InputStreamReader(is),SimulationPreset[].class));
    }

    public static void saveConfig(SimulationConfig config, Path path) throws IOException {
        try(Writer w = Files.newBufferedWriter(path)) {
            gson.toJson(config, w);
        }
    }

    public static SimulationConfig loadConfig(Path path) throws IOException {
        try(Reader r = Files.newBufferedReader(path)) {
            return gson.fromJson(r, SimulationConfig.class);
        }
    }
}
