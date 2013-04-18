package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class AnimScriptLoader {
	public AnimScript load(String definition) {
		Reader reader = new StringReader(definition);
		try {
			return load(reader);
		} catch (IOException e) {
			Gdx.app.error("AnimScript", "Failed to parse `" + definition + "`");
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public AnimScript load(Reader reader) throws IOException {
		AnimScript anim = new AnimScript();
		StreamTokenizer tokenizer = new StreamTokenizer(reader);
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
			assert(cmd != null);
			InstructionDefinition def = mInstructionDefinitionMap.get(cmd);
			Instruction instruction = def.parse(tokenizer);
			anim.addInstruction(instruction);
		} while (tokenizer.ttype != StreamTokenizer.TT_EOF);
		return anim;
	}

	public void registerAction(String name, ArgumentDefinition<?>... types) {
		Class<?> actionsClass = Actions.class;
		Class<?> args[] = new Class<?>[types.length];
		for (int idx = 0; idx < types.length; ++idx) {
			args[idx] = types[idx].javaType;
		}
		Method method;
		try {
			method = actionsClass.getDeclaredMethod(name, args);
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			throw new RuntimeException();
		} catch (SecurityException e1) {
			e1.printStackTrace();
			throw new RuntimeException();
		}
		mInstructionDefinitionMap.put(name, new InstructionDefinition(method, types));
	}

	public static AnimScriptLoader getInstance() {
		if (sInstance == null) {
			sInstance = new AnimScriptLoader();
			sInstance.initMap();
		}
		return sInstance;
	}

	private void initMap() {
		registerAction("moveTo",
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Width),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Height),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Duration, 0)
		);
		registerAction("moveBy",
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Width),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Height),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Duration, 0)
		);
		registerAction("rotateTo",
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Scalar),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Duration, 0)
		);
		registerAction("scaleTo",
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Scalar),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Scalar),
				new FloatArgumentDefinition(FloatArgumentDefinition.Domain.Duration, 0)
		);
	}

	private Map<String, InstructionDefinition> mInstructionDefinitionMap = new HashMap<String, InstructionDefinition>();

	private static AnimScriptLoader sInstance = null;
}
