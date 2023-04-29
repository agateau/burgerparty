package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.GridGroup;
import com.agateau.burgerparty.utils.HorizontalGroup;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.UiUtils;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class LevelListScreen extends BurgerPartyScreen {
    private static final int COL_COUNT = 5;

    private static final float SURPRISE_ROTATE_ANGLE = 20f;
    private static final float SURPRISE_ROTATE_DURATION = 0.8f;

    private LevelWorld mLevelWorld;
    private TextureRegion mStarOff;
    private TextureRegion mStarOn;
    private TextureRegion mLock;
    private TextureRegion mSurpriseRegion;

    public LevelListScreen(BurgerPartyGame game, int worldIndex) {
        super(game);
        TextureAtlas atlas = getTextureAtlas();
        Image bgImage = new Image(atlas.findRegion("ui/menu-bg"));
        setBackgroundActor(bgImage);

        mLevelWorld = game.getCurrentUniverse().get(worldIndex);

        mStarOff = atlas.findRegion("ui/star-off");
        mStarOn = atlas.findRegion("ui/star-on");
        mLock = atlas.findRegion("ui/lock-key");
        mSurpriseRegion = atlas.findRegion("ui/surprise");
        setupWidgets();

        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().showLevelListScreen(mLevelWorld.getIndex());
                dispose();
            }
        };
    }

    private class Builder extends BurgerPartyUiBuilder {
        public Builder(Assets assets) {
            super(assets);
        }

        @Override
        protected Actor createActorForElement(XmlReader.Element element) {
            if (element.getName().equals("LevelGrid")) {
                return createLevelButtonGridGroup(element);
            } else if (element.getName().equals("LevelBaseButton")) {
                return new LevelBaseButton(getGame().getAssets());
            } else {
                return super.createActorForElement(element);
            }
        }
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new Builder(getGame().getAssets());
        builder.build(FileUtils.assets("screens/levellist.gdxui"));
        AnchorGroup root = builder.getActor("root");
        getStage().addActor(root);
        root.setFillParent(true);

        builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                onBackPressed();
            }
        });

        ImageButton cutSceneReplayButton = builder.<ImageButton>getActor("cutSceneReplayButton");
        if (mLevelWorld.getIndex() > 0) {
            cutSceneReplayButton.addListener(new ChangeListener() {
                public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                    getGame().showNewWorldScreen(mLevelWorld.getIndex());
                }
            });
        } else {
            cutSceneReplayButton.setVisible(false);
        }

        TextureRegion region = getGame().getAssets().getTextureAtlas().findRegion("ui/corner-" + getGame().getDifficulty().name);
        UiUtils.setImageRegion(builder.<Image>getActor("difficultyImage"), region);
    }

    private GridGroup createLevelButtonGridGroup(XmlReader.Element element) {
        float cellSize = element.getFloatAttribute("cellSize");
        float cellSpacing = element.getFloatAttribute("cellSpacing");
        float starScale = element.getFloatAttribute("starScale");
        float lockScale = element.getFloatAttribute("lockScale");

        GridGroup gridGroup = new GridGroup();
        gridGroup.setSpacing(cellSpacing);
        gridGroup.setColumnCount(COL_COUNT);
        gridGroup.setCellSize(cellSize, cellSize);

        for (Level level: mLevelWorld.getLevels()) {
            gridGroup.addActor(createLevelButton(level, cellSize, starScale, lockScale));
        }
        return gridGroup;
    }

    private static class LevelBaseButton extends TextButton {
        public LevelBaseButton(Assets assets) {
            super("", assets.getSkin(), "level-button");
            mAssets = assets;
        }
        @Override
        public void draw(Batch batch, float parentAlpha) {
            if (isDisabled()) {
                batch.setShader(mAssets.getDisabledShader());
                super.draw(batch, parentAlpha);
                batch.setShader(null);
            } else {
                super.draw(batch, parentAlpha);
            }
        }
        protected Assets mAssets;
    }

    private class LevelButton extends LevelBaseButton {
        public LevelButton(Assets assets, int levelWorldIndex, int levelIndex) {
            super(assets);
            this.levelWorldIndex = levelWorldIndex;
            this.levelIndex = levelIndex;

            mGroup = new AnchorGroup();
            addActor(mGroup);
            mGroup.setFillParent(true);
        }

        public void createStars(int stars, float starScale) {
            setText(String.valueOf(levelWorldIndex + 1) + "-" + String.valueOf(levelIndex + 1));
            HorizontalGroup starGroup = new HorizontalGroup();
            starGroup.setSpacing(4);
            for (int n = 1; n <= 3; ++n) {
                Image image = new Image(n > stars ? mStarOff : mStarOn);
                starGroup.addActor(image);
            }
            starGroup.setScale(starScale);
            starGroup.pack();
            mGroup.addRule(starGroup, Anchor.BOTTOM_CENTER, mGroup, Anchor.BOTTOM_CENTER, 0, 8);
        }

        public void createPerfectIndicator() {
            Image image = new Image(mAssets.getTextureAtlas().findRegion("ui/perfect"));
            image.setScale(0.4f);
            mGroup.addRule(image, Anchor.BOTTOM_CENTER, mGroup, Anchor.BOTTOM_CENTER, 0, -14);
            image.setZIndex(0);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (isDisabled()) {
                float width = mLock.getRegionWidth();
                float height= mLock.getRegionHeight();
                float posX = getX() + (getWidth() - width * mLockScale) / 2;
                float posY = getY() + (getHeight() - height * mLockScale) / 2;
                batch.draw(mLock, posX, posY, 0, 0, width, height, mLockScale, mLockScale, 0);
            }
        }

        private AnchorGroup mGroup;
        public float mLockScale;

        public int levelWorldIndex;
        public int levelIndex;
    }

    private Actor createLevelButton(Level level, float size, float starScale, float lockScale) {
        LevelButton button = new LevelButton(getGame().getAssets(), mLevelWorld.getIndex(), level.getIndex());
        button.mLockScale = lockScale;
        button.setSize(size, size);

        AnchorGroup group = new AnchorGroup();
        group.addRule(button, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT);
        if (level.isLocked()) {
            button.setDisabled(true);
        } else {
            button.createStars(level.getStarCount(), starScale);
            if (level.isPerfect()) {
                button.createPerfectIndicator();
            }
        }
        if (level.hasBrandNewItem()) {
            createSurpriseImage(group);
        }

        button.addListener(getGame().getAssets().getClickListener());
        button.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                LevelButton button = (LevelButton)actor;
                getGame().startLevel(button.levelWorldIndex, button.levelIndex);
            }
        });
        return group;
    }

    private static class PendulumAction extends TemporalAction {
        private float mStart;
        private float mAmplitude;

        protected void begin () {
            mStart = actor.getRotation();
        }

        protected void update (float percent) {
            float angle = mAmplitude / 2 * MathUtils.sin(percent * MathUtils.PI) * MathUtils.sin(percent * MathUtils.PI2 * 5);
            actor.setRotation(mStart + angle);
        }

        public void setAmplitude(float amplitude) {
            mAmplitude = amplitude;
        }
    }

    private void createSurpriseImage(AnchorGroup group) {
        Image image = new Image(mSurpriseRegion);
        image.setTouchable(Touchable.disabled);
        image.setOrigin(mSurpriseRegion.getRegionWidth() / 2f, mSurpriseRegion.getRegionHeight() - 2f);
        group.addRule(image, Anchor.TOP_RIGHT, group, Anchor.CENTER_RIGHT, -17 + image.getOriginX(), 0);
        float variation = MathUtils.random(0.9f, 1.1f);

        PendulumAction pendulumAction = new PendulumAction();
        pendulumAction.setAmplitude(SURPRISE_ROTATE_ANGLE);
        pendulumAction.setDuration(SURPRISE_ROTATE_DURATION * variation * 10);
        image.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.delay(MathUtils.random(0f, 2f)),
                    pendulumAction,
                    Actions.delay(MathUtils.random(1f, 2f))
                )
            )
        );
    }

    @Override
    public void onBackPressed() {
        getGame().showWorldListScreen();
    }
}
