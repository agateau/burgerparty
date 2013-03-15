package com.agateau.burgerparty.utils;

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

	// A version of Actor.localToStageCoordinates which works with scaled actors 
	static private Vector2 localToStageCoordinates(Actor actor, Vector2 pos) {
		while (actor != null) {
			pos.x = actor.getX() + pos.x * actor.getScaleX();
			pos.y = actor.getY() + pos.y * actor.getScaleY();
			actor = actor.getParent();
		}
		return pos;
	}

	private void applyRule(Rule rule) {
		// Compute reference position
		Vector2 referencePos = new Vector2(
			rule.reference.getWidth() * rule.referenceAnchor.hPercent,
			rule.reference.getHeight() * rule.referenceAnchor.vPercent);

		Vector2 stagePos = localToStageCoordinates(rule.reference, referencePos);

		// Apply space
		stagePos.add(rule.hSpace * mSpacing, rule.vSpace * mSpacing);

		// Position target (use target parent because setPosition() works in parent coordinates)
		Actor targetParent = rule.target.getParent();
		if (targetParent == null) {
			return;
		}
		Vector2 targetPos = targetParent.stageToLocalCoordinates(stagePos);

		// Apply target offset
		targetPos.add(
			-rule.target.getWidth() * rule.target.getScaleX() * rule.targetAnchor.hPercent,
			-rule.target.getHeight() * rule.target.getScaleY() * rule.targetAnchor.vPercent);

		rule.target.setPosition(targetPos.x, targetPos.y);
		//Gdx.app.log("applyRule", rule.target.toString());
	}

	public void layout() {
		for(Rule rule: mRules) {
			applyRule(rule);
		}
	}
}
