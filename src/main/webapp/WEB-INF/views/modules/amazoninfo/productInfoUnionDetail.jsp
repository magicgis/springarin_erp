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
					}if(clsAttr.indexOf('special')>0){
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
	    	var total = $.trim(trTfoot.find("td :eq(11)").text());
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
             
            
		});
		
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
		

		function display_by(type,country,name){
			var productName = name;
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
						body +="<td style='vertical-align: middle;text-align: center;' >"+ele.toCountry+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+(ele.offlineSta=='1'?"(线下)":"")+"</td><td style='vertical-align: middle;text-align: center;'>"
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
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityOffline+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.orderDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.deliveryDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.tranWeek+"</td><td style='vertical-align: middle;text-align: center;'>"
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
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;' >"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"
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
	</script>
</head>
<body>
<div id="imgtest"></div> 
	<table class="desc table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<td width="100">
					<a href="#" class="thumbnail">
				      <img src="<c:url value='${not empty firstImage?firstImage:product.link}'/>" alt="" class="image1">
				    </a>
				</td>
				<td style="font-weight: bold;" colspan="20">
					<span style="float: left;margin-left: 350px">${fn:replace(product.chineseName,';','</br>')}</span> 
					<span style="float: left;margin-left: ${not empty product.chineseName?'10':'350'}px">
					<c:if test="${not empty product.chineseName}">
						&nbsp;&nbsp;&nbsp;&&nbsp;&nbsp;&nbsp;
					</c:if>
					<input id="typeahead" type="text" class="span3 search-query" value="${productName}" style="width:200px;margin-top: 5px"  autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
					</span>
				</td>
			</tr>
			<tr>
			    <th><spring:message code="amaInfo_businessReport_productName"/></th>
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
				
			</tr>
			<c:set var="temp1" value="0" />
			<c:set var="temp2" value="0" />
			<c:set var="temp3" value="0" />
			<c:set var="temp4" value="0" />
			<c:set var="temp5" value="0" />
			<c:set var="temp6" value="0" />
			<c:set var="temp7" value="0" />
			<c:set var="temp8" value="0" />
			<c:set var="temp9" value="0" />
			<c:set var="temp10" value="0" />
			<c:set var="temp11" value="0" />
			<c:forEach items="${nameList }" var="name">
			    <tr>
				    <td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}">${fn:substringAfter(name," ")}</a></td>
				    <c:set var="producting1"  value="${producting[name].quantity}"/>
					<c:set var="transportting1"  value="${transportting[name].quantity}"/>
					<c:set var="transportting2"  value="${preTransportting[name].quantity}"/>
					<c:set var="inventorysCN"  value="${inventorys[name].totalQuantityCN}"/>
					<c:set var="inventorysNotCN"  value="${inventorys[name].totalQuantityNotCN}"/>
					<td>
					<c:if test="${producting1>0}">
					    <c:set var="temp1" value="${temp1+producting1 }" />
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'','${name}');return false;">
							${producting1}
						</a>
					</c:if>
				</td>
				<td><c:if test="${fn:length(inventorys[name].cnTip)>0}"><c:set var="temp2" value="${temp2+inventorysCN }" /></c:if>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">
						${fn:length(inventorys[name].cnTip)>0?inventorysCN:''}
					</a>
				</td>
				<td>
					<c:if test="${transportting2>0}"><c:set var="temp3" value="${temp3+transportting2 }" />
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'','${name}');return false;">
							${transportting2}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting1>0}"><c:set var="temp4" value="${temp4+transportting1 }" />
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'','${name}');return false;">
							${transportting1}
						</a>
					</c:if>
				</td>
				<td><c:if test="${fn:length(inventorys[name].notCnTip)>0}"><c:set var="temp5" value="${temp5+inventorysNotCN }" /></c:if>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].notCnTip}">
						${fn:length(inventorys[name].notCnTip)>0?inventorysNotCN:''}
					</a>
				</td>
				<td>
					<c:if test="${returnMap[name]['total']>0}"><c:set var="temp6" value="${temp6+returnMap[name]['total']}" />
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(8,'total','${name}');return false;">${returnMap[name]['total']}</a>
					</c:if>
				</td>
				<td>
					<c:set var="de" value="${name}_de" />
					<c:set var="de"  value="${fbaTran[de]>0?fbaTran[de]:0}" />
					<c:set var="fr" value="${name}_fr" />
					<c:set var="fr"  value="${fbaTran[fr]>0?fbaTran[fr]:0}" />
					<c:set var="uk" value="${name}_uk" />
					<c:set var="uk"  value="${fbaTran[uk]>0?fbaTran[uk]:0}" />
					<c:set var="it" value="${name}_it" />
					<c:set var="it"  value="${fbaTran[it]>0?fbaTran[it]:0}" />
					<c:set var="es" value="${name}_es" />
					<c:set var="es"  value="${fbaTran[es]>0?fbaTran[es]:0}" />
					<c:set var="fbaEuTran"  value="${de+fr+uk+it+es}" />
					<c:set var="com" value="${name}_com" />
					<c:set var="com"  value="${fbaTran[com]>0?fbaTran[com]:0}" />
					<c:set var="ca" value="${name}_ca" />
					<c:set var="ca"  value="${fbaTran[ca]>0?fbaTran[ca]:0}" />
					<c:set var="mx" value="${name}_mx" />
					<c:set var="mx"  value="${fbaTran[mx]>0?fbaTran[mx]:0}" />
					
					<c:set var="jp" value="${name}_jp" />
					<c:set var="jp"  value="${fbaTran[jp]>0?fbaTran[jp]:0}" />
					<c:set value="${de+fr+uk+it+es+com+ca+jp+mx}" var="fbaTrans" />
					<c:if test="${fbaTrans>0}"><c:set var="temp7" value="${temp7+fbaTrans}" />
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'','${name}');return false;">${fbaTrans}</a>
					</c:if>
				</td>
				<td>
					<c:set var="de" value="${name}_de" />
					<c:set var="de"  value="${fbas[de].total>0?fbas[de].total:0}" />
					<c:set var="fr" value="${name}_fr" />
					<c:set var="fr"  value="${fbas[fr].total>0?fbas[fr].total:0}" />
					<c:set var="uk" value="${name}_uk" />
					<c:set var="uk"  value="${fbas[uk].total>0?fbas[uk].total:0}" />
					<c:set var="it" value="${name}_it" />
					<c:set var="it"  value="${fbas[it].total>0?fbas[it].total:0}" />
					<c:set var="es" value="${name}_es" />
					<c:set var="es"  value="${fbas[es].total>0?fbas[es].total:0}" />
					<c:set var="com" value="${name}_com" />
					<c:set var="com"  value="${fbas[com].total>0?fbas[com].total:0}" />
					<c:set var="ca" value="${name}_ca" />
					<c:set var="ca"  value="${fbas[ca].total>0?fbas[ca].total:0}" />
					<c:set var="mx" value="${name}_mx" />
					<c:set var="mx"  value="${fbas[mx].total>0?fbas[mx].total:0}" />
					
					<c:set var="jp" value="${name}_jp" />
					<c:set var="jp"  value="${fbas[jp].total>0?fbas[jp].total:0}" />
					<c:set value="${de+fr+uk+it+es+com+ca+jp}" var="fbaTotal" />
					<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>US:${com}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}<br/>CA:${ca}<br/>MX:${mx}<br/>JP:${jp}">${fbaTotal>0?fbaTotal:''}</a>
					<c:if test="${fbaTotal>0 }"><c:set var="temp8" value="${temp8+fbaTotal}" />	</c:if>			
				</td>
				<td>
					<c:set var="total" value="${fbaTotal+producting1+transportting1+inventorysCN+inventorysNotCN+returnMap['total']}" />
					${total>0?total:''}<c:if test="${total>0 }"><c:set var="temp9" value="${temp9+total}" />	</c:if>	
				</td>
				
				<td>${empty productAttr[name]?'':productAttr[name][1]}<c:if test="${not empty productAttr[name]}"><c:set var="temp10" value="${temp10+productAttr[name][1]}" />	</c:if>	</td>
				<td>${fancha[name].day31Sales>0?fancha[name].day31Sales:''}<c:if test="${fancha[name].day31Sales>0}"><c:set var="temp11" value="${temp11+fancha[name].day31Sales}" />	</c:if></td>
				<td>${fns:roundUp(fancha[name].day31Sales/31)}</td>
				<td>
					<c:if test="${total>0 && fancha[name].day31Sales>0}">
						<fmt:formatNumber value="${total/fancha[name].day31Sales}" maxFractionDigits="1" />
					</c:if>
				</td>
				<%--预测库销比 --%>
				<td>
					<c:if test="${not empty productAttr[name] && productAttr[name][2]> 0}">
						<fmt:formatNumber value="${productAttr[name][2]}" maxFractionDigits="1" />
					</c:if>
				</td>
				 </tr>
			 </c:forEach>
			 
			<tr>
			    <td>Total</td>
			    <td>${temp1>0?temp1:''}</td>
			    <td>${temp2>0?temp2:'' }</td>
			    <td>${temp3>0?temp3:'' }</td>
			    <td>${temp4>0?temp4:'' }</td>
			    <td>${temp5>0?temp5:'' }</td>
			    <td>${temp6>0?temp6:'' }</td>
			    <td>${temp7>0?temp7:'' }</td>
			    <td>${temp8>0?temp8:'' }</td>
			    <td>${temp9>0?temp9:'' }</td>
			    <td>${temp10>0?temp10:'' }</td>
			    <td>${temp11>0?temp11:''}</td>
			    <td>${fns:roundUp(temp11/31)}</td>
			    <td><fmt:formatNumber value="${temp9/temp11}" maxFractionDigits="1" /></td>
			    <td></td>
			 </tr>
		</tbody>
	</table>
	
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
		 
		</span><br/><br/>
		<span style="float: left;">
		 <a class="btn btn-small btn-success" style="height:14px; font-size:12px; line-height:12px;">Vendor</a>
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
									<a ${(not empty data[x]['com'].classType)?'data-toggle=popover data-html=true rel=popover':''} data-content="${data[x]['com'].businessOrder>0?'B2B:':''}${data[x]['com'].businessOrder>0?data[x]['com'].businessOrder:''}&nbsp;${data[x]['com'].reviewVolume>0?'Marketing：':''}${data[x]['com'].reviewVolume>0?data[x]['com'].reviewVolume:'' }&nbsp;${data[x]['com'].supportVolume>0?'Support：':''}${data[x]['com'].supportVolume>0?data[x]['com'].supportVolume:'' }&nbsp;${data[x]['com'].promotionsOrder>0?promotionsSales:''}${data[x]['com'].promotionsOrder>0?data[x]['com'].promotionsOrder:'' }&nbsp;${data[x]['com'].flashSalesOrder>0?flashOrder:''}${data[x]['com'].flashSalesOrder>0?data[x]['com'].flashSalesOrder:'' }&nbsp;${data[x]['com'].maxOrder>0?bulkOrder:''}${data[x]['com2'].maxOrder>0?data[x]['com'].maxOrder:'' }&nbsp;${data[x]['com'].freeOrder>0?freeSales:''}${data[x]['com'].freeOrder>0?data[x]['com'].freeOrder:'' }&nbsp;${data[x]['com'].adsOrder>0?'SPA:':''}${data[x]['com'].adsOrder>0?data[x]['com'].adsOrder:'' }&nbsp;${data[x]['com'].amsOrder>0?'AMS:':''}${data[x]['com'].amsOrder>0?data[x]['com'].amsOrder:''}&nbsp;${data[x]['com2'].outsideOrder>0?outsideSales:''}${data[x]['com'].outsideOrder>0?data[x]['com'].outsideOrder:'' }&nbsp;${data[x]['com'].coupon>0?'coupon:':''}${data[x]['com'].coupon>0?data[x]['com'].coupon:''}" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=com&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small ${data[x]['com'].classType}" style="height:14px; font-size:12px; line-height:12px;">
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
					
				</tfoot>
			</table>
			
	
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
						<th >德国DE(${'USD' eq saleReport.currencyType?'$':'€'})</th>
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
