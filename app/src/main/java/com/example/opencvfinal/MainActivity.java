package com.example.opencvfinal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.opencvfinal.dao.AuthService;
import com.example.opencvfinal.dao.UserDetail;
import com.example.opencvfinal.entities.AdminUserDTO;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView image;
    private List<Point> clickedPoints = new ArrayList<>();

    private TextView left;
    private TextView right;

    private Button processImageBtn;
    private Bitmap selectedBitmap;
    private Button resetBtn;
    private Button sendBtn;

    private Button dsicBtn;
    AdminUserDTO adminUserDTO;

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        Uri uri = data.getData();
                            selectedBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dent1);
                            image.setImageBitmap(selectedBitmap);
                            // Process the selected image
                            processImageAndDrawLines(selectedBitmap);

                    }
                }
            });

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    // your code
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.dent_image);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);
        processImageBtn = findViewById(R.id.btnProccess);
        resetBtn = findViewById(R.id.btnReset);
        sendBtn = findViewById(R.id.btnSend);
        dsicBtn = findViewById(R.id.btnDisc);
        String username = getIntent().getStringExtra("username");
        fetchUserDetails(username);

        // Initialize OpenCV asynchronously
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "OpenCV initialization failed.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mOpenCVCallBack);
        } else {
            Log.d("OpenCV", "OpenCV initialization succeeded.");
            // OpenCV is already initialized, you can execute your code here
            processImageBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                launcher.launch(intent);
            });
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img);
            //processImageAndDrawLines(bitmap);
        }

        // Set a click listener for the button to open the image picker
        processImageBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            launcher.launch(intent);
        });

        resetBtn.setOnClickListener(v -> resetActivity());
        dsicBtn.setOnClickListener(v->clearAuthToken());
    }

    private void resetActivity() {
        clickedPoints.clear();
        image.setImageBitmap(null);
        left.setText("Left Angle: ");
        right.setText("Right Angle: ");
    }

    private void processImageAndDrawLines(Bitmap originalBitmap) {
        // Convert Bitmap to Mat with CV_8UC4 type
        Mat matImage = new Mat();
        Utils.bitmapToMat(originalBitmap, matImage);

        // Perform image processing
        Mat edges = preprocessImage(matImage);

        // Find and draw points
        MatOfPoint points = findAndDrawPoints(edges, matImage);

        // Display the processed image with points in ImageView
        Bitmap processedBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matImage, processedBitmap);
        image.setImageBitmap(processedBitmap);

        // Set click listener for further interaction
        image.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN && clickedPoints.size() < 4) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                Mat processedImage = mouseClick(points, x, y, matImage);

                Mat linesImage = drawLinesBetweenPoints(processedImage);

                // Convert Mat to Bitmap with CV_8UC4 type
                Bitmap linesBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(linesImage, linesBitmap);
                image.setImageBitmap(linesBitmap);
            } else if (clickedPoints.size()==4) {
                calculateAngles(clickedPoints);
            }
            return true;
        });
    }
    private Mat preprocessImage(Mat image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 0);
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 50, 150);
        return edges;
    }

    private void clearAuthToken() {
        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("authToken");
        editor.apply();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private MatOfPoint findAndDrawPoints(Mat edges, Mat originalImage) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        Mat processedImage = Mat.zeros(edges.size(), CvType.CV_8UC3);
        List<Point> pointsArray = new ArrayList<>();

        for (MatOfPoint contour : contours) {
            for (int i = 0; i < contour.rows(); i++) {
                double[] point = contour.get(i, 0);
                double x = point[0];
                double y = point[1];
                pointsArray.add(new Point(x, y));
                Imgproc.circle(originalImage, new Point(x, y), 2, new Scalar(255, 0, 0), -1);
            }
        }

        return new MatOfPoint(pointsArray.toArray(new Point[0]));
    }

    private Mat mouseClick(MatOfPoint points, int x, int y, Mat originalImage) {
        double[] distances = new double[points.rows()];
        for (int i = 0; i < points.rows(); i++) {
            Point point = points.toList().get(i);
            distances[i] = Math.sqrt(Math.pow(point.x - x, 2) + Math.pow(point.y - y, 2));
        }

        int closestPointIndex = findIndexOfMinValue(distances);
        Point closestPoint=points.toList().get(closestPointIndex);
        Mat processedImage = new Mat();
        originalImage.copyTo(processedImage);

        for (Point point : clickedPoints) {
            Imgproc.circle(processedImage, point, 10, new Scalar(255, 0, 0), -1);
        }

        clickedPoints.add(closestPoint);

        Imgproc.circle(processedImage, closestPoint, 10, new Scalar(255, 0, 0), -1);

        return processedImage;
    }

    private int findIndexOfMinValue(double[] array) {
        int minIndex = 0;
        double minValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < minValue) {
                minValue = array[i];
                minIndex = i;
            }
        }
        return minIndex;
    }
    private Mat drawLinesBetweenPoints(Mat image) {
        if (clickedPoints.size() == 4) {
            Mat processedImage = new Mat();
            image.copyTo(processedImage);

            // Draw lines between the selected points
            Imgproc.line(processedImage, clickedPoints.get(0), clickedPoints.get(1), new Scalar(255, 255, 0), 3);
            Imgproc.line(processedImage, clickedPoints.get(2), clickedPoints.get(3), new Scalar(255, 255, 0), 3);

            return processedImage;
        } else {
            return image;
        }
    }

    private void calculateAngles(List<Point> points) {
        if (points.size() == 4) {
            Point p1 = points.get(0);
            Point p2 = points.get(1);
            Point p3 = points.get(2);
            Point p4 = points.get(3);
            double angle2 = Math.toDegrees(Math.atan((p2.y - p1.y) / (p2.x - p1.x)));
            double angle3 = Math.toDegrees(Math.atan((p4.y - p3.y) / (p4.x - p3.x)));
            setUI(angle2,angle3);
            Log.d("Angle", "Deviation angle for the first line: " + angle2);
            Log.d("Angle", "Deviation angle for the second line: " + angle3);
        }
    }

    private void setUI(double left,double right){
        this.left.setText("Left Angle: "+String.valueOf(left));
        this.right.setText("Right Angle: "+String.valueOf(right));
    }

    private void fetchUserDetails(String username) {
        // Retrieve the stored authentication token
        SharedPreferences preferences = getSharedPreferences("auth", MODE_PRIVATE);
        String authToken = preferences.getString("authToken", null);

        if (authToken != null) {
            // Include the token in the request headers
            String authorizationHeader = "Bearer " + authToken;
            Map<String, String> headers = new HashMap<>();
            headers.put("Authorization", authorizationHeader);

            // Create a custom OkHttpClient with the headers
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(chain -> {
                Request original = chain.request();
                Request.Builder requestBuilder = original.newBuilder();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    requestBuilder.addHeader(entry.getKey(), entry.getValue());
                }
                Request request = requestBuilder.build();
                return chain.proceed(request);
            });

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://128.10.4.35:8080/api/")
                    .client(httpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            UserDetail userDetail = retrofit.create(UserDetail.class);


            Call<AdminUserDTO> call = userDetail.getUserDetails(username);
            call.enqueue(new Callback<AdminUserDTO>() {
                @Override
                public void onResponse(Call<AdminUserDTO> call, Response<AdminUserDTO> response) {
                    if (response.isSuccessful()) {
                        adminUserDTO = response.body();
                    } else {
                        Toast.makeText(MainActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AdminUserDTO> call, Throwable t) {
                    // Handle network or unexpected errors
                    Toast.makeText(MainActivity.this, "Failed to fetch user details: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "No authentication token found", Toast.LENGTH_SHORT).show();
            // Handle the case where no token is found (e.g., redirect to login)
        }
    }


}