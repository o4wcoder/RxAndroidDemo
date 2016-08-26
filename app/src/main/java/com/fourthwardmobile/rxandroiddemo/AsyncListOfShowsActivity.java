package com.fourthwardmobile.rxandroiddemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AsyncListOfShowsActivity extends AppCompatActivity {

    private static final String TAG = AsyncListOfShowsActivity.class.getSimpleName();

    private Subscription mTvShowSubscription;
    private RecyclerView mTvShowListView;
    private ProgressBar mProgressBar;
    private SimpleStringAdapter mSimpleStringAdapter;
    private RestClient mRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_list_of_shows);

        mProgressBar = (ProgressBar) findViewById(R.id.loader);
        mTvShowListView = (RecyclerView) findViewById(R.id.tv_show_list);
        mTvShowListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mTvShowListView.setAdapter(mSimpleStringAdapter);

        mRestClient = new RestClient(this);
        createObservable();
    }

    private void createObservable() {

        //Need to use Observable.fromCallable since this is a blocking call (over the network. Or
        // at least simulated over the network)
        Observable<List<String>> tvShowObservable = Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {

                //getFavoriteTvShows() will be called on a different thread as set by
                //Schedulers.io below
                return mRestClient.getFavoriteTvShows();
            }
        });

        /**
         * 1. Subscribe to observables on spearate thread defined by subscribeOn(Schedulers.io)
         * 2. We want our onNext() method to be called on main UI thread since it sets views
         *    so we set observeOn(AndroidSchedulers.mainThread()
         * 3. Call subscribe to actual execute the Observable tvShwObservable
         */

        mTvShowSubscription = tvShowObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                        Log.e(TAG,"onCompleted()");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"onError()");
                    }

                    @Override
                    public void onNext(List<String> tvShows) {
                        Log.e(TAG,"onNext()");
                         displayTvShows(tvShows);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Sever the connection between the Observer and Observable. Unsubscribe the Subscription
        // when the Activity is destroyed
        if (mTvShowSubscription != null && !mTvShowSubscription.isUnsubscribed()) {
            mTvShowSubscription.unsubscribe();
        }
    }

    private void displayTvShows(List<String> tvShows) {
        mSimpleStringAdapter.setStrings(tvShows);
        mProgressBar.setVisibility(View.GONE);
        mTvShowListView.setVisibility(View.VISIBLE);
    }
}
