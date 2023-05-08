package cs1302.api;

import javafx.scene.control.ComboBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.control.ProgressBar;
import com.google.gson.Gson;
import javafx.scene.layout.TilePane;
import com.google.gson.GsonBuilder;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import java.io.IOException;
import javafx.scene.layout.Priority;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import java.lang.IllegalArgumentException;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import java.net.HttpURLConnection;

/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {

 /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();

    Stage stage;
    Scene scene;
    VBox root;
    private HBox topBox;
    private Button searchButton;
    private TextField searchBar;
    private Text txt;
    private HBox loadMessage;
    private Text loadText;
    private HBox imageBox;
    private HBox middleBox;
    private Text countryText;
    private static final String WEATHER_KEY = "fcadcbf6a1944780b74195723230705";
    private static final String WEATHER_API = "http://api.weatherapi.com/v1/current.json?key=";
    private Text dropText;
    private static final String NINJA_KEY = "+VNlSWd1sKbp+u9OF+Mx5A==sTeVLDvyDYnbfoCL";
    private TextField countryBar;
    private Text output;
    private NinjaResponse[] locator;
    private Image weather;
    private Text description;
    private ImageView weatherImg;
/**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */

    public ApiApp() {
        root = new VBox();
        this.topBox = new HBox(2);
        this.searchButton = new Button();
        this.searchBar = new TextField("Enter city");
        this.txt = new Text("City:");
        this.loadMessage = new HBox();
        this.loadText = new Text("Type in a city in the first bar,type in the country in the second"
        + " bar and click the search button");
        this.middleBox = new HBox();
        searchBar.setText("Enter City");
        searchButton.setText("Search");
        System.out.println("init() called");
        this.countryBar = new TextField("Enter Country");
        this.countryText = new Text("Country: ");
        this.dropText = new Text("Select Distance in Miles");
        this.output = new Text();
        this.description = new Text();
        this.weatherImg = new ImageView("file:resources/Solid_grey.svg.png");
        this.imageBox = new HBox();
        weatherImg.setFitHeight(300);
        weatherImg.setFitWidth(300);
        this.loadText.setWrappingWidth(525);
        root.getChildren().addAll(topBox,loadMessage,imageBox);
        topBox.getChildren().addAll(txt, searchBar, countryText,
            countryBar, searchButton);
        imageBox.setMaxHeight(200);
        imageBox.setMaxWidth(200);
        imageBox.getChildren().addAll(weatherImg, description);
        loadMessage.getChildren().add(loadText);
        root.setMaxHeight(700);
        root.setMaxWidth(300);
    } // ApiApp
    /** {@inheritDoc} */

    @Override
    public void init() {
        EventHandler<ActionEvent> search = (ActionEvent e) -> {
            this.toLocation();
        };
        searchButton.setOnAction(search);

    }


    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;
        this.scene = new Scene(this.root);
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

/**
 *Acesses 2 APIs to convert a city to latitude and longitdue coordinates to pass into
 *an API that uses the cordinates to determine weather.
 *If the text fields are not completed properly or duplicate cities are found,
 *the weather of the best match to the inputs given will be shown.
 *If the program errors out than current results will be left displayed.
 *@throws IllegalArgumentException if no results are found from the search terms given
 */

    public void toLocation() {
        String term = URLEncoder.encode(this.searchBar.getText(), StandardCharsets.UTF_8);
        String  code = URLEncoder.encode(this.countryBar.getText(), StandardCharsets.UTF_8);

        String url = "https://api.api-ninjas.com/v1/geocoding?city=" + term + "&country=" + code;

        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).
                header("X-Api-Key", NINJA_KEY).build();
            HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
            if (response.body().equals("{\"message\": \"Internal server error\"}")) {
                throw new Error("Internal server error");
            }
            locator  =
                GSON.<NinjaResponse[]>fromJson(response.body(), NinjaResponse[].class);
            if (locator.length == 0) {
                throw new IllegalArgumentException("No results found, enter new terms in"
                + "text fields");
            }
            System.out.println("hi");
            Double latitude = locator[0].latitude;
            Double longitude = locator[0].longitude;
            String query = WEATHER_API + WEATHER_KEY + "&q=" + latitude + ","
                + longitude + "&aqi=no";
            HttpRequest secRequest = HttpRequest.newBuilder().uri(URI.create(query)).build();
            HttpResponse<String> sresponse = HTTP_CLIENT.send(secRequest, BodyHandlers.ofString());
            System.out.println(sresponse.body());
            WeatherResponse ports =
                GSON.<WeatherResponse>fromJson(sresponse.body(), WeatherResponse.class);
            String urlRe = "https:" + ports.current.condition.icon;
            this.weather = new Image(urlRe);
            this.weatherImg.setImage(weather);
            description.setText("The weather is:\n" + ports.current.condition.text + '\n' +
                "The current temperature is:\n" + ports.current.tempF + " degrees Fahrenheit" +
                "\nThe humidity is:\n" + ports.current.humidity +  "%");
        } catch (Throwable t) {
            this.alert(t.toString());
        }
    }



    /**
     *Creates an alert box and prints out the string given.
     *@param str - string to print out
     */

    public void alert(String str) {
        TextArea text = new TextArea(str);
        text.setWrapText(true);
        text.setEditable(false);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(true);
        alert.showAndWait();
        searchButton.setDisable(false);
    } // alert





} // ApiApp
