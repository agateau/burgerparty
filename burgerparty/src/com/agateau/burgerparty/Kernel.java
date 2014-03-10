package com.agateau.burgerparty;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

public class Kernel {
    public static ImageButton createRoundButton(Assets assets, String name) {
        ImageButton button = new ImageButton(assets.getSkin(), "round-button");
        button.getImage().setDrawable(assets.getSkin().getDrawable(name));
        button.addListener(assets.getClickListener());
        return button;
    }
}
