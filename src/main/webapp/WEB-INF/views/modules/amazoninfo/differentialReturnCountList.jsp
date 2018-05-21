<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Differential Return</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
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
		
		$.fn.dataTableExt.afnSortData['pecent'] = function  ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().replace('%','');
			} );
		}
		
		$(document).ready(function() {
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#contentTable tbody tr").each(function(){
				var tr = $(this);
				var saleAmount = tr.find(".saleAmount").text();
				var returnAmount = tr.find(".returnAmount").text();
				var feedbackComment=tr.find(".feedbackComment").text();
				var totalComment=tr.find(".totalComment").text();
				if(saleAmount!=0){
					tr.find(".saleRate").text((parseFloat(returnAmount)*100/saleAmount).toFixed(2)+"%");						
				}
				if(totalComment!=0){
					tr.find(".commentRate").text((parseFloat(feedbackComment)*100/totalComment).toFixed(2)+"%");						
				}
			});
			
			 $(".total").each(function(){
					var i = $(this).parent().find("td").index($(this));
					var num = 0;
					$("#"+$(this).attr("tid")+" tr").find("td:eq("+i+")").each(function(){
						if($.isNumeric($(this).text())){
							num += parseInt($(this).text());
						}
					});
					$(this).text(num);
				});
			    
			    
			    $(".totalf").each(function(){
					var i = $(this).parent().find("td").index($(this));
					var num = 0;
					$("#"+$(this).attr("tid")+" tr").find("td:eq("+i+")").each(function(){
						if($.isNumeric($(this).text())){
							num += parseFloat($(this).text());
						}
					});
					$(this).text(num.toFixed(2));
				});
			
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 10,
				"aLengthMenu":[[10, 20, 60,100,-1], [10, 20, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			 	"aoColumns": [null,{ "sSortDataType":"dom-html", "sType":"numeric" },
					          { "sSortDataType":"dom-html", "sType":"numeric" },
					          { "sSortDataType":"pecent", "sType":"numeric" },
					          <c:if test="${'1' eq flag}" >
					             null,null,
					          </c:if>
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"pecent", "sType":"numeric" },null,
						      ], "aaSorting": [[3, "desc" ]]
			});
			// $(".row:first").append($("#searchContent").html());
			  
			 $(".totalReturnRate").text((parseInt($(".totalReturnRate").prev().text())/parseInt($(".totalReturnRate").prev().prev().text())*100).toFixed(2)+"%");
			 $(".totalCommentRate").text((parseInt($(".totalCommentRate").prev().text())/parseInt($(".totalCommentRate").prev().prev().text())*100).toFixed(2)+"%");
		});
		
		
		function expCount(){
			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/differentialReturnCountListExport");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/differentialReturnCountList");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		function expByTime(flag){
			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/byTimeExport?type="+flag);
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/differentialReturnCountList");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
    <li class="${empty returnGoods.country?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${returnGoods.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>

	<form:form id="searchForm"  modelAttribute="returnGoods" action="${ctx}/amazoninfo/returnGoods/differentialReturnCountList" method="post"  class="breadcrumb form-search">
		&nbsp;&nbsp;&nbsp;&nbsp;订单时间:<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${returnGoods.startDate}" pattern="yyyy-MM-dd" />" id="satrt" class="input-small"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" name="returnDate" value="<fmt:formatDate value="${returnGoods.returnDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			<input  name="country" type="hidden" value="${returnGoods.country}" />
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<%-- <input  class="btn btn-primary" onclick="expCount()" type="button" value="<spring:message code="sys_but_export"/>"/> --%>
			<div class="btn-group">
						   <button type="button" class="btn btn-primary">导出</button>
						   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" id="allExport">
						      <li><a onclick="expCount()">original</a></li>
						      <li><a onclick="expByTime('0')">by week</a></li>
						      <li><a onclick="expByTime('1')">by month</a></li> 
						   </ul>
				 </div>
	</form:form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th rowspan="2" style="text-align: center;vertical-align: middle;width:20%;">产品</th>
				<shiro:hasPermission name="psi:inventory:stockPriceView"> 
				    <th colspan="5" style="text-align: center;vertical-align: middle;width:50%;">退货</th>
				</shiro:hasPermission>
				<shiro:lacksPermission name="psi:inventory:stockPriceView"> 
				    <th colspan="3" style="text-align: center;vertical-align: middle;width:50%;">退货</th>
				</shiro:lacksPermission>
				<th colspan="3" style="text-align: center;vertical-align: middle;width:30%;"> 差评</th>
				<th rowspan="2" style="text-align: center;vertical-align: middle;width:6%;">供应商</th>
			</tr>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:10%;">销售数量</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">退货数量</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">退货率(%)</th>
				<shiro:hasPermission name="psi:inventory:stockPriceView"> 
				<th style="text-align: center;vertical-align: middle;width:10%;">单价($)</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">退货金额($)</th>
				</shiro:hasPermission>
				<th style="text-align: center;vertical-align: middle;width:10%;">
				<a href="#" data-toggle="tooltip" title="每一订单子项算一订单数量" style="color: #08c;">订单数量</a></th>
				<th style="text-align: center;vertical-align: middle;width:10%;">差评数量</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">差评率(%)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page}" var="name">
			<tr>
				<td>
					<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target='_blank'>${name}</a>
					(${typeLineMap[fn:toLowerCase(nameTypeMap[name])] }线)
				</td>
				
				<td style="text-align: center;vertical-align: middle" class="saleAmount"><a href="${ctx}/amazoninfo/returnGoods/returnCountList?productName=${name}&startDate=${goods.startDate}&endDate=${goods.returnDate}&country=${goods.country}" target='_blank'>${orderMap[name][1] }</a></td>
				<td style="text-align: center;vertical-align: middle" class="returnAmount"><a href="${ctx}/amazoninfo/returnGoods/returnCountList?productName=${name}&startDate=${goods.startDate}&endDate=${goods.returnDate}&country=${goods.country}" target='_blank'>${returnMap[name][1] }</a></td>
				<c:if test="${!empty returnMap[name][1]}">
				   <c:if test="${!empty orderMap[name][1] }">
					    <c:if test="${orderMap[name][1]>0 }"> <td style="text-align: center;vertical-align: middle" class="saleRate"></td></c:if>
					    <c:if test="${orderMap[name][1]==0  }"> <td></td></c:if>
				   </c:if>
				    <c:if test="${empty orderMap[name][1] }">
					   <td></td>
				   </c:if>
				</c:if>
				<c:if test="${empty returnMap[name][1] }">
				  <td></td>
				</c:if>
				<shiro:hasPermission name="psi:inventory:stockPriceView"> 
				<td style="text-align: center;vertical-align: middle">${productsMoqAndPrice[name].price}</td>
				<td style="text-align: center;vertical-align: middle">${productsMoqAndPrice[name].price*returnMap[name][1]}</td>
				</shiro:hasPermission>
				
			    <td style="text-align: center;vertical-align: middle" class="totalComment"><a href="${ctx}/amazoninfo/returnGoods/commentCountList?productName=${name}&startDate=${goods.startDate}&endDate=${goods.returnDate}&country=${goods.country}" target='_blank'>${returnMap[name][3] }</a></td>
			    <td style="text-align: center;vertical-align: middle" class="feedbackComment"><a href="${ctx}/amazoninfo/returnGoods/commentCountList?productName=${name}&startDate=${goods.startDate}&endDate=${goods.returnDate}&country=${goods.country}" target='_blank'>${returnMap[name][2] }</a></td>
			    <c:if test="${!empty returnMap[name][2] }">
				  <c:if test="${returnMap[name][3]>0}"> <td class="commentRate" style="text-align: center;vertical-align: middle"></td></c:if>
				  <c:if test="${returnMap[name][3]==0}"><td></td></c:if>
				</c:if>
				<c:if test="${empty returnMap[name][2] }">
				  <td></td>
				</c:if> 
				<td>${productSupplier[name] }</td>
			</tr>
			</c:forEach>
			<tfoot>
			<tr>
			   <td style="text-align: center;vertical-align: middle">合计</td>
			   <td style="text-align: center;vertical-align: middle" tid="contentTable" class="total"></td>
			   <td style="text-align: center;vertical-align: middle" tid="contentTable" class="total"></td>
			   <td style="text-align: center;vertical-align: middle" tid="contentTable" class="totalReturnRate"></td>
			   <shiro:hasPermission name="psi:inventory:stockPriceView"> 
				   <td style="text-align: center;vertical-align: middle"></td>
				   <td style="text-align: center;vertical-align: middle" tid="contentTable" class="totalf"></td>
			   </shiro:hasPermission>
			   <td style="text-align: center;vertical-align: middle" tid="contentTable" class="total"></td>
			   <td style="text-align: center;vertical-align: middle" tid="contentTable" class="total"></td>
			   <td style="text-align: center;vertical-align: middle" class="totalCommentRate"></td>
			   <td></td>
			</tr>   
		   </tfoot>	
		</tbody>
		
	</table>
</body>
</html>
