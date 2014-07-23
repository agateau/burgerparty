package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.AchievementsButtonController;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class StartScreen extends BurgerPartyScreen {
    private static final float MORE_ANIM_HEIGHT = 24;

    private ImageButton mMuteButton;

    @SuppressWarnings("unused")
    private AchievementsButtonController mAchievementsButtonController;

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

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
        builder.build(FileUtils.assets("screens/start.gdxui"));
        AnchorGroup root = builder.getActor("root");
        getStage().addActor(root);
        root.setFillParent(true);

        builder.<ImageButton>getActor("startButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                onStartClicked();
            }
        });
        builder.<ImageButton>getActor("aboutButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                getGame().showAboutScreen();
            }
        });

        mAchievementsButtonController = new AchievementsButtonController(
            builder.<ImageButton>getActor("achievementsButton"), getGame());

        ImageButton moreButton = builder.<ImageButton>getActor("moreButton");
        final AnchorGroup moreGroup = builder.<AnchorGroup>getActor("moreGroup");
        moreGroup.setColor(1, 1, 1, 0);
        moreButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                boolean showing = moreGroup.getColor().a < 1;
                moreGroup.addAction(Actions.alpha(showing ? 1 : 0, 0.2f));
                moreGroup.addAction(Actions.moveBy(0, showing ? MORE_ANIM_HEIGHT : -MORE_ANIM_HEIGHT, 0.2f, Interpolation.pow2Out));
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
        moreGroup.setPosition(moreButton.getX(), moreButton.getTop() + root.getSpacing() - MORE_ANIM_HEIGHT);
    }

    private void onStartClicked() {
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
