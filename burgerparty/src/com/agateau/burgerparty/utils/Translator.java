package com.agateau.burgerparty.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.agateau.burgerparty.utils.Messages.PluralId;

public class Translator {
    private static class Impl {
        private Messages mMessages;

        public Impl(Messages messages) {
            mMessages = messages;
        }

        public String tr(String src) {
            if (mMessages == null) {
                return src;
            }
            String txt = mMessages.plainEntries.get(src);
            return txt == null ? src : txt;
        }

        public String trn(String singular, String plural, int n) {
            String txt = findPluralTranslation(singular, plural, n);
            if (txt == null) {
                txt = n == 1 ? singular : plural;
            }
            return txt.replace("%n", String.valueOf(n));
        }

        private String findPluralTranslation(String singular, String plural, int n) {
            if (mMessages == null) {
                return null;
            }
            PluralId id = new PluralId(singular, plural);
            String[] lst = mMessages.pluralEntries.get(id);
            if (lst == null) {
                return null;
            }
            return lst[mMessages.plural(n)];
        }
    }

    private static Impl sImpl;

    public static String tr(String src) {
        init();
        return sImpl.tr(src);
    }

    public static String tr(String src, Object... args) {
        return String.format(tr(src), args);
    }

    public static String trn(String singular, String plural, int n) {
        init();
        return sImpl.trn(singular, plural, n);
    }

    public static String trn(String singular, String plural, int n, Object... args) {
        return String.format(trn(singular, plural, n), args);
    }

    private static void init() {
        if (sImpl != null) {
            return;
        }
        Messages messages = null;
        try {
            messages = (Messages)ResourceBundle.getBundle("Messages");
        } catch (MissingResourceException exception) {
        }
        sImpl = new Impl(messages);
    }
}
