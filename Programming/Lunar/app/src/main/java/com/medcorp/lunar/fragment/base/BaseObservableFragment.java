package com.medcorp.lunar.fragment.base;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;

import com.medcorp.lunar.fragment.observer.FragmentObservable;


/**
 * Created by karl-john on 16/12/15.
 */
public abstract class BaseObservableFragment extends  BaseFragment implements FragmentObservable {

    public static BaseObservableFragment instantiate(Context context, String tag)
    {
        return (BaseObservableFragment) instantiate(context,tag,null);
    }

    public AppCompatActivity getAppCompatActivity(){
        return (AppCompatActivity) getActivity();
    }

    public void startActivity(Class <?> cls) {
        Intent intent = new Intent(getActivity(), cls);
        if(android.os.Build.VERSION.SDK_INT >= 21) {
            getActivity().getWindow().setExitTransition(new Explode());
        }
        getActivity().startActivity(intent);
    }
}