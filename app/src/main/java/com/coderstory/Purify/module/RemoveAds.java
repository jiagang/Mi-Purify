package com.coderstory.Purify.module;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;

import com.coderstory.Purify.plugins.IModule;
import com.coderstory.Purify.utils.XposedHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class RemoveAds extends XposedHelper implements IModule {

    @Override
    public void initZygote(IXposedHookZygoteInit.StartupParam startupParam) {
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) {

        if (!prefs.getBoolean("EnableBlockAD", false)) {
            return;
        }
        //核心模块
        if (loadPackageParam.packageName.equals("com.miui.core")) {
            findAndHookMethod("miui.os.SystemProperties", loadPackageParam.classLoader, "get", String.class, String.class, new XC_MethodHook() {

                protected void afterHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                        throws Throwable {
                    if (paramAnonymousMethodHookParam.args[0].toString().equals("ro.product.mod_device")) {
                        paramAnonymousMethodHookParam.setResult("gemini_global");
                    }
                }

                protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                        throws Throwable {
                    if (paramAnonymousMethodHookParam.args[0].toString().equals("ro.product.mod_device")) {
                        paramAnonymousMethodHookParam.setResult("gemini_global");
                    }
                }
            });
            return;
        }

        //垃圾清理
        if (loadPackageParam.packageName.equals("com.miui.cleanmaster")) {

            if (prefs.getBoolean("EnableSafeCenter", false)) {
                findAndHookMethod("com.miui.optimizecenter.result.DataModel", loadPackageParam.classLoader, "post", Map.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                            throws Throwable {
                        paramAnonymousMethodHookParam.setResult("");
                    }
                });
                findAndHookMethod("com.miui.optimizecenter.config.MiStat", loadPackageParam.classLoader, "getChannel", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                            throws Throwable {
                        paramAnonymousMethodHookParam.setResult("international");
                    }
                });

                findAndHookMethod("com.miui.optimizecenter.Application", loadPackageParam.classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences((Context) param.thisObject);
                        pref.edit().putBoolean("key_information_setting_close", false).apply();
                    }
                });
            }
        }

        if (loadPackageParam.packageName.equals("com.android.browser")) {
            findAndHookMethod("miui.browser.a.a", loadPackageParam.classLoader, "a", String.class, String.class, String.class, List.class, HashMap.class, XC_MethodReplacement.returnConstant(null));
        }


        //日历
        if (loadPackageParam.packageName.equals("com.miui.calendar")) {
            findAndHookMethod("com.miui.calendar.ad.AdService", loadPackageParam.classLoader, "onHandleIntent", Intent.class, XC_MethodReplacement.returnConstant(null));
            findAndHookMethod("com.miui.calendar.card.single.local.SummarySingleCard", loadPackageParam.classLoader, "needShowAdBanner", XC_MethodReplacement.returnConstant(false));
            findAndHookMethod("com.miui.calendar.card.single.custom.ad.AdSingleCard", loadPackageParam.classLoader, "needDisplay", XC_MethodReplacement.returnConstant(false));
            findAndHookMethod("com.miui.calendar.ad.AdUtils", loadPackageParam.classLoader, "getAdConfigJson", Context.class, XC_MethodReplacement.returnConstant(null));
            findAndHookMethod("com.miui.calendar.card.single.custom.RecommendSingleCard", loadPackageParam.classLoader, "needDisplay", XC_MethodReplacement.returnConstant(false));
            findAndHookMethod("com.miui.calendar.card.single.custom.ad.LargeImageAdSingleCard", loadPackageParam.classLoader, "needDisplay", XC_MethodReplacement.returnConstant(false));
            Class<?> clsC = XposedHelpers.findClass("com.xiaomi.ad.internal.common.module.a$c", loadPackageParam.classLoader);
            if (clsC != null) {
                findAndHookMethod("com.xiaomi.ad.internal.common.module.a", loadPackageParam.classLoader, "b", clsC, XC_MethodReplacement.returnConstant(null));
            }
            findAndHookMethod("com.xiaomi.ad.common.pojo.Ad", loadPackageParam.classLoader, "parseJson", JSONObject.class, XC_MethodReplacement.returnConstant(null));
            findAndHookMethod("com.xiaomi.ad.internal.a.e", loadPackageParam.classLoader, "onAdInfo", String.class, XC_MethodReplacement.returnConstant(null));
            findAndHookMethod("com.miui.calendar.util.DiskStringCache", loadPackageParam.classLoader, "getString", Context.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    String key = (String) param.args[1];
                    if (key.startsWith("bottom_banner_is_closed_today")) {
                        param.setResult("true");
                    }
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    String key = (String) param.args[1];
                    if (key.startsWith("bottom_banner_is_closed_today")) {
                        param.setResult("true");
                    }
                }
            });
            return;
        }

        //文件管理器
        if (loadPackageParam.packageName.equals("com.android.fileexplorer")) {
            if (prefs.getBoolean("EnableFileManager", false)) {

                findAndHookMethod("com.android.fileexplorer.model.ConfigHelper", loadPackageParam.classLoader, "supportAd", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.fileexplorer.video.upload.VideoItemManager", loadPackageParam.classLoader, "initLoad", new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        return null;
                    }
                });
                findAndHookMethod("com.android.fileexplorer.model.Config", loadPackageParam.classLoader, "isStickerEnabled", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.fileexplorer.model.Config", loadPackageParam.classLoader, "isVideoEnabled", XC_MethodReplacement.returnConstant(false));
            }
        }

        //音乐
        if (loadPackageParam.packageName.equals("com.miui.player")) {
            if (prefs.getBoolean("EnableMusic", false)) {

                XposedHelpers.findAndHookMethod("com.miui.player.util.AdUtils", loadPackageParam.classLoader, "isAdEnable", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.miui.player.util.ExperimentsHelper", loadPackageParam.classLoader, "isAdEnabled", XC_MethodReplacement.returnConstant(false));

                findAndHookMethod("com.miui.player.phone.view.NowplayingAlbumPage", loadPackageParam.classLoader, "getPlayAd", XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.miui.player.util.Configuration", loadPackageParam.classLoader, "isCmTest", XC_MethodReplacement.returnConstant(true));

                findAndHookMethod("com.miui.player.hybrid.feature.GetAdInfo", loadPackageParam.classLoader, "addAdQueryParams", Context.class, Uri.class, XC_MethodReplacement.returnConstant(""));
                findAndHookMethod("com.miui.player.display.view.cell.BannerAdItemCell", loadPackageParam.classLoader, "onFinishInflate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
                        View vThis = (View) param.thisObject;
                        try {
                            ((View) XposedHelpers.getObjectField(param.thisObject, "mClose")).setVisibility(View.GONE);
                        } catch (Throwable t) {
                            XposedBridge.log(t.getMessage());
                        }
                        try {
                            ((View) XposedHelpers.getObjectField(param.thisObject, "mImage")).setVisibility(View.GONE);
                        } catch (Throwable t) {
                            XposedBridge.log(t.getMessage());
                        }
                        ViewGroup.LayoutParams lp = vThis.getLayoutParams();
                        lp.height = 0;
                        vThis.setLayoutParams(lp);
                    }
                });
                // 2.7.300
                findAndHookMethod("com.miui.player.content.MusicHybridProvider", loadPackageParam.classLoader, "parseCommand", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        String scheme = (String) param.args[0];
                        if (Objects.equals(scheme, "advertise")) {
                            param.args[0] = "";
                        }
                    }
                });
                // 2.7.400
                XposedHelpers.findAndHookMethod("com.miui.player.phone.view.NowplayingContentView", loadPackageParam.classLoader, "setInfoVisibility", java.lang.Boolean.TYPE, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        param.args[0] = true;
                    }
                });

                Class<?> clsAdShowingListener = XposedHelpers.findClass("com.miui.player.phone.view.NowplayingAlbumPage$AdShowingListener", loadPackageParam.classLoader);
                if (clsAdShowingListener != null) {
                    findAndHookMethod("com.miui.player.phone.view.NowplayingAlbumView", loadPackageParam.classLoader, "setAdShowingListener", clsAdShowingListener, XC_MethodReplacement.returnConstant(null));
                }
            }
            return;
        }


        //下载管理
        if (loadPackageParam.packageName.equals("com.android.providers.downloads.ui")) {
            if (prefs.getBoolean("EnableDownload", false)) {
                findAndHookMethod("com.android.providers.downloads.ui.utils.CloudConfigHelper", loadPackageParam.classLoader, "getAdButtonType", XC_MethodReplacement.returnConstant(0));
                findAndHookMethod("com.android.providers.downloads.ui.utils.CloudConfigHelper", loadPackageParam.classLoader, "isShouldShowAppSubject", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.providers.downloads.ui.utils.CloudConfigHelper", loadPackageParam.classLoader, "isShouldShowExtraAd", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.providers.downloads.ui.utils.CloudConfigHelper", loadPackageParam.classLoader, "isShouldShowRecommendInfo", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.providers.downloads.ui.utils.CloudConfigHelper", loadPackageParam.classLoader, "isStableShowActivateNotify", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.providers.downloads.ui.utils.CloudConfigHelper", loadPackageParam.classLoader, "supportRank", XC_MethodReplacement.returnConstant(false));
            }
        }

        //天气
        if (loadPackageParam.packageName.equals("com.miui.weather2")) {
            if (prefs.getBoolean("EnableWeather", false)) {
                findAndHookMethod("com.miui.weather2.structures.DailyForecastAdData", loadPackageParam.classLoader, "isAdInfosExistence", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.miui.weather2.structures.DailyForecastAdData", loadPackageParam.classLoader, "isAdTitleExistence", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.miui.weather2.structures.DailyForecastAdData", loadPackageParam.classLoader, "isLandingPageUrlExistence", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.miui.weather2.structures.DailyForecastAdData", loadPackageParam.classLoader, "isUseSystemBrowserExistence", XC_MethodReplacement.returnConstant(false));
                // 8.2.1
                findAndHookMethod("com.miui.weather2.WeatherApplication", loadPackageParam.classLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Context ctx = (Context) param.args[0];
                        SharedPreferences pref = ctx.getSharedPreferences("com.miui.weather2.information", 0);
                        pref.edit().putBoolean("agree_to_have_information", false).apply();
                    }
                });
                return;
            }
        }

        //个性主题
        if (loadPackageParam.packageName.equals("com.android.thememanager")) {
            if (prefs.getBoolean("EnableTheme", false)) {
                findAndHookMethod("com.android.thememanager.model.AdInfo", loadPackageParam.classLoader, "parseAdInfo", String.class, XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.android.thememanager.model.AdInfo", loadPackageParam.classLoader, "isSupported", "com.android.thememanager.model.AdInfo", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.thememanager.view.AdBannerView", loadPackageParam.classLoader, "showAdMark", new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                            throws Throwable {
                        paramAnonymousMethodHookParam.args[0] = false;
                    }
                });

                Class<?> clsPageItem = XposedHelpers.findClass("com.android.thememanager.model.PageItem", loadPackageParam.classLoader);
                if (clsPageItem != null) {
                    findAndHookMethod("com.android.thememanager.controller.online.PageItemViewConverter", loadPackageParam.classLoader, "buildAdView", clsPageItem, XC_MethodReplacement.returnConstant(null));
                }

                Class<?> clsHybridView = XposedHelpers.findClass("miui.hybrid.HybridView", loadPackageParam.classLoader);
                if (clsHybridView != null) {
                    findAndHookMethod("com.android.thememanager.h5.ThemeHybridFragment$BaseWebViewClient", loadPackageParam.classLoader, "shouldInterceptRequest", clsHybridView, String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            String url = (String) param.args[1];
                            if (url.contains("AdCenter")) {
                                param.args[1] = "http://127.0.0.1/";
                            }
                        }
                    });
                }
                findAndHookMethod("com.android.thememanager.util.ApplicationHelper", loadPackageParam.classLoader, "isFreshMan", XC_MethodReplacement.returnConstant(false));
                findAndHookMethod("com.android.thememanager.util.ApplicationHelper", loadPackageParam.classLoader, "hasFreshManMarkRecord", Context.class, XC_MethodReplacement.returnConstant(false));
                return;
            }
        }

        // 短信
        if (loadPackageParam.packageName.equals("com.android.mms")) {
            if (prefs.getBoolean("EnableMMS", false)) {

                findAndHookMethod("com.android.mms.util.SmartMessageUtils", loadPackageParam.classLoader, "isMessagingTemplateAllowed", Context.class, new XC_MethodHook() {
                    protected void beforeHookedMethod(MethodHookParam paramAnonymousMethodHookParam)
                            throws Throwable {
                        Context mc = (Context) paramAnonymousMethodHookParam.args[0];
                        XposedBridge.log("短信 当前类：" + mc.getClass().getName().toLowerCase());
                        if (mc.getClass().getName().toLowerCase().contains("app")) {
                            paramAnonymousMethodHookParam.setResult(false);
                            XposedBridge.log("返回false");
                        } else {
                            paramAnonymousMethodHookParam.setResult(true);
                            XposedBridge.log("返回true");
                        }
                    }
                });
                findAndHookMethod("com.android.mms.ui.SingleRecipientConversationActivity", loadPackageParam.classLoader, "showMenuMode",boolean.class,  XC_MethodReplacement.returnConstant(null));
                findAndHookMethod("com.android.mms.util.MiStatSdkHelper", loadPackageParam.classLoader, "recordBottomMenuShown", String.class, XC_MethodReplacement.returnConstant(null));
            }
        }
    }

}


