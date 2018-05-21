<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票管理</title>
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
				if($(this).text()=='Summary'){
					$(this).text('Close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			
			 $("#contentTable").on('click', '.save-row', function(e){
				    e.preventDefault();
					var tr =$(this).parent().parent();
					var $this=$(this);
					var invoiceQuantity=tr.find("input[name='invoiceQuantity']").val();
					var invoiceNo=tr.find("input[name='invoiceNo']").val();
					if(!invoiceQuantity){
						top.$.jBox.tip("数量不能为空", 'info',{timeout:2000});
						return false;
					}
					if(!invoiceNo){
						top.$.jBox.tip("发票不能为空", 'info',{timeout:2000});
						return false;
					}
					var param = {};
					param.inoviceId= tr.find("input[name='invoiceId']").val();
					param.invoiceQuantity = invoiceQuantity;
					param.invoiceNo= invoiceNo;
					
					$.get("${ctx}/psi/lcPurchaseOrder/saveInvoice?"+$.param(param),function(data){
						if(!(data)){    
							top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
						}else{
							$this.remove();
							top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
							tr.find(".itemId").val(data);
						}
					});
					return true;
				});
			
			
			 $(".add-row").on("click",function(e){
					e.preventDefault();
				    var inoviceId=$(this).attr('idVal');;
					var tbody=$(this).parent().parent();
					var tr=$("<tr></tr>");
				    tr.append("<td><input type='hidden'  name='invoiceId' value='"+inoviceId+"'/></td>");
				    tr.append("<td><input type='text' style='width: 85%'  name='invoiceNo' /></td>");
				    tr.append("<td><input type='text' maxlength='11'  style='width: 85%'  name='invoiceQuantity'  class='required number' /></td>");
				    tr.append("<td></td>");
		            tr.append("<td><input type='hidden' class='itemId' /> <a class='save-row'>保存</a>&nbsp;&nbsp;</br><a href='#' class='remove-row'>删除</a></td>");
					tbody.after(tr);
					
				});
			 
			 
			 $("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
					//取消原来输入的数据
					var tr = $(this).parent().parent();
					var itemId = tr.find(".itemId").val();
					if(itemId){
						var param = {};
						param.itemId = itemId;
						$.get("${ctx}/psi/lcPurchaseOrder/deleteItem?"+$.param(param),function(data){
							if(!(data)){    
								top.$.jBox.tip("删除失败！", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("删除成功！", 'info',{timeout:2000});
								 tr.remove();
							}
						});
					}else{
						 tr.remove();
					}
					
			});
			 
			 
			 $("#contentTable").on("change",".invoice",function(){
				  var  tr = $(this).parent().parent();
				  var id=$(this).val();
				  var nameColor=$(this).children('option:selected').attr('nameVal');
				  var quantityVal=$(this).children('option:selected').attr('quantityVal');
				  var supplierVal=$(this).children('option:selected').attr('supplierVal');
				  console.log(nameColor+"="+quantityVal+"="+supplierVal+"="+id);
				  tr.find(".name").text(nameColor);
				  tr.find(".invoiceQuantity").text(quantityVal);
				  tr.find(".supplierName").text(supplierVal);
				  tr.find(".invoiceId").val(id);
			  });
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/lcPurchaseOrder/">(理诚)采购订单列表</a></li>
		<shiro:hasPermission name="psi:order:edit">
			<li><a href="${ctx}/psi/lcPurchaseOrder/add">(理诚)新建采购订单</a></li>
			<li  class="active"><a href="${ctx}/psi/lcPurchaseOrder/invoiceList">(理诚)发票列表</a></li>	
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/">采购订单列表</a></li>
		<shiro:hasPermission name="psi:order:edit">
			<li><a href="${ctx}/psi/purchaseOrder/add">新建采购订单</a></li>
		</shiro:hasPermission>
		
		
	</ul>
	
	<tags:message content="${message}"/>
	<div class="alert">采购单号:${invoiceList[0].order.orderNo}&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
	
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th>No.</th><th>产品名</th><th>订单数量</th><th>发票数量</th><th>操作</th></tr></thead>
		<tbody>
		       
			   <c:forEach items="${invoiceList}" var="invoice" varStatus="i">
			       <tr>
			         <td>${i.index+1}</td>
			         <td>${invoice.productName}</td>
					 <td>${invoice.quantityOrdered}</td>
					 <td>${invoice.quantityMatched}</td>
					 <td colspan='2'><input type="hidden" value="${invoice.id }" class="invoiceId"/>
					     <a class="btn btn-small btn-info open">Close</a>
					 </td>
				   </tr>
				   <tr style="background-color:#D2E9FF;" name="${invoice.id}"><td>&nbsp;&nbsp;<shiro:hasPermission name="psi:order:edit"><a href="#" class="add-row"  idVal='${invoice.id}'><span class="icon-plus"></span></a></shiro:hasPermission></td><td>发票号</td><td>分配数量</td><td></td><td></td></tr>
				   <c:if test="${fn:length(invoice.items)>0}">
				         <c:forEach items="${invoice.items}" var="item">
				            <tr style="background-color:#D2E9FF;" name="${item.id}">
				                 <td></td>
				                 <td>${item.invoiceNo}</td>
				                 <td>${item.invoiceQuantity}</td>
					             <td></td>
					             <td><input type='hidden' class='itemId' value='${item.id }'/><shiro:hasPermission name="psi:order:edit"><a href='#' class='remove-row'>删除</a></shiro:hasPermission></td>
				            </tr>
				         </c:forEach>
				      </tr>
				   </c:if>
			   </c:forEach>
		</tbody>
	</table>
</body>
</html>
