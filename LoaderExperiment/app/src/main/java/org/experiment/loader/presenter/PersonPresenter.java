package org.experiment.loader.presenter;

import android.os.Handler;

import org.experiment.loader.bean.PersonBean;
import org.experiment.loader.model.IPersonModel;
import org.experiment.loader.model.PersonModel;
import org.experiment.loader.ui.IPersonView;

/**
 * @author xiarui 16/09/24
 * @description Presenter类
 * @remark 需要同时持有 Model 和 View 的接口
 */

public class PersonPresenter implements BasePresenter {

    private IPersonModel mPersonModel;  //Model接口
    private IPersonView mPersonView;    //View接口

    private static Handler mHandler = new Handler();   //模拟耗时用的 没实质性作用
    private boolean isLoad = true;              //循环加载标志

    PersonPresenter(IPersonView mPersonView) {
        mPersonModel = new PersonModel();
        this.mPersonView = mPersonView;
    }

    public void updateUIByLocal() {
        final PersonBean personBean = mPersonModel.loadPersonInfo();
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (mPersonView) {
                    while (isLoad) {
                        //模拟3s耗时
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //运行在 Main 线程
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                //View层更新
                                mPersonView.updateUI(personBean);
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public void onViewAttached(Object view) {
        if (!isLoad) {
            this.isLoad = true;
            updateUIByLocal();
        }
    }

    public void onViewDetached() {
        if (isLoad) {
            this.isLoad = false;
        }
    }

    public void onDestroyed() {
        if (isLoad) {
            this.isLoad = false;
        }
    }
}
