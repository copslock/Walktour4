package com.walktour.gui.main.model;

import android.content.Intent;

import com.walktour.Utils.WalkStruct;

/**
 * @author jinfeng.xie
 * @version 1.0.0
 * @date on 2018/8/27
 * @describe 菜单项
 */
public class TestType {
    public String name;
    public int iconRes;
    public Intent intent;
    public WalkStruct.SceneType type = WalkStruct.SceneType.Auto;

    public WalkStruct.SceneType getType() {
        return type;
    }

    public void setType(WalkStruct.SceneType type) {
        this.type = type;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    @Override
    public String toString() {
        return "TestType{" +
                "name='" + name + '\'' +
                ", iconRes=" + iconRes +
                ", intent=" + intent +
                '}';
    }
}
