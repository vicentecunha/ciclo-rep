package com.inbramed.vicente.cicloergometer;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Vector;

public class IntervaledEditor {

    //==================================================
    // STATIC FIELDS
    //==================================================

    static double torqueExercise_kgfm = 0;
    static double torqueRest_kgfm = 0;
    static double torqueStep_kgfm = 0.05;
    static long loadExercise_W = 0;
    static long loadRest_W = 0;
    static long timeExercise_s = 0;
    static long timeRest_s = 0;
    static long loadStep_W = 5;
    static long timeStep_s = 5;
    static Vector<Double> torqueVector = new Vector<>();
    static Vector<Long> loadVector = new Vector<>();
    static Vector<Long> timeVector = new Vector<>();

    //==================================================
    // VIEW FIELDS
    //==================================================

    static EditText protocolNameEditor;
    static ImageButton l1up;
    static ImageButton l1down;
    static ImageButton l2up;
    static ImageButton l2down;
    static ImageButton t1left;
    static ImageButton t1right;
    static ImageButton t2left;
    static ImageButton t2right;
    static ImageView imageView;
    static TextView cancelButton;
    static TextView saveButton;
    static TextView t1value;
    static TextView t2value;
    static TextView loadExerciseValue;
    static TextView loadRestValue;
    static TextView stepLabel;
    static RadioButton oneWattStep;
    static RadioButton fiveWattStep;
    static RadioButton tenWattStep;
    static RadioGroup loadStepGroup;
    static RadioButton oneSecondStep;
    static RadioButton fiveSecondStep;
    static RadioButton oneMinuteStep;
    static RadioButton fiveMinuteStep;
    static RadioGroup timeStepGroup;

    //==================================================
    // STATIC METHODS
    //==================================================

    static void setVisibility(int visibility) {
        protocolNameEditor.setVisibility(visibility);
        imageView.setVisibility(visibility);
        t1value.setVisibility(visibility);
        t2value.setVisibility(visibility);
        loadExerciseValue.setVisibility(visibility);
        loadRestValue.setVisibility(visibility);
        l1up.setVisibility(visibility);
        l1down.setVisibility(visibility);
        l2up.setVisibility(visibility);
        l2down.setVisibility(visibility);
        t1left.setVisibility(visibility);
        t1right.setVisibility(visibility);
        t2left.setVisibility(visibility);
        t2right.setVisibility(visibility);
        cancelButton.setVisibility(visibility);
        loadStepGroup.setVisibility(visibility);
        timeStepGroup.setVisibility(visibility);
        stepLabel.setVisibility(visibility);
    }

    static void resetVariables() {
        loadExercise_W = 0;
        loadRest_W = 0;
        torqueExercise_kgfm = 0;
        torqueRest_kgfm = 0;
        timeExercise_s = 0;
        timeRest_s = 0;
        protocolNameEditor.setText("");
    }

    static void updateVariables() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                imageView.setImageResource(R.drawable.intervaled_load);
                oneWattStep.setText(MainActivity.ONE_WATT_STEP);
                fiveWattStep.setText(MainActivity.FIVE_WATTS_STEP);
                tenWattStep.setText(MainActivity.TEN_WATTS_STEP);
                updateLoad1();
                updateLoad2();
                break;
            case CONSTANT_TORQUE:
                imageView.setImageResource(R.drawable.intervaled_torque);
                oneWattStep.setText(MainActivity.ONE_CKGFM_STEP);
                fiveWattStep.setText(MainActivity.FIVE_CKGFM_STEP);
                tenWattStep.setText(MainActivity.TEN_CKGFM_STEP);
                updateTorque1();
                updateTorque2();
                break;
        }
        updateTime1();
        updateTime2();
    }

    static void updateLoad1() {
        loadExerciseValue.setText(String.format(Locale.US, "%d W", loadExercise_W));
    }

    static void updateLoad2() {
        loadRestValue.setText(String.format(Locale.US, "%d W", loadRest_W));
    }

    static void updateTorque1() {
        loadExerciseValue.setText(String.format(Locale.US, "%.2f kgfm", torqueExercise_kgfm));
    }

    static void updateTorque2() {
        loadRestValue.setText(String.format(Locale.US, "%.2f kgfm", torqueRest_kgfm));
    }

    static void updateTime1() {
        long minutes = (timeExercise_s % 3600)/60;
        long seconds = timeExercise_s % 60;
        if (timeExercise_s >= 3600) {
            long hours = timeExercise_s / 3600;
            t1value.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else t1value.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void updateTime2() {
        long minutes = (timeRest_s % 3600)/60;
        long seconds = timeRest_s % 60;
        if (timeRest_s >= 3600) {
            long hours = timeRest_s / 3600;
            t2value.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else t2value.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void toggleSaveButton() {
        if (timeExercise_s > 0 && timeRest_s > 0 &&
                (loadExercise_W > loadRest_W || torqueExercise_kgfm > torqueRest_kgfm) &&
                !ProtocolSelector.ps_reservedNames.contains(protocolNameEditor.getText().toString()))
            saveButton.setVisibility(View.VISIBLE);
        else saveButton.setVisibility(View.INVISIBLE);
    }

    //==================================================
    // CONSTRUCTOR
    //==================================================

    IntervaledEditor(Context context) {
        protocolNameEditor = (EditText) ((Activity) context).findViewById(R.id.ie_protocolNameEditor);
        imageView = (ImageView) ((Activity) context).findViewById(R.id.ie_intervaledImage);
        t1value = (TextView) ((Activity) context).findViewById(R.id.ie_timeExerciseValue);
        t2value = (TextView) ((Activity) context).findViewById(R.id.ie_timeRestValue);
        loadExerciseValue = (TextView) ((Activity) context).findViewById(R.id.ie_loadExerciseValue);
        loadRestValue = (TextView) ((Activity) context).findViewById(R.id.ie_loadRestValue);
        cancelButton = (TextView) ((Activity) context).findViewById(R.id.ie_cancelButton);
        saveButton = (TextView) ((Activity) context).findViewById(R.id.ie_saveButton);
        l1up = (ImageButton) ((Activity) context).findViewById(R.id.ie_loadExerciseUp);
        l1down = (ImageButton) ((Activity) context).findViewById(R.id.ie_loadExerciseDown);
        l2up = (ImageButton) ((Activity) context).findViewById(R.id.ie_loadRestUp);
        l2down = (ImageButton) ((Activity) context).findViewById(R.id.ie_loadRestDown);
        t1left = (ImageButton) ((Activity) context).findViewById(R.id.ie_t1left);
        t1right = (ImageButton) ((Activity) context).findViewById(R.id.ie_t1right);
        t2left = (ImageButton) ((Activity) context).findViewById(R.id.ie_t2left);
        t2right = (ImageButton) ((Activity) context).findViewById(R.id.ie_t2right);
        oneWattStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_oneWattIncrement);
        fiveWattStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_fiveWattIncrement);
        tenWattStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_tenWattIncrement);
        loadStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.ie_loadStepGroup);
        oneSecondStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_oneSecondIncrement);
        fiveSecondStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_fiveSecondIncrement);
        oneMinuteStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_oneMinuteIncrement);
        fiveMinuteStep = (RadioButton) ((Activity) context).findViewById(R.id.ie_fiveMinuteIncrement);
        timeStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.ie_timeStepGroup);
        stepLabel = (TextView) ((Activity) context).findViewById(R.id.ie_stepLabel);

        //==================================================
        // LISTENERS
        //==================================================

        l1up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        loadExercise_W = MainActivity.increaseLong(loadExercise_W,
                                loadStep_W, MainActivity.MAX_LOAD_W);
                        updateLoad1();
                        break;
                    case CONSTANT_TORQUE:
                        torqueExercise_kgfm = MainActivity.increaseDouble(torqueExercise_kgfm,
                                torqueStep_kgfm, MainActivity.MAX_TORQUE_kgfm);
                        updateTorque1();
                        break;
                }
                toggleSaveButton();
            }
        });

        l1down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        loadExercise_W = MainActivity.decreaseLong(loadExercise_W,
                                loadStep_W, MainActivity.MIN_LOAD_W);
                        updateLoad1();
                        break;
                    case CONSTANT_TORQUE:
                        torqueExercise_kgfm = MainActivity.decreaseDouble(torqueExercise_kgfm,
                                torqueStep_kgfm, MainActivity.MIN_TORQUE_kgfm);
                        updateTorque1();
                        break;
                }
                toggleSaveButton();
            }
        });

        l2up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        loadRest_W = MainActivity.increaseLong(loadRest_W,
                                loadStep_W, MainActivity.MAX_LOAD_W);
                        updateLoad2();
                        break;
                    case CONSTANT_TORQUE:
                        torqueRest_kgfm = MainActivity.increaseDouble(torqueRest_kgfm,
                                torqueStep_kgfm, MainActivity.MAX_TORQUE_kgfm);
                        updateTorque2();
                        break;
                }
                toggleSaveButton();
            }
        });

        l2down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        loadRest_W = MainActivity.decreaseLong(loadRest_W,
                                loadStep_W, MainActivity.MIN_LOAD_W);
                        updateLoad2();
                        break;
                    case CONSTANT_TORQUE:
                        torqueRest_kgfm = MainActivity.decreaseDouble(torqueRest_kgfm,
                                torqueStep_kgfm, MainActivity.MIN_TORQUE_kgfm);
                        updateTorque2();
                        break;
                }
                toggleSaveButton();
            }
        });

        t1left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeExercise_s = MainActivity.decreaseLong(timeExercise_s, timeStep_s, ProtocolEditor.MIN_TIME_s);
                updateTime1();
                toggleSaveButton();
            }
        });

        t1right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeExercise_s = MainActivity.increaseLong(timeExercise_s, timeStep_s, ProtocolEditor.MAX_TIME_s);
                updateTime1();
                toggleSaveButton();
            }
        });

        t2left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeRest_s = MainActivity.decreaseLong(timeRest_s, timeStep_s, ProtocolEditor.MIN_TIME_s);
                updateTime2();
                toggleSaveButton();
            }
        });

        t2right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeRest_s = MainActivity.increaseLong(timeRest_s, timeStep_s, ProtocolEditor.MAX_TIME_s);
                updateTime2();
                toggleSaveButton();
            }
        });

        loadStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (oneWattStep.isChecked()) {
                    loadStep_W = 1;
                    torqueStep_kgfm = 0.01;
                } else if (fiveWattStep.isChecked()) {
                    loadStep_W = 5;
                    torqueStep_kgfm = 0.05;
                } else if (tenWattStep.isChecked()) {
                    loadStep_W = 10;
                    torqueStep_kgfm = 0.10;
                }
            }
        });

        timeStepGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (oneSecondStep.isChecked()) timeStep_s = 1;
                else if (fiveSecondStep.isChecked()) timeStep_s = 5;
                else if (oneMinuteStep.isChecked()) timeStep_s = 60;
                else if (fiveMinuteStep.isChecked()) timeStep_s = 300;
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
                toggleSaveButton();
                ProtocolSelector.setVisibility(View.VISIBLE);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.g_currentWindow = MainActivity.Window.MAIN;
                setVisibility(View.INVISIBLE);

                loadVector.clear();
                loadVector.add(loadRest_W);
                loadVector.add(loadExercise_W);
                torqueVector.clear();
                torqueVector.add(torqueRest_kgfm);
                torqueVector.add(torqueExercise_kgfm);
                timeVector.clear();
                timeVector.add(timeRest_s);
                timeVector.add(timeExercise_s);

                String protocolName = protocolNameEditor.length() == 0 ?
                        "(sem nome)" : protocolNameEditor.getText().toString();

                MainActivity.g_protocol.setProtocolType(Protocol.ProtocolType.INTERVALED);
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
