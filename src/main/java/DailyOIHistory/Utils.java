package DailyOIHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static void main(String[] args) throws ParseException {

        System.out.println(returnNextExpiry());

    }
    public static String returnNextExpiry(){
        Calendar calendar=Calendar.getInstance();
        if(String.valueOf(calendar.getTime()).contains("Fri") || String.valueOf(calendar.getTime()).contains("Sat")
        ||String.valueOf(calendar.getTime()).contains("Sun")){
            calendar.add(Calendar.DAY_OF_WEEK,7);
        }
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);


//        date.add(Calendar.DAY_OF_WEEK,7);

//        date.setFirstDayOfWeek(5);

//        System.out.println(date.getFirstDayOfWeek());
//        calendar.add(Calendar.DATE,7);
        return new SimpleDateFormat("dd-MM-YYYY").format(calendar.getTime());

    }
}
