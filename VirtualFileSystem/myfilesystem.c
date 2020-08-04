#include<stdlib.h>
#include<string.h>
#include<stdio.h>
#include<stdint.h>
#include<unistd.h>
#include<fcntl.h>
#include<sys/types.h>
#include<pthread.h>
#define MAXREC 65536
#include "myfilesystem.h"
#include<math.h>
typedef struct head{
	FILE *file;
	FILE *directory;
	FILE *hash;
}head;
typedef struct btnode{
	uint8_t *hash_data;
	struct btnode *leftchild;
	struct btnode *rightchild;
}btnode;
int no_of_file;
uint32_t offandlen[MAXREC][3];
uint32_t sequence[MAXREC];
uint32_t position[MAXREC];
uint32_t copy[MAXREC][3];
uint32_t tooriginal[MAXREC][3];
pthread_mutex_t mutex;
/*extract the offset and length of each file and sort it according to offset's order, then
 compute the offset after the repack, all of these data are stored in global variable */
void computeoffandlen(void *helper,char *name){
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	int len=0;
	int off=0;
	uint32_t pos=0;
	char file_name[64];
	no_of_file=0;
	//if name==NULL then extract all the file's data and do works, otherwise skip the file that matches our input
	if(name==NULL){
		while(!feof(directory)){
		fread(file_name,sizeof(char),64,directory);
		if(strcmp(file_name,"\0")!=0){
			fread(&off,sizeof(int),1,directory);
			fread(&len,sizeof(int),1,directory);
			offandlen[no_of_file][0]=off;
			offandlen[no_of_file][1]=len;
			offandlen[no_of_file][2]=no_of_file;
			sequence[no_of_file]=no_of_file;
			position[no_of_file]=pos;
			no_of_file++; 
		}else{
			fread(&off,sizeof(uint32_t),1,directory);
			fread(&len,sizeof(uint32_t),1,directory);
		}
		pos+=72;
	}
		rewind(directory);
	}else{
		while(!feof(directory)){
		//extract offset and length into variables
		fread(file_name,sizeof(char),64,directory);
		if(strcmp(file_name,"\0")!=0){
			if(strcmp(file_name,name)==0){
				fread(&off,sizeof(uint32_t),1,directory);
				fread(&len,sizeof(uint32_t),1,directory);
				pos+=72;
				continue;
			}
			fread(&off,sizeof(uint32_t),1,directory);
			fread(&len,sizeof(uint32_t),1,directory);
			offandlen[no_of_file][0]=off;
			offandlen[no_of_file][1]=len;
			offandlen[no_of_file][2]=no_of_file;
			sequence[no_of_file]=no_of_file;
			position[no_of_file]=pos;
			no_of_file++; 
		}else{
			fread(&off,sizeof(uint32_t),1,directory);
			fread(&len,sizeof(uint32_t),1,directory);
		}
		pos+=72;
	}
	rewind(directory);
	}
	//rewind(directory);
	//sort the table in increasing order using bubble sort
	for(int j=0;j<no_of_file-1;j++){
		for(int k=0;k<no_of_file-j-1;k++){
			if(offandlen[k][0]>offandlen[k+1][0]){
				int of= offandlen[k][0];
				int le= offandlen[k][1];
				int no= offandlen[k][2];
				offandlen[k][0]=offandlen[k+1][0];
				offandlen[k][1]=offandlen[k+1][1];
				offandlen[k][2]=offandlen[k+1][2];
				offandlen[k+1][0]=of;
				offandlen[k+1][1]=le;
				offandlen[k+1][2]=no;
			}
		}
	}
	// create a new table that represents offset and length after repack
	for(int j=0;j<no_of_file;j++){
		copy[j][0]=0;
		copy[j][1]=offandlen[j][1];
		copy[j][2]=offandlen[j][2];
	}
	for(int j=0;j<no_of_file-1;j++){
		copy[j+1][0]+=copy[j][1]+copy[j][0];
	}
	//sort the new table back into the order the same as directory
	for(int j=0;j<no_of_file;j++){
		for(int k=0;k<no_of_file;k++){
			if(copy[k][2]==sequence[j]){
				tooriginal[j][0]=copy[k][0];
				tooriginal[j][1]=copy[k][1];
				tooriginal[j][2]=copy[k][2];
			}
		}
	}
}
void * init_fs(char * f1, char * f2, char * f3, int n_processors) {
	head *first=malloc(sizeof(head));
	FILE *file_data=fopen(f1,"rb+");
	FILE *directory_data=fopen(f2,"rb+");
	FILE *hash_data=fopen(f3,"rb+");
	first->file=file_data;
	first->directory=directory_data;
	first->hash=hash_data;
    return first;
}
void close_fs(void * helper) {
	head *he=(head *)helper;
	fclose(he->file);
	fclose(he->directory);
	fclose(he->hash);
	free(helper);
    return;
}

int create_file(char * filename, size_t length, void * helper) {
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	FILE *file=nodes->file;
	fseek(file,0,SEEK_END);
	int all=ftell(file);
	rewind(file);
	uint32_t offset;
	uint32_t len;
	//check if there exists a file name that is the same as the input file name
	while(!feof(directory)){
		char file_name[64];
		fread(file_name,sizeof(char),64,directory);
		fread(&offset,sizeof(uint32_t),1,directory);
		fread(&len,sizeof(uint32_t),1,directory);
		if(strcmp(file_name,filename)==0){
			return 1;
		}
	}
	rewind(directory);
	computeoffandlen(helper,NULL);
	int newoff=0;
	int space=0;
	int counter=0;
	/*do some work to calculate the unallocated spaces in increasing offset order and
	 compare it to the file length, if there exists a space that can hold the length, set the newoffset
	 to the start of that space*/
	for(int j=0;j<no_of_file;j++){
		if(j==0){
			if(offandlen[j][0]>=length){
				newoff=0;
				counter++;
				break;
			}
		}else{
			space=offandlen[j][0]-(offandlen[j-1][0]+offandlen[j-1][1]);
			if(space>=length){
				newoff=offandlen[j-1][0]+offandlen[j-1][1];
				counter++;
				break;
			}
		}
		if(j==no_of_file-1){
			space=all-(offandlen[j][0]+offandlen[j][1]);
			if(space>=length){
				newoff=offandlen[j][0]+offandlen[j][1];
				counter++;
				break;
			}
		}
		
	}
	//if offset is not set and nothing changed, repack the file_data and set new offset
	if(newoff==0&&counter==0&&no_of_file!=0){
		repack(helper);
		for(int j=0;j<no_of_file;j++){
			newoff+=offandlen[j][1];
		}
	}
	//write the newoffset and length into first available slot of direcotry table
	while(!feof(directory)){
		char file_name[64];
		fread(file_name,sizeof(char),64,directory);
		fread(&offset,sizeof(uint32_t),1,directory);
		fread(&len,sizeof(uint32_t),1,directory);
		if(strcmp(file_name,"\0")==0){
			fseek(directory,-72,SEEK_CUR);
			fwrite(filename,sizeof(char),strlen(filename),directory);			
			fseek(directory,64-strlen(filename),SEEK_CUR);
			fwrite(&newoff,sizeof(uint32_t),1,directory);
			fwrite(&length,sizeof(uint32_t),1,directory);
			fflush(directory);
			fflush(file);
			return 0;
		}
	}
	fflush(file);
	fflush(directory);
	rewind(directory);
	rewind(file);
    return 2;
}

int resize_file(char * filename, size_t length, void * helper) {
	uint32_t offset;
	uint32_t len;
	char file_name[64];
	int newoff=0;
	int space=0;
	head *nodes=(head *)helper;
	FILE *file=nodes->file;
	FILE *directory=nodes->directory;
	fseek(file,0,SEEK_END);
	int b=ftell(file);
	rewind(file);
	if(length>b){
		return 2;
	}
	rewind(file);
	computeoffandlen(helper,NULL);
	//calculate the space between the selected filename and the file forward
	while(!feof(directory)){
		fread(file_name,sizeof(char),64,directory);
		fread(&offset,sizeof(uint32_t),1,directory);
		fread(&len,sizeof(uint32_t),1,directory);
		if(strcmp(file_name,filename)==0){
			for(int j=0;j<no_of_file;j++){
				if(j!=no_of_file-1){
					if(offandlen[j][0]==offset&&offandlen[j][1]==len){
						space=offandlen[j+1][0]-(offandlen[j][0]+offandlen[j][1]);
						break;
					}
			}else{
				space=b-(offandlen[j][0]+offandlen[j][1]);
				break;
				}
			}
			break;
			}
		}
	rewind(directory);
	if(length>len){
		if(space<length-len){
			char data[len];
			char tofill[len];
			fseek(file,offset,SEEK_SET);
			fread(data,sizeof(char),len,file);
			memset(tofill,0,len);
			fseek(file,-len,SEEK_CUR);
			for(int i=0;i<len;i++){
				fwrite(&tofill[i],sizeof(char),1,file);
			}
			rewind(file);
			computeoffandlen(helper,file_name);
			repack(helper);
			for(int j=0;j<no_of_file;j++){
				newoff+=offandlen[j][1];
			}
			fseek(file,newoff,SEEK_SET);
			fwrite(data,sizeof(char),len,file);
			while(!feof(directory)){
				fread(file_name,sizeof(char),64,directory);
				fread(&offset,sizeof(uint32_t),1,directory);
				fread(&len,sizeof(uint32_t),1,directory);
				if(strcmp(file_name,filename)==0){
					fseek(directory,-8,SEEK_CUR);
					fwrite(&newoff,sizeof(uint32_t),1,directory);
					fwrite(&length,sizeof(uint32_t),1,directory);
					rewind(directory);
					fflush(directory);
					fflush(file);
					compute_hash_tree(helper);
					return 0;
				}
			}
		}else{
			while(!feof(directory)){
				fread(file_name,sizeof(char),64,directory);
				fread(&offset,sizeof(uint32_t),1,directory);
				fread(&len,sizeof(uint32_t),1,directory);
				if(strcmp(file_name,filename)==0){
					fseek(directory,-4,SEEK_CUR);
					fwrite(&length,sizeof(uint32_t),1,directory);
					rewind(directory);
					fflush(directory);
					fflush(file);
					compute_hash_tree(helper);
					return 0;
				}
			}
		}
	}else if(length<=len){
		fseek(file,offset+length,SEEK_SET);
		char tofill[length];
		memset(tofill,0,length);
		for(int i=0;i<length;i++){
			fwrite(&tofill[i],sizeof(char),1,file);
		}
		while(!feof(directory)){
				fread(file_name,sizeof(char),64,directory);
				fread(&offset,sizeof(uint32_t),1,directory);
				fread(&len,sizeof(uint32_t),1,directory);
				if(strcmp(file_name,filename)==0){
					fseek(directory,-4,SEEK_CUR);
					fwrite(&length,sizeof(uint32_t),1,directory);
					rewind(directory);
					fflush(directory);
					fflush(file);
					compute_hash_tree(helper);
					return 0;
				}
			}
		
	}
	return 1;
}

void repack(void * helper) {
	head *nodes=(head *)helper;
	FILE *file=nodes->file;
	FILE *directory=nodes->directory;
	if(no_of_file==0){
		computeoffandlen(helper,NULL);
	}
	int total=0;
	for(int j=0;j<no_of_file;j++){
		char data[offandlen[j][1]];
		fseek(file,offandlen[j][0],SEEK_SET);	
		fread(data,sizeof(char),offandlen[j][1],file);
		fseek(file,copy[j][0],SEEK_SET);
		fwrite(data,sizeof(char),offandlen[j][1],file);
		total+=offandlen[j][1];
	}
	fflush(file);
	fseek(file,0,SEEK_END);
	int all=ftell(file);
	rewind(file);
	char tofill[all-total];
	memset(tofill,0,all-total);
	fseek(file,total,SEEK_SET);
	for(int i=0;i<all-total;i++){
		fwrite(&tofill[i],sizeof(char),1,file);
	}
	fflush(file);
	rewind(file);
	rewind(directory);
	for(int j=0;j<no_of_file;j++){
		fseek(directory,position[j]+64,SEEK_CUR);
		fwrite(&tooriginal[j][0],sizeof(uint32_t),1,directory);
		rewind(directory);
	}
	rewind(directory);
	fflush(directory);
	compute_hash_tree(helper);
    return;
}

int delete_file(char * filename, void * helper) {
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	uint32_t offset;
	uint32_t length;
	while(!feof(directory)){
		char file_name[64];
		fread(file_name,sizeof(char),64,directory);
		fread(&offset,sizeof(uint32_t),1,directory);
		fread(&length,sizeof(uint32_t),1,directory);
		if(strcmp(file_name,filename)==0){
			file_name[0]='\0';
			fseek(directory,-72,SEEK_CUR);
			fwrite(file_name,sizeof(char),64,directory);
			rewind(directory);
			return 0;
		}
	}
	rewind(directory);
    return 1;
}

int rename_file(char * oldname, char * newname, void * helper) {
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	while(!feof(directory)){
		char file_name[64];
		fread(file_name,sizeof(char),64,directory);
		fseek(directory,8,SEEK_CUR);
		if(strcmp(file_name,newname)==0){
			rewind(directory);
			return 1;
		}
		if(strcmp(file_name,oldname)==0){
			fseek(directory,-72,SEEK_CUR);
			fwrite(newname,sizeof(char),strlen(newname),directory);
			rewind(directory);
			return 0;
		}
	}
	rewind(directory);
    return 1;
}

int read_file(char * filename, size_t offset, size_t count, void * buf, void * helper) {
	uint32_t offsets;
	uint32_t length;
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	FILE *file=nodes->file;
	FILE *hash=nodes->hash;
	fseek(hash,0,SEEK_END);
	int hash_block=ftell(hash)/16;
	rewind(hash);
	uint8_t hash_data[hash_block][16];
	uint8_t new[hash_block][16];
	for(int i=0;i<hash_block;i++){
		fread(hash_data[i],sizeof(uint8_t),16,hash);
	}
	rewind(hash);
	compute_hash_tree(helper);
	for(int i=0;i<hash_block;i++){
		fread(new[i],sizeof(uint8_t),16,hash);
	}
	for(int k=0;k<hash_block;k++){
		for(int h=0;h<16;h++){
			if(hash_data[k][h]!=new[k][h]){
				return 3;
			}
		}
	}
	while(!feof(directory)){
		char file_name[64];
		fread(file_name,sizeof(char),64,directory);
		fread(&offsets,sizeof(uint32_t),1,directory);
		fread(&length,sizeof(uint32_t),1,directory);
		if(strcmp(filename,file_name)==0){
			if(length-offset<count){
				rewind(directory);
				return 2;
			}
			fseek(file,offsets+offset,SEEK_SET);
			fread((char *)buf,sizeof(char),count,file);
			return 0;
		}
	}
	rewind(directory);
    return 1;
}

int write_file(char * filename, size_t offset, size_t count, void * buf, void * helper) {
	uint32_t offsets;
	uint32_t len;
	char file_name[64];
	int space=0;
	int counter=0;
	int newoff=0;
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	FILE *file=nodes->file;
	fseek(file,0,SEEK_END);
	int b=ftell(file);
	rewind(file);
	computeoffandlen(helper,NULL);
	pthread_mutex_lock(&mutex);
	while(!feof(directory)){
		//extract offset and length into variables
		fread(file_name,sizeof(char),64,directory);
		fread(&offsets,sizeof(uint32_t),1,directory);
		fread(&len,sizeof(uint32_t),1,directory);
		if(strcmp(file_name,filename)==0){
			counter++;
			for(int j=0;j<no_of_file;j++){
				if(j!=no_of_file-1){
					if(offandlen[j][0]==offset&&offandlen[j][1]==len){
						space=offandlen[j+1][0]-(offandlen[j][0]+offandlen[j][1]);
						break;
					}
			}else{
				space=b-(offandlen[j][0]+offandlen[j][1]);
				break;
				}
			}
			break;
			}
		}
	rewind(directory);
	if(offset>len){
		pthread_mutex_unlock(&mutex);
		return 2;
	}
	if(counter==0){
		pthread_mutex_unlock(&mutex);
		return 1;
	}
	int total=count+offset;
	int sp=count-(len-offset);
	if(space<sp){
		char data[len];
		char tofill[len];
		fseek(file,offset,SEEK_SET);
		fread(data,sizeof(char),len,file);
		memset(tofill,0,len);
		fseek(file,-len,SEEK_CUR);
		for(int i=0;i<len;i++){
			fwrite(&tofill[i],sizeof(char),1,file);
		}
		rewind(file);
		computeoffandlen(helper,filename);
		repack(helper);
		for(int j=0;j<no_of_file;j++){
			newoff+=offandlen[j][1];
		}
	while(!feof(directory)){
		fread(file_name,sizeof(char),64,directory);
		fread(&offsets,sizeof(uint32_t),1,directory);
		fread(&len,sizeof(uint32_t),1,directory);
		if(strcmp(filename,file_name)==0){
			fseek(directory,-8,SEEK_CUR);
			fwrite(&newoff,sizeof(uint32_t),1,directory);
			if(total>len){
				fwrite(&total,sizeof(uint32_t),1,directory);
			}
			fseek(file,newoff+offset,SEEK_SET);
			fwrite((char *)buf,sizeof(char),count,file);
			rewind(directory);
			rewind(file);
			fflush(file);
			fflush(directory);
			compute_hash_tree(helper);
			pthread_mutex_unlock(&mutex);
			return 0;
		}
		
	}
	}else{
		while(!feof(directory)){
		fread(file_name,sizeof(char),64,directory);
		fread(&offsets,sizeof(uint32_t),1,directory);
		fread(&len,sizeof(uint32_t),1,directory);
		if(strcmp(filename,file_name)==0){
			fseek(file,offsets+offset,SEEK_SET);
			fwrite((char *)buf,sizeof(char),count,file);
			if(total>len){
				fseek(directory,-4,SEEK_CUR);
				fwrite(&total,sizeof(int),1,directory);
			}
			rewind(directory);
			rewind(file);
			fflush(file);
			fflush(directory);
			compute_hash_tree(helper);
			pthread_mutex_unlock(&mutex);
			return 0;
		}
	}
	}
	fflush(file);
	fflush(directory);
	return 0;
}

ssize_t file_size(char * filename, void * helper) {
	head *nodes=(head *)helper;
	FILE *directory=nodes->directory;
	uint32_t offset;
	uint32_t size;
	while(!feof(directory)){
		char file_name[64];  
		fread(file_name,sizeof(char),64,directory);
		fread(&offset,sizeof(uint32_t),1,directory);
		fread(&size,sizeof(uint32_t),1,directory);
		if(strcmp(file_name,filename)==0){
			rewind(directory);
			return size;
		}
	}
	rewind(directory);
    return -1;
}

void fletcher(uint8_t * buf, size_t length, uint8_t * output) {
	uint64_t a=0;
	uint64_t b=0;
	uint64_t c=0;
	uint64_t d=0;
	uint32_t *buffer=(uint32_t *)buf;
	int len=length/4;
	for(int i=0;i<len;i++){
		a = (a + buffer[i]) %((uint64_t)pow(2,32)-1);
		b = (b + a) % ((uint64_t)pow(2,32)-1);
		c = (c + b) % ((uint64_t)pow(2,32)-1);
		d = (d + c) % ((uint64_t)pow(2,32)-1);
	}
	memcpy(output,(uint32_t *)&a,4);
	memcpy(output+4,(uint32_t *)&b,4);
	memcpy(output+8,(uint32_t *)&c,4);
	memcpy(output+12,(uint32_t *)&d,4);
    return;
}

void compute_hash_tree(void * helper){
	head *nodes=(head *)helper;
	FILE *file=nodes->file;
	FILE *hash=nodes->hash;
	fseek(file,0,SEEK_END);
	int file_block=ftell(file)/256;
	int total=file_block;
	rewind(file);
	fseek(hash,0,SEEK_END);
	int hash_block=ftell(hash)/16;
	rewind(hash);
	int floor=0;
	int calc=file_block;
	while(calc!=1){
		calc/=2;
		floor++;
	}
	floor++;
	uint8_t buf[256];
	uint8_t out[16];
	uint8_t hash_data[hash_block][16];
	for(int i=floor;i>0;i--){
		if(i==floor){
			for(int k=0;k<file_block;k++){
			fread(buf,sizeof(uint8_t),256,file);
			fletcher(buf,256,out);
			memcpy(hash_data[hash_block-total+k],out,16);
			}
			rewind(file);
		}else{
			for(int h=0;h<file_block;h++){
				uint8_t buffer[32];
				memcpy(buffer,hash_data[2*(hash_block-total+h)+1],16);
				memcpy(&buffer[16],hash_data[2*(hash_block-total+h)+2],16);
				fletcher(buffer,32,out);
				memcpy(hash_data[hash_block-total+h],out,16);
			}
			
		}
		file_block/=2;
		total+=file_block;
	}

	for(int i=0;i<hash_block;i++){
		fwrite(hash_data[i],sizeof(uint8_t),16,hash);
	}
	rewind(hash);
    return;
}

void compute_hash_block(size_t block_offset, void * helper) {
	head *nodes=(head *)helper;
	FILE *file=nodes->file;
	FILE *hash=nodes->hash;
	fseek(file,0,SEEK_END);
	int file_block=ftell(file)/256;
	int total=file_block;
	rewind(file);
	fseek(hash,0,SEEK_END);
	int hash_block=ftell(hash)/16;
	rewind(hash);
	int floor=0;
	int calc=file_block;
	while(calc!=1){
		calc/=2;
		floor++;
	}
	floor++;
	uint8_t buf[256];
	uint8_t out[16];
	uint8_t hash_data[hash_block][16];
	for(int i=0;i<hash_block;i++){
		fread(hash_data[i],sizeof(uint8_t),16,hash);
	}
	rewind(hash);
	int index=hash_block-total+block_offset;
	for(int j=floor;j>0;j--){
		if(j==floor){
			fseek(file,block_offset*256,SEEK_SET);
			fread(buf,sizeof(uint8_t),256,file);
			fletcher(buf,256,out);
			memcpy(hash_data[index],out,16);
			rewind(file);
		}else{
			uint8_t buffer[32];
			memcpy(buffer,hash_data[2*index+1],16);
			memcpy(buffer+16,hash_data[2*index+2],16);
			fletcher(buffer,32,out);
			memcpy(hash_data[index],out,16);
		}
		//printf("%d\n",index);
		if(index%2==0){
			index=(index-2)/2;
		}else{
			index=(index-1)/2;
		}
	}
	for(int k=0;k<hash_block;k++){
		fwrite(hash_data[k],sizeof(uint8_t),16,hash);
	}
	fflush(hash);
	rewind(hash);
    return;
}
