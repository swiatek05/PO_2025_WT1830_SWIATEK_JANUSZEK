package model;

import java.util.List;
import java.util.Random;

public enum MapDirection {
    NORTH,
    NORTHEAST,
    EAST,
    SOUTHEAST,
    SOUTH,
    SOUTHWEST,
    WEST,
    NORTHWEST;

//    public MapDirection next(){
//        return switch (this){
//            case NORTH -> EAST;
//            case EAST -> SOUTH;
//            case SOUTH -> WEST;
//            case WEST -> NORTH;
//        };
//    }
//
//    public MapDirection previous(){
//        return switch (this){
//            case NORTH -> WEST;
//            case WEST -> SOUTH;
//            case SOUTH -> EAST;
//            case EAST -> NORTH;
//        };
//    }

    public Vector2d toUnitVector(){
        return switch (this) {
            case NORTH -> new Vector2d(0, 1);
            case NORTHEAST -> new Vector2d(1,1);
            case SOUTH -> new Vector2d(0, -1);
            case SOUTHEAST -> new Vector2d(1,-1);
            case WEST -> new Vector2d(-1, 0);
            case SOUTHWEST -> new Vector2d(-1,-1);
            case EAST -> new Vector2d(1, 0);
            case NORTHWEST -> new Vector2d(-1,1);
        };
    }

    public MapDirection reverse(){
        return switch (this) {
            case NORTH -> SOUTH;
            case NORTHEAST -> SOUTHWEST;
            case SOUTH -> NORTH;
            case SOUTHEAST -> NORTHWEST;
            case WEST -> EAST;
            case SOUTHWEST -> NORTHEAST;
            case EAST -> WEST;
            case NORTHWEST -> SOUTHEAST;
        };
    }

    public static MapDirection getRandomDirection(){
        Random rn = new Random();

        List<MapDirection> toChoose = List.of(MapDirection.values());

        return toChoose.get(rn.nextInt(toChoose.size()));
    }
}
