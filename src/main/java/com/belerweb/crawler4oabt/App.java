package com.belerweb.crawler4oabt;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.impl.cookie.DateUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {
  private static final String[] DATE_FORMATE = new String[] {"yyyy-MM-dd HH:mm:ss"};

  public static void main(String[] args) {
    try {
      Document document = Jsoup.parse(HttpUtil.get("http://www.oabt.org/index.php?page=1"));
      Element list = document.select(".toplist").get(1);
      Element first = list.select("tbody").get(1);
      String rel = first.select("a").get(1).attr("rel");
      int maxTid = Integer.parseInt(rel);
      debug("获取到最新资源ID：" + maxTid);
      if (maxTid > DbUtil.getMaxTid()) {
        debug("最新资源ID比数据库纪录的ID（" + DbUtil.getMaxTid() + "）大，存入数据库中...");
        DbUtil.setMaxTid(maxTid);
      }
      for (int i = DbUtil.getTid(); i <= DbUtil.getMaxTid(); i++) {
        debug("开始获取资源：" + i);
        String html = HttpUtil.get(getTidUrl(i));
        if (html.contains("'资源不存在'")) {
          debug("资源不存在：" + i);
          continue;
        }

        debug("开始数据解析：" + i);
        Data data = new Data();
        Document doc = Jsoup.parse(html);
        Elements title = doc.select("div.component div.title");
        data.setTag(title.select("b").text().substring(7));
        Matcher matcher =
            Pattern.compile(
                "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) 由 ([^\\s]+) 发布 下载次数\\((\\d+)\\)")
                .matcher(title.select("span").text());
        if (matcher.find()) {
          data.setTime(DateUtils.parseDate(matcher.group(1), DATE_FORMATE));
          data.setAuthor(matcher.group(2));
          data.setDownload(Integer.parseInt(matcher.group(3)));
        }
        data.setName(doc.select("td.name p a").get(0).text());
        data.setSize(doc.select("td.name p em").text());
        data.setMagnet(doc.select("td.addres textarea").text());
        data.setEd2k(doc.select("td.ed2k input").attr("value"));
        data.setDescription(doc.select("td.description").html());
        data.setExtra("www.oabt.org");
        data.setExtraKey(String.valueOf(i));
        debug("数据解析结束：" + i);
        try {
          debug("数据存入数据库：" + i);
          DbUtil.save(data);
        } catch (Exception e) {
          debug("数据存入数据库失败：" + i + "。退出程序。");
          e.printStackTrace();
          System.exit(-1);
        }
        debug("资源" + i + "处理结束。");
        debug("===================================");
      }
    } catch (Exception e) {
      debug("未知错误，退出程序。");
      e.printStackTrace();
    }
  }

  private static String getTidUrl(int tid) {
    return "http://www.oabt.org/show.php?tid=" + tid;
  }

  private static void debug(String log) {
    System.out.println(new Date() + "\t" + log);
  }

}
