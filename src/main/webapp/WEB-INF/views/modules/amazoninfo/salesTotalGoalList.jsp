<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>line</title>
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
		function fmoney(s, n){   
		   temp = s;	
		   if(s<0){
			  temp= -s;
		   }
		   n1 = n;
		   n = n > 0 && n <= 20 ? n : 2;   
		   temp  = parseInt((temp + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
		   var l = temp.split(".")[0].split("").reverse(),   
		   r = temp.split(".")[1];   
		   t = "";   
		   for(i = 0; i < l.length; i ++ )   
		   {   
		      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
		   }   
		   temp =  t.split("").reverse().join("") + "." + r;   
		   if(s<0){
			   temp= "-"+temp;
		   }
		   if(n1==0){
			   temp = temp.replace(".00","")
		   }
		   return temp;
		} 
		
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().split(',').join('');
			} );
		};
		
		$(document).ready(function() {
			var arr = $("#contentTable tbody tr");
			var num1 = 0;
			var num2 = 0;
			var num3 = 0;
			var num4 = 0;
			var num5 = 0;
			var num6 = 0;
			var num7 = 0;
			var num8 = 0;
			var num9 = 0;
			var num10 = 0;
			var num11 = 0;
			var num12 = 0;
			var num13 = 0;
			arr.each(function() {
				if(parseInt($(this).find("td :eq(1)").text().split(',').join('')))
					num1 += parseInt($(this).find("td :eq(1)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(2)").text().split(',').join('')))
					num2 += parseInt($(this).find("td :eq(2)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(3)").text().split(',').join('')))
					num3 += parseInt($(this).find("td :eq(3)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(4)").text().split(',').join('')))
					num4 += parseInt($(this).find("td :eq(4)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(5)").text().split(',').join('')))
					num5 += parseInt($(this).find("td :eq(5)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(6)").text().split(',').join('')))
					num6 += parseInt($(this).find("td :eq(6)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(7)").text().split(',').join('')))
					num7 += parseInt($(this).find("td :eq(7)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(8)").text().split(',').join('')))
					num8 += parseInt($(this).find("td :eq(8)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(9)").text().split(',').join('')))
					num9 += parseInt($(this).find("td :eq(9)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(10)").text().split(',').join('')))
					num10 += parseInt($(this).find("td :eq(10)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(11)").text().split(',').join('')))
					num11 += parseInt($(this).find("td :eq(11)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(12)").text().split(',').join('')))
					num12 += parseInt($(this).find("td :eq(12)").text().split(',').join(''));
				if(parseInt($(this).find("td :eq(13)").text().split(',').join('')))
					num13 += parseInt($(this).find("td :eq(13)").text().split(',').join(''));
				
			});
			var tr = $("#contentTable tfoot tr#totalTr");
			tr.find("td :eq(1)").text(fmoney(num1.toFixed(0),0));
			tr.find("td :eq(2)").text(fmoney(num2.toFixed(0),0));
			tr.find("td :eq(3)").text(fmoney(num3.toFixed(0),0));
			tr.find("td :eq(4)").text(fmoney(num4,0));
			tr.find("td :eq(5)").text(fmoney(num5.toFixed(0),0));
			tr.find("td :eq(6)").text(fmoney(num6.toFixed(0),0));
			tr.find("td :eq(7)").text(fmoney(num7.toFixed(0),0));
			tr.find("td :eq(8)").text(fmoney(num8.toFixed(0),0));
			tr.find("td :eq(9)").text(fmoney(num9.toFixed(0),0));
			tr.find("td :eq(10)").text(fmoney(num10.toFixed(0),0));
			tr.find("td :eq(11)").text(fmoney(num11.toFixed(0),0));
			tr.find("td :eq(12)").text(fmoney(num12.toFixed(0),0));
			tr.find("td :eq(13)").text(fmoney(num13.toFixed(0),0));
			
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 15,
				"aLengthMenu":[[15, 30, 60,100,-1], [15, 30, 60, 100, "All"]],
			 	"bScrollCollapse": true,
				"aoColumns": [
						         null,
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     ],
			 	"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[0, "asc" ]]
			});
			var cnt="<form  id='inputForm' action='${ctx}/amazoninfo/enterpriseGoal/countryGoal' method='post' >"+
			"&nbsp;&nbsp;&nbsp;&nbsp;时间: "+
			"	<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyyMM'});  readonly='readonly' class='Wdate' type='text' id='addDate' name='startMonth' value='<fmt:formatDate value="${enterpriseGoal.startMonth}" pattern="yyyyMM" />'  class='input-small'/> "+
			"	&nbsp;-&nbsp;<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyyMM'});  readonly='readonly' class='Wdate' type='text'  name='endMonth' value='<fmt:formatDate value="${enterpriseGoal.endMonth}" pattern="yyyyMM" />'  class='input-small'/> "+
			"	<input class='btn btn-primary' type='submit' value='查询'/>&nbsp;&nbsp;<input id='export' class='btn btn-primary' type='button' value='导出'/> "+

			//"	<input id='add' class='btn btn-primary' onclick=addGoal() type='button' value='新增'/> "+
			"</form> ";
			$(".row:first").append(cnt);
			 
			 $(".editGoal").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.goal = newValue;
						param.month=$this.attr("keyVal");
						$.get("${ctx}/amazoninfo/enterpriseGoal/updateAllGoal?"+$.param(param),function(data){
							if(data==null || data==""){
								$this.text(oldVal);
							}else{
								$.jBox.tip(data, 'info',{timeout:1500});
							}
							setTimeout(function(){$("#inputForm").submit();}, 1500); 
							//$("#inputForm").submit();
						});
						return true;
			 }}); 
			 
			 $(".editCountryGoal").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(":hidden").val();
						param.goal = newValue.split(',').join('');
						param.month=$this.attr("keyVal");
						param.country=$this.attr("keyName");
						$.get("${ctx}/amazoninfo/enterpriseGoal/updateTotalGoal?"+$.param(param),function(data){
							if(data==null || data==""){
								$this.text(oldVal);
							}else{
								$.jBox.tip(data, 'info',{timeout:2000});
							}
							setTimeout(function(){$("#inputForm").submit();}, 2000); 
						});
						return true;
			 }});

			 $("#export").click(function(){
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseGoal/exportCountryData");
				 $("#inputForm").submit();
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseGoal/countryGoal");
			 });

			 $("#typeGoal").click(function(){
				 window.location.href = "${ctx}/amazoninfo/enterpriseTypeGoal";
			 });
				
		});
		
		function changeType(type){
			if(type == 1){
				window.location.href = "${ctx}/amazoninfo/enterpriseGoal/countryGoal";
			} else if (type == 2) {
				window.location.href = "${ctx}/amazoninfo/enterpriseGoal/findMonthLineGoal";
			} else {
				window.location.href = "${ctx}/amazoninfo/enterpriseTypeGoal";
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a class="typeHref" href="#" onclick="changeType(1)">国家月目标</a></li>
		<li><a class="typeHref" href="#" onclick="changeType(2)">产品线月目标</a></li>
		<%--<li><a class="typeHref" href="#" onclick="changeType(3)">产品类型月目标</a></li> --%>
	</ul>
	<div style="overflow:auto">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:6%;">日期</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">原定总目标(€)</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">实际目标/销售额(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">英语国家(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">非英语国家(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">德国|DE(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">美国|US(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">英国|UK(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">法国|FR(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">意大利|IT(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">西班牙|ES(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">加拿大|CA(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">日本|JP(€)</th>
		        <th style="text-align: center;vertical-align: middle;width:10%;">墨西哥|MX(€)</th>
			</tr>
		</thead>
		<tbody>
			 <c:forEach items="${data}" var="enterprise" varStatus="i"> 
				<tr>
					<td style="text-align: center;vertical-align: middle;">${enterprise.key }</td>
					  <td style="text-align: center;vertical-align: middle;">
					    <fmt:formatNumber pattern="#,#00" value="${rs[enterprise.key]}"  maxFractionDigits="0" />
					  </td>
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['total']?'#08c':''}">
					    <c:if test="${not empty sales[enterprise.key]['total'] }">
					    	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['total'] }"  maxFractionDigits="0" />
					    </c:if>
					    <c:if test="${empty sales[enterprise.key]['total'] }">
					    	<fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['total'].goal}"  maxFractionDigits="0" />
					    </c:if>
					  </td>
					  <!-- 英语国家 -->
		            <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['com']?'#08c':''}">
					      <c:set var='enTotal' value="0"/>
					      <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
						   <c:if test="${dic.value eq 'uk'||dic.value eq 'com'||dic.value eq 'ca'}">
								  <c:if test="${not empty sales[enterprise.key][dic.value] }">
								    <c:set var='enTotal' value="${enTotal+sales[enterprise.key][dic.value] }"/>
								  </c:if>
								  <c:if test="${empty sales[enterprise.key][dic.value] }">
								    <c:set var='enTotal' value="${enTotal+map[enterprise.key][dic.value].goal }"/>
								  </c:if>
						   </c:if>
		                </c:forEach>
		                <c:if test="${enTotal>0}">
		                  <fmt:formatNumber pattern="#,#00" value="${enTotal}"  maxFractionDigits="0" />
		                </c:if>
					  </td>
					  <!-- 非英语国家 -->
		              <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['de']?'#08c':''}">
					      <c:set var='notEnTotal' value="0"/>
					      <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
						   <c:if test="${dic.value ne 'uk' && dic.value ne 'com' && dic.value ne 'ca'}">
								  <c:if test="${not empty sales[enterprise.key][dic.value] }">
								    <c:set var='notEnTotal' value="${notEnTotal+sales[enterprise.key][dic.value] }"/>
								  </c:if>
								  <c:if test="${empty sales[enterprise.key][dic.value] }">
								    <c:set var='notEnTotal' value="${notEnTotal+map[enterprise.key][dic.value].goal }"/>
								  </c:if>
						   </c:if>
		                </c:forEach>
		                <c:if test="${notEnTotal>0}">
		                  <fmt:formatNumber pattern="#,#00" value="${notEnTotal}"  maxFractionDigits="0" />
		                </c:if>
					  </td>
					  <!-- de-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['de']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['de'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['de'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['de'] }">
						   <input type="hidden" value="${map[enterprise.key]['de'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="de" data-pk="1" data-title="Enter goal_de" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['de'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['de'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- com-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['com']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['com'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['com'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['com'] }">
						   <input type="hidden" value="${map[enterprise.key]['com'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="com" data-pk="1" data-title="Enter goal_com" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['com'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['com'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- uk-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['uk']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['uk'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['uk'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['uk'] }">
						   <input type="hidden" value="${map[enterprise.key]['uk'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="uk" data-pk="1" data-title="Enter goal_uk" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['uk'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['uk'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- fr-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['fr']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['fr'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['fr'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['fr'] }">
						   <input type="hidden" value="${map[enterprise.key]['fr'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="fr" data-pk="1" data-title="Enter goal_fr" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['fr'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['fr'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- it-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['it']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['it'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['it'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['it'] }">
						   <input type="hidden" value="${map[enterprise.key]['it'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="it" data-pk="1" data-title="Enter goal_it" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['it'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['it'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- es-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['es']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['es'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['es'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['es'] }">
						   <input type="hidden" value="${map[enterprise.key]['es'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="es" data-pk="1" data-title="Enter goal_es" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['es'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['es'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- ca-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['ca']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['ca'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['ca'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['ca'] }">
						   <input type="hidden" value="${map[enterprise.key]['ca'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="ca" data-pk="1" data-title="Enter goal_ca" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['ca'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['ca'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- jp-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['jp']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['jp'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['jp'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['jp'] }">
						   <input type="hidden" value="${map[enterprise.key]['jp'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="jp" data-pk="1" data-title="Enter goal_jp" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['jp'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['jp'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
					  <!-- mx-->
					  <td style="text-align: center;vertical-align: middle;color:${not empty sales[enterprise.key]['mx']?'#08c':''}">
						  <c:if test="${not empty sales[enterprise.key]['mx'] }">
						  	<fmt:formatNumber pattern="#,#00" value="${sales[enterprise.key]['mx'] }"  maxFractionDigits="0" />
						  </c:if>
						  <c:if test="${empty sales[enterprise.key]['mx'] }">
						   <input type="hidden" value="${map[enterprise.key]['mx'].id}" /> 
						  <c:if test="${'1' eq enterprise.value}">
						        <a href="#" class="editCountryGoal"  data-type="text" keyVal="${enterprise.key }" keyName="mx" data-pk="1" data-title="Enter goal_mx" >
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['mx'].goal}"  maxFractionDigits="0" />
						        </a>
						     </c:if>
						     <c:if test="${'0' eq enterprise.value}">
						            <fmt:formatNumber pattern="#,#00" value="${map[enterprise.key]['mx'].goal}"  maxFractionDigits="0" />
						     </c:if>
						     </c:if>
					  </td>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr id = "totalTr">
				<td style="font-size: 18px; font-weight: bold;">Total</td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
			</tr>
			<tr>
				<td style="font-size: 12px; font-weight: bold; color:red">至今缺口</td>
				<td style="text-align: center;vertical-align: middle; color:red"></td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['total'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
				   <c:set var='enTotalGap' value="0"/>
				   <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
					  <c:if test="${dic.value eq 'uk'||dic.value eq 'com'||dic.value eq 'ca'}">
					 	  <c:set var='enTotalGap' value="${enTotalGap+change[dic.value] }"/>
					 </c:if>
				  </c:forEach>
				  <fmt:formatNumber pattern="#,##0" value="${enTotalGap}"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
				   <c:set var='notEnTotalGap' value="0"/>
				   <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
					  <c:if test="${dic.value ne 'uk'&&dic.value ne 'com'&&dic.value ne 'ca'}">
					 	  <c:set var='notEnTotalGap' value="${notEnTotalGap+change[dic.value] }"/>
					 </c:if>
				  </c:forEach>
				  <fmt:formatNumber pattern="#,##0" value="${notEnTotalGap}"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['de'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['com'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['uk'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['fr'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['it'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['es'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['ca'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['jp'] }"  maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle; color:red">
					<fmt:formatNumber pattern="#,##0" value="${change['mx'] }"  maxFractionDigits="0" />
				</td>
			</tr>
		</tfoot>
	</table>
	</div>
</body>
</html>
