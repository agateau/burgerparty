package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * Controls an ImageButton, indicates when new achievements have been unlocked
 * and shows the AchievementsScreen when the button is clicked
 */
public class AchievementsButtonIndicator extends Group {
    private static final float MAX_SCALE = 1.2f;
    private static final float MAX_ROTATE = 30f;

    public AchievementsButtonIndicator(Assets assets) {
        TextureAtlas atlas = assets.getTextureAtlas();

        Image shadow = new Image(atlas.findRegion("ui/new-achievement-indicator"));
        addActor(shadow);
        shadow.setPosition(4, -4);
        shadow.setColor(new Color(0, 0, 0, 0.2f));

        Image image = new Image(atlas.findRegion("ui/new-achievement-indicator"));
        addActor(image);

        setSize(image.getWidth(), image.getHeight());

        createActions(shadow);
        createActions(image);
    }

    private void createActions(Image image) {
        image.setOrigin(getWidth() / 2, getHeight() / 2);
        image.setRotation(-MAX_ROTATE / 2);
        image.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.repeat(2,
                        Actions.sequence(
                            Actions.rotateBy(MAX_ROTATE, 2, Interpolation.pow2),
                            Actions.rotateBy(-MAX_ROTATE, 2, Interpolation.pow2)
                        )
                    )/*,
                    Actions.rotateBy(360 * 4 + MAX_ROTATE, 1.5f, Interpolation.circle),
                    Actions.rotateBy(-MAX_ROTATE, 2, Interpolation.pow2)*/
                )
            )
        );
        image.setScale(0);
        image.addAction(
            Actions.forever(
                Actions.sequence(
                    Actions.scaleTo(MAX_SCALE, MAX_SCALE, 0.4f, Interpolation.pow3),
                    Actions.scaleTo(1, 1, 0.4f, Interpolation.pow3In),
                    Actions.delay(4)
                )
            )
        );
    }
}
