(ns ecommerce_02.aula2
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce_02.db :as db]
            [ecommerce_02.model :as model]))

(db/apaga-banco)
(def conn (db/abre-conexao))
(db/cria-schema conn)

(def computador (model/novo-produto (model/uuid) "Computador Novo", "/computador_novo", 2500.10M))
(def celular (model/novo-produto (model/uuid) "Celular Novo", "/celular", 1999.99M))
(def celular-barato (model/novo-produto (model/uuid) "Celular barato", "/celular_barato", 499.99M))
(def calculadora {:produto/nome "Calculadora 4 operações"})

(pprint @(d/transact conn [computador celular calculadora celular-barato]))

;;usar o mesmo uuid para inserir novo elemento
(def celular-barato2 (model/novo-produto (:produto/id celular-barato) "Celular barato 2", "/celular_barato_2", 279.99M))
(pprint celular-barato2)
(pprint @(d/transact conn [celular-barato2]))


;;;;;;;;;;;;;;;;;;;;;;;;;;
(def produtos (db/todos-os-produtos-pull (d/db conn)))
(pprint produtos)

;Se usar um mesmo id (identity) ele atualiza
;dbid e uuid são chaves