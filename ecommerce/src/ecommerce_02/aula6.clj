(ns ecommerce-02.aula6
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
(pprint @(db/adiciona-produtos! conn [computador celular calculadora celular-barato xadrez] "200.216.222.125"))
(pprint (db/todos-os-produtos-do-ip (d/db conn) "200.216.222.125"))

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

(def produtos (db/todos-os-produtos-pull (d/db conn)))
(pprint produtos)

(pprint (db/todos-os-produtos-mais-caros (d/db conn)))
(pprint (db/todos-os-produtos-mais-caros-nested-querys (d/db conn)))


;Transação:
;;A primeira entidade representa o registro da transação
;[#datom[13194139534322 50 #inst "2020-05-11T23:26:04.757-00:00" 13194139534322 true]
; #datom[17592186045427 73 "Camiseta" 13194139534322 true]
; #datom[17592186045427 74 "/camiseta" 13194139534322 true]
; #datom[17592186045427 75 30M 13194139534322 true]
; #datom[17592186045427 72 #uuid "c73fed5e-826e-40e5-93aa-8473b8f059ea" 13194139534322 true]
; #datom[17592186045427 76 17592186045428 13194139534322 true]
; #datom[17592186045428 77 "Roupas" 13194139534322 true]
; #datom[17592186045428 78 #uuid "8adfcdae-cd2a-4089-a8b9-ecc4f17579a4" 13194139534322 true]],
;id-entidade  atributo valor id-transacao(ID-TX) operação


;É possível armazenar mais dados no registro da transação.

;tx-data é o namespace usado para dados de transação
;;db.type/ref para referenciar uma entidade.