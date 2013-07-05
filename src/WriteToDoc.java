import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Y.Lucia
 * Mail: ylnb91@gmail.com
 * Date: 13-7-5
 * Time: 下午12:35
 */
public class WriteToDoc {

    private ArrayList<String> allUrl = new ArrayList<String>();
    private StringBuilder sb = new StringBuilder();
    private int threadcount = 10;
    public static final Object signal = new Object();

    public WriteToDoc(ArrayList<String> allurlSet) {
        Collections.copy(this.allUrl, allurlSet);
    }

    public WriteToDoc(ArrayList<String> allurlSet, int threadcount) {
        Collections.copy(this.allUrl, allurlSet);
        this.threadcount = threadcount;
    }

    public void setAllUrl(ArrayList<String> allurlSet) {
        Collections.copy(allUrl, allurlSet);
    }

    public void setThreadcount(int threadcount) {
        this.threadcount = threadcount;
    }

    private synchronized String getUrl() {
        if (allUrl.isEmpty())
            return null;
        else {
            String s = allUrl.get(0);
            allUrl.remove(0);
            return s;
        }
    }

    private synchronized void addInfo(StringBuffer s) {
        sb.append(s);
    }

    public void run() {
        for (int i = 0; i < threadcount; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        String s = getUrl();
                        if (s == null) {
                            synchronized (signal) {
                                try {
                                    signal.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else
                            readFromUrl(s);
                    }
                }
            }).run();
        }
    }

    private void readFromUrl(String sUrl) {
        URL url;
        try {
            url = new URL(sUrl);
            URLConnection urlconnection = url.openConnection();
            urlconnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
            InputStream is = url.openStream();
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is, "gb2312"));
            StringBuffer sb = new StringBuffer();//sb为爬到的网页内容
            String rLine = null;
            while ((rLine = bReader.readLine()) != null)
                sb.append(rLine);
            getInfo(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getInfo(String context) {
        String regex = "<dt>心　　得：</dt><dd>.*</dd>";
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(context);
        StringBuffer strB = new StringBuffer();
        while (mt.find()) {
            strB.append(mt.group().replaceAll("<dt>心　　得：</dt><dd>|</dd>","")).append("/r/n");
        }
        addInfo(strB);
    }

    public boolean write(){
        System.out.print(sb);
        return true;
    }

}
