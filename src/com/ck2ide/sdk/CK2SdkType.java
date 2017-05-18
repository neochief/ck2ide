package com.ck2ide.sdk;

import com.ck2ide.CK2Bundle;
import com.ck2ide.CK2Icons;
import com.ck2ide.CK2Constants;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Defines CK2ModuleLibrariesInitializer SDK. In other words, CK2ModuleLibrariesInitializer game directory.
 */
public class CK2SdkType extends com.intellij.openapi.projectRoots.SdkType {

    public CK2SdkType() {
        super(CK2Constants.SDK_TYPE_ID);
    }

    @NotNull
    public static CK2SdkType getInstance() {
        return SdkType.findInstance(CK2SdkType.class);
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return CK2Icons.CK2;
    }

    @NotNull
    @Override
    public Icon getIconForAddAction() {
        return getIcon();
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        VirtualFile suggestSdkDirectory = CK2SdkUtil.suggestSdkDirectory();
        return suggestSdkDirectory != null ? suggestSdkDirectory.getPath() : null;
    }

    @Override
    public boolean isValidSdkHome(@NotNull String path) {
        CK2SdkService.LOG.debug("Validating sdk path: " + path);
        if (getVersionString(path) == null) {
            CK2SdkService.LOG.debug("Unknown game version.");
            return false;
        }
        CK2SdkService.LOG.debug("Found valid game directory: " + path);
        return true;
    }

    @NotNull
    @Override
    public String suggestSdkName(@Nullable String currentSdkName, @NotNull String sdkHome) {
        return CK2Bundle.message("ck2.version", getVersionString(sdkHome));
    }

    @Nullable
    @Override
    public String getVersionString(@NotNull String sdkHome) {
        return CK2SdkUtil.retrieveCK2Version(sdkHome);
    }


    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(@NotNull SdkModel sdkModel, @NotNull SdkModificator sdkModificator) {
        return null;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData sdkAdditionalData, @NotNull Element element) {

    }

    @NotNull
    @NonNls
    @Override
    public String getPresentableName() {
        return CK2Bundle.message("ck2.sdk");
    }

    @Override
    public void setupSdkPaths(@NotNull Sdk sdk) {
        String versionString = sdk.getVersionString();
        if (versionString == null) throw new RuntimeException("ck2.sdk.version.undefined");

        SdkModificator modificator = sdk.getSdkModificator();
        String path = sdk.getHomePath();
        if (path == null) return;
        modificator.setHomePath(path);

        for (VirtualFile file : CK2SdkUtil.getCK2GameScriptsDirectories(path)) {
            modificator.addRoot(file, OrderRootType.CLASSES);
            modificator.addRoot(file, OrderRootType.SOURCES);
        }

        modificator.commitChanges();
    }
}
