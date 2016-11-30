# 用Loader对MVP中的Presenter生命周期进行管理
## 有趣的BUG
当在首页执行了加载数据后，会有一个线程在后台搞事，而当首页跳转到另一个页面或者销毁首页时，就会出现**ANR**。 
## 生命周期
01. Activity的**onCreate()**
02. LoaderManager.LoaderCallbacks的回调方法**onCreateLoader()**
03. Activity中onCreate()里面进行的操作，如常见的控件初始化
04. Loader的**onStartLoading()**
05. LoaderManager.LoaderCallbacks的回调方法**onLoadFinished()**
06. Loader的**onForceLoad()**
07. Activity的**onResume()**
08. Activity的**onPause()**
09. Activity的**onStop()**
10. Activity的**onDestroy()**
11. LoaderManager.LoaderCallbacks的回调方法**onLoaderReset()**
12. Loader的**onReset()**
