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
		var num2 = 0;
		var num3 = 0;
		var num4 = 0;
		var num5 = 0;
		var num6 = 0;
		var num7 = 0;
		var num8 = 0;
		var num9 = 0;
		var num9 = 0;
		var num10 = 0;
		var num11 = 0;
		var num12 = 0;
		arr.each(function() {
			if($(this).find("td :eq(2)").text())
			num2 += parseInt($(this).find("td :eq(2)").text().split(",").join(''));	//销量
			
			if($(this).find("td :eq(3)").text())
			num3 += parseFloat($(this).find("td :eq(3)").text().split(",").join(''));
			
			if($(this).find("td :eq(4)").text())
				num4 += parseFloat($(this).find("td :eq(4)").text().split(",").join(''));
			if($(this).find("td :eq(5)").text())
				num5 += parseFloat($(this).find("td :eq(5)").text().split(",").join(''));
			if($(this).find("td :eq(6)").text())
				num6 += parseFloat($(this).find("td :eq(6)").text().split(",").join(''));
			if($(this).find("td :eq(7)").text())
				num7 += parseFloat($(this).find("td :eq(7)").text().split(",").join(''));
			if($(this).find("td :eq(8)").text())
				num8 += parseFloat($(this).find("td :eq(8)").text().split(",").join(''));
			if($(this).find("td :eq(9)").text())
				num9 += parseFloat($(this).find("td :eq(9)").text().split(",").join(''));
			if($(this).find("td :eq(10)").text())
				num10 += parseFloat($(this).find("td :eq(10)").text().split(",").join(''));
			if($(this).find("td :eq(11)").text())
				num11 += parseFloat($(this).find("td :eq(11)").text().split(",").join(''));
			if($(this).find("td :eq(12)").text())
				num12 += parseFloat($(this).find("td :eq(12)").text().split(",").join(''));
			
		});

		var tr = $("#contentTable tfoot tr#totalTr");
		tr.find("td :eq(2)").text(fmoney(num2.toFixed(0),0));
		tr.find("td :eq(3)").text(fmoney(num3.toFixed(0),0));
		tr.find("td :eq(4)").text(fmoney(num4.toFixed(0),0));
		tr.find("td :eq(5)").text(fmoney(num5.toFixed(0),0));
		tr.find("td :eq(6)").text(fmoney(num6.toFixed(0),0));
		tr.find("td :eq(7)").text(fmoney(num7.toFixed(0),0));
		tr.find("td :eq(8)").text(fmoney(num8.toFixed(0),0));
		tr.find("td :eq(9)").text(fmoney(num9.toFixed(0),0));
		tr.find("td :eq(10)").text(fmoney(num10.toFixed(0),0));
		tr.find("td :eq(11)").text(fmoney(num11.toFixed(0),0));
		tr.find("td :eq(12)").text(fmoney(num12.toFixed(0),0));
		
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
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     
							   ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
				,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             if(iDisplayIndex==0){
		            	 addd2=0;
		            	 addd3=0;
		            	 addd4=0;
		            	 addd5=0;
		            	 addd6=0;
		            	 addd7=0;
		            	 addd8=0;
		            	 addd9=0;
		            	 addd10=0;
		            	 addd11=0;
		            	 addd12=0;
		             }
		             
		             addd2+=parseInt($(nRow).attr("key"));//第几列
		             
		             
		             if($.isNumeric(aData[3])){
		            	 addd3+=parseFloat(aData[3]);//第几列
		             }else if($.isNumeric(aData[3].split(",").join(''))){
		            	 addd3+=parseFloat(aData[3].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[4])){
		            	 addd4+=parseFloat(aData[4]);//第几列
		             }else if($.isNumeric(aData[4].split(",").join(''))){
		            	 addd4+=parseFloat(aData[4].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[5])){
		            	 addd5+=parseFloat(aData[5]);//第几列
		             }else if($.isNumeric(aData[5].split(",").join(''))){
		            	 addd5+=parseFloat(aData[5].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[6])){
		            	 addd6+=parseFloat(aData[6]);//第几列
		             }else if($.isNumeric(aData[6].split(",").join(''))){
		            	 addd6+=parseFloat(aData[6].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[7])){
		            	 addd7+=parseFloat(aData[7]);//第几列
		             }else if($.isNumeric(aData[7].split(",").join(''))){
		            	 addd7+=parseFloat(aData[7].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[8])){
		            	 addd8+=parseFloat(aData[8]);//第几列
		             }else if($.isNumeric(aData[8].split(",").join(''))){
		            	 addd8+=parseFloat(aData[8].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[9])){
		            	 addd9+=parseFloat(aData[9]);//第几列
		             }else if($.isNumeric(aData[9].split(",").join(''))){
		            	 addd9+=parseFloat(aData[9].split(",").join(''));//第几列
					 }
		             if($.isNumeric(aData[10])){
		            	 addd10+=parseFloat(aData[10]);//第几列
		             }else if($.isNumeric(aData[10].split(",").join(''))){
		            	 addd10+=parseFloat(aData[10].split(",").join(''));//第几列
					 }
		             
		             if($.isNumeric(aData[11])){
		            	 addd11+=parseFloat(aData[11]);//第几列
		             }else if($.isNumeric(aData[11].split(",").join(''))){
		            	 addd11+=parseFloat(aData[11].split(",").join(''));//第几列
					 }
		             
		             if($.isNumeric(aData[12])){
		            	 addd12+=parseFloat(aData[12]);//第几列
		             }else if($.isNumeric(aData[12].split(",").join(''))){
		            	 addd12+=parseFloat(aData[12].split(",").join(''));//第几列
					 }
		            
		             $("#total2").html(fmoney(addd2,0));
		             $("#total3").html(fmoney(addd3,0));
		             $("#total4").html(fmoney(addd4,0));
		             $("#total5").html(fmoney(addd5,0));
		             $("#total6").html(fmoney(addd6,0));
		             $("#total7").html(fmoney(addd7,0));
		             $("#total8").html(fmoney(addd8,0));
		             $("#total9").html(fmoney(addd9,0));
		             $("#total10").html(fmoney(addd10,0));
		             $("#total11").html(fmoney(addd11,0));
		             $("#total12").html(fmoney(addd12,0));	
		             return nRow;
		         },"fnPreDrawCallback": function( oSettings ) { 
		        	 $("#total2").html(0);
		             $("#total3").html(0);
		             $("#total4").html(0);
		             $("#total5").html(0);
		             $("#total6").html(0);
		             $("#total7").html(0);
		             $("#total8").html(0);
		             $("#total9").html(0);
		             $("#total10").html(0);
		             $("#total11").html(0);
		             $("#total12").html(0);
		         }  
		});
		
		
		
		<%-- 货币美元&欧元切换,默认为欧元版	 --%>
		var html1 = " 货币类型:<select name=\"currencyType\" id=\"currencyType\" style=\"width: 100px\" onchange=\"changeCurrencyType()\">"+
			"<option value=\"EUR\" ${'EUR' eq fn:trim(currencyType)?'selected':''}>EUR</option>"+
			"<option value=\"USD\" ${'USD' eq fn:trim(currencyType)?'selected':''}>USD</option></select> &nbsp;&nbsp;&nbsp;";
		
		var html = "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">订单</a>&nbsp;"+
		"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/skuList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">Sku</a>&nbsp;"+
		"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品</a>&nbsp;"+
		<c:if test="${'3' eq byTime}">
	        "<a class=\"btn btn-info btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productListByDate?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品按日</a>&nbsp;"+
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

</script>
</head>
<body>
	<div id="contentTbDiv" style="width:1200px;margin: auto">
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${country eq 'eu'}">欧洲</c:when>
			<c:when test="${country eq 'en'}">英语国家</c:when>
			<c:when test="${country eq 'unEn'}">非英语国家</c:when>
			<c:otherwise>${fns:getDictLabel(country,'platform','合计')}</c:otherwise>
		</c:choose>
			${'total' eq lineType?'':allLine[lineType] }销售报告${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}产品明细
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
						<th style="width: 200px">Date</th>
						<th><div style="width:170px">ProductName</div></th>
						<th>总销量</th>
						<th>其中站内促销</th>
						<th>其中站外促销</th>
						<th>其中闪购</th>
						<th>其中大订单</th>
						<th>其中免费</th>
						<th>其中SPA</th>
						<th>其中AMS</th>
						<th>其中B2B</th>
						<th>营销Marketing</th>
						<th>替代Support</th>
					</tr>
				</thead>
				<tbody>
				  <c:forEach items="${ops}" var="tempOps" varStatus="i">
					<c:forEach items="${ops[tempOps.key]}" var="op" varStatus="i">
						<tr key="${op[1]}"> <c:set value='${op[14]}${op[0]}' var='dateKey' />
							<td>${tempOps.key} </td>
							<td>
								<c:if test="${flag && 'EUR' eq fn:trim(currencyType)}">
									<shiro:hasPermission name="amazoninfo:sale:run">
										<c:if test="${fns:getDictLabel(country,'platform','')==''}">
											<span style="color:red">
												${(op[19]==null||op[19]<op[1])?'[预估]':''}
											</span> 	
										</c:if>
										<c:if test="${fns:getDictLabel(country,'platform','')!=''}">
											<span style="color:red">${op[19]>0?'':'[预估]'}</span> 
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
							<td>
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
								<a ${(op[3]>0||op[4]>0||op[5]>0||op[6]>0||op[7]>0||op[8]>0||op[9]>0||op[10]>0||op[11]>0||op[12]>0)?'data-toggle=popover data-html=true rel=popover':''} data-content="${op[8]>0?'Marketing:':''}${op[8]>0?op[8]:'' }&nbsp;${op[9]>0?'Support:':''}${op[9]>0?op[9]:'' }&nbsp;${op[3]>0?'站内促销:':''}${op[3]>0?op[3]:'' }&nbsp;${op[4]>0?'闪购:':''}${op[4]>0?op[4]:'' }&nbsp;${op[5]>0?'最大订单:':''}${op[5]>0?op[5]:'' }&nbsp;${op[6]>0?'免费:':''}${op[6]>0?op[6]:'' }&nbsp;${op[7]>0?'SPA:':''}${op[7]>0?op[7]:'' }&nbsp;${op[10]>0?'AMS:':''}${op[10]>0?op[10]:'' }&nbsp;${op[11]>0?'站外促销:':''}${op[11]>0?op[11]:'' }&nbsp;${op[12]>0?'B2B:':''}${op[12]>0?op[12]:'' }"  target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&productName=${op[0]}&currencyType=${fn:trim(currencyType)}&lineType=total" class="btn btn-small ${classType }"  style="height:14px; font-size:12px; line-height:12px;" >
									<fmt:formatNumber value="${op[1]}" maxFractionDigits="0" />
								</a>
							</td>
							<td>${op[3]>0?op[3]:''}</td>
							<td>${op[11]>0?op[11]:''}</td>
							<td>${op[4]>0?op[4]:''}</td>
							<td>${op[5]>0?op[5]:''}</td>
							<td>${op[6]>0?op[6]:''}</td>
							<td>${op[7]>0?op[7]:''}</td>
							<td>${op[10]>0?op[10]:''}</td>
							<td>${op[12]>0?op[12]:''}</td>
							<td>${op[8]>0?op[8]:''}</td>
							<td>${op[9]>0?op[9]:''}</td>
						</tr>
					</c:forEach>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr >
						<td style="font-size: 12px; font-weight: bold;">Page Total</td>
						<td></td>
						<td id="total2"></td>
						<td id="total3"></td>
						<td id="total4"></td>
						<td id="total5"></td>
						<td id="total6"></td>
						<td id="total7"></td>
						<td id="total8"></td>
						<td id="total9"></td>
						<td id="total10"></td>
						<td id="total11"></td>
						<td id="total12"></td>
					</tr>
					
					<tr id = "totalTr">
						<td style="font-size: 18px; font-weight: bold;">Total</td>
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
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>
