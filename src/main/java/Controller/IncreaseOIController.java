package Controller;


import IndexDataGathering.DBControllerIndex;
import IndexNSELive.NSEBNDController;
import MoneyControlDataFetcher.ESInsertionIncPri;
import NSEData.JSONReader;
import NSEData.NseChartController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequestMapping("/dashboard")
public class IncreaseOIController {

    @RequestMapping(value = "/IncPriIncOi/{id}",method = RequestMethod.GET)
    public void IncPriceInOI(@PathVariable("id") Integer id) throws InterruptedException, ParseException, IOException {
        System.out.println("Inside the method");
new ESInsertionIncPri().execute(id);
    }
    @RequestMapping(value = "/nse/{id}",method = RequestMethod.GET)
    void nseDataGathering(@PathVariable("id")Integer id) throws InterruptedException, IOException {
new JSONReader().execute(id);
    }
    @RequestMapping(value = "/indexOption/{id}",method = RequestMethod.GET)
    void indexOptionData(@PathVariable("id")Integer id) throws InterruptedException, IOException, ParseException {
        new DBControllerIndex().execute(id);
    }
    @RequestMapping(value = "/nsebnoi",method = RequestMethod.GET)
    void nsebnoidata() throws InterruptedException, ParseException, IOException {
        new NSEBNDController().execute(0);
    }

    @RequestMapping(value = "",method = RequestMethod.GET)
    public void checkSetup(){
        System.out.println("Get call is fine");
    }
}
