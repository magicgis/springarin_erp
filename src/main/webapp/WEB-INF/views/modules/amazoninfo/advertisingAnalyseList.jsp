<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告报表</title>

	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
	</style>
	
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
			
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			if($("#searchType").val()==1){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchType").val()==2){
		    	$("#showTab1").addClass("active");
		    }else if($("#searchType").val()==3){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    }
			
			oldSearchFlag= $("#searchType").val();
		

			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
				"aLengthMenu" : [12,24,36,120], //更改显示记录数选项
				 "iDisplayLength" :12,
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":false,"bSort":false
			 	
			});
			 new FixedColumns( oTable,{
			 		"iLeftColumns":1,
					"iLeftWidth":300
			 	} );
			 $(".row:first").append($("#searchContent").html());
			 
		/*  $('.chartsShow').mouseover(function(){
			        $(this).css({
			            'backgroundColor':'#FFEC8B',
			            'color':'#63B8FF'
			        });
			    });
			    $('.chartsShow').mouseout(function(){
			        $(this).css({
			            'backgroundColor':'#FFFFFF',
			            'color':'#242424'
			        });
			    });  */
			
			    $("#btnExport").click(function(){
			    
			    	$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportAdsKeyword");
			    	$("#searchForm").submit();
			    	$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/adsAnalyse");
			    });
			    
			    $("#btnExport2").click(function(){
			    	
			    	$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportAdsKeyword2");
			    	$("#searchForm").submit();
			    	$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/adsAnalyse");
			    });
		});
		
		function mouseover(c){
			 $(c).css({
		            'backgroundColor':'#FFEC8B',
		            'color':'#63B8FF'
		     });
		}
		
		function mouseout(c){
			 $(c).css({
		            'backgroundColor':'#FFFFFF',
		            'color':'#242424'
		        });
		}
		
		function showCharts(name){
			var createDate = $("#start").val();
			var dataDate = $("#end").val();
			var  country = $("#country").val();
			var  searchFlag=$("#searchType").val();
			$.ajax({
				    type: 'post',
				    async:false,
				    url: '${ctx}/amazoninfo/advertising/findKeyWordByName',
				    data: {
				    	"country":country,
				    	"dataDate1":dataDate,
				    	"createDate1":createDate,
				    	"searchFlag":searchFlag,
				    	"name":name
				    },
				    success:function(data){ 
				    	$("#chartsType").find(".modal-body").html(data);
			        }
			 });
			$("#chartsType").modal();
		}
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			if($("#searchType").val()==1){
		    	 $("#showTab0").addClass("active");
		    	 $("#showTab1").removeClass("active");
		    	 $("#showTab2").removeClass("active");
		    }else if($("#searchType").val()==2){
		    	$("#showTab1").addClass("active");
		    	 $("#showTab0").removeClass("active");
		    	 $("#showTab2").removeClass("active");
		    }else if($("#searchType").val()==3){
		    	$("#showTab2").addClass("active");
		    	 $("#showTab1").removeClass("active");
		    	 $("#showTab0").removeClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    	 $("#showTab1").removeClass("active");
		    	 $("#showTab2").removeClass("active");
		    }
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
				<li class="${advertising.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<!-- <div style="display: none" id="searchContent"> -->

	<form id="searchForm"  action="${ctx}/amazoninfo/advertising/adsAnalyse" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<div style="float: left;">
			     <ul class="nav nav-pills" style="width:180px;float:left;" id="myTab">
					<!-- <li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false">By Day</a></li> -->
					<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false">By Week</a></li>
					<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false">By Month</a></li>
				 </ul>
				<span id="date" >
					<input  style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${advertising.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${advertising.dataDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
					&nbsp;&nbsp;
				</span>
				<input  id="country" name="country" type="hidden" value="${advertising.country}"/>
				<input  name="searchFlag" type="hidden" value="${advertising.searchFlag}" id='searchType'/>
				产品线:<select name="groupName" id="groupName" style="width:100px">
				<option value="">--All--</option>
					<c:forEach items="${lineList}" var="lineList">
						<option value="${lineList.id}" ${lineList.id eq advertising.groupName?'selected':''}>${lineList.name}</option>			
					</c:forEach>
			   </select>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			    &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
			    &nbsp;<input id="btnExport2" class="btn btn-primary" type="button" value="导出(关键字)"/><span style='color:#ff0033;'>(货币单位为对应当地货币单位)</span> 
			</div>
		</div>
	</form>
	<!-- </div> -->
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;">产品</th>
				<!-- <th style="text-align: center;vertical-align: middle;">类型</th> -->
                <c:forEach items="${xAxis}" var="xAxis">
                   <th style="text-align: center;vertical-align: middle;">${xAxis}</th>
                </c:forEach>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${adsMap}" var="ads" varStatus="i">
			<tr class='chartsShow' onclick="showCharts('${ads.key}');" onMouseOver='mouseover(this);' onMouseOut='mouseout(this);'>
				<td style="text-align: left;vertical-align: middle;${i.index%2==0?'background-color: #EEE9E9;':'' }">${ads.key}(Clicks)</td>
			 
			    <c:forEach items="${xAxis}" var="xAxis">
                    <td style="text-align: center;vertical-align: middle;" >${adsMap[ads.key][xAxis].clicks }</td>
                </c:forEach>
			</tr>
			<tr class='chartsShow' onclick="showCharts('${ads.key}');" onMouseOver='mouseover(this);' onMouseOut='mouseout(this);'>
				<td style="text-align: left;vertical-align: middle;${i.index%2==0?'background-color: #EEE9E9;':'' }">${ads.key}(Ads Quantity)</td>

			     <c:forEach items="${xAxis}" var="xAxis">
                    <td style="text-align: center;vertical-align: middle;">${adsMap[ads.key][xAxis].sameSkuOrdersPlaced }</td>
                </c:forEach>
			</tr>
			<tr class='chartsShow' onclick="showCharts('${ads.key}');" onMouseOver='mouseover(this);' onMouseOut='mouseout(this);'>
				<td style="text-align: left;vertical-align: middle;${i.index%2==0?'background-color: #EEE9E9;':'' }">${ads.key}(Ads Costs)</td>

				 <c:forEach items="${xAxis}" var="xAxis">
                    <td style="text-align: center;vertical-align: middle;">${adsMap[ads.key][xAxis].totalSpend }</td>
                </c:forEach>
			</tr>
		</c:forEach>	
		</tbody>
	</table>
	
	<div id="chartsType"  class="modal hide fade" tabindex="-1"  style="width: auto;position: absolute;left:350px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
		</div>
		<div class="modal-body" >
			 
		</div>
	</div>
	
</body>
</html>
