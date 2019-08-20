package com.walktour.mapextention.ibwave;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jhlabs.map.java.Point2D;
import com.jhlabs.map.java.Rectangle2D;
import com.jhlabs.map.proj.Projection;
import com.jhlabs.map.proj.ProjectionFactory;

import java.io.File;

public class LabelView extends View {
	private LabelViewListener mLabelViewListener;
	
	private Drawer mDrawerTabRaster = new DrawerTabRaster();
	private Drawer mDrawerSite = new DrawerSite();
	
	private Paint mTextPaint;
	private String mText;
	private int mAscent;
	
	private Rectangle2D.Double mRectangle2D; // DoBeforeAddLayer 与  DoAfterAddLayer 使用
	
	private LayerManager mLayerManager;
	public LayerManager getLayerManager() {
		return mLayerManager;
	}
	
	private ViewPort mViewPort;
	public ViewPort getViewPort() {
		return mViewPort;
	}

	private Projection mProj;
	public Projection getProj() {
		return mProj;
	}

	private Point mDownPoint;
	private Point2D.Double mDownPoint2D;
	private Rectangle2D.Double mDownViewArea2D = new Rectangle2D.Double();
	private Point mMovePoint;
	private double mOldDist;
	private Point2D.Double mMidPoint2D = new Point2D.Double();
	
	private static final int NONE = 0; 
	private static final int DRAG = 1; 
	private static final int ZOOM = 2; 
	private int mMode = NONE;
	   
	/**
	 * Constructor. This version is only needed if you will be instantiating the
	 * object manually (not from a layout XML file).
	 * 
	 * @param context
	 */
	public LabelView(Context context) {
		super(context);
		initLabelView();
	}

	/**
	 * Construct object, initializing with any attributes we understand from a
	 * layout file. These attributes are defined in
	 * SDK/assets/res/any/classes.xml.
	 * 
	 * @see android.view.View#View(android.content.Context,
	 *      android.util.AttributeSet)
	 */
	public LabelView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLabelView();
		// Retrieve the color(s) to be used for this view and apply them.
		// Note, if you only care about supporting a single color, that you
		// can instead call a.getColor() and pass that to setTextColor().
		// setTextColor(a.getColor(R.styleable.LabelView_textColor,
		// 0xFF000000));
		//
		// int textSize = a.getDimensionPixelOffset(
		// R.styleable.LabelView_textSize, 0);
		// if (textSize > 0) {
		// setTextSize(textSize);
		// }

//		try
//		{
//			mProj = ProjectionFactory.readProjectionFile("epsg", "32618");
//		}
//		catch(Exception e)
//		{
//			
//		}
		//String args[] = new String[]{"+proj=utm", "+zone=18", "+ellps=WGS84", "+datum=WGS84", "+units=m", "+lon_0=-75", "+no_defs"};    
		String args[] = new String[]{"+proj=utm", "+zone=18", "+ellps=WGS84", "+datum=WGS84", "+units=m", "+no_defs"};
        mProj = ProjectionFactory.fromPROJ4Specification( args );
        
		mViewPort = new ViewPort();
		
		ResizeViewPort();
		
		mLayerManager = new LayerManager(); 

		mDownPoint = new Point();
		mMovePoint = new Point();
		mDownPoint2D = new Point2D.Double();
		File sdcard = Environment.getExternalStorageDirectory();
        //String sTab = sdcard.toString() + "/map/tab/bb981e46-1941-43d1-85a6-eec40c3dd8a9.tab";
        String sTab = sdcard.toString() + "/地图/tab地图/Rangers Ballpark_iBwave Export/Rangers Ballpark__1000 Ball Park Way__Arlington, Tx 76011 HEADEND LOGICAL SCHEMATIC.tab";
		AddTabRasterLayer(sTab);

	}

	private void DoBeforeAddLayer()
	{
		mRectangle2D = mLayerManager.getDataScope();
	}

	private void DoAfterAddLayer()
	{
		if (mRectangle2D.width == 0 || mRectangle2D.height == 0)
		{
			mRectangle2D = mLayerManager.getDataScope();
			mViewPort.setViewArea(mRectangle2D);
		}
	}
	
	public void AddTabRasterLayer(String sFileName){
		DoBeforeAddLayer();
		
		TabMap mMap = new TabMapRaster(sFileName,new StringBuffer());
		mMap.recalcDataScope(mProj);
		mMap.setmDrawer(mDrawerTabRaster);
		
		mLayerManager.AddLayer(mMap);
		
		DoAfterAddLayer();
	}
	
	public void AddSiteLayer(String sFileName){
		DoBeforeAddLayer();

		TabMap mMap = new SiteMap(sFileName);
		mMap.recalcDataScope(mProj);
		mMap.setmDrawer(mDrawerSite);
		mLayerManager.AddLayer(mMap);
		
		DoAfterAddLayer();
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) 
	{
		super.onSizeChanged(w, h, oldw, oldh);
		this.ResizeViewPort();
	}  

	private final void initLabelView() {
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(16);
		mTextPaint.setColor(0xFF000000);
		setPadding(3, 3, 3, 3);
	}

	public int getClientX()
	{
		return 0;
	}
	
	public int getClientY()
	{
		return 0;
	}
	
	public int getClientWidth()
	{
		return this.getWidth();
	}
	
	public int getClientHeight()
	{
		return this.getHeight();
	}
	
	public boolean isPointInClient(float x, float y)
	{
		boolean b = x >= getClientX() && x < getClientX() + getClientWidth()
				&& y > getClientY() && y < getClientY() + getClientWidth();
		return b;
	}

	/**
	 * Sets the text to display in this label
	 * 
	 * @param text
	 *            The text to display. This will be drawn as one line.
	 */
	public void setText(String text) {
		mText = text;
		requestLayout();
		invalidate();
	}

	/**
	 * Sets the text size for this label
	 * 
	 * @param size
	 *            Font size
	 */
	public void setTextSize(int size) {
		mTextPaint.setTextSize(size);
		requestLayout();
		invalidate();
	}

	/**
	 * Sets the text color for this label.
	 * 
	 * @param color
	 *            ARGB value for the text
	 */
	public void setTextColor(int color) {
		mTextPaint.setColor(color);
		invalidate();
	}

	/**
	 * @see android.view.View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = (int) mTextPaint.measureText(mText) + getPaddingLeft()
					+ getPaddingRight();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		mAscent = (int) mTextPaint.ascent();
		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop()
					+ getPaddingBottom();
			if (specMode == MeasureSpec.AT_MOST) {
				// Respect AT_MOST value if that was what is called for by
				// measureSpec
				result = Math.min(result, specSize);
			}
		}
		return result;
	}
	
	private void ResizeViewPort()
	{
		mViewPort.resize(getClientX(), getClientY(), getClientWidth(), getClientHeight());
	}

	/**
	 * Render the text
	 * 
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		mLayerManager.Draw(canvas, mProj, mViewPort);
		
		//canvas.drawText(mText, iw / 2, ih / 2 - mAscent, mTextPaint);
		
		
		
		
		//mProj.transform(m_pt2d_src, m_pt2d_dst);

		//canvas.drawText(m_pt2d_src.toString(), iw / 2, ih / 3 - mAscent, mTextPaint);
		//canvas.drawText(m_pt2d_dst.toString(), iw / 2, ih / 4 - mAscent, mTextPaint);

		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		switch(event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			if (isPointInClient(event.getX(), event.getY()))
			{
				PointF vp = new PointF();
				vp.x = (int)event.getX();
				vp.y = (int)event.getY();
				Point2D.Double dp = new Point2D.Double();
				mViewPort.VPToDP(vp,  dp);
				
				mDownPoint.x = (int)Math.floor(event.getX());
				mDownPoint.y = (int)Math.floor(event.getY());
				mDownPoint2D.x = mViewPort.getViewArea().x;
				mDownPoint2D.y = mViewPort.getViewArea().y;
				mMode = DRAG;
			}
			break;		
		//设置多点触摸模式 
		case MotionEvent.ACTION_POINTER_DOWN: 
			mOldDist = spacing(event); 
			if (mOldDist > 10f) {  
				PointF midPoint = new PointF();
				midPoint(midPoint, event); 
				mViewPort.VPToDP(midPoint, mMidPoint2D);
				mDownViewArea2D.setRect(mViewPort.getViewArea());
				mMode = ZOOM; 
			}
			break; 
		case MotionEvent.ACTION_MOVE:
			switch (mMode)
			{
			case DRAG:
				mMovePoint.x = (int)Math.floor(event.getX());
				mMovePoint.y = (int)Math.floor(event.getY());
				Point pt = new Point();
				pt.x = mMovePoint.x - mDownPoint.x;
				pt.y = mMovePoint.y - mDownPoint.y;
				mViewPort.move(mDownPoint2D, pt);
				break;
			case ZOOM:
				double newDist = spacing(event); 
				if (newDist > 10f) 
				{ 
					double scale = newDist / mOldDist; 
					mViewPort.zoom(mDownViewArea2D, scale);
				} 
				break;
			}
			this.invalidate();
			break;
		case MotionEvent.ACTION_UP:
			mLabelViewListener.OnClick(event.getX(), event.getY());

			break;
        case MotionEvent.ACTION_POINTER_UP: 
            mMode = NONE; 
            break; 	
		}
		return true;
	}
	
	//计算移动距离 
	private double spacing(MotionEvent event) { 
		float x = event.getX(0) - event.getX(1); 
		float y = event.getY(0) - event.getY(1); 
		return Math.sqrt(x * x + y * y);
	}
	
	//计算中点位置 
	private void midPoint(PointF point, MotionEvent event) { 
		float x = event.getX(0) + event.getX(1); 
		float y = event.getY(0) + event.getY(1); 
		point.set(x / 2, y / 2); 
	}

	public LabelViewListener getLabelViewListener() {
		return mLabelViewListener;
	}

	public void setLabelViewListener(LabelViewListener mLabelViewListener) {
		this.mLabelViewListener = mLabelViewListener;
	}
}

