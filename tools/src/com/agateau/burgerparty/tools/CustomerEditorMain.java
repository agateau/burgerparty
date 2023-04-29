package com.agateau.burgerparty.tools;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class CustomerEditorMain {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "burgerparty";
        cfg.width = 800;
        cfg.height = 480;

        String partsXml = args[0];
        new LwjglApplication(new CustomerEditorGame(partsXml), cfg);
    }

}
