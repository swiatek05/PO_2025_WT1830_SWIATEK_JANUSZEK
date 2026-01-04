package simulation;

import model.MapDirection;
import model.Vector2d;
import model.animal.Animal;
import model.animal.Genome;
import model.map.GlobeWorldMap;
import org.junit.jupiter.api.Test;
import simulation.configs.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationTest {

    @Test
    void testRemoveDeadAnimals() {
        // given
        MapConfig defaultMapConfig = new MapConfig(10,10);
        AnimalConfig defaultAnimalConfig = new AnimalConfig(10,0,0,0);
        GenomeConfig defaultGenomeConfig = new GenomeConfig(4,0,0, List.of(1,1,1,1));
        PlantConfig defaultPlantConfig = new PlantConfig(1,1);
        SimulationConfig defaultSimulationConfig = new SimulationConfig(defaultAnimalConfig,defaultGenomeConfig,defaultPlantConfig,defaultMapConfig,SimulationVariant.STANDARD,0,0,0);
        Simulation.setConfig(defaultSimulationConfig);

        GlobeWorldMap map = new GlobeWorldMap();
        Simulation simulation = new Simulation(map);
        Genome genome = new Genome();
        Vector2d position1 = new Vector2d(1,1);
        Vector2d position2 = new Vector2d(2,2);
        Vector2d position3 = new Vector2d(3,3);
        Animal animal1 = new Animal(position1,genome, MapDirection.NORTH,-1,1,0,0);
        Animal animal2 = new Animal(position2,genome, MapDirection.NORTH,-1,1,0,0);
        Animal animal3 = new Animal(position3,genome, MapDirection.NORTH,-1,1,0,0);

        map.place(animal1);
        map.place(animal2);
        map.place(animal3);

        simulation.addAnimalToAlive(animal1);
        simulation.addAnimalToAlive(animal2);
        simulation.addAnimalToAlive(animal3);

        // when
        simulation.removeDeadAnimals();

        // then
        assertEquals(List.of(), map.animalsAt(position1));
        assertEquals(List.of(), map.animalsAt(position2));
        assertEquals(List.of(), map.animalsAt(position3));
    }
}
