package com.walktour.gui.about;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.walktour.Utils.ApplicationModel;
import com.walktour.Utils.BuildPower;
import com.walktour.gui.R;
import com.walktour.gui.about.tableview.LicenseTableView;

import java.util.LinkedList;
import java.util.List;

public class LicenseViewActivity extends Activity {
    boolean isBusinessExpand = true;//默认展开
    boolean isNetExpand = true;//默认展开
    boolean isModelExpand = true;//默认展开
    boolean isSpecialExpand = true;//默认展开
     //业务类所包含的权限ID
    List<Integer> businssKey=new LinkedList<>();
    //网络类所包含的权限ID
    List<Integer> netKey=new LinkedList<>();
    //方式类所包含的权限ID
    List<Integer> modelKey=new LinkedList<>();
    //专项类所包含的权限ID
    List<Integer> specialKey=new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license_view);
        ImageButton imageButton = (ImageButton) this.findViewById(R.id.pointer);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initNetTypes();
        showBusinessType();
        showNetType();
        showModeType();
        showSpecialType();
    }
    /***
     * 网络类权限ID
     */
    private void initNetTypes() {
        this.initBusinessKeys();
        this.initModelKeys();
        this.initNetKeys();
        this.initSpecialKeys();
    }

    private void showBusinessType() {
        //业务类
        final LinearLayout licenseBusiness = (LinearLayout) this.findViewById(R.id.licensebusiness);
        TextView licensetype = (TextView) licenseBusiness.findViewById(R.id.licensetype);
        final LinearLayout licensetypeheader = (LinearLayout) licenseBusiness.findViewById(R.id.licenestypeheader);
        final LinearLayout licensekeylayout = (LinearLayout) licenseBusiness.findViewById(R.id.licensekeylayout);
        licensetypeheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBusinessExpand)
                    licensekeylayout.setVisibility(View.GONE);
                else
                    licensekeylayout.setVisibility(View.VISIBLE);
                isBusinessExpand = !isBusinessExpand;
            }
        });
        licensetype.setText(getString(R.string.license_type_business));
        List<String> license= getLicenseString(businssKey);
        int numberOfColumn = 2;
//        int cellDimension = 24;
//        int cellPadding = 10;
        for (int i = 0; i < license.size(); ) {
            LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_license_table_view, null);
            LicenseTableView row_column1 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column1);
            LicenseTableView row_column2 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column2);
            LicenseTableView row_column3 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column3);
            row_column1.setVisibility(View.GONE);
            row_column2.setVisibility(View.GONE);
            row_column3.setVisibility(View.GONE);
            for (int column = 0; column < numberOfColumn; column++) {
                switch (column) {
                    case 0:
                        row_column1.setVisibility(View.VISIBLE);
                        row_column1.setText(license.get(i));
                        break;
                    case 1:
                        row_column2.setVisibility(View.VISIBLE);
                        row_column2.setText(license.get(i));
                        break;
                    case 2:
                        row_column3.setVisibility(View.VISIBLE);
                        row_column3.setText(license.get(i));
                        break;
                }
                i = i + 1;
                if (i >= license.size())
                    break;
            }
            licensekeylayout.addView(relativeLayout);
        }
    }
    private void showNetType() {
        //网络类
        final LinearLayout licensenet = (LinearLayout) this.findViewById(R.id.licensenet);
        TextView licensetype = (TextView) licensenet.findViewById(R.id.licensetype);
        final LinearLayout licensetypeheader = (LinearLayout) licensenet.findViewById(R.id.licenestypeheader);
        final LinearLayout licensekeylayout = (LinearLayout) licensenet.findViewById(R.id.licensekeylayout);
        licensetypeheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetExpand)
                    licensekeylayout.setVisibility(View.GONE);
                else
                    licensekeylayout.setVisibility(View.VISIBLE);
                isNetExpand = !isNetExpand;
            }
        });
        licensetype.setText(getString(R.string.license_type_net));
        List<String> license= getLicenseString(netKey);
        int numberOfColumn = 2;
//        int cellDimension = 24;
//        int cellPadding = 10;
        for (int i = 0; i < license.size(); ) {
            LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_license_table_view, null);
            LicenseTableView row_column1 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column1);
            LicenseTableView row_column2 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column2);
            LicenseTableView row_column3 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column3);
            row_column1.setVisibility(View.GONE);
            row_column2.setVisibility(View.GONE);
            row_column3.setVisibility(View.GONE);
            for (int column = 0; column < numberOfColumn; column++) {
                switch (column) {
                    case 0:
                        row_column1.setVisibility(View.VISIBLE);
                        row_column1.setText(license.get(i));
                        break;
                    case 1:
                        row_column2.setVisibility(View.VISIBLE);
                        row_column2.setText(license.get(i));
                        break;
                    case 2:
                        row_column3.setVisibility(View.VISIBLE);
                        row_column3.setText(license.get(i));
                        break;
                }
                i = i + 1;
                if (i >= license.size())
                    break;
            }
            licensekeylayout.addView(relativeLayout);
        }
    }
    private void showModeType() {
        //方式类
        final LinearLayout licensemode = (LinearLayout) this.findViewById(R.id.licensemode);
        TextView licensetype = (TextView) licensemode.findViewById(R.id.licensetype);
        final LinearLayout licensetypeheader = (LinearLayout) licensemode.findViewById(R.id.licenestypeheader);
        final LinearLayout licensekeylayout = (LinearLayout) licensemode.findViewById(R.id.licensekeylayout);
        licensetypeheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isModelExpand)
                    licensekeylayout.setVisibility(View.GONE);
                else
                    licensekeylayout.setVisibility(View.VISIBLE);
                isModelExpand = !isModelExpand;
            }
        });
        licensetype.setText(getString(R.string.license_type_mode));
        List<String> license= getLicenseString(modelKey);
        int numberOfColumn = 2;
//        int cellDimension = 24;
//        int cellPadding = 10;
        for (int i = 0; i < license.size(); ) {
            LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_license_table_view, null);
            LicenseTableView row_column1 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column1);
            LicenseTableView row_column2 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column2);
            LicenseTableView row_column3 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column3);
            row_column1.setVisibility(View.GONE);
            row_column2.setVisibility(View.GONE);
            row_column3.setVisibility(View.GONE);
            for (int column = 0; column < numberOfColumn; column++) {
                switch (column) {
                    case 0:
                        row_column1.setVisibility(View.VISIBLE);
                        row_column1.setText(license.get(i));
                        break;
                    case 1:
                        row_column2.setVisibility(View.VISIBLE);
                        row_column2.setText(license.get(i));
                        break;
                    case 2:
                        row_column3.setVisibility(View.VISIBLE);
                        row_column3.setText(license.get(i));
                        break;
                }
                i = i + 1;
                if (i >= license.size())
                    break;
            }
            licensekeylayout.addView(relativeLayout);
        }
    }
    private void showSpecialType() {
        //专项类
        final LinearLayout licensespecial = (LinearLayout) this.findViewById(R.id.licensespecial);
        TextView licensetype = (TextView) licensespecial.findViewById(R.id.licensetype);
        final LinearLayout licensetypeheader = (LinearLayout) licensespecial.findViewById(R.id.licenestypeheader);
        final LinearLayout licensekeylayout = (LinearLayout) licensespecial.findViewById(R.id.licensekeylayout);
        licensetypeheader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSpecialExpand)
                    licensekeylayout.setVisibility(View.GONE);
                else
                    licensekeylayout.setVisibility(View.VISIBLE);
                isSpecialExpand = !isSpecialExpand;
            }
        });
        licensetype.setText(getString(R.string.license_type_special));
        List<String> license= getLicenseString(specialKey);
        int numberOfColumn = 2;
//        int cellDimension = 24;
//        int cellPadding = 10;
        for (int i = 0; i < license.size(); ) {
            LinearLayout relativeLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.activity_license_table_view, null);
            LicenseTableView row_column1 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column1);
            LicenseTableView row_column2 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column2);
            LicenseTableView row_column3 = (LicenseTableView) relativeLayout.findViewById(R.id.row_column3);
            row_column1.setVisibility(View.GONE);
            row_column2.setVisibility(View.GONE);
            row_column3.setVisibility(View.GONE);
            for (int column = 0; column < numberOfColumn; column++) {
                switch (column) {
                    case 0:
                        row_column1.setVisibility(View.VISIBLE);
                        row_column1.setText(license.get(i));
                        break;
                    case 1:
                        row_column2.setVisibility(View.VISIBLE);
                        row_column2.setText(license.get(i));
                        break;
                    case 2:
                        row_column3.setVisibility(View.VISIBLE);
                        row_column3.setText(license.get(i));
                        break;
                }
                i = i + 1;
                if (i >= license.size())
                    break;
            }
            licensekeylayout.addView(relativeLayout);
        }
    }
    /***
     * 初始化业务类所包含的key
     */
    private void initBusinessKeys() {
        businssKey.clear();
        businssKey.add(40);
        businssKey.add(13);
        businssKey.add(14);
        businssKey.add(15);
        businssKey.add(16);
        businssKey.add(17);
        businssKey.add(18);
        businssKey.add(19);
        businssKey.add(20);
        businssKey.add(21);
        businssKey.add(22);
        businssKey.add(23);
        businssKey.add(24);
        businssKey.add(31);
        businssKey.add(35);
        businssKey.add(37);
        businssKey.add(38);
        businssKey.add(39);
        businssKey.add(41);
        businssKey.add(44);
        businssKey.add(45);
        businssKey.add(73);
        businssKey.add(74);
        businssKey.add(80);
        businssKey.add(25);
        businssKey.add(26);
        businssKey.add(28);
        businssKey.add(29);
        businssKey.add(32);
        businssKey.add(34);
        businssKey.add(36);
        businssKey.add(43);
        businssKey.add(46);
        businssKey.add(48);
        businssKey.add(42);
        businssKey.add(102);
        businssKey.add(113);
        businssKey.add(114);
        businssKey.add(115);
        businssKey.add(116);
        businssKey.add(117);
        businssKey.add(123);
        businssKey.add(126);
        businssKey.add(127);
        businssKey.add(128);
        businssKey.add(129);
        businssKey.add(131);
        businssKey.add(132);
        businssKey.add(133);
        businssKey.add(134);
        businssKey.add(135);
        businssKey.add(136);
        businssKey.add(137);
        businssKey.add(138);
        businssKey.add(139);
        businssKey.add(140);
    }
    /***
     * 初始化网络类所包含的key
     */
    private void initNetKeys() {
        netKey.clear();
        netKey.add(3);
        netKey.add(4);
        netKey.add(5);
        netKey.add(52);
        netKey.add(53);
        netKey.add(55);
        netKey.add(51);
        netKey.add(7);
        netKey.add(93);
        netKey.add(104);
        netKey.add(105);
        netKey.add(106);
        netKey.add(107);
        netKey.add(122);
        netKey.add(130);
        netKey.add(141);
    }
    /***
     * 初始化方式类所包含的key
     */
    private void initModelKeys() {
        modelKey.clear();
        modelKey.add(1);
        modelKey.add(2);
        modelKey.add(110);
        modelKey.add(91);
        modelKey.add(54);
        modelKey.add(60);
        modelKey.add(61);
        modelKey.add(62);
        modelKey.add(63);
        modelKey.add(64);
        modelKey.add(65);
        modelKey.add(71);
        modelKey.add(112);
        modelKey.add(109);
        modelKey.add(108);
    }
    /***
     * 初始化专项类所包含的key
     */
    private void initSpecialKeys() {
        specialKey.clear();
        specialKey.add(9);
        specialKey.add(10);
        specialKey.add(66);
        specialKey.add(67);
        specialKey.add(68);
        specialKey.add(75);
        specialKey.add(76);
        specialKey.add(77);
        specialKey.add(103);
        specialKey.add(111);
        specialKey.add(79);
        specialKey.add(81);
        specialKey.add(95);
        specialKey.add(96);
        specialKey.add(121);
        specialKey.add(85);
        specialKey.add(119);
        specialKey.add(94);
        specialKey.add(122);
        specialKey.add(124);
        specialKey.add(125);
    }
    /**
     * 权限
     *
     * @param map 权限映射
     * @return 权限名称
     */
    private List<String> getLicenseString(List<Integer> lists) {
        List<String> licc = new LinkedList<>();
        for (Integer integer : ApplicationModel.getInstance().getLicenseKeyIDS()) {

            if(lists.contains(integer)){
                BuildPower.PowerList power=BuildPower.PowerList.getPowerById(integer);
                if(BuildPower.PowerList.UnKnown!=power){
                    licc.add(power.getPowerStr());
                }
            }
        }
        return licc;
    }
}
