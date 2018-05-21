<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品线人员关系编辑</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
		table tr{height:50px}
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
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('Please wait a moment!');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$("#btnSave").on("click",function(){
				var groupId = $("#groupId").val();
				$("#contentTable tbody tr ").each(function(){
					var param = {};
					param.groupId=groupId;
					param.country=$(this).find("input[name='country']").val();
					param.users=$(this).find("select[name='users']").val();
					$.get("${ctx}/psi/product/productGroupUserAjaxSave?"+$.param(param,true),function(){});
				});
				top.$.jBox.tip("保存[${groupName}]成功！","info");   
				window.location.href="${ctx}/psi/product/productGroupUser";
			});
		});
	
	</script> 
</head>
<body>
	
	<form:form id="inputForm" modelAttribute="" action="" method="post" class="breadcrumb form-search" Style="height: 30px;text-align:center;">
	<span style="font-size: 18px;font-weight: bold">${groupName}</span>
	<input name="groupId" type="hidden" value="${groupId}" id="groupId"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed" style="margin-top:20px">
		<thead>
		<tr style="height:30px">
			   <th style="width:15%">平台</th> <th style="width:85%">担当</th>
		</tr>
		</thead>
		<tbody>
			<tr >
				<td>${fns:getDictLabel('de','platform','')}<input type='hidden' name="country" value="de"/></td>
				<td>
					<select name="users" id="de" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['de']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['de'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('uk','platform','')}<input type='hidden' name="country" value="uk"/></td>
				<td>
					<select name="users" id="uk" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['uk']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['uk'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('it','platform','')}<input type='hidden' name="country" value="it"/></td>
				<td>
					<select name="users" id="it" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['it']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['it'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('es','platform','')}<input type='hidden' name="country" value="es"/></td>
				<td>
					<select name="users" id="es" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['es']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['es'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('fr','platform','')}<input type='hidden' name="country" value="fr"/></td>
				<td>
					<select name="users" id="fr" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['de']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['fr'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('jp','platform','')}<input type='hidden' name="country" value="jp"/></td>
				<td>
					<select name="users" id="jp" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['jp']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['jp'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('com','platform','')}<input type='hidden' name="country" value="com"/></td>
				<td>
					<select name="users" id="com" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['com']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['com'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('ca','platform','')}<input type='hidden' name="country" value="ca"/></td>
				<td>
					<select name="users" id="ca" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['ca']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['ca'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
			<tr>
				<td>${fns:getDictLabel('mx','platform','')}<input type='hidden' name="country" value="mx"/></td>
				<td>
					<select name="users" id="mx" multiple  style="width:95%" class="multiSelect">
						<c:forEach items="${shelvesMap['mx']}" var="shelves">
							<c:set value="${fn:split(shelves,',')[0]}," var="userId"/>
							<option value="${fn:split(shelves,',')[0]}" ${fn:contains(countryMap['mx'],userId)?'selected':'' }>${fn:split(shelves,',')[1]}</option>
						</c:forEach>
					</select>
				</td>
			</tr>
		</tbody>
	</table>
	<div class="form-actions">
			<input id="btnSave" class="btn btn-primary" type="button" value="保 存" />&nbsp;&nbsp;&nbsp;
		</div>
	</form:form>
</body>
</html>
