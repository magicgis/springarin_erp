<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>line</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
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
		
		
		$(document).ready(function() {

			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			$(".edit").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						if(data){
							if(!ckeckNums(data)){
								return "品类佣金必须是大于0的正整数!!";
							}
						}
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(".attrId").val();
						param.productType = encodeURI($this.parent().find(".productType").val());
						param.country = $this.parent().find(".country").val();
						param.commissionPcent = newValue;
						$.get("${ctx}/amazoninfo/amazonPortsDetail/updateProductTypeCharge?"+$.param(param),function(data){
							if(!data){
								$this.text(oldVal);
							}else{
								$.jBox.tip("修改成功！", 'info',{timeout:2000});
								$this.parent().find(".attrId").val(data);
							}
						});
						return true;
			 }});
				
			
				
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 15,
				"aLengthMenu":[[15, 30, 60,100,-1], [15, 30, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
			 	"ordering":true,
			     "aaSorting": [[1, "asc" ]]
			});
			
			// $(".row:first").append("&nbsp;&nbsp;<a class='btn'  href='${ctx}/psi/psiProductAttribute/export'>导出</a>");
			
		});

		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}

		function ckeckWeeks(text) {
			if(text.length != 1){
				return false;
			}
			if(text == 1 || text == 2 || text == 3 || text == 0){
				return true;
			}
			return false;
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list">产品列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">产品其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
		       <li><a href="${ctx}/psi/productEliminate">在售淘汰明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/addedMonthlist">上架日期</a></li>
		       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		    </ul>
	   </li>
	</ul>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:30%;">产品类型</th>
				<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
					<c:if test="${dic.value ne 'com.unitek'}">
						<th style="text-align: left;vertical-align: middle;">${fns:getDictLabel(dic.value,'platform','')}</th>
					</c:if>
		        </c:forEach>
				
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${dictsList}" var="countryDict" varStatus="i">
				<tr>
					<td style="text-align: left;vertical-align: middle;">
							 ${countryDict.value }
					</td>
					<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
						<c:if test="${dic.value ne 'com.unitek'}">
							<td style="text-align: left;vertical-align: middle;">
							  <c:set var="key" value="${countryDict.value}_${dic.value}" />
							  <c:set var="showFlag" value="1" />
							   <shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
							  <%--  <shiro:hasPermission name="psi:product:commissionEdit"> --%>
							  		<c:set var="showFlag" value="0" />
							       <input type="hidden" class="productType" value="${countryDict.value}" />
								    <input type="hidden" class="country" value="${dic.value}" />
								   
								    <input type="hidden" class="attrId" value="${codeMap[key].id }" />
								    <a href="#" class="edit"  data-type="text" data-pk="1" data-title="Enter" >
									     	${codeMap[key].commissionPcent}
									</a>
									<%-- </shiro:hasPermission> --%>
							   </shiro:hasPermission>
							   <c:if test="${'1' eq showFlag }">${codeMap[key].commissionPcent}</c:if>
							</td>
						</c:if>
		            </c:forEach>
	           </tr> 
			</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
