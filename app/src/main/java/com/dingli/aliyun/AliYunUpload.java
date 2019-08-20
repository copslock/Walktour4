package com.dingli.aliyun;

import android.content.Context;

public abstract class AliYunUpload {
	protected final String TAG 				= "AliYunUpload";
	protected final String endpoint 			= "oss-cn-shanghai.aliyuncs.com";
	protected final String accessKeyId 		= "LTAIrF5dFZa7rOPM";
	protected final String accessKeySecret 	= "M1u3zg1bNR17LpKiFDmQmi6VjuUDQk";
	protected final String bucketName 		= "android-cloud";
	protected String uploadKey					= "Android-";
	protected long nextAppendPosition = 0l;
	
	public abstract void fileUpload(Context context,String filePath);
}
