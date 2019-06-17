package util;

import ElasticSearch.ElasticSearchUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {
    public static void main(String[] args) throws IOException {
        createFile(new File("/Users/anoop/Downloads/OPTSTK_VEDL_CE_25-04-2019_TO_24-05-2019.csv"));
    }

    public static void createFile(File file) throws IOException {
        BufferedReader bufferedReader= new BufferedReader(new FileReader(file));
        String line="";
        String resultLine="";
        while ((line=bufferedReader.readLine())!=null){
String []lineArray=line.split(",");
if(!lineArray[15].equalsIgnoreCase("\"0\"")){
    resultLine=resultLine+line+"\n";
}
        }

        FileWriter fileWriter=new FileWriter(file.getName()+"Without0Data.csv");
        fileWriter.append(resultLine);
    }

    public static boolean processFile(String symbolName) throws IOException, ParseException {
        symbolName=symbolName.replaceAll(" ","");
        boolean isFileProcessed;
//        File file= new File("/Users/anoop/Downloads/OPTIDX_BANKNIFTY_PE_01-03-2019_TO_07-03-2019.csv");
//        System.out.println(new SimpleDateFormat("dd-MM-YYYY HH:mm").format(file.lastModified()));
        // list down all the files from directory
        String dirPath=System.getProperty("user.home");
        File file1= new File(dirPath+"/Downloads");
        File files[]=file1.listFiles();

        String oldFileName=null;
        String newFileName=null;
        String currentDate=new SimpleDateFormat("dd-MMMM-YYYY").format(Calendar.getInstance().getTime());
        for (int i = 0; i <files.length ; i++) {
            System.out.println(files[i].getAbsoluteFile());
            if(files[i].getName().contains(symbolName) && files[i].getName().contains("CE")){
                oldFileName=files[i].getName();
                newFileName=symbolName+"-CE-"+currentDate+".csv";
                if(files[i].renameTo(new File(newFileName))){
                    System.out.println("File is renamed");
                }else System.out.println("File is not renamed");
            }
            else if(files[i].getName().contains(symbolName) && files[i].getName().contains("PE")){
                oldFileName=files[i].getName();
                newFileName=symbolName+"-PE-"+currentDate+".csv";
                if(files[i].renameTo(new File(newFileName))){
                    System.out.println("File is renamed");
                }
                else
                    System.out.println("FIle is not renamed");
            }


        }

        writeES(newFileName);
        return isFileProcessed=true;

    }

    public static void writeES(String fileName) throws IOException, ParseException {
        BufferedReader bufferedReader = null;
//    try {
        bufferedReader = new BufferedReader(new FileReader(fileName));
        String line = null;
        int lineCount=0;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
            if(lineCount++>0) {
                line=line.replaceAll("\"","");
                String rowData[] = line.split(",");
                Map dataMap = new HashMap();
                DecimalFormat decimalFormat = new DecimalFormat();
                dataMap.put("Symbol", rowData[4] +" "+ rowData[3]);
                System.out.println(new SimpleDateFormat("dd-MMM-YYYY").parse("01-Mar-2019"));
                System.out.println("Actual file Date: "+new SimpleDateFormat("dd-MMM-YYYY").parse(rowData[1]));
                dataMap.put("Date", new SimpleDateFormat("dd-MMM-yyyy").parse(rowData[1]));


                double priceLTP;
                double currentOI;
                double changeOI;
                double vol;
                try {
                    priceLTP = decimalFormat.parse(rowData[9]).doubleValue();
                    currentOI = decimalFormat.parse((String) rowData[14]).doubleValue();
                    changeOI = decimalFormat.parse((String) rowData[15]).doubleValue();
//            vol = decimalFormat.parse((String) map.get("Volume")).doubleValue();

// Logic to skip the ES entry when changeOI and CurrentOI==0
                    if (changeOI==0.0 && currentOI==0.0)
                        continue;
                } catch (ParseException e) {
                    priceLTP = 0.0;
                    currentOI = 0.0;
                    changeOI = 0.0;
                    vol = 0.0;
                }
                dataMap.put("Price",priceLTP);
                dataMap.put("OI",currentOI);
                dataMap.put("CHG in OI",changeOI);

                String json = new ObjectMapper().writeValueAsString(dataMap);
                System.out.println("Printing the final Map: "+dataMap);
                if(currentOI!=0.0){
                    double currentStrikePrice=Double.valueOf(rowData[4]);
//                    if((currentStrikePrice-NSEHistoryScrapper.BNCurrentValue)>-900 &&(currentStrikePrice-NSEHistoryScrapper.BNCurrentValue)<900){
                    ElasticSearchUtil.putData(json, "bn_oi_history");

                }

            }
        }
//    } catch (Exception e) {

        bufferedReader.close();
//    }
    }
}
