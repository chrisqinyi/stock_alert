(ns qinyi.StockBean (:gen-class
                        :implements [qinyi.IStockBean]
                        ))
(import HttpXmlClient)
(import BaiduPushUtil)
(import DataPersistUtil)
(import java.text.SimpleDateFormat)
(import java.util.Date)
(require '[clojure.string :as str])
(require '[clojure.set :as set])
(def STOCK_CODES #{"6011771","6003861","3003382","0025572","0024272","0022582"})
(loop [i 0 stockPriceMinutelyMap {}]
  (if (= i (.size STOCK_CODES)) stockPriceMinutelyMap (recur (inc i)(assoc stockPriceMinutelyMap (STOCK_CODES i) []))))
(defn -processMinutely [stockBean])
(defn getNewStocks[]
  (let [response (HttpXmlClient/get "http://www.iwencai.com/stockpick/search?preParams=&ts=1&f=1&qs=1&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w=5%E6%97%A510%E6%97%A530%E6%97%A5%E5%9D%87%E7%BA%BF%E7%B2%98%E5%90%88+%E4%B8%8A%E5%B8%82%E6%97%A5%E6%9C%9F%E5%9C%A81%E5%B9%B4%E5%89%8D++%E7%9B%98%E6%95%B4+%E4%B8%AD%E5%B0%8F%E7%9B%98+%E9%9D%9E%E5%88%9B%E4%B8%9A%E6%9D%BF+MACD%E4%B8%8A%E5%8D%87+%E8%82%A1%E4%BB%B7%3C20")
        matcher (re-matcher #"\[\"(\d{6}\.SZ|SH)\",.*?\]" response)]
    (loop [match (re-find matcher) lst []]
    (if match 
      (recur (re-find matcher) (conj lst (str (second match)))) (map (fn[s](str/replace (str/replace s #".SH" "1") #".SZ" "2")) lst)))))
(defn getStockSuggestion[code]
(let [response  (HttpXmlClient/get (str/join ["http://www.iwencai.com/stockpick/search?preParams=&ts=1&f=1&qs=1&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w=" (.substring code 0 6) ]))]
(re-find #"buy|sell" response)))
(defn getStockDailyPrice[code]
(let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HD&jsname=em_data_DailyK_1404559419762"]))
      matcher (re-matcher #"\"\d{8},\d{1,5}.\d{2},(\d{1,5}.\d{2}),\d{1,5}.\d{2},\d{1,5}.\d{2},\d{1,5}.\d{2},\d+?\"" response)]  
  (loop [match (re-find matcher) lst []]
    (if match 
      (recur (re-find matcher) (conj lst (java.lang.Double/valueOf (str (second match))))) lst))))

(defn getStock5MinutesPrice[code]
(let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HM5&jsname=em_data_Minute5_1404558145282"]))
      matcher (re-matcher #"\"(\d{8}\d{4}),\d{1,2}.\d{2},(\d{1,2}.\d{2}),\d{1,2}.\d{2},\d{1,2}.\d{2},\d{1,2}.\d{2},\d+?\"" response)
      format2 (SimpleDateFormat. "yyyyMMdd")
      s (.format format2 (Date.))]  
  (loop [match (and (.contains response s) (re-find matcher)) lst []]
    (if match 
      (recur (re-find matcher) (conj lst (java.lang.Double/valueOf (str (second match))))) lst))))

(defn getEXPMA[prices number]
(let [k (/  2.0 (+ number 1.0))]
  (reduce (fn [ema price](+ (* price k) (* ema (- 1 k)))) prices)))

(defn getMACD[prices shortPeriod longPeriod midPeriod]
(let [diffList (map (fn[i](- (getEXPMA (take (+ i 1) prices) shortPeriod) 
                             (getEXPMA (take (+ i 1) prices) longPeriod))) 
                    (range (.size prices))) 
      dif (last diffList) 
      dea (getEXPMA diffList midPeriod) 
      macd (if (nil? dif)0.00(* (- dif dea) 2))] 
      {:DIF dif :DEA dea :MACD macd}))

(defn -processDaily [stockBean]
(let [_ (println "before process")
      STOCK_CODES (set (concat (DataPersistUtil/getData) (getNewStocks)))
      _ (println (str/join ["STOCK_CODES: " STOCK_CODES]))
      STOCK_CODES_TMP (map (fn[code](let [stockDailyPrice (getStockDailyPrice code)
                            dLastestPrice (last stockDailyPrice) 
                            dMacd ((getMACD stockDailyPrice 9 26 12) :MACD)
                            dOldMacd ((getMACD (if (empty? stockDailyPrice)[](pop stockDailyPrice)) 9 26 12) :MACD)
                            _ (if (and (and (>= dMacd -0.005) (<= dMacd 0.005)) (or (> dOldMacd 0.005) (< dOldMacd -0.005))) 
                                (let [format2 (SimpleDateFormat."yyyyMMdd HH:mm") s (.format format2 (Date.))
							                       _(if (> dMacd dOldMacd) (let [_ (BaiduPushUtil/push_mailToAll (str/join [s " " code]) (str/join ["daily price:" dLastestPrice ";gold cross"]))
							                                                     _ (println (str/join s " " code ":daily MACD gold cross signal sent!" ))]) 
																		                         (let [_ (BaiduPushUtil/push_mailToAll (str/join [s " " code]) (str/join ["daily price:" dLastestPrice ";dead cross"]))
							                                                     _ (println (str/join s " " code ":daily MACD dead cross signal sent!" ))]))]) nil)
														stockSuggestion (getStockSuggestion code)
                                     _ (if (str/blank? stockSuggestion) nil (let[format2 (SimpleDateFormat."yyyyMMdd HH:mm") s (.format format2 (Date.))
														                        _ (BaiduPushUtil/push_mailToAll (str/join [s " " code]) (str/join ["suggestion:" stockSuggestion]))
														                        _ (println (str/join [s " " code ":suggestion signal sent!"] ))]))]stockSuggestion))(concat STOCK_CODES ["0000011" "3990012"]))
      _ (println (str/join ["STOCK_CODES_TMP: " STOCK_CODES_TMP]))
      STOCK_CODES_SELL (reduce (fn[result i](if(= (nth STOCK_CODES_TMP i) "sell")(conj result (nth (concat [] STOCK_CODES) i))result))(cons [] (range (.size STOCK_CODES))))
      _ (println (str/join ["STOCK_CODES_SELL: " STOCK_CODES_SELL]))
      _ (def STOCK_CODES_MON (reduce (fn[result i](if(str/blank? (nth STOCK_CODES_TMP i))result(conj result (nth (concat [] STOCK_CODES) i))))(cons [] (range (.size STOCK_CODES)))))
      _ (println (str/join ["STOCK_CODES_MON: " STOCK_CODES_MON]))
      _ (println (str/join ["STOCK_CODES: " STOCK_CODES]))
      _ (def STOCK_CODES (clojure.set/difference STOCK_CODES STOCK_CODES_SELL))
      _ (println (str/join ["STOCK_CODES: " STOCK_CODES]))
      _ (DataPersistUtil/putData STOCK_CODES)
      _ (println "after process")]))

(defn -process5Minutesly [stockBean]
(let [_ (println "before process")  
      STOCK_CODES STOCK_CODES_MON
      _(loop[i 0 _ nil]
         (if (= i (.size STOCK_CODES))nil(recur (inc i)
               (let [code (STOCK_CODES i)
                     stock5MinutesPrice (getStock5MinutesPrice code) 
                     dLastestPrice (last stock5MinutesPrice) 
                     dMacd ((getMACD stock5MinutesPrice 9 26 12) :MACD)
                     dOldMacd ((getMACD (if (empty? stock5MinutesPrice)[](pop stock5MinutesPrice)) 9 26 12) :MACD)
                     _ (if (and (and (>= dMacd -0.005) (<= dMacd 0.005)) (or (> dOldMacd 0.005) (< dOldMacd -0.005))) 
                                                          (let [format2 (SimpleDateFormat."yyyyMMdd HH:mm") s (.format format2 (Date.))
																				                       _(if (> dMacd dOldMacd) (let [_ (BaiduPushUtil/push_mailToAll (str/join [s " " code]) (str/join ["5m price:" dLastestPrice ";gold cross"]))
																				                                                     _ (println (str/join [s " " code ":5 minutes MACD gold cross signal sent!"] ))
																				                                                     ]) 
																															                         (let [_ (BaiduPushUtil/push_mailToAll (str/join [s " " code]) (str/join ["5m price:" dLastestPrice ";dead cross"]))
																				                                                     _ (println (str/join [s " " code ":5 minutes MACD dead cross signal sent!"] ))
																															                               ]))]) nil)]))))
      _ (println "after process")]))
