package simulation;

import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import model.map.WorldMap;
import model.plant.AbstractPlant;
import simulation.configs.SimulationConfig;

public class Simulation {
    private final WorldMap map;
    public Simulation(GlobeWorldMap map, SimulationConfig config) {
        Animal.setAnimalConfig(config.animalConfig());
        AbstractPlant.setPlantConfig(config.plantConfig());
        Genome.setConfig(config.genomeConfig());
        this.map = map;

        map.spawnPlants(config.startingPlantAmount());
        for (int i = 0; i < config.startingAnimalAmount(); i++) {
            Vector2d position = new Vector2d()
        }
    }
}
