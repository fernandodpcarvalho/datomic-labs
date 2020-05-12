(ns ecommerce_02.db
  (:use clojure.pprint)
  (:require [datomic.api :as d])
  (:require [ecommerce_02.schema :as schema]))

(def db-uri "datomic:dev://localhost:4334/ecommerce")

(defn abre-conexao []
  (d/create-database db-uri)
  (d/connect db-uri))

(defn apaga-banco []
  (d/delete-database db-uri))

(defn cria-schema [conn]
  (d/transact conn schema/schema))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;CURSO2
(defn um-produto-por-dbid [db produto-id]
  (d/pull db '[*] produto-id))

(defn um-produto [db produto-id]
  (d/pull db '[*] [:produto/id produto-id]))


(defn todas-as-categorias [db]
  (d/q '[:find (pull ?categoria [*])
         :where [?categoria :categoria/id]]
       db))

(defn gera-db-adds-de-atribuicao-de-categorias [produtos categoria]
  (reduce (fn [db-adds produto] (conj db-adds [:db/add
                                               [:produto/id (:produto/id produto)]
                                               :produto/categoria
                                               [:categoria/id (:categoria/id categoria)]]))
          []
          produtos))

(defn atribui-categorias! [conn produtos categoria]
  (let [a-transacionar (gera-db-adds-de-atribuicao-de-categorias produtos categoria)]
    (d/transact conn a-transacionar)))

(defn adiciona-produtos!
  ([conn produtos]
   (d/transact conn produtos))
  ([conn produtos ip]
   (let [db-add-ip [:db/add "datomic.tx" :tx-data/ip ip]]
     (d/transact conn (conj produtos db-add-ip)))))

(defn adiciona-categorias! [conn categorias]
  (d/transact conn categorias))


(defn todos-os-nomes-de-produtos-e-categorias [db]
  (d/q '[:find ?nome ?categoria
         :where [_ :produto/nome ?nome]
         [_ :produto/categoria ?categoria]]
       db))

;JOIN
(defn todos-os-nomes-de-produtos-e-nomes-de-categorias [db]
  (d/q '[:find ?nome-do-produto ?nome-da-categoria
         :keys produto categoria
         :where [?produto :produto/nome ?nome-do-produto]
         [?produto :produto/categoria ?categoria]
         [?categoria :categoria/nome ?nome-da-categoria]]
       db))

;forward navigation
(defn todos-os-produtos-da-cateoria-forward [db categoria-nome]
  (d/q '[:find (pull ?produto [:produto/nome :produto/slug {:produto/categoria [:categoria/nome]}])
         :in $ ?categoria-nome
         :where [?categoria :categoria/nome ?categoria-nome]
         [?produto :produto/categoria ?categoria]]
       db categoria-nome))

;backward navigation
(defn todos-os-produtos-da-cateoria-backward [db categoria-nome]
  (d/q '[:find (pull ?categoria [:categoria/nome {:produto/_categoria [:produto/nome :produto/slug]}])
         :in $ ?categoria-nome
         :where [?categoria :categoria/nome ?categoria-nome]]
       db categoria-nome))

;(defn resumo-dos-produtos [db]
;  (d/q '[:find (min ?preco) (max ?preco) (count ?preco)
;         :where [_ :produto/preco ?preco]]
;       db))
;Datomic trata osvalores como um conjunto, sem repetição.
;Se dois produtos tiverem o mesmo preço, serão considerados o mesmo no count


(defn resumo-dos-produtos [db]
  (d/q '[:find (min ?preco) (max ?preco) (count ?preco)
         :keys minimo maximo total
         :with ?produto
         :where [?produto :produto/preco ?preco]]
       db))

;group by categoria
(defn resumo-dos-produtos-por-categoria [db]
  (d/q '[:find ?categoria (min ?preco) (max ?preco) (count ?preco)
         :keys categoria minimo maximo total
         :with ?produto
         :where [?produto :produto/preco ?preco]
         [?produto :produto/categoria ?categoria]]
       db))

;duas querys
(defn todos-os-produtos-mais-caros [db]
  (let [preco-mais-alto (ffirst (d/q '[:find (max ?preco)
                                       :where [_ :produto/preco ?preco]]
                                     db))]
    (d/q '[:find (pull ?produto [*])
           :in $ ?preco
           :where [?produto :produto/preco ?preco]]
         db preco-mais-alto)))

;uma query
(defn todos-os-produtos-mais-caros-nested-querys [db]
  (d/q '[:find (pull ?produto [*])
         :where [(q '[:find (max ?preco)
                      :where [_ :produto/preco ?preco]]
                    $) [[?preco]]]
         [?produto :produto/preco ?preco]]
       db))

(defn todos-os-produtos-do-ip [db ip]
  (d/q '[:find (pull ?produto [*])
         :in $ ?ip
         :where [?transacao :tx-data/ip ?ip]
                [?produto :produto/id _ ?transacao]]
       db ip))