package org.jeecg.modules.supply_chain.modules.sport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;
import org.jeecg.modules.supply_chain.modules.sport.entity.Target;
import org.jeecg.modules.supply_chain.modules.sport.mapper.SportParticipantMapper;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportConfigService;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SportParticipantServiceImpl extends ServiceImpl<SportParticipantMapper, Participant> implements ISportParticipantService {

    @Autowired
    private ISportConfigService sportConfigService;

    @Override
    public List<Participant> generateTarget(int sex) {
        LambdaQueryWrapper<Participant> participantLambdaQueryWrapper = new LambdaQueryWrapper<>();
        participantLambdaQueryWrapper.eq(Participant::getSex, sex)
            .orderByAsc(Participant::getLocation);
        long count = this.count(participantLambdaQueryWrapper);


        sportConfigService.setTargetCount(sex, count, LocalDate.now().getYear());

        List<Participant> participantList = this.list(participantLambdaQueryWrapper);
        int targetCount = sportConfigService.getTargetCount(sex, LocalDate.now().getYear());
        int target = 1;
        int indexInTarget = 1;
        boolean reverseFlag = false;
        for (Participant participant : participantList) {
            if (!reverseFlag) {
                participant.setTarget(target++);
            } else {
                participant.setTarget(target--);
            }
            participant.setIndexInTarget(indexInTarget);

            if (target > targetCount) {
                reverseFlag = true;
                indexInTarget++;
            } else if (target < 1) {
                reverseFlag = false;
                indexInTarget++;
            }
        }
        return participantList;
    }

    @Override
    public Participant getByIdNumber(String idNumber, int year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getIdNumber, idNumber);
        return this.list(queryWrapper).stream().filter(item -> item.getCreateTime().getYear() == year).collect(Collectors.toList()).get(0);
    }

    @Override
    public Participant getByTarget(Target target, int year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getTarget, target.getPrefix())
            .eq(Participant::getIndexInTarget, target.getSuffix());
        return this.list().stream().filter(item -> item.getCreateTime().getYear() == year).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<List<Participant>> getGroup(int sex, int year) {

        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getSex, sex)
            .orderByAsc(Participant::getTarget)
            .orderByAsc(Participant::getId);

        List<Participant> participantList = this.list(queryWrapper);
        Integer targetCount = sportConfigService.getTargetCount(sex, year);
        List<List<Participant>> participantGroupList = new ArrayList<>(targetCount);
        for (int i = 0; i < targetCount; i++) {
            participantGroupList.add(new ArrayList<>());
        }
        participantList.forEach(participant -> {
            participantGroupList.get(participant.getTarget() - 1).add(participant);
        });

        return participantGroupList;
    }

}
