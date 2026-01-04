package model;

import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import org.junit.jupiter.api.Test;
import simulation.configs.AnimalConfig;
import simulation.configs.GenomeConfig;
import simulation.configs.MapConfig;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GlobeWorldMapTest {

    @Test
    void testCorrectPlace() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        GlobeWorldMap.setMapConfig(defaultMapConfig);
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        GlobeWorldMap map = new GlobeWorldMap();
        Vector2d position = new Vector2d(1,1);
        Animal animal = new Animal(position,0);

        // when
        map.place(animal);

        // then
        assertFalse(map.animalsAt(position).isEmpty());
    }

    @Test
    void testRemoveDeadAnimalsFromMap() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        GlobeWorldMap.setMapConfig(defaultMapConfig);
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        GlobeWorldMap map = new GlobeWorldMap();
        Vector2d position1 = new Vector2d(1,1);
        Animal deadAnimal1 = new Animal(position1, new Genome(List.of(0,1,1,3)),MapDirection.NORTH,-1,0);
        Vector2d position2 = new Vector2d(2,2);
        Animal deadAnimal2 = new Animal(position2, new Genome(List.of(0,1,1,3)),MapDirection.NORTH,-1,0);
        map.place(deadAnimal1);
        map.place(deadAnimal2);

        // when
        map.removeDeadAnimals(0);

        // then
        assertTrue(map.animalsAt(position1).isEmpty());
        assertTrue(map.animalsAt(position2).isEmpty());
    }

    @Test
    void testCorrectSingleMoveWithinBounds() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        GlobeWorldMap.setMapConfig(defaultMapConfig);
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        GlobeWorldMap map = new GlobeWorldMap();
        Vector2d position = new Vector2d(1,1);
        Animal animal = new Animal(position, new Genome(List.of(0,0,0,0)),MapDirection.NORTH,10,0);
        Vector2d expectedPosition = new Vector2d(1,2);

        // when
        map.move(animal);

        // then
        assertTrue(map.animalsAt(position).isEmpty());
        assertFalse(map.animalsAt(expectedPosition).isEmpty());

    }

    @Test
    void testCorrectMultipleMoveWithingBounds() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        GlobeWorldMap.setMapConfig(defaultMapConfig);
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        GlobeWorldMap map = new GlobeWorldMap();
        Vector2d position = new Vector2d(1,1);
        Animal animal = new Animal(position, new Genome(List.of(0,1,1,3)),MapDirection.NORTH,10,0);
        Vector2d expectedPosition = new Vector2d(2,2);

        // when
        map.move(animal);
        map.move(animal);
        map.move(animal);
        map.move(animal);

        // then
        assertTrue(map.animalsAt(position).isEmpty());
        assertFalse(map.animalsAt(expectedPosition).isEmpty());
    }

    @Test
    void testWrapAroundMap() {
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,10,10,10);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        GlobeWorldMap.setMapConfig(defaultMapConfig);
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        GlobeWorldMap map = new GlobeWorldMap();
        Vector2d position = new Vector2d(9,1);
        Animal animal = new Animal(position, new Genome(List.of(0,0,0,0)),MapDirection.EAST,10,0);
        Vector2d expectedPosition = new Vector2d(0,1);

        // when
        map.move(animal);

        // then
        assertTrue(map.animalsAt(position).isEmpty());
        assertFalse(map.animalsAt(expectedPosition).isEmpty());
    }

    @Test
    void testReverseAtEdge() {
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,10,10,10);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        GlobeWorldMap.setMapConfig(defaultMapConfig);
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        GlobeWorldMap map = new GlobeWorldMap();
        Vector2d position = new Vector2d(1,9);
        Animal animal = new Animal(position, new Genome(List.of(0,0,0,0)),MapDirection.NORTH,10,0);
        MapDirection expectedOrientation = MapDirection.SOUTH;

        // when
        map.move(animal);

        // then
        assertEquals(expectedOrientation, animal.getOrientation());
    }
}
