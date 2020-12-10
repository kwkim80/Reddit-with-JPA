package logic;

import common.Utility;
import common.ValidationException;
import dal.PostDAL;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kw244
 */
public class PostLogic extends GenericLogic<Post, PostDAL> {
    
    
    public static final String CREATED = "created";
    public static final String TITLE= "title";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String POINTS  = "points";
    public static final String ID = "id";
    public static final String UNIQUE_ID = "unique_id";
    public static final String REDDIT_ACCOUNT_ID = "reddit_account_id";
    public static final String SUBREDDIT_ID  = "subreddi    t_id";

    PostLogic() {
        super(new PostDAL());
                 }

    @Override
    public List<Post> getAll(){
       return get( () -> dal().findAll() ); 
    }
    @Override
    public Post getWithId(int id){
        return get( () -> dal().findById(id) );
    } 
    public Post getPostWithUniqueId(String uniqueId){
        return get( () -> dal().findByUniqueId(uniqueId) );
    }
    public List<Post> getPostWithPoints(int points){
        return get( () -> dal().findByPoints(points) );
    }
    public List<Post> getPostsWithCommentCount(int commentCount) {
        return get( () -> dal().findByCommentCount(commentCount) );
    }
    public  List<Post> getPostsWithAuthorID(int id) {
        return get( () -> dal().findByAuthor(id) );
    }
    public List<Post> getPostsWithTitle(String title){
         return get( () -> dal().findByTitle(title) );
    }
    public List<Post> getPostsWithCreated(Date created) {
        return get( () -> dal().findByCreated(created) );
    }
    
    @Override
    public Post createEntity(Map<String, String[]> parameterMap ){
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Post entity = new Post();

        //ID is generated, so if it exists add it to the entity object
        //otherwise it does not matter as mysql will create an if for it.
        //the only time that we will have id is for update behaviour.
       
       if( parameterMap.containsKey( ID ) ){
                try {
                    entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }
        if( parameterMap.containsKey( POINTS ) ){
                try {
                    entity.setPoints(Integer.parseInt( parameterMap.get( POINTS )[ 0 ] ) );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }
        
        if( parameterMap.containsKey( COMMENT_COUNT ) ){
                try {
                    entity.setCommentCount(Integer.parseInt( parameterMap.get( COMMENT_COUNT )[ 0 ] ) );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }
        //before using the values in the map, make sure to do error checking.
        //simple lambda to validate a string, this can also be place in another
        //method to be shared amoung all logic classes.
        ObjIntConsumer< String> validator = ( value, length ) -> {
            if( value == null || value.trim().isEmpty() || value.length() > length ){
                String error = "";
                if( value == null || value.trim().isEmpty() ){
                    error = "value cannot be null or empty: " + value;
                }
                if( value.length() > length ){
                    error = "string length is " + value.length() + " > " + length;
                }
                throw new ValidationException( error );
            }
        };

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.

       
        String title = parameterMap.get( TITLE )[ 0 ];
        String unique_id = parameterMap.get( UNIQUE_ID )[ 0 ];
        if( parameterMap.containsKey( CREATED ) ){
                    try {
                        entity.setCreated(new SimpleDateFormat("yyyyMMdd").parse(parameterMap.get( CREATED )[ 0 ] ));
                    } catch (ParseException ex) {
                        entity.setCreated(Date.from( Instant.now( Clock.systemDefaultZone())));
                    }     
        }
       validator.accept( unique_id, 10 );
      validator.accept( title, 255 );
        //set values on entity
       
        entity.setTitle(title );
        entity.setUniqueId(unique_id );
//        entity.setCommentCount(comment_count );
//        entity.setPoints(points );
       
//    entity.setCreated(created );
//         entity.setRedditAccountId(reddit_account_id );
//        entity.setSubredditId(subreddit_id );

        
        return entity;     
    } 
    
  
  
    
    @Override
    public List<?> extractDataAsList(Post e) {
        return Arrays.asList( e.getId(), e.getTitle(), e.getUniqueID(), e.getRedditAccountId().getName(), e.getSubredditId().getName(), e.getCommentCount(), e.getPoints(), e.getCreated() );   
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( ID, TITLE, UNIQUE_ID, REDDIT_ACCOUNT_ID,SUBREDDIT_ID,COMMENT_COUNT,POINTS,CREATED );
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( "ID", "TITLE", "UNIQUE_ID", "REDDIT_ACCOUNT_ID","SUBREDDIT_ID","COMMENT_COUNT","POINTS","CREATED" );
    }

   
}
