package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class ProgressIOTest {

    @Test
    public void testLoadV1() {
        Array<LevelWorld> worlds = createTestWorlds();
        XmlReader.Element root = TestUtils.parseXml(
                                     "<progress>"
                                     + "<item world='1' level='1' score='10000'/>"
                                     + "<item world='2' level='2' score='20000'/>"
                                     + "</progress>"
                                 );
        ProgressIO progressIO = new ProgressIO(worlds);
        progressIO.load(root);
        assertEquals(10000, worlds.get(0).getLevel(0).getScore());
        assertEquals(1, worlds.get(0).getLevel(0).getStarCount());

        assertTrue(worlds.get(0).getLevel(1).isLocked());
        assertTrue(worlds.get(1).getLevel(0).isLocked());

        assertEquals(20000, worlds.get(1).getLevel(1).getScore());
        assertEquals(2, worlds.get(1).getLevel(1).getStarCount());
    }

    @Test
    public void testLoadV2() {
        Array<LevelWorld> worlds = createTestWorlds();
        XmlReader.Element root = TestUtils.parseXml(
                                     "<progress version='2'>"
                                     + "    <levels>"
                                     + "        <level world='1' level='1' score='10000'/>"
                                     + "        <level world='2' level='2' score='20000'/>"
                                     + "    </levels>"
                                     + "</progress>"
                                 );
        ProgressIO progressIO = new ProgressIO(worlds);
        progressIO.load(root);
        assertEquals(10000, worlds.get(0).getLevel(0).getScore());
        assertEquals(1, worlds.get(0).getLevel(0).getStarCount());

        assertTrue(worlds.get(0).getLevel(1).isNew());
        assertTrue(worlds.get(1).getLevel(0).isLocked());

        assertEquals(20000, worlds.get(1).getLevel(1).getScore());
        assertEquals(2, worlds.get(1).getLevel(1).getStarCount());
    }

    @Test
    public void testLoadV3() {
        Array<LevelWorld> worlds = createTestWorlds();
        XmlReader.Element root = TestUtils.parseXml(
                                     "<progress version='3'>"
                                     + "    <levels>"
                                     + "        <level world='1' level='1' score='12' stars='2'/>"
                                     + "        <level world='2' level='1' score='24' stars='4'/>"
                                     + "        <level world='2' level='2' score='24' stars='3'/>"
                                     + "    </levels>"
                                     + "</progress>"
                                 );
        ProgressIO progressIO = new ProgressIO(worlds);
        progressIO.load(root);
        Level level;

        // 1-1
        level = worlds.get(0).getLevel(0);
        assertEquals(12, level.getScore());
        assertEquals(2, level.getStarCount());

        // 1-2
        level = worlds.get(0).getLevel(1);
        assertTrue(level.isNew());

        // 1-3
        level = worlds.get(0).getLevel(2);
        assertTrue(level.isLocked());

        // 2-1
        level = worlds.get(1).getLevel(0);
        assertEquals(24, level.getScore());
        assertEquals(3, level.getStarCount());
        assertTrue(level.isPerfect());

        // 2-2
        level = worlds.get(1).getLevel(1);
        assertEquals(24, level.getScore());
        assertEquals(3, level.getStarCount());
        assertFalse(level.isPerfect());
    }

    @Test
    public void testSave() {
        Level level;
        Array<LevelWorld> worlds = createTestWorlds();

        // 1-1
        level = worlds.get(0).getLevel(0);
        level.setScore(12);
        level.setStarCount(2);

        // 1-2
        level = worlds.get(0).getLevel(1);
        level.setScore(12);
        level.setStarCount(3);
        level.markPerfect();

        // 2-2
        level = worlds.get(1).getLevel(1);
        level.unlock();

        StringWriter writer = new StringWriter();
        XmlWriter xmlWriter = new XmlWriter(writer);
        ProgressIO progressIO = new ProgressIO(worlds);
        progressIO.save(xmlWriter);

        XmlReader.Element root = TestUtils.parseXml(writer.toString());
        assertEquals(root.getIntAttribute("version"), 3);
        assertEquals(root.getChildCount(), 1);

        XmlReader.Element levelsElement = root.getChildByName("levels");
        assertNotNull(levelsElement);

        XmlReader.Element child;
        // 1-1
        child = levelsElement.getChild(0);
        assertEquals(child.getIntAttribute("world"), 1);
        assertEquals(child.getIntAttribute("level"), 1);
        assertEquals(child.getIntAttribute("score"), 12);
        assertEquals(child.getIntAttribute("stars"), 2);

        // 1-2
        child = levelsElement.getChild(1);
        assertEquals(child.getIntAttribute("world"), 1);
        assertEquals(child.getIntAttribute("level"), 2);
        assertEquals(child.getIntAttribute("score"), 12);
        assertEquals(child.getIntAttribute("stars"), 4);

        // 2-1
        child = levelsElement.getChild(2);
        assertEquals(child.getIntAttribute("world"), 2);
        assertEquals(child.getIntAttribute("level"), 2);
        assertEquals(child.getIntAttribute("score"), ProgressIO.SCORE_NEW);
    }

    private static Array<LevelWorld> createTestWorlds() {
        Array<LevelWorld> worlds = new Array<LevelWorld>();

        LevelWorld world1 = new LevelWorld(1, "1");
        world1.addLevel(new Level(world1, "1-1"));
        world1.addLevel(new Level(world1, "1-2"));
        world1.addLevel(new Level(world1, "1-3"));

        LevelWorld world2 = new LevelWorld(2, "2");
        world2.addLevel(new Level(world2, "2-1"));
        world2.addLevel(new Level(world2, "2-2"));
        world2.addLevel(new Level(world2, "2-3"));

        worlds.add(world1);
        worlds.add(world2);

        return worlds;
    }
}
