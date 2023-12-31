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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SportParticipantServiceImpl extends ServiceImpl<SportParticipantMapper, Participant> implements ISportParticipantService {

    @Autowired
    private ISportConfigService sportConfigService;

    @Override
    public List<Participant> generateTarget(Integer sex) {
        LambdaQueryWrapper<Participant> participantLambdaQueryWrapper = new LambdaQueryWrapper<>();
        participantLambdaQueryWrapper.eq(Participant::getSex, sex).orderByAsc(Participant::getLocation);
        long count = this.count(participantLambdaQueryWrapper);


        sportConfigService.setTargetCount(sex, count, LocalDate.now().getYear());

        List<Participant> participantList = this.list(participantLambdaQueryWrapper);

//        按照地区分组
        List<List<Participant>> tmpList = new ArrayList<>();
        participantList.stream().collect(Collectors.groupingBy(Participant::getLocation)).forEach((k, v) -> tmpList.add(v));
        for (List<Participant> participants : tmpList) {
//            打乱各地区内人员的顺序
            participants.sort((o1, o2) -> {
                if (Math.random() > 0.5) {
                    return 1;
                } else {
                    return -1;
                }
            });
        }
//        打乱地区的顺序
        tmpList.sort((o1, o2) -> {
            if (Math.random() > 0.5) {
                return 1;
            } else {
                return -1;
            }
        });
//        扁平化
        participantList = tmpList.stream().flatMap(List::stream).collect(Collectors.toList());

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
                target = targetCount;
                indexInTarget++;
            } else if (target < 1) {
                reverseFlag = false;
                target = 1;
                indexInTarget++;
            }
        }
        return participantList;
    }

    @Override
    public Participant getByIdNumber(String idNumber, Integer year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getIdNumber, idNumber);
        return this.list(queryWrapper).stream().filter(item -> item.getCreateTime().getYear() == year).collect(Collectors.toList()).get(0);
    }

    @Override
    public Participant getByTarget(Target target, Integer year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getTarget, target.getPrefix()).eq(Participant::getIndexInTarget, target.getSuffix());
        return this.list().stream().filter(item -> item.getCreateTime().getYear() == year).collect(Collectors.toList()).get(0);
    }

    @Override
    public List<List<Participant>> getGroup(Integer sex, Integer year) {

        List<Participant> participantList = this.getListBySexAndYear(sex, year);

        Integer targetCount = sportConfigService.getTargetCount(sex, year);
        List<List<Participant>> participantGroupList = new ArrayList<>();
        for (int i = 0; i < targetCount; i++) {
            participantGroupList.add(Arrays.asList(new Participant[4]));
        }

        participantList.forEach(participant -> participantGroupList.get(participant.getTarget() - 1).set(participant.getIndexInTarget() - 1, participant));

        return participantGroupList;
    }

    @Override
    public List<Participant> getListBySexAndYear(Integer sex, Integer year) {
        LambdaQueryWrapper<Participant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Participant::getSex, sex);
        return this.list(queryWrapper).stream().filter(item -> item.getCreateTime().getYear() == year).collect(Collectors.toList());
    }

}
