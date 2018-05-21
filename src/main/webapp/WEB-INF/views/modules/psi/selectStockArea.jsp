<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>仓库库区库位管理</title>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		var officeTree;
		var selectedTree;//zTree已选择对象
		
		// 初始化
		$(document).ready(function(){
			officeTree = $.fn.zTree.init($("#officeTree"), setting, officeNodes);
			userTree = $.fn.zTree.init($("#userTree"), setting, "");
			selectedTree = $.fn.zTree.init($("#selectedTree"), setting, selectedNodes);
			officeTree.expandAll(true);
		});
		var setting = {view: {selectedMulti:false,nameIsHTML:true,showTitle:false},
				data: {simpleData: {enable: true}},
				callback: {onClick: treeOnClick}};
		
		var officeNodes=[
	            <c:forEach items="${stocks}" var="stocks">
	            {id:"${stocks.id}",
	             pId:"0", 
	             name:"${stocks.stockName}"},
	            </c:forEach>];
	
		var pre_selectedNodes =[
   		        <c:forEach items="${role.userList}" var="user">
   		        {id:"${user.id}",
   		         pId:"0",
   		         name:"<font color='red' style='font-weight:bold;'>${user.name}</font>"},
   		        </c:forEach>];
		
		var selectedNodes =[
		        <c:forEach items="${role.userList}" var="user">
		        {id:"${user.id}",
		         pId:"0",
		         name:"<font color='red' style='font-weight:bold;'>${user.name}</font>"},
		        </c:forEach>];
		
		var pre_ids = "${selectIds}".split(",");
		var ids = "${selectIds}".split(",");
		
		//点击选择项回调
		function treeOnClick(event, treeId, treeNode, clickFlag){
			if("officeTree"==treeId){
				$.get("${ctx}/psi/stockArea/areas?officeId=" + treeNode.id, function(userNodes){
					$.fn.zTree.init($("#userTree"), setting, userNodes);
				});
			}
			if("userTree"==treeId){
				//alert(treeNode.id + " | " + ids);
				//alert(typeof ids[0] + " | " +  typeof treeNode.id);
				//alert(treeNode.id)
				$.get("${ctx}/psi/stockLocation/locations?officeId=" + treeNode.id, function(userNodes){
                    $.fn.zTree.init($("#selectedTree"), setting, userNodes);
                });
			}
            if("selectedTree"==treeId){
               
            }
		}
				
		function toAddArea(){
			var selectedNodes = officeTree.getSelectedNodes();
			if(selectedNodes == "" || selectedNodes == null || selectedNodes == undefined){
                top.$.jBox.info("新增库区前请先选择仓库！");
            }
			var id = selectedNodes["0"].id;
			var html = "<div style='padding:10px;'><form id=''>库区名称：<input type='text' id='name' name='name'/><br/><br/>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：<input type='text' id='remarks' name='remarks'  class=‘required’/></form></div>";
			var submit = function (v, h, f) {
			    if (f.name == '') {
			        // f.some 或 h.find('#some').val() 等于 top.$('#some').val()
			        top.$.jBox.tip("请输入库区名称", 'error', { focusId: "name" }); // 关闭设置 some 为焦点
			        return false;
			    }
			    var name = f.name;
			    var remarks = f.remarks;
			    $.get('${ctx}/psi/stockArea/save1?name='+name+'&remarks='+remarks+"&stockId="+id, function(result){
			    	console.log(result)
		            $.fn.zTree.getZTreeObj("userTree").addNodes( null,{id:result.areaId, pId:"0", name:result.stockName});
			    });
			    top.$.jBox.info("新增库区成功！");
			    return true;
			};

			top.$.jBox(html, { title: "新增库区", submit: submit });
			
        }
			
		function toEditArea(){
            var selectedNodes = userTree.getSelectedNodes();
            if(selectedNodes == "" || selectedNodes == null || selectedNodes == undefined){
                top.$.jBox.info("编辑库区前请先选择库区！");
            }
            var id = selectedNodes["0"].id;
            
            $.get("${ctx}/psi/stockArea/form1?id="+id, function(result){
            	var id1=result.id;
            	var name1 = result.name;
            	var remark1 = result.remarks;
            	if(remark1 == "undefined" || remark1 == null){
            		remark1="";
            	}
            	var html = "<div style='padding:10px;'>库区名称：<input type='text' id='name' value='"+name1+"' name='name'/><br/><br/>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：<input type='text' value='"+remark1+"' id='remarks' name='remarks'  class=‘required’/><input type='hidden' id='id1' value='"+id1+"' name='id1'/></div>";
                var submit = function (v, h, f) {
                    if (f.name == '') {
                        // f.some 或 h.find('#some').val() 等于 top.$('#some').val()
                        top.$.jBox.tip("请输入库区名称", 'error', { focusId: "name" }); // 关闭设置 some 为焦点
                        return false;
                    }
                    var name = f.name;
                    var remarks = f.remarks;
                    var areaid = f.id1;
                    $.get('${ctx}/psi/stockArea/save1?name='+name+'&remarks='+remarks+"&id="+areaid, function(result){
                    	var treeObj = $.fn.zTree.getZTreeObj("userTree");
                    	var nodes = treeObj.getSelectedNodes();
                    	if (nodes.length>0) {
                    	    nodes[0].name = result.stockName;
                    	    treeObj.updateNode(nodes[0]);
                    	}
                    	top.$.jBox.info("修改成功！");
                    });
                    return true;
                };
                top.$.jBox(html, { title: "编辑库区", submit: submit });
            })
            
        }
			function delArea(){
                var selectedNodes = userTree.getSelectedNodes();
                if(selectedNodes == "" || selectedNodes == null || selectedNodes == undefined){
                    top.$.jBox.info("请选择将要删除的库区！");
                }
                var id = selectedNodes["0"].id;
                var treedata=$("#selectedTree").html();
                if(treedata.length==0){
                    var submit = function (v, h, f) {
                        if (v == 'ok'){
                        	$.get("${ctx}/psi/stockArea/delete?id=" + id, function(userNodes){
                            }); 
                            //选中节点
                            $.fn.zTree.getZTreeObj("userTree").removeNode(selectedNodes["0"]);
                            top.$.jBox.info("已删除！");
                        }
                        return true; //close
                    };

                    top.$.jBox.confirm("确定删除该库区吗？", "提示", submit);
                	
                }else{
                	top.$.jBox.info("该库区下有库位，请先删除该区库位！");
                }
                   
                    
            }
		
		     function toAddLocation(){
		            var selectedNodes = userTree.getSelectedNodes();
		            if(selectedNodes == "" || selectedNodes == null || selectedNodes == undefined){
		                top.$.jBox.info("新增前请先选择库区！");
		            }
		            var id = selectedNodes["0"].id;
		            var html = "<div style='padding:10px;'><form id=''>库位名称：<input type='text' id='name' name='name'/><br/><br/>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：<input type='text' id='remarks' name='remarks'  class=‘required’/></form></div>";
		            var submit = function (v, h, f) {
		                if (f.name == '') {
		                    // f.some 或 h.find('#some').val() 等于 top.$('#some').val()
		                    top.$.jBox.tip("请输入库位名称", 'error', { focusId: "name" }); // 关闭设置 some 为焦点
		                    return false;
		                }
		                var name = f.name;
		                var remarks = f.remarks;
		                $.get('${ctx}/psi/stockLocation/save1?name='+name+'&remarks='+remarks+"&stockAreaId="+id, function(result){
		                    $.fn.zTree.getZTreeObj("selectedTree").addNodes( null,{id:result.locationId, pId:"0", name:result.locationName});
		                });
		                top.$.jBox.info("新增库位成功！");
		                return true;
		            };

		            top.$.jBox(html, { title: "新增库位", submit: submit });
		        }
		     
		     function toEditLocation(){
		            var selectedNodes = selectedTree.getSelectedNodes();
		            if(selectedNodes == "" || selectedNodes == null || selectedNodes == undefined){
		                top.$.jBox.info("编辑前请先选择库位！");
		            }
		            var id = selectedNodes["0"].id;
		            $.get("${ctx}/psi/stockLocation/form1?id="+id, function(result){
		                var id1=result.id;
		                var name1 = result.name;
		                var remark1 = result.remarks;
		                if(remark1 == "undefined" || remark1 == null){
		                    remark1="";
		                }
		                var html = "<div style='padding:10px;'>库位名称：<input type='text' id='name' value='"+name1+"' name='name'/><br/><br/>备&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;注：<input type='text' value='"+remark1+"' id='remarks' name='remarks'  class=‘required’/><input type='hidden' id='id1' value='"+id1+"' name='id1'/></div>";
		                var submit = function (v, h, f) {
		                    if (f.name == '') {
		                        // f.some 或 h.find('#some').val() 等于 top.$('#some').val()
		                        top.$.jBox.tip("请输入库位名称", 'error', { focusId: "name" }); // 关闭设置 some 为焦点
		                        return false;
		                    }
		                    var name = f.name;
		                    var remarks = f.remarks;
		                    var areaid = f.id1;
		                    $.get('${ctx}/psi/stockLocation/save1?name='+name+'&remarks='+remarks+"&id="+areaid, function(result){
		                        var treeObj = $.fn.zTree.getZTreeObj("selectedTree");
		                        var nodes = treeObj.getSelectedNodes();
		                        if (nodes.length>0) {
		                            nodes[0].name = result.locationName;
		                            treeObj.updateNode(nodes[0]);
		                        }
		                        top.$.jBox.info("修改成功！");
		                    });
		                    return true;
		                };
		                top.$.jBox(html, { title: "编辑库位", submit: submit });
		            })
		        }
		     
		     function delLocation(){
	                var selectedNodes = selectedTree.getSelectedNodes();
	                if(selectedNodes == "" || selectedNodes == null || selectedNodes == undefined){
                        top.$.jBox.info("请先选择将要删除的库位！");
                    }
	                var id = selectedNodes["0"].id;
	                
                    $.get("${ctx}/psi/stockLocation/delete1?id=" + id, function(result){
                    	console.log(result)
                    	if(result=='1'){
                    		var submit = function (v, h, f) {
                    		    if (v == 'ok'){
                    		    	//选中节点
                                    $.fn.zTree.getZTreeObj("selectedTree").removeNode(selectedNodes["0"]);
                                    top.$.jBox.info("已删除！");
                    		    }
                    		    return true; //close
                    		};
                    		top.$.jBox.confirm("确定删除该库位吗？", "提示", submit);
                    	}else{
                    		top.$.jBox.info("该库位下有货物，不能删除！");
                    	}
                    	
                    }); 
	       }
	</script>
</head>
<body>
	<div id="assignRole" class="row-fluid span12">
		<div class="span4" style="border-right: 1px solid #A8A8A8;">
			<p>仓库：</p>
			<div id="officeTree" class="ztree"></div>
		</div>
		<div class="span4">
			<p>库区:&nbsp;&nbsp;<input type='button' value='新增' onClick="toAddArea()"><input type='button' value='编辑'  onClick="toEditArea()"><input type='button' value='删除' onClick="delArea()"></p>
			<div id="userTree" class="ztree"></div>
		</div>
		<div class="span4" style="padding-left:16px;border-left: 1px solid #A8A8A8;">
			<p>库位：&nbsp;&nbsp;<input type='button' value='新增' onClick="toAddLocation()"><input type='button' value='编辑'  onClick="toEditLocation()"><input type='button' value='删除' onClick="delLocation()"></p>
			<div id="selectedTree" class="ztree"></div>
		</div>
	</div>
   
 
</body>
</html>
