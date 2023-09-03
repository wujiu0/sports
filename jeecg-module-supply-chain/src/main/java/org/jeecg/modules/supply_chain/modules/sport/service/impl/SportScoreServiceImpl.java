package org.jeecg.modules.supply_chain.modules.sport.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;
import org.jeecg.modules.supply_chain.modules.sport.mapper.SportScoreMapper;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SportScoreServiceImpl extends ServiceImpl<SportScoreMapper, Score> implements ISportScoreService {
    @Override
    public Integer totalByParticipantIdAndRoundAndGroup(Integer participantId, Integer round, Integer group) {
        int total = 0;
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round)
            .eq(Score::getGroup, group);
        List<Score> scoreList = this.list(queryWrapper);
        for (Score score : scoreList) {
            total += score.getScore();
        }
        return total;
    }

    @Override
    public Integer getTenCountByParticipantIdAndRoundAndGroup(Integer participantId, Integer round, Integer group) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round)
            .eq(Score::getGroup, group)
            .eq(Score::getScore, 10);
        return (int) this.count(queryWrapper);
    }

    @Override
    public Integer getXCountByParticipantIdAndRoundAndGroup(Integer participantId, Integer round, Integer group) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round)
            .eq(Score::getGroup, group)
            .eq(Score::getX, 1);
        return (int) this.count(queryWrapper);
    }

    @Override
    public Integer totalByParticipantIdAndRound(Integer participantId, Integer round) {
        int total = 0;
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round);
        List<Score> scoreList = this.list(queryWrapper);
        for (Score score : scoreList) {
            total += score.getScore();
        }
        return total;
    }

    @Override
    public Integer getTenCountByParticipantIdAndRound(Integer participantId, Integer round) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round)
            .eq(Score::getScore, 10);
        return (int) this.count(queryWrapper);
    }

    @Override
    public Integer getXCountByParticipantIdAndRound(Integer participantId, Integer round) {
        LambdaQueryWrapper<Score> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round)
            .eq(Score::getX, 1);
        return (int) this.count(queryWrapper);
    }

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


    public Integer countByParticipantId(Integer participantId, Integer round, Integer group) {
        LambdaQueryWrapper<Score> qw = new LambdaQueryWrapper<>();
        qw.eq(Score::getParticipantId, participantId)
            .eq(Score::getRound, round)
            .eq(Score::getGroup, group);
        return (int) this.count(qw);
    }

}
