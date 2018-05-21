<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品分组类型管理</title>
	<meta name="decorator" content="default"/>
	<link rel="stylesheet" href="${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/zTreeStyle.css" type="text/css">
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
		<style type="text/css">
.ztree li span.button.pIcon01_ico_open{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/1_open.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.pIcon01_ico_close{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/1_close.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.pIcon02_ico_open, .ztree li span.button.pIcon02_ico_close{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/2.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.icon01_ico_docu{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/3.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.icon02_ico_docu{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/4.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.icon03_ico_docu{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/5.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.icon04_ico_docu{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/6.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.icon05_ico_docu{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/7.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
.ztree li span.button.icon06_ico_docu{margin-right:2px; background: url(${ctxStatic}/jquery-ztree/3.5.12/css/zTreeStyle/img/diy/11.png) no-repeat scroll 0 0 transparent; vertical-align:top; *vertical-align:middle}
	</style>
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
					data:{simpleData:{enable:true}},showTitle:true,key: {title:"title"},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
						return false;
					}}};
			var zNodes=[ {id:'0', pId:'', name:"<spring:message code="psi_product_type"/>",nocheck: true}, 
					<c:forEach items="${typeGroupList}" var="menu">{id:'${menu.id}', pId:'${not empty menu.parent.id?menu.parent.id:0}', name:"${not empty menu.parent.id?fns:unescapeHtml(menu.name):'类型列表'}"},
		            </c:forEach>
		            <c:forEach items="${typeProductList}" var="menu1">{id:'${menu1.id}', pId:'${menu1.parent.id}', name:"${menu1.name}",title:"${'0' eq menu1.delFlag?'淘汰':'在售'}",nocheck: true,iconSkin:"${'0' eq menu1.delFlag?'icon06':'icon01'}"},
		            </c:forEach> ];
			
			var tree = $.fn.zTree.init($("#menuTree"), setting, zNodes);
			//tree.expandAll(true);
			var rootNode=tree.getNodes()[0];  
	        expandLevel(tree,rootNode,1);
			tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			$("#updateGroupType").click(function(){
				var ids = [], nodes = tree.getCheckedNodes(true);
				for(var i=0; i<nodes.length; i++) {
					ids.push(nodes[i].id);
				}
				$("#selectNodes").val(ids);
			});
			
			$("#updateName").click(function(){
				var ids = [], nodes = tree.getCheckedNodes(true);
				for(var i=0; i<nodes.length; i++) {
					ids.push(nodes[i].id);
				}
				if(nodes.length==0){
					$.jBox.tip('请先勾选一个节点'); 
					return;
				}
			 	if(nodes.length>1){
					$.jBox.tip('只能勾选一个节点'); 
					return;
				} else{
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/psi/psiProductTypeGroupDict/isDeleteGroupNode?deleteIds="+ids,
		      			   async: false,
		      			   success: function(msg){
		      				  if(msg=="0"){
		      					$.jBox.tip('只能勾选分组类型节点'); 
		    					return;
		      				  }else if(msg=="1"){
		  	      					$("#inputForm").attr("action","${ctx}/psi/psiProductTypeGroupDict/psiProductTypeForm?id="+ids);
		  							$("#inputForm").submit();
		  							$("#inputForm").attr("action","${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit");
		      				  }
		      			   }
		          		});
				}
			});
			
			$("#updateAllType").click(function(e){
				   if($("#selectNodes").val()==""){
						$.jBox.tip('请先勾选变更节点'); 
						return;
				   }else{
					   $.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/psi/psiProductTypeGroupDict/isUpdateGroupNode?updateIds="+$("#selectNodes").val(),
		      			   async: false,
		      			   success: function(msg){
		      				  if(msg=="0"){
		      					$.jBox.tip('只能勾选产品类型节点'); 
		    					return;
		      				  }else if(msg=="1"){
		      					 $("#updateForm").submit();
		      				  }
		      			   }
		          		});
				   }  
			});
			
			$("#delBtn").click(function(){
				var ids = [], nodes = tree.getCheckedNodes(true);
				for(var i=0; i<nodes.length; i++) {
					ids.push(nodes[i].id);
				}
				$("#delIds").val(ids);
				if($("#delIds").val()==""){
					$.jBox.tip('请先勾选删除分组节点'); 
					return;
				}else{
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/psi/psiProductTypeGroupDict/isDeleteGroupNode?deleteIds="+$("#delIds").val(),
		      			   async: false,
		      			   success: function(msg){
		      				  if(msg=="0"){
		      					$.jBox.tip('只能勾选分组类型节点'); 
		    					return;
		      				  }else if(msg=="1"){
     	      					$("#inputForm").attr("action","${ctx}/psi/psiProductTypeGroupDict/deleteNode");
     							$("#inputForm").submit();
     							$("#inputForm").attr("action","${ctx}/psi/psiProductTypeGroupDict/psiProductUpdateAll");
		      				  }
		      			   }
		          		});
				}
				
			});
			
		    $("#typeSelect").change(function(){
		    	$("#inputForm").submit();
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
		<li class="active"><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit"><spring:message code="psi_product_line"/></a></li>
		<shiro:hasPermission name="amazoninfo:productTypeGroup:edit"><li><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeForm?id=${dictType.id}&parent.id=${dictType.parent.id}">分组${not empty dictType.id?'修改':'添加'}</a></li></shiro:hasPermission>
		<li><a href="${ctx}/psi/psiProductManageGroup/psiProductManageEdit"><spring:message code="psi_product_product_manager"/></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/psiProductPurchaseEdit"><spring:message code="psi_product_purchasing_manager"/></a></li>
		<li><a href="${ctx}/psi/product/productGroupUser"><spring:message code="psi_product_sales"/></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomer"><spring:message code="psi_product_event"/></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomerEmail"><spring:message code="psi_product_email"/></a></li>
	<%-- 	<li><a href="${ctx}/psi/psiProductManageGroup/findGroupPhoto"><spring:message code="psi_product_photographer"/></a></li> --%>
	</ul>
	<form:form id="inputForm"  action="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<input type="hidden" name="delIds" id="delIds">
		<div class="control-group">
		    <select style="width:180px" id='typeSelect' name='type'>
		       <option value='0' ${'0' eq type?'selected':''}>All</option>
		       <option value='1' ${'1' eq type?'selected':''}><spring:message code="psi_product_select_type"/></option>
		    </select>&nbsp;&nbsp;
            <shiro:hasPermission name="amazoninfo:productTypeGroup:edit">
	             <a href="#update" role="button" class="btn btn-primary" data-toggle="modal" id="updateGroupType">更新关系</a> 
	             <button class="btn btn-primary" type="button" id="delBtn">删 除</button>
	              <button class="btn btn-primary" type="button" id="updateName">修改名称</button>
             </shiro:hasPermission>
             <div style='color:#ff0033;text-align: right;vertical-align: right;'><spring:message code="psi_product_means"/></div>
		</div><br/><br/>
		<div class="control-group">
			<label class="control-label">TYPE:</label>
			<div class="controls">
				<div id="menuTree" class="ztree"  style="margin-top:3px;float:center;"></div>
			</div>
		</div>
		
	</form:form>
	  <div id="update" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  id="updateForm"   method="post" action="${ctx}/psi/psiProductTypeGroupDict/psiProductUpdateAll" class="form-inline">
				 <input type="hidden" id="selectNodes" name="selectNodes">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">更新分组</h3>
						  </div>
						  <div class="modal-body">
								<label>分组名称:</label>
					               <!-- <tags:treeselect id="dictType" name="parent.id" value="${dictType.parent.id}" labelName="parent.name" labelValue="${dictType.parent.name}"
										title="类型" url="/psi/psiProductTypeGroupDict/treeData" extId="${dictType.id}"  cssClass="required" /> --> 
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