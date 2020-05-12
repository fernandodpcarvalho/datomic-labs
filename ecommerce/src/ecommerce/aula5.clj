(ns ecommerce.aula5
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador_novo", 2500.10M)
      celular (model/novo-produto "Celular Novo", "/celular", 999.99M)
      resultado @(d/transact conn [computador celular])]
  (pprint resultado))

;snapshot deste momento antes do segundo "insert"
(def fotografia-no-passado (d/db conn))

(let [calculadora {:produto/nome "Calculadora 4 operações"}
      celular-barato (model/novo-produto "Celular barato", "/celular_barato", 499.99M)
      resultado @(d/transact conn [calculadora celular-barato])]
  (pprint resultado))

;snapshot" do passado.
(pprint (count (db/todos-os-produtos-pull fotografia-no-passado)))
(pprint (count (db/todos-os-produtos-pull (d/as-of (d/db conn) #inst "2020-05-01T16:31:53.986-00:00"))))
(pprint (db/todos-os-produtos-pull (d/as-of (d/db conn) #inst "2020-05-01T16:31:53.986-00:00")))

;snapshot db presente.
(pprint (count (db/todos-os-produtos-pull (d/db conn))))
(pprint (count (db/todos-os-produtos-pull (d/as-of (d/db conn) #inst "2020-05-01T16:31:53.998-00:00"))))
(pprint (db/todos-os-produtos-pull (d/as-of (d/db conn) #inst "2020-05-01T16:31:53.998-00:00")))
