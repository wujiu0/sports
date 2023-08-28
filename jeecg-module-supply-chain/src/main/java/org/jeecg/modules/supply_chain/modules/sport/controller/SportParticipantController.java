package org.jeecg.modules.supply_chain.modules.sport.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Api(tags = "射箭比赛-参赛人员")
@RestController
@RequestMapping("/sport/participant")
@Slf4j
public class SportParticipantController extends JeecgController<Participant, ISportParticipantService> {

    @Autowired
    private ISportParticipantService sportParticipantService;


    /**
     * 添加
     *
     * @param participant 参赛人员
     * @return
     */
    @AutoLog(value = "参赛人员-添加")
    @ApiOperation(value = "参赛人员-添加", notes = "参赛人员-添加")
    @PostMapping(value = "/add")
    public Result<String> add(@RequestBody Participant participant) {

        generateTarget(participant, 42);
        sportParticipantService.save(participant);
        return Result.OK("添加成功！");
    }

    private void generateTarget(Participant participant, int targetTotal) {
        // 获取下一个靶子数
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getSex, participant.getSex());
        long count = sportParticipantService.count(queryWrapper) + 1;

        int quotient = (int) (count / targetTotal);
        int mold = (int) (count % targetTotal);
        int target = 0;
        if (mold != 0) {
            if (quotient % 2 == 0) {
                target = mold;
            } else {
                target = targetTotal - mold + 1;
            }
        } else {
            if (quotient % 2 == 0) {
                target = mold + 1;
            } else {
                target = targetTotal - mold;
            }
        }
        // 设置靶子序号
        participant.setTarget(target);
    }

    /**
     * 全部列表查询
     *
     * @return
     */
    @ApiOperation(value = "参赛人员-全部列表查询", notes = "参赛人员-全部列表查询")
    @GetMapping(value = "/queryAll")
    @ResponseBody
    public Result<List<Participant>> queryAll() {
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
    @ResponseBody
    public Result<List<List<Participant>>> queryGroup(@RequestParam int sex) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Participant::getSex, sex)
            .orderByAsc(Participant::getTarget)
            .orderByAsc(Participant::getId);

        List<Participant> participantList = sportParticipantService.list(queryWrapper);
        List<List<Participant>> participantGroupList = new ArrayList<>(42);
        for (int i = 0; i < 42; i++) {
            participantGroupList.add(new ArrayList<>());
        }
        participantList.forEach(participant -> {
            participantGroupList.get(participant.getTarget() - 1).add(participant);
        });
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
    @AutoLog(value = "参赛人员-编辑")
    @ApiOperation(value = "参赛人员-编辑", notes = "参赛人员-编辑")
    @RequestMapping(value = "/edit", method = {RequestMethod.PUT, RequestMethod.POST})
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


}
