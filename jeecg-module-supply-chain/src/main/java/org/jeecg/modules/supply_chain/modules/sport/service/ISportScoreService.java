package org.jeecg.modules.supply_chain.modules.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;

public interface ISportScoreService extends IService<Score> {

    /**
     * 根据参赛人员id, 轮次和组号计算总成绩
     */
    Integer totalByParticipantIdAndRoundAndGroup(Integer participantId, Integer round, Integer group);

    /**
     * 根据参赛人员id, 轮次和组号计算10环次数
     */
    Integer getTenCountByParticipantIdAndRoundAndGroup(Integer participantId, Integer round, Integer group);

    /**
     * 根据参赛人员id, 轮次和组号计算x环次数
     */
    Integer getXCountByParticipantIdAndRoundAndGroup(Integer participantId, Integer round, Integer group);


    /**
     * 根据参赛人员id和轮次计算总成绩
     */
    Integer totalByParticipantIdAndRound(Integer participantId, Integer round);

    /**
     * 根据参赛人员id和轮次计算10环次数
     */
    Integer getTenCountByParticipantIdAndRound(Integer participantId, Integer round);

    /**
     * 根据参赛人员id和轮次计算x环次数
     */
    Integer getXCountByParticipantIdAndRound(Integer participantId, Integer round);

    /**
     * 根据参赛人员id计算总成绩
     */
    Integer totalByParticipantId(Integer participantId);

    /**
     * 根据参赛人员id计算10环次数
     */
    Integer getTenCountByParticipantId(Integer participantId);

    /**
     * 根据参赛人员id计算x环次数
     */
    Integer getXCountByParticipantId(Integer participantId);


    Integer countByParticipantId(Integer participantId, Integer round, Integer group);

}
