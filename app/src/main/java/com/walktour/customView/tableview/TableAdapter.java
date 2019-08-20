package com.walktour.customView.tableview;

/**
 * @author Max
 * @data 2019/06/13
 */
public interface TableAdapter {

    int getColumnCount();

    String[] getColumnContent(int position);

}
