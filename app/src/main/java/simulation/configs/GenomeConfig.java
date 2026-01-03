package simulation.configs;

import model.animal.Genome;

public record GenomeConfig(int genomeLength, int minMutateCount, int maxMutateCount, Genome immunityGenome) {
}
