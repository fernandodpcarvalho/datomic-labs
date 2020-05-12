(ns ecommerce.aula6
  (:use clojure.pprint)
  (:require [datomic.api :as d]
            [ecommerce.db :as db]
            [ecommerce.model :as model]))

(db/apaga-banco)
(def conn (db/abre-conexao))
(db/cria-schema conn)

(let [computador (model/novo-produto "Computador Novo", "/computador_novo", 2500.10M)
      celular (model/novo-produto "Celular Novo", "/celular", 1999.99M)
      calculadora {:produto/nome "Calculadora 4 operações"}
      celular-barato (model/novo-produto "Celular barato", "/celular_barato", 499.99M)
      ]
  (pprint @(d/transact conn [computador celular calculadora celular-barato])))

;update com alter table
;Substituir o ID do produto gerado
(d/transact conn [[:db/add 17592186045418 :produto/palavra-chave "desktop"]
                  [:db/add 17592186045418 :produto/palavra-chave "computador"]
                  ])
(pprint (db/todos-os-produtos-pull (d/db conn)))

(pprint (db/todos-os-produtos (d/db conn)))
(pprint (db/todos-os-produtos-02 (d/db conn)))
(pprint (db/todos-os-produtos-03 (d/db conn)))
(pprint (db/todos-os-produtos-04 (d/db conn)))

(db/todos-os-produtos-por-preco-minimo (d/db conn) 1000)


;(d/transact conn [[:db/retract 17592186045418 :produto/palavra-chave "desktop"]])

;(pprint (db/todos-os-produtos-por-palavra-chave (d/db conn) "computador"))