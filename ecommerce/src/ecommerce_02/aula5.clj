(ns ecommerce_02.aula5
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
(pprint @(db/adiciona-produtos! conn [computador celular calculadora celular-barato xadrez]))

(db/atribui-categorias! conn [computador, celular, celular-barato] eletronicos)
(db/atribui-categorias! conn [xadrez] esporte)

;add com nested maps
;adiciona produto e categoria
(pprint @(db/adiciona-produtos! conn [{:produto/nome      "Camiseta"
                                       :produto/slug      "/camiseta"
                                       :produto/preco     30M
                                       :produto/id        (model/uuid)
                                       :produto/categoria {:categoria/nome "Roupas"
                                                           :categoria/id   (model/uuid)}}]))

;Categoria já existe - lookup ref
(pprint @(db/adiciona-produtos! conn [{:produto/nome  "Dama"
                                       :produto/slug  "/dama"
                                       :produto/preco 15M
                                       :produto/id    (model/uuid)
                                       :produto/categoria {:categoria/id (:categoria/id esporte)}}]))

(pprint (def produtos (db/todos-os-produtos-pull (d/db conn))))
(pprint (db/todos-os-nomes-de-produtos-e-nomes-de-categorias (d/db conn)))

(pprint (db/resumo-dos-produtos (d/db conn)))
(pprint (db/resumo-dos-produtos-por-categoria (d/db conn)))