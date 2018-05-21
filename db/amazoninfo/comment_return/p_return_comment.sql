DELIMITER $$

USE `erp`$$

DROP PROCEDURE IF EXISTS `p_return_comment`$$

CREATE DEFINER=`erp`@`%` PROCEDURE `p_return_comment`()
BEGIN
   INSERT INTO `amazoninfo_return_comment` (country,purchase_date,product_name,color,return_amount,comment_amount,order_amount) 
    SELECT g.country,g.purchase_date,g.product_name,g.color,SUM(g.return_amount) return_amount,SUM(g.comment_amount) comment_amount,SUM(g.order_amount) order_amount FROM(     
       SELECT g.`country` country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date, s.product_name product_name,s.`color` color, SUM(g.quantity) return_amount,0 comment_amount,0 order_amount
	FROM amazoninfo_return_goods g JOIN amazoninfo_order o ON o.`amazon_order_id` = g.`order_id`  
	JOIN (SELECT DISTINCT product_name,color,country,sku FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND `product_name` NOT LIKE '%Old%' AND del_flag='0' ) s ON s.`sku`=g.`sku` AND  g.`country`=s.`country` 
	WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -6 MONTH)
	AND s.`product_name` NOT LIKE '%other%' AND s.`product_name` NOT LIKE '%Old%'
	 GROUP BY g.`country`,s.`product_name` ,s.`color`,DATE_FORMAT(o.purchase_date,'%Y-%m-%d')
	UNION ALL
	SELECT c.`country` country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date,t.`product_name` product_name,t.`color` color,0 return_amount,COUNT(*) comment_amount,0 order_amount
	FROM (SELECT a.ID,a.`remarks`,a.country,SUBSTRING_INDEX(SUBSTRING_INDEX(a.`invoice_number`,',',b.help_topic_id+1),',',-1) order_id
	FROM custom_event_manager a JOIN mysql.help_topic b
	ON b.help_topic_id < (LENGTH(a.`invoice_number`) - LENGTH(REPLACE(a.`invoice_number`,',',''))+1)
	WHERE a.type='1' AND a.`invoice_number`!='not find oderID' AND a.`invoice_number` IS NOT NULL) c 
	JOIN  (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND `product_name` NOT LIKE '%Old%' AND del_flag='0' ) t ON t.`asin`=c.`remarks` AND t.`country`=c.country
	JOIN amazoninfo_order o ON o.`amazon_order_id` = c.`order_id`
	JOIN amazoninfo_orderitem r ON o.id=r.order_id AND r.asin=c.remarks
	WHERE  o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -6 MONTH)
	 GROUP BY c.`country`,t.`product_name` ,t.`color`,o.`amazon_order_id`,DATE_FORMAT(o.purchase_date,'%Y-%m-%d')
	 UNION ALL
	  SELECT SUBSTRING_INDEX(o.`sales_channel`,'.',-1) country,DATE_FORMAT(o.purchase_date,'%Y-%m-%d') purchase_date,t.`product_name` product_name,t.`color` color,0 return_amount,0 comment_amount, COUNT(*) order_amount 
	 FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id`
	 WHERE o.`order_status`='Shipped' AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -6 MONTH) AND t.`product_name` NOT LIKE '%other%' AND t.`product_name` NOT LIKE '%Old%'
	 GROUP BY DATE_FORMAT(o.purchase_date,'%Y-%m-%d'),t.`product_name`,t.`color`,SUBSTRING_INDEX(o.`sales_channel`,'.',-1)
	)  g GROUP BY g.country,g.purchase_date,g.product_name,g.color
ON DUPLICATE KEY UPDATE `return_amount` = VALUES(return_amount),`comment_amount` =VALUES(comment_amount),`order_amount` = VALUES(order_amount);
    END$$

DELIMITER ;