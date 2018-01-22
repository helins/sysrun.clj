(ns dvlopt.sysrun

  "Miscellaneous system utilities."

  {:author "Adam Helinski"})




;;;;;;;;;; Private


(def ^:private ^Runtime -runtime

  "java.lang.Runtime"

  (Runtime/getRuntime))




;;;;;;;;;; Data


(def line-separator

  "Current system-dependant line separator."

  (System/lineSeparator))




;;;;;;;;;; Functions


(defn timestamp

  "Current unix time in milliseconds.
  
   Depends on the system time. Not necessarely monotonically increasing.
  
   Only as accurate as the underlying operating system, often measuring time in units of 10's of milliseconds."

  []

  (System/currentTimeMillis))




(defn nano-timing

  "Current time expressed in nanoseconds relative to an arbitratry origin defined by the JVM.

   Deltas of values at different times provides high resolution timing.

   Nanosecond resolution does NOT mean nanosecond accuracy.
  
   Accuracy is at least as good as that of `timestamp`, often better."

  []

  (System/nanoTime))




(defn env

  "Gets environment variables or one of them.
  

   Throws
  
     java.lang.SecurityException
       When not allowed."

  ([]

   (into {}
         (System/getenv)))


  ([variable]

   (when variable
     (System/getenv variable))))




(defn- -property?

  "Is `property` a valid property string ?"

  [property]

  (boolean (and property
                (not-empty property))))




(defn property

  "Gets, sets or removes (if `value` is nil) system properties.
  
  
   Throws
  
     java.lang.SecurityException
       When not allowed."

  ([]

   (into {}
         (System/getProperties)))


  ([property]

   (when (-property? property)
     (System/getProperty property)))


  ([property value]

   (when (-property? property)
     (if value
       (System/setProperty property
                           value)
       (System/clearProperty property)))))




(defn processors

  "Number of available processors available to the JVM.
  
   Might change during a particular invocation of the JVM."

  []

  (.availableProcessors -runtime))




(defn memory-allocated

  "Amound of memory currently allocated by the JVM, in bytes."

  []

  (.totalMemory -runtime))




(defn memory-allocated-free

  "Approximately the amount of free memory available relative to what is currently allocated by the JVM, in bytes."

  []

  (.freeMemory -runtime))




(defn memory-used

  "Approximately the amount of memory currently used, in bytes."

  []

  (- (memory-allocated)
     (memory-allocated-free)))




(defn memory-limit

  "Maximum amount of memory the JVM will attempt to use in total, in bytes."

  []

  (.maxMemory -runtime))




(defn memory-free

  "Approximately the amount of available free memory the JVM can claim at most, right now, in bytes."

  []

  (- (memory-limit)
     (memory-used)))




(defn finalize

  "Suggests the finalization of discarded objects not yet finalized.

   The JVM makes the best effort to do it before returning."

  []

  (.runFinalization -runtime))




(defn garbage-collection

  "Suggests to run garbarge collection.

   The JVM makes the best effort to do it before returning."

  []

  (.gc -runtime))




(defn native-name

  "Depending on the OS, native library naming usually follow a convention.

   Given a generic name, returns a platform-specific name.

   Ex. On linux, \"c\" => \"libc.so\""

  [lib-name]

  (when lib-name
    (System/mapLibraryName lib-name)))




(defn on-shutdown

  "Registers an action (ie. no-arg function) to execute on JVM shutdown.
  
   Returns a no-arg function for cancelling the registration.
  
  
   Throws
  
     java.lang.SecurityException
       When not allowed."

  [^Runnable action]

  (let [thread (Thread. action)]
    (try
      (.addShutdownHook -runtime
                        thread)
      (fn cancel-action []
        (try
          (.removeShutdownHook -runtime
                               thread)
          (catch Exception _
            nil))
        nil)
      (catch IllegalStateException _
        (fn cancel-action []
          nil)))))




(defn shutdown

  "Terminates the JVM.

   Before halting the process, runs shutdown hooks.


   Throws

     java.lang.SecurityException
       When not allowed.

   Cf. `on-shutdown`"

  ([]

   (shutdown 1))


  ([status]

   (.exit -runtime
          status)))
