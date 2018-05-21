<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='psi_purchase_order'/>--<spring:message code='sys_but_add'/></title>
	<meta name="decorator" content="default"/>
	
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchasePlan/">新品采购计划列表</a></li>
		<li class="active"><a href="#">查看新品采购计划</a></li>
	</ul>
	<br/>
	
	<form:form id="inputForm" modelAttribute="purchasePlan" action="" method="post" class="form-horizontal" enctype="multipart/form-data">
		<table class="table table-striped table-bordered" style="width: 100%">
            <tbody>
                <tr class="info">
                    <td colspan="8" class="tdTitle">
                        <i class="icon-play"></i>
                                                             基本信息
                    </td>
                </tr>
                <tr>
                    <td style="width:80px"><strong>产品定位</strong></td>
                    <td>${fns:getDictLabel(purchasePlan.productPosition,'product_position','')}</td>
                </tr>
                <tr>
                    <td style="width:80px"><strong>销售计划</strong></td>
                    <td><c:if test="${not empty purchasePlan.attFilePath}"><a href="${ctx}/psi/purchasePlan/download?fileName=/${purchasePlan.attFilePath}&productName=${purchasePlan.productName}">查看</a></c:if></td>
                </tr>
                <tr>
                    <td style="width:80px"><strong>备注</strong></td>
                    <td>${purchasePlan.remark}</td>
                </tr>
            </tbody>
          </table>
		
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			 <tr class="info">
	                    <td colspan="10" class="tdTitle">
	                        <i class="icon-play"></i>
	                         <spring:message code='psi_product_productInfo'/>
	                    </td>
	         </tr>
			 <tr>
				   <th style="width: 15%"><spring:message code='psi_product_name'/></th>
				   <th style="width: 5%"><spring:message code='sys_label_country'/></th>
				   <th style="width: 5%">MOQ</th>
				   <th style="width: 5%">装箱数</th>
				   <th style="width: 5%">申请数量</th>
				   <th style="width: 5%">初审数量</th>
				   <th style="width: 5%">终极数量</th>
				   <th style="width: 10%">备注</th>
				   <th style="width: 10%">初审备注</th>
				   <th style="width: 15%">终极备注</th>
				   
			 </tr>
			 <c:if test="${not empty purchasePlan.id}">
				<c:forEach items="${purchasePlan.items}" var="item" >
					<tr>
						<td>
							<input type="hidden" name="id" value="${item.id}" />
							<input type="hidden" name="plan.id" value="${item.plan.id}" />
							<input type="hidden" name="colorCode" value="${item.colorCode}"/>
							<input type="hidden" name="countryCode" value="${item.countryCode}"/>
							<input type="hidden" name="productName" value="${item.productName}"/>
							<input type="hidden" name="product.id" value="${item.product.id}"/>
							<input type="hidden" name="remark" value="${item.remark}"/>
							<input type="hidden" name="remarkReview" value="${item.remarkReview}"/>
							<input type="hidden" name="quantity" value="${item.quantity}"/>
							<input type="hidden" name="quantityReview" value="${item.quantityReview}"/>
							${item.productNameColor}
						</td>
						<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
						<td>${item.product.minOrderPlaced}</td>
						<td>${item.product.packQuantity}</td>
						<td>${item.quantity}</td>
						<td>${item.quantityReview}</td>
						<td>${item.quantityBossReview}</td>
						<td>${item.remark}</td>
						<td>${item.remarkReview}</td>
						<td>${item.remarkBossReview}</td>
					</tr>
			   </c:forEach>
		   </c:if>
	</table>
	</form:form>
</body>
</html>
