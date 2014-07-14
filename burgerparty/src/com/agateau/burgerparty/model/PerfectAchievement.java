package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.Signal0;

import static com.greenyetilab.linguaj.Translator.tr;

public class PerfectAchievement extends Achievement {
    private final Set<Object> mHandler = new HashSet<Object>();
    private final LevelWorld mWorld;

    public PerfectAchievement(Universe universe, int index) {
        super("perfect-" + (index + 1),
                tr("Perfect #%d", index + 1),
                tr("Get a perfect in all levels of world %d.", index + 1)
                );
        mWorld = universe.get(index);
        universe.saveRequested.connect(mHandler, new Signal0.Handler() {
            @Override
            public void handle() {
                update();
            }
        });
        setAlreadyUnlocked(allPerfect());
    }

    @Override
    public String getIconName() {
        return "perfect";
    }

    private void update() {
        if (isUnlocked()) {
            return;
        }
        if (allPerfect()) {
            unlock();
        }
    }

    private boolean allPerfect() {
        for (Level level: mWorld.getLevels()) {
            if (!level.isPerfect()) {
                return false;
            }
        }
        return true;
    }
}
