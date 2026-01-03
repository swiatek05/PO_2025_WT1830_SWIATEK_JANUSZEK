package model.plant;

import model.Vector2d;

public class PoisonGrass extends AbstractPlant {

    public PoisonGrass(Vector2d position) {
        super(position);
        this.consumptionEnergy = plantConfig.plantPoisonConsumptionDamage();
    }
}
