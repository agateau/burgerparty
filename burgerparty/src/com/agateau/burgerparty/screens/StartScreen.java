package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.model.Difficulty;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class StartScreen extends BurgerPartyScreen {
    private static final float MORE_ANIM_HEIGHT = 24;

    private ImageButton mMuteButton;

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
        builder.<Image>getActor(name).addListener(new ClickListener() {
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

        builder.<ImageButton>getActor("aboutButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                getGame().showAboutScreen();
            }
        });

        final ImageButton moreButton = builder.<ImageButton>getActor("moreButton");
        final VerticalGroup moreGroup = builder.<VerticalGroup>getActor("moreGroup");
        moreGroup.setVisible(false);
        moreGroup.setColor(1, 1, 1, 0);
        moreButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                boolean showing = moreButton.isChecked();
                if (moreGroup.getX() == 0) {
                    moreGroup.setPosition(moreButton.getX() + moreButton.getWidth() / 2, moreButton.getY() - moreGroup.getHeight() + MORE_ANIM_HEIGHT);
                }
                if (showing) {
                    moreGroup.setVisible(true);
                    moreGroup.addAction(Actions.alpha(1, 0.2f));
                    moreGroup.addAction(Actions.moveBy(0, -MORE_ANIM_HEIGHT, 0.2f, Interpolation.pow2Out));
                } else {
                    moreGroup.addAction(Actions.alpha(0, 0.2f));
                    moreGroup.addAction(
                        Actions.sequence(
                            Actions.moveBy(0, MORE_ANIM_HEIGHT, 0.2f, Interpolation.pow2Out),
                            Actions.hide()
                        )
                    );
                }
            }
        });

        mMuteButton = builder.<ImageButton>getActor("muteButton");
        mMuteButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                MusicController controller = getGame().getMusicController();
                controller.setMuted(!controller.isMuted());
                updateMuteButton();
            }
        });
        updateMuteButton();
        root.layout();
    }

    private void onStartClicked(Difficulty difficulty) {
        getGame().setDifficulty(difficulty);
        getGame().getAdController().onStartPlaying();
        getGame().showWorldListScreen();
    }

    @Override
    public void onBackPressed() {
        Gdx.app.exit();
    }

    private void updateMuteButton() {
        boolean muted = getGame().getMusicController().isMuted();
        Drawable drawable = getGame().getAssets().getSkin().getDrawable(muted ? "ui/icon-sound-off" : "ui/icon-sound-on");
        mMuteButton.getImage().setDrawable(drawable);
    }
}
