<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Event Count</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			if(!(top)){
				top = self;
			}
			
			$("#selectT,#country").change(function(){
				$("#searchForm").attr("action","${ctx}/custom/event/count");
				$("#searchForm").submit();
			});
			var countEvent = 0;
			$("tbody tr").each(function(){
				var temp = parseInt($(this).find("td:eq(1)").text());
				if(!isNaN(temp)){
					countEvent = countEvent + parseInt($(this).find("td:eq(1)").text());
				}
			});
			$("#countEvent").text(countEvent);
			
			var countEvent1 = 0;
			$("tbody tr").each(function(){
				var temp = parseInt($(this).find("td:eq(2)").text());
				if(!isNaN(temp)){
					countEvent1 = countEvent1 + parseInt($(this).find("td:eq(2)").text());
				}
			});
			$("#countTwo").text(countEvent1);
			
			$("#count1").text((countEvent1/countEvent*100).toFixed(2)+'%');
			
			countEvent = 0;
			$("tbody tr").each(function(){
				var temp = parseInt($(this).find("td:eq(3)").text());
				if(!isNaN(temp)){
					countEvent = countEvent + parseInt($(this).find("td:eq(3)").text());
				}
			});
			$("#countFor").text(countEvent);
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code='custom_event_note3'/>","<spring:message code='sys_label_tips_msg'/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/custom/event/countExport");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnExport1").click(function(){
				top.$.jBox.confirm("<spring:message code='custom_event_note4'/>","<spring:message code='sys_label_tips_msg'/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/custom/event/ratingExport");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/custom/event/count");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="event" action="${ctx}/custom/event/count" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
		<%-- <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/> --%>
		<%-- <input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/> --%>
		<span style="float: right;">
		事件区间:<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/event/count');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${event.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/event/count');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${event.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<spring:message code='sys_label_country'/>：<select name="country" id="country" style="width: 120px">
					<option value="" ${event.country eq ''?'selected':''}><spring:message code='custom_event_all'/></option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<option value="${dic.value}" ${event.country eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:forEach>
					<option value="other" ${event.country eq 'other'?'selected':''}><spring:message code='custom_event_other'/></option>	
			</select>&nbsp;&nbsp;
		<spring:message code='custom_event_type'/>：<select name="type" style="width: 160px" id="selectT">
					<option value=""><spring:message code='custom_event_all'/></option>
					<option value="1" ${event.type eq '1'?'selected':''}>Rating</option>
					<option value="2" ${event.type eq '2'?'selected':''}>Account Rating</option>
					<option value="4" ${event.type eq '4'?'selected':''}>Tax_Refund</option> 
					<option value="5" ${event.type eq '5'?'selected':''}>Support</option>
					<option value="7" ${event.type eq '7'?'selected':''}>Support_Voucher</option>
					<option value="8" ${event.type eq '8'?'selected':''}>Marketing Order</option>
					<option value="6" ${event.type eq '6'?'selected':''}>FAQ</option>
					<option value="3" ${event.type eq '3'?'selected':''}>FAQ_Email</option>
					<option value="9" ${event.type eq '9'?'selected':''}>Product Improvement</option>
					<option value="10" ${event.type eq '10'?'selected':''}>Product Recall</option>
					<option value="11" ${event.type eq '11'?'selected':''}>Review Refund</option>
				</select>&nbsp;&nbsp;	
				<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code='custom_event_export'/>"/>
				<input id="btnExport1" class="btn btn-primary" type="button" value="<spring:message code='custom_event_exportByReview'/>"/>
		</span>
		</div>
	</form:form>
	<div class="alert alert-info"><strong >解决率=时间区间内完成事件数  /(时间区间内收到事件数+非时间区间收到但在时间区间完成的事件数)</strong></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th><spring:message code='custom_event_colName'/></th>
				<th><spring:message code='custom_event_colName1'/></th>
				<th><spring:message code='custom_event_colName2'/></th>
				<th><spring:message code='custom_event_colName3'/></th>
				<th><spring:message code='custom_event_colName4'/>(%)</th>
				<th><spring:message code='custom_event_colName5'/></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="minutes" value="0" />
			<c:set var="length" value="${fn:length(date)}" />
			
			<c:forEach items="${date}" var="count">
			
			<tr>
				<td>${count.value.user}</td>
				<td>${count.value['noFor']==null?'0':count.value['noFor']}</td>
				<td>${count.value['two']==null?'0':count.value['two']}</td>
				<td>${count.value['for']==null?'0':count.value['for']}</td>
				<td><fmt:formatNumber value="${count.value['noFor']==null|| count.value['noFor']=='0'?'0':count.value['two']*100/count.value['noFor']}" minFractionDigits="2"/>%</td>
				<td><c:if test="${not empty count.value['avg']}">${(count.value['avg']/86400)>1?'':'小于'}<fmt:formatNumber value="${(count.value['avg']/86400)>1?(count.value['avg']/86400):'1'}" maxFractionDigits="2"/>days</c:if></td>
				<c:set var="minutes" value="${count.value['avg']+minutes}" />
			</tr>
			<c:if test="${count.value['two']>0}">
				<tr>
					<td colspan="7">
						<h5>Details：</h5>
						${count.value['result']}
					</td>
				</tr>
			</c:if>
			</c:forEach>	
			<tr>
				<td><h4>Total</h4></td>
				<td id="countEvent"></td>
				<td id="countTwo"></td>
				<td id="countFor"></td>
				<td id="count1"></td>
				<td ><c:if test="${length>0}">
					${(minutes/length/86400)>1?'':'小于'}<fmt:formatNumber value="${(minutes/length/86400)>1?(minutes/length/86400):'1'}" maxFractionDigits="2"/>days
				</c:if></td>
			</tr>
		</tbody>
	</table>
</body>
</html>
