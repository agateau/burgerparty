package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.StreamTokenizer;

interface InstructionDefinition {

    public abstract Instruction parse(StreamTokenizer tokenizer) throws IOException;

}