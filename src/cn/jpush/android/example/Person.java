package cn.jpush.android.example;

import java.io.Serializable;  
public class Person implements Serializable {  
    private static final long serialVersionUID = -7060210544600464481L;   
    private String method;  
    private String agrs;  
    public String getMethod() {  
        return method;  
    }  
    public void setMethod(String method) {  
        this.method = method;  
    }  
    public String getArgs() {  
        return agrs;  
    }  
    public void setArgs(String agrs) {  
        this.agrs = agrs;  
    }  
      
}  