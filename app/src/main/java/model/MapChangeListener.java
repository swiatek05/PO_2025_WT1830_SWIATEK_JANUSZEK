package model;

import model.map.WorldMap;

public interface MapChangeListener {
    void mapChanged(WorldMap worldMap, String message);
}
