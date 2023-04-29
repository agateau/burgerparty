package com.agateau.burgerparty;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;

public class PackerMain {
    public static void main(String[] args) {
        packTextures();
    }

    private static void packTextures() {
        TexturePacker.Settings settings = new TexturePacker.Settings();
        settings.filterMin = TextureFilter.Linear;
        settings.filterMag = TextureFilter.Linear;
        settings.pot = false;
        settings.combineSubdirectories = true;
        TexturePacker.process(settings, "../core/assets", "../android/assets", "burgerparty");
        System.out.println("Done");
    }
}
