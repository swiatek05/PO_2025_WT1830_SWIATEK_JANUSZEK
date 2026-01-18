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
    private  SimulationConfig simulationConfig;
    private SimulationPresenter presenter;
    private List<Animal> aliveAnimals = new ArrayList<>();
    private int totalDeadAnimals = 0;
    private long sumAgeOfDead = 0;
    private boolean isRunning = true;
    private volatile boolean isPaused = false;
    private int currentDay = 0;
    private final int MOVE_DELAY = 300;
    private final SimulationStatistics statistics=new SimulationStatistics();

    public Simulation(GlobeWorldMap map,SimulationConfig simulationConfig,SimulationPresenter presenter) {
        this.map = map;
        this.simulationConfig=simulationConfig;
        this.presenter=presenter;

        map.spawnPlants(simulationConfig.startingPlantAmount());

        Random rn = new Random();
        for (int i = 0; i < simulationConfig.startingAnimalAmount(); i++) {
            Vector2d position = new Vector2d(rn.nextInt(map.getMapWidth()), rn.nextInt(map.getMapHeight()));
            Animal animal = new Animal(position, this.currentDay,simulationConfig.animalConfig(),simulationConfig.genomeConfig());
            map.place(animal);
            this.aliveAnimals.add(animal);
        }
    }

    @Override
    public void run() {

        while (isRunning) {
            if (isPaused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            killStarvedAnimals();
            removeDeadAnimals();
            moveAnimals();
            eatPlants();
            mateAnimals();
            growPlants();
            //map.mapChanged("Dzień: "+currentDay);

            statistics.update(aliveAnimals,map.getPlantsCount(),map.getFreeFields(),sumAgeOfDead,totalDeadAnimals);

            List<Integer> MinMaxEnergy=map.findMinMaxEnergy();

            if (presenter != null) {
                presenter.mapChanged(currentDay,MinMaxEnergy.get(0),MinMaxEnergy.get(1));
            }
            currentDay++;
            if(presenter != null) {
                try {
                    Thread.sleep(MOVE_DELAY);
                } catch (InterruptedException e) {
                    System.out.println("Wątek symulacji przerwany");
                    break;
                }
            }
        }
    }
    void killStarvedAnimals() {
        Iterator<Animal> it=aliveAnimals.iterator();
        while (it.hasNext()) {
            Animal animal=it.next();
            if (animal.getEnergy()<=0){
                animal.killOff(currentDay);
                totalDeadAnimals++;
                sumAgeOfDead += animal.getAgeForDead();
                it.remove();
            }
        }
    }
    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void stop() {
        isRunning = false;
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
        /*Collection<AbstractPlant> plants = List.copyOf(this.map.getPlants().values());
        for (AbstractPlant plant : plants) {
            List <Animal> potentialAnimals = map.animalsAt(plant.getPosition());
            if(!potentialAnimals.isEmpty()) {
                Animal strongest = max(potentialAnimals);
                strongest.eat(plant);
                map.removePlantFromMap(plant);
            }
        }*/
        map.eatPlants();
    }

    private void mateAnimals() {
        /*for (Animal animal : this.getAliveAnimals()) {
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
        }*/
        List<Animal> newAnimals =map.reproduce(currentDay);
        aliveAnimals.addAll(newAnimals);
    }

    private void growPlants() {
        map.spawnPlants(simulationConfig.plantCountToGrowEveryday());
    }

    public List<Animal> getAliveAnimals() {
        return aliveAnimals;
    }


    public void addAnimalToAlive(Animal animal) {
        this.aliveAnimals.add(animal);
    }
    public SimulationStatistics getStatistics(){
        return statistics;

    }
}
