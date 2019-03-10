import IndexNSELive.NSEBNDController;

import java.io.IOException;
import java.text.ParseException;

public class Executor {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        for (int i = 0; i <10000 ; i++) {
           try {
               System.out.println("Thread waiting for 2 minutes");
               Thread.sleep(80000);
               new NSEBNDController().execute(0);
               System.out.println("Count : " + i);
           }
           catch (Exception e){
               System.out.println("Exception catched");
           }
        }
    }
}
