package com.agateau.burgerparty;

import com.agateau.burgerparty.utils.AnimScriptLoader;
import com.agateau.burgerparty.utils.MusicController;
import com.agateau.burgerparty.utils.SoundAtlas;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class Assets {
    private static final String MAIN_MUSIC = "music/burger-party_main-theme.mp3";

    private AnimScriptLoader mAnimScriptLoader = new AnimScriptLoader();
    private SoundAtlas mSoundAtlas;
    private TextureAtlas mTextureAtlas;
    private Skin mSkin;
    private Sound mClickSound;
    private Music mMusic;
    private ChangeListener mClickListener;
    private AssetManager mAssetManager;
    private ShaderProgram mDisabledShader = createDisabledShader();

    public Assets(MusicController musicController) {
        mAssetManager = new AssetManager();
        Texture.setAssetManager(mAssetManager);
        mAssetManager.load("burgerparty.atlas", TextureAtlas.class);

        mSoundAtlas = new SoundAtlas(mAssetManager, "sounds/", musicController);
        String[] names = {
            "add-item.wav",
            "add-item-bottom.wav",
            "add-item-cheese.wav",
            "add-item-coconut.wav",
            "add-item-onion.wav",
            "add-item-salad.wav",
            "add-item-steak.wav",
            "add-item-tomato.wav",
            "click.wav",
            "coin.wav",
            "error.wav",
            "finished.wav",
            "gameover.ogg",
            "jet.wav",
            "meal-done.wav",
            "sauce.wav",
            "splat.wav",
            "star.wav",
            "tick.wav",
            "time-bonus.wav",
            "trash.wav"
        };
        mSoundAtlas.preload(names);

        mAssetManager.load(MAIN_MUSIC, Music.class);
    }

    public void finishLoad() {
        if (mAssetManager.getQueuedAssets() > 0) {
            Gdx.app.error("Kernel", "Not all assets have been loaded yet, going to block (progress=" + mAssetManager.getProgress() + ")");
        }
        mTextureAtlas = mAssetManager.get("burgerparty.atlas");

        // Fix white-pixel to avoid fading borders
        TextureRegion region = mTextureAtlas.findRegion("ui/white-pixel");
        region.setRegionX(region.getRegionX() + 2);
        region.setRegionY(region.getRegionY() + 2);
        region.setRegionWidth(region.getRegionWidth() - 4);
        region.setRegionHeight(region.getRegionHeight() - 4);

        mSkin = new Skin(Gdx.files.internal("ui/skin.json"), mTextureAtlas);

        mSoundAtlas.finishLoad();

        mClickSound = mSoundAtlas.findSound("click");
        mClickListener = new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mClickSound.play();
            }
        };

        mMusic = mAssetManager.get(MAIN_MUSIC);
    }

    public TextureAtlas getTextureAtlas() {
        return mTextureAtlas;
    }

    public Skin getSkin() {
        return mSkin;
    }

    public SoundAtlas getSoundAtlas() {
        return mSoundAtlas;
    }

    public ChangeListener getClickListener() {
        return mClickListener;
    }

    public AnimScriptLoader getAnimScriptLoader() {
        return mAnimScriptLoader;
    }

    public AssetManager getAssetManager() {
        return mAssetManager;
    }

    public Music getMusic() {
        return mMusic;
    }

    public ShaderProgram getDisabledShader() {
        return mDisabledShader;
    }

    private static ShaderProgram createDisabledShader() {
        String vertexShader = "attribute vec4 "
                              + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                              + "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                              + "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                              + "uniform mat4 u_projTrans;\n"
                              + "varying vec4 v_color;\n"
                              + "varying vec2 v_texCoords;\n"
                              + "\n"
                              + "void main()\n"
                              + "{\n"
                              + " v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n"
                              + " v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n"
                              + " gl_Position = u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
                              + "}\n";
        String fragmentShader = "#ifdef GL_ES\n"
                                + "#define LOWP lowp\n"
                                + "precision mediump float;\n"
                                + "#else\n"
                                + "#define LOWP \n"
                                + "#endif\n"
                                + "varying LOWP vec4 v_color;\n"
                                + "varying vec2 v_texCoords;\n"
                                + "uniform sampler2D u_texture;\n"
                                + "float pump(float v) {"
                                + "  return pow(v, 0.4);"
                                + "}"
                                + "void main()\n"
                                + "{\n"
                                + "  vec4 c = v_color * texture2D(u_texture, v_texCoords);\n"
                                + "  float grey = (pump(c.r) + pump(c.g) + pump(c.b)) / 3.0;\n"
                                + "  gl_FragColor = vec4(grey, grey, grey, c.a);"
                                + "}";

        ShaderProgram shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            throw new IllegalArgumentException("Error compiling shader: " + shader.getLog());
        }
        return shader;
    }
}