<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>productTypeGoal</title>
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
			var num14 = 0;
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
				if(parseInt($(this).find("td :eq(14)").text().split(',').join('')))
					num14 += parseInt($(this).find("td :eq(14)").text().split(',').join(''));
				
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
			tr.find("td :eq(14)").text(fmoney(num14.toFixed(0),0));
			
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 20,
				"aLengthMenu":[[20, 30, 60,100,-1], [20, 30, 60, 100, "All"]],
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
				     { "sSortDataType":"dom-html", "sType":"numeric" },
				     { "sSortDataType":"dom-html", "sType":"numeric" }
				     ],
			 	"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
				"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             if(iDisplayIndex==0){
		            	 addd1=0;
		            	 addd2=0;
		            	 addd3=0;
		            	 addd4=0;
		            	 addd5=0;
		            	 addd6=0;
		            	 addd7=0;
		            	 addd8=0;
		            	 addd9=0;
		            	 addd10=0;
		            	 addd11=0;
		            	 addd12=0;
		            	 addd13=0;
		            	 addd14=0;
		             }
		             if(parseFloat($('td:eq(1)', nRow).find("a:eq(0)").text().split(',').join('')))
		             	addd1 += parseFloat($('td:eq(1)', nRow).find("a:eq(0)").text().split(',').join(''));//第几列
		             if($('td:eq(2)', nRow).find("a:eq(0)").text().split(',').join(''))
		             	addd2 += parseFloat($('td:eq(2)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if(parseFloat($('td:eq(3)', nRow).find("a:eq(0)").text().split(',').join('')))
		             	addd3 += parseFloat($('td:eq(3)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if($('td:eq(4)', nRow).find("a:eq(0)").text().split(',').join(''))
		             	addd4 += parseFloat($('td:eq(4)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if(parseFloat($('td:eq(5)', nRow).find("a:eq(0)").text().split(',').join('')))
		             	addd5 += parseFloat($('td:eq(5)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if($('td:eq(6)', nRow).find("a:eq(0)").text().split(',').join(''))
		             	addd6 += parseFloat($('td:eq(6)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if(parseFloat($('td:eq(7)', nRow).find("a:eq(0)").text().split(',').join('')))
		             	addd7 += parseFloat($('td:eq(7)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if($('td:eq(8)', nRow).find("a:eq(0)").text().split(',').join(''))
			             addd8 += parseFloat($('td:eq(8)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if(parseFloat($('td:eq(9)', nRow).find("a:eq(0)").text().split(',').join('')))
			             addd9 += parseFloat($('td:eq(9)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if($('td:eq(10)', nRow).find("a:eq(0)").text().split(',').join(''))
			             addd10 += parseFloat($('td:eq(10)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if(parseFloat($('td:eq(11)', nRow).find("a:eq(0)").text().split(',').join('')))
			             addd11 += parseFloat($('td:eq(11)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if($('td:eq(12)', nRow).find("a:eq(0)").text().split(',').join(''))
			             addd12 += parseFloat($('td:eq(12)', nRow).find("a:eq(0)").text().split(',').join(''));
		             if(aData[13].split(',').join(''))
			             addd13 += parseFloat(aData[13].split(',').join(''));
		             if(aData[14].split(',').join(''))
			             addd14 += parseFloat(aData[14].split(',').join(''));
		            
		             $("#total1").html(fmoney(addd1,0));
		             $("#total2").html(fmoney(addd2,0));
		             $("#total3").html(fmoney(addd3,0));
		             $("#total4").html(fmoney(addd4,0));
		             $("#total5").html(fmoney(addd5,0));
		             $("#total6").html(fmoney(addd6,0));
		             $("#total7").html(fmoney(addd7,0));
		             $("#total8").html(fmoney(addd8,0));
		             $("#total9").html(fmoney(addd9,0));
		             $("#total10").html(fmoney(addd10,0));
		             $("#total11").html(fmoney(addd11,0));
		             $("#total12").html(fmoney(addd12,0));
		             $("#total13").html(fmoney(addd13,0));
		             $("#total14").html(fmoney(addd14,0));
		             return nRow;
		         },"fnPreDrawCallback": function( oSettings ) { 
		        	 $("#total1").html(0);
		             $("#total2").html(0);
		             $("#total3").html(0);
		             $("#total4").html(0);
		             $("#total5").html(0);
		             $("#total6").html(0);
		             $("#total7").html(0);
		             $("#total8").html(0);
		             $("#total9").html(0);
		             $("#total10").html(0);
		             $("#total11").html(0);
		             $("#total12").html(0);
		             $("#total13").html(0);
		             $("#total14").html(0);
		         },
			     "aaSorting": [[0, "asc" ]]
			});
			var cnt="<form  id='inputForm' action='${ctx}/amazoninfo/enterpriseTypeGoal' method='post' >"+
			"&nbsp;&nbsp;&nbsp;&nbsp;时间: "+
			"	<input style='width: 100px' onclick=\"WdatePicker({dateFmt:'yyyyMM',onpicked:function(){timeOnChange();return true}})\";  readonly='readonly' class='Wdate' type='text' id='goalMonth' name='goalMonth' value='<fmt:formatDate value="${enterpriseTypeGoal.goalMonth}" pattern="yyyyMM" />'  class='input-small'/> "+
			"	<input class='btn btn-primary' type='submit' value='查询'/>"+
			
			//"	<input id='add' class='btn btn-primary' onclick=addGoal() type='button' value='新增'/> "+
			"</form> ";
			 $(".row:first").append(cnt);
			 
			 $(".editSaleGoal").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(":hidden").val();
						param.salesGoal = newValue.split(',').join('');
						param.country=$this.attr("keyName");
						$.get("${ctx}/amazoninfo/enterpriseTypeGoal/updateGoal?"+$.param(param),function(data){
							if(data==null || data==""){
								$this.text(oldVal);
							}else{
								$.jBox.tip(data, 'info',{timeout:1000});
							}
							setTimeout(function(){$("#inputForm").submit();}, 1000); 
							//$("#inputForm").submit();
						});
						return true;
			 }}); 
			 
			 $(".editProfitGoal").editable({
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().find(":hidden").val();
						param.profitGoal = newValue.split(',').join('');
						param.country=$this.attr("keyName");
						$.get("${ctx}/amazoninfo/enterpriseTypeGoal/updateGoal?"+$.param(param),function(data){
							if(data==null || data==""){
								$this.text(oldVal);
							}else{
								$.jBox.tip(data, 'info',{timeout:1000});
							}
							setTimeout(function(){$("#inputForm").submit();}, 1000); 
						});
						return true;
			 }});

			 $("#export").click(function(){
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseTypeGoal/exportData");
				 $("#inputForm").submit();
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseTypeGoal");
			 });

			 $("#doGoal").click(function(){
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseTypeGoal/autoGoal");
				 $("#inputForm").submit();
			 });

			 $("#doLineGoal").click(function(){
				 $("#inputForm").attr("action","${ctx}/amazoninfo/enterpriseTypeGoal/autoProfitGoal");
				 $("#inputForm").submit();
			 });
				
		});

		function changeType(type){
			if(type == 1){
				window.location.href = "${ctx}/amazoninfo/enterpriseGoal/countryGoal";
			} else if (type == 2) {
				window.location.href = "${ctx}/amazoninfo/enterpriseGoal";
			} else {
				window.location.href = "${ctx}/amazoninfo/enterpriseTypeGoal";
			}
		}
		
		function timeOnChange(){
			$("#inputForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a class="typeHref" href="#" onclick="changeType(1)">国家月目标</a></li>
		<li><a class="typeHref" href="#" onclick="changeType(2)">产品线月目标</a></li>
		<li class="active"><a class="typeHref" href="#" onclick="changeType(3)">产品类型月目标</a></li>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;" rowspan="2">产品类型</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">英语国家|EN(€)</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">德国|DE(€)</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">法国|FR(€)</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">意大利|IT(€)</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">西班牙|ES(€)</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">日本|JP(€)</th>
		        <th style="text-align: center;vertical-align: middle;" colspan="2">总计|Total(€)</th>
			</tr>
			<tr>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
		        <th style="text-align: center;vertical-align: middle;">销售额</th>
		        <th style="text-align: center;vertical-align: middle;">利润</th>
			</tr>
		</thead>
		<tbody>
			 <c:forEach items="${data}" var="enterprise" varStatus="i">
			 	<c:set var="salesTotal" value="0"/>
			 	<c:set var="profitsTotal" value="0"/>
				<tr>
					<td style="vertical-align: middle;">${enterprise.key } (${typeLine[fn:toLowerCase(enterprise.key)]}产品线)</td>
					<!-- 英语国家 -->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
		            	<c:set var="salesTotal" value="${salesTotal + data[enterprise.key]['en'].salesGoal }"/>
		            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['en'].id}">
						 	<input type="hidden" value="${data[enterprise.key]['en'].id}" />
						 	<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editSaleGoal"  data-type="text" keyVal="${enterprise.key }" keyName="en" data-pk="1" data-title="Enter goal_en" >
						            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['en'].salesGoal }"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
					        <shiro:lacksPermission name="amazoninfo:goal:edit">
						     	<a style="text-decoration:none;">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['en'].salesGoal }"  maxFractionDigits="0" />
						    	</a>
					        </shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'1' ne isEdit}">
					     	<a style="text-decoration:none;">
					    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['en'].salesGoal }"  maxFractionDigits="0" />
					    	</a>
					    </c:if>
					</td>
		            <td style="text-align: center;vertical-align: middle;">
		            	<c:if test="${data[enterprise.key]['en'].profitGoal > 0 }">
		            		<c:set var="profitsTotal" value="${profitsTotal + data[enterprise.key]['en'].profitGoal }"/>
			            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['en'].id}">
							 	<input type="hidden" value="${data[enterprise.key]['en'].id}" />
						 		<shiro:hasPermission name="amazoninfo:goal:edit">
							        <a href="#" class="editProfitGoal"  data-type="text" keyVal="${enterprise.key }" keyName="en" data-pk="1" data-title="Enter goal_en" >
							            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['en'].profitGoal }"  maxFractionDigits="0" />
							        </a>
						        </shiro:hasPermission>
						        <shiro:lacksPermission name="amazoninfo:goal:edit">
							     	<a style="text-decoration:none;color:black">
							    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['en'].profitGoal }"  maxFractionDigits="0" />
							    	</a>
						        </shiro:lacksPermission>
						     </c:if>
						     <c:if test="${'1' ne isEdit}">
						     	<a style="text-decoration:none;color:black">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['en'].profitGoal }"  maxFractionDigits="0" />
						    	</a>
						    </c:if>
						</c:if>
					</td>
					<!-- de-->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
		            	<c:set var="salesTotal" value="${salesTotal + data[enterprise.key]['de'].salesGoal }"/>
		            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['de'].id}">
						 	<input type="hidden" value="${data[enterprise.key]['de'].id}" />
						 	<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editSaleGoal"  data-type="text" keyVal="${enterprise.key }" keyName="de" data-pk="1" data-title="Enter goal_de" >
						            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['de'].salesGoal }"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
						    <shiro:lacksPermission name="amazoninfo:goal:edit">
						     	<a style="text-decoration:none;">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['de'].salesGoal }"  maxFractionDigits="0" />
						    	</a>
						    </shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'1' ne isEdit}">
					     	<a style="text-decoration:none;">
					    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['de'].salesGoal }"  maxFractionDigits="0" />
					    	</a>
					    </c:if>
					</td>
		            <td style="text-align: center;vertical-align: middle;">
		            	<c:if test="${data[enterprise.key]['de'].profitGoal > 0 }">
		            		<c:set var="profitsTotal" value="${profitsTotal + data[enterprise.key]['de'].profitGoal }"/>
			            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['de'].id}">
							 	<input type="hidden" value="${data[enterprise.key]['de'].id}" />
						 		<shiro:hasPermission name="amazoninfo:goal:edit">
							        <a href="#" class="editProfitGoal"  data-type="text" keyVal="${enterprise.key }" keyName="de" data-pk="1" data-title="Enter goal_en" >
							            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['de'].profitGoal }"  maxFractionDigits="0" />
							        </a>
						        </shiro:hasPermission>
						        <shiro:lacksPermission name="amazoninfo:goal:edit">
							     	<a style="text-decoration:none;color:black">
							    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['de'].profitGoal }"  maxFractionDigits="0" />
							    	</a>
						        </shiro:lacksPermission>
						     </c:if>
						     <c:if test="${'1' ne isEdit}">
						     	<a style="text-decoration:none;color:black">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['de'].profitGoal }"  maxFractionDigits="0" />
						    	</a>
						    </c:if>
						</c:if>
					</td>
					<!-- fr-->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
		            	<c:set var="salesTotal" value="${salesTotal + data[enterprise.key]['fr'].salesGoal }"/>
		            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['fr'].id}">
						 	<input type="hidden" value="${data[enterprise.key]['fr'].id}" />
						 	<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editSaleGoal"  data-type="text" keyVal="${enterprise.key }" keyName="fr" data-pk="1" data-title="Enter goal_fr" >
						            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['fr'].salesGoal }"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
					        <shiro:lacksPermission name="amazoninfo:goal:edit">
						     	<a style="text-decoration:none;">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['fr'].salesGoal }"  maxFractionDigits="0" />
						    	</a>
					        </shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'1' ne isEdit}">
					     	<a style="text-decoration:none;">
					    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['fr'].salesGoal }"  maxFractionDigits="0" />
					    	</a>
					    </c:if>
					</td>
		            <td style="text-align: center;vertical-align: middle;">
		            	<c:if test="${data[enterprise.key]['fr'].profitGoal > 0 }">
		            		<c:set var="profitsTotal" value="${profitsTotal + data[enterprise.key]['fr'].profitGoal }"/>
			            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['fr'].id}">
							 	<input type="hidden" value="${data[enterprise.key]['fr'].id}" />
						 		<shiro:hasPermission name="amazoninfo:goal:edit">
							        <a href="#" class="editProfitGoal"  data-type="text" keyVal="${enterprise.key }" keyName="fr" data-pk="1" data-title="Enter goal_fr" >
							            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['fr'].profitGoal }"  maxFractionDigits="0" />
							        </a>
						        </shiro:hasPermission>
						        <shiro:lacksPermission name="amazoninfo:goal:edit">
						        	<a style="text-decoration:none;color:black">
							    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['fr'].profitGoal }"  maxFractionDigits="0" />
							    	</a>
						        </shiro:lacksPermission>
						     </c:if>
						     <c:if test="${'1' ne isEdit}">
						     	<a style="text-decoration:none;color:black">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['fr'].profitGoal }"  maxFractionDigits="0" />
						    	</a>
						    </c:if>
						</c:if>
					</td>
					<!-- it-->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
		            	<c:set var="salesTotal" value="${salesTotal + data[enterprise.key]['it'].salesGoal }"/>
		            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['it'].id}">
						 	<input type="hidden" value="${data[enterprise.key]['it'].id}" />
						 	<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editSaleGoal"  data-type="text" keyVal="${enterprise.key }" keyName="it" data-pk="1" data-title="Enter goal_it" >
						            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['it'].salesGoal }"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
					        <shiro:lacksPermission name="amazoninfo:goal:edit">
						     	<a style="text-decoration:none;">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['it'].salesGoal }"  maxFractionDigits="0" />
						    	</a>
					        </shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'1' ne isEdit}">
					     	<a style="text-decoration:none;">
					    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['it'].salesGoal }"  maxFractionDigits="0" />
					    	</a>
					    </c:if>
					</td>
		            <td style="text-align: center;vertical-align: middle;">
		            	<c:if test="${data[enterprise.key]['it'].profitGoal > 0 }">
		            		<c:set var="profitsTotal" value="${profitsTotal + data[enterprise.key]['it'].profitGoal }"/>
			            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['it'].id}">
							 	<input type="hidden" value="${data[enterprise.key]['it'].id}" />
						 		<shiro:hasPermission name="amazoninfo:goal:edit">
							        <a href="#" class="editProfitGoal"  data-type="text" keyVal="${enterprise.key }" keyName="it" data-pk="1" data-title="Enter goal_it" >
							            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['it'].profitGoal }"  maxFractionDigits="0" />
							        </a>
						        </shiro:hasPermission>
					        	<shiro:lacksPermission name="amazoninfo:goal:edit">
							     	<a style="text-decoration:none;color:black">
							    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['it'].profitGoal }"  maxFractionDigits="0" />
							    	</a>
					        	</shiro:lacksPermission>
						     </c:if>
						     <c:if test="${'1' ne isEdit}">
						     	<a style="text-decoration:none;color:black">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['it'].profitGoal }"  maxFractionDigits="0" />
						    	</a>
						    </c:if>
						</c:if>
					</td>
					<!-- es-->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
		            	<c:set var="salesTotal" value="${salesTotal + data[enterprise.key]['es'].salesGoal }"/>
		            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['es'].id}">
						 	<input type="hidden" value="${data[enterprise.key]['es'].id}" />
						 	<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editSaleGoal"  data-type="text" keyVal="${enterprise.key }" keyName="es" data-pk="1" data-title="Enter goal_es" >
						            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['es'].salesGoal }"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
				        	<shiro:lacksPermission name="amazoninfo:goal:edit">
						     	<a style="text-decoration:none;">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['es'].salesGoal }"  maxFractionDigits="0" />
						    	</a>
				        	</shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'1' ne isEdit}">
					     	<a style="text-decoration:none;">
					    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['es'].salesGoal }"  maxFractionDigits="0" />
					    	</a>
					    </c:if>
					</td>
		            <td style="text-align: center;vertical-align: middle;">
		            	<c:if test="${data[enterprise.key]['es'].profitGoal > 0 }">
		            		<c:set var="profitsTotal" value="${profitsTotal + data[enterprise.key]['es'].profitGoal }"/>
			            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['es'].id}">
							 	<input type="hidden" value="${data[enterprise.key]['es'].id}" />
							 	<shiro:hasPermission name="amazoninfo:goal:edit">
							        <a href="#" class="editProfitGoal"  data-type="text" keyVal="${enterprise.key }" keyName="es" data-pk="1" data-title="Enter goal_es" >
							            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['es'].profitGoal }"  maxFractionDigits="0" />
							        </a>
						        </shiro:hasPermission>
					        	<shiro:lacksPermission name="amazoninfo:goal:edit">
							     	<a style="text-decoration:none;color:black">
							    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['es'].profitGoal }"  maxFractionDigits="0" />
							    	</a>
					        	</shiro:lacksPermission>
						     </c:if>
						     <c:if test="${'1' ne isEdit}">
						     	<a style="text-decoration:none;color:black">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['es'].profitGoal }"  maxFractionDigits="0" />
						    	</a>
						    </c:if>
						</c:if>
					</td>
					<!-- jp-->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
		            	<c:set var="salesTotal" value="${salesTotal + data[enterprise.key]['jp'].salesGoal }"/>
		            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['jp'].id}">
						 	<input type="hidden" value="${data[enterprise.key]['jp'].id}" />
							<shiro:hasPermission name="amazoninfo:goal:edit">
						        <a href="#" class="editSaleGoal"  data-type="text" keyVal="${enterprise.key }" keyName="jp" data-pk="1" data-title="Enter goal_jp" >
						            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['jp'].salesGoal }"  maxFractionDigits="0" />
						        </a>
					        </shiro:hasPermission>
				        	<shiro:lacksPermission name="amazoninfo:goal:edit">
						     	<a style="text-decoration:none;">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['jp'].salesGoal }"  maxFractionDigits="0" />
						    	</a>
				        	</shiro:lacksPermission>
					     </c:if>
					     <c:if test="${'1' ne isEdit}">
					     	<a style="text-decoration:none;">
					    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['jp'].salesGoal }"  maxFractionDigits="0" />
					    	</a>
					    </c:if>
					</td>
		            <td style="text-align: center;vertical-align: middle;">
		            	<c:if test="${data[enterprise.key]['jp'].profitGoal > 0 }">
		            		<c:set var="profitsTotal" value="${profitsTotal + data[enterprise.key]['jp'].profitGoal }"/>
			            	<c:if test="${'1' eq isEdit && not empty data[enterprise.key]['jp'].id}">
							 	<input type="hidden" value="${data[enterprise.key]['jp'].id}" />
								<shiro:hasPermission name="amazoninfo:goal:edit">
							        <a href="#" class="editProfitGoal"  data-type="text" keyVal="${enterprise.key }" keyName="jp" data-pk="1" data-title="Enter goal_jp" >
							            <fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['jp'].profitGoal }"  maxFractionDigits="0" />
							        </a>
						        </shiro:hasPermission>
					        	<shiro:lacksPermission name="amazoninfo:goal:edit">
							     	<a style="text-decoration:none;color:black">
							    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['jp'].profitGoal }"  maxFractionDigits="0" />
							    	</a>
					        	</shiro:lacksPermission>
						     </c:if>
						     <c:if test="${'1' ne isEdit}">
						     	<a style="text-decoration:none;color:black">
						    		<fmt:formatNumber pattern="#,##0" value="${data[enterprise.key]['jp'].profitGoal }"  maxFractionDigits="0" />
						    	</a>
						    </c:if>
						</c:if>
					</td>
					<!-- total-->
		            <td style="text-align: center;vertical-align: middle;color:#08c">
					    <fmt:formatNumber pattern="#,##0" value="${salesTotal }"  maxFractionDigits="0" />
					</td>
		            <td style="text-align: center;vertical-align: middle;">
					    <fmt:formatNumber pattern="#,##0" value="${profitsTotal }"  maxFractionDigits="0" />
					</td>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td style="font-size: 12px; font-weight: bold;">Page Total</td>
				<td id="total1" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total2" style="text-align: center;vertical-align: middle;"></td>
				<td id="total3" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total4" style="text-align: center;vertical-align: middle;"></td>
				<td id="total5" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total6" style="text-align: center;vertical-align: middle;"></td>
				<td id="total7" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total8" style="text-align: center;vertical-align: middle;"></td>
				<td id="total9" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total10" style="text-align: center;vertical-align: middle;"></td>
				<td id="total11" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total12" style="text-align: center;vertical-align: middle;"></td>
				<td id="total13" style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td id="total14" style="text-align: center;vertical-align: middle;"></td>
			</tr>
			<tr id = "totalTr">
				<td style="font-size: 18px; font-weight: bold;">Total</td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;color:#08c"></td>
				<td style="text-align: center;vertical-align: middle;"></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>
