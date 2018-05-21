<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品分颜色(区分平台)属性</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		$(document).ready(function() {
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
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});

			$(":checkbox.isMain").bootstrapSwitch({'onText':'主力','offText':'普通',size:'small'});
			$(":checkbox.transportType").bootstrapSwitch({'onText':'海运','offText':'空运',size:'small'});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".editPeriod").editable({
					mode:'inline',
					showbuttons:'bottom',
					validate:function(data){
						if(data){
							if(!ckeckNums(data)){
								return "缓冲周期必须是大于0的正整数!!";
							}
						}
					},
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(".attrId").val();
						param.bufferPeriod = newValue;
						param.flag = "2";
						var productName = $this.parent().find(".colorName").val();
						$.get("${ctx}/psi/productEliminate/updateAttrByCountry?"+$.param(param),function(data){
							if(data != "1"){
								$this.text(oldVal);
							}else{
								$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
								$("#searchForm").submit();
							}
							
						});
						return true;
			 }});
			
			$(".editCommission").editable({
				mode:'inline',
				showbuttons:'bottom',
				validate:function(data){
					if(data){
						if(!ckeckNums(data)){
							return "佣金缓冲周期必须是大于0的正整数!!";
						}
					}
				},
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(".attrId").val();
					param.commissionPcent = newValue;
					$.get("${ctx}/psi/productEliminate/updateCommissionPcent?"+$.param(param),function(data){
						if(data != "1"){
							$this.text(oldVal);
						}else{
							$.jBox.tip("修改成功！", 'info',{timeout:2000});
						}
					});
					return true;
		 }});
			
			$("#batchSea").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm("确认批量设置为海运?","系统提示",function(v,h,f){
						if(v=='ok'){
							var param = {};
							param.eid = [];
							$(".checked :hidden").each(function(){
								param.eid[param.eid.length] = $(this).val();
							}); 
							param.transportType = 1;
							$.get("${ctx}/psi/psiProductAttribute/batchTranType?"+$.param(param),function(data){
								if(data != "1"){
									$.jBox.tip("修改失败！", 'info',{timeout:2000});
								}else{
									$.jBox.tip("修改成功！", 'info',{timeout:2000});
									$("#searchForm").submit();
								}
							});
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("请至少勾选一条信息!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#batchSky").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm("确认批量设置为空运?","系统提示",function(v,h,f){
						if(v=='ok'){
							var param = {};
							param.eid = [];
							$(".checked :hidden").each(function(){
								param.eid[param.eid.length] = $(this).val();
							}); 
							param.transportType = 2;
							$.get("${ctx}/psi/psiProductAttribute/batchTranType?"+$.param(param),function(data){
								if(data != "1"){
									$.jBox.tip("修改失败！", 'info',{timeout:2000});
								}else{
									$.jBox.tip("修改成功！", 'info',{timeout:2000});
									$("#searchForm").submit();
								}
							});
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("请至少勾选一条信息!","error",{persistent:false,opacity:0});
				}
			});
				
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
				
		function updateTransportType(obj,id,productName){
			var param = {};
			if($(obj).attr("checked")){
				param.transportType = "1";
			}else{
				param.transportType = "2";
			}
			param.id = id;
			param.flag = "4";
			$.get("${ctx}/psi/productEliminate/updateAttrByCountry?"+$.param(param),function(data){
				if(data == "1"){
					$.jBox.tip(productName+"修改成功！", 'info',{timeout:2000});
					$("#searchForm").submit();
				}
			});
		}
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/psi/psiProductAttribute/listByCountry/");
			$("#searchForm").submit();
        	return false;
        }
		
		function doSubmit(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list"><spring:message code="psi_product_list"/></a></li>
		<li><a href="${ctx}/psi/psiProductAttribute">产品分颜色(不分平台)属性</a></li>
		<li class="active"><a href="${ctx}/psi/psiProductAttribute/listByCountry">产品分颜色(区分平台)属性</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiProductEliminate" action="${ctx}/psi/psiProductAttribute/listByCountry/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="isSale" name="isSale" type="hidden" value="1"/>
		<div>
			<label>产品：</label>
			<form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
			<label>国家：</label>
			<select name="country" id="country" style="width: 120px" onchange="doSubmit();">
				<option value=""  ${empty psiProductEliminate.country?'selected':''}>--<spring:message code="custom_email_template_select"/>--</option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
					<c:if test="${dic.value ne 'com.unitek'}">
						<option value="${dic.value}" ${dic.value eq psiProductEliminate.country?'selected':''}>${dic.label}</option>	
					</c:if>
				</c:forEach>
			</select>
			<label>运输方式：</label>
			<select name="transportType" id="transportType" style="width: 80px" onchange="doSubmit();">
				<option value=""  ${empty psiProductEliminate.transportType?'selected':''}>--<spring:message code="custom_email_template_select"/>--</option>
				<option value="1" ${'1' eq psiProductEliminate.transportType?'selected':''}>海运</option>	
				<option value="2" ${'2' eq psiProductEliminate.transportType?'selected':''}>空运</option>	
			</select>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			&nbsp;<a class="btn btn-primary" href='${ctx}/psi/psiProductAttribute/exportByCountry'>导出</a>
			<shiro:hasPermission name="amazoninfo:attr:edit">
			&nbsp;<input id="batchSea" class="btn btn-primary" type="button" value="批量设置海运"/>
			&nbsp;<input id="batchSky" class="btn btn-primary" type="button" value="批量设置空运"/>
			</shiro:hasPermission>
		</div>
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 20px">
					<span><input type="checkbox"></span>
				</th>
				<th style="text-align: center;vertical-align: middle;" class="sort productName">产品</th>
				<th style="text-align: center;vertical-align: middle;" class="sort country">平台</th>
				<th style="text-align: left;vertical-align: middle;" class="sort bufferPeriod">缓冲周期</th>
				<th style="text-align: left;vertical-align: middle;" class="sort transportType">运输方式</th>
				<th style="text-align: left;vertical-align: middle;">滚动31日销</th>
				<th style="text-align: left;vertical-align: middle;">生产周期</th>
				<th style="text-align: left;vertical-align: middle;">佣金(%)</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="product" varStatus="i">
				<tr>
				 <td>
					<div class="checker">
					<span>
						<c:choose>
				        	<c:when test="${product.product.fanOu && fn:contains('uk,fr,it,es', product.country) }">
							</c:when>
							<c:otherwise>
							  <input type="checkbox"/>
							  <input type="hidden" value="${product.id}" class="eliminateId"/>
							</c:otherwise>
						</c:choose>
					</span>
					</div>
				 </td>
					<td style="text-align: left;vertical-align: middle;">
						<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${product.colorName }">${product.colorName }</a>
						<span style="color: gray">
						   <c:if test="${'0' eq product.isSale}"><spring:message code="psi_inventoty_obsolete" /></c:if>
						</span>
					</td>
					<td>${fns:getDictLabel(product.country,'platform','')}</td>
					<td style="text-align: left;vertical-align: middle;">
				        <input type="hidden" class="attrId" value="${product.id }" />
				        <input type="hidden" class="colorName" value="${product.colorName }" />
				        <shiro:hasPermission name="amazoninfo:attr:edit">
				        	<c:choose>
					        	<c:when test="${product.product.fanOu && fn:contains('uk,fr,it,es', product.country) }">
									<a style="text-decoration:none;color:black">	<!--加a标签兼容自定义排序方法 -->
										${product.bufferPeriod }
									</a>
								</c:when>
								<c:otherwise>
									<a href="#" class="editPeriod"  data-type="text" data-pk="1" data-title="Enter" >
										 ${product.bufferPeriod }
									</a>
								</c:otherwise>
							</c:choose>
						</shiro:hasPermission>
						<shiro:lacksPermission name="amazoninfo:attr:edit">
							<a style="text-decoration:none;color:black">	<!--加a标签兼容自定义排序方法 -->
								${product.bufferPeriod }
							</a>
						</shiro:lacksPermission>
					</td>
					<td>
						<c:choose>
					        	<c:when test="${product.product.fanOu && fn:contains('uk,fr,it,es', product.country) }">
									${product.transportType eq '1'?'海运':'空运'}
								</c:when>
								<c:otherwise>
									<shiro:hasPermission name="psi:productAttribute:edit">
										<input class="transportType" type="checkbox" ${product.transportType eq '1'?'checked':''}  onchange="updateTransportType(this,'${product.id}','${product.colorName}')" />
									</shiro:hasPermission>
									<shiro:lacksPermission name="psi:productAttribute:edit">
										${product.transportType eq '1'?'海运':'空运'}
									</shiro:lacksPermission>
								</c:otherwise>
						</c:choose>
						
					</td>
					<td>
						<c:choose>
				        	<c:when test="${product.product.fanOu && 'de' eq product.country }">
								<c:set var="name" value="${product.colorName}" />
								<c:set var="de" value="${name}_de" />
								<c:set var="de"  value="${fancha[de].day31Sales>0?fancha[de].day31Sales:0}" />
								<c:set var="fr" value="${name}_fr" />
								<c:set var="fr"  value="${fancha[fr].day31Sales>0?fancha[fr].day31Sales:0}" />
								<c:set var="uk" value="${name}_uk" />
								<c:set var="uk"  value="${fancha[uk].day31Sales>0?fancha[uk].day31Sales:0}" />
								<c:set var="it" value="${name}_it" />
								<c:set var="it"  value="${fancha[it].day31Sales>0?fancha[it].day31Sales:0}" />
								<c:set var="es" value="${name}_es" />
								<c:set var="es"  value="${fancha[es].day31Sales>0?fancha[es].day31Sales:0}" />
								<c:set value="${de+fr+uk+it+es}" var="total31Days" />
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}">${total31Days>0?total31Days:'0'}</a>
							</c:when>
							<c:otherwise>
								<c:set var="key" value="${product.colorName}_${product.country}" />
								${fancha[key].day31Sales>0?fancha[key].day31Sales:0}
							</c:otherwise>
						</c:choose>
					</td>
					<td>${product.product.producePeriod}</td>
					<td>
					    <input type="hidden" class="attrId" value="${product.id }" />
					    <shiro:hasPermission name="amazoninfo:feedSubmission:${product.country}">
					        <a href="#" class="editCommission"  data-type="text" data-pk="1" data-title="Enter" >
								 ${product.commissionPcent }
							</a>
					    </shiro:hasPermission>
					    <shiro:lacksPermission name="amazoninfo:feedSubmission:${product.country}">
					         ${product.commissionPcent }
					    </shiro:lacksPermission>
					</td>
	           </tr> 
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
