//  Created by ZengJingzhao on 15-1-19.
//  Copyright (c) 2015年 ZengJingzhao. All rights reserved.
//使用此文件前必须引入 dlUtils.js

/*
用户设置
从原生 app 传入用户设置信息，包括 go or no go 设置和 参数 阈值 分段设置
*/
//配置信息, setter

var dlSystemSetting = {};

function dlLoadSetting (key,p) {
	dlSystemSetting[key] = constructJson(p);
}

//配置信息, getter
function dlGetSetting (key) {
	// body...
	return dlSystemSetting[key];
}

/*  设置样式demo
// go or no go setting json  // key is 'DLGoOrNoGoSetting'
{
	"DefaultColor":"#ff0000",
	"FTP_Download":[
		{
			"paramter":"",
			"condiction":"",
		},
		{
			"paramter":"",
			"condiction":"",
		},
		{
			"paramter":"",
			"condiction":"",
		},
	],
	"FTP_Upload":[
		{
			"paramter":"",
			"condiction":"",
		},
		{
			"paramter":"",
			"condiction":"",
		},
		{
			"paramter":"",
			"condiction":"",
		},
	],
	"LTE":[
		{
			"paramter":"",
			"condiction":"",
		},
		{
			"paramter":"",
			"condiction":"",
		},
		{
			"paramter":"",
			"condiction":"",
		},
	],
}
*/
/*
// threshold // key is 'DLThresholdSetting'
{
	"RxLevelFull":[
		{"range":"","color":""},
		{"range":"","color":""},
		{"range":"","color":""},
		{"range":"","color":""},
		{"range":"","color":""},
	],
}
*/

//判断数值是否满足条件
function GoOrNoGoMatch(con,value)
{
	//获取条件中的数值
	var result = con.match(/-?\d+\.*\d*/g);
	result = result[result.length-1];
	if (con.indexOf(">=") >= 0) {
		return (parseFloat(value) >= parseFloat(result));
	}
	else if(con.indexOf(">") >= 0)
	{
		return (parseFloat(value) > parseFloat(result));
	}
	else if(con.indexOf("<=") >= 0)
	{
		return (parseFloat(value) <= parseFloat(result));
	}
	else if(con.indexOf("<") >= 0)
	{
		return (parseFloat(value) < parseFloat(result));
	}
	else if(con.indexOf("=") >= 0)
	{
		return (parseFloat(value) == parseFloat(result));
	}
	return true;
}

//加载goornogo 参数数值
function innerHtmlForGoOrNoGo(group,paramter,value)
{
	var goORNoGo = dlGetSetting("DLGoOrNoGoSetting");
	var settings = goORNoGo[group];

	var color = goORNoGo.Warning?goORNoGo.Warning:"#FF0000";
	for (var i = 0; i < settings.length; i++) {
		var p = settings[i];
		if (p.paramter == paramter) {
			if (!GoOrNoGoMatch(p.condiction ,value)) {
				return "<font color='" + color +"'>" + value + "</font>";
			}
			break;
		}
	}
	return value;
}

function htmlContnetWithGoNoGo(k,value,con,color)
{
	if (value == null || value === '') {
		return '--';
	}
	var n = dlTranslateValue(k,value);
	if (!GoOrNoGoMatch(con ,n)) {
		return "<font color='" + color +"'>" + dlFormatDisplay(k,value,true) + "</font>";
	}
	return dlFormatDisplay(k,value,true);
}

//获取分段配色
function getThreholdColor(parmater,range)
{
	var settings = dlGetSetting("DLThresholdSetting");
	var thresholds = settings[paramter];
	for (var i = thresholds.length - 1; i >= 0; i--) {
		if (thresholds[i].range == range) {
			return thresholds[i].color;
		}
	}
	return false;
}

//========================================================================
//格式化返回的结果。如果是按场景查询的，那么就把结果转为以场景分组的数据，否则直接返回
function formatJsonResponse(json)
{
	var isBylog = false;
	var patten = '<' + DLByLogSum + '>';
	for (var key in json) {
		if (key.indexOf(patten) != -1) {
			isBylog = true;
			break;
		}
	}
	var resault = json;
	//先按场景归类
  	var flag = false;
  	var temp = {};
    var arr = false;
    for( var key in json){
     	arr = key.split('>_');
     	if (arr.length == 2) {
      		flag = true;
        	var g = arr[0].substring(1);
        	var buf = temp[g];
        	if (!buf) {
        		buf = {};
        		temp[g] = buf;
        	}
        	buf[arr[1]] = json[key];
     	}
    }
    if (flag) {
    	//解析场景名称
    	var data = {};
    	for (var key in temp) {
    		arr = key.split('#');
    		var building = arr[0];
    		var bInfo = data[building];
    		if (!bInfo) {
    			bInfo = {};
    			data[building] = bInfo;
    		}
    		if (arr.length == 2) {
    			var floors = bInfo.floors;
    			if (!floors) {
    				floors = {};
    				bInfo.floors = floors;
    			}
    			floors[arr[1]] = temp[key];
    		}
    		else
    		{
    			bInfo.base = temp[key];
    		}
    	}
    	resault = {'isBylog':isBylog,'data':data};
    }
    else{
    	resault = {'isBylog':isBylog,'data':resault};
    }
 	return resault;
}

//单场景，去掉场景名
function removeSenceJsonResponse(json,scnce)
{
  	//先按场景归类
  	var temp = {};
  	var arr = false;
  	for( var key in json){
  		if(scnce == null || key.indexOf('<' + scnce + ">") != -1){
  			alert(scnce);
    	arr = key.split('>_');
    	if (arr.length == 2) {
        	temp[arr[1]] = json[key];
      	}
      	else
      	{
      		temp[key] = json[key];
      	}
      }	
    }
  	return temp;
}

//通过业务名获取设置
function dlGetGoOrNogoSettingWithBusinessName(n)
{
	var real = n;
	for (var key in DLGoNoGoMatchBusinessNames) {
		var map = DLGoNoGoMatchBusinessNames[key];
		for (var i = 0;i < map.length; i++) {
			if (n == map[i]) {
				real = key;
				break;
			}
		}
	}
	var dlGoNogoSetting = dlGetSetting('DLGoNogoSetting');
	if (!dlGoNogoSetting) {
		dlGoNogoSetting = TestGoroNogoSetting;
	}
	return dlGoNogoSetting[real];
}


//从数据中检索goNogo设置的相关数据
//json 要检索的数据
//findSome function(name,data){};
function dlSearchGoOrNogoSettingWithInData(json,findSome)
{
	var dlGoNogoSetting = dlGetSetting('DLGoNogoSetting');
	if (!dlGoNogoSetting) {
		dlGoNogoSetting = TestGoroNogoSetting;
	}
	for (var key in dlGoNogoSetting) {
		var title = DLBusinessEnum[key];
		if (title) { //判断是否是需要显示的业务
			var msg = json[key];
			if (isArray(msg) && msg.length > 0) {
				if (chechAvaliable(msg[0]))
				{
					findSome(key,msg);
				}
			}
		}
		else
		{
			var map = DLGoNoGoMatchBusinessNames[key];
			if (key == 'Call') {
				if (isArray(map)) {
					var callData = {};
					for (var i = 0; i < map.length; i++) {
						var name = map[i];
						var msg = json[name];
						if (isArray(msg) && msg.length > 0) {
							if (chechAvaliable(msg[0]))
							{
								if (name == 'MO_CS' || name == 'MT_CS') {
									var csData = callData['CS'];
									if (!csData) {
										csData = [];
									}
									csData.push({'name':name,'data':msg[0]});
									callData['CS'] = csData;
								}
								if (name == 'MO_CSFB' || name == 'MT_CSFB') {
									var csfbData = callData['CSFB'];
									if (!csfbData) {
										csfbData = [];
									}
									csfbData.push({'name':name,'data':msg[0]});
									callData['CSFB'] = csfbData;
								}
							}
						}
					}
					for (var n in callData) {
						findSome(n,callData[n]);
					}
				}
			}
			else
			{
				if (isArray(map)) {
					for (var i = 0; i < map.length; i++) {
						title = DLBusinessEnum[map[i]];
						if (title) { //判断是否是需要显示的业务
							var msg = json[map[i]];
							if (isArray(msg) && msg.length > 0) {
								if (chechAvaliable(msg[0]))
								{
									findSome(map[i],msg);
								}
							}
						}
					}
				}
			}

			
		}
	}
}

function dlGetGoOrNoGoColor()
{
	var dlGoNogoSetting = dlGetSetting('DLGoNogoSetting');
	if (!dlGoNogoSetting) {
		dlGoNogoSetting = TestGoroNogoSetting;
	}
	return '#FF0000';
}

function dlGetThresholdSetting(pName,net)
{
	var dlThs = dlGetSetting('DLThreholdSetting');
	if (!dlThs) {
		dlThs = TestThresholdSet;
	}
	var netSet = dlThs[net];
	if (netSet) {
		return netSet[pName];
	}
	return false;
}

//按条件过滤分段数据
function dlFliterThresholdCount(json,paramterName)
{
	var sign = 0;
	if (paramterName == 'UL_Current_AMRCodec' || paramterName == 'DL_Current_AMRCodec') {
		var rtn = [];
		var lastIndex;
		$.each(json,function(n,value){
			if (value.INTERVALNAME) {
				if (value.TOTALCOUNT) {
					sign = parseInt(value.TOTALCOUNT);
				}else
				{
					sign = 0;
				}
				//只计算有值的
				if (sign > 0) {
					lastIndex = n;
					rtn.push(value);
				}
			}
		});

		//补数据
		if (rtn.length > 0 && rtn.length < 2) {
			var index = lastIndex +1;
			if (index < 2) {
				index ++;
			};
			if (index >= json.length) {
				index --;
			}
			rtn = [];
			for (var i = 2; i >= 0; i--) {
				rtn.push(json[index - i]);
			}
		}
		else if(rtn.length > 0 && rtn.length < 2)
		{
			var index = lastIndex +1;
			if (index < json.length) {
				rtn.push(json[index]);
			}
			else
			{
				rtn.push(json[0]);
			}
		}
		//变更名称
		$.each(rtn,function(n,value){
			switch(value.INTERVALNAME){
				case '=1':
				{
					value.INTERVALNAME = 'NB 4.75kbit/s';
					break;
				}
				case '=2':
				{
					value.INTERVALNAME = 'NB 5.15kbit/s';
					break;
				}
				case '=3':
				{
					value.INTERVALNAME = 'NB 5.90kbit/s';
					break;
				}
				case '=4':
				{
					value.INTERVALNAME = 'NB 6.70kbit/s';
					break;
				}
				case '=5':
				{
					value.INTERVALNAME = 'NB 7.40kbit/s';
					break;
				}
				case '=6':
				{
					value.INTERVALNAME = 'NB 7.95kbit/s';
					break;
				}
				case '=7':
				{
					value.INTERVALNAME = 'NB 10.2kbit/s';
					break;
				}
				case '=8':
				{
					value.INTERVALNAME = 'NB 12.2kbit/s';
					break;
				}
				case '=9':
				{
					value.INTERVALNAME = 'WB 6.60kbit/s';
					break;
				}
				case '=10':
				{
					value.INTERVALNAME = 'WB 8.85kbit/s';
					break;
				}
				case '=11':
				{
					value.INTERVALNAME = 'WB 12.65kbit/s';
					break;
				}
				case '=12':
				{
					value.INTERVALNAME = 'WB 14.25kbit/s';
					break;
				}
				case '=13':
				{
					value.INTERVALNAME = 'WB 15.85kbit/s';
					break;
				}
				case '=14':
				{
					value.INTERVALNAME = 'WB 18.25kbit/s';
					break;
				}
				case '=15':
				{
					value.INTERVALNAME = 'WB 19.85kbit/s';
					break;
				}
				case '=16':
				{
					value.INTERVALNAME = 'WB 23.05kbit/s';
					break;
				}
				case '=17':
				{
					value.INTERVALNAME = 'WB 23.85kbit/s';
					break;
				}
				case '=18':
				{
					value.INTERVALNAME = 'WB+ 13.6kbit/s';
					break;
				}
				case '=19':
				{
					value.INTERVALNAME = 'WB+ 18kbit/s';
					break;
				}
				case '=20':
				{
					value.INTERVALNAME = 'WB+ 24kbit/s';
					break;
				}
				default:
				{
					value.INTERVALNAME = 'Other';
					break;
				}
			}
		});
		return rtn;
	}
	return json;
}

//计算分段数值
function dlCalculationThresholdCount(json,paramterName)
{
	var rtn = [];
	var all = 0;
	if (paramterName == 'UL_Current_AMRCodec' || paramterName == 'DL_Current_AMRCodec') {
		$.each(json,function(n,value){
			if (value.INTERVALNAME) {
				var rec = {};
				rec.name = value.INTERVALNAME;
				if (value.TOTALCOUNT) {
					rec.y = parseInt(value.TOTALCOUNT);
				}else
				{
					rec.y = 0;
				}
				//只计算有值的
				if (rec.y > 0) {
					rtn.push(rec);
					all += rec.y;
				}
			}
		});
	}
	else
	{
		$.each(json,function(n,value){
			if (value.INTERVALNAME) {
				var rec = {};
				rec.name = value.INTERVALNAME;
				if (value.TOTALCOUNT) {
					rec.y = parseInt(value.TOTALCOUNT);
				}else
				{
					rec.y = 0;
				}
				rtn.push(rec);
				all += rec.y;
			}
		});
	}
	$.each(rtn,function(n,value){
		value.y = value.y *1.0 / all;
	});
	return rtn;
}

function dlParseCallData(json,csfb)
{
	var rtn = false;
	if (csfb) {
		var temp = json['MO_CSFB'];
		if (isArray(temp) && temp.length > 0) {
			rtn = temp[0];
		}
		else
		{
			temp = json['MT_CSFB'];
			if (isArray(temp) && temp.length > 0) {
				rtn = temp[0];
			}
		}
	}
	else
	{
		var temp = json['MO_CS'];
		if (isArray(temp) && temp.length > 0) {
			rtn = temp[0];
		}
		else
		{
			temp = json['MT_CS'];
			if (isArray(temp) && temp.length > 0) {
				rtn = temp[0];
			}
		}
	}
	return rtn;
}
