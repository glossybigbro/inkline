# Inkline — Consumer ProGuard/R8 Rules
# AAR에 포함되어 소비자 앱의 릴리즈 빌드에 자동 적용된다.

-keep class io.github.glossybigbro.inkline.Inkline { public *; }
-keep class io.github.glossybigbro.inkline.UnderlineConfig { *; }
-keep enum io.github.glossybigbro.inkline.InklineStyle { *; }
-keep interface io.github.glossybigbro.inkline.InklineScope { *; }
-keep class io.github.glossybigbro.inkline.RememberInklineKt { *; }
-keep class io.github.glossybigbro.inkline.DrawBehindExtensionKt { *; }
