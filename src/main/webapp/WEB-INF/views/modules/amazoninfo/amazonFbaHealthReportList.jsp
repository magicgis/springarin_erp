<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="amazon_order_tab1"/></title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<meta name="decorator" content="default"/>
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
		$(document).ready(function() {
			
			
			
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			 $("a[rel='popover']").popover({trigger:'hover',container:"form"});
			 
			 $("a[rel='popover']").live("mouseover",function(){
				var p = $(this).position();
				$('.fade').css("max-width","700px").css("top",p.top+150).css("left",p.left+88);
			 });
			
			 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			 	"aaSorting": [[11, "desc" ]]
			});
			 new FixedColumns( oTable,{
			 		"iLeftColumns":4,
					"iLeftWidth": 490
			 	} );
			 $(".row:first").append($("#searchContent").html());
			 
			

		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		function btnExport(){
			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/export");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
		function btnExport2(){
			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/export2");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		
      function btnExportAllStorage(){
            top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
                if(v=="ok"){
                    $("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/exportAllStorage");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport");
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }
      
      function btnExportBySome(){
            top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
                if(v=="ok"){
                    $("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport/exportBySome");
                    $("#searchForm").submit();
                    $("#searchForm").attr("action","${ctx}/amazoninfo/amazonFbaHealthReport");
                }
            },{buttonsFocus:1});
            top.$('.jbox-body .jbox-icon').css('top','55px');
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty amazonFbaHealthReport.country?'active':''}"><a class="countryHref" href="#total" >总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonFbaHealthReport.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<div style="display: none" id="searchContent">
		<form:form id="searchForm" modelAttribute="amazonFbaHealthReport" action="${ctx}/amazoninfo/amazonFbaHealthReport" method="post" >
			&nbsp;&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="createTime" value="<fmt:formatDate value="${amazonFbaHealthReport.createTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			<input  name="country" type="hidden" value="${amazonFbaHealthReport.country}" />
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<input  onclick="btnExport()" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
			<c:if test="${not empty amazonFbaHealthReport.country &&'ca' ne amazonFbaHealthReport.country}">
			   <input  onclick="btnExport2()" class="btn btn-primary" type="button" value="仓储费导出"/>
			</c:if>
			<input  onclick="btnExportAllStorage()" class="btn btn-primary" type="button" value="库存导出"/>
			<input  onclick="btnExportBySome()" class="btn btn-primary" type="button" value="库存预警导出"/>
		</form:form>
	</div>
	<table id="contentTable"  class="table table-bordered table-condensed">
		<thead>
			<tr>
				<c:if test="${empty amazonFbaHealthReport.country}"><th>platform</th></c:if>
				   <th>Sku</th>	
				   <th>Fnsku</th>
				   <th>Asin</th>	
				   <th>product_name</th>
				   <th>sales<br/>_rank</th>
				   <th>total<br/>_quantity</th>
				   <th>sellable<br/>_quantity</th>
				   <th>unsellable<br/>_quantity</th>
				   <th>age_days<br/>(0-90)</th>	
				   <th>age_days<br/>(91-180)</th>	
				   <th>age_days<br/>(181-270)</th>	
				   <th>age_days<br/>(271-365)</th>	
				   <th>age_days<br/>(365+)</th>	
				   <th>Shipped<br/>(last 24hrs)</th>	
				   <th>Shipped<br/>(last 7days)</th>	
				   <th>Shipped<br/>(last 30days)</th>	
				   <th>Shipped<br/>(last 90days)</th>
				   <th>Shipped<br/>(last 180days)</th>	
				   <th>Shipped<br/>(last 365days)</th>	
				   <th>cover<br/>_weeks(t7)</th>	
				   <th>cover<br/>_weeks(t30)</th>	
				   <th>cover<br/>_weeks(t90)</th>	
				   <th>cover<br/>_weeks(t180)</th>	
				   <th>cover<br/>_weeks(t365)</th>	
				   <th>afn_new<br/>_sellers</th>	
				   <th>afn_used<br/>_sellers</th>	
				   <th>your_price</th>	
				   <th>sales_price</th>	
				   <th>afn_new<br/>_price</th>	
				   <th>afn_used<br/>_price</th>	
				   <th>mfn_new<br/>_price</th>	
				   <th>mfn_used<br/>_price</th>	
				   <th>per_unit<br/>_volume</th>	
				   <th>is_hazmat</th>	
				   <th>inbound<br/>_quantity</th>	
				   <th>asin_limit</th>	
				   <th>inbound_<br/>recommend<br/>_quantity</th>	
			 </tr>	   
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="fba" varStatus="i">
			  <tr>
			  	  <c:if test="${empty amazonFbaHealthReport.country}"><td>${fns:getDictLabel(fba.country,'platform','')}</td></c:if>
				  <td>${fba.sku }</td>
				  <td>${fba.fnsku }</td>
				  <td><a href="http://www.amazon.${fba.country eq 'jp' || fba.country eq 'uk'?'co.':''}${fba.country eq 'com.unitek'?'com':fba.country}/dp/${fba.asin}" target="_blank">${fba.asin }</a></td>
				  <td><a href="#" style="color: #08c;"  data-html="true" rel="popover" data-content="${fba.productName}">${fns:abbr(fba.productName,15)}</a></td>
				  <td>${fba.salesRank }</td>
				  <td>${fba.totalQuantity }</td>
				  <td>${fba.sellableQuantity }</td>
				  <td>${fba.unsellableQuantity }</td>
				  <td>${fba.ageDays90 }</td>
				  <td>${fba.ageDays180 }</td>
				  <td>${fba.ageDays270 }</td>
				  <td>${fba.ageDays365 }</td>
				  <td class="blue">${fba.agePlusDays365 }</td>
				  <td>${fba.shippedHrs24 }</td>
				  <td>${fba.shippedDays7 }</td>
				  <td>${fba.shippedDays30 }</td>
				  <td>${fba.shippedDays90 }</td>
				  <td>${fba.shippedDays180 }</td>
				  <td>${fba.shippedDays365 }</td>
				  <td>${fba.weeksCover7 }</td>
				  <td>${fba.weeksCover30 }</td>
				  <td>${fba.weeksCover90 }</td>
				  <td>${fba.weeksCover180 }</td>
				  <td>${fba.weeksCover365 }</td>
				  <td>${fba.afnNewSellers }</td>
				  <td>${fba.afnUsedSellers }</td>
				  <td>${fba.yourPrice }</td>
				  <td>${fba.salesPrice }</td>
				  <td>${fba.afnNewPrice }</td>
				  <td>${fba.afnUsedPrice }</td>
				  <td>${fba.mfnNewPrice }</td>
				  <td>${fba.mfnUsedPrice }</td>
				  <td>${fba.perUnitVolume }</td>
				  <td>${fba.isHazmat }</td>
				  <td>${fba.inBoundQuantity }</td>
				  <td>${fba.asinLimit }</td>
				  <td>${fba.inboundRecommendQuantity }</td>
			  </tr>
			</c:forEach>		
		</tbody>
	</table>
</body>
</html>
