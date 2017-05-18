package com.ck2ide.sdk;

import com.ck2ide.CK2Constants;
//import com.ck2ide.junk.CK2ApplicationLibrariesService;
//import com.ck2ide.junk.CK2LibrariesService;
//import com.ck2ide.psi.CK2File;
import com.intellij.execution.configurations.PathEnvironmentVariableUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.UserDataHolder;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.util.Function;
import com.intellij.util.ObjectUtils;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.text.VersionComparatorUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.util.containers.ContainerUtil.newLinkedHashSet;

public class CK2SdkUtil {

    @Nullable
    public static VirtualFile suggestSdkDirectory() {
        String winPathC = "C:\\Program Files (x86)\\Steam\\steamapps\\common\\Crusader Kings II";
        String winPathD = "D:\\Steam\\steamapps\\common\\Crusader Kings II";
        File file = FileUtil.findFirstThatExist(winPathC, winPathD);
        if (file != null) {
            return LocalFileSystem.getInstance().findFileByIoFile(file);
        }
        return null;
    }

    @NotNull
    public static Collection<VirtualFile> getSdkDirectoriesToAttach(@NotNull String sdkPath) {
        // At this point, we only add a root path.
        String srcPath = "";
        VirtualFile src = VirtualFileManager.getInstance().findFileByUrl(VfsUtilCore.pathToUrl(FileUtil.join(sdkPath, srcPath)));
        if (src != null && src.isDirectory()) {
            return Collections.singletonList(src);
        }
        return Collections.emptyList();
    }


    public static String retrieveCK2Version(String sdkHomePath) {
        // TODO: Parse the real thing.
        return "2.7.1";
    }

    @NotNull
    public static Collection<VirtualFile> getCK2PathSources(@NotNull Project project, @Nullable Module module) {
        if (module != null) {
            Project project1 = module.getProject();
            return getCK2PathRoots(project1, module);
        }
        return getCK2PathRoots(project, null);
    }

    @NotNull
    private static Collection<VirtualFile> getCK2PathRoots(@NotNull Project project, @Nullable Module module) {
//        Collection<VirtualFile> roots = ContainerUtil.newArrayList();
//        if (CK2ApplicationLibrariesService.getInstance().isUseGoPathFromSystemEnvironment()) {
//            roots.addAll(getCK2PathsRootsFromEnvironment());
//        }
//        roots.addAll(module != null ? CK2LibrariesService.getUserDefinedLibraries(module) : CK2LibrariesService.getUserDefinedLibraries(project));
//        return roots;
        return ContainerUtil.newArrayList();
    }
}
