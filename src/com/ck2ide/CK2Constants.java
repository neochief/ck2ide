package com.ck2ide;

import com.intellij.notification.NotificationGroup;
import com.intellij.openapi.wm.ToolWindowId;
import org.jetbrains.annotations.NonNls;

public class CK2Constants {
    public static final String MODULE_TYPE_ID = "CK2_MODULE";
    public static final String SDK_TYPE_ID = "CK2_SDK";
    @NonNls
    public static final String CK2GAME_EXE = "CK2game.exe";
    @NonNls
    public static final String SQUIRREL = "CK2ModuleLibrariesInitializer";

    public static final NotificationGroup SQUIRREL_NOTIFICATION_GROUP = NotificationGroup.balloonGroup("Squirrel plugin notifications");
    public static final NotificationGroup SQUIRREL_EXECUTION_NOTIFICATION_GROUP = NotificationGroup.toolWindowGroup("Squirrel Execution", ToolWindowId.RUN);


    private CK2Constants() {

    }
}
