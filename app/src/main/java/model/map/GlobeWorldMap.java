package model.map;

import model.MapChangeListener;
import model.Vector2d;
import model.WorldElement;
import model.animal.Animal;
import model.plant.AbstractPlant;
import model.plant.Grass;
import model.plant.PoisonGrass;
import model.util.Boundary;
import model.util.MapException;
import simulation.SimulationVariant;
import simulation.configs.MapConfig;
import simulation.configs.PlantConfig;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Collections.max;


public class GlobeWorldMap implements WorldMap {
    private final MapConfig mapConfig;
    private final PlantConfig plantConfig;
    private final int MapWidth;
    private final int MapHeight;
    private final SimulationVariant simulationVariant;
    private final int sizeJungle;
    private final int sizeSteppe;

    private final Map<Vector2d, List<Animal>> animals = new HashMap<>();
    private final Map<Vector2d, AbstractPlant> plants = new HashMap<>();
    private final Set<Vector2d> FreePlaceForPlantsJungle = new HashSet<>();
    private final Set<Vector2d> FreePlaceForPlantsSteppe = new HashSet<>();
    private final Map<Vector2d,Integer> countOfDeathOnPosition=new HashMap<>();
    private int countOfDeathJungle=0;
    private int countOfDeathSteppe=0;


    private final int jungleMinY; //zakladadam ze włącznie
    private final int jungleMaxY;

    private final List<MapChangeListener> observers = new ArrayList<>();
    private final int id;
    private static final AtomicInteger IdCounter = new AtomicInteger(0);


    public GlobeWorldMap(MapConfig mapConfig, PlantConfig plantConfig) {
        this.MapWidth = mapConfig.MapWidth();
        this.MapHeight = mapConfig.MapHeight();
        this.mapConfig = mapConfig;
        this.plantConfig = plantConfig;
        this.id = IdCounter.getAndIncrement();
        this.simulationVariant = mapConfig.simulationVariant();
        //ustawiam dzungle
        int jungleHeight = Math.max(1, (int) (MapHeight * 0.2));

        this.jungleMinY = (MapHeight - jungleHeight) / 2;
        this.jungleMaxY = jungleMinY + jungleHeight - 1; //liczymy od 0
        //ustawiam wolne miejsca dla dzungli
        for (int x = 0; x < MapWidth; x++) {
            for (int y = 0; y < MapHeight; y++) {
                if (y >= jungleMinY && y <= jungleMaxY) {
                    FreePlaceForPlantsJungle.add(new Vector2d(x, y));
                } else {
                    FreePlaceForPlantsSteppe.add(new Vector2d(x, y));
                }

            }
        }

        sizeJungle=(jungleHeight*MapWidth);
        sizeSteppe=(MapWidth*MapHeight)-sizeJungle;
    }

    @Override
    public int getMapWidth() {
        return MapWidth;
    }

    @Override
    public int getMapHeight() {
        return MapHeight;
    }


    @Override
    public synchronized void place(Animal animal) {
        addAnimalToMap(animal.getPosition(), animal);
    }

    @Override
    public synchronized  void move(Animal animal) {
        Vector2d oldPosition = animal.getPosition();
        removeAnimalfromMap(oldPosition, animal);
        animal.rotate();
        animal.move(this);
        addAnimalToMap(animal.getPosition(), animal);

    }

    // why is this used only once in the whole class? why are not using it in removeDeadAnimals?
    private void removeAnimalfromMap(Vector2d oldPosition, Animal animal) {
        List<Animal> animalList = this.animals.get(oldPosition);
        if (animalList != null) {
            animalList.remove(animal);
            if (animalList.isEmpty()) {
                this.animals.remove(oldPosition);
            }
        }

    }

    @Override
    public synchronized List<Animal> animalsAt(Vector2d position) {
        List<Animal> animalsOnPosition = animals.get(position);
        if (animalsOnPosition == null) {
            return List.of();
        } else {
            return animalsOnPosition;
        }
    }

    @Override
    public synchronized  AbstractPlant plantAt(Vector2d position) {
        return this.plants.get(position);  //mozliwy null czyli brak rosliny
    }

    @Override
    public synchronized  boolean isOccupied(Vector2d position) {
        if (plantAt(position) != null) {
            return true;
        }
        List<Animal> animals = animalsAt(position);
        return animals != null && !animals.isEmpty();
    }

    // i don't really see a use for this method (or idk I will use it for tests)
    @Override
    public synchronized  WorldElement objectAt(Vector2d position, int currentDay) {
        synchronized (animals) {
            List<Animal> animals = animalsAt(position);
            if (animals != null && !animals.isEmpty()) {
                return max(animals);
            }
        }

        if (plantAt(position) != null) {
            return plantAt(position);
        }
        return null;

    }

    // simpler implementation of this method was made as compareTo in Animal
//    public Animal findStrongestAnimal(List<Animal> animals,int currentDay) { //mozna sie zastanowic jak to zmienic bo troche brzydkie a petle sie powtarszaja prawie ze
//        List<Animal> strongestAnimals=new ArrayList<>();
//        int maxEnergy=-1;
//        for (Animal animal:animals){ //szukam max
//            if (animal.getEnergy()>maxEnergy){
//                maxEnergy=animal.getEnergy();
//            }
//        }
//        for (Animal animal:animals){ //szukam zwierzat z max
//            if (animal.getEnergy()==maxEnergy){
//                strongestAnimals.add(animal);
//            }
//        }
//        if  (strongestAnimals.size()==1){return strongestAnimals.get(0);} //sprawa rozszczygnieta
//
//
//        int maxAge=-1;
//        for  (Animal animal:strongestAnimals){
//            if (animal.getAge(currentDay)>maxAge){
//                maxAge=animal.getAge(currentDay);
//            }
//        }
//        List<Animal> tmp= new ArrayList<>();
//        for (Animal animal:strongestAnimals){
//            if (animal.getAge(currentDay)==maxAge){
//                tmp.add(animal);
//            }
//        }
//        strongestAnimals=tmp;
//        if (strongestAnimals.size()==1){return strongestAnimals.get(0);}
//
//
//        int maxChildCount=-1;
//        for  (Animal animal:strongestAnimals){
//            if (animal.getChildrenCount()>maxChildCount){
//                maxChildCount=animal.getChildrenCount();
//            }
//        }
//        tmp= new ArrayList<>();
//        for (Animal animal:strongestAnimals){
//            if (animal.getChildrenCount()==maxChildCount){
//                tmp.add(animal);
//            }
//        }
//        strongestAnimals=tmp;
//        if (strongestAnimals.size()==1){
//            return strongestAnimals.get(0);
//        }
//        else{
//            int randomIdx=(int)(Math.random()*strongestAnimals.size());
//            return strongestAnimals.get(randomIdx);
//        }
//    }
    private void addDeathToPosition(Vector2d position){
        if(countOfDeathOnPosition.containsKey(position)){
            int countOfDeath= countOfDeathOnPosition.get(position);
            countOfDeath++;
            countOfDeathOnPosition.remove(position);
            countOfDeathOnPosition.put(position,countOfDeath);
        }
        else{
            countOfDeathOnPosition.put(position,1);
        }
    }
    private void addDeathToCounter(Vector2d position){
        int y=position.getY();
        if(y<jungleMinY || y>jungleMaxY){
            countOfDeathSteppe+=1;
        }
        else{
            countOfDeathJungle+=1;
        }
    }


    @Override
    public synchronized void removeDeadAnimals(int currentDay) {
        Iterator<Map.Entry<Vector2d, List<Animal>>> it = animals.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector2d, List<Animal>> entry = it.next();
            List<Animal> animalsAtPosition = entry.getValue();

            Iterator<Animal> listIterator = animalsAtPosition.iterator();
            while (listIterator.hasNext()) {
                Animal animal = listIterator.next();
                if (animal.getEnergy() <= 0) {
                    animal.killOff(currentDay);
                    addDeathToPosition(animal.getPosition());
                    addDeathToCounter(animal.getPosition());
                    listIterator.remove();
                }
            }
            if (animalsAtPosition.isEmpty()) {
                it.remove();
            }
        }
    }
    private double getAverageMortalityJungle(){
        double avg= (double)countOfDeathJungle/sizeJungle;
        return avg;
    }
    private double getAverageMortalitySteppe(){
        double avg= (double)countOfDeathSteppe/sizeSteppe;
        return avg;
    }
    @Override
    public synchronized void spawnPlants(int countPlants) {
        for (int i = 0; i < countPlants; i++) {
            double jungleOrSteppe = Math.random();
            boolean isJungle=false;
            Vector2d position = null;
            if (jungleOrSteppe <= 0.8) {
                position = findFreePositionInJungle();
                isJungle=true;
                if (position == null) {
                    position = findFreePositionInSteppe();
                }
            } else {
                position = findFreePositionInSteppe();
                if (position == null) {
                    position = findFreePositionInJungle();
                    isJungle=true;
                }
            }
            if (position != null) {
                AbstractPlant plant;
                if (simulationVariant == SimulationVariant.STANDARD) {
                    plant = new Grass(position, plantConfig);
                } else {
                    double edibleOrPoison = Math.random();
                    if (edibleOrPoison <= plantConfig.poisonPlantChance())
                        plant = new PoisonGrass(position, plantConfig,countOfDeathOnPosition,mapConfig,getAverageMortalitySteppe(),getAverageMortalityJungle(),isJungle);
                    else
                        plant = new Grass(position, plantConfig);

                }
                addPlant(plant);

            } else {
                break;
            }
        }

    }

    private Vector2d findFreePositionInSteppe() {
        if (!FreePlaceForPlantsSteppe.isEmpty()) {
            Random random = new Random();
            int idx = random.nextInt(FreePlaceForPlantsSteppe.size());
            int i = 0;
            for (Vector2d position : FreePlaceForPlantsSteppe) {
                if (i == idx) {
                    return position;
                }
                i += 1;
            }
        }
        return null;
    }

    private Vector2d findFreePositionInJungle() {
        if (!FreePlaceForPlantsJungle.isEmpty()) {
            Random random = new Random();
            int idx = random.nextInt(FreePlaceForPlantsJungle.size());
            int i = 0;
            for (Vector2d position : FreePlaceForPlantsJungle) {
                if (i == idx) {
                    return position;
                }
                i += 1;
            }
        }
        return null;
    }

    public void removePlantFromMap(AbstractPlant plant) {
        Vector2d plantPos = plant.getPosition();

        if (this.plants.get(plantPos) != null) {
            this.plants.remove(plant.getPosition());
            if (plantPos.getY() >= jungleMinY && plantPos.getY() <= jungleMaxY) {
                FreePlaceForPlantsJungle.add(plantPos);
            } else {
                FreePlaceForPlantsSteppe.add(plantPos);
            }
        }
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(MapWidth - 1, MapHeight - 1));
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void addObserver(MapChangeListener observer) {
        observers.add(observer);

    }

    public void mapChanged(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

    // why is this seperate from place, if place just calls this method?
    private void addAnimalToMap(Vector2d position, Animal animal) {
        if (!animals.containsKey(position)) {
            animals.put(position, new ArrayList<>());
        }
        animals.get(position).add(animal);
    }

    public Map<Vector2d, AbstractPlant> getPlants() {
        return plants;
    }


    public void addPlant(AbstractPlant plant) {
        Vector2d position = plant.getPosition();
        Boundary boundary = getCurrentBounds();
        if (!(position.precedes(boundary.maxRightPosition()) && position.follows(boundary.minLeftPosition()))) {
            throw new MapException("Position " + position + " is outside the map boundaries.");
        }
        plants.put(position, plant);
        FreePlaceForPlantsJungle.remove(position);
        FreePlaceForPlantsSteppe.remove(position);
    }

    @Override
    public synchronized void eatPlants() {
        Set<Vector2d> plantPositions = new HashSet<>(plants.keySet());
        for (Vector2d position : plantPositions) {
            List<Animal> animalsOnPosition = animals.get(position);
            if (animalsOnPosition != null && !animalsOnPosition.isEmpty()) {
                Animal Strongest = Collections.max(animalsOnPosition);
                AbstractPlant plant = plants.get(position);
                Strongest.eat(plant);
                removePlantFromMap(plant);
            }

        }
    }

    @Override
    public synchronized List<Animal> reproduce(int currentDay) {
        Set<Vector2d> animalPositions = new HashSet<>(animals.keySet());
        List<Animal> children = new ArrayList<>();
        for (Vector2d position : animalPositions) {
            List<Animal> animalsOnPosition = animals.get(position);
            if (animalsOnPosition.size() < 2) continue;

            Animal Strongest = null;
            Animal SecondStrongest = null;
            for (Animal animal : animalsOnPosition) {
                if (Strongest == null || animal.compareTo(Strongest) > 0) {
                    SecondStrongest = Strongest;
                    Strongest = animal;

                } else if (SecondStrongest == null || animal.compareTo(SecondStrongest) > 0) {
                    SecondStrongest = animal;
                }
            }
            if (Strongest != null && SecondStrongest != null && Strongest.canMate() && SecondStrongest.canMate()) {
                Animal child = Strongest.mate(SecondStrongest, currentDay);
                this.place(child);
                children.add(child);


            }
        }
        return children;
    }
    public synchronized List<Integer> findMinMaxEnergy(){
        if (animals.isEmpty()){
            return List.of(0,0);
        }
        int minEnergy=Integer.MAX_VALUE;
        int maxEnergy=Integer.MIN_VALUE;
        for(List<Animal> animalList : animals.values()){
            for(Animal animal: animalList){
                if(minEnergy>animal.getEnergy()){
                    minEnergy = animal.getEnergy();
                }
                if(maxEnergy<animal.getEnergy()){
                    maxEnergy = animal.getEnergy();
                }
            }
        }
        return List.of(minEnergy,maxEnergy);


    }

    public int getPlantsCount() {
        return plants.size();
    }

    public int getFreeFields() {
        return FreePlaceForPlantsSteppe.size() + FreePlaceForPlantsJungle.size();
    }
}