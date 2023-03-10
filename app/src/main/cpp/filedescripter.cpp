#include <jni.h>
#include <string>
#include "stdio.h"
#include <unordered_map>
#include <iosfwd>
#include <sstream>
#include <regex>

using namespace std;

const char MAP_DELI = ';';
const char PAIR_DELI = ':';


std::string jstring2string(JNIEnv *env, jstring jStr) {

    const jclass stringClass = env->GetObjectClass(jStr);
    const jmethodID getBytes = env->GetMethodID(stringClass, "getBytes", "(Ljava/lang/String;)[B");
    const jbyteArray stringJbytes = (jbyteArray) env->CallObjectMethod(jStr, getBytes, env->NewStringUTF("UTF-8"));

    size_t length = (size_t) env->GetArrayLength(stringJbytes);
    jbyte* pBytes = env->GetByteArrayElements(stringJbytes, NULL);

    std::string ret = std::string((char *)pBytes, length);
    env->ReleaseByteArrayElements(stringJbytes, pBytes, JNI_ABORT);

    env->DeleteLocalRef(stringJbytes);
    env->DeleteLocalRef(stringClass);

    return ret;
}

void serializeMap(unordered_map<string, long> &mapping, string &str) {
    for (auto itr: mapping) {
        str += (itr.first + PAIR_DELI + to_string(itr.second) + MAP_DELI);
    }
}

void getMappingFromString(string cppStr, unordered_map<string, long> &mapping) {
    stringstream ss1(cppStr);
    string pairing;
    while (!ss1.eof()) {
        getline(ss1, pairing, MAP_DELI);
        if (pairing.empty())
            return;
        stringstream ss2(pairing);
        string type, size;
        while(!ss2.eof()) {
            getline(ss2, type, PAIR_DELI);
            getline(ss2, size, PAIR_DELI);
            long sz = stol(size);
            mapping[type] += sz;
        }
    }
}

void doAnalysis(std::string &cppStr, string &results) {
    unordered_map<string, long> mapping;
    getMappingFromString(cppStr, mapping);
    serializeMap(mapping, results);
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_example_filedescripter_Services_CppHelper_getAnalysisFromCpp(JNIEnv *env, jobject thiz,
                                                                      jstring str) {
    string cppStr = jstring2string(env, str);
    string results;

    doAnalysis(cppStr, results);
    results.pop_back();
    int len = results.length();
    char arr[len];

    strcpy(arr, results.c_str());

    return env->NewStringUTF(arr);
}