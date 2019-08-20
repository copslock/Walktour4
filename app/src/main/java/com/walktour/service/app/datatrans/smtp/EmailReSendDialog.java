package com.walktour.service.app.datatrans.smtp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.framework.ui.BasicActivity;
import com.walktour.framework.ui.BasicDialog;
import com.walktour.gui.R;

/**
 * 邮件重发对话框
 * 
 * @author jianchao.wang
 *
 */
public class EmailReSendDialog extends BasicActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		final String[] fileNames = getIntent().getStringArrayExtra(SendMailReport.EXTRA_REPOET_FILES);
		TextView textView1 = new TextView(this);
		TextView textView2 = new TextView(this);
		textView1.setTextSize(metric.densityDpi / 160 * 7);
		textView2.setTextSize(metric.densityDpi / 160 * 5);
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);

		textView1.setText(R.string.email_send_faild);
		textView1.setTextColor(this.getResources().getColor(R.color.app_main_text_color));
		textView2.setText(R.string.check_account_and_network);
		textView2.setTextColor(this.getResources().getColor(R.color.app_main_text_color));
		layout.addView(textView1);
		layout.addView(textView2);
		BasicDialog.Builder mDialog = new BasicDialog.Builder(this).setTitle(R.string.str_tip)
				.setView(layout)
				.setPositiveButton(R.string.email_resend, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						SendMailReport sendMailReport = new SendMailReport();
						sendMailReport.sendReportMail(fileNames, EmailReSendDialog.this);
						dialog.dismiss();
						EmailReSendDialog.this.finish();
					}
				}).setNegativeButton(R.string.str_cancle, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						EmailReSendDialog.this.finish();

					}
				});
		mDialog.show();
	}
}
