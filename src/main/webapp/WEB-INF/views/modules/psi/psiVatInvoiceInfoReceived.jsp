<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
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
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$(".inRemark").editable({
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var oldVal = $(this).text();
					param.id = $(this).parent().parent().find(".inId").val();
					param.remark = encodeURI(newValue);
					$.get("${ctx}/psi/psiVatInvoiceInfo/updateRemark?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$.jBox.tip("success！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ){
				return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr,i) {
					return $('td:eq('+iColumn+')', tr).text().replace(/,/g, "");
				} );
			};
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"aoColumns": [
					          null,
					          { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null,
						      null,
						      null
						],
				 "aaSorting": [[ 0, "asc" ]]
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
		<li ><a href="${ctx}/psi/psiVatInvoiceInfo">增值税发票列表</a></li>
		<li ><a href="${ctx}/psi/psiVatInvoiceInfo/form">新增增值税发票</a></li>
		<li class="active"><a href="">增值税开票详情</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiVatInvoiceInfo" action="${ctx}/psi/psiVatInvoiceInfo/received/" method="post" class="breadcrumb form-search" >
		<label>截止日期：</label><input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">产品名</th><th style="width:5%">已收货金额</th><th style="width:5%">已付款金额</th><th style="width:5%">已开票金额(含税)</th><th style="width:5%">已开票金额(不含税)</th><th style="width:5%">税额</th><th style="width:5%">已使用数量</th><th style="width:5%">采购总数</th><th style="width:5%">未开票数量(含在产)</th><th style="width:5%">未开票数量(不含在产)</th><th style="width:5%">税单详细</th></tr></thead>
		<tbody>
		<c:forEach items="${receiveds}" var="received">
			<tr>
				<td>${received[0]}</td>
				<td><fmt:formatNumber value="${received[1]}" pattern="###,##0.00"/></td>
				<td><fmt:formatNumber value="${received[2]}" pattern="###,##0.00"/></td>
				<c:set var="totalAmount" value="${voiceMap['0'][received[0]]}"/>
				<td><fmt:formatNumber value="${totalAmount}" pattern="###,##0.00"/></td>
				<td><fmt:formatNumber value="${totalAmount/1.17}" pattern="###,##0.00"/></td>
				<td><fmt:formatNumber value="${totalAmount-totalAmount/1.17}" pattern="###,##0.00"/></td>
				<td>${voiceMap['1'][received[0]]}</td>
				<td>${purchaseMap['0'][received[0]]}</td>
				<td>${purchaseMap['0'][received[0]]-voiceMap['1'][received[0]]}</td>
				<td>${purchaseMap['0'][received[0]]-voiceMap['1'][received[0]]-purchaseMap['1'][received[0]]}</td>
				<td><a class="btn btn-small"  href="${ctx}/psi/psiVatInvoiceInfo/list?remark=${received[0]}">查看</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
