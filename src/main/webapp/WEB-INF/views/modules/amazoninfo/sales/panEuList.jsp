<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>可泛欧产品明细管理</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.spanexr {
	float: right;
	min-height: 40px
}

.spanexl {
	float: left;
}

.footer {
	padding: 20px 0;
	margin-top: 20px;
	border-top: 1px solid #e5e5e5;
	background-color: #f5f5f5;
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
	
	function fmoney(s, n){   
	   temp = s;	
	   if(s<0){
		  temp= -s;
	   }
	   n1 = n;
	   n = n > 0 && n <= 20 ? n : 2;   
	   temp  = parseFloat((temp + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
	   var l = temp.split(".")[0].split("").reverse(),   
	   r = temp.split(".")[1];   
	   t = "";   
	   for(i = 0; i < l.length; i ++ )   
	   {   
	      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
	   }   
	   temp =  t.split("").reverse().join("") + "." + r;   
	   if(s<0){
		   temp= "-"+temp;
	   }
	   if(n1==0){
		   temp = temp.replace(".00","")
	   }
	   return temp;
	} 
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			var txt = $('td:eq(1)', tr).text();
			var txt1 = $('td:eq(10)', tr).text();
			if((txt && txt.indexOf('未匹配')>0) || '0'==txt1){
				return -10000;
			}else{
				return $('td:eq('+iColumn+')', tr).text().replace(",","");
			}
		} );
	};
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+')', tr).text().replace("%","");
		} );
	};
	
	
	$(function() {

		$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
		});
		
		var cnt=" &nbsp;&nbsp;<a class='btn btn-primary'  id='expExcel'>导出</a><br/>";

		 $(".row:first").append(cnt);
		
		 
		 $("#expExcel").click(function(){
			 $("#expExcel").attr("href","${ctx}/amazoninfo/salesReprots/exportPanEu");
		});
		
		
	});
	
</script>
</head>
<body>
	<div id="contentTbDiv" style="margin: auto">
		<div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th>Produc Name</th>
						<th>Asin</th>
						<th>Sku</th>
						<th>FnSku</th>
						<th>Pan_Eu</th>
						<th>UK</th>
						<th>DE</th>
						<th>FR</th>
						<th>IT</th>
						<th>ES</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${panEuList}" var="op" varStatus="i">
						<tr>
							<td>${i.count}</td>
							<td>${op[3]}</td>
							<td>${op[1]}</td>
							<td>${op[2]}</td>
							<td>${op[10]}</td>
							<td><c:set var="key"  value="${op[4]} "/>${'1 ' eq key ?'Y':'N'  }</td>
							<td>${op[5]}</td>
							<td>${op[6]}</td>
							<td>${op[7]}</td>
							<td>${op[8]}</td>
							<td>${op[9]}</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
