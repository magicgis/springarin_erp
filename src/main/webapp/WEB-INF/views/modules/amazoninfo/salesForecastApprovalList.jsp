<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>销量预测审批管理</title>
<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
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
	
		
		$(document).ready(function(){
			if(!(top)){
				top = self;
			}
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".countryHref").click(function(){
				window.location.href = "${ctx}/amazoninfo/salesForecastByMonth?country="+$(this).attr("key");
			});
			$(".report").popover({html:true,trigger:'click',content:function(){
				var nameCountry = $(this).attr("key");
				var productName = nameCountry.split(";")[0];
				var country = nameCountry.split(";")[1];
				var id = nameCountry.split(";")[2];
				$this =$(this);
				var content = $this.parent().find(".content").html();
				if(!content){
					$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/amazoninfo/salesForecast/saleReport',
					    data: {
					    	"country":country,
					    	"productName":productName,
					    	"id":id
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
			
			$("a[rel='popover']").live("mouseover",function(){
				$(".report").popover('hide');
				var p = $(this).position();
				$('.fade').css("max-width","700px");
			});
			
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
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$(this).tab('show');
			});
			
			$("#btnApproval").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批通过?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "1";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/salesForecastRecord/batchApproval?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#btnApprovalVeto").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批否决?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "2";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/salesForecastRecord/batchApproval?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$("#flag").val("1");
				}else{
					$("#flag").val("0");
				}
				doSubmit();
			});
			
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
			$(".editor").editable({mode:'inline',validate:function(data){
				if(data){
					if(!ckeckNums(data)){
						return "销售量必须是大于0的正整数!!";
					}
				}
			},success:function(response,newValue){
        			var param = {};
       				var $this = $(this);
       				var oldVal = $this.text();
       				var forecastId = $this.parent().find(".forecastId");
       				var forecastFlag = $this.parent().find(".forecastFlag").val();
   					param.id = forecastId.val();
   					param.quantityForecast = newValue;
   					param.flag = forecastFlag;
       				$.get("${ctx}/amazoninfo/salesForecastRecord/ajaxSave?"+$.param(param),function(data){
       					if(!$.isNumeric(data)){
       						$this.text(oldVal);
       					}else{
       						$.jBox.tip("预测值修改成功！", 'info',{timeout:1000});
       					}
       				});
       				return true;
			}});
			</shiro:hasPermission>
			
		});
		
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
		
		function doSubmit(){
			$("#searchForm").submit();
		}
		
		function approv(id, state){
			var msg = "确认审批通过?";
			if(state == 2){
				msg = "确认审批否决?";
			}
			top.$.jBox.confirm(msg, "提示", function(v, h, f){
			  if (v == 'ok'){
			  	window.location.href = "${ctx}/amazoninfo/salesForecastRecord/approval?state="+state+"&id=" + id;
			  }else{
			  	return true;
			  }
			  return true; //close
			});
		}
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastRecord/");
			$("#searchForm").submit();
        	return false;
        }
		
		function timeOnChange(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					<li class="${salesForecast.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
				</shiro:hasPermission>
				<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					<shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
						<li class="${salesForecast.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
					</shiro:hasPermission>
				</shiro:lacksPermission>
			</c:if>
		</c:forEach>	
		<li><a class="countryHref" href="#" key="total">指标汇总</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/salesForecastRecord/">销量预测审批</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="salesForecastRecord" action="${ctx}/amazoninfo/salesForecastRecord/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="flag" name="flag" type="hidden" value="${flag}"/>
		<div>
			<!-- 平台 -->
			<spring:message code='amazon_order_form4'/>：
			<form:select path="country" style="width: 120px" onchange="doSubmit()">
				<form:option value="" label="---All---" />
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						<form:option value="${dic.value}" label="${dic.label}" />
					</c:if>
				</c:forEach>
			</form:select>&nbsp;&nbsp;
			审批状态：
			<form:select path="state" style="width: 120px" onchange="doSubmit()">
				<form:option value="0"  label="未审批" />
				<form:option value="1"  label="审批通过" />
				<form:option value="2"  label="审批否决" />
			</form:select>&nbsp;&nbsp;
			<label>提交时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',<c:if test='${0 eq salesForecastRecord.state }'>minDate:'%y-%M-%d',maxDate:'%y-%M-%d',</c:if>onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="month" value="${month}" class="input-small" id="month"/>
			<label>产品名称：</label><form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
			<c:if test="${'0' eq salesForecastRecord.state }"><input type="checkbox" id="isCheck" name="isCheck" class="isCheck" value="${flag }" <c:if test="${'1' eq flag }">checked</c:if>/>&nbsp;急需审批</c:if>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询" onclick="return page();"/>
			
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
				&nbsp;<input id="btnApproval" class="btn btn-primary" type="button" value="审批通过"/>
				&nbsp;<input id="btnApprovalVeto" class="btn btn-primary" type="button" value="审批否决"/>
			</shiro:hasPermission>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
				<th style="width: 20px">
					<span><input type="checkbox"></span>
				</th>
			</shiro:hasPermission>
			<th><spring:message code='amazon_order_form4'/></th><!--平台-->
			<th class="sort productName">产品名</th>
			<c:forEach items="${dates}" var="month" varStatus="status">
				<th class="sort forecast${status.count }">${month }月</th>
			</c:forEach>
			<th class="sort createBy">创建者</th>
			<th class="sort createDate">创建时间</th>
			<th class="sort remark">原因</th>
			<th class="sort state">审批状态</th>
			<th>操作</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="record">
				<tr>
					<c:set var="productName" value="${record.productName}"></c:set>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<td>
						<c:if test="${'0' eq record.state }">
							<div class="checker">
							<span>
							  <input type="checkbox"/>
							  <input type="hidden" value="${record.id}" class="productId"/>
							</span>
							</div>
						</c:if>
						</td>
					</shiro:hasPermission>
					<td>${fns:getDictLabel(record.country,'platform','')}</td>
					<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${productName}" target="_blank">${productName}</a>
						<a href="#"  key="${productName};${record.country};${record.id}" class="report"><i class="icon-signal"></i></a>
						<div class="content" style="display: none"></div>
					</td>
					<c:forEach items="${dates}" var="month" varStatus="status">
						<td>
							<c:if test="${status.count==1 }">
								<c:choose>
									<c:when test="${isEditMap[month] && '0' eq record.state}">
										<a href="#" data-type="text" data-pk="1" class="<c:if test="${isEditMap[month] }">editable editable-click editor</c:if>" data-original-title="" title="">${record.forecast1}</a>
										<input class="forecastId" type="hidden" value="${record.id}"/>
										<input class="forecastFlag" type="hidden" value="1"/>
									</c:when>
									<c:otherwise>${record.forecast1}</c:otherwise>
								</c:choose>
								<br/><span style="color:#08c">系统预测:${data[productName][record.country][month].quantityForecast }</span>
							</c:if>
							<c:if test="${status.count==2 }">
								<c:choose>
									<c:when test="${isEditMap[month] && '0' eq record.state}">
										<a href="#" data-type="text" data-pk="1" class="<c:if test="${isEditMap[month] }">editable editable-click editor</c:if>" data-original-title="" title="">${record.forecast2}</a>
										<input class="forecastId" type="hidden" value="${record.id}"/>
										<input class="forecastFlag" type="hidden" value="2"/>
									</c:when>
									<c:otherwise>${record.forecast2}</c:otherwise>
								</c:choose>
								<br/><span style="color:#08c">系统预测:${data[productName][record.country][month].quantityForecast }</span>
							</c:if>
							<c:if test="${status.count==3 }">
								<c:choose>
									<c:when test="${isEditMap[month] && '0' eq record.state}">
										<a href="#" data-type="text" data-pk="1" class="<c:if test="${isEditMap[month] }">editable editable-click editor</c:if>" data-original-title="" title="">${record.forecast3}</a>
										<input class="forecastId" type="hidden" value="${record.id}"/>
										<input class="forecastFlag" type="hidden" value="3"/>
									</c:when>
									<c:otherwise>${record.forecast3}</c:otherwise>
								</c:choose>
								<br/><span style="color:#08c">系统预测:${data[productName][record.country][month].quantityForecast }</span>
							</c:if>
							<c:if test="${status.count==4 }">
								<c:choose>
									<c:when test="${isEditMap[month] && '0' eq record.state}">
										<a href="#" data-type="text" data-pk="1" class="<c:if test="${isEditMap[month] }">editable editable-click editor</c:if>" data-original-title="" title="">${record.forecast4}</a>
										<input class="forecastId" type="hidden" value="${record.id}"/>
										<input class="forecastFlag" type="hidden" value="4"/>
									</c:when>
									<c:otherwise>${record.forecast4}</c:otherwise>
								</c:choose>
								<br/><span style="color:#08c">系统预测:${data[productName][record.country][month].quantityForecast }</span>
							</c:if>
							<c:if test="${status.count==5 }">
								<c:choose>
									<c:when test="${isEditMap[month] && '0' eq record.state}">
										<a href="#" data-type="text" data-pk="1" class="<c:if test="${isEditMap[month] }">editable editable-click editor</c:if>" data-original-title="" title="">${record.forecast5}</a>
										<input class="forecastId" type="hidden" value="${record.id}"/>
										<input class="forecastFlag" type="hidden" value="5"/>
									</c:when>
									<c:otherwise>${record.forecast5}</c:otherwise>
								</c:choose>
								<br/><span style="color:#08c">系统预测:${data[productName][record.country][month].quantityForecast }</span>
							</c:if>
							<c:if test="${status.count==6 }">
								<c:choose>
									<c:when test="${isEditMap[month] && '0' eq record.state}">
										<a href="#" data-type="text" data-pk="1" class="<c:if test="${isEditMap[month] }">editable editable-click editor</c:if>" data-original-title="" title="">${record.forecast6}</a>
										<input class="forecastId" type="hidden" value="${record.id}"/>
										<input class="forecastFlag" type="hidden" value="6"/>
									</c:when>
									<c:otherwise>${record.forecast6}</c:otherwise>
								</c:choose>
								<br/><span style="color:#08c">系统预测:${data[productName][record.country][month].quantityForecast }</span>
							</c:if>
						</td>
					</c:forEach>
					<td>${record.createBy.name}</td>
					<td><fmt:formatDate value="${record.createDate}" pattern="yyyy-MM-dd HH:mm" /></td>
					<td><a href="#" rel="popover" data-content="${record.remark}">${fn:substring(record.remark,0,10)}${not empty record.remark?'...':''}</a></td>
					<td>
						<c:if test="${'0' eq record.state }">未审批</c:if>
						<c:if test="${'1' eq record.state }">审批通过</c:if>
						<c:if test="${'2' eq record.state }">审批否决</c:if>
					</td>
					<td>
						<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<c:if test="${'0' eq record.state }">
							<div class="btn-group">
								<button type="button" class="btn btn-success" >审批</button>
								<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only"></span>
								</button>
								<ul class="dropdown-menu" id="allExport">
									<li><a onclick="approv(${record.id},1)" href="#">审批通过</a></li>
									<li><a onclick="approv(${record.id},2)" href="#">审批否决</a></li>
								</ul>
							</div>
						</c:if>
						</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>