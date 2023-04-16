package com.agateau.burgerparty.utils;

import java.io.IOException;
import java.io.Writer;

import com.badlogic.gdx.files.FileHandle;

public class CsvWriter {
    private FileHandle mHandle;
    private Writer mWriter;
    private char mSeparator = ';';

    public CsvWriter(FileHandle handle) {
        mHandle = handle;
        mWriter = mHandle.writer(false);
    }

    public void setSeparator(char separator) {
        mSeparator = separator;
    }

    public void close() {
        try {
            mWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Object...values) {
        try {
            boolean first = true;
            for (Object value: values) {
                if (first) {
                    first = false;
                } else {
                    mWriter.write(mSeparator);
                }
                mWriter.write(value.toString());
            }
            mWriter.write('\n');
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}