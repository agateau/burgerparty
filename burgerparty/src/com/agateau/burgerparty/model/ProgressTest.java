package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class ProgressTest {

	@Test
	public void testLoad() {
		Array<LevelWorld> worlds = createTestWorlds();
		XmlReader.Element root = parseXml(
			"<progress>"
			+ "<item world='1' level='1' score='12'/>"
			+ "<item world='2' level='2' score='24'/>"
			+ "</progress>"
			);
		Progress.load(root, worlds);
		assertEquals(worlds.get(0).getLevel(0).score, 12);
		assertEquals(worlds.get(0).getLevel(1).score, -1);
		assertEquals(worlds.get(1).getLevel(0).score, -1);
		assertEquals(worlds.get(1).getLevel(1).score, 24);
	}

	@Test
	public void testSave() {
		Array<LevelWorld> worlds = createTestWorlds();
		worlds.get(0).getLevel(0).score = 12;
		worlds.get(1).getLevel(1).score = 24;

		StringWriter writer = new StringWriter();
		XmlWriter xmlWriter = new XmlWriter(writer);
		Progress.save(xmlWriter, worlds);

		XmlReader.Element root = parseXml(writer.toString());
		assertEquals(root.getChildCount(), 2);

		XmlReader.Element item = root.getChild(0);
		assertEquals(item.getAttribute("world"), "1");
		assertEquals(item.getAttribute("level"), "1");
		assertEquals(item.getAttribute("score"), "12");

		item = root.getChild(1);
		assertEquals(item.getAttribute("world"), "2");
		assertEquals(item.getAttribute("level"), "2");
		assertEquals(item.getAttribute("score"), "24");
	}

	private static Array<LevelWorld> createTestWorlds() {
		Array<LevelWorld> worlds = new Array<LevelWorld>();

		LevelWorld world1 = new LevelWorld("1");
		world1.addLevel(new Level(world1, "1-1"));
		world1.addLevel(new Level(world1, "1-2"));

		LevelWorld world2 = new LevelWorld("2");
		world2.addLevel(new Level(world2, "2-1"));
		world2.addLevel(new Level(world2, "2-2"));

		worlds.add(world1);
		worlds.add(world2);

		return worlds;
	}

	private static XmlReader.Element parseXml(String xml) {
		XmlReader reader = new XmlReader();
		return reader.parse(xml);
	}
}
