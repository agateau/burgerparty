package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class AchievementSmallView extends AnchorGroup {
    private static final float PADDING = 6;
    private static final float PADDING_RIGHT = 24;
    private static final float SHADOW_HEIGHT = 3;

    public AchievementSmallView(Assets assets, Achievement achievement) {
        TextureAtlas atlas = assets.getTextureAtlas();
        // Create a new patch so we can remove the top border
        NinePatch patch = atlas.createPatch("ui/achievement-small-bg");
        //patch.setTopHeight(0);
        Image bg = new Image(patch);
        bg.setFillParent(true);
        addActor(bg);

        TextureRegion iconRegion = atlas.findRegion("achievements/" + achievement.getId());
        if (iconRegion == null) {
            iconRegion = atlas.findRegion("achievements/generic");
        }

        Image icon = new Image(iconRegion);
        icon.setScale(0.5f);
        Label titleLabel = new Label(achievement.getTitle(), assets.getSkin(), "achievement-small");

        addRule(icon, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_LEFT, PADDING, PADDING + SHADOW_HEIGHT);
        addRule(titleLabel, Anchor.CENTER_LEFT, icon, Anchor.CENTER_RIGHT, PADDING, 0);
        setSize(
            PADDING + icon.getWidth() * icon.getScaleX() + PADDING + titleLabel.getPrefWidth() + PADDING_RIGHT,
            PADDING + icon.getHeight() * icon.getScaleY() + PADDING + SHADOW_HEIGHT);
        layout();
    }
}
