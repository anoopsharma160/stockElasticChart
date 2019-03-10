package MoneyControlDataFetcher;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MoneyControllerScrapper {
    static List<String> headersList;
    static List<List<String>> rowData;

    public static void main(String[] args) throws IOException {
        System.out.println(new MoneyControllerScrapper().getMappedData());
    }

    public Map<String,Map<String, String>> getMappedData() throws IOException {
        headersList= new LinkedList<>();
        rowData= new LinkedList<>();
        Document doc;
        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_inc_p_inc/homebody.php?opttopic=stkfut&optinst=stkfut&sel_mth=1&sort_order=1").get();

//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_inc_p_inc/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=1").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_dec_p_dec/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=0").get();
//        doc = Jsoup.connect("https://www.moneycontrol.com/stocks/fno/marketstats/futures/oi_dec_p_dec/homebody.php?opttopic=allfut&optinst=allfut&sel_mth=1&sort_order=0").get();

        for (Element table : doc.getElementsByTag("table")) {
            for (Element trElement : table.getElementsByTag("tr")) {
                List<String> internalOneRow=new LinkedList<String>();

                 if(trElement.getElementsByTag("th")!=null){
    for (Element th:trElement.getElementsByTag("th")){
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
        headersList.add(headerText);
        System.out.print(th.text()+"  ");
    }
                }
                    for (Element tdElement : trElement.getElementsByTag("td")) {
                        String intText=tdElement.text();
                        System.out.print(tdElement.text() + "  ");
                        if(intText.contains("%")){
                            intText=intText.replace("%","");
                        }
                        internalOneRow.add(intText);
                    }
                    System.out.println();
                    rowData.add(internalOneRow);
                }
        }

//2
        System.out.println("Print List and Map Data");
        for (List list:rowData){
            for(Object list1:list){
                System.out.print(list1+"   ");
            }
            System.out.println();
        }
//3 Convert Collection to final Map
        Map<String,Map<String, String>> finalMap= new LinkedHashMap<String, Map<String, String>>();
        int stockCount=0;
        for (List list:rowData){
            int symbolCount=0;
//            List list1= new LinkedList();
            Map <String,String> internalmap= new LinkedHashMap<String, String>();
            int headerCount=0;
            for(Object list1:list){
                internalmap.put(headersList.get(headerCount++), (String) list1);
//                System.out.print(list1+"   ");
            }
            if(stockCount++<=20)
            finalMap.put(internalmap.get("Symbol"),internalmap);
            System.out.println();

        }
//4 Print the new Map
//
//        System.out.println();
//        System.out.println();
//        System.out.println();
//        System.out.println("Print the new Map Data");
//
//        for(String map:finalMap.keySet()){
//            Map map1=finalMap.get(map);
//            for (Object key:map1.keySet()){
//                System.out.print(key+" : "+map1.get(key)+"   ");
//            }
//            System.out.println();
//        }

        return finalMap;


    }
}