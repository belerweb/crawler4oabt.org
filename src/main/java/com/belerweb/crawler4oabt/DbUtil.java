package com.belerweb.crawler4oabt;

import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

/**
 * 数据库操作工具类
 * 
 */
public class DbUtil {

  private static final BasicDataSource DATA_SOURCE;

  private static Integer MAX_TID = null;
  private static Integer TID = null;

  public static int getMaxTid() throws SQLException {
    if (MAX_TID == null) {
      QueryRunner query = getQuery();
      Integer maxTid =
          Integer.parseInt(query.query("SELECT value FROM options WHERE name = 'oabt.org_max_tid'",
              new ScalarHandler<String>()));
      if (maxTid == null) {
        query.update("INSERT INTO options (name, value) VALUE ('oabt.org_max_tid', ?)", 0);
        maxTid = 0;
      }
      MAX_TID = maxTid;
    }
    return MAX_TID;
  }

  public static void setMaxTid(int tid) throws SQLException {
    getQuery().update("UPDATE options SET value = ? WHERE name = 'oabt.org_max_tid'", tid);
    MAX_TID = tid;
  }

  public static int getTid() throws SQLException {
    if (TID == null) {
      Integer tid =
          Integer.parseInt(getQuery().query(
              "SELECT MAX(extra_key) FROM videos WHERE extra = 'www.oabt.org'",
              new ScalarHandler<String>()));
      if (tid == null) {
        tid = 0;
      }
      TID = tid;
    }
    return TID;
  }

  public static void save(Data data) throws SQLException {
    Object[] params =
        new Object[] {UUID.randomUUID().toString(), data.getTag(), data.getName(),
            data.getMagnet(), data.getEd2k(), data.getThunder(), data.getTime(), data.getSize(),
            data.getDownload(), data.getAuthor(), data.getDescription(), data.getExtra(),
            data.getExtraKey()};
    int result =
        getQuery()
            .update(
                "INSERT INTO videos (id, tag, name, magnet, ed2k, thunder, created_time, file_size, download, author, description, extra, extra_key)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", params);
    if (result == 1) {
      TID = Integer.parseInt(data.getExtraKey());
    }
  }

  private static QueryRunner getQuery() {
    return new QueryRunner(DATA_SOURCE);
  }

  static {
    DATA_SOURCE = new BasicDataSource();
    DATA_SOURCE.setUrl(System.getProperty("db.url"));
    DATA_SOURCE.setUsername(System.getProperty("db.username"));
    DATA_SOURCE.setPassword(System.getProperty("db.password"));
  }

}
