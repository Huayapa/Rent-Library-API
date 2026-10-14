--
-- PostgreSQL database dump
--

\restrict m3fR3uQ1ZtSMmF9sdKtkmhabxTvtGsYy9ICzm0qCpvC6uJTpjnhLQcMBa3XTfQ5

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

-- Started on 2026-10-11 20:29:22

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 3 (class 3079 OID 26379)
-- Name: pgcrypto; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS pgcrypto WITH SCHEMA public;


--
-- TOC entry 5194 (class 0 OID 0)
-- Dependencies: 3
-- Name: EXTENSION pgcrypto; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION pgcrypto IS 'cryptographic functions';


--
-- TOC entry 2 (class 3079 OID 26372)
-- Name: unaccent; Type: EXTENSION; Schema: -; Owner: -
--

CREATE EXTENSION IF NOT EXISTS unaccent WITH SCHEMA public;


--
-- TOC entry 5195 (class 0 OID 0)
-- Dependencies: 2
-- Name: EXTENSION unaccent; Type: COMMENT; Schema: -; Owner: -
--

COMMENT ON EXTENSION unaccent IS 'text search dictionary that removes accents';


--
-- TOC entry 283 (class 1255 OID 26617)
-- Name: set_updated_at(); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.set_updated_at() RETURNS trigger
    LANGUAGE plpgsql
    AS $$
BEGIN
	NEW.updated_at = now();
	RETURN NEW;
END
$$;


--
-- TOC entry 282 (class 1255 OID 26417)
-- Name: unaccent_immutable(text); Type: FUNCTION; Schema: public; Owner: -
--

CREATE FUNCTION public.unaccent_immutable(text) RETURNS text
    LANGUAGE sql IMMUTABLE PARALLEL SAFE
    AS $_$
    SELECT public.unaccent('public.unaccent'::regdictionary, $1)
$_$;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 222 (class 1259 OID 26419)
-- Name: authors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.authors (
    id bigint NOT NULL,
    name character varying(100) DEFAULT 'Desconocido'::character varying NOT NULL,
    bio text
);


--
-- TOC entry 221 (class 1259 OID 26418)
-- Name: authors_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.authors_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5196 (class 0 OID 0)
-- Dependencies: 221
-- Name: authors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.authors_id_seq OWNED BY public.authors.id;


--
-- TOC entry 239 (class 1259 OID 26600)
-- Name: book_categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.book_categories (
    book_id bigint NOT NULL,
    category_id bigint NOT NULL
);


--
-- TOC entry 228 (class 1259 OID 26453)
-- Name: books; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.books (
    id bigint NOT NULL,
    isbn character varying(13) NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    release_data date,
    is_published boolean DEFAULT false NOT NULL,
    author_id bigint,
    publisher_id bigint,
    scheduled_publish_at timestamp with time zone,
    created_at timestamp with time zone DEFAULT now() NOT NULL,
    updated_at timestamp with time zone DEFAULT now() NOT NULL,
    deleted_at timestamp with time zone
);


--
-- TOC entry 227 (class 1259 OID 26452)
-- Name: books_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.books_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5197 (class 0 OID 0)
-- Dependencies: 227
-- Name: books_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.books_id_seq OWNED BY public.books.id;


--
-- TOC entry 226 (class 1259 OID 26442)
-- Name: categories; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.categories (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


--
-- TOC entry 225 (class 1259 OID 26441)
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5198 (class 0 OID 0)
-- Dependencies: 225
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;


--
-- TOC entry 230 (class 1259 OID 26483)
-- Name: copies; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.copies (
    id bigint NOT NULL,
    inventory_code character varying(50) NOT NULL,
    book_id bigint NOT NULL,
    status character varying(20) DEFAULT 'AVAILABLE'::character varying NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT chk_copy_status CHECK (((status)::text = ANY ((ARRAY['AVAILABLE'::character varying, 'LOANED'::character varying, 'RESERVED'::character varying, 'DAMAGED'::character varying, 'LOST'::character varying])::text[])))
);


--
-- TOC entry 229 (class 1259 OID 26482)
-- Name: copies_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.copies_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5199 (class 0 OID 0)
-- Dependencies: 229
-- Name: copies_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.copies_id_seq OWNED BY public.copies.id;


--
-- TOC entry 236 (class 1259 OID 26550)
-- Name: loans; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.loans (
    id bigint NOT NULL,
    copy_id bigint NOT NULL,
    user_id bigint NOT NULL,
    loaned_at timestamp with time zone DEFAULT now() NOT NULL,
    due_at timestamp with time zone NOT NULL,
    returned_at timestamp with time zone,
    status character varying(20) DEFAULT 'ACTIVE'::character varying NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT chk_loan_status CHECK (((status)::text = ANY ((ARRAY['ACTIVE'::character varying, 'RETURNED'::character varying, 'OVERDUE'::character varying])::text[])))
);


--
-- TOC entry 235 (class 1259 OID 26549)
-- Name: loans_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.loans_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5200 (class 0 OID 0)
-- Dependencies: 235
-- Name: loans_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.loans_id_seq OWNED BY public.loans.id;


--
-- TOC entry 224 (class 1259 OID 26431)
-- Name: publishers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.publishers (
    id bigint NOT NULL,
    name character varying(255) NOT NULL
);


--
-- TOC entry 223 (class 1259 OID 26430)
-- Name: publishers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.publishers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5201 (class 0 OID 0)
-- Dependencies: 223
-- Name: publishers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.publishers_id_seq OWNED BY public.publishers.id;


--
-- TOC entry 238 (class 1259 OID 26576)
-- Name: reservations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.reservations (
    id bigint NOT NULL,
    copy_id bigint NOT NULL,
    user_id bigint NOT NULL,
    reserved_at timestamp with time zone DEFAULT now() NOT NULL,
    expires_at timestamp with time zone,
    status character varying(20) DEFAULT 'PENDING'::character varying NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT chk_reservation_status CHECK (((status)::text = ANY ((ARRAY['PENDING'::character varying, 'FULFILLED'::character varying, 'CANCELLED'::character varying, 'EXPIRED'::character varying])::text[])))
);


--
-- TOC entry 237 (class 1259 OID 26575)
-- Name: reservations_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.reservations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5202 (class 0 OID 0)
-- Dependencies: 237
-- Name: reservations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.reservations_id_seq OWNED BY public.reservations.id;


--
-- TOC entry 234 (class 1259 OID 26524)
-- Name: tokens; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.tokens (
    id bigint NOT NULL,
    token_id character varying(36) NOT NULL,
    token character varying(255) NOT NULL,
    token_type character varying(50) DEFAULT 'BEARER'::character varying NOT NULL,
    expiry_date timestamp with time zone NOT NULL,
    issued_at timestamp with time zone DEFAULT now(),
    is_revoked boolean DEFAULT false,
    device_info character varying(255),
    user_id bigint NOT NULL,
    CONSTRAINT chk_token_type CHECK (((token_type)::text = 'BEARER'::text))
);


--
-- TOC entry 233 (class 1259 OID 26523)
-- Name: tokens_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tokens_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5203 (class 0 OID 0)
-- Dependencies: 233
-- Name: tokens_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.tokens_id_seq OWNED BY public.tokens.id;


--
-- TOC entry 240 (class 1259 OID 26640)
-- Name: tokens_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.tokens_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 232 (class 1259 OID 26503)
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE public.users (
    id bigint NOT NULL,
    dni character(8) NOT NULL,
    name character varying(120) NOT NULL,
    email character varying(255) NOT NULL,
    password_hash character varying(255) NOT NULL,
    role character varying(20) DEFAULT 'MEMBER'::character varying NOT NULL,
    deleted_at timestamp with time zone,
    CONSTRAINT chk_user_role CHECK (((role)::text = ANY ((ARRAY['MEMBER'::character varying, 'ADMIN'::character varying, 'LIBRARIAN'::character varying])::text[])))
);


--
-- TOC entry 231 (class 1259 OID 26502)
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- TOC entry 5204 (class 0 OID 0)
-- Dependencies: 231
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- TOC entry 4948 (class 2604 OID 26422)
-- Name: authors id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.authors ALTER COLUMN id SET DEFAULT nextval('public.authors_id_seq'::regclass);


--
-- TOC entry 4952 (class 2604 OID 26456)
-- Name: books id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books ALTER COLUMN id SET DEFAULT nextval('public.books_id_seq'::regclass);


--
-- TOC entry 4951 (class 2604 OID 26445)
-- Name: categories id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- TOC entry 4956 (class 2604 OID 26486)
-- Name: copies id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies ALTER COLUMN id SET DEFAULT nextval('public.copies_id_seq'::regclass);


--
-- TOC entry 4964 (class 2604 OID 26553)
-- Name: loans id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans ALTER COLUMN id SET DEFAULT nextval('public.loans_id_seq'::regclass);


--
-- TOC entry 4950 (class 2604 OID 26434)
-- Name: publishers id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publishers ALTER COLUMN id SET DEFAULT nextval('public.publishers_id_seq'::regclass);


--
-- TOC entry 4967 (class 2604 OID 26579)
-- Name: reservations id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reservations ALTER COLUMN id SET DEFAULT nextval('public.reservations_id_seq'::regclass);


--
-- TOC entry 4960 (class 2604 OID 26527)
-- Name: tokens id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens ALTER COLUMN id SET DEFAULT nextval('public.tokens_id_seq'::regclass);


--
-- TOC entry 4958 (class 2604 OID 26506)
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- TOC entry 5170 (class 0 OID 26419)
-- Dependencies: 222
-- Data for Name: authors; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.authors (id, name, bio) FROM stdin;
\.


--
-- TOC entry 5187 (class 0 OID 26600)
-- Dependencies: 239
-- Data for Name: book_categories; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.book_categories (book_id, category_id) FROM stdin;
\.


--
-- TOC entry 5176 (class 0 OID 26453)
-- Dependencies: 228
-- Data for Name: books; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.books (id, isbn, title, description, release_data, is_published, author_id, publisher_id, scheduled_publish_at, created_at, updated_at, deleted_at) FROM stdin;
\.


--
-- TOC entry 5174 (class 0 OID 26442)
-- Dependencies: 226
-- Data for Name: categories; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.categories (id, name) FROM stdin;
\.


--
-- TOC entry 5178 (class 0 OID 26483)
-- Dependencies: 230
-- Data for Name: copies; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.copies (id, inventory_code, book_id, status, deleted_at) FROM stdin;
\.


--
-- TOC entry 5184 (class 0 OID 26550)
-- Dependencies: 236
-- Data for Name: loans; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.loans (id, copy_id, user_id, loaned_at, due_at, returned_at, status, deleted_at) FROM stdin;
\.


--
-- TOC entry 5172 (class 0 OID 26431)
-- Dependencies: 224
-- Data for Name: publishers; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.publishers (id, name) FROM stdin;
\.


--
-- TOC entry 5186 (class 0 OID 26576)
-- Dependencies: 238
-- Data for Name: reservations; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.reservations (id, copy_id, user_id, reserved_at, expires_at, status, deleted_at) FROM stdin;
\.


--
-- TOC entry 5182 (class 0 OID 26524)
-- Dependencies: 234
-- Data for Name: tokens; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.tokens (id, token_id, token, token_type, expiry_date, issued_at, is_revoked, device_info, user_id) FROM stdin;
1	bc234a7a-40fe-4a73-9bf4-f61289d693fc	Posg3BSwQzpMSja6PGDZKcFsOhjMfiS7Xxk1sYb+EwA=	BEARER	2026-10-18 20:21:10-05	2026-10-11 20:21:10.162327-05	f	insomnia/13.3.1	1
\.


--
-- TOC entry 5180 (class 0 OID 26503)
-- Dependencies: 232
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

COPY public.users (id, dni, name, email, password_hash, role, deleted_at) FROM stdin;
1	88392832	admin	admin@gmail.com	$2a$10$GGLllkwoYY8FxzlTYLILGugnCD4QHu4tIs0ynLlXqExYxgwUU2r4S	ADMIN	\N
\.


--
-- TOC entry 5205 (class 0 OID 0)
-- Dependencies: 221
-- Name: authors_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.authors_id_seq', 1, false);


--
-- TOC entry 5206 (class 0 OID 0)
-- Dependencies: 227
-- Name: books_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.books_id_seq', 1, false);


--
-- TOC entry 5207 (class 0 OID 0)
-- Dependencies: 225
-- Name: categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.categories_id_seq', 1, false);


--
-- TOC entry 5208 (class 0 OID 0)
-- Dependencies: 229
-- Name: copies_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.copies_id_seq', 1, false);


--
-- TOC entry 5209 (class 0 OID 0)
-- Dependencies: 235
-- Name: loans_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.loans_id_seq', 1, false);


--
-- TOC entry 5210 (class 0 OID 0)
-- Dependencies: 223
-- Name: publishers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.publishers_id_seq', 1, false);


--
-- TOC entry 5211 (class 0 OID 0)
-- Dependencies: 237
-- Name: reservations_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.reservations_id_seq', 1, false);


--
-- TOC entry 5212 (class 0 OID 0)
-- Dependencies: 233
-- Name: tokens_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tokens_id_seq', 1, false);


--
-- TOC entry 5213 (class 0 OID 0)
-- Dependencies: 240
-- Name: tokens_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.tokens_seq', 1, true);


--
-- TOC entry 5214 (class 0 OID 0)
-- Dependencies: 231
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.users_id_seq', 1, true);


--
-- TOC entry 4976 (class 2606 OID 26429)
-- Name: authors authors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.authors
    ADD CONSTRAINT authors_pkey PRIMARY KEY (id);


--
-- TOC entry 5010 (class 2606 OID 26606)
-- Name: book_categories book_categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_categories
    ADD CONSTRAINT book_categories_pkey PRIMARY KEY (book_id, category_id);


--
-- TOC entry 4987 (class 2606 OID 26471)
-- Name: books books_isbn_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_isbn_key UNIQUE (isbn);


--
-- TOC entry 4989 (class 2606 OID 26469)
-- Name: books books_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_pkey PRIMARY KEY (id);


--
-- TOC entry 4982 (class 2606 OID 26451)
-- Name: categories categories_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_name_key UNIQUE (name);


--
-- TOC entry 4984 (class 2606 OID 26449)
-- Name: categories categories_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- TOC entry 4991 (class 2606 OID 26496)
-- Name: copies copies_inventory_code_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies
    ADD CONSTRAINT copies_inventory_code_key UNIQUE (inventory_code);


--
-- TOC entry 4993 (class 2606 OID 26494)
-- Name: copies copies_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies
    ADD CONSTRAINT copies_pkey PRIMARY KEY (id);


--
-- TOC entry 5006 (class 2606 OID 26564)
-- Name: loans loans_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT loans_pkey PRIMARY KEY (id);


--
-- TOC entry 4978 (class 2606 OID 26440)
-- Name: publishers publishers_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publishers
    ADD CONSTRAINT publishers_name_key UNIQUE (name);


--
-- TOC entry 4980 (class 2606 OID 26438)
-- Name: publishers publishers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.publishers
    ADD CONSTRAINT publishers_pkey PRIMARY KEY (id);


--
-- TOC entry 5008 (class 2606 OID 26589)
-- Name: reservations reservations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reservations
    ADD CONSTRAINT reservations_pkey PRIMARY KEY (id);


--
-- TOC entry 5002 (class 2606 OID 26541)
-- Name: tokens tokens_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_pkey PRIMARY KEY (id);


--
-- TOC entry 5004 (class 2606 OID 26543)
-- Name: tokens tokens_token_id_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_token_id_key UNIQUE (token_id);


--
-- TOC entry 4996 (class 2606 OID 26638)
-- Name: users users_dni_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_dni_key UNIQUE (dni);


--
-- TOC entry 4998 (class 2606 OID 26522)
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- TOC entry 5000 (class 2606 OID 26518)
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- TOC entry 4994 (class 1259 OID 26619)
-- Name: idx_copies_book_status; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX idx_copies_book_status ON public.copies USING btree (book_id, status);


--
-- TOC entry 4985 (class 1259 OID 26620)
-- Name: ux_category_normalize; Type: INDEX; Schema: public; Owner: -
--

CREATE UNIQUE INDEX ux_category_normalize ON public.categories USING btree (public.unaccent_immutable(lower((name)::text)));


--
-- TOC entry 5021 (class 2620 OID 26618)
-- Name: books trg_books_updated_at; Type: TRIGGER; Schema: public; Owner: -
--

CREATE TRIGGER trg_books_updated_at BEFORE UPDATE ON public.books FOR EACH ROW EXECUTE FUNCTION public.set_updated_at();


--
-- TOC entry 5019 (class 2606 OID 26607)
-- Name: book_categories book_categories_book_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_categories
    ADD CONSTRAINT book_categories_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE CASCADE;


--
-- TOC entry 5020 (class 2606 OID 26612)
-- Name: book_categories book_categories_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.book_categories
    ADD CONSTRAINT book_categories_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id) ON DELETE CASCADE;


--
-- TOC entry 5011 (class 2606 OID 26472)
-- Name: books books_author_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_author_id_fkey FOREIGN KEY (author_id) REFERENCES public.authors(id) ON DELETE SET NULL;


--
-- TOC entry 5012 (class 2606 OID 26477)
-- Name: books books_publisher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.books
    ADD CONSTRAINT books_publisher_id_fkey FOREIGN KEY (publisher_id) REFERENCES public.publishers(id) ON DELETE SET NULL;


--
-- TOC entry 5013 (class 2606 OID 26497)
-- Name: copies copies_book_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.copies
    ADD CONSTRAINT copies_book_id_fkey FOREIGN KEY (book_id) REFERENCES public.books(id) ON DELETE RESTRICT;


--
-- TOC entry 5015 (class 2606 OID 26565)
-- Name: loans loans_copy_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT loans_copy_id_fkey FOREIGN KEY (copy_id) REFERENCES public.copies(id) ON DELETE RESTRICT;


--
-- TOC entry 5016 (class 2606 OID 26570)
-- Name: loans loans_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT loans_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE RESTRICT;


--
-- TOC entry 5017 (class 2606 OID 26590)
-- Name: reservations reservations_copy_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reservations
    ADD CONSTRAINT reservations_copy_id_fkey FOREIGN KEY (copy_id) REFERENCES public.copies(id) ON DELETE RESTRICT;


--
-- TOC entry 5018 (class 2606 OID 26595)
-- Name: reservations reservations_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reservations
    ADD CONSTRAINT reservations_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE RESTRICT;


--
-- TOC entry 5014 (class 2606 OID 26544)
-- Name: tokens tokens_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.tokens
    ADD CONSTRAINT tokens_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


-- Completed on 2026-10-11 20:29:22

--
-- PostgreSQL database dump complete
--

\unrestrict m3fR3uQ1ZtSMmF9sdKtkmhabxTvtGsYy9ICzm0qCpvC6uJTpjnhLQcMBa3XTfQ5

