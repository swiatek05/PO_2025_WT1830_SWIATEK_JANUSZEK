package model.animal;

import simulation.configs.GenomeConfig;

import java.util.*;
import java.util.stream.IntStream;

public class Genome {
    protected List<Integer> genes = new ArrayList<>();
    private static GenomeConfig genomeConfig;
    protected int currentGeneIndex;

    static void setConfig(GenomeConfig config) {
        genomeConfig = config;
    }

    public Genome() {
        Random rn = new Random();
        int genomeLength = genomeConfig.genomeLength();

        for(int i = 0; i < genomeLength; i++) {
            genes.add(rn.nextInt(8));
        }
        currentGeneIndex = rn.nextInt(genomeLength);
    }

    public Genome(Animal parent1, Animal parent2) {
        Random rn = new Random();
        int genomeLength = genomeConfig.genomeLength();
        List<Integer> genes = new ArrayList<>();

        Animal stronger = parent1.getEnergy() > parent2.getEnergy() ? parent1 : parent2;
        Animal weaker = parent1.getEnergy() > parent2.getEnergy() ? parent2 : parent1;

        int divisionPoint = Math.round((float) (genomeLength * weaker.getEnergy()) / (stronger.getEnergy() + weaker.getEnergy()));

        // right stronger
        if (rn.nextBoolean()) {
            genes = IntStream.range(0,genomeLength)
                    .map(i -> i < divisionPoint ? weaker.getGenome().getGenes().get(i) : stronger.getGenome().getGenes().get(i))
                    .boxed()
                    .toList();
        // left stronger
        } else {
            genes = IntStream.range(0,genomeLength)
                    .map(i -> i < (genomeLength - divisionPoint) ? stronger.getGenome().getGenes().get(i) : weaker.getGenome().getGenes().get(i))
                    .boxed()
                    .toList();
        }

        this.genes = mutate(genes,genomeConfig.minMutateCount(),genomeConfig.minMutateCount());
    }

    public List<Integer> mutate(List<Integer> genes, int minMutateCount, int maxMutateCount) {
        Random rn = new Random();

        int toMutateCount = rn.nextInt(minMutateCount,maxMutateCount+1);
        List<Integer> toMutate = new ArrayList<>(IntStream.range(0, genes.size())
                .boxed()
                .toList());

        Collections.shuffle(toMutate);

        for (int i = 0; i < toMutateCount; i++) {
            int index = toMutate.get(i);
            genes.set(index, rn.nextInt(8));
        }

        return genes;
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }

}
