package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.view.SandBoxGameView;

public class SandBoxGameScreen extends BurgerPartyScreen {
    private SandBoxGameView mSandBoxGameView;

    public SandBoxGameScreen(BurgerPartyGame game) {
        super(game);
        mSandBoxGameView = new SandBoxGameView(this, game);
        getStage().addActor(mSandBoxGameView);

        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().initMealItemDb();
            }
        };
    }

    @Override
    public void onBackPressed() {
        mSandBoxGameView.onBackPressed();
    }
}
