<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>line</title>
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
		
		
		$(document).ready(function() {

			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			$(".edit").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						if(data){
							if(!ckeckNums(data)){
								return "品类代码必须是大于0的正整数!!";
							}
						}
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(".attrId").val();
						param.productType = encodeURI($this.parent().find(".productType").val());
						param.code = newValue;
						console.log(newValue);
						$.get("${ctx}/amazoninfo/amazonPortsDetail/updateProductTypeCode?"+$.param(param),function(data){
							if(!data){
								$this.text(oldVal);
							}else{
								$.jBox.tip("修改成功！", 'info',{timeout:2000});
								$this.parent().find(".attrId").val(data);
							}
						});
						return true;
			 }});
				
			
				
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 15,
				"aLengthMenu":[[15, 30, 60,100,-1], [15, 30, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
			 	"ordering":true,
			     "aaSorting": [[1, "asc" ]]
			});
			
			// $(".row:first").append("&nbsp;&nbsp;<a class='btn'  href='${ctx}/psi/psiProductAttribute/export'>导出</a>");
			
		});

		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}

		function ckeckWeeks(text) {
			if(text.length != 1){
				return false;
			}
			if(text == 1 || text == 2 || text == 3 || text == 0){
				return true;
			}
			return false;
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	    <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">新增管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建母帖</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">新建普通帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=8">新建本地帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=2">复制帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">Cross帖</a></li>
		    </ul>
	   </li>
	     <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">编辑帖子信息</a></li>	
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/commonForm">编辑帖子信息(英语国家)</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=4">帖子类型转换</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=5">帖子一键还原</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>	
		    </ul>
	   </li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList">组合帖管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/findEanList">Ean列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeCodeList">品类代码</a></li>
	</ul>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:30%;">产品类型</th>
				<th style="text-align: left;vertical-align: middle;">品类代码</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${dictsList}" var="dict" varStatus="i">
				<tr>
					<td style="text-align: left;vertical-align: middle;">
							 ${dict.value }
					</td>
					<td>
					<input type="hidden" class="attrId" value="${codeMap[dict.value].id }" />
				        <input type="hidden" class="productType" value="${dict.value}" />
				        <a href="#" class="edit"  data-type="text" data-pk="1" data-title="Enter" >
					     	${codeMap[dict.value].code}
						</a>
					</td>
	           </tr> 
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
