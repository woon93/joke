import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class demo01 {

    public static void main(String[] args) {
        File file1 = new File("./resource/test01.txt");
        File file2 = new File("E:/joke/resource/test01.txt");
        try {
            InputStream fs1 = new FileInputStream(file1);
            System.out.println(fs1.read());

            InputStream fs2 = new FileInputStream(file2);
            System.out.println(fs2.read());


            boolean eqs = fs1.equals(fs2);
            System.out.println(eqs);

            System.out.println(fs1.toString());
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }
}

