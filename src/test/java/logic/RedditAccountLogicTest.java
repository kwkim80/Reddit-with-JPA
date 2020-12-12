/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logic;

import common.TomcatStartUp;
import common.ValidationException;
import dal.EMFactory;
import entity.Account;
import entity.RedditAccount;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import javax.persistence.EntityManager;
import static logic.RedditAccountLogic.CREATED;
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
 * @author Jiyeon Choi
 */
public class RedditAccountLogicTest {
    
    private RedditAccountLogic logic;
    private RedditAccount expectedEntity;

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

        logic = LogicFactory.getFor( "RedditAccount" );
        /* **********************************
         * ***********IMPORTANT**************
         * **********************************/
        //we only do this for the test.
        //always create Entity using logic.
        //we manually make the account to not rely on any logic functionality , just for testing
        RedditAccount entity = new RedditAccount();
        entity.setId(99);
        entity.setName( "Junit 5 Test" );
        entity.setLinkPoints(3);
        entity.setCommentPoints(2);
        entity.setCreated(new Date());

        //get an instance of EntityManager
        EntityManager em = EMFactory.getEMF().createEntityManager();
        //start a Transaction
        em.getTransaction().begin();
        //add a Redditaccount to hibernate, Redditaccount is now managed.
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
        //get all the Redditaccounts from the DB
        List<RedditAccount> list = logic.getAll();
        //store the size of list, this way we know how many Redditaccounts exits in DB
        int originalSize = list.size();

        //make sure Redditaccount was created successfully
        assertNotNull( expectedEntity );
        //delete the new Redditaccount
        logic.delete( expectedEntity );

        //get all Redditaccounts again
        list = logic.getAll();
        //the new size of Redditaccounts must be one less
        assertEquals( originalSize - 1, list.size() );
    }

    /**
     * helper method for testing all RedditAccount fields
     *
     * @param expected
     * @param actual
     */
    private void assertRedditAccountEquals( RedditAccount expected, RedditAccount actual ) {
        //assert all field to guarantee they are the same
        assertEquals( expected.getId(), actual.getId() );
        assertEquals( expected.getName(), actual.getName() );
        assertEquals( expected.getLinkPoints(), actual.getLinkPoints() );
        assertEquals( expected.getCommentPoints(), actual.getCommentPoints() );
    }

    @Test
    final void testGetWithId() {
        //using the id of test Redditaccount get another Redditaccount from logic
        RedditAccount returnedRedditAccount = logic.getWithId( expectedEntity.getId() );

        //the two accounts (testAcounts and returnedAccounts) must be the same
        assertRedditAccountEquals( expectedEntity, returnedRedditAccount );
    }

    @Test
    final void testGetAccountWithDisplayName() {
        RedditAccount returnedRedditAccount = logic.getRedditAccountWithName(expectedEntity.getName() );

        //the two Redditaccounts (testRedditAcounts and returnedRedditAccounts) must be the same
        assertRedditAccountEquals( expectedEntity, returnedRedditAccount );
    }

    @Test
    final void testGetRedditAccountWIthUser() {
        RedditAccount returnedRedditAccount = logic.getRedditAccountWithName( expectedEntity.getName() );

        //the two Redditaccounts (testRedditAcounts and returnedRedditAccounts) must be the same
        assertRedditAccountEquals( expectedEntity, returnedRedditAccount );
    }

    @Test
    final void testGetRedditAccountsWithLinkPoints() {
        int foundFull = 0;
        List<RedditAccount> returnedRedditAccounts = logic.getRedditAccountsWithLinkPoints(expectedEntity.getLinkPoints());
        for( RedditAccount Redditaccount: returnedRedditAccounts ) {
            //all accounts must have the same password
            assertEquals( expectedEntity.getLinkPoints(), Redditaccount.getLinkPoints() );
            //exactly one account must be the same
            if( Redditaccount.getId().equals( expectedEntity.getId() ) ){
                assertRedditAccountEquals( expectedEntity, Redditaccount );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }

    @Test
    final void testSearch() {
        int foundFull = 0;
        //search for a substring of one of the fields in the expectedAccount
        String searchString = expectedEntity.getName().substring( 3 );
        //in Redditaccount we only search for display name and user, this is completely based on your design for other entities.
        List<RedditAccount> returnedRedditAccounts = logic.search( searchString );
        for( RedditAccount Redditaccount: returnedRedditAccounts ) {
            //all Redditaccounts must contain the substring
            assertTrue( Redditaccount.getName().contains( searchString ) || Redditaccount.getName().contains( searchString ) );
            //exactly one Redditaccount must be the same
            if( Redditaccount.getId().equals( expectedEntity.getId() ) ){
                assertRedditAccountEquals( expectedEntity, Redditaccount );
                foundFull++;
            }
        }
        assertEquals( 1, foundFull, "if zero means not found, if more than one means duplicate" );
    }

    @Test
    final void testCreateEntityAndAdd() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ "Test Create Entity" } );
        sampleMap.put( RedditAccountLogic.LINK_POINTS, new String [] {"1"} );
        sampleMap.put( RedditAccountLogic.COMMENT_POINTS, new String [] {"2"});
        sampleMap.put( RedditAccountLogic.CREATED, new String [] {""});

        RedditAccount returnedRedditAccount = logic.createEntity( sampleMap );
        logic.add( returnedRedditAccount );

        returnedRedditAccount = logic.getRedditAccountWithName( returnedRedditAccount.getName() );

        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedRedditAccount.getName() );
        assertEquals( sampleMap.get( RedditAccountLogic.LINK_POINTS )[ 0 ], String.valueOf(returnedRedditAccount.getLinkPoints()));
        assertEquals( sampleMap.get( RedditAccountLogic.COMMENT_POINTS )[ 0 ], String.valueOf(returnedRedditAccount.getCommentPoints()));

        logic.delete( returnedRedditAccount );
    }

    @Test
    final void testCreateEntity() {
        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ expectedEntity.getName() } );
        sampleMap.put( RedditAccountLogic.LINK_POINTS, new String[]{ String.valueOf(expectedEntity.getLinkPoints()) });
        sampleMap.put( RedditAccountLogic.COMMENT_POINTS, new String[]{ String.valueOf(expectedEntity.getCommentPoints())} );

        RedditAccount returnedRedditAccount = logic.createEntity( sampleMap );

        assertRedditAccountEquals( expectedEntity, returnedRedditAccount );
    }

    @Test
    final void testCreateEntityNullAndEmptyValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( RedditAccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( RedditAccountLogic.NAME, new String[]{ expectedEntity.getName() } );
        };

        //idealy every test should be in its own method
        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.ID, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.ID, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );

        fillMap.accept( sampleMap );
        sampleMap.replace( RedditAccountLogic.NAME, null );
        assertThrows( NullPointerException.class, () -> logic.createEntity( sampleMap ) );
        sampleMap.replace( RedditAccountLogic.NAME, new String[]{} );
        assertThrows( IndexOutOfBoundsException.class, () -> logic.createEntity( sampleMap ) );
    }

    @Test
    final void testCreateEntityBadLengthValues() {
        Map<String, String[]> sampleMap = new HashMap<>();
        Consumer<Map<String, String[]>> fillMap = ( Map<String, String[]> map ) -> {
            map.clear();
            map.put( RedditAccountLogic.ID, new String[]{ Integer.toString( expectedEntity.getId() ) } );
            map.put( RedditAccountLogic.NAME, new String[]{ expectedEntity.getName() } );
        };

        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            //from 97 inclusive to 123 exclusive
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };
   }

    @Test
    final void testCreateEntityEdgeValues() {
        IntFunction<String> generateString = ( int length ) -> {
            //https://www.baeldung.com/java-random-string#java8-alphabetic
            return new Random().ints( 'a', 'z' + 1 ).limit( length )
                    .collect( StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append )
                    .toString();
        };

        Map<String, String[]> sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ generateString.apply( 1 ) } );


        //idealy every test should be in its own method
        RedditAccount returnedRedditAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedRedditAccount.getId() );
        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedRedditAccount.getName() );

        sampleMap = new HashMap<>();
        sampleMap.put( RedditAccountLogic.ID, new String[]{ Integer.toString( 1 ) } );
        sampleMap.put( RedditAccountLogic.NAME, new String[]{ generateString.apply( 45 ) } );

        //idealy every test should be in its own method
        returnedRedditAccount = logic.createEntity( sampleMap );
        assertEquals( Integer.parseInt( sampleMap.get( AccountLogic.ID )[ 0 ] ), returnedRedditAccount.getId() );
        assertEquals( sampleMap.get( RedditAccountLogic.NAME )[ 0 ], returnedRedditAccount.getName() );

    }

    @Test
    final void testGetColumnNames() {
        List<String> list = logic.getColumnNames();
        assertEquals( Arrays.asList( "ID", "Name", "Link_Points", "Comment_Points","Created" ), list );
    }

    @Test
    final void testGetColumnCodes() {
        List<String> list = logic.getColumnCodes();
        assertEquals( Arrays.asList( RedditAccountLogic.ID, RedditAccountLogic.NAME, RedditAccountLogic.LINK_POINTS, RedditAccountLogic.COMMENT_POINTS,RedditAccountLogic.CREATED ), list );
    }

    @Test
    final void testExtractDataAsList() {
        List<?> list = logic.extractDataAsList( expectedEntity );
        assertEquals( expectedEntity.getId(), list.get( 0 ) );
        assertEquals( expectedEntity.getName(), list.get( 1 ) );
        assertEquals( expectedEntity.getLinkPoints(), list.get( 2 ) );
        assertEquals( expectedEntity.getCommentPoints(), list.get( 3 ) );
        assertEquals( expectedEntity.getCreated(), list.get( 4 ) );
    }
}