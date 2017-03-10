package fr.bamlab.reactnativenumberpickerdialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Gravity;
// import android.view.ViewGroup.LayoutParams;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
//For Bridge
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;


class RNNumberPickerDialogModule extends ReactContextBaseJavaModule {
    private Context context;
    static int glo;
    public RNNumberPickerDialogModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Override
    public String getName() {
        return "RNNumberPickerDialog";
    }


    private ArrayList<String> arrayToList(ReadableArray array) {
        ArrayList<String> values = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            String value = "";
            switch (array.getType(i).name()) {
                case "Boolean":
                    value = String.valueOf(array.getBoolean(i));
                    break;
                case "Number":
                    try {
                        value = String.valueOf(array.getInt(i));
                    } catch (Exception e) {
                        value = String.valueOf(array.getDouble(i));
                    }
                    break;
                case "String":
                    value = array.getString(i);
                    break;
            }
            values.add(value);
        }

        return values;
    }
    private String[] setMultipleData(ReadableArray childArray){
        ArrayList<String> values = new ArrayList<String>();
        //ReadableArray childArray = array.getArray(i);
        values = arrayToList(childArray);
        return values.toArray(new String[values.size()]);
    }
    public static boolean checkIfInt(String s) {
        for (int i = 0, n = s.length(); i < n; i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    @ReactMethod
    public void show(final ReadableMap options, final Callback onSuccess, final Callback onFailure) {
        ReadableArray values = options.getArray("values");
        ReadableArray selectedValues = options.getArray("selectedValue");
        //Picker Data
        String[] displayedValues;
        String[] displayedValues1;
        if (values.size() == 0) {
            onFailure.invoke("values array must not be empty");
            return;
        }

        final NumberPicker picker = new NumberPicker(getCurrentActivity());
        final NumberPicker picker2 = new NumberPicker(getCurrentActivity());
        //picker.setMinValue(0);
        //picker.setMaxValue(values.size() -1);

        // String[] displayedValues = new String[values.size()];
        // for(int i = 0;i<values.size();++i) {
        //     displayedValues[i] = values.getString(i);
        // }
        picker.setMinValue(0);
        picker.setMaxValue(values.getArray(0).size()-1);
        displayedValues = new String[values.getArray(0).size()];

        //Set data for picker
        displayedValues = setMultipleData(values.getArray(0));
        picker.setDisplayedValues(displayedValues);
        if(selectedValues.getType(0).name()=="String"){
            picker.setValue(Arrays.asList(displayedValues).indexOf(String.valueOf(selectedValues.getString(0))));
        }else{
            picker.setValue(Arrays.asList(displayedValues).indexOf(String.valueOf(selectedValues.getInt(0))));
        }
        picker.setWrapSelectorWheel(false);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);


        //Create LinearLayout Dynamically
        LinearLayout layout = new LinearLayout(getCurrentActivity());
        TextView textview = new TextView(getCurrentActivity());
        textview.setText("   to   ");
        layout.setGravity(Gravity.CENTER);
        //Setup Layout Attributes
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        // params.layout_gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(picker);
        //If year picker, second picker data
        if(values.size()>1){
            displayedValues1 = new String[values.getArray(2).size()];
            picker2.setMinValue(0);
            picker2.setMaxValue(values.getArray(2).size()-1);
            displayedValues1 = setMultipleData(values.getArray(2));
            picker2.setDisplayedValues(displayedValues1);
            picker2.setValue(Arrays.asList(displayedValues1).indexOf(String.valueOf(selectedValues.getInt(2))));
            picker2.setWrapSelectorWheel(false);
            picker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            layout.addView(textview);
            layout.addView(picker2);
        }

        new AlertDialog.Builder(getCurrentActivity())
                .setTitle(options.getString("title"))
                .setMessage(options.getString("message"))
                .setView(layout)
                .setPositiveButton(options.getString("positiveButtonLabel"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // WritableMap map = Arguments.createMap();
                        WritableArray array = Arguments.createArray();
                        // Send selected Value from Picker 1
                        String[] pickerValues= picker.getDisplayedValues();
                        int selectedIndex1 = (int)picker.getValue();
                        if(checkIfInt(pickerValues[selectedIndex1])){
                            array.pushInt(Integer.valueOf(pickerValues[selectedIndex1]));
                            array.pushInt(0);
                            // Send selected Value from Picker 2
                            pickerValues= picker2.getDisplayedValues();
                            int selectedIndex2 = (int)picker2.getValue();
                            array.pushInt(Integer.valueOf(pickerValues[selectedIndex2]));
                            onSuccess.invoke(array);
                        } else {
                            array.pushString(pickerValues[selectedIndex1]);
                            onSuccess.invoke(array);
                        }
                    }
                })
                .setNegativeButton(options.getString("negativeButtonLabel"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        onSuccess.invoke(-1);
                    }
                })
                .create()
                .show();
    }
}
