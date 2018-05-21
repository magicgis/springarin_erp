<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊排名销量分析</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="Content-Type" content="text/html;charset=utf-8">
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" type="text/css" />
	<script type="text/javascript" src="${ctxStatic}/x-editable/js/bootstrap-editable.js"></script>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
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
		});
		
		
		function openTree(){
			var country=$("#country").val();
			if(nodes1!=null&&nodes1.length>0){
				var tree = $.fn.zTree.init($("#menuTree"), setting,nodes1);
	            tree.setting.check.chkboxType = { "Y" : "", "N" : "" };
			}else{
				$.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/treeData2',  
			        dataType:"json",
			        data : "country="+country,  
			        async: true,
			        success : function(msg){
			        	var nodes="[ ";
			        	for(var i=0;i<msg.length;i++){
							nodes+="{\"id\":\""+msg[i]['id']+"\",\"pId\":\""+(msg[i]['pId']==null?0:msg[i]['pId'])+"\",\"name\":\""+html_decode(html_decode(html_decode(html_decode(msg[i]['name']))))+"\",\"title\":\""+htmlEncode(msg[i]['title'])+"\",\"chkDisabled\":"+((msg[i]['title']==null||msg[i]['title']=='')?true:false)+"},";
			        	} 
			        	nodes=nodes.substring(0,nodes.length-1);
			        	nodes+="]";
			        	var treeObj=eval('('+nodes+')');
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
		<li ><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${opponentAsin.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a href="${ctx}/amazoninfo/opponentAsin/form">新建对手产品销量监控</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonPortsDetail/rankSalesAnalyse">排名销量分析</a></li>
	</ul>
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonPortsDetail/rankSalesAnalyse" method="post" class="breadcrumb form-search" >
		<div style="height: 40px;line-height: 30px">
				<div>
				   <label class="control-label"><b>平台:</b></label>
				   	<select id="country" name="country" style="width: 120px" class="required">
					    <option value="" selected="selected">-请选择平台-</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" ${country eq dic.value?'selected':'' }>${dic.label}</option>
							</c:if>
						</c:forEach>
				   </select>
				  &nbsp;&nbsp;&nbsp;&nbsp;
				   <label class='control-label'><b>目录:</b></label>
				    <input  type='text' class='required'  name='catalog'  id='catalog'  style='width:200px;' value='${catalog }'/>
			        <input type='button' onclick="openTree();" value='目录搜索'/>
			        
			         &nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				</div>
				
		</div>		
				       
	</form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;">排名</th>
				<th>日销量</th>
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty rankMap }">
		   <c:forEach items="${rankMap}" var="rank" varStatus="i">
			<tr>
			  <td style="text-align: center;vertical-align: middle;width:300px;">${rank.key }</td>
			  <td style="text-align: left;vertical-align: middle;">${rankMap[rank.key] }</td>
			</tr>
		   </c:forEach>
		</c:if>
		<c:if test="${empty rankMap&&not empty catalog }">
			<tr>
			  <td colspan='2'>最近半年没有产品在此目录下,无排名销量数据</td>
			</tr>
		</c:if>
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