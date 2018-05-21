<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>ebay产品保本价</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		}
		.div-relative{position:relative; color:#000; border:1px solid #000; width:500px; height:400px} 
		.div-b{ position:absolute; left:150px; top:120px;width:100px; height:50px;} 
	</style>
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var text = $('td:eq('+iColumn+')', tr).text();
				if(!parseFloat(text) > 0){
					return 0;
				}
				return parseFloat(text);
			});
		};
	
		$(document).ready(function(){
			
			
			 $("a[rel='popover']").popover({trigger:'hover'});
			$("#tb").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 20, 60, 100, -1 ],
						[ 15, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null, 
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" }
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			
		});
		
		function exportPrice(){
			window.location.href = "${ctx}/amazoninfo/productPrice/exportPrice";
            top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
        }
	</script>
</head>
	<body>
	 <ul class="nav nav-tabs">
		<li ><a href="${ctx}/amazoninfo/productPrice/salePrice">Amazon保本价</a></li>
		<shiro:hasPermission name="amazoninfo:ebayProductSalePrice:view">
           <li class='active'><a href="${ctx}/amazoninfo/productPrice/ebaySalePrice">Ebay保本价</a></li>
        </shiro:hasPermission>
	</ul>
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <strong>Warning!</strong> 
	  公式：A(1+关税)+B*ebay成交费+B*paypal成交费+运费+利润=B/(1+VAT)<br/>
	 A:采购价 B:销售价   保本价为当利润为0时，倒推出的B值
	</div>
	    <div class="div-b">        
	       <input  onclick="exportPrice()" class="btn btn-primary" type="button" value="导出"/>
	    </div>
		<div id= "tbDiv"  style="overflow-x:auto;" class="div-c">
		<table id="tb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="width:25%"><spring:message code="psi_product_name"/></th>
					<th>${fns:getDictLabel('de','platform','')}(€)</th>
					<th>${fns:getDictLabel('de','platform','')}亚马逊在售(€)</th>
					<th>${fns:getDictLabel('com','platform','')}($)</th>
					<th>${fns:getDictLabel('com','platform','')}亚马逊在售($)</th>
				</tr>
			<tbody>
				<c:forEach items="${ebayPriceMap}" var="data">
					<tr>
						<td><b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${data.key}" target="_blank">${data.key}</a></b></td>
						<!-- de保本价 -->
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="Air Price:${ebayPriceMap[data.key]['de'].skyFee}<br/>Sea Price:${ebayPriceMap[data.key]['de'].seaFee}">${ebayPriceMap[data.key]['de'].safePrice}</a>
						</td>
						<!-- de亚马逊在售价 -->
						<td>
							<a href="${productsCurrent[data.key]['de'][2]}" target="_blank">${productsCurrent[data.key]['de'][0]}</a>
						</td>
						<!-- com保本价 -->
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="Air Price:${ebayPriceMap[data.key]['com'].skyFee}<br/>Sea Price:${ebayPriceMap[data.key]['com'].seaFee}">${ebayPriceMap[data.key]['com'].safePrice}</a>
						</td> 
						<!-- com亚马逊在售价 -->
						<td>
							<a href="${productsCurrent[data.key]['com'][2]}" target="_blank">${productsCurrent[data.key]['com'][0]}</a>
						</td>
						
					</tr>		
				</c:forEach>
			</tbody>
		</table>
		</div>
	</body>
</html>