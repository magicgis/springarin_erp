<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件订单付款管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#paymentSta,#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.updateDate=$("input[name='updateDate']").val();
				window.location.href = "${ctx}/psi/lcPsiPartsPayment/expPaymentTotal?"+$.param(params);
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
		<li><a href="${ctx}/psi/psiPartsPayment">配件订单付款列表</a></li>
		<li><a href="${ctx}/psi/psiPartsPayment/add">配件订单付款添加</a></li>
		<li class="active"><a href="${ctx}/psi/lcPsiPartsPayment">(理诚)配件订单付款列表</a></li>
		<li><a href="${ctx}/psi/lcPsiPartsPayment/add">(理诚)配件订单付款添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiPartsPayment" action="${ctx}/psi/lcPsiPartsPayment/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${lcPsiPartsPayment.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${lcPsiPartsPayment.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
		<label>付款单号 ：</label> <form:input path="paymentNo" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
		</div>
			<div style="height: 40px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${lcPsiPartsPayment.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${psiPartsPayment.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>付款状态：</label>
			<form:select path="paymentSta" style="width: 200px" id="paymentSta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${psiPartsPayment.paymentSta eq '0' ?'selected':''} >草稿</option>
				<option value="1" ${psiPartsPayment.paymentSta eq '1' ?'selected':''} >申请</option>
				<option value="2" ${psiPartsPayment.paymentSta eq '2' ?'selected':''} >已付款</option>
				<option value="3" ${psiPartsPayment.paymentSta eq '3' ?'selected':''} >已取消</option>
			</form:select>
			</div>
		</div>
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width:5%">序号</th><th style="width:15%">付款单号</th><th style="width:10%">付款总金额(${partsPayment.currencyType})</th>
				<th style="width:10%">付款状态</th><th style="width:10%">创建时间</th><th style="width:10%">创建人</th>
				<th style="width:15%">备注<th style="width:35%">操作</th>
			</tr>
		</thead>
	
		<tbody>
		<c:forEach items="${page.list}" var="partsPayment">
			<tr id="${partsPayment.id}">
				<td style="text-align:center">${partsPayment.id}</td>
				<td><a  href="${ctx}/psi/lcPsiPartsPayment/view?id=${partsPayment.id}">${partsPayment.paymentNo}</a></td>
				<td class="paymentTotal">${partsPayment.paymentAmountTotal}</td>
				<td>
					<c:if test="${partsPayment.paymentSta eq '0'}"><span class="label  label-important">草稿</span></c:if>
					<c:if test="${partsPayment.paymentSta eq '1'}"><span class="label  label-warning">申请</span></c:if>
					<c:if test="${partsPayment.paymentSta eq '2'}"><span class="label  label-success">已付款</span></c:if>
					<c:if test="${partsPayment.paymentSta eq '3'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${partsPayment.createDate}"/></td>
				<td>${partsPayment.createUser.name}</td>
				<td>${partsPayment.remark}</td>
				<td>
				<input type="hidden" value="${partsPayment.id}"/>
				<a class="btn btn-small btn-info open">概要</a>
				<c:if test="${partsPayment.paymentSta eq '0' }">
					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsPayment/edit?id=${partsPayment.id}">编辑</a>
					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsPayment/cancel?id=${partsPayment.id}&paymentSta=6" onclick="return confirmx('确认要取消该采购付款吗？', this.href)">取消</a>
				</c:if>
				<c:if test="${partsPayment.paymentSta eq '1' }">
					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsPayment/sure?id=${partsPayment.id}">确认</a>
					<div class="btn-group">
					   <button type="button" class="btn btn-small">取消</button>
					   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
					      <span class="caret"></span>
					      <span class="sr-only"></span>
					   </button>
					   <ul class="dropdown-menu" >
					      <li><a href="${ctx}/psi/lcPsiPartsPayment/cancel?id=${partsPayment.id}&paymentSta=4" onclick="return confirmx('确认要取消该配件付款成草稿吗？', this.href)">取消成草稿</a></li>
					      <li><a href="${ctx}/psi/lcPsiPartsPayment/cancel?id=${partsPayment.id}&paymentSta=5" onclick="return confirmx('确认要取消该配件付款吗？', this.href)">取消</a></li>
					   </ul>
					</div>
				</c:if>
				<c:if test="${partsPayment.paymentSta eq '2' }">
					<a class="btn btn-small" target="_blank"  href="<c:url value='/data/site${partsPayment.attchmentPath}' />">查看水单</a>
				</c:if>
					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsPayment/print?id=${partsPayment.id}">打印付款单</a>
				</td>
			</tr>
			
			<c:if test="${fn:length(partsPayment.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${partsPayment.id}"><td></td><td>付款类型</td><td>单号</td><td>付款总额</td><td>备注</td><td colspan="4"></td></tr>
				<c:forEach items="${partsPayment.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${item.psiPartsPayment.id}" >
					<td></td>
					<td>${item.paymentType eq '0'?'订金':'尾款'}</td>
					<td><a target="_blank" href="${ctx}/psi/${item.paymentType eq '0'?'psiPartsOrder':'psiPartsDelivery' }/view?id=${item.paymentType eq '0'?item.order.id:item.ladingBill.id }">${item.billNo}</a></td>
					<td>${item.paymentAmount}</td>
					<td>${item.remark}</td><td colspan="4"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
