/*
 Navicat Premium Data Transfer

 Source Server         : MySQL5.7.44
 Source Server Type    : MySQL
 Source Server Version : 50744
 Source Host           : localhost:3306
 Source Schema         : yunshang-oa

 Target Server Type    : MySQL
 Target Server Version : 50744
 File Encoding         : 65001

 Date: 28/03/2026 11:08:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ACT_EVT_LOG
-- ----------------------------
DROP TABLE IF EXISTS `ACT_EVT_LOG`;
CREATE TABLE `ACT_EVT_LOG` (
  `LOG_NR_` bigint(20) NOT NULL AUTO_INCREMENT,
  `TYPE_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TIME_STAMP_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DATA_` longblob,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `IS_PROCESSED_` tinyint(4) DEFAULT '0',
  PRIMARY KEY (`LOG_NR_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_EVT_LOG
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_GE_BYTEARRAY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_BYTEARRAY`;
CREATE TABLE `ACT_GE_BYTEARRAY` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BYTES_` longblob,
  `GENERATED_` tinyint(4) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_GE_BYTEARRAY
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_GE_PROPERTY
-- ----------------------------
DROP TABLE IF EXISTS `ACT_GE_PROPERTY`;
CREATE TABLE `ACT_GE_PROPERTY` (
  `NAME_` varchar(64) COLLATE utf8_bin NOT NULL,
  `VALUE_` varchar(300) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  PRIMARY KEY (`NAME_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_GE_PROPERTY
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_ACTINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ACTINST`;
CREATE TABLE `ACT_HI_ACTINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `ACT_ID_` varchar(255) COLLATE utf8_bin NOT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CALL_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ACT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_ACT_INST_START` (`START_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`),
  KEY `ACT_IDX_HI_ACT_INST_EXEC` (`EXECUTION_ID_`,`ACT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_ACTINST
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_ATTACHMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_ATTACHMENT`;
CREATE TABLE `ACT_HI_ATTACHMENT` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `URL_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `CONTENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TIME_` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_ATTACHMENT
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_COMMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_COMMENT`;
CREATE TABLE `ACT_HI_COMMENT` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TIME_` datetime(3) NOT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACTION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `MESSAGE_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `FULL_MSG_` longblob,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_COMMENT
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_DETAIL
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_DETAIL`;
CREATE TABLE `ACT_HI_DETAIL` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VAR_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TIME_` datetime(3) NOT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`),
  KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`),
  KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`),
  KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_DETAIL
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_IDENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_IDENTITYLINK`;
CREATE TABLE `ACT_HI_IDENTITYLINK` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_TASK` (`TASK_ID_`),
  KEY `ACT_IDX_HI_IDENT_LNK_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_IDENTITYLINK
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_PROCINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_PROCINST`;
CREATE TABLE `ACT_HI_PROCINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `END_TIME_` datetime(3) DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `END_ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SUPER_PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`),
  KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_PROCINST
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_TASKINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_TASKINST`;
CREATE TABLE `ACT_HI_TASKINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(3) NOT NULL,
  `CLAIM_TIME_` datetime(3) DEFAULT NULL,
  `END_TIME_` datetime(3) DEFAULT NULL,
  `DURATION_` bigint(20) DEFAULT NULL,
  `DELETE_REASON_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `PRIORITY_` int(11) DEFAULT NULL,
  `DUE_DATE_` datetime(3) DEFAULT NULL,
  `FORM_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_TASK_INST_PROCINST` (`PROC_INST_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_TASKINST
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_HI_VARINST
-- ----------------------------
DROP TABLE IF EXISTS `ACT_HI_VARINST`;
CREATE TABLE `ACT_HI_VARINST` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VAR_TYPE_` varchar(100) COLLATE utf8_bin DEFAULT NULL,
  `REV_` int(11) DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME_` datetime(3) DEFAULT NULL,
  `LAST_UPDATED_TIME_` datetime(3) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`),
  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`),
  KEY `ACT_IDX_HI_PROCVAR_TASK_ID` (`TASK_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_HI_VARINST
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_PROCDEF_INFO
-- ----------------------------
DROP TABLE IF EXISTS `ACT_PROCDEF_INFO`;
CREATE TABLE `ACT_PROCDEF_INFO` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `INFO_JSON_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_IDX_INFO_PROCDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_INFO_JSON_BA` (`INFO_JSON_ID_`),
  CONSTRAINT `ACT_FK_INFO_JSON_BA` FOREIGN KEY (`INFO_JSON_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_INFO_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_PROCDEF_INFO
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RE_DEPLOYMENT
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_DEPLOYMENT`;
CREATE TABLE `ACT_RE_DEPLOYMENT` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `DEPLOY_TIME_` timestamp(3) NULL DEFAULT NULL,
  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `VERSION_` int(11) DEFAULT '1',
  `PROJECT_RELEASE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RE_DEPLOYMENT
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RE_MODEL
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_MODEL`;
CREATE TABLE `ACT_RE_MODEL` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LAST_UPDATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `VERSION_` int(11) DEFAULT NULL,
  `META_INFO_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EDITOR_SOURCE_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EDITOR_SOURCE_EXTRA_VALUE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_MODEL_SOURCE` (`EDITOR_SOURCE_VALUE_ID_`),
  KEY `ACT_FK_MODEL_SOURCE_EXTRA` (`EDITOR_SOURCE_EXTRA_VALUE_ID_`),
  KEY `ACT_FK_MODEL_DEPLOYMENT` (`DEPLOYMENT_ID_`),
  CONSTRAINT `ACT_FK_MODEL_DEPLOYMENT` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `ACT_RE_DEPLOYMENT` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE` FOREIGN KEY (`EDITOR_SOURCE_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_MODEL_SOURCE_EXTRA` FOREIGN KEY (`EDITOR_SOURCE_EXTRA_VALUE_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RE_MODEL
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RE_PROCDEF
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RE_PROCDEF`;
CREATE TABLE `ACT_RE_PROCDEF` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `KEY_` varchar(255) COLLATE utf8_bin NOT NULL,
  `VERSION_` int(11) NOT NULL,
  `DEPLOYMENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DGRM_RESOURCE_NAME_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `HAS_START_FORM_KEY_` tinyint(4) DEFAULT NULL,
  `HAS_GRAPHICAL_NOTATION_` tinyint(4) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `ENGINE_VERSION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `APP_VERSION_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  UNIQUE KEY `ACT_UNIQ_PROCDEF` (`KEY_`,`VERSION_`,`TENANT_ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RE_PROCDEF
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_DEADLETTER_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_DEADLETTER_JOB`;
CREATE TABLE `ACT_RU_DEADLETTER_JOB` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_DEADLETTER_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_DEADLETTER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_DEADLETTER_JOB
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_EVENT_SUBSCR
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EVENT_SUBSCR`;
CREATE TABLE `ACT_RU_EVENT_SUBSCR` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `EVENT_TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EVENT_NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACTIVITY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CONFIGURATION_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`),
  KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`),
  CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_EVENT_SUBSCR
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_EXECUTION
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_EXECUTION`;
CREATE TABLE `ACT_RU_EXECUTION` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `SUPER_EXEC_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ROOT_PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ACT_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `IS_ACTIVE_` tinyint(4) DEFAULT NULL,
  `IS_CONCURRENT_` tinyint(4) DEFAULT NULL,
  `IS_SCOPE_` tinyint(4) DEFAULT NULL,
  `IS_EVENT_SCOPE_` tinyint(4) DEFAULT NULL,
  `IS_MI_ROOT_` tinyint(4) DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `CACHED_ENT_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `START_TIME_` datetime(3) DEFAULT NULL,
  `START_USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `LOCK_TIME_` timestamp(3) NULL DEFAULT NULL,
  `IS_COUNT_ENABLED_` tinyint(4) DEFAULT NULL,
  `EVT_SUBSCR_COUNT_` int(11) DEFAULT NULL,
  `TASK_COUNT_` int(11) DEFAULT NULL,
  `JOB_COUNT_` int(11) DEFAULT NULL,
  `TIMER_JOB_COUNT_` int(11) DEFAULT NULL,
  `SUSP_JOB_COUNT_` int(11) DEFAULT NULL,
  `DEADLETTER_JOB_COUNT_` int(11) DEFAULT NULL,
  `VAR_COUNT_` int(11) DEFAULT NULL,
  `ID_LINK_COUNT_` int(11) DEFAULT NULL,
  `APP_VERSION_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`),
  KEY `ACT_IDC_EXEC_ROOT` (`ROOT_PROC_INST_ID_`),
  KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`),
  KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`),
  KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE,
  CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_EXECUTION
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_IDENTITYLINK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_IDENTITYLINK`;
CREATE TABLE `ACT_RU_IDENTITYLINK` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `GROUP_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `USER_ID_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`),
  KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`),
  KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`),
  KEY `ACT_FK_IDL_PROCINST` (`PROC_INST_ID_`),
  CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_IDL_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `ACT_RU_TASK` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_IDENTITYLINK
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_INTEGRATION
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_INTEGRATION`;
CREATE TABLE `ACT_RU_INTEGRATION` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `FLOW_NODE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `CREATED_DATE_` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_INT_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_INT_PROC_INST` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_INT_PROC_DEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_INT_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`) ON DELETE CASCADE,
  CONSTRAINT `ACT_FK_INT_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_INT_PROC_INST` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_INTEGRATION
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_JOB`;
CREATE TABLE `ACT_RU_JOB` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_JOB
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_SUSPENDED_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_SUSPENDED_JOB`;
CREATE TABLE `ACT_RU_SUSPENDED_JOB` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_SUSPENDED_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_SUSPENDED_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_SUSPENDED_JOB
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_TASK
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_TASK`;
CREATE TABLE `ACT_RU_TASK` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `BUSINESS_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `PARENT_TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DESCRIPTION_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TASK_DEF_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `ASSIGNEE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `DELEGATION_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PRIORITY_` int(11) DEFAULT NULL,
  `CREATE_TIME_` timestamp(3) NULL DEFAULT NULL,
  `DUE_DATE_` datetime(3) DEFAULT NULL,
  `CATEGORY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `SUSPENSION_STATE_` int(11) DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  `FORM_KEY_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `CLAIM_TIME_` datetime(3) DEFAULT NULL,
  `APP_VERSION_` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`),
  KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`),
  CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`),
  CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_TASK
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_TIMER_JOB
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_TIMER_JOB`;
CREATE TABLE `ACT_RU_TIMER_JOB` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `LOCK_EXP_TIME_` timestamp(3) NULL DEFAULT NULL,
  `LOCK_OWNER_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROCESS_INSTANCE_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_DEF_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `RETRIES_` int(11) DEFAULT NULL,
  `EXCEPTION_STACK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `EXCEPTION_MSG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `DUEDATE_` timestamp(3) NULL DEFAULT NULL,
  `REPEAT_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_TYPE_` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `HANDLER_CFG_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TENANT_ID_` varchar(255) COLLATE utf8_bin DEFAULT '',
  PRIMARY KEY (`ID_`),
  KEY `ACT_FK_TIMER_JOB_EXECUTION` (`EXECUTION_ID_`),
  KEY `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` (`PROCESS_INSTANCE_ID_`),
  KEY `ACT_FK_TIMER_JOB_PROC_DEF` (`PROC_DEF_ID_`),
  KEY `ACT_FK_TIMER_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_EXECUTION` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_PROCESS_INSTANCE` FOREIGN KEY (`PROCESS_INSTANCE_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_TIMER_JOB_PROC_DEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `ACT_RE_PROCDEF` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_TIMER_JOB
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for ACT_RU_VARIABLE
-- ----------------------------
DROP TABLE IF EXISTS `ACT_RU_VARIABLE`;
CREATE TABLE `ACT_RU_VARIABLE` (
  `ID_` varchar(64) COLLATE utf8_bin NOT NULL,
  `REV_` int(11) DEFAULT NULL,
  `TYPE_` varchar(255) COLLATE utf8_bin NOT NULL,
  `NAME_` varchar(255) COLLATE utf8_bin NOT NULL,
  `EXECUTION_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `PROC_INST_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `TASK_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `BYTEARRAY_ID_` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `DOUBLE_` double DEFAULT NULL,
  `LONG_` bigint(20) DEFAULT NULL,
  `TEXT_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  `TEXT2_` varchar(4000) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`ID_`),
  KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`),
  KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`),
  KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`),
  KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`),
  CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `ACT_GE_BYTEARRAY` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`),
  CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `ACT_RU_EXECUTION` (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of ACT_RU_VARIABLE
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for bnt_building
-- ----------------------------
DROP TABLE IF EXISTS `bnt_building`;
CREATE TABLE `bnt_building` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(50) NOT NULL COMMENT '楼宇名称',
  `address` varchar(200) DEFAULT NULL COMMENT '楼宇地址',
  `description` varchar(500) DEFAULT NULL COMMENT '楼宇描述',
  `is_deleted` tinyint(3) DEFAULT '0' COMMENT '删除标记（0:未删除 1:已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼宇信息表';

-- ----------------------------
-- Records of bnt_building
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for bnt_meeting
-- ----------------------------
DROP TABLE IF EXISTS `bnt_meeting`;
CREATE TABLE `bnt_meeting` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `meeting_name` varchar(30) NOT NULL COMMENT '会议名称',
  `meeting_room_id` bigint(20) NOT NULL COMMENT '会议室号',
  `meeting_resume` varchar(255) DEFAULT NULL COMMENT '会议简介',
  `start` bigint(255) NOT NULL COMMENT '会议开始时间',
  `end` bigint(255) NOT NULL COMMENT '会议结束时间',
  `user_id` bigint(20) NOT NULL COMMENT '会议创建人',
  `is_deleted` tinyint(3) NOT NULL,
  `is_overtime` tinyint(1) DEFAULT '0' COMMENT '是否已过期：0-未过期，1-已过期',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of bnt_meeting
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for bnt_meeting_room
-- ----------------------------
DROP TABLE IF EXISTS `bnt_meeting_room`;
CREATE TABLE `bnt_meeting_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `room_number` varchar(30) NOT NULL COMMENT '会议室门牌号',
  `is_available` tinyint(1) DEFAULT '1' COMMENT '是否可用（0:不可用 1:可用）',
  `is_deleted` tinyint(3) DEFAULT '0' COMMENT '删除标记（0:未删除 1:已删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `idx_room_number` (`room_number`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COMMENT='会议室信息表';

-- ----------------------------
-- Records of bnt_meeting_room
-- ----------------------------
BEGIN;
INSERT INTO `bnt_meeting_room` VALUES (1, '101', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (2, '202', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (4, '201', 0, 0);
INSERT INTO `bnt_meeting_room` VALUES (5, '203', 0, 0);
INSERT INTO `bnt_meeting_room` VALUES (6, '304', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (7, '308', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (8, '401', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (9, '402', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (10, '403', 0, 0);
INSERT INTO `bnt_meeting_room` VALUES (11, '603', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (12, '604', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (13, '707', 1, 0);
INSERT INTO `bnt_meeting_room` VALUES (14, '701', 1, 0);
COMMIT;

-- ----------------------------
-- Table structure for oa_notification
-- ----------------------------
DROP TABLE IF EXISTS `oa_notification`;
CREATE TABLE `oa_notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除: 0-未删除, 1-已删除',
  `type` int(11) NOT NULL DEFAULT '1' COMMENT '通知类型: 1-待审核任务通知, 2-流程完成通知, 3-流程驳回通知',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知标题',
  `content` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `process_id` bigint(20) DEFAULT NULL COMMENT '关联的流程ID',
  `task_id` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联的流程任务ID',
  `user_id` bigint(20) NOT NULL COMMENT '通知接收人用户ID',
  `status` int(11) NOT NULL DEFAULT '0' COMMENT '通知状态: 0-未读, 1-已读, 2-已处理(已审核)',
  `extra_data` text COLLATE utf8mb4_unicode_ci COMMENT '扩展字段JSON，存储额外信息（如流程类型、申请人等）',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_user_status` (`user_id`,`status`),
  KEY `idx_process_id` (`process_id`),
  KEY `idx_type` (`type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- ----------------------------
-- Records of oa_notification
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for oa_process
-- ----------------------------
DROP TABLE IF EXISTS `oa_process`;
CREATE TABLE `oa_process` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `process_code` varchar(50) NOT NULL DEFAULT '' COMMENT '审批code',
  `user_id` bigint(1) NOT NULL DEFAULT '0' COMMENT '用户id',
  `process_template_id` bigint(20) DEFAULT NULL COMMENT '审批模板id',
  `process_type_id` bigint(20) DEFAULT NULL COMMENT '审批类型id',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `form_values` text COMMENT '表单值',
  `process_instance_id` varchar(255) DEFAULT NULL COMMENT '流程实例id',
  `current_auditor` varchar(255) DEFAULT NULL COMMENT '当前审批人',
  `status` tinyint(3) DEFAULT NULL COMMENT '状态（0：默认 1：审批中 2：审批通过 -1：驳回）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8 COMMENT='审批类型';

-- ----------------------------
-- Records of oa_process
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for oa_process_record
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_record`;
CREATE TABLE `oa_process_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `process_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '审批流程id',
  `description` varchar(255) DEFAULT NULL COMMENT '审批描述',
  `status` tinyint(3) DEFAULT '0' COMMENT '状态',
  `operate_user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '操作用户id',
  `operate_user` varchar(20) DEFAULT NULL COMMENT '操作用户',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8 COMMENT='审批记录';

-- ----------------------------
-- Records of oa_process_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for oa_process_template
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_template`;
CREATE TABLE `oa_process_template` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '模板名称',
  `icon_url` varchar(100) DEFAULT NULL COMMENT '图标路径',
  `process_type_id` varchar(255) DEFAULT NULL,
  `form_props` text COMMENT '表单属性',
  `form_options` text COMMENT '表单选项',
  `process_definition_key` varchar(20) DEFAULT NULL COMMENT '流程定义key',
  `process_definition_path` varchar(255) DEFAULT NULL COMMENT '流程定义上传路径',
  `process_model_id` varchar(255) DEFAULT NULL COMMENT '流程定义模型id',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(3) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COMMENT='审批模板';

-- ----------------------------
-- Records of oa_process_template
-- ----------------------------
BEGIN;
INSERT INTO `oa_process_template` VALUES (9, '出差模版测试01', 'https://gw.alicdn.com/tfs/TB1bHOWCSzqK1RjSZFjXXblCFXa-112-112.png', '5', '[{\"type\":\"input\",\"field\":\"Fyj46af98vsgd\",\"title\":\"测试\",\"info\":\"\",\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}]', '{\"form\":{\"inline\":false,\"labelPosition\":\"right\",\"size\":\"mini\",\"labelWidth\":\"125px\",\"hideRequiredAsterisk\":false,\"showMessage\":true,\"inlineMessage\":false},\"submitBtn\":true,\"resetBtn\":false}', 'process', 'processes/test-process.zip', NULL, '', 1, '2026-03-24 19:20:14', '2026-03-24 20:54:06', 0);
INSERT INTO `oa_process_template` VALUES (10, '测试报销', 'https://gw.alicdn.com/tfs/TB11pS_CFzqK1RjSZSgXXcpAVXa-102-102.png', '4', '[{\"type\":\"input\",\"field\":\"F7m06ag2tczgz\",\"title\":\"申请金额\",\"info\":\"\",\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}]', '{\"form\":{\"inline\":false,\"labelPosition\":\"right\",\"size\":\"mini\",\"labelWidth\":\"125px\",\"hideRequiredAsterisk\":false,\"showMessage\":true,\"inlineMessage\":false},\"submitBtn\":true,\"resetBtn\":false}', 'process', 'processes/baoxiao.zip', NULL, '', 1, '2026-03-26 21:00:12', '2026-03-26 21:00:12', 0);
INSERT INTO `oa_process_template` VALUES (11, '换地方了', 'https://gw.alicdn.com/tfs/TB1cbCYCPTpK1RjSZKPXXa3UpXa-112-112.png', '5', '[{\"type\":\"input\",\"field\":\"Fo7v6aggt76kv\",\"title\":\"来吧\",\"info\":\"\",\"_fc_drag_tag\":\"input\",\"hidden\":false,\"display\":true}]', '{\"form\":{\"inline\":false,\"labelPosition\":\"right\",\"size\":\"mini\",\"labelWidth\":\"125px\",\"hideRequiredAsterisk\":false,\"showMessage\":true,\"inlineMessage\":false},\"submitBtn\":true,\"resetBtn\":false}', 'process', 'processes/baoxiao.zip', NULL, '', 1, '2026-03-27 20:30:26', '2026-03-27 20:30:26', 0);
COMMIT;

-- ----------------------------
-- Table structure for oa_process_type
-- ----------------------------
DROP TABLE IF EXISTS `oa_process_type`;
CREATE TABLE `oa_process_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '类型名称',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='审批类型';

-- ----------------------------
-- Records of oa_process_type
-- ----------------------------
BEGIN;
INSERT INTO `oa_process_type` VALUES (1, '出勤', '出勤', '2023-03-17 20:34:55', '2026-03-24 16:21:31', 1);
INSERT INTO `oa_process_type` VALUES (2, '人事', '人事', '2023-03-17 20:35:21', '2026-03-24 16:21:33', 1);
INSERT INTO `oa_process_type` VALUES (3, '财务', '财务', '2023-03-17 20:35:41', '2026-03-24 16:21:35', 1);
INSERT INTO `oa_process_type` VALUES (4, '财务', '', '2026-03-24 16:21:52', '2026-03-24 16:21:52', 0);
INSERT INTO `oa_process_type` VALUES (5, '出差', '', '2026-03-24 16:23:59', '2026-03-24 16:23:59', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_attendance_record
-- ----------------------------
DROP TABLE IF EXISTS `sys_attendance_record`;
CREATE TABLE `sys_attendance_record` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `clock_date` varchar(10) NOT NULL COMMENT '打卡日期 (格式: yyyy-MM-dd)',
  `clock_type` varchar(20) NOT NULL COMMENT '打卡类型: morning-上班, evening-下班',
  `clock_time` datetime DEFAULT NULL COMMENT '打卡时间',
  `status` varchar(20) NOT NULL COMMENT '打卡状态: normal-正常, late-迟到, early_leave-早退, absent-未打卡',
  `status_desc` varchar(100) DEFAULT NULL COMMENT '打卡状态描述',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_user_date_type` (`user_id`,`clock_date`,`clock_type`),
  KEY `idx_clock_date` (`clock_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COMMENT='打卡明细表';

-- ----------------------------
-- Records of sys_attendance_record
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_attendance_statistics
-- ----------------------------
DROP TABLE IF EXISTS `sys_attendance_statistics`;
CREATE TABLE `sys_attendance_statistics` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `user_name` varchar(50) DEFAULT NULL COMMENT '用户姓名',
  `stat_month` varchar(6) NOT NULL COMMENT '统计月份 (格式: yyyyMM)',
  `should_clock_days` int(11) DEFAULT '0' COMMENT '应打卡天数',
  `actual_clock_days` int(11) DEFAULT '0' COMMENT '实际打卡天数',
  `normal_clock_days` int(11) DEFAULT '0' COMMENT '正常打卡天数',
  `late_days` int(11) DEFAULT '0' COMMENT '迟到天数',
  `early_leave_days` int(11) DEFAULT '0' COMMENT '早退天数',
  `absent_days` int(11) DEFAULT '0' COMMENT '未打卡天数',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '是否删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_month` (`user_id`,`stat_month`),
  KEY `idx_stat_month` (`stat_month`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COMMENT='打卡统计表';

-- ----------------------------
-- Records of sys_attendance_statistics
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(100) DEFAULT NULL COMMENT '部门名称',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级部门id',
  `tree_path` varchar(500) DEFAULT NULL COMMENT '树结构',
  `sort_value` int(11) DEFAULT NULL COMMENT '排序',
  `leader` varchar(50) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(30) DEFAULT NULL COMMENT '电话',
  `status` tinyint(4) DEFAULT '1' COMMENT '状态（1正常 0停用）',
  `description` varchar(500) DEFAULT NULL COMMENT '部门简介',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint(4) DEFAULT '0' COMMENT '逻辑删除标识（0未删除 1已删除）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept` VALUES (1, '产品部', NULL, NULL, 1, '令狐冲', '13701900887', 1, '', NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (2, '设计部', NULL, NULL, 2, '赵佩佩', '15234257147', 1, '', NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (3, '研发部', NULL, NULL, 3, '张三丰', '54358089', 1, '', NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (4, '人事部', NULL, NULL, 4, '岳不群', '13301900889', 1, '', NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (5, '后勤部', NULL, NULL, 5, '黄蓉', '15380967251', 1, '', NULL, NULL, 0);
INSERT INTO `sys_dept` VALUES (6, '删除测试部01', NULL, NULL, 1, '黄巢', '15120778291', 1, '', NULL, NULL, 1);
INSERT INTO `sys_dept` VALUES (9, '事业部', NULL, NULL, 6, '达斯维达', '13902887037', 1, '', NULL, NULL, 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_dept_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept_user`;
CREATE TABLE `sys_dept_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `dept_id` bigint(20) NOT NULL COMMENT '部门id',
  `user_id` bigint(20) NOT NULL COMMENT '用户id',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_dept_id` (`dept_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_dept_user` (`dept_id`,`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COMMENT='部门用户关联表';

-- ----------------------------
-- Records of sys_dept_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_dept_user` VALUES (5, 1, 13, NULL);
INSERT INTO `sys_dept_user` VALUES (6, 2, 14, NULL);
INSERT INTO `sys_dept_user` VALUES (7, 3, 15, NULL);
INSERT INTO `sys_dept_user` VALUES (8, 4, 17, NULL);
INSERT INTO `sys_dept_user` VALUES (9, 5, 16, NULL);
INSERT INTO `sys_dept_user` VALUES (10, 9, 20, NULL);
INSERT INTO `sys_dept_user` VALUES (13, 9, 21, NULL);
COMMIT;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `username` varchar(50) DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) DEFAULT '' COMMENT '登录IP地址',
  `status` tinyint(1) DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) DEFAULT '' COMMENT '提示信息',
  `access_time` datetime DEFAULT NULL COMMENT '访问时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=57 DEFAULT CHARSET=utf8 COMMENT='系统访问记录';

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `parent_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '所属上级',
  `name` varchar(20) NOT NULL DEFAULT '' COMMENT '名称',
  `type` tinyint(3) NOT NULL DEFAULT '0' COMMENT '类型(0:目录,1:菜单,2:按钮)',
  `path` varchar(100) DEFAULT NULL COMMENT '路由地址',
  `component` varchar(100) DEFAULT NULL COMMENT '组件路径',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort_value` int(11) DEFAULT NULL COMMENT '排序',
  `status` tinyint(4) DEFAULT NULL COMMENT '状态(0:禁止,1:正常)',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_menu` VALUES (2, 0, '系统管理', 0, 'system', 'Layout', NULL, 'el-icon-s-tools', 1, 1, '2025-05-31 18:05:37', '2025-03-19 13:56:33', 0);
INSERT INTO `sys_menu` VALUES (3, 2, '用户管理', 1, 'sysUser', 'system/sysUser/list', '', 'el-icon-s-custom', 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:47', 0);
INSERT INTO `sys_menu` VALUES (4, 2, '角色管理', 1, 'sysRole', 'system/sysRole/list', '', 'el-icon-user-solid', 2, 1, '2025-05-31 18:05:37', '2025-06-09 09:37:18', 0);
INSERT INTO `sys_menu` VALUES (5, 2, '菜单管理', 1, 'sysMenu', 'system/sysMenu/list', '', 'el-icon-s-unfold', 3, 1, '2025-05-31 18:05:37', '2025-06-09 09:37:21', 0);
INSERT INTO `sys_menu` VALUES (6, 3, '查看', 2, NULL, NULL, 'bnt.sysUser.list', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (7, 3, '添加', 2, NULL, NULL, 'bnt.sysUser.add', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (8, 3, '修改', 2, NULL, NULL, 'bnt.sysUser.update', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (9, 3, '删除', 2, NULL, NULL, 'bnt.sysUser.remove', NULL, 1, 1, '2025-05-31 18:05:37', '2025-03-19 13:57:36', 0);
INSERT INTO `sys_menu` VALUES (10, 4, '查看', 2, NULL, NULL, 'bnt.sysRole.list', NULL, 1, 1, '2025-05-31 18:05:37', '2025-03-19 13:57:51', 0);
INSERT INTO `sys_menu` VALUES (11, 4, '添加', 2, NULL, NULL, 'bnt.sysRole.add', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (12, 4, '修改', 2, NULL, NULL, 'bnt.sysRole.update', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (13, 4, '删除', 2, NULL, NULL, 'bnt.sysRole.remove', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (14, 5, '查看', 2, NULL, NULL, 'bnt.sysMenu.list', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (15, 5, '添加', 2, NULL, NULL, 'bnt.sysMenu.add', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (16, 5, '修改', 2, NULL, NULL, 'bnt.sysMenu.update', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (17, 5, '删除', 2, NULL, NULL, 'bnt.sysMenu.remove', NULL, 1, 1, '2025-05-31 18:05:37', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (18, 3, '分配角色', 2, NULL, NULL, 'bnt.sysUser.assignRole', NULL, 1, 1, '2025-05-23 17:14:32', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (19, 4, '分配权限', 2, 'assignAuth', 'system/sysRole/assignAuth', 'bnt.sysRole.assignAuth', NULL, 1, 1, '2025-05-23 17:18:14', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (20, 2, '部门管理', 1, 'sysDept', 'system/sysDept/list', '', 'el-icon-s-operation', 4, 1, '2025-05-24 10:07:05', '2025-06-09 09:38:12', 0);
INSERT INTO `sys_menu` VALUES (21, 20, '查看', 2, NULL, NULL, 'bnt.sysDept.list', NULL, 1, 1, '2025-05-24 10:07:44', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (22, 2, '岗位管理', 1, 'sysPost', 'system/sysPost/list', '', 'el-icon-more-outline', 5, 0, '2025-05-24 10:25:30', '2026-03-19 17:40:48', 0);
INSERT INTO `sys_menu` VALUES (23, 22, '查看', 2, NULL, NULL, 'bnt.sysPost.list', NULL, 1, 1, '2025-05-24 10:25:45', '2025-06-09 09:22:38', 0);
INSERT INTO `sys_menu` VALUES (24, 20, '添加', 2, NULL, NULL, 'bnt.sysDept.add', NULL, 1, 1, '2025-05-25 15:31:27', '2026-03-19 14:00:05', 0);
INSERT INTO `sys_menu` VALUES (25, 20, '修改', 2, NULL, NULL, 'bnt.sysDept.update', NULL, 1, 1, '2025-05-25 15:31:41', '2026-03-19 14:00:09', 0);
INSERT INTO `sys_menu` VALUES (26, 20, '删除', 2, NULL, NULL, 'bnt.sysDept.remove', NULL, 1, 1, '2025-05-25 15:31:59', '2026-03-19 14:00:12', 0);
INSERT INTO `sys_menu` VALUES (27, 22, '添加', 2, NULL, NULL, 'bnt.sysPost.add', NULL, 1, 1, '2025-05-25 15:32:44', '2026-03-19 14:00:15', 0);
INSERT INTO `sys_menu` VALUES (28, 22, '修改', 2, NULL, NULL, 'bnt.sysPost.update', NULL, 1, 1, '2025-05-25 15:32:58', '2026-03-19 14:00:18', 0);
INSERT INTO `sys_menu` VALUES (29, 22, '删除', 2, NULL, NULL, 'bnt.sysPost.remove', NULL, 1, 1, '2025-05-25 15:33:11', '2026-03-19 14:00:23', 0);
INSERT INTO `sys_menu` VALUES (30, 34, '操作日志', 1, 'sysOperLog', 'system/sysOperLog/list', '', 'el-icon-document-remove', 7, 1, '2025-05-26 16:09:59', '2025-06-09 09:39:23', 0);
INSERT INTO `sys_menu` VALUES (31, 30, '查看', 2, NULL, NULL, 'bnt.sysOperLog.list', NULL, 1, 1, '2025-05-26 16:10:17', '2026-03-19 14:00:34', 0);
INSERT INTO `sys_menu` VALUES (32, 34, '登录日志', 1, 'sysLoginLog', 'system/sysLoginLog/list', '', 'el-icon-s-goods', 8, 1, '2025-05-26 16:36:13', '2026-03-19 14:00:37', 0);
INSERT INTO `sys_menu` VALUES (33, 32, '查看', 2, NULL, NULL, 'bnt.sysLoginLog.list', NULL, 1, 1, '2025-05-26 16:36:31', '2026-03-19 14:00:40', 0);
INSERT INTO `sys_menu` VALUES (34, 2, '日志管理', 0, 'log', 'ParentView', '', 'el-icon-tickets', 6, 0, '2025-05-31 13:23:07', '2026-03-19 17:41:06', 0);
INSERT INTO `sys_menu` VALUES (35, 0, '审批设置', 0, 'processSet', 'Layout', '', 'el-icon-setting', 1, 1, '2025-12-01 09:32:46', '2026-03-19 14:00:46', 0);
INSERT INTO `sys_menu` VALUES (36, 35, '审批模板', 1, 'processTemplate', 'processSet/processTemplate/list', '', 'el-icon-s-help', 2, 1, '2025-12-01 09:37:08', '2025-12-19 14:10:48', 0);
INSERT INTO `sys_menu` VALUES (37, 36, '查看', 2, '', '', 'bnt.processTemplate.list', '', 1, 1, '2025-12-01 09:37:49', '2025-12-01 09:37:49', 0);
INSERT INTO `sys_menu` VALUES (38, 36, '审批模板设置', 2, 'templateSet', 'processSet/processTemplate/templateSet', 'bnt.processTemplate.templateSet', '', 1, 1, '2025-12-01 14:52:08', '2025-12-13 18:11:56', 0);
INSERT INTO `sys_menu` VALUES (39, 35, '审批类型', 1, 'processType', 'processSet/processType/list', '', 'el-icon-s-unfold', 1, 1, '2025-12-02 14:46:18', '2025-12-13 18:12:24', 0);
INSERT INTO `sys_menu` VALUES (40, 39, '查看', 2, '', '', 'bnt.processType.list', '', 1, 1, '2025-12-02 14:46:41', '2025-12-02 14:46:41', 0);
INSERT INTO `sys_menu` VALUES (41, 0, '审批管理', 0, 'processMgr', 'Layout', '', 'el-icon-more-outline', 1, 1, '2025-12-02 14:48:11', '2025-12-20 09:29:30', 0);
INSERT INTO `sys_menu` VALUES (42, 41, '审批列表', 1, 'process', 'processMgr/process/list', '', 'el-icon-document-remove', 1, 1, '2025-12-02 14:49:06', '2025-12-02 14:59:17', 0);
INSERT INTO `sys_menu` VALUES (43, 42, '查看', 2, '', '', 'bnt.process.list', '', 1, 1, '2025-12-02 14:49:24', '2025-12-02 14:49:24', 0);
INSERT INTO `sys_menu` VALUES (44, 36, '在线流程设置', 2, 'onlineProcessSet', 'processSet/processTemplate/onlineProcessSet', 'bnt.processTemplate.onlineProcessSet', '', 1, 1, '2025-12-08 10:13:15', '2025-12-19 18:57:35', 0);
INSERT INTO `sys_menu` VALUES (45, 39, '添加', 2, '', '', 'bnt.processType.add', '', 1, 1, '2025-12-09 09:14:53', '2025-12-09 09:14:53', 0);
INSERT INTO `sys_menu` VALUES (46, 39, '修改', 2, '', '', 'bnt.processType.update', '', 1, 1, '2025-12-09 09:15:10', '2026-03-19 14:02:06', 0);
INSERT INTO `sys_menu` VALUES (47, 39, '删除', 2, '', '', 'bnt.processType.remove', '', 1, 1, '2025-12-09 09:15:25', '2026-03-19 14:02:09', 0);
INSERT INTO `sys_menu` VALUES (48, 36, '删除', 2, '', '', 'bnt.processTemplate.remove', '', 1, 1, '2025-12-09 09:22:29', '2026-03-19 14:02:12', 0);
INSERT INTO `sys_menu` VALUES (49, 36, '发布', 2, '', '', 'bnt.processTemplate.publish', '', 1, 1, '2025-12-09 09:24:47', '2026-03-19 14:02:14', 0);
INSERT INTO `sys_menu` VALUES (50, 0, '公众号菜单', 0, 'wechat', 'Layout', '', 'el-icon-s-operation', 1, 1, '2025-12-13 09:06:58', '2026-03-19 14:02:18', 0);
INSERT INTO `sys_menu` VALUES (51, 50, '菜单列表', 1, 'menu', 'wechat/menu/list', '', 'el-icon-s-help', 1, 1, '2025-12-13 09:07:52', '2026-03-19 14:02:22', 0);
INSERT INTO `sys_menu` VALUES (52, 51, '查看', 2, '', '', 'bnt.menu.list', '', 1, 1, '2025-12-13 09:08:48', '2026-03-19 14:02:25', 0);
INSERT INTO `sys_menu` VALUES (53, 51, '添加', 2, '', '', 'bnt.menu.add', '', 1, 1, '2025-12-13 16:29:25', '2026-03-19 14:02:28', 0);
INSERT INTO `sys_menu` VALUES (54, 51, '修改', 2, '', '', 'bnt.menu.update', '', 1, 1, '2025-12-13 16:29:41', '2026-03-19 14:02:32', 0);
INSERT INTO `sys_menu` VALUES (55, 51, '删除', 2, '', '', 'bnt.menu.remove', '', 1, 1, '2025-12-13 16:29:59', '2026-03-19 14:02:35', 0);
INSERT INTO `sys_menu` VALUES (56, 51, '删除微信菜单', 2, '', '', 'bnt.menu.removeMenu', '', 1, 1, '2025-12-13 16:30:36', '2026-03-19 14:02:38', 0);
INSERT INTO `sys_menu` VALUES (57, 51, '同步微信菜单', 2, '', '', 'bnt.menu.syncMenu', '', 1, 1, '2025-12-13 16:31:00', '2026-03-19 14:02:42', 0);
INSERT INTO `sys_menu` VALUES (60, 2, '考勤管理', 1, 'sysAttendance', 'system/sysAttendance/list', NULL, 'el-icon-time', 1, 1, '2026-03-18 22:30:16', '2026-03-18 23:38:44', 0);
INSERT INTO `sys_menu` VALUES (61, 0, '会议管理', 0, 'meeting', 'Layout', '', 'el-icon-phone', 1, 1, '2026-03-19 00:28:16', '2026-03-19 00:28:16', 0);
INSERT INTO `sys_menu` VALUES (62, 61, '会议室管理', 1, 'meetingRoom', 'meeting/meetingRoom/list', '', 'el-icon-user-solid', 1, 1, '2026-03-19 00:36:10', '2026-03-19 00:36:10', 0);
INSERT INTO `sys_menu` VALUES (63, 61, '会议预约', 1, 'meetingRes', 'meeting/meetingRes/list', '', 'el-icon-date', 2, 1, '2026-03-19 00:42:18', '2026-03-19 00:42:18', 0);
INSERT INTO `sys_menu` VALUES (64, 62, '查看', 2, '', '', 'bnt.meetingRoom.list', '', 1, 1, '2026-03-19 11:18:25', '2026-03-19 11:18:25', 0);
INSERT INTO `sys_menu` VALUES (65, 62, '创建', 2, '', '', 'bnt.meetingRoom.create', '', 2, 1, '2026-03-19 11:20:29', '2026-03-19 11:20:29', 0);
INSERT INTO `sys_menu` VALUES (66, 62, '删除', 2, '', '', 'bnt.meetingRoom.del', '', 3, 1, '2026-03-19 11:21:07', '2026-03-19 11:21:07', 0);
INSERT INTO `sys_menu` VALUES (67, 62, '更新', 2, '', '', 'bnt.meetingRoom.update', '', 4, 1, '2026-03-19 11:21:40', '2026-03-19 11:21:40', 0);
INSERT INTO `sys_menu` VALUES (68, 63, '添加预定', 2, '', '', 'bnt.meetingRes.add', '', 1, 1, '2026-03-19 11:24:13', '2026-03-19 11:24:13', 0);
INSERT INTO `sys_menu` VALUES (69, 63, '取消预定', 2, '', '', 'bnt.meetingRes.cancel', '', 2, 1, '2026-03-19 11:25:18', '2026-03-19 11:25:18', 0);
INSERT INTO `sys_menu` VALUES (70, 63, '查看预定', 2, '', '', 'bnt.meetingRes.list', '', 3, 1, '2026-03-19 11:25:41', '2026-03-19 11:25:41', 0);
INSERT INTO `sys_menu` VALUES (71, 63, '修改预定', 2, '', '', 'bnt.meetingRes.update', '', 4, 1, '2026-03-19 11:27:28', '2026-03-19 11:27:28', 0);
INSERT INTO `sys_menu` VALUES (72, 42, '审批提交', 2, 'processCommit', 'processMgr/processCommit/list', '', '', 1, 1, '2026-03-24 14:18:49', '2026-03-24 14:18:58', 1);
INSERT INTO `sys_menu` VALUES (73, 41, '审批发起', 1, 'processCommit', 'processMgr/processCommit/list', '', 'el-icon-message', 2, 1, '2026-03-24 14:20:07', '2026-03-24 14:20:07', 0);
INSERT INTO `sys_menu` VALUES (74, 41, '等待审核', 1, 'processWait', 'processMgr/processWait/list', '', 'el-icon-user-solid', 3, 1, '2026-03-25 21:12:13', '2026-03-25 21:12:13', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) DEFAULT '' COMMENT '模块标题',
  `business_type` varchar(20) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(100) DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) DEFAULT '' COMMENT '请求方式',
  `operator_type` varchar(20) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) DEFAULT '' COMMENT '主机地址',
  `oper_param` varchar(2000) DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=98 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
  `name` varchar(50) NOT NULL DEFAULT '' COMMENT '岗位名称',
  `description` varchar(255) NOT NULL DEFAULT '' COMMENT '描述',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态（1正常 0停用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色id',
  `role_name` varchar(20) NOT NULL DEFAULT '' COMMENT '角色名称',
  `role_code` varchar(20) DEFAULT NULL COMMENT '角色编码',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='角色';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_role` VALUES (9, '经理', '001', NULL, '2026-03-07 23:37:41', '2026-03-07 23:37:41', 0);
INSERT INTO `sys_role` VALUES (10, '财务', '002', NULL, '2026-03-07 23:54:28', '2026-03-07 23:54:28', 0);
INSERT INTO `sys_role` VALUES (11, '司机', '003', NULL, '2026-03-07 23:54:36', '2026-03-07 23:54:36', 0);
INSERT INTO `sys_role` VALUES (12, '文员', '004', NULL, '2026-03-07 23:54:44', '2026-03-07 23:54:44', 0);
INSERT INTO `sys_role` VALUES (13, '人事', '005', NULL, '2026-03-07 23:54:54', '2026-03-07 23:54:54', 0);
INSERT INTO `sys_role` VALUES (14, '后勤', '006', NULL, '2026-03-19 13:51:58', '2026-03-19 13:51:58', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL DEFAULT '0',
  `menu_id` bigint(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=476 DEFAULT CHARSET=utf8 COMMENT='角色菜单';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
BEGIN;
INSERT INTO `sys_role_menu` VALUES (33, 9, 2, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (34, 9, 3, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (35, 9, 6, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (36, 9, 7, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (37, 9, 8, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (38, 9, 9, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (39, 9, 18, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (40, 9, 4, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (41, 9, 10, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (42, 9, 11, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (43, 9, 12, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (44, 9, 13, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (45, 9, 19, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (46, 9, 5, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (47, 9, 14, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (48, 9, 15, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (49, 9, 16, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (50, 9, 17, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (51, 9, 20, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (52, 9, 21, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (53, 9, 24, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (54, 9, 25, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (55, 9, 26, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (56, 9, 22, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (57, 9, 23, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (58, 9, 27, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (59, 9, 28, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (60, 9, 29, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (61, 9, 34, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (62, 9, 30, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (63, 9, 31, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (64, 9, 32, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (65, 9, 33, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (66, 9, 35, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (67, 9, 36, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (68, 9, 37, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (69, 9, 38, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (70, 9, 44, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (71, 9, 48, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (72, 9, 49, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (73, 9, 39, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (74, 9, 40, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (75, 9, 45, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (76, 9, 46, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (77, 9, 47, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (78, 9, 41, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (79, 9, 42, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (80, 9, 43, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (81, 9, 50, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (82, 9, 51, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (83, 9, 52, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (84, 9, 53, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (85, 9, 54, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (86, 9, 55, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (87, 9, 56, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (88, 9, 57, '2026-03-09 17:36:24', '2026-03-09 18:10:17', 1);
INSERT INTO `sys_role_menu` VALUES (89, 9, 50, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (90, 9, 51, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (91, 9, 52, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (92, 9, 53, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (93, 9, 54, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (94, 9, 55, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (95, 9, 56, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (96, 9, 57, '2026-03-09 18:10:17', '2026-03-09 23:38:49', 1);
INSERT INTO `sys_role_menu` VALUES (97, 9, 2, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (98, 9, 3, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (99, 9, 6, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (100, 9, 7, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (101, 9, 8, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (102, 9, 9, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (103, 9, 18, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (104, 9, 4, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (105, 9, 10, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (106, 9, 11, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (107, 9, 12, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (108, 9, 13, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (109, 9, 19, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (110, 9, 5, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (111, 9, 14, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (112, 9, 15, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (113, 9, 16, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (114, 9, 17, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (115, 9, 20, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (116, 9, 21, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (117, 9, 24, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (118, 9, 25, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (119, 9, 26, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (120, 9, 22, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (121, 9, 23, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (122, 9, 27, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (123, 9, 28, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (124, 9, 29, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (125, 9, 34, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (126, 9, 30, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (127, 9, 31, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (128, 9, 32, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (129, 9, 33, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (130, 9, 35, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (131, 9, 36, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (132, 9, 37, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (133, 9, 38, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (134, 9, 44, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (135, 9, 48, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (136, 9, 49, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (137, 9, 39, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (138, 9, 40, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (139, 9, 45, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (140, 9, 46, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (141, 9, 47, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (142, 9, 41, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (143, 9, 42, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (144, 9, 43, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (145, 9, 50, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (146, 9, 51, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (147, 9, 52, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (148, 9, 53, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (149, 9, 54, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (150, 9, 55, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (151, 9, 56, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (152, 9, 57, '2026-03-09 23:38:49', '2026-03-19 13:49:15', 1);
INSERT INTO `sys_role_menu` VALUES (153, 9, 2, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (154, 9, 3, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (155, 9, 6, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (156, 9, 7, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (157, 9, 8, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (158, 9, 9, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (159, 9, 18, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (160, 9, 4, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (161, 9, 10, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (162, 9, 11, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (163, 9, 12, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (164, 9, 13, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (165, 9, 19, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (166, 9, 5, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (167, 9, 14, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (168, 9, 15, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (169, 9, 16, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (170, 9, 17, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (171, 9, 20, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (172, 9, 21, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (173, 9, 24, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (174, 9, 25, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (175, 9, 26, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (176, 9, 22, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (177, 9, 23, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (178, 9, 27, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (179, 9, 28, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (180, 9, 29, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (181, 9, 34, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (182, 9, 30, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (183, 9, 31, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (184, 9, 32, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (185, 9, 33, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (186, 9, 35, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (187, 9, 36, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (188, 9, 37, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (189, 9, 38, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (190, 9, 44, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (191, 9, 48, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (192, 9, 49, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (193, 9, 39, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (194, 9, 40, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (195, 9, 45, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (196, 9, 46, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (197, 9, 47, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (198, 9, 41, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (199, 9, 42, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (200, 9, 43, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (201, 9, 50, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (202, 9, 51, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (203, 9, 52, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (204, 9, 53, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (205, 9, 54, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (206, 9, 55, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (207, 9, 56, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (208, 9, 57, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (209, 9, 61, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (210, 9, 62, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (211, 9, 64, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (212, 9, 65, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (213, 9, 66, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (214, 9, 67, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (215, 9, 63, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (216, 9, 68, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (217, 9, 69, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (218, 9, 70, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (219, 9, 71, '2026-03-19 13:49:15', '2026-03-21 12:51:25', 1);
INSERT INTO `sys_role_menu` VALUES (220, 10, 41, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (221, 10, 42, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (222, 10, 43, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (223, 10, 61, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (224, 10, 63, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (225, 10, 68, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (226, 10, 69, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (227, 10, 70, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (228, 10, 71, '2026-03-19 13:50:39', '2026-03-19 13:50:39', 0);
INSERT INTO `sys_role_menu` VALUES (229, 13, 41, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (230, 13, 42, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (231, 13, 43, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (232, 13, 61, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (233, 13, 63, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (234, 13, 68, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (235, 13, 69, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (236, 13, 70, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (237, 13, 71, '2026-03-19 13:51:30', '2026-03-19 13:51:30', 0);
INSERT INTO `sys_role_menu` VALUES (238, 14, 61, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (239, 14, 62, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (240, 14, 64, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (241, 14, 65, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (242, 14, 66, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (243, 14, 67, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (244, 14, 63, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (245, 14, 68, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (246, 14, 69, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (247, 14, 70, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (248, 14, 71, '2026-03-19 13:52:29', '2026-03-19 13:52:29', 0);
INSERT INTO `sys_role_menu` VALUES (249, 9, 2, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (250, 9, 3, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (251, 9, 6, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (252, 9, 7, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (253, 9, 8, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (254, 9, 9, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (255, 9, 18, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (256, 9, 4, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (257, 9, 10, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (258, 9, 11, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (259, 9, 12, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (260, 9, 13, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (261, 9, 19, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (262, 9, 5, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (263, 9, 14, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (264, 9, 15, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (265, 9, 16, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (266, 9, 17, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (267, 9, 20, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (268, 9, 21, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (269, 9, 24, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (270, 9, 25, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (271, 9, 26, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (272, 9, 35, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (273, 9, 36, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (274, 9, 37, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (275, 9, 38, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (276, 9, 44, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (277, 9, 48, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (278, 9, 49, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (279, 9, 39, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (280, 9, 40, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (281, 9, 45, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (282, 9, 46, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (283, 9, 47, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (284, 9, 41, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (285, 9, 42, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (286, 9, 43, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (287, 9, 50, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (288, 9, 51, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (289, 9, 52, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (290, 9, 53, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (291, 9, 54, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (292, 9, 55, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (293, 9, 56, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (294, 9, 57, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (295, 9, 61, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (296, 9, 62, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (297, 9, 64, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (298, 9, 65, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (299, 9, 66, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (300, 9, 67, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (301, 9, 63, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (302, 9, 68, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (303, 9, 69, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (304, 9, 70, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (305, 9, 71, '2026-03-21 12:51:25', '2026-03-24 14:57:15', 1);
INSERT INTO `sys_role_menu` VALUES (306, 9, 2, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (307, 9, 3, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (308, 9, 6, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (309, 9, 7, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (310, 9, 8, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (311, 9, 9, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (312, 9, 18, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (313, 9, 4, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (314, 9, 10, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (315, 9, 11, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (316, 9, 12, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (317, 9, 13, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (318, 9, 19, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (319, 9, 5, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (320, 9, 14, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (321, 9, 15, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (322, 9, 16, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (323, 9, 17, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (324, 9, 20, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (325, 9, 21, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (326, 9, 24, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (327, 9, 25, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (328, 9, 26, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (329, 9, 60, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (330, 9, 35, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (331, 9, 36, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (332, 9, 37, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (333, 9, 38, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (334, 9, 44, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (335, 9, 48, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (336, 9, 49, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (337, 9, 39, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (338, 9, 40, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (339, 9, 45, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (340, 9, 46, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (341, 9, 47, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (342, 9, 41, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (343, 9, 42, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (344, 9, 43, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (345, 9, 73, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (346, 9, 50, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (347, 9, 51, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (348, 9, 52, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (349, 9, 53, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (350, 9, 54, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (351, 9, 55, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (352, 9, 56, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (353, 9, 57, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (354, 9, 61, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (355, 9, 62, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (356, 9, 64, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (357, 9, 65, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (358, 9, 66, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (359, 9, 67, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (360, 9, 63, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (361, 9, 68, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (362, 9, 69, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (363, 9, 70, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (364, 9, 71, '2026-03-24 14:57:15', '2026-03-24 16:17:33', 1);
INSERT INTO `sys_role_menu` VALUES (365, 9, 2, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (366, 9, 60, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (367, 9, 35, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (368, 9, 36, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (369, 9, 37, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (370, 9, 38, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (371, 9, 44, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (372, 9, 48, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (373, 9, 49, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (374, 9, 39, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (375, 9, 40, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (376, 9, 45, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (377, 9, 46, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (378, 9, 47, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (379, 9, 41, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (380, 9, 42, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (381, 9, 43, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (382, 9, 73, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (383, 9, 61, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (384, 9, 62, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (385, 9, 64, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (386, 9, 65, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (387, 9, 66, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (388, 9, 67, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (389, 9, 63, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (390, 9, 68, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (391, 9, 69, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (392, 9, 70, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (393, 9, 71, '2026-03-24 16:17:33', '2026-03-25 21:21:43', 1);
INSERT INTO `sys_role_menu` VALUES (394, 9, 2, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (395, 9, 60, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (396, 9, 35, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (397, 9, 36, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (398, 9, 37, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (399, 9, 38, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (400, 9, 44, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (401, 9, 48, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (402, 9, 49, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (403, 9, 39, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (404, 9, 40, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (405, 9, 45, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (406, 9, 46, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (407, 9, 47, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (408, 9, 41, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (409, 9, 42, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (410, 9, 43, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (411, 9, 73, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (412, 9, 74, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (413, 9, 61, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (414, 9, 62, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (415, 9, 64, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (416, 9, 65, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (417, 9, 66, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (418, 9, 67, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (419, 9, 63, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (420, 9, 68, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (421, 9, 69, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (422, 9, 70, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (423, 9, 71, '2026-03-25 21:21:43', '2026-03-26 21:41:48', 1);
INSERT INTO `sys_role_menu` VALUES (424, 9, 2, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (425, 9, 3, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (426, 9, 6, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (427, 9, 7, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (428, 9, 8, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (429, 9, 9, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (430, 9, 18, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (431, 9, 4, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (432, 9, 10, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (433, 9, 11, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (434, 9, 12, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (435, 9, 13, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (436, 9, 19, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (437, 9, 5, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (438, 9, 14, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (439, 9, 15, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (440, 9, 16, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (441, 9, 17, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (442, 9, 20, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (443, 9, 21, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (444, 9, 24, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (445, 9, 25, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (446, 9, 26, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (447, 9, 60, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (448, 9, 35, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (449, 9, 36, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (450, 9, 37, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (451, 9, 38, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (452, 9, 44, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (453, 9, 48, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (454, 9, 49, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (455, 9, 39, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (456, 9, 40, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (457, 9, 45, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (458, 9, 46, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (459, 9, 47, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (460, 9, 41, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (461, 9, 42, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (462, 9, 43, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (463, 9, 73, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (464, 9, 74, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (465, 9, 61, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (466, 9, 62, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (467, 9, 64, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (468, 9, 65, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (469, 9, 66, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (470, 9, 67, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (471, 9, 63, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (472, 9, 68, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (473, 9, 69, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (474, 9, 70, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
INSERT INTO `sys_role_menu` VALUES (475, 9, 71, '2026-03-26 21:41:48', '2026-03-26 21:41:48', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '会员id',
  `username` varchar(20) NOT NULL DEFAULT '' COMMENT '用户名',
  `password` varchar(32) NOT NULL DEFAULT '' COMMENT '密码',
  `name` varchar(50) DEFAULT NULL COMMENT '姓名',
  `phone` varchar(11) DEFAULT NULL COMMENT '手机',
  `head_url` varchar(200) DEFAULT NULL COMMENT '头像地址',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门id',
  `post_id` bigint(20) DEFAULT NULL COMMENT '岗位id',
  `open_id` varchar(255) DEFAULT NULL COMMENT '微信openId',
  `description` varchar(255) DEFAULT NULL COMMENT '描述',
  `status` tinyint(3) DEFAULT NULL COMMENT '状态（1：正常 0：停用）',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
BEGIN;
INSERT INTO `sys_user` VALUES (1, 'admin', '96e79218965eb72c92a549dd5a330112', '管理员', '15121070882', NULL, NULL, NULL, NULL, NULL, 1, '2026-01-13 16:23:40', '2026-03-19 13:39:03', 0);
INSERT INTO `sys_user` VALUES (13, 'linghuchong', 'e10adc3949ba59abbe56e057f20f883e', '令狐冲', '13701900887', NULL, NULL, NULL, NULL, NULL, 1, '2026-01-08 12:27:18', '2026-03-19 13:40:00', 0);
INSERT INTO `sys_user` VALUES (14, 'zhaopei', 'e10adc3949ba59abbe56e057f20f883e', '赵佩佩', '15234257147', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-09 14:52:45', '2026-03-19 13:39:42', 0);
INSERT INTO `sys_user` VALUES (15, '张三', 'e10adc3949ba59abbe56e057f20f883e', '张三丰', '54358089', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-05 21:58:30', '2026-03-21 12:48:59', 0);
INSERT INTO `sys_user` VALUES (16, 'huangrong', 'e10adc3949ba59abbe56e057f20f883e', '黄蓉', '15380967251', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-19 13:45:37', '2026-03-21 12:49:01', 0);
INSERT INTO `sys_user` VALUES (17, 'yuebuqun', 'e10adc3949ba59abbe56e057f20f883e', '岳不群', '13301900889', NULL, NULL, NULL, NULL, NULL, 0, '2026-03-19 13:54:39', '2026-03-21 12:49:03', 0);
INSERT INTO `sys_user` VALUES (18, 'wangmang', 'e10adc3949ba59abbe56e057f20f883e', '王莽', '13701988337', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-19 20:01:48', '2026-03-21 12:49:05', 0);
INSERT INTO `sys_user` VALUES (19, 'huangchao', 'e10adc3949ba59abbe56e057f20f883e', '黄巢', '15120778291', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-19 20:02:15', '2026-03-21 12:49:07', 0);
INSERT INTO `sys_user` VALUES (20, 'dasiweida', 'e10adc3949ba59abbe56e057f20f883e', '达斯维达', '13902887037', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-19 20:03:15', '2026-03-21 12:49:09', 0);
INSERT INTO `sys_user` VALUES (21, 'luke', 'e10adc3949ba59abbe56e057f20f883e', '卢克', '15143895686', NULL, NULL, NULL, NULL, NULL, 1, '2026-03-19 20:03:47', '2026-03-21 12:49:11', 0);
COMMIT;

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `role_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '角色id',
  `user_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '用户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0' COMMENT '删除标记（0:不可用 1:可用）',
  PRIMARY KEY (`id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_admin_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8 COMMENT='用户角色';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
BEGIN;
INSERT INTO `sys_user_role` VALUES (11, 9, 15, '2026-03-07 23:42:05', '2026-03-07 23:46:04', 1);
INSERT INTO `sys_user_role` VALUES (12, 9, 15, '2026-03-07 23:46:04', '2026-03-08 21:18:33', 1);
INSERT INTO `sys_user_role` VALUES (13, 9, 15, '2026-03-08 21:18:33', '2026-03-08 21:36:04', 1);
INSERT INTO `sys_user_role` VALUES (14, 10, 15, '2026-03-08 21:18:33', '2026-03-08 21:36:04', 1);
INSERT INTO `sys_user_role` VALUES (15, 9, 15, '2026-03-08 21:36:04', '2026-03-08 21:36:22', 1);
INSERT INTO `sys_user_role` VALUES (16, 10, 15, '2026-03-08 21:36:04', '2026-03-08 21:36:22', 1);
INSERT INTO `sys_user_role` VALUES (17, 11, 15, '2026-03-08 21:36:04', '2026-03-08 21:36:22', 1);
INSERT INTO `sys_user_role` VALUES (18, 12, 15, '2026-03-08 21:36:04', '2026-03-08 21:36:22', 1);
INSERT INTO `sys_user_role` VALUES (19, 13, 15, '2026-03-08 21:36:04', '2026-03-08 21:36:22', 1);
INSERT INTO `sys_user_role` VALUES (20, 9, 15, '2026-03-08 21:36:22', '2026-03-09 18:44:05', 1);
INSERT INTO `sys_user_role` VALUES (21, 9, 15, '2026-03-09 18:44:05', '2026-03-09 18:44:27', 1);
INSERT INTO `sys_user_role` VALUES (22, 9, 1, '2026-03-09 18:44:21', '2026-03-09 18:44:21', 0);
INSERT INTO `sys_user_role` VALUES (23, 12, 15, '2026-03-20 10:36:52', '2026-03-20 10:36:52', 0);
INSERT INTO `sys_user_role` VALUES (24, 14, 16, '2026-03-20 10:37:10', '2026-03-20 10:37:10', 0);
INSERT INTO `sys_user_role` VALUES (25, 9, 20, '2026-03-20 10:37:31', '2026-03-20 10:37:31', 0);
INSERT INTO `sys_user_role` VALUES (26, 12, 21, '2026-03-20 10:37:38', '2026-03-20 10:37:38', 0);
INSERT INTO `sys_user_role` VALUES (27, 9, 13, '2026-03-21 12:51:16', '2026-03-21 12:51:35', 1);
INSERT INTO `sys_user_role` VALUES (28, 10, 13, '2026-03-21 12:51:16', '2026-03-21 12:51:35', 1);
INSERT INTO `sys_user_role` VALUES (29, 11, 13, '2026-03-21 12:51:16', '2026-03-21 12:51:35', 1);
INSERT INTO `sys_user_role` VALUES (30, 12, 13, '2026-03-21 12:51:16', '2026-03-21 12:51:35', 1);
INSERT INTO `sys_user_role` VALUES (31, 13, 13, '2026-03-21 12:51:16', '2026-03-21 12:51:35', 1);
INSERT INTO `sys_user_role` VALUES (32, 14, 13, '2026-03-21 12:51:16', '2026-03-21 12:51:35', 1);
INSERT INTO `sys_user_role` VALUES (33, 9, 13, '2026-03-21 12:51:35', '2026-03-21 12:51:35', 0);
INSERT INTO `sys_user_role` VALUES (34, 9, 19, '2026-03-26 21:42:12', '2026-03-26 21:42:12', 0);
COMMIT;

-- ----------------------------
-- Table structure for wechat_menu
-- ----------------------------
DROP TABLE IF EXISTS `wechat_menu`;
CREATE TABLE `wechat_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '上级id',
  `name` varchar(50) DEFAULT NULL COMMENT '菜单名称',
  `type` varchar(10) DEFAULT NULL COMMENT '类型',
  `url` varchar(100) DEFAULT NULL COMMENT '网页 链接，用户点击菜单可打开链接',
  `menu_key` varchar(20) DEFAULT NULL COMMENT '菜单KEY值，用于消息接口推送',
  `sort` tinyint(3) DEFAULT NULL COMMENT '排序',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(3) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COMMENT='菜单';

-- ----------------------------
-- Records of wechat_menu
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
