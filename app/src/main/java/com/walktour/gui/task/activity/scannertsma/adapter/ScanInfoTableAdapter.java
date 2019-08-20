package com.walktour.gui.task.activity.scannertsma.adapter;

import com.walktour.customView.tableview.TableAdapter;
import java.util.List;

public class ScanInfoTableAdapter implements TableAdapter {
    private List<String[]> contentList;

    public ScanInfoTableAdapter(List<String[]> contentList){
        this.contentList=contentList;
    }
    @Override
    public int getColumnCount() {
        return contentList.size();
    }

    @Override
    public String[] getColumnContent(int position) {
        return contentList.get(position);
    }
    public void upDateTable(List<String[]> contentList){
        this.contentList=contentList;
    }

//    //将第一行作为标题
//    private void firstRowAsTitle() {
//        //fields是表格中要显示的数据对应到Content类中的成员变量名，其定义顺序要与表格中显示的相同
//        final String[] fields = {"ResultBufferDepth", "ReceiverIndex", "FrontEndSelectionMask", "ValuePerSec", "DecodeOutputMode", "MeasurementMode"};
//        tableLayout.setAdapter(new TableAdapter() {
//            @Override
//            public int getColumnCount() {
//                return fields.length;
//            }
//
//            @Override
//            public String[] getColumnContent(int position) {
//                int rowCount = contentList.size();
//                String contents[] = new String[rowCount];
//                try {
//                    Field field = ScannerTSMAInfoActivity.Content.class.getDeclaredField(fields[position]);
//                    field.setAccessible(true);
//                    for (int i = 0; i < rowCount; i++) {
//                        contents[i] = (String) field.get(contentList.get(i));
//                    }
//                } catch (NoSuchFieldException e) {
//                    e.printStackTrace();
//                } catch (IllegalAccessException e) {
//                    e.printStackTrace();
//                }
//                return contents;
//            }
//        });
//    }

//    //将第一列作为标题
//    private void firstColumnAsTitle() {
//        tableLayout.setAdapter(new TableAdapter() {
//            @Override
//            public int getColumnCount() {
//                return contentList.size();
//            }
//
//            @Override
//            public String[] getColumnContent(int position) {
//                return contentList.get(position);
//            }
//        });
//    }
}
