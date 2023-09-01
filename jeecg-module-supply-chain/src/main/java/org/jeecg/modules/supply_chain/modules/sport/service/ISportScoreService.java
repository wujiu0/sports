package org.jeecg.modules.supply_chain.modules.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.supply_chain.modules.sport.VO.ParticipantScoreVo;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;

public interface ISportScoreService extends IService<Score> {
    /**
     * 根据参赛人员id计算总成绩
     *
     * @param participantId
     * @return
     */
    Integer totalByParticipantId(Integer participantId);

    /**
     * 根据参赛人员id计算10环次数
     *
     * @param participantId
     * @return
     */
    Integer getTenCountByParticipantId(Integer participantId);

    /**
     * 根据参赛人员id计算x环次数
     *
     * @param participantId
     * @return
     */
    Integer getXCountByParticipantId(Integer participantId);

    /**
     * 根据参赛人员id获取成绩汇总
     *
     * @param participantId
     * @return
     */
    ParticipantScoreVo getParticipantScoreOption(Integer participantId);
}
