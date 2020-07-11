(ns lwjgl-example.core
  (:import [org.lwjgl Version]
           [org.lwjgl.glfw GLFWErrorCallback])
  (:gen-class))

(defn init! []
  ;; Setup an error callback. The default implementation
  ;; will print the error message in System.err.
  (-> System/err
      GLFWErrorCallback/createPrint
      .set))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (str "Hello LWGL " (Version/getVersion) "!"))

  (let [window (init!)]


    )
  )
