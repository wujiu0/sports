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
import org.jeecg.modules.supply_chain.modules.sport.VO.ParticipantInfoGroupVo;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.enums.MatchStatsEnum;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportConfigService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Api(tags = "射箭比赛-参赛人员")
@RestController
@RequestMapping("/participant")
@Slf4j
public class SportParticipantController extends JeecgController<Participant, ISportParticipantService> {

    @Autowired
    private ISportParticipantService sportParticipantService;

    @Autowired
    private ISportConfigService configService;

    @PostMapping("/start")
    @ApiOperation("开始报名")
    public Result<String> start() {
        if (configService.getStatus(getNowYear()) == MatchStatsEnum.PROGRESS.getValue()) {
            return Result.error("报名已经开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.PROGRESS.getValue()) {
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
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.PROGRESS.getValue()) {
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

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/importExcel")
    @ApiOperation("通过excel导入数据")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        if (configService.getStatus(getNowYear()) == MatchStatsEnum.NOT_STARTED.getValue()) {
            return Result.error("报名还未开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.PROGRESS.getValue()) {
            return Result.error("报名已经结束！");
        }
//        super.importExcel()
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(1);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Participant> list = ExcelImportUtil.importExcel(file.getInputStream(), Participant.class, params);
//                过滤掉身份证号为空的数据
                list = list.stream().filter(participant -> participant.getIdNumber() != null && !participant.getIdNumber().isEmpty()).collect(Collectors.toList());
                list.forEach(participant -> {
                    participant.setCreateTime(getNowDateTime());
                });
                sportParticipantService.saveBatch(list);
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                String msg = e.getMessage();
                log.error(msg, e);
                if (msg != null && msg.contains("Duplicate entry")) {
                    return Result.error("文件导入失败:有重复数据！");
                } else {
                    return Result.error("文件导入失败:" + e.getMessage());
                }
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    @GetMapping(value = "/exportXls")
    @ApiOperation("导出")
    public ModelAndView exportXls(HttpServletRequest request) {
        Participant participant = new Participant();
        String title = "参赛人员报名表";
        // 组装查询条件
//        QueryWrapper<Participant> queryWrapper = QueryGenerator.initQueryWrapper(participant, request.getParameterMap());

        // 获取导出数据
        List<Participant> exportList = sportParticipantService.list();
        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());

        mv.addObject(NormalExcelConstants.FILE_NAME, title);
        mv.addObject(NormalExcelConstants.CLASS, Participant.class);
        ExportParams exportParams = new ExportParams(title, title);
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }

    @PostMapping("/close")
    @ApiOperation("结束报名")
    public Result<String> close() {
        if (configService.getStatus(getNowYear()) < MatchStatsEnum.PROGRESS.getValue()) {
            return Result.error("报名还未开始！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.PROGRESS.getValue()) {
            return Result.error("报名已经结束！");
        }
        configService.close(getNowYear());
        return Result.ok("结束报名！");
    }

    @ApiOperation(value = "参赛人员-抽签")
    @PostMapping(value = "/generateTarget")
    public Result<String> generateTarget() {
        if (configService.getStatus(getNowYear()) == MatchStatsEnum.NOT_STARTED.getValue()) {
            return Result.error("报名还未开始！");
        } else if (configService.getStatus(getNowYear()) == MatchStatsEnum.PROGRESS.getValue()) {
            return Result.error("报名还未结束！");
        } else if (configService.getStatus(getNowYear()) > MatchStatsEnum.GROUPING.getValue()) {
            return Result.error("抽签已经完成！");
        }

        List<Participant> participantList0 = sportParticipantService.generateTarget(0);
        sportParticipantService.updateBatchById(participantList0);
        List<Participant> participantList1 = sportParticipantService.generateTarget(1);
        sportParticipantService.updateBatchById(participantList1);

        configService.startMatch(getNowYear());
        return Result.ok("抽签成功！");
    }


    /**
     * 全部列表查询
     *
     * @return
     */
    @ApiOperation(value = "参赛人员-全部列表查询")
    @GetMapping(value = "/queryAll")
    public Result<List<Participant>> queryAll(@RequestParam int year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        List<Participant> participantList = sportParticipantService.list(queryWrapper);
        return Result.ok(participantList);
    }

    @Deprecated
    @ApiOperation(value = "参赛人员-查询抽签情况", notes = "参赛人员-查询抽签情况")
    @GetMapping(value = "/queryGroup")
    public Result<List<List<Participant>>> queryGroup(@RequestParam int sex, @RequestParam int year) {
        if (configService.getStatus(year) < MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("抽签还未完成！");
        }
        List<List<Participant>> participantGroupList = sportParticipantService.getGroup(sex, year);
        return Result.OK(participantGroupList);
    }

    @ApiOperation(value = "参赛人员-查询抽签情况(new)")
    @GetMapping(value = "/queryGroupNew")
    public Result<List<ParticipantInfoGroupVo>> queryGroupNew(@RequestParam int sex, @RequestParam int year) {
        if (configService.getStatus(year) < MatchStatsEnum.MATCHING.getValue()) {
            return Result.error("抽签还未完成！");
        }
        List<List<Participant>> participantGroupList = sportParticipantService.getGroup(sex, year);

        List<ParticipantInfoGroupVo> resultList = new ArrayList<>();
        participantGroupList.forEach(participantList -> {
            ParticipantInfoGroupVo pgVo = new ParticipantInfoGroupVo();
            for (int i = 0; i < participantList.size(); i++) {
                Participant participant = participantList.get(i);
                if (participant == null) continue;
                switch (i) {
                    case 0:
                        pgVo.setLocationA(participant.getLocation());
                        pgVo.setNameA(participant.getName());
                        break;
                    case 1:
                        pgVo.setLocationB(participant.getLocation());
                        pgVo.setNameB(participant.getName());
                        break;
                    case 2:
                        pgVo.setLocationC(participant.getLocation());
                        pgVo.setNameC(participant.getName());
                        break;
                    case 3:
                        pgVo.setLocationD(participant.getLocation());
                        pgVo.setNameD(participant.getName());
                        break;
                }
            }
            resultList.add(pgVo);
        });
        return Result.OK(resultList);
    }


    /**
     * 分页列表查询
     */
    @AutoLog(value = "参赛人员-分页列表查询")
//    @ApiOperation(value = "参赛人员-分页列表查询", notes = "参赛人员-分页列表查询")
//    @GetMapping(value = "/list")
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
