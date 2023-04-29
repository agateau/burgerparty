package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.utils.Signal0;

import static com.greenyetilab.linguaj.Translator.tr;

public class AllStarsAchievement extends Achievement {
    private final Set<Object> mHandler = new HashSet<Object>();
    private final LevelWorld mWorld;
    private final Difficulty mDifficulty;

    public AllStarsAchievement(Universe universe, int index) {
        super(String.format("all-stars%s-%d", universe.getDifficulty().suffix, (index + 1)),
                tr("All Stars #%d", index + 1),
                tr("Finish all levels of world %d with 3 stars.", index + 1)
                );
        mWorld = universe.get(index);
        mDifficulty = universe.getDifficulty();
        universe.starCount.changed.connect(mHandler, new Signal0.Handler() {
            @Override
            public void handle() {
                update();
            }
        });
        setAlreadyUnlocked(mWorld.getWonStarCount() == mWorld.getTotalStarCount());
    }

    @Override
    public String getIconName() {
        return "all-stars";
    }

    @Override
    public boolean isValidForDifficulty(Difficulty difficulty) {
        return difficulty == mDifficulty;
    }

    private void update() {
        if (mWorld.getWonStarCount() == mWorld.getTotalStarCount()) {
            unlock();
        }
    }
}
