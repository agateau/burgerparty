package com.agateau.burgerparty.screen;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class LevelListScreen implements Screen {
	private BurgerPartyGame mGame;
	private Stage mStage = new Stage(0, 0, true);

	static private int COL_COUNT = 3;

	public LevelListScreen(BurgerPartyGame game, Skin skin) {
		mGame = game;
		setupWidgets(skin);
		Gdx.input.setInputProcessor(mStage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		mStage.act(delta);
		mStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		mStage.setViewport(width, height, true);
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	private static class GridGroup extends WidgetGroup {
		public void setColumnCount(int colCount) {
			if (mColCount == colCount) {
				return;
			}
			assert(colCount > 0);
			mColCount = colCount;
			invalidate();
		}

		public void setSpacing(float spacing) {
			if (mSpacing == spacing) {
				return;
			}
			mSpacing = spacing;
			invalidate();
		}

		public void addActor(Actor actor) {
			super.addActor(actor);
			mChildren.add(actor);
			invalidate();
		}

		public void layout() {
			float width = getWidth() + mSpacing;
			float cellSize = width / mColCount;
			float widgetSize = cellSize - mSpacing;
			float posX = 0;
			float posY = getHeight() - widgetSize;
			int col = 0;
			Gdx.app.log("GridGroup.layout", this.toString());
			for (Actor actor: mChildren) {
				actor.setBounds(posX, posY, widgetSize, widgetSize);
				Gdx.app.log("GridGroup.layout", actor.toString());
				col++;
				if (col < mColCount) {
					posX += cellSize;
				} else {
					col = 0;
					posX = 0;
					posY -= cellSize;
				}
			}
		}

		private int mColCount = 1;
		private float mSpacing = 0;
		private Array<Actor> mChildren = new Array<Actor>();
	}

	private void setupWidgets(Skin skin) {
		AnchorGroup group = new AnchorGroup();
		group.setSpacing(UiUtils.SPACING);
		mStage.addActor(group);
		group.setFillParent(true);

		TextButton backButton = new TextButton("<- Back", skin);
		backButton.setSize(backButton.getPrefWidth(), UiUtils.BUTTON_HEIGHT);
		group.moveActor(backButton, Anchor.TOP_LEFT, group, Anchor.TOP_LEFT, 1, -1);
		backButton.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				mGame.showMenu();
			}
		});

		GridGroup gridGroup = new GridGroup();
		gridGroup.setSpacing(UiUtils.SPACING);
		gridGroup.setColumnCount(COL_COUNT);
		group.moveActor(gridGroup, Anchor.TOP_LEFT, backButton, Anchor.BOTTOM_LEFT);
		gridGroup.setSize(800, 300);

		for (int idx=0; idx < mGame.getLevelCount(); idx++) {
			Actor levelButton = createLevelButton(idx, skin);
			gridGroup.addActor(levelButton);
		}
	}

	class LevelButton extends TextButton {
		public LevelButton(int idx, Skin skin) {
			super(String.valueOf(idx + 1), skin);
			this.idx = idx;
		}
		public int idx;
	}
	private Actor createLevelButton(int idx, Skin skin) {
		LevelButton button = new LevelButton(idx, skin);
		button.addListener(new ChangeListener() {
			public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
				LevelButton button = (LevelButton)actor;
				mGame.startLevel(button.idx);
			}
		});

		return button;
	}
}
