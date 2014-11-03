package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.utils.FboGroup;
import com.agateau.burgerparty.utils.FileUtils;
import com.agateau.burgerparty.utils.RefreshHelper;
import com.agateau.burgerparty.utils.UiBuilder;
import com.agateau.burgerparty.view.BurgerPartyUiBuilder;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
        assert(rootElement != null);
        BurgerPartyUiBuilder builder = new BurgerPartyUiBuilder(getGame().getAssets());

        float width = getStage().getWidth();
        float height = getStage().getHeight();
        for (XmlReader.Element viewElement: rootElement.getChildrenByName("view")) {
            String type = viewElement.getAttribute("type");
            Actor view = null;
            if (type.equals("gdxui")) {
                view = createGdxuiView(builder, viewElement);
            } else if (type.equals("flying")) {
                view = createFlyingView(viewElement);
            } else {
                throw new RuntimeException("Unknown view type '" + type + "'");
            }
            assert(view != null);
            XmlReader.Element soundElement = viewElement.getChildByName("sound");
            if (soundElement != null) {
                view.addAction(createSoundAction(soundElement));
            }
            mViews.add(createContainerForView(view, width, height));
        }
        goToNextView();
    }

    private Action createSoundAction(XmlReader.Element soundElement) {
        String soundName = soundElement.getAttribute("name");
        Action action = getGame().getAssets().getSoundAtlas().createPlayAction(soundName);
        return Actions.delay(ANIM_DURATION, action);
    }

    private Actor createFlyingView(XmlReader.Element element) {
        return new FlyingView(this, element, mWorldIndex);
    }

    private Actor createContainerForView(Actor view, float width, float height) {
        final float shadowWidth = 8;
        final float shadowAlpha = 0.6f;

        Image shadow = new Image(getTextureAtlas().findRegion("ui/white-pixel"));
        shadow.setColor(0, 0, 0, shadowAlpha);
        shadow.setSize(shadowWidth, height);
        shadow.setX(width);

        Image shadow2 = new Image(getTextureAtlas().findRegion("ui/white-pixel"));
        shadow2.setColor(0, 0, 0, shadowAlpha);
        shadow2.setSize(shadowWidth / 2, height);
        shadow2.setX(width);

        view.setSize(width, height);

        Group container = new FboGroup();
        container.addActor(shadow);
        container.addActor(shadow2);
        container.addActor(view);
        container.setSize(width, height);
        return container;
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
            // Fade in from behind
            newView.toBack();
            newView.setColor(1, 1, 1, 0);
            newView.addAction(Actions.alpha(1, ANIM_DURATION, Interpolation.pow3In));
        }

        Actor oldView = mCurrentViewIndex >= 1 ? mViews.get(mCurrentViewIndex - 1) : null;
        if (oldView != null) {
            SequenceAction oldViewAction = Actions.sequence();
            if (nextAction == null) {
                // Not last view, scroll out to the left
                oldViewAction.addAction(Actions.moveBy(-oldView.getWidth(), 0, ANIM_DURATION, Interpolation.pow3In));
            } else {
                // Last view, fade out
                oldViewAction.addAction(Actions.alpha(0, ANIM_DURATION, Interpolation.pow3In));
                oldViewAction.addAction(nextAction);
            }
            oldViewAction.addAction(Actions.removeActor());
            oldView.addAction(oldViewAction);
        }
    }
}
