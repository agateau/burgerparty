package com.agateau.burgerparty.utils;

import java.util.Iterator;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.utils.Array;

public class AnchorGroup extends WidgetGroup {
	private float mSpacing;

	// A version of Actor.localToStageCoordinates which works with scaled actors
	static private Vector2 localToStageCoordinates(Actor actor, Vector2 pos) {
		while (actor != null) {
			pos.x = actor.getX() + pos.x * actor.getScaleX();
			pos.y = actor.getY() + pos.y * actor.getScaleY();
			actor = actor.getParent();
		}
		return pos;
	}

	class Rule implements AnchorRule {
		public Actor target;
		public Anchor targetAnchor;
		public Actor reference;
		public Anchor referenceAnchor;
		public float hSpace;
		public float vSpace;

		@Override
		public Actor getTarget() {
			return target;
		}

		@Override
		public void apply() {
			// Compute reference position
			Vector2 referencePos = new Vector2(
				reference.getWidth() * referenceAnchor.hPercent,
				reference.getHeight() * referenceAnchor.vPercent);

			Vector2 stagePos = localToStageCoordinates(reference, referencePos);

			// Apply space
			stagePos.add(hSpace * mSpacing, vSpace * mSpacing);

			// Position target (use target parent because setPosition() works in parent coordinates)
			Actor targetParent = target.getParent();
			if (targetParent == null) {
				return;
			}
			Vector2 targetPos = targetParent.stageToLocalCoordinates(stagePos);

			// Apply target offset
			targetPos.add(
				-target.getWidth() * target.getScaleX() * targetAnchor.hPercent,
				-target.getHeight() * target.getScaleY() * targetAnchor.vPercent);

			target.setPosition(targetPos.x, targetPos.y);
			//Gdx.app.log("applyRule", rule.target.toString());
		}
	}
	private Array<AnchorRule> mRules = new Array<AnchorRule>();

	public void setSpacing(float spacing) {
		mSpacing = spacing;
	}

	public float getSpacing() {
		return mSpacing;
	}

	public void addRule(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor) {
		addRule(target, targetAnchor, reference, referenceAnchor, 0, 0);
	}

	public void addRule(Actor target, Anchor targetAnchor, Actor reference, Anchor referenceAnchor, float hSpace, float vSpace) {
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

	public void removeRulesForActor(Actor actor) {
		Iterator<AnchorRule> it = mRules.iterator();
		for (; it.hasNext(); ) {
			AnchorRule rule = it.next();
			if (rule.getTarget() == actor) {
				it.remove();
			}
		}
	}

	public void layout() {
		for(AnchorRule rule: mRules) {
			rule.apply();
		}
	}
}
