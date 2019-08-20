
/*
*画统计柱状图表
*@param div 要绘制到的目标div id
*@param data 绘制的数据
*@param title 标题
*/

var DLScaleInterval = 100;
var DLScaleSplitCount = 5;

function dlDrawColumnChart(div,data,title)
{
	var src = (typeof div == "string")?$('#'+div):div;
	var max = DLScaleInterval;
	var min = 0;

	var data1 = data.column;
	var dMax  = 0;
	var dMin = 0;
	$.each(data1,function(n,value){
		if (dMax < value.y) {
			dMax = value.y;
		}
	});
	dMax = Math.ceil(dMax * 1008 / 1000);

	var data2 = data.lines;
		       
	src.highcharts({
        title: {
            text: null
        },
        subtitle: {
            text: null
        },
        xAxis: [{
            type: 'category',
            title: {
                text: null,
            },
        }],
        yAxis: [{ // Primary yAxis
            labels: {
                style: {
                    color: Highcharts.getOptions().colors[1]
                }
            },
            title: {
                text: null,
            },
            max:dMax,
            min:0,
        }, { // Secondary yAxis
            labels: {
                format: '{value}%',
                style: {
                    color: Highcharts.getOptions().colors[0]
                }
            },
            title: {
                text: null,
            },
            opposite: true,
            max:max,
            min:0,
        }],
        tooltip: {
        	enabled:false,
            shared: false
        },
        legend: {
            layout: 'horizontal',
            align: 'left',
            x: 30,
            verticalAlign: 'top',
            y: -15,
            floating: true,
            backgroundColor: 'rgba(255,255,255,0.3)'
        },
        series: [{
            name: 'Threshold',
            type: 'column',
            yAxis: 0,
            data: data1,

        }, {
            name: 'PDF',
            type: 'spline',
            data: data2[0].value,
            yAxis: 1,
            tooltip: {
                valueSuffix: '%'
            }
        }, {
            name: 'CDF',
            type: 'spline',
            data: data2[1].value,
            yAxis: 1,
            tooltip: {
                valueSuffix: '%'
            }
        }]
    });
}

/*
*画统计饼装图表
*@param div 要绘制到的目标div id
*@param data 绘制的数据
*@param title 标题
*/
function dlDrawPieChart(div,data,title)
{
	var src = (typeof div == "string")?$('#'+div):div;
	var tid = (typeof div == "string")?div:div.attr("id");
	var w = src.width();
	var h = src.height();
				
	src.highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: null
        },
        tooltip: {
            enabled:false
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                }
            }
        },
        series: [{
            type: 'pie',
            name: title,
            data: data
        }]
    });
}

function dlThresholdsDataMake(json,isCake,pName,net)
{
	var rtn = false;
	if(isArray(json))
	{
		var ths = dlGetThresholdSetting(pName,net);
		if (isCake) {
			var cs = ['#FFC0CB','#8B008B','#9932CC','#0000FF','#00FFFF','#008000','#000000','#FFA500','#FFFF00'];
			rtn = [];
			var all = 0;
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
					if (ths) {
						rec.color = ths[value.INTERVALNAME];
					}
					if (!rec.color) {
						rec.color = cs[n];
					}
					rtn.push(rec);
					all += rec.y;
				}
			});
			$.each(rtn,function(n,value){
				value.y = value.y *1.0 / all;
			});
		}
		else
		{
			var cs = ['#FFC0CB','#8B008B','#9932CC','#0000FF','#00FFFF','#008000','#000000','#FFA500','#FFFF00'];
			var bar = [];
			var lines = [
				{
					value:[],
				},
				{
					value:[],
				}
			];
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
					if (ths) {
						rec.color = ths[value.INTERVALNAME];
					}
					if (!rec.color) {
						rec.color = cs[n];
					}
					bar.push(rec);
					//pdf
					var pdf = lines[0].value;
					if (value.PDF) {
						pdf.push(parseFloat(value.PDF)*DLScaleInterval);
					}else
					{
						pdf.push(0);
					}
				
					var cdf = lines[1].value;
					if (value.CDF) {
						cdf.push(parseFloat(value.CDF)*DLScaleInterval);
					}else
					{
						cdf.push(0);
					}
				}
			});
			rtn = {'column':bar,'lines':lines};
		}
	}
	return rtn;
}