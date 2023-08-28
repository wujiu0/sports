package org.jeecg.modules.supply_chain.modules.sport.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.supply_chain.modules.sport.DTO.ParticipantScoreDTO;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "射箭比赛-成绩")
@RestController
@RequestMapping("/score")
public class SportScoreController {
    @Autowired
    private ISportScoreService sportScoreService;

    @ApiOperation(value = "成绩-录入", notes = "成绩-录入")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody ParticipantScoreDTO participantScoreDTO) {

        participantScoreDTO.getScoreList().forEach(item -> {
            Score score = new Score();
            BeanUtils.copyProperties(participantScoreDTO, score, "scoreList");
            if ("X".equals(item)) {
                score.setScore(10);
                score.setX(1);
            } else if ("M".equals(item)) {
                score.setScore(0);
            } else {
                score.setScore(Integer.valueOf(item));
            }
            sportScoreService.save(score);
        });
        return Result.OK("添加成功！");
    }
}
