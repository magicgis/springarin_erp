<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>${fns:getDictLabel(country,'platform','eu' eq country?'欧洲':'合计')}${'total' eq lineType?'':allLine[lineType] }销售报告${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}产品明细</title>
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

	.btn-special {
		    color: #fff;
		    text-shadow: 0 -1px 0 rgba(0,0,0,0.25);
		    background-color: #DA70D6;
		    background-image: -moz-linear-gradient(top,#DA70D6,#DA70D6);
		    background-image: -webkit-gradient(linear,0 0,0 100%,from(#DA70D6),to(#DA70D6));
		    background-image: -webkit-linear-gradient(top,#DA70D6,#DA70D6);
		    background-image: -o-linear-gradient(top,#DA70D6,#DA70D6);
		    background-image: linear-gradient(to bottom,#DA70D6,#DA70D6);
		    background-repeat: repeat-x;
		    border-color: #DA70D6 #DA70D6 #DA70D6;
		    border-color: rgba(0,0,0,0.1) rgba(0,0,0,0.1) rgba(0,0,0,0.25);
		    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#DA70D6',endColorstr='#DA70D6',GradientType=0);
		    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
       }
       
       .btn-special:hover, .btn-special:focus, .btn-special:active, .btn-special.active, .btn-special.disabled, .btn-special[disabled] {
			    color: #fff;
			    background-color: #DA70D6;
        }

</style>

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
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			var txt = $('td:eq(1)', tr).text();
			var txt1 = $('td:eq(10)', tr).text();
			if((txt && txt.indexOf('未匹配')>0) || '0'==txt1){
				return -1000000;
			}else{
				return parseFloat($('td:eq('+iColumn+')', tr).text().split(',').join(''));
			}
		});
	};
	
	$.fn.dataTableExt.afnSortData['dom-html2'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			var rs = 0 ;
			var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
			var clsAttr = a.attr("class");
			if(clsAttr){
				if(clsAttr.indexOf('info')>0){
					rs = parseInt(a.text().split(",").join(''))*1000000000000000000000;
				}if(clsAttr.indexOf('pecial')>0){
					rs = parseInt(a.text().split(",").join(''))*1000000000000000000;
				}else if(clsAttr.indexOf('warning')>0){
					rs = parseInt(a.text().split(",").join(''))*1000000000000000;
				}else if (clsAttr.indexOf('danger')>0){
					rs = parseInt(a.text().split(",").join(''))*1000000000000;
				}else if (clsAttr.indexOf('primary')>0){
					rs = parseInt(a.text().split(",").join(''))*1000000000;
				}else if (clsAttr.indexOf('success')>0){
					rs = parseInt(a.text().split(",").join(''))*1000000;
				}else if (clsAttr.indexOf('inverse')>0){
					rs = parseInt(a.text().split(",").join(''))*1000;
				}else{
					rs =parseInt(a.text().split(",").join(''));
				}
				
			}
			return rs;
		} );
	}	
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+')', tr).text().replace("%","");
		} );
	};
	
	$(function() {
	
		$("a[rel='popover']").popover({trigger:'hover'});	
		
		var arr = $("#contentTable tbody tr");
		var num1 = 0;
		var num2 = 0;
		var num3 = 0;
		var num4 = 0;
		var num5 = 0;
		var num6 = 0;
		var num7 = 0;
		var num8 = 0;
		var num9 = 0;
		var num10 = 0;
		
		var num11=0;
		var num12=0;	//广告费用
		var num13=0;	//广告销量
		var num14=0;	//广告销售额
		var num15=0;	//替代货
		var num16=0;	//评测单
		
		
		
		arr.each(function() {
			
			
			if($(this).find("td :eq(2)").text())
			num1 += parseInt($(this).find("td :eq(2)").text().split(",").join(''));	//销量
			if($(this).find("td :eq(3)").text())
			num2 += parseFloat($(this).find("td :eq(3)").text().split(",").join(''));	//销售额
			if($(this).find("td :eq(6)").text())
			num3 += parseInt($(this).find("td :eq(6)").text().split(",").join(''));	//已确认费用数量
			
			<c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
			<shiro:hasPermission name="amazoninfo:sale:run">
			if($.isNumeric($(this).find("td :eq(7)").text().split(",").join('')))
			num4 += parseFloat($(this).find("td :eq(7)").text().split(",").join(''));	//税后收入
			if($(this).find("td :eq(8)").text())
			num5 += parseFloat($(this).find("td :eq(8)").text().split(",").join(''));
			if($(this).find("td :eq(9)").text())
			num6 += parseFloat($(this).find("td :eq(9)").text().split(",").join(''));
			if($(this).find("td :eq(10)").text())
			num7 += parseFloat($(this).find("td :eq(10)").text().split(",").join(''));
			num8 += parseFloat($(this).find("td :eq(11)").text().split(",").join(''));
			num9 += parseFloat($(this).find("td :eq(12)").text().split(",").join(''));
			if($.isNumeric($(this).find("td :eq(13)").text().split(",").join('')))
			num10 += parseFloat($(this).find("td :eq(13)").text().split(",").join(''));
			if($.isNumeric($(this).find("td :eq(16)").text().split(",").join('')))
				num12 += parseFloat($(this).find("td :eq(16)").text().split(",").join(''));
			if($.isNumeric($(this).find("td :eq(17)").text().split(",").join('')))
				num13 += parseInt($(this).find("td :eq(17)").text().split(",").join(''));
			if($.isNumeric($(this).find("td :eq(18)").text().split(",").join('')))
				num14 += parseFloat($(this).find("td :eq(18)").text().split(",").join(''));
			if($.isNumeric($(this).find("td :eq(19)").text().split(",").join('')))
				num15 += parseFloat($(this).find("td :eq(19)").text().split(",").join(''));
			if($.isNumeric($(this).find("td :eq(20)").text().split(",").join('')))
				num16 += parseFloat($(this).find("td :eq(20)").text().split(",").join(''));
			</shiro:hasPermission>
			</c:if>
			 <c:if test="${not empty priceMap &&'en' ne country &&'eu' ne country &&'total' ne country}">
			 if(parseFloat($(this).find("td :eq(7)").text().split(",").join('')))
				num11+= parseFloat($(this).find("td :eq(7)").text().split(",").join(''));
			 </c:if>
		});

		var tr = $("#contentTable tfoot tr#totalTr");
		tr.find("td :eq(2)").text(fmoney(num1.toFixed(0),0));
		tr.find("td :eq(3)").text(fmoney(num2.toFixed(2),2));
		
		var totalV=0;
		var totalS=0;
		arr.each(function() {
			   if($(this).find("td :eq(2)").text()){
				   var tempV=parseInt($(this).find("td :eq(2)").text().split(",").join(''));	//销量
				   totalV += tempV;
				   $(this).find("td :eq(4)").text(fmoney((parseInt(tempV)*100/parseInt(num1)).toFixed(2),2)+"%");
			   }
				
			   if($(this).find("td :eq(3)").text()){
					var tempS=parseFloat($(this).find("td :eq(3)").text().split(",").join(''));	//销售额
					totalS += tempS;
					$(this).find("td :eq(5)").text(fmoney((parseFloat(tempS)*100/parseFloat(num2)).toFixed(2),2)+"%");
			   }
		});
		$("#allPV").html(fmoney((parseInt(totalV)*100/parseInt(num1)).toFixed(2),2)+"%");
	    $("#allPS").html(fmoney((parseFloat(totalS)*100/parseFloat(num2)).toFixed(2),2)+"%");
	   
		<c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
		<shiro:hasPermission name="amazoninfo:sale:run">
		tr.find("td :eq(6)").text(fmoney(num3,0));
		tr.find("td :eq(7)").text(fmoney(num4.toFixed(2),2));
		tr.find("td :eq(8)").text(fmoney(num5.toFixed(2),2));
		tr.find("td :eq(9)").text(fmoney(num6.toFixed(2),2));
		tr.find("td :eq(10)").text(fmoney(num7.toFixed(2),2));
		tr.find("td :eq(11)").text(fmoney(num8.toFixed(2),2));
		tr.find("td :eq(12)").text(fmoney(num9.toFixed(2),2));
		tr.find("td :eq(13)").text(fmoney(num10.toFixed(2),2));
		tr.find("td :eq(16)").text(fmoney(num12.toFixed(2),2));
		tr.find("td :eq(17)").text(fmoney(num13.toFixed(0),0));
		tr.find("td :eq(18)").text(fmoney(num14.toFixed(2),2));
		tr.find("td :eq(19)").text(fmoney(num15.toFixed(2),2));
		tr.find("td :eq(20)").text(fmoney(num16.toFixed(2),2));
		tr.find("td :eq(15)").html(fmoney((parseFloat(num10)/parseInt(totalV)).toFixed(2),2));
		tr.find("td :eq(14)").html("100%");
		arr.each(function() {
			if($.isNumeric($(this).find("td :eq(13)").text().split(",").join(''))){
				$(this).find("td :eq(14)").text((parseFloat($(this).find("td :eq(13)").text().split(",").join(''))*100/num10).toFixed(2)+'%');
			} else {
				$(this).find("td :eq(14)").text("0%");
			}
		});
		</shiro:hasPermission>
		</c:if>
		 <c:if test="${not empty priceMap &&'en' ne country &&'eu' ne country &&'total' ne country }">
		 	tr.find("td :eq(7)").text(fmoney(num11.toFixed(2),2));
		 </c:if>
		
		
		
		$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
							     { "sSortDataType":"dom-html2", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     <c:if test="${flag && 'EUR' eq fn:trim(currencyType)}">
								<shiro:hasPermission name="amazoninfo:sale:run">
							     ,{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" }
							     </shiro:hasPermission>
								 </c:if>
								 <c:if test="${not empty priceMap&&'en' ne country &&'eu' ne country &&'total' ne country }">
								  ,{ "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html1", "sType":"numeric" }
								 </c:if>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
				,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             if(iDisplayIndex==0){
		            	 addd=0;
		            	 addd1=0;
		            	 addd2=0;
		            	 addd3=0;
					     <c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
						 <shiro:hasPermission name="amazoninfo:sale:run">
		            	 	addd4=0;
		            	 	addd5=0;
		            	 	addd6=0;
		            	 	addd7=0;
		            	 	addd8=0;
		            	 	addd9=0;
		            	 	addd10=0;
		            	 	addd11=0;
		            	 	addd12=0;
		            	 	addd13=0;
		            	 	addd14=0;
					     </shiro:hasPermission>
						 </c:if>
		             }
		             <%--
		             if($.isNumeric(aData[2])){
		            	 addd+=parseInt(aData[2]);//第几列
		             }else{
		            	 addd+=parseInt($(aData[2]).text().split(",").join(''));//第几列
					 }--%>
		             addd+=parseInt($(nRow).attr("key"));//第几列
		             if($.isNumeric(aData[3])){
		            	 addd1+=parseFloat(aData[3]);//第几列
		             }else if($.isNumeric(aData[3].split(",").join(''))){
		            	 addd1+=parseFloat(aData[3].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[6])){
		            	 addd2+=parseFloat(aData[6]);
		             } else {
		            	 addd2+=parseFloat($(aData[6]).text().split(",").join(''));
		             }
		             <c:if test="${(not empty priceMap &&'en' ne country &&'eu' ne country &&'total' ne country) || 'EUR' eq fn:trim(currencyType) }">
		            	 if($.isNumeric(aData[7])){
			            	 addd3+=parseFloat(aData[7]);
			             } else if(aData[7] && $.isNumeric(aData[7].split(",").join(''))){
			            	 addd3+=parseFloat(aData[7].split(",").join(''));
			             }
					 </c:if>
		             $("#total").html(fmoney(addd,0));
		             $("#totalSales").html(fmoney(addd1,2));
		             $("#totalPV").html(fmoney((parseInt(addd)*100/parseInt(num1)).toFixed(2),2)+"%");
			         $("#totalPS").html(fmoney((parseFloat(addd1)*100/parseFloat(num2)).toFixed(2),2)+"%");
				     <c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
					 <shiro:hasPermission name="amazoninfo:sale:run">
						 var data = aData[8];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[8]).text();
						 }
						 if($.isNumeric(data)){
							addd4 += parseFloat(data);
						 } else {
							addd4 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[9];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[9]).text();
						 }
						 if($.isNumeric(data)){
							addd5 += parseFloat(data);
						 } else {
							addd5 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[10];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[10]).text();
						 }
						 if($.isNumeric(data)){
							addd6 += parseFloat(data);
						 } else {
							addd6 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[11];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[11]).text();
						 }
						 if($.isNumeric(data)){
							addd7 += parseFloat(data);
						 } else {
							addd7 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[12];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[12]).text();
						 }
						 if($.isNumeric(data)){
							addd8 += parseFloat(data);
						 } else {
							addd8 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[13];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[13]).text();
						 }
						 if($.isNumeric(data)){
							addd9 += parseFloat(data);
						 } else {
							addd9 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[16];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[16]).text();
						 }
						 if($.isNumeric(data)){
							addd10 += parseFloat(data);
						 } else {
							addd10 += parseFloat(data.split(',').join(''));
						 }
						 data = aData[17];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[17]).text();
						 }
						 if($.isNumeric(data)){
							addd11+= parseFloat(data);
						 } else {
							addd11+= parseFloat(data.split(',').join(''));
						 }
						 data = aData[18];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[18]).text();
						 }
						 if($.isNumeric(data)){
							addd12+= parseFloat(data);
						 } else {
							addd12+= parseFloat(data.split(',').join(''));
						 }
						 data = aData[19];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[19]).text();
						 }
						 if($.isNumeric(data)){
							addd13+= parseFloat(data);
						 } else {
							addd13+= parseFloat(data.split(',').join(''));
						 }
						 data = aData[20];
						 if(!$.isNumeric(data) && data.indexOf('</') > 0){
							 data = $(aData[20]).text();
						 }
						 if($.isNumeric(data)){
							addd14+= parseFloat(data);
						 } else {
							addd14+= parseFloat(data.split(',').join(''));
						 }
		             	$("#confirmNum").html(fmoney(addd2,0));
		             	$("#salesNoTax").html(fmoney(addd3,2));
		             	$("#refund").html(fmoney(addd4,2));
		             	$("#amazonFee").html(fmoney(addd5,2));
		             	$("#otherFee").html(fmoney(addd6,2));
		             	$("#transportFee").html(fmoney(addd7,2));
		             	$("#butCost").html(fmoney(addd8,2));
		             	$("#totalProfit").html(fmoney(addd9,2));
		             	$("#adFee").html(fmoney(addd10,2));
		             	$("#adSalesVolume").html(fmoney(addd11,0));
		             	$("#adSales").html(fmoney(addd12,2));
		             	$("#supportFee").html(fmoney(addd13,2));
		             	$("#reviewFee").html(fmoney(addd14,2));
				        $("#pf").html((parseFloat(addd9)*100/num10).toFixed(2)+'%');
				        $("#sf").html((parseFloat(addd9)/addd).toFixed(2));
		             	</shiro:hasPermission>
					 </c:if>
					 <c:if test="${not empty priceMap &&'en' ne country &&'eu' ne country &&'total' ne country }">
					 	$("#totalProfit").html(fmoney(addd3,2));
					 </c:if>
		             return nRow;
		         },"fnPreDrawCallback": function( oSettings ) { 
		        	  $("#total").html(0);
			          $("#totalSales").html(0);
			          $("#totalPV").html(0);
			          $("#totalPS").html(0);
				     <c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
					 <shiro:hasPermission name="amazoninfo:sale:run">
		             	$("#confirmNum").html(0);
		             	$("#salesNoTax").html(0);
		             	$("#refund").html(0);
		             	$("#amazonFee").html(0);
		             	$("#otherFee").html(0);
		             	$("#transportFee").html(0);
		             	$("#butCost").html(0);
		             	$("#totalProfit").html(0);
		             	$("#adFee").html(0);
		             	$("#adSalesVolume").html(0);
		             	$("#adSales").html(0);
		             	$("#supportFee").html(0);
		             	$("#reviewFee").html(0);
				     </shiro:hasPermission>
					 </c:if>
					 <c:if test="${not empty priceMap &&'en' ne country &&'eu' ne country &&'total' ne country }">
					 	$("#totalProfit").html(0);
					 </c:if>
		         }  
		});
		
		
		
		<%-- 货币美元&欧元切换,默认为欧元版	 --%>
		var html1 = " 货币类型:<select name=\"currencyType\" id=\"currencyType\" style=\"width: 100px\" onchange=\"changeCurrencyType()\">"+
			"<option value=\"EUR\" ${'EUR' eq fn:trim(currencyType)?'selected':''}>EUR</option>"+
			"<option value=\"USD\" ${'USD' eq fn:trim(currencyType)?'selected':''}>USD</option></select> &nbsp;&nbsp;&nbsp;";
		
		var html = "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">订单</a>&nbsp;"+
		"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/skuList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">Sku</a>&nbsp;"+
		"<a class=\"btn btn-info btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品</a>&nbsp;"+
		<c:if test="${'3' eq byTime}">
		    "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productListByDate?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品按日</a>&nbsp;"+
		</c:if>
		"<a class=\"btn btn-warning btn-small\" target=\"_blank\"  href=\"${ctx}/amazoninfo/amazonProduct/businessPriceList\">B2B价格</a>&nbsp;"+
		"<%--<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/exportAll?flag=2&country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">导出</a>&nbsp;--%> ";
		
		$("#contentTbDiv .spanexr div:first").append(html1 + html);
	});
	
	function changeCurrencyType(){
		var currencyType = $("#currencyType").val();
		var url = "${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=" + currencyType;
		window.location.href = url;
	}
	
	
	function searchTypes(searchFlag){
		var currencyType = $("#currencyType").val();
		var dateStr="${dateStr}";
		var month=dateStr.substr(0,6);
		var url = "${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=3&time="+month+"&orderType=${orderType}&lineType=${lineType}&currencyType=" + currencyType;
		window.location.href = url;
	}

</script>
</head>
<body>
	<div id="contentTbDiv" style="width:${flag?'1200px':'800px'};margin: auto">
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${country eq 'eu'}">欧洲</c:when>
			<c:when test="${country eq 'en'}">英语国家</c:when>
			<c:when test="${country eq 'unEn'}">非英语国家</c:when>
			<c:otherwise>${fns:getDictLabel(country,'platform','合计')}</c:otherwise>
		</c:choose>
			${'total' eq lineType?'':allLine[lineType] }销售报告${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}产品明细
			<c:if test="${'1' eq byTime}">&nbsp;&nbsp;&nbsp;
			  
			      <a href="#" class='btn btn-small btn-info' onclick="javaScript:searchTypes('3');return false;">By Month</a>
		      
			</c:if>
			<br/><br/>
			<font color="red" size="2"><spring:message code="amazon_sales_exchange_rate_info"/> </font>
			<br/><span>
				&nbsp;&nbsp;<a class="btn btn-small btn-warning" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_promotions_order"/></a>
				&nbsp;&nbsp;<a class="btn btn-small btn-danger" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_flash_sales_order"/></a>
				&nbsp;&nbsp;<a class="btn btn-small btn-primary" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_max_order"/></a>
				&nbsp;&nbsp;<a class="btn btn-small btn-info" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_free_order"/></a>
				&nbsp;&nbsp;<a class="btn btn-small btn-success" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_ads_order"/></a>
				&nbsp;&nbsp;<a class="btn btn-small btn-special" style="height:14px; font-size:12px; line-height:12px;">B2B</a>
				&nbsp;&nbsp;<a class="btn btn-small btn-inverse" style="height:14px; font-size:12px; line-height:12px;"><spring:message code="amazon_multifarious_order"/></a>
			</span>
		</div>
		<br/>
		<div style="overflow-x:auto">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th><div style="width:170px">ProductName</div></th>
						<th>Quantity</th>
						<th>Sales(${currencySymbol })</th>
						<th><a  href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占选取时间总销量">销量<br/>占比</a></th>
						<th><a  href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占选取时间总销售额">销售额<br/>占比</a></th>
						
						<c:if test="${flag && 'EUR' eq fn:trim(currencyType)}">
							<shiro:hasPermission name="amazoninfo:sale:run">
								<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="结算报告半月一次，如费用数与销量不一致，可能是亚马逊结算报告尚未生成">已确认<br/>费用数量</a></th>
								<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="扣除增值税后的收入">税后收入</a></th>
								<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="包括退货和退款金额">退款</a></th>
								<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="含亚马逊FBAPerUnitFulfillmentFee、FBAWeightBasedFee、Commission三项费用">亚马逊费用</a></th>
								<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="欧洲共享库存费用、亚马逊包装费、自发货邮费收入等">杂费</a></th>
								<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="全年度各市场按公斤平均运费*单品实际重量">运输费</a></th>
								<th>采购成本</th>
								<th>利润</th>
								<th>利润<br/>占比</th>
								<th>单个利润</th>
								<th>广告费用</th>
								<th>广告销量</th>
								<th>广告销售额</th>
								<th>替代货</th>
								<th>Marketing</th>
							</shiro:hasPermission>
						</c:if>
						<c:if test="${not empty priceMap&&'en' ne country &&'eu' ne country &&'total' ne country}">
						    <th>Unit Price</th>
						    <th>Profit</th>
						    
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr key="${op[1]}">
							<td>${i.count}						   
							</td>
							 <td>
								<c:if test="${flag && 'EUR' eq fn:trim(currencyType)}">
									<shiro:hasPermission name="amazoninfo:sale:run">
										<c:if test="${fns:getDictLabel(country,'platform','')==''}">
											<span style="color:red">
												${(op[18]==null||op[18]<op[1])?'[预估]':''}
											</span> 	
										</c:if>
										<c:if test="${fns:getDictLabel(country,'platform','')!=''}">
											<span style="color:red">${op[18]>0?'':'[预估]'}</span> 
										</c:if>
									</shiro:hasPermission></c:if>
								<c:if test="${!fn:contains(op[0],'未匹配')}">
									<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${op[0]}" target="_blank">${op[0]}</a>
									<c:if test="${not empty typeLineMap[fn:toLowerCase(nameTypeMap[op[0]])] }">(${typeLineMap[fn:toLowerCase(nameTypeMap[op[0]])] }线&nbsp;${not empty op[13]?op[13]:''})</c:if>
								</c:if>
								<c:if test="${fn:contains(op[0],'未匹配')}">
									<span style="color: red">${op[0]}</span>
								</c:if>
							</td>
							<td >
							
							    <c:set value='0' var='count' />
							     <c:set value='' var='classType' />
							    <c:if test="${op[3]>0||op[11]>0 }"><c:set value='${count+1}' var='count' />
							      <c:set value='btn-warning' var='classType' />
							    </c:if>
							     <c:if test="${op[4]>0 }"><c:set value='${count+1}' var='count' />
							       <c:set value='btn-danger' var='classType' />
							    </c:if>
							     <c:if test="${op[5]>0 }"><c:set value='${count+1}' var='count' />
							       <c:set value='btn-primary' var='classType' />
							    </c:if>
							     <c:if test="${op[6]>0||op[8]>0||op[9]>0  }"><c:set value='${count+1}' var='count' />
							        <c:set value='btn-info' var='classType' />
							    </c:if>
							     <c:if test="${op[7]>0||op[10]>0 }"><c:set value='${count+1}' var='count' />
							        <c:set value='btn-success' var='classType' />
							    </c:if>
							     <c:if test="${op[12]>0}"><c:set value='${count+1}' var='count' />
							        <c:set value='btn-special' var='classType' />
							    </c:if>
							    <c:if test="${count>=2 }"> <c:set value='btn-inverse' var='classType' /></c:if>
								<a ${(op[3]>0||op[4]>0||op[5]>0||op[6]>0||op[7]>0||op[8]>0||op[9]>0||op[10]>0||op[11]>0||op[12]>0)?'data-toggle=popover data-html=true rel=popover':''} data-content="${op[8]>0?'Review:':''}${op[8]>0?op[8]:'' }&nbsp;${op[9]>0?'Support:':''}${op[9]>0?op[9]:'' }&nbsp;${op[3]>0?'站内促销:':''}${op[3]>0?op[3]:'' }&nbsp;${op[4]>0?'闪购:':''}${op[4]>0?op[4]:'' }&nbsp;${op[5]>0?'最大订单:':''}${op[5]>0?op[5]:'' }&nbsp;${op[6]>0?'免费:':''}${op[6]>0?op[6]:'' }&nbsp;${op[7]>0?'SPA:':''}${op[7]>0?op[7]:'' }&nbsp;${op[10]>0?'AMS:':''}${op[10]>0?op[10]:'' }&nbsp;${op[11]>0?'站外促销:':''}${op[11]>0?op[11]:'' }&nbsp;${op[12]>0?'B2B:':''}${op[12]>0?op[12]:'' }"  target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&productName=${op[0]}&currencyType=${fn:trim(currencyType)}&lineType=total" class="btn btn-small ${classType }"  style="height:14px; font-size:12px; line-height:12px;" >
									<fmt:formatNumber value="${op[1]}" maxFractionDigits="0" />
								</a>
							</td>
							<td ><fmt:formatNumber value="${op[2]}" maxFractionDigits="2" /></td>
							<td class='pv'></td>
							<td class="ps"></td>
							
							<c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
								<shiro:hasPermission name="amazoninfo:sale:run">
									<td><!--5 已确认数量 -->
										<c:if test="${not empty op[18]}">
											<fmt:formatNumber value="${op[18]}" maxFractionDigits="0" />
										</c:if>
										<c:if test="${empty op[18]}">
											0
										</c:if>
										</td>
									<td><%-- 税后收入 --%>
										<c:if test="${not empty profitMap[op[0]].salesNoTax}">
											<fmt:formatNumber value="${profitMap[op[0]].salesNoTax}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].salesNoTax}">
											0
										</c:if>
									</td>
									<td><%-- 退款 --%>
										<c:if test="${not empty profitMap[op[0]].refund}">
											<fmt:formatNumber value="${profitMap[op[0]].refund}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].refund}">
											0
										</c:if>
									</td>
									<td><%-- 亚马逊佣金  --%>
										<c:if test="${not empty profitMap[op[0]].amazonFee}">
											<fmt:formatNumber value="${profitMap[op[0]].amazonFee}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].amazonFee}">
											0
										</c:if>
									</td>
									<td><%-- 杂费 --%>
										<c:if test="${not empty profitMap[op[0]].otherFee}">
											<fmt:formatNumber value="${profitMap[op[0]].otherFee}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].otherFee}">
											0
										</c:if>
									</td>
									<td><%-- 运输费 --%>
										<c:if test="${not empty profitMap[op[0]].transportFee}">
											<fmt:formatNumber value="${profitMap[op[0]].transportFee}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].transportFee}">
											0
										</c:if>
									</td>
									<td><%-- 采购成本 --%>
										<c:if test="${not empty profitMap[op[0]].buyCost}">
											<fmt:formatNumber value="${profitMap[op[0]].buyCost}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].buyCost}">
											0
										</c:if>
									</td>
									<td><%-- 利润 --%>
										<c:if test="${not empty profitMap[op[0]].profits}">
											<fmt:formatNumber value="${profitMap[op[0]].profits}" maxFractionDigits="2" />
										</c:if>
										<c:if test="${empty profitMap[op[0]].profits}">
											0
										</c:if>
									</td>
									<td></td>
									<td>
										<c:if test="${op[18]>0|| op[1] >0 }">
											<fmt:formatNumber value="${profitMap[op[0]].profits/op[1]}" maxFractionDigits="2" />
											<%--<c:if test="${fns:getDictLabel(country,'platform','')==''}">
												<fmt:formatNumber value="${profitMap[op[0]].profits/op[1]}" maxFractionDigits="2" />
											</c:if>
											<c:if test="${fns:getDictLabel(country,'platform','')!=''}">
												<fmt:formatNumber value="${profitMap[op[0]].profits/(op[18]==null?op[1]:op[18])}" maxFractionDigits="2" />
											</c:if> --%>
										</c:if>
										<c:if test="${!(op[18]>0|| op[1] >0 )}">
											0
										</c:if>
									</td>
									
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										站内(market):<fmt:formatNumber value="${profitMap[op[0]].adInProfitFee }"></fmt:formatNumber><br/>
										站内(sales):<fmt:formatNumber value="${profitMap[op[0]].adInEventFee }"></fmt:formatNumber><br/>
										站外(market):<fmt:formatNumber value="${profitMap[op[0]].adOutProfitFee }"></fmt:formatNumber><br/>
										站外(sales):<fmt:formatNumber value="${profitMap[op[0]].adOutEventFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${profitMap[op[0]].adOutEventFee+profitMap[op[0]].adOutProfitFee+profitMap[op[0]].adInEventFee+profitMap[op[0]].adInProfitFee}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										站内(market):<fmt:formatNumber value="${profitMap[op[0]].adInProfitSalesVolume }"></fmt:formatNumber><br/>
										站内(sales):<fmt:formatNumber value="${profitMap[op[0]].adInEventSalesVolume }"></fmt:formatNumber><br/>
										站外(market):<fmt:formatNumber value="${profitMap[op[0]].adOutProfitSalesVolume }"></fmt:formatNumber><br/>
										站外(sales):<fmt:formatNumber value="${profitMap[op[0]].adOutEventSalesVolume}"></fmt:formatNumber>">
									<fmt:formatNumber value="${profitMap[op[0]].adInEventSalesVolume+profitMap[op[0]].adInProfitSalesVolume+profitMap[op[0]].adOutEventSalesVolume+profitMap[op[0]].adOutProfitSalesVolume}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										站内(market):<fmt:formatNumber value="${profitMap[op[0]].adInProfitSales }"></fmt:formatNumber><br/>
										站内(sales):<fmt:formatNumber value="${profitMap[op[0]].adInEventSales }"></fmt:formatNumber><br/>
										站外(market):<fmt:formatNumber value="${profitMap[op[0]].adOutProfitSales }"></fmt:formatNumber><br/>
										站外(sales):<fmt:formatNumber value="${profitMap[op[0]].adOutEventSales}"></fmt:formatNumber>">
									<fmt:formatNumber value="${profitMap[op[0]].adInEventSales+profitMap[op[0]].adInProfitSales+profitMap[op[0]].adOutEventSales+profitMap[op[0]].adOutProfitSales}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										数量:<fmt:formatNumber value="${profitMap[op[0]].supportNum }"></fmt:formatNumber><br/>
										成本:<fmt:formatNumber value="${-profitMap[op[0]].supportCost }"></fmt:formatNumber><br/>
										亚马逊费用:<fmt:formatNumber value="${profitMap[op[0]].supportAmazonFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${profitMap[op[0]].supportAmazonFee-profitMap[op[0]].supportCost}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										数量:<fmt:formatNumber value="${profitMap[op[0]].reviewNum }"></fmt:formatNumber><br/>
										成本:<fmt:formatNumber value="${-profitMap[op[0]].reviewCost }"></fmt:formatNumber><br/>
										亚马逊费用:<fmt:formatNumber value="${profitMap[op[0]].reviewAmazonFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${profitMap[op[0]].reviewAmazonFee-profitMap[op[0]].reviewCost}" maxFractionDigits="2" />
								</a>
							</td>
								</shiro:hasPermission>
							</c:if>
							<c:if test="${not empty priceMap&&'en' ne country &&'eu' ne country &&'total' ne country }">
							 <c:if test="${!fn:contains(op[0],'未匹配')}">
							    <c:set value="${op[0]}_${country}" var="nameKey"/>
						       <%--  <td><fmt:formatNumber value="${priceMap[nameKey] }" maxFractionDigits="2" /></td> --%>
						        <td>
						          <c:if test="${op[1]>0 }">
						            <fmt:formatNumber value="${op[2]/op[1] }" maxFractionDigits="2" />
						          </c:if>
						        </td> 
						            <c:if test="${not empty  priceMap[nameKey]}">
							          <%--<td><fmt:formatNumber value="${op[2]-priceMap[nameKey]*op[1] }" maxFractionDigits="2" /></td> --%>
							          <!-- 利润计算考虑增值税和亚马逊佣金,亏本时不需要考虑 -->
							          <td>
							          	<c:if test="${op[2]>priceMap[nameKey]*op[1] }">
							          		<fmt:formatNumber value="${(op[2]-priceMap[nameKey]*op[1])/(1+vat) - (op[2]-priceMap[nameKey]*op[1])*commission[nameKey]/100}" maxFractionDigits="2" />
							          	</c:if>
							          	<c:if test="${op[2]<=priceMap[nameKey]*op[1] }">
							          		<fmt:formatNumber value="${op[2]-priceMap[nameKey]*op[1] }" maxFractionDigits="2" />
							          	</c:if>
							          </td>
							        </c:if>
									<c:if test="${empty  priceMap[nameKey]}">
							          <td>0</td>
							        </c:if>
						       
							 </c:if>	
							 <c:if test="${fn:contains(op[0],'未匹配')}">
								<td></td>
								<td>0</td>
							 </c:if>
						    </c:if>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr >
						<td style="font-size: 12px; font-weight: bold;">Page Total</td>
						<td></td>
						<td id="total"></td>
						<td id="totalSales"></td>
						<td id="totalPV"></td>
						<td id="totalPS"></td>
						
						<c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
							<shiro:hasPermission name="amazoninfo:sale:run">
								<td id="confirmNum"></td>
								<td id="salesNoTax"></td>
								<td id="refund"></td>
								<td id="amazonFee"></td>
								<td id="otherFee"></td>
								<td id="transportFee"></td>
								<td id="butCost"></td>
								<td id="totalProfit"></td>
								<td id="pf"></td>
								<td id="sf"></td>
								<td id="adFee"></td>
								<td id="adSalesVolume"></td>
								<td id="adSales"></td>
								<td id="supportFee"></td>
								<td id="reviewFee"></td>
							</shiro:hasPermission>
						</c:if>
						<c:if test="${not empty priceMap&&'en' ne country &&'eu' ne country &&'total' ne country }">
						        <td></td>
								<td id="totalProfit"></td>
						</c:if>
					</tr>
					
					<tr id = "totalTr">
						<td style="font-size: 18px; font-weight: bold;">Total</td>
						<td></td>
						<td></td>
						<td></td>
						<td id="allPV"></td>
						<td id="allPS"></td>
						
						<c:if test="${flag && 'EUR' eq fn:trim(currencyType) }">
							<shiro:hasPermission name="amazoninfo:sale:run">
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
								<td></td>
							</shiro:hasPermission>
						</c:if>
						<c:if test="${not empty priceMap&&'en' ne country &&'eu' ne country &&'total' ne country}">
						        <td></td>
								<td></td>  
						</c:if>
					</tr>
					
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>
