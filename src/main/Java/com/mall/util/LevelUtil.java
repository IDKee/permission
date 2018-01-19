package com.mall.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 传入父亲的等级，和父亲的id返回当前的等级
 * 比如： 自己父亲的等级是0.1 ,父亲的id是3
 * 返回0.1.3
 */
public class LevelUtil {

    public final static String SEPARATOR = ".";

    public final static String ROOT = "0";

    // 0
    // 0.1
    // 0.1.2
    // 0.1.3
    // 0.4
    public static String calculateLevel(String parentLevel, int parentId) {
        if (StringUtils.isBlank(parentLevel)) {
            return ROOT;
        } else {
            return StringUtils.join(parentLevel, SEPARATOR, parentId);
        }
    }
}
