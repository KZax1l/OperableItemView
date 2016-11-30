package org.experiment.loader.model;

import org.experiment.loader.bean.PersonBean;

/**
 * @author xiarui 16/09/24
 * @description model 接口类
 * @remark 此接口类纯属演示用 没有任何实际意义
 */

public interface IPersonModel {

    //加载Person信息
    PersonBean loadPersonInfo();
}
