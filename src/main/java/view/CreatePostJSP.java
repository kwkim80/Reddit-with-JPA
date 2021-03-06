package view;

import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.PostLogic;
import logic.LogicFactory;
import logic.RedditAccountLogic;
import logic.SubredditLogic;

/**
 *
 * @author kw2446
 */
@WebServlet( name = "CreatePostJSP", urlPatterns = { "/CreatePostJSP" } )
public class CreatePostJSP extends HttpServlet {
  private String errorMessage = null;
    
    private void fillTableData( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute( "entities", extractTableData( req ) );
        req.setAttribute( "req", toStringMap( req.getParameterMap() ) );
        req.setAttribute( "path", path );
        req.setAttribute( "title", path.substring( 1 ) );
        req.getRequestDispatcher( "/jsp/Input-Post.jsp" ).forward( req, resp );
    }

    private List<?> extractTableData( HttpServletRequest req ) {
        String search = req.getParameter( "searchText" );
        PostLogic logic = LogicFactory.getFor( "Post" );
        req.setAttribute( "columnName", logic.getColumnNames() );
        req.setAttribute( "columnCode", logic.getColumnCodes() );
        req.setAttribute( "errorMessage", errorMessage );
        List<Post> list;
        if( search != null ){
            list = logic.search( search );
        } else {
            list = logic.getAll();
        }
        if( list == null || list.isEmpty() ){
            return Collections.emptyList();
        }
        return appendDatatoNewList( list, logic::extractDataAsList );
    }

    private <T> List<?> appendDatatoNewList( List<T> list, Function<T, List<?>> toArray ) {
        List<List<?>> newlist = new ArrayList<>( list.size() );
        list.forEach( i -> newlist.add( toArray.apply( i ) ) );
        return newlist;
    }

    private String toStringMap( Map<String, String[]> m ) {
        StringBuilder builder = new StringBuilder();
        m.keySet().forEach( ( k ) -> {
            builder.append( "Key=" ).append( k )
                    .append( ", " )
                    .append( "Value/s=" ).append( Arrays.toString( m.get( k ) ) )
                    .append( System.lineSeparator() );
        } );
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req servlet req
     * @param resp servlet resp
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "POST" );
     
        String unique_id = req.getParameter( PostLogic.UNIQUE_ID );
        PostLogic logic=LogicFactory.getFor( "Post" );
        Post item=logic.getPostWithUniqueId(unique_id);
        if(  item== null ){
            try {
                item= logic.createEntity( req.getParameterMap() );
                RedditAccountLogic redditLogic=LogicFactory.getFor("RedditAccount");
                SubredditLogic subLogic=LogicFactory.getFor("Subreddit");
                RedditAccount reddit = redditLogic.getWithId(Integer.valueOf(req.getParameter(PostLogic.REDDIT_ACCOUNT_ID)));
                Subreddit sub = subLogic.getWithId(Integer.valueOf(req.getParameter(PostLogic.SUBREDDIT_ID)));
                item.setRedditAccountId(reddit );
                item.setSubredditId(sub);
                logic.add( item );
                errorMessage="";
            } catch( Exception ex ) {
                errorMessage = ex.getMessage();
            }
        } else {
            //if duplicate print the error message
            errorMessage = "UNIQUE_ID: \"" + unique_id + "\" already exists";
        }
        if( req.getParameter( "add" ) != null ){
            //if add button is pressed return the same page
             
             fillTableData( req, resp );
        } else if( req.getParameter( "view" ) != null ){
            //if view button is pressed redirect to the appropriate table
            resp.sendRedirect( "PostTableJSP" );
        }
       
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param req servlet req
     * @param resp servlet resp
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "GET" );
        fillTableData( req, resp );
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param req servlet req
     * @param resp servlet resp
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPut( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "PUT" );
        doPost( req, resp );
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * @param req servlet req
     * @param resp servlet resp
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doDelete( HttpServletRequest req, HttpServletResponse resp )
            throws ServletException, IOException {
        log( "DELETE" );
        doPost( req, resp );
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Smaple of Post Table using JSP";
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
