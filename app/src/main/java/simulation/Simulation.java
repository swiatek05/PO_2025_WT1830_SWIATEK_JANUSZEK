package simulation;

import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import model.plant.AbstractPlant;
import simulation.configs.SimulationConfig;

import java.util.*;

import static java.util.Collections.max;
import static java.util.Collections.sort;

public class Simulation implements Runnable {
    private final GlobeWorldMap map;
    private final SimulationConfig simulationConfig;
    private List<Animal> aliveAnimals = new ArrayList<>();
    private List<Animal> deadAnimals = new ArrayList<>();
    private boolean isRunning = true;
    private int currentDay = 0;

    public Simulation(GlobeWorldMap map, SimulationConfig config) {
        this.map = map;
        this.simulationConfig = config;

        Animal.setAnimalConfig(config.animalConfig());
        AbstractPlant.setPlantConfig(config.plantConfig());
        Genome.setConfig(config.genomeConfig());
        GlobeWorldMap.setMapConfig(config.mapConfig());

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
        while (isRunning) {

            for (Animal animal : aliveAnimals) {
                if (animal.getEnergy() < 0) {
                    animal.killOff(currentDay);
                    aliveAnimals.remove(animal);
                    deadAnimals.add(animal);
                }
            }

            removeDeadAnimals();
            moveAnimals();
            eatPlants();
            mateAnimals();
            growPlants();
            currentDay++;
        }
    }

    private void removeDeadAnimals() {
        map.removeDeadAnimals(currentDay);
    }

    private void moveAnimals() {
        for (Animal animal : this.aliveAnimals) {
            map.move(animal);
        }
    }

    // maybe we could move this so that map handles it
    private void eatPlants() {
        for (AbstractPlant plant : this.map.getPlants().values()) {
            List <Animal> potentialAnimals = map.animalsAt(plant.getPosition());
            if(!potentialAnimals.isEmpty()) {
                Animal strongest = max(potentialAnimals);
                strongest.eat(plant);
                map.removePlantFromMap(plant);
            }
        }
    }

    private void mateAnimals() {
        for (Animal animal : this.getAliveAnimals()) {
            List <Animal> animalsToMate = map.animalsAt(animal.getPosition());
            Collections.sort(animalsToMate);

            if (animalsToMate.size() > 1) {
                for (int i = 1; i < animalsToMate.size(); i+=2) {
                    Animal parent1 = animalsToMate.get(i);
                    Animal parent2 = animalsToMate.get(i-1);
                    // need to check if animals have enough energy to mate but that is stored in AnimalConfig
                    Animal child = parent1.mate(parent2,this.currentDay);

                    map.place(animal);
                    this.aliveAnimals.add(animal);
                }
            }
        }
    }

    private void growPlants() {
        map.spawnPlants(simulationConfig.plantCountToGrowEveryday());
    }

    public List<Animal> getAliveAnimals() {
        return aliveAnimals;
    }
}
