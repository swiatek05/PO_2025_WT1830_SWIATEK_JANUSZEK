package simulation.presets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import simulation.SimulationPreset;
import simulation.configs.SimulationConfig;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ConfigIO {
    private static final Gson gson = new Gson();

    public static List<SimulationPreset> loadPresets() {
        try (InputStream is = ConfigIO.class.getResourceAsStream("/presets.json")) {
            return List.of( gson.fromJson(new InputStreamReader(is), SimulationPreset[].class));
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się wczytać presetów", e);
        }
    }

    public static void savePresetToFile(Path path, SimulationConfig config) {
        try (Writer w = Files.newBufferedWriter(path)) {
            gson.toJson(config,w);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się zapisać presetu", e);
        }
    }

    public static SimulationConfig readPresetFromFile(Path path) {
        try (Reader r = Files.newBufferedReader(path)) {
            return gson.fromJson(r,SimulationConfig.class);
        } catch (Exception e) {
            throw new RuntimeException("Nie udało się odczytać presetu z pliku");
        }
    }
}
