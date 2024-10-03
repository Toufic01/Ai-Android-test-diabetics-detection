package com.example.diabetics_checker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;
import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {

    Interpreter tflite;
    EditText inputAge, inputSex, inputHighChol, inputBMI, inputPhysActivity, inputAlcohol, inputPhysHlth, inputHighBP;
    TextView predictionResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Load the TFLite model
        try {
            tflite = new Interpreter(Utils.loadModelFile(this, "diabetes.tflite"));
        } catch (Exception e) {
            e.printStackTrace();
        }



        // Initialize the UI components
        inputAge = findViewById(R.id.input_age);
        inputSex = findViewById(R.id.input_sex);  // Correct
        inputHighChol = findViewById(R.id.input_high_chol);
        inputBMI = findViewById(R.id.input_bmi);
        inputPhysActivity = findViewById(R.id.input_phys_activity);
        inputAlcohol = findViewById(R.id.input_alcohol);
        inputPhysHlth = findViewById(R.id.input_phys_health);
        inputHighBP = findViewById(R.id.input_high_bp);
        predictionResult = findViewById(R.id.prediction_result);

        Button predictButton = findViewById(R.id.predict_button);
        predictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predict();
            }
        });
    }

    private void predict() {
        // Prepare the input data as a float array
        float[] inputData = new float[]{
                Float.parseFloat(inputAge.getText().toString()),
                Float.parseFloat(inputSex.getText().toString()),
                Float.parseFloat(inputHighChol.getText().toString()),
                Float.parseFloat(inputBMI.getText().toString()),
                Float.parseFloat(inputPhysActivity.getText().toString()),
                Float.parseFloat(inputAlcohol.getText().toString()),
                Float.parseFloat(inputPhysHlth.getText().toString()),
                Float.parseFloat(inputHighBP.getText().toString())
        };

        // Create a ByteBuffer to pass the input data to the model
        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(8 * 4);  // 8 features * 4 bytes (float)
        inputBuffer.order(ByteOrder.nativeOrder());
        for (float input : inputData) {
            inputBuffer.putFloat(input);
        }

        // Prepare output buffer to receive the model's prediction
        float[][] output = new float[1][10];  // Output shape [1, 10] based on your model

        // Run inference
        tflite.run(inputBuffer, output);

        // Show the prediction result
        int predictedClass = argMax(output[0]);
        predictionResult.setText("Prediction: " + predictedClass);
    }

    // Helper function to find the index of the highest value
    private int argMax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;

    }
}