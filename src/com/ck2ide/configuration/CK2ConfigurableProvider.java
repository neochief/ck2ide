package com.ck2ide.configuration;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurableProvider;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.ck2ide.CK2Bundle;
import com.ck2ide.sdk.CK2SdkService;
import com.ck2ide.CK2Constants;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CK2ConfigurableProvider extends ConfigurableProvider {
    @NotNull
    private final Project myProject;

    public CK2ConfigurableProvider(@NotNull Project project) {
        myProject = project;
    }

    @Nullable
    @Override
    public Configurable createConfigurable() {
        Configurable sdkConfigurable =CK2SdkService.getInstance(myProject).createSdkConfigurable();
        return new SquirrelCompositeConfigurable(sdkConfigurable);
    }

    private static class SquirrelCompositeConfigurable extends SearchableConfigurable.Parent.Abstract {
        private Configurable[] myConfigurables;

        public SquirrelCompositeConfigurable(Configurable... configurables) {
            myConfigurables = configurables;
        }

        @Override
        protected Configurable[] buildConfigurables() {
            return myConfigurables;
        }

        @NotNull
        @Override
        public String getId() {
            return CK2Constants.SQUIRREL;
        }

        @Nls
        @Override
        public String getDisplayName() {
            return CK2Bundle.message("ck2.title");
        }

        @Nullable
        @Override
        public String getHelpTopic() {
            return null;
        }

        @Override
        public void disposeUIResources() {
            super.disposeUIResources();
            myConfigurables = null;
        }
    }
}
