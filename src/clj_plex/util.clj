(ns clj-plex.util
  (:require [clojure.string :as c.string]
            [clojure.java.io :as io]
            [clojure.xml :as xml])
  (:import [javax.xml.parsers SAXParserFactory]))

(defn trim-left
  "Will take the provided string and if that string begins with the provided
  prefix, it will return a new string that omits that prefix. If the provided
  string does not contain that prefix then the original string is returned."
  [str prefix]
  (if (-> str (c.string/starts-with? prefix))
    (subs str (count prefix) (count str))
    str))

(defn trim-right
  "Will take the provided string and if that string ends with the provided
  suffix then a new string without that suffix will be returned. If the string
  does not contain the suffix then the original string is returned."
  [str suffix]
  (if (-> str (c.string/ends-with? suffix))
    (subs str 0 (- (count str) (count suffix)))
    str))

(defn parse-xml-response [{:keys [body]}]
  (let [non-validating (fn [s ch]
                         (..
                          (doto
                           (SAXParserFactory/newInstance)
                            (.setFeature
                             "http://apache.org/xml/features/nonvalidating/load-external-dtd" false))
                          (newSAXParser)
                          (parse s ch)))
        body (if (instance? String body)
               (-> body .getBytes io/input-stream)
               body)]
    (xml/parse body non-validating)))
