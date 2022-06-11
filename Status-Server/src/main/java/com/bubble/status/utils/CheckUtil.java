package com.bubble.status.utils;

import com.bubble.status.exceptions.CommonException;

public class CheckUtil {
    public static boolean isSame(String s1, String s2) {
        return s1.equals(s2);
    }
    public static boolean isNotSame(String s1, String s2) {
        return !isSame(s1, s2);
    }

    public static void check(boolean mustSatisfiedCondition, String unsatisfiedMessage) {
        if (!mustSatisfiedCondition) throw new CommonException(unsatisfiedMessage);
    }
}
