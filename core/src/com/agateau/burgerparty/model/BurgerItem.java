package com.agateau.burgerparty.model;

import com.badlogic.gdx.utils.XmlReader;

public class BurgerItem extends MealItem {
    private int mHeight = 0;
    private int mOffset = 0;
    private SubType mSubType;
    private String mBottomName;

    public static enum SubType {
        MIDDLE_MAIN,
        MIDDLE_OTHER,
        MIDDLE_SAUCE,
        TOP,
        BOTTOM,
        TOP_BOTTOM,
    }

    public BurgerItem(int worldIndex, BurgerItem item) {
        super(worldIndex, item);
        mHeight = item.mHeight;
        mOffset = item.mOffset;
        mSubType = item.mSubType;
        mBottomName = item.mBottomName;
    }

    protected BurgerItem(int worldIndex, XmlReader.Element element) {
        super(worldIndex, Type.BURGER, element);
        String subType = element.getAttribute("subType");
        if (subType.equals("middle-main")) {
            mSubType = SubType.MIDDLE_MAIN;
        } else if (subType.equals("middle-other")) {
            mSubType = SubType.MIDDLE_OTHER;
        } else if (subType.equals("middle-sauce")) {
            mSubType = SubType.MIDDLE_SAUCE;
        } else if (subType.equals("top")) {
            mSubType = SubType.TOP;
            mBottomName = element.getAttribute("bottom");
        } else if (subType.equals("bottom")) {
            mSubType = SubType.BOTTOM;
        } else if (subType.equals("top-bottom")) {
            mSubType = SubType.TOP_BOTTOM;
        } else {
            throw new RuntimeException("Invalid BurgerItem subType: " + subType);
        }
        initFromXml(element);
        assert mHeight > 0;
    }

    public void initFromXml(XmlReader.Element element) {
        super.initFromXml(element);
        mOffset = element.getIntAttribute("offset", mOffset);
        mHeight = element.getIntAttribute("height", mHeight);
    }

    protected BurgerItem(String name) {
        super(Type.BURGER, name);
    }

    public int getHeight() {
        return mHeight;
    }

    public int getOffset() {
        return mOffset;
    }

    public SubType getSubType() {
        return mSubType;
    }

    public String getBottomName() {
        return mBottomName;
    }
}
