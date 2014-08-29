package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.greenyetilab.linguaj.Translator.tr;

public class AboutScreen extends BurgerPartyScreen {
    private static final float PIXEL_PER_SECOND = 48;

    private ScrollPane mScrollPane;
    private VerticalGroup mScrollGroup;

    public AboutScreen(BurgerPartyGame game) {
        super(game);
        Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
        setBackgroundActor(bgImage);
        setupWidgets();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().showAboutScreen();
                dispose();
            }
        };
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
        builder.build(FileUtils.assets("screens/about.gdxui"));
        AnchorGroup root = builder.getActor("root");
        getStage().addActor(root);
        root.setFillParent(true);

        builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                onBackPressed();
            }
        });

        builder.<Label>getActor("titleLabel").addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                ++mClickCount;
                if (mClickCount >= 5) {
                    getGame().showCheatScreen();
                }
            }
            private int mClickCount = 0;
        });

        mScrollPane = builder.<ScrollPane>getActor("scrollPane");

        mScrollGroup = builder.<VerticalGroup>getActor("scrollGroup");
        addText(tr("Version %s", Constants.VERSION));
        addHeading(tr("Code & Design"));
        addText("Aurélien Gâteau");

        addHeading(tr("Musics"));
        addText(tr("Thomas Tripon"));

        addHeading(tr("Testers"));
        addText("Clara Gâteau");
        addText("Antonin Gâteau");
        addText("Gwenaëlle Gâteau");
        addText("Mathieu Maret");
        addText("Joël Bray");
        addText("Thomas Monjalon");

        addHeading(tr("Fonts"));
        addText("Nick Curtis - www.dafont.com/fr/walrus-gumbo.font");
        addText("Jayvee D. Enaguas - www.dafont.com/fr/sanitechtro.font");

        addHeading(tr("Sounds"));
        addText("Clara Gâteau");
        addText("AlienXXX - www.freesound.org/people/AlienXXX");
        addText("artisticdude - opengameart.org/content/inventory-sound-effects");
        addText("Autistic Lucario - www.freesound.org/people/Autistic%20Lucario");
        addText("DrMinky - www.freesound.org/people/DrMinky");
        addText("Florian Reinke - www.freesound.org/people/florian_reinke");
        addText("p0ss - opengameart.org/content/spell-sounds-starter-pack");
        addText("Scriptique - www.freesound.org/people/scriptique");
        addText("Soundjay - www.soundjay.com");

        float screenHeight = getStage().getHeight();
        addPadding(screenHeight / 3);
        addText(tr("© 2013 - 2014 Green Yeti Lab"));
        addPadding(screenHeight * 2 / 3);
    }

    private void addText(String text) {
        Label label = new Label(text, getGame().getAssets().getSkin(), "about-text");
        mScrollGroup.addActor(label);
    }

    private void addHeading(String text) {
        Label label = new Label("\n" + text, getGame().getAssets().getSkin(), "about-heading");
        mScrollGroup.addActor(label);
    }

    private void addPadding(float height) {
        Actor padding = new Actor();
        padding.setHeight(height);
        mScrollGroup.addActor(padding);
    }

    @Override
    public void onBackPressed() {
        getGame().showStartScreen();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (Gdx.input.isTouched()) {
            return;
        }
        float maxY = mScrollPane.getWidget().getHeight();
        float y = mScrollPane.getScrollY();
        if (y < maxY) {
            mScrollPane.setScrollY(y + PIXEL_PER_SECOND * delta);
        }
    }
}
