package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.greenyetilab.linguaj.Translator.tr;

public class PauseOverlay extends Overlay {
    private final WorldView mWorldView;
    private final BurgerPartyGame mGame;
    private ImageButton mMuteButton;

    public PauseOverlay(WorldView worldView, BurgerPartyGame game, TextureAtlas atlas, Skin skin) {
        super(atlas);
        mWorldView = worldView;
        mGame = game;

        String txt = tr("Level %d-%d", game.getLevelWorldIndex() + 1, game.getLevelIndex() + 1) + "\n";
        int highScore = game.getUniverse().getHighScore(game.getLevelWorldIndex(), game.getLevelIndex());
        if (highScore > 0) {
            txt += tr("High score: %d", highScore);
        } else {
            txt += tr("No high score yet");
        }
        Label levelLabel = new Label(txt, skin);
        levelLabel.setAlignment(Align.center, Align.center);

        Label pausedLabel = new Label(tr("Paused"), skin);

        ImageButton resumeButton = Kernel.createRoundButton(game.getAssets(), "ui/icon-play");
        resumeButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mWorldView.resume();
            }
        });

        ImageButton restartButton = Kernel.createRoundButton(game.getAssets(), "ui/icon-restart");
        restartButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.getAdController().onLevelRestarted();
                mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
            }
        });

        ImageButton selectLevelButton = Kernel.createRoundButton(game.getAssets(), "ui/icon-levels");
        selectLevelButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.showLevelListScreen(mGame.getLevelWorldIndex());
            }
        });

        mMuteButton = Kernel.createRoundButton(game.getAssets(), "ui/icon-sound-on");
        mMuteButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                MusicController controller = mGame.getMusicController();
                controller.setMuted(!controller.isMuted());
                updateMuteButton();
            }
        });
        updateMuteButton();

        AnchorGroup group = new AnchorGroup();
        addActor(group);
        group.setFillParent(true);
        group.setSpacing(UiUtils.SPACING);

        group.addRule(levelLabel, Anchor.TOP_CENTER, this, Anchor.TOP_CENTER);
        group.addRule(resumeButton, Anchor.CENTER, this, Anchor.CENTER);
        group.addRule(pausedLabel, Anchor.BOTTOM_CENTER, resumeButton, Anchor.TOP_CENTER, 0, 1);
        group.addRule(restartButton, Anchor.BOTTOM_RIGHT, this, Anchor.BOTTOM_CENTER, -0.5f, 1);
        group.addRule(selectLevelButton, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_CENTER, 0.5f, 1);
        group.addRule(mMuteButton, Anchor.BOTTOM_LEFT, this, Anchor.BOTTOM_LEFT, 0.5f, 1);
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
