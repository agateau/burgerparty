package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class AnimScript {
	public AnimScript(Reader reader) {
		mReader = reader;
	}

	private static class Context {
		float width;
		float height;
		float duration;
	}

	private static abstract class Argument {
		public abstract Class<?> getClassType();
		public abstract Object computeValue(Context context);
	}

	private static class FloatArgument extends Argument {
		public FloatArgumentDefinition.Domain domain;
		public float value;

		public FloatArgument(FloatArgumentDefinition.Domain d, float v) {
			domain = d;
			value = v;
		}

		@Override
		public Class<?> getClassType() {
			return Float.TYPE;
		}

		@Override
		public Object computeValue(Context context) {
			if (domain == FloatArgumentDefinition.Domain.Width) {
				return value * context.width;
			}
			if (domain == FloatArgumentDefinition.Domain.Height) {
				return value * context.height;
			}
			if (domain == FloatArgumentDefinition.Domain.Duration) {
				return value * context.duration;
			}
			return value;
		}
	}

	private static class InstructionDefinition {
		public InstructionDefinition(Method method, ArgumentDefinition<?>... argumentDefinitions) {
			mMethod = method;
			mArgumentDefinitions = argumentDefinitions;
		}

		public Instruction parse(StreamTokenizer tokenizer) {
			Argument[] args = new Argument[mArgumentDefinitions.length];
			for (int idx = 0; idx < mArgumentDefinitions.length; ++idx) {
				ArgumentDefinition<?> def = mArgumentDefinitions[idx];
				args[idx] = def.parse(tokenizer);
			}
			return new Instruction(mMethod, args);
		}

		private Method mMethod;
		private ArgumentDefinition<?>[] mArgumentDefinitions;
	}

	private static abstract class ArgumentDefinition<T> {
		public final Class<?> javaType;
		public final T defaultValue;

		ArgumentDefinition(Class<?> javaType, T defaultValue) {
			this.javaType = javaType;
			this.defaultValue = defaultValue;
		}

		public abstract Argument parse(StreamTokenizer tokenizer);
	}

	private static class FloatArgumentDefinition extends ArgumentDefinition<Float> {
		enum Domain {
			Width,
			Height,
			Duration,
			Scalar
		}

		public Domain domain;

		FloatArgumentDefinition(Domain domain) {
			super(Float.TYPE, null);
			this.domain = domain;
		}

		FloatArgumentDefinition(Domain domain, float defaultValue) {
			super(Float.TYPE, defaultValue);
			this.domain = domain;
		}

		@Override
		public Argument parse(StreamTokenizer tokenizer) {
			try {
				if (this.defaultValue == null) {
					return readFloat(tokenizer, this.domain);
				} else {
					return readFloat(tokenizer, this.domain, defaultValue);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		static FloatArgument readFloat(StreamTokenizer tokenizer, Domain domain) throws IOException {
			tokenizer.nextToken();
			assert(tokenizer.ttype == StreamTokenizer.TT_NUMBER);
			return new FloatArgument(domain, (float)tokenizer.nval);
		}

		static FloatArgument readFloat(StreamTokenizer tokenizer, Domain domain, float defaultValue) throws IOException {
			float value;
			if (tokenizer.nextToken() == StreamTokenizer.TT_NUMBER) {
				value = (float)tokenizer.nval;
			} else {
				tokenizer.pushBack();
				value = defaultValue;
			}
			return new FloatArgument(domain, value);
		}
	}

	private static class Instruction {
		public Instruction(Method method, Argument[] args) {
			mMethod = method;
			mArgs = args;
		}

		public Action run(Context context) {
			Object[] objectArgs = new Object[mArgs.length];
			for(int idx=0; idx < mArgs.length; ++idx) {
				Argument arg = mArgs[idx];
				objectArgs[idx] = arg.computeValue(context);
			}
			try {
				return (Action)mMethod.invoke(null, objectArgs);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new RuntimeException();
			}
		}

		Method mMethod;
		Argument[] mArgs;
	}

	public void createActions(Actor actor, float width, float height, float duration) throws IOException {
		Context context = new Context();
		context.width = width;
		context.height = height;
		context.duration = duration;

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
			assert(cmd != null);
			InstructionDefinition def = sInstructionDefinitionMap.get(cmd);
			Instruction instruction = def.parse(tokenizer);
			Action action = instruction.run(context);
			assert(action != null);
			actor.addAction(action);
			Gdx.app.log("createAction", "done");
		} while (tokenizer.ttype != StreamTokenizer.TT_EOF);
	}

	static public AnimScript fromString(String definition) {
		if (sInstructionDefinitionMap.isEmpty()) {
			initMap();
		}
		Reader reader = new StringReader(definition);
		AnimScript anim = new AnimScript(reader);
		return anim;
	}

	static private void initMap() {
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

	static private void registerAction(String name, ArgumentDefinition<?>... types) {
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
		sInstructionDefinitionMap.put(name, new InstructionDefinition(method, types));
	}

	private Reader mReader;

	private static Map<String, InstructionDefinition> sInstructionDefinitionMap = new HashMap<String, InstructionDefinition>();
}
