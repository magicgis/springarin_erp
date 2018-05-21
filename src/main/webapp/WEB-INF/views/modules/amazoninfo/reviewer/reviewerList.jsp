<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>Reviewer List</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
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

.pr-c7y-badge {
	    border-bottom: 1px dashed #a9a9a9;
	    border-top: 1px dashed #a9a9a9;
	    color: #004b91;
	    display: inline-block;
	    font-size: 9px;
	    font-weight: bold;
	    letter-spacing: 0.5px;
	    line-height: 1.6;
	    margin-bottom: 6px;
	    margin-right: 8px;
	    padding: 0;
	    text-transform: uppercase;
	    white-space: nowrap;
	}
	.a-text-bold {
	    font-weight: 700 !important;
	}
	.a-color-link {
	    color: #0066c0 !important;
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
	
	if(!(top)){
		top = self; 
	}
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+')', tr).text();
		} );
	}
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+')', tr).text().replace("%","");
		} );
	}
	
	$.fn.dataTableExt.afnSortData['dom-html2'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
			if(a.text() == null || a.text() == 'Empty'){
				return -1;
			}
			return parseInt(a.text());
		} );
	};
	
	$(function() {
		
		$(".countryHref").click(function(){
			$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer?');
			$("input[name='country']").val($(this).attr("key"));
			$("#searchForm").submit();
		});
		
		$(".typeHref").click(function(){
			var key = $(this).attr("key");
			if(key == '0' || key == '1'){
				$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer?');
				$("#reviewerType").val(key);
				$("#searchForm").submit();
			} else {
				$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer/totalList?');
				$("#searchForm").submit();
			}
		});
		
		$("a[rel='popover']").popover({trigger:'hover'});
		
		$(".dateEditor").editable({
			mode:'inline',
			showbuttons:'bottom',
			success:function(response, newValue){
				if(newValue != '1' && newValue != '2' && newValue != '3' && 
						newValue != '4' && newValue != '5'){
					$.jBox.tip("客户评分只能为整数,范围为1~5！", 'info',{timeout:3000});
					return false;
				}
				var param = {};
				var $this = $(this);
				var oldVal = $this.text();
				param.id = $this.parent().find(":hidden").val();
				param.star = newValue;
				$.get("${ctx}/amazoninfo/reviewer/addStar?"+$.param(param),function(data){
					if(!(data)){
						$this.text(oldVal);
					}else{
						$.jBox.tip("保存评分成功！", 'info',{timeout:2000});
					}
				});
				return true;
			}
		});
		
		$("#aboutMe,#isVineVoice").change(function(){
			$("#searchForm").submit();
		});
		
		$("#addReviewer").click(function(){
			$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer/form');
			$("#searchForm").submit();
		});
		
		$("#bathSendEmail").click(function(){
			if($(".checked :hidden").size()){
				var params = {};
				params.emails = [];
				$(".checked :hidden").each(function(){
					params.emails[params.emails.length] = $(this).val();
				});
				window.open("${ctx}/amazoninfo/reviewer/batchSendEmail?"+$.param(params)); 
				//window.location.href = "${ctx}/amazoninfo/reviewer/batchSendEmail?"+$.param(params);
			}else{
				top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
			}
		});
		
		$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 30, 60, 100, -1 ],
						[ 10, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
			    "aoColumnDefs": [
			                     { "bSortable": false, "aTargets": [ 0 ] }
			                   ],
				"aoColumns": [
						         null,
						         null,
						         { "sSortDataType":"dom-html2", "sType":"numeric" },	
						         null,
						         { "sSortDataType":"dom-html", "sType":"numeric" },	
						         null,
						         { "sSortDataType":"dom-html1", "sType":"numeric" },	
						         null,
						         { "sSortDataType":"dom-html", "sType":"numeric" },	
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							      { "sSortDataType":"dom-html", "sType":"numeric" },
							     null
							     ],
				"ordering" : true,
				"aaSorting": [[ 3, "asc" ]],
				"fnInitComplete": function(oSettings, json) { 
				     var cnt='跳转至第<input type="text" style="width: 30px;padding-top: 5px;padding-bottom: 5px;height: 18px;border-left: 0px;border-radius: 0px 4px 4px 0px;" id="redirect" class="redirect">页';
					 $("#contentTbDiv .spanexr ul").append(cnt);
					 $(".redirect").keyup(function(e){
		                var ipage = parseInt($(this).val());
		                var oPaging = oSettings.oInstance.fnPagingInfo();
		        		if(isNaN(ipage) || ipage<1){
		                    ipage = 1;
		                }else if(ipage>oPaging.iTotalPages){
		                    ipage=oPaging.iTotalPages;
		                }
		                //$(this).val(ipage);
		                $(".redirect").val(ipage);
		                ipage--;
		                //oSettings._iDisplayStart = ipage * oPaging.iLength;
		                var oTable = $("#contentTable").dataTable();
		                oTable.fnPageChange( ipage );
		                //fnDraw( oSettings );
			        });
				}
		});
		 
		<%-- 站内站外选择
		var html1 = " 评测人类型:<select name=\"selectType\" id=\"selectType\" style=\"width: 100px\" onchange=\"changeReviewerType()\">"+
			"<option value=\"0\" ${'0' eq reviewer.reviewerType?'selected':''}>站内</option>"+
			"<option value=\"1\" ${'1' eq reviewer.reviewerType?'selected':''}>站外</option></select> &nbsp;&nbsp;&nbsp;";
		
		$("#contentTbDiv .spanexr div:first").append(html1); --%>
	});
	
	function changeReviewerType(){
		var key = $("#reviewerType").val();
		if(key == '0' || key == '1'){
			$("#searchForm").submit();
		}  else {
			$('#searchForm').attr('action','${ctx}/amazoninfo/reviewer/totalList?');
			$("#searchForm").submit();
		}
	}

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a class="typeHref" href="#" key="">客户汇总</a></li>
		<li class="active"><a class="typeHref" href="#" key="0">站内客户</a></li>
		<li><a class="typeHref" href="#" key="1">站外客户</a></li>
	</ul>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${reviewer.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	<form:form id="searchForm" modelAttribute="reviewer" action="${ctx}/amazoninfo/reviewer" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${reviewer.country}"/>
		<div style="line-height: 40px">
			<div >
			<label>评测人类型:</label>
			<form:select path="reviewerType" onchange="changeReviewerType()">
				<form:option value="0" label="站内"/>
				<form:option value="1" label="站外"/>
				<form:option value="" label="汇总"/>
			</form:select>
			<label>姓名:</label>
			<form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
			<label>邮箱:</label>
			<form:input path="reviewEmail" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;
			<input type="checkbox" name="aboutMe" id="aboutMe" value="1" ${aboutMe eq '1'?'checked':''}/><spring:message code='custom_email_btn3'/>
			&nbsp;
			<input type="checkbox" name="isVineVoice" id="isVineVoice" value="1" ${isVineVoice eq '1'?'checked':''}/>Vine Voice
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			&nbsp;
			<input id="addReviewer" class="btn btn-primary" type="button" value="添加评测人"/>
			&nbsp;
			<input id="bathSendEmail" class="btn btn-primary" type="button" value="群发邮件"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<div id="contentTbDiv" style="width:100%;margin: auto">
		<div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 20px">
							<span>
								<input type="checkbox">
								</span>
						</th>
						<th style="width:100px">评测人</th>
						<th>客户评分</th>
						<th>排名</th>
						<th>联系次数</th>
						<th>评测<br/>平均分</th>
						<th>电子产品<br/>占比</th>
						<th>更新时间</th>
						<th>Inateck</th>
						<th>Anker</th>
						<th>Aukey</th>
						<th>TaoTronics</th>
						<th>EasyAcc</th>
						<th>Mpow</th>
						<th>RAVPower</th>
						<th>&nbsp;CSL&nbsp;</th>
						<%-- <th>其它</th> --%>
						<th>操作</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr>
							<td>
								<div class="checker">
								<span>
									<c:if test="${not empty op[1]}">
									  <input type="checkbox"/>
									  <input type="hidden" value="${op[1]}" class="reviewerId"/>
									</c:if>
								</span>
								</div>
							</td>
							<td>
							<c:if test="${'0' eq  reviewer.reviewerType}">
								<a target="_blank" href="http://www.amazon.${key}/gp/pdp/profile/${op[17]}">${op[0]}</a>
							</c:if>
							<c:if test="${'1' eq  reviewer.reviewerType}">
								${op[0]}
							</c:if>
							<span style="display: none">${op[17]}</span><%-- 提供按客户ID搜索功能 --%>
							
							<c:if test="${reviewer.isVineVoice eq '1'}"><span class="pr-c7y-badge a-text-bold a-color-link">Vine Voice</span></c:if>
							</td>
							<td>
								<input type="hidden" value="${op[12]}" />
								<a href="#" class="dateEditor"  data-type="number" data-length="1" data-pk="1" data-title="Enter">
								<c:if test="${not empty op[2]}">${op[2]}</c:if></a>
							</td>
							<td>${op[3]}</td><!-- 亚马逊排名 -->
							<td>
								${contactNum[op[12]] }
								<c:if test="${empty contactNum[op[12]] }">0</c:if>
							</td><!-- 联系次数 -->
							<td><fmt:formatNumber value="${op[13]}" maxFractionDigits="2" /></td><!-- 评测平均分 -->
							<td><!-- 电子产品占比 -->
								<c:if test="${not empty op[16]}">
									<fmt:formatNumber pattern="#######.##" value="${op[16]*100}" maxFractionDigits="2" minFractionDigits="2" />%
								</c:if>
							</td>
							<td>${op[14]}</td><!-- 更新时间 -->
							<!-- inateck -->
							<td><c:if test="${op[4] > 0}">
								<a class="btn btn-small btn-info" rel="popover" data-html="true" data-content="${tips[op[12]]}" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=inateck">${op[4]}</a>
							</c:if></td>
							<!-- anker -->
							<td><c:if test="${op[5] > 0}">
								<a class="btn btn-small btn-warning" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=anker">${op[5]}</a>
							</c:if></td>
							<!-- aukey -->
							<td><c:if test="${op[6] > 0}">
								<a class="btn btn-small btn-info" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=aukey">${op[6]}</a>
							</c:if></td>
							<!-- taotronics -->
							<td><c:if test="${op[7] > 0}">
								<a class="btn btn-small btn-warning" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=taotronics">${op[7]}</a>
							</c:if></td>
							<!-- easyacc -->
							<td><c:if test="${op[8] > 0}">
								<a class="btn btn-small btn-info" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=easyacc">${op[8]}</a>
							</c:if></td>
							<!-- mpow -->
							<td><c:if test="${op[9] > 0}">
								<a class="btn btn-small btn-warning" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=mpow">${op[9]}</a>
							</c:if></td>
							<!-- ravpower -->
							<td><c:if test="${op[10] > 0}">
								<a class="btn btn-small btn-info" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=ravpower">${op[10]}</a>
							</c:if></td>
							<!-- csl -->
							<td><c:if test="${op[11] > 0}">
								<a class="btn btn-small btn-warning" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=csl">${op[11]}</a>
							</c:if></td>
							<!-- other
							<td><c:if test="${op[15] > 0}">
								<a class="btn btn-small btn-info" href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}&brandType=other">${op[15]}</a>
							</c:if></td> -->
							<td>
								<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/reviewer/form?id=${op[12]}">编辑</a>
								<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/reviewer/sendEmail?email=${op[1]}">发送邮件</a>
								<div class="btn-group">
									<button type="button" class="btn btn-success" >记录</button>
									<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only"></span>
									</button>
									<ul class="dropdown-menu" id="allExport">
										<li><a href="${ctx}/amazoninfo/reviewer/records?id=${op[12]}">联系记录</a></li>
										<li><a href="${ctx}/amazoninfo/reviewer/reviewerProductList?reviewer.id=${op[12]}">评测记录</a></li>
									</ul>
								</div>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>
