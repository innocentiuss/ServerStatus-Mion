package com.bubble.status.utils;

import com.bubble.status.exceptions.CommonException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckUtil {
    public static boolean isSame(String s1, String s2) {
        return s1.equals(s2);
    }
    public static boolean isNotSame(String s1, String s2) {
        return !isSame(s1, s2);
    }

    public static void check(boolean mustSatisfiedCondition, String unsatisfiedMessage, int httpCode) {
        if (!mustSatisfiedCondition) {
            log.warn("服务端检测到错误发生: " + unsatisfiedMessage);
            throw new CommonException(unsatisfiedMessage, httpCode);
        }
    }
}
