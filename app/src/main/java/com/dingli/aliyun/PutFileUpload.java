package com.dingli.aliyun;

import android.content.Context;
import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.walktour.control.bean.MyPhoneState;

//import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;

public class PutFileUpload extends AliYunUpload {

	@Override
	public void fileUpload(Context context, String filePath) {
		// TODO Auto-generated method stub
		
		uploadKey += MyPhoneState.getInstance().getMyDeviceId(context);
		// 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
		OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
		OSS oss = new OSSClient(context, endpoint, credentialProvider);
		
		// 构造上传请求
		PutObjectRequest put = new PutObjectRequest(bucketName, uploadKey, filePath);
		// 异步上传时可以设置进度回调
		put.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
		    @Override
		    public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
		        Log.d("PutObject", "currentSize: " + currentSize + " totalSize: " + totalSize);
		    }
		});
		
		//OSSAsyncTask task = 
		oss.asyncPutObject(put, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
		    @Override
		    public void onSuccess(PutObjectRequest request, PutObjectResult result) {
		        Log.d("PutObject", "UploadSuccess");
		    }

		    @Override
		    public void onFailure(PutObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
		        // 请求异常
		        if (clientExcepion != null) {
		            // 本地异常如网络异常等
		            clientExcepion.printStackTrace();
		        }
		        if (serviceException != null) {
		            // 服务异常
		            Log.e("ErrorCode", serviceException.getErrorCode());
		            Log.e("RequestId", serviceException.getRequestId());
		            Log.e("HostId", serviceException.getHostId());
		            Log.e("RawMessage", serviceException.getRawMessage());
		        }
		    }
		});
	
		// task.cancel(); // 可以取消任务
		// task.waitUntilFinished(); // 可以等待直到任务完成
	}
}
