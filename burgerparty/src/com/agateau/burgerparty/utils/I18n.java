package com.agateau.burgerparty.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.agateau.burgerparty.utils.Messages.PluralId;

public class I18n {
    private static class Translator {
        private Messages mMessages;

        public Translator(Messages messages) {
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
            if (mMessages != null) {
                PluralId id = new PluralId(singular, plural);
                String[] lst = mMessages.pluralEntries.get(id);
                if (lst != null) {
                    return lst[mMessages.plural(n)];
                }
            }
            return n == 1 ? singular : plural;
        }
    }

    private static Translator sTranslator;

    public static String _(String src) {
        init();
        return sTranslator.tr(src);
    }

    public static String trn(String singular, String plural, int n) {
        init();
        String txt = sTranslator.trn(singular, plural, n);
        return txt.replace("%n", String.valueOf(n));
    }

    private static void init() {
        if (sTranslator != null) {
            return;
        }
        Messages messages = null;
        try {
            messages = (Messages)ResourceBundle.getBundle("Messages");
        } catch (MissingResourceException exception) {
        }
        sTranslator = new Translator(messages);
    }
}
