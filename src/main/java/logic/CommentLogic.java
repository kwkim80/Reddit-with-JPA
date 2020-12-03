/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.CommentDAL;
import entity.Account;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import static logic.AccountLogic.DISPLAYNAME;
import static logic.AccountLogic.ID;
import static logic.AccountLogic.PASSWORD;
import static logic.AccountLogic.USERNAME;

/**
 *
 * @author kw244
 */
public class CommentLogic extends GenericLogic<Comment, CommentDAL> {
public final String REPLYS = "replys";
public final String IS_REPLY = "is_reply";
public final String POINTS = "points";
public final String CREATED = "created";
public final String TEXT = "text";
public final String ID = "id";
public final String UNIQUE_ID = "unique_id";
public final String REDDIT_ACCOUNT_ID = "reddit_account_id";
public final String POST_ID = "post_id";


private  CommentLogic(){
    super(new CommentDAL());
}

public List<Comment> getAll(){
        return get( () -> dal().findAll() );
}

public Comment getWithId(int id){
 return get( () -> dal().findById(id) );
}

public List<Comment> getCommentsWithText(String text){
 return get( () -> dal().findByText(text) );  
}

public Comment getCommentWithUniqueId(String uniqueId){
 return get( () -> dal().findByUniqueId(uniqueId) ); 
}

public List<Comment> getCommentsWithCreated(Date created){
     return get( () -> dal().findByCreated((created)) ); 
}

public List<Comment> getCommentsWithPoints(int points){
    return get( () -> dal().findPoints(points) ); 
    
}

public List<Comment> getCommentsWithReplys(int replys){
     return get( () -> dal().findByReplys(replys) ); 
}

public List<Comment> getCommentsWithReplys(boolean isReply){
    return get( () -> dal().findByIsReply(isReply) ); 
}

public Comment createEntity(Map<String, String[]> parameterMap){
 
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Comment entity = new Comment();

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
        String reddit_account_id = parameterMap.get(REDDIT_ACCOUNT_ID )[ 0 ];
        String post_id = parameterMap.get( POST_ID )[ 0 ];
        String unique_id = parameterMap.get( UNIQUE_ID )[ 0 ];
        String text = parameterMap.get( TEXT )[ 0 ];
        String created = parameterMap.get( CREATED )[ 0 ];
        String points = parameterMap.get(POINTS )[ 0 ];
        String replys = parameterMap.get( REPLYS )[ 0 ];
        String is_reply = parameterMap.get( IS_REPLY )[ 0 ];

      
        //validate the data
        if (isNumeric(reddit_account_id)!=true){
           throw new ValidationException( "non numeric" );
        }
            if (isNumeric(post_id)!=true){
           throw new ValidationException( "non numeric" );
        }
        validator.accept( unique_id, 10 );
        validator.accept( text, 1000 );
      //  validator.accept( created, 45 );
   if (isNumeric(points)!=true){
           throw new ValidationException( "non numeric" );
        }
            if (isNumeric(replys)!=true){
           throw new ValidationException( "non numeric" );
        }
        validator.accept( is_reply, 1 );
        //set values on entity
          RedditAccount reddId = new RedditAccount(Integer.valueOf(reddit_account_id));
          Post postId = new Post(Integer.valueOf(post_id));
    try { 
        Date date1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(created);
        entity.setCreated(date1);
    } catch (ParseException ex) {
        throw new ValidationException("Invalid date");
    }
        entity.setRedditAccountId(reddId);
        entity.setPostId(postId );
        entity.setUniqueId(unique_id);
        

        return entity;
}

public List<String> getColumnNames(){
    return null;
}

public List<String> getColumnCodes(){
    return null;
}

public List<?> extractDataAsList(Comment e){
    return null;
}
public static boolean isNumeric(String strNum) {
    if (strNum == null) {
        return false;
    }
    try {
        int d = Integer.parseInt(strNum);
    } catch (NumberFormatException nfe) {
        return false;
    }
    return true;
}
}