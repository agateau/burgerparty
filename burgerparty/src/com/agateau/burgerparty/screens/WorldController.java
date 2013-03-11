package com.agateau.burgerparty.screens;

import com.agateau.burgerparty.model.World;
import com.agateau.burgerparty.model.BurgerItem;
import com.agateau.burgerparty.view.WorldView;

import com.badlogic.gdx.InputProcessor;

public class WorldController implements InputProcessor {
	private World mWorld;
	private WorldView mWorldView;

	public WorldController(World world, WorldView view) {
		mWorld = world;
		mWorldView = view;
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// FIXME: Ugly coupling
		int index = mWorldView.getInventoryView().getIndexAt(screenX, 480 - screenY);

		BurgerItem item = mWorld.getInventory().get(index);
		if (item == null) {
			return false;
		}
		mWorld.getBurgerStack().addItem(item);
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
