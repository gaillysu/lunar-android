package com.medcorp.nevo.database;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by karl-john on 30/11/15.
 */
public class DatabaseTests extends TestSuite {
    public static Test suite(){
        return new TestSuiteBuilder(DatabaseTests.class).includeAllPackagesUnderHere().build();
    }
}