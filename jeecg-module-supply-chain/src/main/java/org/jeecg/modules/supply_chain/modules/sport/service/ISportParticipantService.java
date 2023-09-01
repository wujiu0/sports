package org.jeecg.modules.supply_chain.modules.sport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.entity.Target;

import java.util.List;


public interface ISportParticipantService extends IService<Participant> {
    List<Participant> generateTarget(int sex);

    Participant getByIdNumber(String idNumber, int year);

    /**
     * 根据靶子查询参赛人员
     */
    Participant getByTarget(Target target, int year);

    /**
     * 查询分组情况
     */
    List<List<Participant>> getGroup(int sex, int year);
}
