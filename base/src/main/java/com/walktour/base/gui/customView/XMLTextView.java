package com.walktour.base.gui.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.walktour.base.util.LogUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 重新格式化xml字符串，
 * 并显示出xml格式的在文本里，
 * 记住，一定要遵守XML格式才可以。
 * @author jinfeng.xie
 * @data 2019/3/18
 */
@SuppressLint("AppCompatCustomView")
public class XMLTextView extends TextView {
    private XmlPullParserFactory factory=null;
    private XmlPullParser xmlParser=null;

    public XMLTextView(Context context) {
        this(context,null,0);
    }

    public XMLTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public XMLTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    @Override
    public void setText(CharSequence text, BufferType type) {
        String s = parserString(text);
        super.setText(s, type);


    }
    private String parserString(CharSequence text){
        if (text.toString().equals("")){
            return "";
        }
        if (factory==null){
            try {
                factory = XmlPullParserFactory.newInstance();
                xmlParser = factory.newPullParser();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        String returnStr=new String();
        try {
        XMLTagBean xmlTagBean=null;
        xmlParser.setInput(new StringReader((String) text));
        int eventType = xmlParser.getEventType();
        List<XMLTagBean> tags = new ArrayList<>();
        int level = 0;//标签等级
        while ((eventType = xmlParser.next()) != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG:
                    level += 1;
                    xmlTagBean = new XMLTagBean();
                    xmlTagBean.setTag(xmlParser.getName());
                    xmlTagBean.setLevel(level);
                    xmlTagBean.setType(XmlPullParser.START_TAG);
                    try {
                        xmlTagBean.setValue(xmlParser.nextText());
                    } catch (Exception e) {
                        xmlTagBean.setValue("");
                    }
                    tags.add(xmlTagBean);
                    break;
                case XmlPullParser.END_TAG:
                    for(int i=0;i<tags.size();i++){
                        if (xmlParser.getName().equals(tags.get(i).getTag())){
                            level -= 1;
                            break;
                        }
                    }
                    if (xmlTagBean!=null&&xmlTagBean.getType()== XmlPullParser.TEXT&&xmlTagBean.getTag()==null&&!xmlTagBean.getValue().equals("")){
                        xmlTagBean.setTag(xmlParser.getName());
//                        xmlTagBean.setValue(xmlParser.getText());
                        xmlTagBean.setLevel(level);
                        xmlTagBean.setType(XmlPullParser.START_TAG);
                        tags.add(xmlTagBean);
                        break;
                    }
                    xmlTagBean = new XMLTagBean();
                    xmlTagBean.setTag(xmlParser.getName());
                    xmlTagBean.setLevel(level);
                    xmlTagBean.setType(XmlPullParser.END_TAG);
                    xmlTagBean.setValue("");
                    tags.add(xmlTagBean);
                    break;
                case XmlPullParser.COMMENT:
                    LogUtil.d("max", "COMMENT:");
                    break;
                case XmlPullParser.CDSECT:
                    LogUtil.d("max", "CDSECT:");
                    break;
                case XmlPullParser.DOCDECL:
                    LogUtil.d("max", "DOCDECL:");
                    break;
                case XmlPullParser.TEXT:
                        xmlTagBean=new XMLTagBean();
                        xmlTagBean.setTag(xmlParser.getName());
                        xmlTagBean.setValue(xmlParser.getText());
                        xmlTagBean.setType(XmlPullParser.TEXT);
//                    tags.add(xmlTagBean);
                    break;
                case XmlPullParser.ENTITY_REF:
                    LogUtil.d("max", "ENTITY_REF:");
                    break;
                case XmlPullParser.START_DOCUMENT:
                    LogUtil.d("max", "TEXSTART_DOCUMENTT:");
                    break;
                case XmlPullParser.IGNORABLE_WHITESPACE:
                    LogUtil.d("max", "IGNORABLE_WHITESPACE:");
                    break;
                case XmlPullParser.PROCESSING_INSTRUCTION:
                    LogUtil.d("max", "PROCESSING_INSTRUCTION:");
                    break;
                case XmlPullParser.END_DOCUMENT:
                    LogUtil.d("max", "END_DOCUMENT:");

            }
        }
        LogUtil.d("max", "tags:" + tags);
        for (int i=0;i<tags.size();i++){
            XMLTagBean tag = tags.get(i);
            for (int j=0;j<tag.getLevel();j++){
                returnStr+=("\t\t");
            }
            switch (tag.getType()){
                case XmlPullParser.START_TAG:
                    if("".equals(tag.getValue())){
                        returnStr+=("<"+tag.getTag()+">"+tag.getValue());
                    }else {
                        returnStr+=("<"+tag.getTag()+">"+tag.getValue()+"</"+tag.getTag()+">");
                    }
                    break;
                case XmlPullParser.END_TAG:
                    returnStr+=("</"+tag.getTag()+">"+tag.getValue());
                    break;
                case XmlPullParser.TEXT:
//                    returnStr+=("<"+tag.getTag()+">"+tag.getValue()+"</"+tag.getTag()+">");
                    break;
                    default:
                        break;
            }

            returnStr+=("\n");

        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnStr;
    }
}
