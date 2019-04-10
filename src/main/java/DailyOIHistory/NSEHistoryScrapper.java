package DailyOIHistory;

import ElasticSearch.ElasticSearchUtil;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.text.ParseException;

public class NSEHistoryScrapper {
    public static double BNCurrentValue;
    public static void main(String[] args) throws InterruptedException, IOException, ParseException {
// First Clear the old data
//        new ElasticSearchUtil().clearElastChartData("bn_oi_history");

        // Adding time stamp
//        Response responseBnNse= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"_doc\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}}")
//        .put("http://localhost:9200/incpriincoitop");
//        System.out.println(responseBnNse.prettyPrint());
//
//        Response responseIncTop= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"_doc\":{\"properties\":{\"timestamp\":{\"type\":\"date\"}}}}}")
//        .put("http://localhost:9200/bnnseoidata");
//        System.out.println(responseIncTop.prettyPrint());

        Response responseBNOIHistory= RestAssured.given().contentType("application/json").body("{\"mappings\":{\"_doc\":{\"properties\":{\"Date\":{\"type\":\"date\"}}}}}").put("http://localhost:9200/bn_oi_history");
        System.out.println(responseBNOIHistory.prettyPrint());



String optionType[]={"ce","pe"};
        for (int i = 0; i <optionType.length ; i++) {
            boolean isSuccesfull = false;
            while (true) {
                try{isSuccesfull = execute(optionType[i]);}
                catch (Exception e){
                    System.out.println("Overall Exceptions Occured: ");
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
                if (isSuccesfull) break;
            }
        }
    }
    static boolean execute(String type) throws InterruptedException, IOException, ParseException {
WebDriver driver = null;
boolean isSuccessful = false;
        try{
            driver= new ChromeDriver();
            driver.get("https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbolCode=-9999&symbol=BANKNIFTY&symbol=BANKNIFTY&instrument=OPTIDX&date=-&segmentLink=17&segmentLink=17");
            String currentBNValue=driver.findElement(By.xpath("//*[@id=\"wrapper_btm\"]/table[1]/tbody/tr/td[2]/div/span[1]/b")).getText();
            System.out.println("Current Bank Nifty Value : "+currentBNValue);
            BNCurrentValue=Double.valueOf(currentBNValue.substring(currentBNValue.indexOf(" ")));
        driver.get("https://www.nseindia.com/products/content/derivatives/equities/historical_fo.htm");
        selectDropDown(driver,"instrumentType"," Index Options ");
        selectDropDown(driver,"symbol","BANK NIFTY");
        selectDropDown(driver,"year","2019");
        selectDropDown(driver,"expiryDate", Utils.returnNextExpiry());
        if(type.contains("ce"))
        selectDropDown(driver,"optionType","CE");
        else if(type.contains("pe"))
            selectDropDown(driver,"optionType","PE");

        driver.findElement(By.id("rdPeriod")).click();
        selectDropDown(driver,"dateRange","15 Days");
        driver.findElement(By.xpath("//*[@id=\"getButton\"]")).click();
        driver.findElement(By.xpath("//*[@id=\"getButton\"]")).click();
        Thread.sleep(20000);
        driver.manage().window().maximize();
        driver.findElement(By.xpath("//*[@id=\"historicalData\"]/div[1]/span[2]/a")).click();
        Thread.sleep(12000);
            isSuccessful=FileUtils.processFile();
        }
catch (Exception e) {
    System.out.println("Below Exception Occured: ");
    e.printStackTrace();
}
finally {
    Thread.sleep(2000);
    driver.close();
    driver.quit();
}

return isSuccessful;

    }

    static void selectDropDown(WebDriver driver,String dropDownName,String dropDownValue){
        Select dropDown=new Select(driver.findElement(By.id(dropDownName)));
        dropDown.selectByVisibleText(dropDownValue);
    }
}
