package com.inbramed.vicente.cicloergometer;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //==================================================
    // CONSTANTS
    //==================================================

    final static String INBRAMED_PROTOCOLS = "INBRAMED_CICLO_PROTOCOLS";
    final static String SHARED_PREFERENCES = "INBRAMED_CICLO";
    final static String LOAD_LABEL = "Carga: ";
    final static String TORQUE_LABEL = "Torque: ";
    final static String ONE_WATT_STEP = "1 Watt";
    final static String FIVE_WATTS_STEP = "5 Watts";
    final static String TEN_WATTS_STEP = "10 Watts";
    final static String ONE_CKGFM_STEP = "0.01 kgfm";
    final static String FIVE_CKGFM_STEP = "0.05 kgfm";
    final static String TEN_CKGFM_STEP = "0.10 kgfm";
    final static String PRE_TRAIN = "pré-treino";
    final static String POST_TRAIN = "pós-treino";
    final static String EXERCISE = "exercício";
    final static String REST = "descanso";
    final static String PRE_TRAIN_STRING = "PRÉ-TREINO";
    final static String POST_TRAIN_STRING = "PÓS-TREINO";
    final static String MANUAL_STRING = "Manual";

    final static double MIN_TORQUE_kgfm = 0;
    final static double MAX_TORQUE_kgfm = 4;
    final static long MIN_LOAD_W = 0;
    final static long MAX_LOAD_W = 400;

    //==================================================
    // STATIC FIELDS
    //==================================================

    static boolean outputShouldStop = false;
    static boolean preTrainEnable = false;
    static boolean postTrainEnable = false;
    static boolean preTrainWasInitialized = false;
    static boolean postTrainWasInitialized = false;
    static char[] inputBuffer = new char[]{'0','0','0','0','0','0'};
    static double torque_kgfm = 0;
    static double torqueStep_kgfm = 0.05;
    static int inputBufferIndex = 0;
    static long load_W = 0;
    static long loadStep_W = 5;
    static long speed_rpm = 0;
    static long stage = 1;
    static long totalTime_s = 0;
    static long totalTimeNoPre_s = 0;
    static long oldStage = 0;

    static Gson gson = new Gson();
    static SerialPort serialPort;
    static SharedPreferences sharedPreferences;
    static Vector<String> protocolNames = new Vector<>();

    static Protocol.CurrentIntervaledStage currentIntervaledStage =
            Protocol.CurrentIntervaledStage.REST;
    static Protocol.CurrentIntervaledStage oldIntervaledStage =
            Protocol.CurrentIntervaledStage.REST;

    static Protocol g_protocol = new Protocol();
    static Protocol preTrain = new Protocol();
    static Protocol postTrain = new Protocol();
    static Window g_currentWindow = Window.MAIN;

    //==================================================
    // VIEWS FIELDS
    //==================================================

    static ImageButton mw_loadUpButton;
    static ImageButton mw_loadDownButton;
    static RadioButton mw_oneWattIncrement;
    static RadioButton mw_fiveWattIncrement;
    static RadioButton mw_tenWattIncrement;
    static RadioButton mw_constantLoad;
    static RadioButton mw_constantTorque;
    static RadioGroup mw_exerciseTypeGroup;
    static RadioGroup mw_loadStepGroup;
    static TextView mw_continueButton;
    static TextView mw_exerciseTypeLabel;
    static TextView mw_loadLabel;
    static TextView mw_loadStepLabel;
    static TextView mw_loadValue;
    static TextView mw_pauseButton;
    static TextView mw_protocolButton;
    static TextView mw_protocolLabel;
    static TextView mw_protocolLoadLabel;
    static TextView mw_protocolLoadValue;
    static TextView mw_protocolValue;
    static TextView mw_runButton;
    static TextView mw_speedLabel;
    static TextView mw_speedValue;
    static TextView mw_stageLabel;
    static TextView mw_stageValue;
    static TextView mw_stopButton;
    static TextView mw_timeLabel;
    static TextView mw_timeValue;
    static TextView mw_torqueLabel;
    static TextView mw_torqueValue;
    static TextView mw_protocolValueBig;
    static TextView mw_preTrainButton;
    static TextView mw_postTrainButton;
    static ToggleButton mw_preTrainEnable;
    static ToggleButton mw_postTrainEnable;

    //==================================================
    // STATIC METHODS
    //==================================================

    static public void setVisibility(int visibility) {
        mw_protocolButton.setVisibility(visibility);
        mw_runButton.setVisibility(visibility);
        mw_torqueLabel.setVisibility(visibility);
        mw_torqueValue.setVisibility(visibility);
        mw_speedLabel.setVisibility(visibility);
        mw_speedValue.setVisibility(visibility);
        mw_timeLabel.setVisibility(visibility);
        mw_timeValue.setVisibility(visibility);
        switch (g_protocol.getProtocolType()) {
            case MANUAL:
                mw_loadLabel.setVisibility(visibility);
                mw_loadValue.setVisibility(visibility);
                mw_loadUpButton.setVisibility(visibility);
                mw_loadDownButton.setVisibility(visibility);
                mw_loadStepLabel.setVisibility(visibility);
                mw_loadStepGroup.setVisibility(visibility);
                mw_oneWattIncrement.setVisibility(visibility);
                mw_fiveWattIncrement.setVisibility(visibility);
                mw_tenWattIncrement.setVisibility(visibility);
                mw_exerciseTypeLabel.setVisibility(visibility);
                mw_exerciseTypeGroup.setVisibility(visibility);
                mw_constantLoad.setVisibility(visibility);
                mw_constantTorque.setVisibility(visibility);
                mw_protocolLabel.setVisibility(visibility);
                mw_protocolValue.setVisibility(visibility);
                break;
            case STAGES:
                mw_preTrainButton.setVisibility(visibility);
                mw_postTrainButton.setVisibility(visibility);
                mw_preTrainEnable.setVisibility(visibility);
                mw_postTrainEnable.setVisibility(visibility);
                mw_protocolLoadLabel.setVisibility(visibility);
                mw_protocolLoadValue.setVisibility(visibility);
                mw_stageLabel.setVisibility(visibility);
                mw_stageValue.setVisibility(visibility);
                mw_protocolValueBig.setVisibility(visibility);
                break;
            case MOUNTAIN:
                mw_preTrainButton.setVisibility(visibility);
                mw_postTrainButton.setVisibility(visibility);
                mw_preTrainEnable.setVisibility(visibility);
                mw_postTrainEnable.setVisibility(visibility);
                mw_protocolLoadLabel.setVisibility(visibility);
                mw_protocolLoadValue.setVisibility(visibility);
                mw_stageLabel.setVisibility(visibility);
                mw_stageValue.setVisibility(visibility);
                mw_protocolValueBig.setVisibility(visibility);
                break;
            case RAMP:
                mw_preTrainButton.setVisibility(visibility);
                mw_postTrainButton.setVisibility(visibility);
                mw_preTrainEnable.setVisibility(visibility);
                mw_postTrainEnable.setVisibility(visibility);
                mw_protocolLoadLabel.setVisibility(visibility);
                mw_protocolLoadValue.setVisibility(visibility);
                mw_stageLabel.setVisibility(visibility);
                mw_stageValue.setVisibility(visibility);
                mw_protocolValueBig.setVisibility(visibility);
                break;
            default:
                mw_preTrainButton.setVisibility(visibility);
                mw_preTrainEnable.setVisibility(visibility);
                mw_protocolLoadLabel.setVisibility(visibility);
                mw_protocolLoadValue.setVisibility(visibility);
                mw_stageLabel.setVisibility(visibility);
                mw_stageValue.setVisibility(visibility);
                mw_protocolValueBig.setVisibility(visibility);
                break;
        }
    }

    /*
        Variable increase and decrease methods.
     */

    static long increaseLong(long variable, long step, long maxValue) {
        long newValue = variable + step;
        if (newValue <= maxValue) return newValue;
        else return variable;
    }

    static long decreaseLong(long variable, long step, long  minValue) {
        long newValue = variable - step;
        if (newValue >= minValue) return newValue;
        else return variable;
    }

    static double increaseDouble(double variable, double step, double maxValue) {
        double newValue = variable + step;
        if (newValue <= maxValue) return newValue;
        else return variable;
    }

    static double decreaseDouble(double variable, double step, double  minValue) {
        double newValue = variable - step;
        if (newValue >= minValue) return newValue;
        else return variable;
    }

    /*
        Configures this class labels for different ExerciseTypes.
     */

    static void updateVariables() {
        switch (g_protocol.getProtocolType()) {
            case MANUAL:
                switch (g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        mw_loadLabel.setText(LOAD_LABEL);
                        mw_torqueLabel.setText(TORQUE_LABEL);
                        mw_oneWattIncrement.setText(ONE_WATT_STEP);
                        mw_fiveWattIncrement.setText(FIVE_WATTS_STEP);
                        mw_tenWattIncrement.setText(TEN_WATTS_STEP);
                        mw_constantLoad.setChecked(true);
                        break;
                    case CONSTANT_TORQUE:
                        mw_loadLabel.setText(TORQUE_LABEL);
                        mw_torqueLabel.setText(LOAD_LABEL);
                        mw_oneWattIncrement.setText(ONE_CKGFM_STEP);
                        mw_fiveWattIncrement.setText(FIVE_CKGFM_STEP);
                        mw_tenWattIncrement.setText(TEN_CKGFM_STEP);
                        mw_constantTorque.setChecked(true);
                        break;
                }
                break;
            default:
                mw_protocolLoadLabel.setText(LOAD_LABEL);
                mw_torqueLabel.setText(TORQUE_LABEL);
                break;
        }
        updateLoad();
        updateTorque();
    }

    /*
        Text formatting methods.
     */

    static void updateLoad() {
        TextView textView;
        switch (g_protocol.getProtocolType()) {
            case MANUAL:
                switch (g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD: textView = mw_loadValue; break;
                    case CONSTANT_TORQUE: textView = mw_torqueValue; break;
                    default: textView = mw_protocolLoadValue; break;
                }
                break;
            default: textView = mw_protocolLoadValue; break;
        }
        textView.setText(String.format(Locale.US, "%d W", load_W));
    }

    static void updateTorque() {
        TextView textView;
        switch (g_protocol.getProtocolType()) {
            case MANUAL:
                switch (g_protocol.getExerciseType()) {
                    case CONSTANT_TORQUE: textView = mw_loadValue; break;
                    default: textView = mw_torqueValue;
                }
                break;
            default: textView = mw_torqueValue; break;
        }
        textView.setText(String.format(Locale.US, "%.2f kgfm", torque_kgfm));
    }

    static void updateTime() {
        long minutes = (totalTime_s % 3600)/60;
        long seconds = totalTime_s % 60;
        if (totalTime_s >= 3600) {
            long hours = totalTime_s / 3600;
            mw_timeValue.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else mw_timeValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void updateSpeed() {
        mw_speedValue.setText(String.format(Locale.US, "%d rpm", speed_rpm));
    }

    static void updateStage() {
        mw_stageValue.setText(String.format(Locale.US, "%d", stage));
    }

    static void updateProtocol() {
        switch (g_protocol.getProtocolType()) {
            case MANUAL: mw_protocolValue.setText(MANUAL_STRING); break;
            default: mw_protocolValueBig.setText(g_protocol.getName()); break;
        }
    }

    /*
        Shared preferences methods.
     */

    static void saveProtocol() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(g_protocol.getName(), gson.toJson(g_protocol));
        editor.apply();
    }

    static void savePreTrain() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ProtocolEditor.PRE_TRAIN_STR, gson.toJson(preTrain));
        editor.apply();
    }

    static void savePostTrain() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ProtocolEditor.POST_TRAIN_STR, gson.toJson(postTrain));
        editor.apply();
    }

    static void removeProtocol(String string) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(string);
        editor.apply();
    }

    static Protocol openProtocol(String string, Protocol original) {
        Protocol protocol = gson.fromJson(sharedPreferences.getString(string, ""), Protocol.class);
        if (protocol != null) return protocol;
        return original;
    }

    static void saveProtocolNames() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(INBRAMED_PROTOCOLS, gson.toJson(protocolNames));
        editor.apply();
    }

    static Vector<String> openProtocolNames(Vector<String> original) {
        Vector<String> stringVector =
                gson.fromJson(sharedPreferences.getString(INBRAMED_PROTOCOLS, ""), Vector.class);
        if (stringVector != null) return stringVector;
        else return original;
    }

    /*
        Serial port methods.
     */

    static void sendLoad(long load, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                outputStream.write((byte) 0xA0);
                outputStream.write(String.format(Locale.US, "%04d", load).getBytes());
            } catch (IOException e) {e.printStackTrace();}
        }
    }

    static void initializePreTrain() {
        preTrain.setProtocolType(Protocol.ProtocolType.STAGES);
        preTrain.setExerciseType(g_protocol.getExerciseType());
        switch (preTrain.getExerciseType()) {
            case CONSTANT_LOAD:
                Vector<Long> loadVector = new Vector<>();
                loadVector.add((long) (0.4 * g_protocol.getMaxLoad()));
                loadVector.add((long) (0.6 * g_protocol.getMaxLoad()));
                loadVector.add((long) (0.8 * g_protocol.getMaxLoad()));
                preTrain.setLoadVector(loadVector);
                break;
            case CONSTANT_TORQUE:
                Vector<Double> torqueVector = new Vector<>();
                torqueVector.add(0.4 * g_protocol.getMaxTorque());
                torqueVector.add(0.6 * g_protocol.getMaxTorque());
                torqueVector.add(0.8 * g_protocol.getMaxTorque());
                preTrain.setTorqueVector(torqueVector);
                break;
        }
        Vector<Long> timeVector = new Vector<>();
        for (int i = 0; i < 3; i++) timeVector.add(60L);
        preTrain.setTimeVector(timeVector);
        savePreTrain();
        preTrainWasInitialized = true;
    }

    static void initializePostTrain() {
        postTrain.setProtocolType(Protocol.ProtocolType.STAGES);
        postTrain.setExerciseType(g_protocol.getExerciseType());
        switch (postTrain.getExerciseType()) {
            case CONSTANT_LOAD:
                Vector<Long> loadVector = new Vector<>();
                loadVector.add((long) (0.5 * g_protocol.getMaxLoad()));
                loadVector.add((long) (0.3 * g_protocol.getMaxLoad()));
                postTrain.setLoadVector(loadVector);
                break;
            case CONSTANT_TORQUE:
                Vector<Double> torqueVector = new Vector<>();
                torqueVector.add(0.5 * g_protocol.getMaxTorque());
                torqueVector.add(0.3 * g_protocol.getMaxTorque());
                postTrain.setTorqueVector(torqueVector);
                break;
        }
        Vector<Long> timeVector = new Vector<>();
        for (int i = 0; i < 2; i++) timeVector.add(60L);
        postTrain.setTimeVector(timeVector);
        savePostTrain();
        postTrainWasInitialized = true;
    }

    //==================================================
    // OVERRIDES
    //==================================================

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return keyCode == KeyEvent.KEYCODE_BACK;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        /*
            Class constructors.
         */

        final ProtocolSelector protocolSelector = new ProtocolSelector(this);
        final ProtocolEditor protocolEditor = new ProtocolEditor(this);
        final IntervaledEditor intervaledEditor = new IntervaledEditor(this);
        final RandomEditor randomEditor = new RandomEditor(this);
        final RampEditor rampEditor = new RampEditor(this);
        final MountainEditor mountainEditor = new MountainEditor(this);

        /*
            View assignments.
         */

        mw_loadUpButton = (ImageButton) findViewById(R.id.mw_loadUpButton);
        mw_loadDownButton = (ImageButton) findViewById(R.id.mw_loadDownButton);
        mw_continueButton = (TextView) findViewById(R.id.mw_continueButton);
        mw_exerciseTypeLabel = (TextView) findViewById(R.id.mw_exerciseTypeLabel);
        mw_loadLabel = (TextView) findViewById(R.id.mw_loadLabel);
        mw_loadValue = (TextView) findViewById(R.id.mw_loadValue);
        mw_loadStepLabel = (TextView) findViewById(R.id.mw_loadStepLabel);
        mw_pauseButton = (TextView) findViewById(R.id.mw_pauseButton);
        mw_protocolButton = (TextView) findViewById(R.id.mw_protocolButton);
        mw_protocolLabel = (TextView) findViewById(R.id.mw_protocolLabel);
        mw_protocolLoadLabel = (TextView) findViewById(R.id.mw_protocolLoadLabel);
        mw_protocolLoadValue = (TextView) findViewById(R.id.mw_protocolLoadValue);
        mw_protocolValue = (TextView) findViewById(R.id.mw_protocolValue);
        mw_runButton = (TextView) findViewById(R.id.mw_runButton);
        mw_speedLabel = (TextView) findViewById(R.id.mw_speedLabel);
        mw_speedValue = (TextView) findViewById(R.id.mw_speedValue);
        mw_stageLabel = (TextView) findViewById(R.id.mw_stageLabel);
        mw_stageValue = (TextView) findViewById(R.id.mw_stageValue);
        mw_stopButton = (TextView) findViewById(R.id.mw_stopButton);
        mw_timeLabel = (TextView) findViewById(R.id.mw_timeLabel);
        mw_timeValue = (TextView) findViewById(R.id.mw_timeValue);
        mw_torqueLabel = (TextView) findViewById(R.id.mw_torqueLabel);
        mw_torqueValue = (TextView) findViewById(R.id.mw_torqueValue);
        mw_protocolValueBig = (TextView) findViewById(R.id.mw_protocolValueBig);
        mw_oneWattIncrement = (RadioButton) findViewById(R.id.mw_oneWattIncrement);
        mw_fiveWattIncrement = (RadioButton) findViewById(R.id.mw_fiveWattIncrement);
        mw_tenWattIncrement = (RadioButton) findViewById(R.id.mw_tenWattIncrement);
        mw_constantLoad = (RadioButton) findViewById(R.id.mw_constantLoad);
        mw_constantTorque = (RadioButton) findViewById(R.id.mw_constantTorque);
        mw_exerciseTypeGroup = (RadioGroup) findViewById(R.id.mw_exerciseTypeGroup);
        mw_loadStepGroup = (RadioGroup) findViewById(R.id.mw_loadStepGroup);
        mw_preTrainButton = (TextView) findViewById(R.id.mw_preTrainButton);
        mw_postTrainButton = (TextView) findViewById(R.id.mw_postTrainButton);
        mw_preTrainEnable = (ToggleButton) findViewById(R.id.mw_enablePreTrain);
        mw_postTrainEnable = (ToggleButton) findViewById(R.id.mw_enablePostTrain);

        /*
            Shared preferences.
         */

        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        /*
            Serial port.
         */

        try {serialPort = new SerialPort(new File("/dev/ttyO1"), 9600, 0);}
        catch (IOException e) {e.printStackTrace();}
        final BufferedReader bufferedReader = serialPort != null ?
                new BufferedReader((new InputStreamReader(serialPort.getInputStream()))) : null;
        final OutputStream outputStream = serialPort != null ? serialPort.getOutputStream() : null;

        final CountDownTimer outTimer = new CountDownTimer(1000, 100) {
            public void onTick(long millisUntilFinished) {
                outputShouldStop = false;
                if (g_protocol.getProtocolType() == Protocol.ProtocolType.MANUAL &&
                        g_protocol.getExerciseType() == Protocol.ExerciseType.CONSTANT_TORQUE) {
                    load_W = (long) (torque_kgfm * speed_rpm);
                    updateLoad();
                } else if (preTrainEnable && totalTime_s <= preTrain.getTotalTime()) {
                    long preTrainStage = preTrain.getStageFromTime(totalTime_s);
                    switch (preTrain.getExerciseType()) {
                        case CONSTANT_LOAD:
                            load_W = preTrain.getLoadVector().get((int) preTrainStage - 1);
                            break;
                        case CONSTANT_TORQUE:
                            torque_kgfm = preTrain.getTorqueVector().get((int) preTrainStage - 1);
                            load_W = (long) (torque_kgfm * speed_rpm);
                            updateTorque();
                            break;
                    }
                    mw_stageValue.setText(PRE_TRAIN);
                    totalTimeNoPre_s = 0;
                } else if (postTrainEnable && totalTimeNoPre_s > g_protocol.getTotalTime()) {
                    long postTrainStage = postTrain.getStageFromTime(totalTimeNoPre_s - g_protocol.getTotalTime());
                    if (postTrainStage != -1) {
                        switch (postTrain.getExerciseType()) {
                            case CONSTANT_LOAD:
                                load_W = postTrain.getLoadVector().get((int) postTrainStage - 1);
                                break;
                            case CONSTANT_TORQUE:
                                torque_kgfm = postTrain.getTorqueVector().get((int) postTrainStage - 1);
                                load_W = (long) (torque_kgfm * speed_rpm);
                                updateTorque();
                                break;
                        }
                        mw_stageValue.setText(POST_TRAIN);
                    } else outputShouldStop = true;
                } else if (totalTimeNoPre_s <= g_protocol.getTotalTime()) {
                    switch (g_protocol.getProtocolType()) {
                        case STAGES:
                            stage = g_protocol.getStageFromTime(totalTimeNoPre_s);
                            if (stage != -1) {
                                updateStage();
                                switch (g_protocol.getExerciseType()) {
                                    case CONSTANT_LOAD:
                                        load_W = g_protocol.getLoadVector().get((int) stage - 1);
                                        break;
                                    case CONSTANT_TORQUE:
                                        torque_kgfm = g_protocol.getTorqueVector().get((int) stage - 1);
                                        load_W = (long) (torque_kgfm * speed_rpm);
                                        updateTorque();
                                        break;
                                }
                                break;
                            }

                        case INTERVALED:
                            currentIntervaledStage = g_protocol.getIntervaledStageFromTime(totalTimeNoPre_s);
                            if (oldIntervaledStage != currentIntervaledStage) {
                                stage++;
                                oldIntervaledStage = currentIntervaledStage;
                            }
                            switch (currentIntervaledStage) {
                                case REST:
                                    switch (g_protocol.getExerciseType()) {
                                        case CONSTANT_LOAD:
                                            load_W = g_protocol.getLoadVector().get(0);
                                            break;
                                        case CONSTANT_TORQUE:
                                            torque_kgfm = g_protocol.getTorqueVector().get(0);
                                            load_W = (long) (torque_kgfm * speed_rpm);
                                            updateTorque();
                                            break;
                                    }
                                    mw_stageValue.setText(REST);
                                    break;
                                case EXERCISE:
                                    switch (g_protocol.getExerciseType()) {
                                        case CONSTANT_LOAD:
                                            load_W = g_protocol.getLoadVector().get(1);
                                            break;
                                        case CONSTANT_TORQUE:
                                            torque_kgfm = g_protocol.getTorqueVector().get(1);
                                            load_W = (long) (torque_kgfm * speed_rpm);
                                            updateTorque();
                                            break;
                                    }
                                    mw_stageValue.setText(EXERCISE);
                                    break;
                            }
                            break;

                        case RANDOM:
                            stage = g_protocol.getRandomStageFromTime(totalTimeNoPre_s);
                            if (stage != oldStage) {
                                oldStage = stage;
                                switch (g_protocol.getExerciseType()) {
                                    case CONSTANT_LOAD:
                                        load_W = g_protocol.getRandomLoad();
                                        break;
                                    case CONSTANT_TORQUE:
                                        torque_kgfm = g_protocol.getRandomTorque();
                                        load_W = (long) (torque_kgfm * speed_rpm);
                                        updateTorque();
                                        break;
                                }
                            }
                            updateStage();
                            break;

                        case RAMP:
                            mw_stageValue.setText("rampa");
                            switch (g_protocol.getExerciseType()) {
                                case CONSTANT_LOAD:
                                    load_W = g_protocol.getRampLoad(totalTimeNoPre_s);
                                    break;
                                case CONSTANT_TORQUE:
                                    torque_kgfm = g_protocol.getRampTorque(totalTimeNoPre_s);
                                    load_W = (long) (torque_kgfm * speed_rpm);
                                    updateTorque();
                                    break;
                            }
                            break;

                        case MOUNTAIN:
                            stage = g_protocol.getMountainStageFromTime(totalTimeNoPre_s);
                            if (stage != -1) {
                                updateStage();
                                switch (g_protocol.getExerciseType()) {
                                    case CONSTANT_LOAD:
                                        load_W = g_protocol.getMountainLoad(stage);
                                        break;
                                    case CONSTANT_TORQUE:
                                        torque_kgfm = g_protocol.getMountainTorque(stage);
                                        load_W = (long) (torque_kgfm * speed_rpm);
                                        updateTorque();
                                        break;
                                }
                            }
                            break;
                    }
                } else outputShouldStop = true;
                updateLoad();
                sendLoad(load_W, outputStream);
            }
            public void onFinish() {
                totalTime_s++;
                totalTimeNoPre_s++;
                updateTime();
                if (!outputShouldStop) this.start();
                else mw_stopButton.callOnClick();
            }
        };

        final CountDownTimer inTimer = new CountDownTimer(1000, 100) {
            public void onTick(long millisUntilFinished) {
                if (bufferedReader != null) {
                    try {
                        while (bufferedReader.ready()) {
                            int c = bufferedReader.read();
                            if ('B' == (char) c) {
                                try {
                                    speed_rpm = Long.parseLong(new String(Arrays.copyOfRange(inputBuffer, 0, 3)));
                                    updateSpeed();
                                    if (g_protocol.getExerciseType() == Protocol.ExerciseType.CONSTANT_LOAD) {
                                        double newTorque = ((double) load_W) / ((double) speed_rpm);
                                        if (Double.isNaN(newTorque)) torque_kgfm = MIN_TORQUE_kgfm;
                                        else if (newTorque > MAX_TORQUE_kgfm)
                                            torque_kgfm = MAX_TORQUE_kgfm;
                                        else if (newTorque < MIN_TORQUE_kgfm)
                                            torque_kgfm = MIN_TORQUE_kgfm;
                                        else torque_kgfm = newTorque;
                                        updateTorque();
                                    }
                                } catch (NumberFormatException e) {e.printStackTrace();}
                                /* TODO cicloergometer fails to send torque correctly
                                if (g_protocol.getExerciseType() == Protocol.ExerciseType.CONSTANT_LOAD) {
                                    try {
                                        torque_kgfm = Double.parseDouble(new String(Arrays.copyOfRange(inputBuffer,3,4)))
                                                + 0.01*Double.parseDouble(new String(Arrays.copyOfRange(inputBuffer,4,6)));
                                        if (torque_kgfm > MAX_TORQUE_kgfm) torque_kgfm = MAX_TORQUE_kgfm;
                                        else if (torque_kgfm < MIN_TORQUE_kgfm) torque_kgfm = MIN_TORQUE_kgfm;
                                        updateTorque();
                                    } catch (NumberFormatException e) {e.printStackTrace();}
                                }
                                */
                                inputBufferIndex = 0;
                            } else if (c != -1 && inputBufferIndex < inputBuffer.length) {
                                inputBuffer[inputBufferIndex] = (char) c;
                                inputBufferIndex++;
                            }
                        }
                    } catch (IOException e) {e.printStackTrace();}
                }
            }
            public void onFinish() {this.start();}
        }.start();

        //==================================================
        // LISTENERS
        //==================================================

        mw_loadStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (mw_oneWattIncrement.isChecked()) {
                    loadStep_W = 1;
                    torqueStep_kgfm = 0.01;
                } else if (mw_fiveWattIncrement.isChecked()) {
                    loadStep_W = 5;
                    torqueStep_kgfm = 0.05;
                } else if (mw_tenWattIncrement.isChecked()) {
                    loadStep_W = 10;
                    torqueStep_kgfm = 0.10;
                }
            }
        });

        mw_exerciseTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (mw_constantLoad.isChecked()) {
                    g_protocol.setExerciseType(Protocol.ExerciseType.CONSTANT_LOAD);
                } else if (mw_constantTorque.isChecked()) {
                    g_protocol.setExerciseType(Protocol.ExerciseType.CONSTANT_TORQUE);
                }
                load_W = 0;
                torque_kgfm = 0;
                updateVariables();
            }
        });

        mw_protocolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g_currentWindow = Window.PROTOCOL_SELECTOR;
                setVisibility(View.INVISIBLE);
                load_W = 0;
                torque_kgfm = 0;
                updateLoad();
                updateTorque();
                preTrainWasInitialized = false;
                postTrainWasInitialized = false;
                mw_preTrainEnable.setChecked(false);
                mw_postTrainEnable.setChecked(false);
                protocolNames = openProtocolNames(protocolNames);
                ProtocolSelector.updateExercise();
                ProtocolSelector.setVisibility(View.VISIBLE);
            }
        });

        mw_preTrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g_currentWindow = Window.PRETRAIN_EDITOR;
                setVisibility(View.INVISIBLE);

                ProtocolEditor.protocolNameEditor.setText(PRE_TRAIN_STRING);
                ProtocolEditor.protocolNameEditor.setFocusable(false);

                load_W = 0;
                torque_kgfm = 0;
                updateLoad();
                updateTorque();
                if (!preTrainWasInitialized) initializePreTrain();

                ProtocolEditor.updateVariables();
                ProtocolEditor.setVisibility(View.VISIBLE);
            }
        });

        mw_postTrainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                g_currentWindow = Window.POSTTRAIN_EDITOR;
                setVisibility(View.INVISIBLE);

                ProtocolEditor.protocolNameEditor.setText(POST_TRAIN_STRING);
                ProtocolEditor.protocolNameEditor.setFocusable(false);
                load_W = 0;
                torque_kgfm = 0;
                updateLoad();
                updateTorque();
                if (!postTrainWasInitialized) initializePostTrain();

                ProtocolEditor.updateVariables();
                ProtocolEditor.setVisibility(View.VISIBLE);
            }
        });

        mw_preTrainEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                preTrainEnable = b;
            }
        });

        mw_postTrainEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                postTrainEnable = b;
            }
        });

        mw_runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!preTrainWasInitialized) initializePreTrain();
                if (!postTrainWasInitialized) initializePostTrain();
                mw_protocolButton.setVisibility(View.INVISIBLE);
                mw_runButton.setVisibility(View.INVISIBLE);
                mw_pauseButton.setVisibility(View.VISIBLE);
                mw_continueButton.setVisibility(View.INVISIBLE);
                mw_stopButton.setVisibility(View.VISIBLE);
                mw_exerciseTypeGroup.setVisibility(View.INVISIBLE);
                mw_exerciseTypeLabel.setVisibility(View.INVISIBLE);
                mw_preTrainButton.setVisibility(View.INVISIBLE);
                mw_postTrainButton.setVisibility(View.INVISIBLE);
                mw_preTrainEnable.setVisibility(View.INVISIBLE);
                mw_postTrainEnable.setVisibility(View.INVISIBLE);
                outTimer.start();
            }
        });

        mw_pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mw_pauseButton.setVisibility(View.INVISIBLE);
                mw_continueButton.setVisibility(View.VISIBLE);
                outTimer.cancel();
            }
        });

        mw_continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mw_pauseButton.setVisibility(View.VISIBLE);
                mw_continueButton.setVisibility(View.INVISIBLE);
                outTimer.start();
            }
        });

        mw_stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mw_protocolButton.setVisibility(View.VISIBLE);
                mw_runButton.setVisibility(View.VISIBLE);
                mw_pauseButton.setVisibility(View.INVISIBLE);
                mw_continueButton.setVisibility(View.INVISIBLE);
                mw_stopButton.setVisibility(View.INVISIBLE);
                switch (g_protocol.getProtocolType()) {
                    case MANUAL:
                        mw_exerciseTypeGroup.setVisibility(View.VISIBLE);
                        mw_exerciseTypeLabel.setVisibility(View.VISIBLE);
                        break;
                    case STAGES:
                        mw_preTrainButton.setVisibility(View.VISIBLE);
                        mw_postTrainButton.setVisibility(View.VISIBLE);
                        mw_preTrainEnable.setVisibility(View.VISIBLE);
                        mw_postTrainEnable.setVisibility(View.VISIBLE);
                        break;
                    case INTERVALED:
                        mw_preTrainButton.setVisibility(View.VISIBLE);
                        mw_preTrainEnable.setVisibility(View.VISIBLE);
                        break;
                    case RAMP:
                        mw_preTrainButton.setVisibility(View.VISIBLE);
                        mw_postTrainButton.setVisibility(View.VISIBLE);
                        mw_preTrainEnable.setVisibility(View.VISIBLE);
                        mw_postTrainEnable.setVisibility(View.VISIBLE);
                        break;
                    case MOUNTAIN:
                        mw_preTrainButton.setVisibility(View.VISIBLE);
                        mw_postTrainButton.setVisibility(View.VISIBLE);
                        mw_preTrainEnable.setVisibility(View.VISIBLE);
                        mw_postTrainEnable.setVisibility(View.VISIBLE);
                        break;
                    case RANDOM:
                        mw_preTrainButton.setVisibility(View.VISIBLE);
                        mw_preTrainEnable.setVisibility(View.VISIBLE);
                        break;
                }

                currentIntervaledStage = Protocol.CurrentIntervaledStage.REST;
                oldIntervaledStage = Protocol.CurrentIntervaledStage.REST;
                oldStage = 0;
                stage = 1;
                load_W = 0;
                torque_kgfm = 0;
                totalTime_s = 0;
                totalTimeNoPre_s = 0;
                speed_rpm = 0;
                mw_stageValue.setText(String.format(Locale.US, "%d", stage));
                updateLoad();
                updateTorque();
                updateTime();
                updateSpeed();
                sendLoad(load_W, outputStream);
                outTimer.cancel();
            }
        });

        mw_loadUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        load_W = increaseLong(load_W, loadStep_W, MAX_LOAD_W);
                        updateLoad();
                        break;
                    case CONSTANT_TORQUE:
                        torque_kgfm = increaseDouble(torque_kgfm, torqueStep_kgfm, MAX_TORQUE_kgfm);
                        updateTorque();
                        break;
                }
            }
        });

        mw_loadDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        load_W = decreaseLong(load_W, loadStep_W, MIN_LOAD_W);
                        updateLoad();
                        break;
                    case CONSTANT_TORQUE:
                        torque_kgfm = decreaseDouble(torque_kgfm, torqueStep_kgfm, MIN_TORQUE_kgfm);
                        updateTorque();
                        break;
                }
            }
        });
    }

    //==================================================
    // ENUMS
    //==================================================

    enum Window {
        MAIN,
        PROTOCOL_SELECTOR,
        PROTOCOL_EDITOR,
        INTERVALED_EDITOR,
        RANDOM_EDITOR,
        TRIANGULAR_EDITOR,
        MOUNTAIN_EDITOR,
        PRETRAIN_EDITOR,
        POSTTRAIN_EDITOR
    }
}


//==================================================
// TODO: HARD KEYS
//==================================================
/*
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F1:
                if (!mw_enableLoadHardkeys) return false;
                if (g_windowState == WindowState.MAIN) {
                    load_W = increaseVariable(load_W, mw_loadStep, IntConstants.MAX_LOAD_WATTS.getValue());
                    updateLoad(load_W);
                } else if (g_windowState == WindowState.PROTOCOLEDITOR) {
                    pe_load = increaseVariable(pe_load, mw_loadStep, IntConstants.MAX_LOAD_WATTS.getValue());
                    updateLoad(pe_load);
                    pe_loadVector.set((int) pe_currentStage - 1, pe_load);
                    updateProtocolTable(pe_loadVector, pe_stageTimeVector);
                }
                break;
            case KeyEvent.KEYCODE_F2:
                if (!mw_enableLoadHardkeys) return false;
                if (g_windowState == WindowState.MAIN) {
                    load_W = decreaseVariable(load_W, mw_loadStep, IntConstants.MIN_LOAD_WATTS.getValue());
                    updateLoad(load_W);
                } else if (g_windowState == WindowState.PROTOCOLEDITOR) {
                    pe_load = decreaseVariable(pe_load, mw_loadStep, IntConstants.MIN_LOAD_WATTS.getValue());
                    updateLoad(pe_load);
                    pe_loadVector.set((int) pe_currentStage - 1, pe_load);
                    updateProtocolTable(pe_loadVector, pe_stageTimeVector);
                }
                break;
            case KeyEvent.KEYCODE_F3:
                if (!pe_enableTimeHardkeys) return false;
                pe_stageTime = increaseVariable(pe_stageTime, pe_stageTimeStep, IntConstants.MAX_STAGETIME_SECONDS.getValue());
                updateStageTime(pe_stageTime);
                pe_stageTimeVector.set((int) pe_currentStage - 1, pe_stageTime);
                updateProtocolTable(pe_loadVector, pe_stageTimeVector);
                break;
            case KeyEvent.KEYCODE_F4:
                if (!pe_enableTimeHardkeys) return false;
                pe_stageTime = decreaseVariable(pe_stageTime, pe_stageTimeStep, IntConstants.MIN_STAGETIME_SECONDS.getValue());
                updateStageTime(pe_stageTime);
                pe_stageTimeVector.set((int) pe_currentStage - 1, pe_stageTime);
                updateProtocolTable(pe_loadVector, pe_stageTimeVector);
                break;
            default: return false;
        }
        return true;
    }
*/