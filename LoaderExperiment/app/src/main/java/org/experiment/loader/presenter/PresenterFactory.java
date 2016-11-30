package org.experiment.loader.presenter;

import org.experiment.loader.ui.IPersonView;

/**
 * @author xiarui
 * @date 2016/9/24 18:32
 * @description Presenter 工厂类
 * @remark 最好抽取基类
 */

public class PresenterFactory {

    private IPersonView mPersonView;

    public PresenterFactory(IPersonView mPersonView) {
        this.mPersonView = mPersonView;
    }

    public PersonPresenter create() {
        return new PersonPresenter(mPersonView);
    }
}
