<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>销量预测管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
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
			
			$(".report").popover({container:"body",html:true,trigger:'click',content:function(){
				var country = '${salesForecast.country}';
				var productName = $(this).attr("key");
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
				$('.fade').css("max-width","700px").css("top",p.top+45).css("left",p.left+15);
				while($("form .fade").size()>1){
					$(".fade:first").remove();
				}
			});
			
			$("a[rel='popover']").popover({container:"form",trigger:'hover'});
			
			$("a[rel='popover']").live("mouseover",function(){
				$(".report").popover('hide');
				var p = $(this).position();
				$('.fade').css("max-width","700px").css("top",p.top+165).css("left",p.left+18);
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#add").click(function(){
				top.$.jBox("get:${ctx}/amazoninfo/salesForecast/addProduct", {persistent: true,width:505,height:200,title:"新增预测产品", buttons:{"<spring:message code='sys_but_save'/>":1,"<spring:message code='sys_but_closed'/>":2},submit:function(v,h,f){
					if(v==1){
						var param = {};
						param.productsName = h.find("#product").val();
						if(!(param.productsName)){
							top.$.jBox.tip("产品不能为空!","error",{persistent:false,opacity:0});
							return false;
						}
						param.country = '${salesForecast.country}';
						window.location.href = "${ctx}/amazoninfo/salesForecast/save?"+$.param(param);
					}
				}});
			});
			
			$("#excel").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过5M，仅允许导入“xls”或“xlsx”格式文件！"});
			});
			
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
       				if(forecastId.val()){
       					param.id = forecastId.val();
       					param.quantityForecast = newValue;
       				}else{
       					param.productName=$this.parent().find(".productName").val();
       					param.quantityForecast = newValue;
           				param.country = '${salesForecast.country}';	
           				param.week = $this.parent().find(".week").val();
       				}
       				$.get("${ctx}/amazoninfo/salesForecast/saveForecast?"+$.param(param),function(data){
       					if(!(data)){
       						$this.text(oldVal);
       					}else{
       						forecastId.val(data);
       						$.jBox.tip("预测值保存成功！", 'info',{timeout:1000});
       					}
       				});
       				return true;
			}});
			
			var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
			});
			
			new FixedColumns( oTable,{
		 		"iLeftColumns":6,
				"iLeftWidth": 550
		 	} );
			
			$("#importForm").live("submit",function(){
				if(!($(this).find("#uploadFile").val())){
					return false;
				}
				loading('正在导入，请稍等...');
				return true;
			});
			
		});
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
	</script>
</head>
<body>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/amazoninfo/salesForecast/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" ><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input  name="country" type="hidden" value="${salesForecast.country}"/>
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="导入"/>
			<a href="${ctx}/amazoninfo/salesForecast/import/template?country=${salesForecast.country}">下载模板</a>
		</form>
	</div>
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
		<li class="${salesForecast.country eq 'ebay' ?'active':''}"><a class="countryHref" href="#" key="ebay">德国Ebay</a></li>
		<li class="${salesForecast.country eq 'total' ?'active':''}"><a class="countryHref" href="#" key="total">指标汇总</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="salesForecast" action="${ctx}/amazoninfo/salesForecast/" method="post" cssStyle='margin-bottom:0px;'>
		<input id="selectM" style="width: 100px" onclick="WdatePicker({isShowWeek:true,firstDayOfWeek:1,onpicked:function(){$('input[name=dataDate]').val($(this).val());$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" value="<fmt:formatDate value="${salesForecast.dataDate}" pattern="yyyy-MM-dd"/>" class="input-small"/>
		<span style="font-size: 18px">当前选择日期为<fmt:formatDate value="${salesForecast.dataDate}" pattern="yyyy年第w周"/></span>
		<input  name="country" type="hidden" value="${salesForecast.country}"/>
		<input  name="dataDate" type="hidden" value="<fmt:formatDate value="${salesForecast.dataDate}" pattern="yyyy-MM-dd"/>"/>
		<%-- <label>产品名称：</label><form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/> --%>
		<c:if test="${salesForecast.country ne 'total'}">
			&nbsp;<input id="add" class="btn btn-primary" type="button" value="新增产品"/>
			&nbsp;<input id="excel" class="btn btn-primary" type="button" value="导入产品数据"/>
		</c:if>
	</form:form>
	<tags:message content="${message}"/>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: center;vertical-align: middle">序号</th>
					<th style="text-align: center;vertical-align: middle;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产品名称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<th style="text-align: center;vertical-align: middle">图表</th>
					<c:forEach items="${dataDates}" var="week" varStatus="i">
						<th style="text-align: center;vertical-align: middle">
							${week}周
							<br/>[${dataDates1[i.index]}]
						</th>
					</c:forEach>
					<th>操作</th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${data}" var="salesForecastEntry" varStatus="i">
				<tr>
					<td style="text-align: center;vertical-align: middle;">${i.count}</td>
					<td style="text-align: center;vertical-align: middle;"><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${salesForecastEntry.key}">${salesForecastEntry.key}</a></td>
					<td style="text-align: center;vertical-align: middle"><a href="#"  key="${salesForecastEntry.key}" class="report">
						<i class="icon-signal"></i></a>
						<div class="content" style="display: none"></div>
					</td>
					<c:forEach items="${dataDates}" var="week" varStatus="i">
						<td style="text-align: center;vertical-align: middle">
							<c:set var="data" value="${salesForecastEntry.value.data[salesForecast.country][week].quantityForecast}" />
							<c:set var="realData" value="${salesForecastEntry.value.realData[salesForecast.country][week]}" />
							<c:if test="${empty realData}">
								<c:if test="${i.count>3}">
									<a href="#"  data-type="text" data-pk="1"  data-title="Fill in SaleForecast" class="editable editable-click editor" data-original-title="" title="">${data}</a>
									<input class="forecastId" type="hidden" value="${salesForecastEntry.value.data[salesForecast.country][week].id}"/>
									<input class="productName" type="hidden" value="${salesForecastEntry.key}"/>
									<input class="week" type="hidden" value="${week}"/>
								</c:if>
								<c:if test="${i.count<=3}">
									${empty data?0:data}
								</c:if>
							</c:if>
							<c:if test="${not empty realData && not empty data }">
								<c:choose>
										<c:when test="${realData eq data}">
											<a href="#" style="color:green;font-weight: bold;" rel="popover" data-content="预测销量:${data}">${realData}</a>
										</c:when>
										<c:when test="${(realData gt data*1.2) || (realData lt data/1.2)}">
											<a href="#" style="color:red" rel="popover" data-content="预测销量:${data}">${realData}</a>
										</c:when>
										<c:otherwise>
											<a href="#" rel="popover" data-content="预测销量:${data}">${realData}</a>
										</c:otherwise>
								</c:choose>
							</c:if>
							<c:if test="${not empty realData && empty data }">
								${realData}
							</c:if>
						</td>
					</c:forEach>
					<td><a href="${ctx}/amazoninfo/salesForecast/delete?product=${salesForecastEntry.key}&country=${salesForecast.country}" onclick="return confirmx('真的不再预测该产品销量了?', this.href)">Delete</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
</body>
</html>
