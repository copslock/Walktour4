package com.walktour.framework.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.dinglicom.dataset.model.ENDCDataModel;
import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.ShowInfo;
import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UnifyParaID;
import com.walktour.Utils.WalkStruct.CurrentNetState;
import com.walktour.base.util.LogUtil;
import com.walktour.model.YwDataModel;

import java.text.DecimalFormat;


/**
 * 仪表盘视图
 *
 * @author jinfeng.xie
 */
@SuppressWarnings("deprecation")
public class Dashboardfor5GView extends View {
    private static final String TAG = "Dashboardfor5GView";
    private final Context context;
    private int mRadius; // 扇形半径
    private int mStartAngle = 135; // 起始角度
    private int mSweepAngle = 270; // 绘制角度
    private int mMin = 0; // 最小值
    private int mMax = 300; // 最大值
    private String mUnitText = "M"; // 单位
    private int mSection = 10; // 值域（mMax-mMin）等分份数
    private int mPortion = 4; // 一个mSection等分份数

    private boolean isShowValue = true; // 是否显示实时读数
    private int mStrokeWidth; // 画笔宽度
    private int mLength1; // 长刻度的相对圆弧的长度
    private int mLength2; // 刻度读数顶部的相对圆弧的长度
    private int mPLRadius; // 指针长半径
    private int mPSRadius; // 指针短半径
    private int mHintCircleRadius;//里面的圆形背景

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private RectF mRectFArc;
    private Path mPath;
    private RectF mRectFInnerArc;
    private Rect mRectText;
    private String[] mTexts;

    protected String progresstext = "";

    private String[] pScale = {"250K", "5M", "10M", "50M", "300M", "2000M", "30K", "500K"};

    private String[] endcScale = {"300M", "500M", "1000M", "2000M"};

    private String maxScale = "0K";

    private CurrentNetState netType = CurrentNetState.ENDC;
    /**
     * 并发时根据name 获取值
     */
    private String name = "";
    private int progress;
    private float mLteValue = 0; // LTE实时读数(默认是kbps)
    private float mNRValue = 0;   //NR实时度数(默认是kbps)
    private RectF mRectLTE;  //LTE 的圆弧
    private RectF mRectNR;  //NR 的圆弧
    private RectF mRectNetType;  //ENDC 的圆弧
    private float arcWidth = dp2px(10);
    private DecimalFormat df = new DecimalFormat("#,###.00");

    public Dashboardfor5GView(Context context)
    {
        this(context, null);
    }

    public Dashboardfor5GView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void init()
    {
        maxScale = pScale[0];
        changeYiBiaoRes();
        getTextValus();
        mStrokeWidth = dp2px(1);
        mLength1 = dp2px(8) + mStrokeWidth;
        mLength2 = mLength1 + dp2px(12);
        mPSRadius = dp2px(10);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        mRectFArc = new RectF();
        mPath = new Path();
        mRectFInnerArc = new RectF();
        mRectText = new Rect();

        mRectLTE = new RectF();
        mRectNR = new RectF();
        mRectNetType = new RectF();
    }

    public void getTextValus()
    {
        mMax = Integer.parseInt(maxScale.substring(0, maxScale.length() - 1));
        mUnitText = maxScale.substring(maxScale.length() - 1);
        mTexts = new String[mSection / 2 + 1]; // 需要显示mSection + 1个刻度读数
        for (int i = 0; i < mTexts.length; i++) {
            mTexts[i] = String.valueOf(mMin + i * (mMax / (mSection / 2))) + mUnitText;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);

        int width = resolveSize(dp2px(250), widthMeasureSpec);
        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;

        mPaint.setTextSize(sp2px(16));
        if (isShowValue) { // 显示实时读数，View高度增加字体高度3倍
            mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        } else {
            mPaint.getTextBounds("0", 0, 0, mRectText);
        }
        // 由半径+指针短半径+实时读数文字高度确定的高度
        int height1 = mRadius + mStrokeWidth * 2 + mPSRadius + mRectText.height() * 3;
        // 由起始角度确定的高度
        float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
        // 由结束角度确定的高度
        float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
        // 取最大值
        int max = (int) Math.max(
                height1,
                Math.max(point1[1] + mRadius + mStrokeWidth * 2, point2[1] + mRadius + mStrokeWidth * 2)
        );
        setMeasuredDimension(width, mRadius * 2);

        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRectFArc.set(
                getPaddingLeft() + mStrokeWidth,
                getPaddingTop() + mStrokeWidth,
                getMeasuredWidth() - getPaddingRight() - mStrokeWidth,
                getMeasuredWidth() - getPaddingBottom() - mStrokeWidth
        );

        mRectNR.set(
                mCenterX - mRadius * 2 / 3 + arcWidth / 2,
                mCenterY - mRadius * 2 / 3 + arcWidth / 2,
                mCenterX + mRadius * 2 / 3 - arcWidth / 2,
                mCenterY + mRadius * 2 / 3 - arcWidth / 2
        );
        mRectLTE.set(
                mCenterX - mRadius * 2 / 3 + arcWidth * 3 / 2,
                mCenterY - mRadius * 2 / 3 + arcWidth * 3 / 2,
                mCenterX + mRadius * 2 / 3 - arcWidth * 3 / 2,
                mCenterY + mRadius * 2 / 3 - arcWidth * 3 / 2
        );
        mRectNetType.set(
                mCenterX - dp2px(30),
                mCenterY - dp2px(50),
                mCenterX + dp2px(30),
                mCenterY - dp2px(20)
        );

        mPaint.setTextSize(sp2px(10));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        mRectFInnerArc.set(
                getPaddingLeft() + mLength2 + mRectText.height(),
                getPaddingTop() + mLength2 + mRectText.height(),
                getMeasuredWidth() - getPaddingRight() - mLength2 - mRectText.height(),
                getMeasuredWidth() - getPaddingBottom() - mLength2 - mRectText.height()
        );

        mPLRadius = mRadius - dp2px(5);
        mHintCircleRadius = mRadius - mLength1 - dp2px(8);

    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        /**
         * 画圆形背景
         */
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#1A82D1"));
        canvas.drawCircle(mCenterX, mCenterY, mHintCircleRadius, mPaint);

        /**
         * 画圆弧
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(1));
        mPaint.setColor(Color.WHITE);


        /**
         * 画长刻度
         * 画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度
         */
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float x0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - cos));
        float y0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - sin));
        float x1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * cos);
        float y1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * sin);

        mPaint.setStrokeWidth(dp2px(8));
        canvas.save();
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i < mSection; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
        canvas.restore();

        /**
         * 画短刻度
         * 同样采用canvas的旋转原理
         */
        canvas.save();
        mPaint.setStrokeWidth(dp2px(2));
        float x2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * cos);
        float y2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * sin);
        canvas.drawLine(x0, y0, x2, y2, mPaint);
        angle = mSweepAngle * 1f / (mSection * mPortion);
        for (int i = 1; i < mSection * mPortion; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            if (i % mPortion == 0) { // 避免与长刻度画重合
                continue;
            }
            canvas.drawLine(x0, y0, x2, y2, mPaint);
        }
        canvas.restore();

        /**
         * 画长刻度读数
         * 添加一个圆弧path，文字沿着path绘制
         */
        mPaint.setTextSize(sp2px(13));
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < mTexts.length; i++) {
            mPaint.getTextBounds(mTexts[i], 0, mTexts[i].length(), mRectText);
            // 粗略把文字的宽度视为圆心角2*θ对应的弧长，利用弧长公式得到θ，下面用于修正角度
            float θ = (float) (180 * mRectText.width() / 2 /
                    (Math.PI * (mRadius - mLength2 - mRectText.height())));
            mPath.reset();
            mPath.addArc(
                    mRectFInnerArc,
                    mStartAngle + i * (mSweepAngle / mSection * 2) - θ, // 正起始角度减去θ使文字居中对准长刻度
                    mSweepAngle
            );
            canvas.drawTextOnPath(mTexts[i], mPath, 0, 0, mPaint);
        }

        /**
         * 画指针围绕的镂空圆心
         */
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(mCenterX, mCenterY, dp2px(12), mPaint);
        /**
         * 画内圆阴影
         */
        mPaint.setColor(Color.parseColor("#55ffffff"));
        canvas.drawCircle(mCenterX, mCenterY, mRadius * 2 / 3 + dp2px(1), mPaint);
        /**
         * 画内圆
         */
        mPaint.setColor(Color.WHITE);
        canvas.drawCircle(mCenterX, mCenterY, mRadius * 2 / 3, mPaint);
        /**
         * 画百分比
         */
        progresstext = progress + "%";
        mPaint.getTextBounds(progresstext, 0, progresstext.length(), mRectText);
        canvas.drawText(progresstext, mCenterX - mRectText.right / 2, mRadius + (mRadius * 8 / 5) / 2, mPaint);
        /**
         * 画实时度数值
         */
        if (isShowValue) {
            String value = getValue(mLteValue + mNRValue);
            mPaint.setColor(Color.parseColor("#0878CE"));
            mPaint.setTextSize(sp2px(28));
            mPaint.setFakeBoldText(true);
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.getTextBounds(value, 0, value.length(), mRectText);
            canvas.drawText(value, mCenterX,
                    mCenterY + dp2px(20), mPaint);
            mPaint.setTextSize(sp2px(16));
            if (mUnitText.equals("M")) {
                canvas.drawText("Mbps", mCenterX,
                        mCenterY + dp2px(40), mPaint);// Mbps
            } else {
                canvas.drawText("kbps", mCenterX,
                        mCenterY + dp2px(40), mPaint);// kbps
            }

        }
        /*画圆弧指针*/
        mPaint.setAntiAlias(true);//取消锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(arcWidth);

        float θAll = 0;
        float θNR = 0;
        float θLTE = 0;
        if ("K".equals(mUnitText.toUpperCase())) {
            θAll = (int) ((mSweepAngle) * (mLteValue + mNRValue - mMin) / (mMax - mMin)); // 指针与水平线夹角
            θNR = (int) ((mSweepAngle) * (mNRValue - mMin) / (mMax - mMin)); // 指针与水平线夹角
            θLTE = (int) ((mSweepAngle) * (mLteValue - mMin) / (mMax - mMin)); // 指针与水平线夹角
        } else {
            θAll = (int) ((mSweepAngle) * (mLteValue / 1024 + mNRValue / 1024 - mMin) / (mMax - mMin)); // 指针与水平线夹角
            θNR = (int) ((mSweepAngle) * (mNRValue / 1024 - mMin) / (mMax - mMin)); // 指针与水平线夹角
            θLTE = (int) ((mSweepAngle) * (mLteValue / 1024 - mMin) / (mMax - mMin)); // 指针与水平线夹角
        }

        mPaint.setColor(Color.parseColor("#00CC00"));
        canvas.drawArc(mRectNR, mStartAngle, θAll, false, mPaint);
        mPaint.setColor(Color.parseColor("#CC33CC"));
        canvas.drawArc(mRectNR, mStartAngle, θNR, false, mPaint);
        mPaint.setColor(Color.parseColor("#FFC993"));
        canvas.drawArc(mRectLTE, mStartAngle, θLTE, false, mPaint);

        /*画圆角矩形*/
        mPaint.setColor(Color.parseColor("#00CC00"));
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mRectNetType, dp2px(8), dp2px(8), mPaint);
        /*画网络类型*/
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(sp2px(16));
        mPaint.setFakeBoldText(true);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(netType.name(), mRectNetType.centerX(),
                mRectNetType.centerY() + dp2px(7), mPaint);

        System.out.println("KKMMLL=="+TraceInfoInterface.getParaValue(UnifyParaID.DATA_HTTP_PAGE_RATE));
        System.out.println("KKMMLL=="+TraceInfoInterface.getParaValue(UnifyParaID.DATA_FTP_DOWNLOAD_RATE));

    }

    public String getValue(float value)
    {

        String dd;
        if (mUnitText.equals("M")) {
            dd = df.format(value / 1024);// Mbps
        } else {
            dd = df.format(value);// kbps
        }
        if (dd.equals(".00") || dd.equals("0.00")) {
            dd = "-";
        } else if (dd.startsWith(".")) {
            dd = "0" + dd;
        }
        return dd;

    }

    private void getData()
    {
        /*非数据业务*/
        if (ApplicationModel.getInstance().getCurrentTask().getDataType() == 0 || ApplicationModel.getInstance().getCurrentTask().getDataType() == 1
                || ApplicationModel.getInstance().getCurrentTask().getDataType() == 4) {
            mLteValue = 0;
            mNRValue = 0;
            progress = 0;
            return;
        }

        /*TODO 获取数据并显示*/
        netType = TraceInfoInterface.currentNetType;
        LogUtil.d(TAG, "当前网络类型:netType" + netType);
        boolean is5G = (netType == CurrentNetState.ENDC);
        YwDataModel ywDataModel = ShowInfo.getInstance().getYwDataModel(name);
        progress = ywDataModel.getBordProgress() > 100 ? 100 : ywDataModel.getBordProgress();
        progress = ywDataModel.getBordProgress() < 0 ? 0 : ywDataModel.getBordProgress();
        if (is5G) {
            ENDCDataModel endcDataModel = ShowInfo.getInstance().getEndcDataModel();
            if (ShowInfo.getInstance().getType() == ShowInfo.DIRECT_TYPE_DOWN) {
                mLteValue = endcDataModel.getL_Thr_DL_PDCP_Thr() / 1024;//    LTE速度，单位是bps         1kbps=1024bps;
                mNRValue = endcDataModel.getNR_Thr_DL_PDCP_Thr() / 1024;//    NR速度，单位是bps           1kbps=1024bps;
            } else if (ShowInfo.getInstance().getType() == ShowInfo.DIRECT_TYPE_UP) {
                mLteValue = endcDataModel.getL_Thr_UL_PDCP_Thr() / 1024;//    LTE速度，单位是bps       1kbps=1024bps;
                mNRValue = endcDataModel.getNR_Thr_UL_PDCP_Thr() / 1024;//    NR速度，单位是bps          1kbps=1024bps;
            }
        } else {
            mLteValue = 0;
            mNRValue = ywDataModel.getBordCurrentSpeed();
        }
//        mNRValue+=1000;
//        mLteValue+=1000;
        LogUtil.d(TAG, "mNRValue:" + mNRValue);
    }


    private int dp2px(int dp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public float[] getCoordinatePoint(int radius, float angle)
    {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    @Override
    protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        this.destroyDrawingCache();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus)
    {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    /**
     * 根据当前网络该表仪表盘资源文件 private String[] pScale = {"250K", "10M", "50M",
     * "300M","5M"};
     */
    private void changeYiBiaoRes()
    {
        netType = TraceInfoInterface.currentNetType;
        switch (netType) {
            case CDMA:
                maxScale = pScale[1];
                break;
            case TDSCDMA:
                maxScale = pScale[2];
                break;
            case WCDMA:
                maxScale = pScale[3];
                break;
            case LTE:
            case ENDC:
//                ENDCDataModel endcDataModel = ShowInfo.getInstance().getEndcDataModel();
//                getMexScale(endcDataModel);
//                float beyondValue = 300000;//当前速率:300mbps=307200kbps,显示的是mbps
                //当前速率大于300mbps，仪表盘最大值设置为1000M（4T4R）
                float total = mLteValue + mNRValue;
                if (total < 300000) {
                    maxScale = endcScale[0];
                } else if (total > 300000 && total < 500000) {
                    maxScale = endcScale[1];
                } else if (total > 500000 && total < 1000000) {
                    maxScale = endcScale[2];
                } else if (total > 1000000) {
                    maxScale = endcScale[3];
                }
//                maxScale = mLteValue + mNRValue > beyondValue ? pScale[5] : pScale[4];
                break;
            case NBIoT:
            case CatM:
                float currentNBSpeed = ShowInfo.getInstance().getYwDataModel(name).getBordCurrentSpeed();
                float beyondNBValue = 30;
                maxScale = currentNBSpeed > beyondNBValue ? pScale[7] : pScale[6];
                break;
            default:
                maxScale = pScale[0];
                break;
        }
    }

    public void refreshView()
    {
        if (!ApplicationModel.getInstance().isFreezeScreen()) {
            getData();
            changeYiBiaoRes();
            getTextValus();
            this.invalidate();
        }
    }

}
