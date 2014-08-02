package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.model.Level;
import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.view.WorldView;

public class GameScreen extends BurgerPartyScreen {
    private World mWorld;
    private WorldView mWorldView;
    private boolean mStarted = false;

    public GameScreen(BurgerPartyGame game, Level level) {
        super(game);
        mWorld = new World(game.getGameStats(), level);
        mWorldView = new WorldView(this, game, mWorld);
        if (level.getLevelWorld().getIndex() == 0 && level.getIndex() == 0) {
            mWorldView.showTutorial();
        }
        getStage().addActor(mWorldView);
    }

    @Override
    public void show() {
        super.show();
        if (!mStarted) {
            mWorld.start();
            mStarted = true;
        }
    }

    @Override
    public void pause() {
        mWorldView.pause();
    }

    @Override
    public void onBackPressed() {
        mWorldView.onBackPressed();
    }
}
