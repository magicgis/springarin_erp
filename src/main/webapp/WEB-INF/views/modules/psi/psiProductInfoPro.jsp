<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>Pro</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
	$(document).ready(function() {
		$("#pro_type").change(function() {
			$("#searchForm").submit();
		});

		$("#btnAdd").click(function() {
			var type = $("#pro_type").val();
			$.jBox("get:${ctx}/psi/product/addDict?type="+type, {persistent: true,width:460,height:260,title:"新增"+type, buttons:{"保存并继续录入":1,"<spring:message code='sys_but_save'/>":2,"<spring:message code='sys_but_closed'/>":3},submit:function(v,h,f){
				if(v==1 || v==2){
					if(h.find("form").valid()){
						var val = h.find("input[name='value']").val();
						var label=h.find("input[name='label']").val();
						var param = {};
						param.value = val;
						param.type = type;
						param.label =label;
						$.post("${ctx}/psi/product/saveDict",param,function(data){
							if(data){
								$("#contentTable").append("<tr><td class='index'>"+($(".index").size()+1)+"</td><td class='val'>"+val+"</td><td>${fns:getUser().name}</td><td>刚刚</td><td><a href=\"#\" class=\"edit\">编辑</a>&nbsp;&nbsp;&nbsp;<a href=\"${ctx}/psi/product/deleteDic?did="+data+"\" onclick=\"return confirmx('确定要删除该产品吗？', this.href)\">删除</a><input type=\"hidden\" value="+data+"></td></tr>");									
							}
						});
					}else{
						return false;
					}
					return v==2;
				}
			}});
		});
		
		$("#contentTable").on('click','.edit',function() {
			var type = $("#pro_type").val();
			var dictId = $(this).parent().find("input").val();
			$.jBox("get:${ctx}/psi/product/addDict?dictId="+dictId, {persistent: true,width:460,height:260,title:"编辑"+type, buttons:{"<spring:message code='sys_but_save'/>":2,"<spring:message code='sys_but_closed'/>":3},submit:function(v,h,f){
				if(v==2){
					if(h.find("form").valid()){
						var val = h.find("input[name='value']").val();
						var label = h.find("input[name='label']").val();
						var param = {};
						param.value = val;
						param.type = type;
						param.label = label;
						param.dictId = dictId;
						$.post("${ctx}/psi/product/saveDict",param,function(data){
							if(data){
								$("input[value='"+dictId+"']").parent().parent().find(".val").html(val);
							}
						});
					}else{
						return false;
					}
					return true;
				}
			}});
		});
	});

	function page(n, s) {
		if (n && s) {
			$("#pageNo").val(n);
			$("#pageSize").val(s);
		}
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="psi:product:edit">
			<li><a href="${ctx}/psi/product/list">产品列表</a></li>
			<li><a href="${ctx}/psi/product/add">新增产品</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="${ctx}/psi/product/listDict">基本属性维护</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="dict"
		action="${ctx}/psi/product/listDict" class="breadcrumb form-search">
		<label>类型：</label>
		<select name="type" id="pro_type" style="width: 150px">
			<shiro:hasPermission name="psi:product:edit">
				<option value="product_type" ${dict.type eq 'product_type'?'selected':''}>产品类型</option>
				<option value="parts_type"   ${dict.type eq 'parts_type'?'selected':''}> 配件类型</option>
				<option value="product_color" ${dict.type eq 'product_color'?'selected':''}>产品颜色</option>
				<option value="product_brand" ${dict.type eq 'product_brand'?'selected':''}>品牌</option>
				<option value="product_material" ${dict.type eq 'product_material'?'selected':''}>材质</option>
			</shiro:hasPermission>
			<shiro:hasPermission name="psi:transport:edit">
				<option value="transport_pod" ${dict.type eq 'transport_pod'?'selected':''}>目的港</option>
			</shiro:hasPermission>
			<shiro:hasPermission name="amazoninfo:outsidePromotion:edit">
				<option value="website" ${dict.type eq 'website'?'selected':''}>站外平台</option>
			</shiro:hasPermission>
		</select>
		&nbsp;&nbsp;<input id="btnAdd" class="btn btn-primary" type="button" value="新  增 " />
	</form:form>
	<tags:message content="${message}" />
	<table id="contentTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 30px;">序号</th>
				<th style="width: 60px">值</th>
				<th style="width: 60px">名称</th>
				<th style="width: 80px;">创建人</th>
				<th style="width: 80px;">创建时间</th>
				<th style="width: 80px;">操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="dic" varStatus="i">
				<tr>
					<td class="index">${i.count}</td>
					<td class="val">${dic.value}</td>
					<td >${dic.label}</td>
					<td>${dic.createBy}</td>
					<td><fmt:formatDate value="${dic.createDate}" pattern="yyyy/MM/dd H:mm"/></td>
					<td><a href="#" class="edit">编辑</a>&nbsp;&nbsp;&nbsp;<a href="${ctx}/psi/product/deleteDic?did=${dic.id}" onclick="return confirmx('确定要删除该产品吗？', this.href)">删除</a><input type="hidden" value="${dic.id}"></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
