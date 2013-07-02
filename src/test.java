import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: y.lucia
 * Date: 13-6-7
 * Time: 下午4:15
 * To change this template use File | Settings | File Templates.
 */
public class test {
    public static void main(String args[]) {

        URL url;
        int responsecode;
        HttpURLConnection urlConnection;
        BufferedReader reader;
        String line;
        try {

            url = new URL("http://www.sina.com.cn");
            urlConnection = (HttpURLConnection) url.openConnection();
            responsecode = urlConnection.getResponseCode();
            if (responsecode == 200) {
                reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "GBK"));
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            } else {
                System.out.println("获取不到网页的源码，服务器响应代码为：" + responsecode);
            }
        } catch (Exception e) {
            System.out.println("获取不到网页的源码,出现异常：" + e);
        }
    }

}
