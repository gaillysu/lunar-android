package com.medcorp.nevo.database;

import android.test.AndroidTestCase;

import com.medcorp.lunar.database.entry.SleepDatabaseHelper;
import com.medcorp.lunar.database.entry.UserDatabaseHelper;
import com.medcorp.lunar.model.Sleep;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Common;

import java.util.Date;

/**
 * Created by Karl on 12/7/15.
 */
public class SleepDatabaseHelperTest extends AndroidTestCase {

    private SleepDatabaseHelper db;
    private UserDatabaseHelper  dbUser;

    //assume one user login and make it owner all sleep data
    private User loginUser;

    private Sleep addSleep;
    private Sleep  updateSleep;
    private Sleep  removeSleep;
    private Date   today;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dbUser = new UserDatabaseHelper();
        loginUser = new User("Karl","Chow", 1, 946728000000l, 20, 70, 180, 946728000000l,"","");

        User thisuser = dbUser.add(loginUser);
        assertEquals(false, thisuser==null);
        //set user ID as a login user
        loginUser.setId(thisuser.getId());
        loginUser.setNevoUserID(thisuser.getId() + "");
        db = new SleepDatabaseHelper();

        //this is today's data, today format is YYYYMMDD 00:00:00
        today = Common.removeTimeFromDate(new Date());

        // sample data
        addSleep = new Sleep(new Date().getTime(), today.getTime(),480,60,360,60,"","","","",0,0,0,"");
        //here must set which one owner this data
        addSleep.setNevoUserID(loginUser.getNevoUserID());

        updateSleep = new Sleep(new Date().getTime(), today.getTime(),490,60,370,60,"","","","",0,0,0,"");
        updateSleep.setNevoUserID(loginUser.getNevoUserID());

        removeSleep = new Sleep(new Date().getTime(), today.getTime(),500,60,380,60,"","","","",0,0,0,"");
        removeSleep.setNevoUserID(loginUser.getNevoUserID());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd()
    {
        //add sample data
        Sleep thisSleep1 = db.add(addSleep);
        assertEquals(false,thisSleep1==null);

        //read sample data
        Sleep thisSleep2 = db.get(loginUser.getNevoUserID(),today);
        assertEquals(false, thisSleep2==null);

        //compare data
        assertEquals(addSleep.getTotalSleepTime(), thisSleep2.getTotalSleepTime());
    }

    public void testUpdate()
    {

        Sleep thisSleep1 = db.add(updateSleep);
        assertEquals(false, thisSleep1==null);
        updateSleep = thisSleep1;


        updateSleep.setTotalSleepTime((int) (Math.random()*10000));

        assertEquals(true, db.update(updateSleep));

        //read data
        Sleep thisSleep2 = db.get(loginUser.getNevoUserID(),today);
        assertEquals(false, thisSleep2==null);

        //compare data
        assertEquals(updateSleep.getTotalSleepTime(), thisSleep2.getTotalSleepTime());
    }

    public void testRemove()
    {
        //add sample data
         Sleep thisSleep1 = db.add(removeSleep);
        assertEquals(false, thisSleep1==null);

        //make sure it is saved ok
        Sleep thisSleep2 = db.get(loginUser.getNevoUserID(),today);
        assertEquals(false,thisSleep2==null);
        assertEquals(removeSleep.getTotalSleepTime(),thisSleep2.getTotalSleepTime());

        //remove it
        assertEquals(true, true);
        db.remove(loginUser.getNevoUserID(),today);
        //read it again,check result
        Sleep thisSleep3 = db.get(loginUser.getNevoUserID(),today);
        assertEquals(true, thisSleep3==null);
    }
}
