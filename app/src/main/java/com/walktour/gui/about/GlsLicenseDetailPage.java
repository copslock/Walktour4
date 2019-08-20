package com.walktour.gui.about;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.gui.R;
import com.walktour.model.LicenseModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlsLicenseDetailPage extends BasicActivity{

	private View convertView;
	private LicenseModel licenseModeldetail;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_gls_license_detail);
		getIntentDate();
		findView();
		dynamicAddView();
		
	}
	
	/**
	 * 获取licenseModel对象
	 */
	private void getIntentDate() {
		licenseModeldetail = (LicenseModel)getIntent().getSerializableExtra("ItemDetail");
	}
	
	
	
	/**
	 * 动态添加View
	 */
	private void dynamicAddView() {
		LinearLayout layout = (LinearLayout)findViewById(R.id.addLayout);
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        for (int i = 0; i < licenseModeldetail.getFeatureList().size(); i++) {
        	convertView = inflater.inflate(R.layout.detail_item, null);  
        	TextView txt = (TextView)convertView.findViewById(R.id.detail_txt);
        	txt.setText(licenseModeldetail.getFeatureList().get(i));
        	layout.addView(convertView);
        	if(i!= licenseModeldetail.getFeatureList().size() - 1){
        		View view = new View(this);
        		view.setBackgroundColor(getResources().getColor(R.color.about_item));
        		view.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT , 1));
        		layout.addView(view);
        	}
		}
	}
	
	
	/**
	 * 加载所有控件
	 */
	private void findView() {
		TextView title =  initTextView(R.id.title_txt);
		title.setText("GLS Management");
		ImageView pointer = initImageView(R.id.pointer);
		pointer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GlsLicenseDetailPage.this.finish();
			}
		});
		TextView gls_detail_area = initTextView(R.id.gls_detail_area);
		TextView gls_detail_project = initTextView(R.id.gls_detail_area_project);
		TextView gls_detail_contract = initTextView(R.id.gls_detail_contract);
		TextView gls_detail_expiration = initTextView(R.id.gls_detail_expiration);
		TextView gls_detail_total_num = initTextView(R.id.gls_detail_total_num);
		TextView gls_detail_available_num = initTextView(R.id.gls_detail_available_num);
		gls_detail_area.setText(licenseModeldetail.getRegion()+", "+licenseModeldetail.getCountry());
		gls_detail_project.setText(licenseModeldetail.getProject());
		gls_detail_contract.setText(licenseModeldetail.getContractname());
		gls_detail_expiration.setText(getStringDateShort(licenseModeldetail.getExpiredate()));
		gls_detail_total_num.setText(String.valueOf(licenseModeldetail.getLicenseQuantity()));
		gls_detail_available_num.setText(String.valueOf(licenseModeldetail.getLicenseQuantity() - licenseModeldetail.getLicenseused()));
	}
	
	
	
	
    /*
     * 时间由String转为显示格式
     * 例：2013-10-24 07:18:43 显示 为 2013/10/24
     */
	public  String getStringDateShort(String time) {
		String dateString = null;
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = formatter.parse(time);
			DateFormat formatter1 = new SimpleDateFormat("yyyy/MM/dd");
			dateString = formatter1.format(date);
		} catch (ParseException e)
		{
			e.getStackTrace();
		}
		return dateString == null ? "time err": dateString;
	}
	
	
	
	
	
	public void writeJSON() {

		  JSONObject object = new JSONObject();
		  
		  JSONArray jsonArray = new JSONArray();
		try {  
		  for (int i = 0; i < 2; i++) {
			  JSONObject jsonObject=new JSONObject();
			  jsonObject.put("i"+"--"+i, i);
			  jsonObject.put("j"+"--"+i, i);
			  
			  jsonArray.put(jsonObject);
		}
		  
		  

		      object.put("name", "Jack Hack");

		      object.put("score", new Integer(200));

		      object.put("current", new Double(152.32));

		      object.put("nickname", "Hacker");
		      
		      object.put("array", jsonArray);
		  } catch (JSONException e) {

		      e.printStackTrace();
		  }

		  System.out.println(object);
		  writeTxt(object.toString());
		}
	
	
	public void writeTxt(String date) {
		File file = new File(Environment.getExternalStorageDirectory().getPath() + "/Walktour/data/license_checkout_detail.xml"); // 要写入的文件
				
		BufferedWriter writer = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			BufferedReader reader = new BufferedReader(new StringReader(date));
			writer = new BufferedWriter(new FileWriter(file));
			int len = 0;
			char[] buffer = new char[1024];
			while ((len = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, len);
			}
            reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null)
				try {
					writer.flush(); 
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
}
