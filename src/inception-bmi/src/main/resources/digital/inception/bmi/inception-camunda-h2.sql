--
-- Complete the following steps to update the Camunda schema in this file:
--
-- 1. Clone the camunda-bpm-platform repository (https://github.com/camunda/camunda-bpm-platform.git)
--
-- 2. Apply the changes in the appropriate file in under the following folder:
--
--    camunda-bpm-platform/engine/src/main/resources/org/camunda/bpm/engine/db/upgrade
--
--    e.g. h2_engine_7.12_to_7.13.sql
--
create schema CAMUNDA;

create table CAMUNDA.ACT_GE_PROPERTY (
    NAME_ varchar(64),
    VALUE_ varchar(300),
    REV_ integer,
    primary key (NAME_)
);

insert into CAMUNDA.ACT_GE_PROPERTY
values ('schema.version', 'fox', 1);

insert into CAMUNDA.ACT_GE_PROPERTY
values ('schema.history', 'create(fox)', 1);

insert into CAMUNDA.ACT_GE_PROPERTY
values ('next.dbid', '1', 1);

insert into CAMUNDA.ACT_GE_PROPERTY
values ('deployment.lock', '0', 1);

insert into CAMUNDA.ACT_GE_PROPERTY
values ('history.cleanup.job.lock', '0', 1);

insert into CAMUNDA.ACT_GE_PROPERTY
values ('startup.lock', '0', 1);

create table CAMUNDA.ACT_GE_BYTEARRAY (
    ID_ varchar(64),
    REV_ integer,
    NAME_ varchar(255),
    DEPLOYMENT_ID_ varchar(64),
    BYTES_ longvarbinary,
    GENERATED_ bit,
    TENANT_ID_ varchar(64),
    TYPE_ integer,
    CREATE_TIME_ timestamp,
    ROOT_PROC_INST_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_GE_SCHEMA_LOG (
    ID_ varchar(64),
    TIMESTAMP_ timestamp,
    VERSION_ varchar(255),
    primary key (ID_)
);

insert into CAMUNDA.ACT_GE_SCHEMA_LOG
values ('0', CURRENT_TIMESTAMP, '7.13.0');

create table CAMUNDA.ACT_RE_DEPLOYMENT (
    ID_ varchar(64),
    NAME_ varchar(255),
    DEPLOY_TIME_ timestamp,
    SOURCE_ varchar(255),
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_EXECUTION (
    ID_ varchar(64),
    REV_ integer,
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    BUSINESS_KEY_ varchar(255),
    PARENT_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    SUPER_EXEC_ varchar(64),
    SUPER_CASE_EXEC_ varchar(64),
    CASE_INST_ID_ varchar(64),
    ACT_INST_ID_ varchar(64),
    ACT_ID_ varchar(255),
    IS_ACTIVE_ bit,
    IS_CONCURRENT_ bit,
    IS_SCOPE_ bit,
    IS_EVENT_SCOPE_ bit,
    SUSPENSION_STATE_ integer,
    CACHED_ENT_STATE_ integer,
    SEQUENCE_COUNTER_ integer,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_JOB (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    TYPE_ varchar(255) NOT NULL,
    LOCK_EXP_TIME_ timestamp,
    LOCK_OWNER_ varchar(255),
    EXCLUSIVE_ boolean,
    EXECUTION_ID_ varchar(64),
    PROCESS_INSTANCE_ID_ varchar(64),
    PROCESS_DEF_ID_ varchar(64),
    PROCESS_DEF_KEY_ varchar(255),
    RETRIES_ integer,
    EXCEPTION_STACK_ID_ varchar(64),
    EXCEPTION_MSG_ varchar(4000),
    FAILED_ACT_ID_ varchar(255),
    DUEDATE_ timestamp,
    REPEAT_ varchar(255),
    REPEAT_OFFSET_ bigint DEFAULT 0,
    HANDLER_TYPE_ varchar(255),
    HANDLER_CFG_ varchar(4000),
    DEPLOYMENT_ID_ varchar(64),
    SUSPENSION_STATE_ integer NOT NULL DEFAULT 1,
    JOB_DEF_ID_ varchar(64),
    PRIORITY_ bigint NOT NULL DEFAULT 0,
    SEQUENCE_COUNTER_ integer,
    TENANT_ID_ varchar(64),
    CREATE_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_JOBDEF (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    PROC_DEF_ID_ varchar(64),
    PROC_DEF_KEY_ varchar(255),
    ACT_ID_ varchar(255),
    JOB_TYPE_ varchar(255) NOT NULL,
    JOB_CONFIGURATION_ varchar(255),
    SUSPENSION_STATE_ integer,
    JOB_PRIORITY_ bigint,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RE_PROCDEF (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    CATEGORY_ varchar(255),
    NAME_ varchar(255),
    KEY_ varchar(255) NOT NULL,
    VERSION_ integer NOT NULL,
    DEPLOYMENT_ID_ varchar(64),
    RESOURCE_NAME_ varchar(4000),
    DGRM_RESOURCE_NAME_ varchar(4000),
    HAS_START_FORM_KEY_ bit,
    SUSPENSION_STATE_ integer,
    TENANT_ID_ varchar(64),
    VERSION_TAG_ varchar(64),
    HISTORY_TTL_ integer,
    STARTABLE_ boolean NOT NULL default TRUE,
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_TASK (
    ID_ varchar(64),
    REV_ integer,
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    CASE_EXECUTION_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    CASE_DEF_ID_ varchar(64),
    NAME_ varchar(255),
    PARENT_TASK_ID_ varchar(64),
    DESCRIPTION_ varchar(4000),
    TASK_DEF_KEY_ varchar(255),
    OWNER_ varchar(255),
    ASSIGNEE_ varchar(255),
    DELEGATION_ varchar(64),
    PRIORITY_ integer,
    CREATE_TIME_ timestamp,
    DUE_DATE_ timestamp,
    FOLLOW_UP_DATE_ timestamp,
    SUSPENSION_STATE_ integer,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_IDENTITYLINK (
    ID_ varchar(64),
    REV_ integer,
    GROUP_ID_ varchar(255),
    TYPE_ varchar(255),
    USER_ID_ varchar(255),
    TASK_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_VARIABLE (
    ID_ varchar(64) not null,
    REV_ integer,
    TYPE_ varchar(255) not null,
    NAME_ varchar(255) not null,
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    CASE_EXECUTION_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    TASK_ID_ varchar(64),
    BYTEARRAY_ID_ varchar(64),
    DOUBLE_ double,
    LONG_ bigint,
    TEXT_ varchar(4000),
    TEXT2_ varchar(4000),
    VAR_SCOPE_ varchar(64) not null,
    SEQUENCE_COUNTER_ integer,
    IS_CONCURRENT_LOCAL_ bit,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_EVENT_SUBSCR (
    ID_ varchar(64) not null,
    REV_ integer,
    EVENT_TYPE_ varchar(255) not null,
    EVENT_NAME_ varchar(255),
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    ACTIVITY_ID_ varchar(255),
    CONFIGURATION_ varchar(255),
    CREATED_ timestamp not null,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_INCIDENT (
    ID_ varchar(64) not null,
    REV_ integer not null,
    INCIDENT_TIMESTAMP_ timestamp not null,
    INCIDENT_MSG_ varchar(4000),
    INCIDENT_TYPE_ varchar(255) not null,
    EXECUTION_ID_ varchar(64),
    ACTIVITY_ID_ varchar(255),
    FAILED_ACTIVITY_ID_ varchar(255),
    PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    CAUSE_INCIDENT_ID_ varchar(64),
    ROOT_CAUSE_INCIDENT_ID_ varchar(64),
    CONFIGURATION_ varchar(255),
    TENANT_ID_ varchar(64),
    JOB_DEF_ID_ varchar(64),
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_AUTHORIZATION (
    ID_ varchar(64) not null,
    REV_ integer not null,
    TYPE_ integer not null,
    GROUP_ID_ varchar(255),
    USER_ID_ varchar(255),
    RESOURCE_TYPE_ integer not null,
    RESOURCE_ID_ varchar(255),
    PERMS_ integer,
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_FILTER (
    ID_ varchar(64) not null,
    REV_ integer not null,
    RESOURCE_TYPE_ varchar(255) not null,
    NAME_ varchar(255) not null,
    OWNER_ varchar(255),
    QUERY_ CLOB not null,
    PROPERTIES_ CLOB,
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_METER_LOG (
    ID_ varchar(64) not null,
    NAME_ varchar(64) not null,
    REPORTER_ varchar(255),
    VALUE_ long,
    TIMESTAMP_ timestamp,
    MILLISECONDS_ bigint DEFAULT 0,
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_EXT_TASK (
    ID_ varchar(64) not null,
    REV_ integer not null,
    WORKER_ID_ varchar(255),
    TOPIC_NAME_ varchar(255),
    RETRIES_ integer,
    ERROR_MSG_ varchar(4000),
    ERROR_DETAILS_ID_ varchar(64),
    LOCK_EXP_TIME_ timestamp,
    SUSPENSION_STATE_ integer,
    EXECUTION_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    PROC_DEF_KEY_ varchar(255),
    ACT_ID_ varchar(255),
    ACT_INST_ID_ varchar(64),
    TENANT_ID_ varchar(64),
    PRIORITY_ bigint NOT NULL DEFAULT 0,
    primary key (ID_)
);

create table CAMUNDA.ACT_RU_BATCH (
    ID_ varchar(64) not null,
    REV_ integer not null,
    TYPE_ varchar(255),
    TOTAL_JOBS_ integer,
    JOBS_CREATED_ integer,
    JOBS_PER_SEED_ integer,
    INVOCATIONS_PER_JOB_ integer,
    SEED_JOB_DEF_ID_ varchar(64),
    BATCH_JOB_DEF_ID_ varchar(64),
    MONITOR_JOB_DEF_ID_ varchar(64),
    SUSPENSION_STATE_ integer,
    CONFIGURATION_ varchar(255),
    TENANT_ID_ varchar(64),
    CREATE_USER_ID_ varchar(255),
    primary key (ID_)
);

create index ACT_IDX_EXEC_ROOT_PI ON CAMUNDA.ACT_RU_EXECUTION(ROOT_PROC_INST_ID_);
create index ACT_IDX_EXEC_BUSKEY ON CAMUNDA.ACT_RU_EXECUTION(BUSINESS_KEY_);
create index ACT_IDX_EXEC_TENANT_ID ON CAMUNDA.ACT_RU_EXECUTION(TENANT_ID_);
create index ACT_IDX_TASK_CREATE ON CAMUNDA.ACT_RU_TASK(CREATE_TIME_);
create index ACT_IDX_TASK_ASSIGNEE ON CAMUNDA.ACT_RU_TASK(ASSIGNEE_);
create index ACT_IDX_TASK_TENANT_ID ON CAMUNDA.ACT_RU_TASK(TENANT_ID_);
create index ACT_IDX_IDENT_LNK_USER ON CAMUNDA.ACT_RU_IDENTITYLINK(USER_ID_);
create index ACT_IDX_IDENT_LNK_GROUP ON CAMUNDA.ACT_RU_IDENTITYLINK(GROUP_ID_);
create index ACT_IDX_EVENT_SUBSCR_CONFIG_ ON CAMUNDA.ACT_RU_EVENT_SUBSCR(CONFIGURATION_);
create index ACT_IDX_EVENT_SUBSCR_TENANT_ID ON CAMUNDA.ACT_RU_EVENT_SUBSCR(TENANT_ID_);
create index ACT_IDX_VARIABLE_TASK_ID ON CAMUNDA.ACT_RU_VARIABLE(TASK_ID_);
create index ACT_IDX_VARIABLE_TENANT_ID ON CAMUNDA.ACT_RU_VARIABLE(TENANT_ID_);
create index ACT_IDX_ATHRZ_PROCEDEF ON CAMUNDA.ACT_RU_IDENTITYLINK(PROC_DEF_ID_);
create index ACT_IDX_INC_CONFIGURATION ON CAMUNDA.ACT_RU_INCIDENT(CONFIGURATION_);
create index ACT_IDX_INC_TENANT_ID ON CAMUNDA.ACT_RU_INCIDENT(TENANT_ID_);
-- CAM-5914
create index ACT_IDX_JOB_EXECUTION_ID ON CAMUNDA.ACT_RU_JOB(EXECUTION_ID_);
create index ACT_IDX_JOB_HANDLER ON CAMUNDA.ACT_RU_JOB(HANDLER_TYPE_,HANDLER_CFG_);
create index ACT_IDX_JOB_PROCINST ON CAMUNDA.ACT_RU_JOB(PROCESS_INSTANCE_ID_);
create index ACT_IDX_JOB_TENANT_ID ON CAMUNDA.ACT_RU_JOB(TENANT_ID_);
create index ACT_IDX_JOBDEF_TENANT_ID ON CAMUNDA.ACT_RU_JOBDEF(TENANT_ID_);

-- new metric milliseconds column
CREATE INDEX ACT_IDX_METER_LOG_MS ON CAMUNDA.ACT_RU_METER_LOG(MILLISECONDS_);
CREATE INDEX ACT_IDX_METER_LOG_NAME_MS ON CAMUNDA.ACT_RU_METER_LOG(NAME_, MILLISECONDS_);
CREATE INDEX ACT_IDX_METER_LOG_REPORT ON CAMUNDA.ACT_RU_METER_LOG(NAME_, REPORTER_, MILLISECONDS_);

-- old metric timestamp column
CREATE INDEX ACT_IDX_METER_LOG_TIME ON CAMUNDA.ACT_RU_METER_LOG(TIMESTAMP_);
CREATE INDEX ACT_IDX_METER_LOG ON CAMUNDA.ACT_RU_METER_LOG(NAME_, TIMESTAMP_);

create index ACT_IDX_EXT_TASK_TOPIC ON CAMUNDA.ACT_RU_EXT_TASK(TOPIC_NAME_);
create index ACT_IDX_EXT_TASK_TENANT_ID ON CAMUNDA.ACT_RU_EXT_TASK(TENANT_ID_);
create index ACT_IDX_EXT_TASK_PRIORITY ON CAMUNDA.ACT_RU_EXT_TASK(PRIORITY_);
create index ACT_IDX_EXT_TASK_ERR_DETAILS ON CAMUNDA.ACT_RU_EXT_TASK(ERROR_DETAILS_ID_);
create index ACT_IDX_AUTH_GROUP_ID ON CAMUNDA.ACT_RU_AUTHORIZATION(GROUP_ID_);
create index ACT_IDX_JOB_JOB_DEF_ID ON CAMUNDA.ACT_RU_JOB(JOB_DEF_ID_);

-- indexes for deadlock problems - https://app.camunda.com/jira/browse/CAM-2567 --
create index ACT_IDX_INC_CAUSEINCID ON CAMUNDA.ACT_RU_INCIDENT(CAUSE_INCIDENT_ID_);
create index ACT_IDX_INC_EXID ON CAMUNDA.ACT_RU_INCIDENT(EXECUTION_ID_);
create index ACT_IDX_INC_PROCDEFID ON CAMUNDA.ACT_RU_INCIDENT(PROC_DEF_ID_);
create index ACT_IDX_INC_PROCINSTID ON CAMUNDA.ACT_RU_INCIDENT(PROC_INST_ID_);
create index ACT_IDX_INC_ROOTCAUSEINCID ON CAMUNDA.ACT_RU_INCIDENT(ROOT_CAUSE_INCIDENT_ID_);
-- index for deadlock problem - https://app.camunda.com/jira/browse/CAM-4440 --
create index ACT_IDX_AUTH_RESOURCE_ID ON CAMUNDA.ACT_RU_AUTHORIZATION(RESOURCE_ID_);
-- index to prevent deadlock ON CAMUNDA.fk constraint - https://app.camunda.com/jira/browse/CAM-5440 --
create index ACT_IDX_EXT_TASK_EXEC ON CAMUNDA.ACT_RU_EXT_TASK(EXECUTION_ID_);

-- indexes to improve deployment
create index ACT_IDX_BYTEARRAY_ROOT_PI ON CAMUNDA.ACT_GE_BYTEARRAY(ROOT_PROC_INST_ID_);
create index ACT_IDX_BYTEARRAY_RM_TIME ON CAMUNDA.ACT_GE_BYTEARRAY(REMOVAL_TIME_);
create index ACT_IDX_BYTEARRAY_NAME ON CAMUNDA.ACT_GE_BYTEARRAY(NAME_);
create index ACT_IDX_DEPLOYMENT_NAME ON CAMUNDA.ACT_RE_DEPLOYMENT(NAME_);
create index ACT_IDX_DEPLOYMENT_TENANT_ID ON CAMUNDA.ACT_RE_DEPLOYMENT(TENANT_ID_);
create index ACT_IDX_JOBDEF_PROC_DEF_ID ON CAMUNDA.ACT_RU_JOBDEF(PROC_DEF_ID_);
create index ACT_IDX_JOB_HANDLER_TYPE ON CAMUNDA.ACT_RU_JOB(HANDLER_TYPE_);
create index ACT_IDX_EVENT_SUBSCR_EVT_NAME ON CAMUNDA.ACT_RU_EVENT_SUBSCR(EVENT_NAME_);
create index ACT_IDX_PROCDEF_DEPLOYMENT_ID ON CAMUNDA.ACT_RE_PROCDEF(DEPLOYMENT_ID_);
create index ACT_IDX_PROCDEF_TENANT_ID ON CAMUNDA.ACT_RE_PROCDEF(TENANT_ID_);
create index ACT_IDX_PROCDEF_VER_TAG ON CAMUNDA.ACT_RE_PROCDEF(VERSION_TAG_);

alter table CAMUNDA.ACT_GE_BYTEARRAY
    add constraint ACT_FK_BYTEARR_DEPL
        foreign key (DEPLOYMENT_ID_)
            references ACT_RE_DEPLOYMENT;

alter table CAMUNDA.ACT_RU_EXECUTION
    add constraint ACT_FK_EXE_PROCINST
        foreign key (PROC_INST_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_EXECUTION
    add constraint ACT_FK_EXE_PARENT
        foreign key (PARENT_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_EXECUTION
    add constraint ACT_FK_EXE_SUPER
        foreign key (SUPER_EXEC_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_EXECUTION
    add constraint ACT_FK_EXE_PROCDEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);

alter table CAMUNDA.ACT_RU_IDENTITYLINK
    add constraint ACT_FK_TSKASS_TASK
        foreign key (TASK_ID_)
            references ACT_RU_TASK;

alter table CAMUNDA.ACT_RU_IDENTITYLINK
    add constraint ACT_FK_ATHRZ_PROCEDEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF;

alter table CAMUNDA.ACT_RU_TASK
    add constraint ACT_FK_TASK_EXE
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_TASK
    add constraint ACT_FK_TASK_PROCINST
        foreign key (PROC_INST_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_TASK
    add constraint ACT_FK_TASK_PROCDEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF;

alter table CAMUNDA.ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_EXE
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_PROCINST
        foreign key (PROC_INST_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_BYTEARRAY
        foreign key (BYTEARRAY_ID_)
            references ACT_GE_BYTEARRAY;

alter table CAMUNDA.ACT_RU_JOB
    add constraint ACT_FK_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_)
            references ACT_GE_BYTEARRAY;

alter table CAMUNDA.ACT_RU_EVENT_SUBSCR
    add constraint ACT_FK_EVENT_EXEC
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION;

alter table CAMUNDA.ACT_RU_INCIDENT
    add constraint ACT_FK_INC_EXE
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table CAMUNDA.ACT_RU_INCIDENT
    add constraint ACT_FK_INC_PROCINST
        foreign key (PROC_INST_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table CAMUNDA.ACT_RU_INCIDENT
    add constraint ACT_FK_INC_PROCDEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);

alter table CAMUNDA.ACT_RU_INCIDENT
    add constraint ACT_FK_INC_CAUSE
        foreign key (CAUSE_INCIDENT_ID_)
            references ACT_RU_INCIDENT (ID_);

alter table CAMUNDA.ACT_RU_INCIDENT
    add constraint ACT_FK_INC_RCAUSE
        foreign key (ROOT_CAUSE_INCIDENT_ID_)
            references ACT_RU_INCIDENT (ID_);

alter table CAMUNDA.ACT_RU_EXT_TASK
    add constraint ACT_FK_EXT_TASK_ERROR_DETAILS
        foreign key (ERROR_DETAILS_ID_)
            references ACT_GE_BYTEARRAY (ID_);

create index ACT_IDX_INC_JOB_DEF ON CAMUNDA.ACT_RU_INCIDENT(JOB_DEF_ID_);
alter table CAMUNDA.ACT_RU_INCIDENT
    add constraint ACT_FK_INC_JOB_DEF
        foreign key (JOB_DEF_ID_)
            references ACT_RU_JOBDEF (ID_);

alter table CAMUNDA.ACT_RU_AUTHORIZATION
    add constraint ACT_UNIQ_AUTH_USER
        unique (TYPE_, USER_ID_,RESOURCE_TYPE_,RESOURCE_ID_);

alter table CAMUNDA.ACT_RU_AUTHORIZATION
    add constraint ACT_UNIQ_AUTH_GROUP
        unique (TYPE_, GROUP_ID_,RESOURCE_TYPE_,RESOURCE_ID_);

alter table CAMUNDA.ACT_RU_VARIABLE
    add constraint ACT_UNIQ_VARIABLE
        unique (VAR_SCOPE_, NAME_);

alter table CAMUNDA.ACT_RU_EXT_TASK
    add constraint ACT_FK_EXT_TASK_EXE
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_);

create index ACT_IDX_BATCH_SEED_JOB_DEF ON CAMUNDA.ACT_RU_BATCH(SEED_JOB_DEF_ID_);
alter table CAMUNDA.ACT_RU_BATCH
    add constraint ACT_FK_BATCH_SEED_JOB_DEF
        foreign key (SEED_JOB_DEF_ID_)
            references ACT_RU_JOBDEF (ID_);

create index ACT_IDX_BATCH_MONITOR_JOB_DEF ON CAMUNDA.ACT_RU_BATCH(MONITOR_JOB_DEF_ID_);
alter table CAMUNDA.ACT_RU_BATCH
    add constraint ACT_FK_BATCH_MONITOR_JOB_DEF
        foreign key (MONITOR_JOB_DEF_ID_)
            references ACT_RU_JOBDEF (ID_);

create index ACT_IDX_BATCH_JOB_DEF ON CAMUNDA.ACT_RU_BATCH(BATCH_JOB_DEF_ID_);
alter table CAMUNDA.ACT_RU_BATCH
    add constraint ACT_FK_BATCH_JOB_DEF
        foreign key (BATCH_JOB_DEF_ID_)
            references ACT_RU_JOBDEF (ID_);


-- create case definition table --

create table CAMUNDA.ACT_RE_CASE_DEF (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    CATEGORY_ varchar(255),
    NAME_ varchar(255),
    KEY_ varchar(255) NOT NULL,
    VERSION_ integer NOT NULL,
    DEPLOYMENT_ID_ varchar(64),
    RESOURCE_NAME_ varchar(4000),
    DGRM_RESOURCE_NAME_ varchar(4000),
    TENANT_ID_ varchar(64),
    HISTORY_TTL_ integer,
    primary key (ID_)
);

-- create case execution table --

create table CAMUNDA.ACT_RU_CASE_EXECUTION (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    CASE_INST_ID_ varchar(64),
    SUPER_CASE_EXEC_ varchar(64),
    SUPER_EXEC_ varchar(64),
    BUSINESS_KEY_ varchar(255),
    PARENT_ID_ varchar(64),
    CASE_DEF_ID_ varchar(64),
    ACT_ID_ varchar(255),
    PREV_STATE_ integer,
    CURRENT_STATE_ integer,
    REQUIRED_ bit,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

-- create case sentry part table --

create table CAMUNDA.ACT_RU_CASE_SENTRY_PART (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    CASE_INST_ID_ varchar(64),
    CASE_EXEC_ID_ varchar(64),
    SENTRY_ID_ varchar(255),
    TYPE_ varchar(255),
    SOURCE_CASE_EXEC_ID_ varchar(64),
    STANDARD_EVENT_ varchar(255),
    SOURCE_ varchar(255),
    VARIABLE_EVENT_ varchar(255),
    VARIABLE_NAME_ varchar(255),
    SATISFIED_ bit,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

-- create index ON CAMUNDA.business key --
create index ACT_IDX_CASE_EXEC_BUSKEY ON CAMUNDA.ACT_RU_CASE_EXECUTION(BUSINESS_KEY_);

-- create foreign key constraints ON CAMUNDA.ACT_RU_CASE_EXECUTION --
alter table CAMUNDA.ACT_RU_CASE_EXECUTION
    add constraint ACT_FK_CASE_EXE_CASE_INST
        foreign key (CASE_INST_ID_)
            references ACT_RU_CASE_EXECUTION;

alter table CAMUNDA.ACT_RU_CASE_EXECUTION
    add constraint ACT_FK_CASE_EXE_PARENT
        foreign key (PARENT_ID_)
            references ACT_RU_CASE_EXECUTION;

alter table CAMUNDA.ACT_RU_CASE_EXECUTION
    add constraint ACT_FK_CASE_EXE_CASE_DEF
        foreign key (CASE_DEF_ID_)
            references ACT_RE_CASE_DEF;

-- create foreign key constraints ON CAMUNDA.ACT_RU_VARIABLE --
alter table CAMUNDA.ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_CASE_EXE
        foreign key (CASE_EXECUTION_ID_)
            references ACT_RU_CASE_EXECUTION;

alter table CAMUNDA.ACT_RU_VARIABLE
    add constraint ACT_FK_VAR_CASE_INST
        foreign key (CASE_INST_ID_)
            references ACT_RU_CASE_EXECUTION;

-- create foreign key constraints ON CAMUNDA.ACT_RU_TASK --
alter table CAMUNDA.ACT_RU_TASK
    add constraint ACT_FK_TASK_CASE_EXE
        foreign key (CASE_EXECUTION_ID_)
            references ACT_RU_CASE_EXECUTION;

alter table CAMUNDA.ACT_RU_TASK
    add constraint ACT_FK_TASK_CASE_DEF
        foreign key (CASE_DEF_ID_)
            references ACT_RE_CASE_DEF;

-- create foreign key constraints ON CAMUNDA.ACT_RU_CASE_SENTRY_PART --
alter table CAMUNDA.ACT_RU_CASE_SENTRY_PART
    add constraint ACT_FK_CASE_SENTRY_CASE_INST
        foreign key (CASE_INST_ID_)
            references ACT_RU_CASE_EXECUTION;

alter table CAMUNDA.ACT_RU_CASE_SENTRY_PART
    add constraint ACT_FK_CASE_SENTRY_CASE_EXEC
        foreign key (CASE_EXEC_ID_)
            references ACT_RU_CASE_EXECUTION;

create index ACT_IDX_CASE_DEF_TENANT_ID ON CAMUNDA.ACT_RE_CASE_DEF(TENANT_ID_);
create index ACT_IDX_CASE_EXEC_TENANT_ID ON CAMUNDA.ACT_RU_CASE_EXECUTION(TENANT_ID_);
--
-- Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
-- under one or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information regarding copyright
-- ownership. Camunda licenses this file to you under the Apache License,
-- Version 2.0; you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed ON CAMUNDA.an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- create decision definition table --
create table CAMUNDA.ACT_RE_DECISION_DEF (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    CATEGORY_ varchar(255),
    NAME_ varchar(255),
    KEY_ varchar(255) NOT NULL,
    VERSION_ integer NOT NULL,
    DEPLOYMENT_ID_ varchar(64),
    RESOURCE_NAME_ varchar(4000),
    DGRM_RESOURCE_NAME_ varchar(4000),
    DEC_REQ_ID_ varchar(64),
    DEC_REQ_KEY_ varchar(255),
    TENANT_ID_ varchar(64),
    HISTORY_TTL_ integer,
    VERSION_TAG_ varchar(64),
    primary key (ID_)
);

-- create decision requirements definition table --
create table CAMUNDA.ACT_RE_DECISION_REQ_DEF (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    CATEGORY_ varchar(255),
    NAME_ varchar(255),
    KEY_ varchar(255) NOT NULL,
    VERSION_ integer NOT NULL,
    DEPLOYMENT_ID_ varchar(64),
    RESOURCE_NAME_ varchar(4000),
    DGRM_RESOURCE_NAME_ varchar(4000),
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

alter table CAMUNDA.ACT_RE_DECISION_DEF
    add constraint ACT_FK_DEC_REQ
        foreign key (DEC_REQ_ID_)
            references ACT_RE_DECISION_REQ_DEF(ID_);

create index ACT_IDX_DEC_DEF_TENANT_ID ON CAMUNDA.ACT_RE_DECISION_DEF(TENANT_ID_);
create index ACT_IDX_DEC_DEF_REQ_ID ON CAMUNDA.ACT_RE_DECISION_DEF(DEC_REQ_ID_);
create index ACT_IDX_DEC_REQ_DEF_TENANT_ID ON CAMUNDA.ACT_RE_DECISION_REQ_DEF(TENANT_ID_);
--
-- Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
-- under one or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information regarding copyright
-- ownership. Camunda licenses this file to you under the Apache License,
-- Version 2.0; you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed ON CAMUNDA.an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

create table CAMUNDA.ACT_HI_PROCINST (
    ID_ varchar(64) not null,
    PROC_INST_ID_ varchar(64) not null,
    BUSINESS_KEY_ varchar(255),
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64) not null,
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    REMOVAL_TIME_ timestamp,
    DURATION_ bigint,
    START_USER_ID_ varchar(255),
    START_ACT_ID_ varchar(255),
    END_ACT_ID_ varchar(255),
    SUPER_PROCESS_INSTANCE_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    SUPER_CASE_INSTANCE_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    DELETE_REASON_ varchar(4000),
    TENANT_ID_ varchar(64),
    STATE_ varchar(255),
    primary key (ID_),
    unique (PROC_INST_ID_)
);

create table CAMUNDA.ACT_HI_ACTINST (
    ID_ varchar(64) not null,
    PARENT_ACT_INST_ID_ varchar(64),
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64) not null,
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64) not null,
    EXECUTION_ID_ varchar(64) not null,
    ACT_ID_ varchar(255) not null,
    TASK_ID_ varchar(64),
    CALL_PROC_INST_ID_ varchar(64),
    CALL_CASE_INST_ID_ varchar(64),
    ACT_NAME_ varchar(255),
    ACT_TYPE_ varchar(255) not null,
    ASSIGNEE_ varchar(64),
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    DURATION_ bigint,
    ACT_INST_STATE_ integer,
    SEQUENCE_COUNTER_ integer,
    TENANT_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_TASKINST (
    ID_ varchar(64) not null,
    TASK_DEF_KEY_ varchar(255),
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    CASE_DEF_KEY_ varchar(255),
    CASE_DEF_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    CASE_EXECUTION_ID_ varchar(64),
    ACT_INST_ID_ varchar(64),
    NAME_ varchar(255),
    PARENT_TASK_ID_ varchar(64),
    DESCRIPTION_ varchar(4000),
    OWNER_ varchar(255),
    ASSIGNEE_ varchar(255),
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    DURATION_ bigint,
    DELETE_REASON_ varchar(4000),
    PRIORITY_ integer,
    DUE_DATE_ timestamp,
    FOLLOW_UP_DATE_ timestamp,
    TENANT_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_VARINST (
    ID_ varchar(64) not null,
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    CASE_DEF_KEY_ varchar(255),
    CASE_DEF_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    CASE_EXECUTION_ID_ varchar(64),
    TASK_ID_ varchar(64),
    ACT_INST_ID_ varchar(64),
    NAME_ varchar(255) not null,
    VAR_TYPE_ varchar(100),
    CREATE_TIME_ timestamp,
    REV_ integer,
    BYTEARRAY_ID_ varchar(64),
    DOUBLE_ double,
    LONG_ bigint,
    TEXT_ varchar(4000),
    TEXT2_ varchar(4000),
    TENANT_ID_ varchar(64),
    STATE_ varchar(20),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_DETAIL (
    ID_ varchar(64) not null,
    TYPE_ varchar(255) not null,
    TIME_ timestamp not null,
    NAME_ varchar(255) NOT null,
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    CASE_DEF_KEY_ varchar(255),
    CASE_DEF_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    CASE_EXECUTION_ID_ varchar(64),
    TASK_ID_ varchar(64),
    ACT_INST_ID_ varchar(64),
    VAR_INST_ID_ varchar(64),
    VAR_TYPE_ varchar(255),
    REV_ integer,
    BYTEARRAY_ID_ varchar(64),
    DOUBLE_ double,
    LONG_ bigint,
    TEXT_ varchar(4000),
    TEXT2_ varchar(4000),
    SEQUENCE_COUNTER_ integer,
    TENANT_ID_ varchar(64),
    OPERATION_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_IDENTITYLINK (
    ID_ varchar(64) not null,
    TIMESTAMP_ timestamp not null,
    TYPE_ varchar(255),
    USER_ID_ varchar(255),
    GROUP_ID_ varchar(255),
    TASK_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    OPERATION_TYPE_ varchar(64),
    ASSIGNER_ID_ varchar(64),
    PROC_DEF_KEY_ varchar(255),
    TENANT_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_COMMENT (
    ID_ varchar(64) not null,
    TYPE_ varchar(255),
    TIME_ timestamp not null,
    USER_ID_ varchar(255),
    TASK_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    ACTION_ varchar(255),
    MESSAGE_ varchar(4000),
    FULL_MSG_ longvarbinary,
    TENANT_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_ATTACHMENT (
    ID_ varchar(64) not null,
    REV_ integer,
    USER_ID_ varchar(255),
    NAME_ varchar(255),
    DESCRIPTION_ varchar(4000),
    TYPE_ varchar(255),
    TASK_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    URL_ varchar(4000),
    CONTENT_ID_ varchar(64),
    TENANT_ID_ varchar(64),
    CREATE_TIME_ timestamp,
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_OP_LOG (
    ID_ varchar(64) not null,
    DEPLOYMENT_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    PROC_DEF_KEY_ varchar(255),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    CASE_DEF_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    CASE_EXECUTION_ID_ varchar(64),
    TASK_ID_ varchar(64),
    JOB_ID_ varchar(64),
    JOB_DEF_ID_ varchar(64),
    BATCH_ID_ varchar(64),
    USER_ID_ varchar(255),
    TIMESTAMP_ timestamp not null,
    OPERATION_TYPE_ varchar(64),
    OPERATION_ID_ varchar(64),
    ENTITY_TYPE_ varchar(30),
    PROPERTY_ varchar(64),
    ORG_VALUE_ varchar(4000),
    NEW_VALUE_ varchar(4000),
    TENANT_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    CATEGORY_ varchar(64),
    EXTERNAL_TASK_ID_ varchar(64),
    ANNOTATION_ varchar(4000),
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_INCIDENT (
    ID_ varchar(64) not null,
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    CREATE_TIME_ timestamp not null,
    END_TIME_ timestamp,
    INCIDENT_MSG_ varchar(4000),
    INCIDENT_TYPE_ varchar(255) not null,
    ACTIVITY_ID_ varchar(255),
    FAILED_ACTIVITY_ID_ varchar(255),
    CAUSE_INCIDENT_ID_ varchar(64),
    ROOT_CAUSE_INCIDENT_ID_ varchar(64),
    CONFIGURATION_ varchar(255),
    HISTORY_CONFIGURATION_ varchar(255),
    INCIDENT_STATE_ integer,
    TENANT_ID_ varchar(64),
    JOB_DEF_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_JOB_LOG (
    ID_ varchar(64) not null,
    TIMESTAMP_ timestamp not null,
    JOB_ID_ varchar(64) not null,
    JOB_DUEDATE_ timestamp,
    JOB_RETRIES_ integer,
    JOB_PRIORITY_ bigint not null default 0,
    JOB_EXCEPTION_MSG_ varchar(4000),
    JOB_EXCEPTION_STACK_ID_ varchar(64),
    JOB_STATE_ integer,
    JOB_DEF_ID_ varchar(64),
    JOB_DEF_TYPE_ varchar(255),
    JOB_DEF_CONFIGURATION_ varchar(255),
    ACT_ID_ varchar(255),
    FAILED_ACT_ID_ varchar(255),
    EXECUTION_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROCESS_INSTANCE_ID_ varchar(64),
    PROCESS_DEF_ID_ varchar(64),
    PROCESS_DEF_KEY_ varchar(255),
    DEPLOYMENT_ID_ varchar(64),
    SEQUENCE_COUNTER_ integer,
    TENANT_ID_ varchar(64),
    HOSTNAME_ varchar(255),
    REMOVAL_TIME_ timestamp,

    primary key (ID_)
);

create table CAMUNDA.ACT_HI_BATCH (
    ID_ varchar(64) not null,
    TYPE_ varchar(255),
    TOTAL_JOBS_ integer,
    JOBS_PER_SEED_ integer,
    INVOCATIONS_PER_JOB_ integer,
    SEED_JOB_DEF_ID_ varchar(64),
    MONITOR_JOB_DEF_ID_ varchar(64),
    BATCH_JOB_DEF_ID_ varchar(64),
    TENANT_ID_  varchar(64),
    CREATE_USER_ID_ varchar(255),
    START_TIME_ timestamp not null,
    END_TIME_ timestamp,
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create table CAMUNDA.ACT_HI_EXT_TASK_LOG (
    ID_ varchar(64) not null,
    TIMESTAMP_ timestamp not null,
    EXT_TASK_ID_ varchar(64) not null,
    RETRIES_ integer,
    TOPIC_NAME_ varchar(255),
    WORKER_ID_ varchar(255),
    PRIORITY_ bigint not null default 0,
    ERROR_MSG_ varchar(4000),
    ERROR_DETAILS_ID_ varchar(64),
    ACT_ID_ varchar(255),
    ACT_INST_ID_ varchar(64),
    EXECUTION_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    PROC_DEF_KEY_ varchar(255),
    TENANT_ID_ varchar(64),
    STATE_ integer,
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

create index ACT_IDX_HI_PRO_INST_END ON CAMUNDA.ACT_HI_PROCINST(END_TIME_);
create index ACT_IDX_HI_PRO_I_BUSKEY ON CAMUNDA.ACT_HI_PROCINST(BUSINESS_KEY_);
create index ACT_IDX_HI_PRO_INST_TENANT_ID ON CAMUNDA.ACT_HI_PROCINST(TENANT_ID_);
create index ACT_IDX_HI_PRO_INST_PROC_DEF_KEY ON CAMUNDA.ACT_HI_PROCINST(PROC_DEF_KEY_);
create index ACT_IDX_HI_PRO_INST_PROC_TIME ON CAMUNDA.ACT_HI_PROCINST(START_TIME_, END_TIME_);
create index ACT_IDX_HI_PI_PDEFID_END_TIME ON CAMUNDA.ACT_HI_PROCINST(PROC_DEF_ID_, END_TIME_);
create index ACT_IDX_HI_PRO_INST_ROOT_PI ON CAMUNDA.ACT_HI_PROCINST(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_PRO_INST_RM_TIME ON CAMUNDA.ACT_HI_PROCINST(REMOVAL_TIME_);

create index ACT_IDX_HI_ACTINST_ROOT_PI ON CAMUNDA.ACT_HI_ACTINST(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_ACT_INST_START_END ON CAMUNDA.ACT_HI_ACTINST(START_TIME_, END_TIME_);
create index ACT_IDX_HI_ACT_INST_END ON CAMUNDA.ACT_HI_ACTINST(END_TIME_);
create index ACT_IDX_HI_ACT_INST_PROCINST ON CAMUNDA.ACT_HI_ACTINST(PROC_INST_ID_, ACT_ID_);
create index ACT_IDX_HI_ACT_INST_COMP ON CAMUNDA.ACT_HI_ACTINST(EXECUTION_ID_, ACT_ID_, END_TIME_, ID_);
create index ACT_IDX_HI_ACT_INST_STATS ON CAMUNDA.ACT_HI_ACTINST(PROC_DEF_ID_, PROC_INST_ID_, ACT_ID_, END_TIME_, ACT_INST_STATE_);
create index ACT_IDX_HI_ACT_INST_TENANT_ID ON CAMUNDA.ACT_HI_ACTINST(TENANT_ID_);
create index ACT_IDX_HI_ACT_INST_PROC_DEF_KEY ON CAMUNDA.ACT_HI_ACTINST(PROC_DEF_KEY_);
create index ACT_IDX_HI_AI_PDEFID_END_TIME ON CAMUNDA.ACT_HI_ACTINST(PROC_DEF_ID_, END_TIME_);
create index ACT_IDX_HI_ACT_INST_RM_TIME ON CAMUNDA.ACT_HI_ACTINST(REMOVAL_TIME_);

create index ACT_IDX_HI_DETAIL_ROOT_PI ON CAMUNDA.ACT_HI_DETAIL(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_DETAIL_PROC_INST ON CAMUNDA.ACT_HI_DETAIL(PROC_INST_ID_);
create index ACT_IDX_HI_DETAIL_ACT_INST ON CAMUNDA.ACT_HI_DETAIL(ACT_INST_ID_);
create index ACT_IDX_HI_DETAIL_CASE_INST ON CAMUNDA.ACT_HI_DETAIL(CASE_INST_ID_);
create index ACT_IDX_HI_DETAIL_CASE_EXEC ON CAMUNDA.ACT_HI_DETAIL(CASE_EXECUTION_ID_);
create index ACT_IDX_HI_DETAIL_TIME ON CAMUNDA.ACT_HI_DETAIL(TIME_);
create index ACT_IDX_HI_DETAIL_NAME ON CAMUNDA.ACT_HI_DETAIL(NAME_);
create index ACT_IDX_HI_DETAIL_TASK_ID ON CAMUNDA.ACT_HI_DETAIL(TASK_ID_);
create index ACT_IDX_HI_DETAIL_TENANT_ID ON CAMUNDA.ACT_HI_DETAIL(TENANT_ID_);
create index ACT_IDX_HI_DETAIL_PROC_DEF_KEY ON CAMUNDA.ACT_HI_DETAIL(PROC_DEF_KEY_);
create index ACT_IDX_HI_DETAIL_BYTEAR ON CAMUNDA.ACT_HI_DETAIL(BYTEARRAY_ID_);
create index ACT_IDX_HI_DETAIL_RM_TIME ON CAMUNDA.ACT_HI_DETAIL(REMOVAL_TIME_);
create index ACT_IDX_HI_DETAIL_TASK_BYTEAR ON CAMUNDA.ACT_HI_DETAIL(BYTEARRAY_ID_, TASK_ID_);
create index ACT_IDX_HI_DETAIL_VAR_INST_ID ON CAMUNDA.ACT_HI_DETAIL(VAR_INST_ID_);

create index ACT_IDX_HI_IDENT_LNK_ROOT_PI ON CAMUNDA.ACT_HI_IDENTITYLINK(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_IDENT_LNK_USER ON CAMUNDA.ACT_HI_IDENTITYLINK(USER_ID_);
create index ACT_IDX_HI_IDENT_LNK_TENANT_ID ON CAMUNDA.ACT_HI_IDENTITYLINK(TENANT_ID_);
create index ACT_IDX_HI_IDENT_LNK_GROUP ON CAMUNDA.ACT_HI_IDENTITYLINK(GROUP_ID_);
create index ACT_IDX_HI_IDENT_LNK_PROC_DEF_KEY ON CAMUNDA.ACT_HI_IDENTITYLINK(PROC_DEF_KEY_);
create index ACT_IDX_HI_IDENT_LINK_TASK ON CAMUNDA.ACT_HI_IDENTITYLINK(TASK_ID_);
create index ACT_IDX_HI_IDENT_LINK_RM_TIME ON CAMUNDA.ACT_HI_IDENTITYLINK(REMOVAL_TIME_);
create index ACT_IDX_HI_IDENT_LNK_TIMESTAMP ON CAMUNDA.ACT_HI_IDENTITYLINK(TIMESTAMP_);

create index ACT_IDX_HI_TASKINST_ROOT_PI ON CAMUNDA.ACT_HI_TASKINST(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_TASK_INST_TENANT_ID ON CAMUNDA.ACT_HI_TASKINST(TENANT_ID_);
create index ACT_IDX_HI_TASK_INST_PROC_DEF_KEY ON CAMUNDA.ACT_HI_TASKINST(PROC_DEF_KEY_);
create index ACT_IDX_HI_TASKINST_PROCINST ON CAMUNDA.ACT_HI_TASKINST(PROC_INST_ID_);
create index ACT_IDX_HI_TASKINSTID_PROCINST ON CAMUNDA.ACT_HI_TASKINST(ID_,PROC_INST_ID_);
create index ACT_IDX_HI_TASK_INST_RM_TIME ON CAMUNDA.ACT_HI_TASKINST(REMOVAL_TIME_);
create index ACT_IDX_HI_TASK_INST_START ON CAMUNDA.ACT_HI_TASKINST(START_TIME_);
create index ACT_IDX_HI_TASK_INST_END ON CAMUNDA.ACT_HI_TASKINST(END_TIME_);

create index ACT_IDX_HI_VARINST_ROOT_PI ON CAMUNDA.ACT_HI_VARINST(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_PROCVAR_PROC_INST ON CAMUNDA.ACT_HI_VARINST(PROC_INST_ID_);
create index ACT_IDX_HI_PROCVAR_NAME_TYPE ON CAMUNDA.ACT_HI_VARINST(NAME_, VAR_TYPE_);
create index ACT_IDX_HI_CASEVAR_CASE_INST ON CAMUNDA.ACT_HI_VARINST(CASE_INST_ID_);
create index ACT_IDX_HI_VAR_INST_TENANT_ID ON CAMUNDA.ACT_HI_VARINST(TENANT_ID_);
create index ACT_IDX_HI_VAR_INST_PROC_DEF_KEY ON CAMUNDA.ACT_HI_VARINST(PROC_DEF_KEY_);
create index ACT_IDX_HI_VARINST_BYTEAR ON CAMUNDA.ACT_HI_VARINST(BYTEARRAY_ID_);
create index ACT_IDX_HI_VARINST_RM_TIME ON CAMUNDA.ACT_HI_VARINST(REMOVAL_TIME_);
create index ACT_IDX_HI_VAR_PI_NAME_TYPE on CAMUNDA.ACT_HI_VARINST(PROC_INST_ID_, NAME_, VAR_TYPE_);

create index ACT_IDX_HI_INCIDENT_TENANT_ID ON CAMUNDA.ACT_HI_INCIDENT(TENANT_ID_);
create index ACT_IDX_HI_INCIDENT_PROC_DEF_KEY ON CAMUNDA.ACT_HI_INCIDENT(PROC_DEF_KEY_);
create index ACT_IDX_HI_INCIDENT_ROOT_PI ON CAMUNDA.ACT_HI_INCIDENT(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_INCIDENT_PROCINST ON CAMUNDA.ACT_HI_INCIDENT(PROC_INST_ID_);
create index ACT_IDX_HI_INCIDENT_RM_TIME ON CAMUNDA.ACT_HI_INCIDENT(REMOVAL_TIME_);

create index ACT_IDX_HI_JOB_LOG_ROOT_PI ON CAMUNDA.ACT_HI_JOB_LOG(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_JOB_LOG_PROCINST ON CAMUNDA.ACT_HI_JOB_LOG(PROCESS_INSTANCE_ID_);
create index ACT_IDX_HI_JOB_LOG_PROCDEF ON CAMUNDA.ACT_HI_JOB_LOG(PROCESS_DEF_ID_);
create index ACT_IDX_HI_JOB_LOG_TENANT_ID ON CAMUNDA.ACT_HI_JOB_LOG(TENANT_ID_);
create index ACT_IDX_HI_JOB_LOG_JOB_DEF_ID ON CAMUNDA.ACT_HI_JOB_LOG(JOB_DEF_ID_);
create index ACT_IDX_HI_JOB_LOG_PROC_DEF_KEY ON CAMUNDA.ACT_HI_JOB_LOG(PROCESS_DEF_KEY_);
create index ACT_IDX_HI_JOB_LOG_EX_STACK ON CAMUNDA.ACT_HI_JOB_LOG(JOB_EXCEPTION_STACK_ID_);
create index ACT_IDX_HI_JOB_LOG_RM_TIME ON CAMUNDA.ACT_HI_JOB_LOG(REMOVAL_TIME_);
create index ACT_IDX_HI_JOB_LOG_JOB_CONF ON CAMUNDA.ACT_HI_JOB_LOG(JOB_DEF_CONFIGURATION_);

create index ACT_HI_BAT_RM_TIME ON CAMUNDA.ACT_HI_BATCH(REMOVAL_TIME_);

create index ACT_HI_EXT_TASK_LOG_ROOT_PI ON CAMUNDA.ACT_HI_EXT_TASK_LOG(ROOT_PROC_INST_ID_);
create index ACT_HI_EXT_TASK_LOG_PROCINST ON CAMUNDA.ACT_HI_EXT_TASK_LOG(PROC_INST_ID_);
create index ACT_HI_EXT_TASK_LOG_PROCDEF ON CAMUNDA.ACT_HI_EXT_TASK_LOG(PROC_DEF_ID_);
create index ACT_HI_EXT_TASK_LOG_PROC_DEF_KEY ON CAMUNDA.ACT_HI_EXT_TASK_LOG(PROC_DEF_KEY_);
create index ACT_HI_EXT_TASK_LOG_TENANT_ID ON CAMUNDA.ACT_HI_EXT_TASK_LOG(TENANT_ID_);
create index ACT_IDX_HI_EXTTASKLOG_ERRORDET ON CAMUNDA.ACT_HI_EXT_TASK_LOG(ERROR_DETAILS_ID_);
create index ACT_HI_EXT_TASK_LOG_RM_TIME ON CAMUNDA.ACT_HI_EXT_TASK_LOG(REMOVAL_TIME_);

create index ACT_IDX_HI_OP_LOG_ROOT_PI ON CAMUNDA.ACT_HI_OP_LOG(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_OP_LOG_PROCINST ON CAMUNDA.ACT_HI_OP_LOG(PROC_INST_ID_);
create index ACT_IDX_HI_OP_LOG_PROCDEF ON CAMUNDA.ACT_HI_OP_LOG(PROC_DEF_ID_);
create index ACT_IDX_HI_OP_LOG_TASK ON CAMUNDA.ACT_HI_OP_LOG(TASK_ID_);
create index ACT_IDX_HI_OP_LOG_RM_TIME ON CAMUNDA.ACT_HI_OP_LOG(REMOVAL_TIME_);
create index ACT_IDX_HI_OP_LOG_TIMESTAMP ON CAMUNDA.ACT_HI_OP_LOG(TIMESTAMP_);
create index ACT_IDX_HI_OP_LOG_USER_ID ON CAMUNDA.ACT_HI_OP_LOG(USER_ID_);
create index ACT_IDX_HI_OP_LOG_OP_TYPE ON CAMUNDA.ACT_HI_OP_LOG(OPERATION_TYPE_);
create index ACT_IDX_HI_OP_LOG_ENTITY_TYPE ON CAMUNDA.ACT_HI_OP_LOG(ENTITY_TYPE_);

create index ACT_IDX_HI_COMMENT_TASK ON CAMUNDA.ACT_HI_COMMENT(TASK_ID_);
create index ACT_IDX_HI_COMMENT_ROOT_PI ON CAMUNDA.ACT_HI_COMMENT(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_COMMENT_PROCINST ON CAMUNDA.ACT_HI_COMMENT(PROC_INST_ID_);
create index ACT_IDX_HI_COMMENT_RM_TIME ON CAMUNDA.ACT_HI_COMMENT(REMOVAL_TIME_);

create index ACT_IDX_HI_ATTACHMENT_CONTENT ON CAMUNDA.ACT_HI_ATTACHMENT(CONTENT_ID_);
create index ACT_IDX_HI_ATTACHMENT_ROOT_PI ON CAMUNDA.ACT_HI_ATTACHMENT(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_ATTACHMENT_PROCINST ON CAMUNDA.ACT_HI_ATTACHMENT(PROC_INST_ID_);
create index ACT_IDX_HI_ATTACHMENT_TASK ON CAMUNDA.ACT_HI_ATTACHMENT(TASK_ID_);
create index ACT_IDX_HI_ATTACHMENT_RM_TIME ON CAMUNDA.ACT_HI_ATTACHMENT(REMOVAL_TIME_);
--
-- Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
-- under one or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information regarding copyright
-- ownership. Camunda licenses this file to you under the Apache License,
-- Version 2.0; you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed ON CAMUNDA.an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

create table CAMUNDA.ACT_HI_CASEINST (
    ID_ varchar(64) not null,
    CASE_INST_ID_ varchar(64) not null,
    BUSINESS_KEY_ varchar(255),
    CASE_DEF_ID_ varchar(64) not null,
    CREATE_TIME_ timestamp not null,
    CLOSE_TIME_ timestamp,
    DURATION_ bigint,
    STATE_ integer,
    CREATE_USER_ID_ varchar(255),
    SUPER_CASE_INSTANCE_ID_ varchar(64),
    SUPER_PROCESS_INSTANCE_ID_ varchar(64),
    TENANT_ID_ varchar(64),
    primary key (ID_),
    unique (CASE_INST_ID_)
);

create table CAMUNDA.ACT_HI_CASEACTINST (
    ID_ varchar(64) not null,
    PARENT_ACT_INST_ID_ varchar(64),
    CASE_DEF_ID_ varchar(64) not null,
    CASE_INST_ID_ varchar(64) not null,
    CASE_ACT_ID_ varchar(255) not null,
    TASK_ID_ varchar(64),
    CALL_PROC_INST_ID_ varchar(64),
    CALL_CASE_INST_ID_ varchar(64),
    CASE_ACT_NAME_ varchar(255),
    CASE_ACT_TYPE_ varchar(255),
    CREATE_TIME_ timestamp not null,
    END_TIME_ timestamp,
    DURATION_ bigint,
    STATE_ integer,
    REQUIRED_ bit,
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

create index ACT_IDX_HI_CAS_I_CLOSE ON CAMUNDA.ACT_HI_CASEINST(CLOSE_TIME_);
create index ACT_IDX_HI_CAS_I_BUSKEY ON CAMUNDA.ACT_HI_CASEINST(BUSINESS_KEY_);
create index ACT_IDX_HI_CAS_I_TENANT_ID ON CAMUNDA.ACT_HI_CASEINST(TENANT_ID_);
create index ACT_IDX_HI_CAS_A_I_CREATE ON CAMUNDA.ACT_HI_CASEACTINST(CREATE_TIME_);
create index ACT_IDX_HI_CAS_A_I_END ON CAMUNDA.ACT_HI_CASEACTINST(END_TIME_);
create index ACT_IDX_HI_CAS_A_I_COMP ON CAMUNDA.ACT_HI_CASEACTINST(CASE_ACT_ID_, END_TIME_, ID_);
create index ACT_IDX_HI_CAS_A_I_TENANT_ID ON CAMUNDA.ACT_HI_CASEACTINST(TENANT_ID_);
--
-- Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
-- under one or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information regarding copyright
-- ownership. Camunda licenses this file to you under the Apache License,
-- Version 2.0; you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
--
--     http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed ON CAMUNDA.an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
--

-- create history decision instance table --
create table CAMUNDA.ACT_HI_DECINST (
    ID_ varchar(64) NOT NULL,
    DEC_DEF_ID_ varchar(64) NOT NULL,
    DEC_DEF_KEY_ varchar(255) NOT NULL,
    DEC_DEF_NAME_ varchar(255),
    PROC_DEF_KEY_ varchar(255),
    PROC_DEF_ID_ varchar(64),
    PROC_INST_ID_ varchar(64),
    CASE_DEF_KEY_ varchar(255),
    CASE_DEF_ID_ varchar(64),
    CASE_INST_ID_ varchar(64),
    ACT_INST_ID_ varchar(64),
    ACT_ID_ varchar(255),
    EVAL_TIME_ timestamp not null,
    REMOVAL_TIME_ timestamp,
    COLLECT_VALUE_ double,
    USER_ID_ varchar(255),
    ROOT_DEC_INST_ID_ varchar(64),
    ROOT_PROC_INST_ID_ varchar(64),
    DEC_REQ_ID_ varchar(64),
    DEC_REQ_KEY_ varchar(255),
    TENANT_ID_ varchar(64),
    primary key (ID_)
);

-- create history decision input table --
create table CAMUNDA.ACT_HI_DEC_IN (
    ID_ varchar(64) NOT NULL,
    DEC_INST_ID_ varchar(64) NOT NULL,
    CLAUSE_ID_ varchar(64),
    CLAUSE_NAME_ varchar(255),
    VAR_TYPE_ varchar(100),
    BYTEARRAY_ID_ varchar(64),
    DOUBLE_ double,
    LONG_ bigint,
    TEXT_ varchar(4000),
    TEXT2_ varchar(4000),
    TENANT_ID_ varchar(64),
    CREATE_TIME_ timestamp,
    ROOT_PROC_INST_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);

-- create history decision output table --
create table CAMUNDA.ACT_HI_DEC_OUT (
    ID_ varchar(64) NOT NULL,
    DEC_INST_ID_ varchar(64) NOT NULL,
    CLAUSE_ID_ varchar(64),
    CLAUSE_NAME_ varchar(255),
    RULE_ID_ varchar(64),
    RULE_ORDER_ integer,
    VAR_NAME_ varchar(255),
    VAR_TYPE_ varchar(100),
    BYTEARRAY_ID_ varchar(64),
    DOUBLE_ double,
    LONG_ bigint,
    TEXT_ varchar(4000),
    TEXT2_ varchar(4000),
    TENANT_ID_ varchar(64),
    CREATE_TIME_ timestamp,
    ROOT_PROC_INST_ID_ varchar(64),
    REMOVAL_TIME_ timestamp,
    primary key (ID_)
);


create index ACT_IDX_HI_DEC_INST_ID ON CAMUNDA.ACT_HI_DECINST(DEC_DEF_ID_);
create index ACT_IDX_HI_DEC_INST_KEY ON CAMUNDA.ACT_HI_DECINST(DEC_DEF_KEY_);
create index ACT_IDX_HI_DEC_INST_PI ON CAMUNDA.ACT_HI_DECINST(PROC_INST_ID_);
create index ACT_IDX_HI_DEC_INST_CI ON CAMUNDA.ACT_HI_DECINST(CASE_INST_ID_);
create index ACT_IDX_HI_DEC_INST_ACT ON CAMUNDA.ACT_HI_DECINST(ACT_ID_);
create index ACT_IDX_HI_DEC_INST_ACT_INST ON CAMUNDA.ACT_HI_DECINST(ACT_INST_ID_);
create index ACT_IDX_HI_DEC_INST_TIME ON CAMUNDA.ACT_HI_DECINST(EVAL_TIME_);
create index ACT_IDX_HI_DEC_INST_TENANT_ID ON CAMUNDA.ACT_HI_DECINST(TENANT_ID_);
create index ACT_IDX_HI_DEC_INST_ROOT_ID ON CAMUNDA.ACT_HI_DECINST(ROOT_DEC_INST_ID_);
create index ACT_IDX_HI_DEC_INST_REQ_ID ON CAMUNDA.ACT_HI_DECINST(DEC_REQ_ID_);
create index ACT_IDX_HI_DEC_INST_REQ_KEY ON CAMUNDA.ACT_HI_DECINST(DEC_REQ_KEY_);
create index ACT_IDX_HI_DEC_INST_ROOT_PI ON CAMUNDA.ACT_HI_DECINST(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_DEC_INST_RM_TIME ON CAMUNDA.ACT_HI_DECINST(REMOVAL_TIME_);

create index ACT_IDX_HI_DEC_IN_INST ON CAMUNDA.ACT_HI_DEC_IN(DEC_INST_ID_);
create index ACT_IDX_HI_DEC_IN_CLAUSE ON CAMUNDA.ACT_HI_DEC_IN(DEC_INST_ID_, CLAUSE_ID_);
create index ACT_IDX_HI_DEC_IN_ROOT_PI ON CAMUNDA.ACT_HI_DEC_IN(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_DEC_IN_RM_TIME ON CAMUNDA.ACT_HI_DEC_IN(REMOVAL_TIME_);

create index ACT_IDX_HI_DEC_OUT_INST ON CAMUNDA.ACT_HI_DEC_OUT(DEC_INST_ID_);
create index ACT_IDX_HI_DEC_OUT_RULE ON CAMUNDA.ACT_HI_DEC_OUT(RULE_ORDER_, CLAUSE_ID_);
create index ACT_IDX_HI_DEC_OUT_ROOT_PI ON CAMUNDA.ACT_HI_DEC_OUT(ROOT_PROC_INST_ID_);
create index ACT_IDX_HI_DEC_OUT_RM_TIME ON CAMUNDA.ACT_HI_DEC_OUT(REMOVAL_TIME_);