package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Dialog;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.greenyetilab.linguaj.Translator.tr;

/**
 * Configuration dialog which appears when one clicks the config button
 */
public class ConfigDialog extends Dialog {
    private static final String FACEBOOK_URL = "https://facebook.com/GreenYetiLab";
    private static final String GPLUS_URL = "https://plus.google.com/+GreenYetiLab";
    private final BurgerPartyGame mGame;
    private final ConfigButton mMuteButton;

    private static class ConfigButton extends HorizontalGroup {
        private ImageButton mButton;

        public ConfigButton(Assets assets, String imageName, String text) {
            this(assets, imageName, text, "default");
        }

        public ConfigButton(Assets assets, String imageName, String text, String styleName) {
            Label label = new Label(text, assets.getSkin(), "config-button");
            mButton = BurgerPartyUiBuilder.createRoundButton(assets, imageName, styleName);
            setSpacing(UiUtils.SPACING / 2);
            addActor(mButton);
            addActor(label);
            setSize(getPrefWidth(), getPrefHeight());
        }

        public Image getImage() {
            return mButton.getImage();
        }
    }

    public ConfigDialog(StageScreen stageScreen, final BurgerPartyGame game) {
        super(stageScreen, game.getAssets());
        mGame = game;
        Assets assets = game.getAssets();
        mMuteButton = new ConfigButton(assets, "ui/icon-sound-on", tr("Sound"));
        ConfigButton aboutButton = new ConfigButton(assets, "ui/icon-info", tr("About"));
        ConfigButton facebookButton = new ConfigButton(assets, "ui/icon-fb", tr("Like us"), "fb-button");
        ConfigButton gplusButton = new ConfigButton(assets, "ui/icon-gplus", tr("Add us"), "gplus-button");
        ConfigButton rateButton = new ConfigButton(assets, "ui/icon-rate", tr("Rate Burger Party"));

        Table root = new Table(assets.getSkin());
        final float spacing = UiUtils.SPACING;
        final float colWidth = stageScreen.getStage().getWidth() * 0.3f;
        root.defaults().left().padBottom(spacing).padRight(spacing);
        root.padTop(spacing).padLeft(spacing);
        root.add(mMuteButton).width(colWidth).uniform();
        root.add(facebookButton).uniform();
        root.row();
        root.add(aboutButton);
        root.add(gplusButton);
        root.row();
        root.add(rateButton).colspan(2);
        root.setSize(root.getPrefWidth(), root.getPrefHeight());
        setChild(root);

        mMuteButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                MusicController controller = mGame.getMusicController();
                controller.setMuted(!controller.isMuted());
                updateMuteButton();
            }
        });
        updateMuteButton();

        aboutButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                game.showAboutScreen();
            }
        });

        rateButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent changeEvent, Actor actor) {
                game.getRatingController().rate();
            }
        });

        facebookButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(FACEBOOK_URL);
            }
        });

        gplusButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(GPLUS_URL);
            }
        });
    }

    private void updateMuteButton() {
        boolean muted = mGame.getMusicController().isMuted();
        Drawable drawable = mGame.getAssets().getSkin().getDrawable(muted ? "ui/icon-sound-off" : "ui/icon-sound-on");
        mMuteButton.getImage().setDrawable(drawable);
    }
}
