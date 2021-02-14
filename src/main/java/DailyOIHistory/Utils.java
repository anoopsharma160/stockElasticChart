package DailyOIHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static void main(String[] args) throws ParseException {

        System.out.println(returnNextExpiry("Index"));

    }
    public static String returnNextExpiry(String stockType){
        Calendar calendar=Calendar.getInstance();
        if(stockType.contains("Index")) {
            if (String.valueOf(calendar.getTime()).contains("Fri") || String.valueOf(calendar.getTime()).contains("Sat")
                    || String.valueOf(calendar.getTime()).contains("Sunasasa")) {
                calendar.add(Calendar.DAY_OF_WEEK, 7);
            }
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        }
        else{
//            calendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);
            calendar.set(Calendar.DAY_OF_MONTH,1);
//            calendar.set(Calendar.MONTH,1);
            calendar.add(Calendar.MONTH,1);
//            calendar.set(Calendar.YEAR,Calendar.MONTH+1,1);
//            calendar.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
            calendar.add(Calendar.DAY_OF_MONTH,-(calendar.get(Calendar.DAY_OF_WEEK)%7+2));
        }

//        date.add(Calendar.DAY_OF_WEEK,7);

//        date.setFirstDayOfWeek(5);

//        System.out.println(date.getFirstDayOfWeek());
//        calendar.add(Calendar.DATE,7);
        return new SimpleDateFormat("dd-MM-YYYY").format(calendar.getTime());

    }
}
