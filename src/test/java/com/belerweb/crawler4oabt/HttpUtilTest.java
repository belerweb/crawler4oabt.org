package com.belerweb.crawler4oabt;

import org.junit.Test;


public class HttpUtilTest {

  @Test
  public void testGet() {
    try {
      System.out.println(HttpUtil.get("http://www.baidu.com"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
