package model.animal;

import model.MapDirection;
import model.Vector2d;
import model.WorldElement;
import model.util.MoveValidator;
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

    public void move(MoveValidator validator) {
        Vector2d newPosition = this.position.add(this.orientation.toUnitVector());
        if (validator.canMoveTo(newPosition)) {
            this.position = newPosition;
        } else {
            // made it to the edge, waiting for map implementation
        }
    }

    public void rotate() {
        this.orientation = this.orientation.rotate(this.genome.getActiveGene());
    }

    public void eat() {
        this.energy += animalConfig.plantConsumptionEnergy();
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

    public void killOff(int currentDay) {
        this.dayOfDeath = currentDay;
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }
}