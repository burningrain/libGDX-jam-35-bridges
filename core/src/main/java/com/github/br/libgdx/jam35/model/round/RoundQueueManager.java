package com.github.br.libgdx.jam35.model.round;

import com.badlogic.gdx.utils.Array;

public class RoundQueueManager {

    private RoundGenerator roundGenerator;

    private int currentRound = 0;
    private Array<RoundStep> queue = new Array<>();

    private int currentStep = 0;

    public int getCurrentRound() {
        return currentRound;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getCurrentId() {
        RoundStep roundStep = getRoundStep(currentStep);
        return roundStep.id;
    }

    public void setRoundGenerator(RoundGenerator roundGenerator) {
        this.roundGenerator = roundGenerator;
    }

    public void insertNextStep(int step, int id, boolean isOutOfOrder) {
        queue.insert(step, new RoundStep(id, isOutOfOrder, false));
    }

    public void goToNextStep() {
        currentStep++;

        RoundStep roundStep = getRoundStep(currentStep);
        if (roundStep.isRoundEnd) {
            goToNextRound();
        }
    }

    private void goToNextRound() {
        currentRound++;

        RoundStep roundStep = getRoundStep(currentStep);
        Array<RoundStep> nextSteps = roundGenerator.generateRoundSteps();
        Array<RoundStep> nextRoundQueue = new Array<>();
        nextRoundQueue.add(roundStep);
        nextRoundQueue.addAll(nextSteps);
        currentStep = 0;

        queue = nextRoundQueue;

    }

    public void init() {
        queue = roundGenerator.generateRoundSteps();
    }

    private RoundStep getRoundStep(int step) {
        return queue.get(step);
    }

    public void clear() {
        currentRound = 0;
        queue.clear();
        currentStep = 0;
    }

}
