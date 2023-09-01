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
        return this.setStatus(year, MatchStatsEnum.FINISHED.getValue());
    }

    private Boolean setStatus(int year, int status) {
        LambdaUpdateWrapper<Config> configLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        configLambdaUpdateWrapper.set(Config::getStatus, status)
            .eq(Config::getCreateYear, year);
        return this.update(configLambdaUpdateWrapper);
    }

    @Override
    public Integer getStatus(int year) {
        LambdaQueryWrapper<Config> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Config::getCreateYear, year);
        return this.getOne(queryWrapper).getStatus();
    }

}
