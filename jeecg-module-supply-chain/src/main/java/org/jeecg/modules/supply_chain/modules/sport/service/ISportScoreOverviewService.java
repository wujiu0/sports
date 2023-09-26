package org.jeecg.modules.supply_chain.modules.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.supply_chain.modules.sport.entity.ScoreOverview;

import java.util.List;

public interface ISportScoreOverviewService extends IService<ScoreOverview> {

    List<ScoreOverview> computeScoreOverview(Integer sex, Integer year);

    List<ScoreOverview> getMainOverview(Integer participantId);

    ScoreOverview getTotalOverview(Integer participantId);

    List<ScoreOverview> getRoundOverview(Integer participantId, Integer round);
}
