(ns zx.http
  (:require [clojure.spec.alpha :as s]
            [integrant.core :as ig]
            [reitit.core :as r]
            [reitit.ring :as ring]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.ring.coercion :as coercion]
            [reitit.coercion.spec]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [zx :as zx]))

(defrecord HttpExecutor [system opts]
  zx/Executor
  (-start
    [this]
    (jetty/run-jetty (:zx.http/service system) opts))

  (-stop
    [this server]
    (.stop server)))

(defn get-executor
  [system opts]
  (->HttpExecutor system opts))

(defn get-context
  [req]
  (-> req (ring/get-match) :data :context))

(defn build-api
  [resources]
  resources)

(defn build-service
  [apis]
  (ring/ring-handler
   (ring/router
    (conj apis
          ["/swagger.json"
           {:get {:no-doc true
                  :swagger {:info {:title "my-api"}}
                  :handler (swagger/create-swagger-handler)}}])
    {:data {:coercion reitit.coercion.spec/coercion
            :muuntaja m/instance
            :middleware [;; query-params & form-params
                         parameters/parameters-middleware
                         ;; content-negotiation
                         muuntaja/format-negotiate-middleware
                           ;; encoding response body
                         muuntaja/format-response-middleware
                           ;; exception handling
                         exception/exception-middleware
                           ;; decoding request body
                         muuntaja/format-request-middleware
                           ;; coercing response bodys
                         coercion/coerce-response-middleware
                           ;; coercing request parameters
                         coercion/coerce-request-middleware
                           ;; multipart
                         multipart/multipart-middleware]}})
   (ring/routes
    (swagger-ui/create-swagger-ui-handler {:path "/"
                                           :config {:validatorUrl nil}})
    (ring/create-default-handler))))

(defmethod ig/init-key :zx.http.api/endpoint
  [_ endpoint]
  endpoint)

(defmethod ig/init-key :zx.http/service
  [_ {:keys [apis]}]
  (build-service apis))

(defmethod ig/init-key :zx.http/api
  [_ {:keys [endpoints]}]
  (build-api endpoints))
