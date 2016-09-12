package com.inbramed.vicente.cicloergometer;

import android.app.Activity;
import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;
import java.util.Vector;

public class ProtocolSelector implements AdapterView.OnItemSelectedListener {

    //==================================================
    // CONSTANTS
    //==================================================

    final static String MANUAL = "Manual";
    final static String NEW_PROTOCOL = "Novo protocolo em estágios";
    final static String NEW_INTERVALED = "Novo protocolo intervalado";
    final static String NEW_RANDOM = "Novo protocolo aleatório";
    final static String NEW_RAMP = "Novo protocolo rampa";
    final static String NEW_MOUNTAIN = "Novo protocolo montanha";

    final static ProtocolSpinnerItem MANUAL_ITEM = new ProtocolSpinnerItem(MANUAL,
            "Descrição: utilize os comandos do\n" +
                    "           painel para controlar a carga\n" +
                    "           mecânica do cicloergômetro.",
            "Descrição: utilize os comandos do\n" +
                    "           painel para controlar o torque\n" +
                    "           mecânico do cicloergômetro.");

    final static ProtocolSpinnerItem NEW_PROTOCOL_ITEM = new ProtocolSpinnerItem(NEW_PROTOCOL,
            "Descrição: crie uma sequência de\n" +
                    "           estágios com carga mecânica\n" +
                    "           e duração predeterminados.",
            "Descrição: crie uma sequência de\n" +
                    "           estágios com torque mecânico\n" +
                    "           e duração predeterminados.");

    final static ProtocolSpinnerItem NEW_INTERVALED_ITEM = new ProtocolSpinnerItem(NEW_INTERVALED,
            "Descrição: crie uma sequência periódica com\n" +
                    "           intervalos para exercício e descanso.\n" +
                    "           É possível determinar duração e\n" +
                    "           carga mecânica de cada intervalo.",
            "Descrição: crie uma sequência periódica com\n" +
                    "           intervalos para exercício e descanso.\n" +
                    "           É possível determinar duração e\n" +
                    "           torque mecânico de cada intervalo.");

    final static ProtocolSpinnerItem NEW_RANDOM_ITEM = new ProtocolSpinnerItem(NEW_RANDOM,
            "Descrição: determine a média e desvio\n" +
                    "           padrão para uma sequência de\n" +
                    "           estágios com carga mecânica\n" +
                    "           aleatória e duração predeterminada.",
            "Descrição: determine a média e desvio\n" +
                    "           padrão para uma sequência de\n" +
                    "           estágios com torque mecânico\n" +
                    "           aleatória e duração predeterminada.");

    final static ProtocolSpinnerItem NEW_RAMP_ITEM = new ProtocolSpinnerItem(NEW_RAMP,
            "Descrição: um protocolo de carga mecânica crescente.\n" +
                    "           É possível determinar a duração do protocolo,\n" +
                    "           carga mecânica mínima e carga mecânica máxima.",
            "Descrição: um protocolo de torque mecânico crescente.\n" +
                    "           É possível determinar a duração do protocolo,\n" +
                    "           torque mecânico mínimo e torque mecânico máximo.");

    final static ProtocolSpinnerItem NEW_MOUNTAIN_ITEM = new ProtocolSpinnerItem(NEW_MOUNTAIN,
            "Descrição: crie uma sequência com intervalos\n" +
                    "           de carga mecânica crescente e decrescente em degraus.\n" +
                    "           É possível determinar duração de cada degrau,\n" +
                    "           número de degraus e carga mecânica mínima e máxima.\n",
            "Descrição: crie uma sequência com intervalos\n" +
                    "           de torque mecânico crescente e decrescente em degraus.\n" +
                    "           É possível determinar duração de cada degrau,\n" +
                    "           número de degraus e torque mecânico mínimo e máximo.\n");

    final static String PROTOCOL_TABLE_HEADER_LOAD =
            "Estágio / Carga / Duração\n--------------------------\n";
    final static String PROTOCOL_TABLE_HEADER_TORQUE =
            "Estágio / Torque / Duração\n---------------------------\n";

    //==================================================
    // STATIC FIELDS
    //==================================================

    static Context context;
    static ProtocolSpinnerItem protocolSpinnerItem = MANUAL_ITEM;
    static Vector<ProtocolSpinnerItem> ps_protocolSpinnerItemVector = new Vector<>();
    static Vector<String> ps_reservedNames = new Vector<>();

    //==================================================
    // VIEW FIELDS
    //==================================================

    static ImageView ps_intervaledImage;
    static RadioButton ps_constantLoad;
    static RadioButton ps_constantTorque;
    static RadioGroup ps_exerciseTypeGroup;
    static Spinner ps_protocolSpinner;
    static TextView ps_cancelDeleteButton;
    static TextView ps_confirmButton;
    static TextView ps_confirmDeleteButton;
    static TextView ps_deleteButton;
    static TextView ps_descriptionLabel;
    static TextView ps_exerciseTypeLabel;
    static TextView ps_newProtocolLabel;
    static TextView ps_loadExerciseValue;
    static TextView ps_loadRestValue;
    static TextView ps_timeExerciseValue;
    static TextView ps_timeRestValue;

    //==================================================
    // STATIC METHODS
    //==================================================

    static void setVisibility(int visibility) {
        ps_newProtocolLabel.setVisibility(visibility);
        ps_protocolSpinner.setVisibility(visibility);
        ps_descriptionLabel.setVisibility(visibility);
        ps_confirmButton.setVisibility(visibility);
        ps_exerciseTypeLabel.setVisibility(visibility);
        ps_exerciseTypeGroup.setVisibility(visibility);
        ps_constantLoad.setVisibility(visibility);
        ps_constantTorque.setVisibility(visibility);
        ps_confirmDeleteButton.setVisibility(View.INVISIBLE);
        ps_cancelDeleteButton.setVisibility(View.INVISIBLE);
        ps_deleteButton.setVisibility(View.INVISIBLE);
        setImageVisibility(View.INVISIBLE);
        updateSpinner(ps_protocolSpinner);
    }

    static void setImageVisibility(int visibility) {
        ps_intervaledImage.setVisibility(visibility);
        ps_loadExerciseValue.setVisibility(visibility);
        ps_loadRestValue.setVisibility(visibility);
        ps_timeExerciseValue.setVisibility(visibility);
        ps_timeRestValue.setVisibility(visibility);
    }

    static void updateSpinner(Spinner spinner) {
        ps_protocolSpinnerItemVector.clear();
        ps_protocolSpinnerItemVector.add(MANUAL_ITEM);
        ps_protocolSpinnerItemVector.add(NEW_PROTOCOL_ITEM);
        ps_protocolSpinnerItemVector.add(NEW_INTERVALED_ITEM);
        ps_protocolSpinnerItemVector.add(NEW_RAMP_ITEM);
        ps_protocolSpinnerItemVector.add(NEW_MOUNTAIN_ITEM);
        ps_protocolSpinnerItemVector.add(NEW_RANDOM_ITEM);
        for (int i = 0; i < MainActivity.protocolNames.size(); i++) {
            ProtocolSpinnerItem protocolSpinnerItem =
                    new ProtocolSpinnerItem(MainActivity.protocolNames.get(i), "", "");
            ps_protocolSpinnerItemVector.add(protocolSpinnerItem);
        }
        Vector<String> stringVector = new Vector<>();
        for (int i = 0; i < ps_protocolSpinnerItemVector.size(); i++)
            stringVector.add(ps_protocolSpinnerItemVector.get(i).getName());
        ArrayAdapter<String> protocolAdapter =
                new ArrayAdapter<>(context, R.layout.arrayadapter_resource, stringVector);
        protocolAdapter.setDropDownViewResource(R.layout.arrayadapter_resource);
        spinner.setAdapter(protocolAdapter);
    }

    static void updateDescription(ProtocolSpinnerItem protocolSpinnerItem, TextView textView) {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                textView.setText(protocolSpinnerItem.getDescriptionLoad());
                break;
            case CONSTANT_TORQUE:
                textView.setText(protocolSpinnerItem.getDescriptionTorque());
                break;
        }
    }

    static void updateExercise() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD: ps_constantLoad.setChecked(true); break;
            case CONSTANT_TORQUE: ps_constantTorque.setChecked(true);
        }
        updateDescription(protocolSpinnerItem, ps_descriptionLabel);
    }

    static void updateProtocolTable() {
        String string;
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                string = ProtocolSelector.PROTOCOL_TABLE_HEADER_LOAD;
                for (int i = 0; i < MainActivity.g_protocol.getLoadVector().size(); i++) {
                    long minutes = (MainActivity.g_protocol.getTimeVector().get(i) % 3600) / 60;
                    long seconds = MainActivity.g_protocol.getTimeVector().get(i) % 60;
                    string += String.format(Locale.US, "%d / %d W / %02d:%02d\n",
                            i + 1, MainActivity.g_protocol.getLoadVector().get(i), minutes, seconds);
                }
                break;
            case CONSTANT_TORQUE:
                string = ProtocolSelector.PROTOCOL_TABLE_HEADER_TORQUE;
                for (int i = 0; i < MainActivity.g_protocol.getTorqueVector().size(); i++) {
                    long minutes = (MainActivity.g_protocol.getTimeVector().get(i) % 3600) / 60;
                    long seconds = MainActivity.g_protocol.getTimeVector().get(i) % 60;
                    string += String.format(Locale.US, "%d / %.2f kgfm / %02d:%02d\n",
                            i + 1, MainActivity.g_protocol.getTorqueVector().get(i), minutes, seconds);
                }
                break;
            default:
                string = "";
        }
        ps_descriptionLabel.setText(string);
    }

    static void updateIntervaledValues() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                ps_intervaledImage.setImageResource(R.drawable.intervaled_load);
                ps_loadRestValue.setText(String.format(Locale.US, "%d W",
                        MainActivity.g_protocol.getLoadVector().get(0)));
                ps_loadExerciseValue.setText(String.format(Locale.US, "%d W",
                        MainActivity.g_protocol.getLoadVector().get(1)));
                break;
            case CONSTANT_TORQUE:
                ps_intervaledImage.setImageResource(R.drawable.intervaled_torque);
                ps_loadRestValue.setText(String.format(Locale.US, "%.2f kgfm",
                        MainActivity.g_protocol.getTorqueVector().get(0)));
                ps_loadExerciseValue.setText(String.format(Locale.US, "%.2f kgfm",
                        MainActivity.g_protocol.getTorqueVector().get(1)));
                break;
        }

        long minutes = (MainActivity.g_protocol.getTimeVector().get(0) % 3600) / 60;
        long seconds = MainActivity.g_protocol.getTimeVector().get(0) % 60;
        ps_timeRestValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));

        minutes = (MainActivity.g_protocol.getTimeVector().get(1) % 3600) / 60;
        seconds = MainActivity.g_protocol.getTimeVector().get(1) % 60;
        ps_timeExerciseValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void updateRandomValues() {
        long minutes = (MainActivity.g_protocol.getTimeVector().get(0) % 3600)/60;
        long seconds = MainActivity.g_protocol.getTimeVector().get(0) % 60;
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                ps_descriptionLabel.setText(String.format(Locale.US,
                        "Protocolo aleatório\n" +
                                "Média: %d W\n" +
                                "Desvio padrão: %d W\n" +
                                "Duração por estágio: %02d:%02d",
                        MainActivity.g_protocol.getLoadVector().get(0),
                        MainActivity.g_protocol.getLoadVector().get(1),
                        minutes, seconds));
                break;
            case CONSTANT_TORQUE:
                ps_descriptionLabel.setText(String.format(Locale.US,
                        "Protocolo aleatório\n" +
                                "Média: %.2f kgfm\n" +
                                "Desvio padrão: %.2f kgfm\n" +
                                "Duração por estágio: %02d:%02d",
                        MainActivity.g_protocol.getTorqueVector().get(0),
                        MainActivity.g_protocol.getTorqueVector().get(1),
                        minutes, seconds));
                break;
        }
    }

    static void updateRampValues() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                ps_intervaledImage.setImageResource(R.drawable.ramp_load);
                ps_loadRestValue.setText(String.format(Locale.US, "%d W",
                        MainActivity.g_protocol.getLoadVector().get(1)));
                ps_loadExerciseValue.setText(String.format(Locale.US, "%d W",
                        MainActivity.g_protocol.getLoadVector().get(0)));
                break;
            case CONSTANT_TORQUE:
                ps_intervaledImage.setImageResource(R.drawable.ramp_torque);
                ps_loadRestValue.setText(String.format(Locale.US, "%.2f kgfm",
                        MainActivity.g_protocol.getTorqueVector().get(1)));
                ps_loadExerciseValue.setText(String.format(Locale.US, "%.2f kgfm",
                        MainActivity.g_protocol.getTorqueVector().get(0)));
                break;
        }

        long minutes = (MainActivity.g_protocol.getTimeVector().get(0) % 3600) / 60;
        long seconds = MainActivity.g_protocol.getTimeVector().get(0) % 60;
        ps_timeExerciseValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));
    }

    static void updateMountainValues() {
        switch (MainActivity.g_protocol.getExerciseType()) {
            case CONSTANT_LOAD:
                ps_intervaledImage.setImageResource(R.drawable.mountain_load);
                ps_loadRestValue.setText(String.format(Locale.US, "%d W",
                        MainActivity.g_protocol.getLoadVector().get(1)));
                ps_loadExerciseValue.setText(String.format(Locale.US, "%d W",
                        MainActivity.g_protocol.getLoadVector().get(0)));
                break;
            case CONSTANT_TORQUE:
                ps_intervaledImage.setImageResource(R.drawable.mountain_torque);
                ps_loadRestValue.setText(String.format(Locale.US, "%.2f kgfm",
                        MainActivity.g_protocol.getTorqueVector().get(1)));
                ps_loadExerciseValue.setText(String.format(Locale.US, "%.2f kgfm",
                        MainActivity.g_protocol.getTorqueVector().get(0)));
                break;
        }

        long minutes = (MainActivity.g_protocol.getTimeVector().get(0) % 3600) / 60;
        long seconds = MainActivity.g_protocol.getTimeVector().get(0) % 60;
        ps_timeRestValue.setText(String.format(Locale.US, "%02d:%02d", minutes, seconds));

        ps_timeExerciseValue.setText(String.format(Locale.US, "%d estágios",
                MainActivity.g_protocol.getTimeVector().get(1) +
                        MainActivity.g_protocol.getTimeVector().get(2)));
    }

    static void updateProtocolView() {
        switch (MainActivity.g_protocol.getProtocolType()) {
            case STAGES:
                updateProtocolTable();
                ps_descriptionLabel.setVisibility(View.VISIBLE);
                setImageVisibility(View.INVISIBLE);
                break;
            case INTERVALED:
                updateIntervaledValues();
                ps_descriptionLabel.setVisibility(View.INVISIBLE);
                setImageVisibility(View.VISIBLE);
                break;
            case RANDOM:
                updateRandomValues();
                ps_descriptionLabel.setVisibility(View.VISIBLE);
                setImageVisibility(View.INVISIBLE);
                break;
            case RAMP:
                updateRampValues();
                ps_descriptionLabel.setVisibility(View.INVISIBLE);
                setImageVisibility(View.VISIBLE);
                ps_timeRestValue.setVisibility(View.INVISIBLE);
                break;
            case MOUNTAIN:
                updateMountainValues();
                ps_descriptionLabel.setVisibility(View.INVISIBLE);
                setImageVisibility(View.VISIBLE);
                break;
        }
    }

    //==================================================
    // CONSTRUCTOR
    //==================================================

    ProtocolSelector(Context context) {
        ProtocolSelector.context = context;

        ps_protocolSpinner = (Spinner) ((Activity) context).findViewById(R.id.ps_protocolSpinner);
        ps_confirmButton = (TextView) ((Activity) context).findViewById(R.id.ps_confirmButton);
        ps_deleteButton = (TextView) ((Activity) context).findViewById(R.id.ps_deleteButton);
        ps_confirmDeleteButton = (TextView) ((Activity) context).findViewById(R.id.ps_confirmDeleteButton);
        ps_cancelDeleteButton = (TextView) ((Activity) context).findViewById(R.id.ps_cancelDeleteButton);
        ps_descriptionLabel = (TextView) ((Activity) context).findViewById(R.id.ps_descriptionLabel);
        ps_newProtocolLabel = (TextView) ((Activity) context).findViewById(R.id.ps_newProtocolLabel);
        ps_exerciseTypeLabel = (TextView) ((Activity) context).findViewById(R.id.ps_exerciseTypeLabel);
        ps_exerciseTypeGroup = (RadioGroup) ((Activity) context).findViewById(R.id.ps_exerciseTypeGroup);
        ps_constantLoad = (RadioButton) ((Activity) context).findViewById(R.id.ps_constantLoad);
        ps_constantTorque = (RadioButton) ((Activity) context).findViewById(R.id.ps_constantTorque);
        ps_intervaledImage = (ImageView) ((Activity) context).findViewById(R.id.ps_intervaledImage);
        ps_loadExerciseValue = (TextView) ((Activity) context).findViewById(R.id.ps_loadExerciseValue);
        ps_loadRestValue = (TextView) ((Activity) context).findViewById(R.id.ps_loadRestValue);
        ps_timeExerciseValue = (TextView) ((Activity) context).findViewById(R.id.ps_timeExerciseValue);
        ps_timeRestValue = (TextView) ((Activity) context).findViewById(R.id.ps_timeRestValue);

        ps_reservedNames.add(MANUAL);
        ps_reservedNames.add(NEW_PROTOCOL);
        ps_reservedNames.add(NEW_INTERVALED);
        ps_reservedNames.add(NEW_RANDOM);
        ps_reservedNames.add(NEW_RAMP);
        ps_reservedNames.add(NEW_MOUNTAIN);
        ps_reservedNames.add(ProtocolEditor.PRE_TRAIN_STR);
        ps_reservedNames.add(ProtocolEditor.POST_TRAIN_STR);

        updateSpinner(ps_protocolSpinner);
        ps_protocolSpinner.setOnItemSelectedListener(this);
        ps_descriptionLabel.setMovementMethod(new ScrollingMovementMethod());

        //==================================================
        // LISTENERS
        //==================================================

        ps_exerciseTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (ps_constantLoad.isChecked())
                    MainActivity.g_protocol.setExerciseType(Protocol.ExerciseType.CONSTANT_LOAD);
                else if (ps_constantTorque.isChecked())
                    MainActivity.g_protocol.setExerciseType(Protocol.ExerciseType.CONSTANT_TORQUE);
                updateDescription(protocolSpinnerItem, ps_descriptionLabel);
            }
        });

        ps_confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (protocolSpinnerItem == NEW_PROTOCOL_ITEM) {
                    MainActivity.g_currentWindow = MainActivity.Window.PROTOCOL_EDITOR;
                    ProtocolEditor.protocolNameEditor.setFocusableInTouchMode(true);
                    setVisibility(View.INVISIBLE);
                    ProtocolEditor.updateVariables();
                    ProtocolEditor.setVisibility(View.VISIBLE);
                } else if (protocolSpinnerItem == NEW_INTERVALED_ITEM) {
                    MainActivity.g_currentWindow = MainActivity.Window.INTERVALED_EDITOR;
                    setVisibility(View.INVISIBLE);
                    IntervaledEditor.updateVariables();
                    IntervaledEditor.setVisibility(View.VISIBLE);
                } else if (protocolSpinnerItem == NEW_RANDOM_ITEM) {
                    MainActivity.g_currentWindow = MainActivity.Window.RANDOM_EDITOR;
                    setVisibility(View.INVISIBLE);
                    RandomEditor.updateVariables();
                    RandomEditor.setVisibility(View.VISIBLE);
                } else if (protocolSpinnerItem == NEW_RAMP_ITEM) {
                    MainActivity.g_currentWindow = MainActivity.Window.TRIANGULAR_EDITOR;
                    setVisibility(View.INVISIBLE);
                    RampEditor.updateVariables();
                    RampEditor.setVisibility(View.VISIBLE);
                } else if (protocolSpinnerItem == NEW_MOUNTAIN_ITEM) {
                    MainActivity.g_currentWindow = MainActivity.Window.MOUNTAIN_EDITOR;
                    setVisibility(View.INVISIBLE);
                    MountainEditor.updateVariables();
                    MountainEditor.setVisibility(View.VISIBLE);
                } else {
                    MainActivity.g_currentWindow = MainActivity.Window.MAIN;
                    setVisibility(View.INVISIBLE);
                    MainActivity.updateProtocol();
                    MainActivity.updateVariables();
                    MainActivity.setVisibility(View.VISIBLE);
                }
            }
        });

        ps_deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ps_protocolSpinner.setEnabled(false);
                ps_confirmDeleteButton.setVisibility(View.VISIBLE);
                ps_cancelDeleteButton.setVisibility(View.VISIBLE);
                ps_deleteButton.setVisibility(View.INVISIBLE);
                ps_confirmButton.setVisibility(View.INVISIBLE);
                ps_descriptionLabel.setText(String.format(Locale.US,
                        "Deseja mesmo excluir o protocolo %s?", MainActivity.g_protocol.getName()));
                ps_descriptionLabel.setVisibility(View.VISIBLE);
                setImageVisibility(View.INVISIBLE);
            }
        });

        ps_cancelDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ps_protocolSpinner.setEnabled(true);
                ps_confirmDeleteButton.setVisibility(View.INVISIBLE);
                ps_cancelDeleteButton.setVisibility(View.INVISIBLE);
                ps_deleteButton.setVisibility(View.VISIBLE);
                ps_confirmButton.setVisibility(View.VISIBLE);
                updateProtocolView();
            }
        });

        ps_confirmDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ps_protocolSpinner.setEnabled(true);
                ps_confirmDeleteButton.setVisibility(View.INVISIBLE);
                ps_cancelDeleteButton.setVisibility(View.INVISIBLE);
                MainActivity.protocolNames.remove(MainActivity.g_protocol.getName());
                MainActivity.saveProtocolNames();
                MainActivity.removeProtocol(MainActivity.g_protocol.getName());
                updateSpinner(ps_protocolSpinner);
                ps_protocolSpinner.setSelection(0);
                ps_confirmButton.setVisibility(View.VISIBLE);
            }
        });
    }

    //==================================================
    // SPINNER
    //==================================================

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        protocolSpinnerItem = ps_protocolSpinnerItemVector.get(i);
        if (MainActivity.g_currentWindow == MainActivity.Window.PROTOCOL_SELECTOR) {
            if (protocolSpinnerItem == MANUAL_ITEM) {
                MainActivity.g_protocol.setProtocolType(Protocol.ProtocolType.MANUAL);
            }
            if (protocolSpinnerItem == MANUAL_ITEM ||
                    protocolSpinnerItem == NEW_PROTOCOL_ITEM ||
                    protocolSpinnerItem == NEW_INTERVALED_ITEM ||
                    protocolSpinnerItem == NEW_RANDOM_ITEM ||
                    protocolSpinnerItem == NEW_RAMP_ITEM ||
                    protocolSpinnerItem == NEW_MOUNTAIN_ITEM) {
                if (ps_constantLoad.isChecked()) {
                    MainActivity.g_protocol.setExerciseType(Protocol.ExerciseType.CONSTANT_LOAD);
                } else if (ps_constantTorque.isChecked()) {
                    MainActivity.g_protocol.setExerciseType(Protocol.ExerciseType.CONSTANT_TORQUE);
                }
                updateDescription(protocolSpinnerItem, ps_descriptionLabel);
                ps_descriptionLabel.setVisibility(View.VISIBLE);
                setImageVisibility(View.INVISIBLE);
                ps_deleteButton.setVisibility(View.INVISIBLE);
                ps_exerciseTypeLabel.setVisibility(View.VISIBLE);
                ps_exerciseTypeGroup.setVisibility(View.VISIBLE);
            } else {
                MainActivity.g_protocol = MainActivity.openProtocol(protocolSpinnerItem.getName(),
                        MainActivity.g_protocol);
                ps_deleteButton.setVisibility(View.VISIBLE);
                ps_exerciseTypeLabel.setVisibility(View.INVISIBLE);
                ps_exerciseTypeGroup.setVisibility(View.INVISIBLE);
                updateProtocolView();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
