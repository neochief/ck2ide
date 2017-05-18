package com.ck2ide.sdk;

import com.ck2ide.project.CK2ModuleType;
import com.intellij.ProjectTopics;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ComponentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.ui.configuration.ProjectSettingsService;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * SDK service for IDEA (java IDE).
 */
public class CK2IdeaSdkService extends CK2SdkService {
    public CK2IdeaSdkService(@NotNull Project project) {
        super(project);
        myProject.getMessageBus().connect(project).subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootAdapter() {
            @Override
            public void rootsChanged(ModuleRootEvent event) {
                incModificationCount();
            }
        });
    }

    @Override
    public String getSdkHomePath(@Nullable final Module module) {
        if (isCK2Module(module)) {
            ComponentManager holder = ObjectUtils.notNull(module, myProject);
            return CachedValuesManager.getManager(myProject).getCachedValue(holder, new CachedValueProvider<String>() {
                @Nullable
                @Override
                public Result<String> compute() {
                    Sdk sdk = getCK2Sdk(module);
                    return Result.create(sdk != null ? sdk.getHomePath() : null, CK2IdeaSdkService.this);
                }
            });
        } else {
            return super.getSdkHomePath(module);
        }
    }

    @Nullable
    @Override
    public String getSdkVersion(@Nullable final Module module) {
        if (isCK2Module(module)) {
            ComponentManager holder = ObjectUtils.notNull(module, myProject);
            return CachedValuesManager.getManager(myProject).getCachedValue(holder, new CachedValueProvider<String>() {
                @Nullable
                @Override
                public Result<String> compute() {
                    Sdk sdk = getCK2Sdk(module);
                    return Result.create(sdk != null ? sdk.getVersionString() : null, CK2IdeaSdkService.this);
                }
            });
        } else {
            return super.getSdkVersion(module);
        }
    }

    @Override
    public void chooseAndSetSdk(@Nullable final Module module) {
        if (isCK2Module(module)) {
            Sdk projectSdk = ProjectSettingsService.getInstance(myProject).chooseAndSetSdk();
            if (projectSdk == null && module != null) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        if (!module.isDisposed()) {
                            ModuleRootModificationUtil.setSdkInherited(module);
                        }
                    }
                });
            }
        } else {
            super.chooseAndSetSdk(module);
        }
    }

    @Override
    public boolean isCK2Module(@Nullable Module module) {
        return module != null && ModuleUtil.getModuleType(module) == CK2ModuleType.getInstance();
    }

    private Sdk getCK2Sdk(@Nullable Module module) {
        if (module != null) {
            Sdk sdk = ModuleRootManager.getInstance(module).getSdk();
            if (sdk != null && sdk.getSdkType() instanceof CK2SdkType) {
                return sdk;
            }
        }
        Sdk sdk = ProjectRootManager.getInstance(myProject).getProjectSdk();
        return sdk != null && sdk.getSdkType() instanceof CK2SdkType ? sdk : null;
    }
}
