package model.animal;

import model.MapDirection;
import model.Vector2d;

public class Animal {
    private MapDirection orientation;
    private Vector2d position;
    private int energy;
    private Genome genome;

    public MapDirection getOrientation() {
        return orientation;
    }

    public Vector2d getPosition() {
        return position;
    }

    public int getEnergy() {
        return energy;
    }

    public Genome getGenome() {
        return genome;
    }
}