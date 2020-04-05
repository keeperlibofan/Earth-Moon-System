package com.bn.EarthMoonDemo;
import java.io.IOException;
import java.io.InputStream;

import android.opengl.ETC1Util;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.opengl.GLES30;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.annotation.SuppressLint;
import android.content.Context;
import static com.bn.EarthMoonDemo.Constant.*;

@SuppressLint("ClickableViewAccessibility")
class MySurfaceView extends GLSurfaceView 
{
	private final float TOUCH_SCALE_FACTOR = 180.0f/320;//�Ƕ����ű���
    private SceneRenderer mRenderer;//������Ⱦ��
    
    private float mPreviousX;//�ϴεĴ���λ��X����
    private float mPreviousY;//�ϴεĴ���λ��Y����
    
    int textureIdEarth;//ϵͳ����ĵ�������id
    int textureIdEarthNight;//ϵͳ����ĵ���ҹ������id
    int textureIdMoon;//ϵͳ�������������id    

    float yAngle=0;//̫���ƹ���y����ת�ĽǶ�
    float xAngle=0;//�������X����ת�ĽǶ�
    
    float eAngle=0;//������ת�Ƕ�    
    float cAngle=0;//������ת�ĽǶ�
	
	public MySurfaceView(Context context) {
        super(context);
        this.setEGLContextClientVersion(3); //����ʹ��OPENGL ES3.0
        mRenderer = new SceneRenderer();	//����������Ⱦ��
        setRenderer(mRenderer);				//������Ⱦ��		        
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);//������ȾģʽΪ������Ⱦ   
    }
	
	//�����¼��ص�����
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        switch (e.getAction()) {
        case MotionEvent.ACTION_MOVE:
        	//���غ���λ��̫����y����ת
            float dx = x - mPreviousX;//���㴥�ر�Xλ�� 
            yAngle += dx * TOUCH_SCALE_FACTOR;//��Xλ������ɽǶ�
            float sunx=(float)(Math.cos(Math.toRadians(yAngle))*100);
            float sunz=-(float)(Math.sin(Math.toRadians(yAngle))*100);
            MatrixState.setLightLocationSun(sunx,5,sunz);  
            
            //��������λ���������x����ת -90��+90
            float dy = y - mPreviousY;//���㴥�ر�Yλ�� 
            xAngle += dy * TOUCH_SCALE_FACTOR;//��Yλ���������X����ת�ĽǶ�
            if(xAngle>90)
            {
            	xAngle=90;
            }
            else if(xAngle<-90)
            {
            	xAngle=-90;
            }
            float cy=(float) (7.2*Math.sin(Math.toRadians(xAngle)));
            float cz=(float) (7.2*Math.cos(Math.toRadians(xAngle)));
            float upy=(float) Math.cos(Math.toRadians(xAngle));
            float upz=-(float) Math.sin(Math.toRadians(xAngle));
            MatrixState.setCamera(0, cy, cz, 0, 0, 0, 0, upy, upz);           
        }
        mPreviousX = x;//��¼���ر�λ��
        mPreviousY = y;
        return true; 
    } 

	private class SceneRenderer implements GLSurfaceView.Renderer 
    {   
    	Earth earth;//����
    	Moon moon;//����
    	Celestial cSmall;//С��������
    	Celestial cBig;//����������
    	
        public void onDrawFrame(GL10 gl) 
        { 
        	//�����Ȼ�������ɫ����
            GLES30.glClear( GLES30.GL_DEPTH_BUFFER_BIT | GLES30.GL_COLOR_BUFFER_BIT);   
            
            //�����ֳ�
            MatrixState.pushMatrix();
            //������ת
            MatrixState.rotate(eAngle, 0, 1, 0);
        	//���Ƶ���
            earth.drawSelf(textureIdEarth,textureIdEarthNight);     
            //������ϵ������λ��            
            MatrixState.translate(2f, 0, 0);  
            //������ת     
            MatrixState.rotate(eAngle, 0, 1, 0);
            //��������
            moon.drawSelf(textureIdMoon);
            //�ָ��ֳ�
            MatrixState.popMatrix();
            
            //�����ֳ�
            MatrixState.pushMatrix();  
            //�ǿ�������ת
            MatrixState.rotate(cAngle, 0, 1, 0);
            //����С�ߴ����ǵ�����
            cSmall.drawSelf();
            //���ƴ�ߴ����ǵ�����
            cBig.drawSelf();
            //�ָ��ֳ�
            MatrixState.popMatrix();
        }   

        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //�����Ӵ���С��λ�� 
        	GLES30.glViewport(0, 0, width, height); 
        	//����GLSurfaceView�Ŀ�߱�
            ratio= (float) width / height;
            //���ô˷����������͸��ͶӰ����
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 4f, 100);
            //���ô˷������������9����λ�þ���
            MatrixState.setCamera(0,0,7.2f,0f,0f,0f,0f,1.0f,0.0f);       
            //�򿪱������
            GLES30.glEnable(GLES30.GL_CULL_FACE);   // �����ü�
            //��ʼ������
            textureIdEarth = initTexture(R.raw.earth, samplers[0], 0);
            textureIdEarthNight = initTexture(R.raw.earthn, samplers[0], 1);
            textureIdMoon = initTexture(R.raw.moon, samplers[0], 0); // ������������
            //����̫���ƹ�ĳ�ʼλ��
            MatrixState.setLightLocationSun(100,5,0);       
            
            //����һ���̶߳�ʱ��ת��������
            new Thread()
            {
            	public void run()
            	{
            		while(threadFlag)
            		{
            			//������ת�Ƕ�
            			eAngle=(eAngle+2)%360;
            			//������ת�Ƕ�
            			cAngle=(cAngle+0.2f)%360;
            			try {
							Thread.sleep(100);
						} catch (InterruptedException e) {				  			
							e.printStackTrace();
						}
            		}
            	}
            }.start();            
        }

        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //������Ļ����ɫRGBA
            GLES30.glClearColor(0.0f,0.0f,0.0f, 1.0f);  
            //����������� 
            earth=new Earth(MySurfaceView.this,2.0f);
            //����������� 
            moon=new Moon(MySurfaceView.this,1.0f);
            //����С�����������
            cSmall=new Celestial(1,0,1000,MySurfaceView.this);
            //�����������������
            cBig=new Celestial(2,0,500,MySurfaceView.this);
            //����ȼ��
            GLES30.glEnable(GLES30.GL_DEPTH_TEST);
            //��ʼ���任����
            MatrixState.setInitStack();
            initSamplers();

        }
    }

    int[] samplers = new int[1];
    public void initSamplers() {
        GLES30.glGenSamplers(1, samplers, 0);
        GLES30.glSamplerParameterf(samplers[0], GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
        GLES30.glSamplerParameterf(samplers[0], GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
        GLES30.glSamplerParameterf(samplers[0], GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glSamplerParameterf(samplers[0], GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE);
    }
	
	public int initTexture(int rawId, int samplerId, int unitId) //textureId
	{
		//��������ID
		int[] textures = new int[1];
		GLES30.glGenTextures
		(
				1,       //����������id������
				textures,   //����id������
				0    //ƫ����
		);    
		int textureId=textures[0];    
		GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureId);
//		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER,GLES30.GL_NEAREST);
//		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D,GLES30.GL_TEXTURE_MAG_FILTER,GLES30.GL_LINEAR);
//		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S,GLES30.GL_CLAMP_TO_EDGE); // ǯλ����Ե
//		GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T,GLES30.GL_CLAMP_TO_EDGE); // ǯλ����Ե
        GLES30.glBindSampler(unitId, samplerId);//������Ԫ��sampler

        //ͨ������������ͼƬ, ͼƬͨ��etc1ѹ��===============begin===================
        InputStream is = this.getResources().openRawResource(rawId);
        try {
            ETC1Util.loadTexture//���������ݼ��ؽ�������
            (
                    GLES30.GL_TEXTURE_2D, //��������
                    0, //������
                    0,//����߿�ߴ�
                    GLES30.GL_RGB,//ɫ��ͨ����ʽ
                    GLES30.GL_UNSIGNED_BYTE, //ÿ����������
                    is//ѹ����������������
            );
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {is.close();} catch(IOException e){e.printStackTrace();}
        }
        //ͨ������������ͼƬ===============end=====================
        return textureId;
	}
}
