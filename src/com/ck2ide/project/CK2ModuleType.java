package com.ck2ide.project;

import com.ck2ide.CK2Bundle;
import com.ck2ide.CK2Icons;
import com.ck2ide.sdk.CK2SdkType;
import com.ck2ide.CK2Constants;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.ProjectJdkForModuleStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Defines CK2ModuleLibrariesInitializer mod as IDEA module.
 */
public class CK2ModuleType extends ModuleType<CK2ModuleBuilder> {

    public static final String MODULE_TYPE_ID = CK2Constants.MODULE_TYPE_ID;

    public CK2ModuleType() {
        super(MODULE_TYPE_ID);
    }

    @NotNull
    public static CK2ModuleType getInstance() {
        return (CK2ModuleType) ModuleTypeManager.getInstance().findByID(MODULE_TYPE_ID);
    }

    @NotNull
    @Override
    public CK2ModuleBuilder createModuleBuilder() {
        return new CK2ModuleBuilder();
    }

    @NotNull
    @Override
    public String getName() {
        return CK2Bundle.message("ck2.title");
    }

    @NotNull
    @Override
    public String getDescription() {
        return CK2Bundle.message("ck2.project.description");
    }

    @Nullable
    @Override
    public Icon getBigIcon() {
        return CK2Icons.CK2;
    }

    @Nullable
    @Override
    public Icon getNodeIcon(boolean isOpened) {
        return CK2Icons.CK2;
    }

    /**
     * When creating a new CK2ModuleLibrariesInitializer project, present a step to pick a path to CK2ModuleLibrariesInitializer game directory.
     */
    @NotNull
    @Override
    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext,
                                                @NotNull final CK2ModuleBuilder moduleBuilder,
                                                @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{new ProjectJdkForModuleStep(wizardContext, CK2SdkType.getInstance()) {
            public void updateDataModel() {
                super.updateDataModel();
                moduleBuilder.setModuleJdk(getJdk());
            }
        }};
    }
}