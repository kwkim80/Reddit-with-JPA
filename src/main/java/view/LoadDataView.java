package view;

import entity.Account;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;
import logic.SubredditLogic;
import reddit.DeveloperAccount;
import reddit.wrapper.AccountWrapper;
import reddit.wrapper.CommentSort;
import reddit.wrapper.PostWrapper;
import reddit.wrapper.RedditWrapper;
import reddit.wrapper.SubSort;

/**
 *
 * @author kw244
 */
@WebServlet(name = "LoadDataView", urlPatterns = {"/LoadDataView"})
public class LoadDataView extends HttpServlet {

    private List<Post> list=null;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>AccountViewNormal</title>");
            out.println("</head>");
            out.println("<body>");
            out.println( "<div style=\"text-align: center;\">" );
            out.println( "<div style=\"display: inline-block; text-align: left;\">" );
             SubredditLogic slogic = LogicFactory.getFor( "Subreddit" );
             List<Subreddit> slist = slogic.getAll();
            out.println( "<form method=\"post\">" );
            out.println("<div> Subreddit : <select name='name' id='subName'>");
           
           
              for( Subreddit e: slist ) {
                   out.printf( "<option value='%s'>%s</option>\n",e.getName(),e.getName() );        
              }
               out.println( "</select>" );
               out.println("<input type=\"submit\" name=\"search\" value=\"Search\"></div>");
            out.println( "</form>" );
            out.print("<div >");
             out.println( "<table style=\"margin-left: auto; margin-right: auto;\" border=\"1\">" );
            out.println( "<caption>Post</caption>" );
            //this is an example, for your other tables use getColumnNames from
            //logic to create headers in a loop.
            //getColumnNames
            
            out.println( "<tr>" );
               PostLogic logic = LogicFactory.getFor( "Post" );
            logic.getColumnCodes().forEach(header -> {
                out.printf( "<th>%s</th>", header.substring(0, 1).toUpperCase() + header.substring(1) );
            });
            out.println( "</tr>" );

          
            if(list==null || list.isEmpty()){
               out.println( "<tr><td colspan='8'>There is no data</td></tr>");  
            }else{
                for( Post e: list ) {
                //for other tables replace the code bellow with
                //extractDataAsList in a loop to fill the data.
                out.printf( "<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>",
                        logic.extractDataAsList( e ).toArray() );
            }

            }
            //List<Post> entities = logic.getAll();
            
            out.println( "<tr>" );
            //this is an example, for your other tables use getColumnNames from
            //logic to create headers in a loop.
            logic.getColumnNames().forEach(columnName -> {
                 out.printf( "<th>%s</th>", columnName.substring(0, 1).toUpperCase() + columnName.substring(1) );
            });
            out.println( "</tr>" );
            out.println( "</table>" );
              out.print("<div style='text-align: right;'> <input type='submit' name='add' value='Add and View'> </div>");
            out.print("</div>");
          
            out.printf("<div style=\"text-align: center;\"><pre>%s</pre></div>", toStringMap(request.getParameterMap()));
            out.println( "</div>" );
            out.println( "</div>" );
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        for (String k : m.keySet()) {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("POST");
        //TODO fill in your reddit infromation here
         String clientID = "oIA7yTgEh7NmeA";
        String clientSecret = "lVhwxbIeZ6vZOhNm1sOCTo79bfk";
        String redditUser = "kw2446";
        String algonquinUser = "kim00395";

        list=new ArrayList<>();
        DeveloperAccount dev = new DeveloperAccount()
                .setClientID(clientID)
                .setClientSecret(clientSecret)
                .setRedditUser(redditUser)
                .setAlgonquinUser(algonquinUser);
        RedditAccountLogic raLogic = LogicFactory.getFor( "RedditAccount" );
        //create a new scraper
        RedditWrapper scrap = new RedditWrapper();
        //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
        scrap.authenticate(dev).setLogger(false);
        String subName = request.getParameter(SubredditLogic.NAME);
        scrap.configureCurentSubreddit(subName, 2, SubSort.BEST);

        //create a lambda that accepts post
        Consumer<PostWrapper> saveData;
        saveData = (PostWrapper post) -> {
            if (post.isPinned()) {
                return;
            }
            AccountWrapper aw = post.getAuthor();
            RedditAccount acc = raLogic.getRedditAccountWithName(aw.getName());
            if (acc == null) {
                Map<String, String[]> map = new HashMap<>(6);
                map.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(aw.getCommentKarma())});
                map.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(aw.getLinkKarma())});
                map.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(aw.getCreated())});
                map.put(RedditAccountLogic.NAME, new String[]{aw.getName()});
                acc = raLogic.createEntity(map);
               // raLogic.add(acc);
            }
            PostLogic pLogic = LogicFactory.getFor( "Post" );
            Post newPost=pLogic.getPostWithUniqueId(post.getUniqueID());
            if(newPost==null){
                Map<String, String[]> map = new HashMap<>(6);
                map.put(PostLogic.TITLE, new String[]{post.getTitle()});
                map.put(PostLogic.UNIQUE_ID, new String[]{post.getUniqueID()});
                map.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(post.getCommentCount())});
                map.put(PostLogic.POINTS, new String[]{Integer.toString(1)});
                newPost = pLogic.createEntity(map);
                // RedditAccountLogic redditLogic=LogicFactory.getFor("RedditAccount");
                SubredditLogic subLogic=LogicFactory.getFor("Subreddit");
               
                Subreddit sub = subLogic.getSubredditWithName(subName);
                newPost.setRedditAccountId(acc );
                newPost.setSubredditId(sub);
                newPost.setCreated(Date.from( Instant.now( Clock.systemDefaultZone())));
                pLogic.add( newPost );
                list.add(newPost);
            }
            post.configComments(2, 2, CommentSort.CONFIDENCE);
            post.processComments(comment -> {
                if (comment.isPinned() || comment.getDepth() == 0) {
                    return;
                }
                
            AccountWrapper aw1 = comment.getAuthor();
            RedditAccount acc1 = raLogic.getRedditAccountWithName(aw1.getName());
            if (acc1 == null) {
                Map<String, String[]> map = new HashMap<>(6);
                map.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(aw1.getCommentKarma())});
                map.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(aw1.getLinkKarma())});
                map.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(aw1.getCreated())});
                map.put(RedditAccountLogic.NAME, new String[]{aw1.getName()});
                acc1 = raLogic.createEntity(map);
                raLogic.add(acc1);
            }
                
                System.out.println((comment.isParrent() ? "----" : comment.getDepth() + ")") + "(" + comment.getAuthor().getName() + ")" + comment.getText());
                
            });
        };
        //get the next page and process every post
        scrap.requestNextPage().proccessCurrentPage(saveData);
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Sample of Account View Normal";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }
}
