<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>花名册管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
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
		$(document).ready(function() {
			$("#officeId,#workSta,#month").on("click",function(){
				$("#searchForm").submit();
			});
			
			
			 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap","sScrollX": "100%",
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
				 	"aaSorting": [[7, "desc" ]]
				});
				 new FixedColumns( oTable,{
				 		"iLeftColumns":9,
						"iLeftWidth": 650
				 	} );
				 $(".row:first").append($("#searchContent").html());
				 
				 
				 
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
		<li class="active"><a href="">花名册列表</a></li>
		<shiro:hasPermission name="oa:roster:edit">
			<li ><a href="${ctx}/oa/roster/form">增加花名册</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="roster" action="${ctx}/oa/roster/" method="post" class="breadcrumb form-search">
		<div style="height: 50px;line-height: 40px">
			<label>部门：</label>
			<select name="office.id" id ="officeId" style="width:150px">
				<option value="">全部</option>
				<c:forEach items="${offices}" var="office">
					<option value="${office.id}" ${office.id eq roster.office.id?'selected':'' }>${office.name}</option>
				</c:forEach>
			</select>
			<label>入职日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${roster.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${roster.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>生日查询：</label>
			<select name="month" id ="month" style="width:100px">
					<option value="">全部</option>
					<option value="01" ${"01" eq month?'selected':'' }>1月</option>
					<option value="02" ${"02" eq month?'selected':'' }>2月</option>
					<option value="03" ${"03" eq month?'selected':'' }>3月</option>
					<option value="04" ${"04" eq month?'selected':'' }>4月</option>
					<option value="05" ${"05" eq month?'selected':'' }>5月</option>
					<option value="06" ${"06" eq month?'selected':'' }>6月</option>
					<option value="07" ${"07" eq month?'selected':'' }>7月</option>
					<option value="08" ${"08" eq month?'selected':'' }>8月</option>
					<option value="09" ${"09" eq month?'selected':'' }>9月</option>
					<option value="10" ${"10" eq month?'selected':'' }>10月</option>
					<option value="11" ${"11" eq month?'selected':'' }>11月</option>
					<option value="12" ${"12" eq month?'selected':'' }>12月</option>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>在岗状态：</label>
			<select name="workSta" id ="workSta" style="width:100px">
					<option value="">所有</option>
					<option value="1" ${"1" eq roster.workSta?'selected':'' }>在岗</option>
					<option value="0" ${"0" eq roster.workSta?'selected':'' }>离职</option>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		</div>
	</form:form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<th>工号</th>
				<th>员工姓名</th>
				<th>英文名</th>
				<th>在岗状态</th>
				<th>部门</th>
				<th>职位</th>
				<th>入职日期</th>
				<th>操&nbsp;&nbsp;&nbsp;&nbsp;作</th>
				<th>转正日期</th>
				<th>性别</th>
				<th>出生日期</th>
				<th>年龄</th>
				<th>身份证号</th>
				<th>联系电话</th>
				<th>司龄</th>
				<th>竞业协议</th>
				<th>最终学历</th>
				<th>毕业学校</th>
				<th>985 or 211</th>
				<th>专业</th>
				<th>户口性质</th>
				<th>户口所在地</th>
				<th>政治面貌</th>
				<th>婚否</th>
				<th style="width:300px">现居住地址</th>
				<th>家庭地址</th>
				<th>籍贯</th>
				<th>星座</th>
				<th>英语等级</th>
				<th>离职日期</th>
				<th>离职原因</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${rosters}" var="roster">
			<tr>
				<td>${roster.id}</td>
				<td>${roster.jobNo}</td>
				<td>${roster.cnName}</td>
				<td><a href="${ctx}/oa/roster/view?id=${roster.id}">${roster.enName}</a></td>
				<td>  
					<c:if test="${roster.workSta eq '1'}">在岗</c:if>
					<c:if test="${roster.workSta eq '0'}">离职</c:if>
				</td>
				<td>${roster.office.name}</td>
				<td>${roster.position}</td>
				<td><fmt:formatDate value="${roster.entryDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<shiro:hasPermission name="oa:roster:edit">
	    				<a class="btn btn-small" href="${ctx}/oa/roster/form?id=${roster.id}">修改</a>&nbsp;&nbsp;&nbsp;&nbsp;
    				</shiro:hasPermission>
				</td>
				
				<td ><fmt:formatDate value="${roster.egularDate}" pattern="yyyy-MM-dd"/> </td>
				<td>  
					<c:if test="${roster.sex eq '1'}">男</c:if>
					<c:if test="${roster.sex eq '0'}">女</c:if>
				</td>
				<td><fmt:formatDate value="${roster.birthDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${roster.age}</td>
				<td>${roster.idCard}</td>
				<td>${roster.phone}</td>
				<td>${roster.workYear}</td>
				<td>${roster.hasCompete}</td>
				<td>${roster.education}</td>
				<td>${roster.school}</td>
				<td>
					<c:if test="${roster.isKey eq '0'}">211</c:if>
					<c:if test="${roster.isKey eq '1'}">985</c:if>
					<c:if test="${roster.isKey eq '2'}">985&211</c:if>
				</td>
				<td>${roster.specialities}</td>
				<td>
					 <c:if test="${roster.homeType eq '1'}">农村</c:if>
				   	 <c:if test="${roster.homeType eq '0'}">城镇</c:if>
				</td>
				<td> ${roster.homeAddress}</td>
				<td>${roster.politicsFace}</td>
				<td>
					 <c:if test="${roster.isMarry eq '1'}">已婚</c:if>
					<c:if test="${roster.isMarry eq '0'}">未婚</c:if>
				</td>
				<td >${roster.address}</td>
				<td>${roster.familyAddress}</td>
				
				<td> ${roster.originPlace}</td>
				<td> ${roster.zodiac}</td>
				<td> ${roster.englishLevel}</td>
				<td> <fmt:formatDate value="${roster.leaveDate}" pattern="yyyy-MM-dd"/></td>
				<td> ${roster.leaveReason}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
