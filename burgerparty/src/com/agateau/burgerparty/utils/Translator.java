package com.agateau.burgerparty.utils;

import java.util.Locale;

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
        initImpl();
        return sImpl.tr(src);
    }

    public static String tr(String src, Object... args) {
        return String.format(tr(src), args);
    }

    public static String trn(String singular, String plural, int n) {
        initImpl();
        return sImpl.trn(singular, plural, n);
    }

    public static String trn(String singular, String plural, int n, Object... args) {
        return String.format(trn(singular, plural, n), args);
    }

    private static void initImpl() {
        if (sImpl != null) {
            return;
        }
        init();
    }

    public static void init() {
        init(null);
    }

    public static void init(String locale) {
        if (locale == null) {
            locale = Locale.getDefault().getLanguage() + "_" + Locale.getDefault().getCountry();
        }
        Messages messages;
        messages = tryLoad(locale);
        if (messages == null) {
            int idx = locale.indexOf('_');
            if (idx > -1) {
                messages = tryLoad(locale.substring(0, idx));
            }
        }
        sImpl = new Impl(messages);
    }

    private static Messages tryLoad(String suffix) {
        Class<?> cls;
        try {
            cls = Class.forName("Messages_" + suffix);
        } catch (ClassNotFoundException exception) {
            return null;
        }

        try {
            return (Messages) cls.newInstance();
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }
        return null;
    }
}
