<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>长期仓储、月仓储汇总</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		 }
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

		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ) {
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().replace("%","");
			} );
		};

		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn ) {
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
				 if(a.text() == null || a.text() == ''){
					return 0.00;
				}
				return parseFloat(a.text().split('-').join(''));
			});
		};
		
		$(document).ready(function() {
			$(".fbaTip").popover({html:true,trigger:'hover',content:function(){
				var td=$(this).parent();
				var country=td.find(".country").val();
				var name=td.find(".name").val();
				var start=td.find(".start").val();
				var end=td.find(".end").val();
				var type=td.find(".type").val();
				var $this = $(this);
				if(!$this.attr("content")){
					if(!$this.attr("data-content")){
						var content="";
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/amazoninfo/storageFee/findFeeByTime',
						    data: {'country':country,'productName':name,'start':start,'end':end,'type':type},
						    success:function(data){ 
						    	content = data;
						    	$this.attr("content",data);
					        }
						});
						return content;
					}
				}
				return $this.attr("content");
				
			}});
			
			$("#btnExport").click(function(){
				var country=$("input[name='countryE']").val();
				var start=$("input[name='startE']").val();
				var end=$("input[name='endE']").val();
				window.location.href = "${ctx}/amazoninfo/storageFee/export?country="+country+"&start="+start+"&end="+end;
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			// 表格排序
			//var orderBy = $("#orderBy").val().split(" ");
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

			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			
			$("#aboutMe").change(function(){
				$("#searchForm").submit();
			});
			
			$(".autoSubmit").change(function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			var tr = $("#contentTable tfoot tr#totalTr");
            var arr = $("#contentTable tbody tr");
            var totalMonthlyFeeAll = 0;
            var totalLongFeeAll = 0; 
            var totalFeeAll = 0;
            var i=0;
            
            arr.each(function() {
                   i+=1;
                   if($(this).find("td :eq(5)").text())
                   totalMonthlyFeeAll += parseFloat($(this).find("td :eq(5)").text());
                   if($(this).find("td :eq(6)").text())
                   totalLongFeeAll += parseFloat($(this).find("td :eq(6)").text());
                   if($(this).find("td :eq(7)").text())
                   totalFeeAll += parseFloat($(this).find("td :eq(7)").text());                    
               });
            $("#totalMonthlyFeeAll").html(totalMonthlyFeeAll.toFixed(2),2);
            $("#totalLongFeeAll").html(totalLongFeeAll.toFixed(2),2);
            $("#totalFeeAll").html(totalFeeAll.toFixed(2),2);
            
			 $("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
						         null,
						         null,
						         null,
						         null,
						         null,
							     { "sSortDataType":"dom-html1", "sType":"numeric" },	
							     { "sSortDataType":"dom-html1", "sType":"numeric" },	
							     null
							     ], 
				"ordering" : true,
				"aoColumnDefs": [{ "bSortable": true, "aTargets": [0,1,2,3,4,5,6,7] }],
				"aaSorting": [[ 7, "desc" ]],
				"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
                    if(iDisplayIndex==0){
                        addd=0;
                        addd1=0;
                        addd2=0;
                    }
                    if(!$.isNumeric(aData[5])){
                    	var start5 = (aData[5]).indexOf('>')+1;
                        var stop5 = (aData[5]).indexOf('/')-1; 
                        addd += parseFloat((aData[5]).substring(start5,stop5));
                    } else {
                    	addd+=parseFloat(aData[5]);
                    }
                    if(!$.isNumeric(aData[6])){
                        var start6 = (aData[6]).indexOf('>')+1;
                        var stop6 = (aData[6]).indexOf('/')-1; 
                        addd1 += parseFloat((aData[6]).substring(start6,stop6));
                    } else {
                        addd1+=parseFloat((aData[6]));
                    }
                    addd2 += parseFloat(aData[7]);//第几列
                    $(".totalMonthlyFee").html(addd.toFixed(2),2);
                    $(".totalLongFee").html(addd1.toFixed(2),2);
                    $(".totalFee").html(addd2.toFixed(2),2);
                    return nRow;
                },"fnPreDrawCallback": function( oSettings ) { 
                    $(".totalMonthlyFee").html(0);
                    $(".totalLongFee").html(0);
                    $(".totalFee").html(0);
                }  
			});

			 
		     
			$(".dateEditor").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.endDateStr = newValue;
					$.get("${ctx}/amazoninfo/afterSale/updateEndDate?"+$.param(param),function(data){
						if(data != "1"){
							$this.text(oldVal);
							$.jBox.tip(data, 'info',{timeout:2000});
						}else{
							$.jBox.tip("修改成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}
			});
			
			$("#bathTransmitOther").click(function(){
				if($(".checked :hidden").size()){
					var userId = $("#transmitSelOther").val();
					var userName = $("#transmitSelOther option[value='"+userId+"']").text();
					top.$.jBox.confirm("",'Forwarding To '+userName+'?',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							}); 
							params.userId = userId;
							window.location.href = "${ctx}/amazoninfo/afterSale/batchTransmitOther?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("No one yet finished processing the message, not forward!","error",{persistent:false,opacity:0});
				}
			});
			
		});
		
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
		<li class="${empty country ?'active':''}"><a class="countryHref" href="#" key=""><b>总计</b></a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}"><b>${dic.label}</b></a></li>
			</c:if>
		</c:forEach>	
	</ul>
<div id="message"></div>
	<form:form id="searchForm" modelAttribute="amazonCustomFilter" action="${ctx}/amazoninfo/storageFee" method="post" class="breadcrumb form-search">
		<input  name="country" type="hidden" value="${country}"/>
		<div style="height: 45px;line-height: 40px">
			<div style="height: 40px;">
				<c:if test="${startDate !=''}">
					<input style="width: 100px" onclick="WdatePicker({maxDate:'${maxDate}',dateFmt:'yyyy-MM',onpicked:function(){$('#searchForm').submit();return true}});"  value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM"/>" readonly="readonly"  class="Wdate" type="text" name="startDate"  id="startDate" class="input-small"/>
				</c:if>
				<c:if test="${startDate ==''}">
					<input style="width: 100px" onclick="WdatePicker({maxDate:'${maxDate}',dateFmt:'yyyy-MM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate"  id="startDate" class="input-small"/>
				</c:if>&nbsp;-&nbsp;
				<c:if test="${endDate !=''}">
					<input style="width: 100px" onclick="WdatePicker({maxDate:'${maxDate}',dateFmt:'yyyy-MM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM"/>" class="Wdate" type="text" name="endDate"  id="endDate" class="input-small"/>
				</c:if>
				<c:if test="${endDate ==''}">
					<input style="width: 100px" onclick="WdatePicker({maxDate:'${maxDate}',dateFmt:'yyyy-MM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate"  id="endDate" class="input-small"/>
				</c:if>
				&nbsp;
				<c:if test="${storageFee!=null }">
				<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
				</c:if>	
			</div>
		</div>
	</form:form>
	<%-- <tags:message content="${message}"/> --%>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width:200px">产品名称</th>	
			   <th>长</th>
			   <th>宽</th>
			   <th>高</th>
			   <th>单位</th>
			   <th>月存储总费(€)</th>
			   <th>长期存储总费(€)</th>
			   <th style="width:200px">总费(€)</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${storageFee}" var="list">
			<tr>
				<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${list.productName}">
				    ${list.productName}</a>(${typeLineMap[fn:toLowerCase(nameTypeMap[list.productName])] }线)
				</td>
				<td>${list.longestSideNew}</td>
				<td>${list.medianSideNew}</td>
				<td>${list.shortestSideNew}</td>
				<td>${list.measurementUnits}</td>
                <td>
                    <c:if test="${list.totalMonthFee!='0.00'}">
						<a style="color: #08c;" class="fbaTip" >${list.totalMonthFee}</a>	
						<input class="country" name="countryE" value="${country }" type="hidden" />
						<input class="name" value="${list.productName}" type="hidden" />
						<input class="start" name="startE" value="${startDate}" type="hidden" />
						<input class="end" name="endE" value="${endDate}" type="hidden" />
						<input class="type" value="monthly" type="hidden" />
					</c:if>
					<c:if test="${list.totalMonthFee=='0.00' }">
                        ${list.totalMonthFee}
                    </c:if>
                </td>				
				<td>
					<c:if test="${list.totalLongFee!='0.00'}">
						<a style="color: #08c;" class="fbaTip" >${list.totalLongFee}</a>	
						<input class="country" value="${country }" type="hidden" />
						<input class="name" value="${list.productName}" type="hidden" />
						<input class="start" value="${startDate}" type="hidden" />
						<input class="end" value="${endDate}" type="hidden" />
						<input class="type" value="long" type="hidden" />
					</c:if>
					<c:if test="${list.totalLongFee=='0.00' }">
						${list.totalLongFee}
					</c:if>
				</td>
				<td>${list.totalFee}</td>
			</tr>
		</c:forEach>
		</tbody>
		<tfoot>
                <tr >
                    <td style="font-weight: bold;">Page Total</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td id="totalMonthlyFee" class="totalMonthlyFee"></td>                    
                    <td id="totalLongFee" class="totalLongFee"></td>
                    <td id="totalFee" class="totalFee"></td>                 
                </tr>
                <tr id = "totalTr">
                    <td style="font-size: 18px; font-weight: bold;">Total</td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td id="totalMonthlyFeeAll"></td>
                    <td id="totalLongFeeAll"></td>
                    <td id="totalFeeAll"></td>                  
                </tr>
            </tfoot>
	</table>
	<%-- <div class="pagination">${page}</div>  --%>
</body>
</html>
