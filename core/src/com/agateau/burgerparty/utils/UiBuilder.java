package com.agateau.burgerparty.utils;

import java.util.HashMap;
import java.util.Map;

import com.agateau.burgerparty.utils.AnchorGroup.Rule;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;

public class UiBuilder {
    private Map<String, Actor> mActorForId = new HashMap<String, Actor>();
    private TextureAtlas mAtlas;
    private Skin mSkin;
    private AnimScriptLoader mAnimScriptloader = null;

    private static final String[] ANCHOR_NAMES = {
        "topLeft",
        "topCenter",
        "topRight",
        "centerLeft",
        "center",
        "centerRight",
        "bottomLeft",
        "bottomCenter",
        "bottomRight"
    };
    private static final Anchor[] ANCHORS = {
        Anchor.TOP_LEFT,
        Anchor.TOP_CENTER,
        Anchor.TOP_RIGHT,
        Anchor.CENTER_LEFT,
        Anchor.CENTER,
        Anchor.CENTER_RIGHT,
        Anchor.BOTTOM_LEFT,
        Anchor.BOTTOM_CENTER,
        Anchor.BOTTOM_RIGHT
    };

    public UiBuilder(TextureAtlas atlas, Skin skin) {
        mAtlas = atlas;
        mSkin = skin;
    }

    public AnimScriptLoader getAnimScriptloader() {
        return mAnimScriptloader;
    }

    public void setAnimScriptloader(AnimScriptLoader animScriptloader) {
        mAnimScriptloader = animScriptloader;
    }

    public Actor build(FileHandle handle) {
        return build(handle, null);
    }

    public Actor build(XmlReader.Element parentElement) {
        return build(parentElement, null);
    }

    public Actor build(FileHandle handle, Group parentActor) {
        XmlReader.Element element = FileUtils.parseXml(handle);
        assert(element != null);
        return build(element, parentActor);
    }

    public Actor build(XmlReader.Element parentElement, Group parentActor) {
        mActorForId.clear();
        return doBuild(parentElement, parentActor);
    }

    private Actor doBuild(XmlReader.Element parentElement, Group parentActor) {
        Actor firstActor = null;
        for (int idx=0, size = parentElement.getChildCount(); idx < size; ++idx) {
            XmlReader.Element element = parentElement.getChild(idx);
            if (element.getName().equals("Action")) {
                continue;
            }
            Actor actor = createActorForElement(element);
            if (idx == 0) {
                firstActor = actor;
            }
            assert(actor != null);
            if (actor instanceof Widget) {
                applyWidgetProperties((Widget)actor, element);
            }
            applyActorProperties(actor, element, parentActor);
            if (mAnimScriptloader != null) {
                createActorActions(actor, element);
            }
            String id = element.getAttribute("id", null);
            if (id != null) {
                if (mActorForId.containsKey(id)) {
                    throw new RuntimeException("Duplicate ids: " + id);
                }
                mActorForId.put(id, actor);
            }
            if (actor instanceof Group && !(actor instanceof ScrollPane)) {
                doBuild(element, (Group)actor);
            }
        }
        return firstActor;
    }

    public <T extends Actor> T getActor(String id) {
        Actor actor = mActorForId.get(id);
        if (actor == null) {
            throw new RuntimeException("No actor with id '" + id + "'");
        }
        @SuppressWarnings("unchecked")
        T obj = (T)actor;
        return obj;
    }

    protected Actor createActorForElement(XmlReader.Element element) {
        String name = element.getName();
        if (name.equals("Image")) {
            return createImage(element);
        } else if (name.equals("ImageButton")) {
            return createImageButton(element);
        } else if (name.equals("TextButton")) {
            return createTextButton(element);
        } else if (name.equals("Group")) {
            return createGroup(element);
        } else if (name.equals("AnchorGroup")) {
            return createAnchorGroup(element);
        } else if (name.equals("Label")) {
            return createLabel(element);
        } else if (name.equals("ScrollPane")) {
            return createScrollPane(element);
        } else if (name.equals("VerticalGroup")) {
            return createVerticalGroup(element);
        } else if (name.equals("HorizontalGroup")) {
            return createHorizontalGroup(element);
        }
        throw new RuntimeException("Unknown UI element type: " + name);
    }

    protected Image createImage(XmlReader.Element element) {
        Image image = new Image();
        String attr = element.getAttribute("name", "");
        if (!attr.isEmpty()) {
            if (attr.endsWith(".9")) {
                initImageFromNinePatchName(image, attr);
            } else {
                initImageFromRegionName(image, attr);
            }
        }
        return image;
    }

    private void initImageFromNinePatchName(Image image, String name) {
        NinePatch patch = mAtlas.createPatch(name.substring(0, name.length() - 2));
        image.setDrawable(new NinePatchDrawable(patch));
    }

    private void initImageFromRegionName(Image image, String name) {
        TextureRegion region = mAtlas.findRegion(name);
        image.setDrawable(new TextureRegionDrawable(region));
        if (image.getWidth() == 0) {
            image.setWidth(region.getRegionWidth());
        }
        if (image.getHeight() == 0) {
            image.setHeight(region.getRegionHeight());
        }
    }

    protected ImageButton createImageButton(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        ImageButton.ImageButtonStyle style =
                new ImageButton.ImageButtonStyle(
                        mSkin.get(styleName, ImageButton.ImageButtonStyle.class));
        String imageName = element.getAttribute("imageName", "");
        if (!imageName.isEmpty()) {
            style.imageUp = mSkin.getDrawable(imageName);
        }
        ImageButton button = new ImageButton(style);
        String imageColor = element.getAttribute("imageColor", "");
        if (!imageColor.isEmpty()) {
            Color color = Color.valueOf(imageColor);
            button.getImage().setColor(color);
        }
        return button;
    }

    protected TextButton createTextButton(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        String text = element.getText();
        return new TextButton(text, mSkin, styleName);
    }

    protected Group createGroup(XmlReader.Element element) {
        return new Group();
    }

    protected AnchorGroup createAnchorGroup(XmlReader.Element element) {
        float spacing = element.getFloatAttribute("spacing", 1);
        AnchorGroup group = new AnchorGroup();
        group.setSpacing(spacing);
        return group;
    }

    protected Label createLabel(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "default");
        String text = element.getText();
        Label label = new Label(text, mSkin, styleName);
        String alignText = element.getAttribute("align", "");
        if (!alignText.isEmpty()) {
            int align = 0;
            if (alignText.equals("left")) {
                align = Align.left;
            } else if (alignText.equals("center")) {
                align = Align.center;
            } else if (alignText.equals("right")) {
                align = Align.right;
            } else {
                throw new RuntimeException("Unknown value of 'align': " + alignText);
            }
            label.setAlignment(align);
        }
        return label;
    }

    protected ScrollPane createScrollPane(XmlReader.Element element) {
        String styleName = element.getAttribute("style", "");
        ScrollPane pane;
        if (styleName.isEmpty()) {
            pane = new ScrollPane(null);
        } else {
            pane = new ScrollPane(null, mSkin, styleName);
        }
        Actor child = doBuild(element, null);
        if (child != null) {
            pane.setWidget(child);
        }
        return pane;
    }

    protected VerticalGroup createVerticalGroup(XmlReader.Element element) {
        VerticalGroup group = new VerticalGroup();
        group.space(element.getFloatAttribute("spacing", 0));
        return group;
    }

    protected HorizontalGroup createHorizontalGroup(XmlReader.Element element) {
        HorizontalGroup group = new HorizontalGroup();
        group.setSpacing(element.getFloatAttribute("spacing", 0));
        return group;
    }

    protected void applyWidgetProperties(Widget widget, XmlReader.Element element) {
        widget.setFillParent(element.getBooleanAttribute("fillParent", false));
    }

    protected void applyActorProperties(Actor actor, XmlReader.Element element, Group parentActor) {
        AnchorGroup anchorGroup = null;
        if (parentActor != null) {
            parentActor.addActor(actor);
            if (parentActor instanceof AnchorGroup) {
                anchorGroup = (AnchorGroup)parentActor;
            }
        }
        String attr = element.getAttribute("x", "");
        if (!attr.isEmpty()) {
            actor.setX(Float.parseFloat(attr));
        }
        attr = element.getAttribute("y", "");
        if (!attr.isEmpty()) {
            actor.setY(Float.parseFloat(attr));
        }
        attr = element.getAttribute("width", "");
        if (!attr.isEmpty()) {
            actor.setWidth(Float.parseFloat(attr));
        }
        attr = element.getAttribute("height", "");
        if (!attr.isEmpty()) {
            actor.setHeight(Float.parseFloat(attr));
        }
        attr = element.getAttribute("originX", "");
        if (!attr.isEmpty()) {
            actor.setOriginX(Float.parseFloat(attr));
        }
        attr = element.getAttribute("originY", "");
        if (!attr.isEmpty()) {
            actor.setOriginY(Float.parseFloat(attr));
        }
        for (int idx = 0, size = ANCHOR_NAMES.length; idx < size; ++idx) {
            String anchorName = ANCHOR_NAMES[idx];
            attr = element.getAttribute(anchorName, "");
            if (!attr.isEmpty()) {
                if (anchorGroup == null) {
                    throw new RuntimeException("Parent of " + actor + " is not an anchor group");
                }
                Rule rule = parseRule(attr, anchorGroup.getSpacing());
                rule.target = actor;
                rule.targetAnchor = ANCHORS[idx];
                anchorGroup.addRule(rule);
            }
        }
    }

    /**
     * Parse a string of the form "$actorId $anchorName [$xOffset $yOffset]"
     * @param txt
     * @return
     */
    private Rule parseRule(String txt, float spacing) {
        Rule rule = new AnchorGroup.Rule();
        String[] tokens = txt.split(" +");
        assert(tokens.length == 1 || tokens.length == 3);
        String[] tokens2 = tokens[0].split("\\.");
        rule.reference = getActor(tokens2[0]);
        for (int idx = 0, size = ANCHOR_NAMES.length; idx < size; ++idx) {
            if (tokens2[1].equals(ANCHOR_NAMES[idx])) {
                rule.referenceAnchor = ANCHORS[idx];
                break;
            }
        }
        if (rule.referenceAnchor == null) {
            throw new RuntimeException("Invalid anchor name: '" + tokens[1] + "'");
        }
        if (tokens.length == 3) {
            rule.hSpace = Float.parseFloat(tokens[1]) * spacing;
            rule.vSpace = Float.parseFloat(tokens[2]) * spacing;
        }
        return rule;
    }

    protected void createActorActions(Actor actor, XmlReader.Element element) {
        for (XmlReader.Element child: element.getChildrenByName("Action")) {
            String definition = child.getText();
            float duration = child.getFloatAttribute("duration", -1);
            if (duration < 0) {
                throw new RuntimeException("Missing 'duration' attribute for action '" + definition + "'");
            }
            AnimScript script = mAnimScriptloader.load(definition);
            actor.addAction(script.createAction(1, 1, duration));
        }
    }

}
