
#include "rtmpclient.h"
#include <stdlib.h>
#include <android/log.h>
#include "rtmp.h"


#define TAG "RTMP-CLIENT"
#define MAX_CLIENT_COUNT  (16)





static RTMPClient * clients[MAX_CLIENT_COUNT];


static ClientId generator = 0;
ClientId generate_client_id(void) {
   //TODO add lock
   return ++generator;
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

   return prc->client_id;
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




int rtmp_client_setup_url(ClientId cid, int type, const char * url) {
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
        __android_log_print(ANDROID_LOG_ERROR, TAG, "Not Found client:%d", cid);
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

    ret = RTMP_ConnectStream(prtmpc->prtmp, 0);
 
    if (!ret) {
        __android_log_print(ANDROID_LOG_ERROR, TAG, "connect stream failed :%d", cid);
        return ERROR_CONNECT_FAILED;
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


