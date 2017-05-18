package com.ck2ide.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.JBCardLayout;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.Alarm;
import com.intellij.util.ArrayUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ui.AsyncProcessIcon;
import com.intellij.util.ui.UIUtil;
import com.ck2ide.CK2Bundle;
import com.ck2ide.sdk.CK2SdkService;
import com.ck2ide.sdk.CK2SdkUtil;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

public class CK2SdkConfigurable implements SearchableConfigurable, Configurable.NoScroll {
    private static final String VERSION_GETTING = "VERSION_GETTING_CARD";
    private static final String VERSION_RESULT = "VERSION_RESULT_CARD";

    @NotNull
    private final Project myProject;
    @NotNull
    private final Disposable myDisposable = Disposer.newDisposable();
    @NotNull
    private final Alarm myAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, myDisposable);
    private JPanel myComponent;
    private TextFieldWithBrowseButton mySdkPathField;
    private JPanel myVersionPanel;
    private JBLabel myVersionLabel;
    private Color myDefaultLabelColor;

    public CK2SdkConfigurable(@NotNull Project project, boolean dialogMode) {
        myProject = project;
        if (dialogMode) {
            myComponent.setPreferredSize(new Dimension(400, -1));
        }
        FileChooserDescriptor chooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor()
                .withTitle(CK2Bundle.message("ck2.sdk.select.root.dir"));
        mySdkPathField.addBrowseFolderListener(myProject, new MyBrowseFolderListener(chooserDescriptor));
        listenForPathUpdate();
        Disposer.register(myDisposable, mySdkPathField);
    }

    @Override
    public void apply() throws ConfigurationException {
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                if (myProject.isDefault() || myProject.isDisposed()) {
                    return;
                }

                LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTable(myProject);
                Library get = table.getLibraryByName(CK2SdkService.LIBRARY_NAME);
                Library lib = get != null ? get : table.createLibrary(CK2SdkService.LIBRARY_NAME);

                Library.ModifiableModel libraryModel = lib.getModifiableModel();
                String libUrl = ArrayUtil.getFirstElement(lib.getUrls(OrderRootType.CLASSES));
                if (libUrl != null) {
                    libraryModel.removeRoot(libUrl, OrderRootType.CLASSES);
                }

                String sdkPath = mySdkPathField.getText();
                boolean toRemove = StringUtil.isEmpty(sdkPath);

                if (!toRemove) {
                    for (VirtualFile file : CK2SdkUtil.getSdkDirectoriesToAttach(sdkPath)) {
                        libraryModel.addRoot(file, OrderRootType.CLASSES);
                    }
                }
                libraryModel.commit();

                if (toRemove) {
                    updateModules(myProject, lib, true);
                    table.removeLibrary(lib);
                }

                table.getModifiableModel().commit();

                if (!toRemove) {
                    updateModules(myProject, lib, false);
                }
                CK2SdkService.getInstance(myProject).incModificationCount();
            }
        });
    }

    @Override
    public void reset() {
        mySdkPathField.setText(StringUtil.notNullize(CK2SdkService.getInstance(myProject).getSdkHomePath(null)));
    }

    @Override
    public boolean isModified() {
        String currentPath = StringUtil.notNullize(CK2SdkService.getInstance(myProject).getSdkHomePath(null));
        return !mySdkPathField.getText().equals(currentPath);
    }

    @NotNull
    @Override
    public String getId() {
        return "CK2ModuleLibrariesInitializer.sdk";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return CK2Bundle.message("ck2.sdk");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return myComponent;
    }

    private static void updateModules(@NotNull Project project, @NotNull Library lib, boolean remove) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
            if (!remove) {
                if (model.findLibraryOrderEntry(lib) == null) {
                    LibraryOrderEntry entry = model.addLibraryEntry(lib);
                    entry.setScope(DependencyScope.PROVIDED);
                }
            } else {
                LibraryOrderEntry entry = model.findLibraryOrderEntry(lib);
                if (entry != null) {
                    model.removeOrderEntry(entry);
                }
            }
            model.commit();
        }
    }

    private void createUIComponents() {
        myVersionLabel = new JBLabel();
        myDefaultLabelColor = myVersionLabel.getForeground();

        myVersionPanel = new JPanel(new JBCardLayout());
        JPanel gettingVersionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        AsyncProcessIcon gettingVersionIcon = new AsyncProcessIcon(CK2Bundle.message("getting.ck2.sdk.version"));
        gettingVersionPanel.add(gettingVersionIcon);
        gettingVersionPanel.add(new JLabel(CK2Bundle.message("getting")));
        myVersionPanel.add(gettingVersionPanel, VERSION_GETTING);
        myVersionPanel.add(myVersionLabel, VERSION_RESULT);
    }

    @Override
    public void disposeUIResources() {
        UIUtil.dispose(myVersionLabel);
        UIUtil.dispose(myVersionPanel);
        UIUtil.dispose(myComponent);
        myVersionLabel = null;
        myVersionPanel = null;
        myDefaultLabelColor = null;
        Disposer.dispose(myDisposable);
    }

    private class MyBrowseFolderListener extends ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {
        public MyBrowseFolderListener(@NotNull FileChooserDescriptor descriptor) {
            super(CK2Bundle.message("ck2.sdk.select.root.dir"), "", mySdkPathField, myProject, descriptor, TextComponentAccessor
                    .TEXT_FIELD_WHOLE_TEXT);
        }

        @Nullable
        @Override
        protected VirtualFile getInitialFile() {
            return ObjectUtils.chooseNotNull(super.getInitialFile(), CK2SdkUtil.suggestSdkDirectory());
        }
    }

    private void listenForPathUpdate() {
        final JTextField textField = mySdkPathField.getTextField();
        final Ref<String> prevPathRef = Ref.create(StringUtil.notNullize(textField.getText()));
        textField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                String sdkPath = StringUtil.notNullize(textField.getText());
                String prevPath = prevPathRef.get();
                if (!prevPath.equals(sdkPath)) {
                    prevPathRef.set(sdkPath);
                }
            }
        });
    }
}
