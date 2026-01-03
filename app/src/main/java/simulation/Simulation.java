package simulation;

import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import model.map.WorldMap;
import model.plant.AbstractPlant;
import simulation.configs.SimulationConfig;

import java.util.*;

public class Simulation implements Runnable {
    private final WorldMap map;
    private final SimulationConfig simulationConfig;
    private final List<Animal> aliveAnimals = new ArrayList<>();
    private int currentDay = 0;

    public Simulation(GlobeWorldMap map, SimulationConfig config) {
        this.map = map;
        this.simulationConfig = config;

        Animal.setAnimalConfig(config.animalConfig());
        AbstractPlant.setPlantConfig(config.plantConfig());
        Genome.setConfig(config.genomeConfig());

        map.spawnPlants(config.startingPlantAmount());

        Random rn = new Random();
        for (int i = 0; i < config.startingAnimalAmount(); i++) {
            Vector2d position = new Vector2d(rn.nextInt(map.getMapWidth()), rn.nextInt(map.getMapHeight()));
            Animal animal = new Animal(position, this.currentDay);
            map.place(animal);
            this.aliveAnimals.add(animal);
        }
    }

    @Override
    public void run() {

    }
}
