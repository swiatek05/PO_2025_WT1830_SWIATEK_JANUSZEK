package model.animal;

import model.MapDirection;
import model.Vector2d;
import model.WorldElement;
import simulation.configs.AnimalConfig;

public class Animal implements WorldElement {
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private final Genome genome;
    private static AnimalConfig animalConfig;
    private int dayOfDeath = -1;

    public static void setAnimalConfig(AnimalConfig config) {
        animalConfig = config;
    }

    // initial animals created at the start of simulation
    public Animal(Vector2d position) {
        this.position = position;
        this.energy = animalConfig.startEnergy();
        this.genome = new Genome();
        this.orientation = MapDirection.getRandomDirection();
    }

    // create animal by mating
    public Animal(Vector2d position, Genome genome){
        this.position = position;
        this.energy = animalConfig.energyUsedToMate() * 2;
        this.genome = genome;
        this.orientation = MapDirection.getRandomDirection();
    }

    // mating behaviour
    public Animal mate(Animal partner) {
        Genome childGenome = new Genome(this, partner);
        this.decreaseEnergyMating();
        partner.decreaseEnergyMating();

        return new Animal(this.position,childGenome);
    }

    public void decreaseEnergyMating() {
        this.energy -= animalConfig.energyUsedToMate();
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
        return position.toString();
    }

    public int getEnergy() {
        return energy;
    }

    public Genome getGenome() {
        return genome;
    }

    public boolean isDead() {
        return dayOfDeath != -1;
    }
}