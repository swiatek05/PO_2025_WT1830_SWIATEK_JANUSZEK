package simulation.configs;

import java.util.List;

public record GenomeConfig(int genomeLength, int minMutateCount, int maxMutateCount, List<Integer> immunityGenes) {
}
