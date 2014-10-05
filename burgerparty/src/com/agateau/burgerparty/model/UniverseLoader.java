package com.agateau.burgerparty.model;

import java.util.HashSet;
import java.util.Set;

import com.agateau.burgerparty.Constants;
import com.agateau.burgerparty.utils.CsvWriter;
import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class UniverseLoader {
    private static final boolean DEBUG_DURATION = false;
    private CsvWriter mCsvWriter;

    public void run(Universe universe) {
        if (DEBUG_DURATION) {
            FileHandle handle = Gdx.files.external("/tmp/duration.dat");
            mCsvWriter = new CsvWriter(handle);
            mCsvWriter.setSeparator(' ');
            mCsvWriter.write("# SLOPES[0]", Constants.SLOPES[0]);
            mCsvWriter.write("# SLOPES[1]", Constants.SLOPES[1]);
            mCsvWriter.write("# SLOPES[2]", Constants.SLOPES[2]);
            mCsvWriter.write("# SECOND_PER_MEALITEM", Constants.SECOND_PER_MEALITEM);
            mCsvWriter.write("# level", "itemCount", "duration", "durationPerItem");
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
            mCsvWriter.close();
        }
        initNewItemFields(universe.getWorlds());
    }

    private LevelWorld loadWorld(int index, String dirName) {
        LevelWorld world = new LevelWorld(index, dirName);
        for (int n=1; n <= Constants.LEVEL_PER_WORLD; n++) {
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
        float normLevelIndex = levelIndex / (Constants.LEVEL_PER_WORLD - 1f);
        int itemCount = level.definition.getTotalItemCount();
        float easiness = Constants.STARTS[worldIndex] + Constants.SLOPES[worldIndex] * normLevelIndex;
        int duration = (int)(itemCount * Constants.SECOND_PER_MEALITEM * easiness);
        level.definition.duration = duration;
        if (DEBUG_DURATION) {
            mCsvWriter.write((worldIndex + 1) * 100 + levelIndex + 1, itemCount, duration, (float)duration / itemCount);
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
}
