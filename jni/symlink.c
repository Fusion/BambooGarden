#include <jni.h>
#include <stddef.h>


jint Java_com_voilaweb_mobile_bamboogarden_RootHelper_nativeCreateSymLink(
        JNIEnv* env,
        jobject jThis,
        jstring jSrcPath,
        jstring jDstPath)
{
    const char *srcPath = (*env)->GetStringUTFChars(env, jSrcPath, NULL);
    const char *dstPath = (*env)->GetStringUTFChars(env, jDstPath, NULL);

    int symd = symlink(srcPath, dstPath);

    (*env)->ReleaseStringUTFChars(env, jSrcPath, srcPath);
    (*env)->ReleaseStringUTFChars(env, jDstPath, dstPath);

    return symd;
} 
