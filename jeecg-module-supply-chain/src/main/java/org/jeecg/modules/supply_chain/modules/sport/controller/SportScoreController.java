package org.jeecg.modules.supply_chain.modules.sport.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.supply_chain.modules.sport.DTO.ParticipantScoreDTO;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;
import org.jeecg.modules.supply_chain.modules.sport.entity.Target;
import org.jeecg.modules.supply_chain.modules.sport.enums.MatchStatsEnum;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportConfigService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "射箭比赛-成绩")
@RestController
@RequestMapping("/score")
public class SportScoreController {
    @Autowired
    private ISportScoreService sportScoreService;
    @Autowired
    private ISportParticipantService sportParticipantService;
    @Autowired
    private ISportConfigService configService;

    @ApiOperation(value = "成绩-总成绩列表")
    @GetMapping(value = "/totalList")
    public Result<List<Integer>> totalList(@RequestParam Integer sex, @RequestParam Integer year) {
        if (configService.getStatus(year) != MatchStatsEnum.FINISHED.getValue()) {
            return Result.error("报名还未结束！");
        }
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getSex, sex);
        List<Participant> participantList = sportParticipantService.list(queryWrapper);
        List<Integer> totalList = participantList.stream().map(item ->
                sportScoreService.totalByParticipantId(item.getId()))
            .collect(Collectors.toList());
        return Result.OK(totalList);
    }

    @ApiOperation(value = "成绩-单人总成绩")
    @GetMapping(value = "/total")
    public Result<Integer> total(@RequestParam Integer participantId) {
        int total = sportScoreService.totalByParticipantId(participantId);
        return Result.OK(total);
    }

    @ApiOperation(value = "成绩-录入", notes = "总分前端做处理，错误提示，不需要传")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ParticipantScoreDTO participantScoreDTO) {

        if (configService.getStatus(getNowYear()) != MatchStatsEnum.FINISHED.getValue()) {
            return Result.error("报名还未结束！");
        }
        // 获取当前靶子所对应的参赛者
        Target target = participantScoreDTO.getTarget();
        Participant participant = sportParticipantService.getByTarget(target, getNowYear());

        // 构造得分对象
        List<String> scoreList = participantScoreDTO.getScoreList();
        for (int i = 0; i < scoreList.size(); i++) {
            Score score = new Score();
            score.setParticipantId(participant.getId());
            score.setRound(participantScoreDTO.getRound());
            score.setGroup(participantScoreDTO.getGroup());
            score.setOrder(i % 6);
            if ("X".equals(scoreList.get(i))) {
                score.setScore(10);
                score.setX(1);
            } else if ("M".equals(scoreList.get(i))) {
                score.setScore(0);
            } else {
                score.setScore(Integer.parseInt(scoreList.get(i)));
            }
            sportScoreService.save(score);
        }
        return Result.OK("添加成功！");
    }

    /**
     * 获取当前年份
     */
    private int getNowYear() {
        return LocalDate.now().getYear();
    }

    /**
     * 获取当前时间
     */
    private LocalDateTime getNowDateTime() {
        return LocalDateTime.now();
    }
}
