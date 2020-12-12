/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;


import entity.Comment;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author kw244
 */
public class CommentDAL extends GenericDAL<Comment>{
    public CommentDAL(){
        super(Comment.class);
    }
    public List<Comment> findAll(){
            return findResults("Comment.findAll", null);
    }
    public Comment findById(int id){
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", id);
        return findResult("Comment.findById", map);
    }
    
    public Comment findByUniqueId(String uniqueId){
             HashMap<String, Object> map = new HashMap<>();
        map.put("uniqueId", uniqueId);
        return findResult("Comment.findByUniqueId", map);
    
    }
    public List<Comment> findByText(String text){
              HashMap<String, Object> map = new HashMap<>();
        map.put("text", text);
        return findResults("Comment.findByText", map);
    }

    public List<Comment> findByCreated(Date created){
              HashMap<String, Object> map = new HashMap<>();
        map.put("created", created);
        return findResults("Comment.findByCreated", map);
    }
    
        public List<Comment> findPoints(int  points){
                  HashMap<String, Object> map = new HashMap<>();
        map.put("points", points);
        return findResults("Comment.findByPoints", map);
    }
            public List<Comment> findByReplys(int replys){
             HashMap<String, Object> map = new HashMap<>();
        map.put("replys", replys);
        return findResults("Comment.findByReplys", map);
    }
                public List<Comment> findByIsReply(boolean isReply){
               HashMap<String, Object> map = new HashMap<>();
        map.put("is_reply", isReply);
        return findResults("Comment.findByIsReply", map);
    }
                
}
