(ns noir-test.views.welcome
  (:require [noir-test.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to noir-test"]))
