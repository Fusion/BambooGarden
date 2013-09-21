#include <jni.h>
#include <stddef.h>
#include <sys/stat.h>


jint Java_com_voilaweb_mobile_bamboogarden_RootHelper_nativeGetOwnerId(
        JNIEnv* env,
        jobject jThis,
        jstring jFilePath)
{
    int ret = -1;
    const char *filePath = (*env)->GetStringUTFChars(env, jFilePath, NULL);

    struct stat fStat;
    if(stat(filePath, &fStat) >= 0) {
        ret = fStat.st_uid;
    }

    (*env)->ReleaseStringUTFChars(env, jFilePath, filePath);

    return ret;
} 
