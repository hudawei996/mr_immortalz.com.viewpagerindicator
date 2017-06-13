package mr_immortalz.com.viewpagerindicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager; //滑动页面
    private ViewPagerIndicator indicator; //指示器
    private FragmentPagerAdapter mAdapter; //fragmentpager适配器
    private List<Fragment> mList; //fragment集合
    private List<String> mDatas; //String集合
    private int itemCount = 3;//指示器标志的数量

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.vp);
        indicator = (ViewPagerIndicator) findViewById(R.id.indicator);

        //根据item数量，确定fragment集合数量
        mList = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            Fragment fragment = new MeFragment();
            mList.add(fragment);
        }

        //根据item数量确定String集合数量，内容
        mDatas = new ArrayList<>();
        for (int i = 0; i < itemCount; i++) {
            mDatas.add("i=" + i);
        }

        //初始化fragment+Viewpager适配器
        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mList.get(position);
            }

            @Override
            public int getCount() {
                return mList.size();
            }
        };

        //viewpager设置适配器
        viewPager.setAdapter(mAdapter);

        //将viewpager与indicator绑定
        indicator.setDatas(mDatas);
        indicator.setViewPager(viewPager);
    }
}
