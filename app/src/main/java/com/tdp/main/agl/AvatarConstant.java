package com.tdp.main.agl;

import com.tdp.main.R;
import java.io.File;

/**
 * Created by tujh on 2018/6/12.
 */
public abstract class AvatarConstant {

    private static final String[] EXPRESSION_BOY_BUNDLE = {"", ""};
    private static final String[] EXPRESSION_GIRL_BUNDLE = {"", ""};

    public static final int[] HAIR_BOY_RES = {R.mipmap.six_four};
    public static final String[] HAIR_BOY_BUNDLE = {"male_hair_basic.bundle"};
    public static final int[] HAIR_GIRL_RES = {R.mipmap.one_five_three};
    public static final String[] HAIR_GIRL_BUNDLE = {"female_hair_basic.bundle"};

    private static final int[] GLASSED_BOY_RES = {0};
    private static final String[] GLASSES_BOY_BUNDLE = {""};
    private static final int[] GLASSED_GIRL_RES = {0};
    private static final String[] GLASSES_GIRL_BUNDLE = {""};

    public static final int[] CLOTHES_BOY_RES = {R.mipmap.m_10, R.mipmap.m_10, R.mipmap.m_10, R.mipmap.m_10, R.mipmap.m_10};
    public static final String[] CLOTHES_BOY_BUNDLE = {"clothes_male_default.bundle","clothes_male_1.bundle","clothes_male_2.bundle","clothes_male_3.bundle","clothes_male_4.bundle"};
    public static final int[] CLOTHES_GIRL_RES = { R.mipmap.f_12,R.mipmap.f_12, R.mipmap.f_12, R.mipmap.f_12, R.mipmap.f_12};
    public static final String[] CLOTHES_GIRL_BUNDLE = {"clothes_female_default.bundle","clothes_female_2.bundle","clothes_female_1.bundle","clothes_female_3.bundle","clothes_female_4.bundle"};
   // private static final String BODY_BOY_BUNDLE = "female_body_basic.bundle";
    private static final String BODY_BOY_BUNDLE = "male_body_basic.bundle";
    private static final String BODY_GIRL_BUNDLE = "female_body_basic.bundle";

    private static final int[] FILTER_RES = {0};

    private static final String[] FILTER_BUNDLE = {""};

    public static String[] expressionBundle(int gender) {
        return gender == 0 ? EXPRESSION_BOY_BUNDLE : EXPRESSION_GIRL_BUNDLE;
    }

    public static int[] hairRes(int gender) {
        return gender == 0 ? HAIR_BOY_RES : HAIR_GIRL_RES;
    }

    public static String[] hairBundle(int gender) {
        return gender == 1 ? HAIR_BOY_BUNDLE : HAIR_GIRL_BUNDLE;
    }

    public static String[] hairBundle(String parent, int gender) {
        String[] hairs = gender == 0 ? HAIR_BOY_BUNDLE : HAIR_GIRL_BUNDLE;
        String[] hairParents = new String[hairs.length];
        for (int i = 0; i < hairs.length; i++) {
            hairParents[i] = parent + File.separator + hairs[i];
        }
        return hairParents;
    }

    public static int[] glassesRes(int gender) {
        return gender == 0 ? GLASSED_BOY_RES : GLASSED_GIRL_RES;
    }

    public static String[] glassesBundle(int gender) {
        return gender == 0 ? GLASSES_BOY_BUNDLE : GLASSES_GIRL_BUNDLE;
    }

    public static int[] clothesRes(int gender) {
        return gender == 0 ? CLOTHES_BOY_RES : CLOTHES_GIRL_RES;
    }

    public static String[] clothesBundle(int gender) {
        return gender == 1 ? CLOTHES_BOY_BUNDLE : CLOTHES_GIRL_BUNDLE;
    }

    public static String bodyBundle(int gender) {
        return gender == 1 ? BODY_BOY_BUNDLE : BODY_GIRL_BUNDLE;
    }

    public static int[] filterRes() {
        return FILTER_RES;
    }

    public static String[] filterBundle() {
        return FILTER_BUNDLE;
    }
}
