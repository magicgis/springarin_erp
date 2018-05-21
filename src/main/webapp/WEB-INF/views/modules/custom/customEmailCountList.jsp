<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Custom Email Count</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
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
			$("#btnExport").click(function(){
				top.$.jBox.confirm("Confirm that you want to export statistical data?","Prompted",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/custom/emailManager/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			var total1=0,total2=0,total4=0,total5=0,total6=0,total8=0,total9=0;
			$("tbody tr").each(function(){
				total1 = total1+parseInt($(this).find("td:eq(1)").text());
				total2 = total2+parseInt($(this).find("td:eq(2)").text());
				total4 = total4+parseInt($(this).find("td:eq(4)").text());
				total5 = total5+parseInt($(this).find("td:eq(5)").text());
				total6 = total6+parseInt($(this).find("td:eq(6)").text());
				total8 = total8+parseFloat($(this).find("td:eq(9)").text());
				total9 = total9+parseFloat($(this).find("td:eq(10)").text());
			});
			
			$(".total td:eq(1)").text(total1);
			$(".total td:eq(2)").text(total2);
			$(".total td:eq(4)").text(total4);
			$(".total td:eq(5)").text(total5);
			$(".total td:eq(6)").text(total6);
			$(".total td:eq(9)").text(total8.toFixed(2));
			$(".total td:eq(10)").text(total9.toFixed(2));
		});
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="customEmail" action="${ctx}/custom/emailManager/count" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
		<span style="float: right;">
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/emailManager/count');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${customEmail.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/emailManager/count');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${customEmail.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value='<spring:message code="sys_but_export"/>'/>
		</span>
		</div>
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><spring:message code="custom_email_form4" /></th>
				<th><spring:message code="custom_email_count1" /></th>
				<th><spring:message code="custom_email_count2" /></th>
				<th><spring:message code="custom_email_count3" />(%)</th>
				<th><spring:message code="custom_email_count4" /></th>
				<th><spring:message code="custom_email_count5" /></th>
				<th><spring:message code="custom_email_count6" /></th>
				<th><spring:message code="custom_email_count7" /></th>
				<th><spring:message code="custom_email_editTime" /></th>
				<th><spring:message code="custom_email_count8" /></th>
				<th><spring:message code="custom_email_count9" /></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="num" value="${fns:pastDays(customEmail.createDate) -fns:pastDays(customEmail.endDate) +1}"/>
			<c:forEach items="${date}" var="count">
				<tr>
					<td>${count.value.user}</td>
					<td>${count.value['noFor']==null?'0':count.value['noFor']}</td>
					<c:set var="two" value="${count.value['two']==null?'0':count.value['two']}" />
					<td>${two}</td>
					<td><fmt:formatNumber value="${count.value['noFor']==null|| count.value['noFor']=='0'?'0':count.value['two']*100/count.value['noFor']}" minFractionDigits="2"/>%</td>
					<td>${count.value['sendEmail']==null?'0':((count.value['sendEmail']-two)>0?(count.value['sendEmail']-two):'0')}</td>
					<td>${count.value['noFor']-count.value['two']}</td>
					<td>${count.value['for']==null?'0':count.value['for']}</td>
					<td><fmt:formatNumber value="${count.value['resp']/3600}" maxFractionDigits="2"/>h</td>
					<td><fmt:formatNumber value="${count.value['avg']/3600}" maxFractionDigits="2"/>h</td>
					<td><fmt:formatNumber value="${(count.value['two']==null?'0':count.value['two'])/num}" maxFractionDigits="2"/></td>
					<td><fmt:formatNumber value="${(count.value['sendEmail']==null?'0':count.value['sendEmail'])/num}" maxFractionDigits="2"/></td>
				</tr>
			</c:forEach>	
			<tr class="total">
				<td><b style="font-size: 18px">Total</b></td>
				<td>0</td>
				<td>0</td>
				<td></td>
				<td>0</td>
				<td>0</td>
				<td>0</td>
				<td></td>
				<td></td>
				<td>0.0</td>
				<td>0.0</td>
			</tr>	
		</tbody>
	</table>
</body>
</html>
