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
import com.alibaba.sdk.android.oss.model.AppendObjectRequest;
import com.alibaba.sdk.android.oss.model.AppendObjectResult;
import com.alibaba.sdk.android.oss.model.GetObjectRequest;
import com.alibaba.sdk.android.oss.model.GetObjectResult;
import com.walktour.control.bean.MyPhoneState;

//import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;

public class AppendFileUpload extends AliYunUpload {

	@Override
	public void fileUpload(Context context,String filePath) {
		// TODO Auto-generated method stub
		uploadKey += MyPhoneState.getInstance().getMyDeviceId(context);

		// 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考后面的`访问控制`章节
		OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(accessKeyId, accessKeySecret);
		OSS oss = new OSSClient(context, endpoint, credentialProvider);
		
		try{
			GetObjectRequest request = new GetObjectRequest(bucketName, uploadKey);
			GetObjectResult objResult = oss.getObject(request);
			
			if(objResult != null){
				Log.w(TAG,"--objResult:" + objResult.getContentLength());
				if(nextAppendPosition == 0l){
					nextAppendPosition = objResult.getContentLength();
				}
				
				objResult.getObjectContent().close();
			}
		
		}catch(Exception e){
			Log.w(TAG,"fileUpload G",e);
		}
		
		try{
			// 构造上传请求
			AppendObjectRequest apend = new AppendObjectRequest(bucketName, uploadKey, filePath);
			apend.setPosition(nextAppendPosition);
			
			
			apend.setProgressCallback(new OSSProgressCallback<AppendObjectRequest>() {
				@Override
			    public void onProgress(AppendObjectRequest request, long currentSize, long totalSize) {
			        Log.d("PutObject", "currentSize: " + currentSize + " : " + totalSize);
			    }
			});
			
	
			//OSSAsyncTask task =	//此处定义的对 
			oss.asyncAppendObject(apend, new OSSCompletedCallback<AppendObjectRequest, AppendObjectResult>() {
			    @Override
			    public void onSuccess(AppendObjectRequest request, AppendObjectResult result) {
			        nextAppendPosition = result.getNextPosition();
			        Log.d("PutObject", "UploadSuccess nextp:" + nextAppendPosition);
			    }
	
			    @Override
			    public void onFailure(AppendObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
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
		}catch(Exception e){
			Log.w(TAG,"fileUpload U",e);
		}
	}

}
