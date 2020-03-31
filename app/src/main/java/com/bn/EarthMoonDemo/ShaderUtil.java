package com.bn.EarthMoonDemo;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.res.Resources;
import android.opengl.GLES30;
import android.util.Log;

//���ض���Shader��ƬԪShader�Ĺ�����
public class ShaderUtil 
{
   //�����ƶ�shader�ķ���
   public static int loadShader
   (
		 int shaderType, //shader������  GLES30.GL_VERTEX_SHADER   GLES30.GL_FRAGMENT_SHADER
		 String source   //shader�Ľű��ַ���
   ) 
   {
	    //����һ����shader
        int shader = GLES30.glCreateShader(shaderType);
        //�������ɹ������shader
        if (shader != 0) 
        {
        	//����shader��Դ����
            GLES30.glShaderSource(shader, source);
            //����shader
            GLES30.glCompileShader(shader);
            //��ű���ɹ�shader����������
            int[] compiled = new int[1];
            //��ȡShader�ı������
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
            if (compiled[0] == 0) 
            {//������ʧ������ʾ������־��ɾ����shader
                Log.e("ES30_ERROR", "Could not compile shader " + shaderType + ":");
                Log.e("ES30_ERROR", GLES30.glGetShaderInfoLog(shader));
                GLES30.glDeleteShader(shader);
                shader = 0;      
            }  
        }
        return shader;
    }
    
   //����shader����ķ���
   public static int createProgram(String vertexSource, String fragmentSource) 
   {
	    //���ض�����ɫ��
        int vertexShader = loadShader(GLES30.GL_VERTEX_SHADER, vertexSource);
        if (vertexShader == 0) 
        {
            return 0;
        }
        
        //����ƬԪ��ɫ��
        int pixelShader = loadShader(GLES30.GL_FRAGMENT_SHADER, fragmentSource);
        if (pixelShader == 0) 
        {
            return 0;
        }

        //��������
        int program = GLES30.glCreateProgram();
        //�����򴴽��ɹ���������м��붥����ɫ����ƬԪ��ɫ��
        if (program != 0) 
        {
        	//������м��붥����ɫ��
            GLES30.glAttachShader(program, vertexShader);
            checkGlError("glAttachShader");
            //������м���ƬԪ��ɫ��
            GLES30.glAttachShader(program, pixelShader);
            checkGlError("glAttachShader");
            //���ӳ���
            GLES30.glLinkProgram(program);
            //������ӳɹ�program����������
            int[] linkStatus = new int[1];
            //��ȡprogram���������
            GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
            //������ʧ���򱨴�ɾ������
            if (linkStatus[0] != GLES30.GL_TRUE) 
            {
                Log.e("ES30_ERROR", "Could not link program: ");
                Log.e("ES30_ERROR", GLES30.glGetProgramInfoLog(program));
                GLES30.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }
    
   //���ÿһ�������Ƿ��д���ķ��� 
   public static void checkGlError(String op) 
   {
        int error;
        while ((error = GLES30.glGetError()) != GLES30.GL_NO_ERROR) 
        {
            Log.e("ES30_ERROR", op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
   }
   
   //��sh�ű��м���shader���ݵķ���
   public static String loadFromAssetsFile(String fname,Resources r)
   {
   	String result=null;    	
   	try
   	{
   		InputStream in=r.getAssets().open(fname);
			int ch=0;
		    ByteArrayOutputStream baos = new ByteArrayOutputStream();
		    while((ch=in.read())!=-1)
		    {
		      	baos.write(ch);
		    }      
		    byte[] buff=baos.toByteArray();
		    baos.close();
		    in.close();
   		result=new String(buff,"UTF-8"); 
   		result=result.replaceAll("\\r\\n","\n");
   	}
   	catch(Exception e)
   	{
   		e.printStackTrace();
   	}    	
   	return result;
   }
}
