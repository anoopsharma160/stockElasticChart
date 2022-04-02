import ElasticSearch.ElasticAlert;
import IndexNSELive.NSEBNDController;

import java.io.IOException;
import java.text.ParseException;

public class Executor {
    public static void main(String[] args) throws InterruptedException, ParseException, IOException {
        for (int i = 0; i <5000 ; i++) {

            System.out.println("Thread waiting for 70 seconds");
//               Thread.sleep(70000);
            Thread.sleep(900);
            NSEBNDController object=new NSEBNDController();
            // Call for BN
            try {
                object.execute(0);
            }

            catch (Exception e){
                System.out.println("Exception catched");
                e.printStackTrace();
            }
            try{
                // Call for NiftyR
                object.executeNifty(0);
                System.out.println("Count : " + i);
            }
            catch (Exception e){
                System.out.println("Exception catched");
                e.printStackTrace();
            }
//// Generating the alert
//            try {
//                Thread.sleep(1000);
//                ElasticAlert.triggerAlert(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis() - 100000),
//                        "2 Minute Alert",5000,-2000);
//
//                ElasticAlert.triggerAlert(String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis() - 600000),
//                        "10 Minute Alert",15000,-10000);
//            }
//            catch (Exception e){
//                System.out.println("Alert Exception occurred!!!!");
//                e.printStackTrace();
//            }
// Executing Inc OI Inc Pri Method
//            try{
////            new ESInsertionIncPri().execute(0);}
//            catch (Exception e){
//                System.out.println("Inc OI Inc Pri : Exception Occurred : Skipping this iteration");
//            }

        }
    }
}
