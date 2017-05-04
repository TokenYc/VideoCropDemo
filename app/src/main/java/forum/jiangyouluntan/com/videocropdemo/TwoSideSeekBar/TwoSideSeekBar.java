package forum.jiangyouluntan.com.videocropdemo.TwoSideSeekBar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import forum.jiangyouluntan.com.videocropdemo.R;

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
    private static final int DEFAULT_HEIGHT = 42;//默认高度，单位dp
    private static final int DEFAULT_WIDTH = 300;//默认宽度，单位dp
    private static final int DEFAULT_DURATION = 10;//默认最长裁剪时间长度
    private static final int DEFAULT_POSITION = -1;//未设置时为止，用来判断是否初始化

    private int mHeight = dp2px(getContext(), DEFAULT_HEIGHT);//View的总高度,默认为42dp
    private int mWidth = dp2px(getContext(), DEFAULT_WIDTH);//measure时获取
    private int mMarginSide = dp2px(getContext(), 30);//左右两边距离屏幕边缘距离
    private int mRectStrokeWidth = dp2px(getContext(), 2);//线条粗
    private int mIndicatorStrokeWidth = dp2px(getContext(), 1);//指示器宽度
    private int mArrowWidth = dp2px(getContext(), 14);//箭头图标宽度

    private int mCoverColor = Color.parseColor("#77ffffff");//左右两边遮罩颜色
    private int mRectStrokeColor = Color.parseColor("#15bfff");//线条颜色
    private int mOutLineRectStrokeColor = Color.parseColor("#77ffffff");
    private int mIndicatorStrokeColor = Color.parseColor("#ffffff");//指示器颜色

    private int mLeftMarkPosition = DEFAULT_POSITION;//左边标记的位置，默认位置为mMarginSide

    private int mRightMarkPosition = DEFAULT_POSITION; //measure时获取

    private Paint mPaintRect;//外围矩形绘笔
    private Paint mPaintCover;//遮罩矩形绘笔
    private Paint mPaintIndicator;//指示器绘笔
    private Paint mPaintOutLine;//外部的轮廓


    private OnVideoStateChangeListener mVideoStateChangeListener;
    private ObjectAnimator mIndicatorAnimator;
    private Context mContext;

    private Bitmap mBitmapLeftArrow;
    private Bitmap mBitmapRightArrow;

    private boolean mIsMoving;

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
        mBitmapLeftArrow = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_arrow_left);
        mBitmapRightArrow = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_arrow_right);
        initPaint();
    }

    private void initPaint() {
        initPaintRect();
        initPaintCover();
        initPaintIndicator();
        initPaintOutLine();
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

    private void initPaintOutLine() {
        mPaintOutLine = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintOutLine.setStrokeWidth(mRectStrokeWidth);
        mPaintOutLine.setColor(mOutLineRectStrokeColor);
        mPaintOutLine.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIndicatorAnimator == null) {
            resetIndicatorAnimator();
        }
        if (mIsMoving) {
            canvas.drawRect(mMarginSide, mRectStrokeWidth / 2, mWidth - mMarginSide, mHeight - mRectStrokeWidth / 2, mPaintOutLine);
        }
        drawIndicator(canvas);
        canvas.drawRect(mLeftMarkPosition, mRectStrokeWidth / 2, mRightMarkPosition, mHeight - mRectStrokeWidth / 2, mPaintRect);
        canvas.drawRect(0, 0, mLeftMarkPosition - mRectStrokeWidth / 2, mHeight, mPaintCover);
        canvas.drawRect(mRightMarkPosition + mRectStrokeWidth / 2, 0, mWidth, mHeight, mPaintCover);
        drawArrow(canvas, mBitmapLeftArrow, mLeftMarkPosition);
        drawArrow(canvas, mBitmapRightArrow, mRightMarkPosition);
    }

    private void drawArrow(Canvas canvas, Bitmap bitmap, int x) {
        Rect src = new Rect();
        src.left = 0;
        src.top = 0;
        src.right = bitmap.getWidth();
        src.bottom = bitmap.getHeight();

        Rect des = new Rect();
        des.left = x - mArrowWidth / 2;
        des.top = 0;
        des.right = x + mArrowWidth / 2;
        des.bottom = mHeight;
        canvas.drawBitmap(bitmap, src, des, null);
    }

    private float value = 0;

    private void drawIndicator(final Canvas canvas) {
        if (mIndicatorAnimator != null) {
            canvas.drawLine(value, 0, value, mHeight, mPaintIndicator);
        }
        invalidate();
    }

    public void resetIndicatorAnimator() {
        cancelIndicatorAnimator();
        mIndicatorAnimator = ObjectAnimator
                .ofFloat(mContext, "yc", mLeftMarkPosition, mRightMarkPosition)
                .setDuration(getCropTime());
        mIndicatorAnimator.removeAllListeners();
        mIndicatorAnimator.setInterpolator(new LinearInterpolator());
        mIndicatorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndicatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                value = (Float) animation.getAnimatedValue();
            }
        });
        mIndicatorAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                Log.d(Tag, "onAnimationStart=====>");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Log.d(Tag, "onAnimationEnd=====>");
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.d(Tag, "onAnimationRepeat=====>");
                if (mVideoStateChangeListener!=null){
                    mVideoStateChangeListener.onEnd();
                }
            }
        });
        if (mIndicatorAnimator != null) {
            mIndicatorAnimator.start();
        }
    }

    public void cancelIndicatorAnimator() {
        if (mIndicatorAnimator != null && mIndicatorAnimator.isRunning()) {
            mIndicatorAnimator.removeAllUpdateListeners();
            mIndicatorAnimator.removeAllListeners();
            mIndicatorAnimator.cancel();
        }
    }


    private boolean leftMarkOnTouch = false;
    private boolean rightMarkOnTouch = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                leftMarkOnTouch = isInLeftMarkArea(event.getX());
                rightMarkOnTouch = isInRightMarkArea(event.getX());
                if (isMarkOnTouch()) {
                    mIsMoving = true;
                    cancelIndicatorAnimator();
                    if (mVideoStateChangeListener != null) {
                        mVideoStateChangeListener.onPause();
                    }
                }
                return isMarkOnTouch();
            case MotionEvent.ACTION_MOVE:
                if (mRightMarkPosition - mLeftMarkPosition >= ((mWidth - 2 * mMarginSide) / 10)) {
                    if (leftMarkOnTouch && event.getX() >= mMarginSide && (mRightMarkPosition - event.getX() >= ((mWidth - 2 * mMarginSide) / 10))) {
                        mLeftMarkPosition = (int) event.getX();
                    } else if (rightMarkOnTouch && event.getX() <= (mWidth - mMarginSide) && (event.getX() - mLeftMarkPosition >= ((mWidth - 2 * mMarginSide) / 10))) {
                        mRightMarkPosition = (int) event.getX();
                    }
                }
                invalidate();
                return isMarkOnTouch();
            case MotionEvent.ACTION_UP:
                if (isMarkOnTouch()) {
                    mIsMoving = false;
                    if (mVideoStateChangeListener != null) {
                        mVideoStateChangeListener.onStart(mLeftMarkPosition, 0);
                    }
                    resetIndicatorAnimator();
                }
        }
        return super.onTouchEvent(event);

    }

    private boolean isMarkOnTouch() {
        return leftMarkOnTouch || rightMarkOnTouch;
    }

    private boolean isInLeftMarkArea(float x) {
        if (x > (mLeftMarkPosition - mArrowWidth / 2) && x < (mLeftMarkPosition + mArrowWidth / 2)) {
            Log.d(Tag, "is in markArea");
            return true;
        } else {
            return false;
        }
    }

    private boolean isInRightMarkArea(float x) {
        if (x > (mRightMarkPosition - mArrowWidth / 2) && x < (mRightMarkPosition + mArrowWidth / 2)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(Tag, "调用了onMeasure");

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
        //// TODO: 2017/4/11 这里在滑动快的时候就调用了
        mMarginSide = mWidth / 12;
        Log.d(Tag, "mMarginSide=====>" + mMarginSide);
        if (mLeftMarkPosition == DEFAULT_POSITION) {
            mLeftMarkPosition = mMarginSide;
        }
        if (mRightMarkPosition == DEFAULT_POSITION) {
            mRightMarkPosition = mWidth - mMarginSide;
        }
        return mWidth;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    public int getSingleWidth() {
        return mMarginSide;
    }

    public int getSingleHeight() {
        return mHeight;
    }

    /**
     * 获取当前裁剪出的长度
     *
     * @return
     */
    private int getCropLength() {
        return mRightMarkPosition - mLeftMarkPosition;
    }

    /**
     * 获取原始裁剪的长度
     *
     * @return
     */
    private int getOriginCropLength() {
        return mWidth - 2 * mMarginSide;
    }

    /**
     * 获取当前裁剪的时间
     *
     * @return
     */
    public long getCropTime() {
        Log.d(Tag, "cropTime=====>" + DEFAULT_DURATION * 1000 * (getCropLength() / getOriginCropLength()));
        return (long) (DEFAULT_DURATION * 1000 * ((float) getCropLength() / getOriginCropLength()));
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

    public interface OnVideoStateChangeListener {

        void onStart(float x, float y);

        void onPause();

        void onEnd();

    }

    public void setOnVideoStateChangeListener(OnVideoStateChangeListener listener) {
        this.mVideoStateChangeListener = listener;
    }

    public void release(){
        mIndicatorAnimator.removeAllListeners();
        mIndicatorAnimator.removeAllUpdateListeners();
        mIndicatorAnimator.cancel();
    }

}
