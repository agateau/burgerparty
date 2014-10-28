package com.agateau.burgerparty.screens;


import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.Universe;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

public class CheatScreen extends BurgerPartyScreen {
    private Array<Label> mWorldLabels = new Array<Label>();
    private Array<Universe> mUniverses = new Array<Universe>();
    private Array<TextButton> mDifficultyButtons = new Array<TextButton>();
    private int mCurrentDifficultyIdx = -1;

    public CheatScreen(BurgerPartyGame game) {
        super(game);
        for (Universe universe: getGame().getUniverses()) {
            mUniverses.add(universe);
        }
        Image bgImage = new Image(getTextureAtlas().findRegion("ui/menu-bg"));
        setBackgroundActor(bgImage);
        setupWidgets();
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().showCheatScreen();
                dispose();
            }
        };
    }

    private void setupWidgets() {
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());
        builder.build(FileUtils.assets("screens/cheat.gdxui"));
        AnchorGroup root = builder.getActor("root");
        getStage().addActor(root);
        root.setFillParent(true);

        builder.<ImageButton>getActor("backButton").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                onBackPressed();
            }
        });

        builder.<TextButton>getActor("reset").addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                reset();
            }
        });

        // Init world buttons
        for (int idx = 1; idx <= 3; ++idx) {
            Label label = builder.<Label>getActor(String.format("world-%d-label", idx));
            mWorldLabels.add(label);
            final int worldIndex = idx - 1;
            for (int star = 0; star <= 3; ++star) {
                final int fstar = star;
                builder.<TextButton>getActor("stars-" +idx + "-" + star).addListener(new ChangeListener() {
                    public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                        setStars(worldIndex, fstar);
                    }
                });
            }
        }

        // Init difficulty buttons
        for (int idx = 0; idx < 3; ++idx) {
            final TextButton button = builder.<TextButton>getActor(String.format("difficulty-%d", idx));
            mDifficultyButtons.add(button);
            final int fidx = idx;
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (button.isChecked()) {
                        setCurrentDifficultyIdx(fidx);
                    }
                }
            });
        }

        setCurrentDifficultyIdx(0);
        mDifficultyButtons.get(0).setChecked(true);
    }

    private void setCurrentDifficultyIdx(int idx) {
        if (idx == mCurrentDifficultyIdx) {
            return;
        }
        if (mCurrentDifficultyIdx != -1) {
            mDifficultyButtons.get(mCurrentDifficultyIdx).setChecked(false);
        }
        mCurrentDifficultyIdx = idx;
        for (int worldIndex = 0; worldIndex < Constants.WORLD_COUNT; ++worldIndex) {
            updateWorldLabel(worldIndex);
        }
    }

    private void updateWorldLabel(int worldIndex) {
        Universe universe = mUniverses.get(mCurrentDifficultyIdx);
        int stars = universe.getWorlds().get(worldIndex).getWonStarCount();
        String text = String.format("World %d: %d", worldIndex + 1, stars);
        mWorldLabels.get(worldIndex).setText(text);
    }

    @Override
    public void onBackPressed() {
        getGame().showStartScreen();
    }

    private void reset() {
        Universe universe = mUniverses.get(mCurrentDifficultyIdx);
        for (LevelWorld world: universe.getWorlds()) {
            for (Level level: world.getLevels()) {
                level.lock();
            }
            updateWorldLabel(world.getIndex());
        }
        universe.get(0).getLevel(0).unlock();
        universe.updateStarCount();
        universe.saveProgress();
    }

    private void setStars(int worldIndex, int stars) {
        Universe universe = mUniverses.get(mCurrentDifficultyIdx);
        LevelWorld world = universe.get(worldIndex);
        for (Level level: world.getLevels()) {
            level.unlock();
            if (level.getStarCount() < stars) {
                level.setStarCount(stars);
                level.setScore(stars * 200);
            }
        }
        universe.updateStarCount();
        universe.saveProgress();
        updateWorldLabel(worldIndex);
    }
}
