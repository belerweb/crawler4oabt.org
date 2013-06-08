package com.belerweb.crawler4oabt;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.DeflateDecompressingEntity;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

/**
 * HTTP 工具类
 */
public class HttpUtil {

  public static final String[] UA_AGENTS =
      new String[] {
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:21.0) Gecko/20100101 Firefox/21.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:20.0) Gecko/20100101 Firefox/20.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:19.0) Gecko/20100101 Firefox/19.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:18.0) Gecko/20100101 Firefox/18.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:17.0) Gecko/20100101 Firefox/17.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:15.0) Gecko/20100101 Firefox/15.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:14.0) Gecko/20100101 Firefox/14.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:13.0) Gecko/20100101 Firefox/13.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:12.0) Gecko/20100101 Firefox/12.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:11.0) Gecko/20100101 Firefox/11.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:10.0) Gecko/20100101 Firefox/10.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:9.0) Gecko/20100101 Firefox/9.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:8.0) Gecko/20100101 Firefox/8.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:7.0) Gecko/20100101 Firefox/7.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:6.0) Gecko/20100101 Firefox/6.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:5.0) Gecko/20100101 Firefox/5.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:4.0) Gecko/20100101 Firefox/4.0",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.28) Gecko/20100101 Firefox/3.6.28",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.27) Gecko/20100101 Firefox/3.6.27",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.26) Gecko/20100101 Firefox/3.6.26",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.25) Gecko/20100101 Firefox/3.6.25",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.24) Gecko/20100101 Firefox/3.6.24",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.23) Gecko/20100101 Firefox/3.6.23",
          "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:3.6.22) Gecko/20100101 Firefox/3.6.22",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.2; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.2; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 6.2; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.2; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.2; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 6.2; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 5.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 5.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 5.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 5.1; WOW64; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 5.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 5.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/6.0; Touch)",
          "Mozilla/5.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/6.0; Touch)",
          "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.2; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.2; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.2; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.2; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.2; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.2; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.2; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 5.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; WOW64; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 10.0; Windows NT 5.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 9.0; Windows NT 5.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)",
          "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/6.0; Touch; .NET4.0E; .NET4.0C; Tablet PC 2.0; InfoPath.3)"};

  /**
   * GET 请求，随机User-Agent，不保存COOKIE
   * 
   * @param url 请求网址
   * @return 网页HTML代码
   * @throws IOException
   * @throws ClientProtocolException
   */
  public static String get(String url) throws ClientProtocolException, IOException {
    HttpGet request = new HttpGet(url);
    request.addHeader(HttpHeaders.ACCEPT,
        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
    request.addHeader(HttpHeaders.REFERER, url);
    request.addHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate");
    request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-cn;q=0.5");
    request.addHeader(HttpHeaders.CACHE_CONTROL, "max-age=0");
    request.addHeader(HttpHeaders.CONNECTION, "keep-alive");
    HttpResponse response = newHttpClient().execute(request);
    int statusCode = response.getStatusLine().getStatusCode();
    HttpEntity entity = response.getEntity();
    String html = IOUtils.toString(entity.getContent(), getContentEncoding(response));
    if (statusCode != HttpStatus.SC_OK) {
      throw new RuntimeException(statusCode + ":\n" + html);
    }
    return html;
  }

  /**
   * 创建一个 HttpClient
   * 
   * @return HttpClient
   */
  public static HttpClient newHttpClient() {
    HttpParams params = new BasicHttpParams();
    params.setParameter(CoreProtocolPNames.USER_AGENT, randomUA());
    params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, randomUA());
    HttpConnectionParams.setConnectionTimeout(params, 30);
    HttpConnectionParams.setSoTimeout(params, 30);
    DefaultHttpClient client = new DefaultHttpClient(params);
    client.addResponseInterceptor(new HttpResponseInterceptor() {
      public void process(final HttpResponse response, final HttpContext context)
          throws HttpException, IOException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
          Header ceheader = entity.getContentEncoding();
          if (ceheader != null) {
            HeaderElement[] codecs = ceheader.getElements();
            for (int i = 0; i < codecs.length; i++) {
              if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                response.setEntity(new GzipDecompressingEntity(response.getEntity()));
                return;
              }
              if (codecs[i].getName().equalsIgnoreCase("deflate")) {
                response.setEntity(new DeflateDecompressingEntity(response.getEntity()));
                return;
              }
            }
          }
        }
      }
    });
    return client;
  }

  /**
   * 获取随机 User-Agent
   * 
   * @return 随机 User-Agent
   */
  public static String randomUA() {
    return UA_AGENTS[(int) Math.random() * UA_AGENTS.length];
  }

  private static String getContentEncoding(HttpResponse response) {
    Header encoding = response.getEntity().getContentEncoding();
    if (encoding != null) {
      return encoding.getValue();
    }

    Header contentType = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
    if (contentType != null && contentType.getValue().contains("=")) {
      return contentType.getValue().split("=")[1];
    }

    return "ISO8859-1";
  }

}
