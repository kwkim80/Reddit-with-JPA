package logic;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import common.ValidationException;
import dal.SubredditDAL;
import entity.Subreddit;

public class SubredditLogic extends GenericLogic<Subreddit, SubredditDAL> {
  public static final String SUBSCRIBERS = "subscribers";
  public static final String NAME = "name";
  public static final String URL = "url";
  public static final String ID = "id";

  SubredditLogic() {
    super(new SubredditDAL());
  }

  public List<Subreddit> getAll() {
    return get(() -> dal().findAll());

  }

  public Subreddit getWithId(int id) {
    return get(() -> dal().findById(id));

  }

  public Subreddit getSubredditWithName(String name) {
    return get(() -> dal().findByName(name));

  }

  public Subreddit getSubredditWithUrl(String url) {
    return get(() -> dal().findByUrl(url));
  }

  public List<Subreddit> getSubredditsWithSubscribers(int subscribers) {
    return get(() -> dal().findBySubscribers(subscribers));

  }

  public List<String> getColumnNames() {
    return Arrays.asList("id", "subscribers", "name", "url");

  }

  public List<String> getColumnCodes() {
    return Arrays.asList(ID, SUBSCRIBERS, NAME, URL);
  }

  public List<?> extractDataAsList(Subreddit e) {
    return Arrays.asList(e.getId(), e.getSubscribers(), e.getName(), e.getUrl());
  }

  public Subreddit createEntity(Map<String, String[]> parameterMap) {
    Objects.requireNonNull(parameterMap, "parameterMap cannot be null");
    Subreddit entity = new Subreddit();

    if (parameterMap.containsKey(ID)) {
      try {
        entity.setId(Integer.parseInt(parameterMap.get(ID)[0]));
      } catch (java.lang.NumberFormatException ex) {
        throw new ValidationException(ex);
      }
    }

    String name = parameterMap.get(NAME)[0];
    String url = parameterMap.get(URL)[0];
    String sub = parameterMap.get(SUBSCRIBERS)[0];

    entity.setName(name);
    entity.setUrl(url);
    entity.setSubscribers(Integer.valueOf(sub));

    return entity;
  }
}
