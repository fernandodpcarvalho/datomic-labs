(ns ecommerce.aula1-2
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(db/apaga-banco)

(def conn (db/abre-conexao))

(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador_novo", 2500.10M)]
  (d/transact conn [computador]))

(let [celular (model/novo-produto "Celular Novo", "/celular", 999.99M)]
  (d/transact conn [celular]))

(let [calculadora {:produto/nome "Calculadora 4 operações"}]
  (d/transact conn [calculadora]))

;Não funciona. Campo nulo é só não enviar nada.
;(let [radio-relogio  {:produto/nome "Radio com relógio", :produto-slug nil}]
;  (d/transact conn [radio-relogio]))

(let [celular-barato (model/novo-produto "Celular barato", "/celular_barato", 499.99M)
      resultado @(d/transact conn [celular-barato])
      id-entidade (first (vals (:tempids resultado)))]
  (pprint (:tempids resultado))
  (pprint (d/transact conn [[:db/add id-entidade :produto/preco 0.1M]]))
  (pprint (d/transact conn [[:db/retract id-entidade :produto/slug "celular barato"]])))

;Banco de leitura - snapshot do bd
(def db (d/db conn))

(d/q '[:find ?entidade
       :where [?entidade :produto/nome]] db)

(defn todos-os-produtos-por-slug [db slug-que-estou-procurando]
  (d/q '[:find ?entidade
         :where [?entidade :produto/slug "computador-novo"]] db))