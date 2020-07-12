(ns lwjgl-example.core
  (:import [org.lwjgl Version]
           [org.lwjgl.glfw GLFW GLFWErrorCallback GLFWKeyCallback Callbacks]
           [org.lwjgl.system MemoryStack]
           [org.lwjgl.opengl GL GL11]
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
           ))))

    (with-open [stack (MemoryStack/stackPush)]
      (let [*width (.mallocInt stack 1)
            *height (.mallocInt stack 1)]
        ;; Get the window size passed to glfwCreateWindow
        (GLFW/glfwGetWindowSize window *width *height)

        ;; Get the resolution of the primary monitor
        (let [video-mode (GLFW/glfwGetVideoMode (GLFW/glfwGetPrimaryMonitor))]
          ;; Center the window
          (GLFW/glfwSetWindowPos
           window
           (-> video-mode .width
               (- (.get *width 0))
               (/ 2))
           (-> video-mode .height
               (- (.get *height 0))
               (/ 2))))

        ;; Make the OpenGL context current
        (GLFW/glfwMakeContextCurrent window)
        ;; Enable v-sync
        (GLFW/glfwSwapInterval 1)

        ;; Make the window visible
        (GLFW/glfwShowWindow window)))
    window))

(defn main-loop [window]
  ;; This line is critical for LWJGL's interoperation with GLFW's
  ;; OpenGL context, or any context that is managed externally.
  ;; LWJGL detects the context that is current in the current thread,
  ;; creates the GLCapabilities instance and makes the OpenGL
  ;; bindings available for use.
  (GL/createCapabilities)

  ;; Set the clear color
  (GL11/glClearColor 1.0 0.0 0.0 0.0)

  ;; Run the rendering loop until the user has attempted to close
  ;; the window or has pressed the ESCAPE key.
  (while (not (GLFW/glfwWindowShouldClose window))
    (GL11/glClear (bit-or GL11/GL_COLOR_BUFFER_BIT GL11/GL_DEPTH_BUFFER_BIT)) ;; clear the framebuffer

    (GLFW/glfwSwapBuffers window) ;; swap the color buffers

    ;; Poll for window events. The key callback above will only be
    ;; invoked during this call.
    (GLFW/glfwPollEvents)))

(defn -main
  "A port of the java example here: https://www.lwjgl.org/guide"
  [& args]
  (println "loading...")
  (clojure.lang.RT/loadLibrary "lwjgl")
  (clojure.lang.RT/loadLibrary "lwjgl_opengl")
  (clojure.lang.RT/loadLibrary "glfw")
  (println (str "Hello LWGL " (Version/getVersion) "!"))

  (let [window (init!)]
    (main-loop window)

    ;; Free the window callbacks and destroy the window
    (Callbacks/glfwFreeCallbacks window)
    (GLFW/glfwDestroyWindow window)

    ;; Terminate GLFW and free the error callback
    (GLFW/glfwTerminate)
    (.free
     (GLFW/glfwSetErrorCallback nil))))
