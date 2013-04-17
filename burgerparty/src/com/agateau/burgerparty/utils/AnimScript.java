package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateToAction;
import com.badlogic.gdx.scenes.scene2d.actions.ScaleToAction;
import com.badlogic.gdx.scenes.scene2d.actions.TemporalAction;

public class AnimScript {
	public AnimScript(Reader reader) {
		mReader = reader;
	}

	static float readFloat(StreamTokenizer tokenizer) throws IOException {
		tokenizer.nextToken();
		assert(tokenizer.ttype == StreamTokenizer.TT_NUMBER);
		return (float)tokenizer.nval;
	}

	static void readDuration(StreamTokenizer tokenizer, TemporalAction action, float duration) throws IOException {
		if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
			action.setDuration((float)tokenizer.nval * duration);
		} else {
			tokenizer.pushBack();
		}
	}

	public void createActions(Actor actor, float width, float height, float duration) throws IOException {
		StreamTokenizer tokenizer = new StreamTokenizer(mReader);
		tokenizer.eolIsSignificant(true);
		tokenizer.slashSlashComments(true);
		tokenizer.slashStarComments(true);
		tokenizer.parseNumbers();
		do {
			while (tokenizer.nextToken() == StreamTokenizer.TT_EOL) {
			}
			if (tokenizer.ttype == StreamTokenizer.TT_EOF) {
				break;
			}
			assert(tokenizer.ttype == StreamTokenizer.TT_WORD);
			String cmd = tokenizer.sval;
			Action action = null;
			if (cmd.equals("moveTo")) {
				float x = readFloat(tokenizer) * width;
				float y = readFloat(tokenizer) * height;
				MoveToAction act = Actions.moveTo(x, y);
				readDuration(tokenizer, act, duration);
				action = act;
			} else if (cmd.equals("moveBy")) {
				float x = readFloat(tokenizer) * width;
				float y = readFloat(tokenizer) * height;
				MoveByAction act = Actions.moveBy(x, y);
				readDuration(tokenizer, act, duration);
				action = act;
			} else if (cmd.equals("rotateTo")) {
				float v = readFloat(tokenizer) * width;
				RotateToAction act = Actions.rotateTo(v);
				readDuration(tokenizer, act, duration);
				action = act;
			} else if (cmd.equals("scaleTo")) {
				float x = readFloat(tokenizer);
				float y = readFloat(tokenizer);
				ScaleToAction act = Actions.scaleTo(x, y);
				readDuration(tokenizer, act, duration);
				action = act;
			} else {
				throw new RuntimeException("Unknown action `"  + cmd + "`." + tokenizer.toString());
			}
			assert(action != null);
			actor.addAction(action);
		} while (tokenizer.ttype != StreamTokenizer.TT_EOF);
	}

	static public AnimScript fromString(String definition) {
		Reader reader = new StringReader(definition);
		AnimScript anim = new AnimScript(reader);
		return anim;
	}

	private Reader mReader;
}
