package org.jeecg.modules.supply_chain.modules.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.supply_chain.modules.sport.entity.Config;

public interface ISportConfigService extends IService<Config> {
    /**
     * 开始报名
     *
     * @param year
     * @return
     */
    Boolean start(int year);

    /**
     * 结束报名
     *
     * @param year
     * @return
     */
    Boolean close(int year);

    /**
     * 获取当前比赛状态
     *
     * @param year
     * @return
     */
    Integer getStatus(int year);

    /**
     * 获取靶子数量
     *
     * @param sex
     * @param year
     * @return
     */
    Integer getTargetCount(int sex, int year);

    /**
     * 设置靶子数量
     *
     * @param sex
     * @param count
     * @param year
     * @return
     */
    Boolean setTargetCount(int sex, long count, int year);

}
