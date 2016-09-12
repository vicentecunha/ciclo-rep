package com.inbramed.vicente.cicloergometer;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Vector;

public class ProtocolEditor {

    //==================================================
    // CONSTANTS
    //==================================================

    final static long MIN_TIME_s = 0;
    final static long MAX_TIME_s = 3599;
    final static long MIN_STAGE = 1;
    final static long MAX_STAGE = 16;

    final static String PRE_TRAIN_STR = "_pretrain_";
    final static String POST_TRAIN_STR = "_posttrain_";

    //==================================================
    // STATIC FIELDS
    //==================================================

    static double torque_kgfm = 0;
    static double torqueStep_kgfm = 0.05;
    static long load_W = 0;
    static long loadStep_W = 5;
    static long stage = 1;
    static long time_s = 0;
    static long timeStep_s = 5;
    static Vector<Double> torqueVector = new Vector<>();
    static Vector<Long> loadVector = new Vector<>();
    static Vector<Long> timeVector = new Vector<>();

    //==================================================
    // VIEW FIELDS
    //==================================================

    static EditText protocolNameEditor;
    static ImageButton pe_leftArrow;
    static ImageButton pe_loadUpButton;
    static ImageButton pe_loadDownButton;
    static ImageButton pe_rightArrow;
    static ImageButton pe_timeUpButton;
    static ImageButton pe_timeDownButton;
    static RadioButton pe_oneWattIncrement;
    static RadioButton pe_fiveWattIncrement;
    static RadioButton pe_tenWattIncrement;
    static RadioGroup pe_loadStepGroup;
    static RadioButton pe_oneSecondIncrement;
    static RadioButton pe_fiveSecondIncrement;
    static RadioButton pe_oneMinuteIncrement;
    static RadioButton pe_fiveMinuteIncrement;
    static RadioGroup pe_timeStepGroup;
    static TextView pe_consoleText;
    static TextView pe_loadLabel;
    static TextView pe_loadStepLabel;
    static TextView pe_loadValue;
    static TextView pe_protocolTable;
    static TextView pe_stageLabel;
    static TextView pe_stageValue;
    static TextView pe_stageTimeLabel;
    static TextView pe_timeValue;
    static TextView pe_timeStepLabel;
    static TextView pe_cancelButton;
    static TextView pe_saveButton;
    static TextView pe_confirmButton;

    //==================================================
    // STATIC METHODS
    //==================================================

    static public void setVisibility(int visibility) {
        if (MainActivity.g_currentWindow == MainActivity.Window.PRETRAIN_EDITOR ||
                MainActivity.g_currentWindow == MainActivity.Window.POSTTRAIN_EDITOR) {
            pe_confirmButton.setVisibility(visibility);
        } else pe_cancelButton.setVisibility(visibility);
        pe_saveButton.setVisibility(View.INVISIBLE);
        pe_leftArrow.setVisibility(visibility);
        pe_rightArrow.setVisibility(visibility);
        pe_timeUpButton.setVisibility(visibility);
        pe_timeDownButton.setVisibility(visibility);
        pe_oneSecondIncrement.setVisibility(visibility);
        pe_fiveSecondIncrement.setVisibility(visibility);
        pe_oneMinuteIncrement.setVisibility(visibility);
        pe_fiveMinuteIncrement.setVisibility(visibility);
        pe_timeStepGroup.setVisibility(visibility);
        pe_consoleText.setVisibility(visibility);
        pe_protocolTable.setVisibility(visibility);
        pe_stageLabel.setVisibility(visibility);
        pe_stageValue.setVisibility(visibility);
        pe_stageTimeLabel.setVisibility(visibility);
        pe_timeValue.setVisibility(visibility);
        pe_timeStepLabel.setVisibility(visibility);
        pe_loadUpButton.setVisibility(visibility);
        pe_loadDownButton.setVisibility(visibility);
        pe_oneWattIncrement.setVisibility(visibility);
        pe_fiveWattIncrement.setVisibility(visibility);
        pe_tenWattIncrement.setVisibility(visibility);
        pe_loadStepGroup.setVisibility(visibility);
        pe_loadLabel.setVisibility(visibility);
        pe_loadStepLabel.setVisibility(visibility);
        pe_loadValue.setVisibility(visibility);
        protocolNameEditor.setVisibility(visibility);
        pe_protocolTable.scrollTo(0,0);
    }

    static void updateProtocolTable() {
        String string;

        switch (MainActivity.g_currentWindow) {
            case PRETRAIN_EDITOR:
                loadVector = MainActivity.preTrain.getLoadVector();
                torqueVector = MainActivity.preTrain.getTorqueVector();
                timeVector = MainActivity.preTrain.getTimeVector();
                break;
            case POSTTRAIN_EDITOR:
                loadVector = MainActivity.postTrain.getLoadVector();
                torqueVector = MainActivity.postTrain.getTorqueVector();
                timeVector = MainActivity.postTrain.getTimeVector();
                break;
        }

        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                string = ProtocolSelector.PROTOCOL_TABLE_HEADER_LOAD;
                for (int i = 0; i < loadVector.size(); i++) {
                    long minutes = (timeVector.get(i) % 3600) / 60;
                    long seconds = timeVector.get(i) % 60;
                    string += String.format(Locale.US, "%d / %d W / %02d:%02d\n",
                            i + 1, loadVector.get(i), minutes, seconds);
                }
                break;
            case CONSTANT_TORQUE:
                string = ProtocolSelector.PROTOCOL_TABLE_HEADER_TORQUE;
                for (int i = 0; i < torqueVector.size(); i++) {
                    long minutes = (timeVector.get(i) % 3600) / 60;
                    long seconds = timeVector.get(i) % 60;
                    string += String.format(Locale.US, "%d / %.2f kgfm / %02d:%02d\n",
                            i + 1, torqueVector.get(i), minutes, seconds);
                }
                break;
            default: string = ""; break;
        }
        pe_protocolTable.setText(string);
    }

    static void updateLoad() {
        pe_loadValue.setText(String.format(Locale.US, "%d W", load_W));
    }

    static void updateTorque() {
        pe_loadValue.setText(String.format(Locale.US, "%.2f kgfm", torque_kgfm));
    }

    static void updateStage() {
        pe_stageValue.setText(String.format(Locale.US, "%d", stage));
    }

    static void updateTime() {
        long minutes = (time_s % 3600)/60;
        long seconds = time_s % 60;
        if (time_s >= 3600) {
            long hours = time_s/3600;
            pe_timeValue.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else pe_timeValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void updateVariables() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                pe_loadLabel.setText(MainActivity.LOAD_LABEL);
                pe_oneWattIncrement.setText(MainActivity.ONE_WATT_STEP);
                pe_fiveWattIncrement.setText(MainActivity.FIVE_WATTS_STEP);
                pe_tenWattIncrement.setText(MainActivity.TEN_WATTS_STEP);

                switch (MainActivity.g_currentWindow) {
                    case PRETRAIN_EDITOR:
                        load_W = MainActivity.preTrain.getLoadVector().get(0);
                        break;
                    case POSTTRAIN_EDITOR:
                        load_W = MainActivity.postTrain.getLoadVector().get(0);
                        break;
                    default: load_W = 0; break;
                }
                updateLoad();
                break;
            case CONSTANT_TORQUE:
                pe_loadLabel.setText(MainActivity.TORQUE_LABEL);
                pe_oneWattIncrement.setText(MainActivity.ONE_CKGFM_STEP);
                pe_fiveWattIncrement.setText(MainActivity.FIVE_CKGFM_STEP);
                pe_tenWattIncrement.setText(MainActivity.TEN_CKGFM_STEP);
                pe_protocolTable.setText(ProtocolSelector.PROTOCOL_TABLE_HEADER_LOAD);

                switch (MainActivity.g_currentWindow) {
                    case PRETRAIN_EDITOR:
                        torque_kgfm = MainActivity.preTrain.getTorqueVector().get(0);
                        break;
                    case POSTTRAIN_EDITOR:
                        torque_kgfm = MainActivity.postTrain.getTorqueVector().get(0);
                        break;
                    default: torque_kgfm = 0; break;
                }
                updateTorque();
                break;
        }
        updateProtocolTable();
        updateStage();

        switch (MainActivity.g_currentWindow) {
            case PRETRAIN_EDITOR:time_s = MainActivity.preTrain.getTimeVector().get(0); break;
            case POSTTRAIN_EDITOR: time_s = MainActivity.postTrain.getTimeVector().get(0); break;
            default:time_s = 0; break;
        }
        updateTime();
    }

    static void resetVariables() {
        stage = 1;
        load_W = 0;
        torque_kgfm = 0;
        time_s = 0;
        loadVector = new Vector<>();
        torqueVector = new Vector<>();
        timeVector = new Vector<>();
        loadVector.add(load_W);
        torqueVector.add(torque_kgfm);
        timeVector.add(time_s);
        protocolNameEditor.setText("");
    }

    static void toggleSaveButton() {
        long totalTime = 0;
        for (int i = 0; i < timeVector.size(); i++) totalTime += timeVector.get(i);
        if (totalTime > 0 && (MainActivity.g_currentWindow == MainActivity.Window.PROTOCOL_EDITOR) &&
                !ProtocolSelector.ps_reservedNames.contains(protocolNameEditor.getText().toString()))
            pe_saveButton.setVisibility(View.VISIBLE);
        else pe_saveButton.setVisibility(View.INVISIBLE);
    }

    //==================================================
    // CONSTRUCTOR
    //==================================================

    ProtocolEditor(Context context) {
        protocolNameEditor = (EditText) ((Activity) context).findViewById(R.id.pe_protocolNameEditor);
        pe_leftArrow = (ImageButton) ((Activity) context).findViewById(R.id.pe_leftArrow);
        pe_loadUpButton = (ImageButton) ((Activity) context).findViewById(R.id.pe_loadUpButton);
        pe_loadDownButton = (ImageButton) ((Activity) context).findViewById(R.id.pe_loadDownButton);
        pe_rightArrow = (ImageButton) ((Activity) context).findViewById(R.id.pe_rightArrow);
        pe_timeUpButton = (ImageButton) ((Activity) context).findViewById(R.id.pe_stageTimeUpButton);
        pe_oneWattIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_oneWattIncrement);
        pe_fiveWattIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_fiveWattIncrement);
        pe_tenWattIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_tenWattIncrement);
        pe_loadStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.pe_loadStepGroup);
        pe_timeDownButton = (ImageButton) ((Activity) context).findViewById(R.id.pe_stageTimeDownButton);
        pe_oneSecondIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_oneSecondIncrement);
        pe_fiveSecondIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_fiveSecondIncrement);
        pe_oneMinuteIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_oneMinuteIncrement);
        pe_fiveMinuteIncrement = (RadioButton) ((Activity) context).findViewById(R.id.pe_fiveMinuteIncrement);
        pe_timeStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.pe_timeStepGroup);
        pe_cancelButton = (TextView) ((Activity) context).findViewById(R.id.pe_cancelbutton);
        pe_consoleText = (TextView) ((Activity) context).findViewById(R.id.pe_consoleText);
        pe_loadLabel = (TextView) ((Activity)context).findViewById(R.id.pe_loadLabel);
        pe_loadStepLabel = (TextView) ((Activity) context).findViewById(R.id.pe_loadStepLabel);
        pe_loadValue = (TextView) ((Activity) context).findViewById(R.id.pe_loadValue);
        pe_protocolTable = (TextView) ((Activity) context).findViewById(R.id.pe_protocolTable);
        pe_saveButton = (TextView) ((Activity) context).findViewById(R.id.pe_saveButton);
        pe_stageLabel = (TextView) ((Activity) context).findViewById(R.id.pe_stageLabel);
        pe_stageValue = (TextView) ((Activity) context).findViewById(R.id.pe_stageValue);
        pe_stageTimeLabel = (TextView) ((Activity) context).findViewById(R.id.pe_stageTimeLabel);
        pe_timeValue = (TextView) ((Activity) context).findViewById(R.id.pe_stageTimeValue);
        pe_timeStepLabel = (TextView) ((Activity) context).findViewById(R.id.pe_timeStepLabel);
        pe_confirmButton = (TextView) ((Activity) context).findViewById(R.id.pe_confirmButton);

        torqueVector.add(torque_kgfm);
        loadVector.add(load_W);
        timeVector.add(time_s);
        updateProtocolTable();
        toggleSaveButton();
        pe_protocolTable.setMovementMethod(new ScrollingMovementMethod());

        //==================================================
        // LISTENERS
        //==================================================

        pe_loadStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (pe_oneWattIncrement.isChecked()) {
                    loadStep_W = 1;
                    torqueStep_kgfm = 0.01;
                }
                else if (pe_fiveWattIncrement.isChecked()) {
                    loadStep_W = 5;
                    torqueStep_kgfm = 0.05;
                }
                else if (pe_tenWattIncrement.isChecked()) {
                    loadStep_W = 10;
                    torqueStep_kgfm = 0.10;
                }
            }
        });

        pe_timeStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (pe_oneSecondIncrement.isChecked()) timeStep_s = 1;
                else if (pe_fiveSecondIncrement.isChecked()) timeStep_s = 5;
                else if (pe_oneMinuteIncrement.isChecked()) timeStep_s = 60;
                else if (pe_fiveMinuteIncrement.isChecked()) timeStep_s = 300;
            }
        });

        pe_loadUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        load_W = MainActivity.increaseLong(load_W, loadStep_W,
                                MainActivity.MAX_LOAD_W);
                        loadVector.set((int)stage-1, load_W);
                        updateLoad();
                        break;
                    case CONSTANT_TORQUE:
                        torque_kgfm = MainActivity.increaseDouble(torque_kgfm, torqueStep_kgfm,
                                MainActivity.MAX_TORQUE_kgfm);
                        torqueVector.set((int)stage-1, torque_kgfm);
                        updateTorque();
                        break;
                }
                updateProtocolTable();
                toggleSaveButton();
            }
        });

        pe_loadDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        load_W = MainActivity.decreaseLong(load_W, loadStep_W,
                                MainActivity.MIN_LOAD_W);
                        loadVector.set((int)stage-1, load_W);
                        updateLoad();
                        break;
                    case CONSTANT_TORQUE:
                        torque_kgfm = MainActivity.decreaseDouble(torque_kgfm, torqueStep_kgfm,
                                MainActivity.MIN_TORQUE_kgfm);
                        torqueVector.set((int)stage-1, torque_kgfm);
                        updateTorque();
                        break;
                }
                updateProtocolTable();
                toggleSaveButton();
            }
        });

        pe_timeUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_s = MainActivity.increaseLong(time_s, timeStep_s, MAX_TIME_s);
                MainActivity.updateTime();
                timeVector.set((int)stage-1, time_s);
                updateTime();
                updateProtocolTable();
                toggleSaveButton();
            }
        });

        pe_timeDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time_s = MainActivity.decreaseLong(time_s, timeStep_s, MIN_TIME_s);
                MainActivity.updateTime();
                timeVector.set((int)stage-1, time_s);
                updateTime();
                updateProtocolTable();
                toggleSaveButton();
            }
        });

        pe_leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stage = MainActivity.decreaseLong(stage, 1, MIN_STAGE);
                pe_stageValue.setText(String.format(Locale.US, "%d", stage));
                time_s = timeVector.get((int)stage-1);
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        load_W = loadVector.get((int)stage-1);
                        updateLoad();
                        break;
                    case CONSTANT_TORQUE:
                        torque_kgfm = torqueVector.get((int)stage-1);
                        updateTorque();
                        break;
                }
                updateTime();
                updateProtocolTable();
                toggleSaveButton();
            }
        });

        pe_rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stage = MainActivity.increaseLong(stage, 1, MAX_STAGE);
                pe_stageValue.setText(String.format(Locale.US, "%d", stage));
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        if (loadVector.size() < stage) {
                            load_W = 0;
                            time_s = 0;
                            loadVector.add(load_W);
                            timeVector.add(time_s);
                        } else {
                            load_W = loadVector.get((int)stage-1);
                            time_s = timeVector.get((int)stage-1);
                        }
                        updateLoad();
                        break;
                    case CONSTANT_TORQUE:
                        if (torqueVector.size() < stage) {
                            torque_kgfm = 0;
                            time_s = 0;
                            torqueVector.add(torque_kgfm);
                            timeVector.add(time_s);
                        } else {
                            torque_kgfm = torqueVector.get((int)stage-1);
                            time_s = timeVector.get((int)stage-1);
                        }
                        updateTorque();
                        break;
                }
                updateTime();
                updateProtocolTable();
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

        pe_cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(View.INVISIBLE);
                resetVariables();
                updateVariables();
                toggleSaveButton();
                MainActivity.g_currentWindow = MainActivity.Window.PROTOCOL_SELECTOR;
                ProtocolSelector.setVisibility(View.VISIBLE);
            }
        });

        pe_saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(View.INVISIBLE);

                String protocolName = protocolNameEditor.length() == 0 ?
                        "(sem nome)" : protocolNameEditor.getText().toString();

                MainActivity.g_protocol.setProtocolType(Protocol.ProtocolType.STAGES);
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
                MainActivity.g_currentWindow = MainActivity.Window.MAIN;
                MainActivity.setVisibility(View.VISIBLE);
            }
        });

        pe_confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibility(View.INVISIBLE);

                switch (MainActivity.g_currentWindow) {
                    case PRETRAIN_EDITOR:
                        MainActivity.preTrain.setProtocolType(Protocol.ProtocolType.STAGES);
                        MainActivity.preTrain.setLoadVector(loadVector);
                        MainActivity.preTrain.setTorqueVector(torqueVector);
                        MainActivity.preTrain.setTimeVector(timeVector);
                        MainActivity.savePreTrain();
                        MainActivity.preTrain =
                                MainActivity.openProtocol(PRE_TRAIN_STR, MainActivity.preTrain);
                        break;
                    case POSTTRAIN_EDITOR:
                        MainActivity.postTrain.setProtocolType(Protocol.ProtocolType.STAGES);
                        MainActivity.postTrain.setLoadVector(loadVector);
                        MainActivity.postTrain.setTorqueVector(torqueVector);
                        MainActivity.postTrain.setTimeVector(timeVector);
                        MainActivity.savePostTrain();
                        MainActivity.postTrain =
                                MainActivity.openProtocol(POST_TRAIN_STR, MainActivity.postTrain);
                        break;
                }

                MainActivity.g_currentWindow = MainActivity.Window.MAIN;
                resetVariables();
                updateVariables();
                MainActivity.setVisibility(View.VISIBLE);
            }
        });
    }
}
