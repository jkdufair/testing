(ns testing.core
  (:use [clojure.string :only (trim)])
  (:use [swank.core])
  (:use [clj-time.core :only (now plus secs minutes before?)])
  (:import (java.applet Applet)
           (java.io File)
           (java.net URL)))

(def max-number 12)

(defn play-file [filename]
  (.play (Applet/newAudioClip (.toURL (File. filename)))))

(defn read-line-everywhere []
  (try
    (swank.core/with-read-line-support (read-line))
    (catch Exception ex
      (read-line))))

(defn problem [numbers-to-test]
  (let [n1 (rand-int (+ max-number 1))
        n2 (rand-nth numbers-to-test)]
    (print (format "%s x %s = " n1 n2))
    (flush)
    (let [answer (try
                   (Integer/parseInt
                   (clojure.string/trim
                    (read-line-everywhere)))
                   (catch Exception _))
          correct (= answer (* n1 n2))]
      (if correct
        (do
          (println "Right!")
          (play-file "woohoo.wav"))
        (do
          (println "Wrong...")
          (play-file "scream.wav")))
      correct)))

(defn quiz [total correct end-time numbers-to-test]
  (if (before? (now) end-time)
    (recur (inc total)
             (if (problem numbers-to-test) (inc correct) correct)
             end-time
             numbers-to-test)
    (assoc {} :correct correct :total total)))

(defn start-quiz [quiz-duration numbers-to-test]
  (let [end-time (plus (now) (minutes quiz-duration))
        {:keys [correct total]} (quiz 0 0 end-time numbers-to-test)]
    (format "right: %s, wrong %s" correct (- total correct))))

(defn -main [& args]
  (start-quiz (first args) (second args)))
