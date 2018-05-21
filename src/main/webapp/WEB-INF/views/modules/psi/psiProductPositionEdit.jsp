<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品定位编辑</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
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
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$(".isCheck").val("1");
				}else{
					$(".isCheck").val("0");
				}
			});
			
			$(".back").on("click",function(){
				window.location.href = "${ctx}/psi/productEliminate";
			});
			
			$(".save").on("click",function(){
				if($(".eliRadio:checked").size() > 0){
					var params = {};
					var flag = '${flag}';
					var productName = '${productName}';
					params.colorSync = $(".isCheck").val();
					params.productName = productName;
					var isSaleStr = "";
					$(".eliRadio:checked").each(function(){
						var eliment = $(this).val();
						var country = $(this).attr("id").split("_")[0];
						isSaleStr += country + "_" + eliment + ",";
					});
					params.isSaleStr = isSaleStr;
					top.$.jBox.confirm('确认修改产品在售属性？', '提示', function (v, h, f) {
						if (v === 'ok') {
							$.get("${ctx}/psi/productEliminate/batchUpdateIsSale?"+$.param(params),function(data){
								if(data !=null && data=="true"){
									top.$.jBox.tip(productName + "状态修改成功！");
									window.location.href = "${ctx}/psi/productEliminate/setPosition?productName="+productName+"&flag=" + flag;
								}
							});
			            } else {
							window.location.href = "${ctx}/psi/productEliminate/setPosition?productName="+productName+"&flag=" + flag;
			            }
						return true;
					});
					//window.location.href = "${ctx}/amazoninfo/reviewer/batchSendEmail?"+$.param(params);
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
		});
			
		
		function changeStatus(obj, country){
			var colorSync = $(".isCheck").val();
			var flag = '${flag}';
			var productName = '${productName}';
			var stateStr = "";
			var method = "updateIsSale";
			if("1" == flag){	//淘汰
				stateStr = "isSale=" + obj.value;
			} else if("2" == flag){ //主力
				stateStr = "isMain=" + obj.value;
				method = "updateIsMain";
			} else {
				stateStr = "transportType=" + obj.value;
				method = "updateTransportType";
			}
			stateStr = stateStr+"&productName="+productName;
			stateStr = stateStr+"&platform="+country;
			stateStr = stateStr+"&colorSync="+colorSync;
			var url = "${ctx}/psi/productEliminate/"+method+"?"+stateStr;
			if("1" == flag){
				top.$.jBox.confirm('确认修改产品在售属性？', '提示', function (v, h, f) {
					if (v === 'ok') {
						$.get(url,function(data){
							if(data !=null && data=="true"){
								top.$.jBox.tip(productName + "状态修改成功！");
								window.location.href = "${ctx}/psi/productEliminate/setPosition?productName="+productName+"&flag=" + flag;
							}
						});
		            } else {
						window.location.href = "${ctx}/psi/productEliminate/setPosition?productName="+productName+"&flag=" + flag;
		            }
					return true;
				});
			} else {
				$.get(url,function(data){
					if(data !=null && data=="true"){
						top.$.jBox.tip(productName + "状态修改成功！");
						window.location.href = "${ctx}/psi/productEliminate/setPosition?productName="+productName+"&flag=" + flag;
					}
				});
			}
		}
		
		function changeProduct(obj){
			window.location.href = "${ctx}/psi/productEliminate/setPosition?productName="+obj.value;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list">产品列表</a></li>
		<li class="active"><a href="${ctx}/psi/productEliminate/setPosition?productName=${productName}&flag=1">产品定位管理</a></li>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">产品其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
		       <li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
		       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/addedMonthlist">上架日期</a></li>
		       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		        <li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		    </ul>
	   </li>
	</ul>
	<form:form id="inputForm" modelAttribute="priceDto" action="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=${flag}" method="post" class="form-horizontal">
	    <input type="hidden" name="flag" value="${flag}">
		<br/>
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">产品信息</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:35%;height:30px">
				<label class="control-label" style="width:125px">产品名:</label>
				<div class="controls" style="margin-left:120px">
					<select style="width:200px;" id="productName" name="productName" onchange="changeProduct(this)">
						<c:forEach items="${productNames}" var="name" varStatus="i" >
							 <option value="${name}" ${name eq productName ?'selected':''}>${name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<c:if test="${hasMulColor}">
				<div class="control-group" style="float:left;width:40%;height:30px">
					<label class="control-label" style="width:85px">同步其他颜色:</label>
					<div class="controls" style="margin-left:90px">
					<c:if test="${'5' ne flag }">
					 <input type="checkbox" name="isCheck" class="isCheck" value="0"/>
					 </c:if>
					 <c:if test="${'5' eq flag }">
					 <input type="checkbox" name="isCheck" class="isCheck" value="1" checked/>
					 </c:if>
					</div>
				</div>
			</c:if>
			<c:if test="${!hasMulColor}">
				<div class="control-group" style="float:left;width:40%;height:30px">
				 	<input type="hidden" name="isCheck" class="isCheck" value="0" />
				</div>
			</c:if>
		</div>
	  	
	    <blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">产品定位明细</p>
		</blockquote>
	
		<!--产品定位-->
		<shiro:hasPermission name="amazoninfo:productPosition:edit">
	  		<div class="control-group">
				<label class="control-label" style="width:125px">ALL:&nbsp;&nbsp;</label>
				<div class="controls" style="margin-left:120px">
					<c:forEach items="${fns:getDictList('product_position')}" var="dic">
							<c:if test="${dic.value eq eliminate.isSale}">${dic.label}</c:if>
						<label><input id="all_radio" name="all_radio" type="radio" value="${dic.value}"  onchange="changeStatus(this,'')" ${('4' eq dic.value && '4' eq product.isSale)?'checked':'' } />${dic.label}</label>
						&nbsp;&nbsp;
					</c:forEach>
				</div>
			</div>
		</shiro:hasPermission>
	  	<c:forEach items="${list}" var="eliminate">
	  		<div class="control-group">
				<label class="control-label" style="width:125px">${fns:getDictLabel(eliminate.country,'platform','')}:&nbsp;&nbsp;</label>
	  			<c:set var="isDisabled" value="1" />
				<shiro:hasPermission name="amazoninfo:productPosition:edit">
					<c:set var="isDisabled" value="0" />
				</shiro:hasPermission>
				<div class="controls" style="margin-left:120px">
					<c:if test="${'1' eq isDisabled}">
						<label>
							<c:forEach items="${fns:getDictList('product_position')}" var="dic">
								<c:if test="${dic.value eq eliminate.isSale}">${dic.label}</c:if>
							</c:forEach>
						</label>
					</c:if>
					<c:if test="${'0' eq isDisabled}">
						<c:forEach items="${fns:getDictList('product_position')}" var="dic">
							<label><input class="eliRadio" id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="${dic.value}" ${dic.value eq eliminate.isSale?'checked':'' } />${dic.label}</label>
							&nbsp;&nbsp;
						</c:forEach>
					</c:if>
				</div>
			</div>
	  	</c:forEach>
		
			<div style="float:left;width:100%" class="form-actions">
				<input id="btnSave" class="btn save" type="button" value="保存"/>
				<input id="btnCancel" class="btn back" type="button" value="返 回"/>
			</div>
		</form:form>
</body>
</html>
