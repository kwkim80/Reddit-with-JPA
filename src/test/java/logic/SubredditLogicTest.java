
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Subreddit;
import entity.Subreddit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SubredditLogicTest {
  private SubredditLogic logic;
  private Subreddit expectedEntity;

  @BeforeAll
  final static void setUpBeforeClass() throws Exception {
    TomcatStartUp.createTomcat("/RedditAnalytic", "common.ServletListener");
  }

  @AfterAll
  final static void tearDownAfterClass() throws Exception {
    TomcatStartUp.stopAndDestroyTomcat();
  }

  @BeforeEach
  final void setUp() throws Exception {

    logic = LogicFactory.getFor("Subreddit");
    /*
     * ********************************** ***********IMPORTANT**************
     **********************************/
    Subreddit entity = new Subreddit();
    entity.setName("Junit 5 Test");
    entity.setSubscribers(5);
    entity.setUrl("junit5");

    EntityManager em = EMFactory.getEMF().createEntityManager();
    em.getTransaction().begin();
    expectedEntity = em.merge(entity);
    em.getTransaction().commit();
    em.close();
  }

  @AfterEach
  final void tearDown() throws Exception {
    if (expectedEntity != null) {
      logic.delete(expectedEntity);
    }
  }

  private void assertSubredditEquals(Subreddit expected, Subreddit actual) {
    // assert all field to guarantee they are the same
    assertEquals(expected.getId(), actual.getId());
    assertEquals(expected.getName(), actual.getName());
    assertEquals(expected.getSubscribers(), actual.getSubscribers());
    assertEquals(expected.getUrl(), actual.getUrl());
  }

  @Test
  final void testGetAll() {
    List<Subreddit> list = logic.getAll();
    int originalSize = list.size();

    assertNotNull(expectedEntity);
    logic.delete(expectedEntity);

    list = logic.getAll();
    assertEquals(originalSize - 1, list.size());
  }

  @Test
  final void testGetWithId() {
    Subreddit returnedSubreddit = logic.getWithId(expectedEntity.getId());

    assertSubredditEquals(expectedEntity, returnedSubreddit);
  }

  @Test
  final void testGetSubredditWithName() {
    Subreddit returnedSubreddit = logic.getSubredditWithName(expectedEntity.getName());
    assertSubredditEquals(expectedEntity, returnedSubreddit);
  }

  @Test
  final void testGetSubredditWithURL() {
    Subreddit returnedSubreddit = logic.getSubredditWithUrl(expectedEntity.getUrl());
    assertSubredditEquals(expectedEntity, returnedSubreddit);
  }

  @Test
  final void testGetSubredditWithSubscribers() {
    int foundFull = 0;

    List<Subreddit> returnedSubreddit = logic.getSubredditsWithSubscribers(expectedEntity.getSubscribers());
    for (Subreddit subreddit : returnedSubreddit) {
      assertEquals(expectedEntity.getSubscribers(), subreddit.getSubscribers());
      if (subreddit.getId().equals(expectedEntity.getId())) {
        assertSubredditEquals(expectedEntity, subreddit);
        foundFull++;
      }
    }
    assertEquals(1, foundFull, "if zero means not found, if more than one means duplicate");

  }

  @Test
  final void testCreateEntityAndAdd() {
    Map<String, String[]> sampleMap = new HashMap<>();
    sampleMap.put(SubredditLogic.NAME, new String[] { "Test Create Entity" });
    sampleMap.put(SubredditLogic.URL, new String[] { "testCreateSubreddit" });
    sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[] { "2" });

    Subreddit returnedSubreddit = logic.createEntity(sampleMap);
    logic.add(returnedSubreddit);

    returnedSubreddit = logic.getSubredditWithUrl(returnedSubreddit.getUrl());

    assertEquals(sampleMap.get(SubredditLogic.NAME)[0], returnedSubreddit.getName());
    assertEquals(sampleMap.get(SubredditLogic.URL)[0], returnedSubreddit.getUrl());
    assertEquals(sampleMap.get(SubredditLogic.SUBSCRIBERS)[0], returnedSubreddit.getSubscribers());

    logic.delete(returnedSubreddit);
  }

  @Test
  final void testCreateEntity() {
    Map<String, String[]> sampleMap = new HashMap<>();
    sampleMap.put(SubredditLogic.ID, new String[] { Integer.toString(expectedEntity.getId()) });
    sampleMap.put(SubredditLogic.NAME, new String[] { expectedEntity.getName() });
    sampleMap.put(SubredditLogic.URL, new String[] { expectedEntity.getUrl() });
    sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[] { String.valueOf(expectedEntity.getSubscribers()) });

    Subreddit returnedSubreddit = logic.createEntity(sampleMap);

    assertSubredditEquals(expectedEntity, returnedSubreddit);
  }

  @Test
  final void testCreateEntityNullAndEmptyValues() {
    Map<String, String[]> sampleMap = new HashMap<>();
    Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
      map.clear();
      map.put(SubredditLogic.ID, new String[] { Integer.toString(expectedEntity.getId()) });
      map.put(SubredditLogic.NAME, new String[] { expectedEntity.getName() });
      map.put(SubredditLogic.URL, new String[] { expectedEntity.getUrl() });
      map.put(SubredditLogic.SUBSCRIBERS, new String[] { String.valueOf(expectedEntity.getSubscribers()) });
    };

    // idealy every test should be in its own method
    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.ID, null);
    assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.ID, new String[] {});
    assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.NAME, null);
    assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.NAME, new String[] {});
    assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.URL, null);
    assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.URL, new String[] {});
    assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.SUBSCRIBERS, null);
    assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.SUBSCRIBERS, new String[] {});
    assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
  }

  @Test
  final void testCreateEntityBadLengthValues() {
    Map<String, String[]> sampleMap = new HashMap<>();
    Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
      map.clear();
      map.put(SubredditLogic.ID, new String[] { Integer.toString(expectedEntity.getId()) });
      map.put(SubredditLogic.NAME, new String[] { expectedEntity.getName() });
      map.put(SubredditLogic.URL, new String[] { expectedEntity.getUrl() });
      map.put(SubredditLogic.SUBSCRIBERS, new String[] { String.valueOf(expectedEntity.getSubscribers()) });
    };

    IntFunction<String> generateString = (int length) -> {
      // https://www.baeldung.com/java-random-string#java8-alphabetic
      // from 97 inclusive to 123 exclusive
      return new Random().ints('a', 'z' + 1).limit(length)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    };

    // idealy every test should be in its own method
    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.ID, new String[] { "" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.ID, new String[] { "12b" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.NAME, new String[] { "" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.NAME, new String[] { generateString.apply(101) });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.URL, new String[] { "" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.URL, new String[] { generateString.apply(256) });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
  }

  @Test
  final void testCreateEntityEdgeValues() {
    Map<String, String[]> sampleMap = new HashMap<>();
    Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
      map.clear();
      map.put(SubredditLogic.ID, new String[] { Integer.toString(expectedEntity.getId()) });
      map.put(SubredditLogic.NAME, new String[] { expectedEntity.getName() });
      map.put(SubredditLogic.URL, new String[] { expectedEntity.getUrl() });
      map.put(SubredditLogic.SUBSCRIBERS, new String[] { String.valueOf(expectedEntity.getSubscribers()) });
    };

    IntFunction<String> generateString = (int length) -> {
      // https://www.baeldung.com/java-random-string#java8-alphabetic
      // from 97 inclusive to 123 exclusive
      return new Random().ints('a', 'z' + 1).limit(length)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
    };

    // idealy every test should be in its own method
    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.ID, new String[] { "" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.ID, new String[] { "12b" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.NAME, new String[] { "" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.NAME, new String[] { generateString.apply(1) });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

    fillMap.accept(sampleMap);
    sampleMap.replace(SubredditLogic.URL, new String[] { "" });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    sampleMap.replace(SubredditLogic.URL, new String[] { generateString.apply(1) });
    assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
  }

  @Test
  final void testGetColumnNames() {
    List<String> list = logic.getColumnNames();
    assertEquals(Arrays.asList("id", "subscribers", "name", "url"), list);
  }

  @Test
  final void testGetColumnCodes() {
    List<String> list = logic.getColumnCodes();
    assertEquals(Arrays.asList(SubredditLogic.ID, SubredditLogic.SUBSCRIBERS, SubredditLogic.NAME, SubredditLogic.URL),
        list);
  }

  @Test
  final void testExtractDataAsList() {
    List<?> list = logic.extractDataAsList(expectedEntity);
    assertEquals(expectedEntity.getId(), list.get(0));
    assertEquals(expectedEntity.getSubscribers(), list.get(1));
    assertEquals(expectedEntity.getName(), list.get(2));
    assertEquals(expectedEntity.getUrl(), list.get(3));
  }

}