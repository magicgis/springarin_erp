<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>report</title>

	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
	</style>

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
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("select[name='productsName']").removeClass('required');
				$("#searchForm").submit();
			});
			
			 $(".report").popover({html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".rank").html();
				//	console.log(content);
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/promotionsWarning/lightningDealRankReport',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".rank").html(data);
					        }
						});
					}
					return content;
				}}); 
			 
			 $(".icon-remove").click(function(){
				    console.log(11);
				    $(".report").each(function(){
						$(this).popover('hide');
					});
					$(".report1").each(function(){
						$(this).popover('hide');
					});
					
			 });
			 $(".report1").popover({html:true,trigger:'click',content:function(){
					var country = $(this).attr("countryKey");
					var productName = $(this).attr("nameKey");
					var start = $(this).attr("startKey");
					var end = $(this).attr("endKey");
					
					$this =$(this);
					var content = $this.parent().find(".sales").html();
				//	console.log(content);
					if(!content){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/amazoninfo/promotionsWarning/lightningDealSalesReport',
						    data: {
						    	"country":country,
						    	"productName":productName,
						    	"start":start,
						    	"end":end
						    },
						    success:function(data){ 
						    	content = data;
						    	$this.parent().find(".sales").html(data);
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
					
					var p = $(this).position();
					if(p.left>700){
						$('.fade').css("left",p.left-700);
					}
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
					
					var p = $(this).position();
					if(p.left>700){
				    	$('.fade').css("left",p.left-700);
					}
					$('.fade').css("max-width","700px");

				});
	
		     <c:forEach items="${productsName}" var="pname" varStatus="i">
				     var otable=$("#contentTable${i.index}").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				    		"sScrollX": "100%",
						 	"ordering":false,
						 	"bSort":false,
						 	"searching": false, 
						    "sDom": '"top"i',  
						    "bFilter": false,    
						    "bLengthChange": false,
						    "bInfo":false,
						    "bPaginate":false
						});
						
					  new FixedColumns(otable,{
					 		"iLeftColumns":1,
							"iLeftWidth": 200
					 	} ); 
		     </c:forEach>
			
		});
		
		function findName(){
			 var param = {};
			    param.country=$("#country").val();
			    param.start=$("#start").val();
			    param.end=$("#end").val();
				$.get("${ctx}/amazoninfo/promotionsWarning/findProductNameList?"+$.param(param),function(data){
					$("#productsName").html("");
					var option="";
					$("#productsName").select2('destroy');
					for(var i=0;i<data.length;i++){
						option += "<option  value=\"" + data[i] + "\" >" + data[i] + "</option>"; 
	                }
					if(option!=''){
						$("#productsName").append(option);
					}
					$("#productsName").select2({"width":"80%"});
				});
			
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	<c:forEach items="${fns:getDictList('platform')}" var="dic">
		<c:if test="${dic.value ne 'com.unitek'}">
			<li class="${amazonLightningDeals.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
		</c:if>
	</c:forEach>
	</ul>	
	
	<form:form id="searchForm" modelAttribute="amazonLightningDeals" action="${ctx}/amazoninfo/promotionsWarning/lightningDealList" method="post" class="breadcrumb form-search">
		<div style="height: 80px;line-height: 40px;">
			<input  name="country" type="hidden" value="${amazonLightningDeals.country}" id="country"/>
			<spring:message code="amaInfo_businessReport_productName"/>:
			<select name="productsName" multiple class="multiSelect required"  style="width:80%" id="productsName">
				<c:forEach items="${productsName}" var="pName">
					<option value="${pName}" selected>${pName}</option>			
				</c:forEach>
				<c:forEach items="${productNames}" var="pName">
					<option value="${pName}">${pName}</option>			
				</c:forEach>
			</select>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			<!-- <a class="btn btn-primary"  id="countByProductsExport">导出</a> -->
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){findName();}});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${amazonLightningDeals.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){findName();}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${amazonLightningDeals.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		</div>
	</form:form>

		<c:if test="${not empty productsName}">
		  <c:forEach items="${productsName}" var="pname" varStatus="i">
		       <table id="contentTable${i.index}" class="table table-striped table-bordered table-condensed contentTable">
				<thead>
					<tr> 
						<td><b>${pname}</b></td>
						<c:forEach items="${map[pname]}" var="dateMap">
							<td  style="text-align: center;vertical-align: middle" class="title" ><b>${dateMap.key}</b></td>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<tr>
					    <td>闪促名称</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title">${map[pname][dateMap.key].internalDesc}</td>
						</c:forEach>
					</tr>
					<tr>
					    <td>售价</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title"><fmt:formatNumber value="${map[pname][dateMap.key].salePrice*rate}" maxFractionDigits="2"/></td>
						</c:forEach>
					</tr>
					<tr>
					    <td>折扣价</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" ><fmt:formatNumber value="${map[pname][dateMap.key].dealPrice*rate}" maxFractionDigits="2"/></td>
						</c:forEach>
					</tr>
					<tr>
					    <td>设置闪促数量</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" >${map[pname][dateMap.key].dealQuantity}</td>
						</c:forEach>
					</tr>
					<tr>
					    <td>实际促销数量</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" >${map[pname][dateMap.key].actualQuantity}</td>
						</c:forEach>
					</tr>
					<tr>
					    <td>单个盈亏(€)</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" ><span style="color:${map[pname][dateMap.key].dealPrice*rate-map[pname][dateMap.key].safePrice>0?'#32CD32':'#FF0000'}"><fmt:formatNumber value="${(map[pname][dateMap.key].dealPrice*rate-map[pname][dateMap.key].safePrice)}" maxFractionDigits="2" minFractionDigits="2" /></span> </td>
						</c:forEach>
					</tr>
					<tr>
					    <td>总产品盈亏(€)</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" ><span style="color:${map[pname][dateMap.key].dealPrice*rate-map[pname][dateMap.key].safePrice>0?'#32CD32':'#FF0000'}"><fmt:formatNumber value="${(map[pname][dateMap.key].dealPrice*rate-map[pname][dateMap.key].safePrice)*map[pname][dateMap.key].actualQuantity}" maxFractionDigits="2" minFractionDigits="2" /></span>  </td>
						</c:forEach>
					</tr>
					<tr>
					    <td>闪促费(€)</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" ><c:if test="${not empty map[pname][dateMap.key].dealFee }">-<fmt:formatNumber value="${map[pname][dateMap.key].dealFee*rate}" maxFractionDigits="2" minFractionDigits="2" /></c:if></td>
						</c:forEach>
					</tr>
					<tr>
					    <td>总盈亏(€)</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" ><span style="color:${(map[pname][dateMap.key].dealPrice*rate-map[pname][dateMap.key].safePrice)*map[pname][dateMap.key].actualQuantity-map[pname][dateMap.key].dealFee*rate>0?'#32CD32':'#FF0000'}"><fmt:formatNumber value="${(map[pname][dateMap.key].dealPrice*rate-map[pname][dateMap.key].safePrice)*map[pname][dateMap.key].actualQuantity-map[pname][dateMap.key].dealFee*rate}" maxFractionDigits="2" minFractionDigits="2" /></span>  </td>
						</c:forEach>
					</tr>
					<tr>
					    <td>前后一周排名</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" >
							     <a class="report" href="#"  nameKey="${pname}" countryKey="${amazonLightningDeals.country}"  startKey="${fns:getDateByPattern(map[pname][dateMap.key].start,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(map[pname][dateMap.key].end,'yyyy-MM-dd')}"><span class="icon-signal"></span></a>
					             <div class="rank" style="display: none"></div>
							</td>
						</c:forEach>
					</tr>
					<tr>
					    <td>前后一周销量</td>
					    <c:forEach items="${map[pname]}" var="dateMap">
							<td style="text-align: center;vertical-align: middle" class="title" >
							     <a class="report1" href="#"  nameKey="${pname}" countryKey="${amazonLightningDeals.country}"  startKey="${fns:getDateByPattern(map[pname][dateMap.key].start,'yyyy-MM-dd')}" endKey="${fns:getDateByPattern(map[pname][dateMap.key].end,'yyyy-MM-dd')}"><span class="icon-signal"></span></a>
					             <div class="sales" style="display: none"></div>
							</td>
						</c:forEach>
					</tr>
				</tbody>
			</table>
			 <br/><br/>
		  </c:forEach>
		 
		</c:if>
	
</body>
</html>
