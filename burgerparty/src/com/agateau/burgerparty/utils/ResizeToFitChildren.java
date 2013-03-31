package com.agateau.burgerparty.utils;

/**
 * An actor can choose to implement this interface if it wants to resize to fit its children.
 * 
 * Children must explicitly call onChildSizeChanged()
 */
public interface ResizeToFitChildren {
	void onChildSizeChanged();
}
