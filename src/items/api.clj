(ns items.api
  (:gen-class)
  (:require [zx]
            [zx.http]))

(defn -main
  []
  (-> (zx/read-config "resources/api.edn")
      (zx/get-system)
      (zx.http/get-executor {:port 3000 :join? false})
      (zx/start)))
