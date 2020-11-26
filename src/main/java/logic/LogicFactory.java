package logic;

//TODO this class is just a skeleton it must be completed

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class LogicFactory {

    private static final String PACKAGE="logic.";
    private static final String SUFFIX="Logic";

    private LogicFactory() {
    }
    
    
    public static < T> T getFor( String entityName ) {
//        if(entityName.equals("AccountLogic"))return (T)new AccountLogic();
//        if(entityName.equals("PostLogic"))return (T)new PostLogic();
//        if(entityName.equals("CommentLogic"))return (T)new CommentLogic();
//        if(entityName.equals("RedditAccountLogic"))return (T)new RedditAccountLogic();
//        if(entityName.equals("SubredditLogic")) return (T)new SubredditLogic();
//        return null;
//        Object object = null;
//        try {
       
//            object = classDefinition.newInstance();
//        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
//            System.out.println(e);
//        }
//        return (T)object;
        try {
            Class classDefinition = Class.forName(PACKAGE + entityName + SUFFIX);
            return getFor((Class< T>) classDefinition);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
            return null;
    }
    
     public static <R> R getFor(Class<R> type )  {
          R item=null;
        try {
            //         Object object = null;
            Constructor<?>[] declaredConstructor =  type.getDeclaredConstructors();
             item= (R) declaredConstructor[0].newInstance();

        } catch (InstantiationException | IllegalAccessException |IllegalArgumentException | InvocationTargetException | SecurityException  ex) {
            Logger.getLogger(LogicFactory.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
        } 
         return item;
     }
}
