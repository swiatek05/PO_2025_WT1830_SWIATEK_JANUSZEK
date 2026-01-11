package model.map;

import model.MapChangeListener;
import model.Vector2d;
import model.animal.Animal;
import model.plant.AbstractPlant;
import model.WorldElement;
import model.util.Boundary;

import java.util.List;

public interface WorldMap  {
    void place(Animal animal); //umieszcza zwierze na mapie

    void move(Animal animal); //przesuwa zwierze zgodnie z jego logika (czyli geny i orientacja)

    List<Animal> animalsAt(Vector2d position); //zwraca liste zwierzat na danej pozycji

    AbstractPlant plantAt(Vector2d position); //zwraca roslin ejesli istnieje

    boolean isOccupied(Vector2d position); //zprawdza czy na polu cos jest (roslina albo zwierze)

    WorldElement objectAt(Vector2d position,int currentDay); //zwraca jakis obiekt na danej pozycji

    void removeDeadAnimals(int currentDay); //usuwa martwe zwierzeta z mapy

    void spawnPlants(int countPlants); //wyrastanie roslin

    Boundary getCurrentBounds(); //zwraca granice mapy dla wyswietlania

    int getId(); //id (pisalo zeby wiele okien zrobic)

    void addObserver(MapChangeListener observer); //rejestracja obserwatora , zeby GUI wiedziało ze trzeba odswiezyc

    void eatPlants();

    List<Animal> reproduce(int currentDay);

    int getMapWidth();
    int getMapHeight();
}