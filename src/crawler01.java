import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class crawler01 {

    public static void main(String[] args) {
        URL url = null;
        URLConnection urlconn = null;
        BufferedReader br = null;
        PrintWriter pw = null;
        //url匹配规则
        String regex = "https://[\\w+\\.?/?]+\\.[A-Za-z]+";
        Pattern p = Pattern.compile(regex);
        try {
            //爬取的网址
            url = new URL("https://www.ifeng.com/");
            urlconn = url.openConnection();
            //将爬取到的链接放到文件中
            pw = new PrintWriter(new FileWriter("./resource/out/crawler01.txt"), true);
            br = new BufferedReader(new InputStreamReader(
                    urlconn.getInputStream()));
            String buf = null;
            while ((buf = br.readLine()) != null) {
                Matcher buf_m = p.matcher(buf);
                while (buf_m.find()) {
                    pw.println(buf_m.group());
                }
            }
            System.out.println("爬取成功^_^");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            pw.close();
        }
    }
}
