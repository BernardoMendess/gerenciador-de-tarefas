Script para criar as tabelas no banco de dados "tarefas" e "tarefas_test"

CREATE TABLE IF NOT EXISTS departamento (
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    nome character varying(200) COLLATE pg_catalog."default",
    CONSTRAINT departamento_pkey PRIMARY KEY (id)
);

INSERT INTO departamento (nome) VALUES
('Recursos Humanos'),
('Tecnologia da Informação'),
('Marketing'),
('Finanças'),
('Operações'),
('Vendas'),
('Pesquisa e Desenvolvimento'),
('Atendimento ao Cliente'),
('Logística'),
('Jurídico');

CREATE TABLE IF NOT EXISTS pessoa (
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    nome character varying(200) COLLATE pg_catalog."default" NOT NULL,
    departamento_id bigint NOT NULL,
    CONSTRAINT pessoa_pkey PRIMARY KEY (id),
    CONSTRAINT pessoa_departamento_id_fkey FOREIGN KEY (departamento_id)
        REFERENCES public.departamento (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

CREATE TABLE IF NOT EXISTS public.tarefa (
    id bigint NOT NULL GENERATED ALWAYS AS IDENTITY ( INCREMENT 1 START 1 MINVALUE 1 MAXVALUE 9223372036854775807 CACHE 1 ),
    titulo character varying(1000) COLLATE pg_catalog."default" NOT NULL,
    descricao text COLLATE pg_catalog."default" NOT NULL,
    prazo date NOT NULL,
    departamento_id bigint NOT NULL,
    duracao bigint,
    finalizado boolean,
    pessoa_id bigint,
    data_inicial timestamp without time zone,
    data_final timestamp without time zone,
    CONSTRAINT tarefa_pkey PRIMARY KEY (id),
    CONSTRAINT tarefa_departamento_id_fkey FOREIGN KEY (departamento_id)
        REFERENCES public.departamento (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT tarefa_pessoa_id_fkey FOREIGN KEY (pessoa_id)
        REFERENCES public.pessoa (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);
