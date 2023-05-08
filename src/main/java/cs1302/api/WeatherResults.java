package cs1302.api;

import com.google.gson.annotations.SerializedName;
/**
 *Creates an object condition from the variables in the Conditions
 *class and cretaes 2 variables for WeatherResponse.
 */

public class WeatherResults {

    @SerializedName("temp_f") Double tempF;
    Conditions condition;
    Double humidity;
}
