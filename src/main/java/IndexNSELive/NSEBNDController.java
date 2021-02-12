package IndexNSELive;

import ElasticSearch.ElasticAlert;
import ElasticSearch.ElasticSearchUtil;
import IndexDataGathering.DBControllerIndex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import util.ParseJSON;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class NSEBNDController {

    public static void main(String  [] args) throws InterruptedException, ParseException, IOException {
//        new NSEBNDController().execute(0);
    }

    public void execute(int sleepTime) throws IOException, InterruptedException, ParseException {
//Thread.sleep(sleepTime*1000);

        double oiCE = 0;
        double oiPE = 0;
        double chgOiCE = 0;
        double chgOiPE = 0;
        double volCE = 0;
        double volPE = 0;

        double oiPcr=0;
        double chgOiPcr=0;
        double volPcr=0;

//        Map<String, Map<String, String>> map = new NSEBankNiftyFetcher().getMappedData("https://www1.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbolCode=-9999&symbol=BANKNIFTY&symbol=BANKNIFTY&instrument=OPTIDX&date=-&segmentLink=17&segmentLink=17");
//        Map<String,Map<String,String>> map= ParseJSON.getMap("https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY");
//        Map<String,Map<String,String>> map= ParseJSON.getMap("https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY&expiryDate=");
//        Map<String, Map<String, String>> map = new MCBNODFetcher().getMappedData("https://www.moneycontrol.com/stocks/fno/view_option_chain.php?ind_id=23&sel_exp_date=20190725");
//            Double bnCurrentValue=new NSEBankNiftyFetcher().getBnCurrentValue();
//        Double bnCurrentValue=new NSEBankNiftyFetcher().getBnCurrentValue();
        Map<String,Map<String,String>> map = null;
//        Map<String, Map<String, String>> map = new NSEBankNiftyFetcher().getMappedData("https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbolCode=-10003&symbol=NIFTY&symbol=NIFTY&instrument=OPTIDX&date=-&segmentLink=17&segmentLink=17");
        for (int i = 0; i <20 ; i++) {
            System.out.println("Trying api call");
            Thread.sleep(1000);
            try {
                map = ParseJSON.getMap("https://www.nseindia.com/api/option-chain-indices?symbol=BANKNIFTY");
                break;
            }catch (Exception e){
                System.out.println("API Call failed!! "+e.toString());
            }
        }

        Double bnCurrentValue=new DecimalFormat().parse(ParseJSON.getBnCurrentValue()).doubleValue();
        new ElasticSearchUtil().clearElastChartData("bnbarchart");

            for (String key : map.keySet()) {
                if (!key.contains("null")) {
                    Map<String, String> internalMap = map.get(key);
                    System.out.println(internalMap.get("strikePrice"));
//                    decimalFormat.parse((String) map.get("LTP")).doubleValue();

                    Double strikePriceValue = new DecimalFormat().parse(internalMap.get("strikePrice")).doubleValue();
//                    Double bnCurrentVal=new NSEBankNiftyFetcher().getBnCurrentValue();
//                    if((strikePriceValue-bnCurrentValue>=-1900)&&(strikePriceValue-bnCurrentValue<=1900))
                    if ((bnCurrentValue - strikePriceValue >= -1200) && (bnCurrentValue - strikePriceValue <= 1200)) {
                        new ElasticSearchUtil().storeDataElasticSearchNSEBN(key, internalMap, "bnnseoidata", "bnotm", "bnotmratio","bnbarchart");


                        Map valueMap = map.get(key);

                        // Logic for PCR
                        if (key.contains("CE")) {
                            oiCE += (double) valueMap.getOrDefault("OI", 0.0);
                            chgOiCE += (double) valueMap.getOrDefault("Chng in OI", 0.0);
                            volCE += (double) valueMap.getOrDefault("Volume", 0.0);
                        } else {
                            oiPE += (double) valueMap.getOrDefault("OI", 0.0);
                            chgOiPE += (double) valueMap.getOrDefault("Chng in OI", 0.0);
                            volPE += (double) valueMap.getOrDefault("Volume", 0.0);
                        }
                    }
                }
            }
        oiPcr=oiPE/oiCE;
        chgOiPcr=chgOiPE/chgOiCE;
        volPcr=volPE/volCE;
        //Construct map
        Map pcrMap= new HashMap();
        pcrMap.put("BN Current",bnCurrentValue);
        pcrMap.put("OI PCR",oiPcr);
        pcrMap.put("CHG OI PCR",chgOiPcr);
        pcrMap.put("Vol PCR",volPcr);
        pcrMap.put("timestamp",System.currentTimeMillis());

        new ElasticSearchUtil().putData(new ObjectMapper().writeValueAsString(pcrMap),"bnpcrindex");

        }
    public void executeNifty(int sleepTime) throws IOException, InterruptedException, ParseException {
//Thread.sleep(sleepTime*1000);
        Map<String,Map<String,String>> map = null;
//        Map<String, Map<String, String>> map = new NSEBankNiftyFetcher().getMappedData("https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbolCode=-10003&symbol=NIFTY&symbol=NIFTY&instrument=OPTIDX&date=-&segmentLink=17&segmentLink=17");
        for (int i = 0; i <20 ; i++) {
            System.out.println("Trying api call");
            Thread.sleep(1000);
            try {
                map = ParseJSON.getMap("https://www.nseindia.com/api/option-chain-indices?symbol=NIFTY");
                break;
            }catch (Exception e){
                System.out.println("API Call failed!! "+e.toString());
            }
        }
        
//        Double bnCurrentValue=new NSEBankNiftyFetcher().getBnCurrentValue();
        Double bnCurrentValue=new DecimalFormat().parse(ParseJSON.getBnCurrentValue()).doubleValue();
        double oiCE = 0;
        double oiPE = 0;
        double chgOiCE = 0;
        double chgOiPE = 0;
        double volCE = 0;
        double volPE = 0;

        double oiPcr=0;
        double chgOiPcr=0;
        double volPcr=0;

        DecimalFormat decimalFormat= new DecimalFormat();
        new ElasticSearchUtil().clearElastChartData("niftybarchart");
        for (String key : map.keySet()) {
            if(!key.contains("null")) {
                Map<String, String> internalMap = map.get(key);
                System.out.println(internalMap.get("strikePrice"));
//                    decimalFormat.parse((String) map.get("LTP")).doubleValue();

                Double strikePriceValue = new DecimalFormat().parse(internalMap.get("strikePrice")).doubleValue();
                // Logic to control range of data
                if ((bnCurrentValue - strikePriceValue > -500) && (bnCurrentValue - strikePriceValue < 500)) {
//                    Double bnCurrentVal=new NSEBankNiftyFetcher().getBnCurrentValue();
//                    if((strikePriceValue-bnCurrentValue>=-1900)&&(strikePriceValue-bnCurrentValue<=1900))
                    new ElasticSearchUtil().storeDataESNifty(key, internalMap, "niftyoidata", "niftyotm", "niftyotmratio","niftybarchart");

                    Map valueMap = map.get(key);

                    // Logic for PCR
                    if (key.contains("CE")) {
                        oiCE += (double) valueMap.getOrDefault("OI", 0.0);
                        chgOiCE += (double) valueMap.getOrDefault("Chng in OI", 0.0);
                        volCE += (double) valueMap.getOrDefault("Volume", 0.0);
                    } else {
                        oiPE += (double) valueMap.getOrDefault("OI", 0.0);
                        chgOiPE += (double) valueMap.getOrDefault("Chng in OI", 0.0);
                        volPE += (double) valueMap.getOrDefault("Volume", 0.0);
                    }

                }
            }

        }
        oiPcr=oiPE/oiCE;
        chgOiPcr=chgOiPE/chgOiCE;
        volPcr=volPE/volCE;
        //Construct map
        Map pcrMap= new HashMap();
        pcrMap.put("Nifty Current",bnCurrentValue);
        pcrMap.put("OI PCR",oiPcr);
        pcrMap.put("CHG OI PCR",chgOiPcr);
        pcrMap.put("Vol PCR",volPcr);
        pcrMap.put("timestamp",System.currentTimeMillis());

        new ElasticSearchUtil().putData(new ObjectMapper().writeValueAsString(pcrMap),"niftypcrindex");


    }
    }

