package com.agateau.burgerparty;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;

public class PackerMain {
    public static void main(String[] args) {
        packTextures();
    }

    private static void packTextures() {
        Settings settings = new Settings();
        settings.filterMin = TextureFilter.Linear;
        settings.filterMag = TextureFilter.Linear;
        settings.pot = false;
        settings.combineSubdirectories = true;
        TexturePacker2.process(settings, "../core/assets", "../android/assets", "burgerparty");
        System.out.println("Done");
    }
}
