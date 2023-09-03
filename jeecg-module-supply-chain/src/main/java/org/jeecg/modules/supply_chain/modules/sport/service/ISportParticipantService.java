package org.jeecg.modules.supply_chain.modules.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.entity.Target;

import java.util.List;


public interface ISportParticipantService extends IService<Participant> {
    List<Participant> generateTarget(Integer sex);

    Participant getByIdNumber(String idNumber, Integer year);

    /**
     * 根据靶子查询参赛人员
     */
    Participant getByTarget(Target target, Integer year);

    /**
     * 查询抽签情况
     */
    List<List<Participant>> getGroup(Integer sex, Integer year);

    List<Participant> getListBySexAndYear(Integer sex, Integer year);
}
