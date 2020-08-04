#include <stdio.h>
#include <pthread.h>
#define TEST(x) test(x, #x)
#include "myfilesystem.h"

/* You are free to modify any part of this file. The only requirement is that when it is run, all your tests are automatically executed */

/* Some example unit test functions */
pthread_t threads[2];
int success() {
    return 0;
}

int failure() {
    return 1;
}

int no_operation() {
    void * helper = init_fs("file1.bin", "file2.bin", "file3.bin", 1); // Remember you need to provide your own test files and also check their contents as part of testing
    close_fs(helper);
    return 0;
}
int create_resize_write_then_repack_combo(){
    void * helper = init_fs("filedata1.bin", "directory1.bin", "hash1.bin", 1);
    char buf[4]="abc";
    int ret1=create_file("pizza",4,helper);
    int ret2=resize_file("pizza",10,helper);
    int ret3=write_file("pizza",0,4,buf,helper);
    repack(helper);
    close_fs(helper);
    if(ret1==0&&ret2==0&&ret3==0){
        return 0;
    }else{
        return 1;
    }
}
int write_offset_larger_than_length(){
    void * helper = init_fs("filedata1.bin", "directory1.bin", "hash1.bin", 1);
    char buf[4]="abc";
    int ret1=write_file("answer.docx",20,4,buf,helper);
    close_fs(helper);
    if(ret1==2){
        return 0;
    }else{
        return 1;
    }
}

int elementry_operations(){
    void * helper = init_fs("filedata1.bin", "directory1.bin", "hash1.bin", 1);
    int ret1=delete_file("nonexist.txt",helper);
    int ret2=file_size("answer.docx",helper);
    int ret3=rename_file("fu.docx","newone.txt",helper);
    close_fs(helper);
    if(ret1==1&&ret2==15&&ret3==0){
        return 0;
    }else{
        return 1;
    }
}
int verify_hash_value_and_read(){
    void * helper = init_fs("filedata1.bin", "directory1.bin", "wrong_hash.bin", 1);
    char buf[5];
    int ret1=read_file("answer.docx",5,5,buf,helper);
    compute_hash_tree(helper);
    int ret2=read_file("answer.docx",5,5,buf,helper);
    close_fs(helper);
    if(ret1==3&&ret2==0){
        return 0;
    }else{
        return 1;
    }
}

/****************************/

/* Helper function */
void test(int (*test_function) (), char * function_name) {
    int ret = test_function();
    if (ret == 0) {
        printf("Passed %s\n", function_name);
    } else {
        printf("Failed %s returned %d\n", function_name, ret);
    }
}

/************************/

int main(int argc, char * argv[]) {
    // You can use the TEST macro as TEST(x) to run a test function named "x"
    TEST(success);
    TEST(failure);
    TEST(no_operation);
    TEST(create_resize_write_then_repack_combo);
    TEST(write_offset_larger_than_length);
    TEST(elementry_operations);
    TEST(verify_hash_value_and_read);
    // Add more tests here

    return 0;
}
