package com.agateau.burgerparty.model;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

import com.agateau.burgerparty.utils.NLog;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class UniverseLoader {
	private static final float SEC_PER_ITEM = 1.3f;
	private static final int TIME_STEP = 30;
	private static final boolean DEBUG_DURATION = false;

	private static NLog log;
	private FileHandle mCsvHandle;
	private Writer mCsvWriter;

	public UniverseLoader() {
		if (log == null) {
			log = NLog.getRoot().create("UniverseLoader");
		}
	}

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
			log.i("run: dir=%s", dirName);
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
			log.i("loadWorld: levelFile=%s", levelFile);
			Level level = Level.fromXml(world, n - 1, levelFile);
			world.addLevel(level);
			initDuration(index, n - 1, level);
		}
		return world;
	}

	private void initDuration(int worldIndex, int levelIndex, Level level) {
		/*
		 * normLevelIndex goes from 0 to 1 between level 1.1 and level 3.LEVEL_PER_WORLD
		 * easiness starts at 3 and tends to 1
		 */
		float normLevelIndex = (worldIndex * LevelWorld.LEVEL_PER_WORLD + levelIndex) / (3f * LevelWorld.LEVEL_PER_WORLD);
		if (normLevelIndex > 1) {
			normLevelIndex = 1;
		}
		int itemCount = level.definition.getTotalItemCount();
		float easiness = 3f - 2f * (float)Math.pow(normLevelIndex, 0.5f);
		int duration = roundUp(itemCount * SEC_PER_ITEM * easiness);
		level.definition.duration = duration;
		if (DEBUG_DURATION) {
			log.d("initDuration: "
					+ " world=" + (worldIndex + 1)
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
		for (LevelWorld world: worlds) {
			for(int idx = 0, n = world.getLevelCount(); idx < n; ++idx) {
				Level level = world.getLevel(idx);
				level.initNewItemField(knownItems);
			}
		}
	}

	private static int roundUp(float x) {
		return MathUtils.ceil(x / (float)TIME_STEP) * TIME_STEP;
	}
}
