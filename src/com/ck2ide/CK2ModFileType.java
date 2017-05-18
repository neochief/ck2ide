package com.ck2ide;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CK2ModFileType extends LanguageFileType {
    public static final CK2ModFileType INSTANCE = new CK2ModFileType();

    public static final String EXTENSION = "mod";

    private CK2ModFileType() {
        super(CK2Language.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "CK2 mod";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "CK2 mod file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return CK2Icons.CK2;
    }
}