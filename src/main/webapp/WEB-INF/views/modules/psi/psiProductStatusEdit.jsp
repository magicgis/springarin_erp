<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品状态编辑</title>
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
			$(".report").popover({html:true,trigger:'click',content:function(){
				var nameCountry = $(this).attr("key");
				var productName = nameCountry.split(";")[0];
				var country = nameCountry.split(";")[1];
				$this =$(this);
				var content = $this.parent().find(".content").html();
				if(!content){
					$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/amazoninfo/salesForecast/saleReport',
					    data: {
					    	"country":country,
					    	"productName":productName
					    },
					    success:function(data){ 
					    	content = data;
					    	$this.parent().find(".content").html(data);
				        }
					});
				}
				return content;
			}});
			
			$(".report").live("click",function(){
				$this = this;
				$(".report").each(function(){
					if(this != $this){
						$(this).popover('hide');
					}
				});
				var p = $(this).position();
				$('.fade').css("max-width","700px");
				while($("form .fade").size()>1){
					$(".fade:first").remove();
				}
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("a[rel='popover']").live("mouseover",function(){
				$(".report").popover('hide');
				var p = $(this).position();
				$('.fade').css("max-width","700px");
			});
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$(".isCheck").val("1");
				}else{
					$(".isCheck").val("0");
				}
			});
			
			$(".back").on("click",function(){
				var flag = '${flag}';
				if(flag == "1"){
					window.location.href = "${ctx}/psi/productEliminate";
				}else if(flag == "2"){
					window.location.href = "${ctx}/psi/productEliminate/isMainlist";
				} else if(flag == "3") {
					window.location.href = "${ctx}/psi/productEliminate/addedMonthlist";
				} else if(flag == "4") {
					window.location.href = "${ctx}/psi/productEliminate/forecastlist";
				} else {
					window.location.href = "${ctx}/psi/productEliminate/transportTypelist";
				}
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
									window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
								}
							});
			            } else {
							window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
			            }
						return true;
					});
					//window.location.href = "${ctx}/amazoninfo/reviewer/batchSendEmail?"+$.param(params);
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
			$(".dateEditor").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.colorSync = $(".isCheck").val();
					param.productName = $this.parent().find(":hidden").val();
					param.addedMonth = newValue;
					$.get("${ctx}/psi/productEliminate/addedMonth?"+$.param(param),function(data){
						if(data != "1"){
							$this.text(oldVal);
						}else{
							$.jBox.tip("保存上架日期成功！", 'info',{timeout:2000});
						}
					});
					return true;
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
								window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
							}
						});
		            } else {
						window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
		            }
					return true;
				});
			} else {
				$.get(url,function(data){
					if(data !=null && data=="true"){
						top.$.jBox.tip(productName + "状态修改成功！");
						window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
					}
				});
			}
		}
		
		//修改销售预测方案
		function changeForecast(obj, country){
			var isC = '${isC}';
			var flag = '${flag}';
			var colorSync = $(".isCheck").val();
			var productName = '${productName}';
			var stateStr = "";
			var method = "updateForecast";
			stateStr = "forecast=" + obj.value;
			stateStr = stateStr+"&productName="+productName;
			stateStr = stateStr+"&platform="+country;
			stateStr = stateStr+"&colorSync="+colorSync;
			stateStr = stateStr+"&isC="+isC;
			var url = "${ctx}/psi/productEliminate/"+method+"?"+stateStr;
			if(obj.value == "3" || isC == "1"){
				top.$.jBox.confirm('此操作将会同步到整个类型的产品,确定要执行吗？', '提示', function (v, h, f) {
					if (v === 'ok') {
						$.get(url,function(data){
							if(data !=null && data=="true"){
								top.$.jBox.tip(productName + "状态修改成功！");
								window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
							}
						});
		            } else {
						window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
		            }
					return true;
				});
			}else {
				$.get(url,function(data){
					if(data !=null && data=="true"){
						top.$.jBox.tip(productName + "状态修改成功！");
						window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
					} else {
						window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+productName+"&flag=" + flag;
					}
				});
			}
		}
		
		function changeProduct(obj){
			var flag = '${flag}';
			window.location.href = "${ctx}/psi/productEliminate/setStatus?productName="+obj.value+"&flag="+flag;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${'1' eq flag?'active':''}"><a href="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=1">产品定位管理</a></li>
		<%--<li class="${'3' eq flag?'active':''}"><a href="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=3">上架时间管理</a></li>
		<li class="${'4' eq flag?'active':''}"><a href="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=4">销售预测方案</a></li> --%>
	</ul>
	<c:if test="${'4' eq flag }">
		<div class="alert alert-info"><strong>销售预测方案说明：A-前月预测法&nbsp;&nbsp;&nbsp;&nbsp; B-月平均预测法&nbsp;&nbsp;&nbsp;&nbsp;  C-季节指数预测法</strong></div>
	</c:if>
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
		<c:if test="${'4' eq flag }">
		<div style="float:left;width:100%;display:inline;">
			<div class="control-group" style="float:left;width:30%" >
				<label class="control-label" style="width:125px">产品类型:</label>
				<div class="controls" style="margin-left:135px">
					${product.type}
				</div>
			</div>
		</div>
	  	</c:if>
	  	
	    <blockquote style="float:left;width:98%;">
	    	<c:if test="${'1' eq flag }">
			<p style="font-size: 15px;font-weight: bold">淘汰明细</p>
			</c:if>
	    	<c:if test="${'2' eq flag }">
			<p style="font-size: 15px;font-weight: bold">主力明细</p>
			</c:if>
	    	<c:if test="${'3' eq flag }">
			<p style="font-size: 15px;font-weight: bold">上架时间</p>
			</c:if>
	    	<c:if test="${'4' eq flag }">
			<p style="font-size: 15px;font-weight: bold">销售预测方案</p>
			</c:if>
	    	<c:if test="${'5' eq flag }">
			<p style="font-size: 15px;font-weight: bold">运输方式</p>
			</c:if>
		</blockquote>
	
		<c:if test="${'1' eq flag }">	<!--淘汰-->
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
				<shiro:hasPermission name="amazoninfo:eliminate:edit">
			  		<div class="control-group">
						<label class="control-label" style="width:125px">ALL:&nbsp;&nbsp;</label>
						<div class="controls" style="margin-left:120px">
							<label><input id="all_radio" name="all_radio" type="radio" value="1" onchange="changeStatus(this,'')" /><spring:message code="psi_product_sale"/></label>
							&nbsp;&nbsp;
							<label><input id="all_radio" name="all_radio" type="radio" value="0" onchange="changeStatus(this,'')"  ${'0' eq product.isSale?'checked':'' } /><spring:message code="psi_product_notSale"/></label>
						</div>
					</div>
				</shiro:hasPermission>
			</shiro:hasPermission>
		</c:if>
		<c:if test="${'5' eq flag }">	<!--运输方式-->
			<div class="control-group">
				<label class="control-label" style="width:125px">ALL:&nbsp;&nbsp;</label>
				<div class="controls" style="margin-left:120px">
					<label><input id="all_radio" name="all_radio" type="radio" value="1" onchange="changeStatus(this,'')" />海运</label>
					&nbsp;&nbsp;
					<label><input id="all_radio" name="all_radio" type="radio" value="2" onchange="changeStatus(this,'')" />空运</label>
				</div>
			</div>
		</c:if>
	  	<c:forEach items="${list}" var="eliminate">
	  		<div class="control-group">
				<label class="control-label" style="width:125px">${fns:getDictLabel(eliminate.country,'platform','')}:&nbsp;&nbsp;</label>
				<c:if test="${'1' eq flag }">	<!--淘汰-->
	  			<c:set var="isDisabled" value="1" />
	  			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					<shiro:hasPermission name="amazoninfo:eliminate:edit">
						<c:set var="isDisabled" value="0" />
					</shiro:hasPermission>
				</shiro:hasPermission>
	  			<shiro:hasPermission name="amazoninfo:feedSubmission:${eliminate.country }">
					<shiro:hasPermission name="amazoninfo:eliminate:edit">
	  					<c:set var="isDisabled" value="0" />
	  				</shiro:hasPermission>
	  			</shiro:hasPermission>
				<div class="controls" style="margin-left:120px">
					<c:if test="${'1' eq isDisabled}"><label><c:if test="${'1' eq eliminate.isSale}"><spring:message code="psi_product_sale"/></c:if><c:if test="${'0' eq eliminate.isSale}"><spring:message code="psi_product_notSale"/></c:if></label></c:if>
					<c:if test="${'0' eq isDisabled}">
					<label><input class="eliRadio" id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="1" ${'1' eq eliminate.isSale?'checked':'' } /><spring:message code="psi_product_sale"/></label>
					&nbsp;&nbsp;
					<label><input class="eliRadio" id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="0" ${'0' eq eliminate.isSale?'checked':'' } /><spring:message code="psi_product_notSale"/></label>
					</c:if>
				</div>
				</c:if>
				<c:if test="${'2' eq flag }">	<!--主力-->
				<div class="controls" style="margin-left:120px">
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="1" onchange="changeStatus(this,'${eliminate.country }')"  ${'1' eq eliminate.isMain?'checked':'' } <c:if test="${fns:getUser().id ne product.createUser.id}">disabled</c:if> />主力</label>
					&nbsp;&nbsp;
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="0" onchange="changeStatus(this,'${eliminate.country }')"  ${'0' eq eliminate.isMain?'checked':'' } <c:if test="${fns:getUser().id ne product.createUser.id}">disabled</c:if> />普通</label>
				</div>
				</c:if>
				<c:if test="${'3' eq flag }">	<!--上架时间-->
				<div class="controls" style="margin-left:120px">
					<input type="hidden" value="${productName}_${eliminate.country}" />
					<a href="#" class="dateEditor"  data-type="date" data-pk="1" data-title="Enter Date">
						<c:if test="${not empty eliminate.addedMonth}">${fn:replace(eliminate.addedMonth,' 00:00:00','')}</c:if>
					</a>
				</div>
				</c:if>
				<c:if test="${'4' eq flag }">	<!--销量预测-->
				<div class="controls" style="margin-left:120px">
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="1" onchange="changeForecast(this,'${eliminate.country }')"  ${'1' eq eliminate.salesForecastScheme?'checked':'' }/>A方案</label>
					&nbsp;&nbsp;
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="2" onchange="changeForecast(this,'${eliminate.country }')"  ${'2' eq eliminate.salesForecastScheme?'checked':'' }/>B方案</label>
					<c:if test="${cType || '3' eq eliminate.salesForecastScheme }">
					&nbsp;&nbsp;
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="3" onchange="changeForecast(this,'${eliminate.country }')"  ${'3' eq eliminate.salesForecastScheme?'checked':'' }/>C方案</label>
					</c:if>
					<c:if test="${not empty eliminate.salesForecastScheme }">
						&nbsp;&nbsp;
						<label>上月预测销量：${empty data[productName][eliminate.country][month][eliminate.salesForecastScheme]?'':data[productName][eliminate.country][month][eliminate.salesForecastScheme]}</label>
						&nbsp;&nbsp;&nbsp;&nbsp;
						<label>上月实际销量：${empty saleData[productName][eliminate.country][month]?0:saleData[productName][eliminate.country][month]}</label>
						&nbsp;&nbsp;
						<a href="#" key="${productName};${eliminate.country}" class="report">
							<button type="button" class="btn btn-success" >历史销量</button>
							<%--<i class="icon-signal"></i> --%>
						</a>
						<div class="content" style="display: none"></div>
					</c:if>
				</div>
				</c:if>
				<c:if test="${'5' eq flag }">	<!--运输方式-->
				<div class="controls" style="margin-left:120px">
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="1" onchange="changeStatus(this,'${eliminate.country }')"  ${'1' eq eliminate.transportType?'checked':'' } />海运</label>
					&nbsp;&nbsp;
					<label><input id="${eliminate.country }_radio" name="${eliminate.country }_radio" type="radio" value="2" onchange="changeStatus(this,'${eliminate.country }')"  ${'2' eq eliminate.transportType?'checked':'' } />空运</label>
				</div>
				</c:if>
			</div>
	  	</c:forEach>
		
			<div style="float:left;width:100%" class="form-actions">
				<c:if test="${'1' eq flag }">
					<input id="btnSave" class="btn save" type="button" value="保存"/>
				</c:if>
				<input id="btnCancel" class="btn back" type="button" value="返 回"/>
			</div>
		</form:form>
</body>
</html>
