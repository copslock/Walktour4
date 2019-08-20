//  Created by ZengJingzhao on 15-1-19.
//  Copyright (c) 2015年 ZengJingzhao. All rights reserved.
//使用此文件前必须引入 dlUtils.js dlData.js


// CQT 测试数据模型
function DLCQTListData()
{
	this.testMode = "CQT";
	this.data = false;
	this.isByLog = false;
}

DLCQTListData.prototype = {
	// 构造
	initWithJson:function(json){
		//取汇总信息
		var resault = formatJsonResponse(json);
		this.isByLog = resault.isBylog;
		this.data = resault.data;
		if (this.data) {
			return true;
		}
		return false;
	},

	//显示
	renderToDoc:function(elecment){
		var html='';
		var count = 0;
		if (this.data) {
			var buildings = [];
			for (var key in this.data) {
				buildings.push(key);
				count++;
			}
			for (var i = 0; i  < buildings.length; i++) {
				var collapsible = this.dislplayBuilding(buildings[i]);
				html += collapsible;
			}
		}
		elecment.html(html).collapsibleset();
		elecment.collapsibleset( "refresh" );
		//刷新所有listview
		var list = "#" + elecment.attr("id") + " ul";
		$(list).listview();
	},

	//显示建筑物
	dislplayBuilding:function(bName){

		if (!bName) {
			return this.displayNoSence();
		}
		var bInfo = this.data[bName];
		var base = bInfo.base;
		var floors = bInfo.floors;
		var fArr = [];
		for (var key in floors) {
			fArr.push(key);
		}
		fArr.sort();

		//取汇总信息
		var baseInfo = {};
		baseInfo.TestTotalTime = dlSecToTime(base['TestTotalTime'][0]['TESTTOTALTIME']); //总时长
		var netArray = ['GSM','CDMA','EVDO','WCDMA','TDSCDMA','LTE'];
		//取覆盖率
		var coverageRtn = '';
		for (var i = 0 ; i < netArray.length;i++) {
			var t = base[netArray[i] + 'Coverage'];
			if ( isArray(t) && t.length > 0) {
				var r = t[0]["SAMPLECOVERAGERATE"];
				if (r && parseFloat(r) > 0) {
					if (coverageRtn.length > 0) {
						coverageRtn += ','
					}
					coverageRtn += (dlStrToPercent(r,false) + '(' + netArray[i] + ")");
				}
			}
		}
		if (coverageRtn == '') {
			coverageRtn = '--';
		}
		baseInfo.coverage = coverageRtn;
		var content = '';
		if (this.isByLog) {
			//头部
			content = '<div data-role="collapsible"  data-theme="c" data-content-theme="a" data-collapsed="false">';
			content += '<h1 class="dl-NoteText">';
			//building 信息
			var tbName = bName;
			if (bName == DLByLogSum) {
				tbName = '汇总';
			}
			content += '统计文件:'+tbName+'<br/>';
			content += '测试时长:'+baseInfo.TestTotalTime+'<br/>';
			content += '覆盖率:'+baseInfo.coverage;
			content += '</h1>';
		}
		else
		{
			//头部
			content = '<div data-role="collapsible"  data-theme="c" data-content-theme="a" data-collapsed="false">';
			content += '<h1 class="dl-NoteText">';
			//building 信息
			content += '建筑物:'+bName+'<br/>';
			content += '测试时长:'+baseInfo.TestTotalTime+'<br/>';
			content += '覆盖率:'+baseInfo.coverage;
			content += '</h1>';
		}
		
		//添加内容
		content += '<table class="fullSet noPadding noMargin">';

		var bus = [];
		dlSearchGoOrNogoSettingWithInData(base,function(n,msg)
		{
			if (n == 'CS' || n == 'CSFB') {
				var t = {};
				t.name = n;
				t.data = [];
				t.data.push({'name':false,'data':msg});
				for (var i = 0; i < fArr.length; i++) {
					fmsg = floors[fArr[i]];
					if (fmsg)
					{
						var subData = [];
						if (n == 'CS') {
							var tempMsg = fmsg['MO_CS'];
							if (isArray(tempMsg) && tempMsg.length > 0) {
								if (chechAvaliable(tempMsg[0]))
								{
									subData.push({'name':'MO_CS','data':tempMsg[0]});
								}
							}
							tempMsg = fmsg['MT_CS'];
							if (isArray(tempMsg) && tempMsg.length > 0) {
								if (chechAvaliable(tempMsg[0]))
								{
									subData.push({'name':'MT_CS','data':tempMsg[0]});
								}
							}
						}
						if (n == 'CSFB') {
							var tempMsg = fmsg['MO_CSFB'];
							if (isArray(tempMsg) && tempMsg.length > 0) {
								if (chechAvaliable(tempMsg[0]))
								{
									subData.push({'name':'MO_CSFB','data':tempMsg[0]});
								}
							}
							tempMsg = fmsg['MT_CSFB'];
							if (isArray(tempMsg) && tempMsg.length > 0) {
								if (chechAvaliable(tempMsg[0]))
								{
									subData.push({'name':'MT_CSFB','data':tempMsg[0]});
								}
							}
						}
						t.data.push({'name':fArr[i],'data':subData});
					}
				}
				bus.push(t);
			}else
			{
				if (chechAvaliable(msg[0])) {
					var t = {};
					t.name = n;
					t.data = [];
					t.data.push({'name':false,'data':msg[0]});
					var fmsg;
					for (var i = 0; i < fArr.length; i++) {
						fmsg = floors[fArr[i]];
						if (fmsg)
						{
							fmsg = fmsg[n];
							if (isArray(fmsg) && fmsg.length > 0) {
								t.data.push({'name':fArr[i],'data':fmsg[0]});
							}
						}
					}
					bus.push(t);
				}
			}
			
		});

		for (var i = 0; i < bus.length;i++) {
			content += this.displayCell(bus[i],bName);
		}

		//取参数
		var pars = [];
		for (var key in DLNetEnum) {
			var sec = {};
			sec.net = key;
			sec.tech = [];
			var ok = false;
			var netParam = dlGetGoOrNogoSettingWithBusinessName(key);
			if (isArray(netParam)) {
				var ptech = {};
				for (var i = 0; i < netParam.length ; i++) {
					var s = netParam[i].paramter;
					if(s)
					{
						var os = s  + '_Sum';
						var msg = base[os];
						if (isArray(msg) && msg.length > 0) {
							ptech[s] = msg[0];
							ok = true;
						}
					}
				}
				if (ok) {
					sec.tech.push({'name':false,'data':ptech});
				}
			}
			if (ok) {
				var fmsg;
				for (var i = 0; i < fArr.length; i++) {
					fmsg = floors[fArr[i]];
					var succ =false;
					if (fmsg)
					{
						var ptech = {};
						for (var j = 0; j < netParam.length ; j++) {
							var s = netParam[j].paramter;
							if(s)
							{
								var os = s  + '_Sum';
								var msg = fmsg[os];
								if (isArray(msg) && msg.length > 0) {
									ptech[s] = msg[0];
									succ = true;
								}
							}
						}
						if (succ) {
							sec.tech.push({'name':fArr[i],'data':ptech});
						}
					}
				}
				pars.push(sec);
			}
		}
		for (var i = 0; i < pars.length;i++) {
			content += this.displayCell(pars[i],bName,true);
		}

		content += '</table>';
		//尾部
		content += '</div>';
		return content;
	},

	//
	displayCell:function(bus,building,isParamter)
	{
		if (isParamter) {
			if (!bus && !building) {
				return '';
			}
			var net = bus.net;
			var tData = bus.tech;
			var html = '<tr class="ui-bar-a"><th class="dl-bus">'+net+'</th></tr>';
			for (var i = 0; i < tData.length; i++) {
				var obj = tData[i];
				var fName = obj.name;
				var sence = building + '#' + fName;
				if (!fName) {
					fName = '汇总';
					sence = building;
				}
				html += '<tr><th class="fullSet noPadding noMargin">';
				html += '<a href="detail.html?biz='+net+'&sen='+sence+'" data-ajax="false">';
				html += '<table class="dl-border" rules="all">';
				if (!this.isByLog) {
					html += '<thead>';
					html += '<tr><td class="dl-subBus" colspan="3">'+ fName+'</td></tr>';
					html += '</thead>';
				}
				html += '<tbody>';
				var fmsg = obj.data;
				var ps = dlGetGoOrNogoSettingWithBusinessName(net);
				for (var j = 0; j < ps.length; j++) {
					var pobj = ps[j];
					var p = pobj.paramter;
					var msg = fmsg[p];
					if (msg) {
						html += '<tr height="25px"><th class="dl-KPI" width="50%">'+DLParamterWithUnitEnum[net][p]+'</th>';
						html += '<td class="dl-condiction" width="25%">'+pobj.condiction+'</td>';
						var avgKey = 'AVERAGE_' + p.toUpperCase() + '_SUM';
						var avg = msg[avgKey];
						if (!avg) {
							avg = msg['AVERAGE'];
						};
						html += '<td class="dl-value" width="25%">'+htmlContnetWithGoNoGo(p,avg,pobj.condiction,dlGetGoOrNoGoColor())+'</td></tr>'
					}
				}
				html += '</tbody>';
				html += '</table></a></th></tr>';
			}
			return html;
		}
		else
		{
			if (!bus && !building) {
				return '';
			}
			var name = bus.name;
			var tData = bus.data;
			if (name == 'CS' || name == 'CSFB') {
				var html = '<tr class="ui-bar-a"><th class="dl-bus">'+DLBusinessEnum[name]+'</th></tr>'
				for (var i = 0; i < tData.length; i++) {
					var obj = tData[i];
					var fName = obj.name;
					var sence = building + '#' + fName;
					if (!fName) {
						fName = '汇总';
						sence = building;
					}
					html += '<tr><th class="fullSet noPadding noMargin">';
					html += '<a href="detail.html?biz='+name+'&sen='+sence+'" data-ajax="false">';
					html += '<table class="dl-border" rules="all">';
					var subData,subName;
					for (var n = 0; n < obj.data.length; n++) {
						subName = fName + '&nbsp;'+ DLVoiceNameEnum[obj.data[n].name];
						subData = obj.data[n].data;
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
						if (!this.isByLog) {
							html += '<tr><td colspan="3" class="dl-subBus"><span class="dl-label">'+ subName+'</span><span class="dl-field">(次数:'+total+')</span></td></tr>';
						}
						else
						{
							html += '<tr><td colspan="3" class="dl-subBus"><span class="dl-label">'+ DLVoiceNameEnum[obj.data[n].name]+'</span><span class="dl-field">(次数:'+total+')</span></td></tr>';
						}
						var ggSteting = dlGetGoOrNogoSettingWithBusinessName('Call');
						if (isArray(ggSteting)) {
							for (var j = 0; j < ggSteting.length; j++) {
								var f = ggSteting[j];
								html += '<tr height="25px"><th class="dl-KPI" width="50%">'+f.display+'</th>';
								html += '<td class="dl-condiction" width="25%">'+f.condiction+'</td>';
								var value = subData[f.paramter];
								html += '<td class="dl-value" width="25%">'+htmlContnetWithGoNoGo(f.paramter,value,f.condiction,dlGetGoOrNoGoColor())+'</td></tr>'
							}
						}
					}
					html += '</table></a></th></tr>';
				}
			}
			else
			{
				var html = '<tr class="ui-bar-a"><th class="dl-bus">'+DLBusinessEnum[name]+'</th></tr>'
				for (var i = 0; i < tData.length; i++) {
					var obj = tData[i];
					var fName = obj.name;
					var sence = building + '#' + fName;
					if (!fName) {
						fName = '汇总';
						sence = building;
					}
					var total = obj.data['BUSINESSCOUNT'];
					if (!total) {
						total = obj.data['BUSINESSCOUNT_ATTEMPTS'];
					}
					if (!total) {
						total = '--';
					}
					else
					{
						total = dlToFixed(total,0);
					}
					html += '<tr><th class="fullSet noPadding noMargin">';
					html += '<a href="detail.html?biz='+name+'&sen='+sence+'" data-ajax="false">';
					html += '<table class="dl-border" rules="all">';
					if (!this.isByLog) {
						html += '<thead>';
						html += '<tr><td colspan="3" class="dl-subBus"><span class="dl-label">'+ fName+'</span><span class="dl-field">(次数:'+total+')</span></td></tr>';
						html += '</thead>';
					}
					html +=  '<tbody>';
					var ggSteting = dlGetGoOrNogoSettingWithBusinessName(name);
					if (isArray(ggSteting)) {
						for (var j = 0; j < ggSteting.length; j++) {
							var f = ggSteting[j];
							html += '<tr height="25px"><th class="dl-KPI" width="50%">'+f.display+'</th>';
							html += '<td class="dl-condiction" width="25%">'+f.condiction+'</td>';
							var value = obj.data[f.paramter];
							html += '<td class="dl-value" width="25%">'+htmlContnetWithGoNoGo(f.paramter,value,f.condiction,dlGetGoOrNoGoColor())+'</td></tr>'
						}
					}
					html += '</tbody>';
					html += '</table></a></th></tr>';
				}
			}
			return html;
		}
	}
};