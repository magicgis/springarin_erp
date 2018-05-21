DROP TABLE IF EXISTS `amazoninfo_return_comment`;

CREATE TABLE `amazoninfo_return_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `purchase_date` date DEFAULT NULL,
  `product_name` varchar(200) COLLATE utf8_unicode_ci DEFAULT NULL,
  `color` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `return_amount` int(11) DEFAULT NULL COMMENT '退货数',
  `comment_amount` int(11) DEFAULT NULL COMMENT '差评数',
  `order_amount` int(11) DEFAULT NULL COMMENT '订单数',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uinque` (`country`,`purchase_date`,`product_name`,`color`)
) COMMENT = '退货差评表' CHARSET=utf8 COLLATE=utf8_unicode_ci;


--初始化表
 INSERT INTO `amazoninfo_return_comment` (country,purchase_date,product_name,color,return_amount,comment_amount,order_amount) 
    SELECT g.country,g.purchase_date,g.product_name,g.color,SUM(g.return_amount) return_amount,SUM(g.comment_amount) comment_amount,SUM(g.order_amount) order_amount FROM(     
       SELECT g.`country` country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date, s.product_name product_name,s.`color` color, SUM(g.quantity) return_amount,0 comment_amount,0 order_amount
	FROM amazoninfo_return_goods g JOIN amazoninfo_order o ON o.`amazon_order_id` = g.`order_id`  
	JOIN (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%') s ON s.`asin`=g.`asin` AND  g.`country`=s.`country` 
	WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -20 MONTH)
	AND s.`product_name` NOT LIKE '%other%'
	 GROUP BY g.`country`,s.`product_name` ,s.`color`,DATE_FORMAT(o.purchase_date,'%Y-%m-%d')
	UNION ALL
	SELECT c.`country` country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date,t.`product_name` product_name,t.`color` color,0 return_amount,COUNT(*) comment_amount,0 order_amount
	FROM (SELECT a.ID,a.`remarks`,a.country,SUBSTRING_INDEX(SUBSTRING_INDEX(a.`invoice_number`,',',b.help_topic_id+1),',',-1) order_id
	FROM custom_event_manager a JOIN mysql.help_topic b
	ON b.help_topic_id < (LENGTH(a.`invoice_number`) - LENGTH(REPLACE(a.`invoice_number`,',',''))+1)
	WHERE a.type='1' AND a.`invoice_number`!='not find oderID' AND a.`invoice_number` IS NOT NULL) c 
	JOIN  (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%') t ON t.`asin`=c.`remarks` AND t.`country`=c.country
	JOIN amazoninfo_order o ON o.`amazon_order_id` = c.`order_id`
	WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -20 MONTH)
	 GROUP BY c.`country`,t.`product_name` ,t.`color`,DATE_FORMAT(o.purchase_date,'%Y-%m-%d')
	 UNION ALL
	  SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1) country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date,t.`product_name` product_name,t.`color` color,0 return_amount,0 comment_amount, COUNT(*) order_amount 
	 FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id`
	 WHERE o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -20 MONTH) AND t.`product_name` NOT LIKE '%other%'
	 GROUP BY DATE_FORMAT(o.purchase_date,'%Y-%m-%d'),t.`product_name`,t.`color`,SUBSTRING_INDEX(o.`sales_channel`,'.',-1)
	)  g GROUP BY g.country,g.purchase_date,g.product_name,g.color
ON DUPLICATE KEY UPDATE `return_amount` = VALUES(return_amount),`comment_amount` =VALUES(comment_amount),`order_amount` = VALUES(order_amount);


CREATE EVENT IF NOT EXISTS eventJob    
ON SCHEDULE EVERY 1 DAY STARTS DATE_ADD(DATE(CURDATE() + 1),INTERVAL 1 HOUR)  -- 凌晨一点
ON COMPLETION PRESERVE ENABLE  
DO CALL p_return_comment();  

SET GLOBAL event_scheduler = 1;  -- 启动定时器
ALTER EVENT eventJob ON  COMPLETION PRESERVE ENABLE;   -- 开启事件