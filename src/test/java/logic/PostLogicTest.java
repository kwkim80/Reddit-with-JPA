    package logic;

    import common.TomcatStartUp;
    import common.ValidationException;
    import dal.EMFactory;
    import entity.Post;
    import entity.Post;
    import entity.RedditAccount;
import entity.Subreddit;
    import java.util.Arrays;
    import java.util.Date;
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

    /**
     *
     * @author Shariar
     */
    class PostLogicTest {

        private PostLogic logic;
        private Post expectedEntity;

        @BeforeAll
        final static void setUpBeforeClass() throws Exception {
            TomcatStartUp.createTomcat( "/RedditAnalytic", "common.ServletListener" );
        }

        @AfterAll
        final static void tearDownAfterClass() throws Exception {
            TomcatStartUp.stopAndDestroyTomcat();
        }

        @BeforeEach
        final void setUp() throws Exception {

            logic = LogicFactory.getFor( "Post" );
            /* **********************************
             * ***********IMPORTANT**************
             * **********************************/
            //we only do this for the test.
            //always create Entity using logic.
            //we manually make the account to not rely on any logic functionality , just for testing
            Post entity = new Post();
            entity.setCommentCount(1);
            entity.setId(99 );
            entity.setUniqueId("test");
            entity.setCreated(new Date(11/11/11));
            entity.setPoints(99);
            entity.setTitle("Junit");
            entity.setUniqueId("1");
            entity.setRedditAccountId(new RedditAccount(1));
            entity.setSubredditId(new Subreddit(1));

            //get an instance of EntityManager
            EntityManager em = EMFactory.getEMF().createEntityManager();
            //start a Transaction
            em.getTransaction().begin();
            //add an account to hibernate, account is now managed.
            //we use merge instead of add so we can get the updated generated ID.
            expectedEntity = em.merge( entity );
            //commit the changes
            em.getTransaction().commit();
            //close EntityManager
            em.close();
        }

        @AfterEach
        final void tearDown() throws Exception {
            if( expectedEntity != null ){
                logic.delete( expectedEntity );
            }
        }

        @Test
        final void testGetAll() {
            //get all the accounts from the DB
            List<Post> list = logic.getAll();
            //store the size of list, this way we know how many accounts exits in DB
            int originalSize = list.size();

            //make sure account was created successfully
            assertNotNull( expectedEntity );
            //delete the new account
            logic.delete( expectedEntity );

            //get all accounts again
            list = logic.getAll();
            //the new size of accounts must be one less
            assertEquals( originalSize - 1, list.size() );
        }

        /**
         * helper method for testing all account fields
         *
         * @param expected
         * @param actual
         */
        private void assertAccountEquals( Post expected, Post actual ) {
            //assert all field to guarantee they are the same
           assertEquals( expected.getId(), actual.getId() );
            assertEquals( expected.getCreated(), actual.getCreated() );
            assertEquals( expected.getCommentCount(), actual.getCommentCount());
            assertEquals( expected.getPoints(), actual.getPoints());
            assertEquals( expected.getTitle(), actual.getTitle());
            assertEquals( expected.getSubredditId(), actual.getSubredditId());
            assertEquals( expected.getUniqueID(), actual.getUniqueID());

        }

        @Test
        final void testGetWithId() {
            //using the id of test account get another account from logic
            Post returnedAccount = logic.getWithId( expectedEntity.getId() );

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testGetPostWithCreated() {
            List<Post> returnedPost = logic.getPostsWithCreated(expectedEntity.getCreated());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedPost.get(0) );
        }
    
        @Test
        final void testGetPostWithPoints() {
            List<Post> returnedPost = logic.getPostWithPoints(expectedEntity.getPoints());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedPost.get(0) );
        }
        
                @Test
        final void testGetPostWithTitle() {
            List<Post> returnedPost = logic.getPostsWithTitle(expectedEntity.getTitle());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedPost.get(0) );
        }

    
        final void testGetPostWithCommentCount() {
            List<Post> returnedPost = logic.getPostsWithCommentCount(expectedEntity.getCommentCount());
            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedPost.get(0) );
        }

        @Test
        final void testCreateEntityAndAdd() {
            Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( PostLogic.CREATED, new String[]{"20201212"});
            sampleMap.put( PostLogic.TITLE, new String[]{ "true" } );
            sampleMap.put( PostLogic.SUBREDDIT_ID, new String[]{  "1" } );
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ "Test" } );
            sampleMap.put( PostLogic.POINTS, new String[]{"1"});
            sampleMap.put( PostLogic.ID, new String[]{"1"} );
            sampleMap.put( PostLogic.UNIQUE_ID, new String[]{"1"} );
         // sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{});
         // sampleMap.put(PostLogic.POST_ID, new String[]{});
            Post returnedAccount = logic.createEntity( sampleMap );
            logic.add( returnedAccount );

            returnedAccount = logic.getWithId(returnedAccount.getId());

            assertEquals( sampleMap.get( PostLogic.CREATED )[ 0 ], returnedAccount.getCreated());
            assertEquals( sampleMap.get( PostLogic.COMMENT_COUNT )[ 0 ], returnedAccount.getCommentCount());
            assertEquals( sampleMap.get( PostLogic.TITLE )[ 0 ], returnedAccount.getTitle());
            assertEquals( sampleMap.get( PostLogic.SUBREDDIT_ID )[ 0 ], returnedAccount.getSubredditId());
            assertEquals( sampleMap.get( PostLogic.POINTS )[ 0 ], returnedAccount.getPoints());
            logic.delete( returnedAccount );
        }
        
        @Test
        final void testCreateEntity() {
           Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( PostLogic.CREATED, new String[]{"20111111"});
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ "true" } );
            sampleMap.put( PostLogic.SUBREDDIT_ID, new String[]{  "99" } );
            sampleMap.put( PostLogic.TITLE, new String[]{ "Test" } );
            sampleMap.put( PostLogic.POINTS, new String[]{"99"});
            sampleMap.put( PostLogic.ID, new String[]{"99"} );
            sampleMap.put( PostLogic.UNIQUE_ID, new String[]{"99"} );
            Post post = new Post(1);    
            sampleMap.put( PostLogic.REDDIT_ACCOUNT_ID, new String[]{"99"} );
      
            Post returnedAccount = logic.createEntity( sampleMap );

            assertAccountEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testCreateEntityNullAndEmptyValues() {
            Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
                map.put( PostLogic.CREATED, new String[]{ expectedEntity.getCreated().toString()} );
                map.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString(expectedEntity.getCommentCount())} );
                map.put( PostLogic.SUBREDDIT_ID, new String[]{ expectedEntity.getSubredditId().toString()} );
           map.put( PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints()) } );
           
            };

            //idealy every test should be in its own method
            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.ID, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.ID, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.CREATED, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.CREATED, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.COMMENT_COUNT, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.COMMENT_COUNT, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.POINTS, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.POINTS, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        }

        @Test
        final void testCreateEntityBadLengthValues() {
          Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
                map.put( PostLogic.CREATED, new String[]{ expectedEntity.getCreated().toString()} );
                map.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString(expectedEntity.getCommentCount())} );
                map.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle()} );
           map.put( PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints()) } );

            IntFunction<String> generateString = ( int length ) -> {
                //https://www.baeldung.com/java-random-string#java8-alphabetic
                //from 97 inclusive to 123 exclusive
                return new Random().ints( 'a', 'z' + 1 ).limit( length )
                        .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                        .toString();
            };
            };
            //idealy every test should be in its own method
            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.ID, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.ID, new String[]{ "12b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.CREATED, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.CREATED, new String[]{ "20111111" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.POINTS, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.POINTS, new String[]{ "1" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.COMMENT_COUNT, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.COMMENT_COUNT, new String[]{ "13" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
        };
                    
        @Test
        final void testCreateEntityEdgeValues() {
            IntFunction<String> generateString = ( int length ) -> {
                //https://www.baeldung.com/java-random-string#java8-alphabetic
                return new Random().ints( 'a', 'z' + 1 ).limit( length )
                        .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                        .toString();
            };

            Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( PostLogic.CREATED, new String[]{ generateString.apply( 1 ) } );
            sampleMap.put( PostLogic.POINTS, new String[]{ generateString.apply( 1 ) } );
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ generateString.apply( 1 ) } );

            //idealy every test should be in its own method
            Post returnedAccount = logic.createEntity( sampleMap );
            assertEquals( Integer.parseInt( sampleMap.get( PostLogic.ID )[ 0 ] ), returnedAccount.getId() );
            assertEquals( sampleMap.get( PostLogic.CREATED )[ 0 ], returnedAccount.getCreated());
            assertEquals( sampleMap.get( PostLogic.POINTS )[ 0 ], returnedAccount.getPoints());
            assertEquals( sampleMap.get( PostLogic.COMMENT_COUNT )[ 0 ], returnedAccount.getCommentCount());

            sampleMap = new HashMap<>();
            sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( PostLogic.CREATED, new String[]{ generateString.apply( 45 ) } );
            sampleMap.put( PostLogic.POINTS, new String[]{ generateString.apply( 45 ) } );
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ generateString.apply( 45 ) } );

            //idealy every test should be in its own method
            returnedAccount = logic.createEntity( sampleMap );
            assertEquals( Integer.parseInt( sampleMap.get( PostLogic.ID )[ 0 ] ), returnedAccount.getId() );
            assertEquals( sampleMap.get( PostLogic.CREATED )[ 0 ], returnedAccount.getCreated());
            assertEquals( sampleMap.get( PostLogic.TITLE )[ 0 ], returnedAccount.getTitle());
            assertEquals( sampleMap.get( PostLogic.COMMENT_COUNT )[ 0 ], returnedAccount.getCommentCount());
        }

        @Test
        final void testGetColumnNames() {
            List<String> list = logic.getColumnNames();
            assertEquals( Arrays.asList( "ID", "Displayname", "Username", "Password" ), list );
        }

        @Test
        final void testGetColumnCodes() {
            List<String> list = logic.getColumnCodes();
            assertEquals( Arrays.asList( PostLogic.ID, PostLogic.TITLE, PostLogic.UNIQUE_ID, PostLogic.REDDIT_ACCOUNT_ID,PostLogic.SUBREDDIT_ID,PostLogic.COMMENT_COUNT,PostLogic.POINTS,PostLogic.CREATED), list );
        }

        @Test
        final void testExtractDataAsList() {
            List<?> list = logic.extractDataAsList( expectedEntity );
            assertEquals( expectedEntity.getId(), list.get( 0 ) );
            assertEquals( expectedEntity.getCreated(), list.get( 1 ) );
            assertEquals( expectedEntity.getPoints(), list.get( 2 ) );
            assertEquals( expectedEntity.getCommentCount(), list.get( 3 ) );
        }
    }
