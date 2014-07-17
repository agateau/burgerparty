package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.screens.AchievementsScreen;
import com.agateau.burgerparty.utils.AchievementManager;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Controls an ImageButton, indicates when new achievements have been unlocked
 * and shows the AchievementsScreen when the button is clicked
 */
public class AchievementsButtonIndicator extends Image {
    private AchievementManager mManager;
    private HashSet<Object> mHandlers = new HashSet<Object>();

    static public void setupButton(ImageButton button, final BurgerPartyGame game) {
        AchievementsButtonIndicator indicator = new AchievementsButtonIndicator(game);
        button.addActor(indicator);

        button.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                AchievementsScreen screen = new AchievementsScreen(game);
                screen.setReturnScreen(game.getScreen());
                game.setScreen(screen);
            }
        });
    }

    public AchievementsButtonIndicator(final BurgerPartyGame game) {
        super(game.getAssets().getTextureAtlas().findRegion("ui/star-on"));
        mManager = game.getGameStats().manager;

        setOrigin(getWidth() / 2, getHeight() / 2);
        addAction(Actions.forever(Actions.rotateBy(360, 5)));

        update();
        mManager.changed.connect(mHandlers, new Signal0.Handler() {
            @Override
            public void handle() {
                update();
            }
        });
    }

    public void update() {
        setVisible(mManager.hasUnseenAchievements());
    }
}
