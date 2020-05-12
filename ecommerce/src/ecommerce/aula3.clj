(ns ecommerce.aula3
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador_novo", 2500.10M)
      celular (model/novo-produto "Celular Novo", "/celular", 999.99M)
      calculadora {:produto/nome "Calculadora 4 operações"}
      celular-barato (model/novo-produto "Celular barato", "/celular_barato", 499.99M)
      ]
  (d/transact conn [computador celular calculadora celular-barato]))





;Banco de leitura - snapshot do bd
(def db (d/db conn))

(pprint (db/todos-os-produtos (d/db conn)))

(pprint (db/todos-os-produtos-pull (d/db conn)))

(pprint (db/todos-os-produtos-por-slug-without-param (d/db conn)))

(pprint (db/todos-os-produtos-por-slug (d/db conn) "/computador_novo"))

(pprint (db/todos-os-produtos-por-slug-with-var-query (d/db conn) "/computador_novo"))

(pprint (db/todos-os-slugs (d/db conn)))

(pprint (db/todos-os-produtos-por-preco (d/db conn)))

;(db/apaga-banco)
