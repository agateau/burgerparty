package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.model.LevelResult;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.MetaAction;
import com.agateau.burgerparty.utils.NLog;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

import static com.greenyetilab.linguaj.Translator.tr;

public class LevelFinishedOverlay extends Overlay {
    private static final float CONSUME_SECONDS_INTERVAL = 0.03f;
    private static final float STAR_ANIM_DURATION = 0.3f;

    private BurgerPartyGame mGame;
    private int mScore;
    private Array<TextureRegionDrawable> mStarTextures = new Array<TextureRegionDrawable>();
    private Array<Image> mStarImages = new Array<Image>();
    private Label mScoreLabel;
    private AnchorGroup mStarGroup;
    @SuppressWarnings("unused")
    private AchievementsButtonController mAchievementsButtonController;

    private final int mLevelWorldIndex;
    private final int mLevelIndex;

    class ConsumeSecondsAction extends Action {

        private int mRemainingSeconds;
        private final Sound mSound;
        private float mWaitDelta = 0;

        public ConsumeSecondsAction(int secs) {
            mRemainingSeconds = secs;
            mSound = mGame.getAssets().getSoundAtlas().findSound("time-bonus");
        }

        @Override
        public boolean act(float delta) {
            mWaitDelta -= delta;
            if (mWaitDelta > 0) {
                return false;
            }
            if (mRemainingSeconds <= 0) {
                return true;
            }
            mWaitDelta = CONSUME_SECONDS_INTERVAL;
            mSound.play(0.1f);
            mScore += Constants.SCORE_BONUS_PER_REMAINING_SECOND;
            mScoreLabel.setText(String.valueOf(mScore));
            --mRemainingSeconds;
            return false;
        }
    }

    class LightUpStarAction extends MetaAction {
        public LightUpStarAction(Overlay parent, int index) {
            mIndex = index;
            mImage = new Image(mStarTextures.get(1));
            mReferenceImage = mStarImages.get(index);
            parent.addActor(mImage);
            mImage.setVisible(false);
            mImage.setOrigin(mImage.getWidth() / 2, mImage.getHeight() / 2);
        }

        @Override
        protected void setup() {
            mImage.setVisible(true);
            Vector2 pos = mReferenceImage.localToAscendantCoordinates(getParent(), new Vector2(0, 0));
            mImage.setPosition(pos.x, pos.y);
            mImage.setColor(1, 1, 1, 0);
            mImage.setScale(10);
            mImage.setRotation(-72);
            mImage.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveBy(0, 30),
                        Actions.moveBy(0, -30, STAR_ANIM_DURATION),
                        Actions.scaleTo(1, 1, STAR_ANIM_DURATION, Interpolation.pow3In),
                        Actions.rotateTo(0, STAR_ANIM_DURATION),
                        Actions.alpha(1, STAR_ANIM_DURATION, Interpolation.pow5In)
                    ),
                    mGame.getAssets().getSoundAtlas().createPlayAction("star", 1 + mIndex * 0.05f),
                    createDoneAction()
                )
            );
            Drawable texture = mStarTextures.get(1);
            mImage.setDrawable(texture);
        }

        @Override
        protected void abort() {
            mImage.clearActions();
        }

        private Image mImage;
        private Image mReferenceImage;
        private int mIndex;
    }

    class HighScoreAction extends MetaAction {
        public HighScoreAction(Overlay parent) {
            mLabel = new Label(tr("New High Score!"), mGame.getAssets().getSkin(), "new-highscore");
            parent.addActor(mLabel);
            mLabel.setVisible(false);
        }
        @Override
        protected void setup() {
            mLabel.setVisible(true);
            float screenWidth = mLabel.getParent().getWidth();
            float finalX = mScoreLabel.getRight() + UiUtils.SPACING;
            float finalY = mScoreLabel.getY() - 2;
            mLabel.setPosition(screenWidth, finalY);
            mLabel.addAction(
                Actions.sequence(
                    Actions.moveTo(finalX, finalY, 1, Interpolation.bounceOut),
                    createDoneAction()
                )
            );
        }

        @Override
        protected void abort() {
            mLabel.clearActions();
        }

        Label mLabel;
    }

    class PerfectAction extends MetaAction {
        private static final float ANIM_DURATION = 0.5f;
        public PerfectAction(Overlay parent) {
            mImage = new Image(mGame.getAssets().getTextureAtlas().findRegion("ui/perfect"));
            parent.addActor(mImage);
            mImage.setOrigin(mImage.getWidth() / 2, mImage.getHeight() / 2);
            mImage.setColor(1, 1, 1, 0);
            mImage.setZIndex(0);
        }
        @Override
        protected void setup() {
            float finalX = (getStage().getWidth() - mImage.getWidth()) / 2;
            float finalY = mStarGroup.getY() - mImage.getHeight() + 12;
            mImage.setPosition(finalX, finalY - 20);
            mImage.addAction(
                Actions.sequence(
                    Actions.parallel(
                        Actions.moveTo(finalX, finalY, ANIM_DURATION, Interpolation.pow2Out),
                        Actions.fadeIn(ANIM_DURATION, Interpolation.pow2Out)
                    ),
                    createDoneAction()
                )
            );
            mImage.addAction(mGame.getAssets().getSoundAtlas().createPlayAction("perfect"));
        }
        @Override
        protected void abort() {
            mImage.clearActions();
        }
        private Image mImage;
    }

    public LevelFinishedOverlay(BurgerPartyGame game, LevelResult levelResult, TextureAtlas atlas, Skin skin) {
        super(atlas);
        mGame = game;

        // Store level indexes to make sure two consecutive calls to doGoToNextLevel() do not end up skipping a level
        mLevelWorldIndex = mGame.getLevelWorldIndex();
        mLevelIndex = mGame.getLevelIndex();
        NLog.i("level: %d-%d", mLevelWorldIndex + 1, mLevelIndex + 1);

        int previousScore = levelResult.getLevel().getScore();
        mScore = levelResult.getScore();
        boolean perfect = levelResult.getCoinCount() == levelResult.getMaximumCoinCount();
        int remainingSeconds = levelResult.getRemainingSeconds();
        int finalScore = mScore + Constants.SCORE_BONUS_PER_REMAINING_SECOND * remainingSeconds;
        int starCount = Math.min(levelResult.getCoinCount() / levelResult.getStarCost(), 3);

        // Store final score *now*
        mGame.getCurrentUniverse().updateLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex(), finalScore, starCount, perfect);

        mStarTextures.add(new TextureRegionDrawable(atlas.findRegion("ui/star-off-big")));
        mStarTextures.add(new TextureRegionDrawable(atlas.findRegion("ui/star-on-big")));
        setupWidgets(skin);

        SequenceAction sequence = Actions.sequence();
        sequence.addAction(new ConsumeSecondsAction(remainingSeconds));
        for (int i = 0; i < starCount; ++i) {
            sequence.addAction(new LightUpStarAction(this, i));
        }
        if (finalScore > previousScore) {
            sequence.addAction(new HighScoreAction(this));
        }
        if (perfect) {
            sequence.addAction(new PerfectAction(this));
        }
        addAction(sequence);
        mGame.getAssets().getSoundAtlas().findSound("finished").play();
    }

    private void setupWidgets(Skin skin) {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(mGame.getAssets());
        builder.build(FileUtils.assets("screens/levelfinishedoverlay.gdxui"));
        AnchorGroup root = builder.getActor("root");
        root.setFillParent(true);
        addActor(root);

        mScoreLabel = builder.<Label>getActor("scoreLabel");
        mScoreLabel.setText(String.valueOf(mScore));
        UiUtils.adjustToPrefSize(mScoreLabel);

        builder.<ImageButton>getActor("selectLevelButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.showLevelListScreen(mGame.getLevelWorldIndex());
            }
        });

        builder.<ImageButton>getActor("restartButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mGame.startLevel(mGame.getLevelWorldIndex(), mGame.getLevelIndex());
            }
        });

        // main label and next button
        ImageButton nextButton = builder.<ImageButton>getActor("nextButton");

        int levelWorldIndex = mGame.getLevelWorldIndex();
        int levelIndex = mGame.getLevelIndex();
        LevelWorld levelWorld = mGame.getCurrentUniverse().get(levelWorldIndex);
        Label mainLabel = builder.<Label>getActor("mainLabel");
        if (levelIndex < levelWorld.getLevelCount() - 1) {
            mainLabel.setText(tr("Congratulations, you finished level %d-%d!", levelWorldIndex + 1, levelIndex + 1));
        } else if (levelWorldIndex < mGame.getCurrentUniverse().getWorlds().size - 1) {
            mainLabel.setText(tr("Congratulations, you finished world %d!", levelWorldIndex + 1));
        } else {
            mainLabel.setText(tr("Congratulations, you finished the game!"));
        }
        UiUtils.adjustToPrefSize(mainLabel);
        nextButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                goToNextLevel();
            }
        });

        mStarGroup = builder.<AnchorGroup>getActor("starGroup");
        for (int i = 0; i < 3; ++i) {
            mStarImages.add(builder.<Image>getActor("star" + String.valueOf(i)));
        }

        mAchievementsButtonController = new AchievementsButtonController(builder.<ImageButton>getActor("achievementsButton"), mGame);
    }

    private void goToNextLevel() {
        LevelWorld levelWorld = mGame.getCurrentUniverse().get(mLevelWorldIndex);
        if (mLevelIndex < levelWorld.getLevelCount() - 1) {
            mGame.startLevel(mLevelWorldIndex, mLevelIndex + 1);
        } else if (mLevelWorldIndex < mGame.getCurrentUniverse().getWorlds().size - 1) {
            mGame.showNewWorldScreen(mLevelWorldIndex + 1);
        } else {
            mGame.showEndScreen();
        }
    }

    @Override
    public void aboutToBeRemoved() {
        clearActions();
    }

    @Override
    public void onBackPressed() {
        clearActions();
        mGame.showLevelListScreen(mGame.getLevelWorldIndex());
    }
}
