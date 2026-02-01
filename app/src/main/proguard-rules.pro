# ---------------------------------------------------------
# CONFIGURACIÓN DE SEGURIDAD Y OFUSCACIÓN (AppLock)
# ---------------------------------------------------------

-optimizationpasses 5
-allowaccessmodification
-dontpreverify
-repackageclasses ''

# 1. ESENCIAL: Mantener el Servicio de Accesibilidad
# Si ProGuard cambia el nombre de la clase, el sistema no la encuentra
-keep public class * extends android.accessibilityservice.AccessibilityService
-keep class com.tuapp.calculadora.AppLockService { *; }
-keep class com.tuapp.calculadora.LockActivity { *; }

# 2. Mantener las clases base de Android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Service
-keep public class * extends android.app.Application
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.view.View { *; }

# 3. Librería Matemática (exp4j)
-keep class net.objecthunter.exp4j.** { *; }

# 4. Glide (Imágenes y Video)
-keep public class com.github.bumptech.glide.** { *; }
-keep class com.github.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder { *** rewind(); }

# 5. Crypto y Seguridad (Importante para que no falle el PIN guardado)
-keep class androidx.security.crypto.** { *; }
-keep class com.google.crypto.tink.** { *; }

# 6. Gson (Si guardas listas de apps bloqueadas)
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# 7. Mantener nombres de recursos y IDs
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 8. Eliminar logs de depuración (Para que sea más difícil de rastrear por hackers)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}
