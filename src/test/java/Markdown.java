import com.tidfore.MarkdownUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 2018/2/7 0007
 * Time: 17:53
 * To change this template use File | Settings | File Templates.
 */
public class Markdown  {

    @Test
    public  void demo() throws IOException {
        MarkdownUtils.toHtml("C:\\Users\\Administrator\\Desktop\\笔记.md","C:\\Users\\Administrator\\Desktop\\笔记.html");
    }

    @Test
    public void demo1() throws IOException {
        String image = MarkdownUtils.base64Image("C:\\Users\\Administrator\\Desktop\\1.jpg");
        System.out.println(image);
    }


}
