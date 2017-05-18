package com.ck2ide.actions;

import com.intellij.CommonBundle;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.ide.actions.CreateFileAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.ck2ide.CK2Bundle;
import com.ck2ide.CK2ScriptFileType;
import com.ck2ide.CK2Icons;
import org.jetbrains.annotations.NotNull;

/**
 * Action to create a new Bash file from a template.
 * <p/>
 * The template data is stored in resources/fileTemplates/internal/Bash Script.sh.ft
 *
 * @author Joachim Ansorg
 */
public class NewCK2ScriptAction extends CreateElementActionBase {
    public NewCK2ScriptAction() {
        super(CK2Bundle.message("newfile.menu.action.text"), CK2Bundle.message("newfile.menu.action.description"), CK2Icons.TXT_FILE);
    }

    static String computeFilename(String inputFilename) {
        String usedExtension = FileUtilRt.getExtension(inputFilename);
        boolean withExtension = !usedExtension.isEmpty();

        return withExtension ? inputFilename : (inputFilename + "." + CK2ScriptFileType.EXTENSION);
    }

    protected String getDialogPrompt() {
        return CK2Bundle.message("newfile.dialog.prompt");
    }

    protected String getDialogTitle() {
        return CK2Bundle.message("newfile.dialog.title");
    }

    protected String getCommandName() {
        return CK2Bundle.message("newfile.command.name");
    }

    protected String getActionName(PsiDirectory directory, String newName) {
        return CK2Bundle.message("newfile.menu.action.text");
    }

    @NotNull
    protected final PsiElement[] invokeDialog(final Project project, final PsiDirectory directory) {
        final MyInputValidator validator = new MyInputValidator(project, directory);
        Messages.showInputDialog(project, getDialogPrompt(), getDialogTitle(), Messages.getQuestionIcon(), "", validator);

        return validator.getCreatedElements();
    }

    @NotNull
    protected PsiElement[] create(String newName, PsiDirectory directory) throws Exception {
        CreateFileAction.MkDirs mkdirs = new CreateFileAction.MkDirs(newName, directory);
        return new PsiElement[]{mkdirs.directory.createFile(computeFilename(mkdirs.newName))};
    }

    protected String getErrorTitle() {
        return CommonBundle.getErrorTitle();
    }
}