package model.plant;

import model.Vector2d;
import model.WorldElement;
import simulation.configs.PlantConfig;

public abstract class AbstractPlant implements WorldElement {
    private final Vector2d position;
    protected int consumptionEnergy;



    public AbstractPlant(Vector2d position,PlantConfig plantConfig) {
        this.position = position;
        this.consumptionEnergy = plantConfig.plantConsumptionEnergy();
    }

    @Override
    public Vector2d getPosition() {
        return position;
    }

    public int getConsumptionEnergy() {
        return consumptionEnergy;
    }
    @Override
    public String toString(){
        return position.toString();
    }
}
