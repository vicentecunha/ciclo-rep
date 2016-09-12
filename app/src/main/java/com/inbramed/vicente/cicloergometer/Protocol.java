package com.inbramed.vicente.cicloergometer;

import java.util.Random;
import java.util.Vector;

public class Protocol {
    private ExerciseType exerciseType = ExerciseType.CONSTANT_LOAD;
    private ProtocolType protocolType = ProtocolType.MANUAL;
    private String name = "";
    private Vector<Double> torqueVector = new Vector<>();
    private Vector<Long> loadVector = new Vector<>();
    private Vector<Long> timeVector = new Vector<>();

    ExerciseType getExerciseType() {return exerciseType;}
    ProtocolType getProtocolType() {return protocolType;}
    String getName() {return name;}
    Vector<Double> getTorqueVector() {return torqueVector;}
    Vector<Long> getLoadVector() {return loadVector;}
    Vector<Long> getTimeVector() {return timeVector;}
    void setExerciseType(ExerciseType exerciseType) {this.exerciseType = exerciseType;}
    void setProtocolType(ProtocolType protocolType) {this.protocolType = protocolType;}
    void setName(String name) {this.name = name;}
    void setTorqueVector(Vector<Double> torqueVector) {this.torqueVector = torqueVector;}
    void setLoadVector(Vector<Long> loadVector) {this.loadVector = loadVector;}
    void setTimeVector(Vector<Long> timeVector) {this.timeVector = timeVector;}

    Protocol() {}

    public long getStageFromTime(long time_s) {
        long totalTime = 0;
        for (int i = 0; i < timeVector.size(); i++) {
            totalTime += timeVector.get(i);
            if (totalTime >= time_s) return i+1;
        }
        return -1;
    }

    public CurrentIntervaledStage getIntervaledStageFromTime(long time_s) {
        long totalTime = 0;
        while (true) {
            totalTime += timeVector.get(0);
            if (totalTime > time_s) return CurrentIntervaledStage.REST;
            totalTime += timeVector.get(1);
            if (totalTime > time_s) return CurrentIntervaledStage.EXERCISE;
        }
    }

    public long getRandomStageFromTime(long time_s) {
        return 1 + time_s/timeVector.get(0);
    }

    public long getMountainStageFromTime(long time_s) {
        return 1 + time_s/timeVector.get(0);
    }

    public long getMountainLoad(long stage) {
        long increaseStep = (loadVector.get(0) - loadVector.get(1)) / (timeVector.get(1) - 1);
        long decreaseStep = (loadVector.get(0) - loadVector.get(1)) / (timeVector.get(2) - 1);

        if (stage == 1 || stage == timeVector.get(1) + timeVector.get(2)) return loadVector.get(1);
        if (stage < timeVector.get(1)) return (stage - 1) * increaseStep + loadVector.get(1);
        if (stage == timeVector.get(1)) return loadVector.get(0);
        if (stage > timeVector.get(1)) return loadVector.get(0)-(stage-timeVector.get(1)-1)*decreaseStep;

        return -1;
    }

    public double getMountainTorque(long stage) {
        double increaseStep = (torqueVector.get(0) - torqueVector.get(1)) / (timeVector.get(1) - 1);
        double decreaseStep = (torqueVector.get(0) - torqueVector.get(1)) / (timeVector.get(2) - 1);

        if (stage == 1 || stage == timeVector.get(1) + timeVector.get(2)) return torqueVector.get(1);
        if (stage < timeVector.get(1)) return (stage - 1) * increaseStep + torqueVector.get(1);
        if (stage == timeVector.get(1)) return torqueVector.get(0);
        if (stage > timeVector.get(1)) return torqueVector.get(0)-(stage-timeVector.get(1)-1)*decreaseStep;

        return -1;
    }

    public long getRandomLoad() {
        Random random = new Random();
        long randomLoad = (long) (loadVector.get(0) + (double)loadVector.get(1)*random.nextGaussian());
        if (randomLoad < MainActivity.MIN_LOAD_W) {return MainActivity.MIN_LOAD_W;}
        if (randomLoad > MainActivity.MAX_LOAD_W) {return MainActivity.MAX_LOAD_W;}
        return randomLoad;
    }

    public double getRandomTorque() {
        Random random = new Random();
        double randomTorque = torqueVector.get(0) + torqueVector.get(1)*random.nextGaussian();
        if (randomTorque < MainActivity.MIN_TORQUE_kgfm) {return MainActivity.MAX_TORQUE_kgfm;}
        if (randomTorque > MainActivity.MAX_TORQUE_kgfm) {return MainActivity.MAX_LOAD_W;}
        return randomTorque;
    }

    public long getRampLoad(long currentTime_s) {
        return loadVector.get(1) +
                (loadVector.get(0) - loadVector.get(1))*currentTime_s/timeVector.get(0);
    }

    public double getRampTorque(long currentTime_s) {
        return torqueVector.get(1) +
                (torqueVector.get(0) - torqueVector.get(1))*currentTime_s/timeVector.get(0);
    }

    long getMaxLoad() {
        long maxValue = 0;
        for (int i = 0; i < loadVector.size(); i++) {
            if (loadVector.get(i) > maxValue) maxValue = loadVector.get(i);
        }
        return maxValue;
    }

    double getMaxTorque() {
        double maxValue = 0;
        for (int i = 0; i < torqueVector.size(); i++) {
            if (torqueVector.get(i) > maxValue) maxValue = torqueVector.get(i);
        }
        return maxValue;
    }

    long getTotalTime() {
        switch (protocolType) {
            case MANUAL: return Long.MAX_VALUE;
            case INTERVALED: return Long.MAX_VALUE;
            case MOUNTAIN: return (timeVector.get(1) + timeVector.get(2))*timeVector.get(0);
            case RANDOM: return Long.MAX_VALUE;
            default:
                long totalTime = 0;
                for (int i = 0; i < timeVector.size(); i++) totalTime += timeVector.get(i);
                return totalTime;
        }
    }

    enum ExerciseType {
        CONSTANT_LOAD,
        CONSTANT_TORQUE
    }

    enum ProtocolType {
        MANUAL,
        STAGES,
        INTERVALED,
        RANDOM,
        RAMP,
        MOUNTAIN
    }

    enum CurrentIntervaledStage {
        REST,
        EXERCISE
    }

    enum CurrentMountainStage {
        INCREASING,
        DECREASING
    }
}
