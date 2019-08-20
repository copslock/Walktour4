package com.walktour.gui.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import com.walktour.Utils.TraceInfoInterface;
import com.walktour.Utils.UtilsMethodPara;
import com.walktour.base.util.LogUtil;
import com.walktour.framework.database.BaseStationDBHelper;
import com.walktour.framework.database.model.BaseStationDetail;
import com.walktour.framework.view.BasicTotalView;
import com.walktour.gui.WalktourApplication;
import com.walktour.model.Especial;
import com.walktour.model.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * 动态参数呈现界面
 */
public class DynamicParamView extends BasicTotalView {
	private static final String TAG = "DynamicParamView";
	DisplayMetrics metric = new DisplayMetrics();

	List<Parameter> parameters;
	private float density;

	private int viewHeight = 1;
	
	private int rowCount = 0;
	private final String[] especialKeys = new String[]{"7F000605", "7F000606"};//TS表格
	
	private List<Parameter> voiceParamList = new ArrayList<Parameter>();
	private List<Parameter> dataParamList = new ArrayList<Parameter>();
	private Context mContext;

	public DynamicParamView(Context context) {
		super(context);
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		mContext = context;
		density = getResources().getDisplayMetrics().density;
	}
	
	public void setParamData(List<Parameter> parameters){
		this.parameters = parameters;
		initViewHeight();
	}

	public DynamicParamView(Context context, AttributeSet attrs) {
		super(context, attrs);
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		mContext = context;
		density = getResources().getDisplayMetrics().density;
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		initViewHeight();
		CreateTable(canvas);
	}

	/**
	 * 创建表格
	 * 
	 * @return 
	 */
	protected void CreateTable(Canvas cv) {
		int width = this.getWidth();
		float startx = 1;
		float starty = 1;
		float stopx = 0;
		float stopy = 0;
		int row = 0;
		// voice table 标题框
		if (voiceParamList.size() > 0) {
			cv.drawLine(startx, starty, width, starty, linePaint);
			cv.drawLine(startx, rowHeight, width, rowHeight, linePaint);
			cv.drawLine(startx, starty, startx, rowHeight, linePaint);
			cv.drawLine(width, starty, width, rowHeight, linePaint);
			// voice 标签
			// cv.drawRect(startx, starty, this.getWidth(), rowHeight, backgroudPaint);//填充标题框
			cv.drawText("Voice", this.getWidth() / 2 - getTextWidth("voice", titleTextSize) / 2, rowHeight / 2 + getFontHeight(titleTextSize) / 2, titleFontPaint);
		}
		boolean isLeftEvenNumber = true;// 是否左边是偶数
		row++;//换行
		for (int i = 0; i < voiceParamList.size(); i++) {
			Parameter p = voiceParamList.get(i);
			if (isTsTable(p.getKey())) {
				row = this.drawTsTable(p, cv, row, startx);
			} else {
				
				if (p.isSingleLine()) {
					if (i % 2 != 0) {
						isLeftEvenNumber = true;
					} else {
						isLeftEvenNumber = false;
					}
					row = this.drawSingleColumnCell(voiceParamList, p, i, cv, row, startx);
					if (p.getEspecialType() == Especial.TYPE_ONE) {
						row = this.drawSpecialTable(p, cv, row, startx);
					}
				} else {
					if (p.getEspecialType() == Especial.TYPE_ONE) {
						if (isLeftEvenNumber && i % 2 == 0) {
							isLeftEvenNumber = false;
						} else if (isLeftEvenNumber && i % 2 != 0) {
							isLeftEvenNumber = true;
						} else if (!isLeftEvenNumber && i % 2 != 0) {
							isLeftEvenNumber = true;
						} else {
							isLeftEvenNumber = false;
						}
						row = drawSpecialTable(p, cv, row, startx);
					} else {
						row = this.drawDoubleColumnCell(voiceParamList, p, i, isLeftEvenNumber, cv, row, startx);
					}
				}
			}
		}
		// =============================================data=================================================================================
		// data table 标题框
		if (dataParamList.size() > 0) {
			if (voiceParamList.size() > 0) {
				row = row + 2;
			} else {
				row = 1;
			}
			startx = 1;
			starty = row * rowHeight;
			cv.drawLine(startx, starty + rowHeight, width, starty + rowHeight, linePaint);
			cv.drawLine(startx, starty, startx, starty + rowHeight, linePaint);
			cv.drawLine(width, starty, width, starty + rowHeight, linePaint);
			// data 标签
			// cv.drawRect(startx, starty - rowHeight, this.getWidth(), starty, backgroudPaint);//填充标题框
			cv.drawText("Data", this.getWidth() / 2 - getTextWidth("voice", titleTextSize) / 2, starty - rowHeight / 2 + getFontHeight(titleTextSize) / 2, titleFontPaint);
		}
		isLeftEvenNumber = true;// 是否左边是偶数
		for (int i = 0; i < dataParamList.size(); i++) {
			Parameter p = dataParamList.get(i);
			if (isTsTable(p.getKey())) {
				row = this.drawTsTable(p, cv, row, startx);
			} else {

				if (p.isSingleLine()) {
					if (i % 2 != 0) {
						isLeftEvenNumber = true;
					} else {
						isLeftEvenNumber = false;
					}
					row = this.drawSingleColumnCell(dataParamList, p, i, cv, row, startx);
					if (p.getEspecialType() == Especial.TYPE_ONE) {
						row = this.drawSpecialTable(p, cv, row, startx);
					}
					
				} else {
					if (p.getEspecialType() == Especial.TYPE_ONE) {
						if (isLeftEvenNumber && i % 2 == 0) {
							isLeftEvenNumber = false;
						} else if (isLeftEvenNumber && i % 2 != 0) {
							isLeftEvenNumber = true;
						} else if (!isLeftEvenNumber && i % 2 != 0) {
							isLeftEvenNumber = true;
						} else {
							isLeftEvenNumber = false;
						}
						row = drawSpecialTable(p, cv, row, startx);
					} else {
						row = this.drawDoubleColumnCell(dataParamList, p, i, isLeftEvenNumber, cv, row, startx);
					}
					
				}
			}

		}
		// 画整个表的框和横线
		rowCount = row + 1;
		viewHeight = (int)rowHeight * rowCount + 2;
		for (int i = 0; i < rowCount; i++) {
			startx = 1;
			stopx = width;
			starty = rowHeight * (i + 1);
			stopy = rowHeight * (i + 1);
			cv.drawLine(startx, starty, stopx, stopy, linePaint);
		}
		cv.drawLine(startx, rowHeight, startx, rowHeight * rowCount, linePaint);// table左边线
		cv.drawLine(this.getWidth(), rowHeight, this.getWidth(), rowHeight * rowCount, linePaint);//table 右边线

		cv.save();
		cv.restore();
	}
	
	
	/**获得得参数队列中指定ID的值*/
    private String getParaValue(int paraId){
        return TraceInfoInterface.getParaValue(paraId);
    }
    
	/**
	 * 画单列单元格
	 * @param p
	 * @param cv
	 * @param row
	 * @param startx
	 * @return
	 */
	private int drawSingleColumnCell(List<Parameter> paramList, Parameter p, int position,  Canvas cv, int row, float startx) {
		String name = p.getShowName();
		String value = "";
		if (p.getId() != null && !p.getId().equals("")) {
			value = UtilsMethodPara.byValue2Enum(p,mContext);
		}
		if(name.equals("(L)Cell Name") || name.equals("(T)Cell Name") || name.equals("(G)Cell Name")){
			String cellId = TraceInfoInterface.getParaValue(0x0A0050A4);
			LogUtil.d(TAG,"----cellId:" + cellId + "----");
			if(TextUtils.isEmpty(cellId)){
				value = "";
			}else{
				List<BaseStationDetail> detailList = BaseStationDBHelper.getInstance(WalktourApplication.getAppContext()).queryCellByCellId(cellId);
				if(null != detailList && !detailList.isEmpty()){
					value = detailList.get(0).cellName;
				}else{
					value = "";
				}
			}
		}
		cv.drawText(name, startx + marginSize, rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
		cv.drawText(value, this.getWidth() - getTextWidth(value, textSize) - marginSize, rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, valuePaint);
		if (position < paramList.size() - 1) {
			row++;
		}
		return row;
	}
	
	
	
	
	/**
	 * 画双列单元格
	 * @param p
	 * @param cv
	 * @param row
	 * @param startx
	 * @return
	 */
	private int drawDoubleColumnCell(List<Parameter> paramList, Parameter p, int position, boolean isLeftEvenNumber, Canvas cv, int row, float startx) {
		String name = p.getShowName() + (p.getUnit().length() == 0 ? "" : "(" + p.getUnit() + ")");
		String value = "";
		if (p.getId() != null && !p.getId().equals("")) {
			value = UtilsMethodPara.byValue2Enum(p,mContext);
		} 
		if (isLeftEvenNumber) {//左边为偶数
			if ((position) % 2 == 0) {//左边
				cv.drawText(name, startx + marginSize, rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
				cv.drawText(value, this.getWidth()/2 - marginSize - getTextWidth(value, textSize), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, valuePaint);
				cv.drawLine(this.getWidth() / 2, rowHeight * row, this.getWidth() / 2, rowHeight * (row + 1), linePaint);
				if ((position < paramList.size() - 1 && (paramList.get(position + 1).isSingleLine())  || (position < paramList.size() - 1 && paramList.get(position + 1).getEspecialType() == Especial.TYPE_ONE)) ) {
					row++;//换行
				}
			} else {//右边
				cv.drawText(name, startx + this.getWidth() / 2 + marginSize, rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
				cv.drawText(value, startx + this.getWidth() - marginSize - getTextWidth(value, textSize), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, valuePaint);

				if (position < paramList.size() - 1) {
					row++;
				}
			}

		} else {
			if (position % 2 == 0) {//右边
				cv.drawText(name, startx + this.getWidth() / 2 + marginSize, rowHeight * row + rowHeight/2 + getFontHeight(textSize) / 2, fontPaint);
				cv.drawText(value, this.getWidth()  - marginSize - getTextWidth(value, textSize), rowHeight * row + rowHeight/2 + getFontHeight(textSize) / 2, valuePaint);
				if (position < paramList.size() - 1) {
					row++;
				}
			} else {//左边
				cv.drawText(name, startx + marginSize, rowHeight*row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
				cv.drawText(value, this.getWidth()/2 - marginSize - getTextWidth(value, textSize), rowHeight*row + rowHeight / 2 + getFontHeight(textSize) / 2, valuePaint);
				cv.drawLine(this.getWidth() / 2, rowHeight * row,this.getWidth() / 2, rowHeight * (row + 1),linePaint);
				if ((position < paramList.size() - 1 && (paramList.get(position + 1).isSingleLine())  || (position < paramList.size() - 1 && paramList.get(position + 1).getEspecialType() == Especial.TYPE_ONE)) ) {
					row++;//换行
				}
			}
		}
		return row;
	}
	
	/**
	 * 画特殊表格
	 * @param cv
	 * @param row
	 * @param startx
	 * @return
	 */
	private int drawSpecialTable(Parameter p, Canvas cv, int row, float startx) {
//			++row;
			int col = p.getEspecial().getCol();
			int rowSpecial = p.getEspecial().getRow();
			//画标题
			cv.drawText(p.getEspecial().getTableTitle(), this.getWidth()/2 - this.getTextWidth(p.getEspecial().getTableTitle(), textSize)/2, rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
			++row;//换行
			int colWidth = 0;
			int columnOneWidth = 0;
			if (p.getEspecial().getColumnWidth() == 0) {
				colWidth = this.getWidth()/(col + 1);
				columnOneWidth = colWidth;
			} else {
				columnOneWidth = (int)(this.getWidth() * (p.getEspecial().getColumnWidth()/100.0));
				colWidth = (this.getWidth() - columnOneWidth)/(col);
				
			}
			for (int j = 0; j < col; j++) {//top标题
				String topTitle = p.getEspecial().getColumnTitles()[j];
				cv.drawText(topTitle, columnOneWidth + colWidth * (j) + (colWidth/2 - this.getTextWidth(topTitle, textSize)/2), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
				cv.drawLine(startx + columnOneWidth + colWidth * (j), rowHeight * row, startx + columnOneWidth + colWidth * (j), rowHeight * (row + 1), linePaint);
			}
			++row;//换行

			for (int j = 0; j < rowSpecial; j++) {
				//leftTitle
//				String leftTitle = p.getEspecial().getTableRows()[j].getName() + (p.getUnit().length() == 0 ? "" : "(" + p.getUnit() + ")");
//				cv.drawText(leftTitle, startx + (colWidth/2 - this.getTextWidth(leftTitle, textSize)/2), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
				String leftTitle = p.getEspecial().getTableRows()[j].getName() + (p.getEspecial().getTableRows()[j].getUnit().length() == 0 ? "" : "(" + p.getEspecial().getTableRows()[j].getUnit() + ")");
				cv.drawText(leftTitle, startx + (columnOneWidth/2 - this.getTextWidth(leftTitle, textSize)/2), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
				int scale =  p.getEspecial().getTableRows()[j].getScale();
				for (int j2 = 0; j2 < col; j2++) {
					String key = p.getEspecial().getTableRows()[j].getKeys()[j2];
					int decimal = p.getEspecial().getTableRows()[j].getDecimal();
					if(key.length()==0){
						key = "0";
					}
					key = UtilsMethodPara.changeScaleValue(key,scale,decimal);
					key = UtilsMethodPara.byValue2Enum(key, p.getEspecial().getTableRows()[j].getEnums() == 1,
							p.getEspecial().getTableRows()[j].getEnumSet());
					cv.drawText(key, columnOneWidth + colWidth * j2 + (colWidth/2 - this.getTextWidth(key, textSize)/2), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, valuePaint);
					cv.drawLine(startx + columnOneWidth + colWidth * j2, rowHeight * row, startx + columnOneWidth + colWidth * j2, rowHeight * (row + 1), linePaint);
				}
				if (j < rowSpecial) {
					row++;
				}
			}
			return row;
	}
	
	
	/**
	 * 处理特殊edge时空隧道参数取值
	 */
	private char[] getTSBinaryChar(String id){
		try {
			char[] tsULOrDl = UtilsMethodPara.getTSBinaryChar(getParaValue(Integer.valueOf(id.trim(),16)));
			return tsULOrDl;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new char[8];
	}
	
	/**
	 * 画时空隧道图
	 * @param p
	 * @param cv
	 * @param row
	 * @param startx
	 * @return
	 */
	private int drawTsTable(Parameter p, Canvas cv, int row, float startx) {
		int col = 8;
		String leftTitle = "";
		int columnOneWidth = (int)getTextWidth("UL TS", textSize) + 14;
		int columnWidth = (this.getWidth() - columnOneWidth) / col;
		if (p.getKey().equals(especialKeys[0])) {
			leftTitle = "DL TS";
		} else if (p.getKey().equals(especialKeys[1])) {
			leftTitle = "UL TS";
		}
		cv.drawText(leftTitle, startx + (columnOneWidth/2 - this.getTextWidth(leftTitle, textSize)/2), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, fontPaint);
		char[] value = getTSBinaryChar(p.getKey()); 
		for (int i = 0; i < 8; i++) {
			String name = "TS" + i;
			if (value[i]=='1') {
				float begin = startx + columnOneWidth + columnWidth * i;
				cv.drawRect(begin, rowHeight * (row + 1) - rowHeight, begin + columnWidth, rowHeight * (row + 1), backgroudPaint);//填充标题框
			}
			cv.drawText(name, columnOneWidth + columnWidth * i + (columnWidth/2 - this.getTextWidth(name, textSize)/2), rowHeight * row + rowHeight / 2 + getFontHeight(textSize) / 2, valuePaint);
			cv.drawLine(startx + columnOneWidth + columnWidth * i, rowHeight * row, startx + columnOneWidth + columnWidth * i, rowHeight * (row + 1), linePaint);
		}
		return ++row;
	}
	
	private boolean isTsTable(String key) {
		for (int i = 0; i < especialKeys.length; i++) {
			if (especialKeys[i].equals(key)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 处理数据
	 */
	private void initViewHeight() {
		voiceParamList.clear();
		dataParamList.clear();
		for (int i = 0; i < parameters.size(); i++) {
			Parameter p = parameters.get(i);
			if (p.getTaskType() == 1) {
				voiceParamList.add(p);
			} else if (p.getTaskType() == 2){
				dataParamList.add(p);
			}
		}
	}
	
	/**
	 * 计算View高度
	 * @return
	 */
	public int calculateViewHeight() {
		int row = 0;
		int rowCount = 0;
		if (voiceParamList.size() > 0) {
			row = row + calculateTableRow(voiceParamList);
		}
		if (dataParamList.size() > 0) {
			if (voiceParamList.size() > 0) {
				row = row + calculateTableRow(dataParamList) + 1;
			} else {
				row = row + calculateTableRow(dataParamList);
			}
		}
		rowCount = row + 1;
		viewHeight = (int) (rowCount * rowHeight) + 2;
		return viewHeight;
	}
	
	private int calculateTableRow(List<Parameter> paramList) {
		int row = 0;
		if (paramList.size() > 0) {
			row++;//换行
		}
		boolean isLeftEvenNumber = true;// 是否左边是偶数
		for (int i = 0; i < paramList.size(); i++) {
			Parameter p = paramList.get(i);
			if (isTsTable(p.getKey())) {
				row++;
			} else {
				if (p.isSingleLine()) {
					if (i % 2 != 0) {
						isLeftEvenNumber = true;
					} else {
						isLeftEvenNumber = false;
					}
					if (i < paramList.size() - 1) {
						row++;
					}
				} else {
					if (p.getEspecialType() == Especial.TYPE_ONE) {
						if (isLeftEvenNumber && i % 2 == 0) {
							isLeftEvenNumber = false;
						} else if (isLeftEvenNumber && i % 2 != 0) {
							isLeftEvenNumber = true;
						} else if (!isLeftEvenNumber && i % 2 != 0) {
							isLeftEvenNumber = true;
						} else {
							isLeftEvenNumber = false;
						}
						row = row + p.getEspecial().getRow() + 2;
					} else {
						if (isLeftEvenNumber) {//左边为偶数
							if ((i) % 2 == 0) {//左边
								if ((i < paramList.size() - 1 && (paramList.get(i + 1).isSingleLine())  || (i < paramList.size() - 1 && paramList.get(i + 1).getEspecialType() == Especial.TYPE_ONE)) ) {
									row++;//换行
								}
							} else {//右边
								if (i < paramList.size() - 1) {
									row++;
								}
							}

						} else {
							if (i % 2 == 0) {//右边
								if (i < paramList.size() - 1) {
									row++;
								}
							} else {//左边
								if ((i < paramList.size() - 1 && (paramList.get(i + 1).isSingleLine())  || (i < paramList.size() - 1 && paramList.get(i + 1).getEspecialType() == Especial.TYPE_ONE)) ) {
									row++;//换行
								}
							}
						};
					}
				}
			}
		}
		return row;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		int width = metric.widthPixels;
		int height = viewHeight;
//		LogUtil.e(TAG,"widthMeasureSpec:"+widthMeasureSpec+",heightMeasureSpec:"+heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, height);
	}

	/**
	 * 计算文字高度
	 * 
	 * @param fontSize
	 *            以多大的字体计算
	 * @return
	 */
	private float getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (float) Math.ceil(fm.descent - fm.ascent) - 6 * density;
	}

	/**
	 * 计算字符串宽度
	 * 
	 * @param text
	 *            要计算的字符串
	 * @return
	 */
	private float getTextWidth(String text, float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		return paint.measureText(text);
	}
}
