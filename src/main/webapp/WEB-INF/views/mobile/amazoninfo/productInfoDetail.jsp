<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<script src="${ctxStatic}/bootstrap/2.3.1/js/bootstrap.min.js" type="text/javascript"></script>
<style type="text/css">
td {
	height:30px;
}
.ui-shadow-inset{
	width:50%;
	height:35px;
	margin-left: auto !important;
	margin-right: auto !important;
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
		
		<c:choose>
			<c:when test="${saleReport.searchType eq '1'}">
				<c:set var="type" value="日" />
			</c:when>
			<c:when test="${saleReport.searchType eq '2'}">
				<c:set var="type" value="周" />
			</c:when>
			<c:otherwise>
				<c:set var="type" value="月" />
			</c:otherwise>
		</c:choose> 
		
		var map = {};
		map.de = 'EUR';
		map.fr = 'EUR';
		map.it = 'EUR';
		map.es = 'EUR';
		map.uk = 'GBP';
		map.ca = 'CDN$';
		map.com = 'USD';
		map.jp = 'JPY';
		map.mx = 'MXN$';
		var oldSearchFlag;
		
		$(document).ready(function() {

			$(".salesDay").each(function(){
			    var now = new Date();
				var zq = $(this).parent().find("td:eq(10)").text();
				var days = $(this).parent().find("td:eq(19)").text();
				if(zq == null || zq.length == 0 ){
					zq = 0;
				}
				if(days == null || days.length == 0 ){
					days = 0;
				} 
				if(zq == 0 && days == 0){
					$(this).text("");
				} else {
					var str = now.getDate()+parseInt(zq)+parseInt($.trim(days));
					if(isNaN(str)){
						$(this).text("");
					} else {
						now.setDate(str);
						$(this).text(now.Format("yyyy-MM-dd"));
					}
				}
			});
			

			var products="";
			$('#typeahead').typeahead({
				source: function (query, process) {
					if(!(products)){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/psi/psiInventory/getAllProductNames' ,
						    dataType: 'json',
						    success:function(data){ 
						    	products = data;
					        }
						});
					}
					process(products);
			    },
				updater:function(item){
					var country = '${country}';
					var sTime = '${time}';
					var sType = '${sType}';
					var url = "${ctx}/psi/psiInventory/mobileProductInfoDetail?productName=" + encodeURIComponent(item) + "&sCountry=" + country
					 	+ "&sTime=" + sTime + "&sType=" + sType + "&searchType=" + oldSearchFlag;
					window.location.href = url;
					$.mobile.showPageLoadingMsg("b","加载中...",false);
					return item;
				}
			});
			//$("a[rel='popover']").popover({trigger:'hover'});
			 
			var arr = $(".sale1 .sale1");
			var num1 = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num1 += parseInt($(this).text());
				}
			});
			
			var arr = $(".sale1 .deSale1");
			var num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});

			if(num1){
				if(num){
					$("#t1").append("<span class=\"btn btn-small sale1\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num1+"</span>");
				} else {
					$("#t1").append(num1);
				}
			}
			if(num){
				$("#t1").append("&nbsp;<span class=\"btn btn-small btn-success\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
			}
			
			arr = $(".sale2");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t2").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t2").append(num);
			}
			
			arr = $(".sale3");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t3").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t3").append(num);
			}
			
			arr = $(".sale4");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t4").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t4").append(num);
			}
			
			arr = $(".sale5");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t5").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t5").append(num);
			}
			
			arr = $(".sale6");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t6").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t6").append(num);
			}
			
			
			arr = $(".sale7");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t7").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t7").append(num);
			}
			
			arr = $(".sale8");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t8").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t8").append(num);
			}
			
			arr = $(".sale9");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t9").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t9").append(num);
			}
			
			
			arr = $(".sale11");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t11").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t11").append(num);
			}
			
			
			
			arr = $(".sale10");
			num = 0;
			arr.each(function() {
				if ($.trim($(this).text())) {
					num += parseInt($(this).text());
				}
			});
			if(num){
				//$("#t10").append("<span class=\"btn btn-small btn-info\" style=\"height:14px; font-size:12px; line-height:12px;\">"+num+"</span>");
				$("#t10").append(num);
			}
			
			
			//-----------------------------------------------------------
			var searchType = $("#searchType").val();
			if(searchType==1){
		    	 $("#showTab0").addClass("active");
		    }else if(searchType==2){
		    	$("#showTab1").addClass("active");
		    }else if(searchType==3){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    }
			
			oldSearchFlag= $("#searchType").val();
			
			
			$("#reback").click(function(){
				window.history.go(-1);				
			});
			
			$("table").css("margin-bottom","5px");
			

		  	$("#saleVolumeTb tbody tr").each(function(i){
		    	var total = $.trim($(this).find("td :eq(12)").text());
		    	if(total){
		    		for ( var j = 2; j <= 10; j++) {
		    			var single = $(this).find("td:eq("+j+")").text();
			    		if(single){
			    			 $("#contrastTb tbody tr:eq("+i+")").find("td:eq("+j+")").text((single*100/total).toFixed(2)+"%");
			    		} 
					}
		    	}
		    });
		  	
		  	var trTfoot = $("#saleVolumeTb tfoot tr:eq(0)");
	    	var total = $.trim(trTfoot.find("td :eq(11)").text());
	    	if(total){
	    		for ( var j = 1; j <= 9; j++) {
	    			var single = trTfoot.find("td:eq("+j+")").text();
		    		if(single){
		    			 $("#contrastTb tfoot tr:eq(0)").find("td:eq("+j+")").text((single*100/total).toFixed(2)+"%");
		    		} 
				}
	    	}
	    	
			$("#saleVolumeTb").dataTable({
			    "searching":false,
				"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"bScrollCollapse" : true,
				"ordering" : true,
				"aaSorting": [[ 0, "desc" ]],
				"bPaginate": false, //翻页功能
				"bSort": false, //排序功能
				"bInfo": false //页脚信息
			});
		});

		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			var productName = '${productName}';
			var country = '${country}';
			var sTime = '${time}';
			var sType = '${sType}';
			var url = "${ctx}/psi/psiInventory/mobileProductInfoDetail?productName=" + productName + "&sCountry=" + country
			 	+ "&sTime=" + sTime + "&sType=" + sType + "&searchType=" + searchFlag;
			window.location.href = url;
			//$("#searchForm").submit();
		}
		
		
		function display_by(type,country){
			var productName = '${productName}';
			var tip= "";
			if(type==1){
				tip = $("#tranTip");
				tip.find("h5").text(productName+"在途清单");
			}else if(type ==2 ){
				tip = $("#fbaTran");
				tip.find("h5").text(productName+"FBA在途清单");
			}else if(type ==3 ){
				tip = $("#produceTip");
				tip.find("h5").text(productName+"在产清单");
			}else if(type ==4 ){
				tip = $("#preTranTip");
				tip.find("h5").text(productName+"待发货清单");
			}
			var param = {};
			param.type = type;
			param.country = country;
			param.name = productName;
			tip.find("tbody").html("");
			$.get("${ctx}/psi/psiInventory/getTipInfo?"+$.param(param),function(data){
				eval(" var data = "+data);
				var body="";
				if(type==1){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						body +="<tr><td style='vertical-align: middle;text-align: center;'>"+ele.billNo+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.sku+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.remark+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td></tr>";
					}	
				}else if(type ==2 ){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						body +="<tr><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentName+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentId+"</td><td style='vertical-align: middle;text-align: center;'>"
						+ele.sku+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityShipped+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityReceived+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentStatus+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.toDate+"</td></tr>";
					}	
				}else if(type ==3 ){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> "+ele.billNo+"</td><td style='vertical-align: middle;text-align: center;'>"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.orderDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.deliveryDate+"</td><td style='vertical-align: middle;text-align: center;'>"
						+ele.remark+"</td></tr>";
					}	
				}else if(type==4){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> "+ele.billNo+"</td><td style='vertical-align: middle;text-align: center;' >"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.remark+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td></tr>";
					}	
				}
				tip.find("tbody").html(body);
			});
			
			if(type==1){
				$.mobile.changePage('#tranTip', 'pop', true, true);
			}else if(type ==2 ){
				$.mobile.changePage('#fbaTran', 'pop', true, true);
			}else if(type ==3 ){
				$.mobile.changePage('#produceTip', 'pop', true, true);
			}else if(type ==4 ){
				$.mobile.changePage('#preTranTip', 'pop', true, true);
			}
			//tip.modal();
		}
		
		// 对Date的扩展，将 Date 转化为指定格式的String 
		// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
		// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
		// 例子： 
		// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423 
		// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18 
		Date.prototype.Format = function(fmt) 
		{ //author: meizz 
		  var o = { 
		    "M+" : this.getMonth()+1,                 //月份 
		    "d+" : this.getDate(),                    //日 
		    "h+" : this.getHours(),                   //小时 
		    "m+" : this.getMinutes(),                 //分 
		    "s+" : this.getSeconds(),                 //秒 
		    "q+" : Math.floor((this.getMonth()+3)/3), //季度 
		    "S"  : this.getMilliseconds()             //毫秒 
		  }; 
		  if(/(y+)/.test(fmt)) 
		    fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length)); 
		  for(var k in o) 
		    if(new RegExp("("+ k +")").test(fmt)) 
		  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length))); 
		  return fmt; 
		}
		
		function doBack(){
			var country = '${country}';
			var sTime = '${time}';
			var sType = '${sType}';
			var orderType = '${orderType}';
			if((country != null && country.length > 0) && sTime != null && sType != null){
				//${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${sType}&time=${time}&currencyType=${saleReport.currencyType}
				var url = "${ctx}/amazoninfo/salesReprots/productList?country=" + country + "&type=" + sType
			 		+ "&time=" + sTime + "&currencyType=EUR"+ "&orderType=" + orderType;
				window.location.href = url;
			} else {
				//window.location.href = "${ctx}";
				window.location.href = "${ctx}/amazoninfo/salesReprots/mobileList";
			}
		}
		
	</script>
</head>
<body>
<div data-role="page" id="home">
  <div data-role="header" data-theme="b" data-position="fixed">
    <a href="" onclick="doBack()" data-role="button">Back</a>
		<input id="typeahead" type="text" class="span3 search-query" style="width:100%;" 
			placeholder="查看产品" autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
   <a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
  </div>
<div data-role="content">
	<%--
	<input id="typeahead" type="text" class="span3 search-query" style="width:100%;margin-top: 5px" 
		placeholder="查看产品" autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
	 --%>
	<div id="div_product_info">
	<!--table1 -->
	<table class="desc table table-striped table-bordered table-condensed">
		<tbody>
			<tr>
				<td colspan="3">${productName}基础信息
				</td>
			</tr>
			<tr>
				<th>类型</th>
				<th>装箱数</th>
				<th>MOQ</th>
			</tr>
			<tr>
				<td>${product.type}</td>
				<td>${product.packQuantity}</td>
				<td>${product.minOrderPlaced}</td>
			</tr>
			<tr>
				<th>默认运输方式</th>
				<th>生产周期</th>
				<th>单品实际重量</th>
			</tr>
			<tr>
				<td>${transportTypeMap[productName] eq '1'?'海运':'空运'}</td>
				<td>${product.producePeriod}</td>
				<td>${product.tranGw}kg</td>
			</tr>
			<tr>
				<th>体积比</th >
				<th>供应商</th>
				<th>上架日期</th>
			</tr>
			<tr>
				<td>${product.volumeRatio}</td>
				<td>	
					<c:forEach items="${product.psiSuppliers}" var="supplier" varStatus="i">
						${supplier.supplier.nikename}${(!i.last)?',':''}
					</c:forEach>
				</td>
				<td>${fn:replace(product.addedMonth,'00:00:00','')}</td>
			</tr>
			<shiro:hasPermission name="psi:product:viewPrice">
			<tr>
				<th>美金价格</th>
				<th colspan="2">人民币价格</th>
			</tr>
			<tr>
				<td>
					<c:forEach var="supplier" items="${product.psiSuppliers}" varStatus="i">
						<c:if test="${fn:length(product.psiSuppliers)>1}">${supplier.supplier.nikename}:</c:if>
						<c:if test="${not empty purchasePriceMap[supplier.supplier.id]['USD']}">
							<b>${purchasePriceMap[supplier.supplier.id]['USD']}</b>${(!i.last)?',':''} 
							<c:if test="${not empty priceLogMap[supplier.supplier.id]}">
								<span style="color: green;">调价原因:${priceLogMap[supplier.supplier.id]}</span>  <br />
							</c:if> 
						</c:if>
					</c:forEach>
				</td>
				<td colspan="2">
					<c:forEach var="supplier" items="${product.psiSuppliers}" varStatus="i">
						<c:if test="${fn:length(product.psiSuppliers)>1}">${supplier.supplier.nikename}:</c:if>
						<c:if test="${not empty purchasePriceMap[supplier.supplier.id]['CNY']}">
							<b>${purchasePriceMap[supplier.supplier.id]['CNY']}</b>${(!i.last)?',':''}
							 <c:if test="${not empty priceLogMap[supplier.supplier.id]}">
								<span style="color: green;">调价原因:${priceLogMap[supplier.supplier.id]}</span>  <br />
							</c:if> 
						</c:if>
					</c:forEach>
				</td>
			</tr>
			</shiro:hasPermission>
		</tbody>
	</table>
	</div>
	
	<div id="div_instock">
	<!--table2 -->
	<table class="table table-striped table-bordered table-condensed desc">
		<c:set var="producting1"  value="${producting.quantity}"/>
		<c:set var="transportting1"  value="${transportting.quantity}"/>
		<c:set var="transportting2"  value="${preTransportting.quantity}"/>
			
		<c:set var="inventorysCN"  value="${inventorys.totalQuantityCN}"/>
		<c:set var="inventorysNotCN"  value="${inventorys.totalQuantityNotCN}"/>
		<tr>
			<td colspan="4">${productName}库存信息</td>
		</tr>
		<tr>
			<th>在产</th>
			<th>待发货</th>
			<th>在途</th>
		</tr>
			<tr>
				<td>
					<c:if test="${producting1>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'');return false;">
							${producting1}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting2>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'');return false;">
							${transportting2}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting1>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'');return false;">
							${transportting1}
						</a>
					</c:if>
				</td>
			</tr>
		<tr>
			<th>FBA在途</th>
			<th>FBA总</th>
			<th>总库存</th>
		</tr>
			<tr>
				<td>
					<c:set var="de" value="${productName}_de" />
					<c:set var="de"  value="${fbaTran[de]>0?fbaTran[de]:0}" />
					<c:set var="fr" value="${productName}_fr" />
					<c:set var="fr"  value="${fbaTran[fr]>0?fbaTran[fr]:0}" />
					<c:set var="uk" value="${productName}_uk" />
					<c:set var="uk"  value="${fbaTran[uk]>0?fbaTran[uk]:0}" />
					<c:set var="it" value="${productName}_it" />
					<c:set var="it"  value="${fbaTran[it]>0?fbaTran[it]:0}" />
					<c:set var="es" value="${productName}_es" />
					<c:set var="es"  value="${fbaTran[es]>0?fbaTran[es]:0}" />
					<c:set var="fbaEuTran"  value="${de+fr+uk+it+es}" />
					<c:set var="com" value="${productName}_com" />
					<c:set var="com"  value="${fbaTran[com]>0?fbaTran[com]:0}" />
					<c:set var="ca" value="${productName}_ca" />
					<c:set var="ca"  value="${fbaTran[ca]>0?fbaTran[ca]:0}" />
					<c:set var="mx" value="${productName}_mx" />
					<c:set var="mx"  value="${fbaTran[mx]>0?fbaTran[mx]:0}" />
					
					<c:set var="jp" value="${productName}_jp" />
					<c:set var="jp"  value="${fbaTran[jp]>0?fbaTran[jp]:0}" />
					<c:set value="${de+fr+uk+it+es+com+ca+jp+mx}" var="fbaTrans" />
					<c:if test="${fbaTrans>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','');return false;">${fbaTrans}</a>
					</c:if>
				</td>
				<td>
					<c:set var="de" value="${productName}_de" />
					<c:set var="de"  value="${fbas[de].total>0?fbas[de].total:0}" />
					<c:set var="fr" value="${productName}_fr" />
					<c:set var="fr"  value="${fbas[fr].total>0?fbas[fr].total:0}" />
					<c:set var="uk" value="${productName}_uk" />
					<c:set var="uk"  value="${fbas[uk].total>0?fbas[uk].total:0}" />
					<c:set var="it" value="${productName}_it" />
					<c:set var="it"  value="${fbas[it].total>0?fbas[it].total:0}" />
					<c:set var="es" value="${productName}_es" />
					<c:set var="es"  value="${fbas[es].total>0?fbas[es].total:0}" />
					<c:set var="com" value="${productName}_com" />
					<c:set var="com"  value="${fbas[com].total>0?fbas[com].total:0}" />
					<c:set var="ca" value="${productName}_ca" />
					<c:set var="ca"  value="${fbas[ca].total>0?fbas[ca].total:0}" />
					<c:set var="mx" value="${productName}_mx" />
					<c:set var="mx"  value="${fbas[mx].total>0?fbas[mx].total:0}" />
					
					<c:set var="jp" value="${productName}_jp" />
					<c:set var="jp"  value="${fbas[jp].total>0?fbas[jp].total:0}" />
					<c:set value="${de+fr+uk+it+es+com+ca+jp}" var="fbaTotal" />
					${fbaTotal>0?fbaTotal:''}		
				</td>
				<td>
					<c:set var="total" value="${fbaTotal+producting1+transportting1+inventorysCN+inventorysNotCN}" />
					${total>0?total:''}
				</td>
			</tr>
			<tr>
				<th>近31天销量</th>
				<th>平均日销量</th>
				<th>库存销量比 </th>
			</tr>
			<tr>
				<td>${fancha[productName].day31Sales>0?fancha[productName].day31Sales:''}</td>
				<td>${fns:roundUp(fancha[productName].day31Sales/31)}</td>
				<td>
					<c:if test="${total>0 && fancha[productName].day31Sales>0}">
						<fmt:formatNumber value="${total/fancha[productName].day31Sales}" maxFractionDigits="1" />
					</c:if>
				</td>
			</tr>
			<tr>
				<th>中国仓</th>
				<th colspan="2">海外仓</th>
			</tr>
			<tr>
				<td>
					<c:if test="${inventorysCN>0}">
						<div data-role="collapsible" data-mini="true">
      						<h6>总计：${inventorysCN}</h6>
      						<div>${inventorys.cnTip}</div>
    					</div>
					</c:if>
				</td>
				<td colspan="2">
					<c:if test="${inventorysNotCN>0}">
						<div data-role="collapsible" data-mini="true">
      						<h6>总计：${inventorysNotCN}</h6>
      						<div>${inventorys.notCnTip}</div>
    					</div>
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	
	<div>
	<div style="float:left;">
	<table id="orderTbLeft" class="table table-striped table-bordered table-condensed desc" >
		<thead>
			<tr>
				<th rowspan="2" style="line-height: 50px;height:80px;">平台</th>
			</tr>
		</thead>
		<tbody>
			<tr><td style="height:30px;">德国</td></tr>	<!-- detr -->
			<tr><td style="height:30px;">英国</td></tr>	<!-- uktr -->
			<tr><td style="height:30px;">法国</td></tr>	<!-- frtr -->
			<tr><td style="height:30px;">意大利</td></tr>	<!-- ittr -->
			<tr><td style="height:30px;">西班牙</td></tr>	<!-- estr -->
			<tr style="background-color: #d9edf7;"><td style="height:30px;">欧洲汇总</td></tr>	<!-- eurtr -->
			<tr><td style="height:30px;">美国</td></tr>	<!-- comtr -->
			<tr><td style="height:30px;">日本</td></tr>	<!-- jptr -->
			<tr><td style="height:30px;">加拿大</td></tr>	<!-- catr -->
			<tr><td style="height:30px;">墨西哥</td></tr>	<!-- mxtr -->
		</tbody>
	</table>
	</div>
	<div id="div_order" style="overflow:auto">
	<!--table3 -->
	<table id="orderTb" class="table table-striped table-bordered table-condensed desc" >
		<thead>
			<tr>
				<th rowspan="2" style="line-height: 50px;;height:80px">在产</th>
				<th rowspan="2" style="line-height: 50px;">中国仓</th>
				<th rowspan="2" style="line-height: 50px;">待发货</th>
				<th rowspan="2" style="line-height: 50px;">在途</th>
				<th rowspan="2" style="line-height: 50px;">海外仓</th>
				<th colspan="4" style="text-align: center">FBA仓</th>
				<th rowspan="2" style="line-height: 50px;">总库存</th>
				<th colspan="5" style="text-align: center">安全库存</th>
				<th colspan="8" style="text-align: center">预测下单</th>
			</tr>
			<tr>
				<th >实</th>
				<th >途</th>
				<th >总</th>
				<th >可销天</th>
				<th >周期</th>
				<th >方差</th>
				<th >安全<br/>库存量</th>
				<th >近31<br/>日均销</th>
				<th >近31<br/>总计销</th>
				<th >采购期<br/>预日销</th>
				<th >销售期<br/>预月销</th>
				<th >下单点</th>
				<th >结余</th>
				<th >库存<br/>可销天</th>
				<th ><div style="width:80px;text-align:center">可售至</div></th>
				<th >下单量</th>
				<th >空运<br/>补货量</th>
			</tr>
		</thead>
		<tbody>
		<!-- detr -->
			<tr>
				<c:set value="${productName}_de"  var="key"/>
				<c:set value="0"  var="total"/>
				<c:set value="0"  var="euPoint"/>
				<c:set value="0"  var="euJy"/>
				<c:set value="0"  var="euOrder"/>
				<c:set value="0"  var="euSky"/>
				<td style="height:30px;">
					<c:if test="${producting.inventorys['de'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'de');return false;">
							${producting.inventorys['de'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['de'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['de'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['de'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['de'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['de'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'de');return false;">
							${preTransportting.inventorys['de'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['de'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'de');return false;">
							${transportting.inventorys['de'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['de'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
					${inventorys.inventorys['de'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['de'].quantityInventory['DE'].newQuantity:0}
					<c:if test="${inventorys.inventorys['de'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['de'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'de');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				
				<td>${fancha[key].period}</td>
				
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<c:set value="${euPoint+point}"  var="euPoint"/>
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<c:set value="${euJy+jy}"  var="euJy"/>
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<!-- salesday -->
				<td class="salesDay"></td>
				<td class="orderQ" key="de">
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<c:set value="${euOrder+(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}"  var="euOrder"/>
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['de'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['de'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						<c:set value="${fns:roundUp(sky/product.packQuantity)*product.packQuantity+euSky}"  var="euSky"/>
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- uktr -->
			<tr>
				<c:set value="${productName}_uk"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['uk'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'uk');return false;">
							${producting.inventorys['uk'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['uk'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['uk'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['uk'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['uk'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['uk'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'uk');return false;">
							${preTransportting.inventorys['uk'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['uk'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'uk');return false;">
							${transportting.inventorys['uk'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['uk'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity:0}
					<c:if test="${inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'uk');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<c:set value="${euPoint+point}"  var="euPoint"/>
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<c:set value="${euJy+jy}"  var="euJy"/>
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="uk">
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<c:set value="${euOrder+(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}"  var="euOrder"/>
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['uk'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['uk'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						<c:set value="${fns:roundUp(sky/product.packQuantity)*product.packQuantity+euSky}"  var="euSky"/>
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- frtr -->
			<tr>
				<c:set value="${productName}_fr"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['fr'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'fr');return false;">
							${producting.inventorys['fr'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['fr'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['fr'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['fr'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['fr'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['fr'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'fr');return false;">
							${preTransportting.inventorys['fr'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['fr'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'fr');return false;">
							${transportting.inventorys['fr'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['fr'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity:0}
					<c:if test="${inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'fr');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<c:set value="${euPoint+point}"  var="euPoint"/>
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<c:set value="${euJy+jy}"  var="euJy"/>
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="fr">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<c:set value="${euOrder+(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}"  var="euOrder"/>
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['fr'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['fr'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					
					<c:if test="${sky>0}">
						<c:set value="${fns:roundUp(sky/product.packQuantity)*product.packQuantity+euSky}"  var="euSky"/>
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- ittr -->
			<tr>
				<c:set value="${productName}_it"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['it'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'it');return false;">
							${producting.inventorys['it'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['it'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['it'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['it'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['it'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['it'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'it');return false;">
							${preTransportting.inventorys['it'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['it'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'it');return false;">
							${transportting.inventorys['it'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['it'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['it'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['it'].quantityInventory['DE'].newQuantity:0}
					<c:if test="${inventorys.inventorys['it'].quantityInventory['DE'].newQuantity>0}">
						<c:set value="${total+inventorys.inventorys['it'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'it');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<c:set value="${euPoint+point}"  var="euPoint"/>
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<c:set value="${euJy+jy}"  var="euJy"/>
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="it">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<c:set value="${euOrder+(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}"  var="euOrder"/>
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['it'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['it'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						<c:set value="${fns:roundUp(sky/product.packQuantity)*product.packQuantity+euSky}"  var="euSky"/>
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- estr -->
			<tr>
				<c:set value="${productName}_es"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['es'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'es');return false;">
							${producting.inventorys['es'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['es'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['es'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['es'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['es'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['es'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'es');return false;">
							${preTransportting.inventorys['es'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['es'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'es');return false;">
							${transportting.inventorys['es'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['es'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].renewQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].oldQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].brokenQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['es'].quantityInventory['DE'].newQuantity>0?inventorys.inventorys['es'].quantityInventory['DE'].newQuantity:0}
					<c:if test="${inventorys.inventorys['es'].quantityInventory['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['es'].quantityInventory['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'es');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<c:set value="${euPoint+point}"  var="euPoint"/>
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<c:set value="${euJy+jy}"  var="euJy"/>
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="es">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<c:set value="${euOrder+(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}"  var="euOrder"/>
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['es'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['es'].quantityInventory['DE'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						<c:set value="${fns:roundUp(sky/product.packQuantity)*product.packQuantity+euSky}"  var="euSky"/>
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- eurtr -->
			<tr style="background-color: #d9edf7;">
				<c:set value="${productName}_eu"  var="key"/>
				<c:set value="0"  var="total"/>
				<td style="height:30px;">
					<c:if test="${producting.quantityEuro>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'eu');return false;">
							${producting.quantityEuro}
						</a>
						<c:set value="${total+producting.quantityEuro}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.quantityEuro['CN'].newQuantity>0}">
						${inventorys.quantityEuro['CN'].newQuantity}
						<c:set value="${total+inventorys.quantityEuro['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.quantityEuro>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'eu');return false;">
							${preTransportting.quantityEuro}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.quantityEuro>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'eu');return false;">
							${transportting.quantityEuro}
						</a>
						<c:set value="${total+transportting.quantityEuro}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.quantityEuro['DE'].renewQuantity>0?inventorys.quantityEuro['DE'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.quantityEuro['DE'].oldQuantity>0?inventorys.quantityEuro['DE'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.quantityEuro['DE'].brokenQuantity>0?inventorys.quantityEuro['DE'].brokenQuantity:''}" var="broken" />
						${inventorys.quantityEuro['DE'].newQuantity>0?inventorys.quantityEuro['DE'].newQuantity:0}
					<c:if test="${inventorys.quantityEuro['DE'].newQuantity>0}">	
						<c:set value="${total+inventorys.quantityEuro['DE'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaEuTran>0}">
						${fbaEuTran}
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<fmt:formatNumber maxFractionDigits="0" value="${euPoint}" />
				</td>
				<td>
					<fmt:formatNumber maxFractionDigits="0" value="${euJy }" />
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${euJy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${euJy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${euJy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${euJy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td>
					<c:if test="${euOrder>0}">
						<span class="${euJy<=0?'badge badge-important':''}">${(fns:roundUp((euOrder)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:if test="${euSky>0}">
						${fns:roundUp(euSky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- comtr -->
			<tr>
				<c:set value="${productName}_com"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['com'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'com');return false;">
							${producting.inventorys['com'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['com'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['com'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['com'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['com'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['com'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'com');return false;">
							${preTransportting.inventorys['com'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['com'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'com');return false;">
							${transportting.inventorys['com'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['com'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['com'].quantityInventory['US'].newQuantity>0?inventorys.inventorys['com'].quantityInventory['US'].newQuantity:0}
					<c:if test="${inventorys.inventorys['com'].quantityInventory['US'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['com'].quantityInventory['US'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'com');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="com">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['com'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['com'].quantityInventory['US'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- jptr -->
			<tr>
				<c:set value="${productName}_jp"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['jp'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'jp');return false;">
							${producting.inventorys['jp'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['jp'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['jp'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['jp'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['jp'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['jp'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'jp');return false;">
							${preTransportting.inventorys['jp'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['jp'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'jp');return false;">
							${transportting.inventorys['jp'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['jp'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].renewQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].oldQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].brokenQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity>0?inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity:0}
					<c:if test="${inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'jp');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="jp">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['jp'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['jp'].quantityInventory['JP'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- catr -->
			<tr>
				<c:set value="${productName}_ca"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['ca'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'ca');return false;">
							${producting.inventorys['ca'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['ca'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['ca'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['ca'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['ca'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['ca'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'ca');return false;">
							${preTransportting.inventorys['ca'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['ca'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'ca');return false;">
							${transportting.inventorys['ca'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['ca'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['ca'].quantityInventory['US'].newQuantity>0?inventorys.inventorys['ca'].quantityInventory['US'].newQuantity:0}
					<c:if test="${inventorys.inventorys['ca'].quantityInventory['US'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['ca'].quantityInventory['US'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'ca');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					${fancha[key].variance}
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${fancha[key].forecastPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="ca">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].forecastAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].forecastAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['ca'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['ca'].quantityInventory['US'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].forecastPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
			
			<!-- mxtr -->
			<tr>
				<c:set value="${productName}_mx"  var="key"/>
				<c:set value="0"  var="total"/>
				<td>
					<c:if test="${producting.inventorys['mx'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'mx');return false;">
							${producting.inventorys['mx'].quantity}
						</a>
						<c:set value="${total+producting.inventorys['mx'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${inventorys.inventorys['mx'].quantityInventory['CN'].newQuantity>0}">
						${inventorys.inventorys['mx'].quantityInventory['CN'].newQuantity}
						<c:set value="${total+inventorys.inventorys['mx'].quantityInventory['CN'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${preTransportting.inventorys['mx'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'mx');return false;">
							${preTransportting.inventorys['mx'].quantity}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${transportting.inventorys['mx'].quantity>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'mx');return false;">
							${transportting.inventorys['mx'].quantity}
						</a>
						<c:set value="${total+transportting.inventorys['mx'].quantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].renewQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].renewQuantity:''}" var="renew" />
					<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].oldQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].oldQuantity:''}" var="old" />
					<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].brokenQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].brokenQuantity:''}" var="broken" />
						${inventorys.inventorys['mx'].quantityInventory['US'].newQuantity>0?inventorys.inventorys['mx'].quantityInventory['US'].newQuantity:0}
					<c:if test="${inventorys.inventorys['mx'].quantityInventory['US'].newQuantity>0}">	
						<c:set value="${total+inventorys.inventorys['mx'].quantityInventory['US'].newQuantity}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0}">
						${fbas[key].fulfillableQuantity}
					</c:if>
				</td>
				<td>
					<c:if test="${fbaTran[key]>0}">
						<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'mx');return false;">
							${fbaTran[key]}
						</a>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].total>0}">
						${fbas[key].total}
						<c:set value="${total+fbas[key].total}"  var="total"/>
					</c:if>
				</td>
				<td>
					<c:if test="${fbas[key].fulfillableQuantity>0&&fancha[key].day31Sales>0}">
						${fns:roundUp(fbas[key].fulfillableQuantity/(fancha[key].day31Sales/31))}
					</c:if>
				</td>
				<td>
					${total}
				</td>
				<td>${fancha[key].period}</td>
				<td>
					<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="<span style='word-wrap:break-word;word-break: normal'>样本：${fancha[key].samplingData}</span>">
						${fancha[key].variance}
					</a>
				</td>
				<td>
					<c:set var="safe" value="0"></c:set>
					<c:if test="${fancha[key].variance>0}">
						<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
						${safe}
					</c:if> 
				</td>
				
				<c:if test="${safe>0}">
					<c:choose>
						<c:when test="${fancha[key].foremxstPreiodAvg >0 }">
							<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].foremxstPreiodAvg}" />
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
						</c:when>
					</c:choose>
				</c:if>	
				<td>${fancha[key].day31Sales>0?fns:roundUp(fancha[key].day31Sales/31):''}</td>	
				<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
				<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
				<td>
					<c:set value="0" var="point"></c:set>
					<c:choose>
						<c:when test="${fancha[key].foremxstPreiodAvg >0 }">
							<c:set value="${fancha[key].foremxstPreiodAvg*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].day31Sales/31)*fancha[key].period+safe}" var="point"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${point>0}">
						<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
					</c:if>
				</td>
				<td>
					<c:set value="${total-point}"  var="jy"/>
					<c:if test="${jy!=0}">
						<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
							<c:set var="bu" value="${jy<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
							<span style="${bu?'color:red':'color:#08c'} "><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}" pattern="#0" /></span>
						</c:when>
					</c:choose>
				</td>
				<td class="salesDay"></td>
				<td class="orderQ" key="mx">
					<c:set value="${total-point}"  var="jy"/>
					<c:set value="${empty fancha[key].foremxstAfterPreiodSalesByMonth?fancha[key].day31Sales:fancha[key].foremxstAfterPreiodSalesByMonth}" var="sale" />
					<c:if test="${sale-jy>0}">
						<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((sale-jy)/product.packQuantity)*product.packQuantity)}</span>
					</c:if>
				</td>
				<td>
					<c:set value="${transportting.inventorys['mx'].quantity}" var="transportVar" />
					<c:set value="${inventorys.inventorys['mx'].quantityInventory['US'].newQuantity}" var="deNew" />
					<c:set value="0" var="sky"></c:set>
					<c:choose>
						<c:when test="${fancha[key].foremxstPreiodAvg >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp(fancha[key].foremxstPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
						<c:when test="${fancha[key].day31Sales >0 }">
							<c:set value="${(fancha[key].period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
						</c:when>
					</c:choose>
					<c:if test="${sky>0}">
						${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	</div>
	
	<form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/psi/psiInventory/mobileProductInfoDetail" method="post" class="breadcrumb form-search">
		<div style="height: 30px;margin-top:10px">
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false">By Month</a></li>
		</ul>
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		</div>
	</form:form>
	
	<!-- salesTable -->
	<div>
		<div style="float:left;">
			<table id="saleVolumeTbLeft" class="table table-striped table-bordered table-condensed desc">
			<thead>
				<tr><th><div style="width:75px">日期</div></th></tr>
			</thead>
			<tbody>
				<tr class="alert alert-block"><td><b style="font-size: 18px">差评率</b></td></tr>
				<tr class="alert alert-error"><td><b style="font-size: 18px">退货率</b></td></tr>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<c:set var="x" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
				<tr><td>${x}${type}</td></tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr><td><b style="font-size: 18px">合计</b></td></tr>
			</tfoot>
			</table>
		</div>
		<div id="saleVolume" style="overflow:auto">
			<table id="saleVolumeTb" class="table table-striped table-bordered table-condensed desc">
				<thead>
					<tr>
						<c:choose>
							<c:when test="${'1' eq saleReport.searchType}">
								<th><div style="width:40px">星期</div></th>
							</c:when>
							<c:otherwise>
								<th><div style="width:150px">区间</div></th>
							</c:otherwise>
						</c:choose>
						<th>德国</th>
						<th>英国</th>
						<th>法国</th>
						<th>意大利</th>
						<th>西班牙</th>
						<th>欧洲</th>
						<th>美国</th>
						<th>日本</th>
						<th>加拿大</th>
						<th>墨西哥</th>
						<th>全球</th>
					</tr>
				</thead>
				
				<tbody>
					<tr class="alert alert-block">
						<td></td>
						<td>
							<c:if test="${not empty returnGoods['de'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['de'][1]}<br/>退货量:${returnGoods['de'][2]}">
									${returnGoods['de'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['uk'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['uk'][1]}<br/>退货量:${returnGoods['uk'][2]}">
									${returnGoods['uk'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['fr'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['fr'][1]}<br/>退货量:${returnGoods['fr'][2]}">
								${returnGoods['fr'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['it'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['it'][1]}<br/>退货量:${returnGoods['it'][2]}">
								${returnGoods['it'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['es'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['es'][1]}<br/>退货量:${returnGoods['es'][2]}">
								${returnGoods['es'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['eu'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['eu'][1]}<br/>退货量:${returnGoods['eu'][2]}">
								${returnGoods['eu'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['com'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['com'][1]}<br/>退货量:${returnGoods['com'][2]}">
								${returnGoods['com'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['jp'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['jp'][1]}<br/>退货量:${returnGoods['jp'][2]}">
								${returnGoods['jp'][5]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['ca'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['ca'][1]}<br/>退货量:${returnGoods['ca'][2]}">
								${returnGoods['ca'][5]}%
								</a>
							</c:if>
						</td>
						<td>		
							<c:if test="${not empty returnGoods['mx'][5]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['mx'][1]}<br/>退货量:${returnGoods['mx'][2]}">
								${returnGoods['mx'][5]}%
								</a>
							</c:if>
						</td>						
						<td>
							<c:if test="${not empty returnGoods['total'][5]}">
								<a style="color: #08c;" href="#" data-placement="bottom" data-toggle="popover" data-html="true" rel="popover" data-content="确认销量:${returnGoods['total'][1]}<br/>退货量:${returnGoods['total'][2]}">
								${returnGoods['total'][5]}%
								</a>
							</c:if>
						</td>
					</tr>
					<tr class="alert alert-error">
						<td></td>
						<td>
							<c:if test="${not empty returnGoods['de'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['de'][4]}<br/>差评量:${returnGoods['de'][3]}">
									${returnGoods['de'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['uk'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['uk'][4]}<br/>差评量:${returnGoods['uk'][3]}">
								${returnGoods['uk'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['fr'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['fr'][4]}<br/>差评量:${returnGoods['fr'][3]}">
								${returnGoods['fr'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['it'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['it'][4]}<br/>差评量:${returnGoods['it'][3]}">
								${returnGoods['it'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['es'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['es'][4]}<br/>差评量:${returnGoods['es'][3]}">
								${returnGoods['es'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['eu'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['eu'][4]}<br/>差评量:${returnGoods['eu'][3]}">
								${returnGoods['eu'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['com'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['com'][4]}<br/>差评量:${returnGoods['com'][3]}">
								${returnGoods['com'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['jp'][6]}">
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['jp'][4]}<br/>差评量:${returnGoods['jp'][3]}">
								${returnGoods['jp'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['ca'][6]}">
								<a style="color: #08c;" href="#"  data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['ca'][4]}<br/>差评量:${returnGoods['ca'][3]}">
								${returnGoods['ca'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['mx'][6]}">
								<a style="color: #08c;" href="#"  data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['mx'][4]}<br/>差评量:${returnGoods['mx'][3]}">
								${returnGoods['mx'][6]}%
								</a>
							</c:if>
						</td>
						<td>
							<c:if test="${not empty returnGoods['total'][6]}">
								<a style="color: #08c;" href="#" data-placement="bottom"  data-toggle="popover" data-html="true" rel="popover" data-content="确认订单量:${returnGoods['total'][4]}<br/>差评量:${returnGoods['total'][3]}">
								${returnGoods['total'][6]}%
								</a>
							</c:if>
						</td>
					</tr>
					<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="x" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<c:set value="0" var="eu" />
							<c:set value="0" var="total" />
							<td>${tip[x]}</td>
							<td class="sale1" style="white-space: nowrap">
								<c:if test="${data[x]['de'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=de&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small sale1" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['de'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['de'].salesVolume}" var="eu" />
								</c:if>
								<c:if test="${otherData[x]['2-de'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&orderType=2-de&lineType=total" class="btn btn-small btn-success deSale1" style="height:14px; font-size:12px; line-height:12px;">
										${otherData[x]['2-de'].salesVolume}
									</a>
								</c:if>
							</td>
							<td class="sale2">
								
								<c:if test="${data[x]['uk'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=uk&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['uk'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['uk'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale3">
								<c:if test="${data[x]['fr'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=fr&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['fr'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['fr'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale4">
								<c:if test="${data[x]['it'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=it&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['it'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['it'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale5">
								<c:if test="${data[x]['es'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=es&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['es'].salesVolume}
									</a>
									<c:set value="${eu+data[x]['es'].salesVolume}" var="eu" />
								</c:if>
							</td>
							<td class="sale6">
								<c:if test="${eu>0}">
									${eu}
									<c:set value="${total+eu}" var="total" />
								</c:if>
							</td>
							<td class="sale7">
								
								<c:if test="${data[x]['com'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=com&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['com'].salesVolume}
									</a>
									<c:set value="${total+data[x]['com'].salesVolume}" var="total" />
								</c:if>
								<%--
								<c:if test="${otherData[x]['2-com'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&orderType=2-com" class="btn btn-small btn-warning" style="height:14px; font-size:12px; line-height:12px;">
										${otherData[x]['2-com'].salesVolume}
									</a>
								</c:if> --%>
							</td>
							<td class="sale8">
								<c:if test="${data[x]['jp'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=jp&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['jp'].salesVolume}
									</a>
									<c:set value="${total+data[x]['jp'].salesVolume}" var="total" />
								</c:if>
							</td>
							<td class="sale9">
								<c:if test="${data[x]['ca'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=ca&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['ca'].salesVolume}
									</a>
									<c:set value="${total+data[x]['ca'].salesVolume}" var="total" />
								</c:if>
							</td>
							<td class="sale11">
								<c:if test="${data[x]['mx'].salesVolume>0}">
									<a href="${ctx}/amazoninfo/salesReprots/orderList?country=mx&type=${saleReport.searchType}&time=${x}&productName=${productName}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small" style="height:14px; font-size:12px; line-height:12px;">
										${data[x]['mx'].salesVolume}
									</a>
									<c:set value="${total+data[x]['mx'].salesVolume}" var="total" />
								</c:if>
							</td>
							<td class="sale10">
								<c:if test="${total>0}">
									${total}
								</c:if>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td></td>
						<td id="t1" style="white-space: nowrap"></td>
						<td id="t2"></td>
						<td id="t3"></td>
						<td id="t4"></td>
						<td id="t5"></td>
						<td id="t6"></td>
						<td id="t7"></td>
						<td id="t8"></td>
						<td id="t9"></td>
						<td id="t11"></td>
						<td id="t10"></td>
					</tr>
					
				</tfoot>
			</table> <!-- salesTable end -->
		</div>
	</div>
	</div> <!-- content end -->
	<!-- /footer -->
	<jsp:include page="../sys/footDiv.jsp"></jsp:include>
	<!-- /footer -->
	</div>

	<%-- tipInfo --%>
	<div data-role="page" id="fbaTran" data-overlay-theme="e" >
		<div data-role="header" data-theme="b">
			<a href="#home">Back</a>
    		<h4>${productName}</h4>
  		</div>
		<div data-role="content" style="overflow:auto">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr><th colspan="7"><h5></h5></th></tr>
					<tr>
						<th style="text-align: center;vertical-align: middle;">ShipmentName</th>
						<th style="text-align: center;vertical-align: middle;">ShipmentId</th>
						<th>SKU</th>
						<th style="text-align: center;vertical-align: middle;">发货数量</th>
						<th style="text-align: center;vertical-align: middle;">已收数量</th>
						<th style="text-align: center;vertical-align: middle;">状态</th>
						<th style="text-align: center;vertical-align: middle;">预计到达</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<jsp:include page="../sys/footDiv.jsp"></jsp:include>
	</div>
	
	<div data-role="page" id="tranTip" data-overlay-theme="e" >
		<div data-role="header" data-theme="b">
			<a href="#home">Back</a>
    		<h4>${productName}</h4>
  		</div>
		<div data-role="content" style="overflow:auto">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr><th colspan="7"><h5></h5></th></tr>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">单号</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">方式</th>
						<th>Sku</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">去向仓库</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">数量</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">出仓日期</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">预计入仓</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<jsp:include page="../sys/footDiv.jsp"></jsp:include>
	</div>
	
	<div data-role="page" id="preTranTip" data-overlay-theme="e" >
		<div data-role="header" data-theme="b">
			<a href="#home">Back</a>
    		<h4>${productName}</h4>
  		</div>
		<div data-role="content" style="overflow:auto">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr><th colspan="7"><h5></h5></th></tr>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">单号</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">产品国家</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">方式</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">去向地</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">数量</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">预计出仓日期</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">预计入仓</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<jsp:include page="../sys/footDiv.jsp"></jsp:include>
	</div>
	
	<div data-role="page" id="produceTip" data-overlay-theme="e" >
		<div data-role="header" data-theme="b">
			<a href="#home">Back</a>
    		<h4>${productName}</h4>
  		</div>
		<div data-role="content" style="overflow:auto">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr><th colspan="7"><h5></h5></th></tr>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">订单号</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">国家</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">数量</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">下单时间</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">订单交期</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">预计交期</th>
						<th style="width: 200px;text-align: center;vertical-align: middle;">说明</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
		</div>
		<jsp:include page="../sys/footDiv.jsp"></jsp:include>
	</div>
</body>
</html>
