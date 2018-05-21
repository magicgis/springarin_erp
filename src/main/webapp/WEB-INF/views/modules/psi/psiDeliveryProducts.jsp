<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>未来收货的产品</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.red{color:red;}
	</style>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ){
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr,i) {
			return $('td:eq('+iColumn+')', tr).text().replace(/,/g, "");
		} );
	};
	
		$(document).ready(function() {
			
			$("#week").on("change",function(){
				$("#searchForm").submit();
			});
			
			$("#firstOnce").on("click",function(){
				if(this.checked){
					$("input[name='firstOnce']").val("1");
				}else{
					$("input[name='firstOnce']").val("0");
				}
				$("#searchForm").submit();
			});
			
			$("#moreOnce").on("click",function(){
				if(this.checked){
					$("input[name='moreOnce']").val("1");
				}else{
					$("input[name='moreOnce']").val("0");
				}
				$("#searchForm").submit();
			});
			
			
			$("#expExcel").click(function(){
				var params = {};
				params.firstOnce=$("input[name='firstOnce']").val();
				params.moreOnce=$("input[name='moreOnce']").val();
				params.week=$("#week").val();
				window.location.href = "${ctx}/psi/purchaseOrder/expPreReceived?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:5000});
			});
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
				              null,
				              null,
				              null,
				              null,
				              null,
				              null
				],
				"ordering" : true,
				 "aaSorting": [[ 4, "asc" ]]
			});
		
			
			
		});
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/preReceived" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
			<label>未来一段时间：</label>
			<select style="width:200px;" name="week" id="week">
				<option value="">全部</option>
				<option value="1" ${week eq '1'?'selected':''}>未来7天</option>
				<option value="2" ${week eq '2'?'selected':''}>未来7-14天</option>
				<option value="3" ${week eq '3'?'selected':''}>未来14-21天</option>
				<option value="4" ${week eq '4'?'selected':''}>未来21-28天</option>
				<option value="5" ${week eq '5'?'selected':''}>未来28-35天</option>    
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>新品首单：</label><input type="checkbox"  id="firstOnce" value="${firstOnce}" ${firstOnce eq '1' ?'checked':'' }/>
			<input  name="firstOnce" type="hidden" value="${firstOnce}"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>新品翻单：</label><input type="checkbox"  id="moreOnce" value="${moreOnce}" ${moreOnce eq '1' ?'checked':'' }/>
			<input  name="moreOnce" type="hidden" value="${moreOnce}"/>    
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
	</form:form>
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="5%">产品名</th><th width="5%">市场</th><th width="5%">数量</th><th width="5%">收货日期</th><th width="5%">收货还剩天数</th><th width="10%">产品属性</th></tr></thead>
		<tbody>
		<c:forEach items="${obs}" var="obj" varStatus="i">
			<tr>
				<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${obj[0]}">${obj[0]}</a></td><td>${fns:getDictLabel(obj[1],'platform','')}</td><td>${obj[2]}</td>
				<td>${obj[3]}</td>
				<td>${fns:pastDaysByStr(obj[3])+1}</td> 
				<td>${attrMap[obj[0]]}</td>   
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
