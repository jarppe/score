(ns score.canvas)

(defn init-canvas [canvas]
  (set! (.-width canvas) (.-width js/document))
  (set! (.-height canvas) (.-height js/document))
  (set! js/canvasW (.-width canvas))
  (set! js/heightW (.-height canvas))
  canvas)

(defn get-context [canvas]
  (let [context (.getContext canvas "2d")]
    (set! (.-lineCap context) "round")
    (set-line! context "#00FF00" 2)
    context))

(defn set-line! [context style width]
  (set! (.-strokeStyle context) style)
  (set! (.-lineWidth context) width)
  context)

(defn set-fill! [context style]
  (set! (.-fillStyle context) style))

(defn make-gradient-fill [context w]
  (doto (.createLinearGradient context 0 0 w 0)
    (.addColorStop 0 "#200000")
    (.addColorStop 1 "#FF0000")))

