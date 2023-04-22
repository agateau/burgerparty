package com.agateau.burgerparty.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class ShaderActor extends Image {
    private ShaderProgram mShader;

    public ShaderActor(TextureRegion region) {
        super(region);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!mShader.isCompiled()) {
            return;
        }
        mShader.begin();
        applyShaderParameters(mShader, parentAlpha);
        mShader.end();
        // Unless pedantic is set to false, ShaderProgram will throw an exception if our shader does not make use of all the uniforms it sets.
        // Since not all shaders have a use of all the uniforms, we set pedantic to false.
        ShaderProgram.pedantic = false;
        batch.setShader(mShader);
        super.draw(batch, parentAlpha);
        batch.setShader(null);
    }

    protected void applyShaderParameters(ShaderProgram shader, float parentAlpha) {
    }

    public void setShader(ShaderProgram shader) {
        mShader = shader;
        if (!mShader.isCompiled()) {
            Gdx.app.error("ShaderActor", "Shader did not compile:\n" + mShader.getLog());
        }
    }
}
