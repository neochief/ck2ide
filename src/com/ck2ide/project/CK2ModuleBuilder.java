package com.ck2ide.project;

import com.ck2ide.CK2Bundle;
import com.ck2ide.CK2Icons;
import com.ck2ide.sdk.CK2SdkType;
import com.intellij.ide.util.projectWizard.JavaModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

/**
 * Responsible for building a fresh CK2ModuleLibrariesInitializer mod contents.
 */
public class CK2ModuleBuilder extends ModuleBuilder {

    @Override
    public String getName() {
        return CK2Bundle.message("ck2.title");
    }

    @Override
    public String getPresentableName() {
        return CK2Bundle.message("ck2.title");
    }

    @Override
    public String getDescription() {
        return CK2Bundle.message("ck2.project.description");
    }

    @Override
    public Icon getBigIcon() {
        return CK2Icons.CK2;
    }

    @Override
    public Icon getNodeIcon() {
        return CK2Icons.CK2;
    }

    @Override
    public ModuleType getModuleType() {
        return CK2ModuleType.getInstance();
    }

    @Override
    public String getParentGroup() {
        return CK2ModuleType.MODULE_TYPE_ID;
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(final WizardContext context, final Disposable parentDisposable) {
        return null;
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdkType) {
        return sdkType == CK2SdkType.getInstance();
    }

    @Override
    public void setupRootModel(final ModifiableRootModel modifiableRootModel) throws ConfigurationException {
        if (myJdk != null){
            modifiableRootModel.setSdk(myJdk);
        } else {
            modifiableRootModel.inheritSdk();
        }
        final ContentEntry contentEntry = doAddContentEntry(modifiableRootModel);
        final VirtualFile baseDir = contentEntry == null ? null : contentEntry.getFile();
        if (baseDir != null) {
            setupProject(modifiableRootModel, baseDir);
        }
    }

    /**
     * Create dummy directories and mod files when creating a fresh project.
     */
    static void setupProject(@NotNull final ModifiableRootModel modifiableRootModel, @NotNull final VirtualFile baseDir) {
        try {
            String name = modifiableRootModel.getModule().getName().toLowerCase(Locale.US);

            VirtualFile modDir = baseDir.createChildDirectory(null, name);
            modDir.createChildDirectory(null, "events")
                    .createChildData(null, "edit_me.txt");
            modDir.createChildDirectory(null, "localisation")
                    .createChildData(null, "text1.csv");

            VirtualFile modFile = baseDir.createChildData(null, name + ".mod");
            modFile.setBinaryContent(("name=\"" + name + "\"\n" +
                    "path=\"mod/" + name + "\"\n").getBytes(Charset.forName("UTF-8")));

            scheduleFilesOpeningAndPubGet(modifiableRootModel.getModule(), Arrays.asList(modFile));
        } catch (IOException ignore) {/*unlucky*/}
    }

    /**
     * Schedule opening main mod file right after project creation.
     */
    private static void scheduleFilesOpeningAndPubGet(@NotNull final Module module, @NotNull final Collection<VirtualFile> files) {
        runWhenNonModalIfModuleNotDisposed(new Runnable() {
            public void run() {
                final FileEditorManager manager = FileEditorManager.getInstance(module.getProject());
                for (VirtualFile file : files) {
                    manager.openFile(file, true);
                }
            }
        }, module);
    }

    static void runWhenNonModalIfModuleNotDisposed(@NotNull final Runnable runnable, @NotNull final Module module) {
        StartupManager.getInstance(module.getProject()).runWhenProjectIsInitialized(new Runnable() {
            @Override
            public void run() {
                if (ApplicationManager.getApplication().getCurrentModalityState() == ModalityState.NON_MODAL) {
                    runnable.run();
                } else {
                    ApplicationManager.getApplication().invokeLater(runnable, ModalityState.NON_MODAL, new Condition() {
                        @Override
                        public boolean value(final Object o) {
                            return module.isDisposed();
                        }
                    });
                }
            }
        });
    }
}
