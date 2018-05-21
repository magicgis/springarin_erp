<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>enterprise_week</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
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
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
		 	$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 10,
				"aLengthMenu":[[10, 20, 60,100,-1], [10, 20, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[0, "asc" ]]
			});
			// $(".row:first").append($("#searchContent").html()); 
			 
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*10)/10;  
	            return f;  
	     }  
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
        <li class="${empty enterpriseWeek.country?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${enterpriseWeek.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	
	<form id="searchForm" action="${ctx}/amazoninfo/enterpriseGoal" method="post" >
	<input  name="country" id="country" type="hidden" value="${enterpriseGoal.country}" />
		&nbsp;&nbsp;&nbsp;&nbsp;统计时间:
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM'});"  readonly="readonly"  class="Wdate" type="text" id="startMonth" name="startMonth" value="<fmt:formatDate value="${enterpriseGoal.startMonth}" pattern="yyyyMM" />" class="input-small"/>&nbsp;-&nbsp;
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM'});"  readonly="readonly"  class="Wdate" type="text" id="startMonth" name="startMonth" value="<fmt:formatDate value="${enterpriseGoal.startMonth}" pattern="yyyyMM" />" class="input-small"/>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form>
	
	<table id="enterpriseWeight" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:12%;">单位：欧</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期一</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期二</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期三</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期四</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期五</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期六</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期日</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">合计</th>
			</tr>
		</thead>
		<tbody>
		     <tr>
			       <td style="text-align: center;vertical-align: middle;">日销售权重指数</td>
			       <td style="text-align: center;vertical-align: middle;" >${weightAfter.monday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.tuesday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.wednesday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.thursday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.friday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.saturday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.sunday}</td>
				   <td style="text-align: center;vertical-align: middle;" ><fmt:formatNumber pattern="#######.##" value="${weightAfter.monday+weightAfter.tuesday+weightAfter.wednesday+weightAfter.thursday+weightAfter.friday+weightAfter.saturday+weightAfter.sunday}"  maxFractionDigits="1"  /></td>
		     </tr>
		</tbody>
	</table>
	<br>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:6%;">序号</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">日期</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期</th>
				<th style="text-align: center;vertical-align: middle;width:18%;">总计销售额</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">权重值</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">日目标</th>
				<th style="text-align: center;vertical-align: middle;width:18%;">月销售预测值</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">预测完成率</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="enterprise" varStatus="i">
			  <c:if test="${enterprise.week!='avg' }">
				<tr>
					<td style="text-align: center;vertical-align: middle;">${i.index+1}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.week}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.monday}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.tuesday}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.wednesday}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.thursday}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.friday}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.saturday}</td>
				</tr>
			  </c:if>
			</c:forEach>	
		</tbody>
	</table>
</body>
</html>
