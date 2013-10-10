package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.utils.Signal1;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class NextBurgerItemArrow extends Image {
	public static final float OVERALL_WIDTH = 45;

	public NextBurgerItemArrow(BurgerView burgerView) {
		super(Kernel.getTextureAtlas().findRegion("ui/icon-next-item"));
		mBurgerView = burgerView;
		initImage();

		burgerView.getBurger().arrowIndexChanged.connect(mHandlers, new Signal1.Handler<Integer>() {
			@Override
			public void handle(Integer index) {
				setArrowIndex(index);
			}
		});
	}

	private void initImage() {
		float bounceWidth = OVERALL_WIDTH - getWidth();
		addAction(Actions.forever(
			Actions.sequence(
				Actions.moveBy(bounceWidth, 0, .3f, Interpolation.pow2In),
				Actions.moveBy(-bounceWidth, 0, .3f, Interpolation.pow2Out)
			)
		));
		setX(0);
		setVisible(false);
	}

	private void setArrowIndex(int index) {
		if (index == -1) {
			setVisible(false);
			return;
		}
		setVisible(true);
		Actor item = mBurgerView.getItemAt(index);
		float deltaY = (item.getY() + (item.getHeight() - getHeight()) / 2) - getY();
		addAction(Actions.moveBy(0, deltaY, 0.3f, Interpolation.pow3Out));
	}

	private HashSet<Object> mHandlers = new HashSet<Object>();
	private BurgerView mBurgerView;
}
