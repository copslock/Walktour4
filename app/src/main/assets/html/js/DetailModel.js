//  Created by ZengJingzhao on 15-1-19.
//  Copyright (c) 2015年 ZengJingzhao. All rights reserved.
//使用此文件前必须引入 dlUtils.js dlData.js

//url = detail.html?biz=?&sen=?

//构建详细信息数据结构。  参数 b  是 业务名称，参数s 是场景
function DLDetailData (b,s) {
	// body...
	this.business = b;
	this.sence = s;
	this.baseInfo = false;              //基本测试信息
	this.staticsInfo = false;           //统计信息 一些业务公共过滤的参数
	this.parameterInfo = false;         //支持该业务过滤的参数信息数组,按网络分组 {网络 ，参数:[{参数名，统计信息，分段信息}]}  

	this.addOn = false;

	//被选择的网络参数
	this.selectedCommonParamIndex = -1;
	this.selectedParamIndex = -1;

	this.showCake = false;
}

DLDetailData.prototype = {
	// 构造
	initWithJson:function(jn){
		var json = removeSenceJsonResponse(jn,this.sence);
		var flag = DLBusinessEnum[this.business];
		if (flag) {
			//传入的是业务了
			if (this.business == 'CS') {
				this.baseInfo = dlParseCallData(json,false);
			}
			else if (this.business == 'CSFB') {
				this.baseInfo = dlParseCallData(json,true);
			}
			else
			{
				var bi = json[this.business];
				if (isArray(bi)) {
					this.baseInfo = bi[0];
				}
				else
				{
					this.baseInfo = false;
				}
			}
			var iden = DLStaticBusinessFields[this.business];
			if (!iden) {
				iden = this.business;
			}
			//取子业务
			var subBizs = DLGoNoGoMatchBusinessNames[iden];
			if (isArray(subBizs)) {
				for (var i = 0; i < subBizs.length; i++) {
					var subData = json[subBizs[i]];
					if (isArray(subData)) {
						if (chechAvaliable(subData[0])) {
							if (!this.addOn) {
								this.addOn = [];
							}
							this.addOn.push({'sub':subBizs[i],'data':subData[0]});
						}
					}
				}
			}

			//业务参数的分段暂时没有数据
			this.staticsInfo = false;
			var shareParams = DLParamterWithUnitEnum['PUBLIC'];
			for (var key in shareParams) {
				var sk = key + "_" + iden;
				var su = sk + "_Sum";
				var td = json[su];
				if (isArray(td) && td.length > 0 ) {
					var  p = {};
					p.name = shareParams[key];
					p.code = key;
					p.key = su.toUpperCase();
					p.sum = td[0];
					td  = json[sk];
					if (isArray(td) && td.length > 0 ) {
						p.log = dlFliterThresholdCount(td,key);
					}
					if (!this.staticsInfo) {
						this.staticsInfo = [];
					}
					this.staticsInfo.push(p);
				}
			}
			//取所有支持该业务过滤的网络参数
			var allNets = [];
			for (var key in DLNetEnum) {
				allNets.push(key);
			}
			this.parameterInfo = false;
			for (var i = 0; i < allNets.length; i++) {
				var params = DLParamterWithUnitEnum[allNets[i]];
				if (params) {
					var obj = {};
					obj.net = DLNetEnum[allNets[i]];
					obj.paramters = [];
					var isOK = false;
					for (var key in params) {
						var k1 = key + '_' +iden; 
						var k = k1 + '_Sum';
						var data = json[k];
						var data1 = json[k1];
						var  p = {};
						p.name = params[key];
						p.code = key;
						if (isArray(data) && data.length > 0 ) {
							isOK = true;
							p.sum = data[0];
							p.key = k.toUpperCase();
							if (isArray(data1) && data1.length > 0 ) {
								isOK = true;
								p.log = data1;
							}
							obj.paramters.push(p);
						}
					}
					if (isOK) {
						if (!this.parameterInfo) {
							this.parameterInfo = [];
						}
						this.parameterInfo.push(obj);
					}
				}
			}
			return true;
		}
		else
		{
			flag = DLNetEnum[this.business];
			if (flag) {
				//传入的是公共参数
				this.baseInfo = false;
				this.staticsInfo = false;
				var params = DLParamterWithUnitEnum[this.business];
				if (params) {
					this.parameterInfo = [];
					var obj = {};
					obj.net = flag;
					obj.paramters = [];
					for (var key in params) {
						var k = key + '_Sum';
						var k1 = key + '_Interval';
						var  p = {};
						p.name = params[key];
						p.code = key;
						p.key = k.toUpperCase();
						var data = json[k];
						if (isArray(data) && data.length > 0) {
							p.sum = data[0];
							p.log = false;
							obj.paramters.push(p);
						}
						var data1 = json[k1];
						if (isArray(data1) && data1.length > 0) {
							p.log = data1;
						}
					}
					if (obj.paramters.length > 0) {
						if (!this.parameterInfo) {
							this.parameterInfo = [];
						}
						this.parameterInfo.push(obj);
					}
				}
				return true;
			}
		}
		return false;
	},

	//显示
	renderToDoc:function(elecment){
		var content='';
		content += this.parseBase();
		content += this.parseStatics();
		content += this.printGridTable();
		content += this.parseParameters();
		//刷新内容
		elecment.html(content).listview();
		elecment.listview('refresh');

		var timeout1;
		var me = this;
		var f1 = function printpc1 () {
			// body...
			clearTimeout(timeout1);           // 可取消由 setTimeout() 方法设置的 timeout
			$('#divtab').hide();
			if (me.staticsInfo) {
				
				// $('#commonParamSelect').change(function(){ 
				// 	me.selectedCommonParamIndex =$(this).val();//这就是selected的值 
				// 	me.printPublicChart();

				// 	$('#commonGridTable').html(me.fliterGridInnerHtml());
				// });
				//=====
				$("#commonParamSelect").on( "click", function(evt) {
					var list = '<ul id="ul-commonParamSelect" data-role="listview" data-inset="true" class="dl-Text">';
					list += '<li data-role="list-divider" data-theme="b">选择参数</li>';
					for (var i = 0; i < me.staticsInfo.length ; i++) {
						var p = me.staticsInfo[i];
						if (p.log) {
							if (me.selectedCommonParamIndex == i) {
								list += ('<li data-icon="check"><a value="'+i+'">'+p.name+'</a></li>');
							}else
							{
								list += ('<li data-icon="false"><a value="'+i+'">'+p.name+'</a></li>');
							}
						};
					}
					list += '</ul>';
      				showPopOver(list,$(this),evt);
      				$('#ul-commonParamSelect li a').on('click',function(){  
          				var target = $(this);
      					var tag = parseInt(target.attr("value"));
          				me.selectedCommonParamIndex = tag;//这就是selected的值 
						me.printPublicChart();
						$('#commonGridTable').html(me.fliterGridInnerHtml());
						$("#commonParamSelect").text(target.text());
    					$("#popup-commonParamSelect").remove();
      				});  
    			});
				//=====  
			};
			if (me.parameterInfo) {
			
				// $('#paramSelect').change(function(){ 
				// 	me.selectedParamIndex =$(this).val();//这就是selected的值 
				// 	me.printParamChart(me.showCake);
				// });
				//=====
				$("#paramSelect").on( "click", function(evt) {
					var list = '<ul id="ul-paramSelect" data-role="listview" data-inset="true" class="dl-Text">';
					var ti = -1;
					list += '<li data-role="list-divider" data-theme="b">选择参数</li>';
					for (var i = 0; i < me.parameterInfo.length ; i++) {
						var obj = me.parameterInfo[i];
						list += ('<li data-role="list-divider">'+ obj.net +'</li>');
						for (var j = 0; j < obj.paramters.length; j++) {
							var p = obj.paramters[j];
							if (p.log) {
								ti = i*100 + j;
								if (me.selectedParamIndex == ti) {
									list += ('<li data-icon="check"><a value="'+ti+'">'+p.name+'</a></li>');
								}else
								{
									list += ('<li data-icon="false"><a value="'+ti+'">'+p.name+'</a></li>');
								}
							}
						}
					}
					list += '</ul>';
      				showPopOver(list,$(this),evt);
      				$('#ul-paramSelect li a').on('click',function(){  
          				var target = $(this);
      					var tag = parseInt(target.attr("value"));
          				me.selectedParamIndex = tag;//这就是selected的值 
						me.printParamChart(me.showCake);
						$("#paramSelect").text(target.text());
    					$("#popup-paramSelect").remove();
      				});  
    			});
				//===== 
				var toAppend = $('#divtab'); 
				$('#tabbarSapce').append(toAppend); 
				if (me.selectedParamIndex != -1) {
					toAppend.show();
				}else{
					toAppend.hide();
				}
				$('#showBar').click(function(){
					me.showCake = false;
					me.printParamChart(me.showCake);
				});
				$('#showPie').click(function(){
					me.showCake = true;
					me.printParamChart(me.showCake);
				});
			}else
			{
				$('#divtab').hide();
			}
			me.printPublicChart();
			me.printParamChart(me.showCake);
		}
		timeout1 = setTimeout(f1, 100); // 延时0.1s执行
	},

	parseBase:function()
	{
		var rtn = '';
		if (this.baseInfo) {

			switch(this.business)
			{
				case 'CS':
				case 'CSFB':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">接通率(%):</span><span class="dl-field">'+dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE_SUCCESSRATE'],true)+'</span>&nbsp;&nbsp;<span class="dl-label">掉话率(%):</span><span class="dl-field">'+dlFormatDisplay('DROPPEDRATE',this.baseInfo['DROPPEDRATE_DROPPEDRATE'],true)+'</span> <br/> ';
					rtn += '<span class="dl-label">平均呼叫建立时长(s):<span class="dl-field">'+dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY_CALLDELAY'],true)+'</span></h1></li>';
					break;
				}
				case 'Network_Connect':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true)+'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">' + dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true)+'</span> ';
					rtn += '</h1></li>';
					break;
				}
				case 'FTPDownload':
				case 'FTPUpload':
				case 'MultiFTPDownload':
				case 'MultiFTPUpload':
				case 'HTTPDownload':
				case 'HTTPUpload':
				{
					var businessCount = parseInt(this.baseInfo['BUSINESSCOUNT']);
					var sCount  = parseInt(this.baseInfo['SUCCESSCOUNT']);
					var dCount = businessCount - sCount;
					var dRate = dCount * 1.0 / sCount;
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">掉线率(%):</span><span class="dl-field">'+ dlFormatDisplay('DROPPEDRATE',dRate,true) +'</span>&nbsp;&nbsp;<span class="dl-label">掉线次数:</span><span class="dl-field">'+ dlToFixed(dCount,0) +'</span> <br/> ';
					rtn += '<span class="dl-label">平均速率(KB/s):</span><span class="dl-field">'+ dlFormatDisplay('TRANSFERRATE',this.baseInfo['TRANSFERRATE'],true) +'</span></h1></li>';
					break;
				}
				case 'Ping':
				case 'Ping_TBFClose':
				case 'Ping_TBFOpen':
				case 'Ping_GSMTotal':
				case 'Ping_CellFach':
				case 'Ping_WCDMATotal':
				case 'Ping_Other':
				case 'Ping_CellDch':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
				case 'HTTPPage_Login':
				case 'HTTPPage_Refresh':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('AVERAGE_TRANSMIT_DELAY',this.baseInfo['AVERAGE_TRANSMIT_DELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
				case 'EmailRecv':
				case 'EmailSend':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
				case 'VideoPlay':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlStrToPercent(this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
				case 'Speedtest':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span><br/>';
					rtn += '下行速率(KB/s):</span><span class="dl-field">'+ dlFormatDisplay('TRANSFERRATE',this.baseInfo['AVERAGE_SPEEDTEST_DOWNSPEED'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">上行速率(KB/s):</span><span class="dl-field">'+ dlFormatDisplay('TRANSFERRATE',this.baseInfo['AVERAGE_SPEEDTEST_UPSPEED'],true) +'</span></h1></li>';
					break;
				}
				case 'DNSLookup':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
				case 'PBM':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">掉线率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['DROPPEDRATE'],true) +'</span><br/>';
					rtn += '<span class="dl-label">下行速率(KB/s):</span><span class="dl-field">'+ dlFormatDisplay('TRANSFERRATE',this.baseInfo['AVERAGE_PBM_DOWNSPEED'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">上行速率(KB/s):</span><span class="dl-field">'+ dlFormatDisplay('TRANSFERRATE',this.baseInfo['AVERAGE_PBM_UPSPEED'],true) +'</span></h1></li>';
					break;
				}
				case 'SMSSend':
				case 'SMSRecv':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</hi></li>';
					break;
				}
				case 'MMSSend':
				case 'MMSRecv':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
				case 'TraceRoute':
				{
					rtn = '<li data-role="list-divider">';
					rtn += '<h1><span class="dl-label">成功率(%):</span><span class="dl-field">'+ dlFormatDisplay('SUCCESSRATE',this.baseInfo['SUCCESSRATE'],true) +'</span>&nbsp;&nbsp;<span class="dl-label">平均延时(s):</span><span class="dl-field">'+ dlFormatDisplay('BUSINESSDELAY',this.baseInfo['BUSINESSDELAY'],true) +'</span>';
					rtn += '</h1></li>';
					break;
				}
			}
		}
		return rtn;
	},

	parseStatics:function()
	{
		var rtn = '';
		if (this.staticsInfo) {
			var selectedP = this.selectedCommonParamIndex;
			if (selectedP == -1) {
				for (var i = 0; i < this.staticsInfo.length ; i++) {
					var p = this.staticsInfo[i];
					if (p.log) {
						selectedP = i;
						break;
					}
				}
			}
			var obj = this.staticsInfo[selectedP];
			var select = '<a name="commonParamSelect" id="commonParamSelect" href="#" class="ui-btn ui-icon-carat-d ui-btn-icon-right dl-NoteText">'+obj.name+'</a>';
			this.selectedCommonParamIndex = selectedP;
			rtn = '<li><table height="280px" class="fullSet noPadding noMargin">';
			rtn += '<tr width="100%" height="50px"><td>'+select+'</td></tr>';
			rtn += '<tr><td><table class="fullSet noPadding noMargin"><tr height="30px">';
			rtn += '<td class="dl-NoteText">Max:</td><td id="commonParamMax" class="dl-NoteText">50</td>';
			rtn += '<td class="dl-NoteText">Min:</td><td id="commonParamMin" class="dl-NoteText">20</td>';
			rtn += '<td class="dl-NoteText">Avg:</td><td id="commonParamAvg" class="dl-NoteText">45</td>';
			rtn += '<td colspan="2" width="50%">&nbsp;&nbsp;&nbsp;&nbsp;</td>';
			rtn += '</tr></table></td></tr>';
			rtn += '<tr><td height="230px"><div id="canvasDiv_s" class="fullSet ichartjs_div"></div></td></tr>';
			rtn += '</table></li>';
		}
		return rtn
	},

	printGridTable:function()
	{
		var rtn = '';
		if (this.baseInfo) {
			rtn = '<li ><table width="100%" class="dl-border"  rules="all" id="commonGridTable">';
			rtn += this.fliterGridInnerHtml();
			rtn += '</table></li>'
		}
		return rtn;
	},

	parseParameters:function()
	{
		var rtn = '';
		if (this.parameterInfo) {
			// var flag = DLBusinessEnum[this.business];
			// if (flag) {
				var selectedP = this.selectedParamIndex;
				var secIndex = 0,rowIndex = 0;
				if (selectedP == -1) {
					for (var i = 0; i < this.parameterInfo.length ; i++) {
						var obj = this.parameterInfo[i];
						for (var j = 0; j < obj.paramters.length; j++) {
							var p = obj.paramters[j];
							if (p.log) {
								selectedP = i*100 + j;
								secIndex = i;
								rowIndex = j;
								break;
							}
						}
					}
				}
				if(selectedP != -1)
				{
					var obj = this.parameterInfo[secIndex];
					var p = obj.paramters[rowIndex];
					var select = '<a name="paramSelect" id="paramSelect" href="#" class="ui-btn ui-icon-carat-d ui-btn-icon-right dl-NoteText">'+p.name+'</a>';
					this.selectedParamIndex = selectedP;
					rtn = '<li><table height="280px" class="fullSet noPadding noMargin">';
					rtn += '<tr width="100%" height="50px"><td>'+select+'</td></tr>';
					rtn += '<tr><td><table class="fullSet noPadding noMargin"><tr height="30px">';
					rtn += '<td class="dl-NoteText">Max:</td><td id="paramMax" class="dl-NoteText">50</td>';
					rtn += '<td class="dl-NoteText">Min:</td><td id="paramMin" class="dl-NoteText">20</td>';
					rtn += '<td class="dl-NoteText">Avg:</td><td id="paramAvg" class="dl-NoteText">45</td>';
					rtn += '<td colspan="2" width="50%">&nbsp;&nbsp;&nbsp;&nbsp;</td>';
					rtn += '</tr></table></td></tr>';
					rtn += '<tr><td height="230px"><div id="canvasDiv_p" class="fullSet ichartjs_div"></div></td></tr>';
					rtn += '<tr><td id="tabbarSapce"></td></tr>';
					rtn += '</table></li>';
				}
			// }
			for (var i = 0; i < this.parameterInfo.length ; i++) {
				var obj = this.parameterInfo[i];
				rtn += '<li ><table width="100%" class="dl-border" rules="all">';
				rtn += '<tr><td colspan="4" class="dl-bus">' + obj.net+ '</td></tr>';
				var dd = 0;
				for (var j = 0; j < obj.paramters.length; j++) {
					var p = obj.paramters[j];
					var sum = p.sum;
					var pvalue = sum['AVERAGE_'+p.key];
					if(pvalue != 0){
						if (!pvalue) {
						pvalue = sum['AVERAGE'];
					}
					}

					if (sum) {
						if (dd % 2 == 0) {
							rtn += '<tr>';
							rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">'+p.name +'</td>';
							rtn += '<td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(pvalue,1) + '</td>';
						}else{
							rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">'+p.name +'</td>';
							rtn += '<td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(pvalue,1) + '</td>';
							rtn += '</tr>';				
						}
						dd++;
					}
				}
				if (dd % 2 >0) {
					rtn += '<td class="dl-KPI"  style="word-break:break-all" width="25%"></td>';
					rtn += '<td class="dl-value"  style="word-break:break-all" width="25%"></td>';
					rtn += '</tr>';	
				}
				rtn += '</table></li>';
			}
		}
		return rtn;
	},

	printPublicChart:function()
	{
		if (!this.staticsInfo) {return};
		var i = this.selectedCommonParamIndex;
		var obj = this.staticsInfo[i];
		if (obj && obj.sum) {
			var sum = obj.sum;
			var key = obj.key
			$('#commonParamMax').text(dlToFixed(sum['MAX_'+key],1));
			$('#commonParamMin').text(dlToFixed(sum['MIN_'+key],1));
			$('#commonParamAvg').text(dlToFixed(sum['AVERAGE_'+key],1));

			var data = dlThresholdsDataMake(obj.log,false,obj.code,'PUBLIC');
			if (data) {
				dlDrawColumnChart("canvasDiv_s",data,obj.name);
			}
		}
	},

	printParamChart:function (isCake) {
		if (!this.parameterInfo) {return};
		// body...
		var i = this.selectedParamIndex;
		if (i < 0) {
			return;
		}
		var secction = Math.floor(i / 100);
		var row = i % 100;
		var netObj = this.parameterInfo[secction];
		var obj = netObj.paramters[row];
		if (obj && obj.sum) {
			var sum = obj.sum;
			var key = obj.key;
			$('#paramMax').text(dlToFixed(sum['MAX_'+key],1));
			$('#paramMin').text(dlToFixed(sum['MIN_'+key],1));
			$('#paramAvg').text(dlToFixed(sum['AVERAGE_'+key],1));

			if (isCake) {
				var data = dlThresholdsDataMake(obj.log,true,obj.code,netObj.net);
				if (data) {
					dlDrawPieChart("canvasDiv_p",data,obj.name);
				}
			}else{
				var data = dlThresholdsDataMake(obj.log,false,obj.code,netObj.net);
				if (data) {
					dlDrawColumnChart("canvasDiv_p",data,obj.name);
				}
			
			}
		}
	},

	fliterGridInnerHtml:function()
	{
		var rtn = '';
		if (this.addOn && this.addOn.length > 1) {
			for (var i = 0; i < this.addOn.length; i++) {
				var a = this.addOn[i];
				rtn += this.printBizFields(a.sub,a.data,true);
			}
		}
		else
		{
			rtn += this.printBizFields(this.business,this.baseInfo,false);
		}
			if (this.staticsInfo) {
				var obj = this.staticsInfo[this.selectedCommonParamIndex];
				rtn += '<tr><td width = "100%" colspan = "4" class="dl-bus">';
				rtn += obj.name;
				rtn += '</td></tr>';
				var gData = obj.log;
				if (isArray(gData)) {

					gData = dlCalculationThresholdCount(gData,obj.name);

					var dd = (gData.length % 2 != 0)?gData.length + 1:gData.length;
					for (var i = 0; i < dd; i++) {
						if (i < gData.length) {
							var thres = gData[i];
							var ps = thres.y*100;
							if (ps >= 0) {
								ps = ps.toFixed(1) + '%';
							}
							else
							{
								ps = '--';
							}
							if (i % 2 == 0) {
								rtn += '<tr><td class="dl-KPI" style="word-break:break-all" width="25%">';
								rtn += thres['name'];
								rtn += '</td><td class="dl-value" style="word-break:break-all" width="25%">';
								rtn += ps;
								rtn += '</td>'
							}
							else
							{
								rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">';
								rtn += thres['name'];
								rtn += '</td><td class="dl-value" style="word-break:break-all" width="25%">';
								rtn += ps;
								rtn += '</td></tr>';
							}
						}
						else
						{
							var thres = gData[i];
							if (i % 2 != 0) {
								rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">';
								rtn += '</td><td class="dl-value" style="word-break:break-all" width="25%">';
								rtn += '</td></tr>';
							}
						}
						
					}
				}
			}
		return rtn;
	},

	printBizFields:function(biz,data,showName)
	{
		var rtn = '';
		if (showName) {
			rtn += '<tr>';
			var subName = DLVoiceNameEnumDetail[biz];
			if (!subName) {
				subName = biz;
			}
			rtn += '<td class="dl-subBus" colspan="4">'+ subName +'</td>';
			rtn += '</tr>';
		};
		switch(biz)
		{
			case 'CS':
			case 'MO_CS':
			case 'MT_CS':
			{

				var att = data['BUSINESSCOUNT_ATTEMPTS'];
				var block = data['BUSINESSCOUNT_BLOCKEDCALL'];
				var succ = dlToFixed(parseInt(att) - parseInt(block));
				rtn += '<tr>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">试呼次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(att) +'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">未接通</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(block) + '</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">接通次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(succ) + '</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">掉话次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlToFixed(data['BUSINESSCOUNT_DROPPEDCALL']) +'</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">接通率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE_SUCCESSRATE'],true)+'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">掉话率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('SUCCESSRATE',data['DROPPEDRATE_DROPPEDRATE'],true)+'</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td colspan="2" class="dl-KPI" style="word-break:break-all" width="50%">平均呼叫建立时延(s)</td><td colspan="2" class="dl-value" style="word-break:break-all" width="50%">'+ dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY_CALLDELAY'],true) +'</td>';
				rtn += '</tr>';
				break;
			}
			case 'CSFB':
			case 'MO_CSFB':
			case 'MT_CSFB':
			{
					
				var att = data['BUSINESSCOUNT_ATTEMPTS'];
				var block = data['BUSINESSCOUNT_BLOCKEDCALL'];
				var succ = dlToFixed(parseInt(att) - parseInt(block));
				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">试呼次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(att,0) +'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">未接通</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(block,0) + '</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">接通次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlToFixed(succ,0) + '</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">掉话次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlToFixed(data['BUSINESSCOUNT_DROPPEDCALL'],0) +'</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">接通率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE_SUCCESSRATE'],true)+'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">掉话率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('SUCCESSRATE',data['DROPPEDRATE_DROPPEDRATE'],true)+'</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td colspan="2" class="dl-KPI" style="word-break:break-all" width="50%">平均呼叫建立时延(s)</td><td colspan="2" class="dl-value" style="word-break:break-all" width="50%">'+ dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY_CALLDELAY'],true) +'</td>';
				rtn += '</tr>';
				break;
			}
			case 'Network_Connect':
			{
				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT'])+'</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
				rtn += '</tr>';
				break;
			}
			case 'FTPDownload':
			case 'MultiFTPDownload':
			{
				rtn += '<tr height="25px">';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
				rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总下载数据(KB)</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('TRANSFERSIZE',data['TRANSFERSIZE'],true) + '</td>';
				rtn += '</tr>';

				rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT'])+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总下载时间</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERTIME',data['TRANSFERTIME'])+'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">下载速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['TRANSFERRATE'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'FTPUpload':
				case 'MultiFTPUpload':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总上传数据(KB)</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('TRANSFERSIZE',data['TRANSFERSIZE'],true) + '</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总上传时间</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERTIME',data['TRANSFERTIME']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">上传速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['TRANSFERRATE'],true) +'</td>';
					rtn += '</tr>';
					break;
				}
				case 'Ping':
				case 'Ping_TBFClose':
				case 'Ping_TBFOpen':
				case 'Ping_GSMTotal':
				case 'Ping_CellFach':
				case 'Ping_CellDch':
				case 'Ping_WCDMATotal':
				case 'Ping_Other':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT'])+'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'HTTPPage_Login':
				case 'HTTPPage_Refresh':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT'])+'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('AVERAGE_TRANSMIT_DELAY',data['AVERAGE_TRANSMIT_DELAY'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'HTTPDownload':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总下载数据(KB)</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('TRANSFERSIZE',data['TRANSFERSIZE'],true) + '</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总下载时间</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERTIME',data['TRANSFERTIME'])+'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">下载速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('TRANSFERRATE',data['TRANSFERRATE'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'HTTPUpload':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总上传数据(KB)</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('TRANSFERSIZE',data['TRANSFERSIZE'],true) + '</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总上传时间</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERTIME',data['TRANSFERTIME']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">上传速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('TRANSFERRATE',data['TRANSFERRATE'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'EmailRecv':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">接收速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('TRANSFERRATE',data['TRANSFERRATE'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'EmailSend':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">发送速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('TRANSFERRATE',data['TRANSFERRATE'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'VideoPlay':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">播放成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">缓冲次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">掉线率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('DROPPEDRATE',data['DROPPEDRATE'],true) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">VMOS</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['AVERAGE_V-MOS']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">不同步概率</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('DROPPEDRATE',data['AVERAGE_ASYNC']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">总比特率</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['AVERAGE_BITRATE']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">品质类型</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('BUSINESSCOUNT',data['AVERAGE_QUALITYTYPE']) +'</td>';
					rtn += '</tr>';
					break;
				}
				case 'Speedtest':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">上行速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['AVERAGE_SPEEDTEST_UPSPEED'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">下行速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['AVERAGE_SPEEDTEST_DOWNSPEED'],true) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%"></td><td class="dl-KPI" style="word-break:break-all" width="25%"></td>';
					rtn += '</tr>';
					break;
				}
				case 'DNSLookup':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT']) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">失败次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('BUSINESSCOUNT',data['FAILURECOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%"></td><td class="dl-KPI" style="word-break:break-all" width="25%"></td>';
					rtn += '</tr>';
					break;
				}
				case 'PBM':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">掉线率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('SUCCESSRATE',data['DROPPEDRATE'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%"></td><td class="dl-value" style="word-break:break-all" width="25%"></td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均上行速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['AVERAGE_PBM_UPSPEED'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均下行速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['AVERAGE_PBM_DOWNSPEED'],true) +'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">最大上行速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['AVERAGE_PBM_MAXUPSPEED'],true) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">最大下行速率(KB/s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('TRANSFERRATE',data['AVERAGE_PBM_MAXDOWNSPEED'],true) +'</td>';
					rtn += '</tr>';
					break;
				}
				case 'SMSSend':
				case 'MMSSend':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">发送成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'MMSRecv':
				case 'SMSRecv':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">接收成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
				case 'TraceRoute':
				{
					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">尝试次数</td><td class="dl-value" style="word-break:break-all" width="25%">' + dlFormatDisplay('BUSINESSCOUNT',data['BUSINESSCOUNT']) +'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功次数</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSCOUNT',data['SUCCESSCOUNT'])+'</td>';
					rtn += '</tr>';

					rtn += '<tr height="25px">';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">成功率(%)</td><td class="dl-value" style="word-break:break-all" width="25%">'+ dlFormatDisplay('SUCCESSRATE',data['SUCCESSRATE'],true)+'</td>';
					rtn += '<td class="dl-KPI" style="word-break:break-all" width="25%">平均延时(s)</td><td class="dl-value" style="word-break:break-all" width="25%">'+dlFormatDisplay('BUSINESSDELAY',data['BUSINESSDELAY'],true)+'</td>';
					rtn += '</tr>';
					break;
				}
			}
		return rtn;	
	}
}