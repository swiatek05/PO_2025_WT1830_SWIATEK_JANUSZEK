package model.animal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    }

    public List<Integer> getGenes() {
        return genes;
    }

    public int getCurrentGeneIndex() {
        return currentGeneIndex;
    }

}
