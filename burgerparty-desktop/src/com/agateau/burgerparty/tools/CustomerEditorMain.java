package com.agateau.burgerparty.tools;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class CustomerEditorMain {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "burgerparty";
		cfg.useGL20 = true;
		cfg.width = 800;
		cfg.height = 480;

		String partsXml = args[0];
		System.out.println("partsXml=" + partsXml);
		new LwjglApplication(new CustomerEditorGame(partsXml), cfg);
	}

}
