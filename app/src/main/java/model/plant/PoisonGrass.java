package model.plant;

import model.Vector2d;
import model.util.Boundary;
import model.util.MapException;
import simulation.configs.MapConfig;
import simulation.configs.PlantConfig;
import model.map.GlobeWorldMap;

import java.util.Map;

public class PoisonGrass extends AbstractPlant {
    private double getLevelPoison(Vector2d position,Map<Vector2d,Integer> countOfDeathOnPosition,MapConfig mapConfig,double averageMortalitySteppe,double averageMortalityJungle,boolean isJungle) {
        int sumOfDeadsInRegion=0;
        int width=mapConfig.MapWidth();
        int height=mapConfig.MapHeight();
        int sizeRegion=9;
        if(position.getY()==0 || position.getY()==height-1){
            sizeRegion=6;
        }
        Vector2d [] neighbourVector={new Vector2d(0,1),new Vector2d(0,-1),new Vector2d(1,0),new Vector2d(-1,0),
                new Vector2d(1,1),new Vector2d(1,-1),new Vector2d(-1,1),new Vector2d(-1,-1),};
        for (Vector2d neighbour : neighbourVector) {
            Vector2d wantedNeighbourPosition=position.add(neighbour);
            int wantedNeighbourX=wantedNeighbourPosition.getX();
            int neighbourY=wantedNeighbourPosition.getY();

            Vector2d neighbourPosition=new Vector2d((wantedNeighbourX+width)%width,neighbourY);
            if(countOfDeathOnPosition.containsKey(neighbourPosition)){
                sumOfDeadsInRegion+=countOfDeathOnPosition.get(neighbourPosition);
            }
        }
        if(countOfDeathOnPosition.containsKey(position)){
            sumOfDeadsInRegion+=countOfDeathOnPosition.get(position);
        }
        double averageDeadsInRegion=(double) sumOfDeadsInRegion/sizeRegion;

        double globalAverage = isJungle ? averageMortalityJungle : averageMortalitySteppe;
        if (globalAverage==0) return 0.0;
        return Math.min(1.0, averageDeadsInRegion/globalAverage);
    }
    public PoisonGrass(Vector2d position, PlantConfig plantConfig, Map<Vector2d,Integer> countOfDeathOnPosition,MapConfig mapConfig,double averageMortalitySteppe,double averageMortalityJungle,boolean isJungle) {
        super(position,plantConfig);
        final double levelPoison=getLevelPoison(position,countOfDeathOnPosition,mapConfig,averageMortalitySteppe,averageMortalityJungle,isJungle);
        int damage=(int) Math.floor(plantConfig.plantPoisonConsumptionDamage()*levelPoison);
        if (damage==0) damage=-1;
        this.consumptionEnergy =damage;
    }
    @Override
    public String toString(){
        return "x";
    }
}
