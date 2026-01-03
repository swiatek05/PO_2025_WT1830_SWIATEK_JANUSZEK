package model.map;

import model.MapChangeListener;
import model.Vector2d;
import model.animal.Animal;
import model.plant.Plant;
import model.WorldElement;
import model.util.Boundary;
import model.util.MoveValidator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorldMap extends MoveValidator {
    void place(Animal animal); //umieszcza zwierze na mapie

    void move(Animal animal); //przesuwa zwierze zgodnie z jego logika (czyli geny i orientacja)

    List<Animal> animalsAt(Vector2d position); //zwraca liste zwierzat na danej pozycji

    Plant plantAt(Vector2d position); //zwraca roslin ejesli istnieje

    boolean isOccupied(Vector2d position); //zprawdza czy na polu cos jest (roslina albo zwierze)

    WorldElement objectAt(Vector2d position,int currentDay); //zwraca jakis obiekt na danej pozycji

    void removeDeadAnimals(int currentDay); //usuwa martwe zwierzeta z mapy

    void spawnPlants(int countPlants); //wyrastanie roslin

    Boundary getCurrentBounds(); //zwraca granice mapy dla wyswietlania

    int getId(); //id (pisalo zeby wiele okien zrobic)

    void addObserver(MapChangeListener observer); //rejestracja obserwatora , zeby GUI wiedziało ze trzeba odswiezyc
}