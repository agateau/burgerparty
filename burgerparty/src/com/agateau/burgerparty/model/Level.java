package com.agateau.burgerparty.model;

import java.io.IOException;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.Random;
import java.util.Set;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;

public class Level {
    private enum Status {
        LOCKED,
        NEW,
        PLAYED
    }
    private static class CustomerDefinition {
        public String type;
        public int burgerSize;
        public Customer create() {
            return new Customer(type, burgerSize);
        }
    }
    public static class Definition {
        public int duration;
        private Array<CustomerDefinition> mCustomerDefinitions = new Array<CustomerDefinition>();
        private Array<BurgerItem> mBurgerItems = new Array<BurgerItem>();
        private Array<MealItem> mExtraItems = new Array<MealItem>();
        private MealItem mNewItem = null;

        public Array<BurgerItem> getBurgerItems() {
            return mBurgerItems;
        }

        public Array<MealItem> getExtraItems() {
            return mExtraItems;
        }

        public MealItem getNewItem() {
            return mNewItem;
        }

        public Array<Customer> createCustomers() {
            Array<Customer> lst = new Array<Customer>();
            for (CustomerDefinition def: mCustomerDefinitions) {
                lst.add(def.create());
            }
            return lst;
        }

        public int getTotalItemCount() {
            int size = 0;
            for (CustomerDefinition def: mCustomerDefinitions) {
                size += def.burgerSize;
            }
            return size;
        }
    }

    private LevelWorld mLevelWorld;
    private String mFileName;
    private int mIndex;

    private Status mStatus = Status.LOCKED;
    private int mScore = 0;
    private int mStarCount = 0;
    private boolean mPerfect = false;

    public Level(LevelWorld world, String fileName) {
        mLevelWorld = world;
        mFileName = fileName;
    }

    public Definition definition = new Definition();

    public void setStarCount(int value) {
        mStatus = Status.PLAYED;
        mStarCount = value;
    }

    public int getStarCount() {
        if (mStatus == Status.PLAYED) {
            return mStarCount;
        } else {
            return 0;
        }
    }

    public boolean isPerfect() {
        return mStatus == Status.PLAYED && mPerfect;
    }

    public boolean hasBrandNewItem() {
        return mStatus != Status.PLAYED && definition.mNewItem != null;
    }

    public LevelWorld getLevelWorld() {
        return mLevelWorld;
    }

    public static Level fromXml(LevelWorld levelWorld, int levelIndex, FileHandle handle) {
        int worldIndex = levelWorld.getIndex();
        XmlReader reader = new XmlReader();
        XmlReader.Element root = null;
        try {
            root = reader.parse(handle);
        } catch (IOException e) {
            throw new MissingResourceException("Failed to load level from " + handle.path() + ". Exception: " + e.toString() + ".", "Level", handle.path());
        }
        if (root == null) {
            throw new MissingResourceException("Failed to load level from " + handle.path() + ". No root element.", "Level", handle.path());
        }
        Level level = new Level(levelWorld, handle.path());
        level.mIndex = levelIndex;
        int burgerSize = root.getIntAttribute("burgerSize");

        Array<MealItem> lst = MealItemDb.getInstance().getItemsForLevel(worldIndex, levelIndex);
        for (MealItem item: lst) {
            if (item.getType() == MealItem.Type.BURGER) {
                level.definition.mBurgerItems.add((BurgerItem)item);
            } else {
                level.definition.mExtraItems.add(item);
            }
        }

        // Use a deterministic seed to generate random burger size to ensure level difficulty remains the same between plays
        Random random = new Random(worldIndex * 1000 + levelIndex);
        // No less than 4 burger items: top + bottom + 2 middle
        int minBurgerSize = Math.max(burgerSize / 2, 4);
        int burgerSizeDelta = burgerSize - minBurgerSize + 1;

        XmlReader.Element elements = root.getChildByName("customers");
        assert(elements != null);
        for (int idx = 0; idx < elements.getChildCount(); ++idx) {
            XmlReader.Element element = elements.getChild(idx);
            CustomerDefinition def = new CustomerDefinition();
            def.type = element.getAttribute("type");
            def.burgerSize = element.getIntAttribute("burgerSize", 0);
            if (def.burgerSize == 0) {
                // Init burger size for customers whose burger size has not been explicitly set
                def.burgerSize = minBurgerSize + random.nextInt(burgerSizeDelta);
            }
            level.definition.mCustomerDefinitions.add(def);
        }

        return level;
    }

    public Set<MealItem> getKnownItems() {
        Set<MealItem> set = new HashSet<MealItem>();
        for (BurgerItem item: definition.mBurgerItems) {
            set.add(item);
        }
        for (MealItem item: definition.mExtraItems) {
            set.add(item);
        }
        return set;
    }

    public void initNewItemField(Set<String> knownItemNames) {
        initNewItemFieldInternal(knownItemNames, definition.mBurgerItems);
        initNewItemFieldInternal(knownItemNames, definition.mExtraItems);
    }

    public int getIndex() {
        return mIndex;
    }

    public boolean isLocked() {
        return mStatus == Status.LOCKED;
    }

    public boolean isNew() {
        return mStatus == Status.NEW;
    }

    public boolean hasBeenPlayed() {
        return mStatus == Status.PLAYED;
    }

    public int getScore() {
        return mStatus == Status.PLAYED ? mScore : 0;
    }

    public void lock() {
        mStatus = Status.LOCKED;
    }

    public void unlock() {
        if (mStatus == Status.LOCKED) {
            mStatus = Status.NEW;
        }
    }

    public void setScore(int value) {
        mStatus = Status.PLAYED;
        mScore = value;
    }

    public void markPerfect() {
        mPerfect = true;
    }

    private void initNewItemFieldInternal(Set<String> knownItemNames, Array<? extends MealItem> list) {
        for (MealItem item: list) {
            if (knownItemNames.contains(item.getName())) {
                continue;
            }
            if (definition.mNewItem == null) {
                definition.mNewItem = item;
                knownItemNames.add(item.getName());
            } else {
                throw new RuntimeException("Error in level defined in " + mFileName + ". Found new item '" + item + "', but there is already a new item: '" + definition.mNewItem + "'");
            }
        }
    }
}