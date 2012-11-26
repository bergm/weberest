(ns berest.helper)

(defn thrush [& args] 
  (reduce #(%2 %1) args))

(defn rcomp [& args]
  (apply comp (reverse args)))

(defn |-> [& args]
  (apply comp (reverse args)))

(def |<- comp)

(def --< juxt)

(def |* partial)

(defn |*kw [f & kw-args]
  (fn [& args]
    (apply f (concat args kw-args))))

(defn --<* 
  "symbol for juxt and directly useable as a function with variable arguments  
(at least be two)"
  [& rest] 
  ((apply juxt (butlast rest)) (last rest)))

(defn swap [f arg1 arg2]
  (f arg2 arg1))

(defn args-21->12 [f arg2 arg1]
  (f arg1 arg2))

(defn args-231->123 [f arg2 arg3 arg1]
  (f arg1 arg2 arg3))