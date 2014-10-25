package com.agateau.burgerparty.screens;

import java.util.HashSet;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Universe;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.Signal1;
import com.agateau.burgerparty.utils.TiledImage;
import com.agateau.burgerparty.view.AchievementsButtonController;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.agateau.burgerparty.view.WorldBaseButton;
import com.agateau.burgerparty.view.WorldListView;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class WorldListScreen extends BurgerPartyScreen {
    private HashSet<Object> mHandlers = new HashSet<Object>();
    private int mStarCount;

    @SuppressWarnings("unused")
    private AchievementsButtonController mAchievementsButtonController;

    public WorldListScreen(BurgerPartyGame game) {
        super(game);
        Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
        mStarCount = getGame().getCurrentUniverse().starCount.getValue();
        setBackgroundActor(bgImage);
        setupWidgets();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().setScreen(new WorldListScreen(getGame()));
                dispose();
            }
        };
    }

    @Override
    public void onBackPressed() {
        getGame().showStartScreen();
    }

    private class SandBoxButton extends WorldBaseButton {
        public SandBoxButton() {
            super("", "sandbox-preview", getGame().getAssets());
            addListener(new ChangeListener() {
                public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                    getGame().startSandBox();
                }
            });
        }
    }

    private class Builder extends BurgerPartyUiBuilder {
        public Builder(Assets assets) {
            super(assets);
        }

        @Override
        protected Actor createActorForElement(XmlReader.Element element) {
            if (element.getName().equals("WorldListView")) {
                Universe universe = getGame().getCurrentUniverse();
                WorldListView view = new WorldListView(universe.getWorlds(), -1, getGame().getAssets(), WorldListView.Details.SHOW_STARS);

                SandBoxButton button = new SandBoxButton();
                if (!getGame().getGameStats().sandBoxAchievement.isUnlocked()) {
                    button.createLockOverlay();
                }

                Actor ruler = new TiledImage(getGame().getAssets().getTextureAtlas().findRegion("ui/vruler"));
                ruler.setHeight(button.getPrefHeight());
                ruler.setWidth(6);
                ruler.setColor(1, 1, 1, 0.4f);

                view.addActor(ruler);
                view.addActor(button);
                return view;
            }
            return super.createActorForElement(element);
        }
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new Builder(getGame().getAssets());
        builder.build(FileUtils.assets("screens/worldlist.gdxui"));
        AnchorGroup root = builder.getActor("root");
        getStage().addActor(root);
        root.setFillParent(true);

        builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                onBackPressed();
            }
        });

        builder.<WorldListView>getActor("worldListView").currentIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
            @Override
            public void handle(Integer index) {
                getGame().showLevelListScreen(index);
            }
        });

        Label starCountLabel = builder.<Label>getActor("starCountLabel");
        starCountLabel.setText(String.valueOf(mStarCount));

        mAchievementsButtonController = new AchievementsButtonController(
            builder.<ImageButton>getActor("achievementsButton"), getGame());
    }
}
