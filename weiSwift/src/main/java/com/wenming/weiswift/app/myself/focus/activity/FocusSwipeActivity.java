package com.wenming.weiswift.app.myself.focus.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wenming.weiswift.R;
import com.wenming.weiswift.app.api.StatusesAPI;
import com.wenming.weiswift.app.common.FillContent;
import com.wenming.weiswift.app.common.base.BaseSwipeActivity;
import com.wenming.weiswift.app.common.entity.User;
import com.wenming.weiswift.app.common.oauth.AccessTokenManager;
import com.wenming.weiswift.app.mvp.presenter.FriendActivityPresent;
import com.wenming.weiswift.app.mvp.presenter.imp.FriendActivityPresentImp;
import com.wenming.weiswift.app.mvp.view.FriendActivityView;
import com.wenming.weiswift.app.myself.focus.adapter.FocusAdapter;
import com.wenming.weiswift.widget.endlessrecyclerview.EndlessRecyclerOnScrollListener;
import com.wenming.weiswift.widget.endlessrecyclerview.HeaderAndFooterRecyclerViewAdapter;
import com.wenming.weiswift.widget.endlessrecyclerview.utils.RecyclerViewStateUtils;
import com.wenming.weiswift.widget.endlessrecyclerview.weight.LoadingFooter;

import java.util.ArrayList;

/**
 * Created by wenmingvs on 16/5/1.
 */
public class FocusSwipeActivity extends BaseSwipeActivity implements FriendActivityView {

    public FocusAdapter mAdapter;
    private ArrayList<User> mDatas;
    public Context mContext;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public RecyclerView mRecyclerView;
    public StatusesAPI mStatusesAPI;
    public boolean mRefrshAllData;
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;
    private FriendActivityPresent mFriendActivityPresent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_friends_layout);
        mContext = this;
        mFriendActivityPresent = new FriendActivityPresentImp(this);
        initRefreshLayout();
        initRecyclerView();
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mFriendActivityPresent.pullToRefreshData(Long.valueOf(AccessTokenManager.getInstance().getOAuthToken().getUid()), mContext);
            }
        });
    }

    protected void initRefreshLayout() {
        mRefrshAllData = true;
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.base_swipe_refresh_widget);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mFriendActivityPresent.pullToRefreshData(Long.valueOf(AccessTokenManager.getInstance().getOAuthToken().getUid()), mContext);
            }
        });
    }


    public void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.base_RecyclerView);
        mAdapter = new FocusAdapter(mDatas, mContext) {
            @Override
            public void friendLayoutClick(User user, int position, ImageView friendIcon, TextView friendText) {
                friendIcon.setImageResource(R.drawable.bga_refresh_loading02);
                friendText.setText("");
                if (user.following) {
                    mFriendActivityPresent.user_destroy(user, mContext, friendIcon, friendText);
                } else {
                    mFriendActivityPresent.user_create(user, mContext, friendIcon, friendText);
                }
            }
        };
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);

    }


    public EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {
        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);
            if (mDatas != null && mDatas.size() > 0) {
                showLoadFooterView();
                mFriendActivityPresent.requestMoreData(Long.valueOf(AccessTokenManager.getInstance().getOAuthToken().getUid()), mContext);
            }
        }
    };

    public void onArrorClick(View view) {
        finish();
    }

    @Override
    public void updateListView(ArrayList<User> userlist) {
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        mDatas = userlist;
        mAdapter.setData(userlist);
        mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void showLoadingIcon() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideLoadingIcon() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showLoadFooterView() {
        RecyclerViewStateUtils.setFooterViewState(FocusSwipeActivity.this, mRecyclerView, mDatas.size(), LoadingFooter.State.Loading, null);
    }

    @Override
    public void hideFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.Normal);
    }

    @Override
    public void showEndFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.TheEnd);
    }

    @Override
    public void showErrorFooterView() {
        RecyclerViewStateUtils.setFooterViewState(mRecyclerView, LoadingFooter.State.NetWorkError);
    }


    @Override
    public void updateRealtionShip(Context context, User user, ImageView icon, TextView text) {
        FillContent.updateRealtionShip(context, user, icon, text);
    }


}
