package com.medcorp.lunar.util;


import com.medcorp.lunar.model.Steps;

import java.util.Comparator;

/**
 * Created by gaillysu on 15/12/16.
 */
public class StepsSorter implements Comparator<Steps> {
    @Override
    public int compare(Steps lhs, Steps rhs) {
        return lhs.compareTo(rhs);
    }
}
