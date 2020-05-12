(ns ecommerce_02.aula3
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce_02.db :as db]
            [ecommerce_02.model :as model]))

(db/apaga-banco)
(def conn (db/abre-conexao))
(db/cria-schema conn)

;;Categorias
(def eletronicos (model/nova-categoria "Eletrônicos"))
(def esporte (model/nova-categoria "Esporte"))

(pprint @(db/adiciona-categorias! conn [eletronicos esporte]))

(def categorias (db/todas-as-categorias (d/db conn)))
(pprint categorias)

;;Produtos
(def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador_novo", 2500.10M))
(def celular (model/novo-produto (model/uuid) "Celular Novo", "/celular", 1999.99M))
(def celular-barato (model/novo-produto (model/uuid) "Celular barato", "/celular_barato", 499.99M))
(def xadrez (model/novo-produto "Tabuleiro", "/tabuleiro", 39.99M))
(def calculadora {:produto/nome "Calculadora 4 operações"})

(pprint @(db/adiciona-produtos! conn [computador celular calculadora celular-barato]))

(def produtos (db/todos-os-produtos-pull (d/db conn)))
(pprint produtos)

(db/atribui-categorias! conn [computador, celular, celular-barato] eletronicos)

(pprint (db/um-produto (d/db conn) (:produto/id computador)))

(pprint @(db/adiciona-produtos! conn [xadrez]))
(db/atribui-categorias! conn [xadrez] esporte)

(def produtos (db/todos-os-produtos-pull (d/db conn)))
(pprint produtos)




