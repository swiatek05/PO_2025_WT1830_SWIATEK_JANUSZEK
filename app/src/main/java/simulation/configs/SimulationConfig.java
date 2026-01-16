package simulation.configs;

import simulation.SimulationVariant;

public record SimulationConfig(AnimalConfig animalConfig, GenomeConfig genomeConfig, PlantConfig plantConfig, MapConfig mapConfig, SimulationVariant simulationVariant, int startingPlantAmount, int startingAnimalAmount, int plantCountToGrowEveryday) {
}