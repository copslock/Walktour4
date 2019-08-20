//  Created by ZengJingzhao on 15-1-19.
//  Copyright (c) 2015年 ZengJingzhao. All rights reserved.
$.mobile.transitionFallbacks.slide = "none";
$.mobile.buttonMarkup.hoverDelay = "false";
//跳转
function goTo(page) {
	// showLoading();
	window.location.href = page;
}

//返回
function goBack() {
	window.history.go(-1);
}

//显示加载
function showLoading(){
	$.mobile.loading("show",{
		text: "加载中...",
		textVisible: true,
		theme: "a",
		textonly: false,
		html: ''
	});
}

//隐藏加载
function hideLoading(){
	$.mobile.loading( "hide" );
}

//错误信息
function errpic(thepic) {
	thepic.src = "../img/no_pic.png" 
}

//返回url参数数组
function getUrlParam(string) {  
    var obj =  new Array();  
	    if (string.indexOf("?") != -1) {  
	        var string = string.substr(string.indexOf("?") + 1); 
	        var strs = string.split("&");  
	        for(var i = 0; i < strs.length; i ++) {  
	            var tempArr = strs[i].split("=");  
	            obj[i] = tempArr[1];
	        }  
	    }  
	    return obj;  
}

//获取路径参数
function dlGetUrlParam(name) {
	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if (r != null) return unescape(r[2]); return null;
}


//测试
function dlTestUtil()
{
    alert('test');
}

//把秒格式化成时间
function dlSecToTime(ss)
{
	var s = (typeof ss == "string")? parseFloat(ss):ss;
	var t;
	if(s > -1){
    	var hour = Math.floor(s/3600);
    	var min = Math.floor(s/60) % 60;
    	var sec = s % 60;
    	var day = parseInt(hour / 24);
    	if (day > 0) {
        	hour = hour - 24 * day;
        	t = day + "day " + hour + ":";
        }
    	else {
    		t = hour + ":";  
    	} 
    	if(min < 10){
    		t += "0";
    	}
        t += min + ":";
    	if(sec < 10){
    		t += "0";
    	}
        t += sec.toFixed(0);
	}
	return t;
}

//把字符串转成百分比
function dlStrToPercent(value,returnDouble)
{
	var p = (typeof value == "string")? parseFloat(value):value;
	if (p <= 1) {
		p *= 100;
	}
	if (returnDouble) {
		return p;
	}
	return p.toFixed(1) + "%";
}

//构建json对象.安全检查入参的类型。如果是字符串进行转换，否则直接返回
function constructJson(abs)
{
    return (typeof abs == "string")?$.parseJSON(abs):abs;
}

//判断是否是数组
function isArray(o) {  
  return Object.prototype.toString.call(o) === '[object Array]';   
}

//转换数值
function dlTranslateValue(key,value)
{
	var m = key.toUpperCase();
	if (m.indexOf('SUCCESSRATE')>=0 || m.indexOf('DROPPEDRATE')>=0) {
		return '' + dlStrToPercent(value,true);
	}
	return value;
}

//格式化数字精度
function dlToFixed(value,num)
{
	if (value == null || value == 'undefined') {return '--'};
	var p = (typeof value == "string")? parseFloat(value):value;
	return p.toFixed(num);
}

//格式化数值
function dlFomatValue(k,v)
{
	if (value == 'NaN' || value == 'undefined') {
		return '--';
	}
	if (k.indexOf('TRANSFERRATE') >= 0) {
		return v + 'kbps';
	}
	if (k.indexOf('SUCCESSRATE')>=0 ) {
		return dlStrToPercent(v,false);
	}
	if (k.indexOf('DROPPEDRATE')>=0) {
		return dlStrToPercent(v,false);
	}
	if (k.indexOf('SAMPLECOVERAGERATE')>=0) {
		return dlStrToPercent(v,false);
	}
	return dlToFixed(v,1);
}

function showPopOver(list,target,evt) {
	var pid = 'popup-'+target.attr("id");
	if (evt) {
		var popup = '<div data-role="popup" id="'+pid+'" data-theme="a" data-arrow="true"></div>';
		// Create the popup.
		$(list).appendTo($(popup).appendTo($.mobile.activePage).popup()).listview();
		$('#'+pid).popup( "open",{ x: evt.pageX, y: evt.pageY } );
		evt.preventDefault();
	}
	else
	{
		var popup = '<div data-role="popup" id="'+pid+'" data-theme="a"></div>';
		// Create the popup.
		$(list).appendTo($(popup).appendTo($.mobile.activePage).popup()).listview();
		$('#'+pid).popup( "open" );
	}
	$(document).on("popupafterclose",".ui-popup",function(){
		$(this).remove();
	});
}

//显示数值，返回格式化后的字符串
//统一格式化显示的数值
function dlFormatDisplay(kpi,value,noUnit)
{
	if (value == null || value == 'NaN' || value == 'undefined') {
		return '--';
	}
	var name = false,alias = false;
	var pos = kpi.indexOf('_');
	if (pos != -1) {
		name = kpi.substring(0,pos).toUpperCase();
		alias = kpi.substring(pos+1).toUpperCase();
	}
	else
	{
		name = kpi;
	}
	var rtn = dlToFixed(value,1);
	//遍历字段名
	if (name == 'AVERAGE')
	{
		if (alias.indexOf('TRANSFERRATE') != -1) {
			if (noUnit) {
				rtn = dlToFixed(value,1);
			}else
			{
				rtn = dlToFixed(value,1) + 'KBps';
			}
			
		}
		else if (alias.indexOf('SPEED') != -1) {
			if (noUnit) {
				rtn = dlToFixed(value,1);
			}else
			{
				rtn = dlToFixed(value,1) + 'KBps';
			}
		}
		else if (alias.indexOf('DELAY') != -1) {
			if (noUnit) {
				rtn = dlToFixed(value,4);
			}else
			{
				rtn = dlToFixed(value,4) + 's';
			}
		}
	}
	else if (name == 'BUSINESSCOUNT')
	{
		rtn = dlToFixed(value,0);
	}
	else if (name == 'SUCCESSCOUNT')
	{
		rtn = dlToFixed(value,0);
	}
	else if (name == 'SUCCESSRATE')
	{
		if (noUnit) {
			rtn = dlStrToPercent(value,true).toFixed(1);
		}else
		{
			rtn = dlStrToPercent(value);
		}
	}
	else if (name == 'DROPPEDRATE')
	{
		if (noUnit) {
			rtn = dlStrToPercent(value,true).toFixed(1);
		}else
		{
			rtn = dlStrToPercent(value);
		}
	}
	else if (name == 'BUSINESSDELAY')
	{
		if (noUnit) {
			rtn = dlToFixed(value,3);
		}else
		{
			rtn = dlToFixed(value,3) + 's';
		}
	}
	else if (name == 'TRANSFERSIZE')
	{
		if (noUnit) {
			rtn = dlToFixed(value,1);
		}else
		{
			rtn = dlToFixed(value,1) + 'KB';
		}
	}
	else if (name == 'TRANSFERTIME')
	{
		rtn = dlSecToTime(value);
	}
	else if (name == 'TRANSFERRATE') {
		if (noUnit) {
			rtn = dlToFixed(value,1);
		}else
		{
			rtn = dlToFixed(value,1) + 'KBps';
		}
	}
	return rtn;
}

function dlGetSumValue(json,param_biz,field)
{
	
}

function chechAvaliable(bizJson)
{
	var total = bizJson['BUSINESSCOUNT'];
	if (!total) {
		total = bizJson['BUSINESSCOUNT_ATTEMPTS'];
	}
	if (!total) {
		return false;
	}
	var num = parseInt(total);
	if (num < 1) {
		return false;
	}
	return true;
}

