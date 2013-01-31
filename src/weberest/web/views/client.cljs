(ns weberest.web.views.client
    (:require [enfocus.core :as ec]
              [dommy.template :as dt]
              [domina :as d]
              [domina.css :as dc])
    (:require-macros [enfocus.macros :as em]))

(defn create-svg-element 
  [tag & {:keys [text attrs styles] 
          :or {text "" attrs {} styles {}}}]
  (-> (.createElementNS js/document "http://www.w3.org/2000/svg" tag)
      (d/set-attrs! ,,, attrs)
      (d/set-styles! ,,, styles)
      (d/set-text! ,,, text)))
 
(def value-dot-increase-factor 3)
 
(defn rgb [r g b]
  (str "rgb(" r "," g "," b ")")) 
 
(defn dot-label [id x y text options]
  [:text (merge {:id id 
                 :x x :y y
                 :label-font-family "Verdana"
                 :label-font-size "55px"
                 :visibility :visible
                 :fill (rgb 100 100 150)} options) 
         text])
   
(defn create-dot-label 
  [& {:keys [x y text color]
      :or {x 0 y 0 text "" color "black"}}]
  (create-svg-element "text" 
                      :text text
                      :attrs {:x x :y y 
                              :transform "translate(-8, -20)"}
                      :styles {:fill color}))
  
(defn enlarge-value-dot [evt]
  (let [ct (.-currentTarget evt)
        {old-r :r x :cx y :cy 
         color :fill day 
         :data-x value :data-y} (d/attrs ct)
        {label :data-label unit :data-unit} (d/attrs (.-parentNode ct))
        dl (create-dot-label :x x :y y :color color
                             :text (str "Tag: " day " | " label ": " value unit))]
       (d/insert-after! ct dl)
       (d/set-attr! ct :r (* old-r value-dot-increase-factor))))

(defn shrink-value-dot [evt]
  (let [ct (.-currentTarget evt)
        dl (.-nextElementSibling ct)]
       (when (and dl (= (.-localName "text")))
             (d/destroy! dl))
       (d/set-attr! ct :r (/ (d/attr ct :r) 
                             value-dot-increase-factor))))

(defn ^:export setup-value-display-svg-listeners []
  (em/at js/document
         ["circle"] (em/do-> 
                          (em/listen :mouseenter enlarge-value-dot)
                          (em/listen :mouseleave shrink-value-dot))))

















#_(em/defaction 
 transform-rect []
 ["#bla"] (em/substitute "<text x=0 y=0>text</text>"))

#_(defn ^:export call-transform-rect [] 
  (d/append! 
           (dc/sel "svg") 
           (create-svg-element "rect" :attrs {:x 100
                                              :y 100
                                              :width 100
                                              :height 100}
                                             :styles {:fill "green"})))
  
#_(defn start [] 
  (em/at js/document
    ["#xxx"] (hello-world)))

#_(em/defaction 
 setup []
 ["#xxx"] (em/listen :click hello-world))

#_(defn start [] 
  (em/at js/document
    ["#xxx"] (em/content "Hello world!")))

#_(set! (.-onload js/window) setup)

#_(defn ^:export hello-world []
  (js/alert "Hello world!"))
 