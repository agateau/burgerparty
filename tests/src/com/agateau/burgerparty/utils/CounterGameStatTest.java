package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;

import org.junit.Test;

import com.agateau.burgerparty.model.TestUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class CounterGameStatTest {
    private HashSet<Object> mHandlers = new HashSet<Object>();

    @Test
    public void testLoad() {
        String xml = "<gamestat id='foo' value='12'/>";
        XmlReader.Element element = TestUtils.parseXml(xml);
        CounterGameStat stat = new CounterGameStat();
        stat.load(element);
        assertEquals(12, stat.getValue());
    }

    @Test
    public void testSave() throws IOException {
        CounterGameStat stat = new CounterGameStat();
        assertEquals(0, stat.getValue());
        stat.increase();
        assertEquals(1, stat.getValue());

        StringWriter writer = new StringWriter();
        {
            XmlWriter xmlWriter = new XmlWriter(writer);
            XmlWriter root = xmlWriter.element("gamestat");
            stat.save(root);
            root.pop();
            xmlWriter.close();
        }

        XmlReader.Element root = TestUtils.parseXml(writer.toString());
        assertEquals(root.getIntAttribute("value"), 1);
    }

    @Test
    public void testIncrease() {
        CounterGameStat stat = new CounterGameStat();

        TestUtils.SignalSpy0 spy = new TestUtils.SignalSpy0();
        stat.changed.connect(mHandlers, spy);
        assertEquals(0, stat.getValue());
        stat.increase();
        assertEquals(1, stat.getValue());
        assertEquals(1, spy.count);
    }

}
