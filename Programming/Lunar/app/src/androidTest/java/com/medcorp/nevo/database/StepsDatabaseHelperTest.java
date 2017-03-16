package com.medcorp.lunar.database;

import android.test.AndroidTestCase;

import com.medcorp.lunar.database.entry.StepsDatabaseHelper;
import com.medcorp.lunar.database.entry.UserDatabaseHelper;
import com.medcorp.lunar.model.Steps;
import com.medcorp.lunar.model.User;
import com.medcorp.lunar.util.Common;

import net.medcorp.library.ble.util.Optional;

import java.util.Date;

/**
 * Created by gaillysu on 15/12/8.
 */
public class StepsDatabaseHelperTest extends AndroidTestCase {
    private StepsDatabaseHelper db;
    private UserDatabaseHelper dbUser;

    //assume one user login and make it owner all sleep data
    private User loginUser;

    private Steps addSteps;
    private Steps updateSteps;
    private Steps removeSteps;
    private Date today;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        dbUser = new UserDatabaseHelper();
        loginUser = new User("Karl", "Chow", 1, 946728000000l, 20, 70, 180, 946728000000l, "", "");

        User thisuser = dbUser.add(loginUser);
        assertEquals(false, thisuser == null);
        //set user ID as a login user
        loginUser.setId(thisuser.getId());

        db = new StepsDatabaseHelper();

        //this is today's data, today format is YYYYMMDD 00:00:00
        today = Common.removeTimeFromDate(new Date());

        //initialize sample data
        addSteps = new Steps(new Date().getTime(), today.getTime(), 1000, 800, 200, 500, 10, "", "", "", 0, 0, 0, 10000, 0, 0, 0, 0, "");
        updateSteps = new Steps(new Date().getTime(), today.getTime(), 2000, 1800, 200, 1000, 20, "", "", "", 0, 0, 0, 10000, 0, 0, 0, 0, "");
        removeSteps = new Steps(new Date().getTime(), today.getTime(), 3000, 2800, 200, 1500, 30, "", "", "", 0, 0, 0, 10000, 0, 0, 0, 0, "");

        //set who owner these data.
        addSteps.setNevoUserID(loginUser.getNevoUserID());
        updateSteps.setNevoUserID(loginUser.getNevoUserID());
        removeSteps.setNevoUserID(loginUser.getNevoUserID());

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd() {
        //add sample data
        Steps thisSteps1 = db.add(addSteps);
        assertEquals(false, thisSteps1 == null);

        //read today data
        Steps thisSteps2 = db.get(loginUser.getNevoUserID(), today);
        assertEquals(false, thisSteps2 == null);

        //compare data
        assertEquals(addSteps.getSteps(), thisSteps2.getSteps());
    }

    public void testUpdate() {

        Steps thisSteps1 = db.add(updateSteps);
        assertEquals(false, thisSteps1 == null);
        updateSteps = thisSteps1;

        updateSteps.setSteps((int) (Math.random() * 10000));
        updateSteps.setGoal((int) (Math.random() * 10000));
        assertEquals(true, db.update(updateSteps));

        //read it again
        Steps thisSteps2 = db.get(loginUser.getNevoUserID(), today);
        assertEquals(false, thisSteps2 == null);

        //compare data
        assertEquals(updateSteps.getSteps(), thisSteps2.getSteps());
        assertEquals(updateSteps.getGoal(), thisSteps2.getGoal());
    }

    public void testRemove() {
        //add "remove" data
        Steps thisSteps1 = db.add(removeSteps);
        assertEquals(false, thisSteps1 == null);

        //check add result
        Steps thisSteps2 = db.get(loginUser.getNevoUserID(), today);
        assertEquals(false, thisSteps2 == null);
        assertEquals(removeSteps.getSteps(), thisSteps2.getSteps());
        assertEquals(removeSteps.getGoal(), thisSteps2.getGoal());

        //remove it
        db.remove(loginUser.getNevoUserID(), today);

        //read it again, check it exist in database.
        Steps thisSteps3 = db.get(loginUser.getNevoUserID(), today);
        assertEquals(true, thisSteps3 == null);
    }
}
