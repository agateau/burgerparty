package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.Overlay;

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
        setupWidgets();
        mGame.getAssets().getSoundAtlas().findSound("gameover").play();
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(mGame.getAssets());
        builder.build(FileUtils.assets("screens/gameoveroverlay.gdxui"));
        AnchorGroup root = builder.getActor("root");
        root.setFillParent(true);
        addActor(root);

        Label label = builder.getActor("gameOverLabel");
        label.setText(tr("Game Over"));

        ImageButton tryAgainButton = builder.getActor("tryAgainButton");
        tryAgainButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
            }
        });

        ImageButton selectLevelButton = builder.getActor("selectLevelButton");
        selectLevelButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.showLevelListScreen(mGame.getLevelWorldIndex());
            }
        });
    }

    @Override
    public void onBackPressed() {
        mGame.showLevelListScreen(mGame.getLevelWorldIndex());
    }
}
