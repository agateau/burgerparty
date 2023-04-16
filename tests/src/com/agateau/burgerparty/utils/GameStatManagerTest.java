package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

import com.agateau.burgerparty.model.TestUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class GameStatManagerTest {
    @Test
    public void testLoad() {
        String xml = "<gamestats>"
                + " <gamestat id='lst'><item>One</item><item>Two</item></gamestat>"
                + " <gamestat id='num' value='12'/>"
                + "</gamestats>";
        XmlReader.Element element = TestUtils.parseXml(xml);

        StringListGameStat lst = new StringListGameStat();
        CounterGameStat num = new CounterGameStat();

        GameStatManager manager = new GameStatManager();
        manager.add("lst", lst);
        manager.add("num", num);
        manager.load(element);

        assertEquals(2, lst.getCount());
        assertEquals(12, num.getValue());
    }

    @Test
    public void testSave() throws IOException {
        StringListGameStat lst = new StringListGameStat();
        lst.add("Hello");
        lst.add("Good Bye");

        CounterGameStat num = new CounterGameStat();
        num.increase();

        GameStatManager manager = new GameStatManager();
        manager.add("lst", lst);
        manager.add("num", num);

        StringWriter writer = new StringWriter();
        {
            XmlWriter xmlWriter = new XmlWriter(writer);
            manager.save(xmlWriter);
        }

        XmlReader.Element root = TestUtils.parseXml(writer.toString());

        Array<XmlReader.Element> gameStatElements = root.getChildrenByName("gamestat");
        assertEquals(2, gameStatElements.size);

        XmlReader.Element gameStatElement = TestUtils.findChildElementById(root, "lst");
        assertNotNull(gameStatElement);
        Array<XmlReader.Element> items = gameStatElement.getChildrenByName("item");
        assertEquals("Hello", items.get(0).getText());
        assertEquals("Good Bye", items.get(1).getText());

        gameStatElement = TestUtils.findChildElementById(root, "num");
        assertNotNull(gameStatElement);
        assertEquals("num", gameStatElement.getAttribute("id"));
        assertEquals(1, gameStatElement.getIntAttribute("value"));
    }
}
