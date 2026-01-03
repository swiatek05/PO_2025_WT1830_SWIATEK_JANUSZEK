package simulation;

import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import model.plant.AbstractPlant;
import simulation.configs.SimulationConfig;

import java.util.*;

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
                if (animal.isDead()) {
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

    private void eatPlants() {
        for (AbstractPlant plant : this.map.getPlants().values()) {
            List <Animal> potentialAnimals = map.animalsAt(plant.getPosition());
            if(!potentialAnimals.isEmpty()) {
                Animal strongest = map.findStrongestAnimal(potentialAnimals,currentDay);
                strongest.eat(plant);
                // we now need to remove the plant (easy) and then figure out if it was in the steppe or in the jungle
                // and accordingly add a free space there
            }
        }
    }

    private void mateAnimals() {

    }

    private void growPlants() {
        map.spawnPlants(simulationConfig.plantCountToGrowEveryday());
    }
}
