package model.plant;

import model.Vector2d;
import model.WorldElement;
import simulation.configs.PlantConfig;

public abstract class AbstractPlant implements WorldElement {
    private final Vector2d position;
    protected static PlantConfig plantConfig;
    protected int consumptionEnergy;

    public static void setPlantConfig(PlantConfig config) {
        plantConfig = config;
    }

    public AbstractPlant(Vector2d position) {
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
}
