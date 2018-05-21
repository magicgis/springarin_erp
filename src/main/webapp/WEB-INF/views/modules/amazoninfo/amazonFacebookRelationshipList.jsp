<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>amazonFacebookRelationship</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
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
				 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap","sScrollX": "100%",
						"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0 ] }] , 
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
					 	"aaSorting": [[1, "desc" ]]
					});
					/*  new FixedColumns( oTable,{
					 		"iLeftColumns":3,
							"iLeftWidth": 400
					 	} ); */
					 $(".row:first").append($("#searchContent").html());
					 	
						$("#checkall").click(function(){
							 $('[name=checkId]:checkbox').each(function(){
							     if($(this).attr("checked")=='checked'){
							    	 $(this).attr("checked", false);
							     }else{
							    	 $(this).attr("checked", true);
							     }
							 });
						});

		});
		function deleteDate(){
			var ids = $("input:checkbox[name='checkId']:checked");
			if(!ids.length){
		    	$.jBox.tip('请选择删除数据！');
				return;
			}		
			var arr = new Array();
			for(var i=0;i<ids.length; i++){
				var id = ids[i].value;
				arr.push(id);
			}
			var idsAll = arr.join(',');
			top.$.jBox.confirm("确定删除这些数据？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					   $("#deleteBtn").attr("disabled","disabled");
					   $("#searchForm").attr("action","${ctx}/amazoninfo/amazonAndFacebook/deleteRelationArr?ids="+idsAll);
					   $("#searchForm").submit();
					   $("#searchForm").attr("action","${ctx}/amazoninfo/amazonAndFacebook/amazonFacebookRelationship");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	    <li><a href="${ctx}/amazoninfo/amazonAndFacebook/totalList">投放数据分析</a></li>
		<li><a href="${ctx}/amazoninfo/amazonAndFacebook/list">Facebook投放数据</a></li>
		<li><a href="${ctx}/amazoninfo/amazonAndFacebook/amazonFacebookList">Amazon订单数据</a></li>
		<li  class="active"><a href="${ctx}/amazoninfo/amazonAndFacebook/amazonFacebookRelationship">关联数据</a></li>
	</ul>
	<div style="display: none" id="searchContent">
		<form id="searchForm" modelAttribute="amazonFacebookRelationship" action="${ctx}/amazoninfo/amazonAndFacebook/amazonFacebookRelationship" method="post">
		&nbsp;&nbsp;
	    <label><b>Date Shipped：</b></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="date" value="<fmt:formatDate value="${amazonFacebookRelationship.date}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonFacebookRelationship.endDate}" pattern="yyyy-MM-dd"/>" id="end" class="input-small"/>

		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>	&nbsp;&nbsp;
		<input id="deleteBtn" class="btn btn-primary" type="button" value="删除" onclick='deleteDate();'/>	
	   <%--  <a href="#updateExcel" role="button" class="btn  btn-primary" data-toggle="modal" id="uploadFile"><spring:message code="sys_but_upload"/></a>  --%>
	<form>
	</div>
	
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		            <th style="width: 3%"><input type="checkbox" id="checkall" checked></th>
		            <th>Date</th>
		            <th>Market</th>
					<th>Product Line</th>
					<th>Product</th>
					<th>Gender</th>
					<th>Audience</th>
					<th>Age</th>
					<th>Placement</th>
					<th>ASIN on Ad</th>
					<th>Ad ID</th>
					<th>Tracking ID</th>
					<th>Pre-view</th>
					<th>操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="report">
			<tr>
			    <td><input type="checkbox" class="chebox" name="checkId" value="${report.id}" checked/></td>
			    <td>${report.date}</td>
				<td>${fns:getDictLabel(report.market, 'platform', defaultValue)}</td>
				<td>${report.productLine}</td>
				<td>${report.product}</td>
				<td>${report.gender}</td>
				<td>${report.audience}</td>
				<td>${report.age}</td>
				<td>${report.placement}</td>
				<td>${report.asinOnAd}</td>
				<td>${report.adId}</td>
				<td>${report.trackingId}</td>
				<td><a href='${report.preView}' target='_blank'>${fns:abbr(report.preView,20)}</a></td>
				<td><a class="btn btn-danger" href="${ctx}/amazoninfo/amazonAndFacebook/deleteRelation?id=${report.id}">delete</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
	<div id="updateExcel" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  enctype="multipart/form-data" id="uploadForm" action="${ctx}/amazoninfo/amazonAndFacebook/uploadFile" method="post">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">文件上传</h3>
						  </div>
						  <div class="modal-body">
							<label ><spring:message code="psi_transport_fileType"/> ：</label>
							<select  id="type" name="type">
								<option value="0">Facebook数据文件</option>
								<option value="1">Amazon数据文件</option>
								<option value="2">关联文件</option>
							</select><br/><br/>
							<input type="file" name="excel"  id="excel" accept="application/msexcel" class="required"/> 
						  </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="submit" id="uploadTypeFile"><spring:message code="sys_but_upload"/></button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
	
</body>
</html>
