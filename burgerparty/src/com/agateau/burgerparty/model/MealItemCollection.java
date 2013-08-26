package com.agateau.burgerparty.model;

import java.util.Collection;

import com.agateau.burgerparty.utils.Signal0;
import com.agateau.burgerparty.utils.Signal1;

public abstract class MealItemCollection<T extends MealItem> {
	public Signal1<MealItem> itemAdded = new Signal1<MealItem>();
	public Signal0 initialized = new Signal0();
	public Signal0 cleared = new Signal0();
	public Signal0 trashed = new Signal0();

	public enum CompareResult {
		SUBSET,
		SAME,
		DIFFERENT;

		public String toString() {
			if (this == SUBSET) {
				return "SUBSET";
			} else if (this == SAME) {
				return "SAME";
			} else {
				return "DIFFERENT";
			}
		}
	}

	public boolean isEmpty() {
		return getItems().isEmpty();
	}

	public void clear() {
		getItems().clear();
		cleared.emit();
	}

	public void trash() {
		getItems().clear();
		trashed.emit();
	}

	public void addItem(T item) {
		getItems().add(item);
		itemAdded.emit(item);
	}

	public void setItems(Collection<T> items) {
		Collection<T> col = getItems();
		col.clear();
		col.addAll(items);
		initialized.emit();
	}

	@Override
	public String toString() {
		String out = "[";
		for(T item: getItems()) {
			out += item.getName() + ", ";
		}
		return out + "]";
	}

	public abstract Collection<T> getItems();
}
