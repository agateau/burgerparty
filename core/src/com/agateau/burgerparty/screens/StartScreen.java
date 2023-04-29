package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.model.Difficulty;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.agateau.burgerparty.view.ConfigDialog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class StartScreen extends BurgerPartyScreen {
    public StartScreen(BurgerPartyGame game) {
        super(game);
        Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
        setBackgroundActor(bgImage);
        setupWidgets();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().showStartScreen();
                dispose();
            }
        };
    }

    private void setupStartButton(BurgerPartyUiBuilder builder, String name, final Difficulty difficulty) {
        builder.getActor(name).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().getAssets().getSoundAtlas().findSound("click").play();
                onStartClicked(difficulty);
            }
        });
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
        builder.build(FileUtils.assets("screens/start.gdxui"));
        AnchorGroup root = builder.getActor("root");
        getStage().addActor(root);
        root.setFillParent(true);

        setupStartButton(builder, "startEasy", Constants.EASY);
        setupStartButton(builder, "startNormal", Constants.NORMAL);
        setupStartButton(builder, "startHard", Constants.HARD);

        final ImageButton moreButton = builder.getActor("moreButton");
        final StageScreen self = this;
        moreButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                setOverlay(new ConfigDialog(self, getGame()));
            }
        });
        root.layout();
    }

    private void onStartClicked(Difficulty difficulty) {
        getGame().setDifficulty(difficulty);
        getGame().showWorldListScreen();
    }

    @Override
    public void onBackPressed() {
        Gdx.app.exit();
    }
}
