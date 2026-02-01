# Ofuscación para proteger la lógica del AppLock
-optimizationpasses 5
-allowaccessmodification
-dontpreverify

# Evita que borre las clases de Android básicas
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Protege la librería de la calculadora (exp4j)
-keep class net.objecthunter.exp4j.** { *; }

# Protege Glide para que las fotos se sigan viendo
-keep public class com.github.bumptech.glide.** { *; }

# NO tocar las clases que usa el sistema de accesibilidad
-keep class com.tuapp.calculadora.AppLockService { *; }
-keep class com.tuapp.calculadora.LockActivity { *; }

# Mantener nombres de recursos necesarios
-keepclassmembers class **.R$* {
    public static <fields>;
}
