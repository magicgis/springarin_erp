<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
<meta name="decorator" content="default"/>
<title>outsidePromotionView</title>

<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();

	$(document).ready(function(){
		
		$("#country").change(function(){
			var params = {};
			if($(this).val()){
				params.country = $(this).val();
			}
			window.location.href = "${ctx}/amazoninfo/outsidePromotion/add?"+$.param(params);
		});
		
		$(".Wdate").on("click", function (){
			 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
		});
		
		
		$("#promotionCode").change(function(){
			$("input[name='trackId']").val("");
	    	$("input[name='promotionCode']").val("");
	    	$("input[name='startDate']").val("");
	    	$("input[name='endDate']").val("");
	    	$("input[name='productName']").val("");
	    	$("input[name='buyerGets']").val("");  
	    	$("input[name='promoWarning.id']").val("");  
			var id=$(this).val();
			if(id){
				$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/amazoninfo/outsidePromotion/getProInfo' ,
					    data: {
					    	"id":id
					    },
					    dataType: 'json',
					    success:function(data){ 
					    	$("input[name='trackId']").val(data.promotionId);
					    	$("input[name='promotionCode']").val(data.promotionCode);
					    	$("input[name='startDate']").val(data.startDate);
					    	$("input[name='endDate']").val(data.endDate);
					    	$("input[name='productName']").val(data.productName);
					    	$("input[name='buyerGets']").val(data.buyerGets);  
					    	$("input[name='asin']").val(data.asin);  
					    	$("input[name='promoWarning.id']").val(data.id);  
				        }
				});
			}
			
		});
	
		$("#inputForm").validate({
			submitHandler: function(form){
				loading('Please wait a moment!');
				form.submit();
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("Entered incorrectly, please correct");
				if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
					error.appendTo(element.parent().parent());
				} else {
					error.insertAfter(element);
				}
			}
		});
		
	});

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/outsidePromotion">站外促销分析列表</a></li>
		<li class="active"><a href="#">同折扣码对比</a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="" class="breadcrumb form-search" cssStyle="text-align:center;font-size:18px">
	
	<div >
		<div style="display:inline-block;font-size: 30px">同折扣码${outsidePromotion.promotionCode}对比&nbsp;&nbsp;&nbsp;&nbsp;</div>
		<div style="display:inline-block;">
			<c:forEach items="${websites}" var="site" >
				<a target="_blank" href="${site.url}">${site.website}</a> 发布日期：<fmt:formatDate value='${site.promoDate}' pattern='yyyy-MM-dd'/><br/>
			</c:forEach>
		</div>
	</div>
	</form:form>
	<div  style="width:100%;height:25px;padding:0px;font-size:20px" class="alert">合并列中代号分别为：
			<c:forEach items="${productList}" var="productName" varStatus="i">
				<th width="5%"><b>${i.index+1}</b>：${productName}</th>
			</c:forEach>
	</div>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th width="10%" rowspan="2" style="text-align:center">日期</th>
				<th width="20%" colspan="${fn:length(productList)}" style="text-align:center">流量</th>
				<th width="20%" colspan="${fn:length(productList)}" style="text-align:center">转化率(%)</th>
				<th width="20%" colspan="${fn:length(productList)}" style="text-align:center">总销量</th>
				<th width="20%" colspan="${fn:length(productList)}" style="text-align:center">促销销量</th>
			</tr>
			<tr>
				<c:forEach items="${productList}" var="productName" varStatus="i">
					<th>${i.index+1}</th>
				</c:forEach>
				<c:forEach items="${productList}" var="productName" varStatus="i">
					<th>${i.index+1}</th>
				</c:forEach>
				<c:forEach items="${productList}" var="productName" varStatus="i">
					<th>${i.index+1}</th>
				</c:forEach>
				<c:forEach items="${productList}" var="productName" varStatus="i">
					<th>${i.index+1}</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${rsMap}" var="dtoEntry">
				<tr>
					<c:set value="${dtoEntry.value}" var="dto" />
					<td>
						<fmt:formatDate value="${dto.dataDate}" pattern="yyyy-MM-dd"/>
						<c:if test="${dto.dataDate eq startDate}"><span style="color:green">(开始)</span></c:if>
						<c:if test="${dto.dataDate eq endDate}"><span style="color:blue">(结束)</span></c:if>
						<c:if test="${not empty promoDates&& fn:contains(promoDates,dto.dataDateStr)}"><span style="color:red">(站外发布)</span></c:if>
					 </td>
					
					<c:forEach items="${dto.sessions}" var="session">
						<td>${session}</td>
					</c:forEach>
					
					<c:if test="${empty dto.sessions}">
						<c:forEach items="${productList}" var="productName" varStatus="i">
							<td/>
						</c:forEach>
					</c:if>
					
					<c:forEach items="${dto.conversions}" var="conversion">
						<td><fmt:formatNumber value="${conversion}" pattern="#.##"/></td>
					</c:forEach>
					<c:if test="${empty dto.conversions}">
						<c:forEach items="${productList}" var="productName" varStatus="i">
							<td/>
						</c:forEach>
					</c:if>
					
					<c:forEach items="${dto.saleQuantitys}" var="saleQuantity">
						<td>${saleQuantity}</td>
					</c:forEach>
					<c:if test="${empty dto.saleQuantitys}">
						<c:forEach items="${productList}" var="productName" varStatus="i">
							<td/>
						</c:forEach>
					</c:if>
					
					<c:forEach items="${dto.saleQuantityPros}" var="saleQuantityPro">
						<td>${saleQuantityPro}</td>
					</c:forEach>
					<c:if test="${empty dto.saleQuantityPros}">
						<c:forEach items="${productList}" var="productName" varStatus="i">
							<td/>
						</c:forEach>
					</c:if>
				</tr>
			</c:forEach>
		
		</tbody>
	</table>
</body>
</html>