package forum.jiangyouluntan.com.videocropdemo.listVideo.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 一句话功能简述 圆形加载框
 * 功能详细描述
 *
 * @author 杨晨 on 2016/8/9 16:39
 * @e-mail 247067345@qq.com
 * @see [相关类/方法](可选)
 */
public class CircleProgressView extends View{
    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mProgress =1;

    private Paint mPaint;
    public CircleProgressView(Context context) {
        this(context,null);
    }

    public CircleProgressView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.mWidth=getWidth();
        this.mHeight=getHeight();
        this.mRadius=mWidth>mHeight?(int)(mHeight*1.0f/2):(int)(mWidth*1.0f/2);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(mWidth*1.0f/2,mHeight*1.0f/2,mRadius,mPaint);
        mPaint.setColor(Color.parseColor("#88D1D1D1"));
        mPaint.setStyle(Paint.Style.FILL);
        RectF oval2 = new RectF(5, 5, mWidth-5, mHeight-5);// 设置个新的长方形，扫描测量
        canvas.drawArc(oval2, 0, this.mProgress, true, mPaint);
    }

    public void setProgress(float percent){
        this.mProgress =(int)(360*1.0f*percent);
        postInvalidate();
    }
}
