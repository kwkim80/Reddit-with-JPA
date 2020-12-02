/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.Comment;
import java.sql.Date;
import java.util.List;

/**
 *
 * @author kw244
 */
public class CommentDAL {
    public CommentDAL(){
        
    }
    public List<Comment> findAll(){
            return null;
    }
    public Comment findById(int id){
        return null;
    }
    
    public Comment findByUniqueId(String uniqueID){
        return null;
    }
    
    public List<Comment> findByText(String text){
        return null;
    }
    
    public List<Comment> findByCreated(Date created){
        return null;
    }
    
        public List<Comment> findPoints(int points){
        return null;
    }
            public List<Comment> findByReplys(int replys){
        return null;
    }
                public List<Comment> findByIsReply(boolean isReply){
        return null;
    }
                
    
    
}
