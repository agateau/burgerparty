package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.UiUtils;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static com.greenyetilab.linguaj.Translator.tr;

public class GameOverOverlay extends Overlay {
    private BurgerPartyGame mGame;

    public GameOverOverlay(BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
        super(atlas);
        mGame = game;

        Label label = new Label(tr("Game Over"), skin);

        ImageButton tryAgainButton = Kernel.createRoundButton(mGame.getAssets(), "ui/icon-restart");
        tryAgainButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
            }
        });

        ImageButton selectLevelButton = Kernel.createRoundButton(mGame.getAssets(), "ui/icon-levels");
        selectLevelButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.showLevelListScreen(mGame.getLevelWorldIndex());
            }
        });

        AnchorGroup group = new AnchorGroup();
        addActor(group);
        group.setFillParent(true);
        group.setSpacing(UiUtils.SPACING);

        group.addRule(label, Anchor.BOTTOM_CENTER, this, Anchor.CENTER, 0, 2);
        group.addRule(tryAgainButton, Anchor.TOP_CENTER, this, Anchor.CENTER);
        group.addRule(selectLevelButton, Anchor.TOP_CENTER, tryAgainButton, Anchor.BOTTOM_CENTER, 0, -1);

        game.getAssets().getSoundAtlas().findSound("gameover").play();
    }

    @Override
    public void onBackPressed() {
        mGame.showLevelListScreen(mGame.getLevelWorldIndex());
    }
}
