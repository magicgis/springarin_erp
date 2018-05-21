<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd ">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style>
<!--
-->
</style>
</head>
<body>
	<div>
		<span style="line-height: 1.5;">Hi all,</span>
	</div>
	<div>
		<div style="font: Verdana normal 14px; color: #000;">
			<div class="WordSection1">
				<p class="MsoNormal" style="margin-bottom: 12.0pt">
					<span lang="EN-US"><br></span>${payment.createUser.name},申请支付<span lang="EN-US">${payment.supplier.name}</span>账户货款<span
						lang="EN-US" style="color: red">${payment.currencyType}  ${payment.paymentAmountTotal?string(',###.##')}</span><br/>订单信息:${orderNo};
						账户如下，<span	lang="EN-US"><br></span>银行帐号信息<span lang="EN-US" style="font-size:20px">:${payment.account}<br/></span>
						<span	style="color: blue">备注：${payment.remark}</span>
				</p>
				<table border="0" cellspacing="0" cellpadding="0"
					style="border-collapse: collapse">
					<tbody>
						<tr>
							<td colspan="7"
								style="border: solid #666666 1.0pt; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									align="center" style="text-align: center">
									<b><span style="font-size: 8.5pt; color: #333333">付款单详情</span></b><b><span
										lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span></b>
								</p></td>
						</tr>
						<tr>
							<td
								style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">序号</span><span
										lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p></td>
							<td
								style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">款项类型</span><span
										lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p></td>
								
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
							    <p align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">提单/订单号</span>
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>	
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
							    <p align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">提示信息</span>
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p>
							</td>	
							
							<td
								style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">合同总额</span><span
										lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p></td>		
							<td
								style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">可付金额</span><span
										lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p></td>	
							<td
								style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									class="MsoNormal" align="center" style="text-align: center">
									<span style="font-size: 8.5pt; color: #333333">本次实付金额</span><span
										lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
								</p></td>
						</tr>
						<#list payment.items as item>
						<tr>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
							    <p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${item_index+1}
									</span>
								</p>
							</td>
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.typeName}
									</span>
								</p>
							</td>	
								
							
								
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.url}
									</span>
								</p>
							</td>	
							
							<#if (item.paymentType=='0')>
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									</span>
								</p>
							</td>	
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.totalAmount?string(',###.##')}
									</span>
								</p>
							</td>	
							
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.needPaymentAmount?string(',###.##')}
									</span>
								</p>
							</td>	
							
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.paymentAmount?string(',###.##')}
									</span>
								</p>
							</td>
							
							<#else>
									<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										  ${item.ladingBillItem.productNameColor}  ${item.ladingBillItem.country}&nbsp;&nbsp;${item.ladingBillItem.quantityLading}个&nbsp;&nbsp;单价:${item.ladingBillItem.itemPrice}&nbsp;&nbsp;定金比例:${item.ladingBillItem.deposit}%&nbsp;&nbsp;
									</span>
								</p>
							</td>	
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.ladingBillItem.noDepositTotalAmount?string(',###.##')}
									</span>
								</p>
							</td>	
							
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${(item.ladingBillItem.noDepositCanPayAmount+item.paymentAmount)?string(',###.##')}
									</span>
								</p>
							</td>	
							
							
							<td	style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
								<p	class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US" style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
										${item.paymentAmount?string(',###.##')}
									</span>
								</p>
							</td>
							
							 </#if>
						
									
						</tr>
						</#list>
					</tbody>
				</table>
				
				
				
				<#if (payment.adjusts)??>
					<table border="0" cellspacing="0" cellpadding="0"style="border-collapse: collapse;width:600px">
					<tbody>
						<tr>
							<td colspan="3"	style="border: solid #666666 1.0pt; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align:center">
									<b><span style="font-size: 8.5pt; color: #333333">额外付款详情</span></b>
							</td>
						</tr>
						<tr>
							
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align: center">
								<p	class="MsoNormal">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									款项主题
									</span>
								</p>
							</td>
							
							<td	style="text-align: center;border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
									<span style="font-size: 8.5pt; color: #333333">金额</span>
							</td>
							
							
							<td	style="text-align: center;border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">
									<span style="font-size: 8.5pt; color: #333333">备注</span>
							</td>
								
						
						</tr>
						<#list payment.adjusts as adjust>
						<tr>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align: center">
								<p	class="MsoNormal">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${adjust.subject}
									</span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align: center">
								<p	class="MsoNormal">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${adjust.adjustAmount?string(',###.##')}
									</span>
								</p>
							</td>
							<td	style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt;text-align: center">
								<p	class="MsoNormal">
									<span lang="EN-US"	style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">
									${adjust.remark}
									</span>
								</p>
							</td>
						</tr>
						</#list>
					</tbody>
				</table>
				</#if>
				
				<br/>
				<br/>
				<br/>
				<#list payment.orders as bill>
					<table class="MsoNormalTable" border="0" cellspacing="0"
						cellpadding="0" style="border-collapse: collapse" id="ex0">
						<tbody>
							<tr>
								<td colspan="11"
									style="border: solid #666666 1.0pt; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<b><span style="font-size: 8.5pt; color: #333333">订单定金详情</span></b><b><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span></b>
									</p></td>
							</tr>
							<tr>
								<td colspan="2"
									style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">订单号</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">订单状态</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">货币</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">定金比例</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>	
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">总金额</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">定金金额</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">尾款总额</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td 
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">跟单员</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
							</tr>						
							
							<tr>
								<td  colspan="2"
									style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bill.orderNo}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span class="label"><span
											style="font-size: 8.5pt; color: #333333">${bill.statusName}</span></span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span class="label"><span
											style="font-size: 8.5pt; color: #333333">${payment.supplier.currencyType}</span></span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span class="label"><span
											style="font-size: 8.5pt; color: #333333">${bill.deposit?string(',###.##')}%</span></span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bill.totalAmount?string(',###.##')}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bill.depositTotal?string(',###.##')}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${(bill.totalAmount-bill.depositTotal)?string(',###.##')}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bill.createUser.name}</span>
									</p></td>
							</tr>
							<tr>
								<td colspan="11"
									style="border: solid #666666 1.0pt; border-top: none; background: #DEDEDE; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<b><span style="font-size: 8.5pt; color: #333333">货物清单</span></b><b><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span></b>
									</p></td>
							</tr>
							<tr>
								<td
									style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">序号</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">产品名称</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">平台</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">颜色</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">条码</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">数量</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">单价</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
									<td 
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">定金总金额</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
								<td 
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span style="font-size: 8.5pt; color: #333333">尾款总金额</span><span
											lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333"></span>
									</p></td>
							</tr>
							<#list bill.items as bItem>
							<tr>
								<td 
									style="border: solid #666666 1.0pt; border-top: none; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem_index+1}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.productName}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.countryCode}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.colorCode}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt">${bItem.barcode?replace('null','')}</td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.quantityOrdered}</span>
									</p></td>
								<td
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.itemPrice?string(',###.##')}</span>
									</p></td>
								<td 
								style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
									class="MsoNormal" align="center" style="text-align: center">
									<span lang="EN-US"
										style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.depositAmount?string(',###.##')}</span>
								</p></td>
								<td 
									style="border-top: none; border-left: none; border-bottom: solid #666666 1.0pt; border-right: solid #666666 1.0pt; background: white; padding: 3.0pt 3.0pt 3.0pt 3.0pt"><p
										class="MsoNormal" align="center" style="text-align: center">
										<span lang="EN-US"
											style="font-size: 8.5pt; font-family: &amp; amp; quot; Verdana &amp;amp; quot; ,&amp; amp; quot; sans-serif &amp;amp; quot;; color: #333333">${bItem.totalAmount?string(',###.##')}</span>
									</p></td>
							</tr>
							</#list>
						</tbody>
					</table>
					<br/>
				</#list>
	
			</div>
		</div>
	</div>
	<style></style>

</body>
</html>
