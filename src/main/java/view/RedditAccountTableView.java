/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import entity.RedditAccount;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.RedditAccountLogic;

/**
 *
 * @author Jiyeon Choi
 */
@WebServlet( name = "RedditAccountTable", urlPatterns = { "/RedditAccountTable" } )
public class RedditAccountTableView extends HttpServlet{
   protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>RedditAccountViewNormal</title>" );
            out.println( "</head>" );
            out.println( "<body>" );

            out.println( "<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">" );
            out.println( "<caption>RedditAccount</caption>" );
            //this is an example, for your other tables use getColumnNames from
            //logic to create headers in a loop.
                      out.println( "<tr>" );
            RedditAccountLogic logic = LogicFactory.getFor( "RedditAccount" );
            logic.getColumnCodes().forEach(h -> out.println( "<th>"+h+"</th>" ));
            out.println( "</tr>" );
   
            List<RedditAccount> entities = logic.getAll();
            for( RedditAccount e: entities ) {
                //for other tables replace the code bellow with
                //extractDataAsList in a loop to fill the data.
                out.printf( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        logic.extractDataAsList( e ).toArray() );
            }

            out.println( "</table>" );
            out.printf( "<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap( request.getParameterMap() ) );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp); //To change body of generated methods, choose Tools | Templates.
        log( "GET" );
        processRequest( req, resp );
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doGet(req, resp); 
        log( "GET" );
        processRequest( req, resp );
    }

    @Override
    public void log(String message, Throwable t) {
        super.log(message, t); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void log(String msg) {
        super.log(msg); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getServletInfo() {
        return super.getServletInfo(); //To change body of generated methods, choose Tools | Templates.
    }
        private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        for( String k: m.keySet() ) {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        }
        return builder.toString();
    }
}
