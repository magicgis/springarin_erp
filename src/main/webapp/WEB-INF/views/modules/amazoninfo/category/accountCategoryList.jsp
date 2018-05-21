<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>账号:${name}大目录分析</title>
<%@include file="/WEB-INF/views/include/datatables.jsp" %>
<meta name="decorator" content="default"/>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>

<script type="text/javascript">

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
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return parseFloat($('td:eq('+iColumn+')', tr).text().split(',').join(''));
		});
	};
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return parseFloat($('td:eq('+iColumn+')', tr).text().replace("%",""));
		} );
	};
	
	$(function() {
		
		
		$(".countryHref").click(function(){
			$("#country").val($(this).attr("key"));
			$("#searchForm").submit();
		});
	
		
		$("#contentTable").dataTable({
			"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"sScrollX": "100%",
			"iDisplayLength" : 15,
			"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
					[ 15, 30, 60, 100, "All" ] ],
			"bScrollCollapse" : true,
			"aaSorting": [[ 2, "desc" ]],
			"aoColumns": [
					         null,	
					         { "sSortDataType":"dom-html", "sType":"numeric" },
					         { "sSortDataType":"dom-html", "sType":"numeric" },
						     { "sSortDataType":"dom-html", "sType":"numeric" },
						     { "sSortDataType":"dom-html", "sType":"numeric" },
						     { "sSortDataType":"dom-html", "sType":"numeric" },
						     null
						   ],
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"ordering" : true
		});
		
		$("#changeCountry").change(function(){
			$("#searchForm").submit();
		});

		 
	});
	
	function changeType(){
		$("#searchForm").submit();
	}
	
	function searchType(searchFlag){
		var oldSearchFlag= $("#flag").val();
		if(oldSearchFlag==searchFlag){
			return;
		}
		$("#day").val("");
		$("#end").val("");
		$("#flag").val(searchFlag);
		$("#searchForm").submit();
	}
	
	function timeOnChange(obj){
		var id = obj.id;
		if('day' == id){
			$("#end").val(obj.value);
		}
		$("#searchForm").submit();
	}

</script>
</head>
<body>
	<div class="alert">
	  <strong>平台:${country} 账号:${name} 大目录分析</strong>&nbsp;&nbsp;&nbsp;&nbsp;
	</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>大目录名</th>
					<th>Sessions</th>
					<th>销量</th>
					<th>转化率(%)</th>
					<th>销售额(${country eq 'DE'?'€':'$'})</th>
					<th>客单价(${country eq 'DE'?'€':'$'})</th>
					<th>单目录分析</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${data}" var="row" varStatus="i">
					<tr>
						<td>${row[0]}</td>
						<td>${row[1]}</td>
						<td>${row[2]}</td>
						<td><fmt:formatNumber value="${row[3]}" pattern="#.##" minFractionDigits="2" /></td>
						<td><fmt:formatNumber value="${row[4]}" type="number"  /></td>
						<td>${row[5]}</td>
						<td><a class="btn btn-small btn-success"  href="${ctx}/amazoninfo/productCatalog/singleCategory?country=${country}&name=${name}&merchantCustomerId=${merchantCustomerId}&categoryName=${row[0]}" target="_blank">分析</a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
</body>
</html>
