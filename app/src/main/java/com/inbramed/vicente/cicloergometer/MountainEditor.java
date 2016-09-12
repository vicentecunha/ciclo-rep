package com.inbramed.vicente.cicloergometer;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Locale;
import java.util.Vector;

public class MountainEditor {

    //==================================================
    // STATIC FIELDS
    //==================================================

    static double minTorque_kgfm = 0;
    static double maxTorque_kgfm = 0;
    static double torqueStep_kgfm = 0.05;
    static long minLoad_W = 0;
    static long maxLoad_W = 0;
    static long stageTime_s = 0;
    static long loadStep_W = 5;
    static long timeStep_s = 5;
    static long increaseSteps = 2;
    static long decreaseSteps = 2;
    static Vector<Double> torqueVector = new Vector<>();
    static Vector<Long> loadVector = new Vector<>();
    static Vector<Long> timeVector = new Vector<>();

    static EditText protocolNameEditor;
    static ImageView imageView;
    static ImageButton maxLoadLeft;
    static ImageButton maxLoadRight;
    static ImageButton minLoadLeft;
    static ImageButton minLoadRight;
    static ImageButton stageLengthLeft;
    static ImageButton stageLengthRight;
    static TextView cancelButton;
    static TextView saveButton;
    static TextView stepLabel;
    static TextView maxLoadValue;
    static TextView minLoadValue;
    static TextView stageTimeValue;
    static TextView stageLengthLabel;
    static TextView increasePickerLabel;
    static TextView decreasePickerLabel;
    static RadioButton oneWattStep;
    static RadioButton fiveWattStep;
    static RadioButton tenWattStep;
    static RadioGroup loadStepGroup;
    static RadioButton oneSecondStep;
    static RadioButton fiveSecondStep;
    static RadioButton oneMinuteStep;
    static RadioButton fiveMinuteStep;
    static RadioGroup timeStepGroup;
    static NumberPicker increasePicker;
    static NumberPicker decreasePicker;

    //==================================================
    // STATIC METHODS
    //==================================================

    static void setVisibility(int visibility) {
        imageView.setVisibility(visibility);
        cancelButton.setVisibility(visibility);
        protocolNameEditor.setVisibility(visibility);
        stepLabel.setVisibility(visibility);
        oneWattStep.setVisibility(visibility);
        fiveWattStep.setVisibility(visibility);
        tenWattStep.setVisibility(visibility);
        oneSecondStep.setVisibility(visibility);
        fiveSecondStep.setVisibility(visibility);
        oneMinuteStep.setVisibility(visibility);
        fiveMinuteStep.setVisibility(visibility);
        loadStepGroup.setVisibility(visibility);
        timeStepGroup.setVisibility(visibility);
        maxLoadValue.setVisibility(visibility);
        minLoadValue.setVisibility(visibility);
        stageTimeValue.setVisibility(visibility);
        maxLoadLeft.setVisibility(visibility);
        maxLoadRight.setVisibility(visibility);
        minLoadLeft.setVisibility(visibility);
        minLoadRight.setVisibility(visibility);
        stageLengthLeft.setVisibility(visibility);
        stageLengthRight.setVisibility(visibility);
        increasePicker.setVisibility(visibility);
        stageLengthLabel.setVisibility(visibility);
        increasePickerLabel.setVisibility(visibility);
        decreasePicker.setVisibility(visibility);
        decreasePickerLabel.setVisibility(visibility);
    }

    static void resetVariables() {
        minTorque_kgfm = 0;
        maxTorque_kgfm = 0;
        minLoad_W = 0;
        maxLoad_W = 0;
        stageTime_s = 0;
        increaseSteps = 2;
        decreaseSteps = 2;
        protocolNameEditor.setText("");
    }

    static void updateVariables() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                imageView.setImageResource(R.drawable.mountain_load);
                oneWattStep.setText(MainActivity.ONE_WATT_STEP);
                fiveWattStep.setText(MainActivity.FIVE_WATTS_STEP);
                tenWattStep.setText(MainActivity.TEN_WATTS_STEP);
                updateMaxLoad();
                updateMinLoad();
                break;
            case CONSTANT_TORQUE:
                imageView.setImageResource(R.drawable.mountain_torque);
                oneWattStep.setText(MainActivity.ONE_CKGFM_STEP);
                fiveWattStep.setText(MainActivity.FIVE_CKGFM_STEP);
                tenWattStep.setText(MainActivity.TEN_CKGFM_STEP);
                updateMaxTorque();
                updateMinTorque();
                break;
        }
        updateStageTime();
        increasePicker.setValue((int) increaseSteps);
        decreasePicker.setValue((int) decreaseSteps);
    }

    static void updateMaxLoad() {
        maxLoadValue.setText(String.format(Locale.US, "%d W", maxLoad_W));
    }

    static void updateMinLoad() {
        minLoadValue.setText(String.format(Locale.US, "%d W", minLoad_W));
    }

    static void updateMaxTorque() {
        maxLoadValue.setText(String.format(Locale.US, "%.2f kgfm", maxTorque_kgfm));
    }

    static void updateMinTorque() {
        minLoadValue.setText(String.format(Locale.US, "%.2f kgfm", minTorque_kgfm));
    }

    static void updateStageTime() {
        long minutes = (stageTime_s % 3600)/60;
        long seconds = stageTime_s % 60;
        if (stageTime_s >= 3600) {
            long hours = stageTime_s / 3600;
            stageTimeValue.setText(String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds));
        } else stageTimeValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void toggleSaveButton() {
        if (stageTime_s > 0 && (maxLoad_W > minLoad_W || maxTorque_kgfm > minTorque_kgfm) &&
                !ProtocolSelector.ps_reservedNames.contains(protocolNameEditor.getText().toString()))
            saveButton.setVisibility(View.VISIBLE);
        else saveButton.setVisibility(View.INVISIBLE);
    }

    //==================================================
    // CONSTRUCTOR
    //==================================================

    MountainEditor(Context context) {
        imageView = (ImageView) ((Activity) context).findViewById(R.id.me_mountainImage);
        cancelButton = (TextView) ((Activity) context).findViewById(R.id.me_cancelButton);
        saveButton = (TextView) ((Activity) context).findViewById(R.id.me_saveButton);
        protocolNameEditor = (EditText) ((Activity) context).findViewById(R.id.me_protocolNameEditor);
        stepLabel = (TextView) ((Activity) context).findViewById(R.id.me_stepLabel);
        loadStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.me_loadStepGroup);
        oneWattStep = (RadioButton) ((Activity) context).findViewById(R.id.me_oneWattIncrement);
        fiveWattStep = (RadioButton) ((Activity) context).findViewById(R.id.me_fiveWattIncrement);
        tenWattStep = (RadioButton) ((Activity) context).findViewById(R.id.me_tenWattIncrement);
        timeStepGroup = (RadioGroup) ((Activity) context).findViewById(R.id.me_timeStepGroup);
        oneSecondStep = (RadioButton) ((Activity) context).findViewById(R.id.me_oneSecondIncrement);
        fiveSecondStep = (RadioButton) ((Activity) context).findViewById(R.id.me_fiveSecondIncrement);
        oneMinuteStep = (RadioButton) ((Activity) context).findViewById(R.id.me_oneMinuteIncrement);
        fiveMinuteStep = (RadioButton) ((Activity) context).findViewById(R.id.me_fiveMinuteIncrement);
        maxLoadValue = (TextView) ((Activity) context).findViewById(R.id.me_maxLoadValue);
        minLoadValue = (TextView) ((Activity) context).findViewById(R.id.me_minLoadValue);
        stageTimeValue = (TextView) ((Activity) context).findViewById(R.id.me_stageLengthValue);
        maxLoadLeft = (ImageButton) ((Activity) context).findViewById(R.id.me_maxLoadLeft);
        maxLoadRight = (ImageButton) ((Activity) context).findViewById(R.id.me_maxLoadRight);
        minLoadLeft = (ImageButton) ((Activity) context).findViewById(R.id.me_minLoadLeft);
        minLoadRight = (ImageButton) ((Activity) context).findViewById(R.id.me_minLoadRight);
        stageLengthLeft = (ImageButton) ((Activity) context).findViewById(R.id.me_stageLengthLeft);
        stageLengthRight = (ImageButton) ((Activity) context).findViewById(R.id.me_stageLengthRight);
        increasePicker = (NumberPicker) ((Activity) context).findViewById(R.id.me_increasePicker);
        stageLengthLabel = (TextView) ((Activity) context).findViewById(R.id.me_stageLengthLabel);
        increasePickerLabel = (TextView) ((Activity) context).findViewById(R.id.me_increasePickerLabel);
        decreasePicker = (NumberPicker) ((Activity) context).findViewById(R.id.me_decreasePicker);
        decreasePickerLabel = (TextView) ((Activity) context).findViewById(R.id.me_decreasePickerLabel);

        increasePicker.setMinValue(2);
        increasePicker.setMaxValue(16);
        decreasePicker.setMinValue(2);
        decreasePicker.setMaxValue(16);

        //==================================================
        // LISTENERS
        //==================================================

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
                loadVector.add(maxLoad_W);
                loadVector.add(minLoad_W);
                torqueVector.clear();
                torqueVector.add(maxTorque_kgfm);
                torqueVector.add(minTorque_kgfm);
                timeVector.clear();
                timeVector.add(stageTime_s);
                timeVector.add(increaseSteps);
                timeVector.add(decreaseSteps);

                String protocolName = protocolNameEditor.length() == 0 ?
                        "(sem nome)" : protocolNameEditor.getText().toString();

                MainActivity.g_protocol.setProtocolType(Protocol.ProtocolType.MOUNTAIN);
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

        maxLoadRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        maxLoad_W = MainActivity.increaseLong(maxLoad_W,
                                loadStep_W, MainActivity.MAX_LOAD_W);
                        updateMaxLoad();
                        break;
                    case CONSTANT_TORQUE:
                        maxTorque_kgfm = MainActivity.increaseDouble(maxTorque_kgfm,
                                torqueStep_kgfm, MainActivity.MAX_TORQUE_kgfm);
                        updateMaxTorque();
                        break;
                }
                toggleSaveButton();
            }
        });

        maxLoadLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        maxLoad_W = MainActivity.decreaseLong(maxLoad_W,
                                loadStep_W, MainActivity.MIN_LOAD_W);
                        updateMaxLoad();
                        break;
                    case CONSTANT_TORQUE:
                        maxTorque_kgfm = MainActivity.decreaseDouble(maxTorque_kgfm,
                                torqueStep_kgfm, MainActivity.MIN_TORQUE_kgfm);
                        updateMaxTorque();
                        break;
                }
                toggleSaveButton();
            }
        });

        minLoadRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        minLoad_W = MainActivity.increaseLong(minLoad_W,
                                loadStep_W, MainActivity.MAX_LOAD_W);
                        updateMinLoad();
                        break;
                    case CONSTANT_TORQUE:
                        minTorque_kgfm = MainActivity.increaseDouble(minTorque_kgfm,
                                torqueStep_kgfm, MainActivity.MAX_TORQUE_kgfm);
                        updateMinTorque();
                        break;
                }
                toggleSaveButton();
            }
        });

        minLoadLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (MainActivity.g_protocol.getExerciseType()) {
                    case CONSTANT_LOAD:
                        minLoad_W = MainActivity.decreaseLong(minLoad_W,
                                loadStep_W, MainActivity.MIN_LOAD_W);
                        updateMinLoad();
                        break;
                    case CONSTANT_TORQUE:
                        minTorque_kgfm = MainActivity.decreaseDouble(minTorque_kgfm,
                                torqueStep_kgfm, MainActivity.MIN_TORQUE_kgfm);
                        updateMinTorque();
                        break;
                }
                toggleSaveButton();
            }
        });

        stageLengthLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageTime_s = MainActivity.decreaseLong(stageTime_s, timeStep_s, ProtocolEditor.MIN_TIME_s);
                updateStageTime();
                toggleSaveButton();
            }
        });

        stageLengthRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stageTime_s = MainActivity.increaseLong(stageTime_s, timeStep_s, ProtocolEditor.MAX_TIME_s);
                updateStageTime();
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

        increasePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldval, int newval) {
                increaseSteps = newval;
            }
        });

        decreasePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldval, int newval) {
                decreaseSteps = newval;
            }
        });
    }
}
