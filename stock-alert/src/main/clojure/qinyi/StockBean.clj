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
(def STOCK_CODES #{"6011771","6003861","3003382","0024272"})
(def STOCK_CODES_NEW ["0025572"])
(def STOCK_CODES_SELL ["0022582"])
(def STOCK_CODES_BUY [])
(defn -getStockCodes [stockBean]STOCK_CODES)
(defn -setStockCodes [stockBean _STOCK_CODES]
  (def STOCK_CODES _STOCK_CODES))
(defn -getStockCodesNew [stockBean]STOCK_CODES_NEW)
(defn -setStockCodesNew [stockBean _STOCK_CODES_NEW]
  (def STOCK_CODES_NEW _STOCK_CODES_NEW))
(defn -getStockCodesSell [stockBean]STOCK_CODES_SELL)
(defn -setStockCodesSell [stockBean _STOCK_CODES_SELL]
  (def STOCK_CODES_SELL _STOCK_CODES_SELL))

(def CROSS_STRATEGY {"goldcross\"sale\"" "\"sale\"" 
                     "deadcross\"buy\"" "\"buy\""})
(def TRADE_STRATEGY {"gold\"sale\"" "sale" 
                     "dead\"buy\"" "buy" 
                     "goldcross\"sale\"" "sale" 
                     "deadcross\"buy\"" "buy"
                     "" "keep"
                     "dead" "keep"
                     "deadcross" "keep"
                     "goldcross" "keep"
                     "gold" "keep"
                     "\"buy\"" "keep"
                     "\"sale\"" "keep"
                     "goldcross\"buy\"" "keep"
                     "gold\"buy\"" "keep"
                     "deadcross\"sale\"" "remove"
                     "dead\"sale\"" "remove"})

(loop [i 0 stockPriceMinutelyMap {}]
  (if (= i (.size STOCK_CODES)) stockPriceMinutelyMap (recur (inc i)(assoc stockPriceMinutelyMap (STOCK_CODES i) []))))

(defn -processMinutely [stockBean])

(defn getNewStocks[]
  (let [response (HttpXmlClient/get "http://www.iwencai.com/stockpick/search?typed=1&preParams=&ts=1&f=1&qs=1&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w=10%E6%97%A55%E6%97%A530%E6%97%A5%E5%9D%87%E7%BA%BF%E7%B2%98%E5%90%88++%E9%A6%96%E5%8F%91%E4%B8%8A%E5%B8%82%E6%97%A5%E6%9C%9F%E5%9C%A82014%E5%B9%B41%E6%9C%881%E6%97%A5%E4%B9%8B%E5%89%8D++%E4%B8%AD%E5%B0%8F%E7%9B%98+%E8%BF%912%E5%A4%A9macd%3E%3D-0.05+%E8%BF%912%E5%A4%A9macd%3C%3D0.05+%E8%82%A1%E4%BB%B7%3C20+%E9%9D%9E%E5%88%9B%E4%B8%9A+%E8%BF%912%E5%A4%A9MACD%E4%B8%8A%E5%8D%87")
        matcher (re-matcher #"\[\"(\d{6}\.SZ|SH)\",.*?\]" response)]
    (loop [match (re-find matcher) lst []]
    (if match 
      (recur (re-find matcher) (conj lst (str (second match)))) (map (fn[s](str/replace (str/replace s #".SH" "1") #".SZ" "2")) lst)))))

(defn getStockMonthlyPrice[code](let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HM&jsname=em_data_MonthlyK_1404559419762"]))
                                    matcher (re-matcher #"\"\d{8},(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d+?)\"" response)]  
  (loop [match (re-find matcher) lst []]
    (if match 
            (recur (re-find matcher) (conj lst {:open (java.lang.Double/valueOf (str (second match)))
                                          :close (java.lang.Double/valueOf (str (nth match 2)))
                                          :high (java.lang.Double/valueOf (str (nth match 3)))
                                          :low (java.lang.Double/valueOf (str (nth match 4)))
                                          :lClose (java.lang.Double/valueOf (str (nth match 5)))
                                          :volume (java.lang.Long/valueOf (str (nth match 6)))})) lst))))

(defn getStockWeeklyPrice[code](let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HW&jsname=em_data_WeeklyK_1404559419762"]))
                                    matcher (re-matcher #"\"\d{8},(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d+?)\"" response)]  
  (loop [match (re-find matcher) lst []]
    (if match 
            (recur (re-find matcher) (conj lst {:open (java.lang.Double/valueOf (str (second match)))
                                          :close (java.lang.Double/valueOf (str (nth match 2)))
                                          :high (java.lang.Double/valueOf (str (nth match 3)))
                                          :low (java.lang.Double/valueOf (str (nth match 4)))
                                          :lClose (java.lang.Double/valueOf (str (nth match 5)))
                                          :volume (java.lang.Long/valueOf (str (nth match 6)))})) lst))))

(defn getStockDailyPrice[code](let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HD&jsname=em_data_DailyK_1404559419762"]))
                                    matcher (re-matcher #"\"\d{8},(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d{1,5}\.\d{1,2}),(\d+?)\"" response)]  
  (loop [match (re-find matcher) lst []]
    (if match 
      (recur (re-find matcher) (conj lst {:open (java.lang.Double/valueOf (str (second match)))
                                          :close (java.lang.Double/valueOf (str (nth match 2)))
                                          :high (java.lang.Double/valueOf (str (nth match 3)))
                                          :low (java.lang.Double/valueOf (str (nth match 4)))
                                          :lClose (java.lang.Double/valueOf (str (nth match 5)))
                                          :volume (java.lang.Long/valueOf (str (nth match 6)))})) lst))))

(defn getStockHourlyPrice[code]
(let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HM60&jsname=em_data_Minute5_1404558145282"]))
      matcher (re-matcher #"\"(\d{8}\d{4}),(\d{1,2}\.\d{1,2}),(\d{1,2}.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d+?)\"" response)
      format2 (SimpleDateFormat. "yyyyMMdd")
      s (.format format2 (Date.))]  
  (loop [match (and (.contains response s) (re-find matcher)) lst []]
    (if match 
            (recur (re-find matcher) (conj lst {:open (java.lang.Double/valueOf (str (nth match 2)))
                                          :close (java.lang.Double/valueOf (str (nth match 3)))
                                          :high (java.lang.Double/valueOf (str (nth match 4)))
                                          :low (java.lang.Double/valueOf (str (nth match 5)))
                                          :lClose (java.lang.Double/valueOf (str (nth match 6)))
                                          :volume (java.lang.Long/valueOf (str (nth match 7)))})) lst))))

(defn getStock15MinutesPrice[code]
(let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HM15&jsname=em_data_Minute15_1404558145282"]))
      matcher (re-matcher #"\"(\d{8}\d{4}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d+?)\"" response)
      format2 (SimpleDateFormat. "yyyyMMdd")
      s (.format format2 (Date.))]  
  (loop [match (and (.contains response s) (re-find matcher)) lst []]
    (if match 
            (recur (re-find matcher) (conj lst {:open (java.lang.Double/valueOf (str (nth match 2)))
                                          :close (java.lang.Double/valueOf (str (nth match 3)))
                                          :high (java.lang.Double/valueOf (str (nth match 4)))
                                          :low (java.lang.Double/valueOf (str (nth match 5)))
                                          :lClose (java.lang.Double/valueOf (str (nth match 6)))
                                          :volume (java.lang.Long/valueOf (str (nth match 7)))})) lst))))

(defn getStock5MinutesPrice[code]
(let [response (HttpXmlClient/get (str/join ["http://183.136.160.2/EM_HTML5/quote.aspx?id=" code "&type=HM5&jsname=em_data_Minute5_1404558145282"]))
      matcher (re-matcher #"\"(\d{8}\d{4}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d{1,2}\.\d{1,2}),(\d+?)\"" response)
      format2 (SimpleDateFormat. "yyyyMMdd")
      s (.format format2 (Date.))]  
  (loop [match (and (.contains response s) (re-find matcher)) lst []]
    (if match 
            (recur (re-find matcher) (conj lst {:open (java.lang.Double/valueOf (str (nth match 2)))
                                          :close (java.lang.Double/valueOf (str (nth match 3)))
                                          :high (java.lang.Double/valueOf (str (nth match 4)))
                                          :low (java.lang.Double/valueOf (str (nth match 5)))
                                          :lClose (java.lang.Double/valueOf (str (nth match 6)))
                                          :volume (java.lang.Long/valueOf (str (nth match 7)))})) lst))))

(def getPrices [getStock5MinutesPrice, getStock15MinutesPrice, getStockHourlyPrice, getStockDailyPrice, getStockWeeklyPrice,getStockMonthlyPrice])

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

(defn getStockSuggestion[code]
(let [response  (HttpXmlClient/get (str/join ["http://www.iwencai.com/stockpick/search?preParams=&ts=1&f=1&qs=1&selfsectsn=&querytype=&searchfilter=&tid=stockpick&w=" (.substring code 0 6) ]))
      result (re-find #"\"(buy|sale)\"" response)
      result (if (nil? result) "" (str (second result)))]result))

;;(defn getAveragePrice [prices n] (/ (reduce + (map (* (/ (reduce + (take-last n prices)) n) )  ) n))
;;(defn getAveragePrice1 [prices n] (/ (reduce + (take-last n prices)) n))

(defn getPriceValue [prices key]
  (map (fn[price](price key))  prices))

(defn getAveragePrice [prices n key] 
  (reduce + 
    (map (fn[i]
           (* (/ (reduce +  (take-last n (take (- (.size prices) i)(getPriceValue prices :volume)))) n) 
              (/ (reduce +  (take-last n (take (- (.size prices) i)(getPriceValue prices key)))) n))) 
         [0 1 2 3 4 5 6 7 8 9 10])))

(defn _getKLineStatus[prices key]
  (let[_threadName (.getName (Thread/currentThread))
      k5b (getAveragePrice prices 5 key)
      k10b (getAveragePrice prices 10 key)
      k30b (getAveragePrice prices 30 key)
      _ (println (str/join [_threadName " k5b:" k5b "k10b:" k10b "k30b:" k30b]))
      _tmp (sort [k5b k10b k30b])
      result (if (= _tmp [k30b k10b k5b])"buy" "")
      result (if (= _tmp [k5b k10b k30b])"sale" result)
      result (if (not (str/blank? result))result "keep")
      ]result))

(defn getKLineStatus[prices]
  (reduce (fn[i,j](if (= i (and (not (= i "keep"))(_getKLineStatus prices j))) i "keep")) [(_getKLineStatus prices :open) :close :high :low]))



(defn getDailyKLineStatus[code](getKLineStatus code getStockDailyPrice ))

(defn get5MinutesKLineStatus[code](getKLineStatus code getStock5MinutesPrice))

;; (defn processKLine[code priceses](let[format2 (SimpleDateFormat."yyyyMMdd HH:mm:ss.SSS") s (.format format2 (Date.))
;;                                       _(println (str/join [s " processKLine:" code]))
;;                                       kLineStatuses (map (fn[prices](eval prices)) priceses)
;;                                       kLineStatus (reduce (fn[m n](if (= m n) m "keep")) kLineStatuses)
;;                                       _(println (str/join [s " kLineStatuses:" (pr-str kLineStatuses)]))
;;                                       _stockSuggestion (getStockSuggestion code)
;;                                       _(println (str/join [s " stockSuggestion:" _stockSuggestion]))]
;;                                    (if (or (.contains _stockSuggestion "sale") (.contains _stockSuggestion kLineStatus))(let [_ (BaiduPushUtil/push_mailToAll (str/join [code " " _stockSuggestion " " s]) "")
;;                                                    _ (println (str/join [s " " code ": " _stockSuggestion "signal sent!"]))]kLineStatus))))
 (defn processStatus[code shortStatuses longStatuses dailyPrices](let[format2 (SimpleDateFormat."yyyyMMdd HH:mm:ss.SSS") s (.format format2 (Date.))
                                       _threadName (.getName (Thread/currentThread))
                                      _(println (str/join [_threadName " " s " processKLine:" code]))
                                      _shortResult (reduce (fn[result status](if (or (not= result "sale") (not= result "keep")) (let [_tmp (eval (read-string status))](if (or (= _tmp "sale") (or (= result "") (= result _tmp))) _tmp "keep")) result )) shortStatuses)
                                       _longResult (reduce (fn[result status](if (or (not= result "sale") (not= result "keep")) (let [_tmp (eval (read-string status))](if (or (= _tmp "sale") (or (= result "") (= result _tmp))) _tmp "keep")) result )) longStatuses)
                                      _(println (str/join [_threadName " " s " _shortResult:" (pr-str _shortResult)]))
                                       _(println (str/join [_threadName " " s " _longResult:" (pr-str _longResult)]))
                                       _K_LINE_STRATEGY {"buybuy" (str/join ["(qinyi.StockBean/sendmail \"" code "\" \"buy\" " dailyPrices ")"])
                                                         "buysale" (str/join ["(qinyi.StockBean/sendmail \"" code "\" \"buy\" " dailyPrices ")"]) 
                                                         "buykeep" (str/join ["(qinyi.StockBean/sendmail \"" code "\" \"buy\" " dailyPrices ")"])
                                                        "salebuy" (str/join ["(qinyi.StockBean/sendmail \"" code "\" \"sale\" " dailyPrices ")"])
                                                         "salekeep" (str/join ["(qinyi.StockBean/sendmail \"" code "\" \"sale\" " dailyPrices ")"])
                                                         "salesale" (str/join ["(qinyi.StockBean/sendmail \"" code "\" \"sale\" " dailyPrices ")"])
                                                         }
                                       _exec (_K_LINE_STRATEGY (str/join [_shortResult _longResult]))
                                       _ (if (nil? _exec) nil (eval (read-string _exec)))]_shortResult))

 (defn sendmail[code shortResult prices](let[_tmp (reverse prices) _lastPrice (first _tmp) _yesterdayPrice (second _tmp)
                                      _threadName (.getName (Thread/currentThread))
                                       format2 (SimpleDateFormat."yyyyMMdd HH:mm:ss.SSS") s (.format format2 (Date.))
								                      _ (BaiduPushUtil/push_mailToAll (str/join [code " " (.floatValue (* 100 (with-precision 3 (/ (- (bigdec _lastPrice) _yesterdayPrice) _yesterdayPrice))) ) "% " shortResult " " s]) "")
								                     _ (println (str/join [_threadName " " s " " code ": " shortResult "signal sent!"]))]))

 ;;(defn getDailyCrossStatus[code stockSuggestion](getCrossStatus code stockSuggestion getStockDailyPrice "daily"))

 ;;(defn get5MinutesCrossStatus[code stockSuggestion](getCrossStatus code stockSuggestion getStock5MinutesPrice "5mins"))

 (defn getBuySale[_STOCK_CODES STOCK_STATUS  value]
  (remove nil? (map (fn[code status](if(= status value) code nil))_STOCK_CODES STOCK_STATUS)))

 (defn getSaleBuy[prices]
   (let [_tmp (reverse prices) _lastPrice (first _tmp) _yesterdayPrice (second _tmp)]
     (if (= _lastPrice _yesterdayPrice) "keep" (if (> _lastPrice _yesterdayPrice) "sale" "buy"))))

 (defn getMarketTrend[code](getKLineStatus (getStockDailyPrice (if (= (.substring code 0 1) "6") "0000011" "3990012"))))

 (defn getCrossStatus [stockPrice]
  (let [dLastestPrice (last stockPrice) 
        dMacd ((getMACD stockPrice 9 26 12) :MACD)
        dOldMacd ((getMACD (if (empty? stockPrice)[](pop stockPrice)) 9 26 12) :MACD)
        deadOrAlive (if (> dMacd dOldMacd) "buy" "sale")
        deadOrAlive (if(and (and (>= dMacd -0.005) (<= dMacd 0.005)) (or (> dOldMacd 0.005) (< dOldMacd -0.005)))deadOrAlive "keep")]deadOrAlive))


 (defn getOutstandingItems[_STOCK_CODES _STOCK_CODES_NEW](let [
       _STOCK_CODES (distinct (concat _STOCK_CODES _STOCK_CODES_NEW))                                                               
       ;;STOCK_SUGGESTIONS (map getStockSuggestion _STOCK_CODES)
       ;;STOCK_CROSS_STATUS (map getDailyCrossStatus _STOCK_CODES STOCK_SUGGESTIONS)]
       STOCK_K_LINE (map (fn [code](processStatus code ["" (str/join["(qinyi.StockBean/getKLineStatus(qinyi.StockBean/getStockDailyPrice \"" code "\"))"]) 
                                                        (str/join["(qinyi.StockBean/getKLineStatus(qinyi.StockBean/getStockWeeklyPrice \"" code "\"))"]) 
                                                        (str/join["(qinyi.StockBean/getKLineStatus(qinyi.StockBean/getStockMonthlyPrice \"" code "\"))"])][""][])) _STOCK_CODES )]
		   {:buy (getBuySale _STOCK_CODES STOCK_K_LINE "buy")
		    :sale (getBuySale _STOCK_CODES STOCK_K_LINE "sale")
		    :keep (getBuySale _STOCK_CODES STOCK_K_LINE "keep")}))

  (defn -processDaily [stockBean]
(let [_ (println "before process")
       _ (println (str/join ["STOCK_CODES: " (pr-str STOCK_CODES)]))
       _ (println (str/join ["STOCK_CODES_NEW: " (pr-str STOCK_CODES_NEW)]))
       _ (println (str/join ["STOCK_CODES_BUY: " (pr-str STOCK_CODES_BUY)]))
       _ (println (str/join ["STOCK_CODES_SELL: " (pr-str STOCK_CODES_SELL)]))
       ;;_ (def STOCK_CODES_NEW (distinct (concat STOCK_CODES_NEW (getNewStocks))))
       _ (future (getOutstandingItems ["0000011" "3990012"] []))
       _pMAP1 (promise)
       _pMAP2 (promise)
       _pMAP3 (promise)
       _ (future (deliver _pMAP1 (getOutstandingItems STOCK_CODES_NEW (getNewStocks))))
       _ (future (deliver _pMAP2 (getOutstandingItems STOCK_CODES [])))
       _ (future (deliver _pMAP3 (getOutstandingItems STOCK_CODES_SELL [])))
       _MAP1 @_pMAP1
       _MAP2 @_pMAP2
       _MAP3 @_pMAP3
       _ (println (str/join ["_MAP1: " _MAP1]))
       _ (println (str/join ["_MAP2: " _MAP2]))
       _ (println (str/join ["_MAP3: " _MAP3]))
       _ (def STOCK_CODES_NEW (_MAP1 :keep))
       _ (def STOCK_CODES (distinct (concat (_MAP2 :keep) (_MAP2 :buy) (_MAP3 :buy) (_MAP1 :buy) [])))      
       _ (def STOCK_CODES_BUY (distinct (concat (_MAP2 :buy) (_MAP3 :buy) (_MAP1 :buy))))
       _ (def STOCK_CODES_SELL (concat (_MAP3 :keep) (_MAP2 :sale)))
       _ (println (str/join ["STOCK_CODES: " (pr-str STOCK_CODES)]))
      _ (println (str/join ["STOCK_CODES_NEW: " (pr-str STOCK_CODES_NEW)]))
      _ (println (str/join ["STOCK_CODES_BUY: " (pr-str STOCK_CODES_BUY)]))
      _ (println (str/join ["STOCK_CODES_SELL: " (pr-str STOCK_CODES_SELL)]))
      _ (println "after process")]))

(defn -process5Minutesly [stockBean]
(let [_ (println "before process")  
       _STOCK_CODES (distinct(concat STOCK_CODES_SELL STOCK_CODES))
       _ (doseq [x _STOCK_CODES](let[y (getStockDailyPrice x)](future (processStatus x ["" (str/join["(qinyi.StockBean/getSaleBuy " y ")"])
                                                                 (str/join["(qinyi.StockBean/getStockSuggestion \"" x "\")"]) 
                                                                 (str/join["(qinyi.StockBean/getKLineStatus(qinyi.StockBean/getStock5MinutesPrice \"" x "\"))"]) 
                                                                 (str/join["(qinyi.StockBean/getKLineStatus(qinyi.StockBean/getStock15MinutesPrice \"" x "\"))"]) 
                                                                 (str/join["(qinyi.StockBean/getKLineStatus(qinyi.StockBean/getStockHourlyPrice \"" x "\"))"])] 
                                                              ["" (str/join["(qinyi.StockBean/getKLineStatus " y ")"])] y))))
      _ (println "after process")]))
