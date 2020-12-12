/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.ValidationException;
import dal.SubredditDAL;
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
import static logic.PostLogic.CREATED;
import static logic.PostLogic.TITLE;
import static logic.PostLogic.UNIQUE_ID;

/**
 *
 * @author choi0118
 */
public class SubredditLogic extends GenericLogic <Subreddit, SubredditDAL>{
    public static final String ID ="id"  ;
    public static final String NAME  = "name";
    public static final String URL  = "url";
    public static final String SUBSCRIBERS  = "subscribers";




    SubredditLogic() {
        super(new SubredditDAL());
    }

     @Override
    public List<Subreddit> getAll() {
        return get( () -> dal().findAll() ); 
    }

    @Override
    public Subreddit getWithId(int id) {
        return get( () -> dal().findById(id) );
    }
    public Subreddit getSubredditWithName(String name ) {
        return get (() -> dal().findByName(name));
    }

    public Subreddit getSubredditsWithUrl(String url ){
        return get( () -> dal().findByUrl(url));
    } 
    
    public List<Subreddit> getSubredditsWithSubscribers(int subscribers){
        return get( () -> dal().findBySubscribers(subscribers));
    } 
    
    
  
    @Override
    public Subreddit createEntity(Map<String, String[]> parameterMap) {
        Objects.requireNonNull( parameterMap, "parameterMap cannot be null" );
        //same as if condition below
//        if (parameterMap == null) {
//            throw new NullPointerException("parameterMap cannot be null");
//        }

        //create a new Entity object
        Subreddit entity = new Subreddit();

          if( parameterMap.containsKey( ID ) ){
                try {
                    entity.setId( Integer.parseInt( parameterMap.get( ID )[ 0 ] ) );
                } catch( java.lang.NumberFormatException ex ) {
                    throw new ValidationException( ex );
                }
        }
      
        if( parameterMap.containsKey( SUBSCRIBERS ) ){
                try {
                    entity.setSubscribers(Integer.parseInt( parameterMap.get( SUBSCRIBERS )[ 0 ] ) );
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
     
        String name = parameterMap.get( NAME )[ 0 ];
        String url = parameterMap.get( URL )[ 0 ];
      
       validator.accept( name, 100 );
        validator.accept( url, 255 );
        //set values on entity
       
        entity.setName(name );
        entity.setUrl(url);

        return entity;        }

   
   
     @Override
    public List<String> getColumnCodes() {
        return Arrays.asList( ID, NAME, URL, SUBSCRIBERS);
    }

    @Override
    public List<String> getColumnNames() {
        return Arrays.asList( "ID", "Name", "Url", "Subscribers");
    }

    @Override
    public List<?> extractDataAsList(Subreddit e) {
        return Arrays.asList( e.getId(), e.getName(), e.getUrl(), e.getSubscribers());   
    }
    
    @Override
    public List<Subreddit> search(String search) {
        return get(() -> dal().findContaining(search));
    }

}
