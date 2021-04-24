import java.util.Scanner;

/**
 * Created by 76204 on 2017/7/10.
 */
public class TimeTest {
      public static void main(String[] args) {
          Scanner scanner=new Scanner(System.in);
          System.out.println("begin::::按 enter 开始");
          String next = scanner.nextLine();
              while (true){
                  long result = System.currentTimeMillis() % (2000);
                  if(result==0L){
                      System.out.println("time out...");
                      try {
                          Thread.sleep(1000);
                      } catch (InterruptedException e) {
                          e.printStackTrace();
                      }

                  }else{
//                      System.out.println(System.currentTimeMillis()+"wait.....");

                  }


              }
        }
}
