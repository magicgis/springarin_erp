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
					var itemId =$(this).parent().find(".itemId").val();
					var $this=$(this);
					var remark=tr.find("input[name='quantity']").val();
					var qty=tr.find("input[name='totalQuantity']").val();
					if(!remark){
						top.$.jBox.tip("数量不能为空", 'info',{timeout:2000});
						return false;
					}
					var invoiceQty=tr.find(".invoiceQuantity").text();
					if(parseInt(invoiceQty)<parseInt(remark)){
						top.$.jBox.tip("分配数量不能大于发票数量", 'info',{timeout:2000});
						return false;
					}
					var totalQuantity=0;
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("input[name='orderItemId']").each(function(){
							if($(this).val()==tr.find("input[name='orderItemId']").val()){
								totalQuantity=parseInt(totalQuantity)+parseInt($(this).parent().parent().find("input[name='quantity']").val());
							}
						}); 
				    });
					if(parseInt(totalQuantity)>parseInt(qty)){
						top.$.jBox.tip("分配数量不能大于运单总数量", 'info',{timeout:2000});
						return false;
					}
					var param = {};
					param.inoviceId=tr.find("select[name='invoice']").val();
					param.quantity = tr.find("input[name='quantity']").val();
					param.orderItemId=tr.find("input[name='orderItemId']").val();
					param.id = itemId;
					param.country=tr.find("input[name='country']").val();
					param.name=tr.find("input[name='pname']").val();
					
					$.get("${ctx}/psi/lcPsiTransportOrder/saveInvoice?"+$.param(param),function(data){
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
				
					var tbody=$(this).parent().parent();
					var nameColor=$(this).attr('nameVal');
					var idVal=$(this).attr('idVal');
					var countryVal=$(this).attr('countryVal');
					var totalQuantityVal=$(this).attr('totalQuantityVal');
					var tr=$("<tr></tr>");
				
					var nameOptions="";
					var name="";
					var supplierName="";
					var invoiceQuantity="";
					var invoiceId="";
				   <c:forEach items="${invoiceList}" var="invoice">
				       if(nameColor=='${invoice.productName}'){
				    	    nameOptions+='<option value="${invoice.id}" quantityVal="${invoice.remainingQuantity}" nameVal="${invoice.productName}" supplierVal="${invoice.supplierName}" selected>${invoice.invoiceNo}_${invoice.productName}_${invoice.supplierName}</option>';
				    	    name="${invoice.productName}";
							supplierName="${invoice.supplierName}";
							invoiceQuantity="${invoice.remainingQuantity}";
							invoiceId="${invoice.id}";
				       }else{
				    	   nameOptions+='<option value="${invoice.id}" quantityVal="${invoice.remainingQuantity}" nameVal="${invoice.productName}" supplierVal="${invoice.supplierName}">${invoice.invoiceNo}_${invoice.productName}_${invoice.supplierName}</option>';
				       }
				    </c:forEach>
				   // <tr><td>发票号<td><td>产品名<td><td>供应商</td><td>发票数量</td><td>分配数量</td><td></td></tr>
				 
				    tr.append("<td><input type='hidden' name='invoiceId' value='"+invoiceId+"' /><select class='invoice' name='invoice' style='width:95%'>"+nameOptions+"</select></td>");
				    tr.append("<td style='vertical-align: middle;text-align: center' class='name'>"+name+"</td>");
				    tr.append("<td style='vertical-align: middle;text-align: center' class='supplierName'>"+supplierName+"</td>");
				    tr.append("<td style='vertical-align: middle;text-align: center' class='invoiceQuantity'>"+invoiceQuantity+"</td>");
				    tr.append("<td><input type='hidden' name='totalQuantity' value='"+totalQuantityVal+"'/><input type='hidden' name='orderItemId' value='"+idVal+"'/><input type='hidden' name='country' value='"+countryVal+"'/><input type='hidden' name='pname' value='"+nameColor+"'/><input type='text' maxlength='11'  style='width: 85%'  name='quantity'  class='required number' /></td>");
		            tr.append("<td><input type='hidden' class='itemId' /> <a class='save-row'>保存</a>&nbsp;&nbsp;</br><a href='#' class='remove-row'>删除</a></td>");
					tbody.after(tr);
					tr.find("select.invoice").select2();
				});
			 
			 
			 $("#contentTable").on('click', '.remove-row', function(e){
				  e.preventDefault();
					//取消原来输入的数据
					var tr = $(this).parent().parent();
					var itemId = tr.find(".itemId").val();
					var invoiceId =tr.find("input[name='invoiceId']").val();
					var quantity =tr.find("input[name='quantity']").val();
					if(itemId){
						var param = {};
						param.itemId = itemId;
						param.inoviceId=invoiceId;
						param.quantity=quantity;
						$.get("${ctx}/psi/lcPsiTransportOrder/deleteItem?"+$.param(param),function(data){
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
		<li><a href="${ctx}/psi/lcPsiTransportOrder/">(理诚)<spring:message code="psi_transport_list"/></a></li>
		<li  class="active"><a href="#">发票分配</a></li>	
	</ul>
	<tags:message content="${message}"/>
	<div class="alert">运单号:${order.transportNo}&nbsp;&nbsp;&nbsp;&nbsp;
	  <a href="${ctx}/psi/lcPsiTransportOrder/exportInvoice?id=${order.id}"><input  class="btn btn-warning" type="button" value="导出"/></a>
	</div>
	
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:13%">No.</th><th style="width:13%">产品名</th><th style="width:8%">颜色</th><th style="width:8%">国家</th><th style="width:8%">数量</th><th style="width:8%">操作</th></tr></thead>
		<tbody>
		       
			   <c:forEach items="${order.items}" var="item" varStatus="i">
			       <tr>
			         <td>${i.index+1}</td>
			         <td>${item.productName}</td>
			         <td>${item.colorCode}</td>
					 <td>${fns:getDictLabel(item.countryCode, 'platform', defaultValue)}</td>
					 <td>${item.quantity}</td>
					 <td colspan='2'><input type="hidden" value="${item.id }" class="transportId"/>
					     <a class="btn btn-small btn-info open">Close</a>
					     
					 </td>
				   </tr>
				   <tr style="background-color:#D2E9FF;" name="${item.id}"><td>发票号&nbsp;&nbsp;<a href="#" class="add-row" totalQuantityVal='${item.quantity }' nameVal='${item.productName}' idVal='${item.id}' countryVal='${item.countryCode }'><span class="icon-plus"></span></a></td><td>产品名</td><td>供应商</td><td>发票数量</td><td>分配数量</td><td></td></tr>
				   <c:if test="${fn:length(item.invoices)>0}">
				         <c:forEach items="${item.invoices}" var="invoice">
				            <tr style="background-color:#D2E9FF;" name="${item.id}">
				                 <td><input type='hidden' name='invoiceId' value='${invoice.invoice.id}'  />${invoice.invoice.invoiceNo}</td>
				                 <td><a target='_blank' href='${ctx}/psi/psiVatInvoiceInfo?productName=${invoice.invoice.invoiceNo}'>${invoice.invoice.productName}</a></td>
				                 <td>${invoice.invoice.supplierName}</td>
					             <td></td>
					             <td class='invoiceQty'><input type='hidden' name='orderItemId' value='${item.id}'/><input name='quantity' value='${invoice.quantity}' readonly/></td>
					             <td><input type='hidden' class='itemId' value='${invoice.id }'/><a href='#' class='remove-row'>删除</a></td>
				            </tr>
				         </c:forEach>
				      </tr>
				   </c:if>
			   </c:forEach>
		</tbody>
	</table>
</body>
</html>
