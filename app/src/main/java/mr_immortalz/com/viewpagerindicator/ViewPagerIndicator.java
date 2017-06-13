package mr_immortalz.com.viewpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by asus on 2016/3/22.
 */
public class ViewPagerIndicator extends LinearLayout {
    private ViewPager mViewPager;

    private int width;
    private int height;
    private int visibleItemCount = 3;
    private int itemCount = 3;

    //绘制框框
    private Paint paint;
    private float mWidth = 0;
    private float mHeight = 0;
    private float everageX1start = 0;
    private float everageX1finish = 0;
    private float everageX2start = 0;
    private float everageX2finish = 0;
    private float everageY1start = 0;
    private float everageY1finish = 0;
    private float everageY2start = 0;
    private float everageY2finish = 0;
    private float mLeft = 0;//marginLeft，两个偏移量
    private float mTop = 0;//marginTop，两个偏移量
    private float radiusX = 10;
    private float radiusY = 10;
    private int mPadding = 8;

    private List<String> mDatas;//数据集合
    private boolean isSetData = true;//是否设置数据
    private Context context;
    private int currentPosition;//当前位置
    private boolean isAutoSelect = false;//判断是否进行切换
    private boolean isAutoScroll = false;//判断是否在自行滑动
    private float rebounceOffset;//反弹距离

    public ViewPagerIndicator(Context context) {
        super(context);
        this.context = context;
        init();
    }


    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();

    }

    private void init() {
        LogUtil.m();
        //本view设置背景的填充色，和圆角度，使用drawable资源来定义圆角和背景填充色
        this.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg));

        //设置画笔，绘制中间的白色模块
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);//填充
        paint.setColor(getResources().getColor(R.color.white));//色块为白色
        paint.setAntiAlias(true);//抗锯齿
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //获得测量的宽高
        width = getMeasuredWidth();
        height = getMeasuredHeight();

        //计算单个可视块的宽度
        mWidth = width / visibleItemCount;//整个view的外围宽
        mHeight = height;//整个view外围高

        //画分割线，等分；画两条Y线
        everageX1start = width / 3;
        everageX1finish = width / 3;
        everageY1start = 0;
        everageY1finish = height;

        everageX2start = width * 2 / 3;
        everageX2finish = width * 2 / 3;
        everageY2start = 0;
        everageY2finish = height;

        //输出测量的宽高值
        LogUtil.m("width " + width + "  height " + height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        LogUtil.m();
        super.onSizeChanged(w, h, oldw, oldh);
        //drowText();//在这里是绘制不出来文字的
    }

    /**
     * 绘制文字
     */
    private void drowText() {
        if (isSetData) {
            isSetData = false;
            //清空本view中，所有的子view
            this.removeAllViews();
            //添加TextView
            for (int i = 0; i < mDatas.size(); i++) {
                TextView tv = new TextView(context);
                tv.setPadding(mPadding, mPadding, mPadding, mPadding);
                tv.setText(mDatas.get(i));
                //绘制的textView的区域占据了整个view的区域，其实就是画布层层的覆盖，如果没有计算好谁先绘制，会导致覆盖底下的图
                //其实默认绘制的textView就是背景色就是透明的
                tv.setBackgroundColor(getResources().getColor(R.color.transparent));

                //设置文字的位置,这种位置关系，不需要知道具体的位置距离，直接就是相对布局好在父布局中间即可
                LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                lp.width = width / visibleItemCount;//三分之一宽
                lp.height = height;//和父view一样宽

                tv.setGravity(Gravity.CENTER);
                tv.setTextColor(getResources().getColor(R.color.font_red));
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tv.setLayoutParams(lp);
                final int finalI = i;
                tv.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mViewPager != null) {
                            mViewPager.setCurrentItem(finalI);
                        }
                    }
                });

                //添加到当前的view中
                this.addView(tv);
            }

            //设置标题文字颜色
            setTitleColor();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 实时绘制
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        //LogUtil.m();

        //绘制分割线
        canvas.drawLine(everageX1start, everageY1start, everageX1finish, everageY1finish, paint);
        canvas.drawLine(everageX2start, everageY2start, everageX2finish, everageY2finish, paint);

        //绘制文字
        drowText();

        //绘制白框
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //drawRoundRect需要的最低API是21
            //距离外框，(左，上，右，下）来绘制圆角矩形
            canvas.drawRoundRect(mLeft + mPadding, mTop + mPadding, mLeft + mWidth - mPadding, mTop + mHeight - mPadding, radiusX, radiusY, paint);
        } else {
            canvas.drawRoundRect(new RectF(mLeft + mPadding, mTop + mPadding, mLeft + mWidth - mPadding, mTop + mHeight - mPadding), radiusX, radiusX, paint);
            //canvas.drawRect(mLeft + mPadding, mTop + mPadding, mLeft + mWidth - mPadding, mTop + mHeight - mPadding, paint);
        }


    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        LogUtil.m();
        super.dispatchDraw(canvas);
    }

    //设置和viewpager的联系
    public void setViewPager(ViewPager viewpager, int position) {
        LogUtil.m();
        this.mViewPager = viewpager;
        this.currentPosition = position;

        //如果Viewpager不为空
        if (mViewPager != null) {
            //Viewpager滑动事件监听
            viewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                /**
                 * 页面滚动事件处理
                 * @param position
                 * @param positionOffset
                 * @param positionOffsetPixels
                 */
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    LogUtil.m("positionOffset:" + positionOffset + ",rebounceOffset:" + rebounceOffset);

                    //一，当移动的是最左边item，且是自动滚动
                    //if (isAutoSelect && currentPosition == 0) {
                    if (isAutoScroll && currentPosition == 0) {
                        //1.1,自动滚动时候，偏移距离大于回弹距离的一半；则让最左边（即第一个）item滑动到左边缘位置
                        if (positionOffset > rebounceOffset / 2) {
                            mLeft = (position + (positionOffset - rebounceOffset / 2) * 2) * mWidth;
                        } else
                            //如果自动滚动时，偏移位置小于反弹距离的一半，但大于反弹距离的三分之一时
                            //让最左边（即第一个）item 向右回弹一部分距离
                            if (positionOffset > rebounceOffset / 3 && positionOffset < rebounceOffset / 2) {

                                mLeft = (position + (rebounceOffset / 2) - positionOffset) * mWidth * 6 / 12;
                            } else {
                                //让最左边（即最后一个）item 向左回弹到边缘位置
                                mLeft = (position + positionOffset) * mWidth * 6 / 12;
                            }
                        invalidate();
                    } else
                        //二，当移动的是最右边（即最后一个）item
                        //if (isAutoSelect && currentPosition == itemCount - 1) {
                        if (isAutoScroll && currentPosition == itemCount - 1) {
                            //滑动手松开时，让最右边（即最后一个）item滑动到右边缘位置
                            if (positionOffset >= rebounceOffset && positionOffset < (1 - (1 - rebounceOffset) / 2)) {
                                //
                                mLeft = (position + positionOffset / (1 - (1 - rebounceOffset) / 2)) * mWidth;
                                //当item数大于visibleItem可见数，本控件(本质LinearLayout)才滚动
                                if (visibleItemCount < itemCount) {
                                    scrollTo((int) (mWidth * positionOffset / (1 - (1 - rebounceOffset) / 2) + (position - visibleItemCount + 1) * mWidth), 0);
                                }
                                if ((mLeft + mWidth) > (getChildCount() * mWidth)) {
                                    //当(mLeft + mWidth)大于最边缘的宽度时，设置
                                    mLeft = (itemCount - 1) * mWidth;
                                }
                            } else
                                //让最右边（即最后一个）item 向左回弹一部分距离
                                if (positionOffset > (1 - (1 - rebounceOffset) / 2) && positionOffset < (1 - (1 - rebounceOffset) / 4)) {

                                    //当item数大于visibleItem可见数，且本控件未滚动到指定位置，则设置控件滚动到指定位置
                                    if (visibleItemCount < itemCount && getScrollX() != (itemCount - visibleItemCount) * mWidth) {
                                        scrollTo((int) ((itemCount - visibleItemCount) * mWidth), 0);
                                    }
                                    mLeft = (position + 1) * mWidth - (positionOffset - (1 - (1 - rebounceOffset) / 2)) * mWidth * 7 / 12;
                                } else {
                                    //让最右边（即最后一个）item 向右回弹到边缘位置

                                    //因为onPageScrolled 最后positionOffset会变成0，所以这里需要判断一下
                                    //当positionOffset = 0 时，设置mLeft位置
                                    if (positionOffset != 0) {
                                        mLeft = (position + 1) * mWidth - (1.0f - positionOffset) * mWidth * 7 / 12;
                                        if (mLeft > (itemCount - 1) * mWidth) {
                                            mLeft = (itemCount - 1) * mWidth;
                                        }
                                    } else {
                                        mLeft = (itemCount - 1) * mWidth;
                                    }

                                }
                            invalidate();
                        } else {
                            //当移动的是中间item
                            scrollTo(position, positionOffset);
                            rebounceOffset = positionOffset;
                        }

                    //每次去重绘制（显示）连续运动的滑动距离中间的某些位置状态，的时候，确实需要去再次判定文字显示的颜色
                    setTitleColor();
                }

                /**
                 * 页面选择
                 * @param position
                 */
                @Override
                public void onPageSelected(int position) {
                    LogUtil.m("position " + position);
                    //获得当前页面位置
                    currentPosition = position;
                }

                /**
                 * 页面滚动状态变更
                 * @param state
                 */
                @Override
                public void onPageScrollStateChanged(int state) {
                    LogUtil.m("state " + state);

                    //
                    if (state == 2) {
                        //当state = 2时，表示手松开，viewpager开启自动滑动
                        //isAutoSelect = true;
                        isAutoScroll = true;
                    }
                    if (state == 0) {
                        //当state = 0时，表示viewpager滑动停止
                        //isAutoSelect = false;
                        isAutoScroll = false;
                    }
                }
            });
        }
    }

    /**
     * 和viewpager关联
     *
     * @param viewpager
     */
    public void setViewPager(ViewPager viewpager) {
        LogUtil.m();
        setViewPager(viewpager, 0);
    }

    /**
     * 正常滑动
     *
     * @param position
     * @param positionOffset
     */
    private void scrollTo(int position, float positionOffset) {
        //item数量大于可见item，linearlayout才滑动
        if (visibleItemCount < itemCount) {
            if (positionOffset > 0 && position > (visibleItemCount - 2)) {
                //使得整个view滚动
                this.scrollTo((int) (mWidth * positionOffset + (position - visibleItemCount + 1) * mWidth), 0);
            }
        }
        mLeft = (position + positionOffset) * mWidth;
        invalidate();
    }

    /**
     * 设置字体颜色
     */
    private void setTitleColor() {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                if (i == currentPosition) {
                    ((TextView) getChildAt(currentPosition)).setTextColor(getResources().getColor(R.color.font_red));
                } else {
                    ((TextView) getChildAt(i)).setTextColor(getResources().getColor(R.color.font_white));
                }
            }
        }
    }

    /**
     * 设置内容数据
     *
     * @param mDatas
     */
    public void setDatas(List<String> mDatas) {
        LogUtil.m();
        this.isSetData = true;
        this.mDatas = mDatas;
        this.itemCount = mDatas.size();
        if (itemCount < visibleItemCount) {
            visibleItemCount = itemCount;
        }

    }
}
