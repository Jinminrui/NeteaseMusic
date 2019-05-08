package com.jmr.nuist.neteasemusic.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmr.nuist.neteasemusic.Fragment.MineFragment;
import com.jmr.nuist.neteasemusic.Fragment.RankFragment;
import com.jmr.nuist.neteasemusic.Fragment.RecommendFragment;
import com.jmr.nuist.neteasemusic.Fragment.SearchFragment;
import com.jmr.nuist.neteasemusic.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RecommendFragment recommendFragment; // 推荐页面
    private RankFragment rankFragment; // 排行榜页面
    private SearchFragment searchFragment; // 搜索页面
    private MineFragment mineFragment; // 我的页面

    private View tabRecommend;
    private View tabRank;
    private View tabSearch;
    private View tabMine;

    private ImageView imageRecommend;
    private TextView textRecommend;

    private ImageView imageRank;
    private TextView textRank;

    private ImageView imageMine;
    private TextView textMine;

    private ImageView imageSearch;
    private TextView textSearch;


    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        fragmentManager = getSupportFragmentManager();
        setTabSelection(0);

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        String userId = settings.getString("userId", "default").toString();
    }

    private void initView() {
        tabRecommend = this.findViewById(R.id.tab_recommend);
        tabRank = this.findViewById(R.id.tab_rank);
        tabSearch = this.findViewById(R.id.tab_search);
        tabMine = this.findViewById(R.id.tab_mine);

        tabRecommend.setOnClickListener(this);
        tabRank.setOnClickListener(this);
        tabSearch.setOnClickListener(this);
        tabMine.setOnClickListener(this);

    }

    private void setTabSelection(int index) {
        imageRecommend = this.findViewById(R.id.recommend_image);
        textRecommend = this.findViewById(R.id.recommend_text);
        imageRank = this.findViewById(R.id.rank_image);
        textRank = this.findViewById(R.id.rank_text);
        imageMine = this.findViewById(R.id.mine_image);
        textMine = this.findViewById(R.id.mine_text);
        imageSearch = this.findViewById(R.id.search_image);
        textSearch = this.findViewById(R.id.search_text);

        // 每次选中之前先清楚掉上次的选中状态
        clearSelection();
        // 开启一个Fragment事务
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // 先隐藏掉所有的Fragment，以防止有多个Fragment显示在界面上的情况
        transaction.setCustomAnimations(
                R.anim.slide_right_in,
                R.anim.slide_left_out,
                R.anim.slide_left_in,
                R.anim.slide_right_out);

        hideFragments(transaction);
        switch (index) {
            case 0:
                imageRecommend.setImageResource(R.drawable.recommend);
                textRecommend.setTextColor(0XFFff4d4f);
                if (recommendFragment == null) {
                    // 如果一个fragment不存在，那就new一个，并添加。
                    recommendFragment = new RecommendFragment();
                    transaction.add(R.id.content, recommendFragment);
                } else {
                    // 存在即显示
                    transaction.show(recommendFragment);
                }
                break;
            case 1:
                imageRank.setImageResource(R.drawable.rank);
                textRank.setTextColor(0XFFff4d4f);
                if (rankFragment == null) {
                    rankFragment = new RankFragment();
                    transaction.add(R.id.content, rankFragment);
                } else {
                    transaction.show(rankFragment);
                }
                break;
            case 2:

                imageMine.setImageResource(R.drawable.mine);

                textMine.setTextColor(0XFFff4d4f);
                if (mineFragment == null) {
                    mineFragment = new MineFragment();
                    transaction.add(R.id.content, mineFragment);
                } else {
                    transaction.show(mineFragment);
                }
                break;
            case 3:

                imageSearch.setImageResource(R.drawable.search);
                textSearch.setTextColor(0XFFff4d4f);
                if (searchFragment == null) {
                    searchFragment = new SearchFragment();
                    transaction.add(R.id.content, searchFragment);
                } else {
                    transaction.show(searchFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    /**
     * 将所有的fragment都隐藏
     *
     * @param transaction
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (recommendFragment != null) {
            transaction.hide(recommendFragment);
        }
        if (rankFragment != null) {
            transaction.hide(rankFragment);
        }
        if (searchFragment != null) {
            transaction.hide(searchFragment);
        }
        if (mineFragment != null) {
            transaction.hide(mineFragment);
        }
    }

    /**
     * 清除选中效果
     */
    private void clearSelection() {
        imageRecommend.setImageResource(R.drawable.recommend_org);
        imageRank.setImageResource(R.drawable.rank_org);
        imageMine.setImageResource(R.drawable.mine_org);
        imageSearch.setImageResource(R.drawable.search_org);

        textRecommend.setTextColor(0xff8a8a8a);
        textRank.setTextColor(0xff8a8a8a);
        textMine.setTextColor(0xff8a8a8a);
        textSearch.setTextColor(0xff8a8a8a);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_recommend:
                setTabSelection(0);
                break;
            case R.id.tab_rank:
                setTabSelection(1);
                break;
            case R.id.tab_mine:
                setTabSelection(2);
                break;
            case R.id.tab_search:
                setTabSelection(3);
                break;
            default:
                break;
        }
    }
}
