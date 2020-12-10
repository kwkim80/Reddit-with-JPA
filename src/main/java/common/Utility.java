/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

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

}
