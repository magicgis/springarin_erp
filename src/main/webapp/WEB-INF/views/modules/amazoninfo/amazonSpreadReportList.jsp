<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>report</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		var strdata=new Array();
		$.fn.dataTableExt.afnSortData['dom-html'] = function  ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+') a', tr).html();
			} );
		}
		
		
		$(document).ready(function() {
			
			 $(".report").popover({placement:'left',html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var asin = $(this).attr("asinKey");
					var sku = $(this).attr("skuKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".content").html();
				//	console.log(content);
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/amazonOperationalReport/sessionCharts',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"asin":asin,
						    	"sku":sku,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".content").html(data);
					        }
						});
					}
					return content;
				}}); 
			 
			 $(".report1").popover({placement:'left',html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var asin = $(this).attr("asinKey");
					var sku = $(this).attr("skuKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".content1").html();
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/amazonOperationalReport/orderCharts',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"asin":asin,
						    	"sku":sku,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".content1").html(data);
					        }
						});
					}
					return content;
				}}); 
			 
			 
			 $(".report2").popover({placement:'left',html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var asin = $(this).attr("asinKey");
					var sku = $(this).attr("skuKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".content2").html();
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/amazonOperationalReport/salesVolumeCharts',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"asin":asin,
						    	"sku":sku,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".content2").html(data);
					        }
						});
					}
					return content;
				}}); 
			 
			 
			 $(".report3").popover({placement:'left',html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var asin = $(this).attr("asinKey");
					var sku = $(this).attr("skuKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".content3").html();
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/amazonOperationalReport/salesCharts',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"asin":asin,
						    	"sku":sku,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".content3").html(data);
					        }
						});
					}
					return content;
				}}); 
				
			 
			 
			 $(".report4").popover({placement:'auto',html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var asin = $(this).attr("asinKey");
					var sku = $(this).attr("skuKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".content4").html();
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/amazonOperationalReport/conversionCharts',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"asin":asin,
						    	"sku":sku,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".content4").html(data);
					        }
						});
					}
					return content;
				}});
				
				$(".report").live("click",function(){
					$this = this;
					$(".report").each(function(){
						if(this != $this){
							$(this).popover('hide');
						}
					});
					$(".report1").each(function(){
						$(this).popover('hide');
					});
					$(".report2").each(function(){
						$(this).popover('hide');
					});
					$(".report3").each(function(){
						$(this).popover('hide');
					});
					$(".report4").each(function(){
						$(this).popover('hide');
					});
					
					var p = $(this).position();
					$('.fade').css("top",p.top).css("left",p.left-580);
					$('.fade').css("max-width","700px");

				});
				
				$(".report1").live("click",function(){
					$this = this;
					$(".report1").each(function(){
						if(this != $this){
							$(this).popover('hide');
						}
					});
					$(".report").each(function(){
						$(this).popover('hide');
					});
					$(".report2").each(function(){
						$(this).popover('hide');
					});
					$(".report3").each(function(){
						$(this).popover('hide');
					});
					$(".report4").each(function(){
						$(this).popover('hide');
					});
					
					var p = $(this).position();
					$('.fade').css("top",p.top).css("left",p.left-580);
					$('.fade').css("max-width","700px");
					
				});
				
				$(".report2").live("click",function(){
					$this = this;
					$(".report2").each(function(){
						if(this != $this){
							$(this).popover('hide');
						}
					});
					$(".report1").each(function(){
						$(this).popover('hide');
					});
					$(".report").each(function(){
						$(this).popover('hide');
					});
					$(".report3").each(function(){
						$(this).popover('hide');
					});
					$(".report4").each(function(){
						$(this).popover('hide');
					});
					
					var p = $(this).position();
					$('.fade').css("top",p.top).css("left",p.left-580);
					$('.fade').css("max-width","700px");
					
				});
				
				$(".report3").live("click",function(){
					$this = this;
					$(".report3").each(function(){
						if(this != $this){
							$(this).popover('hide');
						}
					});
					$(".report1").each(function(){
						$(this).popover('hide');
					});
					$(".report2").each(function(){
						$(this).popover('hide');
					});
					$(".report").each(function(){
						$(this).popover('hide');
					});
					$(".report4").each(function(){
						$(this).popover('hide');
					});
					
					var p = $(this).position();
					$('.fade').css("top",p.top).css("left",p.left-580);
					$('.fade').css("max-width","700px");
					
				});
				
				
				$(".report4").live("click",function(){
					$this = this;
					$(".report4").each(function(){
						if(this != $this){
							$(this).popover('hide');
						}
					});
					$(".report1").each(function(){
						$(this).popover('hide');
					});
					$(".report2").each(function(){
						$(this).popover('hide');
					});
					$(".report3").each(function(){
						$(this).popover('hide');
					});
					$(".report").each(function(){
						$(this).popover('hide');
					});
					
					var p = $(this).position();
					$('.fade').css("top",p.top).css("left",p.left-580);
					$('.fade').css("max-width","700px");
					
				});
				
				
			 	$("a[rel='popover']").popover({container:"body",trigger:'hover'});
				
				$("a[rel='popover']").live("mouseover",function(){
					$(".report").popover('hide');
					$(".report1").popover('hide');
					$(".report2").popover('hide');
					$(".report3").popover('hide');
					$(".report4").popover('hide');
					var p = $(this).position();
					$('.fade').css("max-width","700px").css("top",p.top).css("left",p.left-580);
				}); 
				
				$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap",
					"iDisplayLength": 10,
					"aLengthMenu":[[10, 30, 60,100,-1], [10, 30, 60, 100, "All"]],
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
				 	"ordering":true,
					"aoColumns": [null,null,null,null,null,
					              <shiro:hasPermission name="spreadReport:price:view">
						             null,null,
						          </shiro:hasPermission>
							      { "sSortDataType":"dom-html", "sType":"numeric"},
							      { "sSortDataType":"dom-html", "sType":"numeric"},
							      { "sSortDataType":"dom-html", "sType":"numeric"},
							      { "sSortDataType":"dom-html", "sType":"numeric"},
							      { "sSortDataType":"dom-html", "sType":"numeric"},
							      ],
				    "aaSorting": [[1, "desc" ]]
				});
				
				$(".countryHref").click(function(){
					$("input[name='country']").val($(this).attr("key"));
					$("#inputForm").submit();
				});
				
				
				var cnt="<form  id='inputForm' action='${ctx}/amazoninfo/amazonOperationalReport/spreadReportList' method='post' >"+
				"&nbsp;&nbsp;&nbsp;&nbsp;时间: "+
				"	<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'});  readonly='readonly' class='Wdate' type='text' name='createDate' value='<fmt:formatDate value="${amazonSpreadReport.createDate}" pattern="yyyy-MM-dd" />'  class='input-small'/> "+
				" -<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'});  readonly='readonly' class='Wdate' type='text'  name='endDate' value='<fmt:formatDate value="${amazonSpreadReport.endDate}" pattern="yyyy-MM-dd" />'  class='input-small'/> "+
				"	<input name='country' type='hidden' value='${amazonSpreadReport.country}'/><input class='btn btn-primary' type='submit' value='查询'/> &nbsp;&nbsp;<input class='btn btn-primary' onclick='exportData();'  type='button' value='导出'/> &nbsp;&nbsp;   "+
				"</form> ";
				 $(".row:first").append(cnt);
				 
			
		});
        
		function exportData(){
			$("#inputForm").attr("action","${ctx}/amazoninfo/amazonOperationalReport/exportSpreadReportList");
			$("#inputForm").submit();
			$("#inputForm").attr("action","${ctx}/amazoninfo/amazonOperationalReport/spreadReportList");
		}
		
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonSpreadReport.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: left;vertical-align: middle;">Product Line</th>
				<th style="text-align: left;vertical-align: middle;">Product Name</th>
				<th style="text-align: left;vertical-align: middle;">Asin</th>
				<th style="text-align: left;vertical-align: middle;">Sku</th>
				<th style="text-align: left;vertical-align: middle;">Price</th>
				<shiro:hasPermission name="spreadReport:price:view">
					<th style="text-align: left;vertical-align: middle;">Cost</th>
					<th style="text-align: left;vertical-align: middle;">Profit</th>
				</shiro:hasPermission>
				<th style="text-align: left;vertical-align: middle;">Daily Session</th>
				<th style="text-align: left;vertical-align: middle;">Daily Order</th>
				<th style="text-align: left;vertical-align: middle;">Daily Ps</th>
				<th style="text-align: left;vertical-align: middle;">Daily Revenue</th>
				<th style="text-align: left;vertical-align: middle;">CR(%)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${reportList}" var="report" varStatus="i">
				<tr>
					<td>${nameAndLineMap[report.productName]}</td>
					<td>${report.productName}</td>
					<td>${report.asin}</td>
					<td>${report.sku}</td>
					
					<td>${report.price}</td>
					<shiro:hasPermission name="spreadReport:price:view">
							<td><fmt:formatNumber value="${report.cost}" maxFractionDigits="2" minFractionDigits="2" /></td>
							<td><fmt:formatNumber value="${report.profit}" maxFractionDigits="2" minFractionDigits="2" /></td>
					</shiro:hasPermission>
					<td>
					  <a href="#"   nameKey="${report.productName}" countryKey="${report.country}" asinKey="${report.asin}" skuKey="${report.sku}" startKey="${fns:getDateByPattern(amazonSpreadReport.createDate,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(amazonSpreadReport.endDate,'yyyy-MM-dd')}" class="report">${report.session}</a>
					  <div class="content" style="display: none"></div>
					</td>
					<td>
					  <a href="#"   nameKey="${report.productName}" countryKey="${report.country}" asinKey="${report.asin}" skuKey="${report.sku}" startKey="${fns:getDateByPattern(amazonSpreadReport.createDate,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(amazonSpreadReport.endDate,'yyyy-MM-dd')}" class="report1">${report.order}</i></a>
					  <div class="content1" style="display: none"></div>
					</td>
					<td>
					  <a href="#"   nameKey="${report.productName}" countryKey="${report.country}" asinKey="${report.asin}" skuKey="${report.sku}" startKey="${fns:getDateByPattern(amazonSpreadReport.createDate,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(amazonSpreadReport.endDate,'yyyy-MM-dd')}" class="report2">${report.salesVolume}</a>
					  <div class="content2" style="display: none"></div>
					</td>
					<td>
					  <a href="#"   nameKey="${report.productName}" countryKey="${report.country}" asinKey="${report.asin}" skuKey="${report.sku}" startKey="${fns:getDateByPattern(amazonSpreadReport.createDate,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(amazonSpreadReport.endDate,'yyyy-MM-dd')}" class="report3">${report.sales}</a>
					  <div class="content3" style="display: none"></div>
					</td>
					<td>
					  <a href="#" nameKey="${report.productName}" countryKey="${report.country}" asinKey="${report.asin}" skuKey="${report.sku}" startKey="${fns:getDateByPattern(amazonSpreadReport.createDate,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(amazonSpreadReport.endDate,'yyyy-MM-dd')}" class="report4">${report.conversion}</a>
					  <div class="content4" style="display: none"></div>
					</td>
	           </tr> 
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
