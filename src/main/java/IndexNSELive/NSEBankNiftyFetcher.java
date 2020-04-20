package IndexNSELive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NSEBankNiftyFetcher {

    static List<String> headersList= new LinkedList<String>();
    static List<List<String>> rowData= new LinkedList<List<String>>();
    static Double bnCurrentValue;




    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(new ObjectMapper().writeValueAsString(new NSEBankNiftyFetcher().getMappedData("https://www.nseindia.com/live_market/dynaContent/live_watch/option_chain/optionKeys.jsp?symbolCode=-10003&symbol=NIFTY&symbol=NIFTY&instrument=OPTIDX&date=-&segmentLink=17&segmentLink=17")));
    }
    public Double getBnCurrentValue(){
        return bnCurrentValue;
    }
    public Map<String,Map<String, String>> getMappedData(String url) throws IOException, ParseException {
        Document doc;
        doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.108 Safari/537.36").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_inc_p_inc/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=1").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_dec_p_dec/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=0").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_dec_p_dec/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=0").get();
    Elements bnCurrentValueElem=doc.select("#wrapper_btm > table:nth-child(3) > tbody > tr > td:nth-child(2) > div > span:nth-child(1) > b");
    if(url.contains("BANK"))
    bnCurrentValue=Double.valueOf(bnCurrentValueElem.text().replace("BANKNIFTY","").replace(" ",""));
    else
        bnCurrentValue=Double.valueOf(bnCurrentValueElem.text().replace("NIFTY","").replace(" ",""));

        System.out.println("Bank Nifty Current Value: "+bnCurrentValue);

        for (Element table : doc.select("#octable")) {

            for (Element trElement : table.getElementsByTag("tr")) {
                int trCount=0;
                List<String> internalOneRow=new LinkedList<String>();

                 if(trElement.getElementsByTag("th")!=null){
    for (Element th:trElement.getElementsByTag("th")){

//        if(trCount++==0 || trCount++==1 || trCount++==3 || trCount++==23)
//            continue;
        String headerText=th.text();
        if(headerText.contains(".")){
            headerText=headerText.replace(".","");
        }
        if(headerText.contains("(")){
            headerText=headerText.replace("(","");
        }
        if(headerText.contains(")")){
            headerText=headerText.replace(")","");
        }
        switch (headerText){

            case "":
                break;
            case "CALLS":
                break;

            case "PUTS":
                break;
           case "Chart":
               break;
               default:
                   headersList.add(headerText);
        }


        System.out.print(th.text()+"  ");
    }
                }
                    for (Element tdElement : trElement.getElementsByTag("td")) {
                        String intText=tdElement.text();
                        System.out.print(tdElement.text() + "  ");
                        if(intText.contains("%")){
                            intText=intText.replace("%","");
                        }
                        switch (intText){
                            case "":
                                break;

                                default:
                                    internalOneRow.add(intText);
                        }

                    }
                    System.out.println();

                    rowData.add(internalOneRow);
                }
        }
//1 Printing the header list
        System.out.println("Header List after Web srapping: "+headersList);
        System.out.println("Rows List: "+rowData);

//
//
////2
//        System.out.println("Print List and Map Data");
//        for (List list:rowData){
//            for(Object list1:list){
//                System.out.print(list1+"   ");
//            }
//            System.out.println();
//        }
//3 Convert Collection to final Map
        Map<String,Map<String, String>> finalMap= new LinkedHashMap<String, Map<String, String>>();
        for (List list:rowData){
            int symbolCount=0;
//            List list1= new LinkedList();
            Map <String,String> internalmap= new LinkedHashMap<String, String>();
            int headerCount=0;
            Map callMap= new LinkedHashMap();
            Map putMap= new LinkedHashMap();
            int rowDataCount=0;
            for(Object list1:list){
                if(rowDataCount<10){
                    callMap.put(headersList.get(headerCount++),list1);
                    rowDataCount++;
                }
                else if(rowDataCount==10)
                {
                    callMap.put(headersList.get(headerCount),list1);
                    putMap.put(headersList.get(headerCount),list1);
                    headerCount++;
                    rowDataCount++;

                }
                else if(rowDataCount>10)
                {
                    putMap.put(headersList.get(headerCount++),list1);
                    rowDataCount++;
                }
//                internalmap.put(headersList.get(headerCount++), (String) list1);
//                System.out.print(list1+"   ");
            }

            Double strikePVCall = 0.0;
            Double strikePVPut=0.0;
try {
    strikePVCall = new DecimalFormat().parse((String) callMap.get("Strike Price")).doubleValue();
    strikePVPut = new DecimalFormat().parse((String) callMap.get("Strike Price")).doubleValue();
}
catch (NullPointerException e){
    System.out.println("Exception Occured");

}
int threasholdValue=500;
if(url.contains("symbol=NIFTY")){
    threasholdValue=500;
}
else{
    threasholdValue=1500;
}

            if(strikePVCall-bnCurrentValue<=threasholdValue &&strikePVCall-bnCurrentValue>=-threasholdValue) {
                finalMap.put(callMap.get("Strike Price") + " CE", callMap);
            }
            if(strikePVPut-bnCurrentValue>=-threasholdValue && strikePVPut-bnCurrentValue<=threasholdValue) {
                finalMap.put(putMap.get("Strike Price") + " PE", putMap);
            }

//            finalMap.put(internalmap.get("Symbol")+" "+internalmap.get("Strike Price")+" "+internalmap.get("Option Type"),internalmap);

            System.out.println();

        }
        System.out.println("Printing final map: "+finalMap);

//        System.out.println("Printing the final map: "+finalMap);
////4 Print the new Map
//
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("Print the new Map Data");
//
//        for(String map:finalMap.keySet()){
//            Map map1=finalMap.get(map);
//            for (Object key:map1.keySet()){
//                System.out.print("Key: "+key+" : "+" Value : "+map1.get(key)+"   ");
//            }
//            System.out.println();
//        }

        return finalMap;


    }
}