package com.ck2ide;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CK2ScriptFileType extends LanguageFileType {
    public static final CK2ScriptFileType INSTANCE = new CK2ScriptFileType();

    public static final String EXTENSION = "txt";
    public static final String MOD_EXTENSION = "mod";

    private CK2ScriptFileType() {
        super(CK2Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "CK2 script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "CK2 script file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return CK2Icons.TXT_FILE;
    }
}