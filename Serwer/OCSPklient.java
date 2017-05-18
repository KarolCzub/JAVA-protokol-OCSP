package OCSP;

import java.io.*;
import java.util.*;
 
public class OCSPklient implements Serializable
{
    int Version = 1;
    int CertificateID;
    char[] HashAlgorithm;
    int NameHash = 1;
    int KeyHash = 2;
    int SerialNumber= 345;
    int Nonce = 1;
    char[] ResponseStatus;
    char[] ResponseType;
    int ResponderId;
    char[] ProducedAt;
    boolean CertStatus;
    char[] ThisUpdate;
    char[] NextUpdate;
   
    OCSPklient(int a, int b, String c, int d, int e, int f, int g, String h, String i, int j, String k, boolean l, String m, String n)
    {
        Version = a;
        CertificateID = b;
        HashAlgorithm =c.toCharArray();
        NameHash = d;
        KeyHash = e;
        SerialNumber= f;
        Nonce = g;
        ResponseStatus=h.toCharArray();
        ResponseType=i.toCharArray();
        ResponderId=j;
        ProducedAt=k.toCharArray();
        CertStatus=l;
        ThisUpdate=m.toCharArray();
        NextUpdate=n.toCharArray();
    }
    public int getVersion() {
        return Version ;
     }
 
    public void setVersion(int num) {
        Version = num;
     }
     
    public int getCertificateID() {
         return CertificateID ;
      }
 
     public void setCertificateID(int num) {
          CertificateID = num;
      }
     
     public int getNameHash() {
          return NameHash ;
       }
 
     public void setNameHash(int num) {
           NameHash = num;
       }
     public int getKeyHash() {
           return KeyHash ;
        }
 
     public void setKeyHash(int num) {
            KeyHash = num;
        }
     public int getSerialNumber() {
            return SerialNumber ;
         }
 
     public void setSerialNumber(int num) {
             SerialNumber = num;
         }
     public int getNonce() {
             return Nonce ;
          }
 
     public void setNonce(int num) {
              Nonce = num;
          }
     public int getResponderId() {
              return ResponderId ;
           }
 
     public void setResponderId(int num) {
               ResponderId = num;
           }
     public String getHashAlgorithm() {
         return String.valueOf(HashAlgorithm);
      }
 
      public void setHashAlgorithm(String name) {
          HashAlgorithm = name.toCharArray();
      }
      public String getResponseStatus() {
          return String.valueOf(ResponseStatus);

       }
 
       public void setResponseStatus(String name) {
           ResponseStatus = name.toCharArray();
       }      
       public String getResponseType() {
              return String.valueOf(ResponseType);
            		
           }
 
       public void setResponseType(String name) {
               ResponseType = name.toCharArray();
           }  
       public String getProducedAt() {
              return String.valueOf(ProducedAt);
            		
           }
 
       public void setProducedAt(String name) {
               ProducedAt = name.toCharArray();
           }
       public String getThisUpdate() {
                  return String.valueOf(ThisUpdate);
                		
               }
 
        public void setThisUpdate(String name) {
                   ThisUpdate = name.toCharArray();
               }
        public String getNextUpdate() {
                      return String.valueOf(NextUpdate);
                    		 
                   }
 
        public void setNextUpdate(String name) {
                       NextUpdate = name.toCharArray();
                   }
        public boolean getCertStatus() {
              return CertStatus;
           }
 
        public void setNextUpdate(boolean name) {
            CertStatus = name;
           }
}
