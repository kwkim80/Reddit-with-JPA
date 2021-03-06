package view;


import common.Utility;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.PostLogic;
import logic.LogicFactory;
import static logic.PostLogic.REDDIT_ACCOUNT_ID;
import static logic.PostLogic.SUBREDDIT_ID;
import logic.RedditAccountLogic;
import logic.SubredditLogic;

/**
 *
 * @author kw244
 */
@WebServlet( name = "CreatePost", urlPatterns = { "/CreatePost" } )
public class CreatePost extends HttpServlet {

    private String errorMessage = null;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        response.setContentType( "text/html;charset=UTF-8" );
        try( PrintWriter out = response.getWriter() ) {
            PostLogic logic = LogicFactory.getFor( "Post" );
            /* TODO output your page here. You may use following sample code. */
            out.println( "<!DOCTYPE html>" );
            out.println( "<html>" );
            out.println( "<head>" );
            out.println( "<title>Create Post</title>" );
            out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"style/tablestyle.css\">");
            out.println( "</head>" );
            out.println( "<body>" );
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
            out.println( "<h2>Create Post</h2>" );
            out.println( "<form method=\"post\">" );
            logic.getColumnCodes().forEach((var column) -> {
                if(!column.equalsIgnoreCase("ID")){
                    out.printf( "%s:<br>",column.substring(0,1).toUpperCase()+column.substring(1) );
                    if(column.equalsIgnoreCase("Created")){
                          out.printf( "<input type=\"text\" name=\"%s\" value=\"\" placeholder='%s'><br>", column, Utility.getNowDate() );
                    }else out.printf( "<input type=\"text\" name=\"%s\" value=\"\"><br>", column );

                    out.println( "<br>" );  
                }
            });
           
       
            out.println( "<input type=\"submit\" name=\"view\" value=\"Add and View\">" );
            out.println( "<input type=\"submit\" name=\"add\" value=\"Add\">" );
            out.println( "</form>" );
            if( errorMessage != null && !errorMessage.isEmpty() ){
                out.println( "<p color=red>" );
                out.println( "<font color=red size=4px>" );
                out.println( errorMessage );
                out.println( "</font>" );
                out.println( "</p>" );
                errorMessage="";
               
            }
            out.println( "<pre>" );
            out.println( "Submitted keys and values:" );
            out.println( toStringMap( request.getParameterMap() ) );
            out.println( "</pre>" );
            out.println( "</div>" );
            out.println( "</div>" );
            out.println( "</body>" );
            out.println( "</html>" );
        }
    }

    private String toStringMap( Map<String, String[]> values ) {
        StringBuilder builder = new StringBuilder();
        values.forEach( ( k, v ) -> builder.append( "Key=" ).append( k )
                .append( ", " )
                .append( "Value/s=" ).append( Arrays.toString( v ) )
                .append( System.lineSeparator() ) );
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * get method is called first when requesting a URL. since this servlet will create a host this method simple
     * delivers the html code. creation will be done in doPost method.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "GET" );
        processRequest( request, response );
    }

    static int connectionCount = 0;

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * this method will handle the creation of entity. as it is called by user submitting data through browser.
     *
     * @param request servlet request
     * @param response servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest request, HttpServletResponse response )
            throws ServletException, IOException {
        log( "POST" );
        log( "POST: Connection=" + connectionCount );

      
        String unique_id = request.getParameter( PostLogic.UNIQUE_ID );
        PostLogic logic=LogicFactory.getFor( "Post" );
        Post temp=logic.getPostWithUniqueId(unique_id);
        if(  temp== null ){
            try {
                Post post = logic.createEntity( request.getParameterMap() );
                       
                //create the two logics for reddit account and subreddit
                //get the entities from logic using getWithId
                //set the entities on your post object before adding them to db
                RedditAccountLogic redditLogic=LogicFactory.getFor("RedditAccount");
                SubredditLogic subLogic=LogicFactory.getFor("Subreddit");
                RedditAccount reddit = redditLogic.getWithId(Integer.valueOf(request.getParameter(PostLogic.REDDIT_ACCOUNT_ID)));
                Subreddit sub = subLogic.getWithId(Integer.valueOf(request.getParameter(PostLogic.SUBREDDIT_ID)));
                post.setRedditAccountId(reddit );
                post.setSubredditId(sub);
                logic.add( post );
            } catch( Exception ex ) {
                errorMessage = ex.getMessage();
            }
        } else {
            //if duplicate print the error message
            errorMessage = "UNIQUE_ID: \"" + unique_id + "\" already exists";
        }
        if( request.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
            processRequest( request, response );
        } else if( request.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            response.sendRedirect( "PostTable" );
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Create a Post Entity";
    }

    private static final boolean DEBUG = true;

    public void log( String msg ) {
        if( DEBUG ){
            String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
            getServletContext().log( message );
        }
    }

    public void log( String msg, Throwable t ) {
        String message = String.format( "[%s] %s", getClass().getSimpleName(), msg );
        getServletContext().log( message, t );
    }
}
