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

    // initial animal genome generation, genes are chosen at random
    public Genome() {
        Random rn = new Random();
        int genomeLength = genomeConfig.genomeLength();

        for(int i = 0; i < genomeLength; i++) {
            genes.add(rn.nextInt(8));
        }
        currentGeneIndex = rn.nextInt(genomeLength);
    }

    // genome created when two animals mate, genes are a split between parents
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

        this.genes = mutate(genes);
        this.currentGeneIndex = rn.nextInt(0,genomeLength);
    }

    // used to mutate a list of genes
    public List<Integer> mutate(List<Integer> genes) {
        int minMutateCount = genomeConfig.minMutateCount();
        int maxMutateCount = genomeConfig.minMutateCount();
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

    public void moveCurrentGeneIndex() {
        this.currentGeneIndex = (currentGeneIndex + 1) % 8;
    }

    public int getActiveGene() {
        return genes.get(currentGeneIndex);
    }

}