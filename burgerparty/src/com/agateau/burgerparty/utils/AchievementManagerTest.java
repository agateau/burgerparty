package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import java.io.StringWriter;

import org.junit.Test;

import com.agateau.burgerparty.model.TestUtils;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlWriter;

public class AchievementManagerTest {

    @Test
    public void testLoad() {
        XmlReader.Element root = TestUtils.parseXml(
            "<achievements>"
            + "  <achievement id='foo' unlocked='false'/>"
            + "  <achievement id='bar' unlocked='true'/>"
            + "</achievements>"
        );
        AchievementManager manager = new AchievementManager();
        Achievement foo = createAchievement("foo");
        Achievement bar = createAchievement("bar");
        Achievement baz = createAchievement("baz");
        manager.add(foo);
        manager.add(bar);
        manager.add(baz);
        manager.load(root);
        assertFalse(foo.isUnlocked());
        assertTrue(bar.isUnlocked());
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
        manager.add(foo);
        manager.add(bar);
        foo.setUnlocked(true);

        assertTrue(manager.mSaveCalled);

        XmlReader.Element root = TestUtils.parseXml(manager.mWriter.toString());
        assertEquals(1, root.getChildCount());

        XmlReader.Element child = root.getChild(0);
        assertEquals("foo", child.getAttribute("id"));
        assertTrue(child.getBooleanAttribute("unlocked"));
    }

    private static Achievement createAchievement(String id) {
        return new Achievement(id, id + " title", "Description of " + id);
    }
}
