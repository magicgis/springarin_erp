SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE custom_email_manager;

CREATE TABLE custom_email_manager
(
	id varchar(64) NOT NULL COMMENT '任务编号',
	email_id varchar(1000) COMMENT '邮件id',
	create_by varchar(64) COMMENT '创建者',
	subject varchar(1000) COMMENT '主题',
	create_date datetime COMMENT '创建时间',
	custom_send_date datetime COMMENT '客户发送时间',
	end_date datetime COMMENT '处理结束时间',
	answer_date datetime COMMENT '响应时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '修改时间',
	receive_content MEDIUMTEXT COMMENT '接收内容',
	state char(1) COMMENT '状态：0:未响应   1：已响应   2：处理完成',
	master_by varchar(64) COMMENT '处理人',
	custom_id varchar(255) COMMENT '客户id',
	revert_email varchar(1000) COMMENT '发件人邮箱',
	revert_server_email varchar(1000) COMMENT '来至服务器邮箱',
	remarks varchar(1000) COMMENT '备用字段',
	attchment_path varchar(1000) COMMENT '附件路径',
	inline_attchment_path varchar(1000) COMMENT '内联附件路径',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	transmit varchar(1000) COMMENT '转发记录',
	urgent char(1) DEFAULT '0' NOT NULL COMMENT '是否紧急',
	result varchar(500) COMMENT '处理结果',
	flag char(2)  COMMENT '邮件标记',
	country   		varchar(10)     COMMENT  '国家',
	product_name 	varchar(100)    COMMENT  '产品名',
	problem_type 	varchar(200)    COMMENT  '问题类型',
	problem     	varchar(500)    COMMENT  '问题补充',
	order_nos       varchar(500)    COMMENT  '订单号',
	PRIMARY KEY (id)
) COMMENT = '邮箱管理表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE custom_send_email;

CREATE TABLE custom_send_email
(
	id varchar(64) NOT NULL COMMENT '任务编号',
	create_by varchar(64) COMMENT '创建者',
	send_subject varchar(1000) COMMENT '主题',
	send_content MEDIUMTEXT COMMENT '发送内容',
	send_email varchar(1000) COMMENT '收件人邮箱',
	send_attchment_path varchar(1000) COMMENT '附件路径',
	cc_to_email varchar(1000) COMMENT '抄送人邮箱',
	bcc_to_email varchar(1000) COMMENT '秘密抄送人邮箱',
	sent_date datetime COMMENT '发送时间',
	custom_email varchar(64) COMMENT '客服邮件id',
	type char(1) DEFAULT '0' NOT NULL COMMENT '邮件类型',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	send_flag char(1) DEFAULT '0' NOT NULL COMMENT '发送情况',
	check_user varchar(64) COMMENT '审核人',
	check_state char(1) DEFAULT '3' COMMENT '审核状态',
	PRIMARY KEY (id)
) COMMENT = '发送邮箱表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE custom_user_signature;

CREATE TABLE custom_user_signature
(
	user_id varchar(64) COMMENT '用户id',
	signature_content MEDIUMTEXT COMMENT '签名类容'
) COMMENT = '客服签名表' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE custom_event_manager;

CREATE TABLE custom_event_manager
(
	id int NOT NULL AUTO_INCREMENT COMMENT '编号',
	create_by varchar(64) COMMENT '创建者',
	country varchar(64) COMMENT '国家平台',
	create_date datetime COMMENT '创建时间',
	end_date datetime COMMENT '处理结束时间',
	answer_date datetime COMMENT '响应时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '修改时间',
	state char(1) COMMENT '状态：0:未响应   1：已响应   2：处理完成',
	master_by varchar(64) COMMENT '处理人',
	remarks varchar(1000) COMMENT '备用字段',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	transmit varchar(1000) COMMENT '转发记录',
	subject varchar(1000) COMMENT '主题',
	type varchar(64) COMMENT '类型',
	description MEDIUMTEXT COMMENT '事件描述',
	priority varchar(64) COMMENT '优先级',
	custom_id varchar(255) COMMENT '客户id',
	invoice_number varchar(255) COMMENT '订单号',
	review_link varchar(255) COMMENT '评论链接',
	review_date datetime COMMENT '评论时间',
	attchment_path varchar(1000) COMMENT '附件路径',
	custom_name varchar(500) COMMENT '客户姓名',
	custom_email varchar(500) COMMENT '客户邮箱',
	result varchar(500) COMMENT '处理结果',
	reason varchar(500) COMMENT '事件原由',
	problem_type varchar(100) COMMENT '问题类型',
	product_name varchar(100) COMMENT '产品名',
	tax_id varchar(100) COMMENT '税号',
	email_notice char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否已电子邮件通知客户,针对召回事件',
  	product_quantity int(11) DEFAULT NULL COMMENT '召回事件产品数量',
  	ship_to_china char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '是否发回中国0：不发送 1;发送',
  	refund_type char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '返款方式',
  	card_number varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '卡号',
  	total_price decimal(11,2) DEFAULT NULL COMMENT '返款总价',
  	product_attribute varchar(10) COMMENT '产品属性',
	PRIMARY KEY (id)
) COMMENT = '事件管理表' CHARSET utf8 COLLATE utf8_unicode_ci; -- 建表一定加这句话.

DROP TABLE custom_event_comment;

CREATE TABLE custom_event_comment
(	
	id varchar(64) NOT NULL COMMENT '编号',
	event varchar(64) COMMENT '事件id',
	type char(1) COMMENT '0:手动输入的   1：系统生成的',
	comment MEDIUMTEXT COMMENT '事件记录内容',
	create_date datetime COMMENT '创建时间',
	create_by varchar(64) COMMENT '创建者',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '修改时间',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	remarks varchar(1000) COMMENT '备用字段'
) COMMENT = '事件处理记录表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE custom_auto_reply;

CREATE TABLE custom_auto_reply
(
	id varchar(64) NOT NULL COMMENT '编号',
	
	subject varchar(1000) COMMENT '主题',
	content MEDIUMTEXT COMMENT '发送内容',
	type char(1) DEFAULT '0' NOT NULL COMMENT '自动回复类型',
	used char(1) DEFAULT '0' NOT NULL COMMENT '自动发送开关',
	used_forward char(1) DEFAULT '0' NOT NULL COMMENT '自动转发开关',
	forward_to varchar(64) COMMENT '转发谁',
	
	create_date datetime COMMENT '创建时间',
	create_by varchar(64) COMMENT '创建者',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '修改时间',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	remarks varchar(1000) COMMENT '备用字段',
	PRIMARY KEY (id)
) COMMENT = '自动回复设置' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE custom_unsubscribe_email;

CREATE TABLE custom_unsubscribe_email
(
	id INT NOT NULL AUTO_INCREMENT COMMENT 'id',
	create_date DATETIME COMMENT '创建时间',
	custom_email VARCHAR(1000) COMMENT '客户邮箱',
	PRIMARY KEY (id)
) COMMENT = '退订邮箱通知' CHARSET utf8 COLLATE utf8_unicode_ci;


create table custom_email_template(
 id int primary key AUTO_INCREMENT COMMENT '编号',
 template_type char(1) not null comment '0:系统模板,1:共享模板,2:私人模板',
 template_name varchar(1000) COMMENT '模板名称',
 template_subject varchar(1000) COMMENT '主题',
 template_content MEDIUMTEXT COMMENT '内容',
 role_id varchar(64) COMMENT '共享模板有值，其他类型为空',
 create_by varchar(64) NOT NULL COMMENT  '创建人',
 create_date datetime comment '创建时间',
 last_update_date datetime comment '最后修改时间',
 last_update_by varchar(64) COMMENT  '最后修改人',
 file_name varchar(100) COMMENT  '附件名称',
 file_path varchar(500) COMMENT  '附件路径',
 del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记'
) COMMENT = '邮件模板管理表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE custom_product_problem(
	country   		varchar(10)     COMMENT  '国家',
	product_name 	varchar(100)    COMMENT  '产品名',
	problem_type 	varchar(200)    COMMENT  '问题类型',
	problem     	varchar(500)    COMMENT  '问题补充',
	order_nos       varchar(500)    COMMENT  '订单号',
	create_date     date            COMMENT  '创建日期',
	data_date       date            COMMENT  '数据创建日期',
	data_type       char(1)         COMMENT  '邮件/事件',
	data_id         varchar(64)     COMMENT  '原数据id',
	revert_email    varchar(500)    COMMENT  '邮箱',
	PRIMARY KEY (data_id,data_type)
) COMMENT = '产品问题表' CHARSET utf8 COLLATE utf8_unicode_ci;


CREATE TABLE `custom_product_type_problems` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `product_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `problem_type` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '问题类型',
  `del_flag` char(1) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '删除标记',
  PRIMARY KEY (`id`)
) COMMENT = '产品问题类型表' CHARSET=utf8 COLLATE=utf8_unicode_ci


CREATE TABLE `custom_suggestion` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `custom_email_id` varchar(64) COLLATE utf8_unicode_ci DEFAULT NULL,
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '国家',
  `product_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品名称',
  `product_type` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '产品类型',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `content` varchar(1000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '建议内容',
  PRIMARY KEY (`id`)
) COMMENT='客户建议表' DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci


DROP TABLE IF EXISTS `custom_email_estimate`;

CREATE TABLE `custom_email_estimate` (
  `id` int(11) NOT NULL COMMENT '任务编号',
  `country` varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL,
  `end_flag` varchar(500) COLLATE utf8_unicode_ci DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) COMMENT = '邮箱配置表' CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `amazoninfo_match_private_email` (
  `amz_email` varchar(1000) COLLATE utf8_unicode_ci NOT NULL,
  `email` varchar(1000) COLLATE utf8_unicode_ci NOT NULL
) COMMENT = '私人邮箱' CHARSET=utf8 COLLATE=utf8_unicode_ci;
