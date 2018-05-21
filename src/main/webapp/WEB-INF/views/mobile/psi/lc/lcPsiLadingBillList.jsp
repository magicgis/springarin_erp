<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货单管理</title>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.controls {
			display:none !important;
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
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find(".ladingKey").val();
				$("*[name='"+className+"']").toggle();
			});
			
			var message = "${message}";
			if(message != null && message!=''){
				top.$.jBox.tip(message,"info",{timeout:5000});
			}
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return f.toFixed(3);  
	     };
		
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
<div data-role="page" id="home">
	<jsp:include page="../../sys/headDiv.jsp"></jsp:include>
	<div data-role="content">
	<%--<form:form id="searchForm" modelAttribute="lcPsiLadingBill" action="${ctx}/psi/lcPsiLadingBill/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>    
		<div style="height: 100px;line-height: 40px">
			<label>单号/产品：</label>
			<form:input path="billNo" htmlEscape="false" maxlength="50" class="input-small" value="${lcPsiLadingBill.billNo }" style="width:40%"/>
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</form:form> --%>
	<form id="searchForm" method="post" action="${ctx}/psi/lcPsiLadingBill/" data-ajax="false">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/> 
      	<div data-role="fieldcontain" style="margin-bottom:5px">
	        <input type="text" name="billNo" id="billNo" value="${lcPsiLadingBill.billNo }" placeholder="单号/产品">    
      	</div>
      	<input id="btnSubmit" class="btn" type="submit" value="查询" style="width:100%"/>
    </form>
    <div style="overflow:auto">
	<table id="contentTable"  class="table table-striped table-bordered table-condensed">   
		<thead>
		<tr>
			<th style="width:10%">收货单号</th>
			<th style="width:10%">产品名称</th>
			<th style="width:5%">总数量</th>
			<th style="width:5%">供应商</th>
			<th style="width:6%">创建人/产品经理</th>
			<th style="width:5%">品检状态</th>
			<th style="width:15%">操作</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="lcPsiLadingBill">
		<c:forEach items="${lcPsiLadingBill.tempLadingBills}" var="ladingBill" varStatus="i">
		<tr>
			<c:choose>
				<c:when test="${i.index eq '0' }">
				<td>${ladingBill.billNo}</td>
				</c:when>
				<c:otherwise>
					<td></td>
				</c:otherwise>
			</c:choose>
			<td>${ladingBill.tempProductNameColor}</td>
			<td>${ladingBill.ladingTotal}</td>
			<td>${ladingBill.supplier.nikename}</td>
			<td>${ladingBill.createUser.name}/
				<c:set var="oldUserId" value="${productMangerIdMap[ladingBill.tempProductName]}" />
				${fns:getUserById(oldUserId).name}&nbsp;
			</td>
			<td>
				<c:if test="${empty firstTestMap[ladingBill.tempKey] && ladingBill.billSta eq '0'}">
					<span class="label label-info">待质检</span>
				</c:if>
				<c:if test="${not empty firstTestMap[ladingBill.tempKey]}">
				<c:forEach items="${firstTestMap[ladingBill.tempKey]}" var="testInfo">
					<c:set  var="testSta" value="${fn:split(fn:split(testInfo,',')[1],'_')[0]}" />
					<c:set  var="isOk" value="${fn:split(fn:split(testInfo,',')[1],'_')[1]}" />
					<c:set  var="dealWay" value="${fn:split(fn:split(testInfo,',')[1],'_')[2]}" />
					<c:if test="${testSta eq '0'}"><span class="label label-important">${fn:split(testInfo,',')[0]}待品检申请</span></c:if>
					<c:if test="${testSta eq '3' && dealWay eq '9'}"><span class='label label-warning'>${fn:split(testInfo,',')[0]}待品质主管审核</span></c:if>
					<c:if test="${testSta eq '3' && dealWay eq '8'}"><span class='label label-info'>${fn:split(testInfo,',')[0]}待各方协商</span></c:if>
					<c:if test="${testSta eq '5' && isOk eq '0'}"><span class='label label-important'>${fn:split(testInfo,',')[0]}不合格</span></c:if>
					<c:if test="${testSta eq '5' && isOk eq '1'}"><span class='label label-success'>${fn:split(testInfo,',')[0]}合格</span></c:if>
					<c:if test="${testSta eq '5' && isOk eq '2'}"><span class='label label-warning'>${fn:split(testInfo,',')[0]}部分合格</span></c:if>
					<c:if test="${testSta eq '8'}"><span class='label label-inverse'>${fn:split(testInfo,',')[0]}已取消</span></c:if>
				</c:forEach>
				<br/>
					<c:if test="${fn:split(testReceivedMap[ladingBill.tempKey],',')[1]>ladingBill.ladingReceivedTotal}"><span style="color:red">(可收货${fn:split(testReceivedMap[ladingBill.tempKey],',')[1]-ladingBill.ladingReceivedTotal})</span></c:if>
					<c:if test="${fn:split(testReceivedMap[ladingBill.tempKey],',')[0]<ladingBill.ladingTotal}"><span style="color:red">(待质检${ladingBill.ladingTotal-fn:split(testReceivedMap[ladingBill.tempKey],',')[0]})</span></c:if>
				</c:if>
			</td>
			<td>
				<input type="hidden" value="${ladingBill.id}" class="ladingBillId"/>
				<input type="hidden" value="${ladingBill.billNo},${ladingBill.tempProductNameColor}" class='ladingKey'/>
				<a class="btn btn-small btn-info open" style="white-space: nowrap;">概要</a>
				<c:if test="${ladingBill.billSta eq '0'||ladingBill.billSta eq '5'}">
					<shiro:hasPermission name="psi:ladingBill:qualityTest">
						<c:if test="${empty testReceivedMap[ladingBill.tempKey]||ladingBill.ladingTotal>fn:split(testReceivedMap[ladingBill.tempKey],',')[0]}">
							<br/><a class="btn btn-small" style="white-space: nowrap;margin-top:4px"  href="${ctx}/psi/lcPsiLadingBill/qualityTest?ladingId=${ladingBill.id}&supplierId=${ladingBill.supplier.id}&ladingBillNo=${ladingBill.billNo}&productName=${ladingBill.tempProductName}&color=${ladingBill.tempColor}">填写品检记录</a>
						</c:if>
					</shiro:hasPermission>
				</c:if>
				<c:if test="${not empty testReceivedMap[ladingBill.tempKey]}">  
					<br/><a class="btn btn-small" style="white-space: nowrap;margin-top:4px"  href="${ctx}/psi/lcPsiLadingBill/qualityView?ladingId=${ladingBill.id}&ladingBillNo=${ladingBill.billNo}&productName=${ladingBill.tempProductName}&color=${ladingBill.tempColor}">品检单</a>
				</c:if>
			</td>
			</tr>
			<c:if test="${fn:length(ladingBill.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${ladingBill.billNo},${ladingBill.tempProductNameColor}">
				<td>订单号[SN]</td><td>收货总数</td><td>线下数</td><td>国家</td><td>颜色</td><td>备品数</td><td colspan="5">sku</td></tr>
				<c:forEach items="${ladingBill.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${ladingBill.billNo},${ladingBill.tempProductNameColor}">
					<td><a target="_blank" href="${ctx}/psi/lcPurchaseOrder/view?id=${item.purchaseOrderItem.purchaseOrder.id}">
					${item.purchaseOrderItem.purchaseOrder.orderNo}[${item.purchaseOrderItem.purchaseOrder.snCode}]</a></td>
					<td>${item.quantityLading}</td>
					<td>${item.quantityOffLading}</td>
					<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
					<td><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;"  target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td>
					<td>${item.quantitySpares}</td>
					<td colspan="5">${item.sku }&nbsp;条码:${fnskuMap[item.sku]}</td>
					</tr>
				</c:forEach>
			</c:if>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
	</div>
		<div class="pagination">${page}</div>
	</div>
		<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
</div>
</body>
</html>
