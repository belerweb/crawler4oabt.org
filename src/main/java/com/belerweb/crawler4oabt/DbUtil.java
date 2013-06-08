package com.belerweb.crawler4oabt;

/**
 * 数据库操作工具类
 * 
 */
public class DbUtil {

  private static int MAX_TID = 0;
  private static int TID = 0;

  public static int getMaxTid() {
    return MAX_TID;
  }

  public static void setMaxTid(int tid) {
    MAX_TID = tid;
  }

  public static int getTid() {
    return TID;
  }

  public static void setTid(int tid) {
    TID = tid;
  }

}
