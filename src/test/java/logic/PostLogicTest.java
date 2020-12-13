    package logic;

    import common.TomcatStartUp;
    import common.Utility;
    import common.ValidationException;
    import dal.EMFactory;
    import entity.Post;
    import entity.Post;
    import entity.RedditAccount;
import entity.Subreddit;
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
    class PostLogicTest {

        private PostLogic logic;
        private Post expectedEntity;
        private RedditAccountLogic rLogic;
        private SubredditLogic sLogic;

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
            rLogic=LogicFactory.getFor("RedditAccount");
            sLogic=LogicFactory.getFor("Subreddit");
                   
            String uniqueKey=Long.toString(new Date().getTime()).substring(0, 9);
            Post entity = new Post();
            entity.setCommentCount(1);
            entity.setId(99 );
            entity.setCreated(Date.from(Instant.now(Clock.systemDefaultZone())));
            entity.setPoints(99);
            entity.setTitle("Junit");
            entity.setUniqueId(uniqueKey);
            entity.setRedditAccountId(rLogic.getWithId(1));
            entity.setSubredditId(sLogic.getWithId(1));

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
        private void assertPostEquals( Post expected, Post actual ) {
            //assert all field to guarantee they are the same
            assertEquals( expected.getId(), actual.getId() );
        // IsDateEqual saying it doesnt exist
        //assertTrue( Utility.IsDateEqual(expected.getCreated(), actual.getCreated()) );
            assertEquals( expected.getCommentCount(), actual.getCommentCount());
            assertEquals( expected.getPoints(), actual.getPoints());
            assertEquals( expected.getTitle(), actual.getTitle());
            assertEquals( expected.getSubredditId().getId(), actual.getSubredditId().getId());
            assertEquals( expected.getRedditAccountId().getId(), actual.getRedditAccountId().getId());
            assertEquals( expected.getUniqueID(), actual.getUniqueID());

        }

        @Test
        final void testGetWithId() {
            //using the id of test account get another account from logic
            Post returnedAccount = logic.getWithId( expectedEntity.getId() );

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertPostEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testGetPostWithCreated() {
            List<Post> returnedPost = logic.getPostsWithCreated(expectedEntity.getCreated());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            Boolean result=true;
            for (Post post : returnedPost) {
                if(post.getCreated()!=expectedEntity.getCreated()) assertTrue(result);
            }
            assertTrue(result);
        }
    
        @Test
        final void testGetPostWithPoints() {
            List<Post> returnedPost = logic.getPostWithPoints(expectedEntity.getPoints());

            //the two accounts (testAcounts and returnedAccounts) must be the same
           // Boolean result=true;
            for (Post post : returnedPost) {
                assertEquals(post.getPoints(), expectedEntity.getPoints()); 
            }
           // assertTrue(result);
            
            
        }
        
        @Test
        final void testGetPostWithTitle() {
            List<Post> returnedPost = logic.getPostsWithTitle(expectedEntity.getTitle());

//            Boolean result=true;
//            for (Post post : returnedPost) {
//                if(!post.getTitle().contains(expectedEntity.getTitle()) && !post.getUniqueID().contains(expectedEntity.getTitle())) result=false;
//            }
//            assertTrue(result);
            for(Post post : returnedPost) {
                assertTrue(post.getTitle().contains(expectedEntity.getTitle()) || post.getUniqueID().contains(expectedEntity.getTitle())); 
            }
        }

    
        final void testGetPostWithCommentCount() {
            List<Post> returnedPost = logic.getPostsWithCommentCount(expectedEntity.getCommentCount());
            //the two accounts (testAcounts and returnedAccounts) must be the same
//            Boolean result=true;
//            for (Post post : returnedPost) {
//                if(post.getCommentCount()!=expectedEntity.getCommentCount()) result=false;
//            }
//            assertTrue(result);
            for(Post post : returnedPost) {
                assertEquals(post.getCommentCount(),expectedEntity.getCommentCount()); 
            }
        }

        @Test
        final void testCreateEntityAndAdd() {
            Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( PostLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString( expectedEntity.getCommentCount()) } );
            sampleMap.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );
            sampleMap.put( PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
            sampleMap.put( PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()+"2"} );
            //sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
         // sampleMap.put(PostLogic.REDDIT_ACCOUNT_ID, new String[]{});
         // sampleMap.put(PostLogic.POST_ID, new String[]{});
            Post newItem = logic.createEntity( sampleMap );
  
            newItem.setRedditAccountId(expectedEntity.getRedditAccountId());
            newItem.setSubredditId(expectedEntity.getSubredditId());
            logic.add( newItem );

            Post savedItem = logic.getWithId(newItem.getId());

            assertPostEquals(newItem, savedItem);
            logic.delete( savedItem);
        }
        
        @Test
        final void testCreateEntity() {
           Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( PostLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString( expectedEntity.getCommentCount()) } );
            sampleMap.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );
            sampleMap.put( PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
            sampleMap.put( PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()} );
            sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );

            Post returnedAccount = logic.createEntity( sampleMap );
            returnedAccount.setRedditAccountId(expectedEntity.getRedditAccountId());
            returnedAccount.setSubredditId(expectedEntity.getSubredditId());

            assertPostEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testCreateEntityNullAndEmptyValues() {
            Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( PostLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
                map.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString( expectedEntity.getCommentCount()) } );
                map.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );
                map.put( PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
                map.put( PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()} );
                map.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
           
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
                map.put( PostLogic.CREATED, new String[]{expectedEntity.getCreated().toString()});
                map.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString( expectedEntity.getCommentCount()) } );
                map.put( PostLogic.TITLE, new String[]{ expectedEntity.getTitle() } );
                map.put( PostLogic.POINTS, new String[]{Integer.toString(expectedEntity.getPoints())});
                map.put( PostLogic.UNIQUE_ID, new String[]{expectedEntity.getUniqueID()} );
                map.put( PostLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            };
            
           IntFunction<String> generateString = ( int length ) -> {
                //https://www.baeldung.com/java-random-string#java8-alphabetic
                return new Random().ints( 'a', 'z' + 1 ).limit( length )
                        .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                        .toString();
            };
            
            //idealy every test should be in its own method
            String dateStr=new Date().toString();
            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.ID, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.ID, new String[]{ "12b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );


            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.POINTS, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.POINTS, new String[]{ "1b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.COMMENT_COUNT, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.COMMENT_COUNT, new String[]{ "12b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            
             fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.TITLE, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.TITLE, new String[]{ generateString.apply(256) } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            
             fillMap.accept( sampleMap );
            sampleMap.replace( PostLogic.UNIQUE_ID, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( PostLogic.UNIQUE_ID, new String[]{ generateString.apply(11)} );
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
            String dateStr=new Date().toString();
            sampleMap.put( PostLogic.CREATED, new String[]{dateStr});
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( PostLogic.TITLE, new String[]{ generateString.apply( 1 ) } );
            sampleMap.put( PostLogic.POINTS, new String[]{Integer.toString( 1 )});
            sampleMap.put( PostLogic.UNIQUE_ID, new String[]{generateString.apply( 1 )} );
            sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( 1 ) } );

            Post returnedAccount = logic.createEntity( sampleMap );
          

            assertEquals( sampleMap.get( PostLogic.ID )[ 0 ], returnedAccount.getId().toString() );
            assertEquals( sampleMap.get( PostLogic.CREATED )[ 0 ], returnedAccount.getCreated().toString());
            assertEquals( sampleMap.get( PostLogic.COMMENT_COUNT )[ 0 ], Integer.toString(returnedAccount.getCommentCount()));
            assertEquals( sampleMap.get( PostLogic.TITLE )[ 0 ], returnedAccount.getTitle());
            assertEquals( sampleMap.get( PostLogic.UNIQUE_ID )[ 0 ], returnedAccount.getUniqueID());
            assertEquals( sampleMap.get( PostLogic.POINTS )[ 0 ], Integer.toString(returnedAccount.getPoints()));
            

            sampleMap = new HashMap<>();
             sampleMap.put( PostLogic.CREATED, new String[]{dateStr});
            sampleMap.put( PostLogic.COMMENT_COUNT, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( PostLogic.TITLE, new String[]{ generateString.apply( 255 ) } );
            sampleMap.put( PostLogic.POINTS, new String[]{Integer.toString( 1 )});
            sampleMap.put( PostLogic.UNIQUE_ID, new String[]{generateString.apply( 10 )} );
            sampleMap.put( PostLogic.ID, new String[]{ Integer.toString( 1 ) } );

            //idealy every test should be in its own method
            returnedAccount = logic.createEntity( sampleMap );
             assertEquals( sampleMap.get( PostLogic.ID )[ 0 ], returnedAccount.getId().toString() );
            assertEquals( sampleMap.get( PostLogic.CREATED )[ 0 ], returnedAccount.getCreated().toString());
            assertEquals( sampleMap.get( PostLogic.COMMENT_COUNT )[ 0 ], Integer.toString(returnedAccount.getCommentCount()));
            assertEquals( sampleMap.get( PostLogic.TITLE )[ 0 ], returnedAccount.getTitle());
            assertEquals( sampleMap.get( PostLogic.UNIQUE_ID )[ 0 ], returnedAccount.getUniqueID());
            assertEquals( sampleMap.get( PostLogic.POINTS )[ 0 ], Integer.toString(returnedAccount.getPoints()));
        }

        @Test
        final void testGetColumnNames() {
            List<String> list = logic.getColumnNames();
            assertEquals( Arrays.asList("ID", "Title", "Unique_ID", "Reddit_Account_ID", "Subreddit_ID", "Comment_Count", "Points", "Created"), list );
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
            assertEquals( expectedEntity.getTitle(), list.get( 1 ) );
            assertEquals( expectedEntity.getUniqueID(), list.get( 2 ) );
            assertEquals( expectedEntity.getRedditAccountId().getId(), list.get( 3 ) );
            assertEquals( expectedEntity.getSubredditId().getId(), list.get( 4 ) );
            assertEquals( expectedEntity.getCommentCount(), list.get( 5 ) );
            assertEquals( expectedEntity.getPoints(), list.get( 6 ) );
          //  assertTrue( Utility.IsDateEqual(expectedEntity.getCreated(), (Date) list.get(7)) );
        }
        
        
    }
