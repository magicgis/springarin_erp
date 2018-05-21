<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
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
			$("#btnSubmit").click(function(){
				var flag='0';
				var invoiceIds="";
				$("#contentTable tbody tr").each(function(){
					var invoiceId=$(this).find("select.invoiceId").val();
					if(invoiceId==''){
						flag='1';
						return false;
					}
					var declareId=$(this).find("select.invoiceId").children("option:selected").attr("idVal");
					
					invoiceIds=invoiceIds+declareId+","+invoiceId+";";
				});
				if(flag=='1'){
					top.$.jBox.tip("不能存在空的发票分配!!!", 'info',{timeout:3000});
					return false;
				}
				var params = {};
				params.invoiceIds=invoiceIds;
				params.declareNo='${declareNo}';
				$.post("${ctx}/psi/psiInvoice/invoiceSingleArrange",$.param(params),function(data){
					if(data==0){
						top.$.jBox.tip("发票分配成功!!!",'info',{timeout:3000});
						window.location.href="${ctx}/psi/psiInvoice/editDeclare?declareNo=${declareNo}";
					}else{
						top.$.jBox.tip(data+",发票分配失败!!!",'info',{timeout:4000});
					}
				});	
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
	<form id="searchForm"  class="breadcrumb form-search" >
		<input id="btnSubmit" class="btn btn-info" type="button" value="保存"/>&nbsp;&nbsp;
		<a class="btn btn-warning" href="${ctx}/psi/psiInvoice/resetDeclareList?declareNo=${declareNo}">还原分配</a>&nbsp;&nbsp;
		<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
	</form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
		   <th>序号</th>
		   <th>出口日期</th>
		   <th>报关单号</th>
		   <th>项号</th>
		   <th>合同号</th>
		   <th>品名</th>
		   <th>数量</th>
		   <th>单价</th>
		   <th>发票号</th>
		   <th>单价</th>
		   <th>新发票号</th>
		 </tr>
		 </thead>   
		<tbody>
		<c:forEach items="${declareList}" var="declare">
			<tr>
				<td>${declare.id}</td>
				<td><fmt:formatDate value="${declare.declareDate}" pattern="yyyy-MM-dd"/></td>
				<td>${declare.declareNo}</td>
				<td>${declare.declareNum}</td>
				<td>${declare.transportNo}</td>
				<td>${declare.productName}</td>
				<td>${declare.quantity}</td>	
				<td>${declare.price}</td>	
				<td>${declare.invoice.invoiceNo}</td>	
				<td>${declare.invoice.price}</td>	
				<td>
				   <select name="invoiceId" class="invoiceId" >
				         <option value="">--请选择--</option>
				         <c:forEach items="${invoiceMap[declare.productName]}" var="invoice">
				             <option  idVal='${declare.id}' value="${invoice.id}" priceVal='${invoice.price}'  ${invoice.id eq declare.invoice.id?'selected':''}>${invoice.id},${invoice.invoiceNo},单价:${invoice.price},单价:${invoice.quantity},${invoice.companyName}</option>
				         </c:forEach>
				   </select>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
