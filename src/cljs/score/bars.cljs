(ns score.bars
  (:use [clojure.string :only [join]]
        [jayq.core :only [$ xhr document-ready dequeue]])
  (:require [score.canvas :as c]))

(defn log [& msg]
  (.log js/console (join " " msg)))

(defn paint [data]
  (let [scores (sort-by (comp - first second) data)
        max (first (second (first scores)))
        canvas (c/init-canvas (.getElementById js/document "score"))
        context (c/get-context canvas)
        w (.-width canvas)
        h (.-height canvas)
        bar-height (/ h (count scores))
        score-width (/ w max)
        grd (c/make-gradient-fill context w)]
    (set! (.-font context) "16px Arial")
    (doseq [[n [user [score]]] (map (fn [a b] [a b]) (range 0 1e6 bar-height) scores)]
      (doto context
        (c/set-fill! grd)
        (.fillRect 0 n (* score score-width) bar-height)
        (c/set-fill! "white")
        (.fillText (str user " (" score ")") 10 (+ 30 n))))))

(defn start []
  (xhr [:get "/rest/score"] {} (fn [d] (paint (get (js->clj d) "data")))))

(document-ready start)
