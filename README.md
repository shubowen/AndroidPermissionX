# AndroidPermissionX
Android动态权限处理库

[![](https://www.jitpack.io/v/shubowen/AndroidPermissionX.svg)](https://www.jitpack.io/#shubowen/AndroidPermissionX)

特别感谢[https://github.com/fccaikai/AndroidPermissionX](https://github.com/fccaikai/AndroidPermissionX "AndroidPermissionX")，原谅我借鉴了你的名字

## 为什么有那么多动态权限处理库，我还要造这个轮子 ##
1. 解决多权限申请时，部分机型存在多次回调问题
2. 解决多权限申请时，用户拒绝其中某个权限，再次申请时，不能对于该权限进行精确解释的问题

## 优点 ##
1. 链式调用，请求和回调一条链解决
2. 提供类回调方式和注解回调方式，更多选择(建议使用注解回调，代码会更简洁)
3. 代码追求极简风格，尽量减少使用者编写额外代码，简洁、易用、易懂

## 使用 ##

首先在项目根目录添加：
	
	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}

再添加依赖

	dependencies {
	        compile 'com.github.shubowen:AndroidPermissionX:版本号'
	}

1、使用类回调方式：

        PermissionCompat.create(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .explain("相机解释", "存储解释","录音解释")
                .retry(true)
                .callBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        // todo 权限授权成功回调
                    }

                    @Override
                    public void onDenied(String permission, boolean retry) {
                        // todo 权限授权失败回调
                    }
                })
                .build()
                .request();

2、使用注解回调方式：

        PermissionCompat.create(this)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .explain("相机解释", "存储解释","录音解释")
                .retry(true)
                .compactCallBack(回调的对象, 请求的id)
                .build()
                .request();

在回调的对象的类中写一个权限授权成功的方法(----方法上添加注解@OnGrant(请求的id)----)：

	@OnGrant(请求的id...)
    public void onGrant() {
        // todo 权限授权成功回调
    }

在回调的对象的类中写一个权限授权失败的方法(----方法上添加注解@OnDeny(请求的id)----)：

	@OnDeny(请求的id...)
    public void onDeny(String permission, boolean retry) {
        // todo 权限授权失败回调
    }

## 注意 ##
*1、使用注解回调时，OnDeny注解的函数必须是public，参数是(String permission, boolean retry),retry为false表示用户点击了不再询问禁止了授权；OnGrant注解的函数必须是public，没有参数*

*2、retry需要配合explain函数，用户拒绝授权后，如果配置了retry(true)就会向用户展示配置的explain，并在此申请授权，默认retry是false*

详细使用方式，请clone代码查看

## 最后 ##
非常欢迎能够提出修改意见和BUG