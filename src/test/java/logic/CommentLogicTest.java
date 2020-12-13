package logic;

import common.TomcatStartUp;
import common.Utility;
import common.ValidationException;
import dal.EMFactory;
import entity.Comment;
import entity.Post;
import entity.RedditAccount;
import java.time.Clock;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import static logic.CommentLogic.CREATED;
import static logic.CommentLogic.ID;
import static logic.CommentLogic.IS_REPLY;
import static logic.CommentLogic.POINTS;
import static logic.CommentLogic.POST_ID;
import static logic.CommentLogic.REDDIT_ACCOUNT_ID;
import static logic.CommentLogic.REPLYS;
import static logic.CommentLogic.TEXT;
import static logic.CommentLogic.UNIQUE_ID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Shariar
 */
class CommentLogicTest {

    private CommentLogic logic;
    private RedditAccountLogic rLogic;
    private PostLogic pLogic;
    private Comment expectedEntity;

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

        logic = LogicFactory.getFor("Comment");
        rLogic = LogicFactory.getFor("RedditAccount");
        pLogic = LogicFactory.getFor("Post");

        Comment entity = new Comment();
        entity.setId(1);
        entity.setText("Junit");
        entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
        entity.setIsReply(true);
        entity.setPoints(99);
        entity.setReplys(99);
        entity.setUniqueId(Utility.generateString(10));
        entity.setRedditAccountId(rLogic.getWithId(1));
        entity.setPostId(pLogic.getWithId(1));

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //add an account to hibernate, account is now managed.
        //we use merge instead of add so we can get the updated generated ID.
        expectedEntity = em.merge(entity);
        //commit the changes
        em.getTransaction().commit();
        //close EntityManager
        em.close();
    }

    @AfterEach
    final void tearDown() throws Exception {
        if (expectedEntity != null) {
            logic.delete(expectedEntity);
        }
    }

    @Test
    final void testGetAll() {
        //get all the accounts from the DB
        List<Comment> list = logic.getAll();
        //store the size of list, this way we know how many accounts exits in DB
        int originalSize = list.size();

        //make sure account was created successfully
        assertNotNull(expectedEntity);
        //delete the new account
        logic.delete(expectedEntity);

        //get all accounts again
        list = logic.getAll();
        //the new size of accounts must be one less
        assertEquals(originalSize - 1, list.size());
    }

    /**
     * helper method for testing all account fields
     *
     * @param expected
     * @param actual
     */
    private void assertCommentEquals(Comment expected, Comment actual) {
        //assert all field to guarantee they are the same
        assertEquals(expected.getId(), actual.getId());
        assertTrue(Utility.IsDateEqual(expected.getCreated(), actual.getCreated()));
        assertEquals(expected.getIsReply(), actual.getIsReply());
        assertEquals(expected.getPoints(), actual.getPoints());
        assertEquals(expected.getReplys(), actual.getReplys());
        assertEquals(expected.getText(), actual.getText());
        assertEquals(expected.getUniqueId(), actual.getUniqueId());
        assertEquals(expected.getRedditAccountId().getId(), actual.getRedditAccountId().getId());
        assertEquals(expected.getPostId().getId(), actual.getPostId().getId());

    }

    @Test
    final void testGetWithId() {
        //using the id of test account get another account from logic
        Comment returnedAccount = logic.getWithId(expectedEntity.getId());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertCommentEquals(expectedEntity, returnedAccount);
    }

    @Test
    final void testGetCommentWithCreated() {
        List<Comment> returnedComment = logic.getCommentsWithCreated(expectedEntity.getCreated());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (Comment comment : returnedComment) {
            assertTrue(Utility.IsDateEqual(expectedEntity.getCreated(), comment.getCreated()));
        }
    }

    @Test
    final void testGetCommentWithPoints() {
        List<Comment> returnedComment = logic.getCommentsWithPoints(expectedEntity.getPoints());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (Comment comment : returnedComment) {
            assertEquals(expectedEntity.getPoints(), comment.getPoints());
        }
    }

    @Test
    final void testGetCommentWithReply() {
        List<Comment> returnedComment = logic.getCommentsWithReplys(expectedEntity.getReplys());

        //the two accounts (testAcounts and returnedAccounts) must be the same
        for (Comment comment : returnedComment) {
            assertEquals(expectedEntity.getReplys(), comment.getReplys());
        }
    }

    final void testGetCommentWithText() {
        List<Comment> returnedComment = logic.getCommentsWithText(expectedEntity.getText());
        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertCommentEquals(expectedEntity, returnedComment.get(0));
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CommentLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
        sampleMap.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(expectedEntity.getIsReply())});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedEntity.getReplys())});
        sampleMap.put(CommentLogic.TEXT, new String[]{expectedEntity.getText()});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
        sampleMap.put(CommentLogic.UNIQUE_ID, new String[]{Utility.generateString(10)});

        Comment newItem = logic.createEntity(sampleMap);
        newItem.setPostId(expectedEntity.getPostId());
        newItem.setRedditAccountId(expectedEntity.getRedditAccountId());

        logic.add(newItem);

        Comment savedItem = logic.getWithId(newItem.getId());

        assertCommentEquals(newItem, savedItem);
        logic.delete(savedItem);
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put(CommentLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
        sampleMap.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(expectedEntity.getIsReply())});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedEntity.getReplys())});
        sampleMap.put(CommentLogic.TEXT, new String[]{expectedEntity.getText()});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
        sampleMap.put(CommentLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
        sampleMap.put(CommentLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueId()});

        Comment returnedAccount = logic.createEntity(sampleMap);
        returnedAccount.setPostId(expectedEntity.getPostId());
        returnedAccount.setRedditAccountId(expectedEntity.getRedditAccountId());
        assertCommentEquals(expectedEntity, returnedAccount);

    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();

            map.put(CommentLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
            map.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(expectedEntity.getIsReply())});
            map.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedEntity.getReplys())});
            map.put(CommentLogic.TEXT, new String[]{expectedEntity.getText()});
            map.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
            map.put(CommentLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
            map.put(CommentLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueId()});
        };
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.ID, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));


        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.TEXT, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.TEXT, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.POINTS, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.POINTS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.REPLYS, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.REPLYS, new String[]{});
        assertThrows(IndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
        
          fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.UNIQUE_ID, null);
        assertThrows(NullPointerException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.UNIQUE_ID, new String[]{});
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> logic.createEntity(sampleMap));
        
          fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.IS_REPLY, null);
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.IS_REPLY, new String[]{});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = (Map<String, String[]> map) -> {
            map.clear();

            map.put(CommentLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
            map.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(expectedEntity.getIsReply())});
            map.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedEntity.getReplys())});
            map.put(CommentLogic.TEXT, new String[]{expectedEntity.getText()});
            map.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
            map.put(CommentLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
            map.put(CommentLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueId()});
        };
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        //idealy every test should be in its own method
        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.ID, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

//        fillMap.accept(sampleMap);
//        sampleMap.replace(CommentLogic.CREATED, new String[]{""});
//        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
//        sampleMap.replace(CommentLogic.CREATED, new String[]{String.valueOf(new Date(11 / 11 / 1111))});
//        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.POINTS, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.POINTS, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.TEXT, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.TEXT, new String[]{generateString.apply(1001)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.UNIQUE_ID, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.UNIQUE_ID, new String[]{generateString.apply(11)});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));

        fillMap.accept(sampleMap);
        sampleMap.replace(CommentLogic.REPLYS, new String[]{""});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        sampleMap.replace(CommentLogic.REPLYS, new String[]{"12b"});
        assertThrows(ValidationException.class, () -> logic.createEntity(sampleMap));
        
    
    }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = (int length) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints('a', 'z' + 1).limit(length)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        String dateStr = new Date().toString();
        sampleMap.put(CommentLogic.CREATED, new String[]{dateStr});
        sampleMap.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(expectedEntity.getIsReply())});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(expectedEntity.getReplys())});
        sampleMap.put(CommentLogic.TEXT, new String[]{expectedEntity.getText()});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
        sampleMap.put(CommentLogic.ID, new String[]{Integer.toString(expectedEntity.getId())});
        sampleMap.put(CommentLogic.UNIQUE_ID, new String[]{Utility.generateString(1)});

        Comment newItem = logic.createEntity(sampleMap);
        newItem.setPostId(expectedEntity.getPostId());
        newItem.setRedditAccountId(expectedEntity.getRedditAccountId());

        assertEquals(sampleMap.get(CommentLogic.ID)[0], newItem.getId().toString());
        assertEquals(sampleMap.get(CommentLogic.CREATED)[0], newItem.getCreated().toString());
        assertEquals(sampleMap.get(CommentLogic.IS_REPLY)[0], Boolean.toString(newItem.getIsReply()));
        assertEquals(sampleMap.get(CommentLogic.REPLYS)[0], Integer.toString(newItem.getReplys()));
        assertEquals(sampleMap.get(CommentLogic.UNIQUE_ID)[0], newItem.getUniqueId());
        assertEquals(sampleMap.get(CommentLogic.POINTS)[0], Integer.toString(newItem.getPoints()));
        assertEquals(sampleMap.get(CommentLogic.TEXT)[0], newItem.getText());

        sampleMap = new HashMap<>();
        sampleMap.put(CommentLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
        sampleMap.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(!expectedEntity.getIsReply())});
        sampleMap.put(CommentLogic.REPLYS, new String[]{Integer.toString(Integer.MAX_VALUE)});
        sampleMap.put(CommentLogic.TEXT, new String[]{Utility.generateString(1000)});
        sampleMap.put(CommentLogic.POINTS, new String[]{Integer.toString(Integer.MAX_VALUE)});
        sampleMap.put(CommentLogic.ID, new String[]{Integer.toString(Integer.MAX_VALUE)});
        sampleMap.put(CommentLogic.UNIQUE_ID, new String[]{Utility.generateString(10)});

        //idealy every test should be in its own method
        newItem = logic.createEntity(sampleMap);
        assertEquals(sampleMap.get(CommentLogic.ID)[0], newItem.getId().toString());
        assertEquals(sampleMap.get(CommentLogic.IS_REPLY)[0], Boolean.toString(newItem.getIsReply()));
        assertEquals(sampleMap.get(CommentLogic.REPLYS)[0], Integer.toString(newItem.getReplys()));
        assertEquals(sampleMap.get(CommentLogic.UNIQUE_ID)[0], newItem.getUniqueId());
        assertEquals(sampleMap.get(CommentLogic.POINTS)[0], Integer.toString(newItem.getPoints()));
        assertEquals(sampleMap.get(CommentLogic.TEXT)[0], newItem.getText());
    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals(Arrays.asList("ID", "Reddit_Account_ID", "Post_ID", "Unique_ID", "Text", "Created", "Points", "Replys", "Is_Reply"), list);
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals(Arrays.asList(ID, REDDIT_ACCOUNT_ID, POST_ID, UNIQUE_ID, TEXT, CREATED, POINTS, REPLYS, IS_REPLY), list);
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList(expectedEntity);
        assertEquals(expectedEntity.getId(), list.get(0));
        assertEquals(expectedEntity.getRedditAccountId().getId(), list.get(1));
        assertEquals(expectedEntity.getPostId().getId(), list.get(2));
        assertEquals(expectedEntity.getUniqueId(), list.get(3));
        assertEquals(expectedEntity.getText(), list.get(4));
        assertTrue(Utility.IsDateEqual(expectedEntity.getCreated(), (Date) list.get(5)));
        assertEquals(expectedEntity.getPoints(), list.get(6));
        assertEquals(expectedEntity.getReplys(), list.get(7));
        assertEquals(expectedEntity.getIsReply(), (Boolean) list.get(8));
    }
}
