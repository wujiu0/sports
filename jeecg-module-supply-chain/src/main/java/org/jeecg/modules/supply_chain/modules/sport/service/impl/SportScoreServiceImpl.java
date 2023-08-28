package org.jeecg.modules.supply_chain.modules.sport.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.supply_chain.modules.sport.entity.Score;
import org.jeecg.modules.supply_chain.modules.sport.mapper.SportScoreMapper;
import org.jeecg.modules.supply_chain.modules.sport.service.ISportScoreService;
import org.springframework.stereotype.Service;

@Service
public class SportScoreServiceImpl extends ServiceImpl<SportScoreMapper, Score> implements ISportScoreService {
}
