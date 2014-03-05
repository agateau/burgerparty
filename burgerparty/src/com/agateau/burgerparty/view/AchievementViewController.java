package com.agateau.burgerparty.view;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.Achievement;
import com.agateau.burgerparty.utils.StageScreen;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class AchievementViewController {
	private static final float SLIDE_DURATION = 0.2f;
	private static final float STAY_DURATION = 2;

	private BurgerPartyGame mGame;
	private Array<AchievementSmallView> mViews = new Array<AchievementSmallView>();

	public AchievementViewController(BurgerPartyGame game) {
		mGame = game;
	}

	public void show(Achievement achievement) {
		AchievementSmallView view = new AchievementSmallView(mGame.getAssets(), achievement);
		mViews.add(view);
		if (mViews.size == 1) {
			showNextView();
		}
	}

	private void showNextView() {
		if (mViews.size == 0) {
			return;
		}
		StageScreen screen = (StageScreen)mGame.getScreen();
		assert(screen != null);
		Stage stage = screen.getStage();
		final AchievementSmallView view = mViews.removeIndex(0);

		screen.addNotificationActor(view);
		view.setPosition((stage.getWidth() - view.getWidth()) / 2, stage.getHeight());
		float viewHeight = view.getHeight();// - AchievementSmallView.PADDING;
		view.addAction(
			Actions.sequence(
				Actions.moveBy(0, -viewHeight, SLIDE_DURATION, Interpolation.pow2Out),
				Actions.delay(STAY_DURATION),
				Actions.moveBy(0, viewHeight, SLIDE_DURATION, Interpolation.pow2In),
				Actions.run(new Runnable() {
					@Override
					public void run() {
						showNextView();
					}
				}),
				Actions.removeActor()
			)
		);
	}
}
