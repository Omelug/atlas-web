--
-- PostgreSQL database dump
--

-- Dumped from database version 15.4 (Debian 15.4-3)
-- Dumped by pg_dump version 16.0 (Debian 16.0-2)

-- Started on 2023-10-11 11:12:51 CEST

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 5 (class 2615 OID 31019)
-- Name: config; Type: SCHEMA; Schema: -; Owner: atlas
--

CREATE SCHEMA config;


ALTER SCHEMA config OWNER TO atlas;

--
-- TOC entry 6 (class 2615 OID 36042)
-- Name: schemaname; Type: SCHEMA; Schema: -; Owner: atlas
--

CREATE SCHEMA schemaname;


ALTER SCHEMA schemaname OWNER TO atlas;

--
-- TOC entry 7 (class 2615 OID 36437)
-- Name: test6; Type: SCHEMA; Schema: -; Owner: atlas
--

CREATE SCHEMA test6;


ALTER SCHEMA test6 OWNER TO atlas;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 217 (class 1259 OID 31116)
-- Name: database; Type: TABLE; Schema: config; Owner: atlas
--

CREATE TABLE config.database (
    id bigint NOT NULL,
    database_access integer,
    name character varying(255),
    databaseaccess integer
);


ALTER TABLE config.database OWNER TO atlas;

--
-- TOC entry 216 (class 1259 OID 31115)
-- Name: database_id_seq; Type: SEQUENCE; Schema: config; Owner: atlas
--

CREATE SEQUENCE config.database_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE config.database_id_seq OWNER TO atlas;

--
-- TOC entry 3459 (class 0 OID 0)
-- Dependencies: 216
-- Name: database_id_seq; Type: SEQUENCE OWNED BY; Schema: config; Owner: atlas
--

ALTER SEQUENCE config.database_id_seq OWNED BY config.database.id;


--
-- TOC entry 218 (class 1259 OID 31122)
-- Name: finds; Type: TABLE; Schema: config; Owner: atlas
--

CREATE TABLE config.finds (
    id bigint NOT NULL,
    author character varying(250),
    color character varying(250),
    name character varying(250),
    name2 character varying(250),
    open boolean NOT NULL,
    parent_group character varying(255),
    text character varying(2500),
    typ integer,
    user_id bigint,
    parentgroup character varying(255)
);


ALTER TABLE config.finds OWNER TO atlas;

--
-- TOC entry 220 (class 1259 OID 31130)
-- Name: log; Type: TABLE; Schema: config; Owner: atlas
--

CREATE TABLE config.log (
    id bigint NOT NULL,
    message character varying(255),
    "time" timestamp without time zone,
    title character varying(255),
    typ integer
);


ALTER TABLE config.log OWNER TO atlas;

--
-- TOC entry 219 (class 1259 OID 31129)
-- Name: log_id_seq; Type: SEQUENCE; Schema: config; Owner: atlas
--

CREATE SEQUENCE config.log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE config.log_id_seq OWNER TO atlas;

--
-- TOC entry 3460 (class 0 OID 0)
-- Dependencies: 219
-- Name: log_id_seq; Type: SEQUENCE OWNED BY; Schema: config; Owner: atlas
--

ALTER SEQUENCE config.log_id_seq OWNED BY config.log.id;


--
-- TOC entry 222 (class 1259 OID 31139)
-- Name: role; Type: TABLE; Schema: config; Owner: atlas
--

CREATE TABLE config.role (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE config.role OWNER TO atlas;

--
-- TOC entry 221 (class 1259 OID 31138)
-- Name: role_id_seq; Type: SEQUENCE; Schema: config; Owner: atlas
--

CREATE SEQUENCE config.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE config.role_id_seq OWNER TO atlas;

--
-- TOC entry 3461 (class 0 OID 0)
-- Dependencies: 221
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: config; Owner: atlas
--

ALTER SEQUENCE config.role_id_seq OWNED BY config.role.id;


--
-- TOC entry 224 (class 1259 OID 31146)
-- Name: udrlink; Type: TABLE; Schema: config; Owner: atlas
--

CREATE TABLE config.udrlink (
    id bigint NOT NULL,
    database_id bigint,
    role_id bigint,
    user_id bigint
);


ALTER TABLE config.udrlink OWNER TO atlas;

--
-- TOC entry 223 (class 1259 OID 31145)
-- Name: udrlink_id_seq; Type: SEQUENCE; Schema: config; Owner: atlas
--

CREATE SEQUENCE config.udrlink_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE config.udrlink_id_seq OWNER TO atlas;

--
-- TOC entry 3462 (class 0 OID 0)
-- Dependencies: 223
-- Name: udrlink_id_seq; Type: SEQUENCE OWNED BY; Schema: config; Owner: atlas
--

ALTER SEQUENCE config.udrlink_id_seq OWNED BY config.udrlink.id;


--
-- TOC entry 226 (class 1259 OID 31153)
-- Name: user; Type: TABLE; Schema: config; Owner: atlas
--

CREATE TABLE config."user" (
    id bigint NOT NULL,
    active boolean NOT NULL,
    currentdb_name character varying(255),
    first_login timestamp without time zone,
    last_login timestamp without time zone,
    modify_date timestamp without time zone,
    name character varying(250),
    password character varying(250),
    firstlogin timestamp without time zone,
    lastlogin timestamp without time zone,
    modifydate timestamp without time zone
);


ALTER TABLE config."user" OWNER TO atlas;

--
-- TOC entry 225 (class 1259 OID 31152)
-- Name: user_id_seq; Type: SEQUENCE; Schema: config; Owner: atlas
--

CREATE SEQUENCE config.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE config.user_id_seq OWNER TO atlas;

--
-- TOC entry 3463 (class 0 OID 0)
-- Dependencies: 225
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: config; Owner: atlas
--

ALTER SEQUENCE config.user_id_seq OWNED BY config."user".id;


--
-- TOC entry 229 (class 1259 OID 36440)
-- Name: color; Type: TABLE; Schema: test6; Owner: atlas
--

CREATE TABLE test6.color (
    id bigint NOT NULL,
    name character varying(255)
);


ALTER TABLE test6.color OWNER TO atlas;

--
-- TOC entry 228 (class 1259 OID 36439)
-- Name: color_id_seq; Type: SEQUENCE; Schema: test6; Owner: atlas
--

CREATE SEQUENCE test6.color_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE test6.color_id_seq OWNER TO atlas;

--
-- TOC entry 3464 (class 0 OID 0)
-- Dependencies: 228
-- Name: color_id_seq; Type: SEQUENCE OWNED BY; Schema: test6; Owner: atlas
--

ALTER SEQUENCE test6.color_id_seq OWNED BY test6.color.id;


--
-- TOC entry 230 (class 1259 OID 36446)
-- Name: color_item; Type: TABLE; Schema: test6; Owner: atlas
--

CREATE TABLE test6.color_item (
    item_id bigint NOT NULL,
    color_id bigint NOT NULL
);


ALTER TABLE test6.color_item OWNER TO atlas;

--
-- TOC entry 231 (class 1259 OID 36451)
-- Name: image; Type: TABLE; Schema: test6; Owner: atlas
--

CREATE TABLE test6.image (
    id bigint NOT NULL,
    file character varying(255),
    createdate timestamp without time zone,
    modifydate timestamp without time zone,
    item_id bigint
);


ALTER TABLE test6.image OWNER TO atlas;

--
-- TOC entry 233 (class 1259 OID 36457)
-- Name: imagerequest; Type: TABLE; Schema: test6; Owner: atlas
--

CREATE TABLE test6.imagerequest (
    id bigint NOT NULL,
    file character varying(255),
    createdate timestamp without time zone,
    modifydate timestamp without time zone,
    request_id bigint
);


ALTER TABLE test6.imagerequest OWNER TO atlas;

--
-- TOC entry 232 (class 1259 OID 36456)
-- Name: imagerequest_id_seq; Type: SEQUENCE; Schema: test6; Owner: atlas
--

CREATE SEQUENCE test6.imagerequest_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE test6.imagerequest_id_seq OWNER TO atlas;

--
-- TOC entry 3465 (class 0 OID 0)
-- Dependencies: 232
-- Name: imagerequest_id_seq; Type: SEQUENCE OWNED BY; Schema: test6; Owner: atlas
--

ALTER SEQUENCE test6.imagerequest_id_seq OWNED BY test6.imagerequest.id;


--
-- TOC entry 234 (class 1259 OID 36463)
-- Name: item; Type: TABLE; Schema: test6; Owner: atlas
--

CREATE TABLE test6.item (
    id bigint NOT NULL,
    author character varying(250),
    createdate timestamp without time zone,
    modifydate timestamp without time zone,
    name character varying(250) NOT NULL,
    name2 character varying(250),
    text character varying(2500),
    typ integer NOT NULL,
    parent_group_id bigint
);


ALTER TABLE test6.item OWNER TO atlas;

--
-- TOC entry 227 (class 1259 OID 36438)
-- Name: native; Type: SEQUENCE; Schema: test6; Owner: atlas
--

CREATE SEQUENCE test6.native
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE test6.native OWNER TO atlas;

--
-- TOC entry 235 (class 1259 OID 36470)
-- Name: request; Type: TABLE; Schema: test6; Owner: atlas
--

CREATE TABLE test6.request (
    id bigint NOT NULL,
    createdate timestamp without time zone,
    link bigint,
    local_visibility boolean NOT NULL,
    modifydate timestamp without time zone,
    name character varying(255),
    parent_request_mark character varying(255),
    request_mark character varying(255),
    request_message character varying(255),
    request_status integer,
    request_typ integer
);


ALTER TABLE test6.request OWNER TO atlas;

--
-- TOC entry 3252 (class 2604 OID 31119)
-- Name: database id; Type: DEFAULT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.database ALTER COLUMN id SET DEFAULT nextval('config.database_id_seq'::regclass);


--
-- TOC entry 3253 (class 2604 OID 31133)
-- Name: log id; Type: DEFAULT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.log ALTER COLUMN id SET DEFAULT nextval('config.log_id_seq'::regclass);


--
-- TOC entry 3254 (class 2604 OID 31142)
-- Name: role id; Type: DEFAULT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.role ALTER COLUMN id SET DEFAULT nextval('config.role_id_seq'::regclass);


--
-- TOC entry 3255 (class 2604 OID 31149)
-- Name: udrlink id; Type: DEFAULT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.udrlink ALTER COLUMN id SET DEFAULT nextval('config.udrlink_id_seq'::regclass);


--
-- TOC entry 3256 (class 2604 OID 31156)
-- Name: user id; Type: DEFAULT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config."user" ALTER COLUMN id SET DEFAULT nextval('config.user_id_seq'::regclass);


--
-- TOC entry 3257 (class 2604 OID 36443)
-- Name: color id; Type: DEFAULT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.color ALTER COLUMN id SET DEFAULT nextval('test6.color_id_seq'::regclass);


--
-- TOC entry 3258 (class 2604 OID 36460)
-- Name: imagerequest id; Type: DEFAULT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.imagerequest ALTER COLUMN id SET DEFAULT nextval('test6.imagerequest_id_seq'::regclass);


--
-- TOC entry 3435 (class 0 OID 31116)
-- Dependencies: 217
-- Data for Name: database; Type: TABLE DATA; Schema: config; Owner: atlas
--

COPY config.database (id, database_access, name, databaseaccess) FROM stdin;
73	0	test6	\N
\.


--
-- TOC entry 3436 (class 0 OID 31122)
-- Dependencies: 218
-- Data for Name: finds; Type: TABLE DATA; Schema: config; Owner: atlas
--

COPY config.finds (id, author, color, name, name2, open, parent_group, text, typ, user_id, parentgroup) FROM stdin;
\.


--
-- TOC entry 3438 (class 0 OID 31130)
-- Dependencies: 220
-- Data for Name: log; Type: TABLE DATA; Schema: config; Owner: atlas
--

COPY config.log (id, message, "time", title, typ) FROM stdin;
1	Database test2 already exists	2023-09-01 09:00:00.347	adminTools	2
2	Database nullis the last database on server, cant be deleted 	2023-09-01 09:21:41.844	admin	2
3	Database nullis the last database on server, cant be deleted 	2023-09-01 09:22:05.633	admin	2
4	Database nullis the last database on server, cant be deleted 	2023-09-01 09:22:06.424	admin	2
5	Database nullis the last database on server, cant be deleted 	2023-09-01 09:23:24.193	admin	2
6	Database nullis the last database on server, cant be deleted 	2023-09-01 09:23:24.742	admin	2
7	Database nullis the last database on server, cant be deleted 	2023-09-01 09:23:25.291	admin	2
8	Database nullis the last database on server, cant be deleted 	2023-09-01 09:23:25.749	admin	2
9	Database nullis the last database on server, cant be deleted 	2023-09-01 09:25:34.47	admin	2
10	Database nullis the last database on server, cant be deleted 	2023-09-01 09:25:34.968	admin	2
11	Database nullis the last database on server, cant be deleted 	2023-09-01 09:25:35.475	admin	2
12	Database nullis the last database on server, cant be deleted 	2023-09-01 09:25:36.069	admin	2
13	Database nullis the last database on server, cant be deleted 	2023-09-01 09:25:36.507	admin	2
14	Database nullis the last database on server, cant be deleted 	2023-09-01 21:06:18.954	admin	2
15	Database awdawd already exists	2023-09-01 21:13:12.955	adminTools	2
16	Database nullis the last database on server, cant be deleted 	2023-09-12 09:28:35.4	admin	2
17	Database nullis the last database on server, cant be deleted 	2023-09-12 09:34:17.115	admin	2
18	Database nullis the last database on server, cant be deleted 	2023-09-12 09:41:11.286	admin	2
19	Database nullis the last database on server, cant be deleted 	2023-09-12 09:43:36.153	admin	2
20	Database nullis the last database on server, cant be deleted 	2023-09-12 09:44:53.234	admin	2
21	Database nullis the last database on server, cant be deleted 	2023-09-12 09:45:28.666	admin	2
22	Database nullis the last database on server, cant be deleted 	2023-09-12 09:45:30.13	admin	2
23	Database nullis the last database on server, cant be deleted 	2023-09-12 09:45:30.894	admin	2
24	Database nullis the last database on server, cant be deleted 	2023-09-12 09:45:31.607	admin	2
25	Database nullis the last database on server, cant be deleted 	2023-09-12 09:46:58.661	admin	2
26	Database nullis the last database on server, cant be deleted 	2023-09-12 09:49:56.057	admin	2
27	Database nullis the last database on server, cant be deleted 	2023-09-12 09:51:13.657	admin	2
28	Database nullis the last database on server, cant be deleted 	2023-09-12 09:51:23.467	admin	2
29	Database nullis the last database on server, cant be deleted 	2023-09-12 09:51:24.189	admin	2
30	Database nullis the last database on server, cant be deleted 	2023-09-12 09:51:25.153	admin	2
31	Database nullis the last database on server, cant be deleted 	2023-09-12 09:51:26.236	admin	2
32	Database nullis the last database on server, cant be deleted 	2023-09-27 10:25:34.499	admin	2
33	Error:ERROR: syntax error at or near "$1"\n  Position: 29	2023-09-27 10:25:43.539	Creating schema	2
34	Error:ERROR: syntax error at or near "$1"\n  Position: 29	2023-09-27 10:26:28.293	Creating schema	2
35	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:23.462	admin	2
36	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:23.608	admin	2
37	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:23.786	admin	2
38	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:23.951	admin	2
39	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:24.425	admin	2
40	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:24.651	admin	2
41	Database nullis the last database on server, cant be deleted 	2023-09-27 10:55:24.819	admin	2
42	Folder for  test2 created	2023-10-10 16:15:57.253	databaseCreation	0
43	Folder for  test3 created	2023-10-10 16:22:26.737	databaseCreation	0
44	Folder for  test4 created	2023-10-10 16:48:22.645	databaseCreation	0
45	Folder for  test5 created	2023-10-10 16:57:15.959	databaseCreation	0
46	Database nullis the last database on server, cant be deleted 	2023-10-10 16:57:18.612	admin	2
47	Database nullis the last database on server, cant be deleted 	2023-10-10 16:57:19.098	admin	2
48	Folder for  test6 created	2023-10-10 17:03:04.891	databaseCreation	0
49	Database nullis the last database on server, cant be deleted 	2023-10-10 17:03:08.234	admin	2
50	Folder for  aadada created	2023-10-11 10:39:23.635	databaseCreation	0
51	Database aadada already exists	2023-10-11 10:39:25.772	adminTools	2
\.


--
-- TOC entry 3440 (class 0 OID 31139)
-- Dependencies: 222
-- Data for Name: role; Type: TABLE DATA; Schema: config; Owner: atlas
--

COPY config.role (id, name) FROM stdin;
1	ADMIN
2	EDITOR
3	USER
4	ADMIN
5	EDITOR
6	USER
\.


--
-- TOC entry 3442 (class 0 OID 31146)
-- Dependencies: 224
-- Data for Name: udrlink; Type: TABLE DATA; Schema: config; Owner: atlas
--

COPY config.udrlink (id, database_id, role_id, user_id) FROM stdin;
2	\N	4	10
\.


--
-- TOC entry 3444 (class 0 OID 31153)
-- Dependencies: 226
-- Data for Name: user; Type: TABLE DATA; Schema: config; Owner: atlas
--

COPY config."user" (id, active, currentdb_name, first_login, last_login, modify_date, name, password, firstlogin, lastlogin, modifydate) FROM stdin;
6	t	\N	2023-08-28 09:39:25.297	2023-08-28 09:51:54.461	2023-08-28 09:51:54.495	user	$2a$10$z.541OiAk77dxbpob19Fc.9AX0a7ELEZZo4Rrjan0C7vPxjLDE4tq	\N	\N	\N
10	t	aadada	2023-09-25 08:57:41.428	2023-10-11 09:35:42.944	\N	admin	$2a$10$X3iBZZQgGfvxx4olu6YwCebHTBiV9iqcEAN3Anb4VbljJZV3oGhPa	\N	\N	2023-10-11 11:12:29.602
\.


--
-- TOC entry 3447 (class 0 OID 36440)
-- Dependencies: 229
-- Data for Name: color; Type: TABLE DATA; Schema: test6; Owner: atlas
--

COPY test6.color (id, name) FROM stdin;
1	black
2	white
3	purple
4	green
5	orange
6	pink
7	yellow
8	gray
9	silver
11	lime
12	brown
\.


--
-- TOC entry 3448 (class 0 OID 36446)
-- Dependencies: 230
-- Data for Name: color_item; Type: TABLE DATA; Schema: test6; Owner: atlas
--

COPY test6.color_item (item_id, color_id) FROM stdin;
2	2
3	1
3	7
3	4
4	5
4	8
\.


--
-- TOC entry 3449 (class 0 OID 36451)
-- Dependencies: 231
-- Data for Name: image; Type: TABLE DATA; Schema: test6; Owner: atlas
--

COPY test6.image (id, file, createdate, modifydate, item_id) FROM stdin;
\.


--
-- TOC entry 3451 (class 0 OID 36457)
-- Dependencies: 233
-- Data for Name: imagerequest; Type: TABLE DATA; Schema: test6; Owner: atlas
--

COPY test6.imagerequest (id, file, createdate, modifydate, request_id) FROM stdin;
\.


--
-- TOC entry 3452 (class 0 OID 36463)
-- Dependencies: 234
-- Data for Name: item; Type: TABLE DATA; Schema: test6; Owner: atlas
--

COPY test6.item (id, author, createdate, modifydate, name, name2, text, typ, parent_group_id) FROM stdin;
1	\N	2023-10-10 17:03:15.396	2023-10-10 17:03:15.396	test6	\N	\N	0	\N
2	\N	2023-10-10 17:03:30.337	2023-10-10 17:03:30.337	awda	\N		1	1
3	\N	2023-10-10 20:41:54.696	2023-10-10 20:41:54.696	awdaw	\N		1	1
4	\N	2023-10-11 09:38:27.751	2023-10-11 09:38:27.751	kkhvkb	\N	<p>jhjvjjv</p>	1	3
\.


--
-- TOC entry 3453 (class 0 OID 36470)
-- Dependencies: 235
-- Data for Name: request; Type: TABLE DATA; Schema: test6; Owner: atlas
--

COPY test6.request (id, createdate, link, local_visibility, modifydate, name, parent_request_mark, request_mark, request_message, request_status, request_typ) FROM stdin;
\.


--
-- TOC entry 3466 (class 0 OID 0)
-- Dependencies: 216
-- Name: database_id_seq; Type: SEQUENCE SET; Schema: config; Owner: atlas
--

SELECT pg_catalog.setval('config.database_id_seq', 74, true);


--
-- TOC entry 3467 (class 0 OID 0)
-- Dependencies: 219
-- Name: log_id_seq; Type: SEQUENCE SET; Schema: config; Owner: atlas
--

SELECT pg_catalog.setval('config.log_id_seq', 51, true);


--
-- TOC entry 3468 (class 0 OID 0)
-- Dependencies: 221
-- Name: role_id_seq; Type: SEQUENCE SET; Schema: config; Owner: atlas
--

SELECT pg_catalog.setval('config.role_id_seq', 6, true);


--
-- TOC entry 3469 (class 0 OID 0)
-- Dependencies: 223
-- Name: udrlink_id_seq; Type: SEQUENCE SET; Schema: config; Owner: atlas
--

SELECT pg_catalog.setval('config.udrlink_id_seq', 2, true);


--
-- TOC entry 3470 (class 0 OID 0)
-- Dependencies: 225
-- Name: user_id_seq; Type: SEQUENCE SET; Schema: config; Owner: atlas
--

SELECT pg_catalog.setval('config.user_id_seq', 10, true);


--
-- TOC entry 3471 (class 0 OID 0)
-- Dependencies: 228
-- Name: color_id_seq; Type: SEQUENCE SET; Schema: test6; Owner: atlas
--

SELECT pg_catalog.setval('test6.color_id_seq', 12, true);


--
-- TOC entry 3472 (class 0 OID 0)
-- Dependencies: 232
-- Name: imagerequest_id_seq; Type: SEQUENCE SET; Schema: test6; Owner: atlas
--

SELECT pg_catalog.setval('test6.imagerequest_id_seq', 1, false);


--
-- TOC entry 3473 (class 0 OID 0)
-- Dependencies: 227
-- Name: native; Type: SEQUENCE SET; Schema: test6; Owner: atlas
--

SELECT pg_catalog.setval('test6.native', 4, true);


--
-- TOC entry 3260 (class 2606 OID 31121)
-- Name: database database_pkey; Type: CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.database
    ADD CONSTRAINT database_pkey PRIMARY KEY (id);


--
-- TOC entry 3262 (class 2606 OID 31128)
-- Name: finds finds_pkey; Type: CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.finds
    ADD CONSTRAINT finds_pkey PRIMARY KEY (id);


--
-- TOC entry 3264 (class 2606 OID 31137)
-- Name: log log_pkey; Type: CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.log
    ADD CONSTRAINT log_pkey PRIMARY KEY (id);


--
-- TOC entry 3266 (class 2606 OID 31144)
-- Name: role role_pkey; Type: CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- TOC entry 3268 (class 2606 OID 31151)
-- Name: udrlink udrlink_pkey; Type: CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.udrlink
    ADD CONSTRAINT udrlink_pkey PRIMARY KEY (id);


--
-- TOC entry 3270 (class 2606 OID 31160)
-- Name: user user_pkey; Type: CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- TOC entry 3274 (class 2606 OID 36450)
-- Name: color_item color_item_pkey; Type: CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.color_item
    ADD CONSTRAINT color_item_pkey PRIMARY KEY (item_id, color_id);


--
-- TOC entry 3272 (class 2606 OID 36445)
-- Name: color color_pkey; Type: CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.color
    ADD CONSTRAINT color_pkey PRIMARY KEY (id);


--
-- TOC entry 3276 (class 2606 OID 36455)
-- Name: image image_pkey; Type: CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.image
    ADD CONSTRAINT image_pkey PRIMARY KEY (id);


--
-- TOC entry 3278 (class 2606 OID 36462)
-- Name: imagerequest imagerequest_pkey; Type: CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.imagerequest
    ADD CONSTRAINT imagerequest_pkey PRIMARY KEY (id);


--
-- TOC entry 3280 (class 2606 OID 36469)
-- Name: item item_pkey; Type: CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.item
    ADD CONSTRAINT item_pkey PRIMARY KEY (id);


--
-- TOC entry 3282 (class 2606 OID 36476)
-- Name: request request_pkey; Type: CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.request
    ADD CONSTRAINT request_pkey PRIMARY KEY (id);


--
-- TOC entry 3284 (class 2606 OID 31219)
-- Name: udrlink fk8nqvvgjw7yfllb8j0wr0cka6b; Type: FK CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.udrlink
    ADD CONSTRAINT fk8nqvvgjw7yfllb8j0wr0cka6b FOREIGN KEY (user_id) REFERENCES config."user"(id);


--
-- TOC entry 3285 (class 2606 OID 31214)
-- Name: udrlink fkhbxpdkb0rxaqqg9n0o5eg4rdw; Type: FK CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.udrlink
    ADD CONSTRAINT fkhbxpdkb0rxaqqg9n0o5eg4rdw FOREIGN KEY (role_id) REFERENCES config.role(id);


--
-- TOC entry 3283 (class 2606 OID 31204)
-- Name: finds fkiqqak9rmc6v7nryxxo3fy846d; Type: FK CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.finds
    ADD CONSTRAINT fkiqqak9rmc6v7nryxxo3fy846d FOREIGN KEY (user_id) REFERENCES config."user"(id);


--
-- TOC entry 3286 (class 2606 OID 31209)
-- Name: udrlink fkln8hm88wfo2s21s2rrrq9rcm5; Type: FK CONSTRAINT; Schema: config; Owner: atlas
--

ALTER TABLE ONLY config.udrlink
    ADD CONSTRAINT fkln8hm88wfo2s21s2rrrq9rcm5 FOREIGN KEY (database_id) REFERENCES config.database(id);


--
-- TOC entry 3291 (class 2606 OID 36497)
-- Name: item fk59u490xlr5kh712mrlav698pf; Type: FK CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.item
    ADD CONSTRAINT fk59u490xlr5kh712mrlav698pf FOREIGN KEY (parent_group_id) REFERENCES test6.item(id);


--
-- TOC entry 3287 (class 2606 OID 36482)
-- Name: color_item fk9eoy3uf2r7guvjw1f38h2c4ei; Type: FK CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.color_item
    ADD CONSTRAINT fk9eoy3uf2r7guvjw1f38h2c4ei FOREIGN KEY (item_id) REFERENCES test6.item(id);


--
-- TOC entry 3288 (class 2606 OID 36477)
-- Name: color_item fkgp1pmjtik5kw34px2gx14v2o3; Type: FK CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.color_item
    ADD CONSTRAINT fkgp1pmjtik5kw34px2gx14v2o3 FOREIGN KEY (color_id) REFERENCES test6.color(id);


--
-- TOC entry 3289 (class 2606 OID 36487)
-- Name: image fkkn5p1goa9wbjdgl8065awce55; Type: FK CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.image
    ADD CONSTRAINT fkkn5p1goa9wbjdgl8065awce55 FOREIGN KEY (item_id) REFERENCES test6.item(id);


--
-- TOC entry 3290 (class 2606 OID 36492)
-- Name: imagerequest fkp41504v3t4auhdqgti5uoke3w; Type: FK CONSTRAINT; Schema: test6; Owner: atlas
--

ALTER TABLE ONLY test6.imagerequest
    ADD CONSTRAINT fkp41504v3t4auhdqgti5uoke3w FOREIGN KEY (request_id) REFERENCES test6.request(id);


-- Completed on 2023-10-11 11:12:51 CEST

--
-- PostgreSQL database dump complete
--

