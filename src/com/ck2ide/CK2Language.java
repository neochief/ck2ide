package com.ck2ide;

import com.intellij.lang.Language;

public class CK2Language extends Language {
    public static final CK2Language INSTANCE = new CK2Language();

    private CK2Language() {
        super("CK2 scripts");
    }
}