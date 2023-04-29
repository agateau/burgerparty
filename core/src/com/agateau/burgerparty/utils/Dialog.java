package com.agateau.burgerparty.utils;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * An overlay which looks like a dialog box
 */
public class Dialog extends Overlay {
    private final AnchorGroup mDialogGroup;
    private final Image mFrameImage;
    private final NinePatch mFramePatch;
    private final StageScreen mScreen;

    public Dialog(StageScreen screen, Assets assets) {
        super(assets.getTextureAtlas());
        mScreen = screen;

        mDialogGroup = new AnchorGroup();
        mDialogGroup.setSpacing(UiUtils.SPACING);
        mDialogGroup.setFillParent(true);
        addActor(mDialogGroup);

        mFramePatch = assets.getTextureAtlas().createPatch("ui/frame");
        mFrameImage = new Image(mFramePatch);

        ImageButton skipButton = BurgerPartyUiBuilder.createRoundButton(assets, "ui/icon-close");
        skipButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                close();
            }
        });

        mDialogGroup.addRule(mFrameImage, Anchor.CENTER, mDialogGroup, Anchor.CENTER);
        mDialogGroup.addRule(skipButton, Anchor.CENTER_RIGHT, mFrameImage, Anchor.BOTTOM_RIGHT, -1, 0);
    }

    public void setChild(Actor child) {
        mFrameImage.setSize(
                MathUtils.ceil(child.getWidth()) + mFramePatch.getPadLeft() + mFramePatch.getPadRight(),
                MathUtils.ceil(child.getHeight()) + mFramePatch.getPadTop() + mFramePatch.getPadBottom()
        );

        mDialogGroup.addRule(child, Anchor.CENTER, mFrameImage, Anchor.CENTER);
    }

    @Override
    public void onBackPressed() {
        close();
    }

    public void close() {
        mScreen.setOverlay(null);
    }
}
