<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>目录销量分析</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<script type="text/javascript">
		if(!(top)){
			top = self; 
		}
		
		var nodes1=[];
		var setting = {check:{enable:true,nocheckInherit:false,chkStyle:'radio',radioType:'all' },view:{selectedMulti:false,fontCss: setHighlight},
				data:{simpleData:{enable:true}},callback:{beforeClick:function(id, node){
						tree.checkNode(node, !node.checked, true, true);
					    return false;
				},onCheck: zTreeOnCheck}};
		
		$(document).ready(function() {
			$("#dataBtn").keydown(function (e) {
			      var curKey = e.which;
			      if (curKey == 13) {
			         $("#dataBtnSearch").click();
			         return false;
			      }
			});
			
			//计算市场份额
			var arr = $("#contentTable tbody tr");
			var totalV=0;
			arr.each(function() {
				   if($(this).find("td :eq(1)").text()){
					   var tempV=parseInt($(this).find("td :eq(1)").text().split(",").join(''));	//销量
					   totalV += tempV;
				   }
			});
			
			arr.each(function() {
				  var tempV=parseInt($(this).find("td :eq(1)").text().split(",").join(''));
				   if(tempV){
					   $(this).find("td :eq(5)").text((100*tempV/totalV).toFixed(2));
				   }
			});
			
			
			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"sScrollX": "100%",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aaSorting": [[ 2, "desc" ]],
				"aoColumns": [
						         null,	
						         { "sSortDataType":"dom-html", "sType":"numeric" },
						         { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     null,
							     { "sSortDataType":"dom-html", "sType":"numeric" }
							  
							   ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			
			$("#country").change(function(){
				$("#inputForm").submit();
			});
			
		});
		
		
		function openTree(){
			var country=$("#country").val();
			if(nodes1!=null&&nodes1.length>0){
				var tree = $.fn.zTree.init($("#menuTree"), setting,nodes1);
	            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			}else{
				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/productCatalog/treeData',  
			        dataType:"json",
			        data : "country="+country,  
			        async: true,
			        success : function(msg){
			        	var treeObj=msg;
			        	nodes1=treeObj;
			        	var tree = $.fn.zTree.init($("#menuTree"), setting,treeObj);
			            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			        }
			   });
			}
		   $("#selectTree").modal();
		}
		
		
        function zTreeOnCheck(event, treeId, treeNode) {
        	$("input[name='catalog']").val(treeNode.title);
		}
		
		function setHighlight(treeId, treeNode) {
	          return (treeNode.highlight) ? {color:"green", "font-weight":"bold", "background-color": "#ddd"} : {color:"#000", "font-weight":"normal"};
	    }
		var timeoutId = null;
		function searchNodeLazy(value) {
			if (timeoutId) {
				clearTimeout(timeoutId);
			}
			timeoutId = setTimeout(function(){
				searchNode(value);	
			}, 500);
		}
		
		function searchNode(){
			var searchCnt=$("#dataBtn").val();
			console.log(searchCnt);
			if(searchCnt==''){
				return;
			}
			var treeObj = $.fn.zTree.getZTreeObj('menuTree'); 
			var nodes = treeObj.getNodesByParamFuzzy("name", searchCnt);
			var allNodes = treeObj.transformToArray(treeObj.getNodes());

			for (var i = 0; i < allNodes.length; i ++) {
				allNodes[i].highlight = false;
				treeObj.updateNode(allNodes[i]);
			}
			treeObj.expandAll(false);
			treeObj.expandNode(treeObj.getNodes()[0],true);
			for (var i = 0; i < nodes.length; i ++) {
				nodes[i].highlight = true;
				treeObj.updateNode(nodes[i]);
				treeObj.expandNode(nodes[i].getParentNode(),true);
			}
		}
		
		function html_decode(str) 
		{ 
		    var s = ""; 
		    if (str.length == 0) return ""; 
		    s = str.replace(/&amp;/g, "&"); 
		    s = s.replace(/&lt;/g, "<"); 
		    s = s.replace(/&gt;/g, ">"); 
		    s = s.replace(/&nbsp;/g, " "); 
		    s = s.replace(/&#39;/g, "\'"); 
		    s = s.replace(/&quot;/g, "\""); 
		    s = s.replace(/<br\/>/g, "\n"); 
		    return s; 
		} 

		function htmlEncode(str) {
		    var div = document.createElement("div");
		    div.appendChild(document.createTextNode(str));
		    return div.innerHTML;
		}
		function htmlDecode(str) {
		    var div = document.createElement("div");
		    div.innerHTML = str;
		    return div.innerHTML;
		}
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<li ><a class="countryHref" >账号分析</a></li>
		<li class="active" ><a class="countryHref" href="${ctx}/amazoninfo/productCatalog/category" >目录分析</a></li> 
	</ul>
	<div class="alert">
	  	<button type="button" class="close" data-dismiss="alert">&times;</button>
	  	<strong>Tips:数据周期为2018-1-27日 至 2018-2-24日</strong> 
	</div>
	<form id="inputForm"  action="${ctx}/amazoninfo/productCatalog/countCategory" method="post" class="breadcrumb form-search" >
		<div style="height: 40px;line-height: 30px">
				<div>
				   <label class="control-label"><b>平台:</b></label>
				   <select name="country" id="country">
						<option value="US" ${'US' eq country?'selected':''}>US</option>
						<option value="DE" ${'DE' eq country?'selected':''}>DE</option>
				   </select>
				  &nbsp;&nbsp;&nbsp;&nbsp;
				   <label class='control-label'><b>目录:</b></label>
				    <input  type='text' class='required'  name='catalog'  id='catalog'  style='width:200px;' value='${catalog}'/>
			        <input type='button' onclick="openTree();" value='目录搜索'/>
			        
			         &nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				</div>
				
		</div>		
				       
	</form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;">品牌</th>
				<th>月销量</th>
				<th>月销售额(${country eq 'DE'?'€':'$'})</th>
				<th>客单价(${country eq 'DE'?'€':'$'})</th>
				<th>产品目录排名</th>
				<th>市场份额(%)</th>
			</tr>
		</thead>
		<tbody>
		    <c:forEach items="${data}" var="row" varStatus="i">
					<tr>
						<td>${row[0]}</td>
						<td>${row[1]}</td>
						<td><fmt:formatNumber value="${row[2]}" pattern="#.##" minFractionDigits="2" /></td>
						<td><fmt:formatNumber value="${row[3]}" pattern="#.##" minFractionDigits="2" /></td>
						<td>${row[4]}</td>
						<td></td>
					</tr>
		    </c:forEach>
		</tbody>
	</table>	
	<div id="selectTree" class="modal hide fade" tabindex="-1" data-width="350">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
				<h3>目录搜索</h3>
			</div>
			<div class="modal-body">
			    <form class="form-search">
				  <input type="text" class="input-medium search-query" id='dataBtn'  placeholder='输入目录名搜索'>
				  <button type="button" class="btn" id="dataBtnSearch" onclick="searchNode()">Search</button>
				</form>
			    
				<div id='menuTree' class='ztree'  style='margin-top:3px;float:center;'></div>
				<div class="modal-footer">
					<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
				</div>
			</div>
	</div>
	
</body>
</html>