package com.ck2ide.sdk;

import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryKind;
import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VirtualFile;
import com.ck2ide.CK2Icons;
import com.ck2ide.CK2Constants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

/**
 * Provide a custom icon and description for CK2ModuleLibrariesInitializer library.
 */
public class CK2SdkLibraryPresentationProvider extends LibraryPresentationProvider<DummyLibraryProperties> {
    private static final LibraryKind KIND = LibraryKind.create(CK2Constants.SQUIRREL);

    public CK2SdkLibraryPresentationProvider() {
        super(KIND);
    }

    @Nullable
    public Icon getIcon(@Nullable DummyLibraryProperties properties) {
        return CK2Icons.TXT_FILE;
    }

    /**
     * Returns non-null value if a library with classes roots {@code classesRoots} is of a kind described by this provider.
     */
    @Nullable
    public DummyLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        for (VirtualFile root : classesRoots) {
            if (CK2SdkService.isCK2SdkRoot(root)) {
                return DummyLibraryProperties.INSTANCE;
            }
        }
        return null;
    }
}
