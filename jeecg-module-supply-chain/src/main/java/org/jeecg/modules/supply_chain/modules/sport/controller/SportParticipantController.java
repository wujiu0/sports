package org.jeecg.modules.supply_chain.modules.sport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.enums.MatchStatsEnum;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportConfigService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Api(tags = "射箭比赛-参赛人员")
@RestController
@RequestMapping("/sport/participant")
@Slf4j
public class SportParticipantController extends JeecgController<Participant, ISportParticipantService> {

    @Autowired
    private ISportParticipantService sportParticipantService;

    @Autowired
    private ISportConfigService configService;

    @PostMapping("/start")
    @ApiModelProperty("开始报名")
    public Result<String> start() {
        if (configService.getStatus(getNowYear()) == MatchStatsEnum.PROGRESS.getValue()) {
            return Result.error("报名已经开始！");
        } else if (configService.getStatus(getNowYear()) == MatchStatsEnum.FINISHED.getValue()) {
            return Result.error("报名已经结束！");
        }
        configService.start(getNowYear());
        return Result.ok("开始报名！");
    }

    /**
     * 添加
     *
     * @param participant 参赛人员
     * @return
     */
    @ApiOperation(value = "参赛人员-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Participant participant) {
        if (configService.getStatus(getNowYear()) == MatchStatsEnum.NOT_STARTED.getValue()) {
            return Result.error("报名还未开始！");
        } else if (configService.getStatus(getNowYear()) == MatchStatsEnum.FINISHED.getValue()) {
            return Result.error("报名已经结束！");
        }
        try {
            participant.setCreateTime(getNowDateTime());
            sportParticipantService.save(participant);
        } catch (DuplicateKeyException e) {
            return Result.error("该参赛人员已经存在！");
        }
        return Result.OK("添加成功！");
    }

    @ApiOperation(value = "参赛人员-分组")
    @PostMapping(value = "/generateTarget")
    public Result<String> generateTarget() {
        if (configService.getStatus(getNowYear()) == MatchStatsEnum.NOT_STARTED.getValue()) {
            return Result.error("报名还未开始！");
        }
        configService.close(getNowYear());

        List<Participant> participantList0 = sportParticipantService.generateTarget(0);
        sportParticipantService.updateBatchById(participantList0);
        List<Participant> participantList1 = sportParticipantService.generateTarget(1);
        sportParticipantService.updateBatchById(participantList1);
        return Result.ok("分组成功！");
    }


    /**
     * 全部列表查询
     *
     * @return
     */
    @ApiOperation(value = "参赛人员-全部列表查询")
    @GetMapping(value = "/queryAll")
    public Result<List<Participant>> queryAll(int year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        List<Participant> participantList = sportParticipantService.list(queryWrapper);
        return Result.ok(participantList);
    }

    /**
     * @param sex 男生/女生组
     * @return
     */
    @ApiOperation(value = "参赛人员-查询分组情况", notes = "参赛人员-查询分组情况")
    @GetMapping(value = "/queryGroup")
    public Result<List<List<Participant>>> queryGroup(@RequestParam int sex, @RequestParam int year) {
        if (configService.getStatus(year) != MatchStatsEnum.FINISHED.getValue()) {
            return Result.error("报名还未结束！");
        }
        List<List<Participant>> participantGroupList = sportParticipantService.getGroup(sex, year);
        return Result.OK(participantGroupList);
    }


    /**
     * 分页列表查询
     */
    @AutoLog(value = "参赛人员-分页列表查询")
    @ApiOperation(value = "参赛人员-分页列表查询", notes = "参赛人员-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<Participant>> queryPageList(Participant participant, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        QueryWrapper<Participant> queryWrapper = QueryGenerator.initQueryWrapper(participant, req.getParameterMap());
        Page<Participant> page = new Page<>(pageNo, pageSize);
        IPage<Participant> pageList = sportParticipantService.page(page, queryWrapper);
        return Result.OK(pageList);
    }


    /**
     * 编辑
     *
     * @param
     * @return
     */
    @ApiOperation(value = "参赛人员-编辑")
    @PostMapping(value = "/edit")
    public Result<String> edit(@RequestBody Participant participant) {
        sportParticipantService.updateById(participant);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "参赛人员-通过id删除", notes = "参赛人员-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<String> delete(@RequestParam(name = "id") Integer id) {
        sportParticipantService.removeById(id);
        return Result.OK("删除成功!");
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
