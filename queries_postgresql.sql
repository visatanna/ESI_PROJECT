CREATE TABLE instituicao (
	nome VARCHAR(30) NOT NULL,
	cod_instituicao SERIAL not null,
	cnpj varchar(22) not null,
	telefone varchar(13) not null,
	email varchar(30) not null,
	login varchar(15) not null,
	senha varchar(20) not null,
	estado varchar(5) not null,
	cidade varchar(20) not null,
	bairro varchar(25) not null,
	rua varchar(20) not null,
	numero int not null,
	complemento varchar(20),
	CEP varchar(15) not null,	
	constraint pk_cod_instituicao primary key (cod_instituicao)
);

CREATE TABLE usuario (
	nome VARCHAR(30) NOT NULL,
	sobrenome VARCHAR(30) NOT NULL,
	apelido varchar(25) not null,
	cod_usuario SERIAL not null,
	sexo varchar(3) not null,
	cpf varchar(22) not null,
	rg varchar(13) not null,
	telefone_fixo varchar(13) not null,
	telefone_movel varchar(13) not null,
	email varchar(30) not null,
	login varchar(15) not null,
	senha varchar(20) not null,
	estado varchar(5) not null,
	cidade varchar(20) not null,
	bairro varchar(25) not null,
	rua varchar(20) not null,
	numero int not null,
	complemento varchar(20),
	CEP varchar(15) not null,	
	constraint pk_cod_usuario primary key (cod_usuario)
);

CREATE TABLE item(
	nome varchar(25) not null,
	categoria varchar(15) not null,
	descricao varchar(100),
	lance_minimo money not null,
	quantidade integer not null,
	foto bytea,
	cod_item SERIAL NOT NULL,
	cod_instituicao integer not null,
	constraint pk_cod_item primary key (cod_item),
	constraint fk_cod_instituicao FOREIGN KEY (cod_instituicao) REFERENCES instituicao (cod_instituicao)	
);

CREATE TABLE lista_compras(
	cod_item integer not null,
	data_compra timestamp not null,
	preco money not null,
	pago boolean not null,
	entregue boolean not null,
	cod_compra serial not null,
	cod_instituicao int not null,
	cod_usuario int not null,
	constraint pk_lista_comp primary key (cod_compra),
	constraint fk_cod_instituicao_lc  FOREIGN KEY(cod_instituicao) REFERENCES instituicao (cod_instituicao),
	constraint fk_cod_us_lc  FOREIGN KEY(cod_usuario) REFERENCES usuario (cod_usuario),
	constraint fk_cod_item_lc  FOREIGN KEY(cod_item) REFERENCES item (cod_item)
); 

CREATE TABLE lista_de_itens(
	cod_item integer not null,
	quantidade integer not null,
	valor_atual integer not null,
	id_usuario_ml1 integer,
	id_usuario_ml2 integer,
	id_usuario_ml3 integer,
	id_usuario_ml4 integer,
	id_usuario_ml5 integer,
	cod_lista serial not null,
	constraint pk_lista_li primary key (cod_lista),
	constraint fk_cod_item_li  FOREIGN KEY(cod_item) REFERENCES item (cod_item)
);

CREATE TABLE leilao(
	nome varchar(30) not null,
	data_inicio timestamp not null,
	data_termino timestamp not null,
	cod_lista int not null,
	cod_leilao serial not null,
	cod_instituicao int not null,
	ativo boolean not null,
	constraint pk_leilao primary key (cod_leilao),
	constraint fk_cod_leilao_i  FOREIGN KEY(cod_instituicao) REFERENCES instituicao (cod_instituicao),
	constraint fk_cod_leilao_l  FOREIGN KEY(cod_lista) REFERENCES lista_de_itens (cod_lista)
);