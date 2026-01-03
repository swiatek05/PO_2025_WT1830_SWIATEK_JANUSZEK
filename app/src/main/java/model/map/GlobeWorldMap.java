package model.map;

import model.MapChangeListener;
import model.Vector2d;
import model.WorldElement;
import model.animal.Animal;
import model.plant.AbstractPlant;
import model.plant.Grass;
import model.util.Boundary;

import java.util.*;

public class GlobeWorldMap implements WorldMap {
    private final int MapWidth;
    private final int MapHeight;

    private final Map<Vector2d, List<Animal>> animals= new HashMap<>();
    private final Map<Vector2d, AbstractPlant> plants=new HashMap<>();
    private final Set<Vector2d> FreePlaceForPlantsJungle=new HashSet<>();
    private final Set<Vector2d> FreePlaceForPlantsSteppe=new HashSet<>();

    private final int jungleMinY; //zakladadam ze włącznie
    private final int jungleMaxY;


    public GlobeWorldMap(int MapWidth,int MapHeight) {
        this.MapWidth=MapWidth;
        this.MapHeight=MapHeight;
        //ustawiam dzungle
        int jungleHeight=Math.max(1,(int) (MapHeight*0.2));

        this.jungleMinY=(MapHeight-jungleHeight)/2;
        this.jungleMaxY=jungleMinY+jungleHeight-1; //liczymy od 0
        //ustawiam wolne miejsca dla dzungli
        for(int x=0;x<MapWidth;x++){
            for(int y=0;y<MapHeight;y++){
                if(y>=jungleMinY && y<=jungleMaxY) {
                    FreePlaceForPlantsJungle.add(new Vector2d(x, y));
                }
                else{
                    FreePlaceForPlantsSteppe.add(new Vector2d(x, y));
                }

            }
        }
    }

    public int getMapWidth() {
        return MapWidth;
    }
    public int getMapHeight() {
        return MapHeight;
    }


    @Override
    public void place(Animal animal) {
        addAnimalToMap(animal.getPosition(),animal);
    }

    @Override
    public void move(Animal animal) {
        Vector2d oldPosition=animal.getPosition();
        removeAnimalfromMap(oldPosition,animal);
        animal.rotate();
        animal.move(this);
        addAnimalToMap(animal.getPosition(),animal);

    }

    private void removeAnimalfromMap(Vector2d oldPosition, Animal animal) {
        List <Animal> animalList=this.animals.get(oldPosition);
        if (animalList!=null){
            animalList.remove(animal);
            if (animalList.isEmpty()){
                this.animals.remove(oldPosition);
            }
        }

    }

    @Override
    public List<Animal> animalsAt(Vector2d position) {
        List<Animal> animalsOnPosition=animals.get(position);
        if (animalsOnPosition==null){
            return List.of();
        }
        else{
            return animalsOnPosition;
        }
    }

    @Override
    public AbstractPlant plantAt(Vector2d position) {
        return this.plants.get(position);  //mozliwy null czyli brak rosliny
    }

    @Override
    public boolean isOccupied(Vector2d position) {
        if (plantAt(position)!=null){
            return true;
        }
        List<Animal> animals=animalsAt(position);
        return animals!=null && !animals.isEmpty();
    }

    @Override
    public WorldElement objectAt(Vector2d position,int currentDay) {
        List<Animal> animals=animalsAt(position);
        if (animals!=null && !animals.isEmpty()){
            return findStrongestAnimal(animals,currentDay);
        }
        if(plantAt(position)!=null){
            return plantAt(position);
        }
        return null;

    }

    private Animal findStrongestAnimal(List<Animal> animals,int currentDay) { //mozna sie zastanowic jak to zmienic bo troche brzydkie a petle sie powtarszaja prawie ze
        List<Animal> strongestAnimals=new ArrayList<>();
        int maxEnergy=-1;
        for (Animal animal:animals){ //szukam max
            if (animal.getEnergy()>maxEnergy){
                maxEnergy=animal.getEnergy();
            }
        }
        for (Animal animal:animals){ //szukam zwierzat z max
            if (animal.getEnergy()==maxEnergy){
                strongestAnimals.add(animal);
            }
        }
        if  (strongestAnimals.size()==1){return strongestAnimals.get(0);} //sprawa rozszczygnieta


        int maxAge=-1;
        for  (Animal animal:strongestAnimals){
            if (animal.getAge(currentDay)>maxAge){
                maxAge=animal.getAge(currentDay);
            }
        }
        List<Animal> tmp= new ArrayList<>();
        for (Animal animal:strongestAnimals){
            if (animal.getAge(currentDay)==maxAge){
                tmp.add(animal);
            }
        }
        strongestAnimals=tmp;
        if (strongestAnimals.size()==1){return strongestAnimals.get(0);}


        int maxChildCount=-1;
        for  (Animal animal:strongestAnimals){
            if (animal.getChildrenCount()>maxChildCount){
                maxChildCount=animal.getChildrenCount();
            }
        }
        tmp= new ArrayList<>();
        for (Animal animal:strongestAnimals){
            if (animal.getChildrenCount()==maxChildCount){
                tmp.add(animal);
            }
        }
        strongestAnimals=tmp;
        if (strongestAnimals.size()==1){
            return strongestAnimals.get(0);
        }
        else{
            int randomIdx=(int)(Math.random()*strongestAnimals.size());
            return strongestAnimals.get(randomIdx);
        }
    }

    @Override
    public void removeDeadAnimals(int currentDay) {
        Iterator<Map.Entry<Vector2d, List<Animal>>> it = animals.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<Vector2d,List<Animal>> entry=it.next();
            List<Animal> animalsAtPosition=entry.getValue();

            Iterator <Animal> listIterator=animalsAtPosition.iterator();
            while (listIterator.hasNext())
            {
                Animal animal=listIterator.next();
                if (animal.getEnergy()<=0){
                    animal.killOff(currentDay);
                    listIterator.remove();
                }
            }
            if (animalsAtPosition.isEmpty()){
                it.remove();
            }
        }
    }

    @Override
    public void spawnPlants(int countPlants) {
        for(int i=0;i<countPlants;i++){
            double randomNumber=Math.random();
            Vector2d position=null;
            if (randomNumber<=0.8){
                position=findFreePositionInJungle();
                if (position==null){
                    position=findFreePositionInSteppe();

                }
            }
            else{
                position=findFreePositionInSteppe();
                if (position==null){
                    position=findFreePositionInJungle();
                }
            }
            if (position!=null){
                plants.put(position,new Grass(position));
                FreePlaceForPlantsJungle.remove(position);
                FreePlaceForPlantsSteppe.remove(position);

            }
            else{
                break;
            }
        }

    }

    private Vector2d findFreePositionInSteppe() {
        if(!FreePlaceForPlantsSteppe.isEmpty()) {
            Random random=new Random();
            int idx=random.nextInt(FreePlaceForPlantsSteppe.size());
            int i=0;
            for (Vector2d position : FreePlaceForPlantsSteppe) {
                if (i==idx){
                    return position;
                }
                i+=1;
            }
        }
        return null;
    }

    private Vector2d findFreePositionInJungle() {
        if(!FreePlaceForPlantsJungle.isEmpty()) {
            Random random=new Random();
            int idx=random.nextInt(FreePlaceForPlantsJungle.size());
            int i=0;
            for (Vector2d position : FreePlaceForPlantsJungle) {
                if (i==idx){
                    return position;
                }
                i+=1;
            }
        }
        return null;
    }

    @Override
    public Boundary getCurrentBounds() {
        return null;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public void addObserver(MapChangeListener observer) {

    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return false;
    }
    private void addAnimalToMap(Vector2d position,Animal animal) {
        if (!animals.containsKey(position)){
            animals.put(position,new ArrayList<>());
        }
        animals.get(position).add(animal);
    }
}