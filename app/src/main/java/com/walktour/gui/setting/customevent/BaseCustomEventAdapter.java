package com.walktour.gui.setting.customevent;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.walktour.base.util.AppFilePathUtil;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.setting.customevent.model.CustomEvent;

import java.io.File;
import java.util.List;

/**
 * 自定义事件列表适配类抽象类
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("InflateParams")
public abstract class BaseCustomEventAdapter<T extends CustomEvent> extends ArrayAdapter<T> implements OnClickListener {
	/** 资源ID */
	private int resourceId;
	/** 事件保存工厂类 */
	private CustomEventFactory mFactory;
	/** 是否为可选模式 */
	private boolean isCheckMode = false;
	/** 页面类 */
	private BaseCustomEventListActivity<T> activity;

	public BaseCustomEventAdapter(BaseCustomEventListActivity<T> context, int textViewResourceId, List<T> objects,
			boolean isCheckMode) {
		super(context, textViewResourceId, objects);
		this.activity = context;
		this.resourceId = textViewResourceId;
		this.mFactory = CustomEventFactory.getInstance();
		this.isCheckMode = isCheckMode;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view;
		if (convertView == null) {
			view = LayoutInflater.from(super.getContext()).inflate(resourceId, null);
		} else {
			view = convertView;
		}
		T event = this.getItem(position);
		TextView title = (TextView) view.findViewById(R.id.ItemTitle);
		title.setText(event.getName());
		ImageView image = (ImageView) view.findViewById(R.id.ItemIcon);
		image.setTag(event);
		image.setImageDrawable(Drawable.createFromPath(event.getIconFilePath()));
		image.setOnClickListener(this);

		TextView description = (TextView) view.findViewById(R.id.ItemContent);
		String content = getDescription(event);
		description.setText(content);

		CheckBox checkBox = (CheckBox) view.findViewById(R.id.ItemCheckble);
		checkBox.setTag(event);
		checkBox.setOnCheckedChangeListener(this.activity);
		if (isCheckMode) {
			checkBox.setVisibility(View.VISIBLE);
			checkBox.setChecked(false);
		} else {
			checkBox.setVisibility(View.GONE);
		}

		return view;
	}

	/**
	 * 获得自定义事件描述
	 * 
	 * @param define
	 *          自定义事件对象
	 * @return
	 */
	protected abstract String getDescription(T define);

	/**
	 * 显示图标选择窗口 showIconDialog 函数功能：
	 * 
	 * @param define
	 *          自定义对象
	 */
	private void showIconDialog(final CustomEvent define) {
		String iconDir = AppFilePathUtil.getInstance().getSDCardBaseDirectory("icons");
		GridView gridView = (GridView) LayoutInflater.from(super.getContext()).inflate(R.layout.gridview_custom_event,
				null);

		final CustomIcomAdatper gridAdatper = new CustomIcomAdatper(super.getContext(), new File(iconDir));
		gridView.setAdapter(gridAdatper);

		final BasicDialog dialog = new BasicDialog.Builder(super.getContext()).setView(gridView).setTitle(define.getName())
				.create();
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				define.setOldName(define.getName());
				define.setIconFilePath(gridAdatper.getItem(position).getAbsolutePath());
				mFactory.editCustomEvent(define);
				dialog.dismiss();
				notifyDataSetChanged();
			}
		});

		dialog.show();
	}

	@Override
	public void onClick(View v) {
		if (!this.isCheckMode)
			showIconDialog((CustomEvent) v.getTag());
	}

	public void setCheckMode(boolean isCheckMode) {
		this.isCheckMode = isCheckMode;
	}

}
