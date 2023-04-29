package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class UiUtilsTest {

    @Test
    public void testCoordinates_plain() {
        Group root = new Group();
        Group child = new Group();
        Actor subChild = new Actor();

        root.addActor(child);
        child.addActor(subChild);

        child.setPosition(10, 4);

        subChild.setPosition(2, 1);

        Vector2 subChildCoord = new Vector2(1, 1);

        Vector2 rootCoord = UiUtils.toAscendantCoordinates(root, subChild, subChildCoord);
        assertEquals(13, rootCoord.x, 0);
        assertEquals(6, rootCoord.y, 0);

        Vector2 subChildCoord2 = UiUtils.toChildCoordinates(root, subChild, rootCoord);
        assertEquals(subChildCoord, subChildCoord2);
    }

    @Test
    public void testToAscendantCoordinates_scaled() {
        Vector2 coord;
        Group root = new Group();
        Group child = new Group();

        root.addActor(child);

        child.setPosition(4, 3);
        child.setScale(2, 3);

        /*  (root)
         * 11 |  (child)
         * 10 |    | (subChild)
         *  9 |  2 | 1 |
         *  8 |    |   |
         *  7 |    |   |
         *  6 |  1 | 0 x------------
         *  5 |    |   0 1 2 3 4 5 6
         *  4 |    |
         *  3 |  0 x----------------
         *  2 |    0 1 2 3 4 5 6 7 8
         *  1 |
         *  0 x---------------------
         *     0 2 4 6 8 1 1 1 1 1 2
         *               0 2 4 6 8 0
         */
        coord = UiUtils.toAscendantCoordinates(root, child, new Vector2(2, 1));
        assertEquals(8, coord.x, 0);
        assertEquals(6, coord.y, 0);

        Actor subChild = new Actor();
        child.addActor(subChild);
        subChild.setPosition(2, 1);

        coord = UiUtils.toAscendantCoordinates(child, subChild, new Vector2(1, 1));
        assertEquals(3, coord.x, 0);
        assertEquals(2, coord.y, 0);

        coord = UiUtils.toAscendantCoordinates(root, subChild, new Vector2(1, 1));
        assertEquals(10, coord.x, 0);
        assertEquals(9, coord.y, 0);
    }
}
