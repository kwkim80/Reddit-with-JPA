    package logic;

    import common.TomcatStartUp;
    import common.ValidationException;
    import dal.EMFactory;
    import entity.Subreddit;
    import entity.Post;
    import entity.RedditAccount;
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
    class SubredditLogicTest {

        private SubredditLogic logic;
        private Subreddit expectedEntity;

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

            logic = LogicFactory.getFor( "Subreddit" );
            /* **********************************
             * ***********IMPORTANT**************
             * **********************************/
            //we only do this for the test.
            //always create Entity using logic.
            //we manually make the account to not rely on any logic functionality , just for testing
            IntFunction<String> generateString = ( int length ) -> {
                //https://www.baeldung.com/java-random-string#java8-alphabetic
                //from 97 inclusive to 123 exclusive
                return new Random().ints( 'a', 'z' + 1 ).limit( length )
                        .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                        .toString();
            };
            
            Subreddit entity = new Subreddit();
            entity.setSubscribers(1);
            entity.setName(generateString.apply(10));
            entity.setUrl(generateString.apply(20) );


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
            List<Subreddit> list = logic.getAll();
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
        private void assertAccountEquals( Subreddit expected, Subreddit actual ) {
            //assert all field to guarantee they are the same
           assertEquals( expected.getId(), actual.getId() );
            assertEquals( expected.getName(), actual.getName() );
            assertEquals( expected.getSubscribers(), actual.getSubscribers());
            assertEquals( expected.getUrl(), actual.getUrl());

        }

        @Test
        final void testGetWithId() {
            //using the id of test account get another account from logic
            Subreddit returnedAccount = logic.getWithId( expectedEntity.getId() );

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testGetSubredditWithName() {
            Subreddit returnedSubreddit = logic.getSubredditWithName(expectedEntity.getName());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedSubreddit );
        }
    
        @Test
        final void testGetSubredditWithUrl() {
            Subreddit returnedSubreddit = logic.getSubredditsWithUrl(expectedEntity.getUrl());

            //the two accounts (testAcounts and returnedAccounts) must be the same
            assertAccountEquals( expectedEntity, returnedSubreddit );
        }
        
                @Test
        final void testSubredditsWithSubscribers() {
          List< Subreddit> returnedSubreddit = logic.getSubredditsWithSubscribers(expectedEntity.getSubscribers());

             for( Subreddit sub : returnedSubreddit) {   
                assertEquals(sub.getSubscribers(),expectedEntity.getSubscribers()); 
            }
        }


        @Test
        final void testCreateEntityAndAdd() {
            Map<String, String[]> sampleMap = new HashMap<>();
            IntFunction<String> generateString = ( int length ) -> {
                //https://www.baeldung.com/java-random-string#java8-alphabetic
                //from 97 inclusive to 123 exclusive
                return new Random().ints( 'a', 'z' + 1 ).limit( length )
                        .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                        .toString();
            };
            sampleMap.put( SubredditLogic.NAME, new String[]{ generateString.apply(10) } );
            sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString(expectedEntity.getSubscribers()) } );
            sampleMap.put( SubredditLogic.URL, new String[]{ generateString.apply(20) } );
 
            Subreddit newItem = logic.createEntity( sampleMap );
            logic.add( newItem );

            Subreddit savedItem = logic.getWithId(newItem.getId());

            assertAccountEquals(newItem, savedItem);
            logic.delete( savedItem );
        }
        
        @Test
        final void testCreateEntity() {
           Map<String, String[]> sampleMap = new HashMap<>();
            sampleMap.put( SubredditLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) });
            sampleMap.put( SubredditLogic.NAME, new String[]{ expectedEntity.getName() } );
            sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString( expectedEntity.getSubscribers()) } );
            sampleMap.put( SubredditLogic.URL, new String[]{ expectedEntity.getUrl() } );
            Subreddit returnedAccount = logic.createEntity( sampleMap );

            assertAccountEquals( expectedEntity, returnedAccount );
        }

        @Test
        final void testCreateEntityNullAndEmptyValues() {
            Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( SubredditLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
                map.put( SubredditLogic.NAME, new String[]{ expectedEntity.getName()} );
                map.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString(expectedEntity.getSubscribers())} );
                map.put( SubredditLogic.URL, new String[]{ expectedEntity.getUrl()} );
  
           
            };

            //idealy every test should be in its own method
            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.ID, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.ID, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.NAME, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.NAME, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.SUBSCRIBERS, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.SUBSCRIBERS, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.URL, null );
            assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.URL, new String[]{} );
            assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
        }

        @Test
        final void testCreateEntityBadLengthValues() {
          Map<String, String[]> sampleMap = new HashMap<>();
            Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
                map.clear();
                map.put( SubredditLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
                map.put( SubredditLogic.NAME, new String[]{ expectedEntity.getName().toString()} );
                map.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString(expectedEntity.getSubscribers())} );
                map.put( SubredditLogic.URL, new String[]{ expectedEntity.getUrl()} );

            };
            IntFunction<String> generateString = ( int length ) -> {
                //https://www.baeldung.com/java-random-string#java8-alphabetic
                //from 97 inclusive to 123 exclusive
                return new Random().ints( 'a', 'z' + 1 ).limit( length )
                        .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                        .toString();
            };
         
            //idealy every test should be in its own method
            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.ID, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.ID, new String[]{ "12b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.NAME, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.NAME, new String[]{ String.valueOf(generateString.apply(101)) } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.SUBSCRIBERS, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.SUBSCRIBERS, new String[]{ "12b" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );

            fillMap.accept( sampleMap );
            sampleMap.replace( SubredditLogic.URL, new String[]{ "" } );
            assertThrows( ValidationException.class, () -> logic.createEntity( sampleMap ) );
            sampleMap.replace( SubredditLogic.URL, new String[]{ generateString.apply(256)} );
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
            sampleMap.put( SubredditLogic.ID, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( SubredditLogic.NAME, new String[]{ generateString.apply( 1 ) } );
            sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString(1) } );
            sampleMap.put( SubredditLogic.URL, new String[]{ generateString.apply( 1 ) } );

            //idealy every test should be in its own method
            Subreddit returnedAccount = logic.createEntity( sampleMap );
            
            assertEquals( Integer.parseInt( sampleMap.get( SubredditLogic.ID )[ 0 ] ), returnedAccount.getId() );
            assertEquals( sampleMap.get( SubredditLogic.NAME )[ 0 ], returnedAccount.getName());
            assertEquals( sampleMap.get( SubredditLogic.SUBSCRIBERS )[ 0 ], Integer.toString(returnedAccount.getSubscribers()));
            assertEquals( sampleMap.get( SubredditLogic.URL )[ 0 ], returnedAccount.getUrl());

            sampleMap = new HashMap<>();
            sampleMap.put( SubredditLogic.ID, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( SubredditLogic.NAME, new String[]{ generateString.apply( 100 ) } );
            sampleMap.put( SubredditLogic.SUBSCRIBERS, new String[]{ Integer.toString( 1 ) } );
            sampleMap.put( SubredditLogic.URL, new String[]{ generateString.apply( 255 ) } );

            //idealy every test should be in its own method
            returnedAccount = logic.createEntity( sampleMap );
            assertEquals( Integer.parseInt( sampleMap.get( SubredditLogic.ID )[ 0 ] ), returnedAccount.getId() );
            assertEquals( sampleMap.get( SubredditLogic.NAME )[ 0 ], returnedAccount.getName());
            assertEquals( sampleMap.get( SubredditLogic.SUBSCRIBERS )[ 0 ], Integer.toString(returnedAccount.getSubscribers()));
            assertEquals( sampleMap.get( SubredditLogic.URL )[ 0 ], returnedAccount.getUrl());
        }

        @Test
        final void testGetColumnNames() {
            List<String> list = logic.getColumnNames();
            assertEquals(  Arrays.asList( "ID", "Name", "Url", "Subscribers"), list );
        }

        @Test
        final void testGetColumnCodes() {
            List<String> list = logic.getColumnCodes();
            assertEquals( Arrays.asList( SubredditLogic.ID, SubredditLogic.NAME, SubredditLogic.URL, SubredditLogic.SUBSCRIBERS), list );
        }

        @Test
        final void testExtractDataAsList() {
            List<?> list = logic.extractDataAsList( expectedEntity );
            assertEquals( expectedEntity.getId(), list.get( 0 ) );
            assertEquals( expectedEntity.getName(), list.get( 1 ) );
            assertEquals( expectedEntity.getUrl(), list.get( 2 ) );
            assertEquals( expectedEntity.getSubscribers(), list.get( 3 ) );
            
        }
    }
