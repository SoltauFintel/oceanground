cls
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_181\jre
call gradlew.bat -g D:\jarcache\gradle -Ptarget=base dockerPushImage
call gradlew.bat -g D:\jarcache\gradle -Ptarget=app  dockerPushImage
