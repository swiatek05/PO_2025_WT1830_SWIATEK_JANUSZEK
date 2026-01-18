package model.plant;

import model.Vector2d;
import simulation.configs.PlantConfig;

public class PoisonGrass extends AbstractPlant {

    public PoisonGrass(Vector2d position,PlantConfig plantConfig) {
        super(position,plantConfig);
        final double levelPoison=Math.random();
        int damage=(int) (plantConfig.plantPoisonConsumptionDamage()*levelPoison);
        if (damage==0) damage=-1;
        this.consumptionEnergy =damage;
    }
    @Override
    public String toString(){
        return "x";
    }
}
