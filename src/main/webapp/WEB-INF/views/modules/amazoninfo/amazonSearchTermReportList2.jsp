<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Search Term Report</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<meta name="decorator" content="default"/>
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
		$(document).ready(function() {
			
			
			
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			 $("a[rel='popover']").popover({trigger:'hover',container:"form"});
			 
			 $("a[rel='popover']").live("mouseover",function(){
				var p = $(this).position();
				$('.fade').css("max-width","700px").css("top",p.top+150).css("left",p.left+88);
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
						sort = (sort[1]&&sort[1].toUpperCase()=="ASC"?"DESC":"ASC");
						$("#orderBy").val(order+" ASC"!=order+" "+sort?"":order+" "+sort);
					}else{
						$("#orderBy").val(order+" DESC");
					}
					page();
				});
				

				$("#exportNegative").click(function(){
					$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/negativeExport?platform=${amazonSearchTermReport.country}");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/");
				});
			
		/* 	 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			 	"aaSorting": [[11, "desc" ]]
			});
			 new FixedColumns( oTable,{
			 		"iLeftColumns":7,
					"iLeftWidth":900
			 	} );
			 $(".row:first").append($("#searchContent").html());
			 
			
 */
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		function btnExport(){
			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
				if(v=="ok"){
					$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportSearchTermList");
					$("#searchForm").submit();
					$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/searchTermList");
				}
			},{buttonsFocus:1});
			top.$('.jbox-body .jbox-icon').css('top','55px');
		}
		function timeOnChange(){
			$("#searchForm").submit();
		}
		function aa(){ 
			var a=document.getElementById("t_r_content").scrollTop; 
			var b=document.getElementById("t_r_content").scrollLeft; 
			document.getElementById("cl_freeze").scrollTop=a; 
			document.getElementById("t_r_t").scrollLeft=b; 
			} 
		

		function priceExport1(){
			var country = $("input[name='country']").val();
			var updateTime = $("input[name='updateTime']").val();
			var keyword = $("input[name='keyword']").val();
			
			window.location.href = "${ctx}/amazoninfo/advertising/priceExport?country="+country+"&updateTime="+updateTime+"&keyword="+keyword;
            top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:3000});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonSearchTermReport.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>

	<form:form id="searchForm" modelAttribute="amazonSearchTermReport" action="${ctx}/amazoninfo/advertising/searchTermList" method="post" class="breadcrumb form-search" cssStyle="height:40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			&nbsp;&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateTime" value="<fmt:formatDate value="${amazonSearchTermReport.updateTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;&nbsp;&nbsp;<label>Keyword/Ad Group Name：</label><input name="keyword"  class="input-xlarge" value='${amazonSearchTermReport.keyword }'/>
			<input  name="country" type="hidden" value="${amazonSearchTermReport.country}" />
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		<input  onclick="btnExport()" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/> 
		
   </form:form>
 
 <div style="overflow-x:scroll;width:100%">
	<table id="contentTable"  class="table table-bordered table-condensed">
		<thead>
			<tr>
              <th>Campaign Name<br/>Ad Group Name</th>	
              <th>Customer Search Term</th>
              <th class="sort keyword">Keyword</th>
              <th>Match<br/>Type</th>
              <th class="sort impressions"><a title='Impressions'>IMPR</a></th>
              <th class="sort clicks">Clicks</th>
              <th class="sort ctr">CTR(%)</th>
              <th class="sort cpc">CPC</th>
              <th class="sort spend"><a title='spend'>spend</a></th>
              <th class="sort dayTotalSales"><a title='day_total_sales'>DTS</a></th>
              <th class="sort acos">ACoS</th>
              <th class="sort roas">RoAS</th>
              <th class="sort dayTotalOrders"><a title='day_total_orders'>DTO</a></th>
              <th class="sort dayTotalUnits"><a title='day_total_units'>DTU</a></th>
              <th class="sort dayConversionRate"><a title='day_conversion_rate'>DCR</a></th>
              <th class="sort dayAdvertisedSkuUnits"><a title='day_advertised_sku_units'>DASU</a></th>
              <th class="sort dayOtherSkuUnits"><a title='day_other_sku_units'>DOSU</a></th>
              <th class="sort dayAdvertisedSkuSales"><a title='day_advertised_sku_sales'>DASS</a></th>
              <th class="sort dayOtherSkuSales"><a title='day_other_sku_sales'>DOSS</a></th>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="report" varStatus="i">
			  <tr ${report.dayAdvertisedSkuUnits>0?'style=background-color:#bce8f1':''}>
				 <td>${report.campaignName }<br/>${report.adGroupName }</td>
				 <td>${report.customerSearchTerm }</td>
				 <td>${report.keyword }</td>
				 <td>${report.matchType }</td>
				 <td>${report.impressions }</td>
				 <td>${report.clicks }</td>
				  <td>${report.ctr }</td>
				   <td>${report.cpc }</td>
				 <td>${report.spend }</td>
				 <td>${report.dayTotalSales }</td>
				 <td>${report.acos }</td>
				  <td>${report.roas }</td>
				 <td>${report.dayTotalOrders }</td>
				 <td>${report.dayTotalUnits }</td>
				  <td>${report.dayConversionRate }</td>
				 <td>${report.dayAdvertisedSkuUnits }</td>
				 <td>${report.dayOtherSkuUnits }</td>
				 <td>${report.dayAdvertisedSkuSales }</td>
				 <td>${report.dayOtherSkuSales }</td>
			  </tr>
			</c:forEach>		
		</tbody>
	</table>
	</div>
	<div class="pagination">${page}</div>
</body>
</html>
