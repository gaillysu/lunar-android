package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.lunar.database.entry.UserDatabaseHelper;
import com.medcorp.lunar.model.User;
/**
 * Created by karl-john on 1/12/15.
 */
public class UserDatabaseHelperTest extends AndroidTestCase {

    private UserDatabaseHelper db;
    private User dummyUser;
    private User addUser;
    private User removeUser;
    private User updateUser;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = new UserDatabaseHelper();
        dummyUser = new User("Karl","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"","");

        addUser = new User("KarlAdd","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"","");
        removeUser = new User("KarlRemove","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"","");
        updateUser = new User("KarlUpdate","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"","");


    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

    }

    public void testAdd()
    {
        addUser.setNevoUserID("999");
        User thisUser1 = db.add(addUser);
        assertEquals(false, thisUser1==null);
        assertEquals(true, thisUser1.getId() != -1);
        User thisUser2 = db.get(thisUser1.getNevoUserID(),null);

        assertEquals(addUser.getId(), thisUser2.getId());
        assertEquals(addUser.getFirstName(), thisUser2.getFirstName());
        assertEquals(addUser.getLastName(),thisUser2.getLastName());
        assertEquals(addUser.getAge(),thisUser2.getAge());
    }
    public void testRemove()
    {
        addUser.setNevoUserID("1000");
        User thisUser1 = db.add(removeUser);
        assertEquals(false, thisUser1==null);
        assertEquals(true, thisUser1.getId()!= -1);
        db.remove(thisUser1.getNevoUserID(), null);
        User thisUser2 = db.get(thisUser1.getNevoUserID(),null);
        assertEquals(true, thisUser2==null);
    }

    public void testUpdate()
    {

        //add new user to update
        updateUser.setNevoUserID("1001");
        User updatedUser = db.add(updateUser);
        assertEquals(false, updatedUser==null);
        updateUser = updatedUser;

        updateUser.setFirstName("werewr");
        updateUser.setLastName("ertretwq11");
        updateUser.setAge(100);

        assertEquals(true, db.update(updateUser));

        User thisUser2 = db.get(updateUser.getNevoUserID(),null);
        assertEquals(false, thisUser2==null);

        assertEquals(updateUser.getId(),thisUser2.getId());
        assertEquals(updateUser.getFirstName(), thisUser2.getFirstName());
        assertEquals(updateUser.getLastName(), thisUser2.getLastName());
        assertEquals(updateUser.getAge(),thisUser2.getAge());

    }

}