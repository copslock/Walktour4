package com.walktour.gui.mos;

import com.walktour.service.bluetoothmos.command.BaseCommand;

/**
 * 算分结果,主要存放到Excel表格里面
 *
 * @author zhicheng.chen
 * @date 2019/3/22
 */
public class MosResult {
    public String name;
    public BaseCommand.FileType type;
    public String mark;
    public String[] scores;
}
