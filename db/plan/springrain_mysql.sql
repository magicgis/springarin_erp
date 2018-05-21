SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE plan_plan;
DROP TABLE plan_product;
DROP TABLE plan_product_flow;

/* Create Tables */
CREATE TABLE plan_plan
(
	id varchar(64) NOT NULL COMMENT '编号',
	content varchar(10000) NOT NULL COMMENT '计划内容',
	type char(1) COMMENT '计划类型',/*月计划、周计划、日志*/
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	performance varchar(10000) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '完成情况',
	flag varchar(10) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '记录的详细标示',
	PRIMARY KEY (id)
) COMMENT = '计划表';

CREATE TABLE plan_product
(
	id varchar(64) NOT NULL COMMENT '编号',
	name varchar(64) COMMENT '新品型号',
	supplier varchar(64) COMMENT '供应商',
	img_path varchar(64) COMMENT '预览图片路径',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	update_by varchar(64) COMMENT '更新者',
	master_by varchar(64) COMMENT '负责人',
	finish varchar(5)  DEFAULT '0' NOT NULL COMMENT '是否发布完成',
	update_date datetime COMMENT '修改时间',
	remarks varchar(10000) COMMENT '功能说明',
	start_date datetime COMMENT '开始时间',
	end_date datetime COMMENT '结束时间',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '新品表';

CREATE TABLE plan_product_flow
(
	id varchar(64) NOT NULL COMMENT '编号',
	product_id varchar(64) COMMENT '新品编号',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	start_date datetime COMMENT '开始时间',
	end_date datetime COMMENT '结束时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '修改时间',
	remarks varchar(10000) COMMENT '完成情况',
	step varchar(10) COMMENT '步骤',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '发布流程表';

CREATE TABLE plan_task
(
	id varchar(64) NOT NULL COMMENT '任务编号',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	start_date datetime COMMENT '开始时间',
	end_date datetime COMMENT '结束时间',
	state char(1) DEFAULT '0' COMMENT '任务状态',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '修改时间',
	remarks varchar(10000) COMMENT '内容',
	subject varchar(255) COMMENT '主题 ',
	attchment_path varchar(64) COMMENT '附件路径',
	flag char(10)  DEFAULT '0' COMMENT '是否已发送邮件提醒 0:代码没有发送成功 1：代码已经发送成功',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '任务表' CHARSET utf8 COLLATE utf8_unicode_ci;

CREATE TABLE plan_task_user
(
	user_id varchar(64) NOT NULL COMMENT '用户编号',
	task_id varchar(64) NOT NULL COMMENT '任务编号',
	PRIMARY KEY (user_id, task_id)
) COMMENT = '执行人-任务' CHARSET utf8 COLLATE utf8_unicode_ci;


