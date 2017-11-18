package com.coderstory.Purify.module;

import com.coderstory.Purify.plugins.IModule;
import com.coderstory.Purify.utils.XposedHelper;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class CorePatch extends XposedHelper implements IModule {


    public void initZygote(IXposedHookZygoteInit.StartupParam paramStartupParam) {

        findAndHookMethod("java.security.MessageDigest", null, "isEqual", byte[].class, byte[].class, new XC_MethodHook() {
            protected void beforeHookedMethod(MethodHookParam methodHookParam)
                    throws Throwable {
                prefs.reload();
                if (prefs.getBoolean("authcreak", false)) {
                    methodHookParam.setResult(true);
                }
            }
        });

        hookAllMethods("com.android.org.conscrypt.OpenSSLSignature", null, "engineVerify", new XC_MethodHook() {
            protected void beforeHookedMethod(XC_MethodHook.MethodHookParam paramAnonymousMethodHookParam)
                    throws Throwable {
                prefs.reload();
                if (prefs.getBoolean("authcreak", false)) {
                    paramAnonymousMethodHookParam.setResult(true);
                }
            }
        });

    }


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam paramLoadPackageParam) {

        if (("android".equals(paramLoadPackageParam.packageName)) && (paramLoadPackageParam.processName.equals("android"))) {

            Class packageClass = XposedHelpers.findClass("android.content.pm.PackageParser.Package", paramLoadPackageParam.classLoader);

            hookAllMethods("com.android.server.pm.PackageManagerService",paramLoadPackageParam.classLoader, "checkDowngrade", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    super.beforeHookedMethod(methodHookParam);
                    Object packageInfoLite = methodHookParam.args[0];
                    prefs.reload();
                    if (prefs.getBoolean("downgrade", false)) {
                        Field field = packageClass.getField("mVersionCode");
                        field.set(packageInfoLite, 0);
                    }
                    ;
                }
            });

            hookAllMethods("com.android.server.pm.PackageManagerService",paramLoadPackageParam.classLoader, "verifySignaturesLP", new XC_MethodHook() {

                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    prefs.reload();
                    if (prefs.getBoolean("authcreak", false)) {
                        methodHookParam.setResult(true);
                    }
                }
            });

            hookAllMethods("com.android.server.pm.PackageManagerService",paramLoadPackageParam.classLoader,"compareSignatures", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    prefs.reload();
                    if (prefs.getBoolean("zipauthcreak", false)) {
                        methodHookParam.setResult(0);
                    }
                }
            });
        }
    }
}
