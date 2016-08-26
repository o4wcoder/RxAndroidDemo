package com.fourthwardmobile.rxandroiddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;

public class ListOfStringsActivity extends AppCompatActivity {

    RecyclerView mColorListView;
    SimpleStringAdapter mSimpleStringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_strings);

        mColorListView = (RecyclerView) findViewById(R.id.color_list);
        mColorListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mColorListView.setAdapter(mSimpleStringAdapter);

        createObservable();
    }

    /**
     * We’re going to make an Observable that emits a single value, a list of strings, and then
     * completes. We’ll then use the emitted value to populate the list. We’ll do this by using the
     * Observable.just() method. This method creates an Observable such that when an Observer
     * subscribes, the onNext() of the Observer is immediately called with the argument provided
     * to Observable.just(). The onComplete() will then be called since the Observable has no
     * other values to emit.
     */

    private void createObservable() {
        //Can use Observable.just because this is not a blocking call (not over network)
        Observable<List<String>> listObservable = Observable.just(getColorList());

        /**
         *  Subscribe to the observables and setup callbacks
         *  1. The onNext() method is called and the emitted list of colors is set as the data
         *  for the adapter.
         *
         *  2. Since there is no more data (we only gave our Observable a single value to emit in
         *  Observable.just()), the onComplete() callback is called.
         */

        listObservable.subscribe(new Observer<List<String>>() {
            @Override
            public void onCompleted() {
               //Don't care about what happens when the Observable has completed
            }

            @Override
            public void onError(Throwable e) {
              //In this case, no error is going to be thrown
            }

            @Override
            public void onNext(List<String> colors) {
               mSimpleStringAdapter.setStrings(colors);
            }
        });
    }


    private static List<String> getColorList() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("blue");
        colors.add("green");
        colors.add("red");
        colors.add("orange");
        colors.add("purple");

        return colors;
    }
}
