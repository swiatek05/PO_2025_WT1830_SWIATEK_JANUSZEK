package simulation;

import model.animal.Animal;

import java.util.*;

public class SimulationStatistics {
    private int numberOfAnimals;
    private int numberOfPlants;
    private int freeFields;
    private List<Integer> mostPopularGenom;
    private double averageEnergy;
    private double averageAgeForDead;
    private double averageChildrenCount;

    public void update(List<Animal> aliveAnimals,int plantsCount,int  freeFieldsCount, long sumAgeOfDead, int totalDeadAnimals) {
        this.numberOfAnimals = aliveAnimals.size();
        this.numberOfPlants = plantsCount;
        this.freeFields = freeFieldsCount;
        this.averageAgeForDead= totalDeadAnimals == 0 ? 0 : (double) sumAgeOfDead / totalDeadAnimals;

        if (aliveAnimals.isEmpty()) {
            this.averageEnergy = 0;
            this.averageChildrenCount = 0;
            this.mostPopularGenom = null;
            return;

        }

        Map<List<Integer>,Integer> genotypeCounts=new HashMap<>();
        long sumEnergy=0;
        long sumChildren=0;
        for(Animal animal : aliveAnimals){
            List<Integer> genes=animal.getGenome().getGenes();
            int count=0;
            if (genotypeCounts.get(genes)!=null){
                count=genotypeCounts.get(genes);
            }
            genotypeCounts.put(genes,count+1);

            sumEnergy+=animal.getEnergy();
            sumChildren+=animal.getChildrenCount();

        }
        this.mostPopularGenom=findMostPopularGenom(genotypeCounts);
        this.averageEnergy=(double) sumEnergy/numberOfAnimals;
        this.averageChildrenCount=(double) sumChildren/numberOfAnimals;





    }
    private List<Integer> findMostPopularGenom(Map<List<Integer>,Integer> genotypeCounts){
        List<Integer> mostPopularGenom =null;
        int max=-1;
        for (Map.Entry<List<Integer>,Integer> entry : genotypeCounts.entrySet()){
            if (entry.getValue()>max){
                max=entry.getValue();
                mostPopularGenom=entry.getKey();
            }
        }
        return mostPopularGenom;
    }

    public int getNumberOfAnimals() {
        return numberOfAnimals;
    }
    public int getNumberOfPlants() {
        return numberOfPlants;
    }
    public int getFreeFields() {
        return freeFields;
    }
    public List<Integer> getMostPopularGenom() {
        return mostPopularGenom;
    }
    public double getAverageEnergy() {
        return averageEnergy;
    }
    public double getAverageAgeForDead() {
        return averageAgeForDead;
    }
    public double getAverageChildrenCount() {
        return averageChildrenCount;
    }


}
