package model.animal;

import simulation.configs.GenomeConfig;

import java.util.*;
import java.util.stream.IntStream;

public class Genome {
    private List<Integer> genes = new ArrayList<>();
    private final float immunityPercentage;
    private final GenomeConfig genomeConfig;
    private int currentGeneIndex;

    // initial animal genome generation, genes are chosen at random
    public Genome(GenomeConfig genomeConfig) {
        this.genomeConfig = genomeConfig;
        Random rn = new Random();
        int genomeLength = genomeConfig.genomeLength();

        for(int i = 0; i < genomeLength; i++) {
            this.genes.add(rn.nextInt(8));
        }
        this.currentGeneIndex = rn.nextInt(genomeLength);
        this.immunityPercentage = calculateImmunity(this.genes);
    }

    // only used for testing
    public Genome(List<Integer> genes,GenomeConfig genomeConfig) {
        this.genomeConfig = genomeConfig;
        this.genes = new ArrayList<>(genes); //
        this.currentGeneIndex = 0;
        this.immunityPercentage = calculateImmunity(this.genes);
    }

    // genome created when two animals mate, genes are a split between parents
    public Genome(Animal parent1, Animal parent2,GenomeConfig genomeConfig) {
        this.genomeConfig = genomeConfig;
        Random rn = new Random();
        int genomeLength = genomeConfig.genomeLength();
        List<Integer> genes;

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

        this.genes = mutate(new ArrayList<>(genes)); //now we can mutate
        this.currentGeneIndex = rn.nextInt(0,genomeLength);
        this.immunityPercentage = calculateImmunity(this.genes);
    }

    // used to mutate a list of genes
    public List<Integer> mutate(List<Integer> genes) {
        int minMutateCount = genomeConfig.minMutateCount();
        int maxMutateCount = genomeConfig.maxMutateCount();
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

    public float calculateImmunity(List<Integer> genes) {
        List<Integer> immunityGenes = genomeConfig.immunityGenes();
        int matching = 0;

        for(int i = 0; i < genomeConfig.genomeLength(); i++) {
            if (Objects.equals(immunityGenes.get(i), genes.get(i))) {
                matching += 1;
            }
        }

        return (float) matching / genomeConfig.genomeLength();
    }

    public List<Integer> getGenes() {
        return genes;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }

    public void moveCurrentGeneIndex() {
        this.currentGeneIndex = (currentGeneIndex + 1) % genes.size();
    }

    public int getActiveGene() {
        return genes.get(currentGeneIndex);
    }

    public float getImmunityPercentage() {
        return immunityPercentage;
    }
}