<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>session和转化率监控管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
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
		$(document).ready(function() {
			
			//<c:if test="${sessionMonitor.country eq 'total'}">
			$("#export").click(function(){
				top.$.jBox.confirm("导出指标汇总表吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/sessionMonitor/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/sessionMonitor");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			//</c:if>
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
					$("#orderBy").val(order+" DESC");
				}
				$("#searchForm").submit();
			});
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("input[name='month']").val("");
				$("#searchForm").submit();
			});
			
			//<c:if test="${sessionMonitor.country ne 'total'}">
			
			$("#syn").click(function(){
				top.$.jBox.confirm("请确认将"+$('#selectM').val()+"月产品指标设置同步为前一月指标吗?注意:此操作不可逆!!","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("input[name='syn']").val("1");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			$("#contentTable").treeTable({expandLevel : 2});
			
			$("select[name='color']").change(function(){
				$("#searchForm").submit();
			});
			
			$(".sessions").editable({validate:function(data){
				if(data){
					if(!ckeckNums(data)){
						return "Sessions必须是大于0的正整数!!";						
					}
				}
			},success:function(response,newValue){
        			var param = {};
       				var $this = $(this);
       				var oldVal = $this.text();
       				var sessionId = $this.parent().parent().find(".sessionId");
       				if(sessionId.val()){
       					param.id = sessionId.val();
       					param.sessions = newValue;
       				}else{
       					param.month = '<fmt:formatDate value="${sessionMonitor.month}" pattern="yyyy-MM-dd"/>';
       					param.productId=$this.parent().parent().find(".productId").val();
       					param.productName=$this.parent().parent().find(".productName").val();
       					param.color=$this.parent().parent().find(".color").val();
       					param.sessions = newValue;
           				param.country = '${sessionMonitor.country}';	
       				}
       				$.get("${ctx}/amazoninfo/sessionMonitor/save?"+$.param(param),function(data){
       					if(!(data)){
       						$this.text(oldVal);
       					}else{
       						sessionId.val(data);
       						$.jBox.tip("sessions保存成功！", 'info',{timeout:2000});
       					}
       				});
       				return true;
			}});
			
			$(".conver").editable({validate:function(data){
				if(data){
					if(!ckeckFloat(data)){
						return "转化率必须大于0并且小于100!!";						
					}
				}
			},success:function(response,newValue){
				var param = {};
   				var $this = $(this);
   				var oldVal = $this.text();
   				var sessionId = $this.parent().parent().find(".sessionId");
   				if(sessionId.val()){
   					param.id = sessionId.val();
   					param.conver = newValue;
   				}else{
   					param.month = '<fmt:formatDate value="${sessionMonitor.month}" pattern="yyyy-MM-dd"/>'
   					param.productId=$this.parent().parent().find(".productId").val();
   					param.productName=$this.parent().parent().find(".productName").val();
   					param.color=$this.parent().parent().find(".color").val();
   					param.conver = newValue;
       				param.country = '${sessionMonitor.country}';	
   				}
   				$.get("${ctx}/amazoninfo/sessionMonitor/save?"+$.param(param),function(data){
   					if(!(data)){
   						$this.text(oldVal);
   					}else{
   						sessionId.val(data);
   						$.jBox.tip("转化率保存成功！", 'info',{timeout:2000});
   					}
   				});
   				return true;
			}});
			//</c:if>
		});
		
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
		
		function ckeckFloat(text) {
			return !(isNaN(text))&&text>0&&text<=100; 
		}
		
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
				<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					<li class="${sessionMonitor.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
				</shiro:hasPermission>
				<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					<shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
						<li class="${sessionMonitor.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
					</shiro:hasPermission>
				</shiro:lacksPermission>
			</c:if>
		</c:forEach>	
		<li class="${sessionMonitor.country eq 'total' ?'active':''}"><a class="countryHref" href="#" key="total">指标汇总</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="sessionMonitor" action="${ctx}/amazoninfo/sessionMonitor/" method="post" class="breadcrumb form-search">
		<input id="orderBy" name="orderBy" type="hidden" value="${orderBy}"/>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input  name="syn" type="hidden" value="0"/>
		<input  name="country" type="hidden" value="${sessionMonitor.country}"/>
		<input  name="month" type="hidden" value="<fmt:formatDate value="${sessionMonitor.month}" pattern="yyyy-MM-dd"/>"/>
		<label>监控月份：</label>
			<input id="selectM" style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){$('input[name=month]').val($(this).val()+'-1');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" value="<fmt:formatDate value="${sessionMonitor.month}" pattern="yyyy-MM"/>" class="input-small"/>
		<c:if test="${sessionMonitor.country ne 'total'}">
			<select name="color" style="width: 140px">
				<option value="">产品类型</option>
				<c:forEach items="${fns:getDictList('product_type')}" var="dic">
					<option value="${dic.value}" ${sessionMonitor.color eq dic.value ?'selected':''}>${dic.label}</option>
				</c:forEach>
				<option value="other" ${sessionMonitor.color eq 'other' ?'selected':''}>其他</option>
			</select>
		</c:if>
		<label>产品名称 ：</label><form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		<c:if test="${sessionMonitor.country eq 'total'}">
			&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出"/>
		</c:if>
		<c:if test="${sessionMonitor.country ne 'total'}">
			 &nbsp;<input id="syn" class="btn btn-primary" type="button" value="快捷同步上月指标"/>
		</c:if>
	</form:form>
	<tags:message content="${message}"/>
	<c:if test="${sessionMonitor.country ne 'total'}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				  <tr>
					   <th style="width:20%">名称</th>
					   <th style="width:10%;" class="sort session">Sessions</th>
					   <th class="sort conversion">Conversion(%)</th>
				   </tr>
			</thead>
			<c:forEach items="${page.list}" var="product" varStatus="i">
				<c:if test="${fn:length(product.barcodeMap2[sessionMonitor.country])>1}">
					<tr id="num${i.index}" >
						<td><b style="font-size: 14px">${product.name}</b></td>
						<td></td>
						<td></td>
					</tr>
					<c:forEach items="${product.barcodeMap2[sessionMonitor.country]}" var="barcode">
						<c:set value="${product.name}_${barcode.key}" var="mapKey" />
						<tr pid="num${i.index}">
							 <td style="text-align: center;vertical-align: middle;"><b style="font-size: 18px;">${empty barcode.key?'No color':barcode.key}</b></td> 
							 <td>
							 	<input type="hidden" value="${map[mapKey].id}" class="sessionId" />
							 	<input type="hidden" value="${product.id}" class="productId" />
							 	<input type="hidden" value="${product.name}" class="productName" />
							 	<input type="hidden" value="${barcode.key}" class="color" />
							 	<a href="#"  data-type="text" data-pk="1"  data-title="Fill in Sessions" class="editable editable-click sessions" data-original-title="" title="">${map[mapKey].sessions}</a>
							 </td>
							 <td><a href="#"  data-type="text" data-pk="1"  data-title="Fill in Conversion" class="editable editable-click conver" data-original-title="" title="">${map[mapKey].conver}</a></td>
						</tr>
					</c:forEach>
				</c:if>
				<c:if test="${fn:length(product.barcodeMap2[sessionMonitor.country])<=1}">
					<c:forEach items="${product.barcodeMap2[sessionMonitor.country]}" var="barcode">
						<c:set value="${product.name}_${barcode.key}" var="mapKey" />
						<tr>
							<td><b style="font-size: 14px">${product.name}${not empty barcode.key?'_':''}${barcode.key}</b></td>
							 <td>
							 	<input type="hidden" value="${map[mapKey].id}" class="sessionId" />
							 	<input type="hidden" value="${product.id}" class="productId" />
							 	<input type="hidden" value="${product.name}" class="productName" />
							 	<input type="hidden" value="${barcode.key}" class="color" />
							 	<a href="#"  data-type="text" data-pk="1"  data-title="Fill in Sessions" class="editable editable-click sessions" data-original-title="" title="">${map[mapKey].sessions}</a>
							 </td>
							 <td><a href="#"  data-type="text" data-pk="1"  data-title="Fill in Conversion" class="editable editable-click conver" data-original-title="" title="">${map[mapKey].conver}</a></td>
						</tr>
					</c:forEach>
				</c:if>
			</c:forEach>
		</table>
	</c:if>
	<c:if test="${sessionMonitor.country eq 'total'}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<tr>
				<th style="width:5%;text-align: center;vertical-align: middle;">序号</th>
				<th style="width:12%;text-align: center;vertical-align: middle;">产品名称</th>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						<th style="width:10%;text-align: center;vertical-align: middle;" class="sort ${dic.value}">${dic.label}</th>
					</c:if>
				</c:forEach>	
			</tr>	
			<c:forEach items="${sessionsMap}" var="product" varStatus="i">
				<tr>
					<td style="text-align: center;vertical-align: middle;">${i.count}</td>
					<td style="text-align: center;vertical-align: middle;">${product.key}</td>				
					<td style="text-align: center;vertical-align: middle;">${product.value['de']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['com']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['uk']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['fr']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['jp']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['it']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['es']}</td>
					<td style="text-align: center;vertical-align: middle;">${product.value['ca']}</td>
				</tr>	
			</c:forEach>
		</table>
	</c:if>
</body>
</html>
