(ns score.graph
  (:require [score.canvas :as c]
            [clojure.string :as s]))

(defn make-fill [context]
  (doto (.createRadialGradient context 0 0 0 0 0 17)
    (.addColorStop 0 "rgba(192,255,0,255)")
    (.addColorStop 1 "rgba(0,255,0,0)")))

(defn draw-figure [context fx fy pixels]
  (dorun
    (for [y (range 8) x (range 5) :when (= (get pixels (+ x (* y 5))) 1)]
      (doto context
        (.setTransform 1 0 0 1 (+ fx (* x 20)) (+ fy (* y 20)))
        (c/set-fill! (make-fill context))
        (.fillRect -30 -30 60 60)))))
