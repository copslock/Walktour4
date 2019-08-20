package com.walktour.gui.locknet.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.walktour.Utils.ApplicationModel;
import com.walktour.base.util.LogUtil;
import com.walktour.control.bean.Verify;
import com.walktour.control.config.Deviceinfo;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;
import com.walktour.gui.locknet.ForceControler.Band;
import com.walktour.gui.locknet.ForceControler.ForceNet;
import com.walktour.gui.locknet.ForceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * 锁定频点对话框
 * 
 * @author jianchao.wang
 *
 */
@SuppressLint("InflateParams")
public class LockFrequencyDialog {
	/** 上下文 */
	private Activity context;
	/** 强制信息管理类 */
	private ForceManager mForceMgr;
	/** 回调方法 */
	private OnDialogChangeListener callback;

	public LockFrequencyDialog(Activity context, OnDialogChangeListener diagListener) {
		this.context = context;
		this.callback = diagListener;
		mForceMgr = ForceManager.getInstance();

	}

	public void show() {
		Deviceinfo device = Deviceinfo.getInstance();
		if (ApplicationModel.getInstance().isNBTest()) {
			showNbIot();
		} else {
			showNormal();
		}
	}

	public void showNormal() {
		// 设备配置文件
		Deviceinfo device = Deviceinfo.getInstance();
		Set<String> types = device.getNettypes();
		Iterator<String> interator = types.iterator();

		// 增加可以锁频的网络类型
		List<String> netList = new ArrayList<>();
		while (interator.hasNext()) {
			String network = interator.next();
			if (device.isSamsungCustomRom()) {
				if (network.equals(Deviceinfo.NET_TYPES_GSM)) {
					netList.add(network);
				}
			}
			if (network.equals(Deviceinfo.NET_TYPES_WCDMA) || network.equals(Deviceinfo.NET_TYPES_LTE)) {
				netList.add(network);
			}
		}
		final String[] networks = new String[netList.size()];
		netList.toArray(networks);

		new BasicDialog.Builder(context).setTitle(R.string.locl_lock_point)
				.setItems(networks, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						showFrequencySetDialog(networks[which]);
					}
				}).show();
	}

	int LTE_EARFCN_To_Band(int EARFCN, boolean DL)
	{
		//Band = 33
		if((EARFCN >= 36000)&&(EARFCN <= 36199))
			return 33;
		else if((EARFCN >= 36200)&&(EARFCN <= 36349))
			return 34;
		else if((EARFCN >= 36350)&&(EARFCN <= 36949))
			return 35;
		else if((EARFCN >= 36950)&&(EARFCN <= 37549))
			return 36;
		else if((EARFCN >= 37550)&&(EARFCN <= 37749))
			return 37;
		else if((EARFCN >= 37750)&&(EARFCN <= 38249))
			return 38;
		else if((EARFCN >= 38250)&&(EARFCN <= 38649))
			return 39;
		else if((EARFCN >= 38650)&&(EARFCN <= 39649))
			return 40;
		else if ((EARFCN >= 39650) && (EARFCN <= 41589))
			return 41;
		else if ((EARFCN >= 41590) && (EARFCN <= 43589))
			return 42;
		//Band 43 --> tdd
		else if ((EARFCN >= 43590) && (EARFCN <= 45589))
			return 43;
		else if((EARFCN >= 45590)&&(EARFCN <= 46589))
			return 44;
		else if ((EARFCN >= 46890)&&(EARFCN <= 54340))
			return 46;
		else if ((EARFCN >= 65200)&&(EARFCN <= 65400))
			return 62;

		//FDD 下行
		if(DL) {
			if ((EARFCN >= 0) && (EARFCN <= 599))
				return 1;
			else if ((EARFCN >= 600) && (EARFCN <= 1199))
				return 2;
			else if ((EARFCN >= 1200) && (EARFCN <= 1949))
				return 3;
			else if ((EARFCN >= 1950) && (EARFCN <= 2399))
				return 4;
			else if ((EARFCN >= 2400) && (EARFCN <= 2649))
				return 5;
			else if ((EARFCN >= 2650) && (EARFCN <= 2749))
				return 6;
			else if ((EARFCN >= 2750) && (EARFCN <= 3449))
				return 7;
			else if ((EARFCN >= 3450) && (EARFCN <= 3799))
				return 8;
			else if ((EARFCN >= 3800) && (EARFCN <= 4149))
				return 9;
			else if ((EARFCN >= 4150) && (EARFCN <= 4749))
				return 10;
			else if ((EARFCN >= 4750) && (EARFCN <= 4949))
				return 11;
			else if ((EARFCN >= 5010) && (EARFCN <= 5179))
				return 12;
			else if ((EARFCN >= 5180) && (EARFCN <= 5279))
				return 13;
			else if ((EARFCN >= 5280) && (EARFCN <= 5379))
				return 14;
			else if ((EARFCN >= 5730) && (EARFCN <= 5849))
				return 17;
			else if ((EARFCN >= 5850) && (EARFCN <= 5999))
				return 18;
			else if ((EARFCN >= 6000) && (EARFCN <= 6149))
				return 19;
			else if ((EARFCN >= 6150) && (EARFCN <= 6449))
				return 20;
			else if ((EARFCN >= 6450) && (EARFCN <= 6599))
				return 21;
			else if ((EARFCN >= 6600) && (EARFCN <= 7399))
				return 22;
			else if ((EARFCN >= 7500) && (EARFCN <= 7699))
				return 23;
			else if ((EARFCN >= 7700) && (EARFCN <= 8039))
				return 24;
			else if ((EARFCN >= 8040) && (EARFCN <= 8689))
				return 25;
			else if ((EARFCN >= 8690) && (EARFCN <= 9039))
				return 26;
			else if ((EARFCN >= 9040) && (EARFCN <= 9209))
				return 27;
			else if ((EARFCN >= 9210) && (EARFCN <= 9659))
				return 28;
			else if ((EARFCN >= 9660) && (EARFCN <= 9769))
				return 29;
		} else { //上行
			if((EARFCN >= 18000)&&(EARFCN <= 18599))
				return 1;
			else if((EARFCN >= 18600)&&(EARFCN <= 19199))
				return  2;
			else if((EARFCN >= 19200)&&(EARFCN <= 19949))
				return  3;
			else if((EARFCN >= 19950)&&(EARFCN <= 20399))
				return 4;
			else if((EARFCN >= 20400)&&(EARFCN <= 20649))
				return 5;
			else if((EARFCN >= 20650)&&(EARFCN <= 20749))
				return 6;
			else if((EARFCN >= 20750)&&(EARFCN <= 21449))
				return 7;
			else if((EARFCN >= 21450)&&(EARFCN <= 21799))
				return 8;
			else if((EARFCN >= 21800)&&(EARFCN <= 22149))
				return 9;
			else if((EARFCN >= 22150)&&(EARFCN <= 22749))
				return 10;
			else if((EARFCN >= 22750)&&(EARFCN <= 22949))
				return 11;
			else if((EARFCN >= 23010)&&(EARFCN <= 23179))
				return 12;
			else if((EARFCN >= 23180)&&(EARFCN <= 23279))
				return 13;
			else if((EARFCN >= 23280)&&(EARFCN <= 23379))
				return 14;
			else if((EARFCN >= 23730)&&(EARFCN <= 23849))
				return 17;
			else if((EARFCN >= 23850)&&(EARFCN <= 23999))
				return 18;
			else if((EARFCN >= 24000)&&(EARFCN <= 24149))
				return 19;
			else if((EARFCN >= 24150)&&(EARFCN <= 24449))
				return 20;
			else if((EARFCN >= 24450)&&(EARFCN <= 24599))
				return 21;
			else if((EARFCN >= 24600)&&(EARFCN <= 25399))
				return 22;
			else if((EARFCN >= 25500)&&(EARFCN <= 25699))
				return 23;
			else if((EARFCN >= 25700)&&(EARFCN <= 26039))
				return 24;
			else if((EARFCN >= 26040)&&(EARFCN <= 26689))
				return 25;
			else if((EARFCN >= 26690)&&(EARFCN <= 27039))
				return 26;
			else if((EARFCN >= 27040)&&(EARFCN <= 27209))
				return 27;
			else if((EARFCN >= 27210)&&(EARFCN <= 27659))
				return 28;
		}
		return 0;
	}

	//避免double类型精度问题,函数扩大十倍后输出;
    //输入为实际的频点与频段值
	int LTE_DLEARFCN_To_Freq(int DLEARFCN, int Band, boolean bDL) {
		int iResult = 0, iEARFCN = DLEARFCN;

		//下行
		if (bDL) {
			switch (Band) {
				case 1:	iResult = (iEARFCN - 0)  + 21100; 		break;
				case 2:	iResult = (iEARFCN - 600) + 19300; 		break;
				case 3:	iResult = (iEARFCN - 1200) + 18050; 	    break;
				case 4:	iResult = (iEARFCN - 1950) + 21100; 	break;
				case 5:	iResult = (iEARFCN - 2400) + 8690;		break;
				case 6:	iResult = (iEARFCN - 2650) + 8750; 		break;
				case 7: iResult = (iEARFCN - 2750) + 26200; 	break;
				case 8: iResult = (iEARFCN - 3450) + 9250; 		break;
				case 9: iResult = (iEARFCN - 3800) + 18449; 	break;
				case 10:iResult = (iEARFCN - 4150) + 21100; 	break;
				case 11:iResult = (iEARFCN - 4750) + 14759; 	break;
				case 12:iResult = (iEARFCN - 5010) + 7290; 		break;
				case 13:iResult = (iEARFCN - 5180) + 7460; 		break;
				case 14:iResult = (iEARFCN - 5280) + 7580; 		break;
				case 17:iResult = (iEARFCN - 5730) + 7340; 		break;
				case 18:iResult = (iEARFCN - 5850) + 8600; 		break;
				case 19:iResult = (iEARFCN - 6000) + 8750; 		break;
				case 20:iResult = (iEARFCN - 6150) + 7910; 		break;
				case 21:iResult = (iEARFCN - 6450) + 14959; 	break;
				case 22:iResult = (iEARFCN - 6600) + 35100;		break;
				case 23:iResult = (iEARFCN - 7500) + 21800; 	break;
				case 24:iResult = (iEARFCN - 7700) + 15250; 	break;
				case 25:iResult = (iEARFCN - 8040) + 19300; 	break;
				case 26:iResult = (iEARFCN - 8690) + 8590;		break;
				case 27:iResult = (iEARFCN - 9040) + 8520;		break;
				case 28:iResult = (iEARFCN - 9210) + 7580;		break;
				case 29:iResult = (iEARFCN - 9600) + 7160;		break;
			}
		} else { //上行
			int ulEARFCN = (DLEARFCN + 18000); //从下行频点转为上行频点后计算
			switch (Band)
			{
				case 1:	iResult = (ulEARFCN - 18000) + 19200; break;
				case 2: iResult = (ulEARFCN - 18600) + 18500; break;
				case 3: iResult = (ulEARFCN - 19200) + 17100; break;
				case 4: iResult = (ulEARFCN - 19950) + 17100; break;
				case 5: iResult = (ulEARFCN - 20400) + 8240; break;
				case 6: iResult = (ulEARFCN - 20650) + 8300; break;
				case 7: iResult = (ulEARFCN - 20750) + 25000; break;
				case 8: iResult = (ulEARFCN - 21450) + 8800; break;
				case 9: iResult = (ulEARFCN - 21800) + 17499; break;
				case 10:iResult = (ulEARFCN - 22150) + 17100; break;
				case 11:iResult = (ulEARFCN - 22750) + 14279; break;
				case 12:iResult = (ulEARFCN - 23010) + 6990; break;
				case 13:iResult = (ulEARFCN - 23180) + 7770; break;
				case 14:iResult = (ulEARFCN - 23280) + 7880; break;
				case 17:iResult = (ulEARFCN - 23730) + 7040; break;
				case 18:iResult = (ulEARFCN - 23850) + 8150; break;
				case 19:iResult = (ulEARFCN - 24000) + 8300; break;
				case 20:iResult = (ulEARFCN - 24150) + 8320; break;
				case 21:iResult = (ulEARFCN - 24450) + 14479; break;
				case 22:iResult = (ulEARFCN - 24600) + 34100; break;
				case 23:iResult = (ulEARFCN - 25500) + 20000; break;
				case 24:iResult = (ulEARFCN - 25700) + 16265; break;
				case 25:iResult = (ulEARFCN - 26040) + 18500; break;
				case 26:iResult = (ulEARFCN - 26690) + 8140; break;
				case 27:iResult = (ulEARFCN - 27040) + 8070; break;
				case 28:iResult = (ulEARFCN - 27210) + 7030; break;
			}
		}

		// 不分上下行
		switch (Band)
		{
			case 33:iResult = (iEARFCN - 36000) + 19000; break;
			case 34:iResult = (iEARFCN - 36200) + 20100; break;
			case 35:iResult = (iEARFCN - 36350) + 18500; break;
			case 36:iResult = (iEARFCN - 36950) + 19300; break;
			case 37:iResult = (iEARFCN - 37550) + 19100; break;
			case 38:iResult = (iEARFCN - 37750) + 25700; break;
			case 39:iResult = (iEARFCN - 38250) + 18800; break;
			case 40:iResult = (iEARFCN - 38650) + 23000; break;
			case 41:iResult = (iEARFCN - 39650) + 24960; break;
			case 42:iResult = (iEARFCN - 41590) + 34000; break;
			case 43:iResult = (iEARFCN - 43590) + 36000; break;
			case 44:iResult = (iEARFCN - 45590) + 7030; break;

			case 46:
				if (DLEARFCN > 50090)
					iResult = (iEARFCN - 50090) + 54800;
				else
					iResult = (iEARFCN - 46890) + 51600;
				break;

			case 60: // 60华为专网特别有
				if (DLEARFCN > 60236)
					iResult = (DLEARFCN - 60236) + 54800;
				else
					iResult = (DLEARFCN - 58236) + 51600;
				break;

			case 62:iResult = (DLEARFCN - 65200) + 17850; break;
		}

		return iResult;
	}

	int LTE_FREQ_To_DLEARFCN(double Frequency, int Band, boolean bDL) {
		int iResult = 0, iFreq = (int)(Frequency * 10);

		//下行
		if(bDL) {
			switch(Band) {
				case 1: iResult = (iFreq - 21100)*10 + 0; break;
				case 2: iResult = (iFreq - 19300)*10 + 6000; break;
				case 3: iResult = (iFreq - 18050)*10 + 12000; break;
				case 4: iResult = (iFreq - 21100)*10 + 19500; break;
				case 5: iResult = (iFreq - 8690)*10 + 24000; break;
				case 6: iResult = (iFreq - 8750)*10 + 26500; break;
				case 7: iResult = (iFreq - 26200)*10 + 27500; break;
				case 8: iResult = (iFreq - 9250)*10 + 34500; break;
				case 9: iResult = (iFreq - 18449)*10 + 38000; break;
				case 10:iResult = (iFreq - 21100)*10 + 41500; break;
				case 11:iResult = (iFreq - 14759)*10 + 47500; break;
				case 12:iResult = (iFreq - 7290)*10 + 50100; break;
				case 13:iResult = (iFreq - 7460)*10 + 51800; break;
				case 14:iResult = (iFreq - 7580)*10 + 52800; break;
				case 17:iResult = (iFreq - 7340)*10 + 57300; break;
				case 18:iResult = (iFreq - 8600)*10 + 58500; break;
				case 19:iResult = (iFreq - 8750)*10 + 60000; break;
				case 20:iResult = (iFreq - 7910)*10 + 61500; break;
				case 21:iResult = (iFreq - 14959)*10 + 64500; break;
				case 22:iResult = (iFreq - 35100)*10 + 66000; break;
				case 23:iResult = (iFreq - 21800)*10 + 75000; break;
				case 24:iResult = (iFreq - 15250)*10 + 77000; break;
				case 25:iResult = (iFreq - 19300)*10 + 80400; break;
				case 26:iResult = (iFreq - 8590)*10 + 86900; break;
				case 27:iResult = (iFreq - 8520)*10 + 90400; break;
				case 28:iResult = (iFreq - 7580)*10 + 92100; break;
				case 29:iResult = (iFreq - 7160)*10 + 96000; break;
			}
		} else { //上行
			switch(Band)
			{
				case 1: iResult = (iFreq - 19200)*10 + 180000; break;
				case 2: iResult = (iFreq - 18500)*10 + 186000; break;
				case 3: iResult = (iFreq - 17100)*10 + 192000; break;
				case 4: iResult = (iFreq - 17100)*10 + 199500; break;
				case 5: iResult = (iFreq - 8240)*10 + 204000; break;
				case 6: iResult = (iFreq - 8300)*10+206500; break;
				case 7: iResult = (iFreq - 25000)*10+207500; break;
				case 8: iResult = (iFreq - 8800)*10+214500; break;
				case 9: iResult = (iFreq - 17499)*10+218000; break;
				case 10:iResult = (iFreq - 17100)*10+221500; break;
				case 11:iResult = (iFreq - 14279)*10+227500; break;
				case 12:iResult = (iFreq - 6990)*10+230100; break;
				case 13:iResult = (iFreq - 7770)*10+231800; break;
				case 14:iResult = (iFreq - 7880)*10+232800; break;
				case 17:iResult = (iFreq - 7040)*10+237300; break;
				case 18:iResult = (iFreq - 8150)*10+238500; break;
				case 19:iResult = (iFreq - 8300)*10+240000; break;
				case 20:iResult = (iFreq - 8320)*10+241500; break;
				case 21:iResult = (iFreq - 14479)*10+244500; break;
				case 22:iResult = (iFreq - 34100)*10+246000; break;
				case 23:iResult = (iFreq - 20000)*10+255000; break;
				case 24:iResult = (iFreq - 16265)*10+257000; break;
				case 25:iResult = (iFreq - 18500)*10+260400; break;
				case 26:iResult = (iFreq - 8140)*10+266900; break;
				case 27:iResult = (iFreq - 8070)*10+270400; break;
				case 28:iResult = (iFreq - 7030)*10+272100; break;
			}
			iResult = iResult - 180000; //从上行频点转为下行频点
		}

		// 不分上下行
		switch (Band)
		{
			case 33:iResult = (iFreq - 19000)*10+360000; break;
			case 34:iResult = (iFreq - 20100)*10+362000; break;
			case 35:iResult = (iFreq - 18500)*10+363500; break;
			case 36:iResult = (iFreq - 19300)*10+369500; break;
			case 37:iResult = (iFreq - 19100)*10+375500; break;
			case 38:iResult = (iFreq - 25700)*10+377500; break;
			case 39:iResult = (iFreq - 18800)*10+382500; break;
			case 40:iResult = (iFreq - 23000)*10+386500; break;
			case 41:iResult = (iFreq - 24960)*10+396500; break;
			case 42:iResult = (iFreq - 34000)*10+415900; break;
			case 43:iResult = (iFreq - 36000)*10+435900; break;
			case 44:iResult = (iFreq - 7030)*10+445900; break;

			case 46:
				if (iFreq > 5480)
					iResult = (iFreq - 54800)*10+500900;
				else
					iResult = (iFreq - 51600)*10+468900;
				break;

			case 60: // 60华为专网特有
				if (iFreq > 5480)
					iResult = (iFreq - 54800)*10+602360;
				else
					iResult = (iFreq - 51600)*10+582360;
				break;

			case 62:iResult = (iFreq - 17850)*10 + 652000; break;
		}

		return iResult;
	}

	protected  void relationFrequency(final String netType, final View view, final EditText earfcnText, final EditText editDLFreq, final EditText editULFreq, final Spinner bandSpinner){
		if (netType.equals(Deviceinfo.NET_TYPES_GSM)) {
			TextView earfcnTxt = (TextView)view.findViewById(R.id.earfcn_txt);
			earfcnTxt.setText(R.string.gsm_servingBCCHARFCN);

			RelativeLayout relativeLayoutDLFreq = (RelativeLayout)view.findViewById(R.id.relativelayout_lock_freq_dl_freq);
			RelativeLayout relativeLayoutULFreq = (RelativeLayout)view.findViewById(R.id.relativelayout_lock_freq_ul_freq);
			relativeLayoutDLFreq.setVisibility(View.GONE);
			relativeLayoutULFreq.setVisibility(View.GONE);
		} else	if(netType.equals(Deviceinfo.NET_TYPES_WCDMA)){
			TextView earfcnTxt = (TextView)view.findViewById(R.id.earfcn_txt);
			earfcnTxt.setText(R.string.base_detail_uarfcn);

			RelativeLayout relativeLayoutDLFreq = (RelativeLayout)view.findViewById(R.id.relativelayout_lock_freq_dl_freq);
			RelativeLayout relativeLayoutULFreq = (RelativeLayout)view.findViewById(R.id.relativelayout_lock_freq_ul_freq);
			relativeLayoutDLFreq.setVisibility(View.GONE);
			relativeLayoutULFreq.setVisibility(View.GONE);
		}

		earfcnText.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String strBand = bandSpinner.getSelectedItem().toString();
					if (strBand.indexOf("Auto") >= 0) {
						new AlertDialog.Builder(context)
								.setTitle("Please select Band!")
								.setPositiveButton("OK", null)
								.show();
						return ;
					}
					int iPos = strBand.indexOf("(");
					if (iPos > 0)
						strBand = strBand.substring(0, iPos - 1);
					int iBand = Integer.parseInt(strBand);

					String strEARFCN = earfcnText.getText().toString();
					if (strEARFCN.length() > 0) {
						int iEARFCN = Integer.parseInt(strEARFCN);

						double dfDLFreq = LTE_DLEARFCN_To_Freq(iEARFCN, iBand, true);
						double dfULFreq = LTE_DLEARFCN_To_Freq(iEARFCN, iBand, false);

						editDLFreq.setText(Double.toString(dfDLFreq / 10));
						editULFreq.setText(Double.toString(dfULFreq / 10));
					}
				}
			}
		});

		editDLFreq.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String strBand = bandSpinner.getSelectedItem().toString();
					if (strBand.indexOf("Auto") >= 0) {
						new AlertDialog.Builder(context)
								.setTitle("Please select Band!")
								.setPositiveButton("OK", null)
								.show();
						return ;
					}
					int iPos = strBand.indexOf("(");
					if (iPos > 0)
						strBand = strBand.substring(0, iPos - 1);
					int iBand = Integer.parseInt(strBand);

					String strDFFreq = editDLFreq.getText().toString();
					if (strDFFreq.length() > 0) {
						double dfDLFreq = Double.parseDouble(strDFFreq);
						int iEARFCN = LTE_FREQ_To_DLEARFCN(dfDLFreq, iBand, true);
						double dfULFreq = LTE_DLEARFCN_To_Freq(iEARFCN / 10, iBand, false);

						earfcnText.setText(Integer.toString(iEARFCN / 10));
						editULFreq.setText(Double.toString(dfULFreq / 10));
					}
				}
			}
		});

		editULFreq.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					String strBand = bandSpinner.getSelectedItem().toString();
					if (strBand.indexOf("Auto") >= 0) {
						new AlertDialog.Builder(context)
								.setTitle("Please select Band!")
								.setPositiveButton("OK", null)
								.show();
						return ;
					}
					int iPos = strBand.indexOf("(");
					if (iPos > 0)
						strBand = strBand.substring(0, iPos - 1);
					int iBand = Integer.parseInt(strBand);

					String strULFreq = editULFreq.getText().toString();
					if (strULFreq.length() > 0) {
						double dfULFreq = Double.parseDouble(strULFreq);
						int iEARFCN = LTE_FREQ_To_DLEARFCN(dfULFreq, iBand, false);
						double dfDLFreq = LTE_DLEARFCN_To_Freq(iEARFCN / 10, iBand, true);

						earfcnText.setText(Integer.toString(iEARFCN / 10));
						editDLFreq.setText(Double.toString(dfDLFreq / 10));
					}
				}
			}
		});

		return ;
	}

	/**
	 * 显示频点对话框
	 *
	 * @param netType
	 *          网络类型
	 */
	protected void showFrequencySetDialog(final String netType) {
		LayoutInflater factory = LayoutInflater.from(this.context);
		final View view = factory.inflate(R.layout.lock_dev_frequency_edit, null);
		final Spinner bandSpinner = (Spinner) view.findViewById(R.id.band_select_edit);

		final EditText editEARFCN = (EditText) view.findViewById(R.id.earfcn_edit);
		final EditText editDLFreq = (EditText)view.findViewById(R.id.dl_freq_edit);
		final EditText editULFreq = (EditText)view.findViewById(R.id.ul_freq_edit);
		
		ArrayList<Band> bands = Band.getBandsByNetType(netType);
		if (bands != null) {
			ArrayAdapter<String> bandList = new ArrayAdapter<>(context,
					R.layout.simple_spinner_custom_layout,
					Band.bandArrayToNames(bands));

			bandList.setDropDownViewResource(R.layout.spinner_dropdown_item);
			bandSpinner.setAdapter(bandList);
		}
		
		String frequency = mForceMgr.getLockFrequency(context);
		if (bands != null && frequency.trim().length() > 0){
			String[] freqs = frequency.split(",");
			if(freqs.length == 3 && netType.equals(freqs[0])){
				for(int i = 0; i < bands.size(); i++){
					if(bands.get(i).toString().startsWith(freqs[1] + " (")){
						bandSpinner.setSelection(i);
						break;
					}
				}
				editEARFCN.setText(freqs[2]);
			}
		}
		relationFrequency(netType, view, editEARFCN, editDLFreq, editULFreq, bandSpinner);
		
		new BasicDialog.Builder(context).setTitle(netType).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String strBand = bandSpinner.getSelectedItem().toString();

						if (editDLFreq.hasFocus())
						{
							if (strBand.indexOf("Auto") >= 0) {
								new AlertDialog.Builder(context)
										.setTitle("Please select Band!")
										.setPositiveButton("OK", null)
										.show();
								return ;
							}
							int iPos = strBand.indexOf("(");
							if (iPos > 0)
								strBand = strBand.substring(0, iPos - 1);
							int iBand = Integer.parseInt(strBand);

							double dfDLFreq = Double.parseDouble(editDLFreq.getText().toString());
							int iEARFCN = LTE_FREQ_To_DLEARFCN(dfDLFreq, iBand, true);
							editEARFCN.setText(Integer.toString(iEARFCN));
						} else if (editULFreq.hasFocus()) {
							if (strBand.indexOf("Auto") >= 0) {
								new AlertDialog.Builder(context)
										.setTitle("Please select Band!")
										.setPositiveButton("OK", null)
										.show();
								return ;
							}
							int iPos = strBand.indexOf("(");
							if (iPos > 0)
								strBand = strBand.substring(0, iPos - 1);
							int iBand = Integer.parseInt(strBand);

							String strULFreq = editULFreq.getText().toString();
							if (strULFreq.length() > 0) {
								double dfULFreq = Double.parseDouble(strULFreq);
								int iEARFCN = LTE_FREQ_To_DLEARFCN(dfULFreq, iBand, false);

								editEARFCN.setText(Integer.toString(iEARFCN));
							}
						}

						String strFrequency = editEARFCN.getText().toString();
						if ((strFrequency.length() > 0) && (Verify.checknum(strFrequency)) && (Integer.parseInt(strFrequency) < 65535)) {
							int end = strBand.indexOf("(");
							if(end < 0)
								end = strBand.length();

							ForceNet forceNet = ForceNet.NET_LTE;
							if (netType.equals(Deviceinfo.NET_TYPES_WCDMA))
								forceNet = ForceNet.NET_WCDMA;
							else if (netType.equals(Deviceinfo.NET_TYPES_GSM))
								forceNet = ForceNet.NET_GSM;



							new LockFrequencyProgress(context, forceNet, callback, strBand.substring(0, end).trim(), strFrequency).execute();
						} else {
							String[] set = mForceMgr.getLockFrequency(context).split(",");
							if (set.length == 3 && set[0].equals(netType))
								editEARFCN.setText(set[2]);
							else
								editEARFCN.setText("");
							Toast.makeText(context, context.getString(R.string.sc_channels_Correct), Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}

	public void showNbIot() {
		LayoutInflater factory = LayoutInflater.from(this.context);
		final View view = factory.inflate(R.layout.lock_dev_frequency_edit, null);
		RelativeLayout relativeLayoutBand = (RelativeLayout)view.findViewById(R.id.relativelayout_lock_freq_band);
		relativeLayoutBand.setVisibility(View.GONE);

		final EditText editText = (EditText) view.findViewById(R.id.earfcn_edit);

		new BasicDialog.Builder(context).setTitle(R.string.locl_lock_point).setView(view)
				.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						String frequency = editText.getText().toString().trim();
						LogUtil.w("LockFrequencyDialog", "Freq: " + frequency);
						if (frequency.length() > 0 && Verify.checknum(frequency) && Integer.parseInt(frequency) < 65535) {
							new LockFrequencyProgress(context, ForceNet.NET_LTE, callback, frequency).execute();
						} else {
							Toast.makeText(context, context.getString(R.string.sc_channels_Correct), Toast.LENGTH_LONG).show();
						}
					}
				}).setNegativeButton(R.string.str_cancle).show();
	}
}
