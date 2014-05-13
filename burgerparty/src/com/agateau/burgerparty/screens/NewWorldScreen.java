package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.UiBuilder;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class NewWorldScreen extends BurgerPartyScreen {
    public static final float ANIM_DURATION = 1f;

    private final int mWorldIndex;
    private final Array<Actor> mViews = new Array<Actor>();

    private int mCurrentViewIndex = -1;

    public NewWorldScreen(BurgerPartyGame game, int worldIndex) {
        super(game);
        mWorldIndex = worldIndex;
        createRefreshHelper();
        loadXml();
    }

    @Override
    public void onBackPressed() {
        startNextLevel();
    }

    private void createRefreshHelper() {
        new RefreshHelper(getStage()) {
            @Override
            protected void refresh() {
                getGame().showNewWorldScreen(mWorldIndex);
            }
        };
    }

    private void startNextLevel() {
        getGame().startLevel(mWorldIndex, 0);
    }

    private void loadXml() {
        XmlReader.Element rootElement = FileUtils.parseXml(FileUtils.assets("levels/" + (mWorldIndex + 1) + "/newworld.xml"));
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());

        for (XmlReader.Element viewElement: rootElement.getChildrenByName("view")) {
            String type = viewElement.getAttribute("type");
            Actor view = null;
            if (type.equals("gdxui")) {
                view = createGdxuiView(builder, viewElement);
            } else if (type.equals("flying")) {
                view = new FlyingView(this, viewElement, mWorldIndex);
            } else {
                throw new RuntimeException("Unknown view type '" + type + "'");
            }
            assert(view != null);
            view.setSize(getStage().getWidth(), getStage().getHeight());
            mViews.add(view);
        }
        goToNextView();
    }

    private Actor createGdxuiView(UiBuilder builder, XmlReader.Element element) {
        float duration = element.getFloatAttribute("duration");

        XmlReader.Element uiElement = element.getChildByName("gdxui");
        Actor view = builder.build(uiElement);
        view.addAction(
            Actions.delay(duration,
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        goToNextView();
                    }
                })
            )
        );
        view.addAction(Actions.scaleBy(0.05f, 0.05f, duration + ANIM_DURATION, Interpolation.pow2In));
        view.setOrigin(view.getWidth() / 2, view.getHeight() / 2);
        return view;
    }

    public void goToNextView() {
        Action nextAction = null;
        ++mCurrentViewIndex;
        Actor newView = mCurrentViewIndex < mViews.size ? mViews.get(mCurrentViewIndex) : null;
        if (newView == null) {
            nextAction = Actions.run(new Runnable() {
                @Override
                public void run() {
                    startNextLevel();
                }
            });
        } else {
            getStage().addActor(newView);
            newView.toBack();
            newView.setColor(0, 0, 0, 0);
            newView.addAction(Actions.alpha(1, ANIM_DURATION));
        }

        Actor oldView = mCurrentViewIndex >= 1 ? mViews.get(mCurrentViewIndex - 1) : null;
        if (oldView != null) {
            SequenceAction oldViewAction = Actions.sequence(
                Actions.parallel(
                    Actions.alpha(0, ANIM_DURATION),
                    Actions.moveBy(-oldView.getWidth(), 0, ANIM_DURATION, Interpolation.pow4In)
                )
            );
            if (nextAction != null) {
                oldViewAction.addAction(nextAction);
            }
            oldViewAction.addAction(Actions.removeActor());
            oldView.addAction(oldViewAction);
        }
    }
}
