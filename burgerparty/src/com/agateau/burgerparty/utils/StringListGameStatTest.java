package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;

import org.junit.Test;

import com.agateau.burgerparty.model.TestUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class StringListGameStatTest {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    @Test
    public void testLoad() {
        String xml = "<gamestat id='foo'><item>One</item><item>Two</item></gamestat>";
        XmlReader.Element element = TestUtils.parseXml(xml);
        StringListGameStat stat = new StringListGameStat("foo");
        stat.load(element);
        assertEquals(2, stat.getCount());
    }

    @Test
    public void testSave() throws IOException {
        StringListGameStat stat = new StringListGameStat("foo");
        stat.add("Hello");
        stat.add("Good Bye");
        assertEquals(2, stat.getCount());

        StringWriter writer = new StringWriter();
        {
            XmlWriter xmlWriter = new XmlWriter(writer);
            XmlWriter root = xmlWriter.element("gamestat");
            stat.save(root);
            root.pop();
            xmlWriter.close();
        }

        XmlReader.Element root = TestUtils.parseXml(writer.toString());
        Array<XmlReader.Element> items = root.getChildrenByName("item");
        assertEquals(2, items.size);
        assertEquals("Hello", items.get(0).getText());
        assertEquals("Good Bye", items.get(1).getText());
    }

    @Test
    public void testAdd() {
        StringListGameStat stat = new StringListGameStat("foo");

        TestUtils.SignalSpy0 spy = new TestUtils.SignalSpy0();
        stat.changed.connect(mHandlers, spy);
        stat.add("Hello");
        assertEquals(1, spy.count);
    }

}
