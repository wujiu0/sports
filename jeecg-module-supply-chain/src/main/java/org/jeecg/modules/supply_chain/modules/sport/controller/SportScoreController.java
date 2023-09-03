package org.jeecg.modules.supply_chain.modules.sport.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.supply_chain.modules.sport.DTO.ParticipantScoreDTO;
import org.jeecg.modules.supply_chain.modules.sport.VO.*;
import org.jeecg.modules.supply_chain.modules.sport.entity.Config;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;
import org.jeecg.modules.supply_chain.modules.sport.entity.ScoreOverview;
import org.jeecg.modules.supply_chain.modules.sport.enums.MatchStatsEnum;
import org.jeecg.modules.supply_chain.modules.sport.enums.SexEnum;
import org.jeecg.modules.supply_chain.modules.sport.enums.TargetIndex;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportConfigService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreOverviewService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "射箭比赛-成绩")
@RestController
@RequestMapping("/score")
public class SportScoreController {
    @Autowired
    private ISportScoreService scoreService;
    @Autowired
    private ISportParticipantService participantService;
    @Autowired
    private ISportConfigService configService;
    @Autowired
    private ISportScoreOverviewService scoreOverviewService;

    @ApiOperation("获取初始化信息")
    @GetMapping("/getInitInfo")
    public Result<InitInfoVO> getInitInfo(@RequestParam String idNumber) {

        Config config = configService.getByYear(getNowYear());

        Participant participant = participantService.getByIdNumber(idNumber, getNowYear());
        Boolean insertFlag = scoreService.countByParticipantId(participant.getId(), config.getRound(), config.getGroup()) > 0;

        InitInfoVO result = new InitInfoVO();
        BeanUtils.copyProperties(config, result);
        result.setInsertFlag(insertFlag);
        return Result.ok(result);
    }


    @ApiOperation(value = "成绩-录入", notes = "总分前端做处理，错误提示，不需要传")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ParticipantScoreDTO participantScoreDTO) {

        if (configService.getStatus(getNowYear()) < MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛还未开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛已经结束！");
        }

        Participant participant = participantService.getByIdNumber(participantScoreDTO.getIdNumber(), getNowYear());
        Config config = configService.getByYear(getNowYear());
        if (scoreService.countByParticipantId(participant.getId(), config.getRound(), config.getGroup()) > 0) {
            return Result.error("当前轮组成绩已经录入！");
        }

        if (participantScoreDTO.getScoreList().size() != 6) {
            return Result.error("成绩数量不正确！");
        }

        // 构造得分对象
        List<String> scoreList = participantScoreDTO.getScoreList();
        for (int i = 0; i < scoreList.size(); i++) {
            Score score = new Score();
            score.setParticipantId(participant.getId());
            score.setRound(participantScoreDTO.getRound());
            score.setGroup(participantScoreDTO.getGroup());
            score.setOrder(i % 6 + 1);
            if ("x".equals(scoreList.get(i)) || "X".equals(scoreList.get(i))) {
                score.setScore(10);
                score.setX(1);
            } else if ("m".equals(scoreList.get(i)) || "M".equals(scoreList.get(i))) {
                score.setScore(0);
            } else {
                score.setScore(Integer.parseInt(scoreList.get(i)));
            }
            scoreService.save(score);
        }
        return Result.OK("添加成功！");
    }


    @ApiOperation("进入下一轮")
    @PostMapping("/nextRound")
    public Result<Integer> nextRound() {
        if (configService.getStatus(getNowYear()) < MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛还未开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛已经结束！");
        }
        Integer res = configService.nextRound(getNowYear());
        if (res == 0) {
            return Result.error("已经是最后一轮！");
        } else if (res == -1) {
            return Result.error("当前轮还未结束");
        } else {
            return Result.ok(res);
        }
    }


    @ApiOperation("进入下一组")
    @PostMapping("/nextGroup")
    public Result<Integer> nextGroup() {
        if (configService.getStatus(getNowYear()) < MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛还未开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛已经结束！");
        }
        Integer res = configService.nextGroup(getNowYear());
        if (res == -1) {
            return Result.error("已经是最后一组！");
        } else {
            return Result.ok(res);
        }
    }

    @PostMapping("/computeRank")
    @ApiOperation("计算排名")
    public Result<String> computeRank() {
        if (configService.getStatus(getNowYear()) < MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛还未开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("比赛已经结束！");
        }
        if (configService.getByYear(getNowYear()).getRound() != 4 || configService.getByYear(getNowYear()).getGroup() != 6) {
            return Result.error("成绩还未录入完成！");
        }
        List<ScoreOverview> scoreOverviewList0 =
            scoreOverviewService.computeScoreOverview(SexEnum.FeMALE.getValue(), getNowYear());
        List<ScoreOverview> scoreOverviewList1 = scoreOverviewService.computeScoreOverview(SexEnum.MALE.getValue(),
            getNowYear());
        scoreOverviewService.saveBatch(scoreOverviewList0);
        scoreOverviewService.saveBatch(scoreOverviewList1);
        return Result.ok("成绩排名已计算完成");
    }

    @PostMapping("/finish")
    @ApiOperation("结束比赛-定榜")
    public Result<String> finish() {
        configService.finish(getNowYear());
        return Result.OK("比赛已结束");
    }

    @ApiOperation("成绩详情-轮次")
    @GetMapping("/listRoundDetail")
    public Result<List<ScoreRoundDetailVO>> listRoundDetail(@RequestParam Integer sex, @RequestParam Integer year) {

        List<Participant> participantList = participantService.getListBySexAndYear(sex, year);
        List<ScoreRoundDetailVO> resultList = new ArrayList<>();
        for (Participant participant : participantList) {
            ScoreRoundDetailVO scoreRoundDetailVO = new ScoreRoundDetailVO();

            scoreRoundDetailVO.setName(participant.getName());
            scoreRoundDetailVO.setLocation(participant.getLocation());

            scoreRoundDetailVO.setTargetName(TargetIndex.get(participant.getTarget(), participant.getIndexInTarget()));

            scoreRoundDetailVO.setTotal(scoreService.totalByParticipantId(participant.getId()));
            scoreRoundDetailVO.setTenCount(scoreService.getTenCountByParticipantId(participant.getId()));
            scoreRoundDetailVO.setXCount(scoreService.getXCountByParticipantId(participant.getId()));
            for (int round = 1; round <= 4; round++) {
                BaseScoreVO scoreRoundItem = new BaseScoreVO();

                scoreRoundItem.setTotal(scoreService.totalByParticipantIdAndRound(participant.getId(), round));
                scoreRoundItem.setXCount(scoreService.getXCountByParticipantIdAndRound(participant.getId(), round));
                scoreRoundItem.setTenCount(scoreService.getTenCountByParticipantIdAndRound(participant.getId(), round));

                scoreRoundDetailVO.getScorebyRoundList().add(scoreRoundItem);
            }

            resultList.add(scoreRoundDetailVO);
        }
        resultList.sort((o1, o2) -> o2.getTotal() - o1.getTotal());
        return Result.ok(resultList);
    }

    @ApiOperation("成绩总览-轮次")
    @GetMapping("/listRoundOverview")
    public Result<List<ScoreMainOverviewVO>> listRoundOverview(@RequestParam Integer sex, @RequestParam Integer year) {

//        获取参赛人员列表
        List<Participant> participantList = participantService.getListBySexAndYear(sex, year);

        List<ScoreMainOverviewVO> resultList = new ArrayList<>();

        for (Participant participant : participantList) {
            ScoreMainOverviewVO scoreMainOverviewVO = new ScoreMainOverviewVO();

//            设置基本信息
            scoreMainOverviewVO.setName(participant.getName());
            scoreMainOverviewVO.setLocation(participant.getLocation());
            scoreMainOverviewVO.setTargetName(TargetIndex.get(participant.getTarget(), participant.getIndexInTarget()));

//            设置轮次成绩
            List<ScoreOverview> mainOverviewList = scoreOverviewService.getMainOverview(participant.getId());
            List<BaseScoreOverviewVO> scoreRoundOverviewVOList =
                mainOverviewList.stream().filter(item -> item.getRound() != 0).map(item -> new BaseScoreOverviewVO(item.getTotal(),
                    item.getRank())).collect(Collectors.toList());
            scoreMainOverviewVO.setScoreRoundOverviewVOList(scoreRoundOverviewVOList);

//            设置总成绩
            ScoreOverview scoreTotalOverview = mainOverviewList.stream().filter(item -> item.getRound() == 0).findFirst().get();
            scoreMainOverviewVO.setTotal(scoreTotalOverview.getTotal());
            scoreMainOverviewVO.setRank(scoreTotalOverview.getRank());

            resultList.add(scoreMainOverviewVO);
        }


        resultList.sort(Comparator.comparingInt(ScoreMainOverviewVO::getRank));
        return Result.ok(resultList);
    }

    @ApiOperation("成绩总览-地区")
    @GetMapping("/listLocationOverview")
    public Result<List<ScoreLocationOverviewVO>> listLocationOverview(@RequestParam Integer sex, @RequestParam Integer year) {

//        获取参赛人员列表
        List<Participant> participantList = participantService.getListBySexAndYear(sex, year);

        List<ScoreLocationOverviewVO> resultList = new ArrayList<>();

//        获取总分列表并按照单位抽签
        List<List<ScoreOverview>> preOverviewList = new ArrayList<>(participantList.stream().map(participant ->
                scoreOverviewService.getTotalOverview(participant.getId())
            )
            .collect(Collectors.groupingBy(ScoreOverview::getLocation)).values());
//        按照总分排序
        preOverviewList.forEach(item -> item.sort((o1, o2) -> o2.getTotal() - o1.getTotal()));

        preOverviewList.forEach(item -> {
                List<ScoreOverview> scoreOverviewList = item.subList(0, 3);

                List<BaseScoreLocationVO> scoreLocationVOList = scoreOverviewList.stream().map(item1 -> new BaseScoreLocationVO(item1.getName(),
                    item1.getTotal())).collect(Collectors.toList());
                ScoreLocationOverviewVO resultItem = new ScoreLocationOverviewVO(0, item.get(0).getLocation(),
                    scoreLocationVOList);
                resultItem.setTotal(scoreOverviewList.stream().mapToInt(ScoreOverview::getTotal).sum());
                resultItem.setTenCount(scoreOverviewList.stream().mapToInt(ScoreOverview::getTenCount).sum());
                resultItem.setXCount(scoreOverviewList.stream().mapToInt(ScoreOverview::getXCount).sum());
                resultList.add(resultItem);
            }
        );
        resultList.sort((o1, o2) -> o2.getTotal() - o1.getTotal());
        for (int i = 0; i < resultList.size(); i++) {
            resultList.get(i).setRank(i + 1);
        }
        return Result.ok(resultList);
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
