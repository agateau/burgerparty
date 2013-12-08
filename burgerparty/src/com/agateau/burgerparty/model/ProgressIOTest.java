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
			+ "<item world='1' level='1' score='12'/>"
			+ "<item world='2' level='2' score='24'/>"
			+ "</progress>"
			);
		ProgressIO progressIO = new ProgressIO(worlds);
		progressIO.load(root);
		assertEquals(worlds.get(0).getLevel(0).score, 12);
		assertEquals(worlds.get(0).getLevel(1).score, Level.SCORE_LOCKED);
		assertEquals(worlds.get(1).getLevel(0).score, Level.SCORE_LOCKED);
		assertEquals(worlds.get(1).getLevel(1).score, 24);
	}

	@Test
	public void testLoadV2() {
		Array<LevelWorld> worlds = createTestWorlds();
		XmlReader.Element root = TestUtils.parseXml(
			  "<progress version='2'>"
			+ "    <levels>"
			+ "        <level world='1' level='1' score='12'/>"
			+ "        <level world='2' level='2' score='24'/>"
			+ "    </levels>"
			+ "</progress>"
			);
		ProgressIO progressIO = new ProgressIO(worlds);
		progressIO.load(root);
		assertEquals(12, worlds.get(0).getLevel(0).score);
		assertEquals(Level.SCORE_NEW, worlds.get(0).getLevel(1).score);
		assertEquals(Level.SCORE_LOCKED, worlds.get(1).getLevel(0).score);
		assertEquals(24, worlds.get(1).getLevel(1).score);
	}

	@Test
	public void testSave() {
		Array<LevelWorld> worlds = createTestWorlds();
		worlds.get(0).getLevel(0).score = 12;
		worlds.get(1).getLevel(1).score = Level.SCORE_NEW;

		StringWriter writer = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(writer);
		ProgressIO progressIO = new ProgressIO(worlds);
		progressIO.save(xmlWriter);

		XmlReader.Element root = TestUtils.parseXml(writer.toString());
		assertEquals(root.getIntAttribute("version"), 2);
		assertEquals(root.getChildCount(), 1);

		XmlReader.Element levelsElement = root.getChildByName("levels");
		assertNotNull(levelsElement);

		XmlReader.Element child = levelsElement.getChild(0);
		assertEquals(child.getIntAttribute("world"), 1);
		assertEquals(child.getIntAttribute("level"), 1);
		assertEquals(child.getIntAttribute("score"), 12);

		child = levelsElement.getChild(1);
		assertEquals(child.getIntAttribute("world"), 2);
		assertEquals(child.getIntAttribute("level"), 2);
		assertEquals(child.getIntAttribute("score"), Level.SCORE_NEW);
	}

	private static Array<LevelWorld> createTestWorlds() {
		Array<LevelWorld> worlds = new Array<LevelWorld>();

		LevelWorld world1 = new LevelWorld(1, "1");
		world1.addLevel(new Level(world1, "1-1"));
		world1.addLevel(new Level(world1, "1-2"));

		LevelWorld world2 = new LevelWorld(2, "2");
		world2.addLevel(new Level(world2, "2-1"));
		world2.addLevel(new Level(world2, "2-2"));

		worlds.add(world1);
		worlds.add(world2);

		return worlds;
	}
}
