# Keep kotlinx.serialization serializers for the app models.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

-keepclassmembers class com.albertolicea00.myussdcodes.** {
    *** Companion;
}
-keepclasseswithmembers class com.albertolicea00.myussdcodes.** {
    kotlinx.serialization.KSerializer serializer(...);
}
