<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
    <title>采购订单管理</title>
    <meta name="decorator" content="default"/>
    <%@include file="/WEB-INF/views/include/dialog.jsp" %>
    <script type="text/javascript">
    var _hmt = _hmt || [];
    (function() {
      var hm = document.createElement("script");
      hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
      var s = document.getElementsByTagName("script")[0]; 
      s.parentNode.insertBefore(hm, s);
    })();
        $(document).ready(function() {
            
            $("#isCheck").on("click",function(){
                if(this.checked){
                    $("input[name='isCheck']").val("1");
                }else{
                    $("input[name='isCheck']").val("0");
                }
                $("#searchForm").submit();
            });
            
            $("a[rel='popover']").popover({trigger:'hover'});
            
            
            
            $(".open").click(function(e){
                if($(this).text()=='概要'){
                    $(this).text('关闭');
                }else{
                    $(this).text('概要');
                }
                var className = $(this).parent().find("input[type='hidden']").val();
                $("*[name='"+className+"']").toggle();
            });
            
            $(".checkPros").click(function(e){
                var checkedStatus = this.checked;
                var name = $(this).parent().parent().find("td:last").find("input[type='hidden']").val();
                $("*[name='"+name+"'] :checkbox").each(function(){
                    this.checked = checkedStatus;
                });
            });
            
            
            
            $("#btnGen").click(function(){
                var planIds = "";
                var itemIds = "";
                var staFlag =1;
                var isChecked =false;
                var i =0;
                $(".checkPro").each(function(){
                    if(this.checked){
                        isChecked=true;
                        i++;
                        return ;
                    };
                });
                
                if(!isChecked){
                    top.$.jBox.tip("必须选中一个！", 'info',{timeout:3000});
                    return false;
                }
                
                $(":checked").parent().parent().find(".planSta").each(function(){
                    if($(this).val()!="4"&&$(this).val()!="5"){
                        staFlag=2;
                        return;
                    }
                    var td = $(this).parent();
                    var createSta = td.find(".createSta").val();
                    i=i-parseInt(createSta);
                    itemIds=itemIds+td.find(".planItemId").val()+",";
                    planIds=planIds+td.find(".planId").val()+",";
                });
                
                if(i==0){
                    top.$.jBox.tip("必须选中一项、订单生成状态为：可生成","info",{timeout:3000});
                    return false;
                }
                
                if(staFlag==2){
                    top.$.jBox.tip("只能选已终极审核、部分生成订单的申请单","info",{timeout:3000});
                    return false;
                }
                
                var params = {};
                params['planItemId'] = itemIds;
                params['planId'] = planIds;
                window.location.href = "${ctx}/psi/purchasePlan/createOrder?"+$.param(params);
            });
            
            
            
            $("#planSta,#productIdColor").change(function(){
                if($("#productIdColor").children('option:selected').val()!=""){
                    $("input[name='productName']").val($("#productIdColor").children('option:selected').text());
                }else{
                    $("input[name='productName']").val($("#productIdColor").children('option:selected').val());
                }
                $("#searchForm").submit();
            });
            
        });
        
        
        
         function toDecimal(x) {  
                var f = parseFloat(x);  
                if (isNaN(f)) {  
                    return;  
                }  
                f = Math.round(x*1000)/1000;  
                return f;  
         }  
        
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
        <li class="active"><a href="${ctx}/psi/purchasePlan/">新品采购计划</a></li>
        
    </ul>
    <form:form id="searchForm" modelAttribute="purchasePlan" action="${ctx}/psi/purchasePlan/" method="post" class="breadcrumb form-search" cssStyle="height:50px;">
        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
        <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
        <input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
        <input name="productName" type="hidden" value="${productName}"/>
        <div style="height: 50px;line-height: 40px">
            <label>产品：</label>
            <select name="productIdColor" style="width:180px" id="productIdColor">
                    <option value="">全部</option>    
                    <c:forEach items="${proColorMap}" var="proEntry">
                             <option value="${proEntry.value}" ${proEntry.key eq productName ?'selected':''}  >${proEntry.key}</option>
                    </c:forEach>    
            </select>&nbsp;&nbsp;
            <label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchasePlan.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
            &nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="reviewDate" value="<fmt:formatDate value="${purchasePlan.reviewDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
            &nbsp;&nbsp;&nbsp;&nbsp;
            <label>订单状态：</label>
            <form:select path="planSta" style="width: 120px" id="planSta">
                <option value="" >全部(非取消)</option>
                <option value="1" ${purchasePlan.planSta eq '1' ?'selected':''} >草稿</option>
                <option value="2" ${purchasePlan.planSta eq '2' ?'selected':''} >已申请</option>
                <option value="3" ${purchasePlan.planSta eq '3' ?'selected':''} >已初级审核</option>
                <option value="4" ${purchasePlan.planSta eq '4' ?'selected':''} >已终极审核</option>
                <option value="5" ${purchasePlan.planSta eq '5' ?'selected':''} >部分生成订单</option>
                <option value="6" ${purchasePlan.planSta eq '6' ?'selected':''} >已完成</option>
                <option value="8" ${purchasePlan.planSta eq '8' ?'selected':''} >已取消</option>
            </form:select>
            
            &nbsp;&nbsp;
            <label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
            <input  name="isCheck" type="hidden" value="${isCheck}"/>
                &nbsp;&nbsp;
            <input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
            &nbsp;&nbsp;
            <shiro:hasPermission name="psi:order:edit">
            <input id="btnGen" class="btn btn-success" type="button" value="生成采购订单"/>
            </shiro:hasPermission>
            </div>
            
    </form:form>
    <tags:message content="${message}"/>   
    <table id="contentTable" class="table table-bordered table-condensed">
        <thead>
            <tr><th width="5%"> <input type="checkbox" id="checkAll" /></th><th width="5%">序号</th><th>(供应商)产品</th><th>定位</th><th >申请数量</th><th >订单状态</th><th>创建人</th><th>创建时间</th><th>销售计划</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${page.list}" var="purchasePlan" varStatus="i">
        <c:forEach items="${purchasePlan.tempPlans}" var="plan" varStatus="j">
        <tr>
            <td><input type="checkbox" class="checkPros" /></td>
            <c:choose>
                <c:when test="${j.index eq'0'}">
                    <td>
                        ${plan.id}
                    </td>
                </c:when>
                <c:otherwise>
                    <td></td>
                </c:otherwise>
            </c:choose>
                
            <td>(${plan.supplierName})<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${plan.tempProductName}">${plan.tempProductName}</a></td>
            <td>${fns:getDictLabel(purchasePlan.productPosition,'product_position','')}</td>
            <td>${plan.itemsQuantity}</td>
            <c:choose>
                <c:when test="${j.index eq'0'}">
                    <td>
                            <c:if test="${plan.planSta eq '1'}"><span class="label label-important">草稿</span></c:if>
                            <c:if test="${plan.planSta eq '2'}"><span class="label "  style="background-color:#DCB5FF">已申请</span></c:if>
                            <c:if test="${plan.planSta eq '3'}"><span class="label label-warning">已初级审核</span></c:if>
                            <c:if test="${plan.planSta eq '4'}"><span class="label label-info">已终极审核</span></c:if>
                            <c:if test="${plan.planSta eq '5'}"><span class="label" style="background-color:#00E3E3">部分生成订单</span></c:if>
                            <c:if test="${plan.planSta eq '6'}"><span class="label  label-success">已完成</span></c:if>
                            <c:if test="${plan.planSta eq '8'}"><span class="label  label-inverse">已取消</span></c:if>
                    </td>
                    <td>${plan.createUser.name}</td>
                    <td><fmt:formatDate value="${plan.createDate}" pattern="yyyy-MM-dd"/> </td>
                </c:when>
                <c:otherwise>
                    <td></td><td></td><td></td>
                </c:otherwise>
            </c:choose>
            <td>
            <c:if test="${not empty purchasePlan.attFilePath}"><a href="${ctx}/psi/purchasePlan/download?fileName=/${purchasePlan.attFilePath}&productName=${purchasePlan.productName}">查看</a></c:if>
            </td>
            <td >
            <input type="hidden" value="${plan.id},${plan.tempProductName}"/>
            <a class="btn btn-small btn-info open">概要</a>
            <a class="btn btn-small"  href="${ctx}/psi/purchasePlan/view?id=${plan.id}" >详情</a>
            <c:if test="${j.index eq '0'}">
            <shiro:hasPermission name="psi:purchasePlan:edit">
                <c:if test="${plan.planSta eq '1'}">
                    <a class="btn btn-small" href="${ctx}/psi/purchasePlan/form?id=${plan.id}">编辑</a>&nbsp;&nbsp;
                    <a class="btn btn-small"  href="${ctx}/psi/purchasePlan/cancel?id=${plan.id}" onclick="return confirmx('确认要取消该计划吗？', this.href)">取消</a>
                </c:if>
            </shiro:hasPermission>
            <shiro:hasPermission name="psi:purchasePlan:review">    
                <c:if test="${plan.planSta eq '2' }">
                        <a class="btn btn-small"  href="${ctx}/psi/purchasePlan/review?id=${plan.id}">初级审核</a>
                </c:if>
            </shiro:hasPermission>  
            <shiro:hasPermission name="psi:purchasePlan:bossReview">
                <c:if test="${plan.planSta eq '3' }">
                        <a class="btn btn-small"  href="${ctx}/psi/purchasePlan/bossReview?id=${plan.id}">终极审核</a>
                </c:if>
            </shiro:hasPermission>
            </c:if>     
                
            
            </td>
        </tr>
        <c:if test="${fn:length(plan.items)>0}">
            <tr style="background-color:#ECF5FF;display: none" name="${plan.id},${plan.tempProductName}">
            <td></td><td>订单</td><td>国家</td><td>申请数量</td><td>初审数量</td><td>终极数量</td><td colspan="5">备注</td></tr>
            <c:forEach items="${plan.items}" var="item">
                <tr style="background-color:#ECF5FF;display: none" name="${plan.id},${plan.tempProductName}" >
                <td>
                <input type="hidden" class="planId" value="${plan.id}" />
                <input type="hidden" class="planItemId" value="${item.id}" />
                <input type="hidden" class="planSta" value="${plan.planSta}" />
                <input type="hidden" class="createSta" value="${item.createSta}" />
                <input type="checkbox" class="checkPro" /></td>
                <td>${item.createSta eq '1'?'已生成':(item.createSta eq '0'?'可生成':'不可生成')} </td>
                <td style="word-break: break-all; word-wrap:break-word;">${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
                <td>${item.quantity}</td>
                <td>${item.quantityReview}</td>
                <td>${item.quantityBossReview}</td>
                <td colspan="5">${item.remark}</td>
                </tr>
            </c:forEach>   
        </c:if>
        </c:forEach>
        </c:forEach>
        </tbody>
    </table>
    <div class="pagination">${page}</div>
</body>
</html>
                  