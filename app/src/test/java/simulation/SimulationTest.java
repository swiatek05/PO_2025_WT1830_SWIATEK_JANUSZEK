package simulation;

import model.MapDirection;
import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import model.plant.Grass;
import org.junit.jupiter.api.Test;
import simulation.configs.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    @Test
    void testRemoveDeadAnimals() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10, 10, SimulationVariant.STANDARD);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10, 0, 0, 0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4, 0, 0, List.of(1, 1, 1, 1));
        PlantConfig defaultPlantConfig = new PlantConfig(1, 1, 0.5);
        SimulationConfig defaultSimulationConfig = new SimulationConfig(defaultAnimalConfig,defaultGenomeConfig,defaultPlantConfig,defaultMapConfig,SimulationVariant.STANDARD,0,0,0);


        GlobeWorldMap map = new GlobeWorldMap(defaultMapConfig,defaultPlantConfig);

        Simulation simulation = new Simulation(map,defaultSimulationConfig,null);
        Genome genome = new Genome(defaultGenomeConfig);
        Vector2d position1 = new Vector2d(1,1);
        Vector2d position2 = new Vector2d(2,2);
        Vector2d position3 = new Vector2d(3,3);
        Animal animal1 = new Animal(position1,genome, MapDirection.NORTH,-1,1,0,0,defaultAnimalConfig,defaultGenomeConfig);
        Animal animal2 = new Animal(position2,genome, MapDirection.NORTH,-1,1,0,0,defaultAnimalConfig,defaultGenomeConfig);
        Animal animal3 = new Animal(position3,genome, MapDirection.NORTH,-1,1,0,0,defaultAnimalConfig,defaultGenomeConfig);

        map.place(animal1);
        map.place(animal2);
        map.place(animal3);

        simulation.addAnimalToAlive(animal1);
        simulation.addAnimalToAlive(animal2);
        simulation.addAnimalToAlive(animal3);

        // when
        simulation.killStarvedAnimals();
        simulation.removeDeadAnimals();

        // then
        assertEquals(List.of(), map.animalsAt(position1));
        assertEquals(List.of(), map.animalsAt(position2));
        assertEquals(List.of(), map.animalsAt(position3));
    }

    @Test
    void testEatPlants() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10, 10, SimulationVariant.STANDARD);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10, 0, 0, 0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4, 0, 0, List.of(1, 1, 1, 1));
        PlantConfig defaultPlantConfig = new PlantConfig(1, 1, 0.5);
        SimulationConfig defaultSimulationConfig = new SimulationConfig(defaultAnimalConfig,defaultGenomeConfig,defaultPlantConfig,defaultMapConfig,SimulationVariant.STANDARD,0,0,0);


        GlobeWorldMap map = new GlobeWorldMap(defaultMapConfig,defaultPlantConfig);

        Simulation simulation = new Simulation(map,defaultSimulationConfig,null);
        Genome genome = new Genome(defaultGenomeConfig);
        Vector2d position1 = new Vector2d(1,1);
        Vector2d position2 = new Vector2d(2,2);
        Vector2d position3 = new Vector2d(3,3);
        Animal animal1 = new Animal(position1,genome, MapDirection.NORTH,1,1,0,0,defaultAnimalConfig,defaultGenomeConfig);
        Animal animal2 = new Animal(position2,genome, MapDirection.NORTH,1,1,0,0,defaultAnimalConfig,defaultGenomeConfig);
        Animal animal3 = new Animal(position3,genome, MapDirection.NORTH,1,1,0,0,defaultAnimalConfig,defaultGenomeConfig);
        Grass grass1 = new Grass(position1,defaultPlantConfig);
        Grass grass2 = new Grass(position2,defaultPlantConfig);
        Grass grass3 = new Grass(position3,defaultPlantConfig);

        map.place(animal1);
        map.place(animal2);
        map.place(animal3);

        map.addPlant(grass1);
        map.addPlant(grass2);
        map.addPlant(grass3);

        simulation.addAnimalToAlive(animal1);
        simulation.addAnimalToAlive(animal2);
        simulation.addAnimalToAlive(animal3);

        // when
        simulation.eatPlants();

        // then

        assertNull(map.plantAt(position1));
        assertNull(map.plantAt(position2));
        assertNull(map.plantAt(position3));

        assertEquals(2,animal1.getEnergy());
        assertEquals(2,animal2.getEnergy());
        assertEquals(2,animal3.getEnergy());
    }
}
