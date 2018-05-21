<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件订单管理</title>
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
			
			$("#partsType,#supplier,#orderSta").on("click",function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.updateDate=$("input[name='updateDate']").val();
				params.partsOrderNo=$("input[name='partsOrderNo']").val();
				params.orderSta=$("#orderSta").val();
				params['supplier.id']=$("#supplier").val();
				window.location.href = "${ctx}/psi/lcPsiPartsOrder/expOrder?"+$.param(params);
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
			
			$(".checkPros").click(function(e){
				var checkedStatus = this.checked;
				var name = $(this).parent().parent().find("td:last").find("input[type='hidden']").val();
				$("*[name='"+name+"'] :checkbox").each(function(){
					this.checked = checkedStatus;
				});
			});

			
			$("#batchReceive").click(function(){
				var ids = "";
				var supplierId ="";
				var currencyType="";
				var flag=1;
				var curFlag=1;
				var staFlag=1;
				var isChecked =false;
				
				$(".checkPro").each(function(){
					if(this.checked){
						isChecked=true;
						return ;
					};
				});
				
				if(!isChecked){
					top.$.jBox.tip("必须选中一种收货项！", 'info',{timeout:3000});
					return false;
				}
				
				$(":checked").parent().parent().find(".orderSta").each(function(){
					if($(this).val()!="1"&&$(this).val()!="3"){
						staFlag=2;
						return;
					}
				});
				
				if(staFlag==2){
					top.$.jBox.tip("只能选生产和部分收货状态的订单","info",{timeout:3000});
					return false;
				}
				
				
				
				//随便取一个供应商
				$(":checked").parent().parent().find(".supplierId").each(function(){
					var $parent =$(this).parent();
					if($(this).val()!=''){
						supplierId=$(this).val();
						currencyType=$parent.find(".currencyType").val();
						return;
					}
				});
				
			
				var canReceived =0;
				
				$(":checked").parent().parent().find(".orderItemId").each(function(){
					var curSupplierId=$(this).parent().find(".supplierId").val();
					var curCurrencyType=$(this).parent().find(".currencyType").val();
					if(curSupplierId!=supplierId){
						flag=2;
						return;
					}
					if(curCurrencyType!=currencyType){
						curFlag=2;
						return;
					}
					var tr =$(this).parent().parent();
					var quantityCanReceived = tr.find(".quantityCanReceived").val();
					canReceived=canReceived+parseInt(quantityCanReceived);
					ids=ids+$(this).val()+",";
				});
				
							
				if(flag==2){
					top.$.jBox.tip("只能对一个供应商进行收货","info",{timeout:3000});
					return false;
				}
				
				if(curFlag==2){
					top.$.jBox.tip("只能对同一币种进行收货","info",{timeout:3000});
					return false;
				}
				
				if(canReceived==0){
					top.$.jBox.tip("所选产品可收货数量为0,请重新选择！","info",{timeout:3000});
					return false;
				}
				
				var params = {};
				params['supplierId'] = supplierId;
				params['currencyType']=currencyType;   
				params['orderItemIds'] = ids;
				window.location.href = "${ctx}/psi/lcPsiPartsDelivery/batchReceive?"+$.param(params);
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
	
	<style type="text/css">
		.spanexr{ float:right;min-height:20px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
		#paymentModal table td {
		     text-align:center;
		     height:20px; 
		     line-height:20px; 
		}
		#paymentModal table th {
		     text-align:center;
		      height:20px; 
		      line-height:20px; 
		}
	</style>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/lcPsiPartsOrder/">(理诚)配件订单列表</a></li>
		<li><a href="${ctx}/psi/lcPsiPartsOrder/add">(理诚)新增配件订单</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiPartsOrder" action="${ctx}/psi/lcPsiPartsOrder/" method="post" class="breadcrumb form-search" cssStyle="height:80px">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label>创建日期：</label>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiPartsOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>&nbsp;&nbsp;-&nbsp;&nbsp;
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${psiPartsOrder.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
				
				<label>供应商：</label>
				<select style="width:150px;" id="supplier" name="supplier.id">
					<option value="" ${psiPartsOrder.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
					<c:forEach items="${suppliers}" var="supplier" varStatus="i">
						 <option value='${supplier.id}' ${supplier.id eq  psiPartsOrder.supplier.id?'selected':''}>${supplier.nikename}</option>;
					</c:forEach>
				</select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<label>订单状态：</label>
				<form:select path="orderSta" style="width: 150px" id="orderSta">
						<option value="" >全部(非取消)</option>
						<option value="0" ${psiPartsOrder.orderSta eq '0' ?'selected':''} >草稿</option>
						<option value="1" ${psiPartsOrder.orderSta eq '1' ?'selected':''} >生产</option>
						<option value="3" ${psiPartsOrder.orderSta eq '3' ?'selected':''} >部分收货</option>
						<option value="5" ${psiPartsOrder.orderSta eq '5' ?'selected':''} >已收货</option>
						<option value="7" ${psiPartsOrder.orderSta eq '7' ?'selected':''} >已完成</option>
						<option value="8" ${psiPartsOrder.orderSta eq '8' ?'selected':''} >已取消</option>    
					</form:select>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
			<div style="height: 40px;">
			<label>配件订单/产品订单号 ：</label>
			<input name="partsOrderNo" value="${psiPartsOrder.partsOrderNo}" type="text" style="width:150px"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>配件名称 ：</label>
			<input name="purchaseOrderNo" value="${psiPartsOrder.purchaseOrderNo}" type="text" style="width:150px"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="batchReceive" class="btn btn-success" type="button" value="收货"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
		<th width="2%"><input type="checkbox" id="checkAll" /></th>
		<th style="width:5%">序号</th><th style="width:15%">配件订单号</th><th style="width:10%">产品订单号</th><th style="width:5%">总数量</th><th style="width:5%">可收货</th><th style="width:5%">待确认</th><th style="width:5%">已收货</th><th style="width:5%">供应商</th><th style="width:5%">创建人</th>	<shiro:hasPermission name="psi:product:viewPrice"><th style="width:5%">总额</th></shiro:hasPermission><th style="width:5%">币种</th><th style="width:5%">支付状态</th><th style="width:5%">订单状态</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiPartsOrder">
			<tr>
				<td>
					<input type="checkbox" class="checkPros" />
				</td>
				<td>${psiPartsOrder.id} </td>
				<td><span class="partsOrderNo"><a href="${ctx}/psi/lcPsiPartsOrder/view?id=${psiPartsOrder.id}">${psiPartsOrder.partsOrderNo}</a></span>&nbsp;${psiPartsOrder.sendEamil eq '1'?'<b>(Sent)</b>':''}</td>
				<td><a href="${ctx}/psi/purchaseOrder/view?id=${psiPartsOrder.purchaseOrderId}">${psiPartsOrder.purchaseOrderNo}</a></td>
				<td>${psiPartsOrder.itemsQuantity}</td>
				<td>${psiPartsOrder.itemsQuantityCanReceived}</td>
				<td>${psiPartsOrder.itemsQuantityPreReceived}</td>
				<td>${psiPartsOrder.itemsQuantityReceived}</td>
				<td>${psiPartsOrder.supplier.nikename}</td>
				<td>${psiPartsOrder.createUser.name}</td>
				<shiro:hasPermission name="psi:product:viewPrice"><td class="totalAmount">${psiPartsOrder.totalAmount}</td></shiro:hasPermission>
				<td class="currencyType">${psiPartsOrder.currencyType}</td>
				<td>
					<c:choose>
						<c:when test="${psiPartsOrder.totalPaymentAmount eq 0}"><span class="label label-important">未支付</span></c:when>
						<c:when test="${psiPartsOrder.totalPaymentAmount ne 0 && psiPartsOrder.totalAmount ne  psiPartsOrder.totalPaymentAmount}"><span class="label label-info">部分支付</span></c:when>
						<c:when test="${psiPartsOrder.totalAmount eq psiPartsOrder.totalPaymentAmount}"><span class="label  label-success">已支付</span></c:when>
					</c:choose>
				</td>
				<td><c:if test="${psiPartsOrder.orderSta eq '0'}"><span class="label label-important">草稿</span></c:if>
					<c:if test="${psiPartsOrder.orderSta eq '1'}"><span class="label label-warning">生产</span></c:if>
					<c:if test="${psiPartsOrder.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
					<c:if test="${psiPartsOrder.orderSta eq '5'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
					<c:if test="${psiPartsOrder.orderSta eq '7'}"><span class="label  label-success">已完成</span></c:if>
					<c:if test="${psiPartsOrder.orderSta eq '8'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td>
				<input type="hidden" value="${psiPartsOrder.id}"/>
				<a class="btn btn-small btn-info open">概要</a>&nbsp;&nbsp;
				
				<a class="btn btn-small" target="_blank" href="${ctx}/psi/lcPsiPartsOrder/printPdf?id=${psiPartsOrder.id}" >PDF</a>&nbsp;&nbsp;
				<c:if test="${empty psiPartsOrder.purchaseOrderNo }">
					<c:if test="${psiPartsOrder.orderSta eq '0'}">
					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsOrder/edit?id=${psiPartsOrder.id}" >编辑</a>&nbsp;&nbsp;
					</c:if>
				</c:if>
				
				<c:if test="${psiPartsOrder.orderSta eq '0' }">
					<c:if test="${psiPartsOrder.sendEamil eq '0'}">
						<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsOrder/sendEmail?id=${psiPartsOrder.id}"  onclick="return confirmx('确认要给该订单的供应商发邮件吗？', this.href)">Send</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${psiPartsOrder.sendEamil eq '1'}">
						<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsOrder/sure?id=${psiPartsOrder.id}" >确认</a>&nbsp;&nbsp;
					</c:if>
				</c:if>
				
				<c:if test="${psiPartsOrder.orderSta eq '0' }">
					<a class="btn btn-small"  href="${ctx}/psi/lcPsiPartsOrder/cancel?id=${psiPartsOrder.id}" onclick="return confirmx('确认要取消该配件订单吗？', this.href)">取消</a>&nbsp;&nbsp;
				</c:if>
				
				</td>
			</tr>
			<c:if test="${fn:length(psiPartsOrder.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${psiPartsOrder.id}"><td></td><td></td><td colspan="2">PartsName</td><td>订单数</td><td>可收货</td><td>待确认</td><td>已收货</td><td colspan="2"></td>	<shiro:hasPermission name="psi:product:viewPrice"><td>Price</td></shiro:hasPermission><td colspan="5"></td></tr>
				<c:forEach items="${psiPartsOrder.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${psiPartsOrder.id}">
					<td>
						<input type="hidden" class="orderItemId" value="${item.id}" />
						<input type="hidden" class="supplierId" value="${psiPartsOrder.supplier.id}" />
						<input type="hidden" class="orderSta" value="${psiPartsOrder.orderSta}" />
						<input type="hidden" class="currencyType" value="${psiPartsOrder.currencyType}" />
						<input type="hidden" class="quantityCanReceived" value="${item.quantityCanReceived}" />
						<input type="checkbox" class="checkPro" />
					</td>
					<td></td><td colspan="2">${item.partsName}</td><td>${item.quantityOrdered}</td><td>${item.quantityCanReceived}</td><td>${item.quantityPreReceived}</td><td>${item.quantityReceived}</td><td colspan="2"></td>	<shiro:hasPermission name="psi:product:viewPrice"><td>${item.itemPrice}</td></shiro:hasPermission><td colspan="5"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
