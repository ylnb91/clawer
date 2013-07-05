import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: y.lucia
 * Date: 13-6-10
 * Time: 下午9:04
 */
public class Crawler {

    ArrayList<String> allurlSet = new ArrayList<String>();//所有的网页url，需要更高效的去重可以考虑HashSet
    ArrayList<String> notCrawlurlSet = new ArrayList<String>();//未爬过的网页url
    HashMap<String, Integer> depth = new HashMap<String, Integer>();//所有网页的url深度
    int crawDepth = 2; //爬虫深度
    int threadCount = 1; //线程数量
    int count = 0; //表示有多少个线程处于wait状态
    public static final Object signal = new Object();   //线程间通信变量

    public static void main(String[] args) {
        final Crawler wc = new Crawler();
        wc.addUrl("http://club.jd.com/review/761788-0-1-0.html", 1);
        long start = System.currentTimeMillis();
        System.out.println("开始爬虫.........................................");
        wc.begin();

        while (true) {
            if (wc.notCrawlurlSet.isEmpty() && Thread.activeCount() == 1 || wc.count == wc.threadCount) {
                long end = System.currentTimeMillis();
                System.out.println("总共爬了" + wc.allurlSet.size() + "个网页");
                for(String s:wc.allurlSet)
                System.out.println(s);
                System.out.println("总共耗时" + (end - start) / 1000 + "秒");
                System.exit(1);
//              break;
            }

        }
    }

    private void begin() {
        for (int i = 0; i < threadCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    while (true) {
                        String tmp = getAUrl();
                        if (tmp != null) {
                            crawler(tmp);
                        } else {
                            synchronized (signal) {
                                    try {
                                        count++;
//                                        System.out.println("当前有" + count + "个线程在等待");
                                        signal.wait();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                            }


                        }
                    }
                }
            }, "thread-" + i).start();
        }
    }

    public synchronized String getAUrl() {
        if (notCrawlurlSet.isEmpty())
            return null;
        String tmpAUrl;
        tmpAUrl = notCrawlurlSet.get(0);
        notCrawlurlSet.remove(0);
        return tmpAUrl;
    }


    public synchronized void addUrl(String url, int d) {
        System.out.println(url);
        notCrawlurlSet.add(url);
        allurlSet.add(url);
        depth.put(url, d);
    }


    public void crawler(String sUrl) {
        URL url;
        try {
            url = new URL(sUrl);
            URLConnection urlconnection = url.openConnection();
            urlconnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            InputStream is = url.openStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is,"gb2312"));
            StringBuffer sb = new StringBuffer();//sb为爬到的网页内容
            String rLine = null;
            while ((rLine = bReader.readLine()) != null) {
                sb.append(rLine);
                sb.append("/r/n");
            }

            int d = depth.get(sUrl);
//            System.out.println("爬网页" + sUrl + "成功，深度为" + d + " 是由线程" + Thread.currentThread().getName() + "来爬");
            if (d < crawDepth) {
                //解析网页内容，从中提取链接
                parseContext(sb.toString(), d + 1);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //从context提取url地址
    public void parseContext(String context, int dep) {
        String regex = "http://club.jd.com/review/761788-0-\\d*-0.html";
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(context);
        while (mt.find()) {
//            Matcher myurl = Pattern.compile("href=\".*?\"").matcher(
//                    mt.group());
//            while (myurl.find()) {
//                String str = myurl.group().replaceAll("href=\"|\"", "");
            String str =mt.group();
                if (str.contains("http:")) { //取出一些不是url的地址
                    if (!allurlSet.contains(str)) {
                        addUrl(str, dep);//加入一个新的url
                        if (count > 0) { //如果有等待的线程，则唤醒
                            synchronized (signal) {
                                count--;
                                signal.notify();
                            }
                        }
//                    }
                }
            }
        }
    }
}
