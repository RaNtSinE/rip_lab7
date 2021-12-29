-- Table: public.task

-- DROP TABLE public.task;

CREATE TABLE IF NOT EXISTS public.task
(
    id bigint NOT NULL,
    "isComplete" boolean,
    text character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    is_complete boolean,
    CONSTRAINT task_pkey PRIMARY KEY (id)
    )

    TABLESPACE pg_default;

ALTER TABLE public.task
    OWNER to postgres;