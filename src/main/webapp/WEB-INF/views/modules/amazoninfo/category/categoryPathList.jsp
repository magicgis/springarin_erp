<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>目录树分析</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<meta name="decorator" content="default"/>
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
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
	</style>
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
		
		
		
		
		$("#country,#startDate").change(function(){
			$("#inputForm").submit();
		});
		
		
		$('.chartsShow').mouseover(function(){
	        $(this).css({
	            'backgroundColor':'#df0001',
	            'color':'#63B8FF'
	        });
	    });
	    $('.chartsShow').mouseout(function(){
	        $(this).css({
	            'backgroundColor':'#F2F2F2',
	            'color':'#242424'
	        });
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
		        url : '${ctx}/amazoninfo/productCatalog/catalogTreeDate',  
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
    	var info = treeNode.title;
    	var arr = info.split("[");
    	
    	$("#catalogInfo").html("<b>目录：<a href='http://www.amazon.com/gp/bestsellers/pc/"+arr[0]+"' target='_blank'>"+treeNode.name.split("[取样数:")[0]+"</a></b>&nbsp;&nbsp;&nbsp;"+arr[1].split("]")[0]);
    	$("input[name='catalog']").val(arr[0]);
    	$("#selectPrice").text('');
    	//获取当前节点的同级兄弟节点  
    	var siblings="";
    	if(treeNode.getParentNode() != null){  
           var  tempNodes = treeNode.getParentNode().children;  
           for(var z=0;z<tempNodes.length;z++){
        	   var tn = tempNodes[z].title;
        	   if(tn.indexOf("[")>=0){
        		   tn = tn.split("[")[0];
        	   }
        	   if(z==tempNodes.length-1){
        		   siblings  = siblings + tn;
        	   }else{
        		   siblings  = siblings+tn +",";
        	   }
           }
           $("#siblings").val(siblings);
        }  
    	
    	var params={};
		params.country=$("#country").val();
		params.catalog=$("#catalog").val();
		params.startDate=$("#startDate").val();
		if(siblings!=''){
			params.siblings=siblings; 
        }
		$("#typeInfo").html("");
		$.ajax({
		    type: 'post',
		    async:false,
		    url: '${ctx}/amazoninfo/productCatalog/brandList',
		    data: $.param(params),
		    success:function(msg){ 
		    	$("#contentTable tbody").empty();
		    	$("#catalogTabel tbody").empty();
		    	$("#asinTable tbody").empty();
		    	$("#priceTable tbody").empty();
			    var tbody=$("#contentTable tbody");
			    var brands=msg['0'];
			    var siblingsCtg=msg['1'];
			    var typeMsg=msg['2'];
			    var priceMsg=msg['3'];
			    var priceTbody=$("#priceTable tbody");
			    if(priceMsg!=undefined){
			    	 for(var i=0;i<priceMsg.length;i++){
			    		   var tr="";
			    		   var priceType=priceMsg[i].brand;
			    		   if(priceMsg[i].brand=='1'){
			    			   priceType='0~25';
			    			   tr="<tr  class='chartsShow' onclick=searchTypes('0~25')>";
			    		   }else if(priceMsg[i].brand=='2'){
			    			   priceType='25~50';
			    			   tr="<tr  class='chartsShow' onclick=searchTypes('25~50')>";
			    		   }else if(priceMsg[i].brand=='3'){
			    			   priceType='50~100';
			    			   tr="<tr  class='chartsShow' onclick=searchTypes('50~100')>";
			    		   }else if(priceMsg[i].brand=='4'){
			    			   priceType='100+';
			    			   tr="<tr  class='chartsShow' onclick=searchTypes('100+')>";
			    		   }else{
			    			   priceType='';
			    			   tr="<tr  class='chartsShow' onclick=searchTypes('')>";
			    		   }
					    	
					    	tr+="<td>"+(priceType==''?'<b>Total</b>':priceType)+"</td>";
					    	tr+="<td>"+toDecimal(priceMsg[i].sales)+"</td>";
					    	tr+="<td>"+priceMsg[i].quantity+"</td>";
					    	tr+="<td>"+priceMsg[i].num+"</td>";
					    	tr+="<td>"+priceMsg[i].brandNum+"</td>";
					    	tr+="<td>"+(toDecimal(priceMsg[i].quantity*100/priceMsg[priceMsg.length-1].quantity))+"%</td>";
					    	tr+="<td>"+(toDecimal(priceMsg[i].sales*100/priceMsg[priceMsg.length-1].sales))+"%</td>";
					    	tr+="<td><input type='button' onclick=searchTypes('"+priceType+"')  class='btn btn-warning' value='分析'/></td>";
					        tr+="</tr>";
					        priceTbody.append(tr);
					   }
			    }
			   /*  if(typeMsg!=undefined){
			    	var typeInfo="<ul class='nav nav-pills' style='float:left;' id='myTab'>";
			    	typeInfo = typeInfo+"<li data-toggle='pills' class='active'><a href='#' onclick=javaScript:searchTypes('')>All</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills'><a href='#' onclick=searchTypes('"+typeMsg[0].price1+"');>价格:"+typeMsg[0].price1+"</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills'><a href='#' onclick=searchTypes('"+typeMsg[0].price2+"');>价格:"+typeMsg[0].price2+"</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills'><a href='#' onclick=searchTypes('"+typeMsg[0].price3+"');>价格:"+typeMsg[0].price3+"</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills'><a href='#' onclick=searchTypes('"+typeMsg[0].price4+"');>价格:"+typeMsg[0].price4+"</a></li>";
			    	typeInfo =typeInfo+"</ul>";
			    	$("#typeInfo").html(typeInfo);
			    } */
			    
			   /*  for(var i=0;i<brands.length;i++){
			    	var asin = brands[i].asin.replace(/\s/g,'-').replace(/\'/g,'');
			    	var tr="<tr  class='chartsShow' onclick=showAsin('"+asin+"')>";
			    	tr+="<td>"+brands[i].brand+"</td>";
			    	tr+="<td>"+toDecimal(brands[i].sales)+"</td>";
			    	tr+="<td>"+brands[i].quantity+"</td>";
			    	tr+="<td>"+toDecimal(brands[i].price)+"</td>";
			    	tr+="<td>"+(brands[i].qtyRate==undefined?"":(toDecimal(brands[i].qtyRate))+"%")+"</td>";
			    	tr+="<td>"+(brands[i].salesRate==undefined?"":(toDecimal(brands[i].salesRate))+"%")+"</td>";
			        tr+="</tr>";
			        tbody.append(tr);
			    } */
			    var tbody1=$("#catalogTabel tbody");
			    if(siblingsCtg!=undefined){
			    	tbody1.append("<tr><th>同级目录</th><th>销售额</th><th>销量</th><th>单价</th><th>销量占比(%)</th><th>销售占比(%)</th></tr>");
			    	for(var i=0;i<siblingsCtg.length;i++){
				    	var tr="<tr>";
				    	tr+="<td>"+siblingsCtg[i].catalog+"</td>";
				    	tr+="<td>"+toDecimal(siblingsCtg[i].sales)+"</td>";
				    	tr+="<td>"+siblingsCtg[i].quantity+"</td>";
				    	tr+="<td>"+toDecimal(siblingsCtg[i].price)+"</td>";
				    	tr+="<td>"+(toDecimal(siblingsCtg[i].quantity*100/siblingsCtg[siblingsCtg.length-1].quantity))+"%</td>";
				    	tr+="<td>"+(toDecimal(siblingsCtg[i].sales*100/siblingsCtg[siblingsCtg.length-1].sales))+"%</td>";
				        tr+="</tr>";
				        tbody1.append(tr);
				    }
			    }
			    
		    }
		});   
	}
    
    function searchTypes(price){
    	var params={};
		params.country=$("#country").val();
		params.catalog=$("#catalog").val();
		params.startDate=$("#startDate").val();
		params.type = price;
		params.siblings=$("#siblings").val();; 
		$("#typeInfo").html("");
		$("#selectPrice").text(price);
		$.ajax({
		    type: 'post',
		    async:false,
		    url: '${ctx}/amazoninfo/productCatalog/brandList',
		    data: $.param(params),
		    success:function(msg){ 
		    	$("#contentTable").dataTable().fnDestroy();
		    	$("#asinTable").dataTable().fnDestroy();
		    	$("#contentTable tbody").empty();
		    	$("#catalogTabel tbody").empty();
		    	$("#asinTable tbody").empty();
			    var tbody=$("#contentTable tbody");
			    var brands=msg['0'];
			    var siblingsCtg=msg['1'];
			    var typeMsg=msg['2'];
			    /* if(typeMsg!=undefined){
			    	var typeInfo="<ul class='nav nav-pills' style='float:left;' id='myTab'>";
			    	typeInfo = typeInfo+"<li data-toggle='pills' "+(price==''?'class=active':'')+"><a href='#' onclick=javaScript:searchTypes('')>All</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills' "+(price==typeMsg[0].price1?'class=active':'')+"><a href='#' onclick=searchTypes('"+typeMsg[0].price1+"');>价格:"+typeMsg[0].price1+"</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills' "+(price==typeMsg[0].price2?'class=active':'')+"><a href='#' onclick=searchTypes('"+typeMsg[0].price2+"');>价格:"+typeMsg[0].price2+"</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills' "+(price==typeMsg[0].price3?'class=active':'')+"><a href='#' onclick=searchTypes('"+typeMsg[0].price3+"');>价格:"+typeMsg[0].price3+"</a></li>";
			    	typeInfo = typeInfo+"<li data-toggle='pills' "+(price==typeMsg[0].price4?'class=active':'')+"><a href='#' onclick=searchTypes('"+typeMsg[0].price4+"');>价格:"+typeMsg[0].price4+"</a></li>";
			    	typeInfo =typeInfo+"</ul>";
			    	$("#typeInfo").html(typeInfo);
			    }
			     */
			    for(var i=0;i<brands.length;i++){
			    	var asin = brands[i].asin.replace(/\s/g,'-').replace(/\'/g,'');
			    	var tr="<tr  class='chartsShow' onclick=showAsin('"+asin+"')>";
			    	tr+="<td>"+brands[i].brand+"</td>";
			    	tr+="<td>"+toDecimal(brands[i].sales)+"</td>";
			    	tr+="<td>"+brands[i].quantity+"</td>";
			    	tr+="<td>"+toDecimal(brands[i].price)+"</td>";
			    	tr+="<td>"+(brands[i].qtyRate==undefined?"":(toDecimal(brands[i].qtyRate))+"%")+"</td>";
			    	tr+="<td>"+(brands[i].salesRate==undefined?"":(toDecimal(brands[i].salesRate))+"%")+"</td>";
			    	tr+="<td><input type='button' onclick=showAsin('"+asin+"')  class='btn btn-warning' value='分析'/></td>";
			        tr+="</tr>";
			        tbody.append(tr);
			    }
			    var tbody1=$("#catalogTabel tbody");
			    if(siblingsCtg!=undefined){
			    	tbody1.append("<tr><th>同级目录</th><th>销售额</th><th>销量</th><th>单价</th><th>销量占比(%)</th><th>销售占比(%)</th></tr>");
			    	for(var i=0;i<siblingsCtg.length;i++){
				    	var tr="<tr>";
				    	tr+="<td>"+siblingsCtg[i].catalog+"</td>";
				    	tr+="<td>"+toDecimal(siblingsCtg[i].sales)+"</td>";
				    	tr+="<td>"+siblingsCtg[i].quantity+"</td>";
				    	tr+="<td>"+toDecimal(siblingsCtg[i].price)+"</td>";
				    	tr+="<td>"+(toDecimal(siblingsCtg[i].quantity*100/siblingsCtg[siblingsCtg.length-1].quantity))+"%</td>";
				    	tr+="<td>"+(toDecimal(siblingsCtg[i].sales*100/siblingsCtg[siblingsCtg.length-1].sales))+"%</td>";
				        tr+="</tr>";
				        tbody1.append(tr);
				    }
			    }
			    
			    $("#contentTable").dataTable({
					"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"sScrollX": "100%",
					"iDisplayLength" : 15,
					"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
							[ 15, 30, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"aaSorting": [[ 1, "desc" ]],
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"bDestroy": true,
					"ordering" : true
				});
		    }
		});   
    }
    
    
    function showAsin(asins){
    	 $("#asinTable").dataTable().fnDestroy();
    	    $("#asinTable tbody").empty();
    	   
    	    var tbody=$("#asinTable tbody");
		    if(asins!='undefined'){
		    	var arr= asins.split(",");
		    	for(var i=0;i<arr.length;i++){
			    	var tr="<tr>";
			    	var temp= arr[i].split("_");
			    	tr+="<td>"+temp[3]+"</td>";
			    	tr+="<td><a target='_blank' href='http://www.amazon.com/dp/"+temp[0]+"'>"+temp[0]+"</a></td>";
			    	tr+="<td>"+temp[2]+"</td>";
			    	tr+="<td>"+temp[1]+"</td>";
			    	tr+="<td>"+toDecimal(parseFloat(temp[2])/parseFloat(temp[1]))+"</td>";
			        tr+="</tr>";
			        tbody.append(tr);
			    }
		    }
		    $("#asinTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"sScrollX": "100%",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aaSorting": [[ 2, "desc" ]],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"bDestroy": true,
				"ordering" : true
			});
    }
    function toDecimal(x) {  
        var f = parseFloat(x);  
        if (isNaN(f)) {  
            return;  
        }  
        f = Math.round(x*100)/100;  
        return f;  
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
 		<li><a class="countryHref" >账号分析</a></li>
		<li ><a class="countryHref" href="${ctx}/amazoninfo/productCatalog/countCategory" >目录分析</a></li> 
		<li  class="active"><a class="countryHref" href="${ctx}/amazoninfo/productCatalog/categoryPathList" >目录树分析</a></li> 
	</ul>
  
	<form id="searchForm" action="${ctx}/amazoninfo/productCatalog/categoryPathList" method="post" class="breadcrumb form-search">
		<label class="control-label"><b>平台:</b></label>
		<select name="country" id="country">
			<option value="com" ${'com' eq country?'selected':''}>US</option>
		</select>
		&nbsp;&nbsp;
		<label class="control-label"><b>日期:</b></label>
		<select name="startDate" id="startDate">
			<option value="2018-03-31" ${'2018-03-31' eq startDate?'selected':''}>2018-03-31</option>
		</select>
		&nbsp;&nbsp;
		<label class='control-label'><b>目录:</b></label>
		<input  type='text'  name='catalog'  id='catalog'  style='width:120px;' value='${catalog }'/>
        <input type='button' onclick="openTree();" class="btn btn-primary"  value='目录搜索'/>
		<!-- &nbsp;&nbsp;<input id="btnSubmit" onclick="searchTypes('');" class="btn btn-primary" type="button" value="查询"/>
		 -->	
		<br/><br/>         
		&nbsp;&nbsp;<span id ='catalogInfo'></span>
		&nbsp;&nbsp;<span id ='typeInfo'></span>
		<input  type="hidden" name="siblings" id="siblings" />
		<!-- <input type='button' id='queryAll' class='btn btn-primary' value='查询'/> -->
	</form>
	
	<div>
	  <table id="catalogTabel" class="table table-striped table-bordered table-condensed">
					<!-- <thead>
						<tr>
							<th>同级目录</th>
							<th>销售额</th>
							<th>销量</th>
							<th>单价</th>
							<th>销量占比(%)</th>
							<th>销售占比(%)</th>
						</tr>
					</thead> -->
					<tbody>
					
					</tbody>
				</table>	
	</div>
	
	<div>
			  <table id="priceTable" class="table table-striped table-bordered table-condensed">
			   <thead>
						<tr>
							<th>价格</th>
							<th>销售额</th>
							<th>数量</th>
							<th>取样数</th>
							<th>品牌数</th>
							<th>销量占比(%)</th>
							<th>销售占比(%)</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
					
					</tbody>
			  </table>
	</div>  
	
			
	<div>
		<table class="table table-striped table-bordered table-condensed">
		    <tr>
		       <td width="55%">
		          
		<div >
			<p><b>品牌：</b></p>
			<div>
			  
			    <table id="contentTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th><span id='selectPrice'></span>品牌</th>
							<th>销售额</th>
							<th>销量</th>
							<th>单价</th>
							<th>销量占比(%)</th>
							<th>销售占比(%)</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
					
					</tbody>
				</table>	
			</div>
		</div>
		       
		       </td>
		       <td width="45%">
		         
		<div>
			<p><b>产品：</b></p>
			<div>
			    <table id="asinTable" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
						    <th><span id='selectbrand'></span>品牌</th>
							<th>Asin</th>
							<th>销售额</th>
							<th>销量</th>
							<th>单价</th>
						</tr>
					</thead>
					<tbody>
					
					</tbody>
				</table>	
			</div>
		</div>
		       </td>
		    </tr>
		</table>
		
	</div>
	
	<div id="selectTree" class="modal hide fade" tabindex="-1" data-width="850">

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
