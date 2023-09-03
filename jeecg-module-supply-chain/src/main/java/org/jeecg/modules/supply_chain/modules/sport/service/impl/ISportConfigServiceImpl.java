package org.jeecg.modules.supply_chain.modules.sport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.supply_chain.modules.sport.enums.MatchStatsEnum;
import org.jeecg.modules.supply_chain.modules.sport.entity.Config;
import org.jeecg.modules.supply_chain.modules.sport.enums.SexEnum;
import org.jeecg.modules.supply_chain.modules.sport.mapper.SportConfigMapper;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportConfigService;
import org.springframework.stereotype.Service;

@Service
public class ISportConfigServiceImpl extends ServiceImpl<SportConfigMapper, Config> implements ISportConfigService {

    @Override
    public Integer getTargetCount(int sex, int year) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getCreateYear, year);
        Config config = this.getOne(queryWrapper);
        return sex == SexEnum.MALE.getValue() ? config.getMaleTargetCount() : config.getFemaleTargetCount();
    }


    @Override
    public Boolean setTargetCount(int sex, long count, int year) {

        int targetCount = (int) (count / 4) + 1;

        LambdaUpdateWrapper<Config> configLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        configLambdaUpdateWrapper.eq(Config::getCreateYear, year)
            .set(sex == SexEnum.MALE.getValue() ? Config::getMaleTargetCount : Config::getFemaleTargetCount, targetCount);

        return this.update(configLambdaUpdateWrapper);
    }

    @Override
    public Boolean start(int year) {
        return this.setStatus(year, MatchStatsEnum.PROGRESS.getValue());
    }

    @Override
    public Boolean close(int year) {
        return this.setStatus(year, MatchStatsEnum.GROUPING.getValue());
    }

    @Override
    public Boolean startMatch(int year) {
        return this.setStatus(year, MatchStatsEnum.MATCHING.getValue());
    }

    @Override
    public Integer nextRound(int year) {
        Config config = this.getByYear(year);
        if (config.getRound() == MAX_ROUND) {
            return 0;
        } else if (config.getGroup() != MAX_GROUP) {
            return -1;
        }
        LambdaUpdateWrapper<Config> configLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        configLambdaUpdateWrapper.set(Config::getRound, config.getRound() + 1)
            .set(Config::getGroup, 1)
            .eq(Config::getCreateYear, year);
        this.update(configLambdaUpdateWrapper);
        return config.getRound();
    }

    @Override
    public Integer nextGroup(int year) {
        Integer group = this.getByYear(year).getGroup() + 1;
        if (group > MAX_GROUP) {
            return -1;
        }
        LambdaUpdateWrapper<Config> configLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        configLambdaUpdateWrapper.set(Config::getGroup, group)
            .eq(Config::getCreateYear, year);
        this.update(configLambdaUpdateWrapper);
        return group;
    }

    @Override
    public Boolean finish(int year) {
        return this.setStatus(year, MatchStatsEnum.FINISHED.getValue());
    }

    @Override
    public Config getByYear(int year) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getCreateYear, year);
        return this.getOne(queryWrapper);
    }

    private Boolean setStatus(int year, int status) {
        LambdaUpdateWrapper<Config> configLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        configLambdaUpdateWrapper.set(Config::getStatus, status)
            .eq(Config::getCreateYear, year);
        return this.update(configLambdaUpdateWrapper);
    }

    @Override
    public Integer getStatus(int year) {
        return this.getByYear(year).getStatus();
    }

    private final int MAX_ROUND = 4;
    private final int MAX_GROUP = 6;

}
