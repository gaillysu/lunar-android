package com.medcorp.database;

import android.test.AndroidTestCase;

import com.medcorp.database.entry.GoalDatabaseHelper;
import com.medcorp.model.Goal;

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
        db = new GoalDatabaseHelper(getContext());
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
        Optional<Goal> thisPreset1 = db.add(addGoal);
        assertEquals(false,thisPreset1.isEmpty());
        addGoal = thisPreset1.get();

        Optional<Goal> thisPreset2 = db.get(addGoal.getId()).get(0);
        assertEquals(false,thisPreset2.isEmpty());

        assertEquals(addGoal.getLabel(),thisPreset2.get().getLabel());
        assertEquals(addGoal.getSteps(),thisPreset2.get().getSteps());
        assertEquals(addGoal.isStatus(),thisPreset2.get().isStatus());

    }
    public void testUpdate()
    {
        Optional<Goal> thisPreset1 = db.add(updateGoal);
        assertEquals(false,thisPreset1.isEmpty());
        updateGoal = thisPreset1.get();

        updateGoal.setStatus(!updateGoal.isStatus());
        updateGoal.setLabel("34terwfgw");
        updateGoal.setSteps((int) (Math.random() * 10000));

        assertEquals(true, db.update(updateGoal));

        Optional<Goal> thisPreset2 = db.get(updateGoal.getId()).get(0);
        assertEquals(false,thisPreset2.isEmpty());

        assertEquals(thisPreset2.get().isStatus(), updateGoal.isStatus());
        assertEquals(thisPreset2.get().getLabel(), updateGoal.getLabel());
        assertEquals(thisPreset2.get().getSteps(), updateGoal.getSteps());

    }
    public void testRemove()
    {
        Optional<Goal> thisPreset1 = db.add(removeGoal);
        assertEquals(false,thisPreset1.isEmpty());
        removeGoal = thisPreset1.get();

        assertEquals(true,db.remove(removeGoal.getId()));

        Optional<Goal> thisPreset2 = db.get(removeGoal.getId()).get(0);
        assertEquals(true,thisPreset2.isEmpty());

    }
}

