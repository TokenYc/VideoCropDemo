package forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 一句话功能简述
 * 功能详细描述
 *
 * @author 杨晨 on 2017/4/6 14:03
 * @e-mail 247067345@qq.com
 * @see [相关类/方法](可选)
 */

public class TwoSideSeekBar extends View {

    private static final String Tag = "TwoSideSeekBar";
    private static int DEFAULT_HEIGHT = 42;//默认高度，单位dp
    private static int DEFAULT_WIDTH = 300;//默认宽度，单位dp

    private int mHeight = dp2px(getContext(), DEFAULT_HEIGHT);//View的总高度,默认为42dp
    private int mWidth = dp2px(getContext(), DEFAULT_WIDTH);//measure时获取
    private int mMarginSide = dp2px(getContext(), 40);//左右两边距离屏幕边缘距离
    private int mRectStrokeWidth = dp2px(getContext(), 2);//线条粗
    private int mIndicatorStrokeWidth = dp2px(getContext(), 1);//指示器宽度

    private int mCoverColor = Color.parseColor("#77ffffff");//左右两边遮罩颜色
    private int mRectStrokeColor = Color.parseColor("#15bfff");//线条颜色
    private int mIndicatorStrokeColor = Color.parseColor("#ffffff");//指示器颜色

    private int mLeftMarkPosition = mMarginSide;//左边标记的位置，默认位置为mMarginSide

    private int mRightMarkPosition; //measure时获取

    private Paint mPaintRect;//外围矩形绘笔
    private Paint mPaintCover;//遮罩矩形绘笔
    private Paint mPaintIndicator;//指示器绘笔

    private Context mContext;

    public TwoSideSeekBar(Context context) {
        this(context, null);
    }

    public TwoSideSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TwoSideSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContext = getContext();
        initPaint();
    }

    private void initPaint() {
        initPaintRect();
        initPaintCover();
        initPaintIndicator();
    }


    private void initPaintRect() {
        mPaintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintRect.setStrokeWidth(mRectStrokeWidth);
        mPaintRect.setColor(mRectStrokeColor);
        mPaintRect.setStyle(Paint.Style.STROKE);
    }

    private void initPaintCover() {
        mPaintCover = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintCover.setColor(mCoverColor);
        mPaintCover.setStyle(Paint.Style.FILL);
    }

    private void initPaintIndicator() {
        mPaintIndicator = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintIndicator.setColor(mIndicatorStrokeColor);
        mPaintIndicator.setStyle(Paint.Style.STROKE);
        mPaintIndicator.setStrokeWidth(mIndicatorStrokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mLeftMarkPosition+200,0,mLeftMarkPosition+200,mHeight,mPaintIndicator);
        canvas.drawRect(mLeftMarkPosition, mRectStrokeWidth /2,mRightMarkPosition,mHeight- mRectStrokeWidth /2,mPaintRect);
        canvas.drawRect(0,0,mLeftMarkPosition- mRectStrokeWidth /2,mHeight,mPaintCover);
        canvas.drawRect(mRightMarkPosition+ mRectStrokeWidth /2,0,mWidth,mHeight,mPaintCover);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureHeight(int heightMeasureSpec) {
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                mHeight = dp2px(mContext, DEFAULT_HEIGHT);
                break;
            case MeasureSpec.EXACTLY:
                mHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                mHeight = dp2px(mContext, DEFAULT_HEIGHT);
                break;
        }
        return mHeight;
    }

    private int measureWidth(int widthMeasureSpec) {
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                mWidth = dp2px(mContext, DEFAULT_WIDTH);
                Log.d(Tag, "specMode=====>AT_MOST" + "specSize=====>" + specSize);
                break;
            case MeasureSpec.EXACTLY:
                mWidth = specSize;
                Log.d(Tag, "specMode=====>EXACTLY" + "specSize=====>" + specSize);
                break;
            case MeasureSpec.UNSPECIFIED:
                mWidth = dp2px(mContext, DEFAULT_WIDTH);
                Log.d(Tag, "specMode=====>UNSPECIFIED" + "specSize=====>" + specSize);
                break;
        }
        mLeftMarkPosition = mMarginSide;
        mRightMarkPosition = mWidth - mMarginSide;
        return mWidth;
    }


    /**
     * dp转化为px
     *
     * @param context
     * @param dpValue
     * @return
     */
    private int dp2px(Context context, int dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
