package dal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entity.Subreddit;

public class SubredditDAL extends GenericDAL<Subreddit> {

  public SubredditDAL() {
    super(Subreddit.class);

  }

  @Override
  public List<Subreddit> findAll() {

    return findResults("Subreddit.findAll", null);
  }

  @Override
  public Subreddit findById(int id) {
    Map<String, Object> map = new HashMap<>();
    map.put("id", id);
    return findResult("Subreddit.findById", map);
  }

  public Subreddit findByName(String name) {
    Map<String, Object> map = new HashMap<>();
    map.put("name", name);
    return findResult("Subreddit.findByName", map);
  }

  public Subreddit findByUrl(String url) {
    Map<String, Object> map = new HashMap<>();
    map.put("url", url);
    return findResult("Subreddit.findByUrl", map);
  }

  public List<Subreddit> findBySubscribers(int subscribers) {
    Map<String, Object> map = new HashMap<>();
    map.put("subscribers", subscribers);
    return findResults("Subreddit.findBySubscribers", map);
  }

}
