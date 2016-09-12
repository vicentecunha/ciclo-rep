package com.inbramed.vicente.cicloergometer;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Vector;

public class RandomEditor {

    //==================================================
    // CONSTANTS
    //==================================================

    final static double DEFAULT_AVG_TORQUE_kgfm = 1;
    final static double DEFAULT_STD_TORQUE_kgfm = 0.25;
    final static long DEFAULT_AVG_LOAD_W = 100;
    final static long DEFAULT_STD_LOAD_W = 25;
    final static long DEFAULT_STAGELENGTH_s = 0;

    //==================================================
    // STATIC FIELDS
    //==================================================

    static double avgTorque_kgfm = DEFAULT_AVG_TORQUE_kgfm;
    static double stdTorque_kgfm = DEFAULT_STD_TORQUE_kgfm;
    static double torqueStep_kgfm = 0.05;
    static long avgLoad_W = DEFAULT_AVG_LOAD_W;
    static long stdLoad_W = DEFAULT_STD_LOAD_W;
    static long loadStep_W = 5;
    static long stageLength_s = DEFAULT_STAGELENGTH_s;
    static long stageLengthStep_s = 5;
    static Vector<Double> torqueVector = new Vector<>();
    static Vector<Long> loadVector = new Vector<>();
    static Vector<Long> timeVector = new Vector<>();

    static EditText protocolNameEditor;
    static ImageButton avgRightButton;
    static ImageButton avgLeftButton;
    static ImageButton stdRightButton;
    static ImageButton stdLeftButton;
    static ImageButton stageLengthRightButton;
    static ImageButton stageLengthLeftButton;
    static TextView cancelButton;
    static TextView saveButton;
    static TextView avgValue;
    static TextView avgLabel;
    static TextView avgStepLabel;
    static TextView stdValue;
    static TextView stdLabel;
    static TextView stageLengthLabel;
    static TextView stageLengthValue;
    static RadioButton oneAvgStep;
    static RadioButton fiveAvgStep;
    static RadioButton tenAvgStep;
    static RadioGroup avgStepGroup;
    static RadioButton oneSecondStep;
    static RadioButton fiveSecondStep;
    static RadioButton oneMinuteStep;
    static RadioButton fiveMinuteStep;
    static RadioGroup timeStepGroup;

    //==================================================
    // STATIC METHODS
    //==================================================

    static void setVisibility(int visibility) {
        avgRightButton.setVisibility(visibility);
        avgLeftButton.setVisibility(visibility);
        stdRightButton.setVisibility(visibility);
        stdLeftButton.setVisibility(visibility);
        cancelButton.setVisibility(visibility);
        avgValue.setVisibility(visibility);
        avgLabel.setVisibility(visibility);
        oneAvgStep.setVisibility(visibility);
        fiveAvgStep.setVisibility(visibility);
        tenAvgStep.setVisibility(visibility);
        avgStepGroup.setVisibility(visibility);
        avgStepLabel.setVisibility(visibility);
        stdValue.setVisibility(visibility);
        stdLabel.setVisibility(visibility);
        protocolNameEditor.setVisibility(visibility);
        stageLengthLabel.setVisibility(visibility);
        stageLengthValue.setVisibility(visibility);
        stageLengthRightButton.setVisibility(visibility);
        stageLengthLeftButton.setVisibility(visibility);
        oneSecondStep.setVisibility(visibility);
        fiveSecondStep.setVisibility(visibility);
        oneMinuteStep.setVisibility(visibility);
        fiveMinuteStep.setVisibility(visibility);
        timeStepGroup.setVisibility(visibility);
    }

    static void resetVariables() {
        avgLoad_W = DEFAULT_AVG_LOAD_W;
        stdLoad_W = DEFAULT_STD_LOAD_W;
        avgTorque_kgfm = DEFAULT_AVG_TORQUE_kgfm;
        stdTorque_kgfm = DEFAULT_STD_TORQUE_kgfm;
        stageLength_s = DEFAULT_STAGELENGTH_s;
        protocolNameEditor.setText("");
    }

    static void updateVariables() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                updateAvgLoad();
                updateStdLoad();
                oneAvgStep.setText(MainActivity.ONE_WATT_STEP);
                fiveAvgStep.setText(MainActivity.FIVE_WATTS_STEP);
                tenAvgStep.setText(MainActivity.TEN_WATTS_STEP);
                break;
            case CONSTANT_TORQUE:
                updateAvgTorque();
                updateStdTorque();
                oneAvgStep.setText(MainActivity.ONE_CKGFM_STEP);
                fiveAvgStep.setText(MainActivity.FIVE_CKGFM_STEP);
                tenAvgStep.setText(MainActivity.TEN_CKGFM_STEP);
                break;
        }
        updateStageLength();
    }

    static void updateAvgLoad() {
        avgValue.setText(String.format(Locale.US, "%d W", avgLoad_W));
    }

    static void updateAvgTorque() {
        avgValue.setText(String.format(Locale.US, "%.2f kgfm", avgTorque_kgfm));
    }

    static void updateStdLoad() {
        stdValue.setText(String.format(Locale.US, "%d W", stdLoad_W));
    }

    static void updateStdTorque() {
        stdValue.setText(String.format(Locale.US, "%.2f kgfm", stdTorque_kgfm));
    }

    static void updateStageLength() {
        long minutes = (stageLength_s % 3600)/60;
        long seconds = stageLength_s % 60;
        if (stageLength_s >= 3600) {
            long hours = stageLength_s / 3600;
            stageLengthValue.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else stageLengthValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void toggleSaveButton() {
        if (stageLength_s > 0 &&
                !ProtocolSelector.ps_reservedNames.contains(protocolNameEditor.getText().toString()))
            saveButton.setVisibility(View.VISIBLE);
        else saveButton.setVisibility(View.INVISIBLE);
    }

    //==================================================
    // CONSTRUCTOR
    //==================================================

    RandomEditor(Context context) {
        avgRightButton = (ImageButton) ((Activity) context).findViewById(R.id.re_avgRightButton);
        avgLeftButton = (ImageButton) ((Activity) context).findViewById(R.id.re_avgLeftButton);
        stdRightButton = (ImageButton) ((Activity) context).findViewById(R.id.re_stdRightButton);
        stdLeftButton = (ImageButton) ((Activity) context).findViewById(R.id.re_stdLeftButton);
        cancelButton = (TextView) ((Activity) context).findViewById(R.id.re_cancelButton);
        saveButton = (TextView) ((Activity) context).findViewById(R.id.re_saveButton);
        avgValue = (TextView) ((Activity) context).findViewById(R.id.re_avgValue);
        avgLabel = (TextView) ((Activity) context).findViewById(R.id.re_avgLabel);
        avgStepLabel = (TextView) ((Activity) context).findViewById(R.id.re_stepLabel);
        stdValue = (TextView) ((Activity) context).findViewById(R.id.re_stdValue);
        stdLabel = (TextView) ((Activity) context).findViewById(R.id.re_stdLabel);
        oneAvgStep = (RadioButton) ((Activity) context).findViewById(R.id.re_oneAvgIncrement);
        fiveAvgStep = (RadioButton) ((Activity) context).findViewById(R.id.re_fiveAvgIncrement);
        tenAvgStep = (RadioButton) ((Activity) context).findViewById(R.id.re_tenAvgIncrement);
        avgStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.re_avgStepGroup);
        protocolNameEditor = (EditText) ((Activity) context).findViewById(R.id.re_protocolNameEditor);
        stageLengthLabel = (TextView) ((Activity) context).findViewById(R.id.re_stageLengthLabel);
        stageLengthValue = (TextView) ((Activity) context).findViewById(R.id.re_stageLengthValue);
        stageLengthRightButton = (ImageButton) ((Activity) context).findViewById(R.id.re_stageLengthRightButton);
        stageLengthLeftButton = (ImageButton) ((Activity) context).findViewById(R.id.re_stageLengthLeftButton);
        oneSecondStep = (RadioButton) ((Activity) context).findViewById(R.id.re_oneSecondIncrement);
        fiveSecondStep = (RadioButton) ((Activity) context).findViewById(R.id.re_fiveSecondIncrement);
        oneMinuteStep = (RadioButton) ((Activity) context).findViewById(R.id.re_oneMinuteIncrement);
        fiveMinuteStep = (RadioButton) ((Activity) context).findViewById(R.id.re_fiveMinuteIncrement);
        timeStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.re_timeStepGroup);

        //==================================================
        // LISTENERS
        //==================================================

        avgStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (oneAvgStep.isChecked()) {
                    loadStep_W = 1;
                    torqueStep_kgfm = 0.01;
                } else if (fiveAvgStep.isChecked()) {
                    loadStep_W = 5;
                    torqueStep_kgfm = 0.05;
                } else if (tenAvgStep.isChecked()) {
                    loadStep_W = 10;
                    torqueStep_kgfm = 0.10;
                }
            }
        });

        timeStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (oneSecondStep.isChecked()) stageLengthStep_s = 1;
                else if (fiveSecondStep.isChecked()) stageLengthStep_s = 5;
                else if (oneMinuteStep.isChecked()) stageLengthStep_s = 60;
                else if (fiveMinuteStep.isChecked()) stageLengthStep_s = 300;
            }
        });

        avgRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        avgLoad_W = MainActivity.increaseLong(avgLoad_W,
                                loadStep_W, MainActivity.MAX_LOAD_W);
                        updateAvgLoad();
                        break;
                    case CONSTANT_TORQUE:
                        avgTorque_kgfm = MainActivity.increaseDouble(avgTorque_kgfm,
                                torqueStep_kgfm, MainActivity.MAX_TORQUE_kgfm);
                        updateAvgTorque();
                        break;
                }
            }
        });

        avgLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        avgLoad_W = MainActivity.decreaseLong(avgLoad_W,
                                loadStep_W, MainActivity.MIN_LOAD_W);
                        updateAvgLoad();
                        break;
                    case CONSTANT_TORQUE:
                        avgTorque_kgfm = MainActivity.decreaseDouble(avgTorque_kgfm,
                                torqueStep_kgfm, MainActivity.MIN_TORQUE_kgfm);
                        updateAvgTorque();
                        break;
                }
            }
        });

        stdRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        stdLoad_W = MainActivity.increaseLong(stdLoad_W, loadStep_W,
                                MainActivity.MAX_LOAD_W);
                        updateStdLoad();
                        break;
                    case CONSTANT_TORQUE:
                        stdTorque_kgfm = MainActivity.increaseDouble(stdTorque_kgfm, torqueStep_kgfm,
                                MainActivity.MAX_TORQUE_kgfm);
                        updateStdTorque();
                        break;
                }
            }
        });

        stdLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        stdLoad_W = MainActivity.decreaseLong(stdLoad_W, loadStep_W,
                                MainActivity.MIN_LOAD_W);
                        updateStdLoad();
                        break;
                    case CONSTANT_TORQUE:
                        stdTorque_kgfm = MainActivity.decreaseDouble(stdTorque_kgfm, torqueStep_kgfm,
                                MainActivity.MIN_TORQUE_kgfm);
                        updateStdTorque();
                        break;
                }
            }
        });

        stageLengthRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageLength_s = MainActivity.increaseLong(stageLength_s, stageLengthStep_s,
                        ProtocolEditor.MAX_TIME_s);
                updateStageLength();
                toggleSaveButton();
            }
        });


        stageLengthLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageLength_s = MainActivity.decreaseLong(stageLength_s, stageLengthStep_s,
                        ProtocolEditor.MIN_TIME_s);
                updateStageLength();
                toggleSaveButton();
            }
        });

        protocolNameEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                toggleSaveButton();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.g_currentWindow = MainActivity.Window.PROTOCOL_SELECTOR;
                setVisibility(View.INVISIBLE);
                resetVariables();
                updateVariables();
                ProtocolSelector.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.g_currentWindow = MainActivity.Window.MAIN;
                setVisibility(View.INVISIBLE);

                loadVector.clear();
                loadVector.add(avgLoad_W);
                loadVector.add(stdLoad_W);
                torqueVector.clear();
                torqueVector.add(avgTorque_kgfm);
                torqueVector.add(stdTorque_kgfm);
                timeVector.clear();
                timeVector.add(stageLength_s);

                String protocolName = protocolNameEditor.length() == 0 ?
                        "(sem nome)" : protocolNameEditor.getText().toString();

                MainActivity.g_protocol.setProtocolType(Protocol.ProtocolType.RANDOM);
                MainActivity.g_protocol.setName(protocolName);
                MainActivity.g_protocol.setLoadVector(loadVector);
                MainActivity.g_protocol.setTorqueVector(torqueVector);
                MainActivity.g_protocol.setTimeVector(timeVector);
                MainActivity.saveProtocol();
                MainActivity.protocolNames.remove(protocolName);
                MainActivity.protocolNames.add(protocolName);
                MainActivity.saveProtocolNames();
                MainActivity.updateVariables();
                MainActivity.updateProtocol();
                MainActivity.g_protocol =
                        MainActivity.openProtocol(protocolName, MainActivity.g_protocol);

                resetVariables();
                updateVariables();
                toggleSaveButton();
                MainActivity.setVisibility(View.VISIBLE);
            }
        });
    }
}
