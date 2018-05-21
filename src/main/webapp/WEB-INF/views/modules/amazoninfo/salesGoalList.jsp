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
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							     ],
			 	"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[0, "asc" ]]
			});
			var cnt="<form  id='inputForm' action='${ctx}/amazoninfo/enterpriseGoal' method='post' >"+
			"&nbsp;&nbsp;&nbsp;&nbsp;时间: "+
			"	<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyyMM'});  readonly='readonly' class='Wdate' type='text' id='addDate' name='startMonth' value='<fmt:formatDate value="${enterpriseGoal.startMonth}" pattern="yyyyMM" />'  class='input-small'/> "+
			"	&nbsp;-&nbsp;<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyyMM'});  readonly='readonly' class='Wdate' type='text'  name='endMonth' value='<fmt:formatDate value="${enterpriseGoal.endMonth}" pattern="yyyyMM" />'  class='input-small'/> "+
			"	<input class='btn btn-primary' type='submit' value='查询'/>&nbsp;&nbsp;&nbsp;<input id='export' class='btn btn-primary' type='button' value='导出'/> "+
			
			//"	<input id='add' class='btn btn-primary' onclick=addGoal() type='button' value='新增'/> "+
			" <input type='hidden' name='country'  value='${enterpriseGoal.country}'/>"+
			"</form> ";
			 $(".row:first").append(cnt);
			 
			 
			 $(".editGoal").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(":hidden").val();
						param.goal = newValue.split(',').join('');
						param.country=$("input[name='country']").val();
						param.month=$this.attr("keyVal");
						param.lineId=$this.attr("keyName");
						$.get("${ctx}/amazoninfo/enterpriseGoal/updateGoal?"+$.param(param),function(data){
							if(data==null || data==""){
								$this.text(oldVal);
							}else{
								$.jBox.tip(data, 'info',{timeout:2000});
							}
							setTimeout(function(){$("#inputForm").submit();}, 2000); 
						});
						return true;
			 }});
			 
			 $(".countryHref").click(function(){
					$("input[name='country']").val($(this).attr("key"));
					$("#inputForm").submit();
			 });
			 
			 $("#export").click(function(){
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseGoal/exportLineData");
				 $("#inputForm").submit();
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseGoal");
			 });
				
		});
		
	    function addGoal(){
	    	$.ajax({
   			   type: "POST",
   			   url: "${ctx}/amazoninfo/enterpriseGoal/isExist?month="+$("#addDate").val(),
   			   async: true,
   			   success: function(msg){
   				  if(msg=="1"){
   					$.jBox.tip('此条件下已确定目标销售额'); 
 					return;
   				  }else if(msg=="0"){
   					 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseGoal/addMonthGoal");
   					 $("#inputForm").submit();
   					 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseGoal");
   				  }
   			   }
       		});
	    }
		
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
		<li><a class="typeHref" href="#" onclick="changeType(1)">国家月目标</a></li>
		<li class="active"><a class="typeHref" href="#" onclick="changeType(2)">产品线月目标</a></li>
		<%--<li><a class="typeHref" href="#" onclick="changeType(3)">产品类型月目标</a></li> --%>
	</ul>
	<!-- <div style="display: none" id="searchContent"> 
	<form  id="inputForm" action="${ctx}/amazoninfo/enterpriseGoal" method="post" >
		&nbsp;&nbsp;&nbsp;&nbsp;时间:
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM'});"  readonly="readonly" class="Wdate" type="text"  id="addDate" name="startMonth" value="<fmt:formatDate value="${enterpriseGoal.startMonth}" pattern="yyyyMM" />"  class="input-small"/>
		<input id="add" class="btn btn-primary" onclick="addGoal();" type="button" value="新增"/>
	</form>-->
	<!-- </div>  -->
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/enterpriseGoal/findMonthLineGoal">总计</a></li>
		<li><a href="${ctx}/amazoninfo/enterpriseGoal/findMonthLineGoal?country=notUs">总计(不含美国)</a></li>
		<li><a href="${ctx}/amazoninfo/enterpriseGoal/findMonthLineGoal?country=en">英语国家</a></li>
		<li><a href="${ctx}/amazoninfo/enterpriseGoal/findMonthLineGoal?country=notEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<%--<c:if test="${dic.value ne 'com.unitek'&& dic.value ne 'com' &&dic.value ne 'uk'&& dic.value ne 'ca'}"> --%>
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${enterpriseGoal.country eq dic.value ?'active':''}"><a  href="${ctx}/amazoninfo/enterpriseGoal?country=${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <!--  <th style="text-align: center;vertical-align: middle;width:2%;">序号</th> -->
				<th style="text-align: center;vertical-align: middle;width:6%;">日期</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">总目标(€)</th>
				<th style="text-align: center;vertical-align: middle;width:10%; ">总销售额(€)</th>
				<c:forEach items="${allLine}" var="dic">
				   <th style="text-align: center;vertical-align: middle;width:12%;">${dic.value} 产品线(€)</th>
				   <th style="text-align: center;vertical-align: middle;width:12%; ">销售额(€)</th>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			 <c:forEach items="${data}" var="enterprise" varStatus="i"> 
				<tr>
					<%-- <td style="text-align: center;vertical-align: middle;">${i.index+1}</td> --%>
					<td style="text-align: center;vertical-align: middle;">${enterprise.key }</td>
					<c:set var="totalGoal" value="0"/>
					<c:set var="totalSale" value="0"/>
					<c:forEach items="${allLine}" var="dic">
					      <c:set var="totalGoal" value="${totalGoal+map[enterprise.key][dic.key][4]}" /> 
					      <c:set var="totalSale" value="${totalSale+saleData[enterpriseGoal.country][dic.value][enterprise.key].sales}" /> 
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
					   <c:if test="${totalGoal==0 }">
					      <fmt:formatNumber pattern="#,##0" value="${map[enterprise.key]['total'][4] }"  maxFractionDigits="0" />
					   </c:if>
					   <c:if test="${totalGoal>0 }">
					      <fmt:formatNumber pattern="#,##0" value="${totalGoal}"  maxFractionDigits="0" />
					   </c:if>
					</td>
					<td style="text-align: center;vertical-align: middle; ">
					   <c:if test="${totalSale>0 }">
					      <fmt:formatNumber pattern="#,##0" value="${totalSale}"  maxFractionDigits="0" />
					   </c:if>
					</td>
					<c:forEach items="${allLine}" var="dic">
					      <%--<c:set var="totalGoal" value="${totalGoal+map[enterprise.key][dic.key][4]}" />  --%>
						 <td style="text-align: center;vertical-align: middle;">
						 <%--产品线目标不再需要修改,直接修改产品类型目标会自动修改产品线目标 --%>
						 	<input type="hidden" value="${map[enterprise.key][dic.key][0]}" /> 
				        	<c:if test="${'1' eq enterprise.value}">
				        	<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editGoal"  data-type="text" keyVal="${enterprise.key }" keyName="${dic.key }" data-pk="1" data-title="Enter goal_${dic.value }" >
						            <fmt:formatNumber pattern="#,##0" value="${map[enterprise.key][dic.key][4]}"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
					        <shiro:lacksPermission name="amazoninfo:goal:edit">
					        	<fmt:formatNumber pattern="#,##0" value="${map[enterprise.key][dic.key][4]}"  maxFractionDigits="0" />
					        </shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'0' eq enterprise.value}">
					            <fmt:formatNumber pattern="#,##0" value="${map[enterprise.key][dic.key][4]}"  maxFractionDigits="0" />
					     </c:if>
				      		<%--<fmt:formatNumber pattern="#,##0" value="${map[enterprise.key][dic.key][4] }"  maxFractionDigits="0" />--%>
						 </td>
					      <%--<c:set var="totalSale" value="${totalSale+saleData[enterpriseGoal.country][dic.value][enterprise.key].sales}" /> --%>
				   			<td style="text-align: center;vertical-align: middle;width:12%; ">
							<c:if test="${saleData[enterpriseGoal.country][dic.value][enterprise.key].sales>0 }">
				   				<fmt:formatNumber pattern="#,##0" value="${saleData[enterpriseGoal.country][dic.value][enterprise.key].sales }"  maxFractionDigits="0" />
				   			</c:if>
				   			</td>
				</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr id = "totalTr">
				<td style="font-size: 18px; font-weight: bold;">Total</td>
				<c:forEach items="${allLine}" var="dic">
				 	<td style="text-align: center;vertical-align: middle;"></td>
		   			<td style="text-align: center;vertical-align: middle;"></td>
				</c:forEach>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>
