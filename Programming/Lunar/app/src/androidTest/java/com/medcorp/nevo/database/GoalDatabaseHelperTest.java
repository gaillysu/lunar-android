package com.medcorp.lunar.database;

import android.test.AndroidTestCase;

import com.medcorp.lunar.database.entry.GoalDatabaseHelper;
import com.medcorp.lunar.model.Goal;

import net.medcorp.library.ble.util.Optional;


/**
 * Created by gaillysu on 15/12/8.
 */
public class GoalDatabaseHelperTest extends AndroidTestCase {

    private GoalDatabaseHelper db;
    private Goal addGoal;
    private Goal updateGoal;
    private Goal removeGoal;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        db = new GoalDatabaseHelper();
        addGoal = new Goal("Low",false,5000);
        updateGoal = new Goal("Normal",true,10000);
        removeGoal = new Goal("Player",false,20000);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAdd()
    {
        Goal thisPreset1 = db.add(addGoal);
        assertEquals(false,thisPreset1);
        addGoal = thisPreset1;

        Goal thisPreset2 = db.get(addGoal.getId());
        assertEquals(false,thisPreset2);

        assertEquals(addGoal.getLabel(),thisPreset2.getLabel());
        assertEquals(addGoal.getSteps(),thisPreset2.getSteps());
        assertEquals(addGoal.isStatus(),thisPreset2.isStatus());

    }
    public void testUpdate()
    {
        Goal thisPreset1 = db.add(updateGoal);
        assertEquals(false,thisPreset1);
        updateGoal = thisPreset1;

        updateGoal.setStatus(!updateGoal.isStatus());
        updateGoal.setLabel("34terwfgw");
        updateGoal.setSteps((int) (Math.random() * 10000));

        assertEquals(true, db.update(updateGoal));

        Goal thisPreset2 = db.get(updateGoal.getId());
        assertEquals(false,thisPreset2);

        assertEquals(thisPreset2.isStatus(), updateGoal.isStatus());
        assertEquals(thisPreset2.getLabel(), updateGoal.getLabel());
        assertEquals(thisPreset2.getSteps(), updateGoal.getSteps());

    }
    public void testRemove()
    {
        Goal thisPreset1 = db.add(removeGoal);
        assertEquals(false,thisPreset1);
        removeGoal = thisPreset1;

        Goal thisPreset2 = db.get(removeGoal.getId());
        assertEquals(true,thisPreset2);

    }
}

