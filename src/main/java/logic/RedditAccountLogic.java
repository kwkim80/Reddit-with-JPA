/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.RedditAccountDAL;
import entity.RedditAccount;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.ObjIntConsumer;

/**
 *
 * @author choi0118
 */
public class RedditAccountLogic extends GenericLogic <RedditAccount, RedditAccountDAL>{
public static final String COMMENT_POINTS = "comment_points";
public static final String LINK_POINTS = "link_points";
public static final String CREATED = "created";
public static final String NAME = "name";
public static final String ID = "id";

    RedditAccountLogic() {
        super(new RedditAccountDAL());
    }
//~RedditAccountLogic()
//+getAll() : List<RedditAccount>
//+getWithId(id : int) : RedditAccount
//+getRedditAccountWithName(name : String) : RedditAccount
//+getRedditAccountsWithLinkPoints(linkPoints : int) : List<RedditAccount>
//+getRedditAccountsWithCommentPoints(commentPoints : int) : List<RedditAccount>
//+getRedditAccountsWithCreated(created : Date) : List<RedditAccount>
//+createEntity(parameterMap : Map<String, String[]>) : RedditAccount
//+getColumnNames() : List<String>
//+getColumnCodes() : List<String>
//+extractDataAsList(e : RedditAccount) : List<?>
    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "id", "name", "link_points", "comment_points","created");
    }

    @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( "id", "name", "link_points", "comment_points","created");
    }

    @Override
    public List<?> extractDataAsList(RedditAccount e) {
        return Arrays.asList( e.getId(), e.getName(), e.getLinkPoints(), e.getCommentPoints(), e.getCreated());   
    }

    @Override
    public RedditAccount createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        RedditAccount entity = new RedditAccount();

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
        if( parameterMap.containsKey( NAME ) ){
                try {
                    entity.setName(parameterMap.get( NAME )[ 0 ] );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }
        
        if( parameterMap.containsKey( LINK_POINTS ) ){
                try {
                    entity.setLinkPoints(Integer.parseInt( parameterMap.get( LINK_POINTS )[ 0 ] ) );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }
        if( parameterMap.containsKey( COMMENT_POINTS ) ){
                try {
                    entity.setCommentPoints(Integer.parseInt( parameterMap.get( COMMENT_POINTS )[ 0 ] ) );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }   

        //extract the date from map first.
        //everything in the parameterMap is string so it must first be
        //converted to appropriate type. have in mind that values are
        //stored in an array of String; almost always the value is at
        //index zero unless you have used duplicated key/name somewhere.

        if( parameterMap.containsKey( CREATED ) ){
                    try {
                        entity.setCreated(new SimpleDateFormat("yyyy-MM-dd").parse(parameterMap.get( CREATED )[ 0 ] ));
                    } catch (ParseException ex) {
                        throw new ValidationException( ex );
                    }     
        }
        return entity;        }

    @Override
    public List<RedditAccount> getAll() {
        return get( () -> dal().findAll() ); 
    }

    @Override
    public RedditAccount getWithId(int id) {
        return get( () -> dal().findById(id) );
    }
    public RedditAccount getRedditAccountWithName(String name ) {
        return get (() -> dal().findByName(name));
    }

    public List<RedditAccount> getRedditAccountsWithLinkPoints(int linkPoints ){
        return get( () -> dal().findByLinkPoints(linkPoints));
    } 
    
    public List<RedditAccount> getRedditAccountsWithCommentPoints(int commentPoints){
        return get( () -> dal().findByCommentPoints(commentPoints));
    } 
    
    public List<RedditAccount> getRedditAccountsWithCreated(Date created ){
         return get( () -> dal().findByCreated(created));
    }
   
}
