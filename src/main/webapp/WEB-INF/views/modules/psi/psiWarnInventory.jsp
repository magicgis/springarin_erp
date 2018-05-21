<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product Inventory Warn</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
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
	</style>
	<script type="text/javascript">
	
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
				return $('td:eq('+iColumn+')', tr).text().replace(",","");
			} );
		}
		
		
		function fmoney(s, n){   
		   n = n > 0 && n <= 20 ? n : 2;   
		   s = parseFloat((s + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
		   var l = s.split(".")[0].split("").reverse(),   
		   r = s.split(".")[1];   
		   t = "";   
		   for(i = 0; i < l.length; i ++ )   
		   {   
		      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
		   }   
		   return t.split("").reverse().join("") + "." + r;   
		} 
		
		
		var oTable2,oTable3,oTable4,oTable5,oTable6,oTable7,oTable8,oTable9,oTable10,oTable11,oTable12,oTable13;
		$(function(){
			//fba库存提示
			var arr = $("#total table tbody tr");
			var num1 = 0;
			var num2 = 0;
			var num3 = 0;
			var num4 = 0;
			var num5 = 0;
			var num6 = 0;
			var num7 = 0;
			var num8 = 0;
			arr.each(function() {
				if($(this).find("td :eq(1)").text()&&$.isNumeric($(this).find("td :eq(1)").text().split(",").join(''))){
					num1+=parseInt($(this).find("td :eq(1)").text().split(",").join(''));
				}
				if($(this).find("td :eq(2)").text()&&$.isNumeric($(this).find("td :eq(2)").text().split(",").join(''))){
					num2+=parseInt($(this).find("td :eq(2)").text().split(",").join(''));
				}
				if($(this).find("td :eq(3)").text()&&$.isNumeric($(this).find("td :eq(3)").text().split(",").join(''))){
					num3+=parseInt($(this).find("td :eq(3)").text().split(",").join(''));
				}
				if($(this).find("td :eq(4)").text()&&$.isNumeric($(this).find("td :eq(4)").text().split(",").join(''))){
					num4+=parseInt($(this).find("td :eq(4)").text().split(",").join(''));
				}
				if($(this).find("td :eq(5)").text()&&$.isNumeric($(this).find("td :eq(5)").text().split(",").join(''))){
					num5+=parseInt($(this).find("td :eq(5)").text().split(",").join(''));
				}
				if($(this).find("td :eq(6)").text()&&$.isNumeric($(this).find("td :eq(6)").text().split(",").join(''))){
					num6+=parseInt($(this).find("td :eq(6)").text().split(",").join(''));
				}
				if($(this).find("td :eq(7)").text()&&$.isNumeric($(this).find("td :eq(7)").text().split(",").join(''))){
					num7+=parseInt($(this).find("td :eq(7)").text().split(",").join(''));
				}
				if($(this).find("td :eq(8)").text()&&$.isNumeric($(this).find("td :eq(8)").text().split(",").join(''))){
					num8+=parseInt($(this).find("td :eq(8)").text().split(",").join(''));
				}
			});	
		
			$("#totalProduct").text(num1);
			$("#totalTitle").text(num2);
			$("#totalW").text(num3);
			$("#totalT").text(num4);
			$("#totalO").text(num5);
			$("#totalF").text(num6);
			$("#totalFf").text(num7);
			$("#totalX").text(num8);
			
			arr = $("#usTotal table tbody tr");
			num1 = 0;
			num2 = 0;
			num3 = 0;
			num4 = 0;
			num5 = 0;
			num6 = 0;
			num7 = 0;
			num8 = 0;
			arr.each(function() {
				if($(this).find("td :eq(1)").text()&&$.isNumeric($(this).find("td :eq(1)").text().split(",").join(''))){
					num1+=parseInt($(this).find("td :eq(1)").text().split(",").join(''));
				}
				if($(this).find("td :eq(3)").text()&&$.isNumeric($(this).find("td :eq(3)").text().split(",").join(''))){
					num2+=parseInt($(this).find("td :eq(3)").text().split(",").join(''));
				}
				if($(this).find("td :eq(4)").text()&&$.isNumeric($(this).find("td :eq(4)").text().split(",").join(''))){
					num3+=parseInt($(this).find("td :eq(4)").text().split(",").join(''));
				}
				if($(this).find("td :eq(5)").text()&&$.isNumeric($(this).find("td :eq(5)").text().split(",").join(''))){
					num4+=parseInt($(this).find("td :eq(5)").text().split(",").join(''));
				}
				if($(this).find("td :eq(6)").text()&&$.isNumeric($(this).find("td :eq(6)").text().split(",").join(''))){
					num5+=parseInt($(this).find("td :eq(6)").text().split(",").join(''));
				}
				if($(this).find("td :eq(7)").text()&&$.isNumeric($(this).find("td :eq(7)").text().split(",").join(''))){
					num6+=parseInt($(this).find("td :eq(7)").text().split(",").join(''));
				}
				if($(this).find("td :eq(8)").text()&&$.isNumeric($(this).find("td :eq(8)").text().split(",").join(''))){
					num7+=parseInt($(this).find("td :eq(8)").text().split(",").join(''));
				}
				if($(this).find("td :eq(9)").text()&&$.isNumeric($(this).find("td :eq(9)").text().split(",").join(''))){
					num8+=parseInt($(this).find("td :eq(9)").text().split(",").join(''));
				}
			});	
			$("#usProduct").text(num1);
			$("#usTitle").text(num2);
			$("#usW").text(num3);
			$("#usT").text(num4);
			$("#usO").text(num5);
			$("#usF").text(num6);
			$("#usFf").text(num7);
			$("#usX").text(num8);
			
			
			arr = $("#jpTotal table tbody tr");
			num1 = 0;
			num2 = 0;
			num3 = 0;
			num4 = 0;
			num5 = 0;
			num6 = 0;
			num7 = 0;
			arr.each(function() {
				if($(this).find("td :eq(1)").text()&&$.isNumeric($(this).find("td :eq(1)").text().split(",").join(''))){
					num1+=parseInt($(this).find("td :eq(1)").text().split(",").join(''));
				}
				if($(this).find("td :eq(3)").text()&&$.isNumeric($(this).find("td :eq(3)").text().split(",").join(''))){
					num2+=parseInt($(this).find("td :eq(3)").text().split(",").join(''));
				}
				if($(this).find("td :eq(4)").text()&&$.isNumeric($(this).find("td :eq(4)").text().split(",").join(''))){
					num3+=parseInt($(this).find("td :eq(4)").text().split(",").join(''));
				}
				if($(this).find("td :eq(5)").text()&&$.isNumeric($(this).find("td :eq(5)").text().split(",").join(''))){
					num4+=parseInt($(this).find("td :eq(5)").text().split(",").join(''));
				}
				if($(this).find("td :eq(6)").text()&&$.isNumeric($(this).find("td :eq(6)").text().split(",").join(''))){
					num5+=parseInt($(this).find("td :eq(6)").text().split(",").join(''));
				}
				if($(this).find("td :eq(7)").text()&&$.isNumeric($(this).find("td :eq(7)").text().split(",").join(''))){
					num6+=parseInt($(this).find("td :eq(7)").text().split(",").join(''));
				}
				if($(this).find("td :eq(8)").text()&&$.isNumeric($(this).find("td :eq(8)").text().split(",").join(''))){
					num7+=parseInt($(this).find("td :eq(8)").text().split(",").join(''));
				}
				
			});	
			$("#jpProduct").text(num1);
			$("#jpTitle").text(num2);
			$("#jpW").text(num3);
			$("#jpT").text(num4);
			$("#jpF").text(num5);
			$("#jpFf").text(num6);
			$("#jpX").text(num7);
			
			var euarr = $("#euTotal table tbody tr");
			var eunum1 = 0;
			var eunum2 = 0;
			var eunum3 = 0;
			var eunum4 = 0;
			var eunum5 = 0;
			var eunum6 = 0;
			var eunum7 = 0;
			var eunum8 = 0;
			euarr.each(function() {
				if($(this).find("td :eq(1)").text()&&$.isNumeric($(this).find("td :eq(1)").text().split(",").join(''))){
					eunum1+=parseInt($(this).find("td :eq(1)").text().split(",").join(''));
				}
				if($(this).find("td :eq(3)").text()&&$.isNumeric($(this).find("td :eq(3)").text().split(",").join(''))){
					eunum2+=parseInt($(this).find("td :eq(3)").text().split(",").join(''));
				}
				if($(this).find("td :eq(4)").text()&&$.isNumeric($(this).find("td :eq(4)").text().split(",").join(''))){
					eunum3+=parseInt($(this).find("td :eq(4)").text().split(",").join(''));
				}
				if($(this).find("td :eq(5)").text()&&$.isNumeric($(this).find("td :eq(5)").text().split(",").join(''))){
					eunum4+=parseInt($(this).find("td :eq(5)").text().split(",").join(''));
				}
				if($(this).find("td :eq(6)").text()&&$.isNumeric($(this).find("td :eq(6)").text().split(",").join(''))){
					eunum5+=parseInt($(this).find("td :eq(6)").text().split(",").join(''));
				}
				if($(this).find("td :eq(7)").text()&&$.isNumeric($(this).find("td :eq(7)").text().split(",").join(''))){
					eunum6+=parseInt($(this).find("td :eq(7)").text().split(",").join(''));
				}
				if($(this).find("td :eq(8)").text()&&$.isNumeric($(this).find("td :eq(8)").text().split(",").join(''))){
					eunum7+=parseInt($(this).find("td :eq(8)").text().split(",").join(''));
				}
				if($(this).find("td :eq(9)").text()&&$.isNumeric($(this).find("td :eq(9)").text().split(",").join(''))){
					eunum8+=parseInt($(this).find("td :eq(9)").text().split(",").join(''));
				}
			});	
		
			$("#euProduct").text(eunum1);
			$("#euTitle").text(eunum2);
			$("#euW").text(eunum3);
			$("#euT").text(eunum4);
			$("#euO").text(eunum5);
			$("#euF").text(eunum6);
			$("#euFf").text(eunum7);
			$("#euX").text(eunum8);
			
			
			$("body").on("focusout",".shipping",function(){
				var bady = $(this).parent().parent().parent();
				var num = 0;
				bady.find("tr").each(function(){
					var pack = $(this).find("td:eq(2)").text();
					var shipped = $(this).find(".shipping").val();
					num+= (shipped/pack);
				});
				bady.parent().parent().parent().parent().find(".tPack").text("共计"+num+"箱");
			});
			
			$(".fbaTip").popover({html:true,trigger:'hover',content:function(){
				var td=$(this).parent();
				params={};
				params.country= td.find(".country").val();
				params.name= td.find(".name").val();
				
				var $this = $(this);
				if(!$this.attr("content")){
					if(!$this.attr("data-content")){
						var content="";
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/psi/psiInventory/fba/ajaxSkuQuantity',
						    data: $.param(params),
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
			
			$(".countryHref").click(function(e){
				e.preventDefault();
				var key = $(this).attr("key");
				if('eu'==key && !(oTable2)){
					oTable2 = $("#eu table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
						"aoColumns": [
							         null,
							         { "sSortDataType":"dom-html", "sType":"numeric" },	
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
					 	"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},
					 	"ordering":true,
					 	"aaSorting": [[28, "desc" ]],
				        "bScrollCollapse": true
					});
					
					$("#eu .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'de\');return false;\"><spring:message code="psi_inventory_details" /></span><b>Type:</b><select class="type" id="type_eu" ><option>'+options+'</option></select>');
					$("#eu .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_eu" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#eu").each( function () {
				        $('.type', this).change( function () {
				            oTable2.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_eu").click(function(){
							var params = {};
							params.type=encodeURI($('#type_eu').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_eu").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );
				} else if('de'==key && !(oTable3)){
					oTable3 = $("#de table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[18, "desc" ]]
					});
					
					$("#de .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'de\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type"  id="type_de"><option>'+options+'</option></select>');
					$("#de .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_de" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#de").each( function () {
				        $('.type', this).change( function () {
				            oTable3.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_de").click(function(){
							var params = {};
							params.type=encodeURI($('#type_de').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_de").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				       
				    });	
				} else if('fr'==key && !(oTable4)){
					oTable4 = $("#fr table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[18, "desc" ]]
					});
					
					$("#fr .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'fr\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type"  id="type_fr"><option>'+options+'</option></select>');
					$("#fr .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_fr" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#fr").each( function () {
				        $('.type', this).change( function () {
				            oTable4.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_fr").click(function(){
							var params = {};
							params.type=encodeURI($('#type_fr').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_fr").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				} else if('uk'==key && !(oTable5)){
					oTable5 = $("#uk table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[18, "desc" ]]
					});
					
					$("#uk .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'uk\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type"  id="type_uk"><option>'+options+'</option></select>');
					$("#uk .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_uk" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#uk").each( function () {
				        $('.type', this).change( function () {
				            oTable5.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_uk").click(function(){
							var params = {};
							params.type=encodeURI($('#type_uk').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_uk").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				} else if('es'==key && !(oTable6)){
					oTable6 = $("#es table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[18, "desc" ]]
					});
					
					$("#es .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'es\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_es" ><option>'+options+'</option></select>');
					$("#es .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_es" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#es").each( function () {
				        $('.type', this).change( function () {
				            oTable6.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_es").click(function(){
							var params = {};
							params.type=encodeURI($('#type_es').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_es").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				
				} else if('it'==key && !(oTable7)){
					oTable7 = $("#it table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[18, "desc" ]]
					});
					
					$("#it .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'it\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type"  id="type_it"><option>'+options+'</option></select>');
					$("#it .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_it" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#it").each( function () {
				        $('.type', this).change( function () {
				            oTable7.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_it").click(function(){
							var params = {};
							params.type=encodeURI($('#type_it').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_it").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				} else if('ca'==key && !(oTable8)){
					oTable8 = $("#ca table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[16, "desc" ]]
					});
					
					$("#ca .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'ca\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_ca" ><option>'+options+'</option></select>');
					$("#ca .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_ca" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#ca").each( function () {
				        $('.type', this).change( function () {
				            oTable8.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_ca").click(function(){
							var params = {};
							params.type=encodeURI($('#type_ca').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_ca").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				} else if('jp'==key && !(oTable9)){
					oTable9 = $("#jp table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
									  null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[16, "desc" ]]
					});
					
					$("#jp .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'jp\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_jp" ><option>'+options+'</option></select>');
					$("#jp .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_jp" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#jp").each( function () {
				        $('.type', this).change( function () {
				            oTable9.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_jp").click(function(){
							var params = {};
							params.type=encodeURI($('#type_jp').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_jp").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				} else if('com'==key && !(oTable10)){
					oTable10 = $("#com table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[16, "desc" ]]
					});
					
					$("#com .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'com\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_com" ><option>'+options+'</option></select>');
					$("#com .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_com" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#com").each( function () {
				        $('.type', this).change( function () {
				            oTable10.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_com").click(function(){
							var params = {};
							params.type=encodeURI($('#type_com').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_com").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				} else if('mx'==key && !(oTable11)){
					oTable11 = $("#mx table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[16, "desc" ]]
					});
					
					$("#mx .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'mx\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_mx" ><option>'+options+'</option></select>');
					$("#mx .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_com" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#mx").each( function () {
				        $('.type', this).change( function () {
				            oTable11.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_mx").click(function(){
							var params = {};
							params.type=encodeURI($('#type_mx').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_mx").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				}else if('com2'==key && !(oTable12)){
					oTable12 = $("#com2 table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[16, "desc" ]]
					});
					
					$("#com2 .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'com2\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_com2" ><option>'+options+'</option></select>');
					$("#com2 .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_com2" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#com2").each( function () {
				        $('.type', this).change( function () {
				            oTable10.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_com2").click(function(){
							var params = {};
							params.type=encodeURI($('#type_com2').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_com2").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				}else if('com3'==key && !(oTable13)){
					oTable13 = $("#com3 table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap",
						"iDisplayLength": 12,
						"aoColumns": [
							          null,
							          { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      null,
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" },
								      { "sSortDataType":"dom-html", "sType":"numeric" }
								     ],
						"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
					 	, "aaSorting": [[16, "desc" ]]
					});
					
					$("#com3 .spanexr div:first").append('<span style=\"height:14px; font-size:12px; line-height:12px;\" class=\"btn btn-warning\" onclick=\"display(\'com3\');return false;\"><spring:message code="psi_inventory_details" /></span> <b>Type:</b><select class="type" id="type_com3" ><option>'+options+'</option></select>');
					$("#com3 .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_com3" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
					$("#com3").each( function () {
				        $('.type', this).change( function () {
				            oTable10.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
				        } );
				        $("#export_com3").click(function(){
							var params = {};
							params.type=encodeURI($('#type_com3').val());
							params.country = $("input[name='selectCountry']").val();
							$("#export_com3").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
					     });
				    } );	
				}
				 $(this).tab('show');	
				 $("input[name='selectCountry']").val(key);
			});
			$("th,td[class!=name]").css("vertical-align","middle").css("text-align","center");
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			var options = "";
			//<c:forEach items="${fns:getDictList('product_type')}" var="dic">
				options +="<option value='${dic.label}' >${dic.label}</option>";
			//</c:forEach>
			
			//加入库占比
			var totalPrice = parseFloat($("#totalPrice").text().replace(/,/g,""));
			if(totalPrice){
				$("#total table tbody tr").each(function(){
					var tr = $(this);
					var price = tr.find(".price").text();
					if(price){
						tr.find(".libraryAccounting").text((parseFloat(price.replace(/,/g,""))*100/totalPrice).toFixed(2));						
					}
				});
			}
			
			var totalPrice1 = parseFloat($("#euTotalPrice").text().replace(/,/g,""));
			if(totalPrice1){
				$("#euTotal table tbody tr").each(function(){
					var tr = $(this);
					var price = tr.find(".price").text();
					if(price){
						tr.find(".libraryAccounting").text((parseFloat(price.replace(/,/g,""))*100/totalPrice1).toFixed(2));						
					}
				});
			}
			
			var totalPrice2 = parseFloat($("#usTotalPrice").text().replace(/,/g,""));
			if(totalPrice2){
				$("#usTotal table tbody tr").each(function(){
					var tr = $(this);
					var price = tr.find(".price").text();
					if(price){
						tr.find(".libraryAccounting").text((parseFloat(price.replace(/,/g,""))*100/totalPrice2).toFixed(2));						
					}
				});
			}
			
			var totalPrice3 = parseFloat($("#jpTotalPrice").text().replace(/,/g,""));
			if(totalPrice3){
				$("#jpTotal table tbody tr").each(function(){
					var tr = $(this);
					var price = tr.find(".price").text();
					if(price){
						tr.find(".libraryAccounting").text((parseFloat(price.replace(/,/g,""))*100/totalPrice3).toFixed(2));						
					}
				});
			}
			
			
			
			var oTable1 = $("#total table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 12,
				"aoColumns": [
					          null,
					          { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null
						      //<shiro:hasPermission name="psi:inventory:stockPriceView">
						      ,null,
						      null,
						      { "sSortDataType":"dom-html1", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:hasPermission>
						       //<shiro:lacksPermission name="psi:inventory:stockPriceView">
						      	 ,{ "sSortDataType":"dom-html", "sType":"numeric" },
						      	 { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:lacksPermission>
				],
				"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[ 12, "desc" ]]
			 	,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             if(iDisplayIndex==0){
		            	 addd=0;
		            	 totalPAdd=0;
		            	 totalTAdd=0;
		            	 totalW=0;
		            	 totalT=0;
		            	 totalO=0;
		            	 totalF=0;
		            	 totalF2=0;
		            	 totalX=0;
		              }
		             <shiro:hasPermission name="psi:inventory:stockPriceView">
			             addd+=parseFloat(aData[18].replace(',',''));//第几列
			             $("#totalPrice1").html(fmoney(addd,2));
			             $("#pcent").html((addd/totalPrice*100).toFixed(2));
		             </shiro:hasPermission>
		             if($.isNumeric($(aData[1]).text().split(",").join(''))){
		            	 totalPAdd+=parseInt($(aData[1]).text().split(",").join(''));
		             }
					 $("#totalProduct1").html(totalPAdd);
		             

					 if($.isNumeric($(aData[2]).text().split(",").join(''))){
		            	 totalTAdd+=parseInt($(aData[2]).text().split(",").join(''));
		             }
					 $("#totalTitle1").html(totalTAdd);
					 
					 if($.isNumeric($(aData[3]).text().split(",").join(''))){
		            	 totalW+=parseInt($(aData[3]).text().split(",").join(''));
		             }
					 $("#totalW1").html(totalW);
					 
					 if($.isNumeric($(aData[4]).text().split(",").join(''))){
		            	 totalT+=parseInt($(aData[4]).text().split(",").join(''));
		             }
					 $("#totalT1").html(totalT);
					 
					 if($.isNumeric($(aData[5]).text().split(",").join(''))){
			            	 totalO+=parseInt($(aData[5]).text().split(",").join(''));
			         }
					 $("#totalO1").html(totalO);
					 
					 if($.isNumeric($(aData[6]).text().split(",").join(''))){
		            	 totalF+=parseInt($(aData[6]).text().split(",").join(''));
		             }
				     $("#totalF1").html(totalF);
					 
				     if($.isNumeric($(aData[7]).text().split(",").join(''))){
				    	 totalF2+=parseInt($(aData[7]).text().split(",").join(''));
		             }
					 $("#totalF2").html(totalF2);
					 
					 
				     if(aData[8]!=null&&aData[8]!=''){
				    	 totalX+=parseInt(aData[8]);
				     }
				     $("#totalX1").html(totalX);
				     
		             return nRow;
		          }, "fnCookieCallback": function (sName, oData, sExpires, sPath){ 
		        	console.log($(oData[1]).text().split(",").join(''));  
		          }
			 	 
			});
			$("#total .spanexr div:first").append('<b>Type:</b><select class="type" id="type_total" ><option>'+options+'</option></select>&nbsp;&nbsp;');
		
			
			var oTable = $("#euTotal table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 12,
				"aoColumns": [
					          null,
					          { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" }
						      //<shiro:hasPermission name="psi:inventory:stockPriceView">
						      ,null,
						      null,
						      { "sSortDataType":"dom-html1", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:hasPermission>
						       //<shiro:lacksPermission name="psi:inventory:stockPriceView">
						      	 ,{ "sSortDataType":"dom-html", "sType":"numeric" },
						      	 { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:lacksPermission>
						      ,null,null,null,null
				],
				"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[ 13, "desc" ]]
			 	,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		            
		             if(iDisplayIndex==0){
		            	 addd=0;
		            	 totalPAdd=0;
		            	 totalTAdd=0;
		            	 totalW=0;
		            	 totalT=0;
		            	 totalO=0;
		            	 totalF=0;
		            	 totalF2=0;
		            	 totalX=0;
		              }
		             <shiro:hasPermission name="psi:inventory:stockPriceView">
			             addd+=parseFloat(aData[18].replace(',',''));//第几列
			             $("#eutotalPrice1").html(fmoney(addd,2));
			             $("#euPcent").html((addd/totalPrice*100).toFixed(2));
		             </shiro:hasPermission>
		             if($.isNumeric($(aData[1]).text().split(",").join(''))){
		            	 totalPAdd+=parseInt($(aData[1]).text().split(",").join(''));
		             }
					 $("#euProduct1").html(totalPAdd);
		             

					 if($.isNumeric($(aData[3]).text().split(",").join(''))){
		            	 totalTAdd+=parseInt($(aData[3]).text().split(",").join(''));
		             }
					 $("#euTitle1").html(totalTAdd);
					 
					 if($.isNumeric($(aData[4]).text().split(",").join(''))){
		            	 totalW+=parseInt($(aData[4]).text().split(",").join(''));
		             }
					 $("#euW1").html(totalW);
					 
					 if($.isNumeric($(aData[5]).text().split(",").join(''))){
		            	 totalT+=parseInt($(aData[5]).text().split(",").join(''));
		             }
					 $("#euT1").html(totalT);
					 
					 if($.isNumeric($(aData[6]).text().split(",").join(''))){
			            	 totalO+=parseInt($(aData[6]).text().split(",").join(''));
			         }
					 $("#euO1").html(totalO);
					 
					 if($.isNumeric($(aData[7]).text().split(",").join(''))){
		            	 totalF+=parseInt($(aData[7]).text().split(",").join(''));
		             }
				     $("#euF1").html(totalF);
					 
				     if($.isNumeric($(aData[8]).text().split(",").join(''))){
				    	 totalF2+=parseInt($(aData[8]).text().split(",").join(''));
		             }
					 $("#euF2").html(totalF2);
					 
					 
				     if(aData[8]!=null&&aData[9]!=''){
				    	 totalX+=parseInt(aData[9]);
				     }
				     $("#euX1").html(totalX);
				     
		             return nRow;
		          } 
			});
			$("#euTotal .spanexr div:first").append(' <b>Type:</b><select class="type" id="type_euTotal" ><option>'+options+'</option></select>&nbsp;&nbsp;');
			
			var oTable_us = $("#usTotal table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 12,
				"aoColumns": [
					          null,
					          { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null
						      //<shiro:hasPermission name="psi:inventory:stockPriceView">
						      ,null,
						      null,
						      { "sSortDataType":"dom-html1", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:hasPermission>
						      //<shiro:lacksPermission name="psi:inventory:stockPriceView">
						      	 ,{ "sSortDataType":"dom-html", "sType":"numeric" },
						      	 { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:lacksPermission>
						      	,null,null,null,null
				],
				"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[ 13, "desc" ]]

			 	,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             
		             if(iDisplayIndex==0){
		            	 addd=0;
		            	 totalPAdd=0;
		            	 totalTAdd=0;
		            	 totalW=0;
		            	 totalT=0;
		            	 totalO=0;
		            	 totalF=0;
		            	 totalF2=0;
		            	 totalX=0;
		              }
		             <shiro:hasPermission name="psi:inventory:stockPriceView">
			             addd+=parseFloat(aData[18].replace(',',''));//第几列
			             $("#usTotalPrice1").html(fmoney(addd,2));
			             $("#usPcent").html((addd/totalPrice*100).toFixed(2));
		             </shiro:hasPermission>
		             
		             if($.isNumeric($(aData[1]).text().split(",").join(''))){
		            	 totalPAdd+=parseInt($(aData[1]).text().split(",").join(''));
		             }
					 $("#usProduct1").html(totalPAdd);
		             

					 if($.isNumeric($(aData[3]).text().split(",").join(''))){
		            	 totalTAdd+=parseInt($(aData[3]).text().split(",").join(''));
		             }
					 $("#usTitle1").html(totalTAdd);
					 
					 if($.isNumeric($(aData[4]).text().split(",").join(''))){
		            	 totalW+=parseInt($(aData[4]).text().split(",").join(''));
		             }
					 $("#usW1").html(totalW);
					 
					 if($.isNumeric($(aData[5]).text().split(",").join(''))){
		            	 totalT+=parseInt($(aData[5]).text().split(",").join(''));
		             }
					 $("#usT1").html(totalT);
					 
					 if($.isNumeric($(aData[6]).text().split(",").join(''))){
			            	 totalO+=parseInt($(aData[6]).text().split(",").join(''));
			         }
					 $("#usO1").html(totalO);
					 
					 if(aData[7]!=null&&aData[7]!=''){
		            	 totalF+=parseInt(aData[7]);
		             }
				     $("#usF1").html(totalF);
					 
				     if($.isNumeric($(aData[8]).text().split(",").join(''))){
				    	 totalF2+=parseInt($(aData[8]).text().split(",").join(''));
		             }
					 $("#usF2").html(totalF2);
					 
					 
				     if(aData[9]!=null&&aData[9]!=''){
				    	 totalX+=parseInt(aData[9]);
				     }
				     $("#usX1").html(totalX);
				     
				     
		             return nRow;
		          } 
		
			});
			$("#usTotal .spanexr div:first").append('<b>Type:</b><select class="type" id="type_usTotal" ><option>'+options+'</option></select>&nbsp;&nbsp;');
			
			
			var oTable_jp = $("#jpTotal table").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
				"iDisplayLength": 12,
				"aoColumns": [
					          null,
					          { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null,
						      { "sSortDataType":"dom-html", "sType":"numeric" },
						      null,
						      null,
						      null
						      //<shiro:hasPermission name="psi:inventory:stockPriceView">
						      ,null,
						      null,
						      { "sSortDataType":"dom-html1", "sType":"numeric" },
						      { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:hasPermission>
						       //<shiro:lacksPermission name="psi:inventory:stockPriceView">
						      	 ,{ "sSortDataType":"dom-html", "sType":"numeric" },
						      	 { "sSortDataType":"dom-html", "sType":"numeric" }
						      //</shiro:lacksPermission>
						      	,null,null,null,null
				],
				"aLengthMenu":[[10, 12, 60,100,-1], [10, 12, 60, 100, "All"]],
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			     "aaSorting": [[ 13, "desc" ]]
			 	,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             
		             if(iDisplayIndex==0){
		            	 addd=0;
		            	 totalPAdd=0;
		            	 totalTAdd=0;
		            	 totalW=0;
		            	 totalT=0;
		            	 totalO=0;
		            	 totalF=0;
		            	 totalF2=0;
		            	 totalX=0;
		              }
		             <shiro:hasPermission name="psi:inventory:stockPriceView">
			             addd+=parseFloat(aData[18].replace(',',''));//第几列
			             $("#jpTotalPrice1").html(fmoney(addd,2));
			             $("#jpPcent").html((addd/totalPrice3*100).toFixed(2));
		             </shiro:hasPermission>
		             if($.isNumeric($(aData[1]).text().split(",").join(''))){
		            	 totalPAdd+=parseInt($(aData[1]).text().split(",").join(''));
		             }
					 $("#jpProduct1").html(totalPAdd);
		             

					 if($.isNumeric($(aData[3]).text().split(",").join(''))){
		            	 totalTAdd+=parseInt($(aData[3]).text().split(",").join(''));
		             }
					 $("#jpTitle1").html(totalTAdd);
					 
					 if($.isNumeric($(aData[4]).text().split(",").join(''))){
		            	 totalW+=parseInt($(aData[4]).text().split(",").join(''));
		             }
					 $("#jpW1").html(totalW);
					 
					 if($.isNumeric($(aData[5]).text().split(",").join(''))){
		            	 totalT+=parseInt($(aData[5]).text().split(",").join(''));
		             }
					 $("#jpT1").html(totalT);
					 
					
					 if(aData[6]!=null&&aData[6]!=''){
		            	 totalF+=parseInt(aData[6]);
		             }
				     $("#jpF1").html(totalF);
					 
				     if($.isNumeric($(aData[7]).text().split(",").join(''))){
				    	 totalF2+=parseInt($(aData[7]).text().split(",").join(''));
		             }
					 $("#jpF2").html(totalF2);
					 
					 
				     if(aData[8]!=null&&aData[8]!=''){
				    	 totalX+=parseInt(aData[8]);
				     }
				     $("#jpX1").html(totalX);
		             return nRow;
		          }
			});
			$("#jpTotal .spanexr div:first").append('<b>Type:</b><select class="type" id="type_jpTotal" ><option>'+options+'</option></select>&nbsp;&nbsp;');
			
			//<spring:message code="sys_but_export" />
			/* 	$("#total .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export" ><spring:message code="sys_but_export" /></a>&nbsp;&nbsp; ');
			$("#total .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="exportSale" ><spring:message code="sys_but_export" />(销售)</a>&nbsp;&nbsp; '); */
			var cnt='<div class="btn-group"><button type="button" class="btn btn-primary"><spring:message code="sys_but_export"/></button> '+
			'<button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">'+
			'<span class="caret"></span><span class="sr-only"></span></button><ul class="dropdown-menu"> '+
				'<li><a id="exportSale"><spring:message code="psi_inventory_export1"/></a></li> '+
				'<li><a id="export"><spring:message code="psi_inventory_export2"/></a></li> '+
				<shiro:hasPermission name="psi:warnInventory:exportHisEu">
				'<li><a id="euExport"><spring:message code="psi_inventory_export3"/></a></li> '+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisJp">
				'<li><a id="jpExport"><spring:message code="psi_inventory_export4"/></a></li> '+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisUs">
				'<li><a id="usExport"><spring:message code="psi_inventory_export5"/></a></li> '+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisCa">
				'<li><a id="caExport"><spring:message code="psi_inventory_export6"/></a></li> '+
				</shiro:hasPermission>
				<%--'<li><a id="mxExport"><spring:message code="psi_inventory_export7"/></a></li> '+--%>
				<shiro:hasPermission name="psi:warnInventory:exportHisEu">
				'<li><a id="euOverseaExport" key="eu" class="overseaExport"><spring:message code="psi_oversea_export1"/></a></li> '+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisUs">
				'<li><a id="amOverseaExport" key="com" class="overseaExport"><spring:message code="psi_oversea_export2"/></a></li> '+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisJp">
				'<li><a id="jpOverseaExport" key="jp" class="overseaExport"><spring:message code="psi_oversea_export3"/></a></li> '+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisCa">
				'<li><a id="amOverseaExport" key="ca" class="overseaExport"><spring:message code="psi_oversea_export4"/></a></li> '+
				</shiro:hasPermission>
			'</ul></div>&nbsp;&nbsp;';
			$("#total .spanexr  div:first ").append(cnt);
			<shiro:hasAnyPermissions  name="psi:warnInventory:exportHisEu,psi:warnInventory:exportHisUs,psi:warnInventory:exportHisCa,psi:warnInventory:exportHisJp">
				var cnt1='<div class="btn-group"><button type="button" class="btn btn-primary"><spring:message code="psi_inventory_exportHis"/></button> '+
				'<button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">'+
				'<span class="caret"></span><span class="sr-only"></span></button><ul class="dropdown-menu"> '+
				<shiro:hasPermission name="psi:warnInventory:exportHisEu">
					'<li><a id="euHisExport" key="eu" class="hisExport"><spring:message code="psi_inventory_hisExport3"/></a></li>'+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisUs">
				'<li><a id="usHisExport" key="us" class="hisExport"><spring:message code="psi_inventory_hisExport5"/></a></li>'+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisCa">
				'<li><a id="caHisExport" key="ca" class="hisExport"><spring:message code="psi_inventory_hisExport6"/></a></li>'+
				</shiro:hasPermission>
				<shiro:hasPermission name="psi:warnInventory:exportHisjP">
				'<li><a id="jpHisExport" key="jp" class="hisExport"><spring:message code="psi_inventory_hisExport4"/></a></li>'+
				</shiro:hasPermission>
				'</ul></div>&nbsp;&nbsp;';
				$("#total .spanexr  div:first ").append(cnt1);
				//$("#total .spanexr  div:first ").append('&nbsp;&nbsp;<a class="btn btn-primary"  id="export_his" ><spring:message code="psi_inventory_exportHis" /></a>&nbsp;&nbsp; ');
			</shiro:hasAnyPermissions>
			$(".hisExport").click(function(){
				var country = $(this).attr("key");
				top.$.jBox.confirm("<input style='width: 160px' readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyyMM',maxDate:'%y-{%M-1}-%d'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var month = h.find("input").val();
						  	if(month){
								var params = {};
								params.month=month;
								params.country=country;
								window.location.href = "${ctx}/psi/psiInventory/exportHisInventory?"+$.param(params);
						  	}else{
						  		top.$.jBox.tip("Please select export month", 'info',{timeout:1500});
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			$(".overseaExport").click(function(){
				var country = $(this).attr("key");
				var params = {};
				params.country=country;
				window.location.href = "${ctx}/psi/psiInventory/inventoryOverseaExport?"+$.param(params);
			});
			
			$("#export_his").click(function(){
				top.$.jBox.confirm("<input style='width: 160px' readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyyMM'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var month = h.find("input").val();
						  	if(month){
								var params = {};
								params.month=month;
								window.location.href = "${ctx}/psi/psiInventory/exportHisInventory?"+$.param(params);
						  	}else{
						  		top.$.jBox.tip("Please select export month", 'info',{timeout:1500});
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
			
			$("#euTotal").each( function () {
				$('.type', this).change( function () {
		        	oTable.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
		        } );
			} );
			
			
			$("#usTotal").each( function () {
				$('.type', this).change( function () {
		        	oTable_us.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
		        } );
			} );
			
			$("#jpTotal").each( function () {
				$('.type', this).change( function () {
		        	oTable_jp.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
		        } );
			} );
			
			
			$("#total").each( function () {
		        
		        
		        $('.type', this).change( function () {
		            oTable1.fnFilter($(this).val().replace("&",''),null,true,true,false,true);
		        } );
		        
		        $('#export').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = $("input[name='selectCountry']").val();
					$("#export").attr("href","${ctx}/psi/psiInventory/inventoryWarnExport?"+$.param(params));
			     });
		        $('#exportSale').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = $("input[name='selectCountry']").val();
					$("#exportSale").attr("href","${ctx}/psi/psiInventory/inventoryWarnSaleExport?"+$.param(params));
			     });
		        $('#euExport').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = 'eu';
					$("#euExport").attr("href","${ctx}/psi/psiInventory/inventoryPriceExport?"+$.param(params));
			     });
		        $('#jpExport').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = 'jp';
					$("#jpExport").attr("href","${ctx}/psi/psiInventory/inventoryPriceExport?"+$.param(params));
			     });
		        $('#usExport').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = 'com';
					$("#usExport").attr("href","${ctx}/psi/psiInventory/inventoryPriceExport?"+$.param(params));
			     });
		        $('#caExport').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = 'ca';
					$("#caExport").attr("href","${ctx}/psi/psiInventory/inventoryPriceExport?"+$.param(params));
			     });
		        $('#mxExport').click(function(){
					var params = {};
					params.type=encodeURI($('#type_total').val());
					params.country = 'mx';
					$("#mxExport").attr("href","${ctx}/psi/psiInventory/inventoryPriceExport?"+$.param(params));
			     });
		    } );
			
			var valid = false;
			
			$("#addFbaF_de").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var skuStr ="";
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							sku = $(this).find("select[name='sku']").val();
							flag = true;
						}
						skuStr=skuStr+$(this).find("select[name='sku']").val()+","+(parseInt($(this).find(".canUsed").text())-parseInt(number))+";";
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					var valiStr="";
					//判断库存预留值
					if(stock=='DE'){
						$.ajax({
						    type: 'post',
						    async:false,
						    url: '${ctx}/psi/psiInventory/ajaxResidue' ,
						    data: {
						    	"valiStr":skuStr
						    },
						    dataType: 'text',
						    success:function(data){
						    		valiStr=data;
					        }
						});
						
					}
					
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					if(valiStr!=''){
						top.$.jBox.confirm(valiStr,'库存预留值不足是否确认补货',function(v,h,f){
						if(v=='ok'){
							$(form).find("tbody tr").each(function(i,j){
								$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
								$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
								$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
							});
							loading('Please wait a moment!');
							form.submit();
						}
						return true;
						},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}else{
						$(form).find("tbody tr").each(function(i,j){
							$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
							$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
							$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
						});
						loading('Please wait a moment!');
						form.submit();
					}
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					};
				}
			});
			
			
			$("#addFbaF_fr").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			
			$("#addFbaF_es").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_it").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_uk").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_jp").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			
			$("#addFbaF_ca").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_mx").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_com").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_com2").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$("#addFbaF_com3").validate({
				submitHandler: function(form){
					if($(form).find("tbody tr").size()==0){
						return false;
					}
					var flag = false;
					var flag1 = true;
					var stock="" ;
					var sku = "";
					$(form).find("tbody tr").each(function(i,j){
						var number = $(this).find("input[name='quantityShipped']").val();
						if(parseInt(number) >parseInt($(this).find(".canUsed").text())){
							flag = true;
							sku = $(this).find("select[name='sku']").val();
						}
						if(!(stock)){
							stock = $(this).find("select[name='stockCode']").val();
						}else{
							flag1 = flag1&&($(this).find("select[name='stockCode']").val()==stock);
							stock = $(this).find("select[name='stockCode']").val();
						}
					});
					if(valid && flag&& stock != 'CN'){
						$.jBox.tip(sku+"发货量不能大于库存值!!", 'error',{timeout:2000});	
						return false;
					}
					if(!flag1){
						$.jBox.tip("不能由两个仓库补货!!", 'error',{timeout:2000});	
						return false;
					}
					if($(form).find("select[name='accountName']").val()==''){
						$.jBox.tip("账号不能为空!!", 'error',{timeout:2000});	
						return false;
					}
					if(!($(form).find(".plan").attr("checked"))){
						$(form).find(".planHid").val("WORKING");
					}else{
						$(form).find(".planHid").val("");
					}
					
					$(form).find("tbody tr").each(function(i,j){
						$(this).find("select[name='sku']").attr("name","items"+"["+i+"]."+$(this).find("select").attr("name"));
						$(this).find("input[name='quantityShipped']").attr("name","items"+"["+i+"]."+$(this).find("input[name='quantityShipped']").attr("name"));
						$(this).find("select[name='stockCode']").attr("name","items"+"["+i+"]."+$(this).find("select[name='stockCode']").attr("name"));
					});
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
					};
				}
			});
			
			$(".pagination a").addClass("nava");
			
			
		
		});
		
		function delitem(country,btn){
			$(btn).parent().parent().remove();
		}

		function displayinboud (country,name){
			var flag = false;
			var ptr;
			$("#createinboud_"+country+" tbody").find("tr").each(function(){
				if(name==$(this).find("td:eq(0)").text()){
					ptr = $(this);
					flag=true;
				}
			});
			if(flag){
				ptr.remove();
				$("#createinboud_"+country+" tbody").prepend(ptr);
				$("#createinboud_"+country).modal();
				return ;
			}
			var opt = "";
			var stockCode = "";
			if('jp'.indexOf(country)>=0){
				stockCode = 'JP';
				opt = "<option value='JP'><spring:message code='psi_inventory_JP_stock'/></option><option value='CN'><spring:message code='psi_inventory_cn_stock'/></option>";
			}else if ('es,fr,de,uk,it'.indexOf(country)>=0){
				stockCode = 'DE';
				opt = "<option value='DE'><spring:message code='psi_inventory_DE_stock'/></option><option value='CN'><spring:message code='psi_inventory_cn_stock'/></option>";
			}else if ('com,ca,mx,com2,com3'.indexOf(country)>=0){
				stockCode = 'CN';
				opt = "<option value='CN'><spring:message code='psi_inventory_cn_stock'/></option><option value='US'><spring:message code='psi_inventory_US_stock'/></option>";
			}
			
			var tr ="<tr><td style='width:150px;vertical-align: middle;text-align: center;'>"+name+"</td><td style='vertical-align: middle;text-align: center;line-height:30px'><select style='width:150px;margin:0px' name='sku' class='skus required' onchange=\"changeShipped(this,'"+country+"')\"></select></td><td class='pack' style='vertical-align: middle;text-align: center;line-height:30px'></td><td style='vertical-align: middle;text-align: center;line-height:30px'><select class='stocks' name='stockCode' onchange=\"changeShipped(this,'"+country+"')\" style='width:100px;margin:0px'>"
				+opt+"</select></td><td style='vertical-align: middle;text-align: center;' class='canUsed'></td><td style='vertical-align: middle;text-align: center;' class='tran'></td><td style='vertical-align: middle;text-align: center;line-height:30px'><input type='text' name='quantityShipped' style='width:60px;margin:0px' class='shipping required number' /></td><td style='vertical-align: middle;text-align: center;width:60px'><a href='#' onclick=\"delitem('"
				+country+"',this);return false;\"><div class='btn btn-warning' style='height:14px; font-size:12px; line-height:12px;'><spring:message code='sys_but_delete'/></div></a></td></tr>";
			var $tr =$(tr);
			$("#createinboud_"+country+" tbody").prepend($tr);
			$("#createinboud_"+country).modal();
			var param = {};
			param.stockCode = stockCode;
			param.country = country;
			param.productName = name;
			$.get("${ctx}/psi/psiInventory/getShipddQuantityAvailable?"+$.param(param),function(data){
				
				eval(" var data = "+data);
				var i = 0;
				for ( var sku in data) {
					if(i==0){
						$tr.find(".skus").append("<option selected value='"+sku+"'>"+sku+"</option>");
						i++;
					}else{
						$tr.find(".skus").append("<option value='"+sku+"'>"+sku+"</option>");
					}
					$tr.find(".canUsed").text(data[sku].quantity);
					$tr.find(".tran").text(data[sku].renewQuantity);
					$tr.find(".pack").text(data[sku].brokenQuantity);
				}
			});			
		}
		
		function display(country){
			$("#createinboud_"+country).modal();
		}
		
		function changeShipped (select,country){
			var tr =$(select).parent().parent();
			var param = {};
			param.stockCode = tr.find(".stocks").val();
			param.sku =  tr.find(".skus").val();
			param.country = country;
			$.get("${ctx}/psi/psiInventory/getShipddQuantityAvailableBySku?"+$.param(param),function(data){
				eval(" var data = "+data);
				tr.find(".canUsed").text(data.quantity);
				tr.find(".tran").text(data.renewQuantity);
			});	
		} 
		
		function display_by(type,productName,country){
			var tip= "";
			if(type==1){
				tip = $("#tranTip");
				tip.find("h3").text(productName+" <spring:message code='psi_inventory_in_transit_list'/>");
			}else if(type ==2 ){
				tip = $("#fbaTran");
				tip.find("h3").text(productName+" FBA <spring:message code='psi_inventory_in_transit_list'/>");
			}else if(type ==3 ){
				tip = $("#produceTip");
				tip.find("h3").text(productName+" <spring:message code='psi_inventory_production_list'/>");
				//查出改进信息
				$.get("${ctx}/psi/productImprovement/getTips?name="+productName,function(data){
					$("#productImprove").css("display","none");
					if(data!=''){
						$("#productImprove").css("display","block");
						$("#productImprove").html(data);
					}
				});
			}else if(type ==4 ){
				tip = $("#preTranTip");
				tip.find("h3").text(productName+" <spring:message code='psi_inventory_list_of_pending_shipments'/>");
			}else if(type ==5 ){
				tip = $("#prewkTip");
				tip.find("h3").text(productName+" FBA Wait For Delivery");
			}
			var param = {};
			param.type = type;
			param.country = country;
			param.name = productName;
			tip.find("tbody").html("");
			$.get("${ctx}/psi/psiInventory/getTipInfo?"+$.param(param),function(data){
				eval(" var data = "+data);
				var body="";
				if(type==1){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var linkUrl='${ctx}/psi/psiTransportOrder/view?transportNo='+ele.billNo;
						if(ele.billNo.indexOf("_LC")>0){
							linkUrl='${ctx}/psi/lcPsiTransportOrder/view?transportNo='+ele.billNo;
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"
							+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.sku+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.toCountry+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.remark+"</td></tr>";
					}	
				}else if(type ==2 ){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						body +="<tr><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentName+"</td><td style='vertical-align: middle;text-align: center;'><a target='_blank' href='${ctx}/psi/fbaInbound?shipmentId="+ele.shipmentId+"&country=' >"+ele.shipmentId+"</a></td><td style='vertical-align: middle;text-align: center;'>"
						+ele.sku+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityShipped+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityReceived+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentStatus+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.pickUpDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.toDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.remark+"</td></tr>";
					}	
				}else if(type ==3 ){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						if(ele.orderDate==ele.deliveryDate){
							ele.deliveryDate = '';
						}
						var linkUrl='${ctx}/psi/purchaseOrder/view?orderNo='+ele.billNo;
						if(ele.billNo.indexOf("_LC")>0){
							linkUrl='${ctx}/psi/lcPurchaseOrder/view?orderNo='+ele.billNo;
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;'>"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityOffline+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.orderDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.deliveryDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.tranWeek+"</td><td style='vertical-align: middle;text-align: center;'>"
						+ele.remark+"</td></tr>";
					}	
				}else if(type==4){
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						var country = ele.country;
						if('com'==country){
							country = 'us';
						}
						var linkUrl='${ctx}/psi/psiTransportOrder/view?transportNo='+ele.billNo;
						if(ele.billNo.indexOf("_LC")>0){
							linkUrl='${ctx}/psi/lcPsiTransportOrder/view?transportNo='+ele.billNo;
						}
						body +="<tr><td style='vertical-align: middle;text-align: center;'> <a target='_blank' href='"+linkUrl+"'>"+ele.billNo+"</a></td><td style='vertical-align: middle;text-align: center;' >"+country.toUpperCase()+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.tranModel+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.remark+"</td><td style='vertical-align: middle;text-align: center;' >"+ele.quantity+"</td><td style='vertical-align: middle;text-align: center;'>"
							+ele.createDate+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.arriveDate+"</td></tr>";
					}	
				}else{
					for ( var i = 0; i < data.length; i++) {
						var ele = data[i];
						body +="<tr><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentName+"</td><td style='vertical-align: middle;text-align: center;'><a target='_blank' href='${ctx}/psi/fbaInbound?shipmentId="+ele.shipmentId+"&country=' >"+ele.shipmentId+"</a></td><td style='vertical-align: middle;text-align: center;'>"
						+ele.sku+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.quantityShipped+"</td><td style='vertical-align: middle;text-align: center;'>"+ele.shipmentStatus+"</td></tr>";
					}	
				}
				tip.find("tbody").html(body);
			});
			tip.modal();
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty psiProduct.platform?'active':''}"><a class="countryHref" href="#total" key="">总计|TOTAL</a></li>
		<li ><a class="countryHref" href="#euTotal" key="eu1"><spring:message code='psi_inventory_EU_total' /></a></li>
		<li ><a class="countryHref" href="#usTotal" key="com1"><spring:message code='psi_inventory_US_total' /></a></li>
		<li ><a class="countryHref" href="#jpTotal" key="jp1"><spring:message code='psi_inventory_JP_total' /></a></li>
		<li ><a class="countryHref" href="#eu" key="eu"><spring:message code='psi_inventory_EU_stock' /></a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${psiProduct.platform eq dic.value ?'active':''}"><a class="countryHref" href="#${dic.value}" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<input type="hidden" name="selectCountry"/>
	<!-- <a class="btn btn-primary"  id="export" style="float:right"><spring:message code="sys_but_export" /></a> -->  
    <c:if test="${'01' eq fns:getMonth()}"><c:set var="ratio"  value="0.84"/></c:if>
    <c:if test="${'02' eq fns:getMonth()}"><c:set var="ratio"  value="0.77"/></c:if>
    <c:if test="${'03' eq fns:getMonth()}"><c:set var="ratio"  value="0.90"/></c:if>
    <c:if test="${'04' eq fns:getMonth()}"><c:set var="ratio"  value="0.96"/></c:if>
    <c:if test="${'05' eq fns:getMonth()}"><c:set var="ratio"  value="1"/></c:if>
    <c:if test="${'06' eq fns:getMonth()}"><c:set var="ratio"  value="0.95"/></c:if>
    <c:if test="${'07' eq fns:getMonth()}"><c:set var="ratio"  value="0.56"/></c:if>
    <c:if test="${'08' eq fns:getMonth()}"><c:set var="ratio"  value="0.62"/></c:if>
    <c:if test="${'09' eq fns:getMonth()}"><c:set var="ratio"  value="0.68"/></c:if>
    <c:if test="${'10' eq fns:getMonth()}"><c:set var="ratio"  value="0.73"/></c:if>
    <c:if test="${'11' eq fns:getMonth()}"><c:set var="ratio"  value="0.67"/></c:if>
    <c:if test="${'12' eq fns:getMonth()}"><c:set var="ratio"  value="0.63"/></c:if>
	<div class="tab-content">
		<div id="total" class="tab-pane active">
		<table  class="table table-striped table-bordered table-condensed dataTable">
			<thead>
			  <tr>
				   <th ><spring:message code='amaInfo_businessReport_productName'/></th>
				   <th ><spring:message code='psi_inventory_in_production' /></th>
				   <th ><spring:message code='psi_inventory_cn_stock' /></th>
				   <th ><spring:message code='psi_inventory_shipment_pending' /></th>
				   <th ><spring:message code='psi_inventory_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_overseas_stock' /></th>
				   <th ><spring:message code='psi_inventory_Gross_FBA_stock' /></th>
				   <th ><spring:message code='psi_inventory_fba_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_Gross_stock' /></th>
				   <th><spring:message code='psi_inventory_upper_FBA_stock_limit' /></th>
				   <th >MOQ</th>
				   <th ><spring:message code='psi_inventory_number_of_cartons' /></th>
				   <th ><a href="#" data-toggle="tooltip" title="昨日开始往前滚动31天销量" style="color: #08c;"><spring:message code='psi_inventory_sales_within_31_days' /></a></th>
				   <th ><spring:message code='psi_inventory_average_daily_sales' /></th>
				   <th ><spring:message code='psi_inventory_remaining_sales_months' /></th><%--滚动31日销库销比 --%>
				   <th ><spring:message code='psi_inventory_sales_months_forecast' /></th><%--预测库销比 --%>
				   <shiro:hasPermission name="psi:inventory:stockPriceView">
				   <th ><spring:message code='psi_inventory_price' />($)</th>
				   <th ><spring:message code='psi_inventory_total_price' />($)</th>
				   <th ><spring:message code='psi_inventoty_amount' />($)</th>
				   <th ><spring:message code='psi_inventoty_stock_percentage' />(%)</th>
				   </shiro:hasPermission>
				   <shiro:lacksPermission name="psi:inventory:stockPriceView">
				   	    <th ><spring:message code='psi_inventory_price' />($)</th>
				   		<th ><spring:message code='psi_inventoty_amount' />($)</th>
				   </shiro:lacksPermission>
			   </tr>
			</thead>
			<tbody>
			<c:set value="0" var="totalPrice" />
			<c:forEach items="${list}" var="product" varStatus="i">
				<c:forEach items="${product.productNameWithColor}" var="name" varStatus="j">
					<c:if test="${!fn:contains(hiddens, name) }">
					<tr >
						<td class="name">
							<b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target="_blank">${fn:replace(name,'Inateck','')} <span style="display: none">${not empty isNewMap[name]?'新品':''}</span></a></b>
							<c:if test="${not empty typeLineMap[fn:toLowerCase(product.type)]}">
								(${typeLineMap[fn:toLowerCase(product.type)] }线)
							</c:if>
							<c:if test="${productTranTypeAndBP[name]['total'].transportType eq '2'}">
								<span class="icon-plane"></span>
							</c:if>
							<span style="color: gray">
								${fns:getDictLabel(isSaleMap[name],'product_position','')}
							</span>
							<span style="display: none"> ${fn:replace(product.type,'&','')}</span>
						</td>
						<c:set var="producting1"  value="${producting[name].quantity}"/>
						<c:set var="transportting1"  value="${transportting[name].quantity}"/>
						<c:set var="transportting2"  value="${preTransportting[name].quantity}"/>
						
						<c:set var="inventorysCN"  value="${inventorys[name].totalQuantityCN}"/>
						<c:set var="inventorysNotCN"  value="${inventorys[name].totalQuantityNotCN}"/>
						<td><c:if test="${producting1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'${name}','');return false;">${producting1}</a></c:if></td>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">${fn:length(inventorys[name].cnTip)>0?inventorysCN:''}</a>
						</td>
						<td><c:if test="${transportting2>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'${name}','');return false;">${transportting2}</a></c:if></td>
						<td><c:if test="${transportting1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'${name}','');return false;">${transportting1}</a></c:if></td>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].notCnTip}">${fn:length(inventorys[name].notCnTip)>0?inventorysNotCN:''}</a>
						</td>
						<td>
							<c:set var="de" value="${name}_de" />
							<c:set var="de"  value="${fbas[de].total>0?fbas[de].total:0}" />
							<c:set var="fr" value="${name}_fr" />
							<c:set var="fr"  value="${fbas[fr].total>0?fbas[fr].total:0}" />
							<c:set var="uk" value="${name}_uk" />
							<c:set var="uk"  value="${fbas[uk].total>0?fbas[uk].total:0}" />
							<c:set var="it" value="${name}_it" />
							<c:set var="it"  value="${fbas[it].total>0?fbas[it].total:0}" />
							<c:set var="es" value="${name}_es" />
							<c:set var="es"  value="${fbas[es].total>0?fbas[es].total:0}" />
							<c:set var="com" value="${name}_com" />
							<c:set var="com"  value="${fbas[com].total>0?fbas[com].total:0}" />
							<c:set var="com2" value="${name}_com2" />
							<c:set var="com2"  value="${fbas[com2].total>0?fbas[com2].total:0}" />
							<c:set var="ca" value="${name}_ca" />
							<c:set var="ca"  value="${fbas[ca].total>0?fbas[ca].total:0}" />
							<c:set var="mx" value="${name}_mx" />
							<c:set var="mx"  value="${fbas[mx].total>0?fbas[mx].total:0}" />
							<c:set var="jp" value="${name}_jp" />
							<c:set var="jp"  value="${fbas[jp].total>0?fbas[jp].total:0}" />
							<c:set value="${de+fr+uk+it+es+com+ca+jp+com2}" var="fbaTotal" />
							<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>US:${com}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}<br/>CA:${ca}<br/>MX:${mx}<br/>JP:${jp}">${fbaTotal>0?fbaTotal:''}</a>
						</td>
						<td>
							<c:set var="de" value="${name}_de" />
							<c:set var="de"  value="${fbaTran[de]>0?fbaTran[de]:0}" />
							<c:set var="fr" value="${name}_fr" />
							<c:set var="fr"  value="${fbaTran[fr]>0?fbaTran[fr]:0}" />
							<c:set var="uk" value="${name}_uk" />
							<c:set var="uk"  value="${fbaTran[uk]>0?fbaTran[uk]:0}" />
							<c:set var="it" value="${name}_it" />
							<c:set var="it"  value="${fbaTran[it]>0?fbaTran[it]:0}" />
							<c:set var="es" value="${name}_es" />
							<c:set var="es"  value="${fbaTran[es]>0?fbaTran[es]:0}" />
							<c:set var="com" value="${name}_com" />
							<c:set var="com"  value="${fbaTran[com]>0?fbaTran[com]:0}" />
							<c:set var="com2" value="${name}_com2" />
							<c:set var="com2"  value="${fbaTran[com2]>0?fbaTran[com2]:0}" />
							<c:set var="ca" value="${name}_ca" />
							<c:set var="ca"  value="${fbaTran[ca]>0?fbaTran[ca]:0}" />
							<c:set var="mx" value="${name}_mx" />
							<c:set var="mx"  value="${fbaTran[mx]>0?fbaTran[mx]:0}" />
							<c:set var="jp" value="${name}_jp" />
							<c:set var="jp"  value="${fbaTran[jp]>0?fbaTran[jp]:0}" />
							<c:set value="${de+fr+uk+it+es+com+ca+jp+mx+com2}" var="fbaTrans" />
							<c:if test="${fbaTrans>0}">
							<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','');return false;">${fbaTrans}</a>
							</c:if>
						</td>
						<td>
							<c:set var="total" value="${fbaTotal+producting1+transportting1+inventorysCN+inventorysNotCN}" />
							${total>0?total:''}
						</td>
						<td>${productAttr[name].quantity}</td>
						<td>
							${productsMoqAndPrice[name].moq}
						</td>
						<td>
							${product.packQuantity}
						</td>
						<td>
							<c:set var="de" value="${name}_de" />
							<c:set var="de"  value="${fancha[de].day31Sales>0?fancha[de].day31Sales:0}" />
							<c:set var="fr" value="${name}_fr" />
							<c:set var="fr"  value="${fancha[fr].day31Sales>0?fancha[fr].day31Sales:0}" />
							<c:set var="uk" value="${name}_uk" />
							<c:set var="uk"  value="${fancha[uk].day31Sales>0?fancha[uk].day31Sales:0}" />
							<c:set var="it" value="${name}_it" />
							<c:set var="it"  value="${fancha[it].day31Sales>0?fancha[it].day31Sales:0}" />
							<c:set var="es" value="${name}_es" />
							<c:set var="es"  value="${fancha[es].day31Sales>0?fancha[es].day31Sales:0}" />
							<c:set var="com" value="${name}_com" />
							<c:set var="com"  value="${fancha[com].day31Sales>0?fancha[com].day31Sales:0}" />
							<c:set var="com2" value="${name}_com2" />
							<c:set var="com2"  value="${fancha[com2].day31Sales>0?fancha[com2].day31Sales:0}" />
							<c:set var="ca" value="${name}_ca" />
							<c:set var="ca"  value="${fancha[ca].day31Sales>0?fancha[ca].day31Sales:0}" />
							<c:set var="mx" value="${name}_mx" />
							<c:set var="mx"  value="${fancha[mx].day31Sales>0?fancha[mx].day31Sales:0}" />
							
							<c:set var="jp" value="${name}_jp" />
							<c:set var="jp"  value="${fancha[jp].day31Sales>0?fancha[jp].day31Sales:0}" />
							<c:set value="${de+fr+uk+it+es+com+ca+jp+com2}" var="total31Days" />
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>US:${com}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}<br/>CA:${ca}<br/>MX:${mx}<br/>JP:${jp}">${total31Days>0?total31Days:''}</a>
						</td>
						<td>
							<c:if test="${total31Days/31 >0.5}"><fmt:formatNumber value="${total31Days/31}" maxFractionDigits="0" pattern="#0" /></c:if>
						</td>
						<td>
							<fmt:formatNumber value="${total31Days>0?total/total31Days:''}" maxFractionDigits="1"/>
						</td>
						<%--预测库销比 --%>
						<td>
							<fmt:formatNumber value="${productAttr[name].inventorySaleMonth>0?productAttr[name].inventorySaleMonth:''}" maxFractionDigits="1"/>
						</td>
						<shiro:hasPermission name="psi:inventory:stockPriceView"> 
						<td>
							${productsMoqAndPrice[name].price}
						</td>
						<td>
							<c:set var="singlePrice" value="${product.tempPartsTotalMap[name]>0?(productsMoqAndPrice[name].price+product.tempPartsTotalMap[name]):productsMoqAndPrice[name].price}" />
							<fmt:formatNumber value="${singlePrice}" maxFractionDigits="2" />
						</td>
						<td class="price">
							<fmt:formatNumber value="${singlePrice*total}" maxFractionDigits="2"/>
							<c:if test="${singlePrice>0}">
								<c:set value="${singlePrice*total+totalPrice}" var="totalPrice" />
							</c:if>
						</td>
						<td class="libraryAccounting"></td>
						</shiro:hasPermission>
						<shiro:lacksPermission name="psi:inventory:stockPriceView"> 
							<td>
								${salePrice[name]}
							</td>
							<td class="salePrice">
								<c:if test="${not empty salePrice[name]}">
									<fmt:formatNumber value="${salePrice[name]*total}" maxFractionDigits="2"/>
								</c:if>
							</td>
						</shiro:lacksPermission>
					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
			<shiro:hasPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					<td id="totalProduct1"></td>
					<td id="totalTitle1"></td>
					<td id="totalW1"></td><td id="totalT1"></td><td id="totalO1"></td><td id="totalF1"></td><td id="totalF2"></td><td id="totalX1"></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="totalPrice1">
						
					</td>
					<td id="pcent">
							
					</td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="totalProduct"></td>
					<td id="totalTitle"></td>
					<td id="totalW"></td><td id="totalT"></td><td id="totalO"></td><td id="totalF"></td><td id="totalFf"></td><td id="totalX"></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="totalPrice">
						<fmt:formatNumber value="${totalPrice}" maxFractionDigits="2" />
					</td>
					<td>
							
					</td>
				</tr>	
				</tfoot>	
			</shiro:hasPermission>
			<shiro:lacksPermission name="psi:inventory:stockPriceView">
			   <tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					<td id="totalProduct1"></td>
					<td id="totalTitle1"></td>
					<td id="totalW1"></td><td id="totalT1"></td><td id="totalO1"></td><td id="totalF1"></td><td id="totalF2"></td><td id="totalX1"></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="totalProduct"></td>
					<td id="totalTitle"></td>
					<td id="totalW"></td><td id="totalT"></td><td id="totalO"></td><td id="totalF"></td><td id="totalFf"></td><td id="totalX"></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>
			
			</shiro:lacksPermission>
			</tbody>
		</table>
		</div>
		<!-- 欧洲 -->
		<div id="euTotal" class="tab-pane">
		<table  class="table table-striped table-bordered table-condensed dataTable">
			<thead>
			  <tr>
				   <th ><spring:message code='amaInfo_businessReport_productName'/></th>
				   <th ><spring:message code='psi_inventory_in_production' /></th>
				   <th ><spring:message code='psi_inventory_cn_stock' /><br/>(ALL)</th>
				   <th ><spring:message code='psi_inventory_cn_stock' /></th>
				   <th ><spring:message code='psi_inventory_shipment_pending' /></th>
				   <th ><spring:message code='psi_inventory_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_DE_stock' /></th>
				   <th ><spring:message code='psi_inventory_Gross_FBA_stock' /></th>
				   <th ><spring:message code='psi_inventory_fba_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_Gross_stock' /></th>
				   <th >MOQ</th>
				   <th ><spring:message code='psi_inventory_number_of_cartons' /></th>
				   <th ><a href="#" data-toggle="tooltip" title="昨日开始往前滚动31天销量" style="color: #08c;"><spring:message code='psi_inventory_sales_within_31_days' /></a></th>
				   <th ><spring:message code='psi_inventory_average_daily_sales' /></th>
				   <th ><spring:message code='psi_inventory_remaining_sales_months' /></th>
				   <th ><spring:message code='psi_inventory_sales_months_forecast' /></th><%--预测库销比 --%>
				   <shiro:hasPermission name="psi:inventory:stockPriceView">
				   <th ><spring:message code='psi_inventory_price' />($)</th>
				   <th ><spring:message code='psi_inventory_total_price' />($)</th>
				   <th ><spring:message code='psi_inventoty_amount' />($)</th>
				   <th ><spring:message code='psi_inventoty_stock_percentage' />(%)</th>
				   </shiro:hasPermission>
				   <shiro:lacksPermission name="psi:inventory:stockPriceView"> 
						<th ><spring:message code='psi_inventory_price' />($)</th>
						<th ><spring:message code='psi_inventoty_amount' />($)</th>
				   </shiro:lacksPermission>
				   <th ><spring:message code='psi_inventoty_turnover_last_month' /></th>
				   <th ><spring:message code='psi_inventoty_turnover_standard_last_month' /></th>
				    <th ><spring:message code='psi_inventoty_turnover_standard' /></th>
				    <th ><spring:message code='psi_inventoty_turnover_by_year' /></th>
			   </tr>
			</thead>
			<tbody>
			<c:set value="0" var="totalPrice" />
			<c:forEach items="${list}" var="product" varStatus="i">
				<c:forEach items="${product.productNameWithColor}" var="name" varStatus="j">
					<c:if test="${!fn:contains(hiddens, name) }">
					<c:set value="${name}_eu"  var="key"/>
					<tr >
						<td class="name">
							<b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target="_blank">${fn:replace(name,'Inateck','')}</a></b>
							<span style="display: none">${not empty isNewMap[key] ?'新品':''} </span>
							<c:if test="${not empty typeLineMap[fn:toLowerCase(product.type)]}">
								(${typeLineMap[fn:toLowerCase(product.type)] }线)
							</c:if>
							<c:if test="${productTranTypeAndBP[name]['eu'].transportType eq '2'}">
								<span class="icon-plane"></span>
							</c:if>
							<span style="color: gray">
								${fns:getDictLabel(isSaleMap[key],'product_position','')}
							</span>
							<span style="display: none">${fn:replace(product.type,'&','')}</span>
						</td>
						<c:set value="${producting[name].quantityEuro}" var="productVar" />
						<c:set value="${inventorys[name].quantityEuro['CN'].newQuantity}" var="cnEuro" />
						<c:set value="${transportting[name].quantityEuro}" var="transportVar" />
						<c:set value="${preTransportting[name].quantityEuro}" var="transportVar1" />
						<c:set value="${inventorys[name].quantityEuro['DE'].newQuantity}" var="deNew" />
						
						
						<c:set var="cntipMap" value="${inventorys[name].cnTipMap}" />
						<c:set var="notCntipMap" value="${inventorys[name].notCnTipMap}" />
						
						<td><c:if test="${productVar>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'${name}','eu');return false;">${productVar}</a></c:if></td>
						<c:set var="inventorysCN"  value="${inventorys[name].totalQuantityCN}"/>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">${fn:length(inventorys[name].cnTip)>0?inventorysCN:''}</a>
						</td>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${inventorys[name].inventorys['de'].quantityInventory['CN'].newQuantity}[${cntipMap['de']}]<br/>UK:${inventorys[name].inventorys['uk'].quantityInventory['CN'].newQuantity}[${cntipMap['uk']}]<br/>FR:${inventorys[name].inventorys['fr'].quantityInventory['CN'].newQuantity}[${cntipMap['fr']}]<br/>IT:${inventorys[name].inventorys['it'].quantityInventory['CN'].newQuantity}[${cntipMap['it']}]<br/>ES:${inventorys[name].inventorys['es'].quantityInventory['CN'].newQuantity}[${cntipMap['es']}]">${cnEuro>0?cnEuro:''}</a>
						</td>
						<td><c:if test="${transportVar1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'${name}','eu');return false;">${transportVar1}</a></c:if></td>
						<td><c:if test="${transportVar>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'${name}','eu');return false;">${transportVar}</a></c:if></td>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${inventorys[name].inventorys['de'].quantityInventory['DE'].newQuantity}[${notCntipMap['de']}]<br/>UK:${inventorys[name].inventorys['uk'].quantityInventory['DE'].newQuantity}[${notCntipMap['uk']}]<br/>FR:${inventorys[name].inventorys['fr'].quantityInventory['DE'].newQuantity}[${notCntipMap['fr']}]<br/>IT:${inventorys[name].inventorys['it'].quantityInventory['DE'].newQuantity}[${notCntipMap['it']}]<br/>ES:${inventorys[name].inventorys['es'].quantityInventory['DE'].newQuantity}[${notCntipMap['es']}]">${deNew>0?deNew:''}</a>
						</td>
						<td>
							<c:set var="de" value="${name}_de" />
							<c:set var="de"  value="${fbas[de].total>0?fbas[de].total:0}" />
							<c:set var="fr" value="${name}_fr" />
							<c:set var="fr"  value="${fbas[fr].total>0?fbas[fr].total:0}" />
							<c:set var="uk" value="${name}_uk" />
							<c:set var="uk"  value="${fbas[uk].total>0?fbas[uk].total:0}" />
							<c:set var="it" value="${name}_it" />
							<c:set var="it"  value="${fbas[it].total>0?fbas[it].total:0}" />
							<c:set var="es" value="${name}_es" />
							<c:set var="es"  value="${fbas[es].total>0?fbas[es].total:0}" />
							<c:set value="${de+fr+uk+it+es}" var="fbaTotal" />
							<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}">${fbaTotal>0?fbaTotal:''}</a>
						</td>
						<td>
							<c:set var="de" value="${name}_de" />
							<c:set var="de"  value="${fbaTran[de]>0?fbaTran[de]:0}" />
							<c:set var="fr" value="${name}_fr" />
							<c:set var="fr"  value="${fbaTran[fr]>0?fbaTran[fr]:0}" />
							<c:set var="uk" value="${name}_uk" />
							<c:set var="uk"  value="${fbaTran[uk]>0?fbaTran[uk]:0}" />
							<c:set var="it" value="${name}_it" />
							<c:set var="it"  value="${fbaTran[it]>0?fbaTran[it]:0}" />
							<c:set var="es" value="${name}_es" />
							<c:set var="es"  value="${fbaTran[es]>0?fbaTran[es]:0}" />
							<c:set value="${de+fr+uk+it+es}" var="fbaTrans" />
							<c:if test="${fbaTrans>0}">
							<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','eu');return false;">${fbaTrans}</a>
							</c:if>
						</td>
						<td>
							<c:set var="total" value="${fbaTotal+productVar+transportVar+cnEuro+deNew}" />
							${total>0?total:''}
						</td>
						<td>
							${productsMoqAndPrice[name].moq}
						</td>
						<td>
							${product.packQuantity}
						</td>
						<td>
							<c:set var="de" value="${name}_de" />
							<c:set var="de"  value="${fancha[de].day31Sales>0?fancha[de].day31Sales:0}" />
							<c:set var="fr" value="${name}_fr" />
							<c:set var="fr"  value="${fancha[fr].day31Sales>0?fancha[fr].day31Sales:0}" />
							<c:set var="uk" value="${name}_uk" />
							<c:set var="uk"  value="${fancha[uk].day31Sales>0?fancha[uk].day31Sales:0}" />
							<c:set var="it" value="${name}_it" />
							<c:set var="it"  value="${fancha[it].day31Sales>0?fancha[it].day31Sales:0}" />
							<c:set var="es" value="${name}_es" />
							<c:set var="es"  value="${fancha[es].day31Sales>0?fancha[es].day31Sales:0}" />
							
							<c:set value="${de+fr+uk+it+es}" var="total31Days" />
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${de}<br/>FR:${fr}<br/>UK:${uk}<br/>IT:${it}<br/>ES:${es}">${total31Days>0?total31Days:''}</a>
						</td>
						<td>
							<c:if test="${total31Days/31 >0.5}"><fmt:formatNumber value="${total31Days/31}" maxFractionDigits="0" pattern="#0" /></c:if>
						</td>
						<td>
							<fmt:formatNumber value="${total31Days>0?total/total31Days:''}" maxFractionDigits="1"/>
						</td>
						<%--预测库销比 --%>
						<td>
							<c:if test="${'1' ne hasPower[product.id] }">
								<fmt:formatNumber value="${inventorySalesMonthMap[name]['eu']>0?inventorySalesMonthMap[name]['eu']:''}" maxFractionDigits="1"/>
							</c:if>
							<c:if test="${'1' eq hasPower[product.id] }">
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-placement="left"
									data-content="DE:<fmt:formatNumber value="${inventorySalesMonthMap[name]['de']}" maxFractionDigits="1" /><br/>UK:<fmt:formatNumber value="${inventorySalesMonthMap[name]['uk']}" maxFractionDigits="1" /><br/>FR:<fmt:formatNumber value="${inventorySalesMonthMap[name]['fr']}" maxFractionDigits="1" /><br/>IT:<fmt:formatNumber value="${inventorySalesMonthMap[name]['it']}" maxFractionDigits="1" /><br/>ES:<fmt:formatNumber value="${inventorySalesMonthMap[name]['es']}" maxFractionDigits="1" />">
									<fmt:formatNumber value="${inventorySalesMonthMap[name]['eu']>0?inventorySalesMonthMap[name]['eu']:''}" maxFractionDigits="1"/></a>
							</c:if>
						</td>
						<shiro:hasPermission name="psi:inventory:stockPriceView"> 
						<td>
							${productsMoqAndPrice[name].price}
						</td>
						<td>
							<c:set var="singlePrice" value="${product.tempPartsTotalMap[name]>0?(productsMoqAndPrice[name].price+product.tempPartsTotalMap[name]):productsMoqAndPrice[name].price}" />
							<fmt:formatNumber value="${singlePrice}" maxFractionDigits="2" />
						</td>
						<td class="price">
							<fmt:formatNumber value="${singlePrice*total}" maxFractionDigits="2"/>
							<c:if test="${singlePrice>0}">
								<c:set value="${singlePrice*total+totalPrice}" var="totalPrice" />
							</c:if>
						</td>
						<td class="libraryAccounting"></td>
						</shiro:hasPermission>
						<shiro:lacksPermission name="psi:inventory:stockPriceView"> 
							<td>
								${salePrice[name]}
							</td>
							<td class="salePrice">
								<c:if test="${not empty salePrice[name]}">
									<fmt:formatNumber value="${salePrice[name]*total}" maxFractionDigits="2"/>
								</c:if>
							</td>
						</shiro:lacksPermission>
						
						
						<c:set value="${name}_eu"  var="tempKey"/>
						
						<td><c:if test="${not empty turnoverMap['eu'][name]&&turnoverMap['eu'][name].ePrice>0 }"><fmt:formatNumber value="${turnoverMap['eu'][name].sPrice/turnoverMap['eu'][name].ePrice}" maxFractionDigits="2"/></c:if></td>
						<td><c:if test='${not empty starandMap[tempKey] }'><fmt:formatNumber value="${ratio*starandMap[tempKey]/12}" maxFractionDigits="2"/></c:if></td>
						<c:set var="yearTurnover"  value="0"/>
						<td><c:set value="${name}_de"  var="tempDEKey"/>
					    	<c:if test="${'4' ne isSaleMap[key] && !fn:contains(newTwoMonth,tempDEKey)}">
							   
								<c:set var="safeDay" value="0" />
								<c:set var="period" value="${fancha[tempKey].period+(empty productTranTypeAndBP[name]['eu'].bufferPeriod ? 0: productTranTypeAndBP[name]['eu'].bufferPeriod)}" />
								<c:if test="${fancha[tempKey].variance>0}">
										<c:set var="safe" value="${fns:roundUp(fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33)}" />
										<c:if test="${safe>0}">
										    <c:choose>
												<c:when test="${fancha[tempKey].forecastPreiodAvg >0 }">
													<c:set var="safeDay" value="${fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33/fancha[tempKey].forecastPreiodAvg}" />
												</c:when>
												<c:when test="${fancha[tempKey].day31Sales >0 }">
													<c:set var="safeDay" value="${(fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33)/(fancha[tempKey].day31Sales/31)}" />
												</c:when>
											</c:choose>
										</c:if>
							    </c:if> 
							    <c:if test="${fns:roundUp(safeDay)+period>0 }">
							        <fmt:formatNumber value="${ratio*365/(fns:roundUp(safeDay)+period)/12}" maxFractionDigits="2"/>
							        <c:set var="yearTurnover"  value="${365/(fns:roundUp(safeDay)+period)}"/>
							    </c:if>
							</c:if>		
						</td>
						<td>
						   <c:if test="${yearTurnover>0 }"> <fmt:formatNumber value="${yearTurnover}" maxFractionDigits="2"/></c:if>
						</td>
					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
			<shiro:hasPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					<td id="euProduct1"></td><td></td>
					<td id="euTitle1"></td>
					<td id="euW1"></td><td id="euT1"></td><td id="euO1"></td><td id="euF1"></td><td id="euF2"></td><td id="euX1"></td>
				    <td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="euTotalPrice1">
						
					</td>
					<td id="euPcent">
					</td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="euProduct"></td><td></td>
					<td id="euTitle"></td>
					<td id="euW"></td><td id="euT"></td><td id="euO"></td><td id="euF"></td><td id="euFf"></td><td id="euX"></td>
					<td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="euTotalPrice">
						<fmt:formatNumber value="${totalPrice}" maxFractionDigits="2" />
					</td>
					<td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>	
			</shiro:hasPermission>
			<shiro:lacksPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					<td id="euProduct1"></td><td></td>
					<td id="euTitle1"></td>
					<td id="euW1"></td><td id="euT1"></td><td id="euO1"></td><td id="euF1"></td><td id="euF2"></td><td id="euX1"></td>
					<td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="euProduct"></td><td></td>
					<td id="euTitle"></td>
					<td id="euW"></td><td id="euT"></td><td id="euO"></td><td id="euF"></td><td id="euFf"></td><td id="euX"></td>
					<td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>	
			</shiro:lacksPermission>
			</tbody>
		</table>
		</div>
		<!-- 美国 -->
		<div id="usTotal" class="tab-pane">
		<table  class="table table-striped table-bordered table-condensed dataTable">
			<thead>
			  <tr>
				    <th ><spring:message code='amaInfo_businessReport_productName'/></th>
				   <th ><spring:message code='psi_inventory_in_production' /></th>
				   <th ><spring:message code='psi_inventory_cn_stock' /><br/>(ALL)</th>
				   <th ><spring:message code='psi_inventory_cn_stock' /></th>
				   <th ><spring:message code='psi_inventory_shipment_pending' /></th>
				   <th ><spring:message code='psi_inventory_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_US_stock' /></th>
				   <th ><spring:message code='psi_inventory_Gross_FBA_stock' /></th>
				   <th ><spring:message code='psi_inventory_fba_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_Gross_stock' /></th>
				   <th >MOQ</th>
				   <th ><spring:message code='psi_inventory_number_of_cartons' /></th>
				   <th ><a href="#" data-toggle="tooltip" title="昨日开始往前滚动31天销量" style="color: #08c;"><spring:message code='psi_inventory_sales_within_31_days' /></a></th>
				   <th ><spring:message code='psi_inventory_average_daily_sales' /></th>
				   <th ><spring:message code='psi_inventory_remaining_sales_months' /></th>
				   <th ><spring:message code='psi_inventory_sales_months_forecast' /></th><%--预测库销比 --%>
				   <shiro:hasPermission name="psi:inventory:stockPriceView">
				   <th ><spring:message code='psi_inventory_price' />($)</th>
				   <th ><spring:message code='psi_inventory_total_price' />($)</th>
				   <th ><spring:message code='psi_inventoty_amount' />($)</th>
				   <th ><spring:message code='psi_inventoty_stock_percentage' />(%)</th>
				   </shiro:hasPermission>
				   <shiro:lacksPermission name="psi:inventory:stockPriceView">
				   	    <th ><spring:message code='psi_inventory_price' />($)</th>
				   		<th ><spring:message code='psi_inventoty_amount' />($)</th>
				   </shiro:lacksPermission>
				   <th ><spring:message code='psi_inventoty_turnover_last_month' /></th>
				   <th ><spring:message code='psi_inventoty_turnover_standard_last_month' /></th>
				    <th ><spring:message code='psi_inventoty_turnover_standard' /></th>
				    <th ><spring:message code='psi_inventoty_turnover_by_year' /></th>
			   </tr>
			</thead>
			<tbody>
			<c:set value="0" var="totalPrice" />
			<c:forEach items="${list}" var="product" varStatus="i">
				<c:forEach items="${product.productNameWithColor}" var="name" varStatus="j">
					<c:if test="${!fn:contains(hiddens, name) }">
					<c:set var="name_country" value="${name }_com"></c:set>
					<tr >
						<td class="name">
							<b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target="_blank">${fn:replace(name,'Inateck','')}</a></b>
							<span style="display: none"> ${not empty isNewMap[name_country] ?'新品':''}</span>
							<c:if test="${not empty typeLineMap[fn:toLowerCase(product.type)]}">
								(${typeLineMap[fn:toLowerCase(product.type)] }线)
							</c:if>
							<c:if test="${productTranTypeAndBP[name]['com'].transportType eq '2'}">
								<span class="icon-plane"></span>
							</c:if>
							<span style="color: gray">
								${fns:getDictLabel(isSaleMap[name_country],'product_position','')}
							</span>
							<span style="display: none">${fn:replace(product.type,'&','')}"</span>
						</td>
						
						<c:set var="cntipMap" value="${inventorys[name].cnTipMap}" />
						<c:set var="notCntipMap" value="${inventorys[name].notCnTipMap}" />
						
						<c:set var="producting1"  value="${producting[name].inventorys['com'].quantity}"/>
						<c:set var="transportting1"  value="${transportting[name].inventorys['com'].quantity}"/>
						<c:set var="transportting2"  value="${preTransportting[name].inventorys['com'].quantity}"/>
						
						<c:set var="inventorysCN"  value="${inventorys[name].inventorys['com'].quantityInventory['CN'].newQuantity}"/>
						<c:set var="inventorysNotCN"  value="${inventorys[name].inventorys['com'].quantityInventory['US'].newQuantity}"/>
						
						<td><c:if test="${producting1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'${name}','com');return false;">${producting1}</a></c:if></td>
						
						<c:set var="inventorysCNTotal"  value="${inventorys[name].totalQuantityCN}"/>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">${fn:length(inventorys[name].cnTip)>0?inventorysCNTotal:''}</a>
						</td>
						
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['com']}">${fn:length(cntipMap['com'])>0?inventorysCN:''}</a>
						</td>
						<td><c:if test="${transportting2>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'${name}','com');return false;">${transportting2}</a></c:if></td>
						<td><c:if test="${transportting1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'${name}','com');return false;">${transportting1}</a></c:if></td>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${notCntipMap['com']}">${fn:length(notCntipMap['com'])>0?inventorysNotCN:''}</a>
						</td>
						<td>
							<c:set var="com" value="${name}_com" />
							<c:set var="com"  value="${fbas[com].total>0?fbas[com].total:0}" />
							<c:set value="${com}" var="fbaTotal" />
								${fbaTotal>0?fbaTotal:''}
						</td>
						<td>
							<c:set var="com" value="${name}_com" />
							<c:set var="com"  value="${fbaTran[com]>0?fbaTran[com]:0}" />
							<c:set value="${com}" var="fbaTrans" />
							<c:if test="${fbaTrans>0}">
							<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','com');return false;">${fbaTrans}</a>
							</c:if>
						</td>
						<td>
							<c:set var="total" value="${fbaTotal+producting1+transportting1+inventorysCN+inventorysNotCN}" />
							${total>0?total:''}
						</td>
						<td>
							${productsMoqAndPrice[name].moq}
						</td>
						<td>
							${product.packQuantity}
						</td>
						<td>
							<c:set var="com" value="${name}_com" />
							<c:set var="com"  value="${fancha[com].day31Sales>0?fancha[com].day31Sales:0}" />
							<c:set value="${com}" var="total31Days" />
								${total31Days>0?total31Days:''}
						</td>
						<td>
							<c:if test="${total31Days/31 >0.5}"><fmt:formatNumber value="${total31Days/31}" maxFractionDigits="0" pattern="#0" /></c:if>
						</td>
						<td>
							<fmt:formatNumber value="${total31Days>0?total/total31Days:''}" maxFractionDigits="1"/>
						</td>
						<%--预测库销比 --%>
						<td>
							<fmt:formatNumber value="${inventorySalesMonthMap[name]['com']>0?inventorySalesMonthMap[name]['com']:''}" maxFractionDigits="1"/>
						</td>
						<shiro:hasPermission name="psi:inventory:stockPriceView"> 
						<td>
							${productsMoqAndPrice[name].price}
						</td>
						<td>
							<c:set var="singlePrice" value="${product.tempPartsTotalMap[name]>0?(productsMoqAndPrice[name].price+product.tempPartsTotalMap[name]):productsMoqAndPrice[name].price}" />
							<fmt:formatNumber value="${singlePrice}" maxFractionDigits="2" />
						</td>
						<td class="price">
							<fmt:formatNumber value="${singlePrice*total}" maxFractionDigits="2"/>
							<c:if test="${singlePrice>0}">
								<c:set value="${singlePrice*total+totalPrice}" var="totalPrice" />
							</c:if>
						</td>
						<td class="libraryAccounting"></td>
						</shiro:hasPermission>
						
						<shiro:lacksPermission name="psi:inventory:stockPriceView"> 
							<td>
								${salePrice[name]}
							</td>
							<td class="salePrice">
								<c:if test="${not empty salePrice[name]}">
									<fmt:formatNumber value="${salePrice[name]*total}" maxFractionDigits="2"/>
								</c:if>
							</td>
						</shiro:lacksPermission>
						
						<c:set value="${name}_com"  var="tempKey"/>
					    <td><c:if test="${not empty turnoverMap['com'][name]&&turnoverMap['com'][name].ePrice>0 }"><fmt:formatNumber value="${turnoverMap['com'][name].sPrice/turnoverMap['com'][name].ePrice}" maxFractionDigits="2"/></c:if></td>
						<td><c:if test='${not empty starandMap[tempKey] }'><fmt:formatNumber value="${ratio*starandMap[tempKey]/12}" maxFractionDigits="2"/></c:if></td>
						
						
						<c:set var="yearTurnover"  value="0"/>
						<td><c:set value="${name}_com"  var="tempUSKey"/>
					    	<c:if test="${'4' ne isSaleMap[name_country] && !fn:contains(newTwoMonth,tempUSKey)}">
							   
								<c:set var="safeDay" value="0" />
								<c:set var="period" value="${fancha[tempKey].period+(empty productTranTypeAndBP[name]['com'].bufferPeriod ? 0: productTranTypeAndBP[name]['com'].bufferPeriod)}" />
								<c:if test="${fancha[tempKey].variance>0}">
										<c:set var="safe" value="${fns:roundUp(fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33)}" />
										<c:if test="${safe>0}">
										    <c:choose>
												<c:when test="${fancha[tempKey].forecastPreiodAvg >0 }">
													<c:set var="safeDay" value="${fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33/fancha[tempKey].forecastPreiodAvg}" />
												</c:when>
												<c:when test="${fancha[tempKey].day31Sales >0 }">
													<c:set var="safeDay" value="${(fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33)/(fancha[tempKey].day31Sales/31)}" />
												</c:when>
											</c:choose>
										</c:if>
							    </c:if> 
							    <c:if test="${fns:roundUp(safeDay)+period>0 }">
							        <fmt:formatNumber value="${ratio*365/(fns:roundUp(safeDay)+period)/12}" maxFractionDigits="2"/>
							        <c:set var="yearTurnover"  value="${365/(fns:roundUp(safeDay)+period)}"/>
							    </c:if>
							</c:if>		
						</td>
						<td>
						   <c:if test="${yearTurnover>0 }"> <fmt:formatNumber value="${yearTurnover}" maxFractionDigits="2"/></c:if>
						</td>
					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
			<shiro:hasPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					
					<td id="usProduct1"></td><td></td>
					<td id="usTitle1"></td>
					<td id="usW1"></td><td id="usT1"></td><td id="usO1"></td><td id="usF1"></td><td id="usF2"></td><td id="usX1"></td>
					<td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="usTotalPrice1">
						
					</td>
					<td id="usPcent">
							
					</td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="usProduct"></td><td></td>
					<td id="usTitle"></td>
					<td id="usW"></td><td id="usT"></td><td id="usO"></td><td id="usF"></td><td id="usFf"></td><td id="usX"></td>
					<td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="usTotalPrice">
						<fmt:formatNumber value="${totalPrice}" maxFractionDigits="2" />
					</td>
					<td>
							
					</td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>	
			</shiro:hasPermission>
			<shiro:lacksPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					
					<td id="usProduct1"></td><td></td>
					<td id="usTitle1"></td>
					<td id="usW1"></td><td id="usT1"></td><td id="usO1"></td><td id="usF1"></td><td id="usF2"></td><td id="usX1"></td>
					<td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="usProduct"></td><td></td>
					<td id="usTitle"></td>
					<td id="usW"></td><td id="usT"></td><td id="usO"></td><td id="usF"></td><td id="usFf"></td><td id="usX"></td>
					<td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>	
			</shiro:lacksPermission>
			</tbody>
		</table>
		</div>
		
		
		<!-- 日本 -->
		<div id="jpTotal" class="tab-pane">
		<table  class="table table-striped table-bordered table-condensed dataTable">
			<thead>
			  <tr>
				   
				   <th ><spring:message code='amaInfo_businessReport_productName'/></th>
				   <th ><spring:message code='psi_inventory_in_production' /></th>
				   <th ><spring:message code='psi_inventory_cn_stock' /><br/>(ALL)</th>
				   <th ><spring:message code='psi_inventory_cn_stock' /></th>
				   <th ><spring:message code='psi_inventory_shipment_pending' /></th>
				   <th ><spring:message code='psi_inventory_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_JP_stock' /></th>
				   <th ><spring:message code='psi_inventory_Gross_FBA_stock' /></th>
				   <th ><spring:message code='psi_inventory_fba_in_transit' /></th>
				   <th ><spring:message code='psi_inventory_Gross_stock' /></th>
				   <th >MOQ</th>
				   <th ><spring:message code='psi_inventory_number_of_cartons' /></th>
				   <th ><a href="#" data-toggle="tooltip" title="昨日开始往前滚动31天销量" style="color: #08c;"><spring:message code='psi_inventory_sales_within_31_days' /></a></th>
				   <th ><spring:message code='psi_inventory_average_daily_sales' /></th>
				   <th ><spring:message code='psi_inventory_remaining_sales_months' /></th>
				   <th ><spring:message code='psi_inventory_sales_months_forecast' /></th><%--预测库销比 --%>
				   <shiro:hasPermission name="psi:inventory:stockPriceView">
				   <th ><spring:message code='psi_inventory_price' />($)</th>
				   <th ><spring:message code='psi_inventory_total_price' />($)</th>
				   <th ><spring:message code='psi_inventoty_amount' />($)</th>
				   <th ><spring:message code='psi_inventoty_stock_percentage' />(%)</th>
				   </shiro:hasPermission>
				   <shiro:lacksPermission name="psi:inventory:stockPriceView">
				   	    <th ><spring:message code='psi_inventory_price' />($)</th>
				   		<th ><spring:message code='psi_inventoty_amount' />($)</th>
				   </shiro:lacksPermission>
				    <th ><spring:message code='psi_inventoty_turnover_last_month' /></th>
				   <th ><spring:message code='psi_inventoty_turnover_standard_last_month' /></th>
				    <th ><spring:message code='psi_inventoty_turnover_standard' /></th>
				    <th ><spring:message code='psi_inventoty_turnover_by_year' /></th>
			   </tr>
			</thead>
			<tbody>
			<c:set value="0" var="totalPrice" />
			<c:forEach items="${list}" var="product" varStatus="i">
				<c:forEach items="${product.productNameWithColor}" var="name" varStatus="j">
					<c:if test="${!fn:contains(hiddens, name) }">
					<c:set var="name_country" value="${name}_jp"></c:set>
					<tr >
						<td class="name">
							<b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target="_blank">${fn:replace(name,'Inateck','')}</a></b>
							<span style="display: none"> ${not empty isNewMap[name_country]?'新品':''}</span>
							<c:if test="${not empty typeLineMap[fn:toLowerCase(product.type)]}">
								(${typeLineMap[fn:toLowerCase(product.type)] }线)
							</c:if>
							<c:if test="${productTranTypeAndBP[name]['jp'].transportType eq '2'}">
								<span class="icon-plane"></span>
							</c:if>
							<span style="color: gray">
								${fns:getDictLabel(isSaleMap[name_country],'product_position','')}
							</span>
							<span style="display: none">${fn:replace(product.type,'&','')}</span>
						</td>
						
						<c:set var="cntipMap" value="${inventorys[name].cnTipMap}" />
						<c:set var="notCntipMap" value="${inventorys[name].notCnTipMap}" />
						
						<c:set var="producting1"  value="${producting[name].inventorys['jp'].quantity}"/>
						<c:set var="transportting1"  value="${transportting[name].inventorys['jp'].quantity}"/>
						<c:set var="transportting2"  value="${preTransportting[name].inventorys['jp'].quantity}"/>
						
						<c:set var="inventorysCN"  value="${inventorys[name].inventorys['jp'].quantityInventory['CN'].newQuantity}"/>
						<c:set var="inventorysNotCN"  value="${inventorys[name].inventorys['jp'].quantityInventory['JP'].newQuantity}"/>
						
						<td><c:if test="${producting1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'${name}','jp');return false;">${producting1}</a></c:if></td>
						<c:set var="inventorysCNTotal"  value="${inventorys[name].totalQuantityCN}"/>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">${fn:length(inventorys[name].cnTip)>0?inventorysCNTotal:''}</a>
						</td>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap['jp']}">${fn:length(cntipMap['jp'])>0?inventorysCN:''}</a>
						</td>
						<td><c:if test="${transportting2>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'${name}','jp');return false;">${transportting2}</a></c:if></td>
						<td><c:if test="${transportting1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'${name}','jp');return false;">${transportting1}</a></c:if></td>
						<c:set var="inventorysNotCnTotal"  value="${inventorys[name].totalQuantityNotCN}"/>
						<td>
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${notCntipMap['jp']}">${fn:length(notCntipMap['jp'])>0?inventorysNotCN:''}</a>
						</td>
						<td>
							<c:set var="jp" value="${name}_jp" />
							<c:set var="jp"  value="${fbas[jp].total>0?fbas[jp].total:0}" />
							<c:set value="${jp}" var="fbaTotal" />
								${fbaTotal>0?fbaTotal:''}
						</td>
						<td>
							<c:set var="jp" value="${name}_jp" />
							<c:set var="jp"  value="${fbaTran[jp]>0?fbaTran[jp]:0}" />
							<c:set value="${jp}" var="fbaTrans" />
							<c:if test="${fbaTrans>0}">
							<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','jp');return false;">${fbaTrans}</a>
							</c:if>
						</td>
						<td>
							<c:set var="total" value="${fbaTotal+producting1+transportting1+inventorysCN+inventorysNotCN}" />
							${total>0?total:''}
						</td>
						<td>
							${productsMoqAndPrice[name].moq}
						</td>
						<td>
							${product.packQuantity}
						</td>
						<td>
							<c:set var="jp" value="${name}_jp" />
							<c:set var="jp"  value="${fancha[jp].day31Sales>0?fancha[jp].day31Sales:0}" />
							<c:set value="${jp}" var="total31Days" />
								${total31Days>0?total31Days:''}
						</td>
						<td>
							<c:if test="${total31Days/31 >0.5}"><fmt:formatNumber value="${total31Days/31}" maxFractionDigits="0" pattern="#0" /></c:if>
						</td>
						<td>
							<fmt:formatNumber value="${total31Days>0?total/total31Days:''}" maxFractionDigits="1"/>
						</td>
						<%--预测库销比 --%>
						<td>
							<fmt:formatNumber value="${inventorySalesMonthMap[name]['jp']>0?inventorySalesMonthMap[name]['jp']:''}" maxFractionDigits="1"/>
						</td>
						<shiro:hasPermission name="psi:inventory:stockPriceView"> 
						<td>
							${productsMoqAndPrice[name].price}
						</td>
						<td>
							<c:set var="singlePrice" value="${product.tempPartsTotalMap[name]>0?(productsMoqAndPrice[name].price+product.tempPartsTotalMap[name]):productsMoqAndPrice[name].price}" />
							<fmt:formatNumber value="${singlePrice}" maxFractionDigits="2" />
						</td>
						<td class="price">
							<fmt:formatNumber value="${singlePrice*total}" maxFractionDigits="2"/>
							<c:if test="${singlePrice>0}">
								<c:set value="${singlePrice*total+totalPrice}" var="totalPrice" />
							</c:if>
						</td>
						<td class="libraryAccounting"></td>
						</shiro:hasPermission>
						<shiro:lacksPermission name="psi:inventory:stockPriceView"> 
							<td>
								${salePrice[name]}
							</td>
							<td class="salePrice">
								<c:if test="${not empty salePrice[name]}">
									<fmt:formatNumber value="${salePrice[name]*total}" maxFractionDigits="2"/>
								</c:if>
							</td>
						</shiro:lacksPermission>
						
						<c:set value="${name}_eu"  var="tempKey"/>
						<td><c:if test="${not empty turnoverMap['jp'][name]&&turnoverMap['jp'][name].ePrice>0 }"><fmt:formatNumber value="${turnoverMap['jp'][name].sPrice/turnoverMap['jp'][name].ePrice}" maxFractionDigits="2"/></c:if></td>
						<td><c:if test='${not empty starandMap[tempKey] }'><fmt:formatNumber value="${ratio*starandMap[tempKey]/12}" maxFractionDigits="2"/></c:if></td>
						
						
						<c:set value="0"  var="yearTurnover"/>
						<td><c:set value="${name}_jp"  var="tempJPKey"/>
					    	<c:if test="${'4' ne isSaleMap[name_country] && !fn:contains(newTwoMonth,tempJPKey)}">
							   
								<c:set var="safeDay" value="0" />
								<c:set var="period" value="${fancha[tempKey].period+(empty productTranTypeAndBP[name]['jp'].bufferPeriod ? 0: productTranTypeAndBP[name]['jp'].bufferPeriod)}" />
								<c:if test="${fancha[tempKey].variance>0}">
										<c:set var="safe" value="${fns:roundUp(fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33)}" />
										<c:if test="${safe>0}">
										    <c:choose>
												<c:when test="${fancha[tempKey].forecastPreiodAvg >0 }">
													<c:set var="safeDay" value="${fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33/fancha[tempKey].forecastPreiodAvg}" />
												</c:when>
												<c:when test="${fancha[tempKey].day31Sales >0 }">
													<c:set var="safeDay" value="${(fancha[tempKey].periodSqrt*fancha[tempKey].variance*2.33)/(fancha[tempKey].day31Sales/31)}" />
												</c:when>
											</c:choose>
										</c:if>
							    </c:if> 
							    <c:if test="${fns:roundUp(safeDay)+period>0 }">
							        <fmt:formatNumber value="${ratio*365/(fns:roundUp(safeDay)+period)/12}" maxFractionDigits="2"/>
							        <c:set var="yearTurnover"  value="${365/(fns:roundUp(safeDay)+period)}"/>
							    </c:if>
							</c:if>		
						</td>
						<td>
						   <c:if test="${yearTurnover>0 }"> <fmt:formatNumber value="${yearTurnover}" maxFractionDigits="2"/></c:if>
						</td>
					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
			<shiro:hasPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					<td id="jpProduct1"></td><td></td>
					<td id="jpTitle1"></td>
					<td id="jpW1"></td><td id="jpT1"></td><td id="jpF1"></td><td id="jpF2"></td><td id="jpX1"></td>
					<td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td id="jpTotalPrice1">
						
					</td>
					<td id="jpPcent">
							
					</td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="jpProduct"></td><td></td>
					<td id="jpTitle"></td>
					<td id="jpW"></td><td id="jpT"></td><td id="jpF"></td><td id="jpFf"></td><td id="jpX">
					</td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td><td id="jpTotalPrice">
						<fmt:formatNumber value="${totalPrice}" maxFractionDigits="2" />
					</td>
					<td>
					</td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>	
			</shiro:hasPermission>
			<shiro:lacksPermission name="psi:inventory:stockPriceView">
				<tfoot>
				<tr>
					<td><b><spring:message code='psi_inventoty_current_page_totals' /></b></td>
					<td id="jpProduct1"></td><td></td>
					<td id="jpTitle1"></td>
					<td id="jpW1"></td><td id="jpT1"></td><td id="jpF1"></td><td id="jpF2"></td><td id="jpX1"></td>
					<td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				<tr>
					<td><b><spring:message code='psi_inventoty_all_totals' /></b></td>
					<td id="jpProduct"></td><td></td>
					<td id="jpTitle"></td>
					<td id="jpW"></td><td id="jpT"></td><td id="jpF"></td><td id="jpFf"></td><td id="jpX">
					</td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td><td></td>
					<td></td><td></td><td></td><td></td>
				</tr>	
				</tfoot>	
			</shiro:lacksPermission>
			</tbody>
		</table>
		</div>
		
		<div id="eu" class="tab-pane">
		<table  class="table table-striped table-bordered table-condensed dataTable" >
			<thead>
			  <tr>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code='amaInfo_businessReport_productName' />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_production' /></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_cn_stock' /><br/>(ALL)</th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_cn_stock' /><br/>(EU)</th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_shipment_pending' /></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
				   <th colspan="6" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_DE_stock' /></th>
				   <th colspan="3" style="vertical-align: middle;text-align: center;">DE FBA</th>
				   <th colspan="3" style="vertical-align: middle;text-align: center;">UK FBA</th>
				   <th colspan="3" style="vertical-align: middle;text-align: center;">FR FBA</th>
				   <th colspan="3" style="vertical-align: middle;text-align: center;">IT FBA</th>
				   <th colspan="3" style="vertical-align: middle;text-align: center;">ES FBA</th>
				   
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_replenishment' /></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="昨日开始往前滚动31天销量" style="color: #08c;">31Sell</a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(FBA在库总库存[不含在途]-安全库存)/滚动31日的日均销量" style="color: #08c;"><spring:message code='psi_inventory_form1' /></a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_Gross_stock' /></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(产品生产周期+运输周期)的预测销量 /(产品生产周期+运输周期)" style="color: #08c;"><spring:message code='psi_inventory_form2' /></a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(产品生产周期+运输周期)后1个月的预测销量 " style="color: #08c;"><spring:message code='psi_inventory_form3' /></a></th>
				   <th colspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_form4' /></th>
				   <%-- <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="总库存/(预测的生产周期日均销量或滚动31天的日均销量)-生产周期-运输周期-安全库存天数" style="color: #08c;"><spring:message code='psi_inventory_form6' /></a></th> --%>
				  	
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(预测的生产周期日均销量或滚动31天的日均销量)*(生产周期+运输周期)+安全库存数量" style="color: #08c;"><spring:message code='psi_inventory_ordered_stock' /></a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="总库存-下单点数量" style="color: #08c;"><spring:message code='psi_inventory_remaining_quantity' /></a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="结余/(预测的销售期日均销量或滚动31天的日均销量)" style="color: #08c;"><spring:message code='psi_inventory_Stock' /><br/><spring:message code='psi_inventory_form62' /></a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="销售期缓冲期销-结余 =下单量(按照整箱补足)" style="color: #08c;"><spring:message code='psi_inventory_ordered_quantity' /></a></th>
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="运输周期*预测的生产周期日均销量-FBA总库存-海外仓在库-在途" style="color: #08c;"><spring:message code='psi_inventory_form8' /></a></th>
				  	
				   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="生产周期+运输周期" style="color: #08c;"><spring:message code='psi_inventory_form7' /></a></th>
			   </tr>
			   <tr>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_fulfillable' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_renewed' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_old' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_Damaged' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_offline' /></th>
				   
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
				   <th style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="德、美、法、英四国亚马逊入仓时,会导致仓库数据不准确，仓库总值为系统提示的参考值" style="color: orange;"><spring:message code='psi_inventory_total' /></a></th>
				   
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
				   <th style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="德、美、法、英四国亚马逊入仓时,会导致仓库数据不准确，仓库总值为系统提示的参考值" style="color: orange;"><spring:message code='psi_inventory_total' /></a></th>
				   
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
				   <th style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="德、美、法、英四国亚马逊入仓时,会导致仓库数据不准确，仓库总值为系统提示的参考值" style="color: orange;"><spring:message code='psi_inventory_total' /></a></th>
				   
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_total' /></th>
				   
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_total' /></th>
				   
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_form5' /></th>
				   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_day' /></th>
			   </tr>
			</thead>
			<tbody>
			<c:forEach items="${list}" var="product" varStatus="i">
				<c:if test="${fn:containsIgnoreCase(product.platform,'fr')||fn:containsIgnoreCase(product.platform,'it')||fn:containsIgnoreCase(product.platform,'de')||fn:containsIgnoreCase(product.platform,'es')||fn:containsIgnoreCase(product.platform,'uk') }">
					<c:forEach items="${product.productNameWithColor}" var="name" varStatus="j">
						<c:if test="${!fn:contains(hiddens, name) }">
						<c:set value="${name}_eu"  var="key"/>
						<c:set value="${name}_de"  var="keyDe"/>
						<c:set value="${name}_uk"  var="keyUk"/>
						<c:set value="${name}_fr"  var="keyFr"/>
						<c:set value="${name}_es"  var="keyEs"/>
						<c:set value="${name}_it"  var="keyIt"/>
						<c:set value="0"  var="total"/>
						<c:set var="cntipMap" value="${inventorys[name].cnTipMap}" />
						<c:set var="notCntipMap" value="${inventorys[name].notCnTipMap}" />
						<tr >
							<td  class="name">
								<b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target="_blank">${fn:replace(name,'Inateck','')}</a></b>
								<span style="display: none">${not empty isNewMap[key]?'新品':''}" </span>
								<c:if test="${not empty typeLineMap[fn:toLowerCase(product.type)]}">
									(${typeLineMap[fn:toLowerCase(product.type)] }线)
								</c:if>
								<c:if test="${productTranTypeAndBP[name]['eu'].transportType eq '2'}">
									<span class="icon-plane"></span>
								</c:if>
								<span style="color: gray">
									${fns:getDictLabel(isSaleMap[key],'product_position','')}
								</span>
								<span style="display: none">${fn:replace(product.type,'&','')}</span>
							</td>
							<c:set value="${producting[name].quantityEuro}" var="productVar" />
							<c:set value="${inventorys[name].quantityEuro['CN'].newQuantity}" var="cnEuro" />
							<c:set value="${transportting[name].quantityEuro}" var="transportVar" />
							<c:set value="${preTransportting[name].quantityEuro}" var="transportVar1" />
							<c:set value="${inventorys[name].quantityEuro['DE'].newQuantity}" var="deNew" />
							<c:set value="${inventorys[name].quantityEuro['DE'].renewQuantity}" var="deRe" />
							<c:set value="${inventorys[name].quantityEuro['DE'].oldQuantity}" var="deOld" />
							<c:set value="${inventorys[name].quantityEuro['DE'].brokenQuantity}" var="deBr" />
							<c:set value="${inventorys[name].quantityEuro['DE'].offlineQuantity}" var="deOff" />
							
							<td><c:if test="${productVar>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'${name}','eu');return false;">${productVar}</a></c:if><c:set value="${total+productVar}"  var="total"/></td>
							
							<c:set var="inventorysCN"  value="${inventorys[name].totalQuantityCN}"/>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">${fn:length(inventorys[name].cnTip)>0?inventorysCN:''}</a>
							</td>
							
							
							<td>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${inventorys[name].inventorys['de'].quantityInventory['CN'].newQuantity}[${cntipMap['de']}]<br/>UK:${inventorys[name].inventorys['uk'].quantityInventory['CN'].newQuantity}[${cntipMap['uk']}]<br/>FR:${inventorys[name].inventorys['fr'].quantityInventory['CN'].newQuantity}[${cntipMap['fr']}]<br/>IT:${inventorys[name].inventorys['it'].quantityInventory['CN'].newQuantity}[${cntipMap['it']}]<br/>ES:${inventorys[name].inventorys['es'].quantityInventory['CN'].newQuantity}[${cntipMap['es']}]">
									${cnEuro>0?cnEuro:''}
								</a>
								<c:set value="${total+cnEuro}"  var="total"/>
							</td>
							<td><c:if test="${transportVar1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'${name}','eu');return false;">${transportVar1>0?transportVar1:''}</a></c:if></td>
							<td><c:if test="${transportVar>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'${name}','eu');return false;">${transportVar>0?transportVar:''}</a></c:if><c:set value="${total+transportVar}"  var="total"/></td>
							<td>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${inventorys[name].inventorys['de'].quantityInventory['DE'].newQuantity}[${notCntipMap['de']}]<br/>UK:${inventorys[name].inventorys['uk'].quantityInventory['DE'].newQuantity}[${notCntipMap['uk']}]<br/>FR:${inventorys[name].inventorys['fr'].quantityInventory['DE'].newQuantity}[${notCntipMap['fr']}]<br/>IT:${inventorys[name].inventorys['it'].quantityInventory['DE'].newQuantity}[${notCntipMap['it']}]<br/>ES:${inventorys[name].inventorys['es'].quantityInventory['DE'].newQuantity}[${notCntipMap['es']}]">
									${deNew>0?deNew:''}
								</a>
								<c:set value="${total+deNew}"  var="total"/>
							</td>
							<td>
								<c:if test="${(fbaWorkingByEuro[name]==null?0:fbaWorkingByEuro[name])!=0}">
									<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(5,'${name}','eu');return false;">
										${deNew-(fbaWorkingByEuro[name]==null?0:fbaWorkingByEuro[name])}
									</a>
								</c:if>
								<c:if test="${(fbaWorkingByEuro[name]==null?0:fbaWorkingByEuro[name])==0}">
									${deNew}
								</c:if>
							</td>
							<td>${deRe>0?deRe:''}</td>
							<td>${deOld>0?deOld:''}</td>
							<td>${deBr>0?deBr:''}</td>
							<td>${deBr>0?deOff:''}</td>
							<td>			
								<a  style="color: #08c;" class="fbaTip" >${fbas[keyDe].fulfillableQuantity>0?fbas[keyDe].fulfillableQuantity:''}</a>	
								<input class="country" value="de" type="hidden" />
								<input class="name" value="${name}" type="hidden" />
							</td>
							<td><c:if test="${fbaTran[keyDe]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','de');return false;">${fbaTran[keyDe]}</a></c:if></td>
							<td>
								<c:set value="" var="day" />
								<c:if test="${fancha[keyDe].day31Sales >0}">
									<c:set value="${fns:roundUp(fbas[keyDe].fulfillableQuantity/(fancha[keyDe].day31Sales/31))}" var="day" />
								</c:if>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="31日销:${fancha[keyDe].day31Sales}<br/>FBA在库库存(不含在途)可销天:${day}">
									${fbas[keyDe].total}
								</a>
								<c:set value="${fbas[keyDe].total}"  var="totalFba"/>
								<c:set value="${fbas[keyDe].fulfillableQuantity>0?fbas[keyDe].fulfillableQuantity:0}"  var="fba"/>
							</td>
							<td>
								<a  style="color: #08c;" class="fbaTip" >${fbas[keyUk].fulfillableQuantity>0?fbas[keyUk].fulfillableQuantity:''}</a>	
								<input class="country" value="uk" type="hidden" />
								<input class="name" value="${name}" type="hidden" />
							</td>
							<td><c:if test="${fbaTran[keyUk]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','uk');return false;">${fbaTran[keyUk]}</a></c:if></td>
							<td>
								<c:set value="" var="day" />
								<c:if test="${fancha[keyUk].day31Sales >0}">
									<c:set value="${fns:roundUp(fbas[keyUk].fulfillableQuantity/(fancha[keyUk].day31Sales/31))}" var="day" />
								</c:if>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="31日销:${fancha[keyUk].day31Sales}<br/>FBA在库库存(不含在途)可销天:${day}">
									${fbas[keyUk].total}
								</a>
								<c:set value="${totalFba+fbas[keyUk].total}"  var="totalFba"/>
								<c:set value="${fba+(fbas[keyUk].fulfillableQuantity>0?fbas[keyUk].fulfillableQuantity:0)}"  var="fba"/>
							</td>
							<td>
								<a  style="color: #08c;" class="fbaTip" >${fbas[keyFr].fulfillableQuantity>0?fbas[keyFr].fulfillableQuantity:''}</a>	
								<input class="country" value="fr" type="hidden" />
								<input class="name" value="${name}" type="hidden" />
							</td>
							<td><c:if test="${fbaTran[keyFr]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','fr');return false;">${fbaTran[keyFr]}</a></c:if></td>
							<td>
								<c:set value="" var="day" />
								<c:if test="${fancha[keyFr].day31Sales >0}">
									<c:set value="${fns:roundUp(fbas[keyFr].fulfillableQuantity/(fancha[keyFr].day31Sales/31))}" var="day" />
								</c:if>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="31日销:${fancha[keyFr].day31Sales}<br/>FBA在库库存(不含在途)可销天:${day}">
									${fbas[keyFr].total}
								</a>
								<c:set value="${totalFba+fbas[keyFr].total}"  var="totalFba"/>
								<c:set value="${fba+(fbas[keyFr].fulfillableQuantity>0?fbas[keyFr].fulfillableQuantity:0)}"  var="fba"/>
							</td>
							<td>
								<a  style="color: #08c;" class="fbaTip" >${fbas[keyIt].fulfillableQuantity>0?fbas[keyIt].fulfillableQuantity:''}</a>	
								<input class="country" value="it" type="hidden" />
								<input class="name" value="${name}" type="hidden" />
							</td>
							<td><c:if test="${fbaTran[keyIt]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','it');return false;">${fbaTran[keyIt]}</a></c:if></td>
							<td>
								<c:set value="" var="day" />
								<c:if test="${fancha[keyIt].day31Sales >0}">
									<c:set value="${fns:roundUp(fbas[keyIt].fulfillableQuantity/(fancha[keyIt].day31Sales/31))}" var="day" />
								</c:if>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="31日销:${fancha[keyIt].day31Sales}<br/>FBA在库库存(不含在途)可销天:${day}">
									${fbas[keyIt].total}
								</a>
								<c:set value="${totalFba+fbas[keyIt].total}"  var="totalFba"/>
								<c:set value="${fba+(fbas[keyIt].fulfillableQuantity>0?fbas[keyIt].fulfillableQuantity:0)}"  var="fba"/>
							</td>
							<td>
								<a  style="color: #08c;" class="fbaTip" >${fbas[keyEs].fulfillableQuantity>0?fbas[keyEs].fulfillableQuantity:''}</a>	
								<input class="country" value="es" type="hidden" />
								<input class="name" value="${name}" type="hidden" />
							</td>
							<td><c:if test="${fbaTran[keyEs]>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','es');return false;">${fbaTran[keyEs]}</a></c:if></td>
							<td>
								<c:set value="" var="day" />
								<c:if test="${fancha[keyEs].day31Sales >0}">
									<c:set value="${fns:roundUp(fbas[keyEs].fulfillableQuantity/(fancha[keyEs].day31Sales/31))}" var="day" />
								</c:if>
								<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="31日销:${fancha[keyEs].day31Sales}<br/>FBA在库库存(不含在途)可销天:${day}">
									${fbas[keyEs].total}
								</a>
								<c:set value="${totalFba+fbas[keyEs].total}"  var="totalFba"/>
								<c:set value="${fba+(fbas[keyEs].fulfillableQuantity>0?fbas[keyEs].fulfillableQuantity:0)}"  var="fba"/>
							</td>
							<c:set var="period" value="${fancha[key].period+(empty productTranTypeAndBP[name]['eu'].bufferPeriod ? 0: productTranTypeAndBP[name]['eu'].bufferPeriod)}" />
							
							<c:set var="safe" value="0"></c:set>
							<c:if test="${fancha[key].variance>0}">
								<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
								<c:choose>
									<c:when test="${fancha[key].forecastPreiodAvg >0 }">
										<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
									</c:when>
									<c:when test="${fancha[key].day31Sales >0 }">
										<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
									</c:when>
								</c:choose>
							</c:if> 
							<c:set value="0" var="point"/>
							<c:choose>
								<c:when test="${fancha[key].forecastPreiodAvg >0 }">
									<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
								</c:when>
								<c:when test="${fancha[key].day31Sales >0 }">
									<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
								</c:when>
							</c:choose>
							<c:set value="${total+totalFba-point}"  var="jy"/>
							
							<td style="vertical-align: middle;text-align: center;">
								<c:set value="${(fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31)}" var="fbaDay"></c:set>
								<c:set var="que" value="${not empty queMap[keyDe]}"  />
								<shiro:hasPermission name="psi:fbaInbound:edit">
								   <a style="height:14px; font-size:12px; line-height:12px;" class="btn${que?' btn-danger ':' '}btn-small" href="#" onclick="displayinboud('de','${name}');return false;"><spring:message code='psi_inventory_replenishment' /></a>
								</shiro:hasPermission>
								<shiro:lacksPermission name="psi:fbaInbound:edit">
								  <a style="height:14px; font-size:12px; line-height:12px;" class="btn${que?' btn-danger ':' '}btn-small" href="#"><spring:message code='psi_inventory_replenishment' /></a>
								</shiro:lacksPermission>
								
								<c:if test="${que}"><input type="hidden" value="缺"></c:if>
							</td>
							
							<td>${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
							
							
							<td>
								<c:if test="${fancha[key].day31Sales >0 }">
									<fmt:formatNumber maxFractionDigits="0"  value="${(fba-safe)/(fancha[key].day31Sales/31)}" pattern="#0" />
								</c:if>
							</td>
							
							<td>${total+totalFba}</td>
							
							<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
							<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
							<td>
								<a href="#" data-toggle="tooltip"  title="方差:${fancha[key].variance}" style="color: #08c;">${safe}</a>
							</td>
							<td>
								<c:if test="${safe>0}">
									${fns:roundUp(safeDay)}
								</c:if>
							</td>
							<!-- 2.27日加入 -->
							<td>
								<c:if test="${point>0}">
									<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
								</c:if>
							</td>
							<td>
								<c:if test="${jy!=0}">
									<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
								</c:if>
							</td>
							<td>
								<c:choose>
									<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
										<c:set var="bu" value="${jy<0}" />
										<span class="${bu?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
									</c:when>
									<c:when test="${fancha[key].day31Sales >0 }">
										<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
										<span class="${bu?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}"  pattern="#0"/></span>
									</c:when>
								</c:choose>
								<c:if test="${bu}">
									<input type="hidden" value="红">
								</c:if>
							</td>
							<td>
								<c:if test="${-jy>0}">
									<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
								</c:if>
							</td>
							<td>
								<c:set value="0" var="sky"></c:set>
								<c:choose>
									<c:when test="${fancha[key].forecastPreiodAvg >0 }">
										<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-totalFba-deNew-transportVar}" var="sky"></c:set>
									</c:when>
									<c:when test="${fancha[key].day31Sales >0 }">
										<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-totalFba-deNew-transportVar}" var="sky"></c:set>
									</c:when>
								</c:choose>
								<c:if test="${sky>0}">
									${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
								</c:if>
							</td>
							
							<%-- <td>
								<c:set var="bu" value="false" />
								<c:choose>
									<c:when test="${fancha[key].forecastPreiodAvg >0 }">
										<c:set var="bu" value="${((total+totalFba)/fancha[key].forecastPreiodAvg -fancha[key].period-safeDay)<0}" />
										<span class="${bu?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${(total+totalFba)/fancha[key].forecastPreiodAvg -fancha[key].period-safeDay}" pattern="#0" /></span>
									</c:when>
									<c:when test="${fancha[key].day31Sales >0 }">
										<c:set var="bu" value="${(total+totalFba)/(fancha[key].day31Sales/31)-fancha[key].period-safeDay<0}" />
										<span class="${bu?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${(total+totalFba)/(fancha[key].day31Sales/31)-fancha[key].period-safeDay}"  pattern="#0"/></span>
									</c:when>
								</c:choose>
								<c:if test="${bu}">
									<input type="hidden" value="红">
								</c:if>
							</td> --%>
							<td>${period}<span style="color: red;">(${(empty productTranTypeAndBP[name]['eu'].bufferPeriod ?0:productTranTypeAndBP[name]['eu'].bufferPeriod)})</span></td>
						</tr>
						</c:if>
					</c:forEach>
				</c:if>
			</c:forEach>
			</tbody>
		</table>
		</div>
	
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<div id="${dic.value}" class="tab-pane">
					<table  class="table table-striped table-bordered table-condensed dataTable">
						<thead>
						   <tr>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<spring:message code='amaInfo_businessReport_productName' />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_production' /></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_cn_stock' /><br/>(ALL)</th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_cn_stock' /></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_shipment_pending' /></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
							   <th colspan="${fn:containsIgnoreCase('fr,de,uk,it,es',dic.value)?8:6}" style="vertical-align: middle;text-align: center;">
							   		<c:choose>
							   			<c:when test="${fn:containsIgnoreCase('fr,de,uk,it,es',dic.value)}"><spring:message code='psi_inventory_DE_stock' /></c:when>
							   			<c:when test="${fn:containsIgnoreCase('com,ca,mx,com2,com3',dic.value)}"><spring:message code='psi_inventory_US_stock' /></c:when>
							   			<c:when test="${fn:containsIgnoreCase('jp',dic.value)}"><spring:message code='psi_inventory_JP_stock' /></c:when>
							   		</c:choose>
							   </th>
							   <th colspan="4" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_FBA_stock' /></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;background-color:#D2E9FF;"><a href="#" data-toggle="tooltip" title="昨日开始往前滚动31天销量" style="color: #08c;"><spring:message code='psi_inventory_sales_within_31_days' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(FBA在库库存[不含在途]-安全库存)/滚动31日的日均销量" style="color: #08c;"><spring:message code='psi_inventory_form1' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_Gross_stock' /></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(产品生产周期+运输周期)的预测销量 /(产品生产周期+运输周期)" style="color: #08c;"><spring:message code='psi_inventory_form2' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(产品生产周期+运输周期)后1个月的预测销量 " style="color: #08c;"><spring:message code='psi_inventory_form3' /></a></th>
							   <th colspan="2" style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_form4' /></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="(预测的生产周期日均销量或滚动31天的日均销量)*(生产周期+运输周期)+安全库存数量" style="color: #08c;"><spring:message code='psi_inventory_ordered_stock' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="总库存-下单点数量" style="color: #08c;"><spring:message code='psi_inventory_remaining_quantity' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="结余/(预测的销售期日均销量或滚动31天的日均销量)" style="color: #08c;"><spring:message code='psi_inventory_Stock' /><br/><spring:message code='psi_inventory_form62' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="销售期缓冲期销-结余 =下单量(按照整箱补足)" style="color: #08c;"><spring:message code='psi_inventory_ordered_quantity' /></a></th>
							   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="运输周期*预测的生产周期日均销量-FBA总库存-海外仓在库-在途" style="color: #08c;"><spring:message code='psi_inventory_form8' /></a></th>
				 			   <th rowspan="2" style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="生产周期+运输周期" style="color: #08c;"><spring:message code='psi_inventory_form7' /></a></th>
						   </tr>
						   <tr>
						   	   <c:if test="${fn:containsIgnoreCase('fr,de,uk,it,es',dic.value)}">
						   		<th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' />(EU)</th>
							   	<th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_fulfillable' />(EU)</th>
						   	   </c:if>
							  
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_real' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_fulfillable' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_renewed' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_old' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_Damaged' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_offline' /></th>
							   
							   <th style="vertical-align: middle;text-align: center;background-color:#D2E9FF;"><spring:message code='psi_inventory_real' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_in_transit' /></th>
							   <th style="vertical-align: middle;text-align: center;"><a href="#" data-toggle="tooltip" title="德、美、法、英四国亚马逊入仓时,会导致仓库数据不准确，仓库总值为系统提示的参考值" style="color: orange;"><spring:message code='psi_inventory_total' /></a></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_replenishment' /></th>
							   
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_form5' /></th>
							   <th style="vertical-align: middle;text-align: center;"><spring:message code='psi_inventory_day' /></th>
						   </tr>
						</thead>
						<tbody>
							<c:forEach items="${list}" var="product" varStatus="i">
								<c:if test="${fn:containsIgnoreCase(product.platform,dic.value)}">
									<c:forEach items="${product.productNameWithColor}" var="name" varStatus="j">
										<c:if test="${!fn:contains(hiddens, name) }">
										<c:set value="${name}_${dic.value}"  var="key"/>
										<c:choose>
											<c:when test="${fn:containsIgnoreCase('fr,de,uk,es,it',dic.value)}">
												<c:set value="DE"  var="keyStock"/>
											</c:when>
											<c:when test="${fn:containsIgnoreCase('com,ca,mx,com2,com3',dic.value)}">
												<c:set value="US"  var="keyStock"/>
											</c:when>
											<c:when test="${fn:containsIgnoreCase('jp',dic.value)}">
												<c:set value="JP"  var="keyStock"/>
											</c:when>
											<c:otherwise>
												<c:set value=""  var="keyStock"/>
											</c:otherwise>
										</c:choose>
										
										<c:set value="0"  var="total"/>
										
										<tr >
											<td  class="name">
												<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}" target="_blank"><b style="font-size: 14px">${fn:replace(name,'Inateck','')}</b></a>
												<span style="display: none">${not empty isNewMap[key]?'新品':''} </span>
												<c:if test="${not empty typeLineMap[fn:toLowerCase(product.type)]}">
													(${typeLineMap[fn:toLowerCase(product.type)] }线)
												</c:if>
												<c:if test="${productTranTypeAndBP[name][dic.value].transportType eq '2'}">
													<span class="icon-plane"></span>
												</c:if>
											     <span style="color: gray">
													${fns:getDictLabel(isSaleMap[key],'product_position','')}
												 </span>
												<span style="display: none">${fn:replace(product.type,'&','')}</span>
											</td>
											
											<c:set value="${producting[name].inventorys[dic.value].quantity}" var="productVar" />
											<c:set value="${inventorys[name].inventorys[dic.value].quantityInventory['CN'].newQuantity}" var="cn" />
											<c:set value="${transportting[name].inventorys[dic.value].quantity}" var="transportVar" />
											<c:set value="${preTransportting[name].inventorys[dic.value].quantity}" var="transportVar1" />
											<c:set value="${inventorys[name].inventorys[dic.value].quantityInventory[keyStock].newQuantity}" var="deNew" />
											<c:set value="${inventorys[name].inventorys[dic.value].quantityInventory[keyStock].renewQuantity}" var="deRe" />
											<c:set value="${inventorys[name].inventorys[dic.value].quantityInventory[keyStock].oldQuantity}" var="deOld" />
											<c:set value="${inventorys[name].inventorys[dic.value].quantityInventory[keyStock].brokenQuantity}" var="deBr" />
											<c:set value="${inventorys[name].inventorys[dic.value].quantityInventory[keyStock].offlineQuantity}" var="deOff" />
											<c:set var="cntipMap" value="${inventorys[name].cnTipMap}" />
											<c:set var="notCntipMap" value="${inventorys[name].notCnTipMap}" />
											
											
											<td><c:if test="${productVar>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(3,'${name}','${dic.value}');return false;">${productVar}</a></c:if><c:set value="${total+productVar}"  var="total"/></td>
											
											<c:set var="inventorysCN"  value="${inventorys[name].totalQuantityCN}"/>
											<td>
												<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${inventorys[name].cnTip}">${fn:length(inventorys[name].cnTip)>0?inventorysCN:''}</a>
											</td>
											<td>
												<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${cntipMap[dic.value]}">
													${fn:length(cntipMap[dic.value])>0?cn:''}
												</a>										
												
												<c:set value="${total+cn}"  var="total"/>
											</td>
											<td><c:if test="${transportVar1>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-info btn-small" href="#" onclick="display_by(4,'${name}','${dic.value}');return false;">${transportVar1}</a></c:if></td>
											
											<td><c:if test="${transportVar>0}"><a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(1,'${name}','${dic.value}');return false;">${transportVar}</a></c:if><c:set value="${total+transportVar}"  var="total"/></td>
											<c:if test="${fn:containsIgnoreCase('fr,de,uk,it,es',dic.value)}">
												<c:set value="${inventorys[name].quantityEuro['DE'].newQuantity}" var="euNew" />
												<td>
													<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${inventorys[name].inventorys['de'].quantityInventory[keyStock].newQuantity}[${notCntipMap['de']}]<br/>UK:${inventorys[name].inventorys['uk'].quantityInventory[keyStock].newQuantity}[${notCntipMap['uk']}]<br/>FR:${inventorys[name].inventorys['fr'].quantityInventory[keyStock].newQuantity}[${notCntipMap['fr']}]<br/>IT:${inventorys[name].inventorys['it'].quantityInventory[keyStock].newQuantity}[${notCntipMap['it']}]<br/>ES:${inventorys[name].inventorys['es'].quantityInventory[keyStock].newQuantity}[${notCntipMap['es']}]">
														${euNew>0?euNew:''}
													</a>
												</td> 
												<td>
													<c:if test="${(fbaWorkingByEuro[name]==null?0:fbaWorkingByEuro[name])!=0}">
														<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(5,'${name}','eu');return false;">
															${euNew-(fbaWorkingByEuro[name]==null?0:fbaWorkingByEuro[name])}
														</a>
													</c:if>
													<c:if test="${(fbaWorkingByEuro[name]==null?0:fbaWorkingByEuro[name])==0}">
														${euNew}
													</c:if>
												</td>		 
											</c:if>
											<td>
												<a style="color: #08c;" href="#" data-toggle="popover" data-html="true" rel="popover" data-content="${notCntipMap[dic.value]}">
													${deNew>0?deNew:''}
												</a>
												<c:set value="${total+deNew}"  var="total"/>
											</td>
											<td>
												<c:if test="${(fbaWorking[key][2]==null?0:fbaWorking[key][2])!=0}">
												    <a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(5,'${name}','${dic.value}');return false;">
														${deNew-(fbaWorking[key][2]==null?0:fbaWorking[key][2])}
												    </a>
												</c:if>
												<c:if test="${(fbaWorking[key][2]==null?0:fbaWorking[key][2])==0}">
													${deNew}
												</c:if>
											</td>
											<td>${deRe>0?deRe:''}</td>
											<td>${deOld>0?deOld:''}</td>
											<td>${deBr>0?deBr:''}</td>
											<td>${deOff>0?deOff:''}</td>
											<td style="background-color:#D2E9FF;">
												<a  style="color: #08c;" class="fbaTip" >${fbas[key].fulfillableQuantity>0?fbas[key].fulfillableQuantity:''}</a>	
												<input class="country" value="${dic.value}" type="hidden" />
												<input class="name" value="${name}" type="hidden" />
													
											</td>
											<td>
												<c:if test="${fbaTran[key]>0}">
													<a style="height:14px; font-size:12px; line-height:12px;" class="btn btn-warning btn-small" href="#" onclick="display_by(2,'${name}','${dic.value}');return false;">${fbaTran[key]}</a>
												</c:if>
											</td>
											<td>${fbas[key].total}</td>
											<td style="vertical-align: middle;text-align: center;">
												<c:set var="safe" value="0"/>
												<c:if test="${fancha[key].variance>0}">
													<c:set var="safe" value="${fns:roundUp(fancha[key].periodSqrt*fancha[key].variance*2.33)}"></c:set>
												</c:if> 
												<c:set value="${(fbas[key].fulfillableQuantity-safe)/(fancha[key].day31Sales/31)}" var="fbaDay"></c:set>
												
												<c:set var="que" value="${(dic.value eq 'de' || dic.value eq 'fr' || dic.value eq 'it' || dic.value eq 'es' || dic.value eq 'uk') && not empty queMap[key]}"  />
												
												<c:if test="${dic.value eq 'de' || dic.value eq 'com'|| dic.value eq 'ca' || dic.value eq 'jp'|| dic.value eq 'mx' || ('uk' eq dic.value && '0' ne fanOuMap[name]) || (dic.value ne 'com2'&&dic.value ne 'com3'&&'2' eq fanOuMap[name]) }">
												   <shiro:hasPermission name="psi:fbaInbound:edit">
												      <a style="height:14px; font-size:12px; line-height:12px;" class="btn${que || ((dic.value eq 'ca' || dic.value eq 'com' || dic.value eq 'jp'|| dic.value eq 'mx')&& fbaDay<=45)?' btn-danger ':' '}btn-small" href="#" onclick="displayinboud('${dic.value}','${name}');return false;"><spring:message code='psi_inventory_replenishment' /></a>
												   </shiro:hasPermission>
												   <shiro:lacksPermission name="psi:fbaInbound:edit">
												      <a style="height:14px; font-size:12px; line-height:12px;" class="btn${que || ((dic.value eq 'ca' || dic.value eq 'com' || dic.value eq 'jp'|| dic.value eq 'mx')&& fbaDay<=45)?' btn-danger ':' '}btn-small" href="#"><spring:message code='psi_inventory_replenishment' /></a>
												   </shiro:lacksPermission>
												</c:if>
												
												 <shiro:hasPermission name="psi:fbaInbound:editNewUS">
													  <c:if test="${dic.value eq 'com2'}">
													     <a style="height:14px; font-size:12px; line-height:12px;" class="btn${que || ((dic.value eq 'ca' || dic.value eq 'com2' || dic.value eq 'jp'|| dic.value eq 'mx')&& fbaDay<=45)?' btn-danger ':' '}btn-small" href="#" onclick="displayinboud('${dic.value}','${name}');return false;"><spring:message code='psi_inventory_replenishment' /></a>
													  </c:if>
												 </shiro:hasPermission>
												 
												  <shiro:hasPermission name="psi:fbaInbound:editNewUS3">
													  <c:if test="${dic.value eq 'com3'}">
													     <a style="height:14px; font-size:12px; line-height:12px;" class="btn${que || ((dic.value eq 'ca' || dic.value eq 'com3' || dic.value eq 'jp'|| dic.value eq 'mx')&& fbaDay<=45)?' btn-danger ':' '}btn-small" href="#" onclick="displayinboud('${dic.value}','${name}');return false;"><spring:message code='psi_inventory_replenishment' /></a>
													  </c:if>
												 </shiro:hasPermission>
												
												<c:if test="${que || ((dic.value eq 'ca' || dic.value eq 'com'  || dic.value eq 'com2' || dic.value eq 'jp'|| dic.value eq 'mx')&& fbaDay<=45)}"><input type="hidden" value="缺"></c:if>
											</td>
											<td style="background-color:#D2E9FF;">${fancha[key].day31Sales>0?fancha[key].day31Sales:''}</td>
											<td>
												<c:if test="${fancha[key].day31Sales >0 }">
													<span class="${fbaDay>=105?'badge badge-info':''}" ><fmt:formatNumber maxFractionDigits='0'  value='${fbaDay}' pattern='#0'/></span>
												</c:if>
												<c:if test="${fbaDay>=105}"><input type="hidden" value="滞销"></c:if>
											</td>
											<td>${total+fbas[key].total>0?total+fbas[key].total:''}</td>
											<c:if test="${safe>0}">
												<c:choose>
													<c:when test="${fancha[key].forecastPreiodAvg >0 }">
														<c:set var="safeDay" value="${fancha[key].periodSqrt*fancha[key].variance*2.33/fancha[key].forecastPreiodAvg}" />
													</c:when>
													<c:when test="${fancha[key].day31Sales >0 }">
														<c:set var="safeDay" value="${(fancha[key].periodSqrt*fancha[key].variance*2.33)/(fancha[key].day31Sales/31)}" />
													</c:when>
												</c:choose>
											</c:if>
											<c:set var="bu" value="false" />
											<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastPreiodAvg}" pattern="#0" /></td>
											<td><fmt:formatNumber maxFractionDigits="0"  value="${fancha[key].forecastAfterPreiodSalesByMonth}" pattern="#0" /></td>
											<td>
												<a href="#" data-toggle="tooltip" title="方差:${fancha[key].variance}" style="color: #08c;">${safe}</a>
											</td>
											<td>
												<c:if test="${safe>0}">
													${fns:roundUp(safeDay)}
												</c:if>
											</td>
											<td>
												<c:set var="period" value="${fancha[key].period+(empty productTranTypeAndBP[name][dic.value].bufferPeriod ? 0: productTranTypeAndBP[name][dic.value].bufferPeriod)}" />
												<c:set value="0" var="point"/>
												<c:choose>
													<c:when test="${fancha[key].forecastPreiodAvg >0 }">
														<c:set value="${fancha[key].forecastPreiodAvg*period+safe}" var="point"></c:set>
													</c:when>
													<c:when test="${fancha[key].day31Sales >0 }">
														<c:set value="${(fancha[key].day31Sales/31)*period+safe}" var="point"></c:set>
													</c:when>
												</c:choose>
												<c:if test="${point>0}">
													<fmt:formatNumber maxFractionDigits="0"  value="${point}" pattern="#0" />
												</c:if>
											</td>
											<td>
												<c:set value="${total+fbas[key].total-point}"  var="jy"/>
												<c:if test="${jy!=0}">
													<span class="${jy<=0?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy}" pattern="#0" /></span>
												</c:if>
											</td>
											<td>
												<c:choose>
													<c:when test="${fancha[key].forecastAfterPreiodSalesByMonth >0 }">
														<c:set var="bu" value="${jy<0}" />
														<span class="${bu?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].forecastAfterPreiodSalesByMonth/31)}" pattern="#0" /></span>
													</c:when>
													<c:when test="${fancha[key].day31Sales >0 }">
														<c:set var="bu" value="${jy/(fancha[key].day31Sales/31)<0}" />
														<span class="${bu?'badge badge-important':''}"><fmt:formatNumber maxFractionDigits="0"  value="${jy/(fancha[key].day31Sales/31)}"  pattern="#0"/></span>
													</c:when>
												</c:choose>
												<c:if test="${bu}">
													<input type="hidden" value="红">
												</c:if>
											</td>
											<td>
												<c:if test="${-jy>0}">
													<span class="${jy<=0?'badge badge-important':''}">${(fns:roundUp((-jy)/product.packQuantity)*product.packQuantity)}</span>
												</c:if>
											</td>
											<td>
												<c:set value="0" var="sky"></c:set>
												<c:choose>
													<c:when test="${fancha[key].forecastPreiodAvg >0 }">
														<c:set value="${(period-product.producePeriod)*fns:roundUp(fancha[key].forecastPreiodAvg)-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
													</c:when>
													<c:when test="${fancha[key].day31Sales >0 }">
														<c:set value="${(period-product.producePeriod)*fns:roundUp((fancha[key].day31Sales/31))-fbas[key].total-deNew-transportVar}" var="sky"></c:set>
													</c:when>
												</c:choose>
												<c:if test="${sky>0}">
													${fns:roundUp(sky/product.packQuantity)*product.packQuantity}
												</c:if>
											</td>
											<td>${period}<span style="color: red;">(${(empty productTranTypeAndBP[name][dic.value].bufferPeriod ?0:productTranTypeAndBP[name][dic.value].bufferPeriod)})</span></td>
										</tr>
										</c:if>
									</c:forEach>
								</c:if>
							</c:forEach>
						</tbody>
					</table>
					</div>
			</c:if>
		</c:forEach>
	</div>

	<div id="createinboud_ca" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> CA FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_ca" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="ca" name="country" />
		<div class="modal-body">
				Address:<select name="docAddress">
					<option value="CN">CHINA</option>
					<option value="US">USA</option>
				</select>
				&nbsp;&nbsp;
				Account:<select name="accountName">
					<c:forEach items="${accountMap['ca']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				
				&nbsp;&nbsp;CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				
				<br/>
				<br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_mx" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> MX FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_mx" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="mx" name="country" />
		<div class="modal-body">
		       Address:<select name="docAddress">
					<option value="CN">CHINA</option>
					<option value="US">USA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['mx']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp;CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="提交">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_com" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> US FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_com" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="com" name="country" />
		<div class="modal-body">
				Address:<select name="docAddress">
					<option value="CN">CHINA</option>
					<option value="US">USA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['com']}" var="account">
					    <c:if test="${'Inateck_US' ne account}">
					        <option value='${account}'>${account}</option>
					    </c:if>
					</c:forEach>
				</select>
				&nbsp;&nbsp;CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_com2" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> NEW US FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_com2" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="com2" name="country" />
		<div class="modal-body">
				Address:<select name="docAddress">
					<option value="CN">CHINA</option>
					<option value="US">USA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['com2']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp; CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	
	<div id="createinboud_com3" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> NEW US_Tomons FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_com3" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="com3" name="country" />
		<div class="modal-body">
				Address:<select name="docAddress">
					<option value="CN">CHINA</option>
					<option value="US">USA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['com3']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp;CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_de" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> DE FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_de" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="de" name="country" />
		<div class="modal-body">
				Address:<select name="docAddress">
					<option value="DE">DE</option>
					<option value="CN">CHINA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['de']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp;CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_it" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> IT FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_it" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="it" name="country" />
		<div class="modal-body">
			Address:<select name="docAddress">
					<option value="DE">DE</option>
					<option value="CN">CHINA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['it']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp;CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	<div id="createinboud_es" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> ES FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_es" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="es" name="country" />
		<div class="modal-body">
				Address:<select name="docAddress">
					<option value="DE">DE</option>
					<option value="CN">CHINA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['es']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp; CreateByPlan: <input class="plan"  type="checkbox">
				<input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_jp" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> JP FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_jp" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="jp" name="country" />
		<div class="modal-body">
			Address:<select name="docAddress">
					<option value="JP">JAPAN</option>
					<option value="CN">CHINA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['jp']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp; CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>
	
	<div id="createinboud_fr" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> FR FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_fr" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="fr" name="country" />
		<div class="modal-body">
			Address:<select name="docAddress">
					<option value="DE">DE</option>
					<option value="CN">CHINA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['fr']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp; CreateByPlan: <input class="plan" type="checkbox"/> <input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>

	<div id="createinboud_uk" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_FBA_sent_to' /> UK FBA <span class="tPack" style="font-size: 20px;font-weight: bold;"></span></h3>
		</div>
		<form id="addFbaF_uk" action="${ctx}/psi/fbaInbound/save" method="post" class="fbaForm"  onkeydown="if(event.keyCode==13)return false;" >
		<input type="hidden" value="uk" name="country" />
		<div class="modal-body">
			Address:<select name="docAddress">
					<option value="DE">DE</option>
					<option value="CN">CHINA</option>
				</select>
				&nbsp;&nbsp;Account:<select name="accountName">
					<c:forEach items="${accountMap['uk']}" var="account">
					    <option value='${account}'>${account}</option>
					</c:forEach>
				</select>
				&nbsp;&nbsp; CreateByPlan: <input class="plan"  type="checkbox"/>
				<input class="planHid" name="shipmentStatus" type="hidden"/>
				<br/><br/>
				<table class="table table-striped table-bordered table-condensed ajaxtable">
					<thead>
						<tr>
							<th ><spring:message code='amaInfo_businessReport_productName' /></th>
							<th >SKU</th>
							<th ><spring:message code='psi_inventory_number_of_cartons' /></th>
							<th ><spring:message code='psi_inventory_shipping_warehouse' /></th>
							<th ><spring:message code='psi_inventory_fulfillable_stock' /></th>
							<th ><spring:message code='psi_inventory_in_transit_stock' /></th>
							<th ><spring:message code='psi_inventory_shipment_quantity' /></th>
							<th ><spring:message code='sys_label_tips_operate' /></th>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
		</div>
		<div class="modal-footer">
			<input type="submit" class="btn btn-primary" value="Submit">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
		</form>
	</div>

	<div id="tranTip" class="modal hide fade" tabindex="-1" data-width="750">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Bill NO.</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_method' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Sku</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_destination_warehouse' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Quantity</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_warehouse_date' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_predicted_in_stock_date' /></th>
						<th style="text-align:left;vertical-align: middle;">Remarks</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	
	<div id="preTranTip" class="modal hide fade" tabindex="-1" data-width="750">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Bill NO.</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Country</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_method' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_destination' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Quantity</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_predicted_warehouse_date' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_predicted_in_stock_date' /></th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	<div id="fbaTran" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 300px;text-align: center;vertical-align: middle;">ShipmentName</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">ShipmentId</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">SKU</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_delivery_quantity' /></th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_received_quantity' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_state' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">pickUpDate</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_expected_arrival' /></th>
						<th style="text-align: left;vertical-align: middle;">Remarks</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>

	<div id="produceTip" class="modal hide fade" tabindex="-1" data-width="750">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3><spring:message code='psi_inventory_production_list' /></h3>
		</div>
		<div class="alert alert-info" id="productImprove" style="display: none;margin-bottom:0px"></div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 100px;text-align: center;vertical-align: middle;">Order NO.</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">Country</th>
						<th style="width: 70px;text-align: center;vertical-align: middle;">Online</th>
						<th style="width: 70px;text-align: center;vertical-align: middle;">Offline</th>
						<th style="width: 200px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_order_date' /></th>
						<th style="width: 200px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_transaction_date' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_estimated_delivery_date' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">TransportWeek</th>
						<th style="text-align: left;vertical-align: middle;">Remarks</th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	<div id="prewkTip" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3></h3>
		</div>
		<div class="modal-body">
			<table class="table table-striped table-bordered table-condensed ajaxtable">
				<thead>
					<tr>
						<th style="width: 300px;text-align: center;vertical-align: middle;">ShipmentName</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">ShipmentId</th>
						<th style="width: 100px;text-align: center;vertical-align: middle;">SKU</th>
						<th style="width: 50px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_delivery_quantity' /></th>
						<th style="width: 100px;text-align: center;vertical-align: middle;"><spring:message code='psi_inventory_state' /></th>
					</tr>
				</thead>
				<tbody>
				</tbody>
			</table>
			<div class="modal-footer">
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
</body>
</html>
