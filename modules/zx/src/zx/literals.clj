(ns zx.literals)

(defn handler
  [form]
  (cond
    (list? form) (eval form)
    (symbol? form) (resolve form)
    (var? form) (deref form)
    :else form))
