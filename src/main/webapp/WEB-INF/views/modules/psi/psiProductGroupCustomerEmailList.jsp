<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>产品线客服分组</title>
	<meta name="decorator" content="default"/>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style>
			table {table-layout:fixed}
			td  {word-wrap:break-word;word-break:break-all;}
			.input-medium{
				width:80px;
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
		
		
		$("#contentTable").on('click', '.save', function(e){
		    e.preventDefault();
		  
			var tr =$(this).parent().parent();
			var itemId =$(this).parent().find("input[name='itemId']").val();
			var lineId =$(this).parent().find("input[name='lineId']").val();
			var country =$(this).parent().find("input[name='country']").val();
			var $this=$(this);
			var userId=tr.find("select[name='userId']").val();
			console.log(userId);
		/* 	if(!userId){
				top.$.jBox.tip("负责人不能为空", 'info',{timeout:2000});
				return false;
			} */
			var param = {};
			param.id = itemId;
			param.lineId = lineId;
			param.userId=encodeURI(userId);
			param.country= country;
			$.get("${ctx}/psi/psiProductManageGroup/seveGroupCustomerEmail?"+$.param(param),function(data){
				if(!(data)){    
					top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
				}else{
					top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
					$this.parent().find("input[name='itemId']").val(data);
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
		<li><a href="${ctx}/psi/psiProductManageGroup/psiProductManageEdit"><spring:message code="psi_product_product_manager"/></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/psiProductPurchaseEdit"><spring:message code="psi_product_purchasing_manager"/></a></li>
		<li><a href="${ctx}/psi/product/productGroupUser"><spring:message code="psi_product_sales"/></a></li>
		<li ><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomer"><spring:message code="psi_product_event"/></a></li>
		<li class="active"><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomerEmail"><spring:message code="psi_product_email"/></a></li>
		<%-- <li><a href="${ctx}/psi/psiProductManageGroup/findGroupPhoto"><spring:message code="psi_product_photographer"/></a></li> --%>
		
	</ul>
	<%-- <form:form id="inputForm" modelAttribute="" action="" method="post" class="breadcrumb form-search" Style="height: 30px;text-align:center;">
		<span style="font-size: 18px;font-weight: bold">产品线(客服)负责人</span>
	</form:form> --%>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead>
		<tr>
			   <th style="width:15%;text-align: center;vertical-align: middle;"><spring:message code="psi_product_line"/></th>
			   <th style="width:15%;text-align: center;vertical-align: middle;">Country</th>
			   <th style="width:60%;text-align: center;vertical-align: middle;"><spring:message code="psi_product_charge"/></th>
			   <th style="width:10%;text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
		</tr>
		</thead>
		<tbody>
			<c:forEach items="${lineList}" var="line">
			      <tr>
			         <td rowspan='8' style="text-align: center;vertical-align: middle;">${line.name}</td>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('de','platform','')}</td>
			         <td>
			             <c:set var="deId" value=""/>
			             <c:set var="deList" value="" />
			             <select name="userId" multiple class="multiSelect required"  style="width:80%">
			               <c:if test="${not empty customerMap[line.id]['de']}">
			                  <c:forEach items="${customerMap[line.id]['de']}" var="groupCustomer">
			                      <c:set var='deId' value='${groupCustomer.id}'/>
			                       <c:set var="deList" value="${deList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['de']}" var="user">
				              <c:if test="${!fn:contains(deList,user.id) }">
				                 <option value="${user.id}">${user.name}</option>
				              </c:if>
						   </c:forEach> 
						</select>
			         </td>
			         <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${deId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="de"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('fr','platform','')}</td>
			         <td>
			             <c:set var="frId" value=""/>
			             <c:set var="frList" value="" />
			             <select name="userId" multiple class="multiSelect required"  style="width:80%">
			               <c:if test="${not empty customerMap[line.id]['fr']}">
			                  <c:forEach items="${customerMap[line.id]['fr']}" var="groupCustomer">
			                      <c:set var='frId' value='${groupCustomer.id}'/>
			                      <c:set var="frList" value="${frList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['fr']}" var="user">
				             <c:if test="${!fn:contains(frList,user.id) }">
								<option value="${user.id}">${user.name}</option>	
							</c:if>		
						   </c:forEach> 
						</select>
			         </td>
			         
			         <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${frId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="fr"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('it','platform','')}</td>
			         <td>
			             <c:set var='itId' value=''/>
			             <c:set var="itList" value="" />
			             <select name="userId" multiple class="multiSelect required"  style="width:80%">
			                <c:if test="${not empty customerMap[line.id]['it']}">
			                  <c:forEach items="${customerMap[line.id]['it']}" var="groupCustomer">
			                      <c:set var='itId' value='${groupCustomer.id}'/>
			                      <c:set var="itList" value="${itList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['it']}" var="user">
				             <c:if test="${!fn:contains(itList,user.id) }">
								<option value="${user.id}">${user.name}</option>
							</c:if>			
						   </c:forEach> 
						</select>
			         </td>
			         <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${itId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="it"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('es','platform','')}</td>
			          <td>
			          <c:set var='esId' value='${groupCustomer.id}'/>
			          <c:set var="esList" value="" />
			            <select name="userId" multiple class="multiSelect required"  style="width:80%">
			              <c:if test="${not empty customerMap[line.id]['es']}">
			                  <c:forEach items="${customerMap[line.id]['es']}" var="groupCustomer">
			                  <c:set var='esId' value='${groupCustomer.id}'/>
			                  <c:set var="esList" value="${esList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['es']}" var="user">
				             <c:if test="${!fn:contains(esList,user.id) }">
								<option value="${user.id}">${user.name}</option>	
							</c:if>		
						   </c:forEach> 
						</select>
			         </td>
			         <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${esId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="es"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('uk','platform','')}</td>
			          <td>
			            <select name="userId" multiple class="multiSelect required"  style="width:80%">
			              <c:set var='ukId' value='${groupCustomer.id}'/>
			              <c:set var="ukList" value="" />
			              <c:if test="${not empty customerMap[line.id]['uk']}">
			                  <c:forEach items="${customerMap[line.id]['uk']}" var="groupCustomer">
			                     <c:set var='ukId' value='${groupCustomer.id}'/>
			                     <c:set var="ukList" value="${ukList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['uk']}" var="user">
				             <c:if test="${!fn:contains(ukList,user.id) }">
								<option value="${user.id}">${user.name}</option>	
							</c:if>		
						   </c:forEach> 
						</select>
			         </td>
			         <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${ukId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="uk"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('com','platform','')}</td>
			         <td>
			         <c:set var='comId' value='${groupCustomer.id}'/>
			         <c:set var="comList" value="" />
			            <select name="userId" multiple class="multiSelect required"  style="width:80%">
			              <c:if test="${not empty customerMap[line.id]['com']}">
			                  <c:forEach items="${customerMap[line.id]['com']}" var="groupCustomer">
			                  <c:set var='comId' value='${groupCustomer.id}'/>
			                  <c:set var="comList" value="${comList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['com']}" var="user">
				             <c:if test="${!fn:contains(comList,user.id) }">
								<option value="${user.id}">${user.name}</option>	
							</c:if>		
						   </c:forEach> 
						</select>
			         </td>
			          <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${comId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="com"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('ca','platform','')}</td>
			          <td>
			          <c:set var='caId' value='${groupCustomer.id}'/>
			          <c:set var="caList" value="" />
			            <select name="userId" multiple class="multiSelect required"  style="width:80%">
			              <c:if test="${not empty customerMap[line.id]['ca']}">
			                  <c:forEach items="${customerMap[line.id]['ca']}" var="groupCustomer">
			                  <c:set var='caId' value='${groupCustomer.id}'/>
			                  <c:set var="caList" value="${caList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['ca']}" var="user">
				             <c:if test="${!fn:contains(caList,user.id) }">
								<option value="${user.id}">${user.name}</option>
							</c:if>			
						   </c:forEach> 
						</select>
			         </td>
			          <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${caId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="ca"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			      <tr>
			         <td style="text-align: center;vertical-align: middle;">${fns:getDictLabel('jp','platform','')}</td>
			          <td>
			           <select name="userId" multiple class="multiSelect required"  style="width:80%">
			           <c:set var='jpId' value='${groupCustomer.id}'/>
			           <c:set var="jpList" value="" />
			              <c:if test="${not empty customerMap[line.id]['jp']}">
			                  <c:forEach items="${customerMap[line.id]['jp']}" var="groupCustomer">
			                  <c:set var='jpId' value='${groupCustomer.id}'/>
			                  <c:set var="jpList" value="${jpList},${groupCustomer.userId}" />
			                      <option value="${groupCustomer.userId}" selected>${groupCustomer.name}</option>		
			                  </c:forEach>
			               </c:if>
				           <c:forEach items="${roleMap['jp']}" var="user">
				             <c:if test="${!fn:contains(jpList,user.id) }">
								<option value="${user.id}">${user.name}</option>
							</c:if>			
						   </c:forEach> 
						</select>
			         </td>
	            
	                 <td style="text-align: center;vertical-align: middle;">
			         <shiro:hasPermission name="psi:product:productGroupCustomerEdit">
					         <input type='hidden' name='itemId'  value="${jpId}"/> 
					         <input type='hidden' name='lineId'  value="${line.id}"/> 
					         <input type='hidden' name='country'  value="jp"/> 
					         <a class='btn btn-primary save'>保存</a>
			         </shiro:hasPermission>
			         </td>
			      </tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
