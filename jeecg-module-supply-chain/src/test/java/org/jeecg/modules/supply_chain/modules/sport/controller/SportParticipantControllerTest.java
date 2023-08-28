package org.jeecg.modules.supply_chain.modules.sport.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.supply_chain.modules.sport.entity.Participant;

@Slf4j
class SportParticipantControllerTest {

    @org.junit.jupiter.api.Test
    void generateTarget() {
        this.generateTarget(new Participant(), 7);
    }

    private void generateTarget(Participant participant, int targetTotal) {
        // 获取下一个靶子数
        long count = 15;

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
        log.info("{}", participant.getTarget());
    }
}