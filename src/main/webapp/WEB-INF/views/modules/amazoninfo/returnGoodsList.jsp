<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Return Goods Manager</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self;			
		}	
		$(function(){
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#btnExportAllCountry").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods/exportAllCountry");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/returnGoods");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$("select[name='country'],select[name='reason'],select[name='disposition']").change(function(){
				$("#searchForm").submit();
			});
			
			var count = 0;
			$(".quantity").each(function(){
				count+= parseInt($(this).text());
			});
			$("#count").text(count);
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="ASC"?"DESC":"ASC");
					$("#orderBy").val(order+" ASC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" DESC");
				}
				page();
			});
			
			$(".countryHref").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/returnGoods');
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#btnExpPdfs").click(function(){
				top.$.jBox.confirm("<input style='width: 160px;'  readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	params.month = h.find("input").val();
							params.country = '${returnGoods.country}';
						  	if(params.month){
						  		window.location.href = "${ctx}/amazoninfo/returnGoods/exportPdfs?"+$.param(params);
						  	}else{
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
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
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${returnGoods.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="returnGoods" action="${ctx}/amazoninfo/returnGoods" method="post" class="breadcrumb form-search">
		<div style="vertical-align: middle;height:120px;line-height: 40px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<input  name="country" type="hidden" value="${returnGoods.country}"/>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/returnGoods');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${returnGoods.startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/returnGoods');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="returnDate" value="<fmt:formatDate value="${returnGoods.returnDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
			Reason:<select name="reason" style="width: 260px">
			 		<option value="" >All</option>
					<c:forEach items="${reasons}" var="reason">
						<option value="${reason}" ${returnGoods.reason eq reason?'selected':''} >${reason}</option>
					</c:forEach>
			 </select>&nbsp;&nbsp;&nbsp;&nbsp;
			 
			 CustomerComment:<input type="checkbox" name="customerComment"  ${returnGoods.customerComment !=null ?'checked':''}  />
			 &nbsp;&nbsp;&nbsp;&nbsp;
			 Disposition:<select name="disposition" style="width: 260px">
			 		<option value="" >All</option>
					<c:forEach items="${dispositions}" var="disposition">
						<option value="${disposition}" ${returnGoods.disposition eq disposition?'selected':''} >${disposition}</option>
					</c:forEach>
			 </select>
			 
			<br/>
			<label>Sku/Asin/OrderId:</label><form:input path="sku" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/><br/>
			<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
			<input id="btnExportAllCountry" class="btn btn-primary" type="button" value="<spring:message code="sys_but_exportAllCountry"/>"/>
			
			<input id="btnExpPdfs" class="btn btn-primary" type="button" value="Export Refunds Bill Summary" />
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:50px;">Return Date</th>
				   <th style="width:150px;">OrderId</th>
				   <th style="width:150px;">Name</th>
				   <th style="width:120px">Sku</th>
				   <th style="width:70px">Fnsku</th>
				   <th style="width:70px" >Asin</th>
				   <th style="width:50px">Quantity</th>
				   <th style="width:40px">FC</th>
				   <th style="width:120px">Disposition</th>
				   <th style="width:120px">Reason</th>
			  </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="goods">
			<tr>
				<td><fmt:formatDate value="${goods.returnDate}" pattern="yyyy-M-dd" /></td>
				<td><a href="${ctx}/amazoninfo/order/form?amazonOrderId=${goods.orderId}" target="_blank">${goods.orderId}</a></td>
				<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${nameMap[goods.asin]}">${nameMap[goods.asin]}</a></td>
				<td>${goods.sku}</td>
				<td>${goods.fnsku}</td>
				<td>${goods.asin}</td>
				<td class="quantity">${goods.quantity}</td>
				<td>${goods.fulfillmentCenterId}</td>
				<td>${goods.disposition}</td>
				<td>
					 <c:choose>
				    	<c:when test="${goods.customerComment!=''}"><a rel="popover" data-placement="left" data-content="${goods.customerComment}">${goods.reason}</a></c:when>
				    	<c:otherwise>${goods.reason}</c:otherwise>
				    </c:choose>
				</td>
		</c:forEach>
		<tr>
			<td><b>Total</b></td>
			<td colspan="5"></td>
			<td id="count">0</td>
			<td colspan="3"></td>
		</tr>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
