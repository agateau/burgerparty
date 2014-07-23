package com.agateau.burgerparty.model;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class UniverseLoader {
    private static final float SEC_PER_ITEM = 0.75f;
    private static final int TIME_STEP = 5;
    private static final boolean DEBUG_DURATION = false;
    private static final float MIN_EASINESS = 1;
    private static final float MAX_EASINESS = 2;

    private FileHandle mCsvHandle;
    private Writer mCsvWriter;

    public void run(Universe universe) {
        if (DEBUG_DURATION) {
            mCsvHandle = Gdx.files.external("/tmp/duration.csv");
            mCsvWriter = mCsvHandle.writer(false);
        }
        for (int n=1;; n++) {
            String dirName = "levels/" + n + "/";
            if (!Gdx.files.internal(dirName + "1.xml").exists()) {
                break;
            }
            NLog.d("dir=%s", dirName);
            LevelWorld world = loadWorld(n - 1, dirName);
            universe.addWorld(world);
        }
        if (DEBUG_DURATION) {
            try {
                mCsvWriter.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        initNewItemFields(universe.getWorlds());
    }

    private LevelWorld loadWorld(int index, String dirName) {
        LevelWorld world = new LevelWorld(index, dirName);
        for (int n=1; n <= LevelWorld.LEVEL_PER_WORLD; n++) {
            String name = dirName + "/" + n + ".xml";
            FileHandle levelFile = Gdx.files.internal(name);
            assert(levelFile.exists());
            Level level = Level.fromXml(world, n - 1, levelFile);
            world.addLevel(level);
            initDuration(index, n - 1, level);
        }
        return world;
    }

    private void initDuration(int worldIndex, int levelIndex, Level level) {
        /*
         * normLevelIndex goes from 0 to 1 between level 1.1 and level 3.LEVEL_PER_WORLD
         * easiness starts at MIN_EASINESS and tends to MAX_EASINESS
         */
        float normLevelIndex = (worldIndex * LevelWorld.LEVEL_PER_WORLD + levelIndex) / (3f * LevelWorld.LEVEL_PER_WORLD - 1f);
        float easiness = MIN_EASINESS + (MAX_EASINESS - MIN_EASINESS) * (float)Math.pow(1 - normLevelIndex, 4f);
        int itemCount = level.definition.getTotalItemCount();
        int duration = roundUp(itemCount * SEC_PER_ITEM * easiness);
        level.definition.duration = duration;
        if (DEBUG_DURATION) {
            NLog.d(" world=" + (worldIndex + 1)
                  + " level=" + (levelIndex + 1)
                  + " normLevelIndex=" + normLevelIndex
                  + " easiness=" + easiness
                  + " itemCount=" + itemCount
                  + " duration=" + level.definition.duration
                 );
            try {
                mCsvWriter.write(itemCount + "\n");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void initNewItemFields(Array<LevelWorld> worlds) {
        assert(worlds.size > 0);
        Level level1 = worlds.get(0).getLevel(0);
        Set<MealItem> knownItems = level1.getKnownItems();
        Set<String> knownItemNames = new HashSet<String>();
        for (MealItem item: knownItems) {
            knownItemNames.add(item.getName());
        }
        for (LevelWorld world: worlds) {
            for (int idx = 0, n = world.getLevelCount(); idx < n; ++idx) {
                Level level = world.getLevel(idx);
                level.initNewItemField(knownItemNames);
            }
        }
    }

    private static int roundUp(float x) {
        return MathUtils.ceil(x / (float)TIME_STEP) * TIME_STEP;
    }
}
