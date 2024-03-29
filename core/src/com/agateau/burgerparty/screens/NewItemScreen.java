package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.MealItem;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.ShaderActor;
import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.TimeLineAction;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.Bubble;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlReader;

import static com.greenyetilab.linguaj.Translator.tr;

public class NewItemScreen extends BurgerPartyScreen {
    private static final float DISPLAY_DURATION = 3f;
    private static final float FADE_IN_DURATION = 0.5f;
    private static final float FADE_OUT_DURATION = 0.8f;
    private static final float TEXT_MAX_WIDTH = 300;

    public Signal0 done = new Signal0();

    private Actor mBgActor;
    private WidgetGroup mFgGroup;
    private Image mFgImage;
    private Bubble mBubble;
    private AnchorGroup mBubbleContent;
    private Label mBubbleLabel;
    private Image mItemImage;
    private float mFgGroupFinalX;

    private static class RayActor extends ShaderActor {
        public RayActor(TextureRegion region, Color bgColor1, Color bgColor2, Color fgColor, float degPerSecond) {
            super(region);
            mBgColor1 = bgColor1;
            mBgColor2 = bgColor2;
            mFgColor = fgColor;
            mDegPerSecond = degPerSecond;
            setShader(new ShaderProgram(FileUtils.assets("shaders/default-vert.glsl"), FileUtils.assets("shaders/new-item-frag.glsl")));
        }
        @Override
        public void act(float delta) {
            mAngle = (mAngle + delta * mDegPerSecond) % 360;
        }

        @Override
        protected void applyShaderParameters(ShaderProgram shader, float parentAlpha) {
            shader.setUniformf("resolution", getWidth(), getHeight());
            shader.setUniformf("startAngle", mAngle);
            shader.setUniformf("bgColor1", mBgColor1.r, mBgColor1.g, mBgColor1.b, mBgColor1.a);
            shader.setUniformf("bgColor2", mBgColor2.r, mBgColor2.g, mBgColor2.b, mBgColor2.a);
            shader.setUniformf("fgColor", mFgColor.r, mFgColor.g, mFgColor.b, mFgColor.a);
            shader.setUniformf("parentAlpha", parentAlpha);
        }

        private Color mBgColor1;
        private Color mBgColor2;
        private Color mFgColor;
        private float mAngle = 0;
        private float mDegPerSecond;
    }

    public NewItemScreen(BurgerPartyGame game, int levelWorld, MealItem item) {
        super(game);

        String levelDir = "levels/" + String.valueOf(levelWorld + 1);
        String fgName = levelDir + "/newitem-fg";
        TextureRegion region = game.getAssets().getTextureAtlas().findRegion(fgName);

        XmlReader.Element root = FileUtils.parseXml(FileUtils.assets(levelDir + "/newitemscreen.xml"));
        assert(root != null);
        Color bgColor1 = Color.valueOf(root.getAttribute("bgColor1"));
        Color bgColor2 = Color.valueOf(root.getAttribute("bgColor2"));
        Color fgColor = Color.valueOf(root.getAttribute("fgColor"));
        mBgActor = new RayActor(region, bgColor1, bgColor2, fgColor, 8);
        setBackgroundActor(mBgActor);

        mFgGroup = new WidgetGroup();
        mFgImage = new Image(region);

        setupBubble(item);

        mFgGroupFinalX = root.getFloatAttribute("fgX", 400);
        float bubbleXOffset = root.getFloatAttribute("bubbleXOffset", 0);
        mBubble.setPosition(-mBubble.getWidth() + bubbleXOffset, mFgImage.getHeight() / 2);

        getStage().addActor(mFgGroup);
        mFgGroup.addActor(mFgImage);
        mFgGroup.addActor(mBubble);

        mFgGroup.setPosition(800, 0);
        mFgGroup.setColor(1, 1, 1, 0);

        game.getAssets().getSoundAtlas().findSound("new-item-unlocked").play();
    }

    private void setupBubble(MealItem newItem) {
        TextureAtlas atlas = getGame().getAssets().getTextureAtlas();
        mBubble = new Bubble(atlas.createPatch("ui/bubble-callout-right"));
        mBubble.setColor(1, 1, 1, 0);

        mBubbleContent = new AnchorGroup();
        mBubble.setChild(mBubbleContent);

        mBubbleLabel = new Label(tr("New item unlocked!"), getGame().getAssets().getSkin(), "bubble-text");
        mBubbleLabel.setAlignment(Align.center);
        float textSpacing = UiUtils.SPACING;
        if (mBubbleLabel.getPrefWidth() > TEXT_MAX_WIDTH) {
            mBubbleLabel.setWrap(true);
            mBubbleLabel.setWidth(TEXT_MAX_WIDTH);
            /* Set textSpacing to 0 to work around a bug in libgdx (I guess) which causes Label.getPrefHeight()
             * to return a too high value when text wraps.
             */
            textSpacing = 0;
        }

        mItemImage = new Image(atlas.findRegion("mealitems/" + newItem.getPath() + "-inventory"));

        mBubbleContent.setSize(
            mBubbleLabel.getWidth(),
            mBubbleLabel.getPrefHeight() + textSpacing + mItemImage.getHeight());

        mBubbleContent.addRule(mBubbleLabel, Anchor.TOP_CENTER, mBubbleContent, Anchor.TOP_CENTER, 0, 0);
        mBubbleContent.addRule(mItemImage, Anchor.BOTTOM_CENTER, mBubbleContent, Anchor.BOTTOM_CENTER, 0, 0);

        UiUtils.notifyResizeToFitParent(mBubbleContent);
    }

    public void show() {
        super.show();
        TimeLineAction tl = new TimeLineAction();
        Actor root = getStage().getRoot();

        tl.addAction(0,
            root,
            Actions.sequence(
                Actions.alpha(0),
                Actions.alpha(1, FADE_IN_DURATION)
            )
        );

        tl.addActionRelative(FADE_IN_DURATION,
            mFgGroup,
            Actions.alpha(1)
        );

        tl.addActionRelative(0,
            mFgGroup,
            Actions.moveTo(mFgGroupFinalX, 0, FADE_IN_DURATION, Interpolation.pow5Out)
        );

        tl.addActionRelative(FADE_IN_DURATION,
            mBubble,
            Actions.alpha(1, FADE_IN_DURATION)
        );

        tl.addActionRelative(FADE_IN_DURATION,
            root,
            Actions.sequence(
                Actions.delay(DISPLAY_DURATION),
                Actions.alpha(0, FADE_OUT_DURATION),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        done.emit();
                    }
                })
            )
        );

        root.addAction(tl);
    }

    @Override
    public void onBackPressed() {
        done.emit();
    }
}
