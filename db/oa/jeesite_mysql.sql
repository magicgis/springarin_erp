SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE oa_leave;
CREATE TABLE oa_leave(
	id varchar(64) NOT NULL COMMENT '编号',
	process_instance_id varchar(64) COMMENT '流程实例编号',
	start_time datetime COMMENT '开始时间',
	end_time datetime COMMENT '结束时间',
	leave_type varchar(20) COMMENT '请假类型',
	reason varchar(255) COMMENT '请假理由',
	apply_time datetime COMMENT '申请时间',
	reality_start_time datetime COMMENT '实际开始时间',
	reality_end_time datetime COMMENT '实际结束时间',
	process_status varchar(50) COMMENT '流程状态',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '请假流程表' CHARSET utf8 COLLATE utf8_unicode_ci;;

ALTER TABLE `oa_leave`   
  ADD COLUMN `apply_day` INT(11) NULL  COMMENT '申请天数' AFTER `del_flag`,
  ADD COLUMN `apply_hour` DECIMAL(11,2) NULL  COMMENT '申请小时数' AFTER `apply_day`,
  ADD COLUMN `reality_day` INT(11) NULL  COMMENT '实际天数' AFTER `apply_hour`,
  ADD COLUMN `reality_hour` DECIMAL(11,2) NULL  COMMENT '实际小时数' AFTER `reality_day`;


/* Create Indexes */

CREATE INDEX oa_leave_create_by ON oa_leave (create_by ASC);
CREATE INDEX oa_leave_process_instance_id ON oa_leave (process_instance_id ASC);
CREATE INDEX oa_leave_del_flag ON oa_leave (del_flag ASC);
CREATE INDEX oa_leave_id ON oa_leave (id ASC);

DROP TABLE oa_buydevice ;




/* Create Tables */

CREATE TABLE oa_buydevice
(
	id varchar(64) NOT NULL COMMENT '编号',
	process_instance_id varchar(64) COMMENT '流程实例编号',
	device_type varchar(20) COMMENT '设备类型',
	reason varchar(255) COMMENT '采购理由',
	name varchar(255) COMMENT '设备清单',
	price DECIMAL(8,2) COMMENT '价格',
	process_status varchar(50) COMMENT '流程状态',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '办公设备采购流程表' CHARSET utf8 COLLATE utf8_unicode_ci;




DROP TABLE oa_amazondiscount;
CREATE TABLE oa_amazondiscount(
	id varchar(64) NOT NULL COMMENT '编号',
	process_instance_id varchar(64) COMMENT '流程实例编号',
	discount_scope varchar(500) COMMENT '打折范围',
	start_date datetime COMMENT '开始时间',
	end_date datetime COMMENT '结束时间',
	price DECIMAL(8,2) COMMENT '打折价格',
	reason varchar(255) COMMENT '申请理由',
	process_status varchar(50) COMMENT '流程状态',
	create_by varchar(64) COMMENT '创建者',
	create_date datetime COMMENT '创建时间',
	update_by varchar(64) COMMENT '更新者',
	update_date datetime COMMENT '更新时间',
	remarks varchar(255) COMMENT '备注信息',
	del_flag char(1) DEFAULT '0' NOT NULL COMMENT '删除标记',
	PRIMARY KEY (id)
) COMMENT = '亚马逊打折申请流程表' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE oa_roster;
CREATE TABLE oa_roster(
   	id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   	job_no               VARCHAR(100)                   NULL,
   	user                 VARCHAR(100)                   NULL,
   	cn_name              VARCHAR(100)                   NULL,
   	en_name              VARCHAR(100)                   NULL,
   	work_sta             CHAR(1)                        NULL,
   	office               VARCHAR(100)                   NULL,
   	position             VARCHAR(100)                   NULL,
   	entry_Date           DATE                           NULL,
   	egular_Date          DATE                           NULL,
   	
    birth_Date           DATE                           NULL,
   	sex             	 CHAR(1)                        NULL,
   	id_card              VARCHAR(100)                   NULL,
   	phone                VARCHAR(100)                   NULL,
   	email                VARCHAR(100)                   NULL,
   	has_secret           CHAR(1)                        NULL,
   	has_compete          CHAR(1)                        NULL,
   	education            VARCHAR(100)                   NULL,
   	school               VARCHAR(100)                   NULL,
   	is_key               CHAR(1)                        NULL,
   	specialities         VARCHAR(100)                   NULL,
   	home_type            CHAR(1)                        NULL,
   	home_address         VARCHAR(200)                   NULL,
   	politics_face        VARCHAR(100)                   NULL,
   	is_marry             CHAR(1)                        NULL,
   	address              VARCHAR(100)                   NULL,
   	family_address       VARCHAR(100)                   NULL,
   	origin_place         VARCHAR(100)                   NULL,
   	zodiac         		 VARCHAR(100)                   NULL,
   	english_level        VARCHAR(100)                   NULL,
   	
   	qualification        VARCHAR(100)                   NULL,
   	contact_name1        VARCHAR(100)                   NULL,
   	contact_phone1       VARCHAR(100)                   NULL,
   	contact_relationship1         VARCHAR(100)          NULL,
    contact_name2        VARCHAR(100)                   NULL,
   	contact_phone2       VARCHAR(100)                   NULL,
   	contact_relationship2         VARCHAR(100)          NULL,	
   	
   	cy_contract_start_date      DATE                    NULL,
   	cy_contract_end_date        DATE                    NULL,
   	cy_contract_continue        VARCHAR(100)            NULL,
   	 	 		
   	lc_contract_start_date1     DATE                    NULL,
   	lc_contract_end_date1       DATE                    NULL,
   	lc_contract_start_date2     DATE                    NULL,
   	lc_contract_end_date2       DATE                    NULL,
   	lc_contract_start_date3     DATE                    NULL,
   	lc_contract_end_date3       DATE                    NULL,
   		
   	leave_date           DATE                           NULL,
   	leave_reason         VARCHAR(100)                   NULL,	
   	
   	resume_url           VARCHAR(100)                   NULL,	
   	resume_file          VARCHAR(100)                   NULL,	
   	probation_url        VARCHAR(100)                   NULL,	
   	probation_file       VARCHAR(100)                   NULL,	
   	train_url            VARCHAR(100)                   NULL,	
   	train_file           VARCHAR(100)                   NULL,	
   	innovate_url         VARCHAR(100)                   NULL,	
   	
   	
   	create_user          VARCHAR(50)                    NULL,
   	create_Date          DATE                           NULL,
   	update_user          VARCHAR(50)                    NULL,
   	update_Date          DATE                           NULL,
   	del_flag             CHAR(1)                        NULL,
   	PRIMARY KEY (id)
) COMMENT = '花名册' CHARSET utf8 COLLATE utf8_unicode_ci;


DROP TABLE oa_reward_punishment_log;
CREATE TABLE oa_reward_punishment_log(
   	id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   	roster_id            INT(11)                        NULL,
   	content              VARCHAR(500)                   NULL,
   	file_path            VARCHAR(500)                   NULL,
    data_Date            DATE                           NULL,
   	del_flag             CHAR(1)                        NULL,
   	PRIMARY KEY (id)
) COMMENT = '奖惩记录' CHARSET utf8 COLLATE utf8_unicode_ci;

DROP TABLE oa_recruit;
CREATE TABLE oa_recruit(
   	id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   	name                 VARCHAR(100)                   NULL,
   	sex                  CHAR(1)                        NULL,
   	phone                VARCHAR(100)                   NULL,
    email                VARCHAR(100)                   NULL,
    origin               VARCHAR(100)                   NULL,
    office            VARCHAR(100)                   NULL,
   	position             VARCHAR(100)                   NULL,
    notice_date          DATE                           NULL,
    interview_date       DATE                           NULL,
    
    interview_review1    VARCHAR(500)                   NULL,
    interview_review2    VARCHAR(500)                   NULL,
    resume_url           VARCHAR(500)                   NULL,
    resume_file          VARCHAR(200)                   NULL,
    remark               VARCHAR(500)                   NULL,
   	create_user          VARCHAR(50)                    NULL,
   	create_Date          DATE                           NULL,
   	update_user          VARCHAR(50)                    NULL,
   	update_Date          DATE                           NULL,
   	del_flag             CHAR(1)                        NULL,
   	PRIMARY KEY (id)
) COMMENT = '招聘管理' CHARSET utf8 COLLATE utf8_unicode_ci;



DROP TABLE oa_fixed_assets;
CREATE TABLE oa_fixed_assets(
   	id                   INT(11)                        NOT NULL AUTO_INCREMENT,
   	owner_user           VARCHAR(50)                    NULL,
   	owner_office         VARCHAR(100)                   NULL,
   	name                 VARCHAR(100)                   NULL,
   	model                VARCHAR(100)                   NULL,
    bill_no              VARCHAR(100)                   NULL,
    fixed_sta            VARCHAR(100)                   NULL,
    place                VARCHAR(500)                   NULL,
   	remark               VARCHAR(500)                   NULL,
    buy_date             DATE                           NULL,

    create_user          VARCHAR(50)                    NULL,
   	create_Date          DATE                           NULL,
   	update_user          VARCHAR(50)                    NULL,
   	update_Date          DATE                           NULL,
   	del_flag             CHAR(1)                        NULL,
   	PRIMARY KEY (id)
) COMMENT = '固定资产管理' CHARSET utf8 COLLATE utf8_unicode_ci;


