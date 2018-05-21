<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title><spring:message code="amazon_sales_product_line"/></title>
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
	
	$(document).ready(function() {
		<c:forEach items="${countryList}" var="country" varStatus="i">
			var data = "[";
			<c:forEach items="${shelvesMap[country]}" var="userInfo">
				data+=("{'value':'"+'${fn:split(userInfo,',')[0]}'+"','text':'"+'${fn:split(userInfo,',')[1]}'+"'},")
			</c:forEach>
			if(data.length>1){
				data=data.substring(0,data.length-1);
			}
			data+="]";
			eval("data="+data);
			$(".groupUser${country}").editable({
			    value:$(this).val(),
			    source:data,
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.groupId = $this.parent().parent().find(".groupId").val();
					param.country = '${country}';
					param.userId = newValue;
					$.get("${ctx}/psi/product/productGroupUserSingleSave?"+$.param(param),function(data){
						if(!(data)){ 
							$this.text(oldVal);						
						}else{
							top.$.jBox.tip("更改产品线人员关系成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
			</c:forEach>
			
			$("#contentTable").on('click', '.save', function(e){
			    e.preventDefault();
			  
				//var tr =$(this).parent().parent();
				var lineId =$(this).parent().find("input[name='lineId']").val();
				var country =$(this).parent().find("input[name='country']").val();
				var userId=$(this).parent().parent().find("select[name='userId']").val();
				var param = {};
				param.groupId = lineId;
				param.userId= encodeURI(userId);
				param.country= country;
				$.get("${ctx}/psi/product/productGroupUserSingleSave?"+$.param(param),function(data){
					if(!(data)){    
						top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
					}else{
						top.$.jBox.tip("更改产品线人员关系成功！", 'info',{timeout:2000});
					}
				});
				return true;
			});
	});
	</script> 
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a
			href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit"><spring:message
					code="psi_product_line" /></a></li>
		<shiro:hasPermission name="amazoninfo:productTypeGroup:edit">
			<li><a
				href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeForm?id=${dictType.id}&parent.id=${dictType.parent.id}">分组${not
					empty dictType.id?'修改':'添加'}</a></li>
		</shiro:hasPermission>
		<li><a
			href="${ctx}/psi/psiProductManageGroup/psiProductManageEdit"><spring:message
					code="psi_product_product_manager" /></a></li>
		<li><a
			href="${ctx}/psi/psiProductManageGroup/psiProductPurchaseEdit"><spring:message
					code="psi_product_purchasing_manager" /></a></li>
		<li class="active"><a href="${ctx}/psi/product/productGroupUser"><spring:message
					code="psi_product_sales" /></a></li>
		<li><a href="${ctx}/psi/psiProductManageGroup/findGroupCustomer"><spring:message
					code="psi_product_event" /></a></li>
		<li><a
			href="${ctx}/psi/psiProductManageGroup/findGroupCustomerEmail"><spring:message
					code="psi_product_email" /></a></li>
	</ul>
	<blockquote>
		<p style="font-size: 14px">平台负责人</p>
	</blockquote>
	<table id="countryTable"
		class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 10%; text-align: center; vertical-align: middle;">
					<spring:message code="amazon_sales_country" />
				</th>
				<c:forEach items="${countryList}" var="country" varStatus="i">
					<th style="width: 10%">${fns:getDictLabel(country,'platform','')}</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td style="text-align: center; vertical-align: middle;">
					<spring:message code="psi_product_charge" /> 
					<input class="groupId" type="hidden" value="0" />
				</td>
				<c:forEach items="${countryList}" var="country" varStatus="i">
					<td>
						<shiro:hasPermission name="psi:product:productGroupUserEdit">
							<a href="#" class="groupUser${country }" data-type="select" data-pk="1" data-title="Enter User "
								data-value="${fn:split(userMap['0'][country],',')['0']}"></a>
						</shiro:hasPermission> 
						<shiro:lacksPermission name="psi:product:productGroupUserEdit">
							${fn:split(userMap['0'][country],',')[1]}
						</shiro:lacksPermission></td>
				</c:forEach>
			</tr>
		</tbody>
	</table>
	
	<blockquote>
		<p style="font-size: 14px">平台产品线负责人</p>
	</blockquote>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead>
		<tr>
			 <th style="width:15%;text-align: center;vertical-align: middle;"><spring:message code="psi_product_line"/></th>
			 <th style="width:15%;text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_country" /></th>
			 <th style="width:60%;text-align: center;vertical-align: middle;"><spring:message code="psi_product_charge"/></th>
			 <th style="width:10%;text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
		</tr>
		</thead>
		<tbody>
			<c:forEach items="${lineList}" var="line">
			      <tr>
			         <td rowspan='9' style="text-align: center;vertical-align: middle;">${line.name}</td>
					 <c:forEach items="${countryList}" var="country">
					 	<c:if test="${'de' ne country }"><tr></c:if>
				         <td style="text-align: center;vertical-align: middle;">
				         	${fns:getDictLabel(country,'platform','')}
				         </td>
				        <td>
				             <c:set var="id" value=""/>
				             <c:set var="list" value="" />
				             <select name="userId" multiple class="multiSelect required"  style="width:80%">
				               <c:if test="${not empty lineSalesMap[line.id][country]}">
				                  <c:forEach items="${lineSalesMap[line.id][country]}" var="groupUser">
				                      <c:set var='id' value='${groupUser.id}'/>
				                      <c:set var="list" value="${list},${groupUser.responsible}" />
				                      <option value="${groupUser.responsible}" selected>${groupUser.name}</option>		
				                  </c:forEach>
				               </c:if>
					           <c:forEach items="${shelvesMap[country]}" var="user">
					              <c:if test="${!fn:contains(list,fn:split(user,',')[0]) }">
					                 <option value="${fn:split(user,',')[0]}">${fn:split(user,',')[1]}</option>
					              </c:if>
							   </c:forEach>
							</select>
				         </td>
				         <td style="text-align: center;vertical-align: middle;">
					         <shiro:hasPermission name="psi:product:productGroupUserEdit">
						         <input type='hidden' name='itemId'  value="${id}"/> 
						         <input type='hidden' name='lineId'  value="${line.id}"/> 
						         <input type='hidden' name='country'  value="${country }"/> 
						         <a class='btn btn-primary save'>保存</a>
					         </shiro:hasPermission>
				         </td>
				         </tr>
					</c:forEach>
			      </tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
