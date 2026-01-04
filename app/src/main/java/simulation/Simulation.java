package simulation;

import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import model.plant.AbstractPlant;
import simulation.configs.SimulationConfig;

import java.util.*;

import static java.util.Collections.max;

public class Simulation implements Runnable {
    private final GlobeWorldMap map;
    private static SimulationConfig simulationConfig;
    private List<Animal> aliveAnimals = new ArrayList<>();
    private List<Animal> deadAnimals = new ArrayList<>();
    private boolean isRunning = true;
    private int currentDay = 0;

    public static void setConfig(SimulationConfig config) {
        simulationConfig = config;
        Animal.setAnimalConfig(simulationConfig.animalConfig());
        AbstractPlant.setPlantConfig(simulationConfig.plantConfig());
        Genome.setConfig(simulationConfig.genomeConfig());
        GlobeWorldMap.setMapConfig(simulationConfig.mapConfig());
    }

    public Simulation(GlobeWorldMap map) {
        this.map = map;

        map.spawnPlants(simulationConfig.startingPlantAmount());

        Random rn = new Random();
        for (int i = 0; i < simulationConfig.startingAnimalAmount(); i++) {
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

    public void removeDeadAnimals() {
        map.removeDeadAnimals(currentDay);
    }

    private void moveAnimals() {
        for (Animal animal : this.aliveAnimals) {
            map.move(animal);
        }
    }

    // maybe we could move this so that map handles it
    public void eatPlants() {
        // this probably should be changed idk (concurent modification idk how to fix differently)
        Collection<AbstractPlant> plants = List.copyOf(this.map.getPlants().values());
        for (AbstractPlant plant : plants) {
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
                    int energyNeededToMate = Animal.getEnergyNeededToMate();

                    if (parent1.getEnergy() >= energyNeededToMate && parent2.getEnergy() >= energyNeededToMate ){
                        Animal child = parent1.mate(parent2, this.currentDay);

                        map.place(child);
                        this.aliveAnimals.add(child);
                    }
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

    // for testing
    public void addAnimalToAlive(Animal animal) {
        this.aliveAnimals.add(animal);
    }
}
