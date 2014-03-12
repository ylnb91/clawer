import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
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

    private ArrayList<String> allUrl = null;
    private StringBuffer sb = new StringBuffer();
    private int threadcount = 50;
    private int count = 0;

    public WriteToDoc(ArrayList<String> allurlSet) {
        allUrl = new ArrayList(Arrays.asList(new String[allurlSet.size()]));
        Collections.copy(this.allUrl, allurlSet);
    }

    public WriteToDoc(ArrayList<String> allurlSet, int threadcount) {
        allUrl = new ArrayList(Arrays.asList(new String[allurlSet.size()]));
        Collections.copy(this.allUrl, allurlSet);
        this.threadcount = threadcount;
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
                public void run() {
                    while (!Thread.currentThread().isInterrupted()) {
                        String s = getUrl();
                        if (s == null) {
                            count++;
                            Thread.currentThread().interrupt();
                        } else
                            readFromUrl(s);
                    }
                }
            }).start();
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
            StringBuffer sbr = new StringBuffer();//sb为爬到的网页内容
            String rLine = null;
            while ((rLine = bReader.readLine()) != null)
                sbr.append(rLine);
            getInfo(sbr.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getInfo(String context) {
        String regex = "<dt>心.*?得：</dt>\\s*<dd>.*?</dd>";
        Pattern pt = Pattern.compile(regex);
        Matcher mt = pt.matcher(context);
        StringBuffer strB = new StringBuffer();
        while (mt.find()) {
            strB.append(mt.group().replaceAll("<dt>心.*?得：</dt>\\s*<dd>|</dd>","")).append("/r/n");
        }

        addInfo(strB);
    }

    public boolean write(){
        while(true){
            if(count==threadcount){
                String path="e://a.txt";
                try {
                    FileWriter fw = new FileWriter(path,true);
                    BufferedWriter bw = new BufferedWriter(fw);
                    Pattern pt = Pattern.compile(".*?/r/n");
                    Matcher mt = pt.matcher(sb.toString());
                    while (mt.find()) {
                        bw.newLine();
                        bw.write((mt.group().replaceAll("/r/n","")));

                    }
                    bw.close();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.print("end");
                return true;
            }
      }
    }

}
