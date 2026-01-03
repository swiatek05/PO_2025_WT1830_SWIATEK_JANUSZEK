package model.animal;

import model.MapDirection;
import model.Vector2d;
import model.WorldElement;
import model.map.GlobeWorldMap;
import model.plant.AbstractPlant;
import model.util.MoveValidator;
import simulation.configs.AnimalConfig;

public class Animal implements WorldElement {
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private final Genome genome;
    private static AnimalConfig animalConfig;
    private int dayOfDeath = -1;
    private int birthDay;
    private int childCount=0;

    public static void setAnimalConfig(AnimalConfig config) {
        animalConfig = config;
    }

    // initial animals created at the start of simulation
    public Animal(Vector2d position,int currentDay) {
        this.position = position;
        this.energy = animalConfig.startEnergy();
        this.genome = new Genome();
        this.orientation = MapDirection.getRandomDirection();
        this.birthDay = currentDay;
    }

    // create animal by mating
    public Animal(Vector2d position, Genome genome,int currentDay) {
        this.position = position;
        this.energy = animalConfig.energyUsedToMate() * 2;
        this.genome = genome;
        this.orientation = MapDirection.getRandomDirection();
        this.birthDay = currentDay;
    }
    public int getAge(int currentDay) {
        if (isDead()){
            return dayOfDeath-birthDay;
        }
        return currentDay-birthDay;
    }

    // mating behaviour
    public Animal mate(Animal partner,int currentDay) {
        Genome childGenome = new Genome(this, partner);
        this.decreaseEnergyMating();
        partner.decreaseEnergyMating();
        this.childCount++;
        partner.childCount++;

        return new Animal(this.position,childGenome,currentDay);
    }

    public void move(GlobeWorldMap map) {
        Vector2d unitVector= this.orientation.toUnitVector();
        Vector2d wantedPosition = this.position.add(unitVector);
        this.position=calculateNewPosition(unitVector,this.position,wantedPosition,map);
        this.decreaseEnergyEndOfDay();
    }

    private Vector2d calculateNewPosition(Vector2d unitVector,Vector2d oldPosition,Vector2d wantedPosition,GlobeWorldMap map) {
        int height=map.getMapHeight();
        int width=map.getMapWidth();
        int wantedX=wantedPosition.getX();
        int wantedY=wantedPosition.getY();
        Vector2d finalPosition = wantedPosition;
        finalPosition= new Vector2d((wantedX+width)%width,wantedY); //to zawsze dziala , wiec nie trzeba rozpatrywac przypadkow
        if (wantedY<0 || wantedY>=height){
            this.orientation=this.getOrientation().reverse();
            finalPosition=new Vector2d(finalPosition.getX(),oldPosition.getY());
        }
        return finalPosition;
    }
    public void rotate() {
        this.orientation = this.orientation.rotate(this.genome.getActiveGene());
    }

    public void eat(AbstractPlant plant) {
        this.energy += plant.getConsumptionEnergy();
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
    public int getChildrenCount(){
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

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }
}