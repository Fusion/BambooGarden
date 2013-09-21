#include <jni.h>
#include <stddef.h>


jint Java_com_voilaweb_mobile_bamboogarden_RootHelper_nativeChownerId(
        JNIEnv* env,
        jobject jThis,
        jstring jFilePath,
        jint jUid)
{
    int ret = 0;
    const char *filePath = (*env)->GetStringUTFChars(env, jFilePath, NULL);

    if(chown(filePath, jUid, jUid) < 0) {
        ret = -1;
    }

    (*env)->ReleaseStringUTFChars(env, jFilePath, filePath);

    return ret;
} 
