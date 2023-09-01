package org.jeecg.modules.supply_chain.modules.sport.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.supply_chain.modules.sport.VO.ParticipantScoreVo;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;
import org.jeecg.modules.supply_chain.modules.sport.mapper.SportScoreMapper;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportScoreServiceImpl extends ServiceImpl<SportScoreMapper, Score> implements ISportScoreService {
    @Override
    public Integer totalByParticipantId(Integer participantId) {
        int total = 0;
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId);
        List<Score> scoreList = this.list(queryWrapper);
        for (Score score : scoreList) {
            total += score.getScore();
        }
        return total;
    }

    @Override
    public Integer getTenCountByParticipantId(Integer participantId) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getScore, 10);
        return (int) this.count(queryWrapper);
    }

    @Override
    public Integer getXCountByParticipantId(Integer participantId) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getX, 1);
        return (int) this.count(queryWrapper);
    }

    @Override
    public ParticipantScoreVo getParticipantScoreOption(Integer participantId) {
        Integer xCount = this.getXCountByParticipantId(participantId);
        Integer tenCount = this.getTenCountByParticipantId(participantId);
        Integer total = this.totalByParticipantId(participantId);
        return new ParticipantScoreVo(total, xCount, tenCount);
    }
}
