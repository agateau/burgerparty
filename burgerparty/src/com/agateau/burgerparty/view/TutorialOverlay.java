package com.agateau.burgerparty.view;

import com.agateau.burgerparty.Assets;
import com.agateau.burgerparty.BurgerPartyGame;
import com.agateau.burgerparty.Kernel;
import com.agateau.burgerparty.model.Burger;
import com.agateau.burgerparty.model.BurgerGenerator;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.model.Inventory;
import com.agateau.burgerparty.model.MealExtra;
import com.agateau.burgerparty.model.MealItemDb;
import com.agateau.burgerparty.utils.Anchor;
import com.agateau.burgerparty.utils.AnchorGroup;
import com.agateau.burgerparty.utils.Overlay;
import com.agateau.burgerparty.utils.TimeLineAction;
import com.agateau.burgerparty.utils.UiUtils;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

public class TutorialOverlay extends Overlay {
    private static final Color EMPTY_INVENTORY_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);
    private static final Color WHITE_TRANSPARENT = new Color(1, 1, 1, 0);
    private static final float SCALE = 0.6f;

    private final WorldView mWorldView;
    private final Image mBgImage;
    private final TextureAtlas mAtlas;
    private InventoryView mInventoryView;
    private AnchorGroup mTutorialGroup;
    private BurgerPartyGame mGame;
    private Bubble mBubble;
    private BurgerGenerator mBurgerGenerator;
    private Burger mTargetBurger;
    private Burger mBurger;
    private MealView mMealView;
    private TimeLineAction mTimeLineAction;
    private TutorialIndicatorController mTutorialController;
    private InventoryView mEmptyInventoryView;
    private Image mCustomer;

    public TutorialOverlay(WorldView worldView, BurgerPartyGame game) {
        super(game.getAssets().getTextureAtlas());
        mWorldView = worldView;
        mGame = game;
        mAtlas = game.getAssets().getTextureAtlas();

        ImageButton skipButton = Kernel.createRoundButton(game.getAssets(), "ui/icon-next");
        skipButton.addListener(new ChangeListener() {
            public void changed(ChangeListener.ChangeEvent Event, Actor actor) {
                mWorldView.resume();
            }
        });

        NinePatch framePatch = mAtlas.createPatch("ui/frame");
        Image frameImage = new Image(framePatch);
        mBgImage = new Image(mAtlas.findRegion("tutorial/bg"));
        frameImage.setSize(
            MathUtils.ceil(mBgImage.getWidth() * SCALE) + framePatch.getPadLeft() + framePatch.getPadRight(),
            MathUtils.ceil(mBgImage.getHeight() * SCALE) + framePatch.getPadTop() + framePatch.getPadBottom()
            );
        setupEmptyInventoryView();
        setupInventoryView();
        setupTargetMealView();
        setupCustomer();

        mTutorialController = new TutorialIndicatorController(mGame, mTargetBurger, mInventoryView);

        mTutorialGroup = new AnchorGroup();
        mTutorialGroup.setScale(SCALE);
        mTutorialGroup.setSize(mBgImage.getWidth(), mBgImage.getHeight());

        AnchorGroup group = new AnchorGroup();
        addActor(group);
        group.setFillParent(true);
        group.setSpacing(UiUtils.SPACING);

        group.addRule(frameImage, Anchor.CENTER, group, Anchor.CENTER, 0, 0);
        group.addRule(mTutorialGroup, Anchor.CENTER, frameImage, Anchor.CENTER, 0, 0);
        group.addRule(skipButton, Anchor.CENTER_RIGHT, frameImage, Anchor.BOTTOM_RIGHT, -1, 0);

        mTutorialGroup.addRule(mBgImage, Anchor.BOTTOM_LEFT, mTutorialGroup, Anchor.BOTTOM_LEFT);
        mTutorialGroup.addRule(mCustomer, Anchor.BOTTOM_CENTER, mTutorialGroup, Anchor.TOP_CENTER, 0, -222 * SCALE);
        mTutorialGroup.addRule(mInventoryView, Anchor.BOTTOM_LEFT, mTutorialGroup, Anchor.BOTTOM_LEFT);
        mTutorialGroup.addRule(mEmptyInventoryView, Anchor.BOTTOM_LEFT, mTutorialGroup, Anchor.BOTTOM_LEFT);
        mTutorialGroup.addRule(mBubble, Anchor.BOTTOM_LEFT, mTutorialGroup, Anchor.CENTER, 50 * SCALE, 45 * SCALE);

        addActor(mTutorialController.getIndicator());

        setupTimeLine();
    }

    @Override
    public void onBackPressed() {
        mWorldView.resume();
    }

    private void setupCustomer() {
        mCustomer = new Image(mAtlas.findRegion("tutorial/customer"));
    }

    private void setupEmptyInventoryView() {
        Inventory inventory = new Inventory();
        mEmptyInventoryView = new InventoryView(mAtlas);
        mEmptyInventoryView.setWorldDirName("levels/1/");
        mEmptyInventoryView.setInventory(inventory);
        mEmptyInventoryView.setSize(800, 180);
        mEmptyInventoryView.setColor(EMPTY_INVENTORY_COLOR);
    }

    private void setupInventoryView() {
        MealItemDb db = MealItemDb.getInstance();
        Array<BurgerItem> lst = new Array<BurgerItem>();
        lst.add(db.getBurgerItem("bottom"));
        lst.add(db.getBurgerItem("top"));
        lst.add(db.getBurgerItem("meat"));
        lst.add(db.getBurgerItem("tomato"));
        lst.add(db.getBurgerItem("salad"));
        lst.add(db.getBurgerItem("cheese"));

        Inventory inventory = new Inventory();
        inventory.setItems(lst);

        mBurgerGenerator = new BurgerGenerator(0, lst);

        mInventoryView = new InventoryView(mAtlas);
        mInventoryView.setWorldDirName("levels/1/");
        mInventoryView.setInventory(inventory);
        mInventoryView.setSize(800, 180);
    }

    private void setupTargetMealView() {
        mTargetBurger = new Burger();
        MealExtra extra = new MealExtra();

        mBubble = new Bubble(mAtlas.createPatch("ui/bubble-callout-left"));

        Assets assets = mGame.getAssets();
        MealView targetMealView = new MealView(mTargetBurger, extra, mAtlas, assets.getSoundAtlas(), assets.getAnimScriptLoader(), false);
        targetMealView.getBurgerView().setPadding(WorldView.TARGET_BURGER_PADDING);

        MealViewScrollPane scrollPane = new MealViewScrollPane(targetMealView, mAtlas);
        scrollPane.setScale(0.6f, 0.6f);

        mBubble.setChild(scrollPane);
    }

    private void setupMealView() {
        mBurger = new Burger();
        MealExtra extra = new MealExtra();
        Assets assets = mGame.getAssets();
        mMealView = new MealView(mBurger, extra, mAtlas, assets.getSoundAtlas(), assets.getAnimScriptLoader(), true);
        mTutorialController.setMealView(mMealView);
    }

    private void setupTimeLine() {
        // Reset state
        mCustomer.setColor(WHITE_TRANSPARENT);
        mBubble.setColor(WHITE_TRANSPARENT);
        mTutorialController.getIndicator().setColor(WHITE_TRANSPARENT);

        mTargetBurger.setItems(mBurgerGenerator.run(5));
        mTargetBurger.resetArrow();

        setupMealView();
        mMealView.setColor(WHITE_TRANSPARENT);
        mTutorialGroup.addRule(mMealView, Anchor.BOTTOM_CENTER, mInventoryView, Anchor.TOP_CENTER, 0, 10);
        mTutorialGroup.layout(); // Move all actors back to their starting places

        mTimeLineAction = new TimeLineAction();

        mTimeLineAction.addActionRelative(0.5f, mCustomer, Actions.alpha(1, 0.3f));
        mTimeLineAction.addActionRelative(1f, mBubble, Actions.alpha(1, 0.3f));
        mTimeLineAction.addActionRelative(1f, mMealView, Actions.alpha(1, 0.3f));
        mTimeLineAction.addActionRelative(0.5f, mEmptyInventoryView, Actions.alpha(0, 0.3f));

        // Create burger
        for (int x = 0; x < mTargetBurger.getItems().size(); ++x) {
            mTimeLineAction.addActionRelative(1, this, createUpdateIndicatorAction());
            if (x == 0) {
                mTimeLineAction.addActionRelative(0, mTutorialController.getIndicator(), Actions.alpha(1, 0.3f));
            }
        }
        mTimeLineAction.addActionRelative(1, mTutorialController.getIndicator(), Actions.alpha(0, 0.3f));

        // Move customer and meal away
        mTimeLineAction.addActionRelative(0.5f, mBubble, Actions.alpha(0, 0.3f));
        mTimeLineAction.addActionRelative(0.5f, mCustomer, Actions.moveBy(300, 0, 0.3f, Interpolation.pow2In));
        mTimeLineAction.addActionRelative(0, mCustomer, Actions.alpha(0, 0.3f));
        mTimeLineAction.addActionRelative(0, mMealView, Actions.moveBy(300, 0, 0.3f, Interpolation.pow2In));
        mTimeLineAction.addActionRelative(0, mMealView, Actions.alpha(0, 0.3f));

        mTimeLineAction.addActionRelative(0.5f, mEmptyInventoryView, Actions.color(EMPTY_INVENTORY_COLOR, 0.3f));

        // Restart animation
        mTimeLineAction.addActionRelative(1.5f, this, Actions.run(new Runnable() {
            @Override
            public void run() {
                setupTimeLine();
            }
        }));

        addAction(mTimeLineAction);
    }

    private Action createUpdateIndicatorAction() {
        return Actions.run(new Runnable() {
            @Override
            public void run() {
                mTutorialController.updateIndicator();
            }
        });
    }
}
