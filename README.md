# Xps
## 一款基于AspectJ实现的权限申请和日志打印框架,通过AOP思想实现简单几个注解即可解耦大量重复代码



### 如何集成到项目？    

#### 1.将JitPack存储库添加到项目根目录的build.gradle中  

```groovy
dependencies {

    ...

    classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.8'
}


allprojects {
	repositories {

		...

		maven { url 'https://jitpack.io' }

	}
}
```

​    

#### 2.添加依赖到moudle  

```groovy
//添加到module中build.gradle第一行
apply plugin: 'android-aspectjx'


dependencies {
	...
	
	implementation 'com.github.Liera-tech:Xps:0.1.0'
}
```

​    

#### 注：使用期间，如果相认项目没错却编译不通过，Build -> clean project一下



#### 申请权限  

```java
注意：权限注解主要分为三个
	//这个注解放在有权限后的第一个方法上，用于检查和申请权限，可以单个申请权限和通过{}批量申请权限，标注对应的requestCode
	@XpsPermissionRequest(permissions = Manifest.permission.CAMERA, requestCode = REQUEST_CAMERA_CODE)
	//这个注解放在用户点击了取消时的方法上
	@XpsPermissionCancel(requestCodes = REQUEST_CAMERA_CODE)
	//这个注解放在用户勾选了不再询问，且点击了取消的方法上
	@XpsPermissionDenied(requestCodes = REQUEST_CAMERA_CODE)
额外：
	//这个注解是为了方便在申请权限前一步的回调方法，有权限了不会被调用
	@XpsPermissionBeforeEvent( requestCodes = {REQUEST_CAMERA_CODE, REQUEST_STORAGE_CODE})
    
申请权限时，实际上是打开了一个全新的透明Activity，申请权限完毕后关闭这个Activity，页面如有特殊需求，请做好标记

以下是使用实例：

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_CODE = 101;
    private static final int REQUEST_STORAGE_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    //点击按钮触发事件
    public void buttonClick(View view) {
        openCamera();
    }

    //单个获取权限
    @XpsPermissionRequest(permissions = Manifest.permission.CAMERA, requestCode = REQUEST_CAMERA_CODE)
    public void openCamera(){
        content("权限已拿到，随我搞事情");
    }

    @XpsPermissionCancel(requestCodes = REQUEST_CAMERA_CODE)
    public void cancelCamera(){
        content("权限被那娘们给取消了");
    }

    @XpsPermissionDenied(requestCodes = REQUEST_CAMERA_CODE)
    public void deniedCamera(){
        content("权限被拒绝且勾选了不再访问");
    }



    //批量获取权限
    @XpsPermissionRequest(permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.BLUETOOTH_ADMIN}, requestCode = REQUEST_STORAGE_CODE)
    public void openSDCard(){
        content("SD卡和蓝牙权限已拿到，随我搞事情");
    }

    @XpsPermissionCancel(requestCodes = REQUEST_STORAGE_CODE)
    public void cancelStorage(){
        content("存储和蓝牙权限被那娘们给取消了");
    }

    @XpsPermissionDenied(requestCodes = REQUEST_STORAGE_CODE)
    public void deniedStorage(){
        content("存储和蓝牙权限被拒绝且勾选了不再访问");
    }


    //申请权限前可在这个注解的函数内操作
    @XpsPermissionBeforeEvent( requestCodes = {REQUEST_CAMERA_CODE, REQUEST_STORAGE_CODE})
    public void beforeEvent(){
        content("在需要申请权限前搞点事情");
    }

    //跳到系统app权限申请页面注解
    @XpsPageToAppSetting
    private void toSettingApp(){}

    //打印日志的函数
    @XpsLogE
    public void content(@XpsLogContent String content){

    }
}
```





#### 日志打印

##### 1.可以设置是否打印日志,系统默认是不打印，需手动调用设置为true

```
XpsUtil.setDebug(true);
```



##### 2.可以设置排除哪些等级的日志不打印,如下代表不打印Log.i 和Log.w的日志

```
XpsUtil.exexcludeDebug(XpsUtil.TAGI, XpsUtil.TAGW);
```



##### 3.在方法中注解并打印日志

```
//打印方式一
//@XpsLog代表注解的方法需要打印日志，@XpsLogLevel代表等级，这个注解可以添加到方法上也可以添加在方法的形参上，如果不添加该注解，默认等级为I，@XpsLogContent代表要打印日志的内容，如下content方法在需要大日志的地方调用

@XpsLog
private void content(@XpsLogContent String content){}
或
@XpsLog
@XpsLogLevel(level = Xpsutil.TAGW)
private void content(@XpsLogContent String content){}
或
@XpsLog
private void content(@XpsLogLevel String level, @XpsLogContent String content){}
或
@XpsLog
private void content(@XpsLogLevel(level = Xpsutil.TAGW) String level, @XpsLogContent String content){}

//打印方式二
//直接注解一个@XpsLogX即可
@XpsLogE
private void content(@XpsLogContent String content){}
```



