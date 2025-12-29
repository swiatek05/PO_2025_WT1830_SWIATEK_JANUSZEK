package model.animal;

import java.util.*;
import java.util.stream.IntStream;

public class Genome {
    protected List<Integer> genes = new ArrayList<>();
    protected int currentGeneIndex;

    public Genome(int genomeLength) {
        Random rn = new Random();
        for(int i = 0; i < genomeLength; i++) {
            genes.add(rn.nextInt(8));
        }
        currentGeneIndex = rn.nextInt(genomeLength);
    }

    public Genome(Animal parent1, Animal parent2, int genomeLength) {
        Random rn = new Random();
        if (parent1.getEnergy() > parent2.getEnergy()) {
            Animal stronger = parent1;
            Animal weaker = parent2;
        } else {
            Animal stronger = parent2;
            Animal weaker = parent1;
        }

        int divisionPoint = genomeLength;

        if (rn.nextBoolean()) {

        } else {

        }
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
