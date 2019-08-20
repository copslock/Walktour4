/*
 * 文件名: ControlPanelLinstener.java
 * 版    权：  Copyright Dingli Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: 黄广府
 * 创建时间:2013-9-13
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.dinglicom.dataset.logic;

/**
 * [一句话功能简述]<BR>
 * [功能详细描述]
 * @author 黄广府
 * @version [WalkTour Client V100R001C03, 2013-9-13] 
 */
public interface ControlPanelLinstener {
    
    /**
     * 播放<BR>
     * [功能详细描述]
     * @return
     */
	public boolean onPlay(int pointIndexTotal);
    
    /**
     * 停止<BR>
     * [功能详细描述]
     * @return
     */
    public boolean onStop();
    
    /**
     * 暂停<BR>
     * [功能详细描述]
     * @return
     */
    public boolean onPasue();
    
    /**
     * 快进<BR>
     * [功能详细描述]
     * @return
     */
    public boolean onFastForward(int speed);
    
    /**
     * 快退<BR>
     * [功能详细描述]
     * @return
     */
    public boolean onRewind(int speed);
    
    /**
     * 下一个<BR>
     * [功能详细描述]
     * @return
     */
    public boolean onNext();
    
    
    /**
     * 上一个<BR>
     * [功能详细描述]
     * @return
     */
    public boolean onUp();
    
    /**
     * 更新进度<BR>
     * [功能详细描述]
     * @param progress
     */
    public void onSeekBar(int progress);



    /**
     * 更新进度<BR>
     * 时间为单位
     * @param progressTime
     */
    public void onSeekBar(long progressTime,int progress);
    
}
