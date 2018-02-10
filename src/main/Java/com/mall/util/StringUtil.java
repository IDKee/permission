package com.mall.util;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 把一个字符串分割成一个list
 *
 */
public class StringUtil {

    public static List<Integer> splitToListInt(String str) {
        // Splitter通过逗号分隔，先trim ， 移除所有空的串转换成list
        // 1,2,3,4,, , -> 1,2,3,4
        List<String> strList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        return strList.stream().map(strItem -> Integer.parseInt(strItem)).collect(Collectors.toList());
    }
}
