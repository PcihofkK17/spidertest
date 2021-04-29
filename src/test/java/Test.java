/**
 * 匹配替换
 * Created by 76204 on 2017/7/17.
 */
public class Test {

    public static void main(String[] args) {
        System.out.println("A01B02".replaceAll("([A-Z])","$1|"));
    }
}
