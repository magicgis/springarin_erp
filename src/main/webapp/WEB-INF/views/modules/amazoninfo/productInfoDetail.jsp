<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${productName}汇总</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<style type="text/css">
	
		.desc th{
			text-align: center;
			vertical-align: middle;
		}
		.desc td{
			text-align: center;
			vertical-align: middle;
		}
		.spanexr {
			float: right;
			min-height: 40px
		}
		.spanexl {
			float: left;
		 }
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
		
		.btn-special {
		    color: #fff;
		    text-shadow: 0 -1px 0 rgba(0,0,0,0.25);
		    background-color: #DA70D6;
		    background-image: -moz-linear-gradient(top,#DA70D6,#DA70D6);
		    background-image: -webkit-gradient(linear,0 0,0 100%,from(#DA70D6),to(#DA70D6));
		    background-image: -webkit-linear-gradient(top,#DA70D6,#DA70D6);
		    background-image: -o-linear-gradient(top,#DA70D6,#DA70D6);
		    background-image: linear-gradient(to bottom,#DA70D6,#DA70D6);
		    background-repeat: repeat-x;
		    border-color: #DA70D6 #DA70D6 #DA70D6;
		    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
		    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#DA70D6',endColorstr='#DA70D6',GradientType=0);
		    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
       }
       
       .btn-special:hover, .btn-special:focus, .btn-special:active, .btn-special.active, .btn-special.disabled, .btn-special[disabled] {
			    color: #fff;
			    background-color: #DA70D6;
        }
        
        .btn-vendor {
		    color: #fff;
		    text-shadow: 0 -1px 0 rgba(0,0,0,0.25);
		    background-color: #FF8247;
		    background-image: -moz-linear-gradient(top,#FF8247,#FF8247);
		    background-image: -webkit-gradient(linear,0 0,0 100%,from(#FF8247),to(#FF8247));
		    background-image: -webkit-linear-gradient(top,#FF8247,#FF8247);
		    background-image: -o-linear-gradient(top,#FF8247,#FF8247);
		    background-image: linear-gradient(to bottom,#FF8247,#FF8247);
		    background-repeat: repeat-x;
		    border-color: #FF8247 #FF8247 #FF8247;
		    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
		    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#FF8247',endColorstr='#FF8247',GradientType=0);
		    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
       }
       
       .btn-vendor:hover, .btn-vendor:focus, .btn-vendor:active, .btn-vendor.active, .btn-vendor.disabled, .btn-vendor[disabled] {
			    color: #fff;
			    background-color: #FF8247;
        }
       
		#imgtest{  position:absolute;
	         top:100px; 
	         left:300px; 
	         z-index:1; 
	         } 
	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<script type="text/javascript">
	
	
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var rs = 0 ;
				var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
				var clsAttr = a.attr("class");
				if(clsAttr){
					if(clsAttr.indexOf('inverse')>0){
						rs = parseInt(a.text())*100000000000000;
					}else if (clsAttr.indexOf('special')>0){
						rs = parseInt(a.text())*1000000000000;
					}else if(clsAttr.indexOf('warning')>0){
						rs = parseInt(a.text())*10000000000;
					}else if (clsAttr.indexOf('danger')>0){
						rs = parseInt(a.text())*100000000;
					}else if (clsAttr.indexOf('primary')>0){
						rs = parseInt(a.text())*1000000;
					}else if (clsAttr.indexOf('info')>0){
						rs = parseInt(a.text())*10000;
					}else if (clsAttr.indexOf('success')>0){
						rs = parseInt(a.text())*100;
					}else{
						rs = parseInt(a.text());
					}
				}
				return rs;
			} );
		}	
	
	
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		/* <c:choose>
				<c:when test="${saleReport.searchType eq '1'}">
					<c:set var="type" value="" />
				</c:when>
				<c:when test="${saleReport.searchType eq '2'}">
					<c:set var="type" value="W" />
				</c:when>
				<c:otherwise>
					<c:set var="type" value="M" />
				</c:otherwise>
			</c:choose> 
		*/
		var map = {};
		map.de = 'EUR';
		map.fr = 'EUR';
		map.it = 'EUR';
		map.es = 'EUR';
		map.uk = 'GBP';
		map.ca = 'CDN$';
		map.com = 'USD';
		map.jp = 'JPY';
		map.mx = 'MXN$';
		
		$(document).ready(function() {
			//<c:if test="${fangChaFlag}">
				$("#tip").modal();
			//</c:if>
			var flag=0;
			//fba库存提示
			$(".fbaTip").popover({html:true,trigger:'hover',content:function(){
				var td=$(this).parent();
				params={};
				params.country= td.find(".country").val();
				params.name= td.find(".name").val();
				
				var $this = $(this);
				if(!$this.attr("content")){
					if(!$this.attr("data-content")){
						var content="";
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/psi/psiInventory/fba/ajaxSkuQuantity',
						    data: $.param(params),
						    success:function(data){ 
						    	content = data;
						    	$this.attr("content",data);
					        }
						});
						return content;
					}
				}
				return $this.attr("content");
				
			}});
			
			
			$(".image1").mouseover(function(e) { 
				if($(this).is("img")){ 
					var img=$("<img id='tipImg' src='"+$(this).attr("src").replace("/compressPic","")+"'>").css({ "height":$(this).height()*6, "width":$(this).width()*6});
					img.appendTo($("#imgtest"));
				}
			});
			
			$(".image1").mouseout(function() { 
				$("#tipImg").remove();
			}); 
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			$(".tipDay").popover({placement:'left',trigger:'hover',content:function(){
				var now = new Date();
				var zq = $(this).parent().parent().find("td:eq(12)").text();
				now.setDate(now.getDate()+parseInt(zq)+parseInt($.trim($(this).text())));
				return "可售至:"+now.Format("yyyy-MM-dd");
			}});
			
			var products="";
			$('#typeahead').typeahead({
				source: function (query, process) {
					if(!(products)){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/psi/psiInventory/getUnionProductNames' ,
						    dataType: 'json',
						    success:function(data){ 
						    	products = data;
					        }
						});
					}
					process(products);
			    },
				updater:function(item){
					window.location.href="${ctx}/psi/psiInventory/productInfoDetail?productName="+encodeURIComponent(item);
					return item;
				}
			});
			
			
			var arr = $(".sale1 .sale1");
			var num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t1").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			var arr = $(".sale1 .deSale1");
			var num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t1").append("&nbsp;<span class=\"btn btn-small btn-vendor\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			arr = $(".sale2");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t2").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			 arr = $(".sale3");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t3").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			 arr = $(".sale4");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t4").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			 arr = $(".sale5");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t5").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			 arr = $(".sale6");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t6").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			
			arr = $(".sale7");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t7").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			 arr = $(".sale8");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t8").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			 arr = $(".sale9");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t9").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			
			 arr = $(".sale11");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t11").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			
			
			arr = $(".sale10");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t10").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			arr = $(".sale12");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t12").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			arr = $(".sale13");
			 num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				$("#t13").append("<span class=\"btn btn-small\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}

			var arr2 = $(".v1");
			var dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt1").text(dnum.toFixed(2));
			
			arr2 = $(".v2");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt2").text(dnum.toFixed(2));
			
			arr2 = $(".v3");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt3").text(dnum.toFixed(2));
			
			arr2 = $(".v4");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt4").text(dnum.toFixed(2));
			
			arr2 = $(".v5");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt5").text(dnum.toFixed(2));
			
			arr2 = $(".v6");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt6").text(dnum.toFixed(2));
			
			arr2 = $(".v7");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt7").text(dnum.toFixed(2));
			
			arr2 = $(".v8");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});
			$("#vt8").text(dnum.toFixed(2));
			
			arr2 = $(".v9");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt9").text(dnum.toFixed(2));
			
			arr2 = $(".v11");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt11").text(dnum.toFixed(2));
			
			
			arr2 = $(".v10");
			dnum = 0.0;
			arr2.each(function() {
				if ($.trim($(this).text())) {
					dnum += parseFloat($(this).text());
				}
			});

			$("#vt10").text(dnum.toFixed(2));
			
			//-----------------------------------------------------------
			if($("#searchType").val()==1){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchType").val()==2){
		    	$("#showTab1").addClass("active");
		    }else if($("#searchType").val()==3){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    }
			
			oldSearchFlag= $("#searchType").val();
			
			$("#reback").click(function(){
				window.history.go(-1);				
			});
			
			$("table").css("margin-bottom","5px");
			
			
			
			
			$(".saleHref").click(function(e){
				e.preventDefault();
				if(!(myChart1.series)){
					myChart1.setOption(option1);
					$("#saleChart").css("width",$("#saleChart").parent().parent().parent().width()-20);
					myChart1.resize();
				}
				$(this).tab('show');
				var href=$(this).attr("href");
				if(href=="#saleVolume"){
					$("#exportDiv").show();
				}else{
					$("#exportDiv").hide();
				}
			});
			//下单数提示用
			var order ={};
			
			
			//销量图表  saleChart
			var myChart;
			var myChart1;
			var option1;
			<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">var myChart${dic.value};var option${dic.value}; var myChartS${dic.value};var optionS${dic.value};</c:if></c:forEach>
			
			require.config({
		        paths:{ 
		            echarts:'${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/line': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/bar': '${ctxStatic}/echarts/js/echarts'
		        }
		    });
		    // Step:4 require echarts and use it in the callback.
		    // Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
		    require(
		        ['echarts','echarts/chart/line','echarts/chart/bar'],
		        function(ec) {
		            myChart = ec.init(document.getElementById("chart"));
		            myChart.showLoading({
					    text: 'Loading data...',    //loading话术
					});
					//ecahrts-----------------
		            var option = {
		            	title:{text:'${productName} <spring:message code="amazon_product_sale_quantity_statistics"/>',x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}':false${i.last?'':','}</c:if></c:forEach>},
		                    data:['总计|TOTAL',<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}'${i.last?'':','}</c:if></c:forEach>]
		                },
		                toolbox: {
		                    show : true,
		                    feature : {
		                        mark : false,
		                        dataView :false,
		                        magicType:{type:['line','bar'],show:true},
		                        restore : {show:true},
		                        saveAsImage : {show:true}
		                    }
		                },
		                calculable : false,
		                animation:false,
		                xAxis : [
		                    {
		                       axisLabel: {
									rotate: 50
								}, 
		                        type : 'category',
		                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
		                   		boundaryGap:false
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        splitArea : {show : true},
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
							{
							      name:'总计|TOTAL',
							      type:'bar',
							      barWidth:16,
							      itemStyle : {
					                    normal: {
					                        color:'#8A8A8A',
					                        borderWidth:1,
					                        borderColor:'#8A8A8A'
					                    }
					              },
							      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty data[x]['total'].salesVolume?0:data[x]['total'].salesVolume}${i.last?'':','}</c:forEach>],
							      markLine : {
							      	 data : [
							              {type : 'average', name: 'Average Line'}
							          ]
							      }
							},
							//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
								{
								      name:'${dic.label}',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty data[x][dic.value].salesVolume?0:data[x][dic.value].salesVolume}${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: 'Average Line'}
								          ]
								      }
								}${i.last?'':','}
							//</c:if></c:forEach>
		                ]
		            };
		            myChart.setOption(option);
		            
		            //销售额chart
		            
		            myChart1 = ec.init(document.getElementById("saleChart"));
		            myChart1.showLoading({
					    text: 'Loading Data...',    //loading话术
					});
					//ecahrts-----------------
		            option1 = {
		            	title:{text:'${productName} <spring:message code="amazon_product_sales_statistics"/> ',x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}':false${i.last?'':','}</c:if></c:forEach>},
		                    data:['总计|TOTAL',<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}'${i.last?'':','}</c:if></c:forEach>]
		                },
		                toolbox: {
		                    show : true,
		                    feature : {
		                        mark : false,
		                        dataView :false,
		                        magicType:{type:['line','bar'],show:true},
		                        restore : {show:true},
		                        saveAsImage : {show:true}
		                    }
		                },
		                calculable : false,
		                animation:false,
		                xAxis : [
		                    {
		                       axisLabel: {
									rotate: 50
								}, 
		                        type : 'category',
		                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
		                   		boundaryGap:false
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        splitArea : {show : true},
			                    axisLabel : {
		                            formatter: '{value}$'
		                        },
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
							{
							      name:'总计|TOTAL',
							      type:'bar',
							      barWidth:16,
							      itemStyle : {
					                    normal: {
					                        color:'#8A8A8A',
					                        borderWidth:1,
					                        borderColor:'#8A8A8A'
					                    }
					              },
							      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty data[x]['total'].sales?0:data[x]['total'].sales}"  maxFractionDigits="2"  />${i.last?'':','}</c:forEach>],
							      markLine : {
							      	 data : [
							              {type : 'average', name: 'Average Line'}
							          ]
							      }
							},
							//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
								{
								      name:'${dic.label}',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data[x][dic.value].sales?0:data[x][dic.value].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: 'Average Line'}
								          ]
								      }
								}${i.last?'':','}
							//</c:if></c:forEach>
		                ]
		            };
		            
		        }
		    );
			
		  	$("#saleVolumeTb tbody tr").each(function(i){
		    	var total = $.trim($(this).find("td :eq(12)").text());
		    	if(total){
		    		for ( var j = 2; j <= 10; j++) {
		    			var single = $(this).find("td:eq("+j+")").text();
			    		if(single){
			    			 $("#contrastTb tbody tr:eq("+i+")").find("td:eq("+j+")").text((single*100/total).toFixed(2)+"%");
			    		} 
					}
		    	}
		    });
		  	
		  	var trTfoot = $("#saleVolumeTb tfoot tr:eq(0)");
	    	var total = $.trim(trTfoot.find("td :eq(12)").text());
	    	if(total){
	    		for ( var j = 1; j <= 9; j++) {
	    			var single = trTfoot.find("td:eq("+j+")").text();
		    		if(single){
		    			 $("#contrastTb tfoot tr:eq(0)").find("td:eq("+j+")").text((single*100/total).toFixed(2)+"%");
		    		} 
				}
	    	}
		    
			
		    $("#btnSubmit").click(function(){
				$("#searchForm").submit();
			});
		    
		    $("#btnExp").click(function(){
		    	//${ctx}/psi/psiInventory/productInfoDetail
		    	$("#searchForm").attr("action","${ctx}/psi/psiInventory/expSingleProductSales");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/psi/psiInventory/productInfoDetail");
			});
			
			$("#saleTb").dataTable({
		    	"searching":false,
				"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
			 $("#saleVolumeTb").dataTable({
			    	"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"aoColumns": [
							         null,
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
					],
					"ordering" : true,
					"aaSorting": [[ 0, "desc" ]]
				});
			 
			 $("#contrastTb").dataTable({
			    	"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					"aaSorting": [[ 0, "desc" ]]
				});
			 
			
			 $(".pagination a").addClass("nava");
			 
			 //快捷下单
			 //<shiro:hasPermission name="psi:order:edit">
			 	$("#order").click(function(){
			 		var paramStr = "";
			 		var i = 0;
			 		$("#orderTb tbody tr .orderQ").each(function(){
			 			if($.trim($(this).text())){
			 				if(i==0){
								paramStr +="?";
							}else{
								paramStr +="&";
							}
			 				paramStr +="items["+i+"].quantityOrdered="+$.trim($(this).text());
			 				paramStr +="&items["+i+"].product.id=${product.id}";
			 				paramStr +="&items["+i+"].countryCode="+$(this).attr("key");
			 				paramStr +="&items["+i+"].colorCode=${color}";
			 				paramStr +="&items["+i+"].productName=${product.brand} ${product.model}";
			 				i++;				
			 			}
			 		});
			 		
			 		$(this).attr("href","${ctx}/psi/purchaseOrder/fastCreateOrder"+paramStr)
			 	});
			 //</shiro:hasPermission>
              
			 $("#showDetail").click(function(){
					
					var params={};
					params.productName="${productName}";
					var cssVal=$("#code").css("display");
					if(cssVal=="none"){
						$("#code").css("display","block");
						if(flag==0){
							$.ajax({
							    type: 'post',
							    async:false,
							    url: '${ctx}/psi/psiInventory/getAmazonDetail',
							    data: $.param(params),
							    success:function(msg){ 
									var tr="";
								    var tbody=$("#detailTable tbody");
								    tr+="<tr>";
								    tr+="<td><spring:message code='amazon_order_form4'/></td>";
								    tr+="<td><spring:message code='psi_product_shelvesTime'/></td>";
								    tr+="<td><spring:message code='psi_product_new'/></td>";
								    tr+="<td><spring:message code='psi_product_position'/></td>";
								    tr+="<td>Account</td>";
								    tr+="<td><spring:message code='amazon_product_type'/></td>";
								    tr+="<td><spring:message code='amazon_product_strichcode'/></td>";
								    tr+="<td>SKU</td>";
								    tr+="<td>EAN</td>";
								    tr+="<td>ASIN</td>";
								    tr+="<td><spring:message code='amazon_product_price'/></td>";
								    tr+="<td><spring:message code='psi_product_shelvesTime1'/></td>";
								    tr+="<td><spring:message code='psi_product_shelvesTime2'/></td>";
								   
								    tr+="</tr>";
								   
								    tr+=showAttr(msg['de'],"${fns:getDictLabel('de', 'platform', defaultValue)}");
								    tr+=showAttr(msg['uk'],"${fns:getDictLabel('uk', 'platform', defaultValue)}");
								    tr+=showAttr(msg['fr'],"${fns:getDictLabel('fr', 'platform', defaultValue)}");
								    tr+=showAttr(msg['it'],"${fns:getDictLabel('it', 'platform', defaultValue)}");
								    tr+=showAttr(msg['es'],"${fns:getDictLabel('es', 'platform', defaultValue)}");
								    tr+=showAttr(msg['com'],"${fns:getDictLabel('com', 'platform', defaultValue)}");
								    tr+=showAttr(msg['com2'],"${fns:getDictLabel('com2', 'platform', defaultValue)}");
								    tr+=showAttr(msg['com3'],"${fns:getDictLabel('com3', 'platform', defaultValue)}");
								    tr+=showAttr(msg['jp'],"${fns:getDictLabel('jp', 'platform', defaultValue)}");
								    tr+=showAttr(msg['ca'],"${fns:getDictLabel('ca', 'platform', defaultValue)}");
								    tr+=showAttr(msg['mx'],"${fns:getDictLabel('mx', 'platform', defaultValue)}");
								   

								    /* for(var i=0;i<msg.length;i++){
								    	    tr+="<tr>";
								    	    tr+="<td>"+msg[i].accountName+"</td>";
										    tr+="<td>"+(msg[i].barcodeType==undefined?'':msg[i].barcodeType)+"</td>";
										    tr+="<td>";
										    tr+="<a class="+(msg[i].barcode==undefined?'':'btn  btn-small')+" href='${ctx}/psi/product/genBarcode?country="+msg[i].country+"&type="+(msg[i].barcodeType)+"&productName=${productName}&barcode="+(msg[i].barcode)+"' target='_blank'>"+(msg[i].barcode==undefined?'':msg[i].barcode)+"</a>"
										    tr+="</td>";		
										    tr+="<td>";
										    if(msg[i].sku!=undefined && msg[i].sku!='-'){
										    	    var sku=msg[i].sku.split(",");
												    for(var j in sku){
												    	tr+=sku[j]+"<br/>";
												    }
										    }
										   
										    tr+="</td>";
										    tr+="<td>";
										 
										    if(msg[i].ean!=undefined && msg[i].ean!='-'){
										    	  var ean=msg[i].ean.split(",");
												  for(var j in ean){
												    tr+=ean[j]+"<br/>";
												  }
										    }
										   
										    tr+="</td>";
										    tr+="<td>";
										    if(msg[i].asin!=undefined && msg[i].asin!='-'){
										    	    var asin=msg[i].asin.split(",");
												    var country=msg[i].country;
												    if(country=="jp"||country=="uk"){
												    	country="co."+country;
												    }else if(country=="mx"){
												    	country="com.mx";
												    }else if(country.indexOf("com")>=0){
												    	country="com";
												    }
												    for(var j in asin){
												    	tr+="<a target='_blank' href='http://www.amazon."+country+"/dp/"+asin[j]+"'>"+asin[j]+"</a><br/>";
												    }
										    }
										   
										    tr+="</td>";
										    tr+="<td>";
										    if(msg[i].price!=undefined && msg[i].price!='-'){
									    	    var price=msg[i].price.split(",");
											    for(var j in price){
											    	tr+=price[j]+"<br/>";
											    }
									       }
										   
										    tr+="</td>";
										   
										    tr+="<td>"+msg[i].addedMonth+"</td>";
										    tr+="<td>"+msg[i].period+"</td>";
										    tr+="<td>"+msg[i].salesDate+"</td>";
										    if(msg[i].newAttr=='1'){
												 tr+="<td><spring:message code='psi_product_new'/></td>";
											}else{
												 tr+="<td><spring:message code='psi_product_noNew'/></td>";
											}
										  
										    var productPositionMap = new Map();
											productPositionMap.set('1',"${fns:getDictLabel('1','product_position','')}");
											productPositionMap.set('2',"${fns:getDictLabel('2','product_position','')}");
											productPositionMap.set('3',"${fns:getDictLabel('3','product_position','')}");
										    productPositionMap.set('4',"${fns:getDictLabel('4','product_position','')}");
											  
										    if(msg[i].salesAttr=="1"){
										    	tr+="<td>"+productPositionMap.get('1')+"</td>";
										    }else  if(msg[i].salesAttr=="2"){
										    	tr+="<td>"+productPositionMap.get('2')+"</td>";
										    }else  if(msg[i].salesAttr=="3"){
										    	tr+="<td>"+productPositionMap.get('3')+"</td>";
										    }else  if(msg[i].salesAttr=="4"){
										    	tr+="<td>"+productPositionMap.get('4')+"</td>";
										    }else{
										    	tr+="<td></td>";
										    }
										    
											
										    tr+="</tr>";
			 	                    } */
									tbody.append(tr);
									flag="1";
						        }
							});
							
						}
					}else{
						$("#code").css("display","none");
					}
				});
              
			 
			 
              var goodsFlag=0;
              $("#showGoodsDetail").click(function(){
					var params={};
					params.productName="${productName}";
					params.start=$("#start").val();
					params.end=$("#end").val();
					var cssVal=$("#goodsCode").css("display");
					if(cssVal=="none"){
						$("#goodsCode").css("display","block");
						if(goodsFlag==0){
							$.ajax({
							    type: 'post',
							    async:false,
							    url: '${ctx}/psi/psiInventory/getReturnGoods',
							    data: $.param(params),
							    success:function(data){
							    	var tr="";
								    var tbody=$("#detailGoodsTable tbody");
								    tr+="<tr class='alert alert-block'>";
								    tr+="<td><spring:message code="amazon_product_rate2"/></td>";
								    tr+="<td>";
							    	if(data['returnGoods']['de']!=undefined&&data['returnGoods']['de'][5]!=undefined){
							    		tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['de'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['de'][2]+"'>"+ data['returnGoods']['de'][5]+"%</a>";
							    	}
							    	tr+="</td>";
							    	tr+="<td>";
								    if(data['returnGoods']['uk']!=undefined&&data['returnGoods']['uk'][5]!=undefined){
								    		tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['uk'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['uk'][2]+"'>"+ data['returnGoods']['uk'][5]+"%</a>";
								    }
								    tr+="</td>";
								    tr+="<td>";
							    	if(data['returnGoods']['fr']!=undefined&&data['returnGoods']['fr'][5]!=undefined){
							    		tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['fr'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['fr'][2]+"'>"+ data['returnGoods']['fr'][5]+"%</a>";
							    	}
							    	tr+="</td>";
							    	tr+="<td>";
								    if(data['returnGoods']['it']!=undefined&&data['returnGoods']['it'][5]!=undefined){
								    	tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['it'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['it'][2]+"'>"+ data['returnGoods']['it'][5]+"%</a>";
								    }
								    tr+="</td>";
								    tr+="<td>";
									if(data['returnGoods']['es']!=undefined&&data['returnGoods']['es'][5]!=undefined){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['es'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['es'][2]+"'>"+ data['returnGoods']['es'][5]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['com']!=undefined&&data['returnGoods']['com'][5]!=undefined){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['com'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['com'][2]+"'>"+ data['returnGoods']['com'][5]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['jp']!=undefined&&data['returnGoods']['jp'][5]!=undefined){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['jp'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['jp'][2]+"'>"+ data['returnGoods']['jp'][5]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['ca']!=undefined&&data['returnGoods']['ca'][5]!=undefined){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['ca'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['ca'][2]+"'>"+ data['returnGoods']['ca'][5]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['mx']!=undefined&&data['returnGoods']['mx'][5]!=undefined){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['mx'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['mx'][2]+"'>"+ data['returnGoods']['mx'][5]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['total']!=undefined&&data['returnGoods']['total'][5]!=undefined){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_sure'/>:"+data['returnGoods']['total'][1]+"<br/><spring:message code='amazon_sales_return'/>:"+data['returnGoods']['total'][2]+"'>"+ data['returnGoods']['total'][5]+"%</a>";
									}
									tr+="</td>";
									
							    	tr+="</tr>";
							    	
							    	tr+="<tr class='alert alert-error'>";
							    	tr+="<td><spring:message code="amazon_product_rate1"/></td>";
							    	tr+="<td>";
									if(data['returnGoods']['de']!=undefined&&data['returnGoods']['de'][6]!=undefined&&data['returnGoods']['de'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['de'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['de'][3]+"'>"+ data['returnGoods']['de'][6]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['uk']!=undefined&&data['returnGoods']['uk'][6]!=undefined&&data['returnGoods']['uk'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['uk'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['uk'][3]+"'>"+ data['returnGoods']['uk'][6]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['fr']!=undefined&&data['returnGoods']['fr'][6]!=undefined&&data['returnGoods']['fr'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['fr'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['fr'][3]+"'>"+ data['returnGoods']['fr'][6]+"%</a>";
									}
									tr+="</td>";
									
									tr+="<td>";
									if(data['returnGoods']['it']!=undefined&&data['returnGoods']['it'][6]!=undefined&&data['returnGoods']['it'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['it'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['it'][3]+"'>"+ data['returnGoods']['it'][6]+"%</a>";
									}
									tr+="</td>";
									
									tr+="<td>";
									if(data['returnGoods']['es']!=undefined&&data['returnGoods']['es'][6]!=undefined&&data['returnGoods']['es'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['es'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['es'][3]+"'>"+ data['returnGoods']['es'][6]+"%</a>";
									}
									tr+="</td>";
									
									tr+="<td>";
									if(data['returnGoods']['com']!=undefined&&data['returnGoods']['com'][6]!=undefined&&data['returnGoods']['com'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['com'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['com'][3]+"'>"+ data['returnGoods']['com'][6]+"%</a>";
									}
									tr+="</td>";
									
									tr+="<td>";
									if(data['returnGoods']['jp']!=undefined&&data['returnGoods']['jp'][6]!=undefined&&data['returnGoods']['jp'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['jp'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['jp'][3]+"'>"+ data['returnGoods']['jp'][6]+"%</a>";
									}
									tr+="</td>";
									
									tr+="<td>";
									if(data['returnGoods']['ca']!=undefined&&data['returnGoods']['ca'][6]!=undefined&&data['returnGoods']['ca'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['ca'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['ca'][3]+"'>"+ data['returnGoods']['ca'][6]+"%</a>";
									}
									tr+="</td>";
									tr+="<td>";
									if(data['returnGoods']['mx']!=undefined&&data['returnGoods']['mx'][6]!=undefined&&data['returnGoods']['mx'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['mx'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['mx'][3]+"'>"+ data['returnGoods']['mx'][6]+"%</a>";
									}
									tr+="</td>";
									
									tr+="<td>";
									if(data['returnGoods']['total']!=undefined&&data['returnGoods']['total'][6]!=undefined&&data['returnGoods']['total'][6]!=''){
									    tr+="<a style='color: #08c;' href='#' data-toggle='popover' data-html='true' rel='popover' data-content='<spring:message code='amazon_sales_order_quantity'/>:"+data['returnGoods']['total'][4]+"<br/><spring:message code='amazon_sales_difference_evaluation'/>:"+data['returnGoods']['total'][3]+"'>"+ data['returnGoods']['total'][6]+"%</a>";
									}
									tr+="</td>";
							    	tr+="</tr>";
							    	
							    	tbody.append(tr);
							    	$("a[rel='popover']").popover({trigger:'hover'});
							    }
							});
						}
						goodsFlag=1;
					}else{
						$("#goodsCode").css("display","none");
					}	
			});	
              
            
		});
		
		
		function showAttr(data,country){
			 var tr="";  
			 if(data!=null&&data!=undefined ){
				    var productPositionMap = new Map();
					productPositionMap.set('1',"${fns:getDictLabel('1','product_position','')}");
					productPositionMap.set('2',"${fns:getDictLabel('2','product_position','')}");
					productPositionMap.set('3',"${fns:getDictLabel('3','product_position','')}");
				    productPositionMap.set('4',"${fns:getDictLabel('4','product_position','')}");
					
			    	for(var i in data){
			    		tr+="<tr>";
			    		if(i==0){
				    			 tr+="<td rowspan='"+data.length+"'>"+country+"</td>";
				    			 tr+="<td rowspan='"+data.length+"'>"+(data[i].salesDate==undefined?'':data[i].salesDate)+"</td>";
								 if(data[i].newAttr=='1'){
										 tr+="<td rowspan='"+data.length+"'><spring:message code='psi_product_new'/></td>";
								}else{
										 tr+="<td rowspan='"+data.length+"'><spring:message code='psi_product_noNew'/></td>";
								}
							  
							    if(data[i].salesAttr=="1"){
							    	tr+="<td rowspan='"+data.length+"'>"+productPositionMap.get('1')+"</td>";
							    }else  if(data[i].salesAttr=="2"){
							    	tr+="<td rowspan='"+data.length+"'>"+productPositionMap.get('2')+"</td>";
							    }else  if(data[i].salesAttr=="3"){
							    	tr+="<td rowspan='"+data.length+"'>"+productPositionMap.get('3')+"</td>";
							    }else  if(data[i].salesAttr=="4"){
							    	tr+="<td rowspan='"+data.length+"'>"+productPositionMap.get('4')+"</td>";
							    }else{
							    	tr+="<td rowspan='"+data.length+"'></td>";
							    }
			    		}
			    		
			    		tr+="<td>"+data[i].accountName+"</td>";
			    		tr+="<td>"+(data[i].barcodeType==undefined?'':data[i].barcodeType)+"</td>";
			    		tr+="<td>";
						tr+="<a class="+(data[i].barcode==undefined?'':'btn  btn-small')+" href='${ctx}/psi/product/genBarcode?country="+data[i].country+"&type="+(data[i].barcodeType)+"&productName=${productName}&barcode="+(data[i].barcode)+"' target='_blank'>"+(data[i].barcode==undefined?'':data[i].barcode)+"</a>"
						tr+="</td>";	
						    
			    		tr+="<td>";
					    if(data[i].sku!=undefined && data[i].sku!='-'){
					    	    var sku=data[i].sku.split(",");
							    for(var j in sku){
							    	tr+=sku[j]+"<br/>";
							    }
					    }
					   
					    tr+="</td>";
					    tr+="<td>";
					 
					    if(data[i].ean!=undefined && data[i].ean!='-'){
					    	  var ean=data[i].ean.split(",");
							  for(var j in ean){
							    tr+=ean[j]+"<br/>";
							  }
					    }
					   
					    tr+="</td>";
					    tr+="<td>";
					    if(data[i].asin!=undefined && data[i].asin!='-'){
					    	    var asin=data[i].asin.split(",");
							    var country=data[i].country;
							    if(country=="jp"||country=="uk"){
							    	country="co."+country;
							    }else if(country=="mx"){
							    	country="com.mx";
							    }else if(country.indexOf("com")>=0){
							    	country="com";
							    }
							    for(var j in asin){
							    	tr+="<a target='_blank' href='http://www.amazon."+country+"/dp/"+asin[j]+"'>"+asin[j]+"</a><br/>";
							    }
					    }
					   
					    tr+="</td>";
					    tr+="<td>";
					    if(data[i].price!=undefined && data[i].price!='-'){
				    	    var price=data[i].price.split(",");
						    for(var j in price){
						    	tr+=price[j]+"<br/>";
						    }
				       }
					   
					    tr+="</td>";
					   
					    tr+="<td>"+data[i].addedMonth+"</td>";
					    tr+="<td>"+data[i].period+"</td>";
					    
					    tr+="</tr>";
					}
			    }
			 return tr;
		}
		
		function toDecimal(x) {  
            var f = parseFloat(x);  
            if (isNaN(f)) {  
                return;  
            }  
            f = Math.round(x*100)/100;  
            return f;  
     }  
		
		function searchTypes(searchFlag){
			if(oldSearchFlag && oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			$("#start").val("");
			$("#end").val("");
			$("#searchForm").submit();
		}
		
		
		function display_by(type,country){
			var productName = '${productName}';
			var tip= "";
			if(type==1){
				tip = $("#tranTip");
				tip.find("h3").text(productName+"<spring:message code='psi_inventory_in_transit_list'/>");
			}else if(type ==2 ){
				tip = $("#fbaTran");
				tip.find("h3").text(productName+"FBA<spring:message code='psi_inventory_in_transit_list'/>");
			}else if(type ==3 ){
				tip = $("#produceTip");
				tip.find("h3").text(productName+"<spring:message code='psi_inventory_production_list'/>");
				//查出改进信息
				$.get("${ctx}/psi/productImprovement/getTips?name="+productName,function(data){
					$("#productImprove").css("display","none");
					if(data!=''){
						$("#productImprove").css("display","block");
						$("#productImprove").html(data);
					}
				});
			}else if(type ==4 ){
				tip = $("#preTranTip");
				tip.find("h3").text(productName+"<spring:message code='psi_inventory_list_of_pending_shipments'/>");
			}else if(type ==8 ){
				tip = $("#recallingTip");
				tip.find("h3").text(productName+"<spring:message code='psi_inventory_recalling_list'/>");
			}
			var param = {};
			param.type = type;
			param.country = country;
			param.name = productName;
			tip.find("tbody").html("");
			$.get("${ctx}/psi/psiInventory/getTipInfo?"+$.param(param),function(data){
				eval(" var data = "+data);
				var body="";
				if(type==1){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var linkUrl='${ctx}/psi/psiTransportOrder/view?transportNo='+ele.billNo;
						if(ele.billNo.indexOf("_LC")>0){
							linkUrl='${ctx}/psi/lcPsiTransportOrder/view?transportNo='+ele.billNo;
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"
							+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.sku+"</td>";
						/*<td style='vertical-align: middle;text-align: center;'> 
						if(ele.barcode!=''){
						  body +="<a href='${ctx}/psi/product/genBarcode?country="+ele.country+"&type=FNSKU&productName="+productName+"&barcode="+ele.barcode+"' target='_blank' style='height: 14px' class='btn btn-warning' >"+ele.barcode+"</a>";
						} */
						body +="<td style='vertical-align: middle;text-align: center;' >"+ele.tranWeek+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.toCountry+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+(ele.offlineSta=='1'?"(线下)":"")+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.remark+"</td></tr>";
					}	
				}else if(type ==2 ){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						body +="<tr><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentName+"</td><td style='vertical-align: middle;text-align: center;'><a target='_blank' href='${ctx}/psi/fbaInbound?shipmentId="+ele.shipmentId+"&country='>"+ele.shipmentId+"</a></td><td style='vertical-align: middle;text-align: center;'>"
						+ele.sku+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityShipped+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityReceived+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentStatus+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.pickUpDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.toDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.remark+"</td></tr>";
					}	
				}else if(type ==3 ){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						if(ele.orderDate==ele.deliveryDate){
							ele.deliveryDate = '';
						}
						var linkUrl='${ctx}/psi/purchaseOrder/view?orderNo='+ele.billNo;
						if(ele.billNo.indexOf("_LC")>0){
							linkUrl='${ctx}/psi/lcPurchaseOrder/view?orderNo='+ele.billNo;
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityOffline+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.orderDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.deliveryDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.tranWeek+"</td><td style='vertical-align: middle;text-align: left;'>"
						+ele.remark+"</td></tr>";
					}	
				}else if(type==4){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						var linkUrl='${ctx}/psi/psiTransportOrder/view?transportNo='+ele.billNo;
						if(ele.billNo.indexOf("_LC")>0){
							linkUrl='${ctx}/psi/lcPsiTransportOrder/view?transportNo='+ele.billNo;
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;' >"+ele.tranWeek+"</td><td style='vertical-align: middle;text-align: center;' >"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.remark+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td></tr>";
					}	
				}else if(type==8){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						var linkUrl='${ctx}/amazoninfo/removalOrder/?amazonOrderId='+ele.amazonOrderId;
						body +="<tr><td ><a target='_blank' href='"+linkUrl+"'>"+ele.amazonOrderId+"</a></td>"
							+"<td>"+country+"</td>"
							+"<td>"+ele.sku+"</td>"
							+"<td>"+ele.requestedQty+"</td>"
							+"<td>"+ele.inProcessQty+"</td>"
							+"<td>"+ele.orderType+"</td>"
							+"<td>"+ele.orderStatus+"</td></tr>"; 
						
					}	
				}
				tip.find("tbody").html(body);
			});
			tip.modal();
		}
		
		// 对Date的扩展，将 Date 转化为指定格式的String 
		// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
		// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
		// 例子： 
		// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
		// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
		Date.prototype.Format = function(fmt) 
		{ //author: meizz 
		  var o = { 
		    "M+" : this.getMonth()+1,                 //月份 
		    "d+" : this.getDate(),                    //日 
		    "h+" : this.getHours(),                   //小时 
		    "m+" : this.getMinutes(),                 //分 
		    "s+" : this.getSeconds(),                 //秒 
		    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
		    "S"  : this.getMilliseconds()             //毫秒 
		  }; 
		  if(/(y+)/.test(fmt)) 
		    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		  for(var k in o) 
		    if(new RegExp("("+ k +")").test(fmt)) 
		  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
		  return fmt; 
		}
		
		function exportReview(country){
			$("#searchForm").attr("action","${ctx}/psi/psiInventory/exportProductReview?country=" + country);
			$("#searchForm").submit();
			$('#searchForm').attr('action','${ctx}/psi/psiInventory/productInfoDetail');
			top.$.jBox.tip('<spring:message code="amazon_order_tips25"/>', 'loading',{timeout:10000});
		}
	</script>
</head>
<body>
<div id="imgtest"></div> 
	<table class="desc table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<td><spring:message code="psi_product_image"/></td>
				<td colspan="14"><spring:message code="product_manager"/>:${managerName} &nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="purchase_manager"/>：${purchaseName}&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="photo_manager"/>:${photoName}&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="sys_menu_salesInformation"/>:${salesName}
				&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code="psi_product_merchandiser"/>：${merchandiser}
				</td>
			</tr>
			<tr>
				<td rowspan="8" width="100">
					<a href="#" class="thumbnail">
				      <img src="<c:url value='${not empty firstImage?firstImage:product.link}'/>" alt="" class="image1">
				    </a>
				</td>
				<td colspan="14" style="font-weight: bold;">
					<span style="float: left;margin-left: 350px">${fn:replace(product.chineseName,';','</br>')}</span> 
					<span style="float: left;margin-left: ${not empty product.chineseName?'10':'350'}px">
					<c:if test="${not empty product.chineseName}">
						&nbsp;&nbsp;&nbsp;&&nbsp;&nbsp;&nbsp;
					</c:if>
					<input id="typeahead" type="text" class="span3 search-query" value="${productName}" style="width:200px;margin-top: 5px"  autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
					&nbsp;&nbsp;&nbsp;<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/amazonPortsDetail/viewByName?productName=${productName}&asin=${deAsin}&country=${asinsCountry}"><spring:message code="amazon_posts_information"/></a>
					&nbsp;&nbsp;<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/psi/product/view?id=${product.id}"><spring:message code="psi_product_properties"/></a>
					</span>
					<span style="float: left;margin-left: 20px;margin-top: 10px"><font size='5'>${fns:getDictLabel(productIsSale,'product_position','')}</font></span>
				</td>
			</tr>
			<tr>
				<td><spring:message code="psi_product_type"/></td>
				<td><spring:message code="psi_inventory_number_of_cartons"/></td>
				<td>MOQ</td>
				<td><spring:message code="psi_product_productionCycle"/></td>
				<td><spring:message code="psi_transport_model"/> </td>
				<td><spring:message code="psi_product_supplier"/></td>
				<td><spring:message code="psi_product_volumeRatio"/></td>
				<td><spring:message code="psi_product_singleTranWeight"/></td>
				<td><spring:message code="psi_product_shelvesTime"/></td>
				<td><spring:message code="psi_product_new"/></td>
				<td><spring:message code="psi_transport_withPower"/></td>
				<td>Line</td>
				<c:if test="${warnNum > 0 }">
					<td><font color="red"><spring:message code='psi_product_warningLetters'/></font></td>
				</c:if>
			</tr>
			<tr>
				<td>${product.type}</td>
				<%-- <td>${product.length}cm x ${product.width}cm x ${product.height}cm  Weight=${product.weight}g</td>
				<td>${product.productPackLength}cm x ${product.productPackWidth}cm x ${product.productPackHeight}cm</td> --%>
				<td>${product.packQuantity}</td>
				<td>${product.minOrderPlaced}</td>
				<td>${product.producePeriod}</td>
				<td>
				   	<c:choose>
							<c:when test="${transportTypeMap[productName]['total'] eq '1'}"><spring:message code="psi_product_ocean_shipping"/></c:when>
							<c:when test="${transportTypeMap[productName]['total'] eq '3'}">
								<spring:message code="psi_product_ocean_shipping"/>/<spring:message code="psi_inventory_form81"/>
							</c:when>
							<c:otherwise><spring:message code="psi_inventory_form81"/></c:otherwise>
					</c:choose>
				</td>
				<td>	
					<c:forEach items="${product.psiSuppliers}" var="supplier" varStatus="i">
						${supplier.supplier.nikename}${(!i.last)?',':''}
					</c:forEach>
				</td>
				<td>${product.volumeRatio}</td>
				<td>${product.tranGw}kg</td>
				<td><c:if test="${not empty allInfo['addedMonth'] }">${fn:replace(allInfo['addedMonth'],'00:00:00','')}</c:if></td>
				<td>
					<c:choose>
						<c:when test="${allInfo['isNew'] eq '0'}"><spring:message code="psi_product_noNew"/></c:when>
						<c:otherwise><spring:message code="psi_product_new"/></c:otherwise>
					</c:choose>
				</td>
				<td>
					<c:choose>
						<c:when test="${product.hasPower eq '0'}"><spring:message code="psi_transport_without_power"/></c:when>
						<c:otherwise><spring:message code="psi_transport_withPower"/></c:otherwise>
					</c:choose>
				</td>
				<td>${lineName}</td>
				<c:if test="${warnNum > 0 }">
					<td><a target="_blank" title="view" href="${ctx}/amazoninfo/warningLetter?productName=${productName}">${warnNum}</a></td>
				</c:if>
			</tr>
			 <c:set value="false"  var="managerFlag"/>
		    <c:forEach items="${fn:split(managerName,',')}" var="manager">
		         <c:if test="${fns:getUser().name eq manager}"> <c:set value="true"  var="managerFlag"/></c:if>
		    </c:forEach>
		    <c:set var="showPrice" value="0"/>
		    <c:forEach var="supplier" items="${product.psiSuppliers}" varStatus="i">
			    <c:if test="${'1' eq supplier.supplier.eliminate}">
			    	 <c:set var="showPrice" value="1"/>
			    </c:if>
		    </c:forEach>
			
			   <c:choose>
			   		<c:when test="${fn:contains(follows,fns:getUser().id) && '1' ne fns:getUser().id}">
			   			<c:if test="${fns:getUser().id eq product.createUser.id && '1' eq showPrice}">
			   				<tr>
								<td><spring:message code="psi_purchase_price"/></td>
								<td colspan="13" style="text-align:left">
									<c:forEach var="supplier" items="${product.psiSuppliers}" varStatus="i">
										<c:if test="${'1' eq supplier.supplier.eliminate}">
										<c:if test="${fn:length(product.psiSuppliers)>1}">${supplier.supplier.nikename}:</c:if>
										<c:if test="${not empty purchasePriceMap[supplier.supplier.id]['CNY']}">
										    <spring:message code="amazon_order_form39"/>:
											¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']}" pattern="0.##"/></b>
											<spring:message code="amazon_order_form40"/>:
											¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']*100/(100+supplier.supplier.taxRate)}" pattern="0.##"/></b>${(!i.last)?',':''}
											<spring:message code="amazon_order_form41"/>:
											¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']*100/(100+product.taxRefund)}" pattern="0.##"/></b>${(!i.last)?',':''}
										</c:if></c:if>
									</c:forEach>
								</td>
							</tr>
			   			</c:if>
			   		</c:when>
			   		<c:when test="${(managerFlag)||(fns:getUser().name eq purchaseName)}">
			   			<c:if test="${'1' eq showPrice }">
			   			<tr>
							<td><spring:message code="psi_purchase_price"/></td>
							<td colspan="13" style="text-align:left">
								<c:forEach var="supplier" items="${product.psiSuppliers}" varStatus="i">
									<c:if test="${'1' eq supplier.supplier.eliminate}">
									<c:if test="${fn:length(product.psiSuppliers)>1}">${supplier.supplier.nikename}:</c:if>
									<c:if test="${not empty purchasePriceMap[supplier.supplier.id]['CNY']}">
									    <spring:message code="amazon_order_form39"/>:
										¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']}" pattern="0.##"/></b>
										<spring:message code="amazon_order_form40"/>:
										¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']*100/(100+supplier.supplier.taxRate)}" pattern="0.##"/></b>${(!i.last)?',':''}
										<spring:message code="amazon_order_form41"/>:
										¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']*100/(100+product.taxRefund)}" pattern="0.##"/></b>${(!i.last)?',':''}
									</c:if></c:if>
								</c:forEach>
							</td>
						</tr>
						</c:if>
						<shiro:lacksPermission name="amazoninfo:productSalePrice:all">
			   			<tr>
						     <td><spring:message code="amazon_product_break-even_price"/></td>
							 <td style="text-align: left;vertical-align: middle;" colspan="13">
							   <c:if test="${not empty safePrice}">
						           <c:if test="${not empty safePrice[0]}">DE:€${safePrice[0]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[2]}">UK:￡${safePrice[2]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[4]}">IT:€${safePrice[4]};&nbsp;&nbsp;</c:if> 
						           <c:if test="${not empty safePrice[5]}">ES:€${safePrice[5]};&nbsp;&nbsp;</c:if>   
						           <c:if test="${not empty safePrice[3]}">FR:€${safePrice[3]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[6]}">JP:¥${safePrice[6]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[1]}">US:$${safePrice[1]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[7]}">CA:C$${safePrice[7]};&nbsp;&nbsp;</c:if>   
						           <c:if test="${not empty safePrice[8]}">MX:M$${safePrice[8]};&nbsp;&nbsp;</c:if>
						           <c:if test="${not empty safePrice[9]}">USvendor:$${safePrice[9]};&nbsp;&nbsp;</c:if>   
						           
						           
						           <c:if test="${empty safePrice[0]&&not empty avgPriceMap['de']}">DE:€<fmt:formatNumber value="${avgPriceMap['de']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[2]&&not empty avgPriceMap['uk']}">UK:￡<fmt:formatNumber value="${avgPriceMap['uk']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[4]&&not empty avgPriceMap['it']}">IT:€<fmt:formatNumber value="${avgPriceMap['it']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
						           <c:if test="${empty safePrice[5]&&not empty avgPriceMap['es']}">ES:€<fmt:formatNumber value="${avgPriceMap['es']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
						           <c:if test="${empty safePrice[3]&&not empty avgPriceMap['fr']}">FR:€<fmt:formatNumber value="${avgPriceMap['fr']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[6]&&not empty avgPriceMap['jp']}">JP:¥<fmt:formatNumber value="${avgPriceMap['jp']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[1]&&not empty avgPriceMap['com']}">US:$<fmt:formatNumber value="${avgPriceMap['com']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[7]&&not empty avgPriceMap['ca']}">CA:C$<fmt:formatNumber value="${avgPriceMap['ca']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
						           <c:if test="${empty safePrice[8]&&not empty avgPriceMap['mx']}">MX:M$<fmt:formatNumber value="${avgPriceMap['mx']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>     
						           
						    </c:if>
						    <c:if test="${empty safePrice&&not empty avgPriceMap}">
							           <c:if test="${not empty avgPriceMap['de']}">DE:€<fmt:formatNumber value="${avgPriceMap['de']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['uk']}">UK:￡<fmt:formatNumber value="${avgPriceMap['uk']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['it']}">IT:€<fmt:formatNumber value="${avgPriceMap['it']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty avgPriceMap['es']}">ES:€<fmt:formatNumber value="${avgPriceMap['es']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
							           <c:if test="${not empty avgPriceMap['fr']}">FR:€<fmt:formatNumber value="${avgPriceMap['fr']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['jp']}">JP:¥<fmt:formatNumber value="${avgPriceMap['jp']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['com']}">US:$<fmt:formatNumber value="${avgPriceMap['com']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['ca']}">CA:C$<fmt:formatNumber value="${avgPriceMap['ca']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty avgPriceMap['mx']}">MX:M$<fmt:formatNumber value="${avgPriceMap['mx']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>    
							    </c:if>
							 </td>
						 </tr>
						 <tr>
						     <td><spring:message code="amazon_product_break-even_price"/>(BY SEA)</td>
							 <td style="text-align: left;vertical-align: middle;" colspan="13">
							   <c:if test="${not empty safePriceBysea}">
							           <c:if test="${not empty safePriceBysea[0]}">DE:€${safePriceBysea[0]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[2]}">UK:￡${safePriceBysea[2]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[4]}">IT:€${safePriceBysea[4]};&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty safePriceBysea[5]}">ES:€${safePriceBysea[5]};&nbsp;&nbsp;</c:if>   
							           <c:if test="${not empty safePriceBysea[3]}">FR:€${safePriceBysea[3]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[6]}">JP:¥${safePriceBysea[6]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[1]}">US:$${safePriceBysea[1]};&nbsp;&nbsp;</c:if>  
						    	</c:if>
							 </td>
						 </tr>
						 
						 
						 </shiro:lacksPermission>
			   		</c:when>
			   		<c:otherwise>
			   			<shiro:hasPermission name="psi:product:viewPrice">
			   				<c:if test="${'1' eq showPrice }">
					   			<tr>
									<td><spring:message code="psi_purchase_price"/></td>
									<td colspan="13" style="text-align:left">
										<c:forEach var="supplier" items="${product.psiSuppliers}" varStatus="i">
											<c:if test="${'1' eq supplier.supplier.eliminate}">
											<c:if test="${fn:length(product.psiSuppliers)>1}">${supplier.supplier.nikename}:</c:if>
											<c:if test="${not empty purchasePriceMap[supplier.supplier.id]['CNY']}">
											    <spring:message code="amazon_order_form39"/>:
												¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']}" pattern="0.##"/></b>
												<spring:message code="amazon_order_form40"/>:
												¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']*100/(100+supplier.supplier.taxRate)}" pattern="0.##"/></b>${(!i.last)?',':''}
												<spring:message code="amazon_order_form41"/>:
												¥<b><fmt:formatNumber value="${purchasePriceMap[supplier.supplier.id]['CNY']*100/(100+product.taxRefund)}" pattern="0.##"/></b>${(!i.last)?',':''}
											</c:if></c:if>
										</c:forEach>
									</td>
								</tr>
							</c:if>
						</shiro:hasPermission>
			   		</c:otherwise>
			   </c:choose>
			   
			   <shiro:hasPermission name="amazoninfo:productSalePrice:all">
			    <tr>
			     <td><spring:message code="amazon_product_break-even_price"/></td>
				 <td style="text-align: left;vertical-align: middle;" colspan="13">
				     <c:if test="${not empty safePrice}">
						           <c:if test="${not empty safePrice[0]}">DE:€${safePrice[0]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[2]}">UK:￡${safePrice[2]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[4]}">IT:€${safePrice[4]};&nbsp;&nbsp;</c:if> 
						           <c:if test="${not empty safePrice[5]}">ES:€${safePrice[5]};&nbsp;&nbsp;</c:if>   
						           <c:if test="${not empty safePrice[3]}">FR:€${safePrice[3]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[6]}">JP:¥${safePrice[6]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[1]}">US:$${safePrice[1]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[7]}">CA:C$${safePrice[7]};&nbsp;&nbsp;</c:if>   
						           <c:if test="${not empty safePrice[8]}">MX:M$${safePrice[8]};&nbsp;&nbsp;</c:if>
						           <c:if test="${not empty safePrice[9]}">USvendor:$${safePrice[9]};&nbsp;&nbsp;</c:if>      
						           
						           
						           <c:if test="${empty safePrice[0]&&not empty avgPriceMap['de']}">DE:€<fmt:formatNumber value="${avgPriceMap['de']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[2]&&not empty avgPriceMap['uk']}">UK:￡<fmt:formatNumber value="${avgPriceMap['uk']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[4]&&not empty avgPriceMap['it']}">IT:€<fmt:formatNumber value="${avgPriceMap['it']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
						           <c:if test="${empty safePrice[5]&&not empty avgPriceMap['es']}">ES:€<fmt:formatNumber value="${avgPriceMap['es']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
						           <c:if test="${empty safePrice[3]&&not empty avgPriceMap['fr']}">FR:€<fmt:formatNumber value="${avgPriceMap['fr']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[6]&&not empty avgPriceMap['jp']}">JP:¥<fmt:formatNumber value="${avgPriceMap['jp']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[1]&&not empty avgPriceMap['com']}">US:$<fmt:formatNumber value="${avgPriceMap['com']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[7]&&not empty avgPriceMap['ca']}">CA:C$<fmt:formatNumber value="${avgPriceMap['ca']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
						           <c:if test="${empty safePrice[8]&&not empty avgPriceMap['mx']}">MX:M$<fmt:formatNumber value="${avgPriceMap['mx']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>     
						           
						    </c:if>
						    <c:if test="${empty safePrice&&not empty avgPriceMap}">
							           <c:if test="${not empty avgPriceMap['de']}">DE:€<fmt:formatNumber value="${avgPriceMap['de']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['uk']}">UK:￡<fmt:formatNumber value="${avgPriceMap['uk']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['it']}">IT:€<fmt:formatNumber value="${avgPriceMap['it']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty avgPriceMap['es']}">ES:€<fmt:formatNumber value="${avgPriceMap['es']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
							           <c:if test="${not empty avgPriceMap['fr']}">FR:€<fmt:formatNumber value="${avgPriceMap['fr']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['jp']}">JP:¥<fmt:formatNumber value="${avgPriceMap['jp']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['com']}">US:$<fmt:formatNumber value="${avgPriceMap['com']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['ca']}">CA:C$<fmt:formatNumber value="${avgPriceMap['ca']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty avgPriceMap['mx']}">MX:M$<fmt:formatNumber value="${avgPriceMap['mx']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>    
							    </c:if>
				 </td>
			    </tr>
			     <tr>
				     <td><spring:message code="amazon_product_break-even_price"/>(BY SEA)</td>
					 <td style="text-align: left;vertical-align: middle;" colspan="13">
					   <c:if test="${not empty safePriceBysea}">
					           <c:if test="${not empty safePriceBysea[0]}">DE:€${safePriceBysea[0]};&nbsp;&nbsp;</c:if>  
					           <c:if test="${not empty safePriceBysea[2]}">UK:￡${safePriceBysea[2]};&nbsp;&nbsp;</c:if>  
					           <c:if test="${not empty safePriceBysea[4]}">IT:€${safePriceBysea[4]};&nbsp;&nbsp;</c:if> 
					           <c:if test="${not empty safePriceBysea[5]}">ES:€${safePriceBysea[5]};&nbsp;&nbsp;</c:if>   
					           <c:if test="${not empty safePriceBysea[3]}">FR:€${safePriceBysea[3]};&nbsp;&nbsp;</c:if>  
					           <c:if test="${not empty safePriceBysea[6]}">JP:¥${safePriceBysea[6]};&nbsp;&nbsp;</c:if>  
					           <c:if test="${not empty safePriceBysea[1]}">US:$${safePriceBysea[1]};&nbsp;&nbsp;</c:if>  
				    	</c:if>
					 </td>
				 </tr>
			  </shiro:hasPermission>
			  <shiro:lacksPermission name="amazoninfo:productSalePrice:all">
			  	  <c:set var="uname" value="${fns:getUser().name}"/>
			  	  <c:if test="${fn:contains(salesName,uname) || merchandiser eq uname }">
			  	  		 <tr>
					     <td><spring:message code="amazon_product_break-even_price"/></td>
						 <td style="text-align: left;vertical-align: middle;" colspan="13">
						    <c:if test="${not empty safePrice}">
						           <c:if test="${not empty safePrice[0]}">DE:€${safePrice[0]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[2]}">UK:￡${safePrice[2]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[4]}">IT:€${safePrice[4]};&nbsp;&nbsp;</c:if> 
						           <c:if test="${not empty safePrice[5]}">ES:€${safePrice[5]};&nbsp;&nbsp;</c:if>   
						           <c:if test="${not empty safePrice[3]}">FR:€${safePrice[3]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[6]}">JP:¥${safePrice[6]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[1]}">US:$${safePrice[1]};&nbsp;&nbsp;</c:if>  
						           <c:if test="${not empty safePrice[7]}">CA:C$${safePrice[7]};&nbsp;&nbsp;</c:if>   
						           <c:if test="${not empty safePrice[8]}">MX:M$${safePrice[8]};&nbsp;&nbsp;</c:if>
						           <c:if test="${not empty safePrice[9]}">USvendor:$${safePrice[9]};&nbsp;&nbsp;</c:if>      
						           
						           
						            <c:if test="${empty safePrice[0]&&not empty avgPriceMap['de']}">DE:€<fmt:formatNumber value="${avgPriceMap['de']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[2]&&not empty avgPriceMap['uk']}">UK:￡<fmt:formatNumber value="${avgPriceMap['uk']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[4]&&not empty avgPriceMap['it']}">IT:€<fmt:formatNumber value="${avgPriceMap['it']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
						           <c:if test="${empty safePrice[5]&&not empty avgPriceMap['es']}">ES:€<fmt:formatNumber value="${avgPriceMap['es']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
						           <c:if test="${empty safePrice[3]&&not empty avgPriceMap['fr']}">FR:€<fmt:formatNumber value="${avgPriceMap['fr']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[6]&&not empty avgPriceMap['jp']}">JP:¥<fmt:formatNumber value="${avgPriceMap['jp']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[1]&&not empty avgPriceMap['com']}">US:$<fmt:formatNumber value="${avgPriceMap['com']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
						           <c:if test="${empty safePrice[7]&&not empty avgPriceMap['ca']}">CA:C$<fmt:formatNumber value="${avgPriceMap['ca']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
						           <c:if test="${empty safePrice[8]&&not empty avgPriceMap['mx']}">MX:M$<fmt:formatNumber value="${avgPriceMap['mx']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>     
						           
						    </c:if>
						    <c:if test="${empty safePrice&&not empty avgPriceMap}">
							           <c:if test="${not empty avgPriceMap['de']}">DE:€<fmt:formatNumber value="${avgPriceMap['de']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['uk']}">UK:￡<fmt:formatNumber value="${avgPriceMap['uk']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['it']}">IT:€<fmt:formatNumber value="${avgPriceMap['it']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty avgPriceMap['es']}">ES:€<fmt:formatNumber value="${avgPriceMap['es']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>   
							           <c:if test="${not empty avgPriceMap['fr']}">FR:€<fmt:formatNumber value="${avgPriceMap['fr']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['jp']}">JP:¥<fmt:formatNumber value="${avgPriceMap['jp']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['com']}">US:$<fmt:formatNumber value="${avgPriceMap['com']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty avgPriceMap['ca']}">CA:C$<fmt:formatNumber value="${avgPriceMap['ca']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty avgPriceMap['mx']}">MX:M$<fmt:formatNumber value="${avgPriceMap['mx']}" pattern="0.##"/><span style="color:red;">[空运估算]</span>;&nbsp;&nbsp;</c:if>    
							    </c:if>
						 </td>
					    </tr>
					    
					     <tr>
						     <td><spring:message code="amazon_product_break-even_price"/>(BY SEA)</td>
							 <td style="text-align: left;vertical-align: middle;" colspan="13">
							   <c:if test="${not empty safePriceBysea}">
							           <c:if test="${not empty safePriceBysea[0]}">DE:€${safePriceBysea[0]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[2]}">UK:￡${safePriceBysea[2]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[4]}">IT:€${safePriceBysea[4]};&nbsp;&nbsp;</c:if> 
							           <c:if test="${not empty safePriceBysea[5]}">ES:€${safePriceBysea[5]};&nbsp;&nbsp;</c:if>   
							           <c:if test="${not empty safePriceBysea[3]}">FR:€${safePriceBysea[3]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[6]}">JP:¥${safePriceBysea[6]};&nbsp;&nbsp;</c:if>  
							           <c:if test="${not empty safePriceBysea[1]}">US:$${safePriceBysea[1]};&nbsp;&nbsp;</c:if>  
						    	</c:if>
							 </td>
						 </tr>
			  	  </c:if>
			  </shiro:lacksPermission>
		</tbody>
	</table>
	<c:set value="${product.barcodeMap[color]}" var="code" />
	<div class="accordion-heading" id="showDetail" style="height:30px">
		<a ><b><spring:message code="amazon_product_information"/>[<spring:message code="amazon_product_strichcode"/>|SKU|EAN|ASIN|<spring:message code="amazon_product_price"/>]</b></a>
	</div>
	<div id="code"  style="display: none" >
		<table id="detailTable"  class="desc table table-striped table-bordered table-condensed">
			<tbody>	
			</tbody>
		</table>
	</div>
	<div style="overflow-x:auto;">
	<table class="table table-striped table-bordered table-condensed desc">
		<thead>
			<tr>
				<th><spring:message code="psi_inventory_in_production"/> </th>
				<th><spring:message code="psi_inventory_cn_stock"/></th>
				<th><spring:message code="psi_inventory_shipment_pending"/></th>
				<th><spring:message code="psi_inventory_in_transit"/></th>
				<th><spring:message code="psi_inventory_overseas_stock"/></th>
				<th><spring:message code="psi_inventory_recalling"/></th>
				<th><spring:message code="psi_inventory_fba_in_transit"/></th>
				<th><spring:message code="psi_inventory_Gross_FBA_stock"/></th>
				<th><spring:message code="psi_inventory_total_stock"/></th>
				<th><spring:message code="psi_inventory_upper_FBA_stock_limit"/></th>
				<th><a href="#" data-toggle="tooltip" title="昨天往前滚动31天销量" style="color: #08c;"><spring:message code="psi_inventory_sales_within_31_days"/></a></th>
				<th><spring:message code="psi_inventory_average_daily_sales"/></th>
				<th><spring:message code="psi_inventory_remaining_sales_months"/></th>
				<th><spring:message code="psi_inventory_sales_months_forecast"/> </th>
				<th><spring:message code="psi_inventory_out_of_storage_records"/></th>
				<th><spring:message code="psi_inventory_barcode_conversion_records"/></th>
				<th><spring:message code="psi_inventory_purchasing_records"/></th>
				<th>LC_<spring:message code="psi_inventory_purchasing_records"/></th>
				<th><spring:message code="psi_inventory_transport_records"/></th>
				<th>LC_<spring:message code="psi_inventory_transport_records"/></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="producting1"  value="${producting.quantity}"/>
			<c:set var="transportting1"  value="${transportting.quantity}"/>
			<c:set var="transportting2"  value="${preTransportting.quantity}"/>
			
			<c:set var="inventorysCN"  value="${inventorys.totalQuantityCN}"/>
			<c:set var="inventorysNotCN"  value="${inventorys.totalQuantityNotCN}"/>
			<tr>
				<td>
					<c:if test="${producting1>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'');return false;">
							${producting1}
						</a>
					</c:if>
				</td>
				<td>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys.cnTip}">
						${fn:length(inventorys.cnTip)>0?inventorysCN:''}
					</a>
				</td>
				<td>
					<c:if test="${transportting2>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'');return false;">
							${transportting2}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting1>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'');return false;">
							${transportting1}
						</a>
					</c:if>
				</td>
				<td>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys.notCnTip}">
						${fn:length(inventorys.notCnTip)>0?inventorysNotCN:''}
					</a>
				</td>
				<td>
					<c:if test="${returnMap['total']>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'total');return false;">${returnMap['total']}</a>
					</c:if>
				</td>
				<td>
					<c:set var="de" value="${productName}_de" />
					<c:set var="de"  value="${fbaTran[de]>0?fbaTran[de]:0}" />
					<c:set var="fr" value="${productName}_fr" />
					<c:set var="fr"  value="${fbaTran[fr]>0?fbaTran[fr]:0}" />
					<c:set var="uk" value="${productName}_uk" />
					<c:set var="uk"  value="${fbaTran[uk]>0?fbaTran[uk]:0}" />
					<c:set var="it" value="${productName}_it" />
					<c:set var="it"  value="${fbaTran[it]>0?fbaTran[it]:0}" />
					<c:set var="es" value="${productName}_es" />
					<c:set var="es"  value="${fbaTran[es]>0?fbaTran[es]:0}" />
					<c:set var="fbaEuTran"  value="${de+fr+uk+it+es}" />
					<c:set var="com" value="${productName}_com" />
					<c:set var="com"  value="${fbaTran[com]>0?fbaTran[com]:0}" />
					<c:set var="ca" value="${productName}_ca" />
					<c:set var="ca"  value="${fbaTran[ca]>0?fbaTran[ca]:0}" />
					<c:set var="mx" value="${productName}_mx" />
					<c:set var="mx"  value="${fbaTran[mx]>0?fbaTran[mx]:0}" />
					
					<c:set var="jp" value="${productName}_jp" />
					<c:set var="jp"  value="${fbaTran[jp]>0?fbaTran[jp]:0}" />
					
					<c:set var="com2" value="${productName}_com2" />
					<c:set var="com2"  value="${fbaTran[com2]>0?fbaTran[com2]:0}" />
					
					<c:set var="com3" value="${productName}_com3" />
					<c:set var="com3"  value="${fbaTran[com3]>0?fbaTran[com3]:0}" />
					
					<c:set value="${de+fr+uk+it+es+com+ca+jp+mx+com2+com3}" var="fbaTrans" />
					<c:if test="${fbaTrans>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','');return false;">${fbaTrans}</a>
					</c:if>
				</td>
				<td>
					<c:set var="de" value="${productName}_de" />
					<c:set var="de"  value="${fbas[de].total>0?fbas[de].total:0}" />
					<c:set var="fr" value="${productName}_fr" />
					<c:set var="fr"  value="${fbas[fr].total>0?fbas[fr].total:0}" />
					<c:set var="uk" value="${productName}_uk" />
					<c:set var="uk"  value="${fbas[uk].total>0?fbas[uk].total:0}" />
					<c:set var="it" value="${productName}_it" />
					<c:set var="it"  value="${fbas[it].total>0?fbas[it].total:0}" />
					<c:set var="es" value="${productName}_es" />
					<c:set var="es"  value="${fbas[es].total>0?fbas[es].total:0}" />
					<c:set var="com" value="${productName}_com" />
					<c:set var="com"  value="${fbas[com].total>0?fbas[com].total:0}" />
					<c:set var="ca" value="${productName}_ca" />
					<c:set var="ca"  value="${fbas[ca].total>0?fbas[ca].total:0}" />
					<c:set var="mx" value="${productName}_mx" />
					<c:set var="mx"  value="${fbas[mx].total>0?fbas[mx].total:0}" />
					
					<c:set var="jp" value="${productName}_jp" />
					<c:set var="jp"  value="${fbas[jp].total>0?fbas[jp].total:0}" />
					<c:set var="com2" value="${productName}_com2" />
					<c:set var="com2"  value="${fbas[com2].total>0?fbas[com2].total:0}" />
					<c:set var="com3" value="${productName}_com3" />
					<c:set var="com3"  value="${fbas[com3].total>0?fbas[com3].total:0}" />
					
					<c:set value="${de+fr+uk+it+es+com+ca+jp+com2+com3}" var="fbaTotal" />
					<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>US:${com}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}<br/>CA:${ca}<br/>MX:${mx}<br/>JP:${jp}<br/>USNEW:${com2}<br/>USTOMONS:${com3}">${fbaTotal>0?fbaTotal:''}</a>				
				</td>
				<td>
					<c:set var="total" value="${fbaTotal+producting1+transportting1+inventorysCN+inventorysNotCN+returnMap['total']}" />
					${total>0?total:''}
				</td>
				
				<td>${empty productAttr?'':productAttr.quantity}</td>
				<td>${fancha[productName].day31Sales>0?fancha[productName].day31Sales:''}</td>
				<td>${fns:roundUp(fancha[productName].day31Sales/31)}</td>
				<td>
					<c:if test="${total>0 && fancha[productName].day31Sales>0}">
						<fmt:formatNumber value="${total/fancha[productName].day31Sales}" maxFractionDigits="1" />
					</c:if>
				</td>
				<%--预测库销比 --%>
				<td>
					<c:if test="${not empty productAttr && productAttr.inventorySaleMonth > 0}">
						<fmt:formatNumber value="${productAttr.inventorySaleMonth}" maxFractionDigits="1" />
					</c:if>
				</td>
				<td><a class="btn btn-warning" target="_blank" href="${ctx}/psi/psiInventoryRevisionLog?productId=${product.id}&warehouseId=19&colorCode=${color}">View</a></td>
				<td><a class="btn btn-warning" target="_blank" href="${ctx}/psi/psiInventoryRevisionLog?productId=${product.id}&warehouseId=19&colorCode=${color}&showFlag=1">View</a></td>
				<td><a class="btn btn-warning" target="_blank" href="${ctx}/psi/purchaseOrder/singleProduct?productIdColor=${product.id},${color}&productName=${productName}">View</a></td>
				<td><a class="btn btn-warning" target="_blank" href="${ctx}/psi/lcPurchaseOrder/singleProduct?productIdColor=${product.id},${color}&productName=${productName}">View</a></td>
				<td><a class="btn btn-warning" target="_blank" href="${ctx}/psi/psiTransportOrder/singleTran?productName=${productName}">View</a></td>
				<td><a class="btn btn-warning" target="_blank" href="${ctx}/psi/lcPsiTransportOrder/singleTran?productName=${productName}">View</a></td>
			</tr>
		</tbody>
	</table>
	</div>
	<div style="overflow-x:auto;">
	<table id="orderTb" class="table table-striped table-bordered table-condensed desc" >
		<thead>
			<tr>
				<th rowspan="2" style="line-height:60px;"><spring:message code="amazon_order_form4"/></th>
				<th rowspan="2" style="line-height: 50px;"><spring:message code="psi_inventory_in_production"/></th>
				<th rowspan="2" style="line-height: 50px;"><spring:message code="psi_inventory_cn_stock"/></th>
				<th rowspan="2" style="line-height: 50px;"><spring:message code="psi_inventory_shipment_pending"/></th>
				<th rowspan="2" style="line-height: 50px;"><spring:message code="psi_inventory_in_transit"/></th>
				<th rowspan="2" style="line-height: 50px;"><spring:message code="psi_inventory_overseas_stock"/></th>
				<th rowspan="2" style="line-height:50px;"><spring:message code="psi_inventory_recalling"/></th>
				<th  colspan="4"><spring:message code="psi_inventory_FBA_stock"/></th>
				<th rowspan="2" style="line-height: 50px;"><spring:message code="psi_inventory_total_stock"/></th>
				<th  colspan="5"><spring:message code="psi_inventory_form4"/></th>
				<th  colspan="7"><spring:message code="psi_inventory_forecast_order"/>&nbsp;&nbsp;&nbsp;<c:if test="${productIsSale eq '1'}"><shiro:hasPermission name="psi:order:edit"><a class="btn btn-info btn-small" href="#" id="order" ><spring:message code="psi_inventory_quick_order"/></a></shiro:hasPermission></c:if></th>
			</tr>
			<tr>
				<!-- <th >实</th>
				<th >翻</th>
				<th >旧</th>
				<th >损</th> -->
				<th ><spring:message code="psi_inventory_real"/></th>
				<th ><spring:message code="psi_inventory_in_transit"/></th>
				<th ><spring:message code="psi_inventory_total"/></th>
				<th ><a href="#" data-toggle="tooltip" title="(FBA在库库存[不含在途]-安全库存)/滚动31日的日均销量" style="color: #08c;"><spring:message code="psi_inventory_form11"/></a></th>
				<!--<th >库存数</th>
				 <th >
					<a href="#" data-toggle="tooltip" title="总库存/周期日均销售-生产周期-运输周期-安全库存天数" style="color: #08c;">可售天</a>
				</th> -->
				<th ><spring:message code="psi_inventory_form7"/><br/><span style="color: red">(缓冲)</span></th>
				<!-- <th >周期<br/>日均销</th> -->
				<!-- <th >方差</th> -->
				<th ><spring:message code="psi_inventory_form5"/></th>
				<th ><spring:message code="psi_inventory_day"/></th>
				<th ><a href="#" data-toggle="tooltip" title="昨天往前滚动31日均销量" style="color: #08c;"><spring:message code="psi_inventory_average_daily_sales"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="昨天往前滚动31天销量" style="color: #08c;"><spring:message code="psi_inventory_sales_within_31_days"/></a></th>
				<!-- <th >安全<br/>库存值</th> -->
				<th ><a href="#" data-toggle="tooltip" title="(产品生产周期+运输周期)的预测销量 /(产品生产周期+运输周期)" style="color: #08c;"><spring:message code="psi_inventory_form2"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="(产品生产周期+运输周期)后1个月的预测销量 " style="color: #08c;"><spring:message code="psi_inventory_form3"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="(预测的生产周期日均销量或滚动31天的日均销量)*(生产周期+运输周期)+安全库存数量" style="color: #08c;"><spring:message code="psi_inventory_ordered_stock"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="总库存-下单点数量" style="color: #08c;"><spring:message code="psi_inventory_remaining_quantity"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="结余/(预测的销售期日均销量或滚动31天的日均销量)" style="color: #08c;"><spring:message code="psi_inventory_Stock"/><br/><spring:message code="psi_inventory_form62"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="销售期缓冲期销-结余 =下单量(按照整箱补足)" style="color: #08c;"><spring:message code="psi_inventory_ordered_quantity"/></a></th>
				<th ><a href="#" data-toggle="tooltip" title="运输周期*预测的生产周期日均销量-FBA总库存-海外仓在库-在途" style="color: #08c;"><spring:message code="psi_inventory_form8"/></a></th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td>德国|DE</td>
				<c:set value="${productName}_de"  var="key"/>
				<c:set value="0"  var="total"/>
				<c:set var="cntipMap" value="${inventorys.cnTipMap}" />
				<td>
					<c:if test="${producting.inventorys['de'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'de');return false;">
							${producting.inventorys['de'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['de'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['de'])>0 || inventorys.inventorys['de'].quantityInventory['CN'].newQuantity>0}">
					   	<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['de']}">
							${inventorys.inventorys['de'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['de'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['de'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'de');return false;">
							${preTransportting.inventorys['de'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['de'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'de');return false;">
							${transportting.inventorys['de'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['de'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					
						<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].offlineQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['de'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['de'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['de'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${returnMap['de']>0}">
						<c:set value="${total+returnMap['de']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'de');return false;">${returnMap['de']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="de" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'de');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				
				<c:set var="safe" value="0"></c:set>
				<c:if test="${fancha[key].variance>0}">
					<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
				</c:if> 
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['de']?0:bufferPeriodMap['de'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['de']?0:bufferPeriodMap['de']})</span></td>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				
				<%-- <td>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="<span style='word-wrap:break-word;word-break: normal'>样本：${fancha[key].samplingData}</span>">
						${fancha[key].variance}
					</a>
				</td> --%>
				<%-- <td>
					<span class="${fancha[key].periodSqrt*fancha[key].variance*2.33+fancha[key].dayPeriodSales>total?'badge badge-important':''}">
						<fmt:formatNumber maxFractionDigits="0" value="${fancha[key].periodSqrt*fancha[key].variance*2.33+fancha[key].dayPeriodSales}" />
					</span>
				</td> --%>
				
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="${product.hasPower eq '1'?'orderQ':''}" key="de">
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['de'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<tr>
				<td>英国|UK</td>
				<c:set value="${productName}_uk"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['uk'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'uk');return false;">
							${producting.inventorys['uk'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['uk'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['uk'])>0 ||inventorys.inventorys['uk'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['uk']}">
						${inventorys.inventorys['uk'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['uk'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['uk'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'uk');return false;">
							${preTransportting.inventorys['uk'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['uk'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'uk');return false;">
							${transportting.inventorys['uk'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['uk'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					
						<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].offlineQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${returnMap['uk']>0}">
						<c:set value="${total+returnMap['uk']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'uk');return false;">${returnMap['uk']}</a>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['uk'].quantityInventory['uk'].renewQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['uk'].quantityInventory['uk'].oldQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['uk'].quantityInventory['uk'].brokenQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="uk" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'uk');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['uk']?0:bufferPeriodMap['uk'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['uk']?0:bufferPeriodMap['uk']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				
				<td class="${product.hasPower eq '1'?'orderQ':''}" key="uk">
					
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['uk'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
				
			</tr>
			
			<tr>
				<td>法国|FR</td>
				<c:set value="${productName}_fr"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['fr'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'fr');return false;">
							${producting.inventorys['fr'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['fr'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['fr'])>0 ||inventorys.inventorys['fr'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['fr']}">
						${inventorys.inventorys['fr'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['fr'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['fr'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'fr');return false;">
							${preTransportting.inventorys['fr'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['fr'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'fr');return false;">
							${transportting.inventorys['fr'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['fr'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					
						<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].offlineQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['fr'].quantityInventory['fr'].renewQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['fr'].quantityInventory['fr'].oldQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['fr'].quantityInventory['fr'].brokenQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['fr']>0}">
						<c:set value="${total+returnMap['fr']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'fr');return false;">${returnMap['fr']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="fr" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'fr');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>	
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['fr']?0:bufferPeriodMap['fr'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['fr']?0:bufferPeriodMap['fr']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="${product.hasPower eq '1'?'orderQ':''}" key="fr">
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['fr'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
				
				
			</tr>
			<tr>
				<td>意大利|IT</td>
				<c:set value="${productName}_it"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['it'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'it');return false;">
							${producting.inventorys['it'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['it'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['it'])>0 ||inventorys.inventorys['it'].quantityInventory['CN'].newQuantity>0}">
					<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['it']}">
						${inventorys.inventorys['it'].quantityInventory['CN'].newQuantity}
					</a>	
						<c:set value="${total+inventorys.inventorys['it'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['it'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'it');return false;">
							${preTransportting.inventorys['it'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['it'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'it');return false;">
							${transportting.inventorys['it'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['it'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
						<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].offlineQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['it'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['it'].quantityInventory['DE'].newQuantity>0}">
						<c:set value="${total+inventorys.inventorys['it'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['it'].quantityInventory['it'].renewQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['it'].quantityInventory['it'].oldQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['it'].quantityInventory['it'].brokenQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['it']>0}">
						<c:set value="${total+returnMap['it']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'it');return false;">${returnMap['it']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="it" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'it');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['it']?0:bufferPeriodMap['it'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['it']?0:bufferPeriodMap['it']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="${product.hasPower eq '1'?'orderQ':''}" key="it">
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['it'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
				
			</tr>
			<tr>
				<td>西班牙|ES</td>
				<c:set value="${productName}_es"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['es'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'es');return false;">
							${producting.inventorys['es'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['es'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['es'])>0 ||inventorys.inventorys['es'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['es']}">
						
						${inventorys.inventorys['es'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['es'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['es'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'es');return false;">
							${preTransportting.inventorys['es'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['es'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'es');return false;">
							${transportting.inventorys['es'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['es'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
						<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].offlineQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['es'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['es'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['es'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['es'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['es'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['es'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['es']>0}">
						<c:set value="${total+returnMap['es']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'es');return false;">${returnMap['es']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="es" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'es');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>	
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
			<%-- 	<td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['es']?0:bufferPeriodMap['es'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['es']?0:bufferPeriodMap['es']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="${product.hasPower eq '1'?'orderQ':''}" key="es">
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['es'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			<tr style="background-color: ${product.hasPower eq '0'?'#d9edf7':'#a6ffa6'};">
				<td>${product.hasPower eq '0' ?'Pan EU':'EU'}</td>
				<c:set value="${productName}_eu"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.quantityEuro>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'eu');return false;">
							${producting.quantityEuro}
						</a>
						<c:set value="${total+producting.quantityEuro}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.quantityEuro['CN'].newQuantity>0}">
						${inventorys.quantityEuro['CN'].newQuantity}
						<c:set value="${total+inventorys.quantityEuro['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.quantityEuro>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'eu');return false;">
							${preTransportting.quantityEuro}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.quantityEuro>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'eu');return false;">
							${transportting.quantityEuro}
						</a>
						<c:set value="${total+transportting.quantityEuro}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.quantityEuro['DE'].renewQuantity>0?inventorys.quantityEuro['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.quantityEuro['DE'].oldQuantity>0?inventorys.quantityEuro['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.quantityEuro['DE'].brokenQuantity>0?inventorys.quantityEuro['DE'].brokenQuantity:''}" var="broken" />
					<c:set value="${inventorys.quantityEuro['DE'].offlineQuantity>0?inventorys.quantityEuro['DE'].offlineQuantity:''}" var="offline" />
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
						${inventorys.quantityEuro['DE'].newQuantity>0?inventorys.quantityEuro['DE'].newQuantity:0}
					</a>
					<c:if test="${inventorys.quantityEuro['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.quantityEuro['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.quantityEuro['DE'].renewQuantity>0?inventorys.quantityEuro['DE'].renewQuantity:''}
				</td>
				<td>
					${inventorys.quantityEuro['DE'].oldQuantity>0?inventorys.quantityEuro['DE'].oldQuantity:''}
				</td>
				<td>
					${inventorys.quantityEuro['DE'].brokenQuantity>0?inventorys.quantityEuro['DE'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['eu']>0}">
						<c:set value="${total+returnMap['eu']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'eu');return false;">${returnMap['eu']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaEuTran>0}">
						${fbaEuTran}
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>
						
					<c:if test="${(fbas[key].fulfillableQuantity)>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['de']?0:bufferPeriodMap['de'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['de']?0:bufferPeriodMap['de']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<%-- <td>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="<span style='word-wrap:break-word;word-break: normal'>样本：${fancha[key].samplingData}</span>">
						${fancha[key].variance}
					</a>
				</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
					</c:if> 
				</td>
						
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				
				
				<%-- <td>
					<span class="${fancha[key].periodSqrt*fancha[key].variance*2.33+fancha[key].dayPeriodSales>total?'badge badge-important':''}">
						<fmt:formatNumber maxFractionDigits="0" value="${fancha[key].periodSqrt*fancha[key].variance*2.33+fancha[key].dayPeriodSales}" />
					</span>
				</td> --%>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="euPoint"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="euPoint"></c:set>
						</c:when>
					</c:choose>
					<fmt:formatNumber maxFractionDigits="0" value="${euPoint}" />
				</td>
				<td>
					<c:set value="${total-euPoint}"  var="euJy"/>
					<c:if test="${euJy!=0}">
						<span class="${euJy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${euJy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${euJy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${euJy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${euJy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${euJy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="${product.hasPower eq '0'?'orderQ':''}" key="de">
					<c:if test="${-euJy>0}">
						<span class="${euJy<=0?'badge badge-important':''}">${(fns:roundUp((-euJy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-total-fbaEuTran}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-total-fbaEuTran}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			<tr>
				<td>美国|US</td>
				<c:set value="${productName}_com"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['com'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'com');return false;">
							${producting.inventorys['com'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['com'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['com'])>0 ||inventorys.inventorys['com'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['com']}">
							${inventorys.inventorys['com'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['com'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['com'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'com');return false;">
							${preTransportting.inventorys['com'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['com'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'com');return false;">
							${transportting.inventorys['com'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['com'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
						<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].offlineQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['com'].quantityInventory['US'].newQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['com'].quantityInventory['US'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['com'].quantityInventory['US'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['com'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['com'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['com'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['com']>0}">
						<c:set value="${total+returnMap['com']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'com');return false;">${returnMap['com']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="com" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'com');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>	
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['com']?0:bufferPeriodMap['com'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['com']?0:bufferPeriodMap['com']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="orderQ" key="com">
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['com'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			
			<tr>
				<td>日本|JP</td>
				<c:set value="${productName}_jp"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['jp'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'jp');return false;">
							${producting.inventorys['jp'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['jp'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['jp'])>0 ||inventorys.inventorys['jp'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['jp']}">
						
						${inventorys.inventorys['jp'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['jp'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['jp'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'jp');return false;">
							${preTransportting.inventorys['jp'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['jp'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'jp');return false;">
							${transportting.inventorys['jp'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['jp'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
						<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].renewQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].oldQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].brokenQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].offlineQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['jp'].quantityInventory['JP'].renewQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['jp'].quantityInventory['JP'].oldQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['jp'].quantityInventory['JP'].brokenQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['jp']>0}">
						<c:set value="${total+returnMap['jp']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'jp');return false;">${returnMap['jp']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="jp" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'jp');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>	
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['jp']?0:bufferPeriodMap['jp'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['jp']?0:bufferPeriodMap['jp']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="orderQ" key="jp">
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['jp'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			<tr>
				<td>加拿大|CA</td>
				<c:set value="${productName}_ca"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['ca'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'ca');return false;">
							${producting.inventorys['ca'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['ca'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['ca'])>0 ||inventorys.inventorys['ca'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['ca']}">
						${inventorys.inventorys['ca'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['ca'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['ca'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'ca');return false;">
							${preTransportting.inventorys['ca'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['ca'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'ca');return false;">
							${transportting.inventorys['ca'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['ca'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
						<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].offlineQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['ca'].quantityInventory['US'].newQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['ca'].quantityInventory['US'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['ca'].quantityInventory['US'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['ca']>0}">
						<c:set value="${total+returnMap['ca']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'ca');return false;">${returnMap['ca']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="ca" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'ca');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>	
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['ca']?0:bufferPeriodMap['ca'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['ca']?0:bufferPeriodMap['ca']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="orderQ" key="ca">
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['ca'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			<tr>
				<td>墨西哥|MX</td>
				<c:set value="${productName}_mx"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['mx'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'mx');return false;">
							${producting.inventorys['mx'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['mx'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fn:length(cntipMap['mx'])>0 ||inventorys.inventorys['mx'].quantityInventory['CN'].newQuantity>0}">
						<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['mx']}">
						${inventorys.inventorys['mx'].quantityInventory['CN'].newQuantity}
						</a>
						<c:set value="${total+inventorys.inventorys['mx'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['mx'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'mx');return false;">
							${preTransportting.inventorys['mx'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['mx'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'mx');return false;">
							${transportting.inventorys['mx'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['mx'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
						<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].renewQuantity:''}" var="renew" />
						<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].oldQuantity:''}" var="old" />
						<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].brokenQuantity:''}" var="broken" />
						<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].offlineQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].offlineQuantity:''}" var="offline" />
						<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="另有<br/>offline:${offline}<br/>renew:${renew}<br/>old:${old}<br/>broken:${broken}">
							${inventorys.inventorys['mx'].quantityInventory['US'].newQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].newQuantity:0}
						</a>
					<c:if test="${inventorys.inventorys['mx'].quantityInventory['US'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['mx'].quantityInventory['US'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<%-- <td>
					${inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity:''}
				</td>
				<td>
					${inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity:''}
				</td> --%>
				<td>
					<c:if test="${returnMap['mx']>0}">
						<c:set value="${total+returnMap['mx']}"  var="total"/>
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'mx');return false;">${returnMap['mx']}</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
						<input class="country" value="mx" type="hidden" />
						<input class="name" value="${productName}" type="hidden" />
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'mx');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
					</c:if>	
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp((fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<%-- <td>
					<c:set var="periodSell" value="${fancha[key].dayPeriodSales/fancha[key].period}" />
					<c:if test="${total>0 && periodSell>0}">
						<c:set var="sellDay" value="${fns:roundUp(total/periodSell-fancha[key].period-(fancha[key].periodSqrt*fancha[key].variance*2.33/periodSell))}" />
						<span class="${sellDay>0?'':'badge badge-important'}">${sellDay}</span>
					</c:if>
				</td> --%>
				<c:set var="period" value="${fancha[key].period+(empty bufferPeriodMap['mx']?0:bufferPeriodMap['mx'])}" />
				<td>${period}<span style="color: red">(${empty bufferPeriodMap['mx']?0:bufferPeriodMap['mx']})</span></td>
				<%-- <td>${fns:roundUp(periodSell)}</td> --%>
				<td>
					<c:if test="${fancha[key].variance>0}">
						${safe}
						
						<c:choose>
							<c:when test="${fancha[key].forecastPreiodAvg >0 }">
								<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
							</c:when>
							<c:when test="${fancha[key].day31Sales >0 }">
								<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
							</c:when>
						</c:choose>
						
					</c:if> 
				</td>
				
				<td>
					<c:if test="${safe>0}">
						${fns:roundUp(safeDay)}
					</c:if>
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
							</a>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<a class="tipDay" href="#" style="color: #08c;" data-toggle="popover" data-html="true"  >
								<span style="${bu?'color:red':''} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
							</a>
						</c:when>
					</c:choose>
				</td>
				<td class="orderQ" key="ca">
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['mx'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	<ul class="nav nav-tabs">
		<li class="active"><a class="saleHref" href="#saleVolume" ><spring:message code="amazon_sales_volume_comparative"/></a></li>
		<li ><a class="saleHref" href="#contrast" ><spring:message code="amazon_proportion_of_sales_volume"/></a></li>
		<li ><a class="saleHref" href="#sale" ><spring:message code="amazon_sales_comparative"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/psi/psiInventory/productInfoDetail" method="post" class="breadcrumb form-search">
		<div style="height:70px;margin-top:10px">
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false">By Month</a></li>
		</ul>
	   
		
		 Currency:<select name="currencyType" id="currencyType" style="width: 100px" onchange='$("#searchForm").submit()'>
				<option value="EUR" ${'EUR' eq fn:trim(saleReport.currencyType)?'selected':''}>EUR</option>
				<option value="USD" ${'USD' eq fn:trim(saleReport.currencyType)?'selected':''}>USD</option>
		</select>
		<input type="hidden" value="${productName}" name="productName" />
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		<span style="float: center;">
		<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${saleReport.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${saleReport.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		 <span id="exportDiv">
	        <input id="btnExp" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
	     </span>
	        <div class="btn-group">
				<button type="button" class="btn btn-primary" style="background:#06c"><spring:message code="sys_but_exportReview"/></button><%--导出评论 --%>
				<button type="button" class="btn btn-primary dropdown-toggle" style="background:#06c"  data-toggle="dropdown">
					<span class="caret"></span>
					<span class="sr-only"></span>
				</button>
				<ul class="dropdown-menu" id="allExport">
					<li><a href="#" onclick="exportReview('en')">英语国家|EN</a></li>
					<li><a href="#" onclick="exportReview('eu')">欧洲|EU</a></li>
					<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
						<c:if test="${dic.value ne 'com.unitek'}">
							<li><a href="#" onclick="exportReview('${dic.value}')">${dic.label}</a></li>
						</c:if>
					</c:forEach>	
				</ul>
			</div>
		</span><br/><br/>
		<span style="float: left;">
		 <a class="btn btn-small btn-vendor" style="height:14px; font-size:12px; line-height:12px;">Vendor</a>
		&nbsp;&nbsp;<a class="btn btn-small btn-warning" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_promotions_order"/></a>
		&nbsp;&nbsp;<a class="btn btn-small btn-danger" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_flash_sales_order"/></a>
		&nbsp;&nbsp;<a class="btn btn-small btn-primary" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_max_order"/></a>
		&nbsp;&nbsp;<a class="btn btn-small btn-info" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_free_order"/></a>
		&nbsp;&nbsp;<a class="btn btn-small btn-success" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_ads_order"/></a>
		&nbsp;&nbsp;<a class="btn btn-small btn-special" style="height:14px; font-size:12px; line-height:12px;">B2B</a>
		&nbsp;&nbsp;<a class="btn btn-small btn-inverse" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_multifarious_order"/></a>
		</span>
		</div>
	</form:form>
	<div class="tab-content">
		<div id="saleVolume" class="tab-pane active">
			<table id="saleVolumeTb" class="table table-striped table-bordered table-condensed desc">
				<thead>
					<tr>
						<th><spring:message code="amazon_order_date"/></th>
						<th>
						   <c:choose>
								<c:when test="${'1' eq saleReport.searchType}"><spring:message code="amazon_order_week"/></c:when>
								<c:otherwise><spring:message code="amazon_order_section"/></c:otherwise>
					       </c:choose>
						</th>
						<th>德国|DE</th>
						<th>英国|UK</th>
						<th>法国|FR</th>
						<th>意大利|IT</th>
						<th>西班牙|ES</th>
						<th>欧洲|EU</th>
						<th>美国|US</th>
						<th>日本|JP</th>
						<th>加拿大|CA</th>
						<th>墨西哥|MX</th>
						<th>全球|GLOBAL</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="x" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<c:set value="0" var="eu" />
							<c:set value="0" var="total" />
							<td>${x}${type}</td>
							<td>${tip[x]}</td>
							<td class="sale1">
								<c:if test="${data[x]['de'].salesVolume>0||(data[x]['de'].salesVolume==0&&not empty data[x]['de'].classType)}">
									<a  ${(not empty data[x]['de'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['de'].businessOrder>0?'B2B:':''}${data[x]['de'].businessOrder>0?data[x]['de'].businessOrder:''}&nbsp;${data[x]['de'].reviewVolume>0?'Marketing：':''}${data[x]['de'].reviewVolume>0?data[x]['de'].reviewVolume:'' }&nbsp;${data[x]['de'].supportVolume>0?'Support：':''}${data[x]['de'].supportVolume>0?data[x]['de'].supportVolume:'' }&nbsp;${data[x]['de'].promotionsOrder>0?promotionsSales:''}${data[x]['de'].promotionsOrder>0?data[x]['de'].promotionsOrder:'' }&nbsp;${data[x]['de'].flashSalesOrder>0?flashOrder:''}${data[x]['de'].flashSalesOrder>0?data[x]['de'].flashSalesOrder:'' }&nbsp;${data[x]['de'].maxOrder>0?bulkOrder:''}${data[x]['de'].maxOrder>0?data[x]['de'].maxOrder:'' }&nbsp;${data[x]['de'].freeOrder>0?freeSales:''}${data[x]['de'].freeOrder>0?data[x]['de'].freeOrder:'' }&nbsp;${data[x]['de'].adsOrder>0?'SPA:':''}${data[x]['de'].adsOrder>0?data[x]['de'].adsOrder:''}&nbsp;${data[x]['de'].amsOrder>0?'AMS:':''}${data[x]['de'].amsOrder>0?data[x]['de'].amsOrder:''}&nbsp;${data[x]['de'].outsideOrder>0?outsideSales:''}${data[x]['de'].outsideOrder>0?data[x]['de'].outsideOrder:'' }&nbsp;${data[x]['de'].coupon>0?'coupon:':''}${data[x]['de'].coupon>0?data[x]['de'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=de&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['de'].classType } sale1" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['de'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['de'].salesVolume}" var="eu" />
								</c:if>
								<c:if test="${otherData[x]['2-de'].salesVolume>0}">
								   <span class="btn btn-small btn-vendor deSale1" style="height:14px; font-size:12px; line-height:12px;">${otherData[x]['2-de'].salesVolume}</span>
								</c:if>
							</td>
							<td class="sale2">
								
								<c:if test="${data[x]['uk'].salesVolume>0||(data[x]['uk'].salesVolume==0&&not empty data[x]['uk'].classType)}">
									<a ${(not empty data[x]['uk'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['uk'].businessOrder>0?'B2B:':''}${data[x]['uk'].businessOrder>0?data[x]['uk'].businessOrder:''}&nbsp;${data[x]['uk'].reviewVolume>0?'Marketing：':''}${data[x]['uk'].reviewVolume>0?data[x]['uk'].reviewVolume:'' }&nbsp;${data[x]['uk'].supportVolume>0?'Support：':''}${data[x]['uk'].supportVolume>0?data[x]['uk'].supportVolume:'' }&nbsp;${data[x]['uk'].promotionsOrder>0?promotionsSales:''}${data[x]['uk'].promotionsOrder>0?data[x]['uk'].promotionsOrder:'' }&nbsp;${data[x]['uk'].flashSalesOrder>0?flashOrder:''}${data[x]['uk'].flashSalesOrder>0?data[x]['uk'].flashSalesOrder:'' }&nbsp;${data[x]['uk'].maxOrder>0?bulkOrder:''}${data[x]['uk'].maxOrder>0?data[x]['uk'].maxOrder:'' }&nbsp;${data[x]['uk'].freeOrder>0?freeSales:''}${data[x]['uk'].freeOrder>0?data[x]['uk'].freeOrder:'' }&nbsp;${data[x]['uk'].adsOrder>0?'SPA:':''}${data[x]['uk'].adsOrder>0?data[x]['uk'].adsOrder:'' }&nbsp;${data[x]['uk'].amsOrder>0?'AMS:':''}${data[x]['uk'].amsOrder>0?data[x]['uk'].amsOrder:''}&nbsp;${data[x]['uk'].outsideOrder>0?outsideSales:''}${data[x]['uk'].outsideOrder>0?data[x]['uk'].outsideOrder:'' }&nbsp;${data[x]['uk'].coupon>0?'coupon:':''}${data[x]['uk'].coupon>0?data[x]['uk'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=uk&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['uk'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['uk'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['uk'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale3">
								<c:if test="${data[x]['fr'].salesVolume>0||(data[x]['fr'].salesVolume==0&&not empty data[x]['fr'].classType)}">
									<a ${(not empty data[x]['fr'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['fr'].businessOrder>0?'B2B:':''}${data[x]['fr'].businessOrder>0?data[x]['fr'].businessOrder:''}&nbsp;${data[x]['fr'].reviewVolume>0?'Marketing：':''}${data[x]['fr'].reviewVolume>0?data[x]['fr'].reviewVolume:'' }&nbsp;${data[x]['fr'].supportVolume>0?'Support：':''}${data[x]['fr'].supportVolume>0?data[x]['fr'].supportVolume:'' }&nbsp;${data[x]['fr'].promotionsOrder>0?promotionsSales:''}${data[x]['fr'].promotionsOrder>0?data[x]['fr'].promotionsOrder:'' }&nbsp;${data[x]['fr'].flashSalesOrder>0?flashOrder:''}${data[x]['fr'].flashSalesOrder>0?data[x]['fr'].flashSalesOrder:'' }&nbsp;${data[x]['fr'].maxOrder>0?bulkOrder:''}${data[x]['fr'].maxOrder>0?data[x]['fr'].maxOrder:'' }&nbsp;${data[x]['fr'].freeOrder>0?freeSales:''}${data[x]['fr'].freeOrder>0?data[x]['fr'].freeOrder:'' }&nbsp;${data[x]['fr'].adsOrder>0?'SPA:':''}${data[x]['fr'].adsOrder>0?data[x]['fr'].adsOrder:'' }&nbsp;${data[x]['fr'].amsOrder>0?'AMS:':''}${data[x]['fr'].amsOrder>0?data[x]['fr'].amsOrder:''}&nbsp;${data[x]['fr'].outsideOrder>0?outsideSales:''}${data[x]['fr'].outsideOrder>0?data[x]['fr'].outsideOrder:'' }&nbsp;${data[x]['fr'].coupon>0?'coupon:':''}${data[x]['fr'].coupon>0?data[x]['fr'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=fr&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['fr'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['fr'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['fr'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale4">
							    
								<c:if test="${data[x]['it'].salesVolume>0||(data[x]['it'].salesVolume==0&&not empty data[x]['it'].classType)}">
									<a ${(not empty data[x]['it'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['it'].businessOrder>0?'B2B:':''}${data[x]['it'].businessOrder>0?data[x]['it'].businessOrder:''}&nbsp;${data[x]['it'].reviewVolume>0?'Marketing：':''}${data[x]['it'].reviewVolume>0?data[x]['it'].reviewVolume:'' }&nbsp;${data[x]['it'].supportVolume>0?'Support：':''}${data[x]['it'].supportVolume>0?data[x]['it'].supportVolume:'' }&nbsp;${data[x]['it'].promotionsOrder>0?promotionsSales:''}${data[x]['it'].promotionsOrder>0?data[x]['it'].promotionsOrder:'' }&nbsp;${data[x]['it'].flashSalesOrder>0?flashOrder:''}${data[x]['it'].flashSalesOrder>0?data[x]['it'].flashSalesOrder:'' }&nbsp;${data[x]['it'].maxOrder>0?bulkOrder:''}${data[x]['it'].maxOrder>0?data[x]['it'].maxOrder:'' }&nbsp;${data[x]['it'].freeOrder>0?freeSales:''}${data[x]['it'].freeOrder>0?data[x]['it'].freeOrder:'' }&nbsp;${data[x]['it'].adsOrder>0?'SPA:':''}${data[x]['it'].adsOrder>0?data[x]['it'].adsOrder:'' }&nbsp;${data[x]['it'].amsOrder>0?'AMS:':''}${data[x]['it'].amsOrder>0?data[x]['it'].amsOrder:''}&nbsp;${data[x]['it'].outsideOrder>0?outsideSales:''}${data[x]['it'].outsideOrder>0?data[x]['it'].outsideOrder:'' }&nbsp;${data[x]['it'].coupon>0?'coupon:':''}${data[x]['it'].coupon>0?data[x]['it'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=it&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['it'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['it'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['it'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale5">
								<c:if test="${data[x]['es'].salesVolume>0||(data[x]['es'].salesVolume==0&&not empty data[x]['es'].classType)}">
									<a ${(not empty data[x]['es'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['es'].businessOrder>0?'B2B:':''}${data[x]['es'].businessOrder>0?data[x]['es'].businessOrder:''}&nbsp;${data[x]['es'].reviewVolume>0?'Marketing：':''}${data[x]['es'].reviewVolume>0?data[x]['es'].reviewVolume:'' }&nbsp;${data[x]['es'].supportVolume>0?'Support：':''}${data[x]['es'].supportVolume>0?data[x]['es'].supportVolume:'' }&nbsp;${data[x]['es'].promotionsOrder>0?promotionsSales:''}${data[x]['es'].promotionsOrder>0?data[x]['es'].promotionsOrder:'' }&nbsp;${data[x]['es'].flashSalesOrder>0?flashOrder:''}${data[x]['es'].flashSalesOrder>0?data[x]['es'].flashSalesOrder:'' }&nbsp;${data[x]['es'].maxOrder>0?bulkOrder:''}${data[x]['es'].maxOrder>0?data[x]['es'].maxOrder:'' }&nbsp;${data[x]['es'].freeOrder>0?freeSales:''}${data[x]['es'].freeOrder>0?data[x]['es'].freeOrder:'' }&nbsp;${data[x]['es'].adsOrder>0?'SPA:':''}${data[x]['es'].adsOrder>0?data[x]['es'].adsOrder:'' }&nbsp;${data[x]['es'].amsOrder>0?'AMS:':''}${data[x]['es'].amsOrder>0?data[x]['es'].amsOrder:''}&nbsp;${data[x]['es'].outsideOrder>0?outsideSales:''}${data[x]['es'].outsideOrder>0?data[x]['es'].outsideOrder:'' }&nbsp;${data[x]['es'].coupon>0?'coupon:':''}${data[x]['es'].coupon>0?data[x]['es'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=es&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['es'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['es'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['es'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale6">
								<c:if test="${eu>0||(eu==0&&not empty data[x]['eu'].classType)}">
									<a class="btn btn-small ${data[x]['eu'].classType }" ${(not empty data[x]['eu'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['eu'].businessOrder>0?'B2B:':''}${data[x]['eu'].businessOrder>0?data[x]['eu'].businessOrder:''}&nbsp;${data[x]['eu'].reviewVolume>0?'Marketing：':''}${data[x]['eu'].reviewVolume>0?data[x]['eu'].reviewVolume:'' }&nbsp;${data[x]['eu'].supportVolume>0?'Support：':''}${data[x]['eu'].supportVolume>0?data[x]['eu'].supportVolume:'' }&nbsp;${data[x]['eu'].promotionsOrder>0?promotionsSales:''}${data[x]['eu'].promotionsOrder>0?data[x]['eu'].promotionsOrder:'' }&nbsp;${data[x]['eu'].flashSalesOrder>0?flashOrder:''}${data[x]['eu'].flashSalesOrder>0?data[x]['eu'].flashSalesOrder:'' }&nbsp;${data[x]['eu'].maxOrder>0?bulkOrder:''}${data[x]['eu'].maxOrder>0?data[x]['eu'].maxOrder:'' }&nbsp;${data[x]['eu'].freeOrder>0?freeSales:''}${data[x]['eu'].freeOrder>0?data[x]['eu'].freeOrder:'' }&nbsp;${data[x]['eu'].adsOrder>0?'SPA:':''}${data[x]['eu'].adsOrder>0?data[x]['eu'].adsOrder:'' }&nbsp;${data[x]['eu'].amsOrder>0?'AMS:':''}${data[x]['eu'].amsOrder>0?data[x]['eu'].amsOrder:''}&nbsp;${data[x]['eu'].outsideOrder>0?outsideSales:''}${data[x]['eu'].outsideOrder>0?data[x]['eu'].outsideOrder:'' }&nbsp;${data[x]['eu'].coupon>0?'coupon:':''}${data[x]['eu'].coupon>0?data[x]['eu'].coupon:''}" style="height:14px; font-size:12px; line-height:12px;" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=eu&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total">
										${eu}
									</a>
									<c:set value="${total+eu}" var="total" />
								</c:if>
							</td>
							<td class="sale7">
								
								<c:if test="${data[x]['com'].salesVolume>0||(data[x]['com'].salesVolume==0&&not empty data[x]['com'].classType)}">
									<a ${(not empty data[x]['com'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['com'].businessOrder>0?'B2B:':''}${data[x]['com'].businessOrder>0?data[x]['com'].businessOrder:''}&nbsp;${data[x]['com'].reviewVolume>0?'Marketing：':''}${data[x]['com'].reviewVolume>0?data[x]['com'].reviewVolume:'' }&nbsp;${data[x]['com'].supportVolume>0?'Support：':''}${data[x]['com'].supportVolume>0?data[x]['com'].supportVolume:'' }&nbsp;${data[x]['com'].promotionsOrder>0?promotionsSales:''}${data[x]['com'].promotionsOrder>0?data[x]['com'].promotionsOrder:'' }&nbsp;${data[x]['com'].flashSalesOrder>0?flashOrder:''}${data[x]['com'].flashSalesOrder>0?data[x]['com'].flashSalesOrder:'' }&nbsp;${data[x]['com'].maxOrder>0?bulkOrder:''}${data[x]['com'].maxOrder>0?data[x]['com'].maxOrder:'' }&nbsp;${data[x]['com'].freeOrder>0?freeSales:''}${data[x]['com'].freeOrder>0?data[x]['com'].freeOrder:'' }&nbsp;${data[x]['com'].adsOrder>0?'SPA:':''}${data[x]['com'].adsOrder>0?data[x]['com'].adsOrder:'' }&nbsp;${data[x]['com'].amsOrder>0?'AMS:':''}${data[x]['com'].amsOrder>0?data[x]['com'].amsOrder:''}&nbsp;${data[x]['com'].outsideOrder>0?outsideSales:''}${data[x]['com'].outsideOrder>0?data[x]['com'].outsideOrder:'' }&nbsp;${data[x]['com'].coupon>0?'coupon:':''}${data[x]['com'].coupon>0?data[x]['com'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=com&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['com'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['com'].salesVolume}
									</a>
									<c:set value="${total+data[x]['com'].salesVolume}" var="total" />
								</c:if>
								<c:if test="${otherData[x]['2-com'].salesVolume>0}">
									<span class="btn btn-small btn-vendor comSale1" style="height:14px; font-size:12px; line-height:12px;">${otherData[x]['2-com'].salesVolume}</span>
								</c:if>
							</td>
							
							<td class="sale8">
								<c:if test="${data[x]['jp'].salesVolume>0||(data[x]['jp'].salesVolume==0&&not empty data[x]['jp'].classType)}">
									<a ${(not empty data[x]['jp'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['jp'].businessOrder>0?'B2B:':''}${data[x]['jp'].businessOrder>0?data[x]['jp'].businessOrder:''}&nbsp;${data[x]['jp'].reviewVolume>0?'Marketing：':''}${data[x]['jp'].reviewVolume>0?data[x]['jp'].reviewVolume:'' }&nbsp;${data[x]['jp'].supportVolume>0?'Support：':''}${data[x]['jp'].supportVolume>0?data[x]['jp'].supportVolume:'' }&nbsp;${data[x]['jp'].promotionsOrder>0?promotionsSales:''}${data[x]['jp'].promotionsOrder>0?data[x]['jp'].promotionsOrder:'' }&nbsp;${data[x]['jp'].flashSalesOrder>0?flashOrder:''}${data[x]['jp'].flashSalesOrder>0?data[x]['jp'].flashSalesOrder:'' }&nbsp;${data[x]['jp'].maxOrder>0?bulkOrder:''}${data[x]['jp'].maxOrder>0?data[x]['jp'].maxOrder:'' }&nbsp;${data[x]['jp'].freeOrder>0?freeSales:''}${data[x]['jp'].freeOrder>0?data[x]['jp'].freeOrder:'' }&nbsp;${data[x]['jp'].adsOrder>0?'SPA:':''}${data[x]['jp'].adsOrder>0?data[x]['jp'].adsOrder:'' }&nbsp;${data[x]['jp'].amsOrder>0?'AMS:':''}${data[x]['jp'].amsOrder>0?data[x]['jp'].amsOrder:''}&nbsp;${data[x]['jp'].outsideOrder>0?outsideSales:''}${data[x]['jp'].outsideOrder>0?data[x]['jp'].outsideOrder:'' }&nbsp;${data[x]['jp'].coupon>0?'coupon:':''}${data[x]['jp'].coupon>0?data[x]['jp'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=jp&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['jp'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['jp'].salesVolume}
									</a>
									<c:set value="${total+data[x]['jp'].salesVolume}" var="total" />
								</c:if>
							</td>
							<td class="sale9">
								<c:if test="${data[x]['ca'].salesVolume>0||(data[x]['ca'].salesVolume==0&&not empty data[x]['ca'].classType)}">
									<a ${(not empty data[x]['ca'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['ca'].businessOrder>0?'B2B:':''}${data[x]['ca'].businessOrder>0?data[x]['ca'].businessOrder:''}&nbsp;${data[x]['ca'].reviewVolume>0?'Marketing：':''}${data[x]['ca'].reviewVolume>0?data[x]['ca'].reviewVolume:'' }&nbsp;${data[x]['ca'].supportVolume>0?'Support：':''}${data[x]['ca'].supportVolume>0?data[x]['ca'].supportVolume:'' }&nbsp;${data[x]['ca'].promotionsOrder>0?promotionsSales:''}${data[x]['ca'].promotionsOrder>0?data[x]['ca'].promotionsOrder:'' }&nbsp;${data[x]['ca'].flashSalesOrder>0?flashOrder:''}${data[x]['ca'].flashSalesOrder>0?data[x]['ca'].flashSalesOrder:'' }&nbsp;${data[x]['ca'].maxOrder>0?bulkOrder:''}${data[x]['ca'].maxOrder>0?data[x]['ca'].maxOrder:'' }&nbsp;${data[x]['ca'].freeOrder>0?freeSales:''}${data[x]['ca'].freeOrder>0?data[x]['ca'].freeOrder:'' }&nbsp;${data[x]['ca'].adsOrder>0?'SPA:':''}${data[x]['ca'].adsOrder>0?data[x]['ca'].adsOrder:'' }&nbsp;${data[x]['ca'].amsOrder>0?'AMS:':''}${data[x]['ca'].amsOrder>0?data[x]['ca'].amsOrder:''}&nbsp;${data[x]['ca'].outsideOrder>0?outsideSales:''}${data[x]['ca'].outsideOrder>0?data[x]['ca'].outsideOrder:'' }&nbsp;${data[x]['ca'].coupon>0?'coupon:':''}${data[x]['ca'].coupon>0?data[x]['ca'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=ca&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['ca'].classType}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['ca'].salesVolume}
									</a>
									<c:set value="${total+data[x]['ca'].salesVolume}" var="total" />
								</c:if>
							</td>
							<td class="sale11">
								<c:if test="${data[x]['mx'].salesVolume>0||(data[x]['mx'].salesVolume==0&&not empty data[x]['mx'].classType)}">
									<a ${(not empty data[x]['mx'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['mx'].businessOrder>0?'B2B:':''}${data[x]['mx'].businessOrder>0?data[x]['mx'].businessOrder:''}&nbsp;${data[x]['mx'].reviewVolume>0?'Marketing：':''}${data[x]['mx'].reviewVolume>0?data[x]['mx'].reviewVolume:'' }&nbsp;${data[x]['mx'].supportVolume>0?'Support：':''}${data[x]['mx'].supportVolume>0?data[x]['mx'].supportVolume:'' }&nbsp;${data[x]['mx'].promotionsOrder>0?promotionsSales:''}${data[x]['mx'].promotionsOrder>0?data[x]['mx'].promotionsOrder:'' }&nbsp;${data[x]['mx'].flashSalesOrder>0?flashOrder:''}${data[x]['mx'].flashSalesOrder>0?data[x]['mx'].flashSalesOrder:'' }&nbsp;${data[x]['mx'].maxOrder>0?bulkOrder:''}${data[x]['mx'].maxOrder>0?data[x]['mx'].maxOrder:'' }&nbsp;${data[x]['mx'].freeOrder>0?freeSales:''}${data[x]['mx'].freeOrder>0?data[x]['mx'].freeOrder:'' }&nbsp;${data[x]['mx'].adsOrder>0?'SPA：':''}${data[x]['mx'].adsOrder>0?data[x]['mx'].adsOrder:'' }&nbsp;${data[x]['mx'].amsOrder>0?'AMS:':''}${data[x]['mx'].amsOrder>0?data[x]['mx'].amsOrder:''}&nbsp;${data[x]['mx'].outsideOrder>0?outsideSales:''}${data[x]['mx'].outsideOrder>0?data[x]['mx'].outsideOrder:'' }&nbsp;${data[x]['mx'].coupon>0?'coupon:':''}${data[x]['mx'].coupon>0?data[x]['mx'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=mx&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['mx'].promotionsOrder>0?'btn-warning':(data[x]['mx'].classType)}" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['mx'].salesVolume}
									</a>
									<c:set value="${total+data[x]['mx'].salesVolume}" var="total" />
								</c:if>
							</td>
							<td class="sale10">
								<c:if test="${total>0||(total==0&&not empty data[x]['mx'].classType)}">
									<a class="btn btn-small ${data[x]['total'].classType }" ${(not empty data[x]['total'].classType)?'data-toggle=popover  data-placement=left  data-html=true rel=popover':''} data-content="${data[x]['total'].businessOrder>0?'B2B:':''}${data[x]['total'].businessOrder>0?data[x]['total'].businessOrder:''}&nbsp;${data[x]['total'].reviewVolume>0?'Marketing：':''}${data[x]['total'].reviewVolume>0?data[x]['total'].reviewVolume:'' }&nbsp;${data[x]['total'].supportVolume>0?'Support：':''}${data[x]['total'].supportVolume>0?data[x]['total'].supportVolume:'' }&nbsp;${data[x]['total'].promotionsOrder>0?promotionsSales:''}${data[x]['total'].promotionsOrder>0?data[x]['total'].promotionsOrder:'' }&nbsp;${data[x]['total'].flashSalesOrder>0?flashOrder:''}${data[x]['total'].flashSalesOrder>0?data[x]['total'].flashSalesOrder:'' }&nbsp;${data[x]['total'].maxOrder>0?bulkOrder:''}${data[x]['total'].maxOrder>0?data[x]['total'].maxOrder:'' }&nbsp;${data[x]['total'].freeOrder>0?freeSales:''}${data[x]['total'].freeOrder>0?data[x]['total'].freeOrder:'' }&nbsp;${data[x]['total'].adsOrder>0?'SPA：':''}${data[x]['total'].adsOrder>0?data[x]['total'].adsOrder:'' }&nbsp;${data[x]['total'].amsOrder>0?'AMS:':''}${data[x]['total'].amsOrder>0?data[x]['total'].amsOrder:''}&nbsp;${data[x]['total'].outsideOrder>0?outsideSales:''}${data[x]['total'].outsideOrder>0?data[x]['total'].outsideOrder:'' }&nbsp;${data[x]['total'].coupon>0?'coupon:':''}${data[x]['total'].coupon>0?data[x]['total'].coupon:''}"  style="height:14px; font-size:12px; line-height:12px;" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total">
										${total}
									</a>
								</c:if>
							</td>
							
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2"><b style="font-size: 18px">合计|TOTAL</b></td>
						<td id="t1"></td>
						<td id="t2"></td>
						<td id="t3"></td>
						<td id="t4"></td>
						<td id="t5"></td>
						<td id="t6"></td>
						<td id="t7"></td>
						
						<td id="t8"></td>
						<td id="t9"></td>
						<td id="t11"></td>
						<td id="t10"></td>
					</tr>
					
					<%--for test<tr class="alert alert-block">
						<td colspan="2"><b style="font-size: 18px">退货率</b></td>
						<td>
							<c:if test="${not empty returnGoods['de'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['de'][1]}<br/>退货量:${returnGoods['de'][2]}">
									${returnGoods['de'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['uk'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['uk'][1]}<br/>退货量:${returnGoods['uk'][2]}">
									${returnGoods['uk'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['fr'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['fr'][1]}<br/>退货量:${returnGoods['fr'][2]}">
								${returnGoods['fr'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['it'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['it'][1]}<br/>退货量:${returnGoods['it'][2]}">
								${returnGoods['it'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['es'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['es'][1]}<br/>退货量:${returnGoods['es'][2]}">
								${returnGoods['es'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['eu'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['eu'][1]}<br/>退货量:${returnGoods['eu'][2]}">
								${returnGoods['eu'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['com'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['com'][1]}<br/>退货量:${returnGoods['com'][2]}">
								${returnGoods['com'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['jp'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['jp'][1]}<br/>退货量:${returnGoods['jp'][2]}">
								${returnGoods['jp'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['ca'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['ca'][1]}<br/>退货量:${returnGoods['ca'][2]}">
								${returnGoods['ca'][5]}%
								</a>
							</c:if>
						</td>
						<td>		
							<c:if test="${not empty returnGoods['mx'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['mx'][1]}<br/>退货量:${returnGoods['mx'][2]}">
								${returnGoods['mx'][5]}%
								</a>
							</c:if>
						</td>						
						<td>
							<c:if test="${not empty returnGoods['total'][5]}">
								<a style="color: #08c;" href="#" data-placement="bottom" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['total'][1]}<br/>退货量:${returnGoods['total'][2]}">
								${returnGoods['total'][5]}%
								</a>
							</c:if>
						</td>
					</tr>
					<tr class="alert alert-error">
						<td colspan="2"><b style="font-size: 18px">差评率</b></td>
						<td>
							<c:if test="${not empty returnGoods['de'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['de'][4]}<br/>差评量:${returnGoods['de'][3]}">
									${returnGoods['de'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['uk'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['uk'][4]}<br/>差评量:${returnGoods['uk'][3]}">
								${returnGoods['uk'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['fr'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['fr'][4]}<br/>差评量:${returnGoods['fr'][3]}">
								${returnGoods['fr'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['it'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['it'][4]}<br/>差评量:${returnGoods['it'][3]}">
								${returnGoods['it'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['es'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['es'][4]}<br/>差评量:${returnGoods['es'][3]}">
								${returnGoods['es'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['eu'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['eu'][4]}<br/>差评量:${returnGoods['eu'][3]}">
								${returnGoods['eu'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['com'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['com'][4]}<br/>差评量:${returnGoods['com'][3]}">
								${returnGoods['com'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['jp'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['jp'][4]}<br/>差评量:${returnGoods['jp'][3]}">
								${returnGoods['jp'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['ca'][6]}">
								<a style="color: #08c;" href="#"  data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['ca'][4]}<br/>差评量:${returnGoods['ca'][3]}">
								${returnGoods['ca'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['mx'][6]}">
								<a style="color: #08c;" href="#"  data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['mx'][4]}<br/>差评量:${returnGoods['mx'][3]}">
								${returnGoods['mx'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['total'][6]}">
								<a style="color: #08c;" href="#" data-placement="bottom"  data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['total'][4]}<br/>差评量:${returnGoods['total'][3]}">
								${returnGoods['total'][6]}%
								</a>
							</c:if>
						</td>
					</tr> for test end--%>
				</tfoot>
			</table>
			
	 <div class="accordion-heading" id="showGoodsDetail" style="height:30px"><a ><b><spring:message code="amazon_product_return_rate"/></b></a></div>
	 <div id="goodsCode"  style="display: none" >
		<table  id='detailGoodsTable' class="desc table table-striped table-bordered table-condensed">
			<thead>
				<tr>
				    <td></td>
					<td>德国|DE</td>
					<td>英国|UK</td>
					<td>法国|FR</td>
					<td>意大利|IT</td>
					<td>西班牙|ES</td>
					<td>美国|US</td>
					<td>日本|JP</td>
					<td>加拿大|CA</td>
					<td>墨西哥|MX</td>
					<td>全球|GLOBAL</td>
				</tr>
			</thead>
			<tbody>	
			</tbody>
		</table>
	</div>
	 <div  id="showChartsDetail" style="height:30px;"><a class='btn btn-info'  target='_blank' href="${ctx}/psi/psiInventory/getCharts?productName=${productName }&startDate=${fns:getDateByPattern(saleReport.start,'yyyyMMdd')}&endDate=${fns:getDateByPattern(saleReport.end,'yyyyMMdd')}&searchType=${saleReport.searchType}"><b><spring:message code="amazon_product_charts"/></b></a></div>
	<br/>
	
			<div style="border:1px solid #ccc;width: 98%">
				<div id="chart" style="height:400px;"></div>
			</div>
		</div>
		<div id="sale" class="tab-pane">
			<table id="saleTb" class="table table-striped table-bordered table-condensed desc">
				<thead>
					<tr>
						<th ><spring:message code="amazon_order_date"/></th>
						<th >
						 <c:choose>
								<c:when test="${'1' eq saleReport.searchType}"><spring:message code="amazon_order_week"/></c:when>
								<c:otherwise><spring:message code="amazon_order_section"/></c:otherwise>
					       </c:choose>
						</th>
						<th >德国|DE(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >英国|UK(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >法国|FR(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >意大利|IT(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >西班牙|ES(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >欧洲|EU(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >美国|US(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >日本|JP(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >加拿大|CA(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >墨西哥|MX(${'USD' eq saleReport.currencyType?'$':'€'})</th>
						<th >全球|GLOBAL(${'USD' eq saleReport.currencyType?'$':'€'})</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="x" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<c:set value="0" var="eu" />
							<c:set value="0" var="total" />
							<td>${x}${type}</td>
							<td>${tip[x]}</td>
							<td class="v1">
								${data[x]['de'].sales}
								<c:if test="${data[x]['de'].sales>0}">
									<c:set value="${eu+data[x]['de'].sales}" var="eu" />
								</c:if>
							</td>
							<td class="v2">
								${data[x]['uk'].sales}
								<c:if test="${data[x]['uk'].sales>0}">
									<c:set value="${eu+data[x]['uk'].sales}" var="eu" />
								</c:if>
							</td>
							<td class="v3">
								${data[x]['fr'].sales}
								<c:if test="${data[x]['fr'].sales>0}">
									<c:set value="${eu+data[x]['fr'].sales}" var="eu" />
								</c:if>
							</td>
							<td class="v4">
								${data[x]['it'].sales}
								<c:if test="${data[x]['it'].sales>0}">
									<c:set value="${eu+data[x]['it'].sales}" var="eu" />
								</c:if>
							</td>
							<td class="v5">
								${data[x]['es'].sales}
								<c:if test="${data[x]['es'].sales>0}">
									<c:set value="${eu+data[x]['es'].sales}" var="eu" />
								</c:if>
							</td>
							<td class="v6">
								<c:if test="${eu>0}">
									<fmt:formatNumber value="${eu}" pattern="#########.##" maxFractionDigits="2"/>  
								</c:if>
								<c:set value="${total+eu}" var="total" />
							</td>
							<td class="v7">
								${data[x]['com'].sales}
								<c:if test="${data[x]['com'].sales>0}">
									<c:set value="${total+data[x]['com'].sales}" var="total" />
								</c:if>
							</td>
							<td class="v8">
								${data[x]['jp'].sales}
								<c:if test="${data[x]['jp'].sales>0}">
									<c:set value="${total+data[x]['jp'].sales}" var="total" />
								</c:if>
							</td>
							<td class="v9">
								${data[x]['ca'].sales}
								<c:if test="${data[x]['ca'].sales>0}">
									<c:set value="${total+data[x]['ca'].sales}" var="total" />
								</c:if>
							</td>
							<td class="v11">
								${data[x]['mx'].sales}
								<c:if test="${data[x]['mx'].sales>0}">
									<c:set value="${total+data[x]['mx'].sales}" var="total" />
								</c:if>
							</td>
							<td class="v10">
								<c:if test="${total>0}">
									<fmt:formatNumber value="${total}" pattern="#########.##" maxFractionDigits="2" minFractionDigits="2"/>
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2"><b style="font-size: 18px">合计|TOTAL</b></td>
						<td id="vt1"></td>
						<td id="vt2"></td>
						<td id="vt3"></td>
						<td id="vt4"></td>
						<td id="vt5"></td>
						<td id="vt6"></td>
						<td id="vt7"></td>
						<td id="vt8"></td>
						<td id="vt9"></td>
						<td id="vt11"></td>
						<td id="vt10"></td>
					</tr>
				</tfoot>
			</table>
			<div id="saleChartDiv" style="border:1px solid #ccc;width: 98%">
				<div id="saleChart" style="height:400px;"></div>
			</div>
		</div><div id="contrast" class="tab-pane">
			<table id="contrastTb" class="table table-striped table-bordered table-condensed desc">
				<thead>
					<tr>
					    <th><spring:message code="amazon_order_date"/></th>
						<th>
						 <c:choose>
								<c:when test="${'1' eq saleReport.searchType}"><spring:message code="amazon_order_week"/></c:when>
								<c:otherwise><spring:message code="amazon_order_section"/></c:otherwise>
					       </c:choose>
						</th>
						
						<th >德国|DE</th>
						<th >英国|UK</th>
						<th >法国|FR</th>
						<th >意大利|IT</th>
						<th >西班牙|ES</th>
						<th >欧洲|EU</th>
						<th >美国|US</th>
						<th >日本|JP</th>
						<th >加拿大|CA</th>
						<th >墨西哥|MX</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="x" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td>${x}${type}</td>
							<td>${tip[x]}</td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2"><b style="font-size: 18px">合计|TOTAL</b></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
						<td ></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
	<br/>
	
	<div id="tranTip" class="modal hide fade" tabindex="-1" data-width="750">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Trans NO.</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Model</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Sku</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">From</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_destination_warehouse"/></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Quantity</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_warehouse_date"/></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_predicted_in_stock_date"/></th>
						<th style="text-align: left;vertical-align: middle;">Remarks</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	<div id="preTranTip" class="modal hide fade" tabindex="-1" data-width="750">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Trans NO.</th>
							<th style="width: 100px;text-align: center;vertical-align: middle;">From</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Country</th>
					
						<th style="width: 100px;text-align: center;vertical-align: middle;">Model</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_destination"/></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Quantity</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_predicted_warehouse_date"/></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_predicted_in_stock_date"/></th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	
		<div id="recallingTip" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" >&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th >Order NO.</th>
						<th >Country</th>
						<th >Sku</th>
						<th >RequestedQty</th>
						<th >InProcessQty</th>
						<th >createDate</th>
						<th>disposition</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	<div id="fbaTran" class="modal hide fade" tabindex="-1" data-width="850" style="width: 850px">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 300px;text-align: center;vertical-align: middle;">ShipmentName</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">ShipmentId</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">SKU</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_shipped_quantity"/></th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_received_quantity"/></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_state"/></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">pickUpDate</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_expected_arrival"/></th>
						<th style="text-align: left;vertical-align: middle;">Remarks</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>

	<div id="produceTip" class="modal hide fade" tabindex="-1" data-width="750">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code="psi_inventory_production_list"/></h3>
		</div>
		<div class="alert alert-info" id="productImprove" style="display: none;margin-bottom:0px"></div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code="custom_event_form6"/></th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code="sys_label_country"/> </th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_online"/></th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_offline"/></th>
						<th style="width: 150px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_order_date"/></th>
						<th style="width: 150px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_transaction_date"/></th>
						<th style="width: 150px;text-align: center;vertical-align: middle;"><spring:message code="psi_inventory_estimated_delivery_date"/></th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code="psi_transport_week"/></th>
						<th style="width: 150px;text-align: center;vertical-align: middle;">Remarks</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	<div class="modal hide fade" id="tip">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 style="color: red">Info</h3>
		</div>
		<div class="modal-body">
			<p style="font-size: 18px;font-weight: bold;"><spring:message code="amazon_sales_data_info"/></p>
		</div>
		<div class="modal-footer">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
	</div>
</body>
</html>
