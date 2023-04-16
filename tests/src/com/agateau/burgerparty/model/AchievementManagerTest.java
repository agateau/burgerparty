package com.agateau.burgerparty.model;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;

import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class AchievementManagerTest {

    @Test
    public void testLoad() {
        XmlReader.Element root = TestUtils.parseXml(
            "<achievements>"
            + "  <achievement id='foo' unlocked='false'/>"
            + "  <achievement id='bar' unlocked='true' seen='false'/>"
            + "  <achievement id='bing' unlocked='true' seen='true'/>"
            + "</achievements>"
        );
        AchievementManager manager = new AchievementManager();
        Achievement foo = createAchievement("foo");
        Achievement bar = createAchievement("bar");
        Achievement baz = createAchievement("baz");
        Achievement bing = createAchievement("bing");
        manager.add(foo);
        manager.add(bar);
        manager.add(baz);
        manager.add(bing);
        manager.load(root);
        assertFalse(foo.isUnlocked());

        assertTrue(bar.isUnlocked());
        assertFalse(bar.hasBeenSeen());

        assertTrue(bing.isUnlocked());
        assertTrue(bing.hasBeenSeen());

        assertFalse(baz.isUnlocked());
    }

    @Test
    public void testSave() {
        class MyAchievementManager extends AchievementManager {
            @Override
            public void save() {
                mSaveCalled = true;
                XmlWriter xmlWriter = new XmlWriter(mWriter);
                save(xmlWriter);
            }
            public StringWriter mWriter = new StringWriter();
            public boolean mSaveCalled = false;
        };

        MyAchievementManager manager = new MyAchievementManager();
        Achievement foo = createAchievement("foo");
        Achievement bar = createAchievement("bar");
        Achievement baz = createAchievement("baz");
        manager.add(foo);
        manager.add(bar);
        manager.add(baz);
        foo.unlock();
        bar.unlock();
        bar.markSeen();

        assertTrue(manager.mSaveCalled);

        XmlReader.Element root = TestUtils.parseXml(manager.mWriter.toString());
        assertEquals(2, root.getChildCount());

        XmlReader.Element child = root.getChild(0);
        assertEquals("foo", child.getAttribute("id"));
        assertTrue(child.getBooleanAttribute("unlocked"));
        assertFalse(child.getBooleanAttribute("seen"));

        child = root.getChild(1);
        assertEquals("bar", child.getAttribute("id"));
        assertTrue(child.getBooleanAttribute("unlocked"));
        assertTrue(child.getBooleanAttribute("seen"));
    }

    private static Achievement createAchievement(String id) {
        return new Achievement(id, id + " title", "Description of " + id);
    }
}
