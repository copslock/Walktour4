//  Created by ZengJingzhao on 15-1-19.
//  Copyright (c) 2015年 ZengJingzhao. All rights reserved.
//使用此文件前必须引入 dlUtils.js dlData.js

// DT 测试数据模型
function DLDTListData()
{
	this.testMode = "DT";
	this.data = {};
}

function DlValueNull(value){
	try{
		if(value == null || value == 'NaN' || value == 'undefined'){
		return '0';
	}
	}catch(e){
		return value;
	}		
	
}

DLDTListData.prototype = {
	// 构造
	initWithJson:function(jn){
		var json = removeSenceJsonResponse(jn);
		//取汇总信息
		var baseInfo = {};
		if(json['TestTotalTime'] == null || json['TestTotalTime'] == 'NaN' || json['TestTotalTime'] == 'undefined'){
			baseInfo.TestTotalTime = 0;
		}else{
			baseInfo.TestTotalTime = json['TestTotalTime'][0]['TESTTOTALTIME'];//总时长
			
		}
		if(json['TestTotalMileage'] == null || json['TestTotalMileage'] == 'NaN' || json['TestTotalMileage'] == 'undefined'){
			baseInfo.TestTotalMileage = 0;
		}else{
			baseInfo.TestTotalMileage = json['TestTotalMileage'][0]['TESTTOTALMILEAGE'];//总里程	
		}	
		
		var netArray = ['GSM','CDMA','EVDO','WCDMA','TDSCDMA','LTE'];
		//取覆盖率
		var coverageRtn = '';
		for (var i = 0 ; i < netArray.length;i++) {
			var t = json[netArray[i] + 'Coverage'];
			if ( isArray(t) && t.length > 0) {
				var r = t[0]["SAMPLECOVERAGERATE"];
				if (r && parseFloat(r) > 0) {
					if (coverageRtn.length > 0) {
						coverageRtn += '|'
					}
					coverageRtn += (netArray[i] + "/" + dlStrToPercent(r,false));
				}
			}
		}
		if (coverageRtn == '') {
			coverageRtn = '--';
		}
		baseInfo.coverage = coverageRtn;
		this.data.baseData = baseInfo;
		//取业务
		var bus = [];
		dlSearchGoOrNogoSettingWithInData(json,function(n,msg)
		{
			if (n == 'CS' || n == 'CSFB') {
				var t = {};
				t.name = n;
				t.data = msg;
				bus.push(t);
			}else
			{
				if (chechAvaliable(msg[0])) {
					var t = {};
					t.name = n;
					t.data = msg[0];
					bus.push(t);
				}
			}
		});
		this.data.bussinesses = bus;
		//取参数
		var pars = [];
		for (var key in DLNetEnum) {
			var sec = {};
			sec.net = key;
			var ok = false;
			var netParam = dlGetGoOrNogoSettingWithBusinessName(key);
			if (isArray(netParam)) {
				for (var i = 0; i < netParam.length ; i++) {
					var s = netParam[i].paramter;
					if(s)
					{
						var os = s  + '_Sum';
						var msg = json[os];
						if (isArray(msg) && msg.length > 0) {
							sec[s] = msg[0];
							ok = true;
						}
					}
				}
			}
			if (ok) {
				pars.push(sec);
			}
		}
		this.data.params = pars;
		return true;
	},

	//显示
	renderToDoc:function(elecment){
		var content = '';

		//如果有测试汇总信息
		content += '<li data-role="list-divider">';
		content += '<h1><span class="dl-label">Total Distance:</span><span class="dl-field">'+ dlToFixed(this.data.baseData.TestTotalMileage,1) +'m</span>';
		content += '&nbsp;&nbsp;<span class="dl-label">Testing  Duration:</span><span class="dl-field">'+dlSecToTime(this.data.baseData.TestTotalTime)+'</span>';
		content += '<br/><span class="dl-label">Coverage Rate:</span><span class="dl-field">'+this.data.baseData.coverage+'</span></h1></li>';
		
		//添加内容
		var bussiness = this.data.bussinesses;
		for (var i = 0; i < bussiness.length;i++) {
			content += this.displayCell(bussiness[i],false);
		}
		var params = this.data.params;
		for (var i = 0; i < params.length;i++) {
			content += this.displayCell(params[i],true);
		}

		//刷新内容
		elecment.html(content).listview();
		elecment.listview('refresh');
	},

	//
	displayCell:function(bus,isParam)
	{
		if (isParam) {//公共参数
			var net = bus.net;
			var ps = dlGetGoOrNogoSettingWithBusinessName(net);
			var html = '<li data-icon="false"><a href="detail.html?biz='+net+'" data-ajax="false"><table class="dl-border" rules="all"><thead><tr class="ui-bar-a"><th class="dl-bus" colspan="3">'+DLNetEnum[net]+'</th></tr></thead>';
			html += '<tbody>';
			for (var i = 0; i < ps.length; i++) {
				var obj = ps[i];
				var p = obj.paramter;
				var msg = bus[p];
				if (msg) {
					html += '<tr><th class="dl-KPI" width="50%">'+ DLParamterWithUnitEnum[net][p]+'</th>';
					html += '<td class="dl-condiction" width="25%">'+obj.condiction+'</td>';
					var avgKey = 'AVERAGE_' + p.toUpperCase() + '_SUM';
					var avg = msg[avgKey];
					if (!avg) {
						avg = msg['AVERAGE'];
					};
					html += '<td class="dl-value" width="25%">'+htmlContnetWithGoNoGo(p,avg,obj.condiction,dlGetGoOrNoGoColor())+'</td></tr>'
				}
			}
			html += '</tbody></table></a></li>';
			return html;
		}
		else
		{

			var name = bus.name;
			var tData = bus.data;
			if (name == 'CS' || name == 'CSFB') {
				var html = '<li data-icon="false"><a href="detail.html?biz='+name+'" data-ajax="false"><table class="dl-border" rules="all"><thead><tr class="ui-bar-a"><th class="dl-bus" colspan="3">'+DLBusinessEnum[name]+'<span  class="dl-field"></span></th></tr></thead>';
				html += '<tbody>';
				var subData,subName;
				for (var n = 0; n < tData.length; n++) {
					subName = tData[n].name;
					subData = tData[n].data;
					var total = subData['BUSINESSCOUNT'];
					if (!total) {
						total = subData['BUSINESSCOUNT_ATTEMPTS'];
					}
					if (!total) {
						total = '--';
					}
					else
					{
						total = dlToFixed(total,0);
					}
					var ggSteting = dlGetGoOrNogoSettingWithBusinessName('Call');
					html += '<tr><th class="dl-bus" colspan="3">'+DLVoiceNameEnum[subName]+'<span  class="dl-field">(Attemps:'+total+')</span></th></tr>';
					for (var i = 0; i < ggSteting.length; i++) {
						var f = ggSteting[i];
						html += '<tr><th class="dl-KPI" width="50%">'+f.display+'</th>';
						html += '<td class="dl-condiction" width="25%">'+f.condiction+'</td>';
						var value = subData[f.paramter];
						html += '<td class="dl-value" width="25%">'+htmlContnetWithGoNoGo(f.paramter,value,f.condiction,dlGetGoOrNoGoColor())+'</td></tr>'
					}
				}
				html += '</tbody></table></a></li>';
			}
			else
			{
				var total = tData['BUSINESSCOUNT'];
				if (!total) {
					total = tData['BUSINESSCOUNT_ATTEMPTS'];
				}
				if (!total) {
					total = '--';
				}
				else
				{
					total = dlToFixed(total,0);
				}
				var ggSteting = dlGetGoOrNogoSettingWithBusinessName(name);
				var html = '<li data-icon="false"><a href="detail.html?biz='+name+'" data-ajax="false"><table class="dl-border" rules="all"><thead><tr class="ui-bar-a"><th class="dl-bus" colspan="3">'+DLBusinessEnum[name]+'<span  class="dl-field">(Attemps:'+total+')</span></th></tr></thead>';
				html += '<tbody>';
				for (var i = 0; i < ggSteting.length; i++) {
					var f = ggSteting[i];
					html += '<tr><th class="dl-KPI" width="50%">'+f.display+'</th>';
					html += '<td class="dl-condiction" width="25%">'+f.condiction+'</td>';
					var value = tData[f.paramter];
					html += '<td class="dl-value" width="25%">'+htmlContnetWithGoNoGo(f.paramter,value,f.condiction,dlGetGoOrNoGoColor())+'</td></tr>'
				}
				html += '</tbody></table></a></li>';
			}
			return html;
		}
		return '';
	}
};