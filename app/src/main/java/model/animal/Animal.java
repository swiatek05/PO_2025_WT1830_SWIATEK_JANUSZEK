package model.animal;

import model.MapDirection;
import model.Vector2d;
import model.WorldElement;
import model.map.GlobeWorldMap;
import model.plant.AbstractPlant;
import org.jspecify.annotations.NonNull;
import simulation.configs.AnimalConfig;
import simulation.configs.GenomeConfig;

import java.util.*;

public class Animal implements WorldElement, Comparable<Animal> {
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private final Genome genome;
    private final AnimalConfig animalConfig;
    private final GenomeConfig genomeConfig;
    private int dayOfDeath = -1;
    private int age = 0;
    private final int birthDay;
    private int childCount = 0;
    private int countEatenPlants = 0;
    private final Set<Animal> descendants= new HashSet<>();
    private Animal parent1=null;
    private Animal parent2=null;
    // initial animals created at the start of simulation
    public Animal(Vector2d position, int currentDay, AnimalConfig animalConfig, GenomeConfig genomeConfig) {
        this.position = position;
        this.animalConfig = animalConfig;
        this.genomeConfig = genomeConfig;
        this.energy = animalConfig.startEnergy();
        this.genome = new Genome(genomeConfig);
        this.orientation = MapDirection.getRandomDirection();
        this.birthDay = currentDay;


    }

    // create animal by mating
    public Animal(Vector2d position, Genome genome, int currentDay, AnimalConfig animalConfig, GenomeConfig genomeConfig,Animal p1,Animal p2) {
        this.position = position;
        this.animalConfig = animalConfig;
        this.genomeConfig = genomeConfig;
        this.energy = animalConfig.energyUsedToMate() * 2;
        this.genome = genome;
        this.orientation = MapDirection.getRandomDirection();
        this.birthDay = currentDay;
        this.parent1 = p1;
        this.parent2 = p2;
    }

    // only used for testing
    public Animal(Vector2d position, Genome genome, MapDirection orientation, int energy, int currentDay, int childCount, int age, AnimalConfig animalConfig, GenomeConfig genomeConfig) {
        this.animalConfig = animalConfig;
        this.genomeConfig = genomeConfig;
        this.position = position;
        this.energy = energy;
        this.genome = genome;
        this.orientation = orientation;
        this.birthDay = currentDay;
        this.childCount = childCount;
        this.age = age;
    }
    public void addDescendant(Animal descendant) {
        Queue<Animal> toProcess = new LinkedList<>();
        toProcess.add(this);

        while (!toProcess.isEmpty()) {
            Animal current = toProcess.poll();
            if (current.descendants.add(descendant)) {
                if (current.parent1 != null) {
                    toProcess.add(current.parent1);
                }
                if (current.parent2 != null) {
                    toProcess.add(current.parent2);
                }
            }
        }
    }

    // mating behaviour
    public Animal mate(Animal partner, int currentDay) {
        Genome childGenome = new Genome(this, partner, this.genomeConfig);
        this.decreaseEnergyMating();
        partner.decreaseEnergyMating();
        this.childCount++;
        partner.childCount++;

        Animal child=new Animal(this.position,childGenome,currentDay,animalConfig,genomeConfig,this,partner);
        this.addDescendant(child);
        partner.addDescendant(child);

        return child;
    }

    public void move(GlobeWorldMap map) {
        Vector2d unitVector = this.orientation.toUnitVector();
        Vector2d wantedPosition = this.position.add(unitVector);
        this.position = calculateNewPosition(unitVector, this.position, wantedPosition, map);
        this.decreaseEnergyEndOfDay();
        this.age++;

        // idk maybe this should be somewhere else
        this.genome.moveCurrentGeneIndex();
    }

    private Vector2d calculateNewPosition(Vector2d unitVector, Vector2d oldPosition, Vector2d wantedPosition, GlobeWorldMap map) {
        int height = map.getMapHeight();
        int width = map.getMapWidth();
        int wantedX = wantedPosition.getX();
        int wantedY = wantedPosition.getY();
        Vector2d finalPosition = wantedPosition;
        finalPosition = new Vector2d((wantedX + width) % width, wantedY); //to zawsze dziala , wiec nie trzeba rozpatrywac przypadkow
        if (wantedY < 0 || wantedY >= height) {
            this.orientation = this.getOrientation().reverse();
            finalPosition = new Vector2d(finalPosition.getX(), oldPosition.getY());
        }
        return finalPosition;
    }

    public void rotate() {
        this.orientation = this.orientation.rotate(this.genome.getActiveGene());
    }

    public void eat(AbstractPlant plant) {
        if (plant.getConsumptionEnergy() < 0) {
            this.energy += (int) (plant.getConsumptionEnergy() * (1 - genome.getImmunityPercentage()));
        } else {
            this.energy += plant.getConsumptionEnergy();
        }
        countEatenPlants++;
    }

    public void decreaseEnergyMating() {
        this.energy -= animalConfig.energyUsedToMate();
    }

    public void decreaseEnergyEndOfDay() {
        this.energy -= animalConfig.dayEndEnergyLoss();
    }

    public boolean canMate() {
        return this.energy >= animalConfig.ableToMateEnergyNeeded();
    }

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() {
        return position;
    }

    @Override
    public String toString() {

        return "Zwierze, który ma %d energii, %d lat, %d dzieci,%d %% odporności, genotyp: %s, aktywny gen: %d, zjadł %d roślin, posiada %d potomków"
                .formatted(this.energy,this.age,this.childCount,(int) (this.getGenome().getImmunityPercentage()*100),
                this.getGenome().getGenes().toString(),this.getGenome().getActiveGene(),this.countEatenPlants,this.descendants.size());
    }

    public int getEnergy() {
        return energy;
    }

    public int getChildrenCount() {
        return childCount;
    }

    public Genome getGenome() {
        return genome;
    }

    public boolean isDead() {
        return dayOfDeath != -1;
    }

    public void killOff(int currentDay) {
        this.dayOfDeath = currentDay;
    }

    @Override
    public int compareTo(@NonNull Animal o) {
        return Comparator.comparing(Animal::getEnergy).thenComparing(Animal::getAge).thenComparing(Animal::getChildrenCount).compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return energy == animal.energy && dayOfDeath == animal.dayOfDeath && age == animal.age && birthDay == animal.birthDay && childCount == animal.childCount && orientation == animal.orientation && Objects.equals(position, animal.position) && Objects.equals(genome, animal.genome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orientation, position, energy, genome, dayOfDeath, age, birthDay, childCount);
    }

    public int getCountEatenPlants() {
        return countEatenPlants;
    }
    public int getDescendantsCount(){
        return descendants.size();
    }
    public int getDayOfDeath(){
        return dayOfDeath;
    }
    public int getAge() {
        return this.age;
    }

    public int getAgeForDead() {
        return dayOfDeath-birthDay;
    }


}