package model.animal;

import model.MapDirection;
import model.Vector2d;
import org.junit.jupiter.api.Test;
import simulation.configs.AnimalConfig;
import simulation.configs.GenomeConfig;

import java.util.Collections;
import java.util.List;

import static java.util.Collections.max;
import static org.junit.jupiter.api.Assertions.*;

public class AnimalTest {

    @Test
    void testCompareToEnergyMax() {
        // given
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        Vector2d position = new Vector2d(1,1);
        Genome genome = new Genome();
        Animal animal1 = new Animal(position,genome, MapDirection.NORTH,1,1,0,0);
        Animal animal2 = new Animal(position,genome,MapDirection.NORTH,2,1,0,0);
        Animal animal3 = new Animal(position,genome,MapDirection.NORTH,3,1,0,0);

        Animal expectedAnimal = new Animal(position,genome,MapDirection.NORTH,3,1,0,0);

        List<Animal> toLookThrough = List.of(animal1,animal3,animal2);

        // when
        Animal foundAnimal = max(toLookThrough);

        // then
        assertEquals(expectedAnimal,foundAnimal);
    }

    @Test
    void testCompareToAgeMax() {
        // given
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        Vector2d position = new Vector2d(1,1);
        Genome genome = new Genome();
        Animal animal1 = new Animal(position,genome,MapDirection.NORTH,1,1,0,1);
        Animal animal2 = new Animal(position,genome,MapDirection.NORTH,1,1,0,2);
        Animal animal3 = new Animal(position,genome,MapDirection.NORTH,1,1,0,3);

        Animal expectedAnimal = new Animal(position,genome,MapDirection.NORTH,1,1,0,3);

        List<Animal> toLookThrough = List.of(animal1,animal3,animal2);

        // when
        Animal foundAnimal = max(toLookThrough);

        // then
        assertEquals(expectedAnimal,foundAnimal);
    }

    @Test
    void testCompareToChildCountMax() {
        // given
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        Vector2d position = new Vector2d(1,1);
        Genome genome = new Genome();
        Animal animal1 = new Animal(position,genome,MapDirection.NORTH,1,1,1,1);
        Animal animal2 = new Animal(position,genome,MapDirection.NORTH,1,1,2,1);
        Animal animal3 = new Animal(position,genome,MapDirection.NORTH,1,1,3,1);

        Animal expectedAnimal = new Animal(position,genome,MapDirection.NORTH,1,1,3,1);

        List<Animal> toLookThrough = List.of(animal1,animal3,animal2);

        // when
        Animal foundAnimal = max(toLookThrough);

        // then
        assertEquals(expectedAnimal,foundAnimal);
    }

    @Test
    void testCompareToEnergySort() {
        // given
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        Vector2d position = new Vector2d(1,1);
        Genome genome = new Genome();
        Animal animal1 = new Animal(position,genome,MapDirection.NORTH,1,1,0,0);
        Animal animal2 = new Animal(position,genome,MapDirection.NORTH,2,1,0,0);
        Animal animal3 = new Animal(position,genome,MapDirection.NORTH,3,1,0,0);

        List<Animal> expectedSorted = List.of(animal1,animal2,animal3);

        List<Animal> toSort = new java.util.ArrayList<>(List.of(animal1, animal3, animal2));

        // when

        Collections.sort(toSort);

        // then
        assertEquals(expectedSorted,toSort);
    }

    @Test
    void testCompareToAgeSort() {
        // given
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        Vector2d position = new Vector2d(1,1);
        Genome genome = new Genome();
        Animal animal1 = new Animal(position,genome,MapDirection.NORTH,1,1,0,1);
        Animal animal2 = new Animal(position,genome,MapDirection.NORTH,1,1,0,2);
        Animal animal3 = new Animal(position,genome,MapDirection.NORTH,1,1,0,3);

        List<Animal> expectedSorted = List.of(animal1,animal2,animal3);

        List<Animal> toSort = new java.util.ArrayList<>(List.of(animal1, animal3, animal2));

        // when

        Collections.sort(toSort);

        // then
        assertEquals(expectedSorted,toSort);
    }

    @Test
    void testCompareToChildCountSort() {
        // given
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        Animal.setAnimalConfig(defaultAnimalConfig);
        Genome.setConfig(defaultGenomeConfig);
        Vector2d position = new Vector2d(1,1);
        Genome genome = new Genome();
        Animal animal1 = new Animal(position,genome,MapDirection.NORTH,1,1,1,1);
        Animal animal2 = new Animal(position,genome,MapDirection.NORTH,1,1,2,1);
        Animal animal3 = new Animal(position,genome,MapDirection.NORTH,1,1,3,1);

        List<Animal> expectedSorted = List.of(animal1,animal2,animal3);

        List<Animal> toSort = new java.util.ArrayList<>(List.of(animal1, animal3, animal2));

        // when

        Collections.sort(toSort);

        // then
        assertEquals(expectedSorted,toSort);
    }
}
