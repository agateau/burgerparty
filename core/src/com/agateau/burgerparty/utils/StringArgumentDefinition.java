package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.StreamTokenizer;

public class StringArgumentDefinition extends ArgumentDefinition<String> {

    public StringArgumentDefinition() {
        super(String.class, null);
    }

    StringArgumentDefinition(String defaultValue) {
        super(String.class, defaultValue);
    }

    @Override
    public Argument parse(StreamTokenizer tokenizer) {
        try {
            tokenizer.nextToken();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        String value;
        if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
            value = tokenizer.sval;
            assert(value != null);
        } else {
            assert(this.defaultValue != null);
            tokenizer.pushBack();
            value = this.defaultValue;
        }
        return new BasicArgument(String.class, value);
    }
}
