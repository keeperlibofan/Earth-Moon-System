# OpenGl draw Earth-Moon system

--- 

该项目为基于 open gl es 3.0 绘制的安卓端地月系统，作为本科设计的一个小demo

todo
- [ ]  加强交互性

## 开发环境
- OS: darwin x64
- Android Studio 4.0
- Java: v8
- OpenGl: ES 3.x
- Shader Language: GLSL ES 

--- 
## 渲染效果图
[图片]

## 光照模型

使用 Phong 光照模型 增加渲染真实感，场景中出现了三种光源
- 高光(Specular)
计算公式：采用半角计算公式
[图片] 
[图片] 只有高光的效果图

- 环境光
计算公式
[图片]
[图片] 只有环境光的效果图

- 散射光
计算公式
[图片]
[图片] 只有散射光的效果图

## 纹理贴图
球体贴图：只给顶点附上纹理坐标值，每片元通过插值来计算。

为了提高渲染真实感，🌍采用两幅贴图，一副光亮部分贴图，一副黑暗贴图。
🌛所采用的贴图只有一副，明部提亮纹理原色，暗部暗化纹理原色。
