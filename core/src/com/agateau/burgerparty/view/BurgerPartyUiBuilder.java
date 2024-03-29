package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.utils.UiBuilder;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.XmlReader;

public class BurgerPartyUiBuilder extends UiBuilder {
    private final Assets mAssets;

    public BurgerPartyUiBuilder(Assets assets) {
        super(assets.getTextureAtlas(), assets.getSkin());
        setAnimScriptloader(assets.getAnimScriptLoader());
        mAssets = assets;
    }

    protected ImageButton createImageButton(XmlReader.Element element) {
        ImageButton button = super.createImageButton(element);
        String soundName = element.getAttribute("sound", "");

        ChangeListener listener;
        if (soundName.isEmpty()) {
            listener = mAssets.getClickListener();
        } else {
            final Sound sound = mAssets.getSoundAtlas().findSound(soundName);
            listener = new ChangeListener() {
                public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                    sound.play();
                }
            };
        };
        button.addListener(listener);

        return button;
    }

    public static ImageButton createRoundButton(Assets assets, String name) {
        return createRoundButton(assets, name, "default");
    }

    public static ImageButton createRoundButton(Assets assets, String name, String styleName) {
        ImageButton.ImageButtonStyle style =
                new ImageButton.ImageButtonStyle(
                        assets.getSkin().get(styleName, ImageButton.ImageButtonStyle.class));
        style.imageUp = assets.getSkin().getDrawable(name);
        return new ImageButton(style);
    }
}
