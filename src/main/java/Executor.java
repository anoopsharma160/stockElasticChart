import ElasticSearch.ElasticAlert;
import IndexNSELive.NSEBNDController;

import java.io.IOException;
import java.text.ParseException;

public class Executor {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        for (int i = 0; i <5000 ; i++) {
           try {
               System.out.println("Thread waiting for 2 minutes");
               Thread.sleep(70000);
               new NSEBNDController().execute(0);
               System.out.println("Count : " + i);
           }
           catch (Exception e){
               System.out.println("Exception catched");
               e.printStackTrace();
           }
// Generating the alert
            try {
                Thread.sleep(1000);
                ElasticAlert.triggerAlert(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis() - 100000));
            }
            catch (Exception e){
                System.out.println("Alert Exception occurred!!!!");
                e.printStackTrace();
            }

        }
    }
}
