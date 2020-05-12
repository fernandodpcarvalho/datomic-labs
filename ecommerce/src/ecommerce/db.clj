(ns ecommerce.db
  (:use clojure.pprint)
  (:require [datomic.api :as d]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco []
  (d/delete-database db-uri))


(def schema [{:db/ident       :produto/nome
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O nome de um produto"}
             {:db/ident       :produto/slug
              :db/valueType   :db.type/string
              :db/cardinality :db.cardinality/one
              :db/doc         "O caminho para acessar esse produto via http"}
             {:db/ident       :produto/preco
              :db/valueType   :db.type/bigdec
              :db/cardinality :db.cardinality/one
              :db/doc         "O preco de um produto com precisão monetária"}])

;             {:db/ident       :produto/palavra-chave
;              :db/ValueType   :db.type/string
;              :db/cardinality :db.cardinality/many
;              :db/doc         "Palavras chave para busca do produto"}])

(defn cria-schema [conn]
  (d/transact conn schema))

(defn todos-os-produtos [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/nome]] db))

(defn todos-os-produtos-02 [db]
  (d/q '[:find ?nome
         :where [_ :produto/nome ?nome]] db))

(defn todos-os-produtos-03 [db]
  (d/q '[:find ?nome
         :where [?e :produto/nome ?nome]
         [?e :produto/slug "/celular"]] db))

(defn todos-os-produtos-04 [db]
  (d/q '[:find ?nome ?slug
         :where [?e :produto/nome ?nome]
         [?e :produto/slug ?slug]
         [?e :produto/preco 2500.10M]] db))

(defn todos-os-produtos-pull [db]
  ;(d/q '[:find (pull ?entidade [:produto/nome :produto/preco :produto/slug])
  (d/q '[:find (pull ?entidade [*])
         :where [?entidade :produto/nome]] db))

;Without param
(defn todos-os-produtos-por-slug-without-param [db]
  (d/q '[:find ?entidade
         :where [?entidade :produto/slug "/computador_novo"]] db))

;With two params ("db" and "slug") needs the ":in"
(defn todos-os-produtos-por-slug [db slug]
  (d/q '[:find ?entidade
         :in $ ?slug
         :where [?entidade :produto/slug ?slug]]
       db slug))

;Notação hungara = "-q" = Não recomendado
(def todos-os-produtos-por-slug-q
  '[:find ?entidade
    :where [?entidade :produto/slug "/computador_novo"]])
(defn todos-os-produtos-por-slug-with-var-query [db slug]
  (d/q todos-os-produtos-por-slug-q db))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;(defn todos-os-slugs [db]
;  (d/q '[:find ?qualquer-valor
;         :where [?entidade :produto/slug ?qualquer-valor]] db))

;(defn todos-os-slugs [db]
;  (d/q '[:find ?produto, ?slug
;         :where [?produto :produto/slug ?slug]] db))

;entity => ?entidade => ?produto => ?p
(defn todos-os-slugs [db]
  (d/q '[:find ?slug
         :where [_ :produto/slug ?slug]] db))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;(defn todos-os-produtos-por-preco [db]
;  (d/q '[:find ?preco
;         :where [_ :produto/preco ?preco]] db))

;(defn todos-os-produtos-por-preco [db]
;  (d/q '[:find ?nome, ?preco
;         :where [?produto :produto/preco ?preco]
;                [?produto :produto/nome ?nome]] db))

(defn todos-os-produtos-por-preco [db]
  (d/q '[:find ?nome, ?preco
         :keys produto/nome, produto/preco
         :where [?produto :produto/preco ?preco]
         [?produto :produto/nome ?nome]] db))

(defn todos-os-produtos-por-preco-minimo [db preco-minimo]
  (d/q '[:find ?nome, ?preco
         :in $, ?preco-minimo
         :keys produto/nome, produto/preco
         :where [?produto :produto/preco ?preco]
         [(> ?preco ?preco-minimo)]
         [?produto :produto/nome ?nome]
         ] db, preco-minimo))


(defn todos-os-produtos-por-palavra-chave [db palavra-chave]
  (d/q '[:find (pull ?produto [*])
         :in $ ?palavra-chave
         :where [?produto :produto/palavra-chave ?palavra-chave]]
       db palavra-chave))