package com.hcw2175.library.qrscan.decode;

/**
 * 解码结果规则校验器
 *
 * @author huchiwei
 * @since 1.0.0
 */
public interface DecodeMatcher {

    /**
     * 校验结果是否合法
     *
     * @param result
     * @return
     */
    boolean isMatch(String result);
}
