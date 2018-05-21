<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件收货管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#supplier,#billSta").on("click",function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.updateDate=$("input[name='updateDate']").val();
				params.billNo=$("input[name='billNo']").val();
				params.billSta=$("#billSta").val();
				params['supplier.id']=$("#supplier").val();
				window.location.href = "${ctx}/psi/lcPsiPartsDelivery/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
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
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiPartsDelivery/">配件收货列表</a></li>
		<li class="active"><a href="${ctx}/psi/lcPsiPartsDelivery/">(理诚)配件收货列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiPartsDelivery" action="${ctx}/psi/lcPsiPartsDelivery/" method="post" class="breadcrumb form-search" style="height:80px">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label>创建日期：</label>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiPartsDelivery.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>&nbsp;&nbsp;-&nbsp;&nbsp;
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${psiPartsDelivery.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
				
				<label>供应商：</label>
				<select style="width:150px;" id="supplier" name="supplier.id">
					<option value="" ${psiPartsDelivery.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
					<c:forEach items="${suppliers}" var="supplier" varStatus="i">
						 <option value='${supplier.id}' ${supplier.id eq  psiPartsDelivery.supplier.id?'selected':''}>${supplier.nikename}</option>;
					</c:forEach>
				</select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<label>订单状态：</label>
				<form:select path="billSta" style="width: 150px" id="billSta">
						<option value="" >全部(非取消)</option>
						<option value="0" ${psiPartsDelivery.billSta eq '0' ?'selected':''} >申请</option>
						<option value="1" ${psiPartsDelivery.billSta eq '1' ?'selected':''} >已确认</option>
						<option value="2" ${psiPartsDelivery.billSta eq '2' ?'selected':''} >已取消</option>    
					</form:select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
			<div style="height: 40px;">
			<label>收货单号 ：</label>
			<input name="billNo" value="${psiPartsDelivery.billNo}" type="text" style="width:150px"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>配件名称 ：</label>
			<input name="remark" value="${psiPartsDelivery.remark}" type="text" style="width:150px"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
		<tr>
		<th style="width:3%">序号</th><th style="width:10%">收货单号</th><th style="width:10%">供应商</th><th style="width:5%">总数量</th>
		<th style="width:5%">创建人</th><th style="width:10%">付款状态</th><th style="width:5%">状态</th><th >操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiPartsDelivery" varStatus="i">
			<tr>
				<td>${psiPartsDelivery.id}</td>
				<td><a href="${ctx}/psi/lcPsiPartsDelivery/view?id=${psiPartsDelivery.id}">${psiPartsDelivery.billNo}</a></td>
				<td>${psiPartsDelivery.supplier.nikename}</td>
				<td>${psiPartsDelivery.ladingTotal}</td>
				<td>${psiPartsDelivery.createUser.name}</td>
				<td>
				<c:choose>
				<c:when test="${psiPartsDelivery.totalPaymentAmount==0&&psiPartsDelivery.totalPaymentPreAmount==0}"><span class='label label-important'>未申请</span></c:when>
				<c:when test="${psiPartsDelivery.totalPaymentAmount==0&&psiPartsDelivery.totalPaymentPreAmount>0}"><span class='label label-warning'>已申请</span></c:when>
				<c:when test="${psiPartsDelivery.totalPaymentAmount>0&&psiPartsDelivery.totalPaymentAmount<psiPartsDelivery.totalAmount}"><span class='label label-info'>部分付款</span></c:when>
				<c:otherwise >
				<span class='label label-success'>已付款</span>
				</c:otherwise>
				</c:choose>
				</td>
				<td>
				<c:if test="${psiPartsDelivery.billSta eq '0'}"><span class="label label-important">申请</span></c:if>
				<c:if test="${psiPartsDelivery.billSta eq '1'}"><span class="label  label-success">已确认</span></c:if>
				<c:if test="${psiPartsDelivery.billSta eq '2'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td>
				<input type="hidden" value="${psiPartsDelivery.billNo}"/>
					<a class="btn btn-small btn-info open">概要</a>
						  <c:if test="${psiPartsDelivery.billSta eq '0' }">
								<div class="btn-group">
								   <button type="button" class="btn btn-small">更改</button>
								   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
								      <span class="caret"></span>
								      <span class="sr-only"></span>
								   </button>
								    <ul class="dropdown-menu" >
										<li><a  href="${ctx}/psi/lcPsiPartsDelivery/sure?id=${psiPartsDelivery.id}">确认</a></li>
										<c:if test="${psiPartsDelivery.totalPaymentPreAmount+psiPartsDelivery.totalPaymentAmount ==0}">
											<li class="divider"></li>
											<li><a  href="${ctx}/psi/lcPsiPartsDelivery/cancel?id=${psiPartsDelivery.id}" onclick="return confirmx('确认要取消该收货单吗？', this.href)">取消</a></li>
										</c:if>
									 </ul>
								</div>
							</c:if>
		   				<a target="_blank" class="btn btn-small"  href="${ctx}/psi/lcPsiPartsDelivery/print?id=${psiPartsDelivery.id}">PDF</a>&nbsp;&nbsp;
		   				<c:if test="${empty psiPartsDelivery.attchmentPath}">
		   					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsDelivery/upload?id=${psiPartsDelivery.id}">上传凭证</a>&nbsp;&nbsp;
		   				</c:if>
				</td>
				</tr>
				<c:if test="${fn:length(psiPartsDelivery.items)>0}">
					<tr style="background-color:#D2E9FF;display: none" name="${psiPartsDelivery.billNo}">
					<td></td><td>配件订单号</td><td>配件名称</td><td>收货数</td><td colspan="5"></td></tr>
					<c:forEach items="${psiPartsDelivery.items}" var="item">
						<tr style="background-color:#D2E9FF;display: none" name="${psiPartsDelivery.billNo}">
						<td></td><td><a target="_blank" href="${ctx}/psi/lcPsiPartsOrder/view?id=${item.partsOrderItem.partsOrder.id}">
						${item.partsOrderItem.partsOrder.partsOrderNo}</a></td>
						<td>${item.partsName}</td>
						<td>${item.quantityLading}</td>
						<td colspan="5"></td>
						</tr>
					</c:forEach>
				</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
