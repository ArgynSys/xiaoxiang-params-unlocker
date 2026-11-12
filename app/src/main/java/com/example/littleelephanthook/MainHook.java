package com.example.littleelephanthook;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class MainHook implements IXposedHookLoadPackage {
    private static final String TARGET_PACKAGE = "com.jiabaida.little_elephant";
    private static final String BLE_UTILS = "com.jiabaida.little_elephant.util.BleUtils";
    private static final String PARAMS_FRAGMENT = "com.jiabaida.little_elephant.ui.fragment.ParamsFragment";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if (!TARGET_PACKAGE.equals(lpparam.packageName)) {
            return;
        }

        try {
            Class<?> bleUtils = XposedHelpers.findClass(BLE_UTILS, lpparam.classLoader);
            setPasswordStatus(bleUtils);

            XposedHelpers.findAndHookMethod(PARAMS_FRAGMENT, lpparam.classLoader,
                    "initDataList", boolean.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            param.args[0] = true;
                            setPasswordStatus(bleUtils);
                        }
                    });
        } catch (Throwable t) {
            XposedBridge.log("[LittleElephantHook] Failed to install hooks: " + t);
        }
    }

    private static void setPasswordStatus(Class<?> bleUtils) {
        XposedHelpers.setStaticBooleanField(bleUtils, "rootPswCheckStatus", true);
        XposedHelpers.setStaticIntField(bleUtils, "blePswStatus", 10006);
    }
}
