package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Dialog;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.StageScreen;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import static com.greenyetilab.linguaj.Translator.tr;

/**
 * Configuration dialog which appears when one clicks the config button
 */
public class ConfigDialog extends Dialog {
    private static final String MASTODON_URL = "https://mastodon.xyz/@agateau";
    private static final String TWITTER_URL = "https://twitter.com/aureliengateau";
    private static final String GOODIES_URL = "http://agateau.com/redirect/bp-goodies";
    private final BurgerPartyGame mGame;
    private final ConfigButton mMuteButton;

    private static class ConfigButton extends HorizontalGroup {
        private final Label mSubtitleLabel;
        private ImageButton mButton;

        public ConfigButton(Assets assets, String imageName, String titleText, String subtitleText) {
            this(assets, imageName, titleText, subtitleText, "default");
        }

        public ConfigButton(Assets assets, String imageName, String titleText, String subtitleText, String styleName) {
            setSpacing(UiUtils.SPACING / 2);

            mButton = BurgerPartyUiBuilder.createRoundButton(assets, imageName, styleName);
            mButton.setTouchable(Touchable.disabled);
            setTouchable(Touchable.enabled);
            addListener(mButton.getClickListener());

            Label titleLabel = new Label(titleText, assets.getSkin(), "config-button-title");
            mSubtitleLabel = new Label(subtitleText, assets.getSkin(), "config-button-subtitle");

            addActor(mButton);
            VerticalGroup vGroup = new VerticalGroup();
            vGroup.setAlignment(Align.left);
            vGroup.addActor(titleLabel);
            vGroup.addActor(mSubtitleLabel);
            vGroup.setSpacing(-10);

            addActor(vGroup);
            setSize(getPrefWidth(), getPrefHeight());
        }

        public Image getImage() {
            return mButton.getImage();
        }

        public void setSubtitleLabel(String subtitleLabel) {
            mSubtitleLabel.setText(subtitleLabel);
        }
    }

    public ConfigDialog(StageScreen stageScreen, final BurgerPartyGame game) {
        super(stageScreen, game.getAssets());
        final float columnWidth = stageScreen.getStage().getWidth() * 0.35f;
        mGame = game;
        Assets assets = game.getAssets();
        mMuteButton = new ConfigButton(assets, "ui/icon-sound-on", tr("Sound"), "");
        ConfigButton aboutButton = new ConfigButton(assets, "ui/icon-info", tr("About"), tr("Who made this?"));
        ConfigButton mastodonButton = new ConfigButton(assets, "ui/icon-mastodon", tr("Mastodon"), tr("Follow me on Mastodon,"), "mastodon-button");
        ConfigButton twitterButton = new ConfigButton(assets, "ui/icon-twitter", tr("Twitter"), tr("or on Twitter!"), "twitter-button");
        ConfigButton rateButton = new ConfigButton(assets, "ui/icon-rate", tr("Rate Burger Party"), tr("Like the game? Would be awesome if you could give it a good rate!"));
        ConfigButton goodiesButton = new ConfigButton(assets, "ui/icon-goodies", tr("Goodies"), tr("Buy Burger Party goodies!"), "goodies-button");

        Table root = new Table(assets.getSkin());
        final float spacing = UiUtils.SPACING * 0.8f;
        root.defaults().width(columnWidth).left().padBottom(spacing).padRight(spacing);
        root.padTop(spacing).padLeft(spacing);
        root.add(mMuteButton);
        root.add(mastodonButton);
        root.row();
        root.add(aboutButton);
        root.add(twitterButton);
        root.row();
        root.add(rateButton).colspan(2);
        root.row();
        root.add(goodiesButton).colspan(2);
        root.setSize(MathUtils.round(root.getPrefWidth()), MathUtils.round(root.getPrefHeight()));
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

        mastodonButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(MASTODON_URL);
            }
        });

        twitterButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(TWITTER_URL);
            }
        });

        goodiesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.net.openURI(GOODIES_URL);
            }
        });
    }

    private void updateMuteButton() {
        boolean muted = mGame.getMusicController().isMuted();
        Drawable drawable = mGame.getAssets().getSkin().getDrawable(muted ? "ui/icon-sound-off" : "ui/icon-sound-on");
        mMuteButton.getImage().setDrawable(drawable);
        mMuteButton.setSubtitleLabel(muted ? tr("Sound is OFF") : tr("Sound is ON"));
    }
}
