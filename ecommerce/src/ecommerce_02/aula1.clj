(ns ecommerce_02.aula1
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce_02.db :as db]
            [ecommerce_02.model :as model]))

(db/apaga-banco)
(def conn (db/abre-conexao))
(db/cria-schema conn)

(let [computador (model/novo-produto (model/uuid) "Computador Novo", "/computador_novo", 2500.10M)
      celular (model/novo-produto (model/uuid) "Celular Novo", "/celular", 1999.99M)
      calculadora {:produto/nome "Calculadora 4 operações"}
      celular-barato (model/novo-produto (model/uuid) "Celular barato", "/celular_barato", 499.99M)
      ]
  (pprint @(d/transact conn [computador celular calculadora celular-barato])))


;;;;;;;;;;;;;;;;;;;;;;;;;;
(def produtos (db/todos-os-produtos-pull (d/db conn)))

(def primeiro-produto-dbid (-> produtos
                             ffirst
                             :db/id))
(println primeiro-produto-dbid)
(pprint (db/um-produto-por-dbid (d/db conn) primeiro-produto-dbid))

;uuid
(def primeiro-produto-id (-> produtos
                             ffirst
                             :produto/id))
(println primeiro-produto-id)

(pprint (db/um-produto (d/db conn) primeiro-produto-id))
