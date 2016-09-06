
#ifndef __RTMP_CLIENT_H
#define __RTMP_CLIENT_H

#include "rtmp.h"


#define CLIENT_TYPE_READ  0x1
#define CLIENT_TYPE_WRITE 0x2

#define RET_SUCCESS (0)
#define ERROR_NO_MEMORY (-1)
#define ERROR_PARAM_INCORRECT (-2)
#define ERROR_NOT_SUPPORT_CLIENT_TYPE (-3)
#define ERROR_NO_AVAI_CLIENT_SLOT (-4)
#define ERROR_CLIENT_ID_NOT_AVAIABLE (-5)
#define ERROR_RTMP_ALLOC_FAILED (-6)
#define ERROR_NOT_FOUND_CLIENT (-7)
#define ERROR_URL_NOT_SUPPORT (-8)
#define ERROR_CONNECT_FAILED (-9)
#define ERROR_NOT_CONNECT_YET (-10)
#define ERROR_PAUSE_RESUME_FAILED (-11)

typedef unsigned int ClientId;

typedef struct RTMP_Client {

   ClientId client_id;
   RTMP * prtmp;
   unsigned char type;

} RTMPClient;


int find_rtmp_client(ClientId cid, RTMPClient ** ppc);

int rtmp_client_initialize(void);

int rtmp_client_release(ClientId cid);

int rtmp_client_setup_url(ClientId cid, int type, const char * url);

int rtmp_client_pause(ClientId cid);

int rtmp_client_resume(ClientId cid);

int rtmp_client_read(ClientId cid, char * buf, int size);

int rtmp_client_write(ClientId cid, const char * buf, int size);
#endif
