
#include "rtmp.h"
#include <jni.h>
#include <stdlib.h>

#define MAX_CLIENT_COUNT  (16)


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


static RTMPClient * clients[MAX_CLIENT_COUNT];



ClientId generate_client_id(void) {
   return RET_SUCCESS;
}


int find_rtmp_client(ClientId cid, RTMPClient ** ppc) {
     if (cid <= 0 || ppc == NULL) {
         return ERROR_PARAM_INCORRECT;
     } 

  
     int idx = 0; 
     for (idx = 0; idx < MAX_CLIENT_COUNT; idx++) {
         if (clients[idx] != NULL && clients[idx]->client_id == cid) {
             break;
         }
     }

     if (idx == MAX_CLIENT_COUNT) {
          return ERROR_NOT_FOUND_CLIENT;
     }

     *ppc = clients[idx];
     if (*ppc == NULL) {
          return ERROR_NOT_FOUND_CLIENT;
     }

     return RET_SUCCESS;
}


/*
 *
 *
 */
int rtmp_client_initialize(void) {

   int idx = 0; 
   for (idx = 0; idx < MAX_CLIENT_COUNT; idx++) {
       if (clients[idx] == NULL) {
           break;
       }
   }

   if (idx == MAX_CLIENT_COUNT) {
      return ERROR_NO_AVAI_CLIENT_SLOT;
   }
 
   RTMPClient * prc = (RTMPClient *) malloc(sizeof(RTMPClient));
   if (prc == NULL) {
      return ERROR_NO_MEMORY;
   }

   memset(prc, 0, sizeof(RTMPClient));
   RTMP * prtmp = RTMP_Alloc();
   if (prtmp == NULL) {
      free(prc);
      return ERROR_RTMP_ALLOC_FAILED;
   }
   RTMP_Init(prtmp);
   
   prc->client_id = generate_client_id();
   prc->prtmp = prtmp;
   clients[idx] = prc;

   return RET_SUCCESS;
}



int rtmp_client_release(ClientId cid) {
    int idx = 0;
    for (idx = 0; idx < MAX_CLIENT_COUNT; idx++) {
        if (clients[idx]->client_id == cid) {
           break;
        }
    }

    if (idx == MAX_CLIENT_COUNT) {
       return ERROR_NOT_FOUND_CLIENT;
    }
    RTMPClient * prtmpcl = clients[idx];
    clients[idx] = NULL;
    if (prtmpcl == NULL) {
        //TODO  shond not happened
        return RET_SUCCESS;
    }

    if (prtmpcl->prtmp != NULL) {
        RTMP_Close(prtmpcl->prtmp);
        RTMP_Free(prtmpcl->prtmp);
    }

    free(prtmpcl);
    
    return RET_SUCCESS;
}




int rtmp_client_setup_url(ClientId cid, int type, char * url) {
    int ret;
    RTMPClient * prtmpc;
    if (type <= 0 || type > (CLIENT_TYPE_READ | CLIENT_TYPE_WRITE) )
    {
        return ERROR_NOT_SUPPORT_CLIENT_TYPE;
    }

    if (url == NULL || *url =='\0') {
        return ERROR_PARAM_INCORRECT;
    }

    ret = find_rtmp_client(cid, &prtmpc);

    if (ret != RET_SUCCESS) {
        return ret;
    }

    ret =  RTMP_SetupURL(prtmpc->prtmp, url);
    if (ret != TRUE) {
        return ERROR_URL_NOT_SUPPORT;
    }

    ret = RTMP_Connect(prtmpc->prtmp, NULL); 
    if (ret != TRUE) {
        return ERROR_CONNECT_FAILED;
    }

    if ((type & CLIENT_TYPE_WRITE) == CLIENT_TYPE_WRITE) {
        RTMP_EnableWrite(prtmpc->prtmp);
    }
  
    return RET_SUCCESS;
}




int rtmp_client_pause(ClientId cid) {
    int ret = RET_SUCCESS;
    RTMPClient * prtmpc;
    ret = find_rtmp_client(cid, &prtmpc);
    if (ret != RET_SUCCESS) {
        return ret;
    }

    if (RTMP_IsConnected(prtmpc->prtmp) == TRUE) {
        ret = RTMP_Pause(prtmpc->prtmp, TRUE); 
        if (ret != TRUE) {
             ret = ERROR_PAUSE_RESUME_FAILED;
        } else {
             ret = RET_SUCCESS;
        }
    } else {
        ret = ERROR_NOT_CONNECT_YET; 
    }
    return ret;
}

int rtmp_client_resume(ClientId cid) {
    int ret = RET_SUCCESS;
    RTMPClient * prtmpc;
    ret = find_rtmp_client(cid, &prtmpc);
    if (ret != RET_SUCCESS) {
        return ret;
    }

    if (RTMP_IsConnected(prtmpc->prtmp) == TRUE) {
        ret = RTMP_Pause(prtmpc->prtmp, FALSE); 
        if (ret != TRUE) {
             ret = ERROR_PAUSE_RESUME_FAILED;
        } else {
             ret = RET_SUCCESS;
        }
    } else {
        ret = ERROR_NOT_CONNECT_YET; 
    }
    return ret;
}


int rtmp_client_read(ClientId cid, char * buf, int size) {
    int ret = RET_SUCCESS;
    RTMPClient * prtmpc;
    ret = find_rtmp_client(cid, &prtmpc);
    if (ret != RET_SUCCESS) {
        return ret;
    }

    if (RTMP_IsConnected(prtmpc->prtmp) == FALSE) {
        return ERROR_NOT_CONNECT_YET;
    }

    if (buf == NULL) {
       return ret;
    } 

    return RTMP_Read(prtmpc->prtmp, buf, size);
}

int rtmp_client_write(ClientId cid, const char * buf, int size) {
    int ret = RET_SUCCESS;
    RTMPClient * prtmpc;
    ret = find_rtmp_client(cid, &prtmpc);
    if (ret != RET_SUCCESS) {
        return ret;
    }

    if (RTMP_IsConnected(prtmpc->prtmp) == FALSE) {
        return ERROR_NOT_CONNECT_YET;
    }

    if (buf == NULL) {
       return ret;
    } 

    return RTMP_Write(prtmpc->prtmp, buf, size);
}


