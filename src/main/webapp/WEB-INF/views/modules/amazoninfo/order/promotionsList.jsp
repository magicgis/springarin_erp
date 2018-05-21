<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code="amazon_order_tab1"/></title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
	</style>
	<script type="text/javascript">
		if(!(top)){
			top = self;
		}
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			 var orderIds= {
			        data : Array(),
			        get : function(key){
			            return this.data[key];
			        },
			        set : function(key, value) {
			            this.data[key] = value;
			            return;
			        }
			    };

			
			
			$(".countryHref").click(function(){
				$("input[name='salesChannel']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/promotionsExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/promotions");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			 var table = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
			});
			 
			 $(".row:first").append($("#searchContent").html());
			 
			 
			 $(".open").live("click",function(e){
					var row = $(this).parents('tr')[0];
					$this = $(this);
					if($(this).text()=='Detail'){
						
						var classId= $(this).parent().parent().find("input[type='hidden']").val();
						if(orderIds!=null&&orderIds.get(classId)!=null){
							console.log(12);
							table.fnOpen( row, "<tr><td colspan='8'>"+orderIds.get(classId)+"<td><tr>", 'details' );
						}else{
							var param ={};
							if($(this).parent().parent().find(".promotionIds").text()=="闪购"){
								param.promotionIds ="";
							}else{
								param.promotionIds = encodeURI($(this).parent().parent().find(".promotionIds").text());
							}
							param.price = $(this).parent().parent().find(".price").text();
							param.asin = $(this).parent().parent().find(".asin").text();
							
							param.startDate = $("#start").val();
							param.endDate = $("#end").val();
							param.country = $("#salesChannel").val();
							$(this).attr("disabled",true);
							$.get("${ctx}/amazoninfo/order/getPromotionOrders?"+$.param(param),function(data){
								var html ="";
								if(data){
									eval("var data = "+data);
									
									$(data).each(function(){
										var orders = this[2];
										html+=(this[0]+"总计"+this[1]+"个订单[");
										$(orders.split(',')).each(function(){
											var index=$.trim(this);
											var tempOrder=$.trim(this);
											if(tempOrder.indexOf("&nbsp;")>=0){
												index=tempOrder.substring(0,tempOrder.indexOf("&nbsp;"));
											}
											html += "<a href='${ctx}/amazoninfo/order/form?amazonOrderId="+index+"' target='_blank'>"+this+"</a> ";									
										});
										html+="]<br/>";
									});
									orderIds.set(classId, html);
								}else{
									html = "PromotionIds is garbled,Unable to query data!";	
								}
								$this.attr("disabled",false);
								table.fnOpen( row, "<tr><td colspan='8'>"+html+"<td><tr>", 'details' );
								
							});
						}
						
						
						$this.text('Close');
					}else{
						table.fnClose( row );
						$(this).text('Detail');
					}
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
		function btnExport(){
			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/order/promotionsExport");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/order/promotions");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		function btnExportAll(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/promotionsExportAll");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/order/promotions");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty amazonOrder.salesChannel ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<li class="${'eu' eq amazonOrder.salesChannel ?'active':''}"><a class="countryHref" href="#" key="eu">欧洲总计</a></li>
		<li class="${'en' eq amazonOrder.salesChannel ?'active':''}"><a class="countryHref" href="#" key="en">英语国家</a></li>
		<li class="${'unEn' eq amazonOrder.salesChannel ?'active':''}"><a class="countryHref" href="#" key="unEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonOrder.salesChannel eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<div style="display: none" id="searchContent">
		<form:form id="searchForm" modelAttribute="amazonOrder" action="${ctx}/amazoninfo/order/promotions" method="post" >
			&nbsp;&nbsp;&nbsp;<label><spring:message code="amazon_order_tips3"/>：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${amazonOrder.purchaseDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="lastUpdateDate" value="<fmt:formatDate value="${amazonOrder.lastUpdateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<input  name="salesChannel" id="salesChannel" type="hidden" value="${amazonOrder.salesChannel}" />
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<input  class="btn btn-primary" onclick="btnExport()" type="button" value="<spring:message code="sys_but_export"/>"/>
			<input  class="btn btn-primary" onclick="btnExportAll()"  value="<spring:message code="sys_but_exportAllCountry"/>"/>
		   &nbsp;&nbsp;<span style='color:#ff0033;'>(支持折扣码搜索)</span> 
		</form:form>
	</div>
	<!-- <input id='resolveJsonArray' class="btn btn-primary" type="button" value='json'/> -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 120px">Name</th>	
				   <th style="width: 120px">Country</th>
				   <th style="width: 50px">Asin</th>	
				   <th style="width: 150px">promotionIds</th>
				   <th style="width: 50px">promotionDiscount</th>
				   <th style="width: 50px">Shipped Sum</th>
				   <th style="width: 50px">Sales(€)</th>
				   <th style="width: 100px">Operating</th>
			 </tr>	   
		</thead>
		<tbody>
			<c:forEach items="${data}" var="entry" varStatus="i">
			  <tr>
				   <td>
				   <a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${entry.name}">${entry.name}</a>
				   	<c:if test="${not empty typeLineMap[fn:toLowerCase(nameTypeMap[entry.name])] }">(${typeLineMap[fn:toLowerCase(nameTypeMap[entry.name])] }线)</c:if>
				   <span style='display:none'>${entry.code }</span>
				   </td>	
				   <td class="country">
				   		${fns:getDictLabel(entry.country,'platform','')}
				   	</td>	
				   <td class="asin">${entry.asin}</td>	
				   <td class="promotionIds">${entry.promotionIds}</td>
				   <td class="price">${entry.promotionDiscount}</td>
				   <td>${entry.sum}</td>
				   <td>${entry.sales}</td>
				   <td>
				   	<input type="hidden" value="num${i.count}"/>
				   	<a class="btn btn-small btn-info open">Detail</a>
				   </td>
			  </tr>
			</c:forEach>		
		</tbody>
	</table>
</body>
</html>
