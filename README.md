# AndroidPermissionX
Android动态权限处理库

## 为什么有那么多动态权限处理库，我还要造这个轮子 ##
1. 解决多权限申请时，部分机型存在多次回调问题
2. 解决多权限申请时，用户拒绝其中某个权限，再次申请时，不能对于该权限进行精确解释的问题

## 优点 ##
1. 链式调用，请求和回调一条链解决
2. 提供类回调方式和注解回调方式，更多选择(建议使用注解回调，代码会更简洁)
3. 代码追求极简风格，尽量减少使用者编写额外代码，简洁、易用、易懂

## 使用 ##
1、使用类回调方式：

	PermissionCompat.create(context)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .explain("相机解释", "存储解释","录音解释")
                .callBack(new OnRequestPermissionsCallBack() {
                    @Override
                    public void onGrant() {
                        // todo 权限授权成功回调
                    }

                    @Override
                    public void onDenied(String permission) {
                        // todo 权限授权失败回调
                    }
                })
                .build()
                .request();

2、使用注解回调方式：

	        PermissionCompat.create(context)
                .permissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO)
                .explain("相机解释", "存储解释","录音解释")
                .compactCallBack(回调的对象, 请求的id)
                .build()
                .request();

在回调的对象的类中写一个权限授权成功的方法(----方法上添加注解@OnGrant(请求的id)----)：

	@OnGrant(请求的id)
    public void startCamera() {
        // todo 权限授权成功回调
    }

在回调的对象的类中写一个权限授权失败的方法(----方法上添加注解@OnDeny(请求的id)----)：

	@OnDeny(请求的id)
    public void onDenied(String permission) {
        // todo 权限授权失败回调
    }

详细使用方式，请clone代码查看

## 最后 ##
非常欢迎能够提出修改意见和BUG