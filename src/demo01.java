import java.io.*;

public class demo01 {

    public static void main(String[] args) {
        try {
            File file1 = new File("./resource/论文初稿.txt");
            InputStream fs1 = new FileInputStream(file1);
            BufferedInputStream bf1 = new BufferedInputStream(fs1);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("././resource/testOut.txt"));
            byte[] bytes = new byte[256];
            int len;
            while ((len = bf1.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
            }
            outputStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

