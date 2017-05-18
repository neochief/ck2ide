package com.ck2ide;

import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public class CK2FileTypeFactory extends FileTypeFactory{
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer) {
        fileTypeConsumer.consume(CK2ScriptFileType.INSTANCE, CK2ScriptFileType.EXTENSION);
        fileTypeConsumer.consume(CK2ModFileType.INSTANCE, CK2ModFileType.EXTENSION);
    }
}

