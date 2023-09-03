package org.jeecg.modules.supply_chain.modules.sport.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.entity.ScoreOverview;
import org.jeecg.modules.supply_chain.modules.sport.mapper.SportScoreOverviewMapper;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreOverviewService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SportScoreOverviewServiceImpl extends ServiceImpl<SportScoreOverviewMapper, ScoreOverview> implements ISportScoreOverviewService {

    @Autowired
    private ISportParticipantService participantService;
    @Autowired
    private ISportScoreService scoreService;

    @Override
    public List<ScoreOverview> computeScoreOverview(Integer sex, Integer year) {

        List<Participant> participantList = participantService.getListBySexAndYear(sex, year);
//        首先移除上一次计算结果
        if (!participantList.isEmpty()) {
            LambdaQueryWrapper<ScoreOverview> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(ScoreOverview::getParticipantId, participantList.stream().map(Participant::getId).collect(Collectors.toList()));
            this.remove(queryWrapper);
        }

//        开始构造新结果
        ScoreOverview scoreOverview = null;
        ScoreOverview scoreRoundOverview = null;
        List<ScoreOverview> preResultList = new ArrayList<>();
        for (Participant participant : participantList) {
            Integer participantId = participant.getId();

            for (int round = 1; round <= 4; round++) {
                for (int group = 1; group <= 6; group++) {
                    Integer total = scoreService.totalByParticipantIdAndRoundAndGroup(participantId, round, group);
                    Integer tenCount = scoreService.getTenCountByParticipantIdAndRoundAndGroup(participantId, round, group);
                    Integer xCount = scoreService.getXCountByParticipantIdAndRoundAndGroup(participantId, round, group);
                    scoreOverview = new ScoreOverview(participantId, participant.getName(), participant.getLocation(), round, group, 0, total, xCount, tenCount);
                    preResultList.add(scoreOverview);
                }
//                计算轮次总成绩，设置于round=round, group=0的位置上
                Integer total = scoreService.totalByParticipantIdAndRound(participantId, round);
                Integer tenCount = scoreService.getTenCountByParticipantIdAndRound(participantId, round);
                Integer xCount = scoreService.getXCountByParticipantIdAndRound(participantId, round);
                scoreOverview = new ScoreOverview(participantId, participant.getName(), participant.getLocation(),
                    round, 0, 0, total, xCount, tenCount);
                preResultList.add(scoreOverview);
            }
//            计算单人总成绩，设置于round=0, group=0的位置上
            Integer total = scoreService.totalByParticipantId(participantId);
            Integer tenCount = scoreService.getTenCountByParticipantId(participantId);
            Integer xCount = scoreService.getXCountByParticipantId(participantId);
            scoreOverview = new ScoreOverview(participantId, participant.getName(), participant.getLocation(),
                0, 0, 0, total, xCount, tenCount);
            preResultList.add(scoreOverview);
        }
//        按照轮次和组号抽签，并分别按照总成绩排序后设置排名
        ArrayList<List<ScoreOverview>> resultList =
            new ArrayList<>(preResultList.stream().collect(Collectors.groupingBy(s -> s.getRound() + "-" + s.getGroup())).values());
        resultList.forEach(scoreOverviews -> {
            scoreOverviews.sort((o1, o2) -> o2.getTotal() - o1.getTotal());
            for (int i = 0; i < scoreOverviews.size(); i++) {
                scoreOverviews.get(i).setRank(i + 1);
            }
        });

//        返回扁平化后的成绩列表
        return resultList.stream().flatMap(List::stream).collect(Collectors.toList());

    }

    @Override
    public List<ScoreOverview> getMainOverview(Integer participantId) {

        LambdaQueryWrapper<ScoreOverview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScoreOverview::getParticipantId, participantId)
            .eq(ScoreOverview::getGroup, 0);
        return this.list(queryWrapper);
    }

    @Override
    public ScoreOverview getTotalOverview(Integer participantId) {
        LambdaQueryWrapper<ScoreOverview> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ScoreOverview::getParticipantId, participantId)
            .eq(ScoreOverview::getRound, 0)
            .eq(ScoreOverview::getGroup, 0);
        return this.getOne(queryWrapper);
    }
}
