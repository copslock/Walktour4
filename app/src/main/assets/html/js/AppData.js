
//统计数据
function StaticsData(isEN){
	this.isEN = isEN;
	this.sheets =[];
}

StaticsData.prototype = {
	//解析数据
	parseData:function(rs,desc){
		//检查数据格式
		var rsJson = rs;
		var dJson = desc;
		if (typeof rs == 'string') {
			rsJson = eval('('+rs+')'); 
		};
		if (typeof desc == 'string') {
			dJson = eval('('+desc+')'); 
		};
		//解析json
		this.sheets = [];
		var sheetsCount = dJson["Sheets"].length;
		for (var i = 0;i < sheetsCount; i++) {
			var s = dJson["Sheets"][i];
			var sheetData = new SheetData(s['SheetName']);
			//解析sheet 中的group
			var groups = s['Groups'];
			for (var gIndex = 0;gIndex < groups.length;gIndex++) {
				var sGroup = groups[gIndex];
				//开始解析数据
				var businesses = sGroup['Businesses'];
				for (var bIndex = 0; bIndex < businesses.length;bIndex++) {
					var business = businesses[bIndex];
					var gData = new GroupData((this.isEN) ? business['BusinessNameEN'] : business['BusinessName']);
					var styles = business['Styles'];
					//解析style
					for (var sIndex = 0; sIndex < styles.length; sIndex++) {
						var style = styles[sIndex];
						var tags = style['Tags'];
						//解析tag
						for (var tIndex = 0;tIndex < tags.length;tIndex++) {
							var tag = tags[tIndex];
							var tData = rsJson[parseTagName(tag['TagNameSum'])];
							if (tData instanceof Array && tData.length) {
								tData = tData[0];
							}
							else{
								tData = false;
							}
							//解析数据
							var tFields = tag['Results'];
							var count = 0;
							if (tData != false) {
								var fValue = false;
								for (var key in tData) {
									if (count <  tFields.length) {
										fValue = tData[key];
										if (isNullValue(fValue)) {
											fValue = "--";
										}
										var item = new StatItem((this.isEN) ? tFields[count]['ExcelTitleEN'] : tFields[count]['ExcelTitle'],fValue);
										gData.addItem(item);
									}
									count++;
								};
							}
							if (count == 0 ) {
								for (count == 0; count < tFields.length; count++) {
									var item = new StatItem((this.isEN) ? tFields[count]['ExcelTitleEN'] : tFields[count]['ExcelTitle'],"--");
										gData.addItem(item);
								}
							}
						};
					}

					//加入
					sheetData.appendGroup(gData);
				}
			}
			//入库
			this.sheets.push(sheetData);
		};
		return true;
	},
	//获取工作表数量
	getSheetsCount:function(){
		return this.sheets.length;
	},
	//通过下标获取工作表数据
	getSheetDataAtIndex:function(index){
		return this.sheets[index];
	},

}

//工作表数据
function SheetData(name)
{
	this.name = name;
	this.stat = [];
}

SheetData.prototype = {
	//获取名称
	getName:function()
	{
		return this.name;
	},

	appendGroup:function(g)
	{
		this.stat.push(g);
	},

	removeGroup:function(index)
	{
		this.stat.splice(index,1);
	},

	getGroupCount:function()
	{
		return this.stat.length;
	},

	getGroup:function(index)
	{
		return this.stat[index];
	},
}

function GroupData(name)
{
	this.name = name;
	this.items = [];
}

GroupData.prototype = {

	getName:function()
	{
		return this.name;
	},

	addItem:function(item){
		this.items.push(item);
	},

	removeItem:function(index){
		this.items.splice(index,1);
	},

	getItemCount:function(){
		return this.items.length;
	},

	getItem:function(index)
	{
		return this.items[index];
	},
}

function StatItem(n,v)
{
	this.name = n;
	if (typeof v == 'string') {
		this.value = v;
	}
	else{
		this.value = v.toFixed(2);
	}
}

StatItem.prototype = {
	getName:function(){
		return this.name;
	},
	setName:function(n)
	{
		this.name = n;
	},

	getValue:function(){
		return this.value;
	},
	setValue:function(v){
		if (typeof v == 'string') {
			this.value = v;
		}
		else{
			this.value = v.toFixed(2);
		}
	},
}

function isEmptyObject(e) {  
    var t;  
    for (t in e)  
        return !1;  
    return !0  
}

function isNullValue(v){
	if(typeof v == "undefined")
		return !0;
	if (v == "NULL")
		return !0;
	return !1;	  
}

function parseTagName(tagname){
	var a = tagname.toLowerCase();
	if (a.indexOf("#dl_") == 0) {
		return tagname.substring(4);
	}
	return tagname;
}

function getTextBounds(v) {
    var d = document.getElementById('dvCompute');
    d.innerHTML = v;
    return { w: d.offsetWidth, h: d.offsetHeight };
}

function isSingleRow(v)
{
	var screenWidth = document.body.clientWidth/2;
	var w = getTextBounds(v).w;
	if (w > screenWidth) {
		return true;
	}
	return false;
}


function viewDidLoadData(data,profile,en) {

	var appData = new StaticsData(en == 'en');
	appData.parseData(data,profile);

	var tabs = [];
	var pages = [];
	var count = appData.getSheetsCount();
	
	var sheet,groud,item; 
	for (var p = 0; p < count; p++) {
		sheet = appData.getSheetDataAtIndex(p);
		tabs.push(sheet.getName());

	    var groupsArray = [];
	    for (var i=0; i< sheet.getGroupCount(); i++) {
	    	groud = sheet.getGroup(i);

	    	groupsArray[i] = {
	        	name: groud.getName(),
	        	items: [],
	        	show: true
	    	};
	    	
	    	var gridRow;
	    	var flag = -1;
	    	for (var j=0; j<groud.getItemCount(); j++) {
	    		item = groud.getItem(j);
	    		if (isSingleRow(item.getName() + ':' +item.getValue()))
	    		{
	    			//另开一行
	    			gridRow = {single:true,kpi1:item.getName(),value1:item.getValue(),kpi2:'',value2:''};
	    			groupsArray[i].items.push(gridRow);
	    		}
	    		else
	    		{
	    			if (flag == -1) {
	    				gridRow = {single:false,kpi1:item.getName(),value1:item.getValue(),kpi2:'',value2:''};
	    				groupsArray[i].items.push(gridRow);
	    				flag =  groupsArray[i].items.length -1;
	    			}
	    			else{
	    				gridRow = groupsArray[i].items[flag];
	    				gridRow.kpi2 = item.getName();
	    				gridRow.value2 = item.getValue();
	    				flag = -1;
	    			}
	    		}
	    	}
	    };
	   	pages.push(groupsArray);
	} 
        
	var appElement = document.querySelector('[ng-controller=MyCtrl]');
	var $scope = angular.element(appElement).scope();
	$scope.reloadData(tabs,pages);
};