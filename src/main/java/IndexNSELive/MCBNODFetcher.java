package IndexNSELive;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;

public class MCBNODFetcher {

    static List<String> headersList= new LinkedList<String>();
    static List<List<String>> rowData= new LinkedList<List<String>>();
    static Double bnCurrentValue;




    public static void main(String[] args) throws IOException, ParseException {
        System.out.println(new ObjectMapper().writeValueAsString(new MCBNODFetcher().getMappedData("https://www.moneycontrol.com/stocks/fno/view_option_chain.php?ind_id=23&sel_exp_date=20190725")));
    }
    public Double getBnCurrentValue(){
        return bnCurrentValue;
    }
    public Map<String,Map<String, String>> getMappedData(String url) throws IOException, ParseException {
        Document doc;
        doc = Jsoup.connect(url).get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_inc_p_inc/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=1").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_dec_p_dec/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=0").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_dec_p_dec/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=0").get();
    Elements bnCurrentValueElem=doc.select("#optchain_div > div.MT10 > div.MB15 > div:nth-child(1) > div > p.r_28.FL > strong");
    if(url.contains("BANK"))
    bnCurrentValue=Double.valueOf(bnCurrentValueElem.text().replace("BANKNIFTY","").replace(" ",""));
    else
        bnCurrentValue=Double.valueOf(bnCurrentValueElem.text().replace("NIFTY","").replace(" ",""));

        System.out.println("Bank Nifty Current Value: "+bnCurrentValue);

// for loop to get the headers list
//        for (Element table : doc.select("#optchain_div > div.MT5 > table")) {
//            int trCount = 0;
//            for (Element trElement : table.getElementsByTag("tr")) {
//
//                List<String> internalOneRow = new LinkedList<String>();
//
//                if (trElement.getElementsByTag("th") != null) {
//                    for (Element th : trElement.getElementsByTag("th")) {
//
////        if(trCount++==0 || trCount++==1 || trCount++==3 || trCount++==23)
////            continue;
//                        String headerText = th.text();
//                        if (headerText.contains(".")) {
//                            headerText = headerText.replace(".", "");
//                        }
//                        if (headerText.contains("(")) {
//                            headerText = headerText.replace("(", "");
//                        }
//                        if (headerText.contains(")")) {
//                            headerText = headerText.replace(")", "");
//                        }
//                        switch (headerText) {
//
//                            case "":
//                                break;
//                            case "CALLS":
//                                break;
//
//                            case "PUTS":
//                                break;
//                            case "Chart":
//                                break;
//                            default:
//                                if (trCount++ > 2)
//                                    headersList.add(headerText);
//                        }
//
//                    }
//
//                }
//            }
//        }
        headersList= Arrays.asList("LTP","Net Chng","Volume","OI","Chng in OI","Strike Price","LTP","Net Chng","Volume","OI","Chng in OI");

// 2 For loop to get the data from another table

        for (Element table : doc.select("#optchain_div > div.MT5 > div > table")) {
            int trCount = 0;
            for (Element trElement : table.getElementsByTag("tr")) {
                List<String> internalOneRow=new LinkedList<String>();

                    for (Element tdElement : trElement.getElementsByTag("td")) {
                        String intText=tdElement.text();
                        System.out.print(tdElement.text() + "  ");
                        if(intText.contains("%")){
                            intText=intText.replace("%","");
                        }
                        if(intText.contains(",")){
                            intText=intText.replace(",","");
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
                if(rowDataCount<5){
                    callMap.put(headersList.get(headerCount++),list1);
                    rowDataCount++;
                }
                else if(rowDataCount==5)
                {
                    callMap.put(headersList.get(headerCount),list1);
                    putMap.put(headersList.get(headerCount),list1);
                    headerCount++;
                    rowDataCount++;

                }
                else if(rowDataCount>5)
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
            if(strikePVCall-bnCurrentValue<=600 &&strikePVCall-bnCurrentValue>=-400) {
                finalMap.put(callMap.get("Strike Price") + " CE", callMap);
            }
            if(strikePVPut-bnCurrentValue>=-600 && strikePVPut-bnCurrentValue<=400) {
                finalMap.put(putMap.get("Strike Price") + " PE", putMap);
            }

//            finalMap.put(internalmap.get("Symbol")+" "+internalmap.get("Strike Price")+" "+internalmap.get("Option Type"),internalmap);

            System.out.println();
            System.out.println("Printing final map: "+finalMap);
        }


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