<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运营业绩报告</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style>
		.imp{
			color:red
		}
		.hasColor{
			color:#08c
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
		
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().replace("%","");
			} );
		};
		
		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().split(',').join('');
			} );
		};
		
		<%--固定列排序--%>
		$.fn.dataTableExt.afnSortData['fix-html'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var newColumn = iColumn -3;
				return $('td:eq('+newColumn+')', tr).text().replace("%","");
			} );
		};
		
		$.fn.dataTableExt.afnSortData['fix-html1'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var newColumn = iColumn -3;
				return $('td:eq('+newColumn+')', tr).text().split(',').join('');
			} );
		};
		
		$(document).ready(function() {
			
			if(!(top)){
				top = self;
			}
			var dataType = $("#dataType").val();
			$(".countryHref").click(function(e){
				$("input[name='divFlag']").val($(this).attr("id"));
				var f = $(this).attr("id");
				if(dataType == '2' && f == 'seasonA'){
					$("#season").css('display','block');
				} else if(dataType == '2'){
					$("#season").css('display','none');
				}
				$(this).tab('show');
				
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#btnSubmit").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/salesReprots/results');
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportResults");
						$("#searchForm").submit();
						$('#searchForm').attr('action','${ctx}/amazoninfo/salesReprots/results');
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnExpTarget").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/enterpriseGoal/exportTargetCompleteData");
						$("#searchForm").submit();
						$('#searchForm').attr('action','${ctx}/amazoninfo/salesReprots/results');
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnExpByMonth").click(function(){
				top.$.jBox.confirm("<input style='width: 160px' readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyyMM',maxDate:'%y-{%M-1}-%d'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var month = h.find("input").val();
						  	if(month){
						  		$("#month").val(month);
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/expResultsByMonth");
								$("#searchForm").submit();
								$('#searchForm').attr('action','${ctx}/amazoninfo/salesReprots/results');
						  	}else{
						  		top.$.jBox.tip("请选择导出月份", 'info',{timeout:1500});
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			<c:if test="${'1' eq dataType}">
			$("#monthTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
						         null
								 <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
								 	,
								 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
								 	,
								 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
								 	,
								 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
								 	,
								 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
								 	,
								 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
								 	,
								 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll}">
									 ,
									 { "sSortDataType":"dom-html1", "sType":"numeric" },
								     { "sSortDataType":"dom-html", "sType":"numeric" }
								 </c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
			      
			$("#seasonTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
									null,
									null,	
									null
									<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
										,
									 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
										{ "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
										,
									 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
										{ "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
										,
									 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
										{ "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
										,
									 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
										{ "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
										,
									 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
										{ "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
										,
									 	{ "sSortDataType":"dom-html1", "sType":"numeric" },
										{ "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll}">
										 ,
										 { "sSortDataType":"dom-html1", "sType":"numeric" },
									    { "sSortDataType":"dom-html", "sType":"numeric" }
									</c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
			</c:if>
			
			<%--利润table增加广告费--%>
			<c:if test="${'2' eq dataType}">
			var monthTable = $("#monthTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"sScrollX": "100%",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
						         null
								 <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
								 	,
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
							     	{ "sSortDataType":"fix-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
								 	,
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
							     	{ "sSortDataType":"fix-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
								 	,
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
							     	{ "sSortDataType":"fix-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
								 	,
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
							     	{ "sSortDataType":"fix-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
								 	,
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
							     	{ "sSortDataType":"fix-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
								 	,
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
							     	{ "sSortDataType":"fix-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll}">
									 ,
									 { "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
								     { "sSortDataType":"fix-html", "sType":"numeric" }
								 </c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});

			 new FixedColumns( monthTable,{
				 "iLeftColumns": 3,
		 		"iLeftWidth": 290
			 });
			      
			 var seasonTable = $("#seasonTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"sScrollX": "100%",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
									null,
									null,	
									null
									<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
										,
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
										{ "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
										,
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
										{ "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
										,
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
										{ "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
										,
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
										{ "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
										,
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
										{ "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
										,
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
									 	{ "sSortDataType":"fix-html1", "sType":"numeric" },
										{ "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
									<c:if test="${viewAll}">
										 ,
										 { "sSortDataType":"fix-html1", "sType":"numeric" },
										 { "sSortDataType":"fix-html1", "sType":"numeric" },
									     { "sSortDataType":"fix-html", "sType":"numeric" }
									</c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});

			 new FixedColumns( seasonTable,{
				"iLeftColumns": 3,
		 		"iLeftWidth": 290
			 });
			</c:if>
			
			<c:if test="${'3' eq dataType}">
			$("#monthTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
						         null
								 <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
			      
			$("#seasonTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
						         null
								 <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll}">
								 	,
							     	{ "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
			</c:if>
			      
			$("#lineTargetTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null
								 <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
								 <c:if test="${viewAll}">
								 ,	
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     </c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});

			var divFlag = $("#divFlag").val();
			if(divFlag != null && divFlag.length > 0){
				$("#"+divFlag+" i").click();
			} else if (dataType == '2') {
				$("#season").css('display','none');
			}
		});
		
		function timeOnChange(){
			$('#searchForm').attr('action','${ctx}/amazoninfo/salesReprots/results');
			$("#searchForm").submit();
		}
		
		function changeType(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/salesReprots/results" method="post" class="breadcrumb form-search">
	<input type="hidden" id="month" name="month"/>
	<input type="hidden" id="divFlag" name="divFlag" value="${divFlag}"></input>
	<div style="height: 30px;margin-top:10px">
		<label>时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="year" value="${year}" class="input-small" id="year"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<%--<c:if test="${'1' eq flag }"></c:if>有查看利润权限可切换查看利润 --%>
		统计类型：<select name="dataType" id="dataType" style="width: 100px" onchange="changeType()">
				<option value="1" ${'1' eq dataType?'selected':''}>销售额</option>
				<option value="2" ${'2' eq dataType?'selected':''}>利润</option>
				<option value="3" ${'3' eq dataType?'selected':''}>利润率</option>
		</select>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		<shiro:hasPermission name="amazoninfo:results:export">
			&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
			&nbsp;&nbsp;<input id="btnExpByMonth" class="btn btn-primary" type="button" value="导出月度报表"/>
			<%--&nbsp;&nbsp;<input id="btnExpTarget" class="btn btn-primary" type="button" value="导出目标完成情况"/> --%>
		</shiro:hasPermission>
		<c:if test="${'2016' eq year }"><%--<span style="color:red">(汇总数据均不含E线)</span> --%></c:if>
	</div>
	</form:form>
	<ul class="nav nav-tabs">
		<li class="active"><a id="monthA" class="countryHref" href="#monthDiv">月度明细<i></i></a></li>
		<li><a id="seasonA" class="countryHref" href="#season">季度明细<i></i></a></li>
		<c:if test="${'1' eq dataType }">
			<li><a id="targetA" class="countryHref" href="#target">销售额完成情况<i></i></a></li>
		</c:if>
		<c:if test="${'2' eq dataType }">
			<li><a id="targetA" class="countryHref" href="#target">利润完成情况<i></i></a></li>
		</c:if>
	</ul>
	<div class="tab-content">
	<c:if test="${'1' eq dataType }">
	<!-- 销售额-->
	<div id="monthDiv" class="tab-pane active">
		<table id="monthTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: left;">日期</th>
					<th style="text-align: left;">产品类型</th>
				    <th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;" class="hasColor">EN(€)</th>
						<th style="text-align: left;" class="hasColor">EN%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE(€)</th>
						<th style="text-align: left;">DE%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR(€)</th>
						<th style="text-align: left;" class="hasColor">FR%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT(€)</th>
						<th style="text-align: left;">IT%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES(€)</th>
						<th style="text-align: left;" class="hasColor">ES%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP(€)</th>
						<th style="text-align: left;">JP%</th>
					</c:if>
				    <c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total(€)</th>
						<th style="text-align: left;" class="hasColor">TT%</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="monthMap">
					<c:set var="month" value="${monthMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString ne month}">
					<c:set var="enTotal" value="${data[month]['total']['en'].sales}"/>
					<c:set var="deTotal" value="${data[month]['total']['de'].sales}"/>
					<c:set var="frTotal" value="${data[month]['total']['fr'].sales}"/>
					<c:set var="itTotal" value="${data[month]['total']['it'].sales}"/>
					<c:set var="esTotal" value="${data[month]['total']['es'].sales}"/>
					<c:set var="jpTotal" value="${data[month]['total']['jp'].sales}"/>
					<c:set var="allTotal" value="${enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal}"/>
					<c:forEach items="${data[month]}" var="typeMap">
					<c:set var="type" value="${typeMap.key}"/>
					<c:if test="${type ne 'total'}">
					<tr>
						<td style="text-align: left;">${month}</td>
						<td style="text-align: left;">
							${type}
						</td>
						<td style="text-align: left;">
							<%--<c:set var="line" value="UnGrouped"/>
							<c:if test="${not empty typeLine[fn:toLowerCase(type)] }">
								<c:set var="line" value="${typeLine[fn:toLowerCase(type)] }"/>
							</c:if>
							${line} --%>
							${data[month][type]['total'].line}线
						</td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="en" value="0"/>
								<c:if test="${not empty data[month][type]['en'].sales }">
									<c:set var="en" value="${data[month][type]['en'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${en}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:if test="${en > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${en/enTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<c:set var="de" value="0"/>
								<c:if test="${not empty data[month][type]['de'].sales }">
									<c:set var="de" value="${data[month][type]['de'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${de}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<c:if test="${de > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${de/deTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
					    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="fr" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].sales }">
									<c:set var="fr" value="${data[month][type]['fr'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${fr}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:if test="${fr > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${fr/frTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<c:set var="it" value="0"/>
								<c:if test="${not empty data[month][type]['it'].sales }">
									<c:set var="it" value="${data[month][type]['it'].sales }"/>
									<fmt:formatNumber  pattern="#,##0" value="${it}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<c:if test="${it > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${it/itTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="es" value="0"/>
								<c:if test="${not empty data[month][type]['es'].sales }">
									<c:set var="es" value="${data[month][type]['es'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${es}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:if test="${es > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${es/esTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<c:set var="jp" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].sales }">
									<c:set var="jp" value="${data[month][type]['jp'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${jp}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<c:if test="${jp > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${jp/jpTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="total" value="0"/>
								<c:if test="${not empty data[month][type]['total'].sales }"><c:set var="total" value="${data[month][type]['total'].sales }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${total}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${total/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
					</tr>
					</c:if>
					</c:forEach>
					<%--月度总计 --%>
					<tr style="background-color: yellow">
						<td style="text-align: left;">${month}</td>
						<td style="text-align: left;">total</td>
						<td style="text-align: left;"></td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">100%</td>
						</c:if>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="season" class="hideCls tab-pane">
		<table id="seasonTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: left;">日期</th>
					<th style="text-align: left;">产品类型</th>
				    <th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;" class="hasColor">EN(€)</th>
						<th style="text-align: left;" class="hasColor">EN%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE(€)</th>
						<th style="text-align: left;">DE%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR(€)</th>
						<th style="text-align: left;" class="hasColor">FR%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT(€)</th>
						<th style="text-align: left;">IT%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES(€)</th>
						<th style="text-align: left;" class="hasColor">ES%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP(€)</th>
						<th style="text-align: left;">JP%</th>
					</c:if>
				    <c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total(€)</th>
						<th style="text-align: left;" class="hasColor">TT%</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="monthMap">
					<c:set var="month" value="${monthMap.key}"/>
					<c:set var="season" value="${year}${month}"/>
					<c:if test="${fn:contains(month, 'q') && currSeason ne season}">
					<c:set var="enTotal" value="${data[month]['total']['en'].sales}"/>
					<c:set var="deTotal" value="${data[month]['total']['de'].sales}"/>
					<c:set var="frTotal" value="${data[month]['total']['fr'].sales}"/>
					<c:set var="itTotal" value="${data[month]['total']['it'].sales}"/>
					<c:set var="esTotal" value="${data[month]['total']['es'].sales}"/>
					<c:set var="jpTotal" value="${data[month]['total']['jp'].sales}"/>
					<c:set var="allTotal" value="${enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal}"/>
					<c:forEach items="${data[month]}" var="typeMap">
					<c:set var="type" value="${typeMap.key}"/>
					<c:if test="${type ne 'total'}">
					<tr>
						<td style="text-align: left;">${fn:toUpperCase(month)}</td>
						<td style="text-align: left;">
							${type}
						</td>
						<td style="text-align: left;">
							<%--<c:set var="line" value="UnGrouped"/>
							<c:if test="${not empty typeLine[fn:toLowerCase(type)] }">
								<c:set var="line" value="${typeLine[fn:toLowerCase(type)] }"/>
							</c:if>
							${line} --%>
							${data[month][type]['total'].line}线
						</td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<td style="text-align: left;" class="hasColor">
							<c:set var="en" value="0"/>
							<c:if test="${not empty data[month][type]['en'].sales }">
								<c:set var="en" value="${data[month][type]['en'].sales }"/>
								<fmt:formatNumber pattern="#,##0" value="${en}"  maxFractionDigits="0" />
							</c:if>
						</td>
						<td style="text-align: left;" class="hasColor">
							<c:if test="${en > 0 }">
								<fmt:formatNumber pattern="#,##0" value="${en/enTotal*100}"  maxFractionDigits="0" />%
							</c:if>
						</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<c:set var="de" value="0"/>
								<c:if test="${not empty data[month][type]['de'].sales }">
									<c:set var="de" value="${data[month][type]['de'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${de}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<c:if test="${de > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${de/deTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="fr" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].sales }">
									<c:set var="fr" value="${data[month][type]['fr'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${fr}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:if test="${fr > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${fr/frTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<c:set var="it" value="0"/>
								<c:if test="${not empty data[month][type]['it'].sales }">
									<c:set var="it" value="${data[month][type]['it'].sales }"/>
									<fmt:formatNumber  pattern="#,##0" value="${it}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<c:if test="${it > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${it/itTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="es" value="0"/>
								<c:if test="${not empty data[month][type]['es'].sales }">
									<c:set var="es" value="${data[month][type]['es'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${es}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:if test="${es > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${es/esTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<c:set var="jp" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].sales }">
									<c:set var="jp" value="${data[month][type]['jp'].sales }"/>
									<fmt:formatNumber pattern="#,##0" value="${jp}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<c:if test="${jp > 0 }">
									<fmt:formatNumber pattern="#,##0" value="${jp/jpTotal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="total" value="0"/>
								<c:if test="${not empty data[month][type]['total'].sales }"><c:set var="total" value="${data[month][type]['total'].sales }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${total}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${total/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
					</tr>
					</c:if>
					</c:forEach>
					<%--季度度总计 --%>
					<tr style="background-color: yellow">
						<td style="text-align: left;">${fn:toUpperCase(month)}</td>
						<td style="text-align: left;">total</td>
						<td style="text-align: left;"></td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">100%</td>
						</c:if>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
	
	<c:if test="${'2' eq dataType }">
	<!-- 利润 -->
	<div id="monthDiv" class="tab-pane active">
		<table id="monthTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: left;">日期</th>
					<th style="text-align: left;">产品类型</th>
				    <th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;" class="hasColor">EN(€)</th>
						<th style="text-align: left;" class="hasColor">AdFee(€)</th>
						<th style="text-align: left;" class="hasColor">EN%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE(€)</th>
						<th style="text-align: left;">AdFee(€)</th>
						<th style="text-align: left;">DE%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR(€)</th>
						<th style="text-align: left;" class="hasColor">AdFee(€)</th>
						<th style="text-align: left;" class="hasColor">FR%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT(€)</th>
						<th style="text-align: left;">AdFee(€)</th>
						<th style="text-align: left;">IT%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES(€)</th>
						<th style="text-align: left;" class="hasColor">AdFee(€)</th>
						<th style="text-align: left;" class="hasColor">ES%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP(€)</th>
						<th style="text-align: left;">AdFee(€)</th>
						<th style="text-align: left;">JP%</th>
					</c:if>
				    <c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total(€)</th>
						<th style="text-align: left;" class="hasColor">AdFee(€)</th>
						<th style="text-align: left;" class="hasColor">TT%</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="monthMap">
					<c:set var="month" value="${monthMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString ne month}">
					<c:set var="enTotal" value="${data[month]['total']['en'].profits}"/>
					<c:set var="deTotal" value="${data[month]['total']['de'].profits}"/>
					<c:set var="frTotal" value="${data[month]['total']['fr'].profits}"/>
					<c:set var="itTotal" value="${data[month]['total']['it'].profits}"/>
					<c:set var="esTotal" value="${data[month]['total']['es'].profits}"/>
					<c:set var="jpTotal" value="${data[month]['total']['jp'].profits}"/>
					<c:set var="allTotal" value="${enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal}"/>
					<c:set var="enAdTotal" value="${data[month]['total']['en'].adInEventFee}"/>
					<c:set var="deAdTotal" value="${data[month]['total']['de'].adInEventFee}"/>
					<c:set var="frAdTotal" value="${data[month]['total']['fr'].adInEventFee}"/>
					<c:set var="itAdTotal" value="${data[month]['total']['it'].adInEventFee}"/>
					<c:set var="esAdTotal" value="${data[month]['total']['es'].adInEventFee}"/>
					<c:set var="jpAdTotal" value="${data[month]['total']['jp'].adInEventFee}"/>
					<c:set var="allAdTotal" value="${enAdTotal + deAdTotal + frAdTotal + itAdTotal + esAdTotal + jpAdTotal}"/>
					<c:forEach items="${data[month]}" var="typeMap">
					<c:set var="type" value="${typeMap.key}"/>
					<c:if test="${type ne 'total'}">
					<tr>
						<td style="text-align: left;">${month}</td>
						<td style="text-align: left;">
							${type}
						</td>
						<td style="text-align: left;">
							<%--<c:set var="line" value="UnGrouped"/>
							<c:if test="${not empty typeLine[fn:toLowerCase(type)] }">
								<c:set var="line" value="${typeLine[fn:toLowerCase(type)] }"/>
							</c:if>
							${line} --%>
							${data[month][type]['total'].line}线
						</td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="en" value="0"/>
								<c:if test="${not empty data[month][type]['en'].profits }"><c:set var="en" value="${data[month][type]['en'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${en}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['en'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['en'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${en/(enTotal<0?-enTotal:enTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<c:set var="de" value="0"/>
								<c:if test="${not empty data[month][type]['de'].profits }"><c:set var="de" value="${data[month][type]['de'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${de}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['de'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['de'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${de/(deTotal<0?-deTotal:deTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="fr" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].profits }"><c:set var="fr" value="${data[month][type]['fr'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${fr}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['fr'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${fr/(frTotal<0?-frTotal:frTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<c:set var="it" value="0"/>
								<c:if test="${not empty data[month][type]['it'].profits }"><c:set var="it" value="${data[month][type]['it'].profits }"/></c:if>
								<fmt:formatNumber  pattern="#,##0" value="${it}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['it'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['it'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${it/(itTotal<0?-itTotal:itTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="es" value="0"/>
								<c:if test="${not empty data[month][type]['es'].profits }"><c:set var="es" value="${data[month][type]['es'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${es}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['es'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['es'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${es/(esTotal<0?-esTotal:esTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<c:set var="jp" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].profits }"><c:set var="jp" value="${data[month][type]['jp'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${jp}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['jp'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jp/(jpTotal<0?-jpTotal:jpTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="total" value="0"/>
								<c:if test="${not empty data[month][type]['total'].profits }"><c:set var="total" value="${data[month][type]['total'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${total}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['total'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['total'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${total/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
					</tr>
					</c:if>
					</c:forEach>
					<%--月度总计 --%>
					<tr style="background-color: yellow">
						<td style="text-align: left;">${month}</td>
						<td style="text-align: left;">total</td>
						<td style="text-align: left;"></td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${enTotal}"  maxFractionDigits="0" />
						</td>
						<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${enAdTotal}"  maxFractionDigits="0" />
						</td>
						<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${enTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
						</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${deTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${frTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${itTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${esTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
							<fmt:formatNumber pattern="#,##0" value="${jpTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">100%</td>
						</c:if>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="season" class="active tab-pane">
		<table id="seasonTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: left;">日期</th>
					<th style="text-align: left;">产品类型</th>
				    <th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;" class="hasColor">EN(€)</th>
						<th style="text-align: left;" class="hasColor">AdFee(€)</th>
						<th style="text-align: left;" class="hasColor">EN%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
					<th style="text-align: left;">DE(€)</th>
					<th style="text-align: left;">AdFee(€)</th>
					<th style="text-align: left;">DE%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
					<th style="text-align: left;" class="hasColor">FR(€)</th>
					<th style="text-align: left;" class="hasColor">AdFee(€)</th>
					<th style="text-align: left;" class="hasColor">FR%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
					<th style="text-align: left;">IT(€)</th>
					<th style="text-align: left;">AdFee(€)</th>
					<th style="text-align: left;">IT%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
					<th style="text-align: left;" class="hasColor">ES(€)</th>
					<th style="text-align: left;" class="hasColor">AdFee(€)</th>
					<th style="text-align: left;" class="hasColor">ES%</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
					<th style="text-align: left;">JP(€)</th>
					<th style="text-align: left;">AdFee(€)</th>
					<th style="text-align: left;">JP%</th>
					</c:if>
				    <c:if test="${viewAll}">
					<th style="text-align: left;" class="hasColor">Total(€)</th>
					<th style="text-align: left;" class="hasColor">AdFee(€)</th>
					<th style="text-align: left;" class="hasColor">TT%</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="monthMap">
					<c:set var="month" value="${monthMap.key}"/>
					<c:set var="season" value="${year}${month}"/>
					<c:if test="${fn:contains(month, 'q') && currSeason ne season}">
					<c:set var="enTotal" value="${data[month]['total']['en'].profits}"/>
					<c:set var="deTotal" value="${data[month]['total']['de'].profits}"/>
					<c:set var="frTotal" value="${data[month]['total']['fr'].profits}"/>
					<c:set var="itTotal" value="${data[month]['total']['it'].profits}"/>
					<c:set var="esTotal" value="${data[month]['total']['es'].profits}"/>
					<c:set var="jpTotal" value="${data[month]['total']['jp'].profits}"/>
					<c:set var="allTotal" value="${enTotal + deTotal + frTotal + itTotal + esTotal + jpTotal}"/>
					<c:set var="enAdTotal" value="${data[month]['total']['en'].adInEventFee}"/>
					<c:set var="deAdTotal" value="${data[month]['total']['de'].adInEventFee}"/>
					<c:set var="frAdTotal" value="${data[month]['total']['fr'].adInEventFee}"/>
					<c:set var="itAdTotal" value="${data[month]['total']['it'].adInEventFee}"/>
					<c:set var="esAdTotal" value="${data[month]['total']['es'].adInEventFee}"/>
					<c:set var="jpAdTotal" value="${data[month]['total']['jp'].adInEventFee}"/>
					<c:set var="allAdTotal" value="${enAdTotal + deAdTotal + frAdTotal + itAdTotal + esAdTotal + jpAdTotal}"/>
					<c:forEach items="${data[month]}" var="typeMap">
					<c:set var="type" value="${typeMap.key}"/>
					<c:if test="${type ne 'total'}">
					<tr>
						<td style="text-align: left;">${fn:toUpperCase(month)}</td>
						<td style="text-align: left;">
							${type}
						</td>
						<td style="text-align: left;">
							<%--<c:set var="line" value="UnGrouped"/>
							<c:if test="${not empty typeLine[fn:toLowerCase(type)] }">
								<c:set var="line" value="${typeLine[fn:toLowerCase(type)] }"/>
							</c:if>
							${line} --%>
							${data[month][type]['total'].line}线
						</td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="en" value="0"/>
								<c:if test="${not empty data[month][type]['en'].profits }"><c:set var="en" value="${data[month][type]['en'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${en}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['en'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['en'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${en/(enTotal<0?-enTotal:enTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<c:set var="de" value="0"/>
								<c:if test="${not empty data[month][type]['de'].profits }"><c:set var="de" value="${data[month][type]['de'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${de}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['de'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['de'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${de/(deTotal<0?-deTotal:deTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="fr" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].profits }"><c:set var="fr" value="${data[month][type]['fr'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${fr}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['fr'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${fr/(frTotal<0?-frTotal:frTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<c:set var="it" value="0"/>
								<c:if test="${not empty data[month][type]['it'].profits }"><c:set var="it" value="${data[month][type]['it'].profits }"/></c:if>
								<fmt:formatNumber  pattern="#,##0" value="${it}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['it'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['it'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${it/(itTotal<0?-itTotal:itTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="es" value="0"/>
								<c:if test="${not empty data[month][type]['es'].profits }"><c:set var="es" value="${data[month][type]['es'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${es}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['es'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['es'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${es/(esTotal<0?-esTotal:esTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<c:set var="jp" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].profits }"><c:set var="jp" value="${data[month][type]['jp'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${jp}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['jp'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jp/(jpTotal<0?-jpTotal:jpTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="total" value="0"/>
								<c:if test="${not empty data[month][type]['total'].profits }"><c:set var="total" value="${data[month][type]['total'].profits }"/></c:if>
								<fmt:formatNumber pattern="#,##0" value="${total}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<c:set var="adFee" value="0"/>
								<c:if test="${not empty data[month][type]['total'].adInEventFee }">
									<c:set var="adFee" value="${data[month][type]['total'].adInEventFee }"/>
								</c:if>
								<fmt:formatNumber pattern="#,##0" value="${adFee}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${total/allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
					</tr>
					</c:if>
					</c:forEach>
					<%--季度度总计 --%>
					<tr style="background-color: yellow">
						<td style="text-align: left;">${fn:toUpperCase(month)}</td>
						<td style="text-align: left;">total</td>
						<td style="text-align: left;"></td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal/(allTotal<0?-allTotal:allTotal)*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allAdTotal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">100%</td>
						</c:if>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
	
	<c:if test="${'3' eq dataType }">
	<!-- 利润率 -->
	<div id="monthDiv" class="tab-pane active">
		<table id="monthTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: left;">日期</th>
					<th style="text-align: left;">产品类型</th>
				    <th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;" class="hasColor">EN</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP</th>
					</c:if>
				    <c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="monthMap">
					<c:set var="month" value="${monthMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString ne month}">
					<c:set var="enTotal" value="${data[month]['total']['en'].profitRate}"/>
					<c:set var="deTotal" value="${data[month]['total']['de'].profitRate}"/>
					<c:set var="frTotal" value="${data[month]['total']['fr'].profitRate}"/>
					<c:set var="itTotal" value="${data[month]['total']['it'].profitRate}"/>
					<c:set var="esTotal" value="${data[month]['total']['es'].profitRate}"/>
					<c:set var="jpTotal" value="${data[month]['total']['jp'].profitRate}"/>
					<c:set var="allTotal" value="${data[month]['total']['total'].profitRate}"/>
					<c:forEach items="${data[month]}" var="typeMap">
					<c:set var="type" value="${typeMap.key}"/>
					<c:if test="${type ne 'total'}">
					<tr>
						<td style="text-align: left;">${month}</td>
						<td style="text-align: left;">
							${type}
						</td>
						<td style="text-align: left;">
							<%--<c:set var="line" value="UnGrouped"/>
							<c:if test="${not empty typeLine[fn:toLowerCase(type)] }">
								<c:set var="line" value="${typeLine[fn:toLowerCase(type)] }"/>
							</c:if>
							${line} --%>
							${data[month][type]['total'].line}线
						</td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="en" value="0"/>
								<c:if test="${not empty data[month][type]['en'].profitRate }">
									<c:set var="en" value="${data[month][type]['en'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${en*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<c:set var="de" value="0"/>
								<c:if test="${not empty data[month][type]['de'].profitRate }">
									<c:set var="de" value="${data[month][type]['de'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${de*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="fr" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].profitRate }">
									<c:set var="fr" value="${data[month][type]['fr'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${fr*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<c:set var="it" value="0"/>
								<c:if test="${not empty data[month][type]['it'].profitRate }">
									<c:set var="it" value="${data[month][type]['it'].profitRate }"/>
									<fmt:formatNumber  pattern="#,##0" value="${it*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="es" value="0"/>
								<c:if test="${not empty data[month][type]['es'].profitRate }">
									<c:set var="es" value="${data[month][type]['es'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${es*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<c:set var="jp" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].profitRate }">
									<c:set var="jp" value="${data[month][type]['jp'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${jp*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="total" value="0"/>
								<c:if test="${not empty data[month][type]['total'].profitRate }">
									<c:set var="total" value="${data[month][type]['total'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${total*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
					</tr>
					</c:if>
					</c:forEach>
					<%--月度总计 --%>
					<tr style="background-color: yellow">
						<td style="text-align: left;">${month}</td>
						<td style="text-align: left;">total</td>
						<td style="text-align: left;"></td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div id="season" class="hideCls tab-pane">
		<table id="seasonTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: left;">日期</th>
					<th style="text-align: left;">产品类型</th>
				    <th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;" class="hasColor">EN</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP</th>
					</c:if>
				    <c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="monthMap">
					<c:set var="month" value="${monthMap.key}"/>
					<c:set var="season" value="${year}${month}"/>
					<c:if test="${fn:contains(month, 'q') && currSeason ne season}">
					<c:set var="enTotal" value="${data[month]['total']['en'].profitRate}"/>
					<c:set var="deTotal" value="${data[month]['total']['de'].profitRate}"/>
					<c:set var="frTotal" value="${data[month]['total']['fr'].profitRate}"/>
					<c:set var="itTotal" value="${data[month]['total']['it'].profitRate}"/>
					<c:set var="esTotal" value="${data[month]['total']['es'].profitRate}"/>
					<c:set var="jpTotal" value="${data[month]['total']['jp'].profitRate}"/>
					<c:set var="allTotal" value="${data[month]['total']['total'].profitRate}"/>
					<c:forEach items="${data[month]}" var="typeMap">
					<c:set var="type" value="${typeMap.key}"/>
					<c:if test="${type ne 'total'}">
					<tr>
						<td style="text-align: left;">${fn:toUpperCase(month)}</td>
						<td style="text-align: left;">
							${type}
						</td>
						<td style="text-align: left;">
							<%--<c:set var="line" value="UnGrouped"/>
							<c:if test="${not empty typeLine[fn:toLowerCase(type)] }">
								<c:set var="line" value="${typeLine[fn:toLowerCase(type)] }"/>
							</c:if>
							${line} --%>
							${data[month][type]['total'].line}线
						</td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="en" value="0"/>
								<c:if test="${not empty data[month][type]['en'].profitRate }">
									<c:set var="en" value="${data[month][type]['en'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${en*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<c:set var="de" value="0"/>
								<c:if test="${not empty data[month][type]['de'].profitRate }">
									<c:set var="de" value="${data[month][type]['de'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${de*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="fr" value="0"/>
								<c:if test="${not empty data[month][type]['fr'].profitRate }">
									<c:set var="fr" value="${data[month][type]['fr'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${fr*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<td style="text-align: left;">
							<c:set var="it" value="0"/>
							<c:if test="${not empty data[month][type]['it'].profitRate }">
								<c:set var="it" value="${data[month][type]['it'].profitRate }"/>
								<fmt:formatNumber  pattern="#,##0" value="${it*100}"  maxFractionDigits="0" />%
							</c:if>
						</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="es" value="0"/>
								<c:if test="${not empty data[month][type]['es'].profitRate }">
									<c:set var="es" value="${data[month][type]['es'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${es*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<c:set var="jp" value="0"/>
								<c:if test="${not empty data[month][type]['jp'].profitRate }">
									<c:set var="jp" value="${data[month][type]['jp'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${jp*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;" class="hasColor">
								<c:set var="total" value="0"/>
								<c:if test="${not empty data[month][type]['total'].profitRate }">
									<c:set var="total" value="${data[month][type]['total'].profitRate }"/>
									<fmt:formatNumber pattern="#,##0" value="${total*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</c:if>
					</tr>
					</c:if>
					</c:forEach>
					<%--季度度总计 --%>
					<tr style="background-color: yellow">
						<td style="text-align: left;">${fn:toUpperCase(month)}</td>
						<td style="text-align: left;">total</td>
						<td style="text-align: left;"></td>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${enTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${frTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${esTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
				    	<c:if test="${viewAll}">
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allTotal*100}"  maxFractionDigits="0" />%
							</td>
						</c:if>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
	
	<c:if test="${'1' eq dataType }">
	<!-- 销售额目标&完成情况 -->
	<div id="target" class="hideCls tab-pane">
		<!-- 完成情况合计 -->
		<c:if test="${viewAll }">
		<table id="totalTargetTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;" colspan="6">
					${year }销售额目标完成情况总计
					<c:if test="${'2016' eq year }"><%--<span style="color:red">(不含E线)</span> --%></c:if>
				</th></tr>
				<tr>
					<th style="text-align: left;">月份</th>
					<th style="text-align: left;">${year }原定目标/万欧</th>
					<th style="text-align: left;">${year }实际目标/万欧</th>
				    <th style="text-align: left;">月销售/万欧</th>
					<th style="text-align: left;">盈亏/万欧</th>
					<th style="text-align: left;">完成比例</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="allGoal1" value="0"/>
				<c:set var="allGoal" value="0"/>
				<c:set var="allSales" value="0"/>
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString > month}">
						<c:set var="enMonth" value="${data[month]['total']['en'].sales}"/>
						<c:set var="deMonth" value="${data[month]['total']['de'].sales}"/>
						<c:set var="frMonth" value="${data[month]['total']['fr'].sales}"/>
						<c:set var="itMonth" value="${data[month]['total']['it'].sales}"/>
						<c:set var="esMonth" value="${data[month]['total']['es'].sales}"/>
						<c:set var="jpMonth" value="${data[month]['total']['jp'].sales}"/>
						<c:set var="allMonth" value="${enMonth + deMonth + frMonth + itMonth + esMonth + jpMonth}"/>
						<c:set var="allSales" value="${allSales + allMonth}"/>
						<tr>
							<c:set var="monthGoal" value="${countryGoalMap[month]['total'].goal}"/>
							<c:set var="allGoal" value="${allGoal + monthGoal}"/>
							<td style="text-align: left;">${month}</td>
							<td style="text-align: left;">
								<c:if test="${targetGoalMap[month]>0}">
								<c:set var="allGoal1" value="${allGoal1 + targetGoalMap[month]}"/>
									<fmt:formatNumber pattern="#,##0" value="${targetGoalMap[month]/10000}"  maxFractionDigits="0" />
								</c:if>
								<c:if test="${empty targetGoalMap[month]}">
								<c:set var="allGoal1" value="${allGoal1 + monthGoal}"/>
									<fmt:formatNumber pattern="#,##0" value="${monthGoal/10000}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${monthGoal/10000}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allMonth/10000}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${(allMonth - monthGoal)/10000}"  maxFractionDigits="0" />
								<%--<fmt:formatNumber pattern="#,##0" value="${(allMonth - targetGoalMap[month])/10000}"  maxFractionDigits="0" /> --%>
							</td>
							<td style="text-align: left;" class="${allMonth > monthGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${allMonth/monthGoal*100}"  maxFractionDigits="0" />%
							</td>
						</tr>
					</c:if>
				</c:forEach>
				<tr>
					<c:set var="monthGoal" value="${countryGoalMap[month]['total'].goal }"/>
					<td style="text-align: left;">总计</td>
					<td style="text-align: left;">
						<c:if test="${allGoal1 > 0}">
							<fmt:formatNumber pattern="#,##0" value="${allGoal1/10000}"  maxFractionDigits="0" />
						</c:if>
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${allGoal/10000}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${allSales/10000}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${(allSales - allGoal1)/10000}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${allSales > allGoal1?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${allSales/allGoal1*100}"  maxFractionDigits="0" />%
					</td>
				</tr>
			</tbody>
		</table>
		<br/>
		</c:if>
		<!-- 分平台完成情况 -->
		<c:if test="${viewAll }">
		<table id="countryTargetTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;" colspan="20">
					${year }分平台销售额目标完成情况
					<c:if test="${'2016' eq year }"><%--<span style="color:red">(不含E线)</span> --%></c:if>(货币单位：欧元)
				</th></tr>
				<tr>
					<th style="text-align: left;">月份</th>
					<th style="text-align: left;">${year }目标</th>
					<th style="text-align: left;" class="hasColor">EN目标</th>
					<th style="text-align: left;" class="hasColor">EN完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>
					<%--<th style="text-align: left;" class="hasColor">NON-EN目标</th>
					<th style="text-align: left;" class="hasColor">NON-EN完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>--%>
					<th style="text-align: left;">DE目标</th>
					<th style="text-align: left;">DE完成</th>
					<th style="text-align: left;">完成比</th>
					<th style="text-align: left;" class="hasColor">FR目标</th>
					<th style="text-align: left;" class="hasColor">FR完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>
					<th style="text-align: left;">IT目标</th>
					<th style="text-align: left;">IT完成</th>
					<th style="text-align: left;">完成比</th>
					<th style="text-align: left;" class="hasColor">ES目标</th>
					<th style="text-align: left;" class="hasColor">ES完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>
					<th style="text-align: left;">JP目标</th>
					<th style="text-align: left;">JP完成</th>
					<th style="text-align: left;">完成比</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="enTotalSale" value="0"/>
				<c:set var="deTotalSale" value="0"/>
				<c:set var="frTotalSale" value="0"/>
				<c:set var="itTotalSale" value="0"/>
				<c:set var="esTotalSale" value="0"/>
				<c:set var="jpTotalSale" value="0"/>
				<c:set var="enTotalGoal" value="0"/>
				<c:set var="deTotalGoal" value="0"/>
				<c:set var="frTotalGoal" value="0"/>
				<c:set var="itTotalGoal" value="0"/>
				<c:set var="esTotalGoal" value="0"/>
				<c:set var="jpTotalGoal" value="0"/>
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString > month}">
						<c:set var="enMonth" value="${data[month]['total']['en'].sales}"/>
						<c:set var="enTotalSale" value="${enTotalSale + enMonth}"/>
						<c:set var="deMonth" value="${data[month]['total']['de'].sales}"/>
						<c:set var="deTotalSale" value="${deTotalSale + deMonth}"/>
						<c:set var="frMonth" value="${data[month]['total']['fr'].sales}"/>
						<c:set var="frTotalSale" value="${frTotalSale + frMonth}"/>
						<c:set var="itMonth" value="${data[month]['total']['it'].sales}"/>
						<c:set var="itTotalSale" value="${itTotalSale + itMonth}"/>
						<c:set var="esMonth" value="${data[month]['total']['es'].sales}"/>
						<c:set var="esTotalSale" value="${esTotalSale + esMonth}"/>
						<c:set var="jpMonth" value="${data[month]['total']['jp'].sales}"/>
						<c:set var="jpTotalSale" value="${jpTotalSale + jpMonth}"/>
						
						<c:set var="enGoal" value="${countryGoalMap[month]['en'].goal}"/>
						<c:set var="enTotalGoal" value="${enTotalGoal + enGoal}"/>
						<c:set var="deGoal" value="${countryGoalMap[month]['de'].goal}"/>
						<c:set var="deTotalGoal" value="${deTotalGoal + deGoal}"/>
						<c:set var="frGoal" value="${countryGoalMap[month]['fr'].goal}"/>
						<c:set var="frTotalGoal" value="${frTotalGoal + frGoal}"/>
						<c:set var="itGoal" value="${countryGoalMap[month]['it'].goal}"/>
						<c:set var="itTotalGoal" value="${itTotalGoal + itGoal}"/>
						<c:set var="esGoal" value="${countryGoalMap[month]['es'].goal}"/>
						<c:set var="esTotalGoal" value="${esTotalGoal + esGoal}"/>
						<c:set var="jpGoal" value="${countryGoalMap[month]['jp'].goal}"/>
						<c:set var="jpTotalGoal" value="${jpTotalGoal + jpGoal}"/>
						
						<%--<c:set var="nonEnMonth" value="${deMonth+frMonth+itMonth+esMonth+jpMonth}"/>
						<c:set var="nonEnTotalSale" value="${deTotalSale+frTotalSale+itTotalSale+esTotalSale+jpTotalSale}"/>
						<c:set var="nonEnGoal" value="${deGoal+frGoal+itGoal+esGoal+jpGoal}"/>
						<c:set var="nonEnTotalGoal" value="${deTotalGoal+frTotalGoal+itTotalGoal+esTotalGoal+jpTotalGoal}"/>--%>
						<tr>
							<c:set var="monthGoal" value="${countryGoalMap[month]['total'].goal}"/>
							
							<td style="text-align: left;">${month}</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${monthGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${enGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${enMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${enMonth > enGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${enMonth/enGoal*100}"  maxFractionDigits="0" />%
							</td>
							<%--<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${nonEnGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${nonEnMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${nonEnMonth > nonEnGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${nonEnMonth/nonEnGoal*100}"  maxFractionDigits="0" />%
							</td>--%>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${deMonth > deGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${deMonth/deGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${frGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${frMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${frMonth > frGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${frMonth/frGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${itMonth > itGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${itMonth/itGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${esGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${esMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${esMonth > esGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${esMonth/esGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${jpMonth > jpGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${jpMonth/jpGoal*100}"  maxFractionDigits="0" />%
							</td>
						</tr>
					</c:if>
				</c:forEach>
				<tr>
					<c:set var="monthGoal" value="${countryGoalMap[month]['total'].goal}"/>
					<%-- 总目标按原始目标计算--%>
					<c:if test="${'2016' eq year }">
						<c:set var="enTotalGoal" value="${allGoal1 * 0.48}"/>
						<c:set var="deTotalGoal" value="${allGoal1 * 0.27}"/>
						<c:set var="frTotalGoal" value="${allGoal1 * 0.1}"/>
						<c:set var="itTotalGoal" value="${allGoal1 * 0.05}"/>
						<c:set var="esTotalGoal" value="${allGoal1 * 0.02}"/>
						<c:set var="jpTotalGoal" value="${allGoal1 * 0.08}"/>
					</c:if>
					<td style="text-align: left;">总计</td>
					<td style="text-align: left;">
						<%--<fmt:formatNumber pattern="#,##0" value="${allGoal}"  maxFractionDigits="0" /> --%>
						<fmt:formatNumber pattern="#,##0" value="${allGoal1}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${enTotalGoal}"  maxFractionDigits="0" />
						<%--<fmt:formatNumber pattern="#,##0" value="${allGoal1 * 0.48}"  maxFractionDigits="0" /> --%>
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${enTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${enTotalSale > enTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${enTotalSale/enTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<!-- 非英语国家总计
						<c:set var="nonEnTotalGoal" value="${deTotalGoal+frTotalGoal+itTotalGoal+esTotalGoal+jpTotalGoal}"/>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${nonEnTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${nonEnTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${nonEnTotalSale > nonEnTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${nonEnTotalSale/nonEnTotalGoal*100}"  maxFractionDigits="0" />%
					</td> -->
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${deTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${deTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${deTotalSale > deTotalGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${deTotalSale/deTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${frTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${frTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${frTotalSale > frTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${frTotalSale/frTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${itTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${itTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${itTotalSale > itTotalGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${itTotalSale/itTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${esTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${esTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${esTotalSale > esTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${esTotalSale/esTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${jpTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${jpTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${jpTotalSale > jpTotalGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${jpTotalSale/jpTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
				</tr>
				<!-- 季度 -->
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:set var="season" value="${year}${month}"/>
					<c:if test="${fn:contains(month, 'q') && currSeason > season}">
						<tr>
							<%--实际目标 --%>
							<c:set var="seasonGoal" value="${countryGoalMap[month]['total'].goal}"/>
							<%-- 总目标按原始目标计算--%>
							<c:if test="${'2016' eq year }">
								<c:set var="seasonGoal" value="${targetGoalMap[month]}"/>
								<c:set var="enSeasonGoal" value="${seasonGoal * 0.48}"/>
								<c:set var="deSeasonGoal" value="${seasonGoal * 0.27}"/>
								<c:set var="frSeasonGoal" value="${seasonGoal * 0.1}"/>
								<c:set var="itSeasonGoal" value="${seasonGoal * 0.05}"/>
								<c:set var="esSeasonGoal" value="${seasonGoal * 0.02}"/>
								<c:set var="jpSeasonGoal" value="${seasonGoal * 0.08}"/>
							</c:if>
							<c:if test="${'2016' ne year }">
								<c:set var="enSeasonGoal" value="${countryGoalMap[month]['en'].goal}"/>
								<c:set var="deSeasonGoal" value="${countryGoalMap[month]['de'].goal}"/>
								<c:set var="frSeasonGoal" value="${countryGoalMap[month]['fr'].goal}"/>
								<c:set var="itSeasonGoal" value="${countryGoalMap[month]['it'].goal}"/>
								<c:set var="esSeasonGoal" value="${countryGoalMap[month]['es'].goal}"/>
								<c:set var="jpSeasonGoal" value="${countryGoalMap[month]['jp'].goal}"/>
							</c:if>
							<td style="text-align: left;">${fn:toUpperCase(month)}</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${seasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<%--<fmt:formatNumber pattern="#,##0" value="${countryGoalMap[month]['en'].goal}"  maxFractionDigits="0" /> --%>
								<fmt:formatNumber pattern="#,##0" value="${enSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['en'].sales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['en'].sales > enSeasonGoal?'imp':'hasColor' }">
								<c:if test="${enSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['en'].sales/enSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<!-- 非英语国家总计
							<c:set var="nonEnSeasonGoal" value="${seasonGoal-enSeasonGoal}"/>
							<c:set var="nonEnSeasonSales" value="${data[month]['total']['de'].sales+data[month]['total']['fr'].sales+data[month]['total']['it'].sales+data[month]['total']['es'].sales+data[month]['total']['jp'].sales}"/>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${nonEnSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${nonEnSeasonSales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${nonEnSeasonSales > nonEnSeasonGoal?'imp':'hasColor' }">
								<c:if test="${nonEnSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${nonEnSeasonSales/nonEnSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							 -->
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['de'].sales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['de'].sales > deSeasonGoal?'imp':'' }">
								<c:if test="${deSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['de'].sales/deSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${frSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['fr'].sales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['fr'].sales > frSeasonGoal?'imp':'hasColor' }">
								<c:if test="${frSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['fr'].sales/frSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['it'].sales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['it'].sales > itSeasonGoal?'imp':'' }">
								<c:if test="${itSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['it'].sales/itSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${esSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['es'].sales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['es'].sales > esSeasonGoal?'imp':'hasColor' }">
								<c:if test="${esSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['es'].sales/esSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['jp'].sales}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['jp'].sales > jpSeasonGoal?'imp':'' }">
								<c:if test="${jpSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['jp'].sales/jpSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
		<br/>
		</c:if>
		<!-- 分产品线完成情况 -->
		<table id="lineTargetTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;" colspan="26">
					${year }分产品线销售额目标完成情况(货币单位：欧元)
				</th></tr>
				<tr>
					<th style="text-align: left;">月份</th>
					<th style="text-align: left;">产品线</th>
				    <c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total目标</th>
						<th style="text-align: left;" class="hasColor">Total完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;">EN目标</th>
						<th style="text-align: left;">EN完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;" class="hasColor">NON-EN目标</th>
						<th style="text-align: left;" class="hasColor">NON-EN完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE目标</th>
						<th style="text-align: left;">DE完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR目标</th>
						<th style="text-align: left;" class="hasColor">FR完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT目标</th>
						<th style="text-align: left;">IT完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES目标</th>
						<th style="text-align: left;" class="hasColor">ES完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
				    <c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP目标</th>
						<th style="text-align: left;">JP完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
				    <%--<c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total目标</th>
						<th style="text-align: left;" class="hasColor">Total完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if> --%>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString > month}">
						<c:forEach items="${lineNameList}" var="lineName">
						<c:if test="${viewAll || fn:contains(viewLine, lineName) }">
							<tr>
								<c:set var="monthGoalTotal" value="0"/>
								<c:set var="monthSaleTotal" value="0"/>
								<td style="text-align: left;">${month}</td>
								<td style="text-align: left;">${lineName}线</td>
								
				    			<c:if test="${viewAll}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['total'].goal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['total'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['total'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${(lineGoalMap[month][lineName]['total'].goal>1 && lineData[month][lineName]['total'].sales > lineGoalMap[month][lineName]['total'].goal)?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['total'].goal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['total'].sales/lineGoalMap[month][lineName]['total'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>	
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['en'].goal > 1 && lineData[month][lineName]['en'].sales > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['en'].goal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['en'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['en'].sales + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['en'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['en'].sales > lineGoalMap[month][lineName]['en'].goal?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['en'].goal > 1 && lineData[month][lineName]['en'].sales > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['en'].sales/lineGoalMap[month][lineName]['en'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
									<c:set var="nonEnMonthGoalTotal" value="${lineGoalMap[month][lineName]['total'].goal - lineGoalMap[month][lineName]['en'].goal }"/>
									<c:set var="nonEnMonthSaleTotal" value="${lineData[month][lineName]['total'].sales - lineData[month][lineName]['en'].sales }"/>
									<td style="text-align: left;" class="hasColor">
										<c:if test="${nonEnMonthGoalTotal > 1 && nonEnMonthSaleTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${nonEnMonthGoalTotal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<fmt:formatNumber pattern="#,##0" value="${nonEnMonthSaleTotal}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${nonEnMonthSaleTotal > nonEnMonthGoalTotal?'imp':'hasColor' }">
										<c:if test="${nonEnMonthGoalTotal > 1 && nonEnMonthSaleTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${nonEnMonthSaleTotal/nonEnMonthGoalTotal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['de'].goal > 1 && lineData[month][lineName]['de'].sales > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['de'].goal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['de'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['de'].sales + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['de'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['de'].sales > lineGoalMap[month][lineName]['de'].goal?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['de'].goal > 1 && lineData[month][lineName]['de'].sales > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['de'].sales/lineGoalMap[month][lineName]['de'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['fr'].goal > 1 && lineData[month][lineName]['fr'].sales > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['fr'].goal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['fr'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['fr'].sales + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['fr'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['fr'].sales > lineGoalMap[month][lineName]['fr'].goal?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['fr'].goal > 1 && lineData[month][lineName]['fr'].sales > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['fr'].sales/lineGoalMap[month][lineName]['fr'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['it'].goal > 1 && lineData[month][lineName]['it'].sales > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['it'].goal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['it'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['it'].sales + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['it'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['it'].sales > lineGoalMap[month][lineName]['it'].goal?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['it'].goal > 1 && lineData[month][lineName]['it'].sales > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['it'].sales/lineGoalMap[month][lineName]['it'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['es'].goal > 1 && lineData[month][lineName]['es'].sales > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['es'].goal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['es'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['es'].sales + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['es'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['es'].sales > lineGoalMap[month][lineName]['es'].goal?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['es'].goal > 1 && lineData[month][lineName]['es'].sales > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['es'].sales/lineGoalMap[month][lineName]['es'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['jp'].goal > 1 && lineData[month][lineName]['jp'].sales > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['jp'].goal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['jp'].goal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['jp'].sales + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['jp'].sales}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['jp'].sales > lineGoalMap[month][lineName]['jp'].goal?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['jp'].goal > 0 && lineData[month][lineName]['jp'].sales > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['jp'].sales/lineGoalMap[month][lineName]['jp'].goal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<%--<c:if test="${viewAll}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${monthGoalTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${monthGoalTotal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<fmt:formatNumber pattern="#,##0" value="${monthSaleTotal}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${(monthGoalTotal>1 && monthSaleTotal > monthGoalTotal)?'imp':'hasColor' }">
										<c:if test="${monthGoalTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${monthSaleTotal/monthGoalTotal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>	 --%>
							</tr>
							</c:if>	
						</c:forEach>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
	
	<c:if test="${'2' eq dataType }">
	<!-- 利润目标&完成情况 -->
	<div id="target" class="hideCls tab-pane" style="scroll:auto">
		<!-- 完成情况合计 -->
		<c:if test="${viewAll }">
		<table id="totalTargetTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;" colspan="6">
					${year }利润目标完成情况总计
					<c:if test="${'2016' eq year }"><%--<span style="color:red">(不含E线)</span> --%></c:if>
				</th></tr>
				<tr>
					<th style="text-align: left;">月份</th>
					<th style="text-align: left;">${year }原定目标/万欧</th>
					<th style="text-align: left;">${year }实际目标/万欧</th>
				    <th style="text-align: left;">月利润/万欧</th>
					<th style="text-align: left;">盈亏/万欧</th>
					<th style="text-align: left;">完成比例</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="allGoal1" value="0"/>
				<c:set var="allGoal" value="0"/>
				<c:set var="allSales" value="0"/>
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString > month}">
						<c:set var="enMonth" value="${data[month]['total']['en'].profits}"/>
						<c:set var="deMonth" value="${data[month]['total']['de'].profits}"/>
						<c:set var="frMonth" value="${data[month]['total']['fr'].profits}"/>
						<c:set var="itMonth" value="${data[month]['total']['it'].profits}"/>
						<c:set var="esMonth" value="${data[month]['total']['es'].profits}"/>
						<c:set var="jpMonth" value="${data[month]['total']['jp'].profits}"/>
						<c:set var="allMonth" value="${enMonth + deMonth + frMonth + itMonth + esMonth + jpMonth}"/>
						<c:set var="allSales" value="${allSales + allMonth}"/>
						<tr>
							<c:set var="monthGoal" value="${countryGoalMap[month]['total'].profitGoal}"/><%--月度总目标 --%>
							<c:set var="allGoal" value="${allGoal + monthGoal}"/>
							<td style="text-align: left;">${month}</td>
							<td style="text-align: left;">
								<c:if test="${targetGoalMap[month]>0}">
								<c:set var="allGoal1" value="${allGoal1 + targetGoalMap[month] * rateMap['total']}"/>
									<fmt:formatNumber pattern="#,##0" value="${(targetGoalMap[month] * rateMap['total'])/10000}"  maxFractionDigits="0" />
								</c:if>
								<c:if test="${empty targetGoalMap[month]}">
								<c:set var="allGoal1" value="${allGoal1 + monthGoal}"/>
									<fmt:formatNumber pattern="#,##0" value="${monthGoal/10000}"  maxFractionDigits="0" />
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${monthGoal/10000}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${allMonth/10000}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${(allMonth - monthGoal)/10000}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${allMonth > monthGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${allMonth/monthGoal*100}"  maxFractionDigits="0" />%
							</td>
						</tr>
					</c:if>
				</c:forEach>
				<tr>
					<c:set var="monthGoal" value="${countryGoalMap[month]['total'].goal * rateMap['total'] }"/>
					<td style="text-align: left;">总计</td>
					<td style="text-align: left;">
						<c:if test="${allGoal1 > 0}">
							<fmt:formatNumber pattern="#,##0" value="${allGoal1/10000}"  maxFractionDigits="0" />
						</c:if>
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${allGoal/10000}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${allSales/10000}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${(allSales - allGoal1)/10000}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${allSales > allGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${allSales/allGoal1*100}"  maxFractionDigits="0" />%
					</td>
				</tr>
			</tbody>
		</table>
		<br/>
		</c:if>
		<!-- 分平台完成情况 -->
		<c:if test="${viewAll }">
		<table id="countryTargetTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;" colspan="20">
					${year }分平台利润目标完成情况
					<c:if test="${'2016' eq year }"><%--<span style="color:red">(不含E线)</span> --%></c:if>(货币单位：欧元)
				</th></tr>
				<tr>
					<th style="text-align: left;">月份</th>
					<th style="text-align: left;">${year }目标</th>
					<th style="text-align: left;" class="hasColor">EN目标</th>
					<th style="text-align: left;" class="hasColor">EN完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>
					<th style="text-align: left;">DE目标</th>
					<th style="text-align: left;">DE完成</th>
					<th style="text-align: left;">完成比</th>
					<th style="text-align: left;" class="hasColor">FR目标</th>
					<th style="text-align: left;" class="hasColor">FR完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>
					<th style="text-align: left;">IT目标</th>
					<th style="text-align: left;">IT完成</th>
					<th style="text-align: left;">完成比</th>
					<th style="text-align: left;" class="hasColor">ES目标</th>
					<th style="text-align: left;" class="hasColor">ES完成</th>
					<th style="text-align: left;" class="hasColor">完成比</th>
					<th style="text-align: left;">JP目标</th>
					<th style="text-align: left;">JP完成</th>
					<th style="text-align: left;">完成比</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="enTotalSale" value="0"/>
				<c:set var="deTotalSale" value="0"/>
				<c:set var="frTotalSale" value="0"/>
				<c:set var="itTotalSale" value="0"/>
				<c:set var="esTotalSale" value="0"/>
				<c:set var="jpTotalSale" value="0"/>
				<c:set var="enTotalGoal" value="0"/>
				<c:set var="deTotalGoal" value="0"/>
				<c:set var="frTotalGoal" value="0"/>
				<c:set var="itTotalGoal" value="0"/>
				<c:set var="esTotalGoal" value="0"/>
				<c:set var="jpTotalGoal" value="0"/>
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString > month}">
						<c:set var="enMonth" value="${data[month]['total']['en'].profits}"/>
						<c:set var="enTotalSale" value="${enTotalSale + enMonth}"/>
						<c:set var="deMonth" value="${data[month]['total']['de'].profits}"/>
						<c:set var="deTotalSale" value="${deTotalSale + deMonth}"/>
						<c:set var="frMonth" value="${data[month]['total']['fr'].profits}"/>
						<c:set var="frTotalSale" value="${frTotalSale + frMonth}"/>
						<c:set var="itMonth" value="${data[month]['total']['it'].profits}"/>
						<c:set var="itTotalSale" value="${itTotalSale + itMonth}"/>
						<c:set var="esMonth" value="${data[month]['total']['es'].profits}"/>
						<c:set var="esTotalSale" value="${esTotalSale + esMonth}"/>
						<c:set var="jpMonth" value="${data[month]['total']['jp'].profits}"/>
						<c:set var="jpTotalSale" value="${jpTotalSale + jpMonth}"/>
						
						<c:set var="enGoal" value="${countryGoalMap[month]['en'].profitGoal}"/>
						<c:set var="enTotalGoal" value="${enTotalGoal + enGoal}"/>
						<c:set var="deGoal" value="${countryGoalMap[month]['de'].profitGoal}"/>
						<c:set var="deTotalGoal" value="${deTotalGoal + deGoal}"/>
						<c:set var="frGoal" value="${countryGoalMap[month]['fr'].profitGoal}"/>
						<c:set var="frTotalGoal" value="${frTotalGoal + frGoal}"/>
						<c:set var="itGoal" value="${countryGoalMap[month]['it'].profitGoal}"/>
						<c:set var="itTotalGoal" value="${itTotalGoal + itGoal}"/>
						<c:set var="esGoal" value="${countryGoalMap[month]['es'].profitGoal}"/>
						<c:set var="esTotalGoal" value="${esTotalGoal + esGoal}"/>
						<c:set var="jpGoal" value="${countryGoalMap[month]['jp'].profitGoal}"/>
						<c:set var="jpTotalGoal" value="${jpTotalGoal + jpGoal}"/>
						<tr>
							<c:set var="monthGoal" value="${countryGoalMap[month]['total'].goal * rateMap['total']}"/>
							<c:if test="${'2016' ne year }">
								<c:set var="monthGoal" value="${countryGoalMap[month]['total'].profitGoal}"/>
							</c:if>
							
							<td style="text-align: left;">${month}</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${monthGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${enGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${enMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${enMonth > enGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${enMonth/enGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${deMonth > deGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${deMonth/deGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${frGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${frMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${frMonth > frGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${frMonth/frGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${itMonth > itGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${itMonth/itGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${esGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${esMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${esMonth > esGoal?'imp':'hasColor' }">
								<fmt:formatNumber pattern="#,##0" value="${esMonth/esGoal*100}"  maxFractionDigits="0" />%
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpMonth}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${jpMonth > jpGoal?'imp':'' }">
								<fmt:formatNumber pattern="#,##0" value="${jpMonth/jpGoal*100}"  maxFractionDigits="0" />%
							</td>
						</tr>
					</c:if>
				</c:forEach>
				<tr>
					<c:if test="${'2016' eq year }">
						<c:set var="enTotalGoal" value="${(allGoal1/0.27)*0.48*0.31}"/>
						<c:set var="deTotalGoal" value="${(allGoal1/0.27)*0.27*0.22}"/>
						<c:set var="frTotalGoal" value="${(allGoal1/0.27)*0.1*0.22}"/>
						<c:set var="itTotalGoal" value="${(allGoal1/0.27)*0.05*0.19}"/>
						<c:set var="esTotalGoal" value="${(allGoal1/0.27)*0.02*0.24}"/>
						<c:set var="jpTotalGoal" value="${(allGoal1/0.27)*0.08*0.32}"/>
					</c:if>
					<td style="text-align: left;">总计</td>
					<td style="text-align: left;">
						<c:if test="${'2016' ne year }">
							<fmt:formatNumber pattern="#,##0" value="${allGoal}"  maxFractionDigits="0" />
						</c:if>
						<c:if test="${'2016' eq year }">
							<fmt:formatNumber pattern="#,##0" value="${allGoal1}"  maxFractionDigits="0" />
						</c:if>
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${enTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${enTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${enTotalSale > enTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${enTotalSale/enTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${deTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${deTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${deTotalSale > deTotalGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${deTotalSale/deTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${frTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${frTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${frTotalSale > frTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${frTotalSale/frTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${itTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${itTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${itTotalSale > itTotalGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${itTotalSale/itTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${esTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="hasColor">
						<fmt:formatNumber pattern="#,##0" value="${esTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${esTotalSale > esTotalGoal?'imp':'hasColor' }">
						<fmt:formatNumber pattern="#,##0" value="${esTotalSale/esTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${jpTotalGoal}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;">
						<fmt:formatNumber pattern="#,##0" value="${jpTotalSale}"  maxFractionDigits="0" />
					</td>
					<td style="text-align: left;" class="${jpTotalSale > jpTotalGoal?'imp':'' }">
						<fmt:formatNumber pattern="#,##0" value="${jpTotalSale/jpTotalGoal*100}"  maxFractionDigits="0" />%
					</td>
				</tr>
				<!-- 季度 -->
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:set var="season" value="${year}${month}"/>
					<c:if test="${fn:contains(month, 'q') && currSeason > season}">
						<tr>
							<%--实际目标 --%>
							<c:set var="seasonGoal" value="${countryGoalMap[month]['total'].goal * rateMap['total']}"/>
							<c:if test="${'2016' ne year }">
								<c:set var="seasonGoal" value="${countryGoalMap[month]['total'].profitGoal}"/>
								<c:set var="enSeasonGoal" value="${countryGoalMap[month]['en'].profitGoal}"/>
								<c:set var="deSeasonGoal" value="${countryGoalMap[month]['de'].profitGoal}"/>
								<c:set var="frSeasonGoal" value="${countryGoalMap[month]['fr'].profitGoal}"/>
								<c:set var="itSeasonGoal" value="${countryGoalMap[month]['it'].profitGoal}"/>
								<c:set var="esSeasonGoal" value="${countryGoalMap[month]['es'].profitGoal}"/>
								<c:set var="jpSeasonGoal" value="${countryGoalMap[month]['jp'].profitGoal}"/>
							</c:if>
							<%-- 总目标按原始目标计算--%>
							<c:if test="${'2016' eq year }">
								<c:set var="seasonGoal" value="${targetGoalMap[month]*0.27}"/>
								<c:set var="enSeasonGoal" value="${targetGoalMap[month] * 0.48 * 0.31}"/>
								<c:set var="deSeasonGoal" value="${targetGoalMap[month] * 0.27 * 0.22}"/>
								<c:set var="frSeasonGoal" value="${targetGoalMap[month] * 0.1 * 0.22}"/>
								<c:set var="itSeasonGoal" value="${targetGoalMap[month] * 0.05 * 0.19}"/>
								<c:set var="esSeasonGoal" value="${targetGoalMap[month] * 0.02 * 0.24}"/>
								<c:set var="jpSeasonGoal" value="${targetGoalMap[month] * 0.08 * 0.32}"/>
							</c:if>
							<td style="text-align: left;">${fn:toUpperCase(month)}</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${seasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
							<%--
								<fmt:formatNumber pattern="#,##0" value="${countryGoalMap[month]['en'].profitGoal}"  maxFractionDigits="0" /> --%>
								<fmt:formatNumber pattern="#,##0" value="${enSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['en'].profits}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['en'].profits > enSeasonGoal?'imp':'hasColor' }">
								<c:if test="${enSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['en'].profits/enSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${deSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['de'].profits}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['de'].profits > deSeasonGoal?'imp':'' }">
								<c:if test="${deSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['de'].profits/deSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${frSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['fr'].profits}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['fr'].profits > frSeasonGoal?'imp':'hasColor' }">
								<c:if test="${frSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['fr'].profits/frSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${itSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['it'].profits}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['it'].profits > itSeasonGoal?'imp':'' }">
								<c:if test="${itSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['it'].profits/itSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${esSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="hasColor">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['es'].profits}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['es'].profits > esSeasonGoal?'imp':'hasColor' }">
								<c:if test="${esSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['es'].profits/esSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${jpSeasonGoal}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;">
								<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['jp'].profits}"  maxFractionDigits="0" />
							</td>
							<td style="text-align: left;" class="${data[month]['total']['jp'].profits > jpSeasonGoal?'imp':'' }">
								<c:if test="${jpSeasonGoal > 0}">
									<fmt:formatNumber pattern="#,##0" value="${data[month]['total']['jp'].profits/jpSeasonGoal*100}"  maxFractionDigits="0" />%
								</c:if>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
		<br/>
		</c:if>
		<!-- 分产品线完成情况 -->
		<table id="lineTargetTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr><th style="text-align: center;" colspan="26">
					${year }分产品线利润目标完成情况(货币单位：欧元)
				</th></tr>
				<tr>
					<th style="text-align: left;">月份</th>
					<th style="text-align: left;">产品线</th>
					<c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total目标</th>
						<th style="text-align: left;" class="hasColor">Total完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
						<th style="text-align: left;">EN目标</th>
						<th style="text-align: left;">EN完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;" class="hasColor">NON-EN目标</th>
						<th style="text-align: left;" class="hasColor">NON-EN完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
						<th style="text-align: left;">DE目标</th>
						<th style="text-align: left;">DE完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
						<th style="text-align: left;" class="hasColor">FR目标</th>
						<th style="text-align: left;" class="hasColor">FR完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
						<th style="text-align: left;">IT目标</th>
						<th style="text-align: left;">IT完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
						<th style="text-align: left;" class="hasColor">ES目标</th>
						<th style="text-align: left;" class="hasColor">ES完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if>
					<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
						<th style="text-align: left;">JP目标</th>
						<th style="text-align: left;">JP完成</th>
						<th style="text-align: left;">完成比</th>
					</c:if>
					<%--<c:if test="${viewAll}">
						<th style="text-align: left;" class="hasColor">Total目标</th>
						<th style="text-align: left;" class="hasColor">Total完成</th>
						<th style="text-align: left;" class="hasColor">完成比</th>
					</c:if> --%>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${countryGoalMap}" var="monthGoalMap">
					<c:set var="month" value="${monthGoalMap.key}"/>
					<c:if test="${!fn:contains(month, 'q') && currMonthString > month}">
						<c:forEach items="${lineNameList}" var="lineName">
						<c:if test="${viewAll || fn:contains(viewLine, lineName) }">
							<tr>
								<c:set var="monthGoalTotal" value="0"/>
								<c:set var="monthSaleTotal" value="0"/>
								<td style="text-align: left;">${month}</td>
								<td style="text-align: left;">${lineName}线</td>
								
								<c:if test="${viewAll}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['total'].profitGoal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['total'].profitGoal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['total'].profits}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['total'].profits > (lineGoalMap[month][lineName]['total'].profitGoal)?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['total'].profitGoal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['total'].profits/lineGoalMap[month][lineName]['total'].profitGoal*100}"  maxFractionDigits="0" />% 
										</c:if>
									</td>
								</c:if>
								<c:if test="${viewAll || fn:contains(viewCountry, 'en')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['en'].profitGoal > 1 }">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['en'].profitGoal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['en'].profitGoal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['en'].profits + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['en'].profits}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['en'].profits > (lineGoalMap[month][lineName]['en'].profitGoal)?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['en'].profitGoal > 1 }">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['en'].profits/(lineGoalMap[month][lineName]['en'].profitGoal)*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
				    			<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
									<c:set var="nonEnMonthGoalTotal" value="${lineGoalMap[month][lineName]['total'].profitGoal - lineGoalMap[month][lineName]['en'].profitGoal }"/>
									<c:set var="nonEnMonthSaleTotal" value="${lineData[month][lineName]['total'].profits - lineData[month][lineName]['en'].profits }"/>
									<td style="text-align: left;" class="hasColor">
										<c:if test="${nonEnMonthGoalTotal > 1 && nonEnMonthSaleTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${nonEnMonthGoalTotal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<fmt:formatNumber pattern="#,##0" value="${nonEnMonthSaleTotal}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${nonEnMonthSaleTotal > nonEnMonthGoalTotal?'imp':'hasColor' }">
										<c:if test="${nonEnMonthGoalTotal > 1 && nonEnMonthSaleTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${nonEnMonthSaleTotal/nonEnMonthGoalTotal*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
								<c:if test="${viewAll || fn:contains(viewCountry, 'de')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['de'].profitGoal > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['de'].profitGoal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['de'].profitGoal }"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['de'].profits + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['de'].profits}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['de'].profits > (lineGoalMap[month][lineName]['de'].profitGoal)?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['de'].profitGoal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['de'].profits/(lineGoalMap[month][lineName]['de'].profitGoal)*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
								<c:if test="${viewAll || fn:contains(viewCountry, 'fr')}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['fr'].profitGoal > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['fr'].profitGoal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['fr'].profitGoal }"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['fr'].profits + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['fr'].profits }"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['fr'].profits > (lineGoalMap[month][lineName]['fr'].profitGoal)?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['fr'].profitGoal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['fr'].profits/(lineGoalMap[month][lineName]['fr'].profitGoal)*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
								<c:if test="${viewAll || fn:contains(viewCountry, 'it')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['it'].profitGoal > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['it'].profitGoal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['it'].profitGoal }"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['it'].profits + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['it'].profits }"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['it'].profits > (lineGoalMap[month][lineName]['it'].profitGoal)?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['it'].profitGoal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['it'].profits/(lineGoalMap[month][lineName]['it'].profitGoal)*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
								<c:if test="${viewAll || fn:contains(viewCountry, 'es')}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['es'].profitGoal > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['es'].profitGoal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['es'].profitGoal }"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;" class="hasColor">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['es'].profits + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['es'].profits }"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['es'].profits > (lineGoalMap[month][lineName]['es'].profitGoal)?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['es'].profitGoal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['es'].profits/(lineGoalMap[month][lineName]['es'].profitGoal)*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
								<c:if test="${viewAll || fn:contains(viewCountry, 'jp')}">
									<td style="text-align: left;">
										<c:if test="${lineGoalMap[month][lineName]['jp'].profitGoal > 1}">
											<c:set var="monthGoalTotal" value="${lineGoalMap[month][lineName]['jp'].profitGoal + monthGoalTotal }"/>
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['jp'].profitGoal}"  maxFractionDigits="0" />
										</c:if>
									</td>
									<td style="text-align: left;">
										<c:set var="monthSaleTotal" value="${lineData[month][lineName]['jp'].profits + monthSaleTotal }"/>
										<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['jp'].profits }"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${lineData[month][lineName]['jp'].profits > (lineGoalMap[month][lineName]['jp'].profitGoal)?'imp':'' }">
										<c:if test="${lineGoalMap[month][lineName]['jp'].profitGoal > 0}">
											<fmt:formatNumber pattern="#,##0" value="${lineData[month][lineName]['jp'].profits/(lineGoalMap[month][lineName]['jp'].profitGoal)*100}"  maxFractionDigits="0" />%
										</c:if>
									</td>
								</c:if>
								<%--<c:if test="${viewAll}">
									<td style="text-align: left;" class="hasColor">
										<c:if test="${lineGoalMap[month][lineName]['total'].profitGoal > 1}">
											<!--<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['total'].goal * rateMap['total']}"  maxFractionDigits="0" /> -->
											<fmt:formatNumber pattern="#,##0" value="${lineGoalMap[month][lineName]['total'].profitGoal}"  maxFractionDigits="0" />
										</c:if>
										<!--
										<c:if test="${monthGoalTotal > 1}">
											<fmt:formatNumber pattern="#,##0" value="${monthGoalTotal}"  maxFractionDigits="0" />
										</c:if> -->
									</td>
									<td style="text-align: left;" class="hasColor">
										<fmt:formatNumber pattern="#,##0" value="${monthSaleTotal}"  maxFractionDigits="0" />
									</td>
									<td style="text-align: left;" class="${monthSaleTotal > (lineGoalMap[month][lineName]['total'].profitGoal)?'imp':'hasColor' }">
										<c:if test="${lineGoalMap[month][lineName]['total'].profitGoal > 1}">
											<!--<fmt:formatNumber pattern="#,##0" value="${monthSaleTotal/(lineGoalMap[month][lineName]['total'].goal * rateMap['total'])*100}"  maxFractionDigits="0" />% -->
											<fmt:formatNumber pattern="#,##0" value="${monthSaleTotal/lineGoalMap[month][lineName]['total'].profitGoal*100}"  maxFractionDigits="0" />% 
										</c:if>
									</td>
								</c:if> --%>
							</tr>
							</c:if>
						</c:forEach>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
	<br/>
	</div>
</body>
</html>
