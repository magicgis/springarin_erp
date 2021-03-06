<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品管理分组</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
		$(document).ready(function(){
			$("#contentTable").on('click', '.save', function(e){
			    e.preventDefault();
				var tr =$(this).parent().parent();
				var lineId =$(this).parent().find("input[name='lineId']").val();
				var userId=tr.find("select[name='userId']").val();
				var param = {};
				param.lineId = lineId;   
				param.userId=encodeURI(userId);
				$.get("${ctx}/psi/psiProductManageGroup/saveGroupManager?"+$.param(param),function(data){
					if(!(data)){    
						top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
					}else{
						top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
					}
				});
				return true;
			});
			
		});
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit"><spring:message code="psi_product_line"/></a></li>
		<shiro:hasPermission name="amazoninfo:productTypeGroup:edit"><li><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeForm?id=${dictType.id}&parent.id=${dictType.parent.id}">分组${not empty dictType.id?'修改':'添加'}</a></li></shiro:hasPermission>
		<li class="active"><a href="${ctx}/psi/psiProductManageGroup/psiProductManageEdit"><spring:message code="psi_product_product_manager"/></a></li>
		<li ><a href="${ctx}/psi/psiProductManageGroup/psiProductPurchaseEdit"><spring:message code="psi_product_purchasing_manager"/></a></li>
		<li ><a href="${ctx}/psi/product/productGroupUser"><spring:message code="psi_product_sales"/></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomer"><spring:message code="psi_product_event"/></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomerEmail"><spring:message code="psi_product_email"/></a></li>
	<%-- 	<li><a href="${ctx}/psi/psiProductManageGroup/findGroupPhoto"><spring:message code="psi_product_photographer"/></a></li> --%>
	</ul>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead>
		<tr>
		   
		   <th style="width:15%;text-align: center;vertical-align: middle;"><spring:message code="psi_product_line"/></th>
		
		    <th style="width:60%;text-align: center;vertical-align: middle;"><spring:message code="psi_product_charge"/></th>
			<th style="width:10%;text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
		</tr>
		</thead>
		<tbody>
			<c:forEach items="${lineList}" var="line">
			      <tr>
			         <td  style="text-align: center;vertical-align: middle;">${line.name}</td>
			         <td>
			             <select name="userId" multiple class="multiSelect required"  style="width:80%">
			                  <c:forEach items="${users}" var="user">
			                      <c:set  var="flag" value="false" />
			                       <c:forEach items="${fn:split(managerMap[line.id],',')}" var="userId">
			                            <c:if test="${user.id eq userId }"><c:set  var="flag" value="true" /></c:if>
			                       </c:forEach>
			                      <option value="${user.id}" ${flag?'selected':''}>${user.name}</option>		
			                  </c:forEach>
						</select>
			         </td>
			         <td style="text-align: center;vertical-align: middle;">
			          <shiro:hasPermission name="amazoninfo:psiProductManageGroup:edit">
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>