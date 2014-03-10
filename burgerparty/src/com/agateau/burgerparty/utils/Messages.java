package com.agateau.burgerparty.utils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;

public class Messages extends ResourceBundle {
	public static class PluralId {
		public final String singular;
		public final String plural;
		public PluralId(String s, String p) {
			singular = s;
			plural = p;
		}
		@Override
		public int hashCode() {
			return singular.hashCode() * plural.hashCode();
		}
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PluralId)) {
				return false;
			}
			PluralId other = (PluralId)obj;
			return singular.equals(other.singular) && plural.equals(other.plural);
		}
	}

	public final HashMap<String, String> plainEntries = new HashMap<String, String>();

	public final HashMap<PluralId, String[]> pluralEntries = new HashMap<PluralId, String[]>();

	@Override
	public Enumeration<String> getKeys() {
		return null;
	}

	@Override
	protected Object handleGetObject(String key) {
		return null;
	}

	public int plural(int n) {
		return n == 1 ? 0 : 1;
	}
}
