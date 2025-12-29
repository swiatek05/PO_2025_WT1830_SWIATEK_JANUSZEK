package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MapDirectionTest {

    @Test
    void testRotate() {
        assertEquals(MapDirection.NORTHEAST,MapDirection.NORTH.rotate(1));
        assertEquals(MapDirection.EAST,MapDirection.NORTH.rotate(2));
        assertEquals(MapDirection.SOUTH,MapDirection.NORTH.rotate(4));

        assertEquals(MapDirection.SOUTHWEST,MapDirection.SOUTH.rotate(1));
        assertEquals(MapDirection.NORTH,MapDirection.SOUTH.rotate(4));
        assertEquals(MapDirection.EAST,MapDirection.SOUTH.rotate(6));
    }
}

