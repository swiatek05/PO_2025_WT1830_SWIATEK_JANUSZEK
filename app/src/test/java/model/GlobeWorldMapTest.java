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
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,10,10,10);
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
}
