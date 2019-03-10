package DailyOIHistory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {
    public static void main(String[] args) throws ParseException {

        System.out.println(returnNextExpiry());

        String dateString="01-Mar-2019";
        Date date= new SimpleDateFormat("dd-MMM-yyyy").parse(dateString);

        System.out.println(date);

    }
    public static String returnNextExpiry(){
        Calendar calendar=Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK,Calendar.THURSDAY);

//        date.add(Calendar.DAY_OF_WEEK,7);

//        date.setFirstDayOfWeek(5);

//        System.out.println(date.getFirstDayOfWeek());
//        calendar.add(Calendar.DATE,7);
        return new SimpleDateFormat("dd-MM-YYYY").format(calendar.getTime());

    }
}
