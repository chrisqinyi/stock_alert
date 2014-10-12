(ns helloclojure.core (:gen-class
                        :implements [qinyi.Clojure]
                        ))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(def users{"kyle"{:password "secretk" :number-pets 2} 
           "siva"{:password "secrets" :number-pets 4}
           "rob"{:password "secretr" :number-pets 6}
           "george"{:password "secretg" :number-pets 8}})

(defn -checkLogin [this username password]
  
 (let [actual-password ((users username):password)
       _sdfsd_dfgdfg (println this)
       _sdfsd_dfgdfg (println users)] 
   (= actual-password password)
 ))
(defn average-pets[]
      (let[user-data (vals users)
           number-pets (map :number-pets user-data)
           total (apply + number-pets)]
        (/ total (count users)))
      )