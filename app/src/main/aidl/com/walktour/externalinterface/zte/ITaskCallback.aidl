package com.walktour.externalinterface.zte;


interface ITaskCallback {
   //scs通知wnl 单站任务开始，taskExtInfo：任务的扩展信息
   boolean notifyStartedTask(String taskExtInfo);

   //通知WNL任务完成列表，taskExtInfo：已完成任务的扩展信息数组
   boolean notifyFinishedTask(in String[] taskExtInfo);

   //通知WNL上传文件，taskFile：JSON格式，已完成任务生成的log文件信息
   boolean notifyUploadFile(String taskFile);

   //通知WNL取消上传
   void cancelUpload();

   //重置任务状态，taskId：待重置任务的扩展信息数组
   boolean resetTaskState(in String[] taskId);

   //获取NBiot设备号和卡号，返回形式：devicesId_cardId
   String getNBiotDevicesInfo();

}
