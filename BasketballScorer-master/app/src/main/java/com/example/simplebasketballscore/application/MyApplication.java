package com.example.simplebasketballscore.application;


import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;

public class MyApplication extends Application {

    /**
     * 提供一个Application级别的静态Context，方便一些不好获取context的类使用
     */
    private static Context context;
    /**
     * 提供一些static变量，方便activity间记录和使用
     */
    private static String teamA;
    private static String teamB;
    private static int perMin;
    private static Boolean isTwoOrFour;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this.getApplicationContext();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    public static Context getContext() {
        return context;
    }

    public static String getTeamA() {
        return teamA;
    }

    public static void setTeamA(String teamA) {
        MyApplication.teamA = teamA;
    }

    public static String getTeamB() {
        return teamB;
    }

    public static void setTeamB(String teamB) {
        MyApplication.teamB = teamB;
    }

    public static int getPerMin() {
        return perMin;
    }

    public static void setPerMin(int perMin) {
        MyApplication.perMin = perMin;
    }

    public static Boolean getIsTwoOrFour() {
        return isTwoOrFour;
    }

    public static void setIsTwoOrFour(Boolean isTwoOrFour) {
        MyApplication.isTwoOrFour = isTwoOrFour;
    }

}
