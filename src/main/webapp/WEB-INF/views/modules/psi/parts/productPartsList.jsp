<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购付款管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
		$(document).ready(function() {
			//$("#treeTable").treeTable({expandLevel : 1});
			 $(".mixtureRatio").editable({
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.proPartsId = $this.parent().find(":hidden").val();
						param.ratio = newValue;
						$.get("${ctx}/psi/productParts/updateMixtrueRatio?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);						
							}else{
								$.jBox.tip("保存配比成功！", 'info',{timeout:2000});
							}
						});
						return true;
					}
			 });
			 
			 
				$("#dataTable").dataTable({
					"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
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
		
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
	</script>
</head>
<body>
	<tags:message content="${message}"/>
	<table id="dataTable" class="table table-bordered table-condensed">   
		<thead>
		<tr>
		<th style="width:30%">产品名_颜色</th><th style="width:70%">配比设置</th></tr></thead>
		<tbody>
		<c:forEach items="${proPartsMap}" var="proPartsEntry">
			<tr id="${proPartsEntry.key}">
				<td style="margin-left:3px;">${proPartsEntry.key}</td><td>
				 <c:forEach items="${proPartsEntry.value}" var="proParts">
					<span style="float:left;width:180px;height:20px">${proParts.parts.partsName}</span>
					<span style="float:left;margin-left:0px;height:20px">
							<input type="hidden" value="${proParts.id}" class="proPartsId"/>
							<a href="#" class="mixtureRatio"  data-type="text"  data-pk="1" data-title="Enter MixtureRatio" data-value="${proParts.mixtureRatio}">${proParts.mixtureRatio}</a>
					</span>
					<br/>
				</c:forEach>
				</td>
			</tr>
		</c:forEach>
		
		
		</tbody>
	</table>
</body>
</html>
