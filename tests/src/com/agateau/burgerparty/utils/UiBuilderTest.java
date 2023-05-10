package com.agateau.burgerparty.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.agateau.burgerparty.model.TestUtils;
import com.badlogic.gdx.utils.XmlReader;

public class UiBuilderTest {
    @Test
    public void testSimple() {
        UiBuilder builder = new UiBuilder(null, null);
        XmlReader.Element rootElement = TestUtils.parseXml(
            "<gdxui><AnchorGroup id='root'></AnchorGroup></gdxui>"
        );
        builder.build(rootElement);
        AnchorGroup group = builder.getActor("root");
        assertNotNull(group);
    }

    @Test
    public void testInsideReference() {
        UiBuilder builder = new UiBuilder(null, null);
        XmlReader.Element rootElement = TestUtils.parseXml(
              "<gdxui>"
            + "  <AnchorGroup id='root'>"
            + "    <VerticalGroup bottomLeft='root.bottomLeft 1 1'/>"
            + "  </AnchorGroup>"
            + "</gdxui>"
        );
        builder.build(rootElement);
        AnchorGroup group = builder.getActor("root");
        assertNotNull(group);
    }
}
