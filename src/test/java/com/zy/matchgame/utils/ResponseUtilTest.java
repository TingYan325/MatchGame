package com.zy.matchgame.utils;

import org.junit.Test;

public class ResponseUtilTest {

    @Test
    public void test_response_Success() {
        ResponseUtil responseUtil = new ResponseUtil();

        System.out.println(responseUtil.response_Success("111"));
    }
}
