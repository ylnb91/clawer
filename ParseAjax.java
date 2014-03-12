import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 13-7-4
 * Time: 上午11:17
 * To change this template use File | Settings | File Templates.
 */
public class ParseAjax {

         public static void main(String[]  args){

             String context = null;
             try {
                 context = getComment();
             } catch (IOException e) {
                 e.printStackTrace();
             }
             System.out.println(context);
             String regex = "<dt>心得：<//dt><dd>.*<//dd>";
             Pattern pt = Pattern.compile(regex);
             Matcher mt = pt.matcher(context);
             while (mt.find()) {
                  String str = mt.group();
                 System.out.println(str);
             }


         }

    public static  String getComment() throws IOException {
        final WebClient webClient = new WebClient();
        URL url = new URL("http://item.jd.com/761788.html");
        HtmlPage page = webClient.getPage(url);
        List<HtmlAnchor> anchors = page.getAnchors();
        for(HtmlAnchor a: anchors){
            System.out.println(a.getHrefAttribute()+  " " +a.getTextContent());
        }
        HtmlAnchor anchor = page.getAnchorByText("全部评价(0)")  ;
        page = anchor.click();
        String pageContent = page.getWebResponse().getContentAsString();
        return pageContent;
    }

}
