<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货单管理</title>
	<meta name="decorator" content="default"/>
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
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.purchaseDate=$("input[name='purchaseDate']").val();
				params.billNo=$("input[name='billNo']").val();
				params.billSta=$("#billSta").val();
				params['supplier.id']=$("#supplier").val();
				window.location.href = "${ctx}/psi/psiLadingBill/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:30000});
			});
			

			$(".checkPros").click(function(e){
				var checkedStatus = this.checked;
				var name = $(this).parent().parent().find("td:last").find("input[type='hidden']").val();
				$("*[name='"+name+"'] :checkbox").each(function(){
					this.checked = checkedStatus;
				});
			});
			
			var totleV = 0 ;
			var totleW = 0 ;
			$(".volume").each(function(){
				totleV =totleV+parseFloat($(this).text());
			});
			$(".weight").each(function(){
				totleW =totleW+parseFloat($(this).text());
			});
			$("#totleV").append("<b>"+toDecimal(totleV)+"</b>");
			$("#totleW").append("<b>"+toDecimal(totleW)+"</b>");
			
			$("#count").click(function(){
				var totleV = 0 ;
				var totleW = 0 ;
				$(":checked").parent().parent().find(".itemVolume").each(function(){
					totleV =totleV+parseFloat($(this).text());
				});
				$(":checked").parent().parent().find(".itemWeight").each(function(){
					totleW =totleW+parseFloat($(this).text());
				});
				top.$.jBox.alert('你勾选的货品装箱体积为:'+toDecimal(totleV)+'m³;毛重为:'+toDecimal(totleW)+'kg', '计算结果');
			});
			
			$("#billSta,#supplier").change(function(){
				$("#searchForm").submit();
			});
			
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*1000)/1000;  
	            return f;  
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
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/lcPsiLadingBill/">收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/">(理诚)收货单列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiLadingBill" action="${ctx}/psi/psiLadingBill/" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>    
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiLadingBill.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${psiLadingBill.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			
			<label>收货单编号/产品名称：</label>
			<form:input path="billNo" htmlEscape="false" maxlength="50" class="input-small" value="${psiLadingBill.billNo }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
			<div style="height: 40px;">
				
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${psiLadingBill.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${psiLadingBill.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>收货单状态：</label>
			<form:select path="billSta" style="width: 200px" id="billSta">
				<option value="" >全部(非取消)</option>
				<option value="0" ${psiLadingBill.billSta eq '0' ?'selected':''} >申请</option>
				<option value="1" ${psiLadingBill.billSta eq '1' ?'selected':''} >已确认</option>
				<option value="5" ${psiLadingBill.billSta eq '5' ?'selected':''} >部分确认</option>
				<option value="2" ${psiLadingBill.billSta eq '2' ?'selected':''} >已取消</option>
			</form:select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="count" class="btn btn-primary" type="button" value="计算勾选货品装箱体积和毛重"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			</div>
		</div>
		
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">   
		<thead><tr>
		<th width="2%"><input type="checkbox" id="checkAll" /></th>
		<th style="width:3%">序号</th><th style="width:10%">收货单号</th><th style="width:10%">产品名称</th><th style="width:5%">总数量</th>
		<th style="width:6%">供应商</th><th style="width:10%">创建人</th><th style="width:5%">体积(m³)</th><th style="width:5%">毛重(kg)</th>
		<th style="width:10%">付款状态</th><th style="width:5%">状态</th><th >操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiLadingBill">
		<c:forEach items="${psiLadingBill.tempLadingBills}" var="ladingBill" varStatus="i">
		<tr>
		<c:choose>
			<c:when test="${ladingBill.billSta eq '1' && empty ladingBill.attchmentPath}"><tr style="background-color:#FF9797"></c:when>
			<c:otherwise><tr></c:otherwise>
		</c:choose>
			<td><input type="checkbox" class="checkPros" /></td>
			<c:choose>
				<c:when test="${i.index eq '0' }">
				<td>${ladingBill.id}</td>
				<td><a href="${ctx}/psi/psiLadingBill/view?id=${ladingBill.id}">${ladingBill.billNo}</a></td>
				</c:when>
				<c:otherwise>
					<td></td><td></td>
				</c:otherwise>
			</c:choose>
			<td>${ladingBill.tempProductName}</td>
			<td>${ladingBill.ladingTotal}</td>
			<td>${ladingBill.supplier.nikename}</td>
			<td>${ladingBill.createUser.name}</td>
			<td class="volume"><fmt:formatNumber value="${ladingBill.volume}" minFractionDigits="3"/></td>
			<td class="weight"><fmt:formatNumber value="${ladingBill.weight}" minFractionDigits="3"/></td>
			<td>
			<fmt:parseNumber value='${ladingBill.totalPaymentAmount}' var="totalPaymentAmount"/>
			<fmt:parseNumber value='${ladingBill.totalPaymentPreAmount}' var="totalPaymentPreAmount"/>
			<fmt:parseNumber value='${ladingBill.totalAmount}' var="totalAmount"/>
			<c:choose>
			<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount==0}"><span class='label label-important'>未申请</span></c:when>
			<c:when test="${totalPaymentAmount==0&&totalPaymentPreAmount>0}"><span class='label label-warning'>已申请</span></c:when>
			<c:when test="${totalPaymentAmount>0&&totalPaymentAmount<totalAmount}"><span class='label label-info'>部分付款</span></c:when>
			<c:otherwise >
			<span class='label label-success'>已付款</span>
			</c:otherwise>
			</c:choose>
			
			</td>
			<td>
			<c:if test="${ladingBill.billSta eq '0'}"><span class="label label-important">申请</span></c:if>
			<c:if test="${ladingBill.billSta eq '1'}"><span class="label  label-success">已确认</span></c:if>
			<c:if test="${ladingBill.billSta eq '5'}"><span class="label  label-warnning">部分确认</span></c:if>
			<c:if test="${ladingBill.billSta eq '2'}"><span class="label  label-inverse">已取消</span></c:if>
			</td>
			<td>
			<input type="hidden" value="${ladingBill.billNo},${ladingBill.tempProductName}"/>
				<a class="btn btn-small btn-info open">概要</a>
				<c:if test="${i.index==0}">
					  <c:if test="${ladingBill.billSta eq '0' || ladingBill.billSta eq '5'}">
							<div class="btn-group">
							   <button type="button" class="btn btn-small">更改</button>
							   <button type="button" class="btn btn-small dropdown-toggle"  data-toggle="dropdown">
							      <span class="caret"></span>
							      <span class="sr-only"></span>
							   </button>
							    <ul class="dropdown-menu" >
									<shiro:hasPermission name="psi:ladingBill:sure">
										<li><a  href="${ctx}/psi/psiLadingBill/sure?id=${ladingBill.id}">确认</a></li>
									</shiro:hasPermission>
									<shiro:hasPermission name="psi:ladingBill:edit">
										<c:if test="${totalPaymentPreAmount+totalPaymentAmount ==0&&ladingBill.billSta eq '0' &&fns:getUser().id eq ladingBill.createUser.id}">
											<li class="divider"></li>
											<li><a  href="${ctx}/psi/psiLadingBill/edit?id=${ladingBill.id}">编辑</a></li>
											<li class="divider"></li>
											<li><a  href="${ctx}/psi/psiLadingBill/cancel?id=${ladingBill.id}" onclick="return confirmx('确认要取消该收货单吗？', this.href)">取消</a></li>
										</c:if>
									</shiro:hasPermission>
								 </ul>
							
							</div>
						</c:if>
	   				<a target="_blank" class="btn btn-small"  href="${ctx}/psi/psiLadingBill/print?id=${ladingBill.id}">PDF</a>&nbsp;&nbsp;
	   				<c:if test="${empty ladingBill.attchmentPath && ladingBill.billSta eq '1'}">
	   					<a class="btn btn-small"  href="${ctx}/psi/psiLadingBill/uploadPi?id=${ladingBill.id}">上传凭证</a>&nbsp;&nbsp;
	   				</c:if>
				</c:if>
			</td>
			</tr>
			<c:if test="${fn:length(ladingBill.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${ladingBill.billNo},${ladingBill.tempProductName}">
				<td></td><td></td><td>订单号[SN]</td><td>收货总数</td><td>线下数</td><td>国家</td><td>颜色</td><td>体积(m³)</td><td>毛重(kg)</td><td>备品数</td><td colspan="2">sku</td></tr>
				<c:forEach items="${ladingBill.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${ladingBill.billNo},${ladingBill.tempProductName}">
					<td><input type="checkbox" class="checkPro" /></td>
					<td></td><td><a target="_blank" href="${ctx}/psi/purchaseOrder/view?id=${item.purchaseOrderItem.purchaseOrder.id}">
					${item.purchaseOrderItem.purchaseOrder.orderNo}[${item.purchaseOrderItem.purchaseOrder.snCode}]</a></td>
					<td>${item.quantityLading}</td>
					<td>${item.quantityOffLading}</td>
					<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
					<td><a class="btn btn-warning" style="height:16px;width:20px;padding:0px;"  target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}${item.colorCode !=''?'_':''}${item.colorCode}"><span class="icon-search"></span></a>&nbsp;${item.colorCode}</td>
					<td class="itemVolume"><fmt:formatNumber value="${item.volume}" minFractionDigits="3"/></td>
					<td class="itemWeight"><fmt:formatNumber value="${item.weight}" minFractionDigits="3"/></td>
					<td>${item.quantitySpares}</td>
					<td colspan="3">${item.sku }&nbsp;条码:${fnskuMap[item.sku]}</td>
					</tr>
				</c:forEach>
			</c:if>
			</c:forEach>
		</c:forEach>
		<tr>
			<td></td>
			<td>合计</td>
			<td colspan="7"></td>
			<td id="totleV"></td>
			<td id="totleW"></td>
			<td colspan="5"></td>
		</tr>
		</tbody>
	</table>   
	<div class="pagination">${page}</div>
</body>
</html>
