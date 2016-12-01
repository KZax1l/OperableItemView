# 用Loader对MVP中的Presenter生命周期进行管理
## 有趣的BUG
当在首页执行了加载数据后，会有一个线程在后台搞事，而当首页跳转到另一个页面或者销毁首页时，就会出现**ANR**。 
## 生命周期
01. Activity的**onCreate()**
02. 在onCreate()中启动Loader，LoaderManager.LoaderCallbacks的回调方法**onCreateLoader()**
03. Loader的**onStartLoading()**
04. LoaderManager.LoaderCallbacks的回调方法**onLoadFinished()**
05. Loader的**onForceLoad()**
06. Activity的**onResume()**
07. Activity的**onPause()**
08. Activity的**onStop()**
09. Activity的**onDestroy()**
10. LoaderManager.LoaderCallbacks的回调方法**onLoaderReset()**
11. Loader的**onReset()**
