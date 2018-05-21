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
			 
			 $(".editWeight").editable({validate:function(data){
					if(!(data)){
						return "日销售权重指数不能为空!";
					}
				},display:false,success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.attr("keyVal");
					var type=$this.attr("keyName");
					param.country = $("#country").val();
					param.updValue = newValue;
					param.type=$this.attr("keyName");
					$.get("${ctx}/amazoninfo/enterpriseWeek/updateWeight?"+$.param(param),function(data){
						if(!(data)){    
							$this.text(oldVal);						
						}else{
							$this.text(newValue);	
							$this.attr("keyVal",newValue);	
							var total=0;
							if(type=="1"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.tuesday}")+parseFloat("${weightAfter.wednesday}")+parseFloat("${weightAfter.thursday}")+parseFloat("${weightAfter.friday}")+parseFloat("${weightAfter.saturday}")+parseFloat("${weightAfter.sunday}");
							}else if(type=="2"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.monday}")+parseFloat("${weightAfter.wednesday}")+parseFloat("${weightAfter.thursday}")+parseFloat("${weightAfter.friday}")+parseFloat("${weightAfter.saturday}")+parseFloat("${weightAfter.sunday}");
							}else if(type=="3"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.monday}")+parseFloat("${weightAfter.tuesday}")+parseFloat("${weightAfter.thursday}")+parseFloat("${weightAfter.friday}")+parseFloat("${weightAfter.saturday}")+parseFloat("${weightAfter.sunday}");
							}else if(type=="4"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.monday}")+parseFloat("${weightAfter.wednesday}")+parseFloat("${weightAfter.tuesday}")+parseFloat("${weightAfter.friday}")+parseFloat("${weightAfter.saturday}")+parseFloat("${weightAfter.sunday}");
							}else if(type=="5"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.monday}")+parseFloat("${weightAfter.wednesday}")+parseFloat("${weightAfter.thursday}")+parseFloat("${weightAfter.tuesday}")+parseFloat("${weightAfter.saturday}")+parseFloat("${weightAfter.sunday}");
							}else if(type=="6"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.monday}")+parseFloat("${weightAfter.wednesday}")+parseFloat("${weightAfter.thursday}")+parseFloat("${weightAfter.friday}")+parseFloat("${weightAfter.tuesday}")+parseFloat("${weightAfter.sunday}");
							}else if(type=="7"){
								total=parseFloat(newValue)+parseFloat("${weightAfter.monday}")+parseFloat("${weightAfter.wednesday}")+parseFloat("${weightAfter.thursday}")+parseFloat("${weightAfter.friday}")+parseFloat("${weightAfter.saturday}")+parseFloat("${weightAfter.tuesday}");
							}
							$("#totalWeight").text(toDecimal(total));
							$.jBox.tip("修改日权重指数成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}});
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*10)/10;  
	            return f;  
	     }  
		
		function create(){
			top.$.jBox.confirm("确定要重新生产年度销售数据？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/enterpriseWeek/create");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/enterpriseWeek");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
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
	
	<form id="searchForm" action="${ctx}/amazoninfo/enterpriseWeek" method="post" >
	<input  name="country" id="country" type="hidden" value="${enterpriseWeek.country}" />
	   <c:if test="${empty enterpriseWeek.country}">
		&nbsp;&nbsp;&nbsp;&nbsp;统计时间:
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" id="startDate" name="startDate" value="<fmt:formatDate value="${enterpriseWeek.startDate}" pattern="yyyy-MM-dd" />" class="input-small"/>&nbsp;-&nbsp;
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" id="endDate" name="endDate" value="<fmt:formatDate value="${enterpriseWeek.endDate}" pattern="yyyy-MM-dd" />" class="input-small"/>
			
            &nbsp;&nbsp;&nbsp;排除国家:
			<select name='removeCountry' style="width: 200px"  id="removeCountry">
			        <option value="">-无-</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
					   <c:if test="${dic.value ne 'com.unitek'}">
						  <option value="${dic.value}" ${removeCountry eq dic.value?'selected':''}>${dic.label}</option>
						</c:if>
					</c:forEach>
			</select> 
			<input id="btnSubmit" class="btn btn-primary" type="button" value="生成年度销售数据" onclick="create();"/>
		</c:if>
	</form>
	
	<c:if test="${not empty enterpriseWeek.country}">
	   <div class="alert alert-info">为确保分站日销售权重指数总和等于企业周权重指数，可手动调整分站日销售权重指数</div>
	</c:if>
	<table id="enterpriseWeight" class="table table-striped table-bordered table-condensed">
	     <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:12%;">单位：美元</th>
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
		        <td style="text-align: center;vertical-align: middle;">平均日销售</td>
		         <td style="text-align: center;vertical-align: middle;" >${avg.monday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avg.tuesday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avg.wednesday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avg.thursday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avg.friday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avg.saturday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avg.sunday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${avgTotal}</td>
			  
		     </tr>
		     <tr>
		       <td style="text-align: center;vertical-align: middle;">公式</td>
		       <td style="text-align: center;vertical-align: middle;" >${weightBefore.monday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${weightBefore.tuesday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${weightBefore.wednesday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${weightBefore.thursday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${weightBefore.friday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${weightBefore.saturday}</td>
			   <td style="text-align: center;vertical-align: middle;" >${weightBefore.sunday}</td>
			   <td style="text-align: center;vertical-align: middle;" ><fmt:formatNumber pattern="#######.##" value="${weightBefore.monday+weightBefore.tuesday+weightBefore.wednesday+weightBefore.thursday+weightBefore.friday+weightBefore.saturday+weightBefore.sunday}"  maxFractionDigits="2"  /></td>
		   
		     </tr>
		     <tr>
		        <c:if test="${empty enterpriseWeek.country}">
			       <td style="text-align: center;vertical-align: middle;">日销售权重指数</td>
			       <td style="text-align: center;vertical-align: middle;" >${weightAfter.monday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.tuesday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.wednesday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.thursday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.friday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.saturday}</td>
				   <td style="text-align: center;vertical-align: middle;" >${weightAfter.sunday}</td>
				   <td style="text-align: center;vertical-align: middle;" ><fmt:formatNumber pattern="#######.##" value="${weightAfter.monday+weightAfter.tuesday+weightAfter.wednesday+weightAfter.thursday+weightAfter.friday+weightAfter.saturday+weightAfter.sunday}"  maxFractionDigits="1"  /></td>
			    </c:if>
			     <c:if test="${not empty enterpriseWeek.country}">
			      <td style="text-align: center;vertical-align: middle;">日销售权重指数</td>
			       <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="1"  keyVal="${weightAfter.monday}" data-type="text" data-pk="1" data-title="Enter Monday-Weight" data-value="${weightAfter.monday}">${weightAfter.monday}</a>
				   </td>
				    <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="2"  keyVal="${weightAfter.tuesday}" data-type="text" data-pk="1" data-title="Enter tuesday-Weight" data-value="${weightAfter.tuesday}">${weightAfter.tuesday}</a>
				   </td>
				   <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="3"  keyVal="${weightAfter.wednesday}" data-type="text" data-pk="1" data-title="Enter wednesday-Weight" data-value="${weightAfter.wednesday}">${weightAfter.wednesday}</a>
				   </td>
				   <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="4"  keyVal="${weightAfter.thursday}" data-type="text" data-pk="1" data-title="Enter thursday-Weight" data-value="${weightAfter.thursday}">${weightAfter.thursday}</a>
				   </td>
				   <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="5"  keyVal="${weightAfter.friday}" data-type="text" data-pk="1" data-title="Enter friday-Weight" data-value="${weightAfter.friday}">${weightAfter.friday}</a>
				   </td>
				   <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="6"  keyVal="${weightAfter.saturday}" data-type="text" data-pk="1" data-title="Enter saturday-Weight" data-value="${weightAfter.saturday}">${weightAfter.saturday}</a>
				   </td>
				   <td style="text-align: center;vertical-align: middle;" >
			         <a href="#" class="editWeight" keyName="7"  keyVal="${weightAfter.sunday}" data-type="text" data-pk="1" data-title="Enter sunday-Weight" data-value="${weightAfter.sunday}">${weightAfter.sunday}</a>
				   </td>
				   <td style="text-align: center;vertical-align: middle;" id="totalWeight"><fmt:formatNumber pattern="#######.##" value="${weightAfter.monday+weightAfter.tuesday+weightAfter.wednesday+weightAfter.thursday+weightAfter.friday+weightAfter.saturday+weightAfter.sunday}"  maxFractionDigits="1"  /></td>
			     </c:if>  
		    	
		     </tr>
		</tbody>
	</table>
	<br>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:6%;">序号</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">周</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期一</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期二</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期三</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期四</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期五</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期六</th>
				<th style="text-align: center;vertical-align: middle;width:12%;">星期日</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="enterprise" varStatus="i">
			  <c:if test="${enterprise.week!='avg' }">
				<tr>
					<td style="text-align: center;vertical-align: middle;">${i.index+1}</td>
					<td style="text-align: center;vertical-align: middle;">${enterprise.week}</td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.monday!=0 }">${enterprise.monday}</c:if></td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.tuesday!=0 }">${enterprise.tuesday}</c:if></td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.wednesday!=0 }">${enterprise.wednesday}</c:if></td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.thursday!=0 }">${enterprise.thursday}</c:if></td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.friday!=0 }">${enterprise.friday}</c:if></td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.saturday!=0 }">${enterprise.saturday}</c:if></td>
					<td style="text-align: center;vertical-align: middle;"><c:if test="${enterprise.sunday!=0 }">${enterprise.sunday}</c:if></td>
				</tr>
			  </c:if>
			</c:forEach>	
		</tbody>
		<tfoot>
		   <tr>
			   <td style="text-align: center;vertical-align: middle;" colspan="2">平均日销售</td>
			   <td style="text-align: center;vertical-align: middle;" id="monday">${avg.monday}</td>
			   <td style="text-align: center;vertical-align: middle;" id="tuesday">${avg.tuesday}</td>
			   <td style="text-align: center;vertical-align: middle;" id="wednesday">${avg.wednesday}</td>
			   <td style="text-align: center;vertical-align: middle;" id="thursday">${avg.thursday}</td>
			   <td style="text-align: center;vertical-align: middle;" id="friday">${avg.friday}</td>
			   <td style="text-align: center;vertical-align: middle;" id="saturday">${avg.saturday}</td>
			   <td style="text-align: center;vertical-align: middle;" id="sunday">${avg.sunday}</td>
		   </tr>
		</tfoot>
	</table>
</body>
</html>
