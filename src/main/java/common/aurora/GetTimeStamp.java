package common.aurora;

import java.sql.Timestamp;
import java.util.Date;

public class GetTimeStamp {

    /*
     * Getting the CurrentTimeStamp Service Class
     */
    public static Timestamp getCurrentTimeStamp(){

        Date date= new Date();

        long time = date.getTime();
        System.out.println("Time in Milliseconds: " + time);

        Timestamp ts = new Timestamp(time);
        return ts;
        }
}
