package com.agateau.burgerparty.utils;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.utils.Array;

public class Animation {
	public void createActions(Actor actor, float width, float height, float duration) {
		for (String line: mLines) {
			String tokens[] = line.split(" ");
			assert(tokens.length > 0);
			String cmd = tokens[0];
			Action action = null;
			if (cmd.equals("moveTo")) {
				assert(tokens.length == 3 || tokens.length == 4);
				float x = Float.parseFloat(tokens[1]) * width;
				float y = Float.parseFloat(tokens[2]) * height;
				MoveToAction act = Actions.moveTo(x, y);
				if (tokens.length == 4) {
					act.setDuration(Float.parseFloat(tokens[3]) * duration);
				}
				action = act;
			} else if (cmd.equals("moveBy")) {
				assert(tokens.length == 3 || tokens.length == 4);
				float x = Float.parseFloat(tokens[1]) * width;
				float y = Float.parseFloat(tokens[2]) * height;
				MoveByAction act = Actions.moveBy(x, y);
				if (tokens.length == 4) {
					act.setDuration(Float.parseFloat(tokens[3]) * duration);
				}
				action = act;
			} else if (cmd.equals("rotateTo")) {
				assert(tokens.length == 2 || tokens.length == 3);
				float v = Float.parseFloat(tokens[1]) * width;
				RotateToAction act = Actions.rotateTo(v);
				if (tokens.length == 3) {
					act.setDuration(Float.parseFloat(tokens[2]) * duration);
				}
				action = act;
			} else if (cmd.equals("scaleTo")) {
				assert(tokens.length == 3 || tokens.length == 4);
				float x = Float.parseFloat(tokens[1]);
				float y = Float.parseFloat(tokens[2]);
				ScaleToAction act = Actions.scaleTo(x, y);
				if (tokens.length == 4) {
					act.setDuration(Float.parseFloat(tokens[3]) * duration);
				}
				action = act;
			} else {
				throw new RuntimeException("Unknown action `"  + cmd + "` in line `" + line + "`");
			}
			assert(action != null);
			actor.addAction(action);
		}
	}

	static public Animation fromString(String definition) {
		Animation anim = new Animation();
		for (String line: definition.split("\n")) {
			line = line.replaceAll("^ *", "");
			if (line.startsWith("#")) {
				continue;
			}
			if (line.isEmpty()) {
				continue;
			}
			anim.mLines.add(line);
		}
		return anim;
	}

	private Array<String> mLines = new Array<String>();
}
