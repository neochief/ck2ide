package com.ck2ide.sdk;

import com.ck2ide.configuration.CK2SdkConfigurable;
import com.ck2ide.CK2Constants;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ShowSettingsUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.SimpleModificationTracker;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * SDK service for all other JetBrains IDEs (ie. PHPStorm).
 */
public class CK2SdkService extends SimpleModificationTracker {
    public static final Logger LOG = Logger.getInstance(CK2SdkService.class);

    @NotNull
    protected final Project myProject;

    protected CK2SdkService(@NotNull Project project) {
        myProject = project;
    }

    public static CK2SdkService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, CK2SdkService.class);
    }

    public static final String LIBRARY_NAME = CK2Constants.SDK_TYPE_ID;

    @Nullable
    public String getSdkHomePath(@Nullable Module module) {
        return getSdkHomeLibPath(module);
    }

    private String getSdkHomeLibPath(@Nullable Module module) {
        ComponentManager holder = ObjectUtils.notNull(module, myProject);
        return CachedValuesManager.getManager(myProject).getCachedValue(holder, new CachedValueProvider<String>() {
            @Nullable
            @Override
            public Result<String> compute() {
                return Result.create(ApplicationManager.getApplication().runReadAction(new Computable<String>() {
                    @Nullable
                    @Override
                    public String compute() {
                        LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(myProject);
                        for (Library library : table.getLibraries()) {
                            String libraryName = library.getName();
                            if (libraryName != null && libraryName.startsWith(LIBRARY_NAME)) {
                                for (VirtualFile root : library.getFiles(OrderRootType.CLASSES)) {
                                    if (isCK2SdkRoot(root)) {
                                        return root.getCanonicalPath();
                                    }
                                }
                            }
                        }
                        return null;
                    }
                }), CK2SdkService.this);
            }
        });
    }

    @Nullable
    public String getSdkVersion(@Nullable final Module module) {
        ComponentManager holder = ObjectUtils.notNull(module, myProject);
        return CachedValuesManager.getManager(myProject).getCachedValue(holder, new CachedValueProvider<String>() {
            @Nullable
            @Override
            public Result<String> compute() {
                String result = null;
                String sdkHomePath = getSdkHomePath(module);
                if (sdkHomePath != null) {
                    result = CK2SdkUtil.retrieveCK2Version(sdkHomePath);
                }
                return Result.create(result, CK2SdkService.this);
            }
        });
    }

    public void chooseAndSetSdk(@Nullable Module module) {
        ShowSettingsUtil.getInstance().editConfigurable(myProject, new CK2SdkConfigurable(myProject, true));
    }

    @Nullable
    public Configurable createSdkConfigurable() {
        return !myProject.isDefault() ? new CK2SdkConfigurable(myProject, false) : null;
    }

    public boolean isCK2Module(@Nullable Module module) {
        return getSdkHomeLibPath(module) != null;
    }

    public static boolean isCK2SdkRoot(@NotNull VirtualFile root) {
        return root.isInLocalFileSystem() &&
                root.isDirectory() &&
                VfsUtilCore.findRelativeFile(CK2Constants.CK2GAME_EXE, root) != null;
    }
}
