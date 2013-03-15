package com.agateau.burgerparty.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class AnchorLayout {
	private float mSpacing;

	public void setSpacing(float spacing) {
		mSpacing = spacing;
	}

	public float getSpacing() {
		return mSpacing;
	}

	public void moveActor(Actor child, Anchor childAnchor, Actor parent, Anchor parentAnchor) {
		moveActor(child, childAnchor, parent, parentAnchor, 0, 0);
	}

	public void moveActor(Actor child, Anchor childAnchor, Actor parent, Anchor parentAnchor, float hSpace, float vSpace) {
		// Compute parent position
		Vector2 parentPos = new Vector2(
			parent.getWidth() * parentAnchor.hPercent,
			parent.getHeight() * parentAnchor.vPercent);

		Vector2 stagePos = parent.localToStageCoordinates(parentPos);

		// Apply space
		stagePos.add(hSpace * mSpacing, vSpace * mSpacing);

		// Apply child offset
		stagePos.add(-child.getWidth() * childAnchor.hPercent, -child.getHeight() * childAnchor.vPercent);

		// Position child (use child parent because setPosition() is in parent coordinates)
		Vector2 childPos = child.getParent().stageToLocalCoordinates(stagePos);
		child.setPosition(childPos.x, childPos.y);
	}
}
