    package logic;

    import common.TomcatStartUp;
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
            TomcatStartUp.createTomcat( "/RedditAnalytic", "common.ServletListener" );
        }

        @AfterAll
        final static void tearDownAfterClass() throws Exception {
            TomcatStartUp.stopAndDestroyTomcat();
        }

        @BeforeEach
        final void setUp() throws Exception {

            logic = LogicFactory.getFor( "Comment" );
            rLogic = LogicFactory.getFor("RedditAccount");
            pLogic = LogicFactory.getFor("Subreddit");
            String uniqueKey=Long.toString(new Date().getTime()).substring(0, 9);
            Comment entity = new Comment();
            entity.setId(99 );
            entity.setText("test");
            entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
            entity.setIsReply(true );
            entity.setPoints(99);
            entity.setReplys(99);
            entity.setUniqueId(uniqueKey);
            entity.setRedditAccountId(rLogic.getWithId(1));       
            entity.setPostId(pLogic.getWithId(1));

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
            List<Comment> list = logic.getAll();
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
        private void assertAccountEquals( Comment expected, Comment actual ) {
            //assert all field to guarantee they are the same
           assertEquals( expected.getId(), actual.getId() );
            assertEquals( expected.getCreated(), actual.getCreated() );
            assertEquals( expected.getIsReply(), actual.getIsReply());
            assertEquals( expected.getPoints(), actual.getPoints());
            assertEquals( expected.getReplys(), actual.getReplys());
            assertEquals( expected.getText(), actual.getText());
            assertEquals( expected.getUniqueId(), actual.getUniqueId());

        }

        @Test
        final void testGetWithId() {
            //using the id of test account get another account from logic
            Comment returnedAccount = logic.getWithId( expectedEntity.getId() );

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testGetCommentWithCreated() {
            List<Comment> returnedComment = logic.getCommentsWithCreated(expectedEntity.getCreated());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedComment.get(0) );
        }
    
        @Test
        final void testGetCommentWithPoints() {
            List<Comment> returnedComment = logic.getCommentsWithPoints(expectedEntity.getPoints());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedComment.get(0) );
        }
        
                @Test
        final void testGetCommentWithReply() {
            List<Comment> returnedComment = logic.getCommentsWithReplys(expectedEntity.getReplys());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedComment.get(0) );
        }

    
        final void testGetCommentWithText() {
            List<Comment> returnedComment = logic.getCommentsWithText(expectedEntity.getText());
            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedComment.get(0) );
        }

        @Test
        final void testCreateEntityAndAdd() {
            Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( CommentLogic.CREATED, new String[]{"20201212"});
            sampleMap.put( CommentLogic.IS_REPLY, new String[]{ "true" } );
            sampleMap.put( CommentLogic.REPLYS, new String[]{  "1" } );
            sampleMap.put( CommentLogic.TEXT, new String[]{ "Test" } );
            sampleMap.put( CommentLogic.POINTS, new String[]{"1"});
            sampleMap.put( CommentLogic.ID, new String[]{"1"} );
            sampleMap.put( CommentLogic.UNIQUE_ID, new String[]{"1"} );
         // sampleMap.put(CommentLogic.REDDIT_ACCOUNT_ID, new String[]{});
         // sampleMap.put(CommentLogic.POST_ID, new String[]{});
            Comment returnedAccount = logic.createEntity( sampleMap );
            logic.add( returnedAccount );

            returnedAccount = logic.getWithId(returnedAccount.getId());

            assertEquals( sampleMap.get( CommentLogic.CREATED )[ 0 ], returnedAccount.getCreated());
            assertEquals( sampleMap.get( CommentLogic.IS_REPLY )[ 0 ], returnedAccount.getIsReply());
            assertEquals( sampleMap.get( CommentLogic.REPLYS )[ 0 ], returnedAccount.getReplys());
            assertEquals( sampleMap.get( CommentLogic.TEXT )[ 0 ], returnedAccount.getText());
            assertEquals( sampleMap.get( CommentLogic.POINTS )[ 0 ], returnedAccount.getPoints());
            logic.delete( returnedAccount );
        }
        
        @Test
        final void testCreateEntity() {
           Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( CommentLogic.CREATED, new String[]{"20111111"});
            sampleMap.put( CommentLogic.IS_REPLY, new String[]{ "true" } );
            sampleMap.put( CommentLogic.REPLYS, new String[]{  "99" } );
            sampleMap.put( CommentLogic.TEXT, new String[]{ "Test" } );
            sampleMap.put( CommentLogic.POINTS, new String[]{"99"});
            sampleMap.put( CommentLogic.ID, new String[]{"99"} );
            sampleMap.put( CommentLogic.UNIQUE_ID, new String[]{"99"} );
            Post post = new Post(1);
          sampleMap.put( CommentLogic.POST_ID, new String[]{String.valueOf(post.getId())});
            sampleMap.put( CommentLogic.REDDIT_ACCOUNT_ID, new String[]{"99"} );
      
            Comment returnedAccount = logic.createEntity( sampleMap );

            assertAccountEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testCreateEntityNullAndEmptyValues() {
            Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( CommentLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
                map.put( CommentLogic.CREATED, new String[]{ expectedEntity.getCreated().toString()} );
                map.put( CommentLogic.REPLYS, new String[]{ Integer.toString(expectedEntity.getReplys())} );
                map.put( CommentLogic.TEXT, new String[]{ expectedEntity.getText()} );
           map.put( CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints()) } );
           
            };

            //idealy every test should be in its own method
            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.ID, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.ID, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.CREATED, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.CREATED, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.TEXT, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.TEXT, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.POINTS, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.POINTS, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        }

        @Test
        final void testCreateEntityBadLengthValues() {
          Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( CommentLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
                map.put( CommentLogic.CREATED, new String[]{ expectedEntity.getCreated().toString()} );
                map.put( CommentLogic.REPLYS, new String[]{ Integer.toString(expectedEntity.getReplys())} );
                map.put( CommentLogic.TEXT, new String[]{ expectedEntity.getText()} );
           map.put( CommentLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints()) } );

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
            sampleMap.replace( CommentLogic.ID, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.ID, new String[]{ "12b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.CREATED, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.CREATED, new String[]{ String.valueOf(new Date(11/11/1111)) } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.POINTS, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.POINTS, new String[]{  } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( CommentLogic.TEXT, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( CommentLogic.TEXT, new String[]{ "Test" } );
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
            sampleMap.put( CommentLogic.ID, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( CommentLogic.CREATED, new String[]{ generateString.apply( 1 ) } );
            sampleMap.put( CommentLogic.POINTS, new String[]{ generateString.apply( 1 ) } );
            sampleMap.put( CommentLogic.REPLYS, new String[]{ generateString.apply( 1 ) } );

            //idealy every test should be in its own method
            Comment returnedAccount = logic.createEntity( sampleMap );
            assertEquals( Integer.parseInt( sampleMap.get( CommentLogic.ID )[ 0 ] ), returnedAccount.getId() );
            assertEquals( sampleMap.get( CommentLogic.CREATED )[ 0 ], returnedAccount.getCreated());
            assertEquals( sampleMap.get( CommentLogic.POINTS )[ 0 ], returnedAccount.getPoints());
            assertEquals( sampleMap.get( CommentLogic.TEXT )[ 0 ], returnedAccount.getText());

            sampleMap = new HashMap<>();
            sampleMap.put( CommentLogic.ID, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( CommentLogic.CREATED, new String[]{ generateString.apply( 45 ) } );
            sampleMap.put( CommentLogic.POINTS, new String[]{ generateString.apply( 45 ) } );
            sampleMap.put( CommentLogic.TEXT, new String[]{ generateString.apply( 45 ) } );

            //idealy every test should be in its own method
            returnedAccount = logic.createEntity( sampleMap );
            assertEquals( Integer.parseInt( sampleMap.get( CommentLogic.ID )[ 0 ] ), returnedAccount.getId() );
            assertEquals( sampleMap.get( CommentLogic.CREATED )[ 0 ], returnedAccount.getCreated());
            assertEquals( sampleMap.get( CommentLogic.REPLYS )[ 0 ], returnedAccount.getReplys());
            assertEquals( sampleMap.get( CommentLogic.TEXT )[ 0 ], returnedAccount.getText());
        }

        @Test
        final void testGetColumnNames() {
            List<String> list = logic.getColumnNames();
            assertEquals( Arrays.asList( "ID","Reddit_Account_ID","Post_ID","Unique_ID","Text","Points","Replys","Is_Reply","Created" ), list );
        }

        @Test
        final void testGetColumnCodes() {
            List<String> list = logic.getColumnCodes();
            assertEquals( Arrays.asList( CommentLogic.ID, CommentLogic.REDDIT_ACCOUNT_ID, CommentLogic.POST_ID, CommentLogic.UNIQUE_ID, CommentLogic.TEXT, CommentLogic.POINTS, CommentLogic.IS_REPLY, CommentLogic.CREATED ), list );
        }

        @Test
        final void testExtractDataAsList() {
            List<?> list = logic.extractDataAsList( expectedEntity );
            assertEquals( expectedEntity.getId(), list.get( 0 ) );
            assertEquals( expectedEntity.getCreated(), list.get( 1 ) );
            assertEquals( expectedEntity.getPoints(), list.get( 2 ) );
            assertEquals( expectedEntity.getReplys(), list.get( 3 ) );
        }
    }
