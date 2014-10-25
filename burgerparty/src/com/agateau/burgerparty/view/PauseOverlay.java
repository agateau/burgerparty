package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.Overlay;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.greenyetilab.linguaj.Translator.tr;

public class PauseOverlay extends Overlay {
    private final WorldView mWorldView;
    private final BurgerPartyGame mGame;
    private ImageButton mMuteButton;
    @SuppressWarnings("unused")
    private AchievementsButtonController mAchievementsButtonController;

    public PauseOverlay(WorldView worldView, BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
        super(atlas);
        mWorldView = worldView;
        mGame = game;
        setupWidgets();
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(mGame.getAssets());
        builder.build(FileUtils.assets("screens/pauseoverlay.gdxui"));
        AnchorGroup root = builder.getActor("root");
        root.setFillParent(true);
        addActor(root);

        String txt = tr("Level %d-%d", mGame.getLevelWorldIndex() + 1, mGame.getLevelIndex() + 1) + "\n";
        int highScore = mGame.getCurrentUniverse().getHighScore(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
        if (highScore > 0) {
            txt += tr("High score: %d", highScore);
        } else {
            txt += tr("No high score yet");
        }
        Label levelLabel = builder.getActor("levelLabel");
        levelLabel.setText(txt);

        Label pausedLabel = builder.getActor("pausedLabel");
        pausedLabel.setText(tr("Paused"));

        builder.getActor("resumeButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mWorldView.resume();
            }
        });

        builder.getActor("restartButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.getAdController().onLevelRestarted();
                mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
            }
        });

        builder.getActor("selectLevelButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.showLevelListScreen(mGame.getLevelWorldIndex());
            }
        });

        mMuteButton = builder.getActor("muteButton");
        mMuteButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                MusicController controller = mGame.getMusicController();
                controller.setMuted(!controller.isMuted());
                updateMuteButton();
            }
        });
        updateMuteButton();

        ImageButton achievementsButton = builder.getActor("achievementsButton");
        mAchievementsButtonController = new AchievementsButtonController(achievementsButton, mGame);
    }

    @Override
    public void onBackPressed() {
        mWorldView.resume();
    }

    private void updateMuteButton() {
        boolean muted = mGame.getMusicController().isMuted();
        Drawable drawable = mGame.getAssets().getSkin().getDrawable(muted ? "ui/icon-sound-off" : "ui/icon-sound-on");
        mMuteButton.getImage().setDrawable(drawable);
    }
}
