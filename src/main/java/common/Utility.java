/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kw244
 */
public  class Utility {
    
    /**
     *
     * @return
     */
    public static String getCurrentDate()  {
        SimpleDateFormat formatter = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss" );

        Date now = Date.from( Instant.now( Clock.systemDefaultZone() ) );
        String strNow = formatter.format( now );
        System.out.println( strNow );

       return strNow;
       
    }
    
    public static boolean IsDateEqual(Date d1, Date d2){
            long timeInMilliSeconds1 = d1.getTime();
            long timeInMilliSeconds2 = d2.getTime();
            long errorRangeInMilliSeconds = 10000;//10 seconds
            return Math.abs( timeInMilliSeconds1 - timeInMilliSeconds2 ) < errorRangeInMilliSeconds;
    }
    
//    public static boolean IsDateEqual(String d1, Date d2){
//           
//        try {
//           Date date1 = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(d1);
//            long timeInMilliSeconds1 = date1.getTime();
//            long timeInMilliSeconds2 = d2.getTime();
//            long errorRangeInMilliSeconds = 10000;//10 seconds
//            return Math.abs( timeInMilliSeconds1 - timeInMilliSeconds2 ) < errorRangeInMilliSeconds;
//        } catch (ParseException ex) {
//            return false;
//        }
//           
//    }
    public static String generateString( int length ) {
                    //https://www.baeldung.com/java-random-string#java8-alphabetic
                    return new Random().ints( 'a', 'z' + 1 ).limit( length )
                            .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                            .toString();
    };
}
