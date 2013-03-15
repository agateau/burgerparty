package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class AnchorGroup extends WidgetGroup {
	private float mSpacing;
	class Rule {
		public Actor target;
		public Anchor targetAnchor;
		public Actor reference;
		public Anchor referenceAnchor;
		public float hSpace;
		public float vSpace;
	}
	private Array<Rule> mRules = new Array<Rule>();

	public void setSpacing(float spacing) {
		mSpacing = spacing;
	}

	public float getSpacing() {
		return mSpacing;
	}

	public void moveActor(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor) {
		moveActor(target, targetAnchor, reference, referenceAnchor, 0, 0);
	}

	public void moveActor(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor, float hSpace, float vSpace) {
		Rule rule = new Rule();
		rule.target = target;
		rule.targetAnchor = targetAnchor;
		rule.reference = reference;
		rule.referenceAnchor = referenceAnchor;
		rule.hSpace = hSpace;
		rule.vSpace = vSpace;
		mRules.add(rule);
		addActor(target);
	}

	private void applyRule(Rule rule) {
		// Compute reference position
		Vector2 referencePos = new Vector2(
			rule.reference.getWidth() * rule.referenceAnchor.hPercent,
			rule.reference.getHeight() * rule.referenceAnchor.vPercent);

		Vector2 stagePos = rule.reference.localToStageCoordinates(referencePos);

		// Apply space
		stagePos.add(rule.hSpace * mSpacing, rule.vSpace * mSpacing);

		// Apply target offset
		stagePos.add(
			-rule.target.getWidth() * rule.targetAnchor.hPercent,
			-rule.target.getHeight() * rule.targetAnchor.vPercent);

		// Position target (use target reference because setPosition() is in reference coordinates)
		Vector2 targetPos = rule.target.getParent().stageToLocalCoordinates(stagePos);
		rule.target.setPosition(targetPos.x, targetPos.y);
		Gdx.app.log("applyRule", rule.target.toString());
	}

	public void layout() {
		for(Rule rule: mRules) {
			applyRule(rule);
		}
	}
}
