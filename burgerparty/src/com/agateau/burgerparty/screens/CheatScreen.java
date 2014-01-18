package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.LevelWorld;
import com.agateau.burgerparty.model.Universe;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class CheatScreen extends BurgerPartyScreen {
	public CheatScreen(BurgerPartyGame game) {
		super(game);
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

		for (int idx = 1; idx <= 3; ++idx) {
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
	}

	@Override
	public void onBackPressed() {
		getGame().showStartScreen();
	}

	private void reset() {
		Universe universe = getGame().getUniverse();
		for (LevelWorld world: universe.getWorlds()) {
			for(Level level: world.getLevels()) {
				level.lock();
			}
		}
		universe.get(0).getLevel(0).unlock();
	}

	private void setStars(int worldIndex, int stars) {
		Universe universe = getGame().getUniverse();
		LevelWorld world = universe.get(worldIndex);
		for(Level level: world.getLevels()) {
			if (stars == 0) {
				level.lock();
				level.unlock();
				continue;
			}
			if (level.getStarCount() < stars) {
				level.setStarCount(stars);
			}
		}
		universe.saveRequested.emit();
	}
}
