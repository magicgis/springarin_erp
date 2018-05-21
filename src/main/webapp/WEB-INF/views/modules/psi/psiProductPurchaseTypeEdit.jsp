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
			var setting = {check:{enable:true,nocheckInherit:false},view:{selectedMulti:false},
					data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
						return false;
					}}};
			var zNodes=[ {id:'0', pId:'', name:"<spring:message code="psi_product_type"/>",nocheck: true}, 
					<c:forEach items="${typeGroupList}" var="menu">{id:'${menu.id}', pId:'${not empty menu.parent.id?menu.parent.id:0}', name:"${not empty menu.parent.id?fns:unescapeHtml(menu.name):'类型列表'}"},
		            </c:forEach>
		            <c:forEach items="${typeProductList}" var="menu1">{id:'${menu1.id}', pId:'${menu1.parent.id}', name:"${menu1.name}",nocheck: true},
		            </c:forEach> ];
			
			var tree = $.fn.zTree.init($("#menuTree"), setting, zNodes);
			//tree.expandAll(true);
			var rootNode=tree.getNodes()[0];  
	        expandLevel(tree,rootNode,1);
			tree.setting.check.chkboxType = { "Y" : "s", "N" : "s" };
			$("#updateGroupType").click(function(){
				var ids = [], nodes = tree.getCheckedNodes(true);
				for(var i=0; i<nodes.length; i++) {
					ids.push(nodes[i].id);
				}
				$("#selectNodes").val(ids);
			});
			
			
			$("#updateAllType").click(function(e){
				   if($("#selectNodes").val()==""){
						$.jBox.tip('请先勾选变更节点'); 
						return;
				   }else{
					  /*  $.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/psi/psiProductManageGroup/isUpdateGroupNode?updateIds="+$("#selectNodes").val(),
		      			   async: true,
		      			   success: function(msg){
		      				  if(msg=="0"){
		      					$.jBox.tip('只能勾选产品类型节点'); 
		    					return;
		      				  }else if(msg=="1"){ */
		      					 $("#updateForm").submit();
		      				/*   }
		      			   }
		          		}); */
				   }  
			});
			
			
		});
		
		function expandLevel(treeObj,node,level)  
        {  
            var childrenNodes = node.children;  
            for(var i=0;i<childrenNodes.length;i++)  
            {  
                treeObj.expandNode(childrenNodes[i], true, false, false);  
                level=level-1;  
                if(level>0)  
                {  
                    expandLevel(treeObj,childrenNodes[i],level);  
                }  
            }  
        } 
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit"><spring:message code="psi_product_line"/></a></li>
		<shiro:hasPermission name="amazoninfo:productTypeGroup:edit"><li><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeForm?id=${dictType.id}&parent.id=${dictType.parent.id}">分组${not empty dictType.id?'修改':'添加'}</a></li></shiro:hasPermission>
		<li ><a href="${ctx}/psi/psiProductManageGroup/psiProductManageEdit"><spring:message code="psi_product_product_manager"/></a></li>
		<li class="active"><a href="${ctx}/psi/psiProductManageGroup/psiProductPurchaseEdit"><spring:message code="psi_product_purchasing_manager"/></a></li>
		<li ><a href="${ctx}/psi/product/productGroupUser"><spring:message code="psi_product_sales"/></a></li>
		<li ><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomer"><spring:message code="psi_product_event"/></a></li>
		<li ><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomerEmail"><spring:message code="psi_product_email"/></a></li>
		<%-- <li><a href="${ctx}/psi/psiProductManageGroup/findGroupPhoto"><spring:message code="psi_product_photographer"/></a></li> --%>
	</ul>
	<form:form id="inputForm"  action="${ctx}/psi/psiProductManageGroup/psiProductTypeEdit" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<input type="hidden" name="delIds" id="delIds">
		<div class="control-group">
            <shiro:hasPermission name="amazoninfo:purchaseManagerProductType:edit">
	             <a href="#update" role="button" class="btn btn-primary" data-toggle="modal" id="updateGroupType">更新关系</a> 
            </shiro:hasPermission>
		</div><br/><br/>
		<div class="control-group">
			<label class="control-label">TYPE:</label>
			<div class="controls">
				<div id="menuTree" class="ztree"  style="margin-top:3px;float:center;"></div>
			</div>
		</div>
	</form:form>
	  <div id="update" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  id="updateForm"   method="post" action="${ctx}/psi/psiProductManageGroup/psiProductPurchaseSave" class="form-inline">
				 <input type="hidden" id="selectNodes" name="selectNodes">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">更新分组</h3>
						  </div>
						  <div class="modal-body">
								<label>采购经理:</label>
					               <!-- <tags:treeselect id="dictType" name="parent.id" value="${dictType.parent.id}" labelName="parent.name" labelValue="${dictType.parent.name}"
										title="类型" url="/psi/psiProductManageGroup/treeData" extId="${dictType.id}"  cssClass="required" /> --> 
									<select  name="parent.id" class="required">
										<option value=""></option>
										<c:forEach items="${typeList}" var="item">
											<option value="${item.id}">${item.name}</option>									
										</c:forEach>
								    </select>
						 </div>
						   <div class="modal-footer">
						   <button class="btn btn-primary"  type="button" id="updateAllType">更新</button>
						    <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true">关闭</button>
						  </div> 
					</form>
		</div>
</body>
</html>