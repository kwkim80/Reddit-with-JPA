/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dal;

import entity.RedditAccount;
import java.util.Date;
import java.util.List;

/**
 *
 * @author choi0118
 */
public class RedditAccountDAL  extends GenericDAL<RedditAccount>  {

	public RedditAccountDAL() {
            super( RedditAccount.class );
	}
	
        @Override
	public List<RedditAccount> findAll() {
            return null;
			
	}
	
        @Override
	public RedditAccount findById(int id) {
		return null;
		
	}
	
	public RedditAccount findByName(String name) {
		return null;
	}
	
	public List<RedditAccount> findByLinkPoints(int linkPoints) {
            return null;
		
	}
	
	public List<RedditAccount> findByCommentPoints(int commentPoints) {
            return null;
		
	}
	
	public List<RedditAccount> findByCreated(Date created){
            return null;
		
	}
}
