package com.agateau.burgerparty.view;

import java.util.HashSet;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.screens.AchievementsScreen;
import com.agateau.burgerparty.utils.AchievementManager;
import com.agateau.burgerparty.utils.Signal0;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

/**
 * Controls an ImageButton, indicates when new achievements have been unlocked
 * and shows the AchievementsScreen when the button is clicked
 */
public class AchievementsButtonController {
    private AchievementManager mManager;
    private HashSet<Object> mHandlers = new HashSet<Object>();
    private AchievementsButtonIndicator mIndicator;

    private static final float SCALE = 0.5f;

    public AchievementsButtonController(ImageButton button, final BurgerPartyGame game) {
        mManager = game.getGameStats().manager;
        mIndicator = new AchievementsButtonIndicator(game.getAssets());
        mIndicator.setScale(SCALE);
        button.addActor(mIndicator);
        final int OFFSET = 8;
        mIndicator.setPosition(
            -OFFSET,
            button.getHeight() - mIndicator.getHeight() * SCALE + OFFSET
        );

        button.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                AchievementsScreen screen = new AchievementsScreen(game);
                screen.setReturnScreen(game.getScreen());
                game.setScreen(screen);
            }
        });
        update();
        mManager.changed.connect(mHandlers, new Signal0.Handler() {
            @Override
            public void handle() {
                update();
            }
        });
    }

    private void update() {
        mIndicator.setVisible(mManager.hasUnseenAchievements());
    }
}
