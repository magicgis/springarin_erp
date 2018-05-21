<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>DPD发货数量统计</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="300"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			var t1 = 0 ;
			var t2 = 0 ;
			var t3=0;
			var t4=0;
			$(".t1").each(function(){
				t1 =t1+parseInt($(this).text()==""?0:$(this).text());
			});
			$(".t2").each(function(){
				t2 =t2+parseInt($(this).text()==""?0:$(this).text());
			});
			$(".t3").each(function(){
				t3 =t3+parseInt($(this).text()==""?0:$(this).text());
			});
			$(".t4").each(function(){
				t4 =t4+parseInt($(this).text()==""?0:$(this).text());
			});
			
			$("#total1").append("<b><span style='padding-left:5px;'>"+t1+"<span></b>");
			$("#total2").append("<b><span style='padding-left:5px;'>"+t2+"<span></b>");
			$("#total3").append("<b><span style='padding-left:5px;'>"+t3+"<span></b>");
			$("#total4").append("<b><span style='padding-left:5px;'>"+t4+"<span></b>");
			
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
   
	<form:form id="searchForm"  action="${ctx}/amazonAndEbay/mfnOrder/countDPD" method="post" class="breadcrumb form-search">
		<div style="height: 30px;line-height: 30px">
			<div>
				<label>Shipped Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="buyTime" value="<fmt:formatDate value="${mfnOrder.buyTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="paidTime" value="<fmt:formatDate value="${mfnOrder.paidTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
				
			</div> 
		</div>
	</form:form>
	<div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th style="text-align: center;vertical-align: middle">Country</th>
			<th style="text-align: center;vertical-align: middle">MFN Order</th>
			<th style="text-align: center;vertical-align: middle">Vendor Order</th>
			<th style="text-align: center;vertical-align: middle">FBA</th>
			<th style="text-align: center;vertical-align: middle">Total</th>
	    </tr>		
		<tbody>
		<c:forEach items="${map}" var="dpd" varStatus="i">
		<tr>
			<td style="text-align: center;vertical-align: middle">${fns:getDictLabel(dpd.key,'platform','')}</td>
			<td style="text-align: center;vertical-align: middle" class="t1">${map[dpd.key]['3']}</td>
			<td style="text-align: center;vertical-align: middle" class="t2">${map[dpd.key]['1']}</td>
			<td style="text-align: center;vertical-align: middle" class="t3">${map[dpd.key]['2']}</td>
			<td style="text-align: center;vertical-align: middle" class="t4"><b>${map[dpd.key]['3']+map[dpd.key]['1']+map[dpd.key]['2']}</b></td>	
		
		</tr>
		</c:forEach>
		</tbody>
		<tfoot>
			<tr><td style="text-align: center;vertical-align: middle" ><b>Total</b></td>
				<td style="text-align: center;vertical-align: middle" id="total1"></td>
				<td style="text-align: center;vertical-align: middle" id="total2"></td>
				<td style="text-align: center;vertical-align: middle" id="total3"></td>
				<td style="text-align: center;vertical-align: middle" id="total4"></td>
			</tr> 
		</tfoot>
	</table>
	</div>
	
</body>
</html>
