function dlBarchar(div,business,json)
{
	var src = (typeof div == "string")?$('#'+div):div;
	var tid = (typeof div == "string")?div:div.attr("id");
	var w = src.width();
	var h = src.height();
	var data = [
						{name : 'MAY\n2011',value : 0.4,color : '#c52120'},
						{name : 'JUN\n2011',value : 0.6,color : '#c52120'},
						{name : 'JUL\n2011',value : 1,color : '#c52120'},
						{name : 'DEC\n2011',value : 7.8,color : '#c52120'},
						{name : 'JAN\n2012',value : 11.8,color : '#c52120'}
					];
			var data1 = [
				        	{
				        		name : '',
				        		value:[16,20,38,52,92],
				        		color:'#34a1d9',
				        		line_width:1
				        	}
				       ];
		       
			var chart = new iChart.Column2D({
				render : tid,
				data : data,
				title:{
					text:"利用ichartjs制作漂亮图表",
                  color:"#111111",
                  fontsize:13,
                  font:"微软雅黑",
                  textAlign:"center",
                  height:30,
                  offsetx:0,
                  offsety:0
				},
				footnote:{
                  text:"  ",
                  color:"#111111",
                  fontsize:12,
                  font:"微软雅黑",
                  textAlign:"right",
                  height:20,
                  offsetx:0,
                  offsety:0
            	},
				width : w,
				height : h,
				padding:0,
				label : {
					fontsize:11,
					fontweight:600,
					color : '#666666'
				},
				gradient:true,
            	color_factor:0.2,
				shadow : true,
				shadow_blur : 2,
				shadow_color : '#aaaaaa',
				shadow_offsetx : 1,
				shadow_offsety : 0,
				background_color : '#f7f7f7',
				column_width : 50,

				animation : true,//开启过渡动画
				animation_duration:800,//800ms完成动画

				coordinate : {
					background_color : "rgba(255,255,255,0)",
					grid_color : '#c0c0c0',
					width : "70%",
					height:"80%",
					axis : {
						color : '#c0d0e0',
						width : [0, 0, 1, 0]
					},
					scale : [{
						position : 'left',
						start_scale : 0,
						end_scale : 12,
						scale_space : 2,
						scale_enable : false,
						label : {
							fontsize:11,
							fontweight:600,
							color : '#666666'
						}
					},{
						 position:'right',	
						 start_scale:0,
						 scale_space:20,
						 end_scale:120,
						 scale_enable : false,
						 scaleAlign:'right',
						 label:{
							fontsize:11,
							fontweight:600,
							color:'#666666'
						 }
					}]
				}
			});
			//构造折线图
			var line = new iChart.LineBasic2D({
				z_index:1000,
				data: data1,
				label:{
					color:'#4c4f48'
				},
				animation : true,//开启过渡动画
				animation_duration:800,//800ms完成动画
				point_space:chart.get('column_width')+chart.get('column_space'),
				scaleAlign : 'right',
				sub_option : {
					label:false,
					point_size:8
				},
				coordinate:chart.coo//共用坐标系
			});
			
			chart.plugin(line);
			
    chart.eventOff();
	chart.draw();
}

