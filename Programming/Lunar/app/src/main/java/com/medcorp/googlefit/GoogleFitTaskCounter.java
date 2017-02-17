package com.medcorp.googlefit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karl-john on 22/2/16.
 */
public class GoogleFitTaskCounter {

    private final int totalTasks;
    private int successCount = 0;
    private int finishedCount = 0;

    public GoogleFitTaskCounter(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public boolean areTasksDone(){
        if (totalTasks == finishedCount){
            return true;
        }
        return false;
    }

    public boolean allSucces(){
        if(areTasksDone()){
            if (finishedCount == successCount){
                return true;
            }
        }
        return false;
    }

    public void reset(){
        successCount = 0;
        finishedCount = 0;
    }

    public void incrementSuccessAndFinish(){
        successCount ++;
        finishedCount ++;
    }

    public void incrementFinish(){
        finishedCount++;
    }
}
