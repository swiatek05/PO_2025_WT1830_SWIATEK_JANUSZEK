package model.map;

import model.MapChangeListener;
import model.Vector2d;
import model.animal.Animal;
import model.plant.AbstractPlant;
import model.WorldElement;
import model.util.Boundary;

import java.util.List;

public interface WorldMap  {
    void place(Animal animal);

    void move(Animal animal);

    List<Animal> animalsAt(Vector2d position);

    AbstractPlant plantAt(Vector2d position);

    boolean isOccupied(Vector2d position);

    WorldElement objectAt(Vector2d position,int currentDay);

    void removeDeadAnimals(int currentDay);

    void spawnPlants(int countPlants);

    Boundary getCurrentBounds();

    int getId();

    void addObserver(MapChangeListener observer);

    void eatPlants();

    List<Animal> reproduce(int currentDay);

    int getMapWidth();
    int getMapHeight();
}