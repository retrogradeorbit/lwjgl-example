(ns lwjgl-example.core
  (:import [org.lwjgl Version]
           [org.lwjgl.glfw GLFW GLFWErrorCallback GLFWKeyCallback]
           [org.lwjgl.system.MemoryStack]
           )
  (:gen-class))

(defn init! []
  ;; Setup an error callback. The default implementation
  ;; will print the error message in System.err.
  (-> System/err
      GLFWErrorCallback/createPrint
      .set)

  ;; Initialize GLFW. Most GLFW functions will not work before doing this.
  (when-not (GLFW/glfwInit)
    (throw (ex-info "Unable to initialize GLFW")))

  ;; Configure GLFW
  (GLFW/glfwDefaultWindowHints) ;; optional, the current window hints are already the default
  (GLFW/glfwWindowHint GLFW/GLFW_VISIBLE GLFW/GLFW_FALSE) ;; the window will stay hidden after creation
  (GLFW/glfwWindowHint GLFW/GLFW_RESIZABLE GLFW/GLFW_TRUE) ;; the window will be resizable

  ;; Create the window
  (let [window (GLFW/glfwCreateWindow 300 300 "Hello World!" 0 0)]
    (when-not window
      (throw (ex-info "Failed to create the GLFW window")))

    ;; Setup a key callback. It will be called every time a key is pressed, repeated or released.
    (GLFW/glfwSetKeyCallback
     window
     (proxy [GLFWKeyCallback] []
       (invoke [window key scancode action mods]
         (when (and (= key GLFW/GLFW_KEY_ESCAPE)
                    (= action GLFW/GLFW_RELEASE))
           (GLFW/glfwSetWindowShouldClose window true) ;; We will detect this in the rendering loop

           )
         )
       ))




    )



  )



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (str "Hello LWGL " (Version/getVersion) "!"))

  (let [window (init!)]


    )
  )
