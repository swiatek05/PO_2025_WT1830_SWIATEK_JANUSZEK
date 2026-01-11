package model.plant;

import model.Vector2d;
import simulation.configs.PlantConfig;

public class Grass extends AbstractPlant {
    public Grass(Vector2d position,PlantConfig plantConfig) {
        super(position,plantConfig);
    }
    @Override
    public String toString(){
        return "*";
    }
}
